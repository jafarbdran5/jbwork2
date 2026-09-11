package com.example.ui.screens.evidence

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.EvidenceEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.ForensicCrypto
import com.example.ui.components.HudType
import com.example.ui.components.InAppConfirmationSheet
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
import com.example.ui.theme.CyberTertiary
import com.example.ui.theme.CyberWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ForensicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceScreen(
    viewModel: ForensicViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val evidenceList by viewModel.rawEvidence.collectAsStateWithLifecycle()
    val casesList by viewModel.rawCases.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("مستودع المرفقات والملفات", "فحص ومعاينة الصور (ELA)", "حساب البصمات الرقمية (Hash)")

    var activeEvidenceDetail by remember { mutableStateOf<EvidenceEntity?>(null) }
    var evidenceToDelete by remember { mutableStateOf<EvidenceEntity?>(null) }
    var showAddEvidenceSheet by remember { mutableStateOf(false) }

    // Hashing tool state
    var hashInputText by remember { mutableStateOf("https://example.com/client-document-verification") }
    var calculatedMd5 by remember { mutableStateOf(ForensicCrypto.calculateMd5(hashInputText)) }
    var calculatedSha256 by remember { mutableStateOf(ForensicCrypto.calculateSha256(hashInputText)) }

    // ELA Simulation state
    var elaSensitivity by remember { mutableFloatStateOf(0.65f) }

    // Add Evidence Form
    var formCaseId by remember { mutableStateOf(casesList.firstOrNull()?.id ?: "case_general") }
    var formCaseNumber by remember { mutableStateOf(casesList.firstOrNull()?.caseNumber ?: "JB-2026-0000") }
    var formEvidenceName by remember { mutableStateOf("") }
    var formFileType by remember { mutableStateOf("لقطة شاشة") }
    var formOriginalFilename by remember { mutableStateOf("document_capture.png") }
    var formDeviceModel by remember { mutableStateOf("Apple iPhone 15 Pro") }
    var formSoftware by remember { mutableStateOf("WhatsApp Messenger v24.18") }
    var formGpsCoords by remember { mutableStateOf("24.7136° N, 46.6753° E") }
    var formNotes by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberBg,
        floatingActionButton = {
            if (selectedTabIndex == 0) {
                FloatingActionButton(
                    onClick = { showAddEvidenceSheet = true },
                    containerColor = CyberPrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.testTag("add_evidence_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة مرفق جديد")
                }
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
                            text = "المرفقات والملفات والتحقق الرقمي",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "إدارة الملفات والوثائق وفحص سلامتها والتحقق من البصمات الرقمية",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    CyberBadge(text = "${evidenceList.size} ملفات ومرفقات", accentColor = CyberSecondary)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Reverse Visual Search Launchers Bar
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CyberSurface
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ImageSearch, contentDescription = null, tint = CyberTertiary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("محركات البحث العكسي عن الصور (Reverse Search):", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Google Lens", "Yandex", "Bing Visual").forEach { engine ->
                                Button(
                                    onClick = { ForensicCrypto.launchReverseSearch(context, engine) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCardElevated),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(engine, fontSize = 11.sp, color = CyberPrimaryLight)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = CyberSurface,
                    contentColor = CyberPrimary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontSize = 11.sp, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }

            // Tab 0: Evidence Locker
            if (selectedTabIndex == 0) {
                if (evidenceList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("لا توجد مرفقات أو ملفات مسجلة حالياً.", color = TextSecondary)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(evidenceList, key = { it.id }) { evi ->
                            EvidenceCard(
                                evidence = evi,
                                onClick = { activeEvidenceDetail = evi },
                                onDelete = { evidenceToDelete = evi }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            }

            // Tab 1: ELA Simulation
            if (selectedTabIndex == 1) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = CyberCardElevated
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Science, contentDescription = null, tint = CyberWarning, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تحليل مستويات الخطأ (Error Level Analysis - ELA)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "يقوم هذا الفحص باكتشاف التباين في معدل ضغط JPEG لكشف التعديلات والفوتوشوب على المستندات والصور المرسلة من المبتز.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated ELA Canvas Visualizer Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF07070A))
                            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = CyberPrimary.copy(alpha = elaSensitivity),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "محاكاة خريطة الفروقات الحرارية لضغط البكسلات",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "حساسية الكشف: ${(elaSensitivity * 100).toInt()}% - ${if (elaSensitivity > 0.6f) "رصد تعديل رقمي في منطقة الرأس" else "ضغط متجانس (صورة أصلية)"}",
                                color = if (elaSensitivity > 0.6f) CyberDanger else CyberSuccess,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("معايرة حساسية الفحص والتباين (Sensitivity Slider):", color = TextSecondary, fontSize = 12.sp)
                    Slider(
                        value = elaSensitivity,
                        onValueChange = { elaSensitivity = it },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberPrimary,
                            activeTrackColor = CyberPrimary,
                            inactiveTrackColor = CyberBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = CyberSurface
                    ) {
                        Column {
                            Text("النتيجة والتحليل الفني:", color = CyberPrimaryLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• البكسلات البيضاء المتوهجة تمثل معدل خطأ أو تباين يشير إلى إمكانية وجود تعديل ببرامج تحرير بعد الحفظ الأولي.\n• الخلفية المعتمة الداكنة تشير إلى اتساق طبقات ملف الصورة الأصلية.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Tab 2: Cryptographic Hashing Tool
            if (selectedTabIndex == 2) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "أداة استخراج البصمة التشفيرية المعتمدة (MD5 & SHA-256)",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "احسب البصمة الرياضية لأي نص أو رابط أو عينة ملف لتضمينها في شهادة المحكمة",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = hashInputText,
                        onValueChange = {
                            hashInputText = it
                            calculatedMd5 = ForensicCrypto.calculateMd5(it)
                            calculatedSha256 = ForensicCrypto.calculateSha256(it)
                        },
                        label = { Text("النص أو الرابط أو البصمة المراد فحصها") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // MD5 Box
                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = CyberCardElevated
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CyberBadge(text = "MD5 HASH (128-bit)", accentColor = CyberSecondary)
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("MD5", calculatedMd5))
                                        viewModel.showHud("تم نسخ بصمة MD5", HudType.SUCCESS)
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = CyberPrimaryLight, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = calculatedMd5,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SHA-256 Box
                    CyberCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = CyberCardElevated
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CyberBadge(text = "SHA-256 HASH (256-bit Certified)", accentColor = CyberSuccess)
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("SHA256", calculatedSha256))
                                        viewModel.showHud("تم نسخ بصمة SHA-256", HudType.SUCCESS)
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = CyberSuccess, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = calculatedSha256,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Evidence Details Sheet
    if (activeEvidenceDetail != null) {
        val evi = activeEvidenceDetail!!
        ModalBottomSheet(
            onDismissRequest = { activeEvidenceDetail = null },
            containerColor = CyberSurface,
            scrimColor = Color.Black.copy(alpha = 0.7f),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = evi.evidenceName,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    CyberBadge(text = evi.fileType, accentColor = CyberPrimaryLight)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("القضية التابعة: ${evi.caseNumber}", color = CyberSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(12.dp))

                // Hashing details
                CyberCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CyberCardElevated) {
                    Column {
                        Text("البصمات التشفيرية المعتمدة:", color = CyberSuccess, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("MD5: ${evi.md5Hash}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("SHA-256: ${evi.sha256Hash}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // EXIF & Device Specs
                CyberCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CyberCardElevated) {
                    Column {
                        Text("بيانات الـ EXIF والجهاز المستخرج:", color = CyberPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• الجهاز: ${evi.exifDeviceModel}", fontSize = 12.sp, color = TextSecondary)
                        Text("• البرنامج / النظام: ${evi.exifSoftware}", fontSize = 12.sp, color = TextSecondary)
                        Text("• الإحداثيات الجغرافية: ${evi.exifGpsCoords}", fontSize = 12.sp, color = TextSecondary)
                        Text("• تاريخ الاستخراج: ${evi.exifTimestamp}", fontSize = 12.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custody / History Log
                Text("سجل ومسار التوثيق (History):", color = TextSecondary, fontSize = 12.sp)
                Text(evi.chainOfCustodyLog, color = TextPrimary, fontSize = 13.sp, lineHeight = 19.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Text("ملاحظات الفاحص:", color = TextSecondary, fontSize = 12.sp)
                Text(evi.notes.ifEmpty { "لا توجد ملاحظات." }, color = TextPrimary, fontSize = 13.sp)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Delete confirmation
    if (evidenceToDelete != null) {
        val target = evidenceToDelete!!
        InAppConfirmationSheet(
            title = "حذف المرفق / الملف",
            description = "هل أنت متأكد من حذف '${target.evidenceName}'؟ سيتم نقله إلى سلة المحذوفات المؤقتة.",
            confirmLabel = "تأكيد الحذف",
            onConfirm = {
                viewModel.deleteEvidence(target)
                evidenceToDelete = null
            },
            onDismiss = { evidenceToDelete = null }
        )
    }

    // Add Evidence Sheet
    if (showAddEvidenceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddEvidenceSheet = false },
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
                    text = "إضافة مرفق / ملف جديد",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = formEvidenceName,
                    onValueChange = { formEvidenceName = it },
                    label = { Text("اسم المرفق أو الملف (مثال: محادثة العميل / وثيقة)") },
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
                    value = formOriginalFilename,
                    onValueChange = { formOriginalFilename = it },
                    label = { Text("اسم الملف الأصلي (مثال: screenshot.png)") },
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
                    value = formDeviceModel,
                    onValueChange = { formDeviceModel = it },
                    label = { Text("نوع الجهاز وموديله (EXIF Model)") },
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
                    value = formNotes,
                    onValueChange = { formNotes = it },
                    label = { Text("ملاحظات الملف ومصدر الاستلام") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
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
                        if (formEvidenceName.isNotBlank()) {
                            val selectedCase = casesList.firstOrNull()
                            viewModel.addEvidence(
                                caseId = selectedCase?.id ?: "case_01",
                                caseNumber = selectedCase?.caseNumber ?: "JB-2026-0814",
                                evidenceName = formEvidenceName,
                                fileType = formFileType,
                                originalFilename = formOriginalFilename,
                                deviceModel = formDeviceModel,
                                software = formSoftware,
                                gpsCoords = formGpsCoords,
                                notes = formNotes,
                                contentSampleForHashing = formEvidenceName + formOriginalFilename
                            )
                            showAddEvidenceSheet = false
                        } else {
                            viewModel.showHud("يرجى إدخال اسم المرفق أو الملف", HudType.WARNING)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("حفظ المرفق وحساب البصمة الرقمية", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun EvidenceCard(
    evidence: EvidenceEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("evidence_card_${evidence.id}"),
        backgroundColor = CyberCardElevated,
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = CyberSuccess, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = evidence.caseNumber,
                        color = CyberPrimaryLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CyberBadge(text = evidence.fileType, accentColor = CyberSecondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger.copy(alpha = 0.7f), modifier = Modifier.size(15.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = evidence.evidenceName,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "SHA256: ${evidence.sha256Hash}",
                fontFamily = FontFamily.Monospace,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = evidence.exifDeviceModel,
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "عرض تفاصيل الملف ←",
                    color = CyberPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
