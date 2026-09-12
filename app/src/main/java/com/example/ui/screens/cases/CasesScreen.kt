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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
val THREAT_TYPES = listOf("طلب خدمة", "استشارة فنية", "متابعة ملف", "دعم تقني", "توثيق بيانات", "مراجعة حسابات", "تنظيم ملفات", "أخرى")
val PRIORITIES = listOf("حرجة", "عالية", "متوسطة", "منخفضة")
val CASE_SOURCES = listOf("واتساب", "اتصال هاتفي", "منصة إكس (Twitter)", "موقع شخصي", "توصية عميل", "أخرى")
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
    var formNotes by remember { mutableStateOf("") }
    var formTotalAmount by remember { mutableStateOf("0") }
    var formCurrency by remember { mutableStateOf("SAR") }
    var formCaseSource by remember { mutableStateOf("واتساب") }

    fun openNewCase() {
        caseToEdit = null
        formTitle = ""
        formClientName = ""
        formClientPhone = "+966"
        formThreatType = "طلب خدمة"
        formPriority = "عالية"
        formStatus = "جديدة"
        formInvestigator = "جعفر بدران (المسؤول الرئيسي)"
        formNotes = ""
        formTotalAmount = "0"
        formCurrency = "SAR"
        formCaseSource = "واتساب"
        showEditorSheet = true
    }

    fun openEditCase(item: CaseEntity) {
        caseToEdit = item
        formTitle = item.title
        formClientName = item.clientName
        formClientPhone = item.clientPhone
        formThreatType = item.threatType
        formPriority = item.priority
        formStatus = item.status
        formInvestigator = item.assignedInvestigator
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

                    CyberBadge(
                        text = "${casesList.size} ملفات",
                        accentColor = CyberPrimary
                    )
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
                    Text(
                        text = currentCase.caseNumber,
                        color = CyberPrimaryLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PriorityBadge(currentCase.priority)
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusBadge(currentCase.status)
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
                                    Text("${currentCase.totalAmount} ${currentCase.currency}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                                Text("${currentCase.paidAmount} ${currentCase.currency}", color = CyberSuccess, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("المتبقي", color = TextSecondary, fontSize = 11.sp)
                                Text(
                                    "${currentCase.remainingAmount} ${currentCase.currency}",
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
                    currency = currentCase.currency
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
                            Text(currentCase.clientName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(currentCase.clientPhone, color = CyberPrimaryLight, fontSize = 12.sp)
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
                Text(
                    text = currentCase.notes.ifEmpty { "لا توجد ملاحظات إضافية مسجلة." },
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Attachments Count
                Text("المرفقات والملفات المرتبطة (${caseEvidence.size}):", color = CyberSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                if (caseEvidence.isEmpty()) {
                    Text("لم يتم ربط مرفقات أو ملفات بهذه القضية بعد.", color = TextMuted, fontSize = 12.sp)
                } else {
                    caseEvidence.forEach { evi ->
                        Text("• ${evi.evidenceName} (SHA256: ${evi.sha256Hash.take(12)}...)", color = TextPrimary, fontSize = 12.sp)
                    }
                }

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
                                            Text(text = item.notes, color = TextSecondary, fontSize = 11.sp)
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

    // Add / Edit Case Sheet
    if (showEditorSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditorSheet = false },
            containerColor = CyberSurface,
            scrimColor = Color.Black.copy(alpha = 0.65f),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (caseToEdit == null) "تسجيل قضية جديدة" else "تعديل بيانات القضية",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = formTitle,
                    onValueChange = { formTitle = it },
                    label = { Text("عنوان القضية / موضوع العمل") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = formClientName,
                    onValueChange = { formClientName = it },
                    label = { Text("اسم العميل / صاحب الطلب") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = formClientPhone,
                    onValueChange = { formClientPhone = it },
                    label = { Text("رقم هاتف العميل للاتصال والواتساب") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Financial Fields
                Text("الأتعاب والسعر المتفق عليه:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = formTotalAmount,
                        onValueChange = { formTotalAmount = it },
                        label = { Text("السعر الإجمالي") },
                        modifier = Modifier.weight(1.5f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberSuccess,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = formCurrency,
                        onValueChange = { formCurrency = it },
                        label = { Text("العملة") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("مصدر القضية / الوصول للعميل:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CASE_SOURCES.forEach { src ->
                        FilterChip(
                            selected = formCaseSource == src,
                            onClick = { formCaseSource = src },
                            label = { Text(src, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("تصنيف القضية / نوع العمل:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    THREAT_TYPES.forEach { threat ->
                        FilterChip(
                            selected = formThreatType == threat,
                            onClick = { formThreatType = threat },
                            label = { Text(threat, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("مستوى الأولوية:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PRIORITIES.forEach { p ->
                        FilterChip(
                            selected = formPriority == p,
                            onClick = { formPriority = p },
                            label = { Text(p, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = formNotes,
                    onValueChange = { formNotes = it },
                    label = { Text("ملاحظات وتفاصيل العمل والمتابعة") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (formTitle.isNotBlank() && formClientName.isNotBlank()) {
                            val isNew = caseToEdit == null
                            val id = caseToEdit?.id ?: "case_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
                            val num = caseToEdit?.caseNumber ?: "JB-2026-${(1000..9999).random()}"
                            val parsedPrice = formTotalAmount.toDoubleOrNull() ?: 0.0
                            val existingPaid = caseToEdit?.paidAmount ?: 0.0
                            val calculatedRemaining = maxOf(0.0, parsedPrice - existingPaid)
                            val calcPaymentStatus = when {
                                parsedPrice == 0.0 -> "غير مدفوع"
                                existingPaid >= parsedPrice -> "مدفوع بالكامل"
                                existingPaid > 0.0 -> "مدفوع جزئيًا"
                                else -> "غير مدفوع"
                            }

                            val entity = CaseEntity(
                                id = id,
                                caseNumber = num,
                                title = formTitle,
                                clientName = formClientName,
                                clientPhone = formClientPhone,
                                threatType = formThreatType,
                                priority = formPriority,
                                status = formStatus,
                                assignedInvestigator = formInvestigator,
                                timelineEventsJson = caseToEdit?.timelineEventsJson ?: "[{\"time\":\"${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())}\",\"event\":\"فتح ملف القضية وتسجيل الطلب\"}]",
                                notes = formNotes,
                                createdDate = caseToEdit?.createdDate ?: System.currentTimeMillis(),
                                totalAmount = parsedPrice,
                                paidAmount = existingPaid,
                                remainingAmount = calculatedRemaining,
                                currency = formCurrency.ifBlank { "SAR" },
                                paymentStatus = calcPaymentStatus,
                                source = formCaseSource
                            )
                            viewModel.saveCase(entity, isNew)
                            showEditorSheet = false
                        } else {
                            viewModel.showHud("يرجى ملء عنوان القضية واسم العميل", HudType.WARNING)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (caseToEdit == null) "تسجيل القضية رسمياً" else "حفظ تعديلات القضية",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CaseCard(
    item: CaseEntity,
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
                Text(
                    text = "العميل: ${item.clientName}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
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
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "الأتعاب: ${item.totalAmount} ${item.currency}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        if (item.paidAmount > 0) {
                            Text(
                                text = "مدفوع: ${item.paidAmount}",
                                color = CyberSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (item.remainingAmount > 0) {
                            Text(
                                text = "متبقي: ${item.remainingAmount}",
                                color = CyberWarning,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
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
