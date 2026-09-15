package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.AdminAuditLogEntity
import com.example.data.local.entities.AppSectionConfigEntity
import com.example.data.local.entities.CaseEntity
import com.example.data.local.entities.ClientEntity
import com.example.data.local.entities.CustomFieldDefinitionEntity
import com.example.data.local.entities.InvestigationToolEntity
import com.example.data.local.entities.SupportFormEntity
import com.example.data.local.entities.SystemCategoryEntity
import com.example.data.local.entities.OfficialSourceEntity
import com.example.data.local.entities.ProfitShareRuleEntity
import com.example.data.local.entities.FinancialRevenueEntity
import com.example.data.local.entities.SystemExpenseEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.HudType
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberInfo
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberPrimaryLight
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSuccess
import com.example.ui.theme.CyberWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ForensicViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AdminDashboardScreen(
    viewModel: ForensicViewModel,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val appSections by viewModel.appSections.collectAsStateWithLifecycle()
    val customFields by viewModel.customFields.collectAsStateWithLifecycle()
    val systemExpenses by viewModel.systemExpenses.collectAsStateWithLifecycle()
    val systemCategories by viewModel.systemCategories.collectAsStateWithLifecycle()
    val adminAuditLogs by viewModel.adminAuditLogs.collectAsStateWithLifecycle()
    val allCases by viewModel.repository.allCases.collectAsStateWithLifecycle(emptyList())
    val allClients by viewModel.repository.allClients.collectAsStateWithLifecycle(emptyList())
    val allTools by viewModel.repository.allInvestigationTools.collectAsStateWithLifecycle(emptyList())
    val allForms by viewModel.repository.allSupportForms.collectAsStateWithLifecycle(emptyList())
    val allOfficialSources by viewModel.rawOfficialSources.collectAsStateWithLifecycle()
    val allProfitRules by viewModel.rawProfitRules.collectAsStateWithLifecycle()
    val allRevenues by viewModel.rawFinancialRevenues.collectAsStateWithLifecycle()

    val topBarTitle by viewModel.topBarTitle.collectAsStateWithLifecycle()
    val topBarSubtitle by viewModel.topBarSubtitle.collectAsStateWithLifecycle()
    val profitTeam by viewModel.profitSplitTeam.collectAsStateWithLifecycle()
    val profitWork by viewModel.profitSplitWork.collectAsStateWithLifecycle()
    val profitReserve by viewModel.profitSplitReserve.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "الأقسام والقوائم",
        "المصادر والنماذج",
        "القضايا والعملاء",
        "الحقول المخصصة",
        "المالية والمصروفات",
        "التصنيفات العامة",
        "النسخ الاحتياطي والأمان"
    )

    // State for Dialogs
    var showSectionEditDialog by remember { mutableStateOf<AppSectionConfigEntity?>(null) }
    var showNewSectionDialog by remember { mutableStateOf(false) }

    var showCustomFieldDialog by remember { mutableStateOf<CustomFieldDefinitionEntity?>(null) }
    var showNewCustomFieldDialog by remember { mutableStateOf(false) }

    var showExpenseDialog by remember { mutableStateOf<SystemExpenseEntity?>(null) }
    var showNewExpenseDialog by remember { mutableStateOf(false) }

    var showCategoryDialog by remember { mutableStateOf<SystemCategoryEntity?>(null) }
    var showNewCategoryDialog by remember { mutableStateOf(false) }

    var showToolDialog by remember { mutableStateOf<InvestigationToolEntity?>(null) }
    var showNewToolDialog by remember { mutableStateOf(false) }

    var showFormDialog by remember { mutableStateOf<SupportFormEntity?>(null) }
    var showNewFormDialog by remember { mutableStateOf(false) }

    var showOfficialSourceDialog by remember { mutableStateOf<OfficialSourceEntity?>(null) }
    var showNewOfficialSourceDialog by remember { mutableStateOf(false) }

    var showRevenueDialog by remember { mutableStateOf<FinancialRevenueEntity?>(null) }
    var showNewRevenueDialog by remember { mutableStateOf(false) }

    var showProfitRuleDialog by remember { mutableStateOf<ProfitShareRuleEntity?>(null) }
    var showNewProfitRuleDialog by remember { mutableStateOf(false) }

    var showEditCaseDialog by remember { mutableStateOf<CaseEntity?>(null) }
    var showEditClientDialog by remember { mutableStateOf<ClientEntity?>(null) }

    var showTopBarConfigDialog by remember { mutableStateOf(false) }
    var showBackupRestoreDialog by remember { mutableStateOf(false) }
    var backupJsonExported by remember { mutableStateOf("") }
    var restoreJsonInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Executive Admin Sleek Bar (Touch friendly & space efficient)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCardElevated),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "إدارة المنظومة",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "وضع المدير العام",
                                    color = CyberSuccess,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "الترويسة: «$topBarTitle»",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showTopBarConfigDialog = true },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberPrimary.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "تعديل الترويسة", tint = CyberPrimaryLight, modifier = Modifier.size(16.dp))
                        }

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    backupJsonExported = viewModel.exportSystemBackupJson()
                                    showBackupRestoreDialog = true
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberSecondary.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "نسخ احتياطي واستعادة", tint = CyberSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Horizontal Tabs - Sleek, Touch-Responsive & Compact Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                FilterChip(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    label = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberPrimary.copy(alpha = 0.22f),
                        selectedLabelColor = CyberPrimaryLight
                    )
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            when (selectedTab) {
                0 -> AdminSectionsTab(
                    sections = appSections,
                    onToggleVisibility = { id, vis, name -> viewModel.toggleSectionVisibility(id, vis, name) },
                    onEditSection = { showSectionEditDialog = it },
                    onDeleteSection = { id, name -> viewModel.deleteCustomSection(id, name) },
                    onAddNewSection = { showNewSectionDialog = true }
                )
                1 -> AdminSourcesAndFormsTab(
                    officialSources = allOfficialSources,
                    tools = allTools,
                    forms = allForms,
                    onEditOfficialSource = { showOfficialSourceDialog = it },
                    onDeleteOfficialSource = { id, name -> viewModel.deleteOfficialSource(id, name) },
                    onToggleOfficialSourceVisibility = { id, vis -> viewModel.toggleOfficialSourceVisibility(id, vis) },
                    onAddNewOfficialSource = { showNewOfficialSourceDialog = true },
                    onEditTool = { showToolDialog = it },
                    onDeleteTool = { id, name -> viewModel.deleteInvestigationTool(id, name) },
                    onAddNewTool = { showNewToolDialog = true },
                    onEditForm = { showFormDialog = it },
                    onDeleteForm = { id, name -> viewModel.deleteSupportForm(id, name) },
                    onAddNewForm = { showNewFormDialog = true }
                )
                2 -> AdminCasesAndClientsTab(
                    cases = allCases,
                    clients = allClients,
                    onEditCase = { showEditCaseDialog = it },
                    onEditClient = { showEditClientDialog = it }
                )
                3 -> AdminCustomFieldsTab(
                    fields = customFields,
                    onEditField = { showCustomFieldDialog = it },
                    onDeleteField = { id, name -> viewModel.deleteCustomField(id, name) },
                    onAddNewField = { showNewCustomFieldDialog = true }
                )
                4 -> AdminFinancesTab(
                    expenses = systemExpenses,
                    cases = allCases,
                    revenues = allRevenues,
                    profitRules = allProfitRules,
                    profitTeam = profitTeam,
                    profitWork = profitWork,
                    profitReserve = profitReserve,
                    onUpdateProfitSplit = { team, work, res -> viewModel.updateProfitSplit(team, work, res) },
                    onEditExpense = { showExpenseDialog = it },
                    onDeleteExpense = { id, title -> viewModel.deleteExpense(id, title) },
                    onAddNewExpense = { showNewExpenseDialog = true },
                    onEditRevenue = { showRevenueDialog = it },
                    onDeleteRevenue = { id, title -> viewModel.deleteFinancialRevenue(id, title) },
                    onAddNewRevenue = { showNewRevenueDialog = true },
                    onEditProfitRule = { showProfitRuleDialog = it },
                    onDeleteProfitRule = { id, name -> viewModel.deleteProfitRule(id, name) },
                    onAddNewProfitRule = { showNewProfitRuleDialog = true }
                )
                5 -> AdminCategoriesTab(
                    categories = systemCategories,
                    onEditCategory = { showCategoryDialog = it },
                    onDeleteCategory = { id, name -> viewModel.deleteCategory(id, name) },
                    onAddNewCategory = { showNewCategoryDialog = true }
                )
                6 -> AdminBackupAndAuditTab(
                    auditLogs = adminAuditLogs,
                    onOpenBackupRestore = {
                        coroutineScope.launch {
                            backupJsonExported = viewModel.exportSystemBackupJson()
                            showBackupRestoreDialog = true
                        }
                    }
                )
            }
        }
    }

    // ==========================================
    // DIALOGS
    // ==========================================

    // Top Bar Customization Dialog
    if (showTopBarConfigDialog) {
        var newTitle by remember { mutableStateOf(topBarTitle) }
        var newSubtitle by remember { mutableStateOf(topBarSubtitle) }

        AlertDialog(
            onDismissRequest = { showTopBarConfigDialog = false },
            title = { Text("تخصيص ترويسة المنظومة", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("يمكنك تغيير الاسم والشعار الذي يظهر أعلى شاشات التطبيق فوراً:", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("العنوان الرئيسي") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSubtitle,
                        onValueChange = { newSubtitle = it },
                        label = { Text("العنوان الفرعي / الوصف") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateTopBarSettings(newTitle, newSubtitle)
                        showTopBarConfigDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                ) { Text("حفظ فوري") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showTopBarConfigDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // Edit Section Dialog
    if (showSectionEditDialog != null) {
        val s = showSectionEditDialog!!
        var dName by remember { mutableStateOf(s.displayName) }
        var dDesc by remember { mutableStateOf(s.description) }
        var dIcon by remember { mutableStateOf(s.iconName) }
        var dOrder by remember { mutableStateOf(s.sortOrder.toString()) }
        var dVis by remember { mutableStateOf(s.isVisible) }

        AlertDialog(
            onDismissRequest = { showSectionEditDialog = null },
            title = { Text("تعديل إعدادات القسم (${s.id})", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dName,
                        onValueChange = { dName = it },
                        label = { Text("اسم القسم المعروض") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dDesc,
                        onValueChange = { dDesc = it },
                        label = { Text("وصف القسم") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dIcon,
                        onValueChange = { dIcon = it },
                        label = { Text("اسم الأيقونة (Folder, Home, People, ...)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dOrder,
                        onValueChange = { dOrder = it },
                        label = { Text("ترتيب الظهور") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("الظهور في القائمة والمنظومة:")
                        Switch(
                            checked = dVis,
                            onCheckedChange = { dVis = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberSuccess)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = s.copy(
                            displayName = dName.trim(),
                            description = dDesc.trim(),
                            iconName = dIcon.trim(),
                            sortOrder = dOrder.toIntOrNull() ?: s.sortOrder,
                            isVisible = dVis
                        )
                        viewModel.saveSectionConfig(updated)
                        showSectionEditDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                ) { Text("حفظ التعديلات") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showSectionEditDialog = null }) { Text("إلغاء") }
            }
        )
    }

    // Add New Custom Section Dialog
    if (showNewSectionDialog) {
        var nId by remember { mutableStateOf("SECTION_${System.currentTimeMillis().toString().takeLast(4)}") }
        var nName by remember { mutableStateOf("") }
        var nDesc by remember { mutableStateOf("") }
        var nIcon by remember { mutableStateOf("Folder") }
        var nCategory by remember { mutableStateOf("قسم مخصص") }

        AlertDialog(
            onDismissRequest = { showNewSectionDialog = false },
            title = { Text("إضافة قسم مخصص جديد", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nName,
                        onValueChange = { nName = it },
                        label = { Text("اسم القسم الجديد *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nDesc,
                        onValueChange = { nDesc = it },
                        label = { Text("وصف القسم والغرض منه") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nCategory,
                        onValueChange = { nCategory = it },
                        label = { Text("التصنيف الرئيسي") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = nIcon,
                        onValueChange = { nIcon = it },
                        label = { Text("رمز الأيقونة (Folder, Assignment, ...)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nName.isNotBlank()) {
                            val newSection = AppSectionConfigEntity(
                                id = nId,
                                displayName = nName.trim(),
                                description = nDesc.trim(),
                                iconName = nIcon.trim(),
                                sortOrder = appSections.size + 1,
                                isVisible = true,
                                isCustom = true,
                                category = nCategory.trim()
                            )
                            viewModel.saveSectionConfig(newSection)
                            showNewSectionDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    enabled = nName.isNotBlank()
                ) { Text("إضافة القسم") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showNewSectionDialog = false }) { Text("إلغاء") }
            }
        )
    }

    // Custom Field Dialog (Add / Edit)
    if (showNewCustomFieldDialog || showCustomFieldDialog != null) {
        val existing = showCustomFieldDialog
        var targetEnt by remember { mutableStateOf(existing?.targetEntity ?: "CASE") }
        var fName by remember { mutableStateOf(existing?.fieldName ?: "") }
        var fType by remember { mutableStateOf(existing?.fieldType ?: "TEXT") }
        var fOptions by remember { mutableStateOf(existing?.optionsJson ?: "") }
        var fReq by remember { mutableStateOf(existing?.isRequired ?: false) }
        var fShowList by remember { mutableStateOf(existing?.showInList ?: true) }

        AlertDialog(
            onDismissRequest = {
                showNewCustomFieldDialog = false
                showCustomFieldDialog = null
            },
            title = { Text(if (existing == null) "إضافة حقل مخصص جديد" else "تعديل الحقل المخصص", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("اختر الجدول المستهدف:", fontSize = 12.sp, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("CASE" to "القضايا", "CLIENT" to "العملاء", "REQUEST" to "الطلبات").forEach { (code, lbl) ->
                            Button(
                                onClick = { targetEnt = code },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (targetEnt == code) CyberPrimary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(lbl, fontSize = 11.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = fName,
                        onValueChange = { fName = it },
                        label = { Text("اسم الحقل *") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("نوع الحقل:", fontSize = 12.sp, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("TEXT" to "نص", "NUMBER" to "رقم", "SELECT" to "قائمة", "DATE" to "تاريخ").forEach { (code, lbl) ->
                            Button(
                                onClick = { fType = code },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (fType == code) CyberSecondary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(lbl, fontSize = 11.sp)
                            }
                        }
                    }

                    if (fType == "SELECT") {
                        OutlinedTextField(
                            value = fOptions,
                            onValueChange = { fOptions = it },
                            label = { Text("الخيارات مفصولة بفواصل (أ, ب, ج)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("حقل إلزامي:")
                        Switch(checked = fReq, onCheckedChange = { fReq = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("إظهار في بطاقة القائمة:")
                        Switch(checked = fShowList, onCheckedChange = { fShowList = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fName.isNotBlank()) {
                            val def = CustomFieldDefinitionEntity(
                                id = existing?.id ?: "cf_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                                targetEntity = targetEnt,
                                fieldName = fName.trim(),
                                fieldType = fType,
                                optionsJson = fOptions.trim(),
                                isRequired = fReq,
                                showInList = fShowList,
                                showInDetails = true,
                                sortOrder = customFields.size + 1
                            )
                            viewModel.saveCustomField(def)
                            showNewCustomFieldDialog = false
                            showCustomFieldDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    enabled = fName.isNotBlank()
                ) { Text("حفظ الحقل") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showNewCustomFieldDialog = false
                    showCustomFieldDialog = null
                }) { Text("إلغاء") }
            }
        )
    }

    // Expense Dialog (Add / Edit)
    if (showNewExpenseDialog || showExpenseDialog != null) {
        val existing = showExpenseDialog
        var eTitle by remember { mutableStateOf(existing?.title ?: "") }
        var eAmount by remember { mutableStateOf(existing?.amount?.toString() ?: "") }
        var eCategory by remember { mutableStateOf(existing?.category ?: "أدوات وبرامج تقنية") }
        var eDate by remember { mutableStateOf(existing?.date ?: "2026-09-12") }
        var eNotes by remember { mutableStateOf(existing?.notes ?: "") }

        AlertDialog(
            onDismissRequest = {
                showNewExpenseDialog = false
                showExpenseDialog = null
            },
            title = { Text(if (existing == null) "تسجيل مصروف تشغيلي جديد" else "تعديل المصروف", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = eTitle,
                        onValueChange = { eTitle = it },
                        label = { Text("عنوان المصروف *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = eAmount,
                        onValueChange = { eAmount = it },
                        label = { Text("المبلغ (SAR) *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = eCategory,
                        onValueChange = { eCategory = it },
                        label = { Text("التصنيف (سيرفرات، أدوات، مكتب، ...)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = eDate,
                        onValueChange = { eDate = it },
                        label = { Text("التاريخ (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = eNotes,
                        onValueChange = { eNotes = it },
                        label = { Text("ملاحظات إضافية") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = eAmount.toDoubleOrNull() ?: 0.0
                        if (eTitle.isNotBlank() && amt > 0) {
                            val exp = SystemExpenseEntity(
                                id = existing?.id ?: "exp_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                                title = eTitle.trim(),
                                amount = amt,
                                category = eCategory.trim(),
                                date = eDate.trim(),
                                notes = eNotes.trim(),
                                relatedCaseId = existing?.relatedCaseId,
                                createdDate = existing?.createdDate ?: System.currentTimeMillis()
                            )
                            viewModel.saveExpense(exp, isNew = (existing == null))
                            showNewExpenseDialog = false
                            showExpenseDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    enabled = eTitle.isNotBlank() && (eAmount.toDoubleOrNull() ?: 0.0) > 0
                ) { Text("حفظ المصروف") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showNewExpenseDialog = false
                    showExpenseDialog = null
                }) { Text("إلغاء") }
            }
        )
    }

    // Category Dialog (Add / Edit)
    if (showNewCategoryDialog || showCategoryDialog != null) {
        val existing = showCategoryDialog
        var cScope by remember { mutableStateOf(existing?.scope ?: "CASES") }
        var cName by remember { mutableStateOf(existing?.name ?: "") }
        var cColor by remember { mutableStateOf(existing?.color ?: "#0288D1") }

        AlertDialog(
            onDismissRequest = {
                showNewCategoryDialog = false
                showCategoryDialog = null
            },
            title = { Text(if (existing == null) "إضافة تصنيف جديد" else "تعديل التصنيف", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("المجال المستهدف:", fontSize = 12.sp, color = TextSecondary)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "CASE_FILES" to "ملفات القضية",
                            "CASES" to "قضايا",
                            "TOOLS" to "أدوات",
                            "FORMS" to "نماذج",
                            "EXPENSES" to "مصاريف"
                        ).forEach { (code, lbl) ->
                            Button(
                                onClick = { cScope = code },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (cScope == code) CyberPrimary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(lbl, fontSize = 11.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = cName,
                        onValueChange = { cName = it },
                        label = { Text("اسم التصنيف *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cColor,
                        onValueChange = { cColor = it },
                        label = { Text("رمز اللون (مثال: #E53935)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cName.isNotBlank()) {
                            val cat = SystemCategoryEntity(
                                id = existing?.id ?: "cat_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                                scope = cScope,
                                name = cName.trim(),
                                color = cColor.trim(),
                                sortOrder = systemCategories.size + 1
                            )
                            viewModel.saveCategory(cat)
                            showNewCategoryDialog = false
                            showCategoryDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    enabled = cName.isNotBlank()
                ) { Text("حفظ التصنيف") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showNewCategoryDialog = false
                    showCategoryDialog = null
                }) { Text("إلغاء") }
            }
        )
    }

    // Edit Case Dialog (Admin Full Control)
    if (showEditCaseDialog != null) {
        val c = showEditCaseDialog!!
        var cNum by remember { mutableStateOf(c.caseNumber) }
        var cTitle by remember { mutableStateOf(c.title) }
        var cClient by remember { mutableStateOf(c.clientName) }
        var cPhone by remember { mutableStateOf(c.clientPhone) }
        var cThreat by remember { mutableStateOf(c.threatType) }
        var cStatus by remember { mutableStateOf(c.status) }
        var cPriority by remember { mutableStateOf(c.priority) }
        var cTotal by remember { mutableStateOf(c.totalAmount.toString()) }
        var cPaid by remember { mutableStateOf(c.paidAmount.toString()) }
        var cInv by remember { mutableStateOf(c.assignedInvestigator) }
        var cNotes by remember { mutableStateOf(c.notes) }

        AlertDialog(
            onDismissRequest = { showEditCaseDialog = null },
            title = { Text("تعديل إداري شامل للقضية (${c.caseNumber})", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(value = cNum, onValueChange = { cNum = it }, label = { Text("رقم القضية") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = cTitle, onValueChange = { cTitle = it }, label = { Text("عنوان القضية") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = cClient, onValueChange = { cClient = it }, label = { Text("اسم العميل") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = cPhone, onValueChange = { cPhone = it }, label = { Text("هاتف العميل") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = cThreat, onValueChange = { cThreat = it }, label = { Text("نوع التهديد") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = cStatus, onValueChange = { cStatus = it }, label = { Text("الحالة (جديدة، قيد التحقيق، مكتملة)") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = cPriority, onValueChange = { cPriority = it }, label = { Text("الأولوية (حرجة، عالية، متوسطة، منخفضة)") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = cTotal, onValueChange = { cTotal = it }, label = { Text("المبلغ الإجمالي (SAR)") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = cPaid, onValueChange = { cPaid = it }, label = { Text("المبلغ المدفوع (SAR)") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = cInv, onValueChange = { cInv = it }, label = { Text("المحقق المسؤول") }, modifier = Modifier.fillMaxWidth())
                    }
                    item {
                        OutlinedTextField(value = cNotes, onValueChange = { cNotes = it }, label = { Text("ملاحظات إدارية وخاصة") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val total = cTotal.toDoubleOrNull() ?: c.totalAmount
                        val paid = cPaid.toDoubleOrNull() ?: c.paidAmount
                        val rem = (total - paid).coerceAtLeast(0.0)
                        val payStatus = when {
                            paid <= 0 -> "غير مدفوع"
                            paid >= total -> "مدفوع بالكامل"
                            else -> "مدفوع جزئيًا"
                        }
                        val updated = c.copy(
                            caseNumber = cNum.trim(),
                            title = cTitle.trim(),
                            clientName = cClient.trim(),
                            clientPhone = cPhone.trim(),
                            threatType = cThreat.trim(),
                            status = cStatus.trim(),
                            priority = cPriority.trim(),
                            totalAmount = total,
                            paidAmount = paid,
                            remainingAmount = rem,
                            paymentStatus = payStatus,
                            assignedInvestigator = cInv.trim(),
                            notes = cNotes.trim()
                        )
                        viewModel.updateFullCase(updated)
                        showEditCaseDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                ) { Text("حفظ القضية") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditCaseDialog = null }) { Text("إلغاء") }
            }
        )
    }

    // Edit Client Dialog
    if (showEditClientDialog != null) {
        val cl = showEditClientDialog!!
        var clName by remember { mutableStateOf(cl.fullName) }
        var clPhone by remember { mutableStateOf(cl.phoneNumber) }
        var clRisk by remember { mutableStateOf(cl.riskLevel) }
        var clNotes by remember { mutableStateOf(cl.notes) }

        AlertDialog(
            onDismissRequest = { showEditClientDialog = null },
            title = { Text("تعديل بيانات العميل (${cl.fullName})", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = clName, onValueChange = { clName = it }, label = { Text("الاسم الكامل") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = clPhone, onValueChange = { clPhone = it }, label = { Text("رقم الهاتف") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = clRisk, onValueChange = { clRisk = it }, label = { Text("مستوى الخطورة (عالي، متوسط، منخفض)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = clNotes, onValueChange = { clNotes = it }, label = { Text("ملاحظات وسجل العميل") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = cl.copy(
                            fullName = clName.trim(),
                            phoneNumber = clPhone.trim(),
                            riskLevel = clRisk.trim(),
                            notes = clNotes.trim()
                        )
                        viewModel.updateFullClient(updated)
                        showEditClientDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
                ) { Text("حفظ العميل") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditClientDialog = null }) { Text("إلغاء") }
            }
        )
    }

    // Tool Dialog (Add / Edit)
    if (showNewToolDialog || showToolDialog != null) {
        val existing = showToolDialog
        var tName by remember { mutableStateOf(existing?.name ?: "") }
        var tCat by remember { mutableStateOf(existing?.category ?: "OSINT") }
        var tUrl by remember { mutableStateOf(existing?.url ?: "https://") }
        var tDesc by remember { mutableStateOf(existing?.description ?: "") }

        AlertDialog(
            onDismissRequest = {
                showNewToolDialog = false
                showToolDialog = null
            },
            title = { Text(if (existing == null) "إضافة أداة / مصدر تقصي جديد" else "تعديل الأداة", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = tName, onValueChange = { tName = it }, label = { Text("اسم الأداة *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tCat, onValueChange = { tCat = it }, label = { Text("التصنيف *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tUrl, onValueChange = { tUrl = it }, label = { Text("رابط الأداة المباشر *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tDesc, onValueChange = { tDesc = it }, label = { Text("وصف الأداة واستخداماتها") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tName.isNotBlank() && tUrl.isNotBlank()) {
                            val tool = InvestigationToolEntity(
                                id = existing?.id ?: "tool_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                                name = tName.trim(),
                                url = tUrl.trim(),
                                category = tCat.trim(),
                                subcategory = "أدوات تقصي",
                                description = tDesc.trim(),
                                officialDomain = if (tUrl.contains("://")) tUrl.substringAfter("://").substringBefore("/") else tUrl,
                                freeOrPaid = "مجاني",
                                isFavorite = existing?.isFavorite ?: false
                            )
                            viewModel.saveInvestigationTool(tool)
                            showNewToolDialog = false
                            showToolDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    enabled = tName.isNotBlank() && tUrl.isNotBlank()
                ) { Text("حفظ الأداة") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showNewToolDialog = false
                    showToolDialog = null
                }) { Text("إلغاء") }
            }
        )
    }

    // Form Dialog (Add / Edit)
    if (showNewFormDialog || showFormDialog != null) {
        val existing = showFormDialog
        var fName by remember { mutableStateOf(existing?.formName ?: "") }
        var fCompany by remember { mutableStateOf(existing?.company ?: "") }
        var fUrl by remember { mutableStateOf(existing?.formUrl ?: "https://") }
        var fCat by remember { mutableStateOf(existing?.category ?: "بوابات رسمية") }
        var fPlatform by remember { mutableStateOf(existing?.platform ?: "منصات الدعم") }

        AlertDialog(
            onDismissRequest = {
                showNewFormDialog = false
                showFormDialog = null
            },
            title = { Text(if (existing == null) "إضافة بوابة / نموذج دعم رسمي" else "تعديل نموذج الدعم", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = fName, onValueChange = { fName = it }, label = { Text("اسم البوابة / النموذج *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = fCompany, onValueChange = { fCompany = it }, label = { Text("الجهة / المنصة التابعة *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = fUrl, onValueChange = { fUrl = it }, label = { Text("الرابط المباشر *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = fCat, onValueChange = { fCat = it }, label = { Text("التصنيف") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = fPlatform, onValueChange = { fPlatform = it }, label = { Text("نوع المنصة") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fName.isNotBlank() && fUrl.isNotBlank()) {
                            val form = SupportFormEntity(
                                id = existing?.id ?: "form_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                                company = fCompany.trim(),
                                platform = fPlatform.trim(),
                                category = fCat.trim(),
                                problemType = "عام",
                                formName = fName.trim(),
                                formUrl = fUrl.trim(),
                                urlType = "DIRECT_FORM",
                                officialDomain = if (fUrl.contains("://")) fUrl.substringAfter("://").substringBefore("/") else fUrl
                            )
                            viewModel.saveSupportForm(form)
                            showNewFormDialog = false
                            showFormDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    enabled = fName.isNotBlank() && fUrl.isNotBlank()
                ) { Text("حفظ النموذج") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showNewFormDialog = false
                    showFormDialog = null
                }) { Text("إلغاء") }
            }
        )
    }

    // Official Source Dialog (Add / Edit)
    if (showNewOfficialSourceDialog || showOfficialSourceDialog != null) {
        val existing = showOfficialSourceDialog
        var sName by remember { mutableStateOf(existing?.name ?: "") }
        var sEntity by remember { mutableStateOf(existing?.companyOrEntity ?: "") }
        var sCategory by remember { mutableStateOf(existing?.category ?: "بوابات إنفاذ القانون") }
        var sSection by remember { mutableStateOf(existing?.sectionType ?: "LAW_ENFORCEMENT") }
        var sUrl by remember { mutableStateOf(existing?.officialUrl ?: "") }
        var sReqs by remember { mutableStateOf(existing?.requirements ?: "") }
        var sRegion by remember { mutableStateOf(existing?.region ?: "عالمي (Global)") }
        var sEmail by remember { mutableStateOf(existing?.notes ?: "") }
        var sIsLE by remember { mutableStateOf(existing?.portalType?.contains("قانون") == true || existing?.sectionType?.contains("LAW") == true) }
        var sVisible by remember { mutableStateOf(existing?.isVisible ?: true) }

        AlertDialog(
            onDismissRequest = {
                showNewOfficialSourceDialog = false
                showOfficialSourceDialog = null
            },
            title = { Text(if (existing == null) "إضافة بوابة / مصدر رسمي جديد" else "تعديل بيانات المصدر الرسمي", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(value = sName, onValueChange = { sName = it }, label = { Text("اسم البوابة / المصدر *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sEntity, onValueChange = { sEntity = it }, label = { Text("الجهة / المنصة (مثال: Meta, Apple, الإنتربول) *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sUrl, onValueChange = { sUrl = it }, label = { Text("الرابط المباشر للبوابة *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sCategory, onValueChange = { sCategory = it }, label = { Text("التصنيف الرئيسي") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sRegion, onValueChange = { sRegion = it }, label = { Text("النطاق الجغرافي (مثال: السعودية, عالمي)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sEmail, onValueChange = { sEmail = it }, label = { Text("البريد الإلكتروني للخطابات (إن وجد)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sReqs, onValueChange = { sReqs = it }, label = { Text("المتطلبات والشروط (أمر قضائي، بريد رسمي...)") }, modifier = Modifier.fillMaxWidth())

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("بوابة إنفاذ قانون معتمدة:", fontSize = 12.sp)
                        Switch(checked = sIsLE, onCheckedChange = { sIsLE = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("مفعل ويظهر للمحققين:", fontSize = 12.sp)
                        Switch(checked = sVisible, onCheckedChange = { sVisible = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (sName.isNotBlank() && sUrl.isNotBlank()) {
                            val src = OfficialSourceEntity(
                                id = existing?.id ?: "source_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                                name = sName.trim(),
                                companyOrEntity = sEntity.trim(),
                                sectionType = sSection.trim(),
                                category = sCategory.trim(),
                                portalType = if (sIsLE) "بوابة قانونية مشفرة" else "بوابة دعم رسمي",
                                officialUrl = sUrl.trim(),
                                description = if (sReqs.isNotBlank()) sReqs.trim() else sName.trim(),
                                region = sRegion.trim(),
                                requirements = if (sReqs.isNotBlank()) sReqs.trim() else "متاح للمصرح لهم",
                                verificationStatus = if (sIsLE) "معتمد ورسمي" else "متاح للعامة",
                                lastVerifiedDate = "سبتمبر 2026",
                                isFavorite = existing?.isFavorite ?: false,
                                notes = sEmail.trim(),
                                isVisible = sVisible,
                                sortOrder = existing?.sortOrder ?: (allOfficialSources.size + 1)
                            )
                            viewModel.saveOfficialSource(src)
                            showNewOfficialSourceDialog = false
                            showOfficialSourceDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    enabled = sName.isNotBlank() && sUrl.isNotBlank()
                ) { Text("حفظ المصدر") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showNewOfficialSourceDialog = false
                    showOfficialSourceDialog = null
                }) { Text("إلغاء") }
            }
        )
    }

    // Revenue Dialog (Add / Edit)
    if (showNewRevenueDialog || showRevenueDialog != null) {
        val existing = showRevenueDialog
        var rTitle by remember { mutableStateOf(existing?.title ?: "") }
        var rClient by remember { mutableStateOf(existing?.clientName ?: "") }
        var rCaseNum by remember { mutableStateOf(existing?.caseNumber ?: "") }
        var rTotal by remember { mutableStateOf(existing?.totalAmount?.toString() ?: "") }
        var rPaid by remember { mutableStateOf(existing?.paidAmount?.toString() ?: "") }
        var rType by remember { mutableStateOf(existing?.incomeType ?: "أتعاب فحص جنائي") }
        var rMethod by remember { mutableStateOf(if (existing?.notes?.contains("طريقة الدفع:") == true) existing.notes.substringAfter("طريقة الدفع:").substringBefore("|").trim() else "تحويل بنكي") }
        var rNotes by remember { mutableStateOf(if (existing?.notes?.contains("|") == true) existing.notes.substringAfter("|").trim() else existing?.notes ?: "") }

        AlertDialog(
            onDismissRequest = {
                showNewRevenueDialog = false
                showRevenueDialog = null
            },
            title = { Text(if (existing == null) "تسجيل إيراد / تحصيل جديد" else "تعديل الإيراد", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(value = rTitle, onValueChange = { rTitle = it }, label = { Text("بيان الإيراد / الخدمة *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rClient, onValueChange = { rClient = it }, label = { Text("اسم العميل / الجهة") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rCaseNum, onValueChange = { rCaseNum = it }, label = { Text("رقم القضية (إن وجد)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rTotal, onValueChange = { rTotal = it }, label = { Text("المبلغ الإجمالي المستحق *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rPaid, onValueChange = { rPaid = it }, label = { Text("المبلغ المسدد / المحصل فعلياً *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rType, onValueChange = { rType = it }, label = { Text("نوع الإيراد (أتعاب قضية، استشارة فنية...)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rMethod, onValueChange = { rMethod = it }, label = { Text("طريقة الدفع (تحويل، نقدي...)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rNotes, onValueChange = { rNotes = it }, label = { Text("ملاحظات إضافية") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val total = rTotal.toDoubleOrNull() ?: 0.0
                        val paid = rPaid.toDoubleOrNull() ?: 0.0
                        if (rTitle.isNotBlank() && total > 0) {
                            val notesCombined = if (rMethod.isNotBlank()) "طريقة الدفع: ${rMethod.trim()} | ${rNotes.trim()}" else rNotes.trim()
                            val rev = FinancialRevenueEntity(
                                id = existing?.id ?: "rev_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                                title = rTitle.trim(),
                                caseId = existing?.caseId,
                                caseNumber = if (rCaseNum.isNotBlank()) rCaseNum.trim() else null,
                                clientName = rClient.trim(),
                                totalAmount = total,
                                paidAmount = paid,
                                remainingAmount = (total - paid).coerceAtLeast(0.0),
                                currency = "SAR",
                                incomeType = rType.trim(),
                                date = existing?.date ?: java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH).format(java.util.Date()),
                                timestamp = existing?.timestamp ?: System.currentTimeMillis(),
                                notes = notesCombined,
                                isDeleted = false
                            )
                            viewModel.saveFinancialRevenue(rev)
                            showNewRevenueDialog = false
                            showRevenueDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    enabled = rTitle.isNotBlank() && (rTotal.toDoubleOrNull() ?: 0.0) > 0
                ) { Text("حفظ الإيراد") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showNewRevenueDialog = false
                    showRevenueDialog = null
                }) { Text("إلغاء") }
            }
        )
    }

    // Profit Share Rule Dialog (Add / Edit)
    if (showNewProfitRuleDialog || showProfitRuleDialog != null) {
        val existing = showProfitRuleDialog
        var ruleName by remember { mutableStateOf(existing?.name ?: "") }
        var ruleBeneficiary by remember { mutableStateOf(if (existing?.description?.contains("المستفيد:") == true) existing.description.substringAfter("المستفيد:").substringBefore("|").trim() else "") }
        var ruleType by remember { mutableStateOf(existing?.type ?: "PERCENTAGE") }
        var ruleValue by remember { mutableStateOf(existing?.value?.toString() ?: "0.0") }
        var ruleNotes by remember { mutableStateOf(if (existing?.description?.contains("|") == true) existing.description.substringAfter("|").trim() else existing?.description ?: "") }

        AlertDialog(
            onDismissRequest = {
                showNewProfitRuleDialog = false
                showProfitRuleDialog = null
            },
            title = { Text(if (existing == null) "إضافة بند توزيع أرباح جديد" else "تعديل بند الأرباح", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(value = ruleName, onValueChange = { ruleName = it }, label = { Text("اسم البند / القاعدة *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = ruleBeneficiary, onValueChange = { ruleBeneficiary = it }, label = { Text("المستفيد / الحساب *") }, modifier = Modifier.fillMaxWidth())
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { ruleType = "PERCENTAGE" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (ruleType == "PERCENTAGE") CyberPrimary else MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) { Text("نسبة مئوية (%)") }

                        Button(
                            onClick = { ruleType = "FIXED_AMOUNT" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (ruleType == "FIXED_AMOUNT") CyberPrimary else MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) { Text("مبلغ ثابت (SAR)") }
                    }

                    OutlinedTextField(
                        value = ruleValue,
                        onValueChange = { ruleValue = it },
                        label = { Text(if (ruleType == "PERCENTAGE") "النسبة المئوية (%)" else "المبلغ الثابت (SAR)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(value = ruleNotes, onValueChange = { ruleNotes = it }, label = { Text("ملاحظات") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (ruleName.isNotBlank()) {
                            val descCombined = if (ruleBeneficiary.isNotBlank()) "المستفيد: ${ruleBeneficiary.trim()} | ${ruleNotes.trim()}" else ruleNotes.trim()
                            val rule = ProfitShareRuleEntity(
                                id = existing?.id ?: "rule_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                                name = ruleName.trim(),
                                type = ruleType,
                                value = ruleValue.toDoubleOrNull() ?: 0.0,
                                description = descCombined,
                                isActive = true,
                                sortOrder = existing?.sortOrder ?: (allProfitRules.size + 1)
                            )
                            viewModel.saveProfitRule(rule)
                            showNewProfitRuleDialog = false
                            showProfitRuleDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    enabled = ruleName.isNotBlank()
                ) { Text("حفظ البند") }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showNewProfitRuleDialog = false
                    showProfitRuleDialog = null
                }) { Text("إلغاء") }
            }
        )
    }

    // Backup & Restore Full Dialog
    if (showBackupRestoreDialog) {
        var isRestoreMode by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showBackupRestoreDialog = false },
            title = { Text(if (!isRestoreMode) "النسخ الاحتياطي الشامل (JSON)" else "استعادة المنظومة من نسخة احتياطية", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { isRestoreMode = false },
                            colors = ButtonDefaults.buttonColors(containerColor = if (!isRestoreMode) CyberPrimary else MaterialTheme.colorScheme.surfaceVariant),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) { Text("تصدير النسخة", fontSize = 11.sp) }

                        Button(
                            onClick = { isRestoreMode = true },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isRestoreMode) CyberPrimary else MaterialTheme.colorScheme.surfaceVariant),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) { Text("استيراد واستعادة", fontSize = 11.sp) }
                    }

                    if (!isRestoreMode) {
                        Text("تم توليد ملف النسخة الاحتياطية بنجاح ويشمل كافة محتويات المنظومة وقواعد البيانات:", fontSize = 12.sp, color = TextSecondary)
                        OutlinedTextField(
                            value = backupJsonExported,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(backupJsonExported))
                                    viewModel.showHud("تم نسخ كود النسخة الاحتياطية إلى الحافظة", HudType.SUCCESS)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberSuccess),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("نسخ النسخة الاحتياطية للحافظة")
                            }
                        }
                    } else {
                        Text("قم بلصق محتوى ملف النسخة الاحتياطية (JSON) هنا للاستعادة الآمنة:", fontSize = 12.sp, color = TextSecondary)
                        OutlinedTextField(
                            value = restoreJsonInput,
                            onValueChange = { restoreJsonInput = it },
                            placeholder = { Text("الصق كود الـ JSON هنا...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp)
                        )
                        Button(
                            onClick = {
                                if (restoreJsonInput.isNotBlank()) {
                                    viewModel.restoreSystemBackup(restoreJsonInput, mergeMode = true) { success, msg ->
                                        if (success) {
                                            showBackupRestoreDialog = false
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberDanger),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = restoreJsonInput.isNotBlank()
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تأكيد استعادة المنظومة الآن")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { showBackupRestoreDialog = false }) { Text("إغلاق") }
            }
        )
    }
}

// ==========================================
// TAB 0: SECTIONS & NAVIGATION (Extracted to AdminSectionsTab.kt)
// ==========================================

// ==========================================
// TAB 1: SOURCES & SUPPORT FORMS
// ==========================================

@Composable
private fun AdminSourcesAndFormsTab(
    officialSources: List<OfficialSourceEntity>,
    tools: List<InvestigationToolEntity>,
    forms: List<SupportFormEntity>,
    onEditOfficialSource: (OfficialSourceEntity) -> Unit,
    onDeleteOfficialSource: (String, String) -> Unit,
    onToggleOfficialSourceVisibility: (String, Boolean) -> Unit,
    onAddNewOfficialSource: () -> Unit,
    onEditTool: (InvestigationToolEntity) -> Unit,
    onDeleteTool: (String, String) -> Unit,
    onAddNewTool: () -> Unit,
    onEditForm: (SupportFormEntity) -> Unit,
    onDeleteForm: (String, String) -> Unit,
    onAddNewForm: () -> Unit
) {
    var subTab by remember { mutableIntStateOf(0) }
    var sourceSearchQuery by remember { mutableStateOf("") }

    val filteredSources = remember(officialSources, sourceSearchQuery) {
        if (sourceSearchQuery.isBlank()) officialSources
        else {
            val q = sourceSearchQuery.trim().lowercase()
            officialSources.filter {
                it.name.lowercase().contains(q) ||
                it.companyOrEntity.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.officialUrl.lowercase().contains(q)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Sub-tabs row with scrolling
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = subTab == 0,
                onClick = { subTab = 0 },
                label = { Text("المصادر الرسمية (${officialSources.size})", fontSize = 11.sp, fontWeight = if (subTab == 0) FontWeight.Bold else FontWeight.Normal) },
                leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(14.dp)) }
            )

            FilterChip(
                selected = subTab == 1,
                onClick = { subTab = 1 },
                label = { Text("أدوات التقصي (${tools.size})", fontSize = 11.sp, fontWeight = if (subTab == 1) FontWeight.Bold else FontWeight.Normal) },
                leadingIcon = { Icon(Icons.Default.TravelExplore, contentDescription = null, modifier = Modifier.size(14.dp)) }
            )

            FilterChip(
                selected = subTab == 2,
                onClick = { subTab = 2 },
                label = { Text("نماذج الدعم (${forms.size})", fontSize = 11.sp, fontWeight = if (subTab == 2) FontWeight.Bold else FontWeight.Normal) },
                leadingIcon = { Icon(Icons.Default.ContactSupport, contentDescription = null, modifier = Modifier.size(14.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Button for Add
        Button(
            onClick = {
                when (subTab) {
                    0 -> onAddNewOfficialSource()
                    1 -> onAddNewTool()
                    else -> onAddNewForm()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                when (subTab) {
                    0 -> "إضافة بوابة / مصدر رسمي جديد"
                    1 -> "إضافة أداة تقصي رقمية جديدة"
                    else -> "إضافة نموذج دعم تقني جديد"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (subTab == 0) {
            OutlinedTextField(
                value = sourceSearchQuery,
                onValueChange = { sourceSearchQuery = it },
                placeholder = { Text("بحث في البوابات الرسمية ومخاطبات إنفاذ القانون...", fontSize = 11.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredSources, key = { it.id }) { s ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = if (s.isVisible) CyberCard else CyberCard.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (s.isVisible) CyberBorder else CyberBorder.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = s.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = s.companyOrEntity, accentColor = CyberPrimaryLight)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    if (s.sectionType.contains("LAW") || s.portalType.contains("قانون")) {
                                        CyberBadge(text = "إنفاذ قانون", accentColor = CyberDanger)
                                    }
                                }
                                Text(text = s.officialUrl, fontSize = 11.sp, color = CyberPrimaryLight)
                                if (s.requirements.isNotBlank()) {
                                    Text(text = "المتطلبات: ${s.requirements}", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onToggleOfficialSourceVisibility(s.id, !s.isVisible) }) {
                                    Icon(
                                        imageVector = if (s.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "الظهور",
                                        tint = if (s.isVisible) CyberSuccess else TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(onClick = { onEditOfficialSource(s) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = CyberPrimaryLight, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { onDeleteOfficialSource(s.id, s.name) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else if (subTab == 1) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tools, key = { it.id }) { t ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberCard),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = t.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = t.category, accentColor = CyberInfo)
                                }
                                Text(text = t.url, fontSize = 11.sp, color = CyberPrimaryLight)
                                if (t.description.isNotBlank()) {
                                    Text(text = t.description, fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                            Row {
                                IconButton(onClick = { onEditTool(t) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = CyberPrimaryLight, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { onDeleteTool(t.id, t.name) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(forms, key = { it.id }) { f ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberCard),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = f.formName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = f.company, accentColor = CyberSuccess)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = f.urlType, accentColor = CyberWarning)
                                }
                                Text(text = f.formUrl, fontSize = 11.sp, color = CyberPrimaryLight)
                            }
                            Row {
                                IconButton(onClick = { onEditForm(f) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = CyberPrimaryLight, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { onDeleteForm(f.id, f.formName) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 2: CASES & CLIENTS FULL CONTROL
// ==========================================

@Composable
private fun AdminCasesAndClientsTab(
    cases: List<CaseEntity>,
    clients: List<ClientEntity>,
    onEditCase: (CaseEntity) -> Unit,
    onEditClient: (ClientEntity) -> Unit
) {
    var subTab by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { subTab = 0 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (subTab == 0) CyberPrimary else MaterialTheme.colorScheme.surfaceVariant),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) { Text("إدارة القضايا (${cases.size})", fontSize = 11.sp) }

                Button(
                    onClick = { subTab = 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = if (subTab == 1) CyberPrimary else MaterialTheme.colorScheme.surfaceVariant),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) { Text("إدارة العملاء (${clients.size})", fontSize = 11.sp) }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("بحث في السجلات بالاسم أو الرقم...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (subTab == 0) {
            val filteredCases = cases.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.caseNumber.contains(query, ignoreCase = true) ||
                it.clientName.contains(query, ignoreCase = true)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredCases, key = { it.id }) { c ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberCard),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "${c.caseNumber} - ${c.title}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = c.status, accentColor = CyberPrimary)
                                }
                                Text(
                                    text = "العميل: ${c.clientName} | الهاتف: ${c.clientPhone}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "المالي: ${c.totalAmount} SAR | مدفوع: ${c.paidAmount} SAR | المتبقي: ${c.remainingAmount} SAR",
                                    fontSize = 11.sp,
                                    color = CyberSuccess,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Button(
                                onClick = { onEditCase(c) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تعديل كامل", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        } else {
            val filteredClients = clients.filter {
                it.fullName.contains(query, ignoreCase = true) ||
                it.phoneNumber.contains(query, ignoreCase = true)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredClients, key = { it.id }) { cl ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberCard),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = cl.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = "مخاطر: ${cl.riskLevel}", accentColor = CyberWarning)
                                }
                                Text(text = "الهاتف: ${cl.phoneNumber}", fontSize = 11.sp, color = TextSecondary)
                                if (cl.notes.isNotBlank()) {
                                    Text(text = "ملاحظات: ${cl.notes}", fontSize = 10.sp, color = TextMuted)
                                }
                            }
                            Button(
                                onClick = { onEditClient(cl) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تعديل", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 3: CUSTOM FIELDS MANAGER
// ==========================================

@Composable
private fun AdminCustomFieldsTab(
    fields: List<CustomFieldDefinitionEntity>,
    onEditField: (CustomFieldDefinitionEntity) -> Unit,
    onDeleteField: (String, String) -> Unit,
    onAddNewField: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "الحقول المخصصة للجداول (${fields.size} حقل)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Button(
                onClick = onAddNewField,
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة حقل مخصص", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (fields.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد حقول مخصصة حالياً. اضغط على «إضافة حقل مخصص» لإنشاء أول حقل.", color = TextMuted, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(fields, key = { it.id }) { f ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberCard),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = f.fieldName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = "جدول: ${f.targetEntity}", accentColor = CyberSecondary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = f.fieldType, accentColor = CyberInfo)
                                }
                                Text(
                                    text = "إلزامي: ${if (f.isRequired) "نعم" else "لا"} | إظهار بالقائمة: ${if (f.showInList) "نعم" else "لا"}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                if (f.optionsJson.isNotBlank()) {
                                    Text(text = "الخيارات: ${f.optionsJson}", fontSize = 10.sp, color = TextMuted)
                                }
                            }
                            Row {
                                IconButton(onClick = { onEditField(f) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = CyberPrimaryLight, modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { onDeleteField(f.id, f.fieldName) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 4: FINANCES, EXPENSES & PROFIT SPLIT
// ==========================================

@Composable
private fun AdminFinancesTab(
    expenses: List<SystemExpenseEntity>,
    cases: List<CaseEntity>,
    revenues: List<FinancialRevenueEntity>,
    profitRules: List<ProfitShareRuleEntity>,
    profitTeam: Float,
    profitWork: Float,
    profitReserve: Float,
    onUpdateProfitSplit: (Float, Float, Float) -> Unit,
    onEditExpense: (SystemExpenseEntity) -> Unit,
    onDeleteExpense: (String, String) -> Unit,
    onAddNewExpense: () -> Unit,
    onEditRevenue: (FinancialRevenueEntity) -> Unit,
    onDeleteRevenue: (String, String) -> Unit,
    onAddNewRevenue: () -> Unit,
    onEditProfitRule: (ProfitShareRuleEntity) -> Unit,
    onDeleteProfitRule: (String, String) -> Unit,
    onAddNewProfitRule: () -> Unit
) {
    var teamSlider by remember { mutableFloatStateOf(profitTeam) }
    var workSlider by remember { mutableFloatStateOf(profitWork) }
    var reserveSlider by remember { mutableFloatStateOf(profitReserve) }

    val totalIncome = cases.sumOf { it.paidAmount } + revenues.sumOf { it.paidAmount }
    val totalExpected = cases.sumOf { it.totalAmount } + revenues.sumOf { it.totalAmount }
    val totalExpenses = expenses.sumOf { it.amount }
    val netProfit = (totalIncome - totalExpenses).coerceAtLeast(0.0)

    val teamShare = netProfit * (teamSlider / 100.0)
    val workShare = netProfit * (workSlider / 100.0)
    val reserveShare = netProfit * (reserveSlider / 100.0)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Summary Cards
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberCardElevated),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("الموجز المالي التنفيذي الشامل", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("إجمالي المحصل", fontSize = 11.sp, color = TextSecondary)
                            Text("${String.format("%.2f", totalIncome)} SAR", fontWeight = FontWeight.Bold, color = CyberSuccess, fontSize = 14.sp)
                            Text("المتوقع: ${String.format("%.0f", totalExpected)} SAR", fontSize = 9.sp, color = TextMuted)
                        }
                        Column {
                            Text("إجمالي المصروفات", fontSize = 11.sp, color = TextSecondary)
                            Text("${String.format("%.2f", totalExpenses)} SAR", fontWeight = FontWeight.Bold, color = CyberDanger, fontSize = 14.sp)
                        }
                        Column {
                            Text("صافي الأرباح", fontSize = 11.sp, color = TextSecondary)
                            Text("${String.format("%.2f", netProfit)} SAR", fontWeight = FontWeight.Bold, color = CyberPrimaryLight, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Profit Split Configuration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("قواعد وتوزيع نسب الأرباح", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Button(
                            onClick = { onUpdateProfitSplit(teamSlider, workSlider, reserveSlider) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("اعتماد النسب", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Team Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("نسبة الفريق والمحققين: ${teamSlider.toInt()}%", fontSize = 12.sp)
                        Text("${String.format("%.1f", teamShare)} SAR", fontWeight = FontWeight.Bold, color = CyberSuccess, fontSize = 12.sp)
                    }
                    Slider(
                        value = teamSlider,
                        onValueChange = { teamSlider = it },
                        valueRange = 0f..100f,
                        steps = 99,
                        colors = SliderDefaults.colors(thumbColor = CyberSuccess, activeTrackColor = CyberSuccess)
                    )

                    // Work / Development Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("نسبة العمل والتطوير: ${workSlider.toInt()}%", fontSize = 12.sp)
                        Text("${String.format("%.1f", workShare)} SAR", fontWeight = FontWeight.Bold, color = CyberPrimaryLight, fontSize = 12.sp)
                    }
                    Slider(
                        value = workSlider,
                        onValueChange = { workSlider = it },
                        valueRange = 0f..100f,
                        steps = 99,
                        colors = SliderDefaults.colors(thumbColor = CyberPrimary, activeTrackColor = CyberPrimary)
                    )

                    // Reserve Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("نسبة الاحتياطي والطوارئ: ${reserveSlider.toInt()}%", fontSize = 12.sp)
                        Text("${String.format("%.1f", reserveShare)} SAR", fontWeight = FontWeight.Bold, color = CyberWarning, fontSize = 12.sp)
                    }
                    Slider(
                        value = reserveSlider,
                        onValueChange = { reserveSlider = it },
                        valueRange = 0f..100f,
                        steps = 99,
                        colors = SliderDefaults.colors(thumbColor = CyberWarning, activeTrackColor = CyberWarning)
                    )
                }
            }
        }

        // Custom Profit Rules Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("بنود وقواعد الأرباح الخاصة (${profitRules.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Button(
                    onClick = onAddNewProfitRule,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("بند أرباح جديد", fontSize = 11.sp)
                }
            }
        }

        items(profitRules, key = { it.id }) { rule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = rule.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            CyberBadge(
                                text = if (rule.type == "PERCENTAGE") "${rule.value}%" else "${rule.value} SAR",
                                accentColor = CyberSuccess
                            )
                        }
                        if (rule.description.isNotBlank()) {
                            Text(text = rule.description, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                    Row {
                        IconButton(onClick = { onEditProfitRule(rule) }) {
                            Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = CyberPrimaryLight, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = { onDeleteProfitRule(rule.id, rule.name) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Revenues Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("الإيرادات والتحصيلات المستقلة (${revenues.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Button(
                    onClick = onAddNewRevenue,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSuccess),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إيراد جديد", fontSize = 11.sp)
                }
            }
        }

        items(revenues, key = { it.id }) { rev ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = rev.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            CyberBadge(text = rev.incomeType, accentColor = CyberInfo)
                        }
                        Text(
                            text = "المحصل: ${rev.paidAmount} / ${rev.totalAmount} SAR",
                            fontSize = 11.sp,
                            color = CyberSuccess
                        )
                        if (rev.clientName.isNotBlank()) {
                            Text(text = "الجهة: ${rev.clientName}", fontSize = 10.sp, color = TextSecondary)
                        }
                        if (rev.notes.isNotBlank()) {
                            Text(text = rev.notes, fontSize = 10.sp, color = TextMuted)
                        }
                    }
                    Row {
                        IconButton(onClick = { onEditRevenue(rev) }) {
                            Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = CyberPrimaryLight, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = { onDeleteRevenue(rev.id, rev.title) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Operational Expenses Header & Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("سجل المصروفات التشغيلية (${expenses.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Button(
                    onClick = onAddNewExpense,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberDanger),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تسجيل مصروف", fontSize = 11.sp)
                }
            }
        }

        // Expenses List
        items(expenses, key = { it.id }) { e ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = e.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            CyberBadge(text = e.category, accentColor = CyberInfo)
                        }
                        Text(text = "المبلغ: ${e.amount} SAR | التاريخ: ${e.date}", fontSize = 11.sp, color = CyberDanger, fontWeight = FontWeight.Medium)
                        if (e.notes.isNotBlank()) {
                            Text(text = e.notes, fontSize = 10.sp, color = TextMuted)
                        }
                    }
                    Row {
                        IconButton(onClick = { onEditExpense(e) }) {
                            Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = CyberPrimaryLight, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = { onDeleteExpense(e.id, e.title) }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 5: SYSTEM CATEGORIES
// ==========================================

@Composable
private fun AdminCategoriesTab(
    categories: List<SystemCategoryEntity>,
    onEditCategory: (SystemCategoryEntity) -> Unit,
    onDeleteCategory: (String, String) -> Unit,
    onAddNewCategory: () -> Unit
) {
    var selectedScopeFilter by remember { mutableStateOf("ALL") }
    val filteredCategories = remember(categories, selectedScopeFilter) {
        if (selectedScopeFilter == "ALL") categories
        else categories.filter { it.scope == selectedScopeFilter }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "تصنيفات المنظومة (${filteredCategories.size} من ${categories.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Button(
                onClick = onAddNewCategory,
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة تصنيف جديد", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter chips for scopes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "ALL" to "الكل",
                "CASE_FILES" to "ملفات القضية",
                "CASES" to "القضايا",
                "TOOLS" to "الأدوات",
                "FORMS" to "النماذج",
                "EXPENSES" to "المصاريف"
            ).forEach { (code, label) ->
                FilterChip(
                    selected = selectedScopeFilter == code,
                    onClick = { selectedScopeFilter = code },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberPrimary.copy(alpha = 0.25f),
                        selectedLabelColor = CyberPrimaryLight
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredCategories, key = { it.id }) { c ->
                val scopeArabic = when (c.scope) {
                    "CASE_FILES" -> "ملفات القضية"
                    "CASES" -> "قضايا"
                    "TOOLS" -> "أدوات"
                    "FORMS" -> "نماذج"
                    "EXPENSES" -> "مصاريف"
                    else -> c.scope
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberCard),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(
                                        try {
                                            if (c.color.startsWith("#")) Color(android.graphics.Color.parseColor(c.color)) else CyberPrimary
                                        } catch (_: Exception) {
                                            CyberPrimary
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = c.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = scopeArabic, accentColor = if (c.scope == "CASE_FILES") CyberPrimary else CyberSecondary)
                                }
                                Text(text = "رمز اللون: ${c.color.ifBlank { "افتراضي" }}", fontSize = 11.sp, color = TextMuted)
                            }
                        }

                        Row {
                            IconButton(onClick = { onEditCategory(c) }) {
                                Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = CyberPrimaryLight, modifier = Modifier.size(18.dp))
                            }
                            IconButton(onClick = { onDeleteCategory(c.id, c.name) }) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 6: BACKUP & AUDIT LOGS
// ==========================================

@Composable
private fun AdminBackupAndAuditTab(
    auditLogs: List<AdminAuditLogEntity>,
    onOpenBackupRestore: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberCardElevated),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("مركز النسخ الاحتياطي والاستعادة الآمنة", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("تصدير أو استيراد ملف JSON شامل لقواعد بيانات المنظومة", fontSize = 11.sp, color = TextSecondary)
                }
                Button(
                    onClick = onOpenBackupRestore,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("فتح المركز", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "سجل تعديلات وإجراءات المدير (${auditLogs.size} عملية)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (auditLogs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد عمليات مسجلة حتى الآن.", color = TextMuted, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(auditLogs, key = { it.id }) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberCard),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = log.action, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CyberPrimaryLight)
                                Text(text = log.dateFormatted, fontSize = 11.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "الهدف: ${log.target}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            if (log.newValue.isNotBlank()) {
                                Text(text = "القيمة الجديدة: ${log.newValue}", fontSize = 11.sp, color = CyberSuccess)
                            }
                            if (log.oldValue.isNotBlank()) {
                                Text(text = "القيمة السابقة: ${log.oldValue}", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getIconByName(name: String): ImageVector {
    return when (name.lowercase()) {
        "home" -> Icons.Default.Home
        "folder" -> Icons.Default.Folder
        "clouddownload" -> Icons.Default.CloudDownload
        "attachfile" -> Icons.Default.AttachFile
        "assignment" -> Icons.Default.Assignment
        "contactsupport" -> Icons.Default.ContactSupport
        "travelexplore" -> Icons.Default.TravelExplore
        "autoawesome" -> Icons.Default.AutoAwesome
        "people" -> Icons.Default.People
        "menubook" -> Icons.AutoMirrored.Filled.MenuBook
        "assessment" -> Icons.Default.Assessment
        "search" -> Icons.Default.Search
        "delete" -> Icons.Default.Delete
        "settings" -> Icons.Default.Settings
        "security" -> Icons.Default.Security
        else -> Icons.Default.Folder
    }
}
