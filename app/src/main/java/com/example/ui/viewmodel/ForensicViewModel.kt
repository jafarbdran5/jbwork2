package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.CaseAuditLogEntity
import com.example.data.local.entities.CaseEntity
import com.example.data.local.entities.CaseFinancialLogEntity
import com.example.data.local.entities.CaseLinkedItemEntity
import com.example.data.local.entities.CasePaymentEntity
import com.example.data.local.entities.ClientEntity
import com.example.data.local.entities.ContentEntity
import com.example.data.local.entities.EvidenceEntity
import com.example.data.local.entities.ExternalRequestEntity
import com.example.data.local.entities.ExternalRequestSourceEntity
import com.example.data.local.entities.ExternalSheetEntity
import com.example.data.local.entities.InvestigationToolEntity
import com.example.data.local.entities.KnowledgeEntity
import com.example.data.local.entities.SupportFormEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.VideoIdeaEntity
import com.example.data.local.entities.VideoScriptEntity
import com.example.data.remote.SheetReadResult
import com.example.data.repository.ExternalRequestsRepository
import com.example.data.repository.ForensicRepository
import com.example.ui.components.ForensicCrypto
import com.example.ui.components.HudMessage
import com.example.ui.components.HudType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class GlobalSearchResults(
    val cases: List<CaseEntity> = emptyList(),
    val clients: List<ClientEntity> = emptyList(),
    val evidence: List<EvidenceEntity> = emptyList(),
    val content: List<ContentEntity> = emptyList(),
    val knowledge: List<KnowledgeEntity> = emptyList(),
    val tasks: List<TaskEntity> = emptyList(),
    val supportForms: List<SupportFormEntity> = emptyList(),
    val investigationTools: List<InvestigationToolEntity> = emptyList(),
    val videoIdeas: List<VideoIdeaEntity> = emptyList(),
    val videoScripts: List<VideoScriptEntity> = emptyList()
) {
    val totalCount: Int get() = cases.size + clients.size + evidence.size + content.size + knowledge.size + tasks.size + supportForms.size + investigationTools.size + videoIdeas.size + videoScripts.size
}


class ForensicViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = ForensicRepository(database, viewModelScope)

    // Current Role & Auth
    val roles = listOf("مدير عام (جعفر بدران)", "مسؤول متابعة القضايا", "محلل تقني أول", "مساعد إداري")
    private val _currentRole = MutableStateFlow(roles[0])
    val currentRole = _currentRole.asStateFlow()

    private val _isBiometricUnlocked = MutableStateFlow(true)
    val isBiometricUnlocked = _isBiometricUnlocked.asStateFlow()

    // App Security & Lock Settings
    private val _isLockEnabled = MutableStateFlow(true)
    val isLockEnabled = _isLockEnabled.asStateFlow()

    private val _appPin = MutableStateFlow("1234")
    val appPin = _appPin.asStateFlow()

    private val _autoLockInterval = MutableStateFlow("IMMEDIATE") // IMMEDIATE, 1_MIN, 5_MIN, 15_MIN, NEVER
    val autoLockInterval = _autoLockInterval.asStateFlow()

    private val _isBiometricHardwareEnabled = MutableStateFlow(true)
    val isBiometricHardwareEnabled = _isBiometricHardwareEnabled.asStateFlow()

    // Theme & Security Settings
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    private val _isScreenshotProtection = MutableStateFlow(false)
    val isScreenshotProtection = _isScreenshotProtection.asStateFlow()

    // Language Toggle: "ar" or "en"
    private val _currentLanguage = MutableStateFlow("ar")
    val currentLanguage = _currentLanguage.asStateFlow()

    // Google Sheets Sync Settings
    val sheetId = MutableStateFlow("1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms")
    val webAppUrl = MutableStateFlow("https://script.google.com/macros/s/AKfycbz_SAMPLE_APPSSCRIPT_ENDPOINT/exec")
    val apiToken = MutableStateFlow("SEC_JB_2026_TOKEN")
    val autoSyncEnabled = MutableStateFlow(true)
    val pendingSyncCount = repository.pendingSyncCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // HUD Messages (non-blocking)
    private val _hudMessage = MutableStateFlow<HudMessage?>(null)
    val hudMessage = _hudMessage.asStateFlow()

    // Search & Filter queries
    val caseSearchQuery = MutableStateFlow("")
    val caseStatusFilter = MutableStateFlow("الكل")

    val knowledgeSearchQuery = MutableStateFlow("")
    val knowledgeCategoryFilter = MutableStateFlow("الكل")

    val contentPlatformFilter = MutableStateFlow("الكل")
    val contentStatusFilter = MutableStateFlow("الكل")

    val clientSearchQuery = MutableStateFlow("")

    val taskSearchQuery = MutableStateFlow("")
    val taskStatusFilter = MutableStateFlow("الكل")
    val taskPriorityFilter = MutableStateFlow("الكل")

    // Payment & Ledger Filters
    val paymentSearchQuery = MutableStateFlow("")
    val paymentStatusFilter = MutableStateFlow("الكل") // الكل, غير مدفوع, مدفوع جزئيًا, مدفوع بالكامل, معفى
    val paymentMethodFilter = MutableStateFlow("الكل")

    // Video Ideas & Scripts Filters
    val videoIdeaSearchQuery = MutableStateFlow("")
    val videoIdeaStatusFilter = MutableStateFlow("الكل")
    val videoIdeaPlatformFilter = MutableStateFlow("الكل")
    val videoIdeaTypeFilter = MutableStateFlow("الكل")
    val videoIdeaPriorityFilter = MutableStateFlow("الكل")

    val videoScriptSearchQuery = MutableStateFlow("")
    val videoScriptStatusFilter = MutableStateFlow("الكل")

    val globalSearchQuery = MutableStateFlow("")

    // Support Forms State & Filters
    val supportFormSearchQuery = MutableStateFlow("")
    val supportFormCompanyFilter = MutableStateFlow("الكل")
    val supportFormProblemFilter = MutableStateFlow("الكل")
    val supportFormDirectOnly = MutableStateFlow(false)
    val supportFormVerifiedOnly = MutableStateFlow(true)

    // Digital Investigation Tools State & Filters
    val investigationSearchQuery = MutableStateFlow("")
    val investigationCategoryFilter = MutableStateFlow("الكل")
    val investigationCostFilter = MutableStateFlow("الكل")
    val investigationActiveTab = MutableStateFlow(0) // 0: All, 1: Favorites, 2: Recent

    // External Requests Repository & State
    val externalRequestsRepo = ExternalRequestsRepository(database)
    val rawExternalSources = externalRequestsRepo.allSources.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawExternalSheets = externalRequestsRepo.allSheets.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawExternalRequests = externalRequestsRepo.allRequests.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val isExternalSyncing = externalRequestsRepo.isSyncing
    val lastExternalSyncTimestamp = externalRequestsRepo.lastGlobalSync

    val externalRequestSearchQuery = MutableStateFlow("")
    val externalRequestStatusFilter = MutableStateFlow("الكل")
    val externalRequestSourceFilter = MutableStateFlow("الكل")
    val externalRequestSheetFilter = MutableStateFlow("الكل")
    val externalRequestUrgencyFilter = MutableStateFlow("الكل")
    val externalRequestActiveTab = MutableStateFlow("الكل")
    val externalRequestsAutoSyncInterval = MutableStateFlow("إيقاف")

    init {
        viewModelScope.launch {
            val savedTheme = repository.getSetting("app_theme")
            if (savedTheme != null) {
                _isDarkTheme.value = (savedTheme == "DARK")
            }
            val savedLang = repository.getSetting("app_language")
            if (savedLang != null) {
                _currentLanguage.value = savedLang
            }
            val savedSec = repository.getSetting("screenshot_protection")
            if (savedSec != null) {
                _isScreenshotProtection.value = (savedSec == "true")
            }
            val savedLock = repository.getSetting("security_lock_enabled")
            if (savedLock != null) {
                _isLockEnabled.value = (savedLock == "true")
            }
            val savedPin = repository.getSetting("security_app_pin")
            if (!savedPin.isNullOrBlank()) {
                _appPin.value = savedPin
            }
            val savedInterval = repository.getSetting("security_auto_lock")
            if (!savedInterval.isNullOrBlank()) {
                _autoLockInterval.value = savedInterval
            }
            val savedBio = repository.getSetting("security_biometric_enabled")
            if (savedBio != null) {
                _isBiometricHardwareEnabled.value = (savedBio == "true")
            }
            if (_isLockEnabled.value) {
                _isBiometricUnlocked.value = false
            }
        }
    }

    // Raw streams from Repository
    val rawCases = repository.allCases.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawClients = repository.allClients.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawEvidence = repository.allEvidence.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawContent = repository.allContent.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawKnowledge = repository.allKnowledge.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawTasks = repository.allTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawSupportForms = repository.allSupportForms.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawInvestigationTools = repository.allInvestigationTools.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawPayments = repository.allPayments.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawFinancialLogs = repository.allFinancialLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawVideoIdeas = repository.allVideoIdeas.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawVideoScripts = repository.allVideoScripts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val auditLogs = repository.recentAuditLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isCloudSyncing = repository.isCloudSyncing
    val lastSyncTimestamp = repository.lastSyncTimestamp

    // Filtered Support Forms Stream
    val filteredSupportForms: StateFlow<List<SupportFormEntity>> = combine(
        rawSupportForms,
        supportFormSearchQuery,
        supportFormCompanyFilter,
        supportFormProblemFilter,
        supportFormDirectOnly,
        supportFormVerifiedOnly
    ) { args: Array<Any> ->
        @Suppress("UNCHECKED_CAST")
        val forms = args[0] as List<SupportFormEntity>
        val query = args[1] as String
        val company = args[2] as String
        val problem = args[3] as String
        val directOnly = args[4] as Boolean
        val verifiedOnly = args[5] as Boolean

        val q = query.trim()
        forms.filter { form ->
            val matchesQuery = q.isBlank() ||
                    form.formName.contains(q, ignoreCase = true) ||
                    form.company.contains(q, ignoreCase = true) ||
                    form.platform.contains(q, ignoreCase = true) ||
                    form.problemType.contains(q, ignoreCase = true) ||
                    form.category.contains(q, ignoreCase = true) ||
                    form.tags.contains(q, ignoreCase = true)

            val matchesCompany = company == "الكل" || form.company.equals(company, ignoreCase = true) || form.platform.equals(company, ignoreCase = true)
            val matchesProblem = problem == "الكل" || form.problemType.contains(problem, ignoreCase = true) || form.category.contains(problem, ignoreCase = true)
            val matchesDirect = !directOnly || (form.urlType == "DIRECT_FORM" || form.urlType == "REPORT_FORM" || form.urlType == "RECOVERY_FORM" || form.urlType == "LEGAL_FORM")
            val matchesVerified = !verifiedOnly || form.verified

            matchesQuery && matchesCompany && matchesProblem && matchesDirect && matchesVerified
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Investigation Tools Stream
    val filteredInvestigationTools: StateFlow<List<InvestigationToolEntity>> = combine(
        rawInvestigationTools,
        investigationSearchQuery,
        investigationCategoryFilter,
        investigationCostFilter,
        investigationActiveTab
    ) { tools, query, category, cost, tab ->
        val q = query.trim()
        val filtered = tools.filter { tool ->
            val matchesQuery = q.isBlank() ||
                    tool.name.contains(q, ignoreCase = true) ||
                    tool.description.contains(q, ignoreCase = true) ||
                    tool.category.contains(q, ignoreCase = true) ||
                    tool.subcategory.contains(q, ignoreCase = true) ||
                    tool.officialDomain.contains(q, ignoreCase = true) ||
                    tool.tags.contains(q, ignoreCase = true)

            val matchesCategory = category == "الكل" || tool.category.contains(category, ignoreCase = true) || tool.subcategory.contains(category, ignoreCase = true)
            val matchesCost = cost == "الكل" || tool.freeOrPaid.contains(cost, ignoreCase = true)
            val matchesTab = when (tab) {
                1 -> tool.isFavorite
                2 -> tool.lastUsedAt != null
                else -> true
            }

            matchesQuery && matchesCategory && matchesCost && matchesTab
        }
        if (tab == 2) filtered.sortedByDescending { it.lastUsedAt ?: 0L } else filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // Trash Streams
    val deletedCases = repository.deletedCases.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val deletedClients = repository.deletedClients.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val deletedEvidence = repository.deletedEvidence.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val deletedContent = repository.deletedContent.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val deletedKnowledge = repository.deletedKnowledge.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val deletedTasks = repository.deletedTasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Tasks Stream
    val filteredTasks: StateFlow<List<TaskEntity>> = combine(
        rawTasks,
        taskSearchQuery,
        taskStatusFilter,
        taskPriorityFilter
    ) { tasks: List<TaskEntity>, query: String, status: String, priority: String ->
        tasks.filter { task ->
            val matchesQuery = query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true) ||
                    (task.relatedCaseNumber?.contains(query, ignoreCase = true) == true)
            val matchesStatus = status == "الكل" || task.status == status
            val matchesPriority = priority == "الكل" || task.priority == priority
            matchesQuery && matchesStatus && matchesPriority
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Video Ideas Stream
    val filteredVideoIdeas: StateFlow<List<VideoIdeaEntity>> = combine(
        rawVideoIdeas,
        videoIdeaSearchQuery,
        videoIdeaStatusFilter,
        videoIdeaPlatformFilter,
        videoIdeaTypeFilter
    ) { ideas, query, status, platform, type ->
        val q = query.trim()
        ideas.filter { idea ->
            val matchesQ = q.isBlank() ||
                    idea.title.contains(q, ignoreCase = true) ||
                    idea.concept.contains(q, ignoreCase = true) ||
                    idea.hook.contains(q, ignoreCase = true) ||
                    idea.keyPoints.contains(q, ignoreCase = true)
            val matchesStatus = status == "الكل" || idea.status == status
            val matchesPlatform = platform == "الكل" || idea.platform == platform
            val matchesType = type == "الكل" || idea.contentType == type
            matchesQ && matchesStatus && matchesPlatform && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Video Scripts Stream
    val filteredVideoScripts: StateFlow<List<VideoScriptEntity>> = combine(
        rawVideoScripts,
        videoScriptSearchQuery,
        videoScriptStatusFilter
    ) { scripts, query, status ->
        val q = query.trim()
        scripts.filter { script ->
            val matchesQ = q.isBlank() ||
                    script.title.contains(q, ignoreCase = true) ||
                    script.hook.contains(q, ignoreCase = true) ||
                    script.mainContent.contains(q, ignoreCase = true) ||
                    script.callToAction.contains(q, ignoreCase = true)
            val matchesStatus = status == "الكل" || script.status == status
            matchesQ && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Payments Stream
    val filteredPayments: StateFlow<List<CasePaymentEntity>> = combine(
        rawPayments,
        paymentSearchQuery,
        paymentMethodFilter
    ) { payments, query, method ->
        val q = query.trim()
        payments.filter { payment ->
            val matchesQ = q.isBlank() ||
                    payment.caseNumber.contains(q, ignoreCase = true) ||
                    payment.notes.contains(q, ignoreCase = true) ||
                    payment.receiptNumber.contains(q, ignoreCase = true)
            val matchesMethod = method == "الكل" || payment.paymentMethod == method
            matchesQ && matchesMethod
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Global Search Engine Stream
    val searchResults: StateFlow<GlobalSearchResults> = combine(
        globalSearchQuery,
        rawCases,
        rawClients,
        rawEvidence,
        rawContent,
        rawKnowledge,
        rawTasks,
        rawSupportForms,
        rawInvestigationTools
    ) { args: Array<Any> ->
        val q = args[0] as String
        @Suppress("UNCHECKED_CAST")
        val cases = args[1] as List<CaseEntity>
        @Suppress("UNCHECKED_CAST")
        val clients = args[2] as List<ClientEntity>
        @Suppress("UNCHECKED_CAST")
        val evidence = args[3] as List<EvidenceEntity>
        @Suppress("UNCHECKED_CAST")
        val content = args[4] as List<ContentEntity>
        @Suppress("UNCHECKED_CAST")
        val knowledge = args[5] as List<KnowledgeEntity>
        @Suppress("UNCHECKED_CAST")
        val tasks = args[6] as List<TaskEntity>
        @Suppress("UNCHECKED_CAST")
        val supportForms = args[7] as List<SupportFormEntity>
        @Suppress("UNCHECKED_CAST")
        val investigationTools = args[8] as List<InvestigationToolEntity>
        val ideas = rawVideoIdeas.value
        val scripts = rawVideoScripts.value

        val query = q.trim()
        if (query.isBlank()) {
            GlobalSearchResults()
        } else {
            GlobalSearchResults(
                cases = cases.filter { it.title.contains(query, ignoreCase = true) || it.caseNumber.contains(query, ignoreCase = true) || it.clientName.contains(query, ignoreCase = true) },
                clients = clients.filter { it.fullName.contains(query, ignoreCase = true) || it.phoneNumber.contains(query, ignoreCase = true) },
                evidence = evidence.filter { it.evidenceName.contains(query, ignoreCase = true) || it.originalFilename.contains(query, ignoreCase = true) || it.sha256Hash.contains(query, ignoreCase = true) },
                content = content.filter { it.title.contains(query, ignoreCase = true) || it.body.contains(query, ignoreCase = true) },
                knowledge = knowledge.filter { it.title.contains(query, ignoreCase = true) || it.summary.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true) },
                tasks = tasks.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) },
                supportForms = supportForms.filter { it.formName.contains(query, ignoreCase = true) || it.company.contains(query, ignoreCase = true) || it.problemType.contains(query, ignoreCase = true) },
                investigationTools = investigationTools.filter { it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) },
                videoIdeas = ideas.filter { it.title.contains(query, ignoreCase = true) || it.concept.contains(query, ignoreCase = true) },
                videoScripts = scripts.filter { it.title.contains(query, ignoreCase = true) || it.mainContent.contains(query, ignoreCase = true) }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GlobalSearchResults())


    // Filtered Cases Stream
    val filteredCases: StateFlow<List<CaseEntity>> = combine(
        rawCases,
        caseSearchQuery,
        caseStatusFilter
    ) { cases, query, status ->
        cases.filter { caseItem ->
            val matchesQuery = query.isBlank() ||
                    caseItem.title.contains(query, ignoreCase = true) ||
                    caseItem.caseNumber.contains(query, ignoreCase = true) ||
                    caseItem.clientName.contains(query, ignoreCase = true) ||
                    caseItem.threatType.contains(query, ignoreCase = true)

            val matchesStatus = status == "الكل" || caseItem.status == status
            matchesQuery && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Knowledge Stream
    val filteredKnowledge: StateFlow<List<KnowledgeEntity>> = combine(
        rawKnowledge,
        knowledgeSearchQuery,
        knowledgeCategoryFilter
    ) { guides, query, cat ->
        guides.filter { guide ->
            val matchesQuery = query.isBlank() ||
                    guide.title.contains(query, ignoreCase = true) ||
                    guide.summary.contains(query, ignoreCase = true) ||
                    guide.tags.contains(query, ignoreCase = true) ||
                    guide.content.contains(query, ignoreCase = true)

            val matchesCat = cat == "الكل" || guide.category == cat
            matchesQuery && matchesCat
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Content Studio Stream
    val filteredContent: StateFlow<List<ContentEntity>> = combine(
        rawContent,
        contentPlatformFilter,
        contentStatusFilter
    ) { items, platform, status ->
        items.filter { item ->
            val matchesPlatform = platform == "الكل" || item.platform == platform
            val matchesStatus = status == "الكل" || item.status == status
            matchesPlatform && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Clients Stream
    val filteredClients: StateFlow<List<ClientEntity>> = combine(
        rawClients,
        clientSearchQuery
    ) { clients, query ->
        clients.filter { client ->
            query.isBlank() ||
                    client.fullName.contains(query, ignoreCase = true) ||
                    client.phoneNumber.contains(query, ignoreCase = true) ||
                    client.riskLevel.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered External Requests Stream
    val filteredExternalRequests: StateFlow<List<ExternalRequestEntity>> = combine(
        rawExternalRequests,
        externalRequestSearchQuery,
        externalRequestStatusFilter,
        externalRequestSourceFilter,
        externalRequestSheetFilter,
        externalRequestUrgencyFilter,
        externalRequestActiveTab
    ) { args: Array<Any> ->
        @Suppress("UNCHECKED_CAST")
        val requests = args[0] as List<ExternalRequestEntity>
        val query = args[1] as String
        val statusFilter = args[2] as String
        val sourceFilter = args[3] as String
        val sheetFilter = args[4] as String
        val urgencyFilter = args[5] as String
        val activeTab = args[6] as String

        requests.filter { req ->
            val matchesQuery = query.isBlank() ||
                    req.clientName.contains(query, ignoreCase = true) ||
                    req.clientPhone.contains(query, ignoreCase = true) ||
                    req.clientEmail.contains(query, ignoreCase = true) ||
                    req.description.contains(query, ignoreCase = true) ||
                    req.internalNotes.contains(query, ignoreCase = true) ||
                    req.requestNumber.contains(query, ignoreCase = true) ||
                    req.sheetName.contains(query, ignoreCase = true) ||
                    req.additionalFields.contains(query, ignoreCase = true)

            val matchesTab = when (activeTab) {
                "الكل" -> true
                "جديدة" -> req.status == "جديد"
                "قيد المراجعة" -> req.status == "قيد المراجعة"
                "قيد المعالجة" -> req.status == "قيد المعالجة"
                "محولة" -> req.status == "تم تحويله إلى قضية"
                "مكتملة" -> req.status == "مكتمل"
                "مرفوضة" -> req.status == "مرفوض"
                "مصادر البيانات" -> true
                else -> true
            }

            val matchesStatus = statusFilter == "الكل" || req.status == statusFilter
            val matchesSource = sourceFilter == "الكل" || req.sourceId == sourceFilter
            val matchesSheet = sheetFilter == "الكل" || req.sheetId == sheetFilter || "${req.sourceId}_${req.sheetId}" == sheetFilter || req.sheetName == sheetFilter
            val matchesUrgency = urgencyFilter == "الكل" || req.urgency == urgencyFilter

            matchesQuery && matchesTab && matchesStatus && matchesSource && matchesSheet && matchesUrgency
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // HUD Helper
    fun showHud(
        text: String,
        type: HudType = HudType.INFO,
        undoAction: (() -> Unit)? = null,
        undoLabel: String = "تراجع"
    ) {
        _hudMessage.value = HudMessage(
            text = text,
            type = type,
            undoAction = undoAction,
            undoLabel = undoLabel
        )
    }

    fun dismissHud() {
        _hudMessage.value = null
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun setRole(role: String) {
        _currentRole.value = role
        viewModelScope.launch {
            repository.logAudit(
                actionType = "ROLE_CHANGE",
                module = "AUTH",
                entityId = "current_session",
                performedBy = role,
                details = "تغيير الدور النشط إلى: $role"
            )
        }
        showHud("تم التبديل إلى دور: $role", HudType.INFO)
    }

    fun toggleBiometricLock() {
        if (!_isLockEnabled.value) {
            showHud("نظام قفل التطبيق معطل في الإعدادات", HudType.INFO)
            return
        }
        val newState = !_isBiometricUnlocked.value
        _isBiometricUnlocked.value = newState
        showHud(
            if (newState) "تم إلغاء القفل بنجاح" else "تم تأمين المنصة بالقفل",
            HudType.SUCCESS
        )
    }

    fun lockBiometrics() {
        if (_isLockEnabled.value) {
            _isBiometricUnlocked.value = false
            showHud("تم تأمين المنصة بالقفل", HudType.INFO)
        }
    }

    fun unlockBiometrics() {
        _isBiometricUnlocked.value = true
        showHud("تم إلغاء القفل عبر المصادقة الحيوية", HudType.SUCCESS)
    }

    fun verifyPin(inputPin: String): Boolean {
        return if (inputPin == _appPin.value) {
            _isBiometricUnlocked.value = true
            showHud("تم فتح المنصة بنجاح بواسطة رمز PIN", HudType.SUCCESS)
            true
        } else {
            showHud("رمز PIN غير صحيح. يرجى المحاولة ثانية.", HudType.ERROR)
            false
        }
    }

    fun changePin(oldPin: String, newPin: String): Pair<Boolean, String> {
        if (oldPin != _appPin.value) {
            return Pair(false, "رمز PIN الحالي غير صحيح")
        }
        if (newPin.length !in 4..6 || !newPin.all { it.isDigit() }) {
            return Pair(false, "يجب أن يتكون رمز PIN الجديد من 4 إلى 6 أرقام")
        }
        _appPin.value = newPin
        viewModelScope.launch {
            repository.saveSetting("security_app_pin", newPin)
            repository.logAudit(
                actionType = "SECURITY",
                module = "SETTINGS",
                entityId = "app_pin",
                performedBy = _currentRole.value,
                details = "تم تغيير رمز PIN السري للمنصة"
            )
        }
        showHud("تم تحديث رمز PIN الجديد بنجاح", HudType.SUCCESS)
        return Pair(true, "تم تحديث الرمز بنجاح")
    }

    fun setLockEnabled(enabled: Boolean) {
        _isLockEnabled.value = enabled
        if (!enabled) {
            _isBiometricUnlocked.value = true
        }
        viewModelScope.launch {
            repository.saveSetting("security_lock_enabled", enabled.toString())
        }
        showHud(if (enabled) "تم تفعيل نظام قفل التطبيق" else "تم تعطيل نظام قفل التطبيق", HudType.INFO)
    }

    fun setAutoLockInterval(interval: String) {
        _autoLockInterval.value = interval
        viewModelScope.launch {
            repository.saveSetting("security_auto_lock", interval)
        }
        val label = when (interval) {
            "IMMEDIATE" -> "فوري عند مغادرة التطبيق"
            "1_MIN" -> "بعد دقيقة واحدة"
            "5_MIN" -> "بعد 5 دقائق"
            "15_MIN" -> "بعد 15 دقيقة"
            else -> "إيقاف القفل التلقائي"
        }
        showHud("تم تعيين القفل التلقائي: $label", HudType.INFO)
    }

    fun setBiometricHardwareEnabled(enabled: Boolean) {
        _isBiometricHardwareEnabled.value = enabled
        viewModelScope.launch {
            repository.saveSetting("security_biometric_enabled", enabled.toString())
        }
        showHud(if (enabled) "تم تفعيل المستشعر الحيوي" else "تم تعطيل المستشعر الحيوي", HudType.INFO)
    }

    fun checkAutoLockOnResume(elapsedMillis: Long) {
        if (!_isLockEnabled.value) return
        val thresholdMillis = when (_autoLockInterval.value) {
            "IMMEDIATE" -> 1500L // 1.5s tolerance
            "1_MIN" -> 60_000L
            "5_MIN" -> 300_000L
            "15_MIN" -> 900_000L
            else -> Long.MAX_VALUE
        }
        if (elapsedMillis >= thresholdMillis) {
            _isBiometricUnlocked.value = false
        }
    }

    // ==========================================
    // CASES ACTIONS
    // ==========================================
    fun saveCase(caseEntity: CaseEntity, isNew: Boolean) {
        viewModelScope.launch {
            repository.insertOrUpdateCase(caseEntity, isNew)
            showHud(
                if (isNew) "تم تسجيل القضية بنجاح (${caseEntity.caseNumber})" else "تم تحديث بيانات القضية (${caseEntity.caseNumber})",
                HudType.SUCCESS
            )
        }
    }

    fun deleteCase(caseEntity: CaseEntity) {
        viewModelScope.launch {
            repository.softDeleteCase(caseEntity.id, caseEntity.caseNumber)
            showHud(
                text = "تم نقل القضية ${caseEntity.caseNumber} لسلة المحذوفات",
                type = HudType.WARNING,
                undoAction = {
                    viewModelScope.launch {
                        repository.restoreCase(caseEntity.id)
                        showHud("تم استعادة القضية ${caseEntity.caseNumber}", HudType.SUCCESS)
                    }
                }
            )
        }
    }

    fun addCasePayment(
        caseId: String,
        amount: Double,
        paymentMethod: String,
        paymentDate: String,
        notes: String,
        receiptNumber: String = ""
    ) {
        viewModelScope.launch {
            val res = repository.addCasePayment(
                caseId = caseId,
                amount = amount,
                paymentMethod = paymentMethod,
                paymentDate = paymentDate,
                notes = notes,
                receiptNumber = receiptNumber,
                performedBy = _currentRole.value
            )
            if (res.isSuccess) {
                showHud("تم تسجيل الدفعة المالية بقيمة $amount بنجاح", HudType.SUCCESS)
            } else {
                showHud("فشل تسجيل الدفعة: ${res.exceptionOrNull()?.message}", HudType.ERROR)
            }
        }
    }

    fun updateCasePrice(caseId: String, newPrice: Double, notes: String) {
        viewModelScope.launch {
            val res = repository.updateCasePrice(
                caseId = caseId,
                newPrice = newPrice,
                notes = notes,
                performedBy = _currentRole.value
            )
            if (res.isSuccess) {
                showHud("تم تعديل السعر بنجاح إلى $newPrice", HudType.SUCCESS)
            } else {
                showHud("فشل تعديل السعر: ${res.exceptionOrNull()?.message}", HudType.ERROR)
            }
        }
    }

    fun changeCaseStatus(caseId: String, newStatus: String) {
        viewModelScope.launch {
            repository.changeCaseStatus(caseId, newStatus, performedBy = _currentRole.value)
            showHud("تم تغيير حالة القضية إلى: $newStatus", HudType.INFO)
        }
    }

    fun changeCasePriority(caseId: String, newPriority: String) {
        viewModelScope.launch {
            repository.changeCasePriority(caseId, newPriority, performedBy = _currentRole.value)
            showHud("تم تعديل أولوية القضية إلى: $newPriority", HudType.INFO)
        }
    }

    fun closeCase(caseId: String, reason: String = "") {
        viewModelScope.launch {
            repository.closeCase(caseId, reason, performedBy = _currentRole.value)
            showHud("تم إغلاق القضية بنجاح", HudType.SUCCESS)
        }
    }

    fun reopenCase(caseId: String) {
        viewModelScope.launch {
            repository.reopenCase(caseId, performedBy = _currentRole.value)
            showHud("تمت إعادة فتح القضية للمتابعة", HudType.INFO)
        }
    }

    fun archiveCase(caseId: String) {
        viewModelScope.launch {
            repository.archiveCase(caseId, performedBy = _currentRole.value)
            showHud("تمت أرشفة القضية بنجاح", HudType.INFO)
        }
    }

    fun duplicateCase(sourceCaseId: String) {
        viewModelScope.launch {
            val res = repository.duplicateCase(sourceCaseId, performedBy = _currentRole.value)
            if (res.isSuccess) {
                showHud("تم استنساخ القضية بنجاح (${res.getOrNull()?.caseNumber})", HudType.SUCCESS)
            } else {
                showHud("فشل استنساخ القضية: ${res.exceptionOrNull()?.message}", HudType.ERROR)
            }
        }
    }

    fun getPaymentsForCase(caseId: String) = repository.getPaymentsForCase(caseId)
    fun getCaseAuditLogs(caseId: String) = repository.getCaseAuditLogs(caseId)
    fun getCaseFinancialLogs(caseId: String) = repository.getCaseFinancialLogs(caseId)

    // Video Ideas & Scripts Actions
    fun saveVideoIdea(idea: VideoIdeaEntity, isNew: Boolean) {
        viewModelScope.launch {
            repository.insertOrUpdateVideoIdea(idea, isNew)
            showHud(if (isNew) "تم حفظ فكرة الفيديو الجديدة" else "تم تحديث فكرة الفيديو", HudType.SUCCESS)
        }
    }

    fun deleteVideoIdea(idea: VideoIdeaEntity) {
        viewModelScope.launch {
            repository.softDeleteVideoIdea(idea.id, idea.title)
            showHud("تم حذف فكرة الفيديو بنجاح", HudType.WARNING)
        }
    }

    fun saveVideoScript(script: VideoScriptEntity, isNew: Boolean) {
        viewModelScope.launch {
            repository.insertOrUpdateVideoScript(script, isNew)
            showHud(if (isNew) "تم حفظ سكربت الفيديو بنجاح" else "تم تحديث سكربت الفيديو", HudType.SUCCESS)
        }
    }

    fun deleteVideoScript(script: VideoScriptEntity) {
        viewModelScope.launch {
            repository.softDeleteVideoScript(script.id, script.title)
            showHud("تم حذف سكربت الفيديو بنجاح", HudType.WARNING)
        }
    }

    fun getScriptsForIdea(ideaId: String) = repository.getScriptsForIdea(ideaId)

    // ==========================================
    // CLIENTS ACTIONS
    // ==========================================
    fun saveClient(client: ClientEntity, isNew: Boolean) {
        viewModelScope.launch {
            repository.insertOrUpdateClient(client, isNew)
            showHud(
                if (isNew) "تم تسجيل العميل بنجاح" else "تم تحديث بيانات العميل",
                HudType.SUCCESS
            )
        }
    }

    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.softDeleteClient(client.id, client.fullName)
            showHud("تم حذف سجل العميل ${client.fullName}", HudType.WARNING)
        }
    }

    // ==========================================
    // EVIDENCE LAB ACTIONS
    // ==========================================
    fun addEvidence(
        caseId: String,
        caseNumber: String,
        evidenceName: String,
        fileType: String,
        originalFilename: String,
        deviceModel: String,
        software: String,
        gpsCoords: String,
        notes: String,
        contentSampleForHashing: String
    ) {
        viewModelScope.launch {
            val md5 = ForensicCrypto.calculateMd5(contentSampleForHashing.ifEmpty { originalFilename + System.currentTimeMillis() })
            val sha256 = ForensicCrypto.calculateSha256(contentSampleForHashing.ifEmpty { originalFilename + System.currentTimeMillis() })
            val evidence = EvidenceEntity(
                id = "evi_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
                caseId = caseId,
                caseNumber = caseNumber,
                evidenceName = evidenceName,
                fileType = fileType,
                originalFilename = originalFilename,
                md5Hash = md5,
                sha256Hash = sha256,
                exifDeviceModel = deviceModel.ifEmpty { "غير محدد / مستخرج من الرسائل" },
                exifSoftware = software.ifEmpty { "Native / Unmodified" },
                exifGpsCoords = gpsCoords.ifEmpty { "لا توجد إحداثيات (تم تجريدها أثناء الإرسال)" },
                exifTimestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date()),
                chainOfCustodyLog = "تم التوثيق والتشفير بواسطة ${_currentRole.value}",
                notes = notes
            )
            repository.insertOrUpdateEvidence(evidence, isNew = true)
            showHud("تم حفظ المرفق واستخراج بصمة SHA-256 بنجاح", HudType.SUCCESS)
        }
    }

    fun deleteEvidence(evidence: EvidenceEntity) {
        viewModelScope.launch {
            repository.softDeleteEvidence(evidence.id, evidence.evidenceName)
            showHud("تم حذف المرفق", HudType.WARNING)
        }
    }

    // ==========================================
    // CONTENT STUDIO ACTIONS (FIXED DELETION & CRUD)
    // ==========================================
    fun saveContent(
        id: String?,
        title: String,
        body: String,
        platform: String,
        status: String,
        tags: String,
        scheduledTimestamp: Long?
    ) {
        viewModelScope.launch {
            val isNew = id.isNullOrBlank()
            val deterministicId = if (isNew) "cnt_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}" else id!!
            val entity = ContentEntity(
                id = deterministicId,
                title = title,
                body = body,
                platform = platform,
                status = status,
                tagsJson = tags,
                scheduledTimestamp = scheduledTimestamp,
                updatedDate = System.currentTimeMillis()
            )
            repository.insertOrUpdateContent(entity, isNew = isNew)
            showHud(
                if (isNew) "تم حفظ المنشور الجديد في الاستوديو" else "تم تحديث المنشور بنجاح",
                HudType.SUCCESS
            )
        }
    }

    fun deleteContent(item: ContentEntity) {
        viewModelScope.launch {
            repository.softDeleteContent(item.id, item.title)
            showHud(
                text = "تم حذف المنشور '${item.title}' من الاستوديو",
                type = HudType.WARNING,
                undoAction = {
                    viewModelScope.launch {
                        repository.restoreContent(item.id)
                        showHud("تمت استعادة المنشور بنجاح", HudType.SUCCESS)
                    }
                },
                undoLabel = "تراجع"
            )
        }
    }

    // ==========================================
    // KNOWLEDGE BASE ACTIONS
    // ==========================================
    fun saveKnowledge(guide: KnowledgeEntity, isNew: Boolean) {
        viewModelScope.launch {
            repository.insertOrUpdateKnowledge(guide, isNew)
            showHud(
                if (isNew) "تمت إضافة الإجراء الجديد إلى الموسوعة" else "تم تحديث الإجراء بنجاح",
                HudType.SUCCESS
            )
        }
    }

    fun deleteKnowledge(guide: KnowledgeEntity) {
        viewModelScope.launch {
            repository.softDeleteKnowledge(guide.id, guide.title)
            showHud("تم حذف المقال التوجيهي من الموسوعة", HudType.WARNING)
        }
    }

    fun reloadOfficialLibrary() {
        viewModelScope.launch {
            val count = repository.reloadOfficialLibrary()
            showHud("تمت إعادة تحميل المكتبة المعرفية الرسمية بنجاح ($count مرجعاً معتمداً)", HudType.SUCCESS)
        }
    }

    // ==========================================
    // INVESTIGATION REPORT GENERATION
    // ==========================================
    fun generateInvestigationReport(
        caseItem: CaseEntity,
        evidenceList: List<EvidenceEntity>,
        linkedItems: List<com.example.data.local.entities.CaseLinkedItemEntity> = emptyList()
    ): String {
        val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date(caseItem.createdDate))
        val linkedSection = if (linkedItems.isEmpty()) {
            ""
        } else {
            "\n[4] إجراءات نماذج الدعم وأدوات الفحص المرتبطة بالقضية:\n" +
            linkedItems.joinToString("\n---\n") { item ->
                val typeLabel = if (item.itemType == "SUPPORT_FORM") "نموذج دعم مباشر" else "أداة فحص وتحقق رقمي"
                "• النوع: $typeLabel (${item.itemPlatformOrCategory})\n" +
                "  الاسم: ${item.itemTitle}\n" +
                "  الرابط المعتمد: ${item.itemUrl}\n" +
                "  ملاحظات المسؤول: ${item.notes.ifBlank { "تم استخدام الإجراء بنجاح دون ملاحظات إضافية" }}"
            } + "\n"
        }
        val sectionNumber = if (linkedItems.isEmpty()) "[4]" else "[5]"
        return """
======================================================
     تقرير متابعة القضية والفحص الفني المعتمد
     JAFFAR BADRAN CASE MANAGEMENT & TECHNICAL CONSULTING
======================================================
رقم القضية: ${caseItem.caseNumber}
التاريخ: $dateStr
العميل: ${caseItem.clientName}
نوع التهديد: ${caseItem.threatType}
مستوى الأولوية: ${caseItem.priority}
حالة القضية: ${caseItem.status}
المسؤول عن المتابعة: ${caseItem.assignedInvestigator}

[1] ملخص القضية وتفاصيل الطلب:
${caseItem.title}
ملاحظات المتابعة: ${caseItem.notes}

[2] التسلسل الزمني للأحداث (Timeline):
${caseItem.timelineEventsJson}

[3] المرفقات والملفات الموثقة وسجل التعديلات:
${
    if (evidenceList.isEmpty()) "لا توجد ملفات أو مرفقات مسجلة حالياً."
    else evidenceList.joinToString("\n---\n") { evi ->
        "• اسم الملف: ${evi.evidenceName} (${evi.fileType})\n" +
        "  الملف الأصلي: ${evi.originalFilename}\n" +
        "  بصمة MD5: ${evi.md5Hash}\n" +
        "  بصمة SHA-256: ${evi.sha256Hash}\n" +
        "  الجهاز والبرنامج: ${evi.exifDeviceModel} | ${evi.exifSoftware}\n" +
        "  سجل التوثيق: ${evi.chainOfCustodyLog}"
    }
}
$linkedSection
$sectionNumber التوصية الفنية والإجرائية:
حفظ هذا التقرير المهني كملف مرجعي رسمي معتمد للقضية ولمتابعة الإجراءات اللاحقة.
======================================================
        التوقيع والاعتماد: جعفر بدران
======================================================
        """.trimIndent()
    }

    // ==========================================
    // TASKS ACTIONS
    // ==========================================
    fun saveTask(task: TaskEntity, isNew: Boolean) {
        viewModelScope.launch {
            repository.insertOrUpdateTask(task, isNew)
            showHud(
                if (isNew) "تمت إضافة المهمة بنجاح" else "تم تحديث بيانات المهمة",
                HudType.SUCCESS
            )
        }
    }

    fun toggleTaskStatus(task: TaskEntity) {
        viewModelScope.launch {
            val newStatus = if (task.status == "مكتملة") "قيد التنفيذ" else "مكتملة"
            val completedAt = if (newStatus == "مكتملة") System.currentTimeMillis() else null
            repository.updateTaskStatus(task.id, newStatus, completedAt)
            showHud(
                if (newStatus == "مكتملة") "تم إنجاز المهمة: ${task.title}" else "تمت إعادة فتح المهمة",
                HudType.INFO
            )
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.softDeleteTask(task.id, task.title)
            showHud(
                text = "تم نقل المهمة '${task.title}' إلى سلة المحذوفات",
                type = HudType.WARNING,
                undoAction = {
                    viewModelScope.launch {
                        repository.restoreTask(task.id)
                        showHud("تمت استعادة المهمة بنجاح", HudType.SUCCESS)
                    }
                },
                undoLabel = "تراجع"
            )
        }
    }

    // ==========================================
    // TRASH ACTIONS (RESTORE & PERMANENT DELETE)
    // ==========================================
    fun restoreItem(module: String, id: String) {
        viewModelScope.launch {
            when (module) {
                "CASES" -> repository.restoreCase(id)
                "CLIENTS" -> repository.restoreClient(id)
                "EVIDENCE" -> repository.restoreEvidence(id)
                "STUDIO" -> repository.restoreContent(id)
                "KNOWLEDGE" -> repository.restoreKnowledge(id)
                "TASKS" -> repository.restoreTask(id)
            }
            showHud("تمت استعادة العنصر بنجاح إلى جدول العمل", HudType.SUCCESS)
        }
    }

    fun permanentDeleteItem(module: String, id: String) {
        viewModelScope.launch {
            repository.permanentDelete(module, id)
            showHud("تم الحذف النهائي للعنصر نهائياً وتطهير مساحته", HudType.WARNING)
        }
    }

    // ==========================================
    // SETTINGS, THEME & GOOGLE SHEETS SYNC
    // ==========================================
    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
        viewModelScope.launch {
            repository.saveSetting("app_theme", if (_isDarkTheme.value) "DARK" else "LIGHT")
        }
        showHud(
            if (_isDarkTheme.value) "تم تفعيل المظهر الليلي (Cyber Dark)" else "تم تفعيل المظهر النهاري (Clean Light)",
            HudType.INFO
        )
    }

    fun toggleScreenshotProtection() {
        _isScreenshotProtection.value = !_isScreenshotProtection.value
        viewModelScope.launch {
            repository.saveSetting("screenshot_protection", _isScreenshotProtection.value.toString())
        }
        showHud(
            if (_isScreenshotProtection.value) "تم تفعيل حماية لقطات الشاشة (Anti-Screenshot)" else "تم تعطيل حماية لقطات الشاشة",
            HudType.INFO
        )
    }

    fun saveGoogleSheetsConfig(newSheetId: String, newUrl: String, newToken: String, autoSync: Boolean) {
        sheetId.value = newSheetId
        webAppUrl.value = newUrl
        apiToken.value = newToken
        autoSyncEnabled.value = autoSync

        viewModelScope.launch {
            repository.saveSetting("sheet_id", newSheetId)
            repository.saveSetting("web_app_url", newUrl)
            repository.saveSetting("api_token", newToken)
            repository.saveSetting("auto_sync_enabled", autoSync.toString())
            showHud("تم حفظ إعدادات الاتصال بجدول Google Sheets بنجاح", HudType.SUCCESS)
        }
    }

    fun syncNowWithGoogleSheets() {
        viewModelScope.launch {
            showHud("بدء المزامنة الفورية مع Google Sheets...", HudType.INFO)
            val result = repository.triggerGoogleSheetsSync(
                sheetId = sheetId.value,
                webAppUrl = webAppUrl.value,
                token = apiToken.value
            )
            if (result.first) {
                showHud(result.second, HudType.SUCCESS)
            } else {
                showHud(result.second, HudType.WARNING)
            }
        }
    }

    // ==========================================
    // SUPPORT FORMS & INVESTIGATION TOOLS ACTIONS
    // ==========================================
    fun toggleToolFavorite(toolId: String, currentFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleToolFavorite(toolId, !currentFavorite)
            showHud(if (!currentFavorite) "تمت إضافة الأداة إلى المفضلة" else "تمت إزالة الأداة من المفضلة", HudType.INFO)
        }
    }

    fun recordToolUsage(toolId: String) {
        viewModelScope.launch {
            repository.recordToolUsage(toolId)
        }
    }

    fun getLinkedItemsForCase(caseId: String): Flow<List<CaseLinkedItemEntity>> {
        return repository.getLinkedItemsForCase(caseId)
    }

    fun linkItemToCase(
        caseId: String,
        itemType: String,
        itemId: String,
        itemTitle: String,
        itemUrl: String,
        itemPlatformOrCategory: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.linkItemToCase(
                caseId = caseId,
                itemType = itemType,
                itemId = itemId,
                itemTitle = itemTitle,
                itemUrl = itemUrl,
                itemPlatformOrCategory = itemPlatformOrCategory,
                notes = notes
            )
            showHud("تم ربط «$itemTitle» بالقضية بنجاح", HudType.SUCCESS)
        }
    }

    fun unlinkItemFromCase(linkId: String, caseId: String, itemTitle: String) {
        viewModelScope.launch {
            repository.unlinkItemFromCase(linkId, caseId, itemTitle)
            showHud("تم إلغاء ربط «$itemTitle» من القضية", HudType.INFO)
        }
    }

    fun openUrl(context: Context, url: String, toolId: String? = null) {
        if (toolId != null) {
            recordToolUsage(toolId)
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            showHud("تعذر فتح الرابط: ${e.localizedMessage}", HudType.ERROR)
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String = "الرابط") {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            showHud("تم نسخ $label إلى الحافظة بنجاح", HudType.SUCCESS)
        } catch (e: Exception) {
            showHud("تعذر النسخ إلى الحافظة", HudType.WARNING)
        }
    }

    fun shareUrl(context: Context, url: String, title: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "$title\n$url")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(intent, "مشاركة الرابط").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        } catch (e: Exception) {
            showHud("تعذر فتح قائمة المشاركة", HudType.WARNING)
        }
    }

    // ==========================================
    // EXTERNAL REQUESTS ACTIONS (طلبات العملاء الخارجية)
    // ==========================================

    fun testExternalSourceConnection(url: String, onResult: (Boolean, String, String) -> Unit) {
        viewModelScope.launch {
            val result = externalRequestsRepo.testConnection(url)
            val docTitle = externalRequestsRepo.extractDocumentTitle(url)
            if (result.isSuccess) {
                onResult(true, result.getOrNull() ?: "تم الاتصال بالملف العام بنجاح.", docTitle)
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "فشل الاتصال بالملف."
                onResult(false, errorMsg, "")
            }
        }
    }

    fun addExternalSource(name: String, url: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = externalRequestsRepo.addSource(name, url)
            if (result.isSuccess) {
                val src = result.getOrThrow()
                repository.logAudit(
                    actionType = "CREATE",
                    module = "EXTERNAL_REQUESTS",
                    entityId = src.id,
                    performedBy = _currentRole.value,
                    details = "إضافة مصدر خارجي جديد: ${src.name} وتحديث الأوراق"
                )
                showHud("تمت إضافة مصدر الطلبات بنجاح وجلب البيانات", HudType.SUCCESS)
                onComplete(true, "تمت الإضافة بنجاح")
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "فشل إضافة المصدر"
                showHud(err, HudType.ERROR)
                onComplete(false, err)
            }
        }
    }

    fun updateExternalSource(source: ExternalRequestSourceEntity) {
        viewModelScope.launch {
            externalRequestsRepo.updateSource(source)
            showHud("تم تحديث بيانات المصدر «${source.name}»", HudType.SUCCESS)
        }
    }

    fun deleteExternalSource(sourceId: String, sourceName: String) {
        viewModelScope.launch {
            externalRequestsRepo.deleteSource(sourceId)
            repository.logAudit(
                actionType = "DELETE",
                module = "EXTERNAL_REQUESTS",
                entityId = sourceId,
                performedBy = _currentRole.value,
                details = "إزالة المصدر الخارجي «$sourceName» من التطبيق محلياً (دون حذف Google Sheet الأصلي)"
            )
            showHud("تم حذف المصدر من التطبيق محلياً. لم يتم تعديل Google Sheet الأصلي.", HudType.INFO)
        }
    }

    fun toggleExternalSource(sourceId: String, enabled: Boolean) {
        viewModelScope.launch {
            externalRequestsRepo.toggleSourceEnabled(sourceId, enabled)
            showHud(if (enabled) "تم تفعيل المصدر" else "تم تعطيل المصدر مؤقتاً", HudType.INFO)
        }
    }

    fun refreshSheetsForSource(sourceId: String) {
        viewModelScope.launch {
            showHud("جاري فحص وتحديث أوراق العمل من Google Sheets...", HudType.INFO)
            val result = externalRequestsRepo.refreshSheetsForSource(sourceId)
            if (result.isSuccess) {
                val sheets = result.getOrThrow()
                showHud("تم تحديث الأوراق بنجاح (${sheets.size} ورقة)", HudType.SUCCESS)
            } else {
                showHud("تعذر تحديث الأوراق: ${result.exceptionOrNull()?.localizedMessage}", HudType.ERROR)
            }
        }
    }

    fun toggleSheetEnabled(sheetId: String, enabled: Boolean) {
        viewModelScope.launch {
            externalRequestsRepo.setSheetEnabled(sheetId, enabled)
            showHud(if (enabled) "تم تفعيل الورقة للاستيراد" else "تم إيقاف استيراد هذه الورقة", HudType.INFO)
        }
    }

    fun toggleSheetIgnored(sheetId: String, ignored: Boolean) {
        viewModelScope.launch {
            externalRequestsRepo.setSheetIgnored(sheetId, ignored)
            showHud(if (ignored) "تم تجاهل الورقة واستثناؤها من المزامنة" else "تم إلغاء تجاهل الورقة", HudType.INFO)
        }
    }

    fun syncSingleSheet(sourceId: String, sheetEntityId: String, sheetName: String) {
        viewModelScope.launch {
            showHud("جاري مزامنة الورقة «$sheetName»...", HudType.INFO)
            val result = externalRequestsRepo.syncSingleSheet(sourceId, sheetEntityId)
            if (result.isSuccess) {
                val count = result.getOrThrow()
                showHud("اكتملت مزامنة الورقة «$sheetName» بنجاح ($count طلب)", HudType.SUCCESS)
            } else {
                showHud("فشلت مزامنة الورقة: ${result.exceptionOrNull()?.localizedMessage}", HudType.ERROR)
            }
        }
    }

    fun deleteExternalSheet(sheetId: String, sheetName: String) {
        viewModelScope.launch {
            externalRequestsRepo.deleteSheetFromApp(sheetId)
            showHud("تم حذف الورقة «$sheetName» من التطبيق محلياً دون المساس بـ Google Sheet", HudType.INFO)
        }
    }

    fun updateSheetDisplayName(sheetId: String, displayName: String?) {
        viewModelScope.launch {
            externalRequestsRepo.updateCustomDisplayName(sheetId, displayName)
            showHud("تم تحديث اسم عرض الورقة", HudType.SUCCESS)
        }
    }

    fun updateSheetColumnMapping(sheetId: String, mapping: Map<String, String>) {
        viewModelScope.launch {
            val json = org.json.JSONObject(mapping as Map<*, *>).toString()
            externalRequestsRepo.updateColumnMapping(sheetId, json)
            showHud("تم حفظ تعيين الأعمدة للورقة بنجاح", HudType.SUCCESS)
        }
    }

    fun addSheetManually(sourceId: String, sheetName: String, sheetGid: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val result = externalRequestsRepo.addSheetManually(sourceId, sheetName, sheetGid)
            if (result.isSuccess) {
                showHud("تمت إضافة الورقة «$sheetName» بنجاح", HudType.SUCCESS)
                onResult(true)
            } else {
                showHud("فشل إضافة الورقة: ${result.exceptionOrNull()?.localizedMessage}", HudType.ERROR)
                onResult(false)
            }
        }
    }

    fun readSheetRawData(sourceId: String, sheetEntityId: String, onResult: (Result<SheetReadResult>) -> Unit) {
        viewModelScope.launch {
            val res = externalRequestsRepo.readSheetRawData(sourceId, sheetEntityId)
            onResult(res)
        }
    }

    fun syncExternalSource(sourceId: String) {
        viewModelScope.launch {
            showHud("جاري مزامنة المصدر وجلب أحدث الطلبات...", HudType.INFO)
            val result = externalRequestsRepo.syncSource(sourceId)
            if (result.isSuccess) {
                val count = result.getOrThrow()
                showHud("اكتملت المزامنة بنجاح ($count طلب تم معالجته)", HudType.SUCCESS)
            } else {
                showHud("فشلت مزامنة المصدر: ${result.exceptionOrNull()?.localizedMessage}", HudType.ERROR)
            }
        }
    }

    fun syncAllExternalSources() {
        viewModelScope.launch {
            showHud("جاري مزامنة كافة مصادر Google Sheets النشطة...", HudType.INFO)
            val results = externalRequestsRepo.syncAllSources()
            val successful = results.values.count { it.isSuccess }
            val failed = results.values.count { it.isFailure }

            if (failed == 0) {
                showHud("تمت مزامنة جميع المصادر بنجاح ($successful مصدر)", HudType.SUCCESS)
            } else {
                showHud("اكتملت المزامنة: نجح $successful وفشل $failed (راجع حالة المصادر)", HudType.WARNING)
            }
        }
    }

    fun updateExternalRequestStatus(requestId: String, newStatus: String) {
        viewModelScope.launch {
            externalRequestsRepo.updateRequestStatus(requestId, newStatus)
            showHud("تم تحديث حالة الطلب إلى: $newStatus", HudType.SUCCESS)
        }
    }

    fun updateExternalRequestNotes(requestId: String, notes: String) {
        viewModelScope.launch {
            externalRequestsRepo.updateRequestNotes(requestId, notes)
            showHud("تم حفظ الملاحظات الداخلية", HudType.SUCCESS)
        }
    }

    fun convertExternalRequestToCase(
        request: ExternalRequestEntity,
        title: String,
        threatType: String,
        priority: String,
        assignedInvestigator: String,
        notes: String,
        onSuccess: (CaseEntity) -> Unit
    ) {
        viewModelScope.launch {
            val caseId = "case_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
            val caseNumber = "JB-2026-${(1000..9999).random()}"
            val currentTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())

            val timeline = "[{\"time\":\"$currentTime\",\"event\":\"تحويل الطلب الخارجي (${request.requestNumber}) من ورقة «${request.sheetName}» إلى قضية رسمية\"}]"

            val caseEntity = CaseEntity(
                id = caseId,
                caseNumber = caseNumber,
                title = title.ifBlank { "طلب خارجي: ${request.clientName}" },
                clientName = request.clientName,
                clientPhone = request.clientPhone,
                threatType = threatType.ifBlank { "ابتزاز إلكتروني" },
                priority = priority.ifBlank { request.urgency },
                status = "جديدة",
                assignedInvestigator = assignedInvestigator.ifBlank { _currentRole.value },
                timelineEventsJson = timeline,
                notes = "${notes.trim()}\n\n[مصدر خارجي: ${request.sheetName} | رقم الطلب: ${request.requestNumber} | الصف: ${request.rowId}]",
                createdDate = System.currentTimeMillis()
            )

            repository.insertOrUpdateCase(caseEntity, isNew = true)
            externalRequestsRepo.linkRequestToCase(request.id, caseId)

            repository.logAudit(
                actionType = "CREATE",
                module = "CASES",
                entityId = caseId,
                performedBy = _currentRole.value,
                details = "تحويل طلب خارجي (${request.requestNumber}) من «${request.sheetName}» إلى القضية رقم ($caseNumber)"
            )

            showHud("تم تحويل الطلب إلى قضية رسمية بنجاح ($caseNumber)", HudType.SUCCESS)
            onSuccess(caseEntity)
        }
    }
}


