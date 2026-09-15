package com.example.ui.screens.reports

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.CaseEntity
import com.example.data.local.entities.CasePaymentEntity
import com.example.data.local.entities.EvidenceEntity
import com.example.reports.ExportResult
import com.example.reports.FullForensicReport
import com.example.reports.LogoPosition
import com.example.reports.LogoSize
import com.example.reports.ReportEvidenceItem
import com.example.reports.ReportExportFormat
import com.example.reports.ReportExporter
import com.example.reports.ReportPaymentSummary
import com.example.reports.ReportSection
import com.example.reports.ReportSourceItem
import com.example.reports.TimelineEventItem
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(viewModel: ForensicViewModel) {
    var activeMainTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = activeMainTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = CyberPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeMainTab]),
                    color = CyberPrimary
                )
            }
        ) {
            Tab(
                selected = activeMainTab == 0,
                onClick = { activeMainTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("تقارير العمل المعتمدة", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
            Tab(
                selected = activeMainTab == 1,
                onClick = { activeMainTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("لوحة الأرباح والمالية", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
        }

        if (activeMainTab == 0) {
            CertifiedReportsView(viewModel = viewModel)
        } else {
            EarningsAndFinanceDashboard(viewModel = viewModel)
        }
    }
}

@Composable
private fun CertifiedReportsView(viewModel: ForensicViewModel) {
    val cases by viewModel.rawCases.collectAsState()
    val evidenceList by viewModel.rawEvidence.collectAsState()
    val rawPayments by viewModel.rawPayments.collectAsState()
    val systemLogoPath by viewModel.systemLogoPath.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedCaseId by remember { mutableStateOf(cases.firstOrNull()?.id ?: "") }
    var selectedReportType by remember { mutableStateOf("تقرير متابعة القضية المعتمد") }
    var selectedFormat by remember { mutableStateOf(ReportExportFormat.PDF) }
    var isExporting by remember { mutableStateOf(false) }
    var lastExportResult by remember { mutableStateOf<ExportResult?>(null) }

    val selectedCase = cases.find { it.id == selectedCaseId } ?: cases.firstOrNull()
    val caseEvidence = selectedCase?.let { c -> evidenceList.filter { it.caseId == c.id } } ?: emptyList()
    val casePayments = selectedCase?.let { c -> rawPayments.filter { it.caseId == c.id } } ?: emptyList()

    // Report Customization States
    var customTitle by remember(selectedReportType) { mutableStateOf(selectedReportType) }
    var customIntro by remember(selectedCase) {
        mutableStateOf(
            selectedCase?.description?.ifBlank { selectedCase.notes }?.ifBlank {
                "تم فحص القضية ومراجعة كافة البيانات الفنية والأدلة الجنائية المرتبطة بها، وتأكيد خلوها من التلاعب وتوثيق البصمات التشفيرية المعتمدة."
            } ?: "ملخص فني معتمد لمتابعة إجراءات الفحص والتحقيق واستخلاص الأدلة."
        )
    }
    var customContent by remember(selectedCase) {
        mutableStateOf(
            selectedCase?.notes?.ifBlank {
                "تمت مطابقة التوقيعات الرقمية وتحليل السجلات واستخلاص البيانات الداعمة للتقرير وفق المعايير والضوابط الجنائية المعتمدة."
            } ?: "فحص وتحليل فني معتمد للبصمات الرقمية وسلسلة الحيازة."
        )
    }
    var customNotesText by remember { mutableStateOf("") }
    var customConclusion by remember { mutableStateOf("تم فحص واعتماد هذا التقرير الجنائي وفق الضوابط والمعايير الرقمية المعتمدة.") }
    var logoPosition by remember { mutableStateOf(LogoPosition.RIGHT) }
    var logoSize by remember { mutableStateOf(LogoSize.MEDIUM) }
    var showLogo by remember { mutableStateOf(true) }
    var visibleSections by remember { mutableStateOf(ReportSection.entries.toList()) }

    var showCustomizeDialog by remember { mutableStateOf(false) }
    var showPreviewDialog by remember { mutableStateOf(false) }

    val reportTypes = listOf(
        "تقرير متابعة القضية المعتمد",
        "سجل المرفقات والتحقق الرقمي",
        "تقرير تقييم العمل والاحتياجات",
        "الملخص التنفيذي العام"
    )

    val reportContent = remember(selectedCase, caseEvidence, selectedReportType, cases.size) {
        generateReportText(selectedReportType, selectedCase, caseEvidence, cases)
    }

    val fullForensicReport = remember(
        selectedCase, caseEvidence, casePayments, customTitle, customIntro, customContent,
        customNotesText, customConclusion, systemLogoPath, showLogo, logoPosition, logoSize, visibleSections
    ) {
        val genDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
        val paidTotal = casePayments.sumOf { it.amount }
        val remaining = ((selectedCase?.totalAmount ?: 0.0) - paidTotal).coerceAtLeast(0.0)
        val paymentSummary = selectedCase?.let { c ->
            if (c.totalAmount > 0 || casePayments.isNotEmpty()) {
                ReportPaymentSummary(
                    totalAmount = c.totalAmount,
                    paidAmount = paidTotal,
                    remainingAmount = remaining,
                    currency = "SAR",
                    paymentStatus = if (remaining <= 0) "مسدد بالكامل" else "متبقي مستحقات",
                    paymentsCount = casePayments.size
                )
            } else null
        }

        FullForensicReport(
            reportId = "REP-${selectedCase?.caseNumber ?: "GEN"}-${System.currentTimeMillis().toString().takeLast(4)}",
            title = customTitle,
            caseNumber = selectedCase?.caseNumber ?: "JB-2026-0001",
            generatedDate = genDate,
            clientName = selectedCase?.clientName ?: "العميل المستهدف",
            clientPhone = selectedCase?.clientPhone ?: "",
            clientEmail = selectedCase?.clientEmail ?: "",
            priority = selectedCase?.priority ?: "متوسط",
            threatType = selectedCase?.threatType ?: "استشارة أمنية وفحص رقمي",
            status = selectedCase?.status ?: "قيد المتابعة",
            dueDate = selectedCase?.dueDate ?: "غير محدد",
            executiveSummary = customIntro,
            timelineEvents = emptyList<TimelineEventItem>(),
            evidenceList = caseEvidence.map { ev ->
                ReportEvidenceItem(
                    name = ev.evidenceName,
                    fileType = ev.fileType,
                    originalFilename = ev.originalFilename,
                    md5Hash = ev.md5Hash,
                    sha256Hash = ev.sha256Hash,
                    fileSizeFormatted = if (ev.fileSizeBytes > 1024 * 1024) "%.1f MB".format(ev.fileSizeBytes.toDouble() / (1024 * 1024)) else "${ev.fileSizeBytes / 1024} KB",
                    chainOfCustody = ev.chainOfCustodyLog
                )
            },
            technicalAnalysis = customContent,
            officialSourcesUsed = emptyList<ReportSourceItem>(),
            finalOutcome = customConclusion,
            securityRecommendations = if (customNotesText.isNotBlank()) {
                customNotesText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
            } else {
                listOf(
                    "تفعيل التحقق بخطوتين عبر تطبيقات المصادقة المتوافقة على جميع الحسابات.",
                    "مراجعة سجلات الوصول والأجهزة المتصلة وتحديث كلمات المرور دورياً.",
                    "حفظ البصمات الرقمية للأدلة في مستودع آمن معزول لضمان عدم العبث أو التلف."
                )
            },
            paymentsSummary = paymentSummary,
            logoPath = systemLogoPath,
            showLogo = showLogo,
            logoPosition = logoPosition,
            logoSize = logoSize,
            visibleSections = visibleSections
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "مركز إصدار تقارير العمل والمتابعة",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "توليد تقارير مهنية موثقة ومعتمدة بصيغ PDF و Word و HTML و TXT مع البصمات الرقمية",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }

        // Report Type Selector
        item {
            Text("اختر نوع التقرير المطلوب:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(reportTypes) { type ->
                    val isSelected = selectedReportType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CyberPrimary else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if (isSelected) CyberPrimaryLight else CyberBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedReportType = type }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = type,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Case Selector - Displayed as a clean vertical list (لا بالسحب)
        if (selectedReportType != "الملخص التنفيذي العام" && cases.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "القضية المستهدفة (اختر من القائمة المباشرة):",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${cases.size} قضية",
                        color = CyberPrimaryLight,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    cases.forEach { c ->
                        val isSelected = (selectedCase?.id == c.id)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCaseId = c.id },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) CyberPrimary.copy(alpha = 0.12f) else CyberCard
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) CyberPrimary else CyberBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedCaseId = c.id },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = CyberPrimary,
                                            unselectedColor = Color.Gray
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = c.caseNumber,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isSelected) CyberPrimaryLight else MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            CyberBadge(
                                                text = c.status,
                                                accentColor = when (c.status) {
                                                    "قيد التحقيق" -> CyberWarning
                                                    "تم الحل بنجاح" -> CyberSuccess
                                                    "عاجلة" -> CyberDanger
                                                    else -> CyberInfo
                                                }
                                            )
                                        }
                                        Text(
                                            text = "${c.clientName} — ${c.title}",
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            maxLines = 1
                                        )
                                    }
                                }

                                if (isSelected) {
                                    CyberBadge(text = "مختارة للتقرير", accentColor = CyberSuccess)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Professional Multi-Format Export Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberPrimaryLight.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = CyberPrimaryLight, modifier = Modifier.size(20.dp))
                            Text(
                                text = "تصدير المستند الرسمي بأعلى جودة",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        CyberBadge(text = "جاهز للتصدير", accentColor = CyberSuccess)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "اختر صيغة الملف المطلوبة للتصدير المعتمد فوراً:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReportExportFormat.entries.forEach { fmt ->
                            val isFmtSelected = selectedFormat == fmt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isFmtSelected) CyberPrimary else MaterialTheme.colorScheme.surface)
                                    .border(1.dp, if (isFmtSelected) CyberPrimaryLight else CyberBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedFormat = fmt }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = fmt.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isFmtSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = when(fmt) {
                                            ReportExportFormat.PDF -> "رسمي"
                                            ReportExportFormat.DOCX -> "وورد"
                                            ReportExportFormat.HTML -> "تفاعلي"
                                            ReportExportFormat.TXT -> "نصي"
                                        },
                                        fontSize = 9.5.sp,
                                        color = if (isFmtSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCustomizeDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberPrimaryLight)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تخصيص التقرير", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showPreviewDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberSuccess)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("معاينة التقرير", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isExporting = true
                                val result = ReportExporter.generateAndSaveReport(context, fullForensicReport, selectedFormat)
                                isExporting = false
                                result.onSuccess { res ->
                                    lastExportResult = res
                                    viewModel.showHud("تم توليد ملف ${res.format.name} بنجاح وحفظه في ذاكرة التخزين", HudType.SUCCESS)
                                }.onFailure { err ->
                                    viewModel.showHud("فشل توليد التقرير: ${err.localizedMessage}", HudType.ERROR)
                                }
                            }
                        },
                        enabled = !isExporting,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isExporting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جاري توليد التقرير واعتماده...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("توليد وتصدير ${selectedFormat.displayName}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Export Result Actions
                    lastExportResult?.let { res ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, CyberSuccess.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberSuccess, modifier = Modifier.size(16.dp))
                                        Text(
                                            text = res.file.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )
                                    }
                                    CyberBadge(text = res.fileSizeFormatted, accentColor = CyberInfo)
                                }

                                Text(
                                    text = "بصمة SHA-256: ${res.sha256Checksum.take(28)}...",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            try {
                                                val shareIntent = ReportExporter.getShareIntent(context, res)
                                                val chooser = Intent.createChooser(shareIntent, "مشاركة تقرير ${res.file.name}")
                                                context.startActivity(chooser)
                                                viewModel.showHud("تم فتح قائمة المشاركة الرسمية", HudType.INFO)
                                            } catch (e: Exception) {
                                                viewModel.showHud("خطأ في المشاركة: ${e.localizedMessage}", HudType.ERROR)
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberSuccess),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("مشاركة الملف", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val viewIntent = ReportExporter.getViewIntent(context, res)
                                                context.startActivity(viewIntent)
                                            } catch (e: Exception) {
                                                viewModel.showHud("لا يوجد تطبيق مهيأ لفتح صيغة ${res.format.extension}", HudType.WARNING)
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("فتح المستند", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Report", reportContent)
                        clipboard.setPrimaryClip(clip)
                        viewModel.showHud("تم نسخ نص التقرير إلى الحافظة بنجاح", HudType.SUCCESS)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("نسخ التقرير", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, reportContent)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "مشاركة التقرير")
                        context.startActivity(shareIntent)
                        viewModel.showHud("تم فتح نافذة المشاركة", HudType.INFO)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberPrimaryLight)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مشاركة التقرير", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Report Document Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = CyberSuccess, modifier = Modifier.size(20.dp))
                            Text(
                                text = "معاينة الوثيقة الرسمية",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberSuccess.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "مختوم رقمياً",
                                color = CyberSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = reportContent,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }

    if (showCustomizeDialog) {
        ReportCustomizeDialog(
            report = fullForensicReport,
            customTitle = customTitle,
            onTitleChange = { customTitle = it },
            customIntro = customIntro,
            onIntroChange = { customIntro = it },
            customContent = customContent,
            onContentChange = { customContent = it },
            customNotes = customNotesText,
            onNotesChange = { customNotesText = it },
            customConclusion = customConclusion,
            onConclusionChange = { customConclusion = it },
            showLogo = showLogo,
            onShowLogoChange = { showLogo = it },
            logoPosition = logoPosition,
            onLogoPositionChange = { logoPosition = it },
            logoSize = logoSize,
            onLogoSizeChange = { logoSize = it },
            visibleSections = visibleSections,
            onVisibleSectionsChange = { visibleSections = it },
            onDismiss = { showCustomizeDialog = false }
        )
    }

    if (showPreviewDialog) {
        ReportPreviewDialog(
            report = fullForensicReport,
            onDismiss = { showPreviewDialog = false },
            onExport = {
                showPreviewDialog = false
                scope.launch {
                    isExporting = true
                    val result = ReportExporter.generateAndSaveReport(context, fullForensicReport, selectedFormat)
                    isExporting = false
                    result.onSuccess { res ->
                        lastExportResult = res
                        viewModel.showHud("تم توليد ملف ${res.format.name} بنجاح وحفظه في ذاكرة التخزين", HudType.SUCCESS)
                    }.onFailure { err ->
                        viewModel.showHud("فشل توليد التقرير: ${err.localizedMessage}", HudType.ERROR)
                    }
                }
            }
        )
    }
}

