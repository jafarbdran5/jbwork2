package com.example.ui.screens.reports

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.CaseEntity
import com.example.data.local.entities.CasePaymentEntity
import com.example.data.local.entities.EvidenceEntity
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
    val context = LocalContext.current

    var selectedCaseId by remember { mutableStateOf(cases.firstOrNull()?.id ?: "") }
    var selectedReportType by remember { mutableStateOf("تقرير متابعة القضية المعتمد") }

    val selectedCase = cases.find { it.id == selectedCaseId } ?: cases.firstOrNull()
    val caseEvidence = selectedCase?.let { c -> evidenceList.filter { it.caseId == c.id } } ?: emptyList()

    val reportTypes = listOf(
        "تقرير متابعة القضية المعتمد",
        "سجل المرفقات والتحقق الرقمي",
        "تقرير تقييم العمل والاحتياجات",
        "الملخص التنفيذي العام"
    )

    val reportContent = remember(selectedCase, caseEvidence, selectedReportType, cases.size) {
        generateReportText(selectedReportType, selectedCase, caseEvidence, cases)
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
                text = "توليد تقارير مهنية موثقة ومعتمدة لمتابعة القضايا وملفات العملاء والمهام",
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

        // Case Selector
        if (selectedReportType != "الملخص التنفيذي العام" && cases.isNotEmpty()) {
            item {
                Text("القضية المستهدفة:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cases) { c ->
                        val isSelected = selectedCase?.id == c.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CyberSecondary else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (isSelected) CyberSecondary else CyberBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedCaseId = c.id }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "${c.caseNumber} - ${c.clientName}",
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
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
