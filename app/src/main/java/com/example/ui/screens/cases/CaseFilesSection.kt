package com.example.ui.screens.cases

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entities.EvidenceEntity
import com.example.ui.components.CyberBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.ForensicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseFilesSection(
    caseId: String,
    caseNumber: String,
    viewModel: ForensicViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val activeFiles by viewModel.getEvidenceForCase(caseId).collectAsStateWithLifecycle(initialValue = emptyList())
    val trashFiles by viewModel.getDeletedEvidenceForCase(caseId).collectAsStateWithLifecycle(initialValue = emptyList())
    val configuredCategories by viewModel.caseFileCategories.collectAsStateWithLifecycle()

    var showTrashDialog by remember { mutableStateOf(false) }
    var selectedFileForDetails by remember { mutableStateOf<EvidenceEntity?>(null) }
    var selectedFileForEdit by remember { mutableStateOf<EvidenceEntity?>(null) }
    var selectedImageForPreview by remember { mutableStateOf<EvidenceEntity?>(null) }
    var fileToDeleteConfirm by remember { mutableStateOf<EvidenceEntity?>(null) }

    var showAddOptionsSheet by remember { mutableStateOf(false) }
    var showManualAddDialog by remember { mutableStateOf(false) }

    var isProcessingFile by remember { mutableStateOf(false) }
    var processingProgressText by remember { mutableStateOf("") }

    // Search and Category Filter
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("الكل") }

    // Camera capture state
    var pendingCameraPhotoFile by remember { mutableStateOf<File?>(null) }
    var pendingCameraPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher: Camera Capture (TakePicture)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCameraPhotoFile != null) {
            val file = pendingCameraPhotoFile!!
            if (file.exists() && file.length() > 0) {
                coroutineScope.launch {
                    isProcessingFile = true
                    processingProgressText = "جاري حفظ الصورة الملتقطة وحساب البصمات الجنائية..."
                    try {
                        withContext(Dispatchers.IO) {
                            processSavedCameraFile(
                                context = context,
                                photoFile = file,
                                caseId = caseId,
                                caseNumber = caseNumber,
                                viewModel = viewModel,
                                category = "صور"
                            )
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "فشل حفظ الصورة الملتقطة: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    } finally {
                        isProcessingFile = false
                        processingProgressText = ""
                        pendingCameraPhotoFile = null
                        pendingCameraPhotoUri = null
                    }
                }
            }
        }
    }

    // Permission launcher for Camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCameraCapture(
                context = context,
                caseId = caseId,
                onUriReady = { uri, file ->
                    pendingCameraPhotoUri = uri
                    pendingCameraPhotoFile = file
                    cameraLauncher.launch(uri)
                }
            )
        } else {
            Toast.makeText(context, "إذن استخدام الكاميرا مطلوب لالتقاط الصور", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher: Multiple Documents / Files Picker (*/*)
    val multipleDocsPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                isProcessingFile = true
                try {
                    withContext(Dispatchers.IO) {
                        uris.forEachIndexed { index, uri ->
                            processingProgressText = "جاري استيراد الملف (${index + 1} من ${uris.size}) وحساب البصمة..."
                            processAndSaveOfflineFile(
                                context = context,
                                uri = uri,
                                caseId = caseId,
                                caseNumber = caseNumber,
                                viewModel = viewModel,
                                overrideCategory = null
                            )
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "تم حفظ وتوثيق ${uris.size} ملفات بنجاح في القضية", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "خطأ أثناء معالجة الملفات: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                } finally {
                    isProcessingFile = false
                    processingProgressText = ""
                }
            }
        }
    }

    // Launcher: Visual Media Picker (Photos & Videos - Single or Multiple)
    val multiplePhotosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                isProcessingFile = true
                try {
                    withContext(Dispatchers.IO) {
                        uris.forEachIndexed { index, uri ->
                            processingProgressText = "جاري استيراد الصورة/الفيديو (${index + 1} من ${uris.size})..."
                            processAndSaveOfflineFile(
                                context = context,
                                uri = uri,
                                caseId = caseId,
                                caseNumber = caseNumber,
                                viewModel = viewModel,
                                overrideCategory = "صور"
                            )
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "تم حفظ وتوثيق ${uris.size} صور بنجاح في القضية", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "خطأ أثناء استيراد الصور: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                } finally {
                    isProcessingFile = false
                    processingProgressText = ""
                }
            }
        }
    }

    // Filter active files based on Search and Category
    val filteredFiles = remember(activeFiles, searchQuery, selectedCategoryFilter) {
        activeFiles.filter { file ->
            val matchesCategory = if (selectedCategoryFilter == "الكل") true else {
                file.category.equals(selectedCategoryFilter, ignoreCase = true) ||
                (selectedCategoryFilter == "صور" && (file.fileType.contains("صورة") || file.mimeType.startsWith("image/"))) ||
                (selectedCategoryFilter == "مستندات" && (file.fileType.contains("مستند") || file.fileType.contains("PDF")))
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                file.evidenceName.contains(searchQuery, ignoreCase = true) ||
                file.originalFilename.contains(searchQuery, ignoreCase = true) ||
                file.description.contains(searchQuery, ignoreCase = true) ||
                file.notes.contains(searchQuery, ignoreCase = true) ||
                file.sha256Hash.contains(searchQuery, ignoreCase = true) ||
                file.category.contains(searchQuery, ignoreCase = true)
            }
            matchesCategory && matchesSearch
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row with Title and Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = null,
                            tint = CyberPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ملفات القضية",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            CyberBadge(
                                text = "${activeFiles.size} ملف",
                                accentColor = CyberPrimary
                            )
                        }
                        Text(
                            text = "أرشيف مشفر محلياً أوفلاين مع حساب البصمات الجنائية",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Header Action: Trash Can Button with Counter
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
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Action Button: «+ إضافة ملف»
            Button(
                onClick = { showAddOptionsSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "+ إضافة ملف",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Processing Progress Bar (Streaming Async Non-blocking)
            AnimatedVisibility(visible = isProcessingFile) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp)),
                        color = CyberPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = processingProgressText,
                        color = CyberPrimaryLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar & Filter Row
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("بحث في ملفات القضية بالاسم أو الوصف أو البصمة...", fontSize = 11.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "مسح", tint = TextMuted, modifier = Modifier.size(14.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberPrimary,
                    unfocusedBorderColor = CyberBorder,
                    focusedContainerColor = CyberCardElevated,
                    unfocusedContainerColor = CyberCardElevated
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Chips
            val allFilterCategories = remember(configuredCategories) {
                val list = mutableListOf("الكل")
                configuredCategories.forEach { if (!list.contains(it)) list.add(it) }
                list
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(allFilterCategories) { category ->
                    val count = if (category == "الكل") activeFiles.size else activeFiles.count {
                        it.category.equals(category, ignoreCase = true) ||
                        (category == "صور" && (it.fileType.contains("صورة") || it.mimeType.startsWith("image/")))
                    }
                    FilterChip(
                        selected = selectedCategoryFilter == category,
                        onClick = { selectedCategoryFilter = category },
                        label = {
                            Text(
                                text = "$category ($count)",
                                fontSize = 11.sp,
                                fontWeight = if (selectedCategoryFilter == category) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberPrimary.copy(alpha = 0.25f),
                            selectedLabelColor = CyberPrimaryLight,
                            containerColor = CyberCardElevated,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selectedCategoryFilter == category) CyberPrimary else CyberBorder,
                            enabled = true,
                            selected = selectedCategoryFilter == category
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Files List
            if (filteredFiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCardElevated)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.InsertDriveFile,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (activeFiles.isEmpty()) {
                                "لا توجد ملفات مرفقة بهذه القضية حتى الآن.\nاضغط على «+ إضافة ملف» لإرفاق صور ومستندات وفيديوهات وفحوصات أوفلاين."
                            } else {
                                "لا توجد ملفات تطابق التصنيف أو البحث المحدد."
                            },
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredFiles.forEach { file ->
                        CaseFileItemCard(
                            file = file,
                            onImageThumbnailClick = {
                                if (file.mimeType.startsWith("image/") || file.fileType.contains("صورة")) {
                                    selectedImageForPreview = file
                                } else {
                                    selectedFileForDetails = file
                                }
                            },
                            onOpenClick = { openCaseFile(context, file) },
                            onShareClick = { shareCaseFile(context, file) },
                            onDownloadClick = { downloadOrExportCaseFile(context, file) },
                            onEditClick = { selectedFileForEdit = file },
                            onDetailsClick = { selectedFileForDetails = file },
                            onDeleteClick = { fileToDeleteConfirm = file }
                        )
                    }
                }
            }
        }
    }

    // ==========================================
    // DIALOGS & BOTTOM SHEETS
    // ==========================================

    // Add Options Modal Sheet
    if (showAddOptionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddOptionsSheet = false },
            containerColor = CyberCard,
            dragHandle = { BottomSheetDefaults.DragHandle(color = CyberBorder) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "إضافة ملفات ومستندات إلى القضية",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
                Text(
                    text = "رقم القضية: $caseNumber • التخزين مشفر محلياً أوفلاين",
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Option 1: Documents & All Files (Multiple / Single)
                AddOptionRow(
                    icon = Icons.Default.Description,
                    iconTint = CyberInfo,
                    title = "اختيار مستندات وملفات من الجهاز",
                    subtitle = "PDF، DOC، DOCX، TXT، XLS، XLSX، CSV، ZIP، RAR، صوتيات، فيديوهات (ملف واحد أو عدة ملفات)",
                    onClick = {
                        showAddOptionsSheet = false
                        multipleDocsPickerLauncher.launch(arrayOf("*/*"))
                    }
                )

                HorizontalDivider(color = CyberBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))

                // Option 2: Photos from Device (Multiple / Single)
                AddOptionRow(
                    icon = Icons.Default.PhotoLibrary,
                    iconTint = CyberPrimary,
                    title = "اختيار صور وفيديوهات من المعرض",
                    subtitle = "اختيار صورة واحدة أو عدة صور دفعة واحدة مع استخراج البصمات",
                    onClick = {
                        showAddOptionsSheet = false
                        multiplePhotosPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        )
                    }
                )

                HorizontalDivider(color = CyberBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))

                // Option 3: Camera Capture
                AddOptionRow(
                    icon = Icons.Default.PhotoCamera,
                    iconTint = CyberSuccess,
                    title = "تصوير صورة وإضافتها مباشرة بالكاميرا",
                    subtitle = "التقاط صورة حية فورية وتوثيقها جنائياً وتشفيرها بالقضية",
                    onClick = {
                        showAddOptionsSheet = false
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            launchCameraCapture(
                                context = context,
                                caseId = caseId,
                                onUriReady = { uri, file ->
                                    pendingCameraPhotoUri = uri
                                    pendingCameraPhotoFile = file
                                    cameraLauncher.launch(uri)
                                }
                            )
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                )

                HorizontalDivider(color = CyberBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))

                // Option 4: Manual File Entry
                AddOptionRow(
                    icon = Icons.Default.EditNote,
                    iconTint = CyberWarning,
                    title = "تسجيل يدوي لبيانات ملف / حرز",
                    subtitle = "إدخال الاسم والتصنيف والوصف والبصمة والملاحظات يدوياً",
                    onClick = {
                        showAddOptionsSheet = false
                        showManualAddDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Image Zoom / Preview Dialog
    if (selectedImageForPreview != null) {
        val file = selectedImageForPreview!!
        Dialog(onDismissRequest = { selectedImageForPreview = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(4.dp),
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = file.evidenceName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { selectedImageForPreview = null }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 180.dp, max = 320.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(File(file.localFilePath))
                                .crossfade(true)
                                .build(),
                            contentDescription = file.evidenceName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${file.fileSizeFormatted} • ${file.category}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { openCaseFile(context, file) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("فتح كامل", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { shareCaseFile(context, file) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("مشاركة", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // File Details Dialog
    if (selectedFileForDetails != null) {
        CaseFileDetailsDialog(
            file = selectedFileForDetails!!,
            onDismiss = { selectedFileForDetails = null },
            onShare = { shareCaseFile(context, selectedFileForDetails!!) },
            onOpen = { openCaseFile(context, selectedFileForDetails!!) },
            onDownload = { downloadOrExportCaseFile(context, selectedFileForDetails!!) },
            onEdit = {
                val f = selectedFileForDetails!!
                selectedFileForDetails = null
                selectedFileForEdit = f
            }
        )
    }

    // Edit File Dialog (Rename, Category, Description, Notes)
    if (selectedFileForEdit != null) {
        CaseFileEditDialog(
            file = selectedFileForEdit!!,
            categories = configuredCategories,
            onDismiss = { selectedFileForEdit = null },
            onSave = { newName, newCategory, newDesc, newNotes ->
                viewModel.updateCaseFileMetadata(
                    id = selectedFileForEdit!!.id,
                    newName = newName,
                    newCategory = newCategory,
                    newDescription = newDesc,
                    newNotes = newNotes
                )
                selectedFileForEdit = null
            }
        )
    }

    // Soft Delete Confirmation Dialog
    if (fileToDeleteConfirm != null) {
        val target = fileToDeleteConfirm!!
        AlertDialog(
            onDismissRequest = { fileToDeleteConfirm = null },
            title = { Text("نقل الملف إلى سلة المحذوفات", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "هل ترغب في حذف الملف «${target.evidenceName}» من القضية؟\n\nسيتم نقله إلى سلة محذوفات القضية مع إمكانية استعادته في أي وقت."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.softDeleteCaseFile(target.id, target.evidenceName)
                        fileToDeleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberDanger)
                ) {
                    Text("نقل للسلة")
                }
            },
            dismissButton = {
                TextButton(onClick = { fileToDeleteConfirm = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Trash Dialog (Restore / Permanent Delete)
    if (showTrashDialog) {
        CaseFileTrashDialog(
            trashFiles = trashFiles,
            onDismiss = { showTrashDialog = false },
            onRestore = { file -> viewModel.restoreCaseFile(file.id, file.evidenceName) },
            onPermanentDelete = { file -> viewModel.permanentlyDeleteCaseFile(file.id, file.evidenceName, file.localFilePath) }
        )
    }

    // Manual File Registration Dialog
    if (showManualAddDialog) {
        ManualEvidenceDialog(
            caseId = caseId,
            caseNumber = caseNumber,
            categories = configuredCategories,
            onDismiss = { showManualAddDialog = false },
            onSave = { name, category, type, description, hash, notes ->
                viewModel.attachCaseOfflineFile(
                    caseId = caseId,
                    caseNumber = caseNumber,
                    fileName = name,
                    fileType = type,
                    originalFilename = name,
                    localPath = "",
                    fileSizeBytes = 0L,
                    fileSizeFormatted = "0 B (يدوي)",
                    mimeType = "application/octet-stream",
                    sha256 = hash.ifBlank { "MANUAL-${System.currentTimeMillis()}" },
                    md5 = "",
                    notes = notes,
                    category = category,
                    description = description
                )
                showManualAddDialog = false
            }
        )
    }
}

// ==========================================
// ITEM CARD COMPONENT
// ==========================================

@Composable
private fun CaseFileItemCard(
    file: EvidenceEntity,
    onImageThumbnailClick: () -> Unit,
    onOpenClick: () -> Unit,
    onShareClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onEditClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    val (icon, badgeColor) = getFileTypeMeta(file.fileType, file.originalFilename, file.mimeType)
    val isImage = file.mimeType.startsWith("image/") || file.fileType.contains("صورة")
    val hasLocalFile = file.localFilePath.isNotBlank() && File(file.localFilePath).exists()

    val formattedDate = remember(file.createdDate) {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(file.createdDate))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDetailsClick() },
        colors = CardDefaults.cardColors(containerColor = CyberCardElevated),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side: Thumbnail or Icon
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    if (isImage && hasLocalFile) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black)
                                .clickable { onImageThumbnailClick() }
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(File(file.localFilePath))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = file.evidenceName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(badgeColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Center Details
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = file.evidenceName,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Category & Format Badges
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CyberBadge(
                                text = file.category.ifBlank { "مستندات" },
                                accentColor = badgeColor
                            )
                            Text(
                                text = file.fileSizeFormatted,
                                color = CyberPrimaryLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text("•", color = TextMuted, fontSize = 9.sp)
                            Text(
                                text = formattedDate,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        if (file.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = file.description,
                                color = TextMuted,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (file.sha256Hash.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "SHA256: ${file.sha256Hash.take(12)}...",
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
            }

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = CyberBorder.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(4.dp))

            // Action Row with Icons: Open, Share, Download, Edit, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Storage and sync status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (hasLocalFile) CyberSuccess else CyberWarning)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (hasLocalFile) "تخزين محلي مؤمّن" else "سجل وصفي",
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                }

                // Interactive Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (hasLocalFile) {
                        IconButton(
                            onClick = onOpenClick,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "فتح الملف",
                                tint = CyberPrimaryLight,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(
                            onClick = onShareClick,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "مشاركة الملف",
                                tint = CyberSuccess,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(
                            onClick = onDownloadClick,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "تحميل للتنزيلات",
                                tint = CyberInfo,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل الملف والتصنيف",
                            tint = CyberWarning,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "نقل لسلة المحذوفات",
                            tint = CyberDanger,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// DETAILS DIALOG
// ==========================================

@Composable
private fun CaseFileDetailsDialog(
    file: EvidenceEntity,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onOpen: () -> Unit,
    onDownload: () -> Unit,
    onEdit: () -> Unit
) {
    val context = LocalContext.current
    val hasLocal = file.localFilePath.isNotBlank() && File(file.localFilePath).exists()
    val formattedDate = remember(file.createdDate) {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(file.createdDate))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
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
                    Text("بيانات وتوثيق الملف بالقضية", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = TextSecondary)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = CyberBorder)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    DetailRow("رقم القضية:", file.caseNumber)
                    DetailRow("اسم الملف:", file.evidenceName)
                    DetailRow("الاسم الأصلي للجهاز:", file.originalFilename)
                    DetailRow("تصنيف الملف:", file.category.ifBlank { "مستندات" })
                    DetailRow("النوع التقني:", file.fileType)
                    DetailRow("الحجم:", "${file.fileSizeFormatted} (${file.fileSizeBytes} بايت)")
                    DetailRow("تاريخ ووقت الإضافة:", formattedDate)

                    if (file.description.isNotBlank()) {
                        DetailRow("وصف الملف:", file.description)
                    }

                    if (file.notes.isNotBlank()) {
                        DetailRow("ملاحظات الفاحص:", file.notes)
                    }

                    DetailRow("نوع الوسيط (MIME):", file.mimeType.ifEmpty { "application/octet-stream" })

                    // Full SHA-256 with Copy Button
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("بصمة التحقق الجنائي (SHA-256):", color = TextSecondary, fontSize = 11.sp)
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

                    if (file.md5Hash.isNotBlank()) {
                        DetailRow("بصمة MD5:", file.md5Hash)
                    }

                    if (file.localFilePath.isNotBlank()) {
                        DetailRow("مسار التخزين المحلي:", file.localFilePath)
                    }

                    DetailRow("حالة التوثيق والمزامنة:", file.syncStatus)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (hasLocal) {
                        Button(
                            onClick = {
                                onOpen()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("فتح", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                onShare()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberSuccess),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("مشاركة", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                onDownload()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberInfo),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("تحميل", fontSize = 11.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("تعديل", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// EDIT FILE DIALOG
// ==========================================

@Composable
private fun CaseFileEditDialog(
    file: EvidenceEntity,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (name: String, category: String, desc: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf(file.evidenceName) }
    var selectedCategory by remember { mutableStateOf(file.category.ifBlank { "مستندات" }) }
    var description by remember { mutableStateOf(file.description) }
    var notes by remember { mutableStateOf(file.notes) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCard),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "تعديل بيانات وتصنيف الملف",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الملف *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("تصنيف الملف:", fontSize = 11.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))

                // Scrollable Category Selection Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberPrimary.copy(alpha = 0.25f),
                                selectedLabelColor = CyberPrimaryLight
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("وصف الملف ومحتواه") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات إضافية") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(name.trim(), selectedCategory, description.trim(), notes.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank()
                    ) {
                        Text("حفظ التعديلات")
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

// ==========================================
// TRASH DIALOG
// ==========================================

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
                .fillMaxHeight(0.85f)
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
                        Text(
                            text = "سلة محذوفات القضية (${trashFiles.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
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
                        Text("سلة المحذوفات لهذه القضية فارغة تماماً.", color = TextMuted, fontSize = 12.sp)
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
                                        Text("${file.fileSizeFormatted} • ${file.category} • ${file.fileType}", fontSize = 10.sp, color = TextSecondary)
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
            title = { Text("تأكيد الحذف والتطهير النهائي", fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من الحذف النهائي للملف «${fileToConfirmDelete!!.evidenceName}»؟ سيتم حذف الملف من الذاكرة المحلية ومسحه نهائياً بدون إمكانية التراجع.") },
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

// ==========================================
// MANUAL REGISTRATION DIALOG
// ==========================================

@Composable
private fun ManualEvidenceDialog(
    caseId: String,
    caseNumber: String,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (name: String, category: String, type: String, description: String, hash: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "مستندات") }
    var type by remember { mutableStateOf("مستند فني") }
    var description by remember { mutableStateOf("") }
    var hash by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val evidenceTypes = listOf("صورة فوتوغرافية", "لقطة شاشة", "مستند PDF", "تسجيل صوتي", "مقطع فيديو", "تفريغ محادثة", "تقرير فحص جنائي", "حزمة مضغوطة", "سجل شبكة PCAP")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("تسجيل يدوي لبيانات ملف / حرز بالقضية", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الملف / المرفق *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("تصنيف الملف:", fontSize = 11.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberPrimary.copy(alpha = 0.25f),
                                selectedLabelColor = CyberPrimaryLight
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("نوع الملف:", fontSize = 11.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
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
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("وصف الملف ومحتواه") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

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
                    label = { Text("ملاحظات إضافية") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(name.trim(), selectedCategory, type, description.trim(), hash.trim(), notes.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank()
                    ) {
                        Text("حفظ وتوثيق")
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

// ==========================================
// BOTTOM SHEET HELPER COMPONENT
// ==========================================

@Composable
private fun AddOptionRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = TextSecondary, lineHeight = 14.sp)
        }
        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextMuted)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Text(value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

// ==========================================
// FILE PROCESSING & DISPATCH UTILS
// ==========================================

@Composable
private fun getFileTypeMeta(fileType: String, filename: String, mimeType: String): Pair<ImageVector, Color> {
    val ext = filename.substringAfterLast('.', "").lowercase()
    return when {
        mimeType.startsWith("image/") || ext in listOf("png", "jpg", "jpeg", "webp", "gif", "heic") || fileType.contains("صورة") ->
            Pair(Icons.Default.Image, CyberPrimary)
        mimeType == "application/pdf" || ext == "pdf" || fileType.contains("PDF") ->
            Pair(Icons.Default.PictureAsPdf, CyberDanger)
        ext in listOf("doc", "docx", "rtf", "odt") ->
            Pair(Icons.Default.Description, CyberInfo)
        ext in listOf("xls", "xlsx", "csv") ->
            Pair(Icons.Default.Assessment, CyberSuccess)
        ext in listOf("txt", "log", "json", "xml") || fileType.contains("نص") ->
            Pair(Icons.Default.Article, CyberPrimaryLight)
        mimeType.contains("zip") || mimeType.contains("compressed") || ext in listOf("zip", "rar", "7z", "tar", "gz") ->
            Pair(Icons.Default.FolderZip, CyberSecondary)
        mimeType.startsWith("audio/") || ext in listOf("mp3", "wav", "m4a", "ogg", "amr") || fileType.contains("صوت") ->
            Pair(Icons.Default.AudioFile, CyberWarning)
        mimeType.startsWith("video/") || ext in listOf("mp4", "mkv", "avi", "mov") || fileType.contains("فيديو") ->
            Pair(Icons.Default.VideoFile, CyberPrimaryLight)
        else ->
            Pair(Icons.Default.InsertDriveFile, TextSecondary)
    }
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(bytes / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
}

private fun launchCameraCapture(
    context: Context,
    caseId: String,
    onUriReady: (Uri, File) -> Unit
) {
    try {
        val caseDir = File(context.filesDir, "cases/$caseId")
        if (!caseDir.exists()) caseDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val photoFile = File(caseDir, "IMG_${timeStamp}.jpg")
        val authority = "${context.packageName}.fileprovider"
        val photoUri = FileProvider.getUriForFile(context, authority, photoFile)
        onUriReady(photoUri, photoFile)
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر تجهيز الكاميرا: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

private fun processSavedCameraFile(
    context: Context,
    photoFile: File,
    caseId: String,
    caseNumber: String,
    viewModel: ForensicViewModel,
    category: String
) {
    val md5Digest = MessageDigest.getInstance("MD5")
    val sha256Digest = MessageDigest.getInstance("SHA-256")

    photoFile.inputStream().use { input ->
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            md5Digest.update(buffer, 0, bytesRead)
            sha256Digest.update(buffer, 0, bytesRead)
        }
    }

    val actualSize = photoFile.length()
    val formattedSize = formatBytes(actualSize)
    val md5Hex = md5Digest.digest().joinToString("") { "%02x".format(it) }
    val sha256Hex = sha256Digest.digest().joinToString("") { "%02x".format(it) }

    viewModel.attachCaseOfflineFile(
        caseId = caseId,
        caseNumber = caseNumber,
        fileName = photoFile.name,
        fileType = "صورة ملتقطة",
        originalFilename = photoFile.name,
        localPath = photoFile.absolutePath,
        fileSizeBytes = actualSize,
        fileSizeFormatted = formattedSize,
        mimeType = "image/jpeg",
        sha256 = sha256Hex,
        md5 = md5Hex,
        notes = "تم التقاط الصورة مباشرة بواسطة كاميرا المنظومة وتوثيقها جنائياً.",
        category = category,
        description = "صورة حية موثقة من الكاميرا"
    )
}

private fun processAndSaveOfflineFile(
    context: Context,
    uri: Uri,
    caseId: String,
    caseNumber: String,
    viewModel: ForensicViewModel,
    overrideCategory: String?
) {
    val contentResolver = context.contentResolver

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

    val safeFileName = displayName.replace(Regex("[^a-zA-Z0-9._\\-\\u0600-\\u06FF]"), "_")

    val caseDir = File(context.filesDir, "cases/$caseId")
    if (!caseDir.exists()) {
        caseDir.mkdirs()
    }

    // Unique file target to prevent collision
    val targetFile = File(caseDir, safeFileName)

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

    val ext = displayName.substringAfterLast('.', "").lowercase()
    val detectedCategory = overrideCategory ?: when {
        mimeType.startsWith("image/") || ext in listOf("png", "jpg", "jpeg", "webp", "gif", "heic") -> "صور"
        mimeType == "application/pdf" || ext in listOf("pdf", "doc", "docx", "txt", "rtf", "odt") -> "مستندات"
        ext in listOf("xls", "xlsx", "csv") -> "تقارير"
        mimeType.startsWith("audio/") || mimeType.startsWith("video/") -> "مراسلات"
        mimeType.contains("zip") || mimeType.contains("compressed") || ext in listOf("zip", "rar", "7z") -> "أخرى"
        else -> "مستندات"
    }

    val fileTypeDescription = when {
        mimeType.startsWith("image/") -> "صورة رقمية"
        mimeType.startsWith("audio/") -> "تسجيل صوتي"
        mimeType.startsWith("video/") -> "مقطع فيديو"
        mimeType == "application/pdf" -> "مستند PDF"
        ext in listOf("doc", "docx") -> "مستند Word"
        ext in listOf("xls", "xlsx") -> "جدول بيانات Excel"
        ext == "csv" -> "بيانات CSV"
        ext in listOf("zip", "rar", "7z") -> "حزمة أرشيف مضغوطة"
        else -> "ملف قضية"
    }

    viewModel.attachCaseOfflineFile(
        caseId = caseId,
        caseNumber = caseNumber,
        fileName = safeFileName,
        fileType = fileTypeDescription,
        originalFilename = displayName,
        localPath = targetFile.absolutePath,
        fileSizeBytes = actualSize,
        fileSizeFormatted = formattedSize,
        mimeType = mimeType,
        sha256 = sha256Hex,
        md5 = md5Hex,
        notes = "تم حفظ الملف محلياً واستخراج بصمة SHA-256 الرقمية.",
        category = detectedCategory,
        description = "ملف مرفق بالقضية ($displayName)"
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
            putExtra(
                Intent.EXTRA_TEXT,
                "ملف رسمي لقضية [${file.caseNumber}]\nالاسم: ${file.evidenceName}\nالتصنيف: ${file.category}\nSHA256: ${file.sha256Hash}"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "مشاركة ملف القضية")
        context.startActivity(chooser)
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر مشاركة الملف: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}

private fun downloadOrExportCaseFile(context: Context, file: EvidenceEntity) {
    try {
        val srcFile = File(file.localFilePath)
        if (!srcFile.exists()) {
            Toast.makeText(context, "الملف غير موجود في الذاكرة المحلية", Toast.LENGTH_SHORT).show()
            return
        }

        val displayName = file.evidenceName.ifBlank { file.originalFilename.ifBlank { srcFile.name } }
        val mime = file.mimeType.ifBlank { "application/octet-stream" }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, mime)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/JaffarForensics")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { output ->
                    srcFile.inputStream().use { input ->
                        input.copyTo(output)
                    }
                }
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
                Toast.makeText(context, "تم حفظ الملف بنجاح في مجلد التنزيلات (Downloads/JaffarForensics)", Toast.LENGTH_LONG).show()
            } else {
                shareCaseFile(context, file)
            }
        } else {
            val targetDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "JaffarForensics")
            if (!targetDir.exists()) targetDir.mkdirs()
            val targetFile = File(targetDir, displayName)
            srcFile.copyTo(targetFile, overwrite = true)
            Toast.makeText(context, "تم حفظ الملف في التنزيلات: ${targetFile.name}", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر التصدير المباشر، جاري فتح المشاركة...", Toast.LENGTH_SHORT).show()
        shareCaseFile(context, file)
    }
}
