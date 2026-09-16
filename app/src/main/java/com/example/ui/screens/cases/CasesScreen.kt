package com.example.ui.screens.cases

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import com.example.ui.screens.cases.creation.CreateOrEditCaseDialog
import com.example.ui.screens.cases.creation.DraftCustomLink
import com.example.ui.screens.cases.creation.DraftIdentifier
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.CaseEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.ForensicCrypto
import com.example.ui.components.HudType
import com.example.ui.components.InAppConfirmationSheet
import com.example.ui.components.PrivacyMaskText
import com.example.ui.components.PrivacyMaskToggle
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberInfo
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberPrimaryLight
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSuccess
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ForensicViewModel
import java.util.UUID

val CASE_STATUSES = listOf("الكل", "جديدة", "قيد المراجعة", "قيد المعالجة", "بانتظار المعلومات", "قيد المتابعة", "مكتملة", "مغلقة", "مؤرشفة")
val THREAT_TYPES = listOf(
    "طلب خدمة واستعادة وصول",
    "استشارة جنائية رقمية",
    "توثيق أدلة وبصمة رقمية",
    "مخاطبة منصة / جهة إنفاذ القانون",
    "بلاغ انتهاك وابتزاز إلكتروني",
    "مكافحة احتيال وانتحال شخصية",
    "فحص وتحليل برمجيات خبيثة",
    "تتبع عناوين IP ونطاقات DNS",
    "استرداد وتأمين حسابات منصات",
    "دعم تقني وتأميني فوري",
    "مراجعة تقارير وتدقيق أمني",
    "أخرى"
)
val PRIORITIES = listOf("حرجة", "عالية", "متوسطة", "منخفضة")
val CASE_SOURCES = listOf(
    "واتساب (WhatsApp)",
    "اتصال هاتفي مباشر",
    "بوابة ميتا الرسمية (Meta LEA / Support)",
    "بوابة جوجل (Google / YouTube Portal)",
    "بوابة مايكروسوفت (Microsoft Compliance)",
    "منصة إكس (X / Twitter Portal)",
    "تيليجرام (Telegram Support)",
    "تيك توك (TikTok Law Enforcement)",
    "سناب شات (Snapchat Law Enforcement)",
    "لينكد إن (LinkedIn Support)",
    "ديسكورد (Discord Trust & Safety)",
    "ريديت (Reddit Legal)",
    "جيت هاب (GitHub Support)",
    "آبل (Apple Legal / Support)",
    "سامسونج (Samsung Security)",
    "شاومي / هواوي / أوبو / فيفو",
    "توصية عميل / إحالة رسمية",
    "موقع شخصي وبوابة استقبال",
    "أخرى"
)
val CURRENCIES = listOf("SAR", "USD", "EUR", "AED", "KWD")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasesScreen(
    viewModel: ForensicViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val casesList by viewModel.filteredCases.collectAsStateWithLifecycle()
    val allEvidence by viewModel.rawEvidence.collectAsStateWithLifecycle()
    val searchQuery by viewModel.caseSearchQuery.collectAsStateWithLifecycle()
    val activeStatus by viewModel.caseStatusFilter.collectAsStateWithLifecycle()
    val isPrivacyMasked by viewModel.isPrivacyMasked.collectAsStateWithLifecycle()

    var activeCaseDetail by remember { mutableStateOf<CaseEntity?>(null) }
    var caseToEdit by remember { mutableStateOf<CaseEntity?>(null) }
    var caseToDelete by remember { mutableStateOf<CaseEntity?>(null) }
    var showEditorSheet by remember { mutableStateOf(false) }

    // Payment & Price Dialogs State
    var caseForPayment by remember { mutableStateOf<CaseEntity?>(null) }
    var caseForPriceUpdate by remember { mutableStateOf<CaseEntity?>(null) }

    // Case Form fields
    var formTitle by remember { mutableStateOf("") }
    var formClientName by remember { mutableStateOf("") }
    var formClientPhone by remember { mutableStateOf("") }
    var formThreatType by remember { mutableStateOf("طلب خدمة") }
    var formPriority by remember { mutableStateOf("عالية") }
    var formStatus by remember { mutableStateOf("جديدة") }
    var formInvestigator by remember { mutableStateOf("جعفر بدران (المسؤول الرئيسي)") }
    var formExternalCaseId by remember { mutableStateOf("") }
    var formSupportTicketId by remember { mutableStateOf("") }
    var formTargetIdentifier by remember { mutableStateOf("") }
    var formInternalCaseNumber by remember { mutableStateOf("") }
    var formNotes by remember { mutableStateOf("") }
    var formTotalAmount by remember { mutableStateOf("0") }
    var formCurrency by remember { mutableStateOf("SAR") }
    var formCaseSource by remember { mutableStateOf("واتساب") }

    fun openNewCase() {
        caseToEdit = null
        formInternalCaseNumber = viewModel.generateInternalCaseNumber()
        formTitle = ""
        formClientName = ""
        formClientPhone = ""
        formThreatType = "طلب خدمة"
        formPriority = "عالية"
        formStatus = "جديدة"
        formInvestigator = "جعفر بدران (المسؤول الرئيسي)"
        formExternalCaseId = ""
        formSupportTicketId = ""
        formTargetIdentifier = ""
        formNotes = ""
        formTotalAmount = "0"
        formCurrency = "SAR"
        formCaseSource = "واتساب"
        showEditorSheet = true
    }

    fun openEditCase(item: CaseEntity) {
        caseToEdit = item
        formInternalCaseNumber = item.caseNumber
        formTitle = item.title
        formClientName = item.clientName
        formClientPhone = item.clientPhone
        formThreatType = item.threatType
        formPriority = item.priority
        formStatus = item.status
        formInvestigator = item.assignedInvestigator
        formExternalCaseId = item.externalPlatformCaseId ?: ""
        formSupportTicketId = item.supportTicketId ?: ""
        formTargetIdentifier = item.targetIdentifier ?: ""
        formNotes = item.notes
        formTotalAmount = item.totalAmount.toInt().toString()
        formCurrency = item.currency
        formCaseSource = item.source
        showEditorSheet = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openNewCase() },
                containerColor = CyberPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_case_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "فتح قضية")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "إدارة القضايا والملفات",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "متابعة مسار القضايا، المالية والدفعات، وإعداد التقارير",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PrivacyMaskToggle(
                            isMasked = isPrivacyMasked,
                            onToggle = { viewModel.togglePrivacyMasking() }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        CyberBadge(
                            text = "${casesList.size} ملفات",
                            accentColor = CyberPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.caseSearchQuery.value = it },
                    placeholder = { Text("بحث برقم القضية، اسم العميل، أو موضوع العمل...", color = TextSecondary, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.caseSearchQuery.value = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("case_search_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Status Filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CASE_STATUSES.forEach { status ->
                        val isSelected = activeStatus == status
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.caseStatusFilter.value = status },
                            label = { Text(status, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Cases List
            if (casesList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("لا توجد قضايا مسجلة مطابقة للبحث", color = TextSecondary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { openNewCase() }) {
                            Text("تسجيل قضية جديدة الآن", color = CyberPrimary)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(casesList, key = { it.id }) { item ->
                        CaseCard(
                            item = item,
                            isMasked = isPrivacyMasked,
                            onClick = { activeCaseDetail = item },
                            onEdit = { openEditCase(item) },
                            onDelete = { caseToDelete = item },
                            onQuickPayment = { caseForPayment = item },
                            onQuickDuplicate = { viewModel.duplicateCase(item.id) },
                            onQuickWhatsApp = {
                                val msg = "السلام عليكم ${item.clientName}، معك جعفر بدران بخصوص ملف القضية رقم (${item.caseNumber}). نؤكد لك أن الحالة قيد المتابعة والمعالجة بسرية تامة ومرفقاتكم محفوظة بأمان."
                                ForensicCrypto.openWhatsApp(context, item.clientPhone, msg)
                            },
                            onExportReport = {
                                val caseEvidence = allEvidence.filter { it.caseId == item.id }
                                val report = viewModel.generateInvestigationReport(item, caseEvidence)
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, report)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "مشاركة تقرير القضية"))
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Payment Dialog
    if (caseForPayment != null) {
        AddPaymentDialog(
            caseEntity = caseForPayment!!,
            isMasked = isPrivacyMasked,
            onDismiss = { caseForPayment = null },
            onConfirmPayment = { amount, method, date, notes, receipt ->
                viewModel.addCasePayment(
                    caseId = caseForPayment!!.id,
                    amount = amount,
                    paymentMethod = method,
                    paymentDate = date,
                    notes = notes,
                    receiptNumber = receipt
                )
            }
        )
    }

    // Price Update Dialog
    if (caseForPriceUpdate != null) {
        UpdateCasePriceDialog(
            caseEntity = caseForPriceUpdate!!,
            isMasked = isPrivacyMasked,
            onDismiss = { caseForPriceUpdate = null },
            onConfirmUpdate = { newPrice, notes ->
                viewModel.updateCasePrice(
                    caseId = caseForPriceUpdate!!.id,
                    newPrice = newPrice,
                    notes = notes
                )
            }
        )
    }

    // Case Details Viewer BottomSheet
    if (activeCaseDetail != null) {
        // Find latest updated instance of this case
        val currentCase = casesList.find { it.id == activeCaseDetail!!.id } ?: activeCaseDetail!!
        val caseEvidence = allEvidence.filter { it.caseId == currentCase.id }
        val linkedItems by viewModel.getLinkedItemsForCase(currentCase.id).collectAsStateWithLifecycle(initialValue = emptyList())
        val casePayments by viewModel.getPaymentsForCase(currentCase.id).collectAsStateWithLifecycle(initialValue = emptyList())
        val caseAuditLogs by viewModel.getCaseAuditLogs(currentCase.id).collectAsStateWithLifecycle(initialValue = emptyList())

        ModalBottomSheet(
            onDismissRequest = { activeCaseDetail = null },
            containerColor = CyberSurface,
            scrimColor = Color.Black.copy(alpha = 0.7f),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("case_details_sheet")
            ) {
                // Top Row: Number and Badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = currentCase.caseNumber,
                            color = CyberPrimaryLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "المرجع الداخلي المعتمد للنظام",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PriorityBadge(currentCase.priority)
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusBadge(currentCase.status)
                    }
                }

                // External Platform Reference IDs (if provided)
                if (!currentCase.externalPlatformCaseId.isNullOrBlank() || !currentCase.supportTicketId.isNullOrBlank() || !currentCase.targetIdentifier.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberCardElevated),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            currentCase.externalPlatformCaseId.takeIf { it.isNotBlank() }?.let { extId ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Receipt, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("معرف المنصة الخارجية: ", color = TextSecondary, fontSize = 11.sp)
                                    PrivacyMaskText(extId, isMasked = isPrivacyMasked, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            currentCase.supportTicketId.takeIf { it.isNotBlank() }?.let { ticket ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = CyberInfo, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("رقم تذكرة الدعم: ", color = TextSecondary, fontSize = 11.sp)
                                    PrivacyMaskText(ticket, isMasked = isPrivacyMasked, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            currentCase.targetIdentifier.takeIf { it.isNotBlank() }?.let { target ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = CyberWarning, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("المعرف / الرابط المستهدف: ", color = TextSecondary, fontSize = 11.sp)
                                    PrivacyMaskText(target, isMasked = isPrivacyMasked, color = CyberPrimaryLight, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                            currentCase.internalCaseEmail.takeIf { it.isNotBlank() }?.let { email ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Default.Message, contentDescription = null, tint = CyberPrimaryLight, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("البريد الداخلي للقضية: ", color = TextSecondary, fontSize = 11.sp)
                                        PrivacyMaskText(email, isMasked = isPrivacyMasked, color = CyberPrimaryLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                            clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("Internal Email", email))
                                            viewModel.showHud("تم نسخ البريد الداخلي للقضية", HudType.INFO)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = CyberPrimaryLight, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Custom Identifiers and Links Preview if available
                val loadedIdentifiers = remember(currentCase.customIdentifiersJson) {
                    DraftIdentifier.listFromJsonString(currentCase.customIdentifiersJson)
                }
                val loadedLinks = remember(currentCase.customLinksJson) {
                    DraftCustomLink.listFromJsonString(currentCase.customLinksJson)
                }

                if (loadedIdentifiers.isNotEmpty() || loadedLinks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberCardElevated),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (loadedIdentifiers.isNotEmpty()) {
                                Text("المعرفات المسجلة:", color = CyberSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                loadedIdentifiers.forEach { idItem ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                            Text("${idItem.type}: ", color = TextSecondary, fontSize = 11.sp)
                                            PrivacyMaskText(idItem.value, isMasked = isPrivacyMasked, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        }
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                                clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("Identifier", idItem.value))
                                                viewModel.showHud("تم نسخ المعرف", HudType.INFO)
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = CyberPrimaryLight, modifier = Modifier.size(13.dp))
                                        }
                                    }
                                }
                            }

                            if (loadedLinks.isNotEmpty()) {
                                if (loadedIdentifiers.isNotEmpty()) HorizontalDivider(color = CyberBorder.copy(alpha = 0.3f), thickness = 0.5.dp)
                                Text("الروابط الموثقة:", color = CyberPrimaryLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                loadedLinks.forEach { linkItem ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            PrivacyMaskText(
                                                text = if (linkItem.groupName.isNotBlank()) "[${linkItem.groupName}] ${linkItem.title.ifBlank { linkItem.url }}" else linkItem.title.ifBlank { linkItem.url },
                                                isMasked = isPrivacyMasked,
                                                color = TextPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            PrivacyMaskText(
                                                text = linkItem.url,
                                                isMasked = isPrivacyMasked,
                                                color = CyberPrimaryLight,
                                                fontSize = 10.sp
                                            )
                                        }
                                        Row {
                                            IconButton(
                                                onClick = {
                                                    try {
                                                        context.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(linkItem.url)))
                                                    } catch (e: Exception) {
                                                        viewModel.showHud("تعذر فتح الرابط", HudType.WARNING)
                                                    }
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.OpenInBrowser, contentDescription = "فتح", tint = CyberSecondary, modifier = Modifier.size(14.dp))
                                            }
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                                    clipboard?.setPrimaryClip(android.content.ClipData.newPlainText("Link", linkItem.url))
                                                    viewModel.showHud("تم نسخ الرابط", HudType.INFO)
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = CyberPrimaryLight, modifier = Modifier.size(14.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentCase.title,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Status & Lifecycle Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCardElevated)
                        .padding(10.dp)
                ) {
                    Text("تحديث حالة القضية سريعاً:", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("جديدة", "قيد المراجعة", "قيد المعالجة", "بانتظار المعلومات", "قيد المتابعة", "مكتملة", "مغلقة").forEach { st ->
                            FilterChip(
                                selected = currentCase.status == st,
                                onClick = { viewModel.changeCaseStatus(currentCase.id, st) },
                                label = { Text(st, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lifecycle Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.duplicateCase(currentCase.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("استنساخ", fontSize = 11.sp)
                        }

                        if (currentCase.status != "مغلقة") {
                            OutlinedButton(
                                onClick = { viewModel.closeCase(currentCase.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = CyberWarning, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إغلاق", fontSize = 11.sp, color = CyberWarning)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { viewModel.reopenCase(currentCase.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Default.LockReset, contentDescription = null, tint = CyberSuccess, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إعادة فتح", fontSize = 11.sp, color = CyberSuccess)
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.archiveCase(currentCase.id) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Archive, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("أرشفة", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // FINANCIAL LEDGER CARD
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CyberCardElevated
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AttachMoney, contentDescription = null, tint = CyberSuccess, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("الملف المالي والأتعاب", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            PaymentStatusBadge(currentCase.paymentStatus)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("السعر المتفق عليه", color = TextSecondary, fontSize = 11.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    PrivacyMaskText("${currentCase.totalAmount} ${currentCase.currency}", isMasked = isPrivacyMasked, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    IconButton(
                                        onClick = { caseForPriceUpdate = currentCase },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "تعديل السعر", tint = CyberPrimaryLight, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            Column {
                                Text("المبلغ المدفوع", color = TextSecondary, fontSize = 11.sp)
                                PrivacyMaskText("${currentCase.paidAmount} ${currentCase.currency}", isMasked = isPrivacyMasked, color = CyberSuccess, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("المتبقي", color = TextSecondary, fontSize = 11.sp)
                                PrivacyMaskText(
                                    text = "${currentCase.remainingAmount} ${currentCase.currency}",
                                    isMasked = isPrivacyMasked,
                                    color = if (currentCase.remainingAmount > 0) CyberWarning else CyberSuccess,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { caseForPayment = currentCase },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSuccess),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تسجيل دفعة جديدة لهذا الملف", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Recorded Payments Section
                CasePaymentsSection(
                    payments = casePayments,
                    onAddPaymentClick = { caseForPayment = currentCase },
                    currency = currentCase.currency,
                    isMasked = isPrivacyMasked
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Client contact card
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CyberCardElevated
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("العميل / صاحب الطلب:", color = TextSecondary, fontSize = 11.sp)
                            PrivacyMaskText(
                                text = currentCase.clientName.ifBlank { "عميل غير مسمى" },
                                isMasked = isPrivacyMasked,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            PrivacyMaskText(
                                text = currentCase.clientPhone.ifBlank { "—" },
                                isMasked = isPrivacyMasked,
                                color = CyberPrimaryLight,
                                fontSize = 12.sp
                            )
                            if (currentCase.source.isNotBlank()) {
                                Text("المصدر: ${currentCase.source}", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                        Row {
                            IconButton(onClick = { ForensicCrypto.openDialer(context, currentCase.clientPhone) }) {
                                Icon(Icons.Default.Call, contentDescription = "اتصال", tint = CyberSuccess)
                            }
                            IconButton(onClick = {
                                val msg = "مرحباً ${currentCase.clientName}، بخصوص ملف القضية (${currentCase.caseNumber})."
                                ForensicCrypto.openWhatsApp(context, currentCase.clientPhone, msg)
                            }) {
                                Icon(Icons.Default.Message, contentDescription = "واتساب", tint = CyberInfo)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("ملاحظات المتابعة والعمل:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                PrivacyMaskText(
                    text = currentCase.notes.ifEmpty { "لا توجد ملاحظات إضافية مسجلة." },
                    isMasked = isPrivacyMasked,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Complete Case Evidence & Offline File System
                CaseFilesSection(
                    caseId = currentCase.id,
                    caseNumber = currentCase.caseNumber,
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Linked Support Forms and Tools
                Text("نماذج الدعم ومصادر العمل المرتبطة (${linkedItems.size}):", color = CyberPrimaryLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                if (linkedItems.isEmpty()) {
                    Text("لم يتم ربط نماذج دعم أو مصادر عمل بهذه القضية بعد.", color = TextMuted, fontSize = 12.sp)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        linkedItems.forEach { item ->
                            val isForm = item.itemType == "SUPPORT_FORM"
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberCardElevated)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (isForm) CyberPrimary.copy(alpha = 0.2f) else CyberSecondary.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (isForm) "نموذج دعم" else "مصدر / أداة عمل",
                                                    fontSize = 10.sp,
                                                    color = if (isForm) CyberPrimaryLight else CyberSecondary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(
                                                text = item.itemTitle,
                                                color = TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (item.notes.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            PrivacyMaskText(
                                                text = item.notes,
                                                isMasked = isPrivacyMasked,
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                    Row {
                                        IconButton(
                                            onClick = { viewModel.openUrl(context, item.itemUrl) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.OpenInBrowser,
                                                contentDescription = "فتح الرابط",
                                                tint = CyberPrimaryLight,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { viewModel.unlinkItemFromCase(item.id, currentCase.id, item.itemTitle) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "إلغاء الربط",
                                                tint = CyberDanger,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Audit Logs Section
                CaseAuditLogsSection(auditLogs = caseAuditLogs)

                Spacer(modifier = Modifier.height(24.dp))

                // Export Full Report Button
                Button(
                    onClick = {
                        val report = viewModel.generateInvestigationReport(currentCase, caseEvidence, linkedItems)
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, report)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "مشاركة تقرير القضية المعتمد"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تصدير ومشاركة تقرير القضية المعتمد", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Deletion In-App BottomSheet
    if (caseToDelete != null) {
        val target = caseToDelete!!
        InAppConfirmationSheet(
            title = "نقل القضية لسلة الحذف",
            description = "هل تريد نقل القضية رقم ${target.caseNumber} لسلة المحذوفات؟ سيتم حفظ نسخة احتياطية ويمكن التراجع الفوري.",
            confirmLabel = "حذف القضية",
            onConfirm = {
                viewModel.deleteCase(target)
                caseToDelete = null
            },
            onDismiss = { caseToDelete = null }
        )
    }

    // Add / Edit Case Dialog (Upgraded flexible dialog without mandatory fields, with full image support, custom links, groups, identifiers, and internal email)
    if (showEditorSheet) {
        CreateOrEditCaseDialog(
            existingCase = caseToEdit,
            viewModel = viewModel,
            onDismiss = {
                showEditorSheet = false
                caseToEdit = null
            },
            onCaseSaved = { savedCase ->
                showEditorSheet = false
                caseToEdit = null
                activeCaseDetail = savedCase
            }
        )
    }
}

@Composable
fun CaseCard(
    item: CaseEntity,
    isMasked: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onQuickPayment: () -> Unit,
    onQuickDuplicate: () -> Unit,
    onQuickWhatsApp: () -> Unit,
    onExportReport: () -> Unit
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("case_card_${item.id}"),
        backgroundColor = CyberCardElevated,
        onClick = onClick
    ) {
        Column {
            // Header Row: Case number, Category, Priority, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.caseNumber,
                        color = CyberPrimaryLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CyberBadge(text = item.threatType, accentColor = CyberSecondary)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PriorityBadge(priority = item.priority)
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(status = item.status)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = item.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Client & Assigned to
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "العميل: ",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    PrivacyMaskText(
                        text = item.clientName.ifBlank { "عميل غير مسمى" },
                        isMasked = isMasked,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = item.assignedInvestigator,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Financial Summary Strip on Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberBg.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "الأتعاب: ",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            PrivacyMaskText(
                                text = "${item.totalAmount} ${item.currency}",
                                isMasked = isMasked,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                minBullets = 5
                            )
                        }
                        if (item.paidAmount > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "مدفوع: ",
                                    color = CyberSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                PrivacyMaskText(
                                    text = "${item.paidAmount}",
                                    isMasked = isMasked,
                                    color = CyberSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    minBullets = 4
                                )
                            }
                        }
                        if (item.remainingAmount > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "متبقي: ",
                                    color = CyberWarning,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                PrivacyMaskText(
                                    text = "${item.remainingAmount}",
                                    isMasked = isMasked,
                                    color = CyberWarning,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    minBullets = 4
                                )
                            }
                        }
                    }

                    PaymentStatusBadge(item.paymentStatus)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Add Payment Quick Action
                    IconButton(onClick = onQuickPayment, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.AttachMoney, contentDescription = "إضافة دفعة", tint = CyberSuccess, modifier = Modifier.size(17.dp))
                    }
                    // Duplicate Quick Action
                    IconButton(onClick = onQuickDuplicate, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "استنساخ القضية", tint = CyberInfo, modifier = Modifier.size(15.dp))
                    }
                    // WhatsApp Quick Action
                    IconButton(onClick = onQuickWhatsApp, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Message, contentDescription = "واتساب", tint = CyberSuccess, modifier = Modifier.size(15.dp))
                    }
                    // Export Quick Action
                    IconButton(onClick = onExportReport, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "مشاركة التقرير", tint = CyberPrimary, modifier = Modifier.size(15.dp))
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Edit
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = TextSecondary, modifier = Modifier.size(15.dp))
                    }
                    // Delete
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger.copy(alpha = 0.7f), modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}
