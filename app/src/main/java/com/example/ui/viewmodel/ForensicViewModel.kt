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
import com.example.data.local.entities.AdminAuditLogEntity
import com.example.data.local.entities.AppSectionConfigEntity
import com.example.data.local.entities.CustomFieldDefinitionEntity
import com.example.data.local.entities.SystemCategoryEntity
import com.example.data.local.entities.SystemExpenseEntity
import com.example.data.local.entities.OfficialSourceEntity
import com.example.data.local.entities.ProfitShareRuleEntity
import com.example.data.local.entities.FinancialRevenueEntity
import com.example.data.preferences.BottomBarDataStore
import com.example.data.preferences.BottomBarItemConfig
import com.example.data.preferences.BottomBarSettings
import com.example.data.preferences.LockScreenCustomTexts
import com.example.ui.navigation.ScreenDestination
import com.example.data.remote.SheetReadResult
import com.example.data.repository.ExternalRequestsRepository
import com.example.data.repository.ForensicRepository
import com.example.ui.components.ForensicCrypto
import com.example.ui.components.HudMessage
import com.example.ui.components.HudType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
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

    // Privacy Masking (Eye Toggle Mode: Zero-CLS bullet redaction for sensitive fields)
    private val _isPrivacyMasked = MutableStateFlow(false)
    val isPrivacyMasked = _isPrivacyMasked.asStateFlow()

    // Language Toggle: "ar" or "en"
    private val _currentLanguage = MutableStateFlow("ar")
    val currentLanguage = _currentLanguage.asStateFlow()

    // Lock Screen Custom Texts (Ar & En independent customization)
    private val _lockScreenTextsAr = MutableStateFlow(LockScreenCustomTexts.defaultForLanguage("ar"))
    val lockScreenTextsAr = _lockScreenTextsAr.asStateFlow()

    private val _lockScreenTextsEn = MutableStateFlow(LockScreenCustomTexts.defaultForLanguage("en"))
    val lockScreenTextsEn = _lockScreenTextsEn.asStateFlow()

    private val _lockScreenTexts = MutableStateFlow(LockScreenCustomTexts.defaultForLanguage("ar"))
    val lockScreenTexts = _lockScreenTexts.asStateFlow()

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

    // Dynamic Scroll Header / TopBar Visibility
    val isGlobalTopBarVisible = MutableStateFlow(true)
    fun setGlobalTopBarVisible(visible: Boolean) {
        if (isGlobalTopBarVisible.value != visible) {
            isGlobalTopBarVisible.value = visible
        }
    }

    // Bottom Navigation Bar Customization (DataStore)
    private val bottomBarDataStore = BottomBarDataStore(application)

    val bottomBarSettings: StateFlow<BottomBarSettings> = bottomBarDataStore.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = bottomBarDataStore.getDefaultSettings()
        )

    val visibleBottomBarItems: StateFlow<List<BottomBarItemConfig>> = bottomBarSettings
        .map { it.activeVisibleItems }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = bottomBarDataStore.getDefaultSettings().activeVisibleItems
        )

    fun saveBottomBarSettings(settings: BottomBarSettings) {
        viewModelScope.launch {
            bottomBarDataStore.saveSettings(settings)
            showHud("تم حفظ تخصيص الشريط السفلي بنجاح", HudType.SUCCESS)
        }
    }

    fun resetBottomBarSettings() {
        viewModelScope.launch {
            bottomBarDataStore.resetToDefault()
            showHud("تمت استعادة الإعدادات الافتراضية للشريط السفلي", HudType.INFO)
        }
    }

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

    private val _sheetDiscoveryErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val sheetDiscoveryErrors: StateFlow<Map<String, String>> = _sheetDiscoveryErrors.asStateFlow()

    private val _isRefreshingSheets = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val isRefreshingSheets: StateFlow<Map<String, Boolean>> = _isRefreshingSheets.asStateFlow()

    // Admin System Management State
    val appSections = repository.allSectionConfigs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val visibleAppSections = repository.visibleSectionConfigs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val customFields = repository.allCustomFields.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val systemExpenses = repository.allExpenses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val systemCategories = repository.allCategories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val adminAuditLogs = repository.adminAuditLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val caseFileCategories: StateFlow<List<String>> = systemCategories.map { cats ->
        val fileCats = cats.filter { it.scope == "CASE_FILES" }
        if (fileCats.isEmpty()) {
            listOf("صور", "مستندات", "مراسلات", "تقارير", "مرفقات العميل", "مرفقات المنصة", "فواتير", "أخرى")
        } else {
            fileCats.map { it.name }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        listOf("صور", "مستندات", "مراسلات", "تقارير", "مرفقات العميل", "مرفقات المنصة", "فواتير", "أخرى")
    )

    val isAdminModeActive = MutableStateFlow(true)
    val topBarTitle = MutableStateFlow("منظومة جعفر بدران")
    val topBarSubtitle = MutableStateFlow("إدارة العمل والقضايا والطلبات")
    val systemLogoPath = MutableStateFlow<String?>(null)
    val favoriteSupportFormIds = MutableStateFlow<Set<String>>(emptySet())

    val profitSplitTeam = MutableStateFlow(20f)
    val profitSplitWork = MutableStateFlow(70f)
    val profitSplitReserve = MutableStateFlow(10f)

    val expenseSearchQuery = MutableStateFlow("")
    val expenseCategoryFilter = MutableStateFlow("الكل")

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
            val savedPrivacyMask = repository.getSetting("privacy_mask_mode")
            if (savedPrivacyMask != null) {
                _isPrivacyMasked.value = (savedPrivacyMask == "true")
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

            // Load customized lock screen texts for AR and EN
            val savedTextsAr = repository.getSetting("lock_screen_texts_ar")
            val textsAr = LockScreenCustomTexts.fromJson(savedTextsAr, "ar")
            _lockScreenTextsAr.value = textsAr

            val savedTextsEn = repository.getSetting("lock_screen_texts_en")
            val textsEn = LockScreenCustomTexts.fromJson(savedTextsEn, "en")
            _lockScreenTextsEn.value = textsEn

            _lockScreenTexts.value = if (_currentLanguage.value == "en") textsEn else textsAr

            val savedTitle = repository.getSettingValue("system_topbar_title", "منظومة جعفر بدران")
            topBarTitle.value = savedTitle
            val savedSub = repository.getSettingValue("system_topbar_subtitle", "إدارة العمل والقضايا والطلبات")
            topBarSubtitle.value = savedSub
            val savedLogo = repository.getSettingValue("system_logo_path", "")
            if (savedLogo.isNotBlank() && File(savedLogo).exists()) {
                systemLogoPath.value = savedLogo
            }
            val savedFavForms = repository.getSettingValue("favorite_support_forms", "")
            if (savedFavForms.isNotBlank()) {
                favoriteSupportFormIds.value = savedFavForms.split(",").filter { it.isNotBlank() }.toSet()
            }
            val savedTeamSplit = repository.getSettingValue("profit_split_team", "20").toFloatOrNull() ?: 20f
            profitSplitTeam.value = savedTeamSplit
            val savedWorkSplit = repository.getSettingValue("profit_split_work", "70").toFloatOrNull() ?: 70f
            profitSplitWork.value = savedWorkSplit
            val savedResSplit = repository.getSettingValue("profit_split_reserve", "10").toFloatOrNull() ?: 10f
            profitSplitReserve.value = savedResSplit

            if (_isLockEnabled.value) {
                _isBiometricUnlocked.value = false
            }

            // Ensure sample external requests table & sheets exist so users can immediately test
            externalRequestsRepo.ensureDefaultSampleData(force = false)
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

    // Official Sources & Law Enforcement & Partner Portals Streams
    val rawOfficialSources = repository.allOfficialSources.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val officialSourceSearchQuery = MutableStateFlow("")
    val officialSourceSectionFilter = MutableStateFlow("الكل")
    val officialSourceCategoryFilter = MutableStateFlow("الكل")
    val officialSourceFavoritesOnly = MutableStateFlow(false)

    val filteredOfficialSources: StateFlow<List<OfficialSourceEntity>> = combine(
        rawOfficialSources,
        officialSourceSearchQuery,
        officialSourceSectionFilter,
        officialSourceCategoryFilter,
        officialSourceFavoritesOnly
    ) { sources, query, section, category, favOnly ->
        sources.filter { source ->
            val matchesQuery = query.isBlank() ||
                source.name.contains(query, ignoreCase = true) ||
                source.companyOrEntity.contains(query, ignoreCase = true) ||
                source.description.contains(query, ignoreCase = true) ||
                source.officialUrl.contains(query, ignoreCase = true) ||
                source.requirements.contains(query, ignoreCase = true) ||
                source.category.contains(query, ignoreCase = true) ||
                source.region.contains(query, ignoreCase = true)
            val matchesSection = section == "الكل" || source.sectionType == section
            val matchesCategory = category == "الكل" || source.category == category
            val matchesFav = !favOnly || source.isFavorite
            matchesQuery && matchesSection && matchesCategory && matchesFav
        }.sortedWith(compareBy({ !it.isFavorite }, { it.sortOrder }, { it.companyOrEntity }))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Profit Rules & Financial Revenues Streams
    val rawProfitRules = repository.allProfitRules.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val rawFinancialRevenues = repository.allFinancialRevenues.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val financialRevenueSearchQuery = MutableStateFlow("")
    val financialRevenueTypeFilter = MutableStateFlow("الكل")

    val filteredFinancialRevenues: StateFlow<List<FinancialRevenueEntity>> = combine(
        rawFinancialRevenues,
        financialRevenueSearchQuery,
        financialRevenueTypeFilter
    ) { revenues, query, type ->
        revenues.filter { rev ->
            val matchesQuery = query.isBlank() ||
                rev.title.contains(query, ignoreCase = true) ||
                rev.clientName.contains(query, ignoreCase = true) ||
                (rev.caseNumber ?: "").contains(query, ignoreCase = true) ||
                rev.notes.contains(query, ignoreCase = true)
            val matchesType = type == "الكل" || rev.incomeType == type
            matchesQuery && matchesType
        }.sortedByDescending { it.timestamp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

            val matchesCompany = company == "الكل" || form.company.equals(company, ignoreCase = true) || form.platform.contains(company, ignoreCase = true)
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
                2 -> tool.isFavorite
                3 -> tool.lastUsedAt != null
                else -> true
            }

            matchesQuery && matchesCategory && matchesCost && matchesTab
        }
        if (tab == 3) filtered.sortedByDescending { it.lastUsedAt ?: 0L } else filtered
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
        _lockScreenTexts.value = if (lang == "en") _lockScreenTextsEn.value else _lockScreenTextsAr.value
        viewModelScope.launch {
            repository.saveSetting("app_language", lang)
        }
    }

    fun getLockScreenTextsForLanguage(lang: String): LockScreenCustomTexts {
        return if (lang == "en") _lockScreenTextsEn.value else _lockScreenTextsAr.value
    }

    fun saveLockScreenTexts(config: LockScreenCustomTexts) {
        viewModelScope.launch {
            val lang = config.language
            val json = LockScreenCustomTexts.toJson(config)
            val key = if (lang == "en") "lock_screen_texts_en" else "lock_screen_texts_ar"
            repository.saveSetting(key, json)
            if (lang == "en") {
                _lockScreenTextsEn.value = config
            } else {
                _lockScreenTextsAr.value = config
            }
            if (_currentLanguage.value == lang) {
                _lockScreenTexts.value = config
            }
            showHud(if (lang == "en") "Lock screen texts saved successfully" else "تم حفظ نصوص شاشة القفل بنجاح", HudType.SUCCESS)
            repository.logAudit(
                actionType = "UPDATE_LOCK_TEXTS",
                module = "SECURITY",
                entityId = key,
                details = "تحديث تخصيص نصوص شاشة قفل التطبيق للغة ($lang)"
            )
        }
    }

    fun resetLockScreenTexts(lang: String) {
        viewModelScope.launch {
            val defaultTexts = LockScreenCustomTexts.defaultForLanguage(lang)
            saveLockScreenTexts(defaultTexts)
            showHud(if (lang == "en") "Reset to default texts" else "تمت استعادة النصوص الافتراضية بنجاح", HudType.INFO)
        }
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

    fun logVerificationToCase(caseId: String, checkType: String, target: String, toolName: String) {
        viewModelScope.launch {
            val caseItem = repository.getCaseById(caseId) ?: return@launch
            val now = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())
            val eventText = "فحص أمني ($checkType) عبر [$toolName] للهدف: $target"
            val newJson = try {
                val array = org.json.JSONArray(caseItem.timelineEventsJson)
                val obj = org.json.JSONObject()
                obj.put("time", now)
                obj.put("event", eventText)
                array.put(obj)
                array.toString()
            } catch (e: Exception) {
                "[{\"time\":\"$now\",\"event\":\"$eventText\"}]"
            }
            val updated = caseItem.copy(
                timelineEventsJson = newJson,
                targetIdentifier = if (caseItem.targetIdentifier.isBlank()) target else caseItem.targetIdentifier,
                updatedDate = System.currentTimeMillis()
            )
            repository.insertOrUpdateCase(updated, isNew = false)
            showHud("تم توثيق فحص ($checkType) في سجل القضية ${caseItem.caseNumber}", HudType.SUCCESS)
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

    fun getEvidenceForCase(caseId: String) = repository.getEvidenceForCase(caseId)

    fun getDeletedEvidenceForCase(caseId: String) = repository.getDeletedEvidenceForCase(caseId)

    fun generateInternalCaseNumber(): String {
        val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val rand = (100000..999999).random()
        return "JB-$year-$rand"
    }

    fun attachCaseOfflineFile(
        caseId: String,
        caseNumber: String,
        fileName: String,
        fileType: String,
        originalFilename: String,
        localPath: String,
        fileSizeBytes: Long,
        fileSizeFormatted: String,
        mimeType: String,
        sha256: String,
        md5: String,
        notes: String = "",
        category: String = "مستندات",
        description: String = ""
    ) {
        viewModelScope.launch {
            val evidence = EvidenceEntity(
                id = "evi_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}",
                caseId = caseId,
                caseNumber = caseNumber,
                evidenceName = fileName,
                fileType = fileType,
                originalFilename = originalFilename,
                md5Hash = md5,
                sha256Hash = sha256,
                exifDeviceModel = "حفظ محلي مشفر",
                exifSoftware = "Jaffar Forensic Core",
                exifGpsCoords = "تخزين محلي بدون إنترنت",
                exifTimestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date()),
                chainOfCustodyLog = "تم الفحص والتوثيق محلياً بواسطة ${_currentRole.value}",
                notes = notes,
                localFilePath = localPath,
                fileSizeBytes = fileSizeBytes,
                fileSizeFormatted = fileSizeFormatted,
                mimeType = mimeType,
                category = category,
                description = description
            )
            repository.insertOrUpdateEvidence(evidence, isNew = true)
            showHud("تم إرفاق وتوثيق الملف «$fileName» بنجاح", HudType.SUCCESS)
        }
    }

    fun updateCaseFileMetadata(
        id: String,
        newName: String,
        newCategory: String,
        newDescription: String,
        newNotes: String
    ) {
        viewModelScope.launch {
            repository.updateCaseFileMetadata(id, newName, newCategory, newDescription, newNotes)
            showHud("تم تحديث وحفظ بيانات وتصنيف الملف بنجاح", HudType.SUCCESS)
        }
    }

    fun softDeleteCaseFile(evidenceId: String, fileName: String) {
        viewModelScope.launch {
            repository.softDeleteEvidence(evidenceId, fileName)
            showHud("تم نقل الملف إلى سلة محذوفات القضية", HudType.WARNING)
        }
    }

    fun restoreCaseFile(evidenceId: String, fileName: String) {
        viewModelScope.launch {
            repository.restoreEvidence(evidenceId, fileName)
            showHud("تم استعادة الملف بنجاح إلى القضية", HudType.SUCCESS)
        }
    }

    fun permanentlyDeleteCaseFile(evidenceId: String, fileName: String, filePath: String?) {
        viewModelScope.launch {
            if (!filePath.isNullOrBlank()) {
                try {
                    java.io.File(filePath).delete()
                } catch (_: Exception) {}
            }
            repository.permanentDeleteEvidence(evidenceId, fileName)
            showHud("تم التطهير والحذف النهائي للملف", HudType.SUCCESS)
        }
    }

    fun deleteEvidence(evidence: EvidenceEntity) {
        viewModelScope.launch {
            repository.softDeleteEvidence(evidence.id, evidence.evidenceName)
            showHud("تم نقل المرفق إلى سلة المحذوفات", HudType.WARNING)
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

    fun togglePrivacyMasking() {
        _isPrivacyMasked.value = !_isPrivacyMasked.value
        viewModelScope.launch {
            repository.saveSetting("privacy_mask_mode", _isPrivacyMasked.value.toString())
        }
        showHud(
            if (_isPrivacyMasked.value) "تم تفعيل وضع الخصوصية وتعتيم البيانات (Eye Mode)" else "تم تعطيل وضع الخصوصية وإظهار البيانات الحساسة",
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
    fun toggleSupportFormFavorite(formId: String) {
        val current = favoriteSupportFormIds.value
        val isFav = current.contains(formId)
        favoriteSupportFormIds.value = if (isFav) current - formId else current + formId
        showHud(if (!isFav) "تمت إضافة النموذج إلى المفضلة" else "تمت إزالة النموذج من المفضلة", HudType.INFO)
    }

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

    fun addCsvSource(name: String, csvContent: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = externalRequestsRepo.addCsvSource(name, csvContent)
            if (result.isSuccess) {
                val src = result.getOrThrow()
                repository.logAudit(
                    actionType = "CREATE",
                    module = "EXTERNAL_REQUESTS",
                    entityId = src.id,
                    performedBy = _currentRole.value,
                    details = "استيراد مصدر طلبات جديد من جدول CSV: ${src.name}"
                )
                showHud("تم استيراد مصدر الطلبات بنجاح والتعرف على الأعمدة", HudType.SUCCESS)
                onComplete(true, "تم الاستيراد بنجاح")
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "فشل استيراد جدول CSV"
                showHud(err, HudType.ERROR)
                onComplete(false, err)
            }
        }
    }

    fun seedSampleParksData(onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                externalRequestsRepo.ensureDefaultSampleData(force = true)
                showHud("تم تحميل جدول الحدائق والأوراق بنجاح", HudType.SUCCESS)
                onComplete(true)
            } catch (e: Exception) {
                showHud("تعذر تحميل البيانات التجريبية", HudType.ERROR)
                onComplete(false)
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
            _isRefreshingSheets.value = _isRefreshingSheets.value + (sourceId to true)
            _sheetDiscoveryErrors.value = _sheetDiscoveryErrors.value - sourceId
            showHud("جاري فحص وتحديث أوراق العمل من Google Sheets...", HudType.INFO)
            val result = externalRequestsRepo.refreshSheetsForSource(sourceId)
            _isRefreshingSheets.value = _isRefreshingSheets.value + (sourceId to false)
            if (result.isSuccess) {
                val sheets = result.getOrThrow()
                _sheetDiscoveryErrors.value = _sheetDiscoveryErrors.value - sourceId
                showHud("تم تحديث الأوراق بنجاح (${sheets.size} ورقة)", HudType.SUCCESS)
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "تعذر قراءة أوراق العمل"
                _sheetDiscoveryErrors.value = _sheetDiscoveryErrors.value + (sourceId to errorMsg)
                showHud(errorMsg, HudType.ERROR)
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

    fun getLinksForCase(caseId: String): kotlinx.coroutines.flow.Flow<List<com.example.data.local.entities.CaseCustomLinkEntity>> {
        return repository.getLinksForCase(caseId)
    }

    fun saveCaseWithFullMetadata(
        caseEntity: CaseEntity,
        draftImages: List<com.example.ui.screens.cases.creation.DraftCaseImage>,
        customLinks: List<com.example.ui.screens.cases.creation.DraftCustomLink>,
        customIdentifiers: List<com.example.ui.screens.cases.creation.DraftIdentifier>,
        internalCaseEmail: String,
        isNew: Boolean,
        context: Context,
        onSuccess: (CaseEntity) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val linksJson = com.example.ui.screens.cases.creation.DraftCustomLink.listToJsonString(customLinks)
                val idsJson = com.example.ui.screens.cases.creation.DraftIdentifier.listToJsonString(customIdentifiers)

                val updatedCase = caseEntity.copy(
                    internalCaseEmail = internalCaseEmail.trim(),
                    customLinksJson = linksJson,
                    customIdentifiersJson = idsJson,
                    updatedDate = System.currentTimeMillis()
                )

                // 1. Insert or update case entity
                repository.insertOrUpdateCase(updatedCase, isNew = isNew)

                // 2. Persist custom links in relational table
                val linkEntities = customLinks.mapIndexed { idx, link ->
                    com.example.data.local.entities.CaseCustomLinkEntity(
                        id = link.id,
                        caseId = updatedCase.id,
                        caseNumber = updatedCase.caseNumber,
                        title = link.title,
                        url = link.url,
                        linkType = link.linkType,
                        groupName = link.groupName,
                        notes = if (link.groupName.isNotBlank()) "مجموعة: ${link.groupName}" else "",
                        sortOrder = idx,
                        createdAt = System.currentTimeMillis()
                    )
                }
                repository.saveCustomLinksForCase(updatedCase.id, linkEntities)

                // 3. Process and persist draft images to permanent case storage
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val caseDir = java.io.File(context.filesDir, "cases/${updatedCase.id}")
                    if (!caseDir.exists()) caseDir.mkdirs()

                    draftImages.forEach { draftImg ->
                        try {
                            val safeName = draftImg.displayName.replace(Regex("[^a-zA-Z0-9._\\-\\u0600-\\u06FF]"), "_")
                            val targetFile = java.io.File(caseDir, safeName)

                            // Copy from draft cache to permanent case storage
                            draftImg.localFile.inputStream().use { input ->
                                targetFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }

                            // Compute MD5 & SHA-256
                            val md5Digest = java.security.MessageDigest.getInstance("MD5")
                            val sha256Digest = java.security.MessageDigest.getInstance("SHA-256")
                            targetFile.inputStream().use { input ->
                                val buffer = ByteArray(8192)
                                var bytesRead: Int
                                while (input.read(buffer).also { bytesRead = it } != -1) {
                                    md5Digest.update(buffer, 0, bytesRead)
                                    sha256Digest.update(buffer, 0, bytesRead)
                                }
                            }
                            val md5Hex = md5Digest.digest().joinToString("") { "%02x".format(it) }
                            val sha256Hex = sha256Digest.digest().joinToString("") { "%02x".format(it) }

                            val evidence = EvidenceEntity(
                                id = "evi_${System.currentTimeMillis()}_${java.util.UUID.randomUUID().toString().take(6)}",
                                caseId = updatedCase.id,
                                caseNumber = updatedCase.caseNumber,
                                evidenceName = safeName,
                                fileType = "صورة رقمية",
                                originalFilename = draftImg.originalFileName,
                                md5Hash = md5Hex,
                                sha256Hash = sha256Hex,
                                exifDeviceModel = "حفظ محلي مشفر",
                                exifSoftware = "Jaffar Forensic Core",
                                exifGpsCoords = "تخزين محلي بدون إنترنت",
                                exifTimestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date()),
                                chainOfCustodyLog = "تم الفحص والتوثيق محلياً أثناء إنشاء القضية بواسطة ${_currentRole.value}",
                                notes = "تم إرفاقها وتوثيقها أثناء إنشاء القضية وحساب البصمة الرقمية فوراً.",
                                localFilePath = targetFile.absolutePath,
                                fileSizeBytes = targetFile.length(),
                                fileSizeFormatted = draftImg.fileSizeFormatted,
                                mimeType = draftImg.mimeType,
                                category = "صور",
                                description = "صورة مرفقة أثناء إنشاء القضية"
                            )
                            repository.insertOrUpdateEvidence(evidence, isNew = true)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                // 4. Audit log
                repository.logAudit(
                    actionType = if (isNew) "CREATE" else "UPDATE",
                    module = "CASES",
                    entityId = updatedCase.id,
                    performedBy = _currentRole.value,
                    details = "تم ${if (isNew) "إنشاء" else "تحديث"} القضية رقم (${updatedCase.caseNumber}) مع ${draftImages.size} صور و ${customLinks.size} روابط و ${customIdentifiers.size} معرفات."
                )

                showHud("تم ${if (isNew) "إنشاء" else "تحديث"} القضية (${updatedCase.caseNumber}) بنجاح", HudType.SUCCESS)
                onSuccess(updatedCase)
            } catch (e: Exception) {
                showHud("خطأ في حفظ القضية: ${e.localizedMessage}", HudType.ERROR)
            }
        }
    }

    // ==========================================
    // ADMIN MODE: FULL SYSTEM CONTROL METHODS
    // ==========================================

    fun toggleAdminMode(enabled: Boolean) {
        _currentRole.value = if (enabled) "مدير عام (جعفر بدران)" else "مسؤول متابعة القضايا"
        isAdminModeActive.value = enabled
        showHud(if (enabled) "تم تفعيل وضع الإدارة الكامل (جعفر بدران)" else "تم إغلاق وضع الإدارة", HudType.INFO)
    }

    // App Section Management
    fun saveSectionConfig(section: AppSectionConfigEntity) {
        viewModelScope.launch {
            repository.saveSectionConfig(section)
            showHud("تم حفظ إعدادات قسم: ${section.displayName}", HudType.SUCCESS)
        }
    }

    fun toggleSectionVisibility(id: String, isVisible: Boolean, name: String) {
        viewModelScope.launch {
            repository.toggleSectionVisibility(id, isVisible, name)
            showHud(if (isVisible) "تم إظهار قسم $name" else "تم إخفاء قسم $name", HudType.INFO)
        }
    }

    fun deleteCustomSection(id: String, name: String) {
        viewModelScope.launch {
            repository.deleteCustomSection(id, name)
            showHud("تم حذف القسم المخصص: $name", HudType.WARNING)
        }
    }

    // Custom Fields Management
    fun saveCustomField(field: CustomFieldDefinitionEntity) {
        viewModelScope.launch {
            repository.saveCustomField(field)
            showHud("تم حفظ الحقل المخصص: ${field.fieldName}", HudType.SUCCESS)
        }
    }

    fun deleteCustomField(id: String, fieldName: String) {
        viewModelScope.launch {
            repository.deleteCustomField(id, fieldName)
            showHud("تم حذف الحقل المخصص: $fieldName", HudType.WARNING)
        }
    }

    // System Expenses
    fun saveExpense(expense: SystemExpenseEntity, isNew: Boolean = false) {
        viewModelScope.launch {
            repository.saveExpense(expense, isNew)
            showHud(if (isNew) "تم تسجيل المصروف بنجاح" else "تم تحديث بيانات المصروف", HudType.SUCCESS)
        }
    }

    fun deleteExpense(id: String, title: String) {
        viewModelScope.launch {
            repository.softDeleteExpense(id, title)
            showHud("تم حذف المصروف: $title", HudType.WARNING)
        }
    }

    // System Categories
    fun saveCategory(category: SystemCategoryEntity) {
        viewModelScope.launch {
            repository.saveCategory(category)
            showHud("تم حفظ التصنيف: ${category.name}", HudType.SUCCESS)
        }
    }

    fun deleteCategory(id: String, name: String) {
        viewModelScope.launch {
            repository.deleteCategory(id, name)
            showHud("تم حذف التصنيف: $name", HudType.WARNING)
        }
    }

    // Profit Split Rules
    fun updateProfitSplit(team: Float, work: Float, reserve: Float) {
        viewModelScope.launch {
            profitSplitTeam.value = team
            profitSplitWork.value = work
            profitSplitReserve.value = reserve
            repository.saveSettingValue("profit_split_team", team.toString())
            repository.saveSettingValue("profit_split_work", work.toString())
            repository.saveSettingValue("profit_split_reserve", reserve.toString())
            showHud("تم تحديث نسب توزيع الأرباح بنجاح", HudType.SUCCESS)
        }
    }

    // Top Bar Customization
    fun updateTopBarSettings(title: String, subtitle: String) {
        viewModelScope.launch {
            topBarTitle.value = title.trim().ifBlank { "منظومة جعفر بدران" }
            topBarSubtitle.value = subtitle.trim().ifBlank { "إدارة العمل والقضايا والطلبات" }
            repository.saveSettingValue("system_topbar_title", topBarTitle.value)
            repository.saveSettingValue("system_topbar_subtitle", topBarSubtitle.value)
            showHud("تم تحديث ترويسة المنظومة بنجاح", HudType.SUCCESS)
        }
    }

    // Official System Logo Management (Permanent Offline Storage)
    fun saveSystemLogo(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val logoFile = File(context.filesDir, "system_logo.png")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(logoFile).use { output ->
                        input.copyTo(output)
                    }
                }
                repository.saveSettingValue("system_logo_path", logoFile.absolutePath)
                systemLogoPath.value = logoFile.absolutePath
                showHud("تم حفظ وتثبيت الشعار بنجاح وسيتم تطبيقه على التقارير تلقائياً", HudType.SUCCESS)
            } catch (e: Exception) {
                showHud("تعذر حفظ الشعار: ${e.localizedMessage}", HudType.ERROR)
            }
        }
    }

    fun deleteSystemLogo(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val logoFile = File(context.filesDir, "system_logo.png")
                if (logoFile.exists()) {
                    logoFile.delete()
                }
                val currentPath = systemLogoPath.value
                if (currentPath != null) {
                    val f = File(currentPath)
                    if (f.exists()) f.delete()
                }
                repository.saveSettingValue("system_logo_path", "")
                systemLogoPath.value = null
                showHud("تم حذف الشعار واستعادة الشعار النصي الافتراضي", HudType.INFO)
            } catch (e: Exception) {
                showHud("تعذر حذف الشعار: ${e.localizedMessage}", HudType.ERROR)
            }
        }
    }

    // Support Form Favorite Toggle
    fun toggleFavoriteSupportForm(formId: String) {
        viewModelScope.launch {
            val current = favoriteSupportFormIds.value.toMutableSet()
            val wasFav = current.contains(formId)
            if (wasFav) {
                current.remove(formId)
            } else {
                current.add(formId)
            }
            favoriteSupportFormIds.value = current
            repository.saveSettingValue("favorite_support_forms", current.joinToString(","))
            showHud(if (wasFav) "تمت إزالة النموذج من المفضلة" else "تمت إضافة النموذج إلى المفضلة", HudType.INFO)
        }
    }

    // Full Case Modification
    fun updateFullCase(case: CaseEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateCase(case, isNew = false)
            repository.logAdminAction(
                action = "تعديل إداري شامل للقضية",
                target = "${case.caseNumber} - ${case.title}",
                newValue = "الحالة: ${case.status} | المالي: ${case.totalAmount} SAR | العميل: ${case.clientName}"
            )
            showHud("تم تحديث بيانات القضية بالكامل (${case.caseNumber})", HudType.SUCCESS)
        }
    }

    // Full Client Modification
    fun updateFullClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateClient(client, isNew = false)
            repository.logAdminAction(
                action = "تعديل إداري لبيانات العميل",
                target = client.fullName,
                newValue = "الهاتف: ${client.phoneNumber} | المخاطر: ${client.riskLevel}"
            )
            showHud("تم تحديث بيانات العميل بنجاح", HudType.SUCCESS)
        }
    }

    // Investigation Tools Administration
    fun saveInvestigationTool(tool: InvestigationToolEntity) {
        viewModelScope.launch {
            database.investigationToolDao().insertOrUpdate(tool)
            repository.logAdminAction(
                action = "إضافة/تعديل أداة تقصي",
                target = tool.name,
                newValue = "${tool.category} | ${tool.url}"
            )
            showHud("تم حفظ الأداة بنجاح: ${tool.name}", HudType.SUCCESS)
        }
    }

    fun deleteInvestigationTool(id: String, name: String) {
        viewModelScope.launch {
            database.investigationToolDao().permanentDelete(id)
            repository.logAdminAction(
                action = "حذف أداة تقصي",
                target = name,
                oldValue = id,
                newValue = "تم الحذف"
            )
            showHud("تم حذف الأداة: $name", HudType.WARNING)
        }
    }

    // Support Forms Administration
    fun saveSupportForm(form: SupportFormEntity) {
        viewModelScope.launch {
            database.supportFormDao().insertOrUpdate(form)
            repository.logAdminAction(
                action = "إضافة/تعديل نموذج دعم",
                target = form.formName,
                newValue = "${form.company} | ${form.formUrl}"
            )
            showHud("تم حفظ نموذج الدعم: ${form.formName}", HudType.SUCCESS)
        }
    }

    fun deleteSupportForm(id: String, name: String) {
        viewModelScope.launch {
            database.supportFormDao().permanentDelete(id)
            repository.logAdminAction(
                action = "حذف نموذج دعم",
                target = name,
                oldValue = id,
                newValue = "تم الحذف"
            )
            showHud("تم حذف نموذج الدعم: $name", HudType.WARNING)
        }
    }

    // Official Sources Management
    fun saveOfficialSource(source: OfficialSourceEntity, isNew: Boolean = false) {
        viewModelScope.launch {
            repository.insertOrUpdateOfficialSource(source)
            showHud(if (isNew) "تمت إضافة البوابة/المصدر بنجاح" else "تم تحديث بيانات المصدر", HudType.SUCCESS)
        }
    }

    fun deleteOfficialSource(id: String, name: String) {
        viewModelScope.launch {
            repository.deleteOfficialSource(id, name)
            showHud("تم حذف المصدر: $name", HudType.WARNING)
        }
    }

    fun toggleOfficialSourceVisibility(id: String, isVisible: Boolean) {
        viewModelScope.launch {
            repository.toggleOfficialSourceVisibility(id, isVisible)
            showHud(if (isVisible) "تم إظهار المصدر" else "تم إخفاء المصدر", HudType.INFO)
        }
    }

    fun toggleOfficialSourceFavorite(id: String, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleOfficialSourceFavorite(id, isFavorite)
        }
    }

    // Profit Share Rules Management
    fun saveProfitRule(rule: ProfitShareRuleEntity, isNew: Boolean = false) {
        viewModelScope.launch {
            repository.insertOrUpdateProfitRule(rule)
            showHud(if (isNew) "تمت إضافة بند توزيع الأرباح بنجاح" else "تم تحديث بند الأرباح", HudType.SUCCESS)
        }
    }

    fun deleteProfitRule(id: String, name: String) {
        viewModelScope.launch {
            repository.deleteProfitRule(id, name)
            showHud("تم حذف بند توزيع الأرباح: $name", HudType.WARNING)
        }
    }

    // Financial Revenues Management
    fun saveFinancialRevenue(revenue: FinancialRevenueEntity, isNew: Boolean = false) {
        viewModelScope.launch {
            repository.insertOrUpdateRevenue(revenue)
            showHud(if (isNew) "تم تسجيل الإيراد بنجاح" else "تم تحديث بيانات الإيراد", HudType.SUCCESS)
        }
    }

    fun deleteFinancialRevenue(id: String, title: String) {
        viewModelScope.launch {
            repository.deleteRevenue(id, title)
            showHud("تم حذف الإيراد: $title", HudType.WARNING)
        }
    }

    // Backup & Restore
    suspend fun exportSystemBackupJson(): String {
        return repository.exportCompleteBackupJson()
    }

    fun restoreSystemBackup(json: String, mergeMode: Boolean, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.restoreBackupFromJson(json, mergeMode)
            if (result.first) {
                showHud("تمت استعادة المنظومة بنجاح", HudType.SUCCESS)
            } else {
                showHud("فشل استيراد النسخة الاحتياطية", HudType.ERROR)
            }
            onResult(result.first, result.second)
        }
    }
}


