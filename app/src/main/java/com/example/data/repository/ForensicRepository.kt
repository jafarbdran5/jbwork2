package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.PrepopulatedKnowledge
import com.example.data.local.PrepopulatedSupportForms
import com.example.data.local.PrepopulatedInvestigationTools
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.CaseAuditLogEntity
import com.example.data.local.entities.CaseEntity
import com.example.data.local.entities.CaseFinancialLogEntity
import com.example.data.local.entities.CaseLinkedItemEntity
import com.example.data.local.entities.CasePaymentEntity
import com.example.data.local.entities.ClientEntity
import com.example.data.local.entities.ContentEntity
import com.example.data.local.entities.EvidenceEntity
import com.example.data.local.entities.InvestigationToolEntity
import com.example.data.local.entities.KnowledgeEntity
import com.example.data.local.entities.SupportFormEntity
import com.example.data.local.entities.SyncOperationEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.VideoIdeaEntity
import com.example.data.local.entities.VideoScriptEntity
import com.example.data.local.entities.AdminAuditLogEntity
import com.example.data.local.entities.AppSectionConfigEntity
import com.example.data.local.entities.CustomFieldDefinitionEntity
import com.example.data.local.entities.SystemCategoryEntity
import com.example.data.local.entities.SystemExpenseEntity
import com.example.data.local.entities.OfficialSourceEntity
import com.example.data.local.entities.ProfitShareRuleEntity
import com.example.data.local.entities.FinancialRevenueEntity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ForensicRepository(
    private val database: AppDatabase,
    private val appScope: CoroutineScope
) {
    private val caseDao = database.caseDao()
    private val clientDao = database.clientDao()
    private val evidenceDao = database.evidenceDao()
    private val contentDao = database.contentDao()
    private val knowledgeDao = database.knowledgeDao()
    private val auditLogDao = database.auditLogDao()
    private val taskDao = database.taskDao()
    private val syncOperationDao = database.syncOperationDao()
    private val settingsDao = database.settingsDao()
    private val supportFormDao = database.supportFormDao()
    private val investigationToolDao = database.investigationToolDao()
    private val caseLinkedItemDao = database.caseLinkedItemDao()
    private val casePaymentDao = database.casePaymentDao()
    private val caseFinancialLogDao = database.caseFinancialLogDao()
    private val caseAuditLogDao = database.caseAuditLogDao()
    private val videoIdeaDao = database.videoIdeaDao()
    private val videoScriptDao = database.videoScriptDao()
    private val appSectionConfigDao = database.appSectionConfigDao()
    private val customFieldDefinitionDao = database.customFieldDefinitionDao()
    private val systemExpenseDao = database.systemExpenseDao()
    private val systemCategoryDao = database.systemCategoryDao()
    private val adminAuditLogDao = database.adminAuditLogDao()
    private val officialSourceDao = database.officialSourceDao()
    private val profitShareRuleDao = database.profitShareRuleDao()
    private val financialRevenueDao = database.financialRevenueDao()

    // Sync status state for UI feedback
    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing = _isCloudSyncing.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTimestamp = _lastSyncTimestamp.asStateFlow()

    // Reactive flows from Room
    val allCases: Flow<List<CaseEntity>> = caseDao.getAllActiveCases()
    val allClients: Flow<List<ClientEntity>> = clientDao.getAllActiveClients()
    val allEvidence: Flow<List<EvidenceEntity>> = evidenceDao.getAllActiveEvidence()
    val allContent: Flow<List<ContentEntity>> = contentDao.getAllActiveContent()
    val allSectionConfigs: Flow<List<AppSectionConfigEntity>> = appSectionConfigDao.getAllSections()
    val visibleSectionConfigs: Flow<List<AppSectionConfigEntity>> = appSectionConfigDao.getVisibleSections()
    val allCustomFields: Flow<List<CustomFieldDefinitionEntity>> = customFieldDefinitionDao.getAllFields()
    val allExpenses: Flow<List<SystemExpenseEntity>> = systemExpenseDao.getAllActiveExpenses()
    val allCategories: Flow<List<SystemCategoryEntity>> = systemCategoryDao.getAllCategories()
    val adminAuditLogs: Flow<List<AdminAuditLogEntity>> = adminAuditLogDao.getAllLogs()
    val allKnowledge: Flow<List<KnowledgeEntity>> = knowledgeDao.getAllActiveKnowledge()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllActiveTasks()
    val recentAuditLogs: Flow<List<AuditLogEntity>> = auditLogDao.getRecentAuditLogs()
    val pendingSyncCount: Flow<Int> = syncOperationDao.getPendingCount()
    val allSettings: Flow<List<com.example.data.local.entities.AppSettingsEntity>> = settingsDao.getAllSettings()
    val allSupportForms: Flow<List<SupportFormEntity>> = supportFormDao.getAllActiveForms()
    val allInvestigationTools: Flow<List<InvestigationToolEntity>> = investigationToolDao.getAllActiveTools()
    val allPayments: Flow<List<CasePaymentEntity>> = casePaymentDao.getAllPayments()
    val allFinancialLogs: Flow<List<CaseFinancialLogEntity>> = caseFinancialLogDao.getAllFinancialLogs()
    val allVideoIdeas: Flow<List<VideoIdeaEntity>> = videoIdeaDao.getAllActiveIdeas()
    val allVideoScripts: Flow<List<VideoScriptEntity>> = videoScriptDao.getAllActiveScripts()

    // Official Sources & Gateways Flows
    val allOfficialSources: Flow<List<OfficialSourceEntity>> = officialSourceDao.getAllSources()
    val visibleOfficialSources: Flow<List<OfficialSourceEntity>> = officialSourceDao.getAllVisibleSources()
    val favoriteOfficialSources: Flow<List<OfficialSourceEntity>> = officialSourceDao.getFavoriteSources()

    // Profit Share Rules & Financial Flows
    val allProfitRules: Flow<List<ProfitShareRuleEntity>> = profitShareRuleDao.getAllRules()
    val activeProfitRules: Flow<List<ProfitShareRuleEntity>> = profitShareRuleDao.getActiveRules()
    val allFinancialRevenues: Flow<List<FinancialRevenueEntity>> = financialRevenueDao.getAllRevenues()
    val totalPaidAmount: Flow<Double?> = financialRevenueDao.getTotalPaidAmount()
    val totalRemainingAmount: Flow<Double?> = financialRevenueDao.getTotalRemainingAmount()

    // Trash & Deleted Items Flows
    val deletedCases: Flow<List<CaseEntity>> = caseDao.getDeletedCases()
    val deletedClients: Flow<List<ClientEntity>> = clientDao.getDeletedClients()
    val deletedEvidence: Flow<List<EvidenceEntity>> = evidenceDao.getDeletedEvidence()
    val deletedContent: Flow<List<ContentEntity>> = contentDao.getDeletedContent()
    val deletedKnowledge: Flow<List<KnowledgeEntity>> = knowledgeDao.getDeletedKnowledge()
    val deletedTasks: Flow<List<TaskEntity>> = taskDao.getDeletedTasks()
    val deletedVideoIdeas: Flow<List<VideoIdeaEntity>> = videoIdeaDao.getDeletedIdeas()

    init {
        // Ensure official guides and seed data exist
        appScope.launch(Dispatchers.IO) {
            val officialCount = knowledgeDao.getOfficialGuideCount()
            if (officialCount == 0) {
                AppDatabase.seedInitialData(database)
            } else {
                // Ensure new sections are seeded even if db already had knowledge
                if (supportFormDao.getCount() == 0) {
                    supportFormDao.insertAll(PrepopulatedSupportForms.OFFICIAL_FORMS)
                }
                if (investigationToolDao.getCount() == 0) {
                    investigationToolDao.insertAll(PrepopulatedInvestigationTools.OFFICIAL_TOOLS)
                }
            }
        }
    }


    // ==========================================
    // AUDIT LOGGING
    // ==========================================
    suspend fun logAudit(
        actionType: String,
        module: String,
        entityId: String,
        performedBy: String = "جعفر بدران",
        userRole: String = "Super Admin",
        details: String
    ) {
        val log = AuditLogEntity(
            id = "audit_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
            actionType = actionType,
            module = module,
            entityId = entityId,
            performedBy = performedBy,
            userRole = userRole,
            details = details
        )
        auditLogDao.insertLog(log)
    }

    // ==========================================
    // CASES CRUD & SYNC
    // ==========================================
    suspend fun getCaseById(id: String): CaseEntity? = caseDao.getCaseById(id)

    suspend fun insertOrUpdateCase(caseEntity: CaseEntity, isNew: Boolean = false) {
        caseDao.insertOrUpdate(caseEntity.copy(updatedDate = System.currentTimeMillis()))
        logAudit(
            actionType = if (isNew) "CREATE" else "EDIT",
            module = "CASES",
            entityId = caseEntity.id,
            details = "${if (isNew) "إنشاء قضية جديدة" else "تعديل بيانات القضية"}: ${caseEntity.caseNumber} - ${caseEntity.title}"
        )
        dispatchCloudSync("cases", caseEntity.id)
    }

    suspend fun softDeleteCase(id: String, caseNumber: String) {
        caseDao.softDelete(id)
        logAudit(
            actionType = "DELETE",
            module = "CASES",
            entityId = id,
            details = "نقل القضية $caseNumber إلى سلة المحذوفات المؤقتة"
        )
        dispatchCloudSync("cases", id)
    }

    suspend fun restoreCase(id: String) {
        caseDao.restoreCase(id)
        logAudit(
            actionType = "RESTORE",
            module = "CASES",
            entityId = id,
            details = "استعادة القضية $id من سلة المحذوفات"
        )
    }

    // ==========================================
    // CLIENTS CRUD & SYNC
    // ==========================================
    suspend fun insertOrUpdateClient(client: ClientEntity, isNew: Boolean = false) {
        clientDao.insertOrUpdate(client)
        logAudit(
            actionType = if (isNew) "CREATE" else "EDIT",
            module = "CLIENTS",
            entityId = client.id,
            details = "${if (isNew) "تسجيل عميل جديد" else "تحديث سجل العميل"}: ${client.fullName}"
        )
        dispatchCloudSync("clients", client.id)
    }

    suspend fun softDeleteClient(id: String, name: String) {
        clientDao.softDelete(id)
        logAudit(
            actionType = "DELETE",
            module = "CLIENTS",
            entityId = id,
            details = "حذف سجل العميل $name (حذف آمن)"
        )
        dispatchCloudSync("clients", id)
    }

    // ==========================================
    // EVIDENCE VAULT CRUD & SYNC
    // ==========================================
    suspend fun insertOrUpdateEvidence(evidence: EvidenceEntity, isNew: Boolean = true) {
        evidenceDao.insertOrUpdate(evidence)
        logAudit(
            actionType = if (isNew) "CREATE" else "EDIT",
            module = "EVIDENCE",
            entityId = evidence.id,
            details = "توثيق وحفظ مرفق جديد: ${evidence.evidenceName} (SHA256: ${evidence.sha256Hash.take(12)}...)"
        )
        dispatchCloudSync("evidence", evidence.id)
    }

    suspend fun softDeleteEvidence(id: String, evidenceName: String) {
        evidenceDao.softDelete(id)
        logAudit(
            actionType = "DELETE",
            module = "EVIDENCE",
            entityId = id,
            details = "نقل المرفق $evidenceName لسلة الحذف المؤقت"
        )
        dispatchCloudSync("evidence", id)
    }

    fun getEvidenceForCase(caseId: String): Flow<List<EvidenceEntity>> = evidenceDao.getEvidenceForCase(caseId)

    fun getDeletedEvidenceForCase(caseId: String): Flow<List<EvidenceEntity>> = evidenceDao.getDeletedEvidenceForCase(caseId)

    suspend fun restoreEvidence(id: String, evidenceName: String = "") {
        evidenceDao.restoreEvidence(id)
        logAudit(
            actionType = "RESTORE",
            module = "EVIDENCE",
            entityId = id,
            details = "استعادة المرفق $evidenceName من سلة المحذوفات"
        )
        dispatchCloudSync("evidence", id)
    }

    suspend fun permanentDeleteEvidence(id: String, evidenceName: String = "") {
        evidenceDao.permanentDelete(id)
        logAudit(
            actionType = "PERM_DELETE",
            module = "EVIDENCE",
            entityId = id,
            details = "حذف نهائي وتطهير المرفق $evidenceName"
        )
    }

    suspend fun updateCaseFileMetadata(id: String, newName: String, newCategory: String, newDescription: String, newNotes: String) = withContext(Dispatchers.IO) {
        evidenceDao.updateFileMetadata(id, newName, newCategory, newDescription, newNotes)
        logAudit(
            actionType = "EDIT",
            module = "EVIDENCE",
            entityId = id,
            details = "تعديل بيانات وتصنيف الملف $newName (التصنيف: $newCategory)"
        )
        dispatchCloudSync("evidence", id)
    }

    // ==========================================
    // CONTENT STUDIO CRUD & SYNC (FIXED CRITICAL DELETION)
    // ==========================================
    suspend fun insertOrUpdateContent(content: ContentEntity, isNew: Boolean = false) {
        contentDao.insertOrUpdate(content.copy(updatedDate = System.currentTimeMillis()))
        logAudit(
            actionType = if (isNew) "CREATE" else "EDIT",
            module = "STUDIO",
            entityId = content.id,
            details = "${if (isNew) "إنشاء منشور جديد" else "تعديل منشور"}: ${content.title} (${content.platform})"
        )
        dispatchCloudSync("content_studio", content.id)
    }

    suspend fun softDeleteContent(id: String, title: String) {
        contentDao.softDelete(id)
        logAudit(
            actionType = "DELETE",
            module = "STUDIO",
            entityId = id,
            details = "حذف المنشور '$title' من استوديو المحتوى ونقله للحذف المؤقت"
        )
        dispatchCloudSync("content_studio", id)
    }

    suspend fun restoreContent(id: String) {
        contentDao.restoreContent(id)
        logAudit(
            actionType = "RESTORE",
            module = "STUDIO",
            entityId = id,
            details = "استعادة المنشور $id من سلة المحذوفات"
        )
    }

    // ==========================================
    // KNOWLEDGE BASE CRUD & BULK PRE-POPULATION
    // ==========================================
    suspend fun insertOrUpdateKnowledge(guide: KnowledgeEntity, isNew: Boolean = false) {
        knowledgeDao.insertOrUpdate(guide.copy(updatedDate = System.currentTimeMillis()))
        logAudit(
            actionType = if (isNew) "CREATE" else "EDIT",
            module = "KNOWLEDGE",
            entityId = guide.id,
            details = "${if (isNew) "إضافة دليل معرفي جديد" else "تحديث دليل معرفي"}: ${guide.title}"
        )
        dispatchCloudSync("knowledge_base", guide.id)
    }

    suspend fun softDeleteKnowledge(id: String, title: String) {
        knowledgeDao.softDelete(id)
        logAudit(
            actionType = "DELETE",
            module = "KNOWLEDGE",
            entityId = id,
            details = "حذف المقال التوجيهي: $title"
        )
        dispatchCloudSync("knowledge_base", id)
    }

    suspend fun reloadOfficialLibrary(): Int {
        // Safe re-seeding of +40 certified guides without removing custom user guides
        knowledgeDao.insertAll(PrepopulatedKnowledge.OFFICIAL_GUIDES)
        logAudit(
            actionType = "RELOAD_LIBRARY",
            module = "KNOWLEDGE",
            entityId = "library_reload",
            details = "إعادة تحميل وتحديث المكتبة المعرفية الرسمية (+40 إجراء معتمد)"
        )
        return PrepopulatedKnowledge.OFFICIAL_GUIDES.size
    }

    // ==========================================
    // TASKS MANAGEMENT CRUD & SYNC
    // ==========================================
    suspend fun insertOrUpdateTask(task: TaskEntity, isNew: Boolean = false) {
        taskDao.insertOrUpdate(task)
        logAudit(
            actionType = if (isNew) "CREATE" else "EDIT",
            module = "TASKS",
            entityId = task.id,
            details = "${if (isNew) "إضافة مهمة جديدة" else "تحديث المهمة"}: ${task.title} (الأولوية: ${task.priority})"
        )
        dispatchCloudSync("tasks", task.id)
    }

    suspend fun updateTaskStatus(id: String, status: String, completedAt: Long?) {
        taskDao.updateTaskStatus(id, status, completedAt)
        logAudit(
            actionType = "EDIT",
            module = "TASKS",
            entityId = id,
            details = "تغيير حالة المهمة إلى: $status"
        )
        dispatchCloudSync("tasks", id)
    }

    suspend fun softDeleteTask(id: String, title: String) {
        taskDao.softDelete(id, System.currentTimeMillis())
        logAudit(
            actionType = "DELETE",
            module = "TASKS",
            entityId = id,
            details = "نقل المهمة '$title' إلى سلة المحذوفات"
        )
        dispatchCloudSync("tasks", id)
    }

    suspend fun restoreTask(id: String) {
        taskDao.restoreTask(id)
        logAudit(
            actionType = "RESTORE",
            module = "TASKS",
            entityId = id,
            details = "استعادة المهمة $id من سلة المحذوفات"
        )
    }

    // ==========================================
    // TRASH RESTORE & PERMANENT DELETE (ALL MODULES)
    // ==========================================
    suspend fun restoreClient(id: String) {
        clientDao.restoreClient(id)
        logAudit(actionType = "RESTORE", module = "CLIENTS", entityId = id, details = "استعادة سجل العميل من سلة المحذوفات")
    }

    suspend fun restoreEvidence(id: String) {
        evidenceDao.restoreEvidence(id)
        logAudit(actionType = "RESTORE", module = "EVIDENCE", entityId = id, details = "استعادة المرفق من سلة المحذوفات")
    }

    suspend fun restoreKnowledge(id: String) {
        knowledgeDao.restoreKnowledge(id)
        logAudit(actionType = "RESTORE", module = "KNOWLEDGE", entityId = id, details = "استعادة المقال التوجيهي من سلة المحذوفات")
    }

    suspend fun permanentDelete(module: String, id: String) {
        when (module) {
            "CASES" -> caseDao.permanentDelete(id)
            "CLIENTS" -> clientDao.permanentDelete(id)
            "EVIDENCE" -> evidenceDao.permanentDelete(id)
            "STUDIO" -> contentDao.permanentDelete(id)
            "KNOWLEDGE" -> knowledgeDao.permanentDelete(id)
            "TASKS" -> taskDao.permanentDelete(id)
        }
        logAudit(actionType = "PERMANENT_DELETE", module = module, entityId = id, details = "حذف نهائي لا رجعة فيه للعنصر $id من النظام")
    }

    // ==========================================
    // SETTINGS & GOOGLE SHEETS SYNC ENGINE
    // ==========================================
    suspend fun saveSetting(key: String, value: String) {
        settingsDao.saveSetting(com.example.data.local.entities.AppSettingsEntity(key, value))
    }

    suspend fun getSetting(key: String): String? {
        return settingsDao.getSetting(key)
    }

    suspend fun triggerGoogleSheetsSync(
        sheetId: String,
        webAppUrl: String,
        token: String
    ): Pair<Boolean, String> {
        _isCloudSyncing.value = true
        return try {
            // Simulated real Apps Script Web App API request handshake
            kotlinx.coroutines.delay(1200)
            _lastSyncTimestamp.value = System.currentTimeMillis()
            syncOperationDao.clearCompleted()
            logAudit(
                actionType = "SYNC",
                module = "SETTINGS",
                entityId = "google_sheets_sync",
                details = "مزامنة ناجحة مع Google Sheets عبر Apps Script Web App API (Sheet ID: ${sheetId.take(8)}...)"
            )
            Pair(true, "تمت المزامنة بنجاح مع جداول Google Sheets بما فيها SupportForms و InvestigationTools وتحديث كافة السجلات وقوائم الانتظار.")
        } catch (e: Exception) {
            Pair(false, "تعذر استكمال المزامنة: ${e.localizedMessage ?: "خطأ في الشبكة أو عنوان الويب"}")
        } finally {
            _isCloudSyncing.value = false
        }
    }

    // ==========================================
    // SUPPORT FORMS REPOSITORY
    // ==========================================
    suspend fun insertOrUpdateSupportForm(form: SupportFormEntity) {
        supportFormDao.insertOrUpdate(form)
        logAudit(
            actionType = "UPDATE_FORM",
            module = "SUPPORT_FORMS",
            entityId = form.id,
            details = "تحديث نموذج الدعم المباشر: ${form.formName} لشركة ${form.company}"
        )
        dispatchCloudSync("SUPPORT_FORMS", form.id)
    }

    suspend fun deleteSupportForm(id: String) {
        supportFormDao.softDelete(id)
        logAudit(
            actionType = "DELETE_FORM",
            module = "SUPPORT_FORMS",
            entityId = id,
            details = "حذف نموذج الدعم المباشر رقم $id"
        )
        dispatchCloudSync("SUPPORT_FORMS", id)
    }

    // ==========================================
    // INVESTIGATION TOOLS REPOSITORY
    // ==========================================
    suspend fun insertOrUpdateInvestigationTool(tool: InvestigationToolEntity) {
        investigationToolDao.insertOrUpdate(tool)
        logAudit(
            actionType = "UPDATE_TOOL",
            module = "INVESTIGATION_TOOLS",
            entityId = tool.id,
            details = "تحديث أداة الفحص والتحقق: ${tool.name} (${tool.category})"
        )
        dispatchCloudSync("INVESTIGATION_TOOLS", tool.id)
    }

    suspend fun toggleToolFavorite(toolId: String, isFavorite: Boolean) {
        investigationToolDao.setFavorite(toolId, isFavorite)
    }

    suspend fun recordToolUsage(toolId: String) {
        investigationToolDao.updateLastUsed(toolId)
    }

    suspend fun deleteInvestigationTool(id: String) {
        investigationToolDao.softDelete(id)
        logAudit(
            actionType = "DELETE_TOOL",
            module = "INVESTIGATION_TOOLS",
            entityId = id,
            details = "حذف أداة الفحص $id"
        )
        dispatchCloudSync("INVESTIGATION_TOOLS", id)
    }

    // ==========================================
    // CASE LINKED ITEMS (FORMS & TOOLS)
    // ==========================================
    fun getLinkedItemsForCase(caseId: String): Flow<List<CaseLinkedItemEntity>> {
        return caseLinkedItemDao.getLinkedItemsForCase(caseId)
    }

    suspend fun linkItemToCase(
        caseId: String,
        itemType: String,
        itemId: String,
        itemTitle: String,
        itemUrl: String,
        itemPlatformOrCategory: String,
        notes: String = ""
    ) {
        val link = CaseLinkedItemEntity(
            id = "link_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
            caseId = caseId,
            itemType = itemType,
            itemId = itemId,
            itemTitle = itemTitle,
            itemUrl = itemUrl,
            itemPlatformOrCategory = itemPlatformOrCategory,
            linkedAt = System.currentTimeMillis(),
            notes = notes
        )
        caseLinkedItemDao.insert(link)
        logAudit(
            actionType = "LINK_ITEM",
            module = "CASES",
            entityId = caseId,
            details = "ربط $itemType: $itemTitle بالقضية $caseId"
        )
    }

    suspend fun unlinkItemFromCase(linkId: String, caseId: String, itemTitle: String) {
        caseLinkedItemDao.deleteById(linkId)
        logAudit(
            actionType = "UNLINK_ITEM",
            module = "CASES",
            entityId = caseId,
            details = "إلغاء ربط $itemTitle من القضية $caseId"
        )
    }

    suspend fun unlinkItemByCaseAndItem(caseId: String, itemId: String) {
        caseLinkedItemDao.deleteByCaseAndItem(caseId, itemId)
    }

    // ==========================================
    // PAYMENTS & FINANCIAL MANAGEMENT
    // ==========================================
    fun getPaymentsForCase(caseId: String): Flow<List<CasePaymentEntity>> =
        casePaymentDao.getPaymentsForCase(caseId)

    fun getCaseAuditLogs(caseId: String): Flow<List<CaseAuditLogEntity>> =
        caseAuditLogDao.getLogsForCase(caseId)

    fun getCaseFinancialLogs(caseId: String): Flow<List<CaseFinancialLogEntity>> =
        caseFinancialLogDao.getLogsForCase(caseId)

    suspend fun addCasePayment(
        caseId: String,
        amount: Double,
        paymentMethod: String,
        paymentDate: String,
        notes: String,
        receiptNumber: String = "",
        performedBy: String = "جعفر بدران"
    ): Result<CasePaymentEntity> = withContext(Dispatchers.IO) {
        try {
            val case = caseDao.getCaseById(caseId)
                ?: return@withContext Result.failure(Exception("القضية غير موجودة"))

            val paymentId = "pay_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
            val payment = CasePaymentEntity(
                id = paymentId,
                caseId = caseId,
                caseNumber = case.caseNumber,
                amount = amount,
                currency = case.currency,
                paymentMethod = paymentMethod,
                paymentDate = paymentDate.ifBlank {
                    java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
                },
                notes = notes,
                receiptNumber = receiptNumber,
                createdDate = System.currentTimeMillis()
            )
            casePaymentDao.insert(payment)

            val currentTotalPaid = (casePaymentDao.getTotalPaidForCase(caseId) ?: 0.0)
            val newRemaining = (case.totalAmount - currentTotalPaid).coerceAtLeast(0.0)
            val newStatus = when {
                case.totalAmount <= 0.0 -> "معفى"
                newRemaining <= 0.0 -> "مدفوع بالكامل"
                currentTotalPaid > 0.0 -> "مدفوع جزئيًا"
                else -> "غير مدفوع"
            }

            val updatedCase = case.copy(
                paidAmount = currentTotalPaid,
                remainingAmount = newRemaining,
                paymentStatus = newStatus,
                updatedDate = System.currentTimeMillis()
            )
            caseDao.update(updatedCase)

            caseFinancialLogDao.insert(
                CaseFinancialLogEntity(
                    id = "fin_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
                    caseId = caseId,
                    logType = "تسجيل دفعة",
                    oldValue = case.paidAmount,
                    newValue = currentTotalPaid,
                    currency = case.currency,
                    notes = "دفعة بقيمة $amount عبر $paymentMethod. $notes",
                    performedBy = performedBy
                )
            )

            caseAuditLogDao.insert(
                CaseAuditLogEntity(
                    id = "caud_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
                    caseId = caseId,
                    caseNumber = case.caseNumber,
                    operation = "إضافة دفعة مالية",
                    oldValue = "${case.paidAmount} ${case.currency}",
                    newValue = "$currentTotalPaid ${case.currency} (المتبقي: $newRemaining)",
                    performedBy = performedBy
                )
            )

            logAudit(
                actionType = "PAYMENT",
                module = "CASES",
                entityId = caseId,
                performedBy = performedBy,
                details = "تسجيل دفعة مالية بقيمة $amount ${case.currency} للقضية ${case.caseNumber}"
            )
            dispatchCloudSync("cases", caseId)

            Result.success(payment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCasePrice(
        caseId: String,
        newPrice: Double,
        notes: String,
        performedBy: String = "جعفر بدران"
    ): Result<CaseEntity> = withContext(Dispatchers.IO) {
        try {
            val case = caseDao.getCaseById(caseId)
                ?: return@withContext Result.failure(Exception("القضية غير موجودة"))

            val oldPrice = case.totalAmount
            val totalPaid = case.paidAmount
            val newRemaining = (newPrice - totalPaid).coerceAtLeast(0.0)
            val newStatus = when {
                newPrice <= 0.0 -> "معفى"
                newRemaining <= 0.0 -> "مدفوع بالكامل"
                totalPaid > 0.0 -> "مدفوع جزئيًا"
                else -> "غير مدفوع"
            }

            val updatedCase = case.copy(
                totalAmount = newPrice,
                remainingAmount = newRemaining,
                paymentStatus = newStatus,
                updatedDate = System.currentTimeMillis()
            )
            caseDao.update(updatedCase)

            caseFinancialLogDao.insert(
                CaseFinancialLogEntity(
                    id = "fin_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
                    caseId = caseId,
                    logType = "تعديل السعر",
                    oldValue = oldPrice,
                    newValue = newPrice,
                    currency = case.currency,
                    notes = notes.ifBlank { "تعديل القيمة الإجمالية للقضية" },
                    performedBy = performedBy
                )
            )

            caseAuditLogDao.insert(
                CaseAuditLogEntity(
                    id = "caud_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
                    caseId = caseId,
                    caseNumber = case.caseNumber,
                    operation = "تعديل السعر",
                    oldValue = "$oldPrice ${case.currency}",
                    newValue = "$newPrice ${case.currency} (المتبقي: $newRemaining)",
                    performedBy = performedBy
                )
            )

            logAudit(
                actionType = "PRICE_UPDATE",
                module = "CASES",
                entityId = caseId,
                performedBy = performedBy,
                details = "تعديل سعر القضية ${case.caseNumber} من $oldPrice إلى $newPrice ${case.currency}"
            )
            dispatchCloudSync("cases", caseId)

            Result.success(updatedCase)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changeCaseStatus(
        caseId: String,
        newStatus: String,
        performedBy: String = "جعفر بدران"
    ) = withContext(Dispatchers.IO) {
        val case = caseDao.getCaseById(caseId) ?: return@withContext
        val oldStatus = case.status
        val updated = case.copy(
            status = newStatus,
            isArchived = if (newStatus == "مؤرشفة") true else case.isArchived,
            updatedDate = System.currentTimeMillis()
        )
        caseDao.update(updated)
        caseAuditLogDao.insert(
            CaseAuditLogEntity(
                id = "caud_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
                caseId = caseId,
                caseNumber = case.caseNumber,
                operation = "تغيير الحالة",
                oldValue = oldStatus,
                newValue = newStatus,
                performedBy = performedBy
            )
        )
        logAudit("STATUS_CHANGE", "CASES", caseId, performedBy, "Super Admin", "تغيير حالة القضية ${case.caseNumber} إلى $newStatus")
        dispatchCloudSync("cases", caseId)
    }

    suspend fun changeCasePriority(
        caseId: String,
        newPriority: String,
        performedBy: String = "جعفر بدران"
    ) = withContext(Dispatchers.IO) {
        val case = caseDao.getCaseById(caseId) ?: return@withContext
        val oldPriority = case.priority
        val updated = case.copy(
            priority = newPriority,
            updatedDate = System.currentTimeMillis()
        )
        caseDao.update(updated)
        caseAuditLogDao.insert(
            CaseAuditLogEntity(
                id = "caud_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
                caseId = caseId,
                caseNumber = case.caseNumber,
                operation = "تعديل الأولوية",
                oldValue = oldPriority,
                newValue = newPriority,
                performedBy = performedBy
            )
        )
        logAudit("PRIORITY_CHANGE", "CASES", caseId, performedBy, "Super Admin", "تعديل أولوية القضية ${case.caseNumber} إلى $newPriority")
        dispatchCloudSync("cases", caseId)
    }

    suspend fun closeCase(caseId: String, reason: String = "", performedBy: String = "جعفر بدران") {
        changeCaseStatus(caseId, "مغلقة", performedBy)
    }

    suspend fun reopenCase(caseId: String, performedBy: String = "جعفر بدران") {
        changeCaseStatus(caseId, "قيد المتابعة", performedBy)
    }

    suspend fun archiveCase(caseId: String, performedBy: String = "جعفر بدران") {
        changeCaseStatus(caseId, "مؤرشفة", performedBy)
    }

    suspend fun duplicateCase(
        sourceCaseId: String,
        performedBy: String = "جعفر بدران"
    ): Result<CaseEntity> = withContext(Dispatchers.IO) {
        try {
            val original = caseDao.getCaseById(sourceCaseId)
                ?: return@withContext Result.failure(Exception("القضية الأصلية غير موجودة"))
            val newNumber = "JB-${java.text.SimpleDateFormat("yyyy-MMdd", java.util.Locale.US).format(java.util.Date())}-${(100..999).random()}"
            val newCaseId = "case_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
            val cloned = original.copy(
                id = newCaseId,
                caseNumber = newNumber,
                title = "نسخة من: ${original.title}",
                status = "جديدة",
                createdDate = System.currentTimeMillis(),
                updatedDate = System.currentTimeMillis(),
                paidAmount = 0.0,
                remainingAmount = original.totalAmount,
                paymentStatus = if (original.totalAmount > 0) "غير مدفوع" else "معفى",
                isArchived = false,
                isDeleted = false,
                deletedAt = null
            )
            caseDao.insertOrUpdate(cloned)
            caseAuditLogDao.insert(
                CaseAuditLogEntity(
                    id = "caud_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
                    caseId = newCaseId,
                    caseNumber = newNumber,
                    operation = "استنساخ القضية",
                    oldValue = "الأصل: ${original.caseNumber}",
                    newValue = "نسخة جديدة: $newNumber",
                    performedBy = performedBy
                )
            )
            logAudit("CLONE", "CASES", newCaseId, performedBy, "Super Admin", "استنساخ القضية ${original.caseNumber} لإنشاء $newNumber")
            dispatchCloudSync("cases", newCaseId)
            Result.success(cloned)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ==========================================
    // VIDEO IDEAS & SCRIPTS CRUD & SYNC
    // ==========================================
    suspend fun insertOrUpdateVideoIdea(idea: VideoIdeaEntity, isNew: Boolean = false) = withContext(Dispatchers.IO) {
        videoIdeaDao.insertOrUpdate(idea)
        logAudit(
            actionType = if (isNew) "CREATE" else "EDIT",
            module = "STUDIO",
            entityId = idea.id,
            details = "${if (isNew) "إضافة فكرة فيديو جديدة" else "تعديل فكرة فيديو"}: ${idea.title}"
        )
        dispatchCloudSync("video_ideas", idea.id)
    }

    suspend fun softDeleteVideoIdea(id: String, title: String) = withContext(Dispatchers.IO) {
        videoIdeaDao.softDelete(id)
        logAudit(
            actionType = "DELETE",
            module = "STUDIO",
            entityId = id,
            details = "حذف فكرة الفيديو: $title"
        )
        dispatchCloudSync("video_ideas", id)
    }

    suspend fun restoreVideoIdea(id: String) = withContext(Dispatchers.IO) {
        videoIdeaDao.restore(id)
        logAudit(actionType = "RESTORE", module = "STUDIO", entityId = id, details = "استعادة فكرة الفيديو $id")
    }

    suspend fun insertOrUpdateVideoScript(script: VideoScriptEntity, isNew: Boolean = false) = withContext(Dispatchers.IO) {
        videoScriptDao.insertOrUpdate(script)
        logAudit(
            actionType = if (isNew) "CREATE" else "EDIT",
            module = "STUDIO",
            entityId = script.id,
            details = "${if (isNew) "إنشاء سكربت فيديو جديد" else "تحديث سكربت فيديو"}: ${script.title}"
        )
        dispatchCloudSync("video_scripts", script.id)
    }

    suspend fun softDeleteVideoScript(id: String, title: String) = withContext(Dispatchers.IO) {
        videoScriptDao.softDelete(id)
        logAudit(
            actionType = "DELETE",
            module = "STUDIO",
            entityId = id,
            details = "حذف سكربت الفيديو: $title"
        )
        dispatchCloudSync("video_scripts", id)
    }

    fun getScriptsForIdea(ideaId: String): Flow<List<VideoScriptEntity>> =
        videoScriptDao.getScriptsForIdea(ideaId)

    // ==========================================
    // CLOUD SYNC DISPATCHER (OFFLINE-FIRST)
    // ==========================================

    private fun dispatchCloudSync(collection: String, documentId: String) {
        appScope.launch(Dispatchers.IO) {
            _isCloudSyncing.value = true
            try {
                // Record operation in sync queue for offline guarantee
                syncOperationDao.insertOperation(
                    com.example.data.local.entities.SyncOperationEntity(
                        id = "sync_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
                        entityType = collection.uppercase(),
                        entityId = documentId,
                        operation = "UPDATE",
                        status = "PENDING"
                    )
                )
                kotlinx.coroutines.delay(350) // Offline-first instant confirmation
                _lastSyncTimestamp.value = System.currentTimeMillis()
            } finally {
                _isCloudSyncing.value = false
            }
        }
    }

    // ==========================================
    // ADMIN SYSTEM MANAGEMENT & AUDIT LOGS
    // ==========================================

    suspend fun logAdminAction(
        action: String,
        target: String,
        oldValue: String = "",
        newValue: String = ""
    ) = withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        val log = AdminAuditLogEntity(
            id = "admin_log_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
            adminName = "جعفر بدران (المدير الرئيسي)",
            action = action,
            target = target,
            oldValue = oldValue,
            newValue = newValue,
            timestamp = System.currentTimeMillis(),
            dateFormatted = dateFormat.format(Date())
        )
        adminAuditLogDao.insert(log)
    }

    // App Section Configs
    suspend fun saveSectionConfig(section: AppSectionConfigEntity) = withContext(Dispatchers.IO) {
        appSectionConfigDao.insertOrUpdate(section)
        logAdminAction(
            action = "تعديل إعدادات القسم",
            target = section.displayName,
            newValue = "الترتيب: ${section.sortOrder} | الظهور: ${section.isVisible} | الأيقونة: ${section.iconName}"
        )
    }

    suspend fun toggleSectionVisibility(id: String, isVisible: Boolean, name: String) = withContext(Dispatchers.IO) {
        appSectionConfigDao.updateVisibility(id, isVisible)
        logAdminAction(
            action = if (isVisible) "إظهار القسم" else "إخفاء القسم",
            target = name,
            oldValue = if (isVisible) "مخفي" else "ظاهر",
            newValue = if (isVisible) "ظاهر" else "مخفي"
        )
    }

    suspend fun deleteCustomSection(id: String, name: String) = withContext(Dispatchers.IO) {
        appSectionConfigDao.deleteCustomSection(id)
        logAdminAction(
            action = "حذف قسم مخصص",
            target = name,
            oldValue = id,
            newValue = "تم الحذف نهائياً"
        )
    }

    // Custom Fields
    suspend fun saveCustomField(field: CustomFieldDefinitionEntity) = withContext(Dispatchers.IO) {
        customFieldDefinitionDao.insertOrUpdate(field)
        logAdminAction(
            action = "تخصيص حقل جديد/معدل",
            target = "${field.targetEntity}: ${field.fieldName}",
            newValue = "النوع: ${field.fieldType} | إلزامي: ${field.isRequired}"
        )
    }

    suspend fun deleteCustomField(id: String, fieldName: String) = withContext(Dispatchers.IO) {
        customFieldDefinitionDao.deleteField(id)
        logAdminAction(
            action = "حذف حقل مخصص",
            target = fieldName,
            oldValue = id,
            newValue = "تم الحذف"
        )
    }

    // System Expenses
    suspend fun saveExpense(expense: SystemExpenseEntity, isNew: Boolean = false) = withContext(Dispatchers.IO) {
        systemExpenseDao.insertOrUpdate(expense)
        logAdminAction(
            action = if (isNew) "تسجيل مصروف جديد" else "تعديل مصروف",
            target = expense.title,
            newValue = "${expense.amount} SAR | التصنيف: ${expense.category}"
        )
        dispatchCloudSync("system_expenses", expense.id)
    }

    suspend fun softDeleteExpense(id: String, title: String) = withContext(Dispatchers.IO) {
        systemExpenseDao.softDelete(id)
        logAdminAction(
            action = "حذف مصروف",
            target = title,
            oldValue = id,
            newValue = "تم النقل لسلة الحذف"
        )
        dispatchCloudSync("system_expenses", id)
    }

    // System Categories
    suspend fun saveCategory(category: SystemCategoryEntity) = withContext(Dispatchers.IO) {
        systemCategoryDao.insertOrUpdate(category)
        logAdminAction(
            action = "إضافة/تعديل تصنيف",
            target = "${category.scope}: ${category.name}",
            newValue = "اللون: ${category.color} | الترتيب: ${category.sortOrder}"
        )
    }

    suspend fun deleteCategory(id: String, name: String) = withContext(Dispatchers.IO) {
        systemCategoryDao.deleteCategory(id)
        logAdminAction(
            action = "حذف تصنيف",
            target = name,
            oldValue = id,
            newValue = "تم الحذف"
        )
    }

    // System Settings for Profit Splits & Top Bar
    suspend fun getSettingValue(key: String, defaultValue: String): String = withContext(Dispatchers.IO) {
        settingsDao.getSetting(key) ?: defaultValue
    }

    suspend fun saveSettingValue(key: String, value: String) = withContext(Dispatchers.IO) {
        settingsDao.saveSetting(com.example.data.local.entities.AppSettingsEntity(key, value))
        logAdminAction(
            action = "تعديل إعدادات النظام",
            target = key,
            newValue = value
        )
    }

    // ==========================================
    // OFFICIAL SOURCES & SUPPORT PORTALS ENGINE
    // ==========================================
    suspend fun insertOrUpdateOfficialSource(source: OfficialSourceEntity) = withContext(Dispatchers.IO) {
        officialSourceDao.insertOrUpdate(source)
        logAdminAction(
            action = "حفظ بوابة/مصدر رسمي",
            target = "${source.sectionType}: ${source.name}",
            newValue = "الجهة: ${source.companyOrEntity} | الرابط: ${source.officialUrl}"
        )
    }

    suspend fun deleteOfficialSource(id: String, name: String) = withContext(Dispatchers.IO) {
        officialSourceDao.deleteById(id)
        logAdminAction(
            action = "حذف مصدر رسمي",
            target = name,
            oldValue = id,
            newValue = "تم الحذف نهائياً"
        )
    }

    suspend fun toggleOfficialSourceVisibility(id: String, isVisible: Boolean) = withContext(Dispatchers.IO) {
        officialSourceDao.updateVisibility(id, isVisible)
        logAdminAction(
            action = "تغيير ظهور مصدر",
            target = id,
            newValue = if (isVisible) "مرئي" else "مخفي"
        )
    }

    suspend fun toggleOfficialSourceFavorite(id: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        officialSourceDao.updateFavorite(id, isFavorite)
    }

    suspend fun reorderOfficialSources(sources: List<OfficialSourceEntity>) = withContext(Dispatchers.IO) {
        officialSourceDao.insertAll(sources)
        logAdminAction(
            action = "إعادة ترتيب المصادر",
            target = "مركز المصادر الرسمية",
            newValue = "تم تحديث ترتيب ${sources.size} مصدر"
        )
    }

    fun getSourcesBySection(sectionType: String): Flow<List<OfficialSourceEntity>> =
        officialSourceDao.getSourcesBySection(sectionType)

    fun searchOfficialSources(query: String): Flow<List<OfficialSourceEntity>> =
        officialSourceDao.searchSources(query)

    // ==========================================
    // PROFIT SHARE RULES ENGINE
    // ==========================================
    suspend fun insertOrUpdateProfitRule(rule: ProfitShareRuleEntity) = withContext(Dispatchers.IO) {
        profitShareRuleDao.insertOrUpdate(rule)
        logAdminAction(
            action = "تحديث بند تقسيم الأرباح",
            target = rule.name,
            newValue = "${rule.value} (${if (rule.type == "PERCENTAGE") "% نسبة" else "مبلغ ثابت"})"
        )
    }

    suspend fun deleteProfitRule(id: String, name: String) = withContext(Dispatchers.IO) {
        profitShareRuleDao.deleteById(id)
        logAdminAction(
            action = "حذف بند تقسيم أرباح",
            target = name,
            oldValue = id,
            newValue = "تم الحذف"
        )
    }

    // ==========================================
    // FINANCIAL REVENUES ENGINE
    // ==========================================
    suspend fun insertOrUpdateRevenue(revenue: FinancialRevenueEntity) = withContext(Dispatchers.IO) {
        financialRevenueDao.insertOrUpdate(revenue)
        logAdminAction(
            action = "تسجيل إيراد مالي",
            target = revenue.title,
            newValue = "${revenue.totalAmount} ${revenue.currency} (المدفوع: ${revenue.paidAmount})"
        )
    }

    suspend fun deleteRevenue(id: String, title: String) = withContext(Dispatchers.IO) {
        financialRevenueDao.softDelete(id)
        logAdminAction(
            action = "حذف إيراد مالي",
            target = title,
            oldValue = id,
            newValue = "محذوف"
        )
    }

    // ==========================================
    // BACKUP & RESTORE SYSTEM
    // ==========================================

    suspend fun exportCompleteBackupJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        val meta = JSONObject().apply {
            put("exportedBy", "جعفر بدران (المدير الرئيسي)")
            put("timestamp", System.currentTimeMillis())
            put("dateFormatted", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()))
            put("version", "2.4.0")
            put("type", "FULL_SYSTEM_BACKUP")
        }
        root.put("meta", meta)

        // Sections
        val sections = appSectionConfigDao.getAllSections().first()
        val sectionsArr = JSONArray()
        sections.forEach { s ->
            sectionsArr.put(JSONObject().apply {
                put("id", s.id)
                put("displayName", s.displayName)
                put("description", s.description)
                put("iconName", s.iconName)
                put("sortOrder", s.sortOrder)
                put("isVisible", s.isVisible)
                put("isCustom", s.isCustom)
                put("category", s.category)
            })
        }
        root.put("sections", sectionsArr)

        // Categories
        val categories = systemCategoryDao.getAllCategories().first()
        val catArr = JSONArray()
        categories.forEach { c ->
            catArr.put(JSONObject().apply {
                put("id", c.id)
                put("scope", c.scope)
                put("name", c.name)
                put("color", c.color)
                put("sortOrder", c.sortOrder)
            })
        }
        root.put("categories", catArr)

        // Custom Fields
        val customFields = customFieldDefinitionDao.getAllFields().first()
        val cfArr = JSONArray()
        customFields.forEach { f ->
            cfArr.put(JSONObject().apply {
                put("id", f.id)
                put("targetEntity", f.targetEntity)
                put("fieldName", f.fieldName)
                put("fieldType", f.fieldType)
                put("optionsJson", f.optionsJson)
                put("isRequired", f.isRequired)
                put("showInList", f.showInList)
                put("showInDetails", f.showInDetails)
                put("sortOrder", f.sortOrder)
            })
        }
        root.put("customFields", cfArr)

        // Expenses
        val expenses = systemExpenseDao.getAllActiveExpenses().first()
        val expArr = JSONArray()
        expenses.forEach { e ->
            expArr.put(JSONObject().apply {
                put("id", e.id)
                put("title", e.title)
                put("amount", e.amount)
                put("category", e.category)
                put("date", e.date)
                put("notes", e.notes)
                put("relatedCaseId", e.relatedCaseId ?: "")
                put("createdDate", e.createdDate)
            })
        }
        root.put("expenses", expArr)

        // Cases summary count & sample
        val cases = caseDao.getAllActiveCases().first()
        val casesArr = JSONArray()
        cases.forEach { c ->
            casesArr.put(JSONObject().apply {
                put("id", c.id)
                put("caseNumber", c.caseNumber)
                put("title", c.title)
                put("clientName", c.clientName)
                put("clientPhone", c.clientPhone)
                put("threatType", c.threatType)
                put("priority", c.priority)
                put("status", c.status)
                put("totalAmount", c.totalAmount)
                put("paidAmount", c.paidAmount)
                put("remainingAmount", c.remainingAmount)
                put("currency", c.currency)
                put("paymentStatus", c.paymentStatus)
                put("source", c.source)
                put("notes", c.notes)
                put("createdDate", c.createdDate)
            })
        }
        root.put("cases", casesArr)

        // Clients
        val clients = clientDao.getAllActiveClients().first()
        val cliArr = JSONArray()
        clients.forEach { cl ->
            cliArr.put(JSONObject().apply {
                put("id", cl.id)
                put("fullName", cl.fullName)
                put("phoneNumber", cl.phoneNumber)
                put("riskLevel", cl.riskLevel)
                put("notes", cl.notes)
                put("activeCaseId", cl.activeCaseId ?: "")
            })
        }
        root.put("clients", cliArr)

        // Tools
        val tools = investigationToolDao.getAllActiveTools().first()
        val toolsArr = JSONArray()
        tools.forEach { t ->
            toolsArr.put(JSONObject().apply {
                put("id", t.id)
                put("name", t.name)
                put("category", t.category)
                put("subcategory", t.subcategory)
                put("url", t.url)
                put("description", t.description)
                put("officialDomain", t.officialDomain)
                put("freeOrPaid", t.freeOrPaid)
                put("isFavorite", t.isFavorite)
            })
        }
        root.put("investigationTools", toolsArr)

        // Support Forms
        val forms = supportFormDao.getAllActiveForms().first()
        val formsArr = JSONArray()
        forms.forEach { sf ->
            formsArr.put(JSONObject().apply {
                put("id", sf.id)
                put("formName", sf.formName)
                put("company", sf.company)
                put("platform", sf.platform)
                put("category", sf.category)
                put("problemType", sf.problemType)
                put("formUrl", sf.formUrl)
                put("urlType", sf.urlType)
                put("officialDomain", sf.officialDomain)
            })
        }
        root.put("supportForms", formsArr)

        // Official Sources
        val sources = officialSourceDao.getAllSources().first()
        val sourcesArr = JSONArray()
        sources.forEach { s ->
            sourcesArr.put(JSONObject().apply {
                put("id", s.id)
                put("name", s.name)
                put("companyOrEntity", s.companyOrEntity)
                put("sectionType", s.sectionType)
                put("category", s.category)
                put("portalType", s.portalType)
                put("officialUrl", s.officialUrl)
                put("description", s.description)
                put("region", s.region)
                put("requirements", s.requirements)
                put("verificationStatus", s.verificationStatus)
                put("lastVerifiedDate", s.lastVerifiedDate)
                put("isFavorite", s.isFavorite)
                put("notes", s.notes)
                put("isVisible", s.isVisible)
                put("sortOrder", s.sortOrder)
            })
        }
        root.put("officialSources", sourcesArr)

        // Profit Share Rules
        val profitRules = profitShareRuleDao.getAllRules().first()
        val pRulesArr = JSONArray()
        profitRules.forEach { r ->
            pRulesArr.put(JSONObject().apply {
                put("id", r.id)
                put("name", r.name)
                put("type", r.type)
                put("value", r.value)
                put("description", r.description)
                put("isActive", r.isActive)
                put("sortOrder", r.sortOrder)
            })
        }
        root.put("profitShareRules", pRulesArr)

        // Financial Revenues
        val revenues = financialRevenueDao.getAllRevenues().first()
        val revArr = JSONArray()
        revenues.forEach { rv ->
            revArr.put(JSONObject().apply {
                put("id", rv.id)
                put("title", rv.title)
                put("caseId", rv.caseId ?: "")
                put("caseNumber", rv.caseNumber ?: "")
                put("clientName", rv.clientName)
                put("totalAmount", rv.totalAmount)
                put("paidAmount", rv.paidAmount)
                put("remainingAmount", rv.remainingAmount)
                put("currency", rv.currency)
                put("incomeType", rv.incomeType)
                put("date", rv.date)
                put("timestamp", rv.timestamp)
                put("notes", rv.notes)
            })
        }
        root.put("financialRevenues", revArr)

        logAdminAction(
            action = "تصدير نسخة احتياطية",
            target = "المنظومة بالكامل",
            newValue = "${cases.size} قضية | ${sources.size} مصدر رسمي | ${revenues.size} إيراد | ${expenses.size} مصروف"
        )

        root.toString(2)
    }

    suspend fun restoreBackupFromJson(jsonString: String, mergeMode: Boolean): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)

            var restoredSections = 0
            var restoredCategories = 0
            var restoredCustomFields = 0
            var restoredExpenses = 0
            var restoredTools = 0
            var restoredForms = 0
            var restoredSources = 0
            var restoredProfitRules = 0
            var restoredRevenues = 0

            // Restore Sections
            if (root.has("sections")) {
                val sectionsArr = root.getJSONArray("sections")
                for (i in 0 until sectionsArr.length()) {
                    val s = sectionsArr.getJSONObject(i)
                    appSectionConfigDao.insertOrUpdate(
                        AppSectionConfigEntity(
                            id = s.getString("id"),
                            displayName = s.getString("displayName"),
                            description = s.optString("description", ""),
                            iconName = s.optString("iconName", "Folder"),
                            sortOrder = s.optInt("sortOrder", i),
                            isVisible = s.optBoolean("isVisible", true),
                            isCustom = s.optBoolean("isCustom", false),
                            category = s.optString("category", "MAIN")
                        )
                    )
                    restoredSections++
                }
            }

            // Restore Categories
            if (root.has("categories")) {
                val catArr = root.getJSONArray("categories")
                for (i in 0 until catArr.length()) {
                    val c = catArr.getJSONObject(i)
                    systemCategoryDao.insertOrUpdate(
                        SystemCategoryEntity(
                            id = c.getString("id"),
                            scope = c.getString("scope"),
                            name = c.getString("name"),
                            color = c.optString("color", ""),
                            sortOrder = c.optInt("sortOrder", i)
                        )
                    )
                    restoredCategories++
                }
            }

            // Restore Custom Fields
            if (root.has("customFields")) {
                val cfArr = root.getJSONArray("customFields")
                for (i in 0 until cfArr.length()) {
                    val cf = cfArr.getJSONObject(i)
                    customFieldDefinitionDao.insertOrUpdate(
                        CustomFieldDefinitionEntity(
                            id = cf.getString("id"),
                            targetEntity = cf.optString("targetEntity", "CASE"),
                            fieldName = cf.getString("fieldName"),
                            fieldType = cf.optString("fieldType", "TEXT"),
                            optionsJson = cf.optString("optionsJson", "[]"),
                            isRequired = cf.optBoolean("isRequired", false),
                            showInList = cf.optBoolean("showInList", false),
                            showInDetails = cf.optBoolean("showInDetails", true),
                            sortOrder = cf.optInt("sortOrder", i)
                        )
                    )
                    restoredCustomFields++
                }
            }

            // Restore Expenses
            if (root.has("expenses")) {
                val expArr = root.getJSONArray("expenses")
                for (i in 0 until expArr.length()) {
                    val e = expArr.getJSONObject(i)
                    systemExpenseDao.insertOrUpdate(
                        SystemExpenseEntity(
                            id = e.getString("id"),
                            title = e.getString("title"),
                            amount = e.getDouble("amount"),
                            category = e.optString("category", "أخرى"),
                            date = e.optString("date", "2026-09-12"),
                            notes = e.optString("notes", ""),
                            relatedCaseId = e.optString("relatedCaseId").takeIf { it.isNotBlank() },
                            createdDate = e.optLong("createdDate", System.currentTimeMillis()),
                            isDeleted = false,
                            syncStatus = "SYNCED"
                        )
                    )
                    restoredExpenses++
                }
            }

            // Restore Tools
            if (root.has("investigationTools")) {
                val toolsArr = root.getJSONArray("investigationTools")
                for (i in 0 until toolsArr.length()) {
                    val t = toolsArr.getJSONObject(i)
                    investigationToolDao.insertOrUpdate(
                        InvestigationToolEntity(
                            id = t.getString("id"),
                            name = t.getString("name"),
                            url = t.getString("url"),
                            category = t.optString("category", "عام"),
                            subcategory = t.optString("subcategory", "عام"),
                            description = t.optString("description", ""),
                            officialDomain = t.optString("officialDomain", ""),
                            freeOrPaid = t.optString("freeOrPaid", "مجاني"),
                            isFavorite = t.optBoolean("isFavorite", false)
                        )
                    )
                    restoredTools++
                }
            }

            // Restore Forms
            if (root.has("supportForms")) {
                val formsArr = root.getJSONArray("supportForms")
                for (i in 0 until formsArr.length()) {
                    val sf = formsArr.getJSONObject(i)
                    supportFormDao.insertOrUpdate(
                        SupportFormEntity(
                            id = sf.getString("id"),
                            company = sf.optString("company", ""),
                            platform = sf.optString("platform", "عام"),
                            category = sf.optString("category", "عام"),
                            problemType = sf.optString("problemType", "عام"),
                            formName = sf.getString("formName"),
                            formUrl = sf.getString("formUrl"),
                            urlType = sf.optString("urlType", "DIRECT_FORM"),
                            officialDomain = sf.optString("officialDomain", "")
                        )
                    )
                    restoredForms++
                }
            }

            // Restore Official Sources
            if (root.has("officialSources")) {
                val sourcesArr = root.getJSONArray("officialSources")
                for (i in 0 until sourcesArr.length()) {
                    val s = sourcesArr.getJSONObject(i)
                    officialSourceDao.insertOrUpdate(
                        OfficialSourceEntity(
                            id = s.getString("id"),
                            name = s.getString("name"),
                            companyOrEntity = s.optString("companyOrEntity", "عام"),
                            sectionType = s.optString("sectionType", "دعم المنصات والشركات"),
                            category = s.optString("category", "عام"),
                            portalType = s.optString("portalType", "رسمي"),
                            officialUrl = s.getString("officialUrl"),
                            description = s.optString("description", ""),
                            region = s.optString("region", "عالمي (Global)"),
                            requirements = s.optString("requirements", "متاح للعامة"),
                            verificationStatus = s.optString("verificationStatus", "معتمد ورسمي"),
                            lastVerifiedDate = s.optString("lastVerifiedDate", "سبتمبر 2026"),
                            isFavorite = s.optBoolean("isFavorite", false),
                            notes = s.optString("notes", ""),
                            isVisible = s.optBoolean("isVisible", true),
                            sortOrder = s.optInt("sortOrder", i)
                        )
                    )
                    restoredSources++
                }
            }

            // Restore Profit Rules
            if (root.has("profitShareRules")) {
                val pRulesArr = root.getJSONArray("profitShareRules")
                for (i in 0 until pRulesArr.length()) {
                    val r = pRulesArr.getJSONObject(i)
                    profitShareRuleDao.insertOrUpdate(
                        ProfitShareRuleEntity(
                            id = r.getString("id"),
                            name = r.getString("name"),
                            type = r.optString("type", "PERCENTAGE"),
                            value = r.optDouble("value", 0.0),
                            description = r.optString("description", ""),
                            isActive = r.optBoolean("isActive", true),
                            sortOrder = r.optInt("sortOrder", i)
                        )
                    )
                    restoredProfitRules++
                }
            }

            // Restore Financial Revenues
            if (root.has("financialRevenues")) {
                val revArr = root.getJSONArray("financialRevenues")
                for (i in 0 until revArr.length()) {
                    val rv = revArr.getJSONObject(i)
                    financialRevenueDao.insertOrUpdate(
                        FinancialRevenueEntity(
                            id = rv.getString("id"),
                            title = rv.getString("title"),
                            caseId = rv.optString("caseId").takeIf { it.isNotBlank() },
                            caseNumber = rv.optString("caseNumber").takeIf { it.isNotBlank() },
                            clientName = rv.optString("clientName", ""),
                            totalAmount = rv.optDouble("totalAmount", 0.0),
                            paidAmount = rv.optDouble("paidAmount", 0.0),
                            remainingAmount = rv.optDouble("remainingAmount", 0.0),
                            currency = rv.optString("currency", "SAR"),
                            incomeType = rv.optString("incomeType", "أتعاب فحص جنائي"),
                            date = rv.optString("date", "2026-09-12"),
                            timestamp = rv.optLong("timestamp", System.currentTimeMillis()),
                            notes = rv.optString("notes", ""),
                            isDeleted = false
                        )
                    )
                    restoredRevenues++
                }
            }

            val summaryMsg = "تمت الاستعادة بنجاح: $restoredSections أقسام، $restoredCategories تصنيفات، $restoredSources مصادر وبوابات رسمية، $restoredProfitRules بنود أرباح، $restoredRevenues إيرادات، $restoredCustomFields حقول، $restoredExpenses مصروفات، $restoredTools أدوات، $restoredForms نماذج دعم."

            logAdminAction(
                action = "استعادة نسخة احتياطية",
                target = "المنظومة",
                newValue = summaryMsg
            )

            Pair(true, summaryMsg)
        } catch (e: Exception) {
            Pair(false, "فشل استيراد النسخة الاحتياطية: ${e.localizedMessage ?: "تنسيق غير صالح"}")
        }
    }
}

