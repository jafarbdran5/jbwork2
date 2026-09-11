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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CaseEntity
import com.example.data.local.entities.EvidenceEntity
import com.example.ui.components.HudType
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberInfo
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberPrimaryLight
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSuccess
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ForensicViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(viewModel: ForensicViewModel) {
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
