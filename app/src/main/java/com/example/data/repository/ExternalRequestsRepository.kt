package com.example.data.repository

import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.entities.ExternalRequestEntity
import com.example.data.local.entities.ExternalRequestSourceEntity
import com.example.data.local.entities.ExternalSheetEntity
import com.example.data.remote.GoogleSheetsPublicService
import com.example.data.remote.SheetReadResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.UUID

class ExternalRequestsRepository(
    private val database: AppDatabase,
    private val sheetsService: GoogleSheetsPublicService = GoogleSheetsPublicService()
) {
    private val sourceDao = database.externalRequestSourceDao()
    private val sheetDao = database.externalSheetDao()
    private val requestDao = database.externalRequestDao()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private val _lastGlobalSync = MutableStateFlow<Long?>(null)
    val lastGlobalSync = _lastGlobalSync.asStateFlow()

    val allSources: Flow<List<ExternalRequestSourceEntity>> = sourceDao.getAllSources()
    val allSheets: Flow<List<ExternalSheetEntity>> = sheetDao.getAllSheets()
    val allRequests: Flow<List<ExternalRequestEntity>> = requestDao.getAllRequests()

    fun getSheetsForSource(sourceId: String): Flow<List<ExternalSheetEntity>> =
        sheetDao.getSheetsForSource(sourceId)

    suspend fun testConnection(publicUrl: String): Result<String> =
        sheetsService.testConnection(publicUrl)

    suspend fun runSpreadsheetDiagnostic(publicUrl: String) =
        sheetsService.runFullSpreadsheetDiagnostic(publicUrl)

    suspend fun addCsvSource(name: String, csvContent: String): Result<ExternalRequestSourceEntity> = withContext(Dispatchers.IO) {
        try {
            val sourceId = "src_csv_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
            val source = ExternalRequestSourceEntity(
                id = sourceId,
                name = name.trim().ifBlank { "مصدر CSV مستورد" },
                publicUrl = "offline://csv_import",
                spreadsheetId = "CSV_IMPORT_${UUID.randomUUID().toString().take(8)}",
                sourceType = "CSV_IMPORT",
                enabled = true,
                status = "محلي (Offline)",
                lastSync = System.currentTimeMillis()
            )
            sourceDao.insertOrUpdate(source)

            val sheetEntity = ExternalSheetEntity(
                id = "${sourceId}_0",
                sourceId = sourceId,
                spreadsheetId = source.spreadsheetId,
                sheetId = "0",
                sheetName = "ورقة البيانات المستوردة",
                index = 0,
                enabled = true,
                ignored = false,
                rowCount = 0,
                columnCount = 0,
                lastSync = System.currentTimeMillis()
            )
            sheetDao.insertIfNotExists(sheetEntity)

            val parsed = sheetsService.parseCsvStringToResult(csvContent, sheetEntity.sheetName)
            if (parsed.isSuccess) {
                val sheetData = parsed.getOrThrow()
                sheetDao.updateMetrics(sheetEntity.id, sheetData.rowCount, sheetData.columnCount, System.currentTimeMillis())

                val existingRequests = requestDao.getRequestsBySheetSync(sourceId, "0")
                val existingFingerprints = existingRequests.map { it.rowId }.toSet()

                val newRequests = sheetData.rows.filter { row ->
                    !existingFingerprints.contains(row.rowId)
                }.mapIndexed { index, row ->
                    ExternalRequestEntity(
                        id = "${source.id}_${source.spreadsheetId}_0_${row.rowId}",
                        sourceId = sourceId,
                        spreadsheetId = source.spreadsheetId,
                        sheetId = "0",
                        sheetName = sheetEntity.sheetName,
                        rowId = row.rowId,
                        requestNumber = "CSV-${source.id.takeLast(4)}-${index + 1}",
                        clientName = row.clientName,
                        clientPhone = row.clientPhone,
                        clientEmail = row.clientEmail,
                        description = row.description,
                        urgency = row.urgency,
                        receivedAt = row.receivedAt,
                        status = "جديد",
                        internalNotes = row.internalNotes,
                        associatedCaseId = null,
                        rawData = row.rawDataJson,
                        additionalFields = row.additionalFieldsJson,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                }
                if (newRequests.isNotEmpty()) {
                    requestDao.insertAll(newRequests)
                }
            }

            Result.success(source)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun extractDocumentTitle(publicUrl: String): String = withContext(Dispatchers.IO) {
        sheetsService.extractDocumentTitle(publicUrl)
    }

    suspend fun addSource(name: String, publicUrl: String): Result<ExternalRequestSourceEntity> = withContext(Dispatchers.IO) {
        try {
            val trimmedUrl = publicUrl.trim()
            val spreadsheetId = GoogleSheetsPublicService.extractSpreadsheetId(trimmedUrl)
            if (spreadsheetId.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("رابط Google Sheet غير صالح"))
            }

            val detectedTitle = sheetsService.extractDocumentTitle(trimmedUrl)
            val finalName = if (name.trim().isBlank() || name.trim() == "مصدر Google Sheet") {
                detectedTitle.ifBlank { "مصدر Google Sheet" }
            } else {
                name.trim()
            }

            val sourceId = "src_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
            val source = ExternalRequestSourceEntity(
                id = sourceId,
                name = finalName,
                publicUrl = trimmedUrl,
                spreadsheetId = spreadsheetId,
                sourceType = "GOOGLE_SHEETS",
                enabled = true,
                status = "متصل",
                lastSync = null
            )

            sourceDao.insertOrUpdate(source)

            // Auto-discover sheets immediately
            val discovered = sheetsService.discoverSheets(trimmedUrl).getOrDefault(emptyList())
            val sheetEntities = discovered.mapIndexed { idx, s ->
                ExternalSheetEntity(
                    id = "${sourceId}_${s.sheetId.ifBlank { idx.toString() }}",
                    sourceId = sourceId,
                    spreadsheetId = spreadsheetId,
                    sheetId = s.sheetId.ifBlank { idx.toString() },
                    sheetName = s.sheetName,
                    index = s.index,
                    enabled = true, // First batch is enabled
                    ignored = false,
                    sheetType = s.sheetType,
                    hidden = s.hidden,
                    rowCount = s.rowCount,
                    columnCount = s.columnCount,
                    lastSync = null
                )
            }
            if (sheetEntities.isNotEmpty()) {
                sheetDao.insertAll(sheetEntities)
            }

            // Trigger immediate sync for this source
            syncSource(sourceId)

            Result.success(source)
        } catch (e: Exception) {
            Log.e("ExternalRequestsRepo", "Failed to add source", e)
            Result.failure(e)
        }
    }

    suspend fun getSourceById(sourceId: String): ExternalRequestSourceEntity? = withContext(Dispatchers.IO) {
        sourceDao.getSourceById(sourceId)
    }

    suspend fun getSheetById(sheetId: String): ExternalSheetEntity? = withContext(Dispatchers.IO) {
        sheetDao.getSheetById(sheetId)
    }

    fun getRequestsBySheet(sourceId: String, sheetId: String): Flow<List<ExternalRequestEntity>> {
        return requestDao.getRequestsBySheet(sourceId, sheetId)
    }

    suspend fun updateSource(source: ExternalRequestSourceEntity) = withContext(Dispatchers.IO) {
        sourceDao.update(source.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteSource(sourceId: String) = withContext(Dispatchers.IO) {
        // Deletes exclusively inside app; NEVER deletes original Google Sheet
        requestDao.deleteBySourceId(sourceId)
        sheetDao.deleteBySourceId(sourceId)
        sourceDao.deleteSource(sourceId)
    }

    suspend fun deleteSheetFromApp(sheetId: String) = withContext(Dispatchers.IO) {
        val sheet = sheetDao.getSheetById(sheetId) ?: return@withContext
        requestDao.deleteRequestsBySheet(sheet.sourceId, sheet.sheetId)
        sheetDao.deleteById(sheetId)
    }

    suspend fun toggleSourceEnabled(sourceId: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        sourceDao.toggleEnabled(sourceId, enabled)
    }

    suspend fun setSheetEnabled(sheetId: String, enabled: Boolean) = withContext(Dispatchers.IO) {
        sheetDao.setSheetEnabled(sheetId, enabled)
    }

    suspend fun setSheetIgnored(sheetId: String, ignored: Boolean) = withContext(Dispatchers.IO) {
        sheetDao.setSheetIgnored(sheetId, ignored)
    }

    suspend fun updateCustomDisplayName(sheetId: String, displayName: String?) = withContext(Dispatchers.IO) {
        sheetDao.updateCustomDisplayName(sheetId, displayName?.trim()?.ifBlank { null })
    }

    suspend fun updateColumnMapping(sheetId: String, mappingJson: String) = withContext(Dispatchers.IO) {
        sheetDao.updateColumnMapping(sheetId, mappingJson)
    }

    suspend fun addSheetManually(
        sourceId: String,
        sheetName: String,
        sheetGid: String
    ): Result<ExternalSheetEntity> = withContext(Dispatchers.IO) {
        try {
            val source = sourceDao.getSourceById(sourceId)
                ?: return@withContext Result.failure(Exception("المصدر غير موجود"))
            val cleanName = sheetName.trim().ifBlank { "ورقة جديدة" }
            val cleanGid = sheetGid.trim().ifBlank { "0" }
            val id = "${sourceId}_$cleanGid"
            val existing = sheetDao.getSheetById(id)
            if (existing != null) {
                return@withContext Result.failure(Exception("هذه الورقة مضافة مسبقاً في هذا المصدر (GID: $cleanGid)"))
            }
            val newSheet = ExternalSheetEntity(
                id = id,
                sourceId = sourceId,
                spreadsheetId = source.spreadsheetId,
                sheetId = cleanGid,
                sheetName = cleanName,
                index = 999,
                enabled = true,
                ignored = false,
                rowCount = 0,
                columnCount = 0,
                lastSync = null,
                customDisplayName = null,
                columnMappingJson = "{}"
            )
            sheetDao.insertIfNotExists(newSheet)
            Result.success(newSheet)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readSheetRawData(
        sourceId: String,
        sheetEntityId: String
    ): Result<SheetReadResult> = withContext(Dispatchers.IO) {
        try {
            val source = sourceDao.getSourceById(sourceId)
                ?: return@withContext Result.failure(Exception("المصدر غير موجود"))
            val sheet = sheetDao.getSheetById(sheetEntityId)
                ?: return@withContext Result.failure(Exception("الورقة غير موجودة"))

            val mapping = parseColumnMapping(sheet.columnMappingJson)
            sheetsService.readSheetData(
                publicUrl = source.publicUrl,
                sheetName = sheet.sheetName,
                sheetGid = sheet.sheetId,
                columnMapping = mapping
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Auto-discovers new sheets in the Google Sheet.
     * Existing sheets keep their enabled/ignored state and mappings.
     * Updates names if changed.
     * Newly discovered sheets are added with enabled = true by default.
     */
    suspend fun refreshSheetsForSource(sourceId: String): Result<List<ExternalSheetEntity>> = withContext(Dispatchers.IO) {
        try {
            val source = sourceDao.getSourceById(sourceId)
                ?: return@withContext Result.failure(Exception("المصدر غير موجود"))

            val existingSheets = sheetDao.getSheetsForSourceSync(sourceId)
            val existingById = existingSheets.associateBy { it.sheetId }
            val existingByNames = existingSheets.associateBy { it.sheetName.trim() }

            val discovered = sheetsService.discoverSheets(source.publicUrl).getOrThrow()
            val newEntities = mutableListOf<ExternalSheetEntity>()

            for (ds in discovered) {
                val cleanName = ds.sheetName.trim()
                val gid = ds.sheetId.ifBlank { "${ds.index}" }
                val matchById = existingById[gid]
                val matchByName = existingByNames[cleanName]

                if (matchById != null) {
                    // Update sheet metadata if changed on Google Sheets
                    val rowCount = if (ds.rowCount > 0) ds.rowCount else matchById.rowCount
                    val colCount = if (ds.columnCount > 0) ds.columnCount else matchById.columnCount
                    sheetDao.updateSheetFullMetadata(
                        id = matchById.id,
                        name = cleanName,
                        index = ds.index,
                        rowCount = rowCount,
                        colCount = colCount,
                        sheetType = ds.sheetType,
                        hidden = ds.hidden
                    )
                } else if (matchByName != null) {
                    // Already exists by name, update metadata
                    val rowCount = if (ds.rowCount > 0) ds.rowCount else matchByName.rowCount
                    val colCount = if (ds.columnCount > 0) ds.columnCount else matchByName.columnCount
                    sheetDao.updateSheetFullMetadata(
                        id = matchByName.id,
                        name = cleanName,
                        index = ds.index,
                        rowCount = rowCount,
                        colCount = colCount,
                        sheetType = ds.sheetType,
                        hidden = ds.hidden
                    )
                } else {
                    // Newly discovered sheet: enabled = true by default
                    newEntities.add(
                        ExternalSheetEntity(
                            id = "${sourceId}_$gid",
                            sourceId = sourceId,
                            spreadsheetId = source.spreadsheetId,
                            sheetId = gid,
                            sheetName = cleanName,
                            index = ds.index,
                            enabled = true,
                            ignored = false,
                            sheetType = ds.sheetType,
                            hidden = ds.hidden,
                            rowCount = ds.rowCount,
                            columnCount = ds.columnCount,
                            lastSync = null,
                            customDisplayName = null,
                            columnMappingJson = "{}"
                        )
                    )
                }
            }

            if (newEntities.isNotEmpty()) {
                sheetDao.insertAll(newEntities)
            }

            val allCurrent = sheetDao.getSheetsForSourceSync(sourceId)
            val activeCount = allCurrent.count { it.enabled && !it.ignored }
            sourceDao.updateSyncStatus(sourceId, "متصل ($activeCount/${allCurrent.size} أوراق مفعلة)", System.currentTimeMillis())

            Result.success(allCurrent)
        } catch (e: Exception) {
            Log.e("ExternalRequestsRepo", "Failed to refresh sheets", e)
            Result.failure(e)
        }
    }

    /**
     * Syncs a single specific sheet tab.
     */
    suspend fun syncSingleSheet(sourceId: String, sheetEntityId: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val source = sourceDao.getSourceById(sourceId)
                ?: return@withContext Result.failure(Exception("المصدر غير موجود"))
            val sheet = sheetDao.getSheetById(sheetEntityId)
                ?: return@withContext Result.failure(Exception("الورقة غير موجودة"))

            val mapping = parseColumnMapping(sheet.columnMappingJson)
            val readResult = sheetsService.readSheetData(
                publicUrl = source.publicUrl,
                sheetName = sheet.sheetName,
                sheetGid = sheet.sheetId,
                columnMapping = mapping
            )

            if (readResult.isFailure) {
                return@withContext Result.failure(readResult.exceptionOrNull() ?: Exception("فشل في قراءة الورقة"))
            }

            val data = readResult.getOrThrow()
            val existingList = requestDao.getRequestsBySheetSync(source.id, sheet.sheetId)
            val existingMap = existingList.associateBy { it.rowId }
            val fetchedRowIds = data.rows.map { it.rowId }.toSet()

            val requestEntities = data.rows.mapIndexed { index, row ->
                val reqId = "${source.id}_${source.spreadsheetId}_${sheet.sheetId}_${row.rowId}"
                val reqNumber = "EXT-${source.id.takeLast(4)}-${index + 1}"

                val existing = existingMap[row.rowId]
                val status = existing?.status ?: "جديد"
                val associatedCaseId = existing?.associatedCaseId
                val internalNotes = if (existing?.internalNotes.isNullOrBlank()) row.internalNotes else existing!!.internalNotes

                ExternalRequestEntity(
                    id = reqId,
                    sourceId = source.id,
                    spreadsheetId = source.spreadsheetId,
                    sheetId = sheet.sheetId,
                    sheetName = sheet.sheetName,
                    rowId = row.rowId,
                    requestNumber = reqNumber,
                    clientName = row.clientName,
                    clientPhone = row.clientPhone,
                    clientEmail = row.clientEmail,
                    description = row.description,
                    urgency = row.urgency,
                    status = status,
                    receivedAt = row.receivedAt,
                    internalNotes = internalNotes,
                    associatedCaseId = associatedCaseId,
                    rawData = row.rawDataJson,
                    additionalFields = row.additionalFieldsJson,
                    createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            }

            if (requestEntities.isNotEmpty()) {
                requestDao.insertAll(requestEntities)
            }

            // Safe sync rule: For rows previously synced from this sheet but now missing from Google Sheet,
            // we NEVER delete customer data (Rule 12). We mark them with a note if not already marked.
            for (oldReq in existingList) {
                if (!fetchedRowIds.contains(oldReq.rowId)) {
                    val alertNote = "[تنبيه: هذا الصف لم يعد موجوداً في ملف Google Sheet الأصلي]"
                    if (!oldReq.internalNotes.contains(alertNote)) {
                        val newNotes = if (oldReq.internalNotes.isBlank()) alertNote else "${oldReq.internalNotes}\n$alertNote"
                        requestDao.updateInternalNotes(oldReq.id, newNotes)
                    }
                }
            }

            sheetDao.updateMetrics(
                id = sheet.id,
                rowCount = data.rowCount,
                colCount = data.columnCount,
                lastSync = System.currentTimeMillis()
            )

            Result.success(requestEntities.size)
        } catch (e: Exception) {
            Log.e("ExternalRequestsRepo", "Failed to sync single sheet: $sheetEntityId", e)
            Result.failure(e)
        }
    }

    /**
     * Reads sheet data directly for preview and column mapping.
     */
    suspend fun readSheetDataDirect(
        publicUrl: String,
        sheetName: String,
        sheetGid: String,
        columnMapping: Map<String, String> = emptyMap()
    ) = sheetsService.readSheetData(publicUrl, sheetName, sheetGid, columnMapping)

    private fun parseColumnMapping(json: String?): Map<String, String> {
        if (json.isNullOrBlank()) return emptyMap()
        return try {
            val obj = org.json.JSONObject(json)
            val map = mutableMapOf<String, String>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val v = obj.optString(k)
                if (v.isNotBlank()) {
                    map[k] = v
                }
            }
            map
        } catch (_: Exception) {
            emptyMap()
        }
    }

    /**
     * Syncs a single source: reads all enabled, non-ignored sheets and updates requests safely.
     */
    suspend fun syncSource(sourceId: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val source = sourceDao.getSourceById(sourceId)
                ?: return@withContext Result.failure(Exception("المصدر غير موجود"))

            if (!source.enabled) {
                return@withContext Result.success(0)
            }

            val sheets = sheetDao.getSheetsForSourceSync(sourceId)
                .filter { it.enabled && !it.ignored }

            if (sheets.isEmpty()) {
                sourceDao.updateSyncStatus(sourceId, "متصل (لا توجد أوراق مفعلة)", System.currentTimeMillis())
                return@withContext Result.success(0)
            }

            var totalNewOrUpdated = 0

            for (sheet in sheets) {
                val res = syncSingleSheet(sourceId, sheet.id)
                if (res.isSuccess) {
                    totalNewOrUpdated += res.getOrDefault(0)
                } else {
                    Log.w("ExternalRequestsRepo", "Failed sheet sync: ${sheet.sheetName}", res.exceptionOrNull())
                }
            }

            sourceDao.updateSyncStatus(sourceId, "متصل ومحدث (${sheets.size} أوراق مفعلة)", System.currentTimeMillis())
            Result.success(totalNewOrUpdated)
        } catch (e: Exception) {
            Log.e("ExternalRequestsRepo", "Failed to sync source: $sourceId", e)
            sourceDao.updateSyncStatus(sourceId, "خطأ في المزامنة", System.currentTimeMillis())
            Result.failure(e)
        }
    }

    /**
     * Syncs all active sources. Failure in one source does NOT block the rest.
     */
    suspend fun syncAllSources(): Map<String, Result<Int>> = withContext(Dispatchers.IO) {
        _isSyncing.value = true
        val results = mutableMapOf<String, Result<Int>>()
        try {
            val sources = sourceDao.getActiveSources()
            for (src in sources) {
                try {
                    val res = syncSource(src.id)
                    results[src.id] = res
                } catch (e: Exception) {
                    results[src.id] = Result.failure(e)
                }
            }
            _lastGlobalSync.value = System.currentTimeMillis()
        } finally {
            _isSyncing.value = false
        }
        results
    }

    suspend fun updateRequestStatus(requestId: String, status: String) = withContext(Dispatchers.IO) {
        requestDao.updateStatus(requestId, status)
    }

    suspend fun linkRequestToCase(requestId: String, caseId: String) = withContext(Dispatchers.IO) {
        requestDao.linkToCase(requestId, caseId)
    }

    suspend fun updateRequestNotes(requestId: String, notes: String) = withContext(Dispatchers.IO) {
        requestDao.updateInternalNotes(requestId, notes)
    }

    suspend fun ensureDefaultSampleData(force: Boolean = false) = withContext(Dispatchers.IO) {
        try {
            val sourceId = "src_sample_national_parks"
            val existing = sourceDao.getSourceById(sourceId)
            if (existing != null) {
                requestDao.deleteBySourceId(sourceId)
                sheetDao.deleteBySourceId(sourceId)
                sourceDao.deleteSource(sourceId)
                Log.d("ExternalRequestsRepo", "Purged dummy sample national parks source.")
            }
        } catch (e: Exception) {
            Log.w("ExternalRequestsRepo", "Dummy cleanup encountered: ${e.message}")
        }
    }
}
