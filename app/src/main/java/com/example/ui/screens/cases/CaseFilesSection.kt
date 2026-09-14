package com.example.ui.screens.cases

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import com.example.data.local.entities.EvidenceEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.ForensicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.DecimalFormat

@Composable
fun CaseFilesSection(
    caseId: String,
    caseNumber: String,
    viewModel: ForensicViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val activeFiles by viewModel.getEvidenceForCase(caseId).collectAsState(initial = emptyList())
    val trashFiles by viewModel.getDeletedEvidenceForCase(caseId).collectAsState(initial = emptyList())

    var showTrashDialog by remember { mutableStateOf(false) }
    var selectedFileForDetails by remember { mutableStateOf<EvidenceEntity?>(null) }
    var showManualAddDialog by remember { mutableStateOf(false) }
    var isProcessingFile by remember { mutableStateOf(false) }
    var processingProgressText by remember { mutableStateOf("") }

    // SAF Document Picker for Offline File Ingestion
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                isProcessingFile = true
                processingProgressText = "جاري قراءة واستنساخ الملف محلياً واستخراج البصمات..."
                try {
                    withContext(Dispatchers.IO) {
                        processAndSaveOfflineFile(
                            context = context,
                            uri = uri,
                            caseId = caseId,
                            caseNumber = caseNumber,
                            viewModel = viewModel
                        )
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "فشل حفظ الملف: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                } finally {
                    isProcessingFile = false
                    processingProgressText = ""
                }
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row with Title and Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CyberSecondary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderZip,
                            contentDescription = null,
                            tint = CyberSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ملفات ومرفقات القضية (${activeFiles.size})",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "أرشفة مشفرة محلياً مع بصمات SHA-256 و MD5 التلقائية",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Trash icon with counter
                IconButton(
                    onClick = { showTrashDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    BadgedBox(
                        badge = {
                            if (trashFiles.isNotEmpty()) {
                                Badge(containerColor = CyberDanger) {
                                    Text(trashFiles.size.toString(), color = Color.White, fontSize = 9.sp)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "سلة محذوفات الملفات",
                            tint = if (trashFiles.isNotEmpty()) CyberWarning else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons (Pick File & Manual Entry)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        filePickerLauncher.launch(arrayOf("*/*"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إرفاق ملف أوفلاين", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { showManualAddDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberPrimaryLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تسجيل يدوي", fontSize = 12.sp)
                }
            }

            // Progress Indicator during ingestion
            AnimatedVisibility(visible = isProcessingFile) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = CyberPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(processingProgressText, color = CyberPrimaryLight, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Active Files List
            if (activeFiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCardElevated)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد ملفات مرفقة بهذه القضية حتى الآن.\nيمكنك إرفاق صور، مستندات PDF، تسجيلات صوتية، سجلات فحص، أو حزم ZIP.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    activeFiles.forEach { file ->
                        CaseFileItemCard(
                            file = file,
                            onOpenClick = {
                                openCaseFile(context, file)
                            },
                            onShareClick = {
                                shareCaseFile(context, file)
                            },
                            onDetailsClick = {
                                selectedFileForDetails = file
                            },
                            onDeleteClick = {
                                viewModel.softDeleteCaseFile(file.id, file.evidenceName)
                            }
                        )
                    }
                }
            }
        }
    }

    // Details Dialog
    if (selectedFileForDetails != null) {
        CaseFileDetailsDialog(
            file = selectedFileForDetails!!,
            onDismiss = { selectedFileForDetails = null },
            onShare = { shareCaseFile(context, selectedFileForDetails!!) },
            onOpen = { openCaseFile(context, selectedFileForDetails!!) }
        )
    }

    // Trash Dialog
    if (showTrashDialog) {
        CaseFileTrashDialog(
            trashFiles = trashFiles,
            onDismiss = { showTrashDialog = false },
            onRestore = { file -> viewModel.restoreCaseFile(file.id, file.evidenceName) },
            onPermanentDelete = { file -> viewModel.permanentlyDeleteCaseFile(file.id, file.evidenceName, file.localFilePath) }
        )
    }

    // Manual Evidence Entry Dialog
    if (showManualAddDialog) {
        ManualEvidenceDialog(
            caseId = caseId,
            caseNumber = caseNumber,
            onDismiss = { showManualAddDialog = false },
            onSave = { name, type, hash, notes ->
                viewModel.attachCaseOfflineFile(
                    caseId = caseId,
                    caseNumber = caseNumber,
                    fileName = name,
                    fileType = type,
                    originalFilename = name,
                    localPath = "",
                    fileSizeBytes = 0L,
                    fileSizeFormatted = "يدوي",
                    mimeType = "text/plain",
                    sha256 = hash.ifBlank { "MANUAL-${System.currentTimeMillis()}" },
                    md5 = "",
                    notes = notes
                )
                showManualAddDialog = false
            }
        )
    }
}

@Composable
private fun CaseFileItemCard(
    file: EvidenceEntity,
    onOpenClick: () -> Unit,
    onShareClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    val (icon, badgeColor) = getFileTypeMeta(file.fileType, file.originalFilename)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailsClick() },
        colors = CardDefaults.cardColors(containerColor = CyberCardElevated),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = file.evidenceName,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = file.fileSizeFormatted,
                            color = CyberPrimaryLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("•", color = TextMuted, fontSize = 10.sp)
                        Text(
                            text = file.fileType,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    if (file.sha256Hash.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SHA256: ${file.sha256Hash.take(10)}...",
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("SHA256 Hash", file.sha256Hash))
                                    Toast.makeText(context, "تم نسخ البصمة كاملة", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = CyberPrimaryLight, modifier = Modifier.size(11.dp))
                            }
                        }
                    }
                }
            }

            // Quick Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (file.localFilePath.isNotBlank()) {
                    IconButton(
                        onClick = onOpenClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "عرض الملف",
                            tint = CyberPrimaryLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "مشاركة",
                            tint = CyberSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "نقل للسلة",
                        tint = CyberDanger,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CaseFileDetailsDialog(
    file: EvidenceEntity,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onOpen: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("بيانات وتوثيق الملف الجنائي", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextSecondary)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CyberBorder)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("اسم الملف:", file.evidenceName)
                    DetailRow("الاسم الأصلي:", file.originalFilename)
                    DetailRow("النوع:", file.fileType)
                    DetailRow("الحجم:", "${file.fileSizeFormatted} (${file.fileSizeBytes} بايت)")
                    DetailRow("نوع الوسيط (MIME):", file.mimeType.ifEmpty { "غير محدد" })

                    // Full SHA-256 with Copy
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("بصمة SHA-256:", color = TextSecondary, fontSize = 11.sp)
                            TextButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("SHA256", file.sha256Hash))
                                    Toast.makeText(context, "تم نسخ بصمة SHA-256", Toast.LENGTH_SHORT).show()
                                },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text("نسخ", fontSize = 11.sp, color = CyberPrimaryLight)
                            }
                        }
                        Text(
                            text = file.sha256Hash.ifEmpty { "غير متوفرة" },
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberCardElevated, RoundedCornerShape(6.dp))
                                .padding(6.dp)
                        )
                    }

                    // MD5 Hash
                    if (file.md5Hash.isNotBlank()) {
                        DetailRow("بصمة MD5:", file.md5Hash)
                    }

                    if (file.notes.isNotBlank()) {
                        DetailRow("ملاحظات الفاحص:", file.notes)
                    }

                    if (file.chainOfCustodyLog.isNotBlank()) {
                        DetailRow("سلسلة الحيازة:", file.chainOfCustodyLog)
                    }

                    if (file.localFilePath.isNotBlank()) {
                        DetailRow("المسار المحلي:", file.localFilePath)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (file.localFilePath.isNotBlank()) {
                        Button(
                            onClick = {
                                onOpen()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("فتح الملف", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                onShare()
                                onDismiss()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberSuccess),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("مشاركة", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CaseFileTrashDialog(
    trashFiles: List<EvidenceEntity>,
    onDismiss: () -> Unit,
    onRestore: (EvidenceEntity) -> Unit,
    onPermanentDelete: (EvidenceEntity) -> Unit
) {
    var fileToConfirmDelete by remember { mutableStateOf<EvidenceEntity?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(4.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberDanger.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = CyberDanger)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("سلة محذوفات ملفات القضية (${trashFiles.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextSecondary)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = CyberBorder)

                if (trashFiles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("سلة المحذوفات فارغة تماماً.", color = TextMuted, fontSize = 12.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(trashFiles, key = { it.id }) { file ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = CyberCardElevated),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(file.evidenceName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                        Text("${file.fileSizeFormatted} • ${file.fileType}", fontSize = 10.sp, color = TextSecondary)
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(
                                            onClick = { onRestore(file) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Restore, contentDescription = "استعادة", tint = CyberSuccess, modifier = Modifier.size(18.dp))
                                        }

                                        IconButton(
                                            onClick = { fileToConfirmDelete = file },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteForever, contentDescription = "حذف نهائي", tint = CyberDanger, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إغلاق السلة", fontSize = 12.sp, color = TextPrimary)
                }
            }
        }
    }

    if (fileToConfirmDelete != null) {
        AlertDialog(
            onDismissRequest = { fileToConfirmDelete = null },
            title = { Text("تأكيد الحذف النهائي والتطهير", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من الحذف النهائي للملف «${fileToConfirmDelete!!.evidenceName}»؟ سيتم إتلاف الملف محلياً ومسح سجله نهائياً ولا يمكن التراجع.") },
            confirmButton = {
                Button(
                    onClick = {
                        val target = fileToConfirmDelete!!
                        fileToConfirmDelete = null
                        onPermanentDelete(target)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberDanger)
                ) {
                    Text("تأكيد الحذف النهائي")
                }
            },
            dismissButton = {
                TextButton(onClick = { fileToConfirmDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun ManualEvidenceDialog(
    caseId: String,
    caseNumber: String,
    onDismiss: () -> Unit,
    onSave: (name: String, type: String, hash: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("مستند فني") }
    var hash by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val evidenceTypes = listOf("صورة فوتوغرافية", "لقطة شاشة", "مستند PDF", "تسجيل صوتي", "مقطع فيديو", "تفريغ محادثة", "تقرير فحص جنائي", "حزمة مضغوطة", "سجل شبكة PCAP")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("تسجيل يدوي لمرفق / دليل بالقضية", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم المرفق / الدليل") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("نوع المرفق:", fontSize = 11.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                // Dropdown or selectable list
                var expandedType by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { expandedType = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(type, color = TextPrimary)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                        evidenceTypes.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    type = item
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = hash,
                    onValueChange = { hash = it },
                    label = { Text("بصمة SHA-256 (اختياري)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات الفاحص") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(name.trim(), type, hash.trim(), notes.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank()
                    ) {
                        Text("حفظ المرفق")
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("إلغاء")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Text(value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

private fun getFileTypeMeta(fileType: String, filename: String): Pair<ImageVector, Color> {
    val ext = filename.substringAfterLast('.', "").lowercase()
    return when {
        ext in listOf("png", "jpg", "jpeg", "webp", "gif", "heic") || fileType.contains("صورة") || fileType.contains("شاشة") ->
            Pair(Icons.Default.Image, CyberPrimary)
        ext == "pdf" || fileType.contains("PDF") ->
            Pair(Icons.Default.PictureAsPdf, CyberDanger)
        ext in listOf("doc", "docx", "txt", "rtf", "odt", "csv", "xlsx") || fileType.contains("مستند") ->
            Pair(Icons.Default.Description, CyberInfo)
        ext in listOf("zip", "rar", "7z", "tar", "gz") || fileType.contains("حزمة") || fileType.contains("مضغوط") ->
            Pair(Icons.Default.FolderZip, CyberSecondary)
        ext in listOf("mp3", "wav", "m4a", "ogg", "amr") || fileType.contains("صوت") ->
            Pair(Icons.Default.AudioFile, CyberWarning)
        ext in listOf("mp4", "mkv", "avi", "mov") || fileType.contains("فيديو") ->
            Pair(Icons.Default.VideoFile, CyberPrimaryLight)
        else ->
            Pair(Icons.Default.InsertDriveFile, TextSecondaryDark)
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(bytes / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
}

private fun processAndSaveOfflineFile(
    context: Context,
    uri: Uri,
    caseId: String,
    caseNumber: String,
    viewModel: ForensicViewModel
) {
    val contentResolver = context.contentResolver

    // Query Display Name and Size
    var displayName = "file_${System.currentTimeMillis()}"
    var reportedSize = 0L

    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (nameIndex != -1) {
                displayName = cursor.getString(nameIndex) ?: displayName
            }
            if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                reportedSize = cursor.getLong(sizeIndex)
            }
        }
    }

    val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

    // Sanitize filename
    val safeFileName = displayName.replace(Regex("[^a-zA-Z0-9._\\-\\u0600-\\u06FF]"), "_")

    // Destination Directory: context.filesDir/cases/{caseId}/
    val caseDir = File(context.filesDir, "cases/$caseId")
    if (!caseDir.exists()) {
        caseDir.mkdirs()
    }

    val targetFile = File(caseDir, safeFileName)

    // Stream and Hash calculation in single pass
    val md5Digest = MessageDigest.getInstance("MD5")
    val sha256Digest = MessageDigest.getInstance("SHA-256")

    var totalBytesCopied = 0L

    contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(targetFile).use { output ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
                md5Digest.update(buffer, 0, bytesRead)
                sha256Digest.update(buffer, 0, bytesRead)
                totalBytesCopied += bytesRead
            }
        }
    }

    val actualSize = if (targetFile.exists()) targetFile.length() else totalBytesCopied
    val formattedSize = formatBytes(actualSize)

    val md5Hex = md5Digest.digest().joinToString("") { "%02x".format(it) }
    val sha256Hex = sha256Digest.digest().joinToString("") { "%02x".format(it) }

    val category = when {
        mimeType.startsWith("image/") -> "صورة رقمية"
        mimeType.startsWith("audio/") -> "تسجيل صوتي"
        mimeType.startsWith("video/") -> "مقطع فيديو"
        mimeType == "application/pdf" -> "مستند PDF"
        mimeType.contains("zip") || mimeType.contains("compressed") || mimeType.contains("tar") -> "حزمة أرشيف"
        else -> "ملف قضية"
    }

    viewModel.attachCaseOfflineFile(
        caseId = caseId,
        caseNumber = caseNumber,
        fileName = safeFileName,
        fileType = category,
        originalFilename = displayName,
        localPath = targetFile.absolutePath,
        fileSizeBytes = actualSize,
        fileSizeFormatted = formattedSize,
        mimeType = mimeType,
        sha256 = sha256Hex,
        md5 = md5Hex,
        notes = "تم الحفظ أوفلاين في الذاكرة المشفرة وحساب البصمة الرقمية."
    )
}

private fun openCaseFile(context: Context, file: EvidenceEntity) {
    try {
        val localFile = File(file.localFilePath)
        if (!localFile.exists()) {
            Toast.makeText(context, "الملف غير موجود في الذاكرة المحلية", Toast.LENGTH_SHORT).show()
            return
        }

        val authority = "${context.packageName}.fileprovider"
        val contentUri = FileProvider.getUriForFile(context, authority, localFile)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, file.mimeType.ifBlank { "*/*" })
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "فتح ملف القضية")
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر فتح الملف: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}

private fun shareCaseFile(context: Context, file: EvidenceEntity) {
    try {
        val localFile = File(file.localFilePath)
        if (!localFile.exists()) {
            Toast.makeText(context, "الملف غير موجود للمشاركة", Toast.LENGTH_SHORT).show()
            return
        }

        val authority = "${context.packageName}.fileprovider"
        val contentUri = FileProvider.getUriForFile(context, authority, localFile)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = file.mimeType.ifBlank { "*/*" }
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "ملف قضية: ${file.caseNumber} - ${file.evidenceName}")
            putExtra(Intent.EXTRA_TEXT, "ملف رسمي للقضية ${file.caseNumber}\nالاسم: ${file.evidenceName}\nSHA256: ${file.sha256Hash}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "مشاركة ملف القضية")
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر مشاركة الملف: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}