@Composable
private fun EarningsAndFinanceDashboard(viewModel: ForensicViewModel) {
    val cases by viewModel.rawCases.collectAsState()
    val rawPayments by viewModel.rawPayments.collectAsState()
    val searchQuery by viewModel.paymentSearchQuery.collectAsState()
    val methodFilter by viewModel.paymentMethodFilter.collectAsState()
    val filteredPayments by viewModel.filteredPayments.collectAsState()
    val context = LocalContext.current

    var showAddPaymentDialog by remember { mutableStateOf(false) }

    // Financial Metrics
    val totalContractAmount = cases.sumOf { it.totalAmount }
    val totalCollected = rawPayments.sumOf { it.amount }
    val totalRemaining = (totalContractAmount - totalCollected).coerceAtLeast(0.0)
    val collectionRate = if (totalContractAmount > 0) ((totalCollected / totalContractAmount) * 100).toInt() else 100

    val paymentMethods = listOf("الكل", "تحويل بنكي", "بطاقة مدى/ائتمان", "STC Pay", "نقدي", "PayPal", "أخرى")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "لوحة متابعة الأرباح والمالية",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "إدارة عوائد القضايا والدفعات وإصدار وصولات السداد الرسمية",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = { showAddPaymentDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_payment_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تسجيل دفعة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Summary KPI Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Collected Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("إجمالي المحصل", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = CyberSuccess, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "%,.2f SAR".format(totalCollected),
                                color = CyberSuccess,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "نسبة التحصيل: $collectionRate%",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Total Contracted Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("إجمالي التعاقدات", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                Icon(Icons.Default.AttachMoney, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "%,.2f SAR".format(totalContractAmount),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${cases.size} قضية مسجلة",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Remaining Uncollected Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("المتبقي للتحصيل", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = CyberWarning, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "%,.2f SAR".format(totalRemaining),
                                color = if (totalRemaining > 0) CyberWarning else CyberSuccess,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (totalRemaining > 0) "مستحقات معلقة" else "لا توجد متأخرات",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Total Payments Count Card
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("عمليات السداد", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                Icon(Icons.Default.Receipt, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${rawPayments.size} دفعة",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "سجل الإيصالات المالية",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Export Financial Statement Action
        item {
            OutlinedButton(
                onClick = {
                    val summaryText = buildString {
                        appendLine("======================================================")
                        appendLine("             كشف المتابعة المالية والأرباح")
                        appendLine("      JAFFAR BADRAN FORENSIC & FINANCIAL AUDIT")
                        appendLine("======================================================")
                        appendLine("تاريخ التقرير: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())}")
                        appendLine("إجمالي قيمة القضايا: %,.2f SAR".format(totalContractAmount))
                        appendLine("إجمالي المبالغ المحصلة: %,.2f SAR".format(totalCollected))
                        appendLine("المبالغ المتبقية للتحصيل: %,.2f SAR".format(totalRemaining))
                        appendLine("نسبة التحصيل: $collectionRate%")
                        appendLine("عدد الدفعات المسجلة: ${rawPayments.size}")
                        appendLine("------------------------------------------------------")
                        appendLine("سجل آخر الدفعات:")
                        if (rawPayments.isEmpty()) {
                            appendLine("لا توجد دفعات مسجلة بعد.")
                        } else {
                            rawPayments.take(15).forEach { p ->
                                appendLine("• رقم الإيصال: ${p.receiptNumber.ifBlank { "غير مسجل" }} | التاريخ: ${p.paymentDate}")
                                appendLine("  القضية: ${p.caseNumber} | المبلغ: %,.2f ${p.currency} | الطريقة: ${p.paymentMethod}".format(p.amount))
                                if (p.notes.isNotBlank()) appendLine("  ملاحظات: ${p.notes}")
                            }
                        }
                        appendLine("======================================================")
                        appendLine("            الاعتماد والتدقيق: جعفر بدران")
                        appendLine("======================================================")
                    }

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, summaryText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "مشاركة كشف الحساب المالي"))
                    viewModel.showHud("تم فتح نافذة مشاركة الكشف المالي", HudType.INFO)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberPrimaryLight)
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("مشاركة وتصدير كشف الحساب المالي الشامل", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Filter Bar & Search
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.paymentSearchQuery.value = it },
                    placeholder = { Text("بحث برقم القضية، رقم الإيصال، أو الملاحظات...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder
                    ),
                    singleLine = true
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(paymentMethods) { method ->
                        val isSelected = methodFilter == method
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.paymentMethodFilter.value = method },
                            label = { Text(method, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = CyberPrimaryLight
                            )
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سجل الإيصالات والمعاملات (${filteredPayments.size})",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Payment Items
        if (filteredPayments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("لا توجد دفعات مالية مسجلة حالياً", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("اضغط على «تسجيل دفعة» لإضافة سداد مالي لإحدى القضايا", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(filteredPayments) { payment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = payment.caseNumber,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (payment.receiptNumber.isNotBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CyberSecondary.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "إيصال: ${payment.receiptNumber}",
                                                color = CyberSecondary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = payment.paymentDate,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "%,.2f ${payment.currency}".format(payment.amount),
                                    color = CyberSuccess,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = payment.paymentMethod,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        if (payment.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = payment.notes,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddPaymentDialog) {
        AddPaymentDialog(
            cases = cases,
            onDismiss = { showAddPaymentDialog = false },
            onConfirm = { caseId, amount, method, date, notes, receiptNumber ->
                viewModel.addCasePayment(
                    caseId = caseId,
                    amount = amount,
                    paymentMethod = method,
                    paymentDate = date,
                    notes = notes,
                    receiptNumber = receiptNumber
                )
                showAddPaymentDialog = false
            }
        )
    }
}

@Composable
private fun AddPaymentDialog(
    cases: List<CaseEntity>,
    onDismiss: () -> Unit,
    onConfirm: (caseId: String, amount: Double, method: String, date: String, notes: String, receiptNumber: String) -> Unit
) {
    var selectedCaseId by remember { mutableStateOf(cases.firstOrNull()?.id ?: "") }
    var amountText by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("تحويل بنكي") }
    var receiptNumber by remember { mutableStateOf("REC-${System.currentTimeMillis().toString().takeLast(6)}") }
    var notes by remember { mutableStateOf("") }
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    val methods = listOf("تحويل بنكي", "بطاقة مدى/ائتمان", "STC Pay", "نقدي", "PayPal", "أخرى")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل دفعة مالية جديدة", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Text("اختر القضية المعنية:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(cases) { c ->
                            val isSelected = selectedCaseId == c.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCaseId = c.id },
                                label = { Text("${c.caseNumber} - ${c.clientName}", fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("المبلغ (SAR)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Text("طريقة السداد:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(methods) { method ->
                            FilterChip(
                                selected = selectedMethod == method,
                                onClick = { selectedMethod = method },
                                label = { Text(method, fontSize = 11.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = receiptNumber,
                        onValueChange = { receiptNumber = it },
                        label = { Text("رقم الإيصال أو المرجع") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات السداد (اختياري)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (selectedCaseId.isNotBlank() && amount > 0) {
                        onConfirm(selectedCaseId, amount, selectedMethod, currentDate, notes, receiptNumber)
                    }
                },
                enabled = selectedCaseId.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
            ) {
                Text("تأكيد وحفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

private fun generateReportText(
    type: String,
    caseItem: CaseEntity?,
    evidence: List<EvidenceEntity>,
    allCases: List<CaseEntity>
): String {
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
    return when (type) {
        "سجل المرفقات والتحقق الرقمي" -> """
======================================================
      تقرير سلامة المرفقات والبيانات الرقمية
      JAFFAR BADRAN SYSTEM & DIGITAL DOCUMENTATION
======================================================
تاريخ الإصدار: $dateStr
رقم القضية: ${caseItem?.caseNumber ?: "عام"}
المسؤول عن المتابعة: جعفر بدران
العميل: ${caseItem?.clientName ?: "غير محدد"}

[1] حصر المرفقات والملفات الرقمية:
${
    if (evidence.isEmpty()) "لا توجد مرفقات مسجلة في هذه القضية حالياً."
    else evidence.joinToString("\n------------------------------------------------------\n") { evi ->
        """• اسم المرفق: ${evi.evidenceName} [${evi.fileType}]
  الملف المصدر: ${evi.originalFilename}
  بصمة MD5: ${evi.md5Hash}
  بصمة SHA-256: ${evi.sha256Hash}
  المصدر / التطبيق: ${evi.exifSoftware.ifBlank { "نظام الإدارة والتوثيق" }}
  سجل التوثيق: ${evi.chainOfCustodyLog}"""
    }
}

[2] إقرار مطابقة البيانات:
نقر بأن جميع المرفقات والملفات أعلاه تم التحقق من سلامتها الفنية ومطابقة بصمات الهاش الرقمية لضمان عدم التعديل.
======================================================
            المصادقة: جعفر بدران
======================================================
        """.trimIndent()

        "تقرير تقييم العمل والاحتياجات" -> """
======================================================
      تقرير تقييم الحالة ومتطلبات العمل
      JAFFAR BADRAN MANAGEMENT SYSTEM
======================================================
التاريخ: $dateStr
رقم الملف: ${caseItem?.caseNumber ?: "JB-2026"}
العميل: ${caseItem?.clientName ?: "غير محدد"}
نوع الطلب / الموضوع: ${caseItem?.threatType ?: "طلب دعم فني"}
مستوى الأولوية: ${caseItem?.priority ?: "عالي"}

[1] التوصيف الفني لملف العمل:
${caseItem?.title ?: "ملف متابعة"}
ملاحظات العمل: ${caseItem?.notes ?: "متابعة الإجراءات ومراجعة المتطلبات"}

[2] خطة العمل والتنفيذ:
1. مراجعة المتطلبات والتواصل المباشر مع العميل.
2. فحص وتأمين الوثائق والبيانات المطلوبة.
3. التنسيق مع الأطراف المعنية لإنجاز متطلبات الملف.
4. تقديم الدعم اللازم والمتابعة المستمرة حتى اكتمال المعالجة.

======================================================
            المسؤول: جعفر بدران
======================================================
        """.trimIndent()

        "الملخص التنفيذي العام" -> """
======================================================
      الملخص التنفيذي لحالة ملفات العمل النشطة
      JAFFAR BADRAN MANAGEMENT SYSTEM
======================================================
تاريخ التقرير: $dateStr
إجمالي القضايا والملفات: ${allCases.size} قضية
الملفات النشطة: ${allCases.count { it.status != "مكتملة" && it.status != "مغلقة" }}
الملفات ذات الأولوية العاجلة: ${allCases.count { it.priority == "حرجة" }}
الملفات المكتملة: ${allCases.count { it.status == "مكتملة" }}

[1] قائمة ملفات العمل الجارية:
${
    if (allCases.isEmpty()) "لا توجد قضايا مسجلة."
    else allCases.take(8).joinToString("\n") { c ->
        "• [${c.caseNumber}] ${c.title} (${c.clientName}) - الحالة: ${c.status} [${c.priority}]"
    }
}

======================================================
            المشرف العام: جعفر بدران
======================================================
        """.trimIndent()

        else -> """
======================================================
      تقرير متابعة القضية وملف العمل المعتمد
      JAFFAR BADRAN SYSTEM
======================================================
رقم القضية: ${caseItem?.caseNumber ?: "JB-2026-0000"}
تاريخ التقرير: $dateStr
العميل: ${caseItem?.clientName ?: "غير محدد"}
تصنيف الملف: ${caseItem?.threatType ?: "طلب عمل"}
مستوى الأولوية: ${caseItem?.priority ?: "عادي"}
حالة القضية: ${caseItem?.status ?: "قيد المتابعة"}
المسؤول: ${caseItem?.assignedInvestigator ?: "جعفر بدران"}

[1] موضوع القضية وتفاصيل العمل:
${caseItem?.title ?: "متابعة ملف عمل"}
ملاحظات المتابعة: ${caseItem?.notes ?: "تمت مراجعة الوثائق وتحديث السجلات"}

[2] التسلسل الزمني للأحداث (Timeline):
${caseItem?.timelineEventsJson ?: "لا توجد مراحل مسجلة"}

[3] المرفقات والملفات المرتبطة:
${
    if (evidence.isEmpty()) "لا توجد مرفقات مسجلة حالياً."
    else evidence.joinToString("\n---\n") { evi ->
        "• ${evi.evidenceName} (${evi.fileType}) | SHA-256: ${evi.sha256Hash.take(16)}..."
    }
}

[4] التوصيات والخطوات القادمة:
استكمال الإجراءات المحددة وفق خطة العمل وموافاة العميل بالنتائج.
======================================================
        المصادقة والاعتماد: جعفر بدران
======================================================
        """.trimIndent()
    }
}

// =========================================================================
// CUSTOMIZE REPORT DIALOG: تخصيص كافة بيانات وتفاصيل وشعار التقرير
// =========================================================================
@Composable
private fun ReportCustomizeDialog(
    report: FullForensicReport,
    customTitle: String,
    onTitleChange: (String) -> Unit,
    customIntro: String,
    onIntroChange: (String) -> Unit,
    customContent: String,
    onContentChange: (String) -> Unit,
    customNotes: String,
    onNotesChange: (String) -> Unit,
    customConclusion: String,
    onConclusionChange: (String) -> Unit,
    showLogo: Boolean,
    onShowLogoChange: (Boolean) -> Unit,
    logoPosition: LogoPosition,
    onLogoPositionChange: (LogoPosition) -> Unit,
    logoSize: LogoSize,
    onLogoSizeChange: (LogoSize) -> Unit,
    visibleSections: List<ReportSection>,
    onVisibleSectionsChange: (List<ReportSection>) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = CyberPrimaryLight, modifier = Modifier.size(22.dp))
                        Column {
                            Text(
                                text = "تخصيص بنية وتفاصيل التقرير",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "التحكم في النصوص والشعار وترتيب الأقسام قبل التصدير",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Section 1: النصوص والعناوين
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "1. العناوين والنصوص الرسمية",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = CyberPrimaryLight
                                )

                                OutlinedTextField(
                                    value = customTitle,
                                    onValueChange = onTitleChange,
                                    label = { Text("عنوان التقرير") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = customIntro,
                                    onValueChange = onIntroChange,
                                    label = { Text("المقدمة والملخص التنفيذي") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = customContent,
                                    onValueChange = onContentChange,
                                    label = { Text("المحتوى والتحليل الفني") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = customNotes,
                                    onValueChange = onNotesChange,
                                    label = { Text("الملاحظات والتوصيات (سطر لكل نقطة)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 2,
                                    placeholder = { Text("مثال:\nتحديث كلمات المرور\nتفعيل التحقق بخطوتين") },
                                    shape = RoundedCornerShape(8.dp)
                                )

                                OutlinedTextField(
                                    value = customConclusion,
                                    onValueChange = onConclusionChange,
                                    label = { Text("الخاتمة والتوصيف النهائي") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 2,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }

                    // Section 2: إعدادات الشعار
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "2. موضع وحجم الشعار الرسمي",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = CyberPrimaryLight
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "تضمين الشعار", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Checkbox(
                                            checked = showLogo,
                                            onCheckedChange = onShowLogoChange,
                                            colors = CheckboxDefaults.colors(checkedColor = CyberPrimary)
                                        )
                                    }
                                }

                                if (showLogo) {
                                    // Location selector
                                    Text(text = "مكان الشعار في رأس الصفحة:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf(
                                            LogoPosition.RIGHT to "اليمين",
                                            LogoPosition.CENTER to "الوسط",
                                            LogoPosition.LEFT to "اليسار"
                                        ).forEach { (pos, label) ->
                                            val isSelected = logoPosition == pos
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) CyberPrimary else MaterialTheme.colorScheme.surface)
                                                    .border(1.dp, if (isSelected) CyberPrimaryLight else CyberBorder, RoundedCornerShape(8.dp))
                                                    .clickable { onLogoPositionChange(pos) }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }

                                    // Size selector
                                    Text(text = "حجم الشعار:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        listOf(
                                            LogoSize.SMALL to "صغير",
                                            LogoSize.MEDIUM to "متوسط",
                                            LogoSize.LARGE to "كبير"
                                        ).forEach { (sz, label) ->
                                            val isSelected = logoSize == sz
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) CyberPrimary else MaterialTheme.colorScheme.surface)
                                                    .border(1.dp, if (isSelected) CyberPrimaryLight else CyberBorder, RoundedCornerShape(8.dp))
                                                    .clickable { onLogoSizeChange(sz) }
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Section 3: ترتيب وظهور الأقسام
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "3. ترتيب وظهور أقسام التقرير",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = CyberPrimaryLight
                                )
                                Text(
                                    text = "حدد الأقسام التي تود طباعتها واستخدم الأسهم لإعادة الترتيب:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                visibleSections.forEachIndexed { index, section ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = "${index + 1}.",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CyberPrimaryLight
                                            )
                                            Text(
                                                text = section.titleAr,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            // Move up
                                            IconButton(
                                                onClick = {
                                                    if (index > 0) {
                                                        val newList = visibleSections.toMutableList()
                                                        val item = newList.removeAt(index)
                                                        newList.add(index - 1, item)
                                                        onVisibleSectionsChange(newList)
                                                    }
                                                },
                                                enabled = index > 0,
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowUpward,
                                                    contentDescription = "تحريك للأعلى",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = if (index > 0) CyberPrimaryLight else Color.Gray.copy(alpha = 0.4f)
                                                )
                                            }

                                            // Move down
                                            IconButton(
                                                onClick = {
                                                    if (index < visibleSections.size - 1) {
                                                        val newList = visibleSections.toMutableList()
                                                        val item = newList.removeAt(index)
                                                        newList.add(index + 1, item)
                                                        onVisibleSectionsChange(newList)
                                                    }
                                                },
                                                enabled = index < visibleSections.size - 1,
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowDownward,
                                                    contentDescription = "تحريك للأسفل",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = if (index < visibleSections.size - 1) CyberPrimaryLight else Color.Gray.copy(alpha = 0.4f)
                                                )
                                            }

                                            // Remove section
                                            IconButton(
                                                onClick = {
                                                    val newList = visibleSections.toMutableList()
                                                    newList.removeAt(index)
                                                    onVisibleSectionsChange(newList)
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "إخفاء القسم",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = CyberDanger
                                                )
                                            }
                                        }
                                    }
                                }

                                // If some sections are hidden, allow re-adding them
                                val hiddenSections = ReportSection.entries.filter { !visibleSections.contains(it) }
                                if (hiddenSections.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "أقسام مخفية (اضغط لإعادة الإضافة):",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        hiddenSections.forEach { hidden ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(MaterialTheme.colorScheme.surface)
                                                    .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                                                    .clickable {
                                                        onVisibleSectionsChange(visibleSections + hidden)
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp), tint = CyberSuccess)
                                                    Text(text = hidden.titleAr, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurface)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "حفظ واعتماد التخصيص", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// =========================================================================
// PREVIEW REPORT DIALOG: معاينة مباشرة وتفاعلية قبل التصدير
// =========================================================================
@Composable
private fun ReportPreviewDialog(
    report: FullForensicReport,
    onDismiss: () -> Unit,
    onExport: () -> Unit
) {
    val logoBitmap = remember(report.logoPath) {
        if (report.logoPath != null) {
            try {
                BitmapFactory.decodeFile(report.logoPath)
            } catch (_: Exception) {
                null
            }
        } else null
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = CyberSuccess, modifier = Modifier.size(22.dp))
                        Column {
                            Text(
                                text = "معاينة التقرير الجنائي المعتمد",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "محاكاة طبق الأصل للشكل النهائي للمستند الرسمي",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Simulated Document Page
                Card(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Header Banner
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F172A))
                                    .padding(14.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Logo Rendering
                                    if (report.showLogo) {
                                        val logoAlignment = when (report.logoPosition) {
                                            LogoPosition.RIGHT -> Alignment.End
                                            LogoPosition.CENTER -> Alignment.CenterHorizontally
                                            LogoPosition.LEFT -> Alignment.Start
                                        }
                                        val logoHeight = when (report.logoSize) {
                                            LogoSize.SMALL -> 36.dp
                                            LogoSize.MEDIUM -> 50.dp
                                            LogoSize.LARGE -> 68.dp
                                        }

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = logoAlignment
                                        ) {
                                            if (logoBitmap != null) {
                                                Image(
                                                    bitmap = logoBitmap.asImageBitmap(),
                                                    contentDescription = "شعار التقرير",
                                                    modifier = Modifier.height(logoHeight),
                                                    contentScale = ContentScale.Fit
                                                )
                                            } else {
                                                Text(
                                                    text = "JAFFAR BADRAN DIGITAL FORENSICS",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color(0xFFD97706)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = report.title,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "رقم القضية: ${report.caseNumber}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                        Text(text = "التاريخ: ${report.generatedDate}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(Color(0xFFD97706))
                                    )
                                }
                            }
                        }

                        // Render Visible Sections
                        items(report.visibleSections) { section ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = section.titleAr,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberPrimaryLight
                                    )

                                    when (section) {
                                        ReportSection.EXECUTIVE_SUMMARY -> {
                                            Text(text = report.executiveSummary, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                                        }
                                        ReportSection.CASE_DETAILS -> {
                                            Text(text = "العميل: ${report.clientName} | التصنيف: ${report.threatType} | الأولوية: ${report.priority} | الحالة: ${report.status}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                        ReportSection.FINANCIAL_SUMMARY -> {
                                            report.paymentsSummary?.let { p ->
                                                Text(text = "إجمالي المبلغ: ${p.totalAmount} SAR | المسدد: ${p.paidAmount} SAR | المتبقي: ${p.remainingAmount} SAR (${p.paymentStatus})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                            } ?: Text(text = "لا توجد مستحقات مالية مسجلة على هذه القضية.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        ReportSection.EVIDENCE_LEDGER -> {
                                            Text(text = "عدد الأدلة والمرفقات الموثقة: ${report.evidenceList.size} ملفات مؤمنة ببصمات SHA-256 وسلسلة حيازة غير قابلة للتلاعب.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                        ReportSection.TECHNICAL_ANALYSIS -> {
                                            Text(text = report.technicalAnalysis, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 16.sp)
                                        }
                                        ReportSection.CUSTOM_NOTES -> {
                                            Text(text = "تم توثيق كافة الملاحظات الفنية والإرشادات المرتبطة بملف الفحص بدقة.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                        ReportSection.RECOMMENDATIONS -> {
                                            report.securityRecommendations.forEach { rec ->
                                                Text(text = "• $rec", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                        }
                                        ReportSection.SIGNATURE -> {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(text = "المحقق الجنائي الرقمي: جعفر بدران", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                                    Text(text = "التوثيق والاعتماد: VERIFIED & OFFICIALLY SEALED", fontSize = 9.5.sp, color = CyberSuccess)
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .border(1.dp, Color(0xFFD97706), RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(text = "ختم الاعتماد الرسمي", fontSize = 10.sp, color = Color(0xFFD97706), fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "إغلاق المعاينة", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onExport,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "تصدير الآن", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
