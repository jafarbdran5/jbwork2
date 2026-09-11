package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.ExternalRequestEntity
import com.example.data.local.entities.ExternalRequestSourceEntity
import com.example.data.local.entities.ExternalSheetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExternalRequestSourceDao {
    @Query("SELECT * FROM external_request_sources ORDER BY createdAt DESC")
    fun getAllSources(): Flow<List<ExternalRequestSourceEntity>>

    @Query("SELECT * FROM external_request_sources")
    suspend fun getAllSourcesList(): List<ExternalRequestSourceEntity>

    @Query("SELECT * FROM external_request_sources WHERE id = :id LIMIT 1")
    suspend fun getSourceById(id: String): ExternalRequestSourceEntity?

    @Query("SELECT * FROM external_request_sources WHERE enabled = 1")
    suspend fun getActiveSources(): List<ExternalRequestSourceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(source: ExternalRequestSourceEntity)

    @Update
    suspend fun update(source: ExternalRequestSourceEntity)

    @Query("UPDATE external_request_sources SET status = :status, lastSync = :lastSync, updatedAt = :lastSync WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, lastSync: Long = System.currentTimeMillis())

    @Query("UPDATE external_request_sources SET enabled = :enabled, updatedAt = :timestamp WHERE id = :id")
    suspend fun toggleEnabled(id: String, enabled: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM external_request_sources WHERE id = :id")
    suspend fun deleteSource(id: String)
}

@Dao
interface ExternalSheetDao {
    @Query("SELECT * FROM external_sheets ORDER BY sheetName ASC")
    fun getAllSheets(): Flow<List<ExternalSheetEntity>>

    @Query("SELECT * FROM external_sheets")
    suspend fun getAllSheetsList(): List<ExternalSheetEntity>

    @Query("SELECT * FROM external_sheets WHERE sourceId = :sourceId ORDER BY sheetName ASC")
    fun getSheetsForSource(sourceId: String): Flow<List<ExternalSheetEntity>>

    @Query("SELECT * FROM external_sheets WHERE sourceId = :sourceId")
    suspend fun getSheetsForSourceSync(sourceId: String): List<ExternalSheetEntity>

    @Query("SELECT * FROM external_sheets WHERE id = :id LIMIT 1")
    suspend fun getSheetById(id: String): ExternalSheetEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotExists(sheet: ExternalSheetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(sheet: ExternalSheetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sheets: List<ExternalSheetEntity>)

    @Query("UPDATE external_sheets SET enabled = :enabled WHERE id = :id")
    suspend fun setSheetEnabled(id: String, enabled: Boolean)

    @Query("UPDATE external_sheets SET ignored = :ignored WHERE id = :id")
    suspend fun setSheetIgnored(id: String, ignored: Boolean)

    @Query("UPDATE external_sheets SET rowCount = :rowCount, columnCount = :colCount, lastSync = :lastSync WHERE id = :id")
    suspend fun updateMetrics(id: String, rowCount: Int, colCount: Int, lastSync: Long = System.currentTimeMillis())

    @Query("UPDATE external_sheets SET customDisplayName = :displayName WHERE id = :id")
    suspend fun updateCustomDisplayName(id: String, displayName: String?)

    @Query("UPDATE external_sheets SET columnMappingJson = :mappingJson WHERE id = :id")
    suspend fun updateColumnMapping(id: String, mappingJson: String)

    @Query("UPDATE external_sheets SET sheetName = :name WHERE id = :id")
    suspend fun updateSheetName(id: String, name: String)

    @Query("DELETE FROM external_sheets WHERE sourceId = :sourceId")
    suspend fun deleteBySourceId(sourceId: String)

    @Query("DELETE FROM external_sheets WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface ExternalRequestDao {
    @Query("SELECT * FROM external_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<ExternalRequestEntity>>

    @Query("SELECT * FROM external_requests WHERE sourceId = :sourceId ORDER BY createdAt DESC")
    fun getRequestsBySource(sourceId: String): Flow<List<ExternalRequestEntity>>

    @Query("SELECT * FROM external_requests WHERE sourceId = :sourceId AND sheetId = :sheetId ORDER BY createdAt DESC")
    fun getRequestsBySheet(sourceId: String, sheetId: String): Flow<List<ExternalRequestEntity>>

    @Query("SELECT * FROM external_requests WHERE sourceId = :sourceId AND sheetId = :sheetId")
    suspend fun getRequestsBySheetSync(sourceId: String, sheetId: String): List<ExternalRequestEntity>

    @Query("DELETE FROM external_requests WHERE sourceId = :sourceId AND sheetId = :sheetId")
    suspend fun deleteRequestsBySheet(sourceId: String, sheetId: String)

    @Query("SELECT * FROM external_requests WHERE status = :status ORDER BY createdAt DESC")
    fun getRequestsByStatus(status: String): Flow<List<ExternalRequestEntity>>

    @Query("SELECT * FROM external_requests WHERE id = :id LIMIT 1")
    suspend fun getRequestById(id: String): ExternalRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(request: ExternalRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requests: List<ExternalRequestEntity>)

    @Query("UPDATE external_requests SET status = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE external_requests SET status = :status, associatedCaseId = :caseId, updatedAt = :timestamp WHERE id = :id")
    suspend fun linkToCase(
        id: String,
        caseId: String,
        status: String = "تم تحويله إلى قضية",
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("UPDATE external_requests SET internalNotes = :notes, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateInternalNotes(id: String, notes: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM external_requests WHERE sourceId = :sourceId")
    suspend fun deleteBySourceId(sourceId: String)

    @Query("DELETE FROM external_requests WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM external_requests")
    fun getCount(): Flow<Int>
}
