package com.example.ui.screens.cases.creation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.entities.CaseEntity
import com.example.ui.components.PrivacyMaskText
import com.example.ui.components.PrivacyMaskToggle
import com.example.ui.theme.*
import com.example.ui.viewmodel.ForensicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrEditCaseDialog(
    existingCase: CaseEntity? = null,
    viewModel: ForensicViewModel,
    onDismiss: () -> Unit,
    onCaseSaved: (CaseEntity) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isEditing = existingCase != null
    val isPrivacyMasked by viewModel.isPrivacyMasked.collectAsStateWithLifecycle()

    // 1. Auto-generated or existing internal case number
    val internalCaseNumber = remember {
        existingCase?.caseNumber ?: run {
            val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val rand = (100000..999999).random()
            "JB-$year-$rand"
        }
    }

    val caseId = remember {
        existingCase?.id ?: "case_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
    }

    // 2. Core Form State (ALL OPTIONAL)
    var formTitle by remember { mutableStateOf(existingCase?.title ?: "") }
    var formClientName by remember { mutableStateOf(existingCase?.clientName ?: "") }
    var formClientPhone by remember { mutableStateOf(existingCase?.clientPhone ?: "") }
    var formClientEmail by remember { mutableStateOf(existingCase?.clientEmail ?: "") }
    var formThreatType by remember { mutableStateOf(existingCase?.threatType ?: "ابتزاز إلكتروني") }
    var formPriority by remember { mutableStateOf(existingCase?.priority ?: "متوسطة") }
    var formStatus by remember { mutableStateOf(existingCase?.status ?: "جديدة") }
    var formInvestigator by remember { mutableStateOf(existingCase?.assignedInvestigator ?: "جعفر بدران") }
    var formDescription by remember { mutableStateOf(existingCase?.description ?: "") }
    var formNotes by remember { mutableStateOf(existingCase?.notes ?: "") }
    var formTotalPrice by remember { mutableStateOf(if (existingCase != null && existingCase.totalAmount > 0) existingCase.totalAmount.toString() else "") }
    var formCurrency by remember { mutableStateOf(existingCase?.currency ?: "SAR") }

    // 3. Links, Groups & Identifiers State
    var customLinks by remember {
        mutableStateOf(
            if (existingCase != null) DraftCustomLink.listFromJsonString(existingCase.customLinksJson)
            else emptyList()
        )
    }

    var customIdentifiers by remember {
        mutableStateOf(
            if (existingCase != null) DraftIdentifier.listFromJsonString(existingCase.customIdentifiersJson)
            else emptyList()
        )
    }

    // 4. Internal Case Email (Optional)
    var internalCaseEmail by remember { mutableStateOf(existingCase?.internalCaseEmail ?: "") }
    var showInternalEmailField by remember { mutableStateOf(existingCase?.internalCaseEmail?.isNotBlank() == true) }

    // 5. Draft Images State
    var draftImages by remember { mutableStateOf<List<DraftCaseImage>>(emptyList()) }

    // 6. Accordion Section Expanded States
    var sectionCaseInfoOpen by remember { mutableStateOf(true) }
    var sectionClientOpen by remember { mutableStateOf(false) }
    var sectionLinksOpen by remember { mutableStateOf(false) }
    var sectionInternalEmailOpen by remember { mutableStateOf(showInternalEmailField) }
    var sectionImagesOpen by remember { mutableStateOf(false) }
    var sectionDetailsOpen by remember { mutableStateOf(false) }

    // Sub-dialogs state
    var showAddLinkDialog by remember { mutableStateOf(false) }
    var editingLinkTarget by remember { mutableStateOf<DraftCustomLink?>(null) }
    var defaultGroupForNewLink by remember { mutableStateOf("") }

    var showAddGroupDialog by remember { mutableStateOf(false) }
    var editingGroupNameTarget by remember { mutableStateOf<String?>(null) }

    var showAddIdentifierDialog by remember { mutableStateOf(false) }
    var editingIdentifierTarget by remember { mutableStateOf<DraftIdentifier?>(null) }

    var previewingImage by remember { mutableStateOf<DraftCaseImage?>(null) }
    var editingImageTarget by remember { mutableStateOf<DraftCaseImage?>(null) }
    var renamingImageTarget by remember { mutableStateOf<DraftCaseImage?>(null) }
    var replacingImageTargetIndex by remember { mutableIntStateOf(-1) }

    var isSaving by remember { mutableStateOf(false) }

    // Function to copy URI to draft cache
    fun importUriToDraft(uri: Uri): DraftCaseImage? {
        return try {
            val contentResolver = context.contentResolver
            var displayName = "IMG_${System.currentTimeMillis()}.jpg"
            var reportedSize = 0L

            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (nameIdx != -1) cursor.getString(nameIdx)?.let { displayName = it }
                    if (sizeIdx != -1 && !cursor.isNull(sizeIdx)) reportedSize = cursor.getLong(sizeIdx)
                }
            }

            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val draftDir = File(context.cacheDir, "draft_cases")
            if (!draftDir.exists()) draftDir.mkdirs()

            val safeName = displayName.replace(Regex("[^a-zA-Z0-9._\\-\\u0600-\\u06FF]"), "_")
            val targetFile = File(draftDir, "draft_${System.currentTimeMillis()}_$safeName")

            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }

            val actualSize = if (targetFile.exists()) targetFile.length() else reportedSize
            val formatted = formatBytesHelper(actualSize)

            // Extract width/height
            val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(targetFile.absolutePath, opts)

            DraftCaseImage(
                id = UUID.randomUUID().toString(),
                uri = Uri.fromFile(targetFile),
                localFile = targetFile,
                originalFileName = displayName,
                displayName = displayName,
                fileSizeFormatted = formatted,
                fileSizeBytes = actualSize,
                mimeType = mimeType,
                width = opts.outWidth,
                height = opts.outHeight
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Photo pickers
    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch(Dispatchers.IO) {
                val draft = importUriToDraft(uri)
                if (draft != null) {
                    withContext(Dispatchers.Main) {
                        draftImages = draftImages + draft
                        sectionImagesOpen = true
                        Toast.makeText(context, "تمت إضافة الصورة بنجاح", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    val multiplePhotosPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (!uris.isNullOrEmpty()) {
            coroutineScope.launch(Dispatchers.IO) {
                val newDrafts = uris.mapNotNull { importUriToDraft(it) }
                withContext(Dispatchers.Main) {
                    draftImages = draftImages + newDrafts
                    sectionImagesOpen = true
                    Toast.makeText(context, "تمت إضافة ${newDrafts.size} صور بنجاح", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val replacePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && replacingImageTargetIndex in draftImages.indices) {
            coroutineScope.launch(Dispatchers.IO) {
                val draft = importUriToDraft(uri)
                if (draft != null) {
                    withContext(Dispatchers.Main) {
                        val updated = draftImages.toMutableList()
                        updated[replacingImageTargetIndex] = draft
                        draftImages = updated
                        replacingImageTargetIndex = -1
                        Toast.makeText(context, "تم استبدال الصورة بنجاح", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Direct Image Download / Export to Device Pictures
    fun exportImageDirectly(img: DraftCaseImage) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "Case_${internalCaseNumber}_${img.displayName}")
                    put(MediaStore.Images.Media.MIME_TYPE, img.mimeType)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Cases")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }

                val resolver = context.contentResolver
                val targetUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                if (targetUri != null) {
                    img.localFile.inputStream().use { input ->
                        resolver.openOutputStream(targetUri)?.use { output ->
                            input.copyTo(output)
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        resolver.update(targetUri, values, null, null)
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "تم حفظ وتنزيل الصورة في مجلد الصور بالجهاز بنجاح", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "فشل حفظ الصورة على الجهاز: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Image Share Helper
    fun shareImage(img: DraftCaseImage) {
        try {
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                img.localFile
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = img.mimeType
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "مشاركة الصورة"))
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر مشاركة الصورة: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    // Submit handler - NO MANDATORY FIELDS
    fun submitCase() {
        isSaving = true
        val resolvedTitle = formTitle.trim().ifBlank {
            if (formClientName.isNotBlank()) "قضية $formClientName ($internalCaseNumber)"
            else "قضية $internalCaseNumber"
        }
        val resolvedClientName = formClientName.trim().ifBlank { "عميل غير محدد" }
        val price = formTotalPrice.toDoubleOrNull() ?: 0.0

        val entity = existingCase?.copy(
            title = resolvedTitle,
            clientName = resolvedClientName,
            clientPhone = formClientPhone.trim(),
            clientEmail = formClientEmail.trim(),
            threatType = formThreatType,
            priority = formPriority,
            status = formStatus,
            assignedInvestigator = formInvestigator,
            description = formDescription.trim(),
            notes = formNotes.trim(),
            totalAmount = price,
            currency = formCurrency
        ) ?: CaseEntity(
            id = caseId,
            caseNumber = internalCaseNumber,
            title = resolvedTitle,
            clientName = resolvedClientName,
            clientPhone = formClientPhone.trim(),
            clientEmail = formClientEmail.trim(),
            threatType = formThreatType,
            priority = formPriority,
            status = formStatus,
            assignedInvestigator = formInvestigator,
            description = formDescription.trim(),
            notes = formNotes.trim(),
            totalAmount = price,
            currency = formCurrency,
            createdDate = System.currentTimeMillis()
        )

        viewModel.saveCaseWithFullMetadata(
            caseEntity = entity,
            draftImages = draftImages,
            customLinks = customLinks,
            customIdentifiers = customIdentifiers,
            internalCaseEmail = if (showInternalEmailField) internalCaseEmail.trim() else "",
            isNew = !isEditing,
            context = context,
            onSuccess = { saved ->
                isSaving = false
                onCaseSaved(saved)
                onDismiss()
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .imePadding()
                .testTag("create_case_dialog"),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Sticky Header
                Surface(
                    color = CyberCardElevated,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(CyberPrimary.copy(alpha = 0.2f))
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.AddModerator,
                                        contentDescription = null,
                                        tint = CyberPrimaryLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = if (isEditing) "تعديل القضية" else "إنشاء قضية جديدة",
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "الرقم الداخلي: $internalCaseNumber (غير إجباري البيانات)",
                                        color = CyberPrimaryLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                PrivacyMaskToggle(
                                    isMasked = isPrivacyMasked,
                                    onToggle = { viewModel.togglePrivacyMasking() }
                                )
                                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "إلغاء", tint = TextMuted)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Actions Bar
                        Text("إضافة سريعة مباشرة:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ActionChip(
                                label = "+ رابط",
                                icon = Icons.Default.Link,
                                onClick = {
                                    editingLinkTarget = null
                                    defaultGroupForNewLink = ""
                                    showAddLinkDialog = true
                                }
                            )
                            ActionChip(
                                label = "+ مجموعة روابط",
                                icon = Icons.Default.Folder,
                                onClick = {
                                    editingGroupNameTarget = null
                                    showAddGroupDialog = true
                                }
                            )
                            ActionChip(
                                label = "+ معرف",
                                icon = Icons.Default.Badge,
                                onClick = {
                                    editingIdentifierTarget = null
                                    showAddIdentifierDialog = true
                                }
                            )
                            ActionChip(
                                label = "+ بريد داخلي",
                                icon = Icons.Default.MailOutline,
                                onClick = {
                                    showInternalEmailField = true
                                    sectionInternalEmailOpen = true
                                }
                            )
                            ActionChip(
                                label = "+ صورة",
                                icon = Icons.Default.Image,
                                onClick = {
                                    singlePhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                            )
                            ActionChip(
                                label = "+ صور متعددة",
                                icon = Icons.Default.Collections,
                                onClick = {
                                    multiplePhotosPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = CyberBorder.copy(alpha = 0.4f), thickness = 1.dp)

                // 2. Scrollable Body Sections
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // SECTION 1: Case Core Data
                    item {
                        SectionContainer(
                            title = "بيانات القضية الأساسية",
                            icon = Icons.Default.FolderSpecial,
                            isOpen = sectionCaseInfoOpen,
                            onToggle = { sectionCaseInfoOpen = !sectionCaseInfoOpen }
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = formTitle,
                                    onValueChange = { formTitle = it },
                                    label = { Text("عنوان القضية (اختياري)", fontSize = 12.sp) },
                                    placeholder = { Text("مثال: ابتزاز إلكتروني عبر واتساب", fontSize = 12.sp) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("case_title_input"),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyberPrimary,
                                        unfocusedBorderColor = CyberBorder
                                    )
                                )

                                OutlinedTextField(
                                    value = formThreatType,
                                    onValueChange = { formThreatType = it },
                                    label = { Text("نوع القضية / التهديد (اختياري)", fontSize = 12.sp) },
                                    placeholder = { Text("مثال: اختراق، انتحال، تشهير", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyberPrimary,
                                        unfocusedBorderColor = CyberBorder
                                    )
                                )

                                Column {
                                    Text("الأولوية:", color = TextSecondary, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("حرجة", "عالية", "متوسطة", "منخفضة").forEach { prio ->
                                            FilterChip(
                                                selected = formPriority == prio,
                                                onClick = { formPriority = prio },
                                                label = { Text(prio, fontSize = 11.sp) }
                                            )
                                        }
                                    }
                                }

                                Column {
                                    Text("الحالة:", color = TextSecondary, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("جديدة", "قيد المراجعة", "قيد المتابعة", "مكتملة").forEach { st ->
                                            FilterChip(
                                                selected = formStatus == st,
                                                onClick = { formStatus = st },
                                                label = { Text(st, fontSize = 11.sp) }
                                            )
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = formInvestigator,
                                    onValueChange = { formInvestigator = it },
                                    label = { Text("المحقق المسؤول (اختياري)", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    // SECTION 2: Client Data
                    item {
                        SectionContainer(
                            title = "بيانات العميل",
                            icon = Icons.Default.Person,
                            isOpen = sectionClientOpen,
                            badge = if (formClientName.isNotBlank()) "مدخل" else null,
                            onToggle = { sectionClientOpen = !sectionClientOpen }
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = formClientName,
                                    onValueChange = { formClientName = it },
                                    label = { Text("اسم العميل (اختياري)", fontSize = 12.sp) },
                                    placeholder = { Text("الاسم الكامل للعميل أو صاحب الطلب", fontSize = 12.sp) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("client_name_input"),
                                    visualTransformation = if (isPrivacyMasked && formClientName.isNotEmpty()) PasswordVisualTransformation('•') else VisualTransformation.None,
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = formClientPhone,
                                    onValueChange = { formClientPhone = it },
                                    label = { Text("رقم الهاتف (اختياري)", fontSize = 12.sp) },
                                    placeholder = { Text("+966...", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    visualTransformation = if (isPrivacyMasked && formClientPhone.isNotEmpty()) PasswordVisualTransformation('•') else VisualTransformation.None,
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = formClientEmail,
                                    onValueChange = { formClientEmail = it },
                                    label = { Text("البريد الإلكتروني للعميل (اختياري)", fontSize = 12.sp) },
                                    placeholder = { Text("client@example.com", fontSize = 12.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    visualTransformation = if (isPrivacyMasked && formClientEmail.isNotEmpty()) PasswordVisualTransformation('•') else VisualTransformation.None,
                                    singleLine = true
                                )
                            }
                        }
                    }

                    // SECTION 3: Links, Link Groups & Identifiers
                    item {
                        val totalLinksCount = customLinks.size
                        val totalIdsCount = customIdentifiers.size
                        val badgeText = if (totalLinksCount + totalIdsCount > 0) "$totalLinksCount روابط / $totalIdsCount معرفات" else null

                        SectionContainer(
                            title = "الروابط والمعرفات",
                            icon = Icons.Default.Share,
                            isOpen = sectionLinksOpen,
                            badge = badgeText,
                            onToggle = { sectionLinksOpen = !sectionLinksOpen }
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                // Buttons Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            editingLinkTarget = null
                                            defaultGroupForNewLink = ""
                                            showAddLinkDialog = true
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.AddLink, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ رابط فردي", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            editingGroupNameTarget = null
                                            showAddGroupDialog = true
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.CreateNewFolder, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ مجموعة روابط", fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            editingIdentifierTarget = null
                                            showAddIdentifierDialog = true
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ معرف", fontSize = 11.sp)
                                    }
                                }

                                // 3A. Standalone Links (not in group)
                                val standaloneLinks = customLinks.filter { it.groupName.isBlank() }
                                if (standaloneLinks.isNotEmpty()) {
                                    Text("روابط فردية مستقلة (${standaloneLinks.size}):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        standaloneLinks.forEach { link ->
                                            LinkCardItem(
                                                link = link,
                                                isMasked = isPrivacyMasked,
                                                onOpen = { openUrlInBrowser(context, link.url) },
                                                onCopy = { copyToClipboard(context, link.url, "تم نسخ الرابط") },
                                                onEdit = {
                                                    editingLinkTarget = link
                                                    showAddLinkDialog = true
                                                },
                                                onDelete = {
                                                    customLinks = customLinks.filter { it.id != link.id }
                                                }
                                            )
                                        }
                                    }
                                }

                                // 3B. Link Groups
                                val groupedLinks = customLinks.filter { it.groupName.isNotBlank() }.groupBy { it.groupName }
                                if (groupedLinks.isNotEmpty()) {
                                    Text("مجموعات الروابط (${groupedLinks.size}):", color = CyberPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    groupedLinks.forEach { (groupName, linksInGroup) ->
                                        LinkGroupContainer(
                                            groupName = groupName,
                                            links = linksInGroup,
                                            isMasked = isPrivacyMasked,
                                            onAddLinkToGroup = {
                                                editingLinkTarget = null
                                                defaultGroupForNewLink = groupName
                                                showAddLinkDialog = true
                                            },
                                            onEditGroupName = {
                                                editingGroupNameTarget = groupName
                                                showAddGroupDialog = true
                                            },
                                            onDeleteGroup = {
                                                customLinks = customLinks.filter { it.groupName != groupName }
                                            },
                                            onOpenLink = { openUrlInBrowser(context, it.url) },
                                            onCopyLink = { copyToClipboard(context, it.url, "تم نسخ الرابط") },
                                            onEditLink = { link ->
                                                editingLinkTarget = link
                                                defaultGroupForNewLink = groupName
                                                showAddLinkDialog = true
                                            },
                                            onDeleteLink = { link ->
                                                customLinks = customLinks.filter { it.id != link.id }
                                            }
                                        )
                                    }
                                }

                                // 3C. Custom Identifiers
                                if (customIdentifiers.isNotEmpty()) {
                                    Text("المعرفات المنظمة (${customIdentifiers.size}):", color = CyberSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        customIdentifiers.forEach { idItem ->
                                            IdentifierCardItem(
                                                item = idItem,
                                                isMasked = isPrivacyMasked,
                                                onCopy = { copyToClipboard(context, idItem.value, "تم نسخ المعرف") },
                                                onEdit = {
                                                    editingIdentifierTarget = idItem
                                                    showAddIdentifierDialog = true
                                                },
                                                onDelete = {
                                                    customIdentifiers = customIdentifiers.filter { it.id != idItem.id }
                                                }
                                            )
                                        }
                                    }
                                }

                                if (customLinks.isEmpty() && customIdentifiers.isEmpty()) {
                                    Text(
                                        text = "لا توجد روابط أو معرفات مضافة بعد. يمكنك استخدام الأزرار أعلاه للإضافة.",
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // SECTION 4: Internal Case Email
                    item {
                        SectionContainer(
                            title = "البريد الداخلي للقضية",
                            icon = Icons.Default.Email,
                            isOpen = sectionInternalEmailOpen,
                            badge = if (internalCaseEmail.isNotBlank() && showInternalEmailField) "مسجل" else null,
                            onToggle = { sectionInternalEmailOpen = !sectionInternalEmailOpen }
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (!showInternalEmailField) {
                                    Button(
                                        onClick = { showInternalEmailField = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary.copy(alpha = 0.2f)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = CyberPrimaryLight)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("+ إضافة بريد داخلي للقضية", color = CyberPrimaryLight, fontSize = 12.sp)
                                    }
                                } else {
                                    OutlinedTextField(
                                        value = internalCaseEmail,
                                        onValueChange = { internalCaseEmail = it },
                                        label = { Text("البريد الداخلي (اختياري - تدخله يدوياً)", fontSize = 12.sp) },
                                        placeholder = { Text("case-$internalCaseNumber@internal.gov", fontSize = 12.sp) },
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        visualTransformation = if (isPrivacyMasked && internalCaseEmail.isNotEmpty()) PasswordVisualTransformation('•') else VisualTransformation.None,
                                        singleLine = true
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (internalCaseEmail.isNotBlank()) {
                                            TextButton(
                                                onClick = { copyToClipboard(context, internalCaseEmail, "تم نسخ البريد الداخلي") },
                                                contentPadding = PaddingValues(horizontal = 8.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(14.dp), tint = CyberPrimaryLight)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("نسخ", fontSize = 11.sp, color = CyberPrimaryLight)
                                            }
                                        }

                                        TextButton(
                                            onClick = {
                                                internalCaseEmail = ""
                                                showInternalEmailField = false
                                            },
                                            contentPadding = PaddingValues(horizontal = 8.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "حذف", modifier = Modifier.size(14.dp), tint = CyberError)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("حذف البريد", fontSize = 11.sp, color = CyberError)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 5: Images and Attachments
                    item {
                        SectionContainer(
                            title = "الصور والمرفقات",
                            icon = Icons.Default.Image,
                            isOpen = sectionImagesOpen,
                            badge = if (draftImages.isNotEmpty()) "${draftImages.size} صور" else null,
                            onToggle = { sectionImagesOpen = !sectionImagesOpen }
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Buttons for photo picker
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            singlePhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ إضافة صورة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            multiplePhotosPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ صور متعددة", fontSize = 11.sp)
                                    }
                                }

                                if (draftImages.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CyberCardElevated.copy(alpha = 0.5f))
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.PhotoCameraBack, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("الصور اختيارية تماماً. يمكنك إنشاء القضية بدون أي صور أو إرفاقها لاحقاً.", color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center)
                                        }
                                    }
                                } else {
                                    Text("المرفقات المحددة (${draftImages.size}):", color = CyberPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        draftImages.forEachIndexed { index, img ->
                                            DraftImageItemCard(
                                                image = img,
                                                isMasked = isPrivacyMasked,
                                                onPreview = { previewingImage = img },
                                                onDownload = { exportImageDirectly(img) },
                                                onEdit = { editingImageTarget = img },
                                                onRename = { renamingImageTarget = img },
                                                onReplace = {
                                                    replacingImageTargetIndex = index
                                                    replacePhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                                },
                                                onShare = { shareImage(img) },
                                                onDelete = {
                                                    draftImages = draftImages.filterIndexed { i, _ -> i != index }
                                                    Toast.makeText(context, "تمت إزالة الصورة من المرفقات", Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // SECTION 6: Details, Notes & Financial
                    item {
                        SectionContainer(
                            title = "التفاصيل والملاحظات والمالية",
                            icon = Icons.Default.Notes,
                            isOpen = sectionDetailsOpen,
                            onToggle = { sectionDetailsOpen = !sectionDetailsOpen }
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = formDescription,
                                    onValueChange = { formDescription = it },
                                    label = { Text("وصف وتفاصيل القضية (اختياري)", fontSize = 12.sp) },
                                    placeholder = { Text("ملخص الوقائع أو تفاصيل التهديد...", fontSize = 12.sp) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp),
                                    visualTransformation = if (isPrivacyMasked && formDescription.isNotEmpty()) PasswordVisualTransformation('•') else VisualTransformation.None,
                                    maxLines = 4
                                )

                                OutlinedTextField(
                                    value = formNotes,
                                    onValueChange = { formNotes = it },
                                    label = { Text("ملاحظات إضافية وسرية (اختياري)", fontSize = 12.sp) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    visualTransformation = if (isPrivacyMasked && formNotes.isNotEmpty()) PasswordVisualTransformation('•') else VisualTransformation.None,
                                    maxLines = 3
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = formTotalPrice,
                                        onValueChange = { formTotalPrice = it },
                                        label = { Text("القيمة الإجمالية (اختياري)", fontSize = 12.sp) },
                                        placeholder = { Text("0.0", fontSize = 12.sp) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1.5f),
                                        visualTransformation = if (isPrivacyMasked && formTotalPrice.isNotEmpty()) PasswordVisualTransformation('•') else VisualTransformation.None,
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = formCurrency,
                                        onValueChange = { formCurrency = it },
                                        label = { Text("العملة", fontSize = 12.sp) },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = CyberBorder.copy(alpha = 0.4f), thickness = 1.dp)

                // 3. Sticky Bottom Action Buttons
                Surface(
                    color = CyberCardElevated,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("إلغاء", fontSize = 13.sp)
                        }

                        Button(
                            onClick = { submitCase() },
                            enabled = !isSaving,
                            modifier = Modifier
                                .weight(2f)
                                .testTag("submit_case_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                val countInfo = buildString {
                                    if (draftImages.isNotEmpty() || customLinks.isNotEmpty()) {
                                        append(" (")
                                        if (draftImages.isNotEmpty()) append("${draftImages.size} صور")
                                        if (draftImages.isNotEmpty() && customLinks.isNotEmpty()) append("، ")
                                        if (customLinks.isNotEmpty()) append("${customLinks.size} روابط")
                                        append(")")
                                    }
                                }
                                Text(
                                    text = if (isEditing) "حفظ التعديلات$countInfo" else "إنشاء القضية$countInfo",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // SUB-DIALOGS
    // ==========================================

    // 1. Add / Edit Link Dialog
    if (showAddLinkDialog) {
        AddEditLinkDialog(
            existing = editingLinkTarget,
            initialGroupName = defaultGroupForNewLink,
            isMasked = isPrivacyMasked,
            onDismiss = { showAddLinkDialog = false },
            onSave = { link ->
                val list = customLinks.toMutableList()
                val idx = list.indexOfFirst { it.id == link.id }
                if (idx != -1) {
                    list[idx] = link
                } else {
                    list.add(link)
                }
                customLinks = list
                sectionLinksOpen = true
                showAddLinkDialog = false
            }
        )
    }

    // 2. Add / Edit Link Group Dialog
    if (showAddGroupDialog) {
        AddEditGroupDialog(
            existingGroupName = editingGroupNameTarget,
            onDismiss = { showAddGroupDialog = false },
            onSave = { newName ->
                if (editingGroupNameTarget != null) {
                    // Rename all links in this group
                    customLinks = customLinks.map {
                        if (it.groupName == editingGroupNameTarget) it.copy(groupName = newName) else it
                    }
                } else {
                    // Create group with empty link or placeholder
                    // Group is created, open Add Link for it
                    defaultGroupForNewLink = newName
                    editingLinkTarget = null
                    showAddLinkDialog = true
                }
                sectionLinksOpen = true
                showAddGroupDialog = false
            }
        )
    }

    // 3. Add / Edit Identifier Dialog
    if (showAddIdentifierDialog) {
        AddEditIdentifierDialog(
            existing = editingIdentifierTarget,
            isMasked = isPrivacyMasked,
            onDismiss = { showAddIdentifierDialog = false },
            onSave = { item ->
                val list = customIdentifiers.toMutableList()
                val idx = list.indexOfFirst { it.id == item.id }
                if (idx != -1) {
                    list[idx] = item
                } else {
                    list.add(item)
                }
                customIdentifiers = list
                sectionLinksOpen = true
                showAddIdentifierDialog = false
            }
        )
    }

    // 4. Full-screen Image Preview Dialog
    if (previewingImage != null) {
        FullScreenImagePreviewDialog(
            image = previewingImage!!,
            onDismiss = { previewingImage = null },
            onDownload = { exportImageDirectly(previewingImage!!) },
            onShare = { shareImage(previewingImage!!) }
        )
    }

    // 5. Image Editor Dialog (Crop, Rotate, Resize)
    if (editingImageTarget != null) {
        ImageEditorDialog(
            image = editingImageTarget!!,
            onDismiss = { editingImageTarget = null },
            onSaveAsNew = { newImg ->
                draftImages = draftImages + newImg
                editingImageTarget = null
            },
            onReplaceOriginal = { replacedImg ->
                draftImages = draftImages.map { if (it.id == replacedImg.id) replacedImg else it }
                editingImageTarget = null
            }
        )
    }

    // 6. Rename Image Dialog
    if (renamingImageTarget != null) {
        RenameImageDialog(
            currentName = renamingImageTarget!!.displayName,
            onDismiss = { renamingImageTarget = null },
            onSave = { newName ->
                val target = renamingImageTarget!!
                draftImages = draftImages.map {
                    if (it.id == target.id) it.copy(displayName = newName) else it
                }
                renamingImageTarget = null
                Toast.makeText(context, "تمت إعادة تسمية الصورة", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ==========================================
// COMPONENT HELPERS
// ==========================================

@Composable
private fun ActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = CyberCardElevated,
        border = BorderStroke(1.dp, CyberBorder.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = CyberPrimaryLight, modifier = Modifier.size(14.dp))
            Text(label, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun SectionContainer(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isOpen: Boolean,
    badge: String? = null,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberCardElevated),
        border = BorderStroke(1.dp, if (isOpen) CyberPrimary.copy(alpha = 0.4f) else CyberBorder.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(icon, contentDescription = null, tint = if (isOpen) CyberPrimaryLight else TextSecondary, modifier = Modifier.size(18.dp))
                    Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    if (badge != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberPrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(badge, color = CyberPrimaryLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Icon(
                    imageVector = if (isOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextMuted
                )
            }

            AnimatedVisibility(visible = isOpen) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun LinkCardItem(
    link: DraftCustomLink,
    isMasked: Boolean = false,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberBorder.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                PrivacyMaskText(
                    text = link.title.ifBlank { link.url },
                    isMasked = isMasked,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                PrivacyMaskText(
                    text = link.url,
                    isMasked = isMasked,
                    color = CyberPrimaryLight,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpen, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = "فتح", tint = CyberSecondary, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = CyberPrimaryLight, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberError, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun LinkGroupContainer(
    groupName: String,
    links: List<DraftCustomLink>,
    isMasked: Boolean = false,
    onAddLinkToGroup: () -> Unit,
    onEditGroupName: () -> Unit,
    onDeleteGroup: () -> Unit,
    onOpenLink: (DraftCustomLink) -> Unit,
    onCopyLink: (DraftCustomLink) -> Unit,
    onEditLink: (DraftCustomLink) -> Unit,
    onDeleteLink: (DraftCustomLink) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CyberCardElevated.copy(alpha = 0.8f)),
        border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Group Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Folder, contentDescription = null, tint = CyberPrimaryLight, modifier = Modifier.size(16.dp))
                    Text(groupName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text("${links.size} روابط", color = CyberPrimaryLight, fontSize = 9.sp)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(onClick = onAddLinkToGroup, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة رابط", tint = CyberSuccess, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onEditGroupName, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل الاسم", tint = TextSecondary, modifier = Modifier.size(14.dp))
                    }
                    IconButton(onClick = onDeleteGroup, modifier = Modifier.size(26.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف المجموعة", tint = CyberError, modifier = Modifier.size(14.dp))
                    }
                }
            }

            // Links inside Group
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                links.forEach { link ->
                    LinkCardItem(
                        link = link,
                        isMasked = isMasked,
                        onOpen = { onOpenLink(link) },
                        onCopy = { onCopyLink(link) },
                        onEdit = { onEditLink(link) },
                        onDelete = { onDeleteLink(link) }
                    )
                }
            }
        }
    }
}

@Composable
private fun IdentifierCardItem(
    item: DraftIdentifier,
    isMasked: Boolean = false,
    onCopy: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberBorder.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberSecondary.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(item.type, color = CyberSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(2.dp))
                PrivacyMaskText(
                    text = item.value,
                    isMasked = isMasked,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = CyberPrimaryLight, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberError, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DraftImageItemCard(
    image: DraftCaseImage,
    isMasked: Boolean = false,
    onPreview: () -> Unit,
    onDownload: () -> Unit,
    onEdit: () -> Unit,
    onRename: () -> Unit,
    onReplace: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = BorderStroke(1.dp, CyberBorder.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Thumbnail with click-to-preview
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { onPreview() }
            ) {
                AsyncImage(
                    model = image.localFile,
                    contentDescription = image.displayName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                PrivacyMaskText(
                    text = image.displayName,
                    isMasked = isMasked,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${image.fileSizeFormatted}${if (image.width > 0) " • ${image.width}×${image.height}" else ""}",
                    color = TextMuted,
                    fontSize = 10.sp
                )
                if (image.isEdited) {
                    Text("تم تعديلها", color = CyberWarning, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Quick Actions: Download, Edit, and Overflow Menu
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                IconButton(onClick = onDownload, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Download, contentDescription = "تحميل فوراً", tint = CyberSuccess, modifier = Modifier.size(16.dp))
                }

                IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Tune, contentDescription = "تعديل", tint = CyberPrimaryLight, modifier = Modifier.size(16.dp))
                }

                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "خيارات إضافية", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("معاينة بالحجم الكامل", fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onPreview()
                            },
                            leadingIcon = { Icon(Icons.Default.Fullscreen, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("تحميل/حفظ على الجهاز", fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onDownload()
                            },
                            leadingIcon = { Icon(Icons.Default.Download, contentDescription = null, tint = CyberSuccess, modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("تعديل (قص / تدوير / حجم)", fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, tint = CyberPrimaryLight, modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("إعادة تسمية الصورة", fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onRename()
                            },
                            leadingIcon = { Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("استبدال بصورة أخرى", fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onReplace()
                            },
                            leadingIcon = { Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("مشاركة الصورة", fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onShare()
                            },
                            leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("حذف من المرفقات", fontSize = 12.sp, color = CyberError) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = CyberError, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SUB-DIALOG IMPLEMENTATIONS
// ==========================================

@Composable
private fun AddEditLinkDialog(
    existing: DraftCustomLink?,
    initialGroupName: String = "",
    isMasked: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (DraftCustomLink) -> Unit
) {
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var url by remember { mutableStateOf(existing?.url ?: "") }
    var groupName by remember { mutableStateOf(existing?.groupName ?: initialGroupName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Text(
                if (existing != null) "تعديل الرابط" else "إضافة رابط جديد",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان أو تسمية الرابط (اختياري)", fontSize = 11.sp) },
                    placeholder = { Text("مثال: حساب إنستغرام المستهدف", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (isMasked && title.isNotEmpty()) PasswordVisualTransformation('•') else VisualTransformation.None,
                    singleLine = true
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("الرابط (URL)*", fontSize = 11.sp) },
                    placeholder = { Text("https://...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (isMasked && url.isNotEmpty()) PasswordVisualTransformation('•') else VisualTransformation.None,
                    singleLine = true
                )

                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("اسم المجموعة (اختياري)", fontSize = 11.sp) },
                    placeholder = { Text("مثال: حسابات العميل", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (url.isNotBlank()) {
                        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
                        onSave(
                            DraftCustomLink(
                                id = existing?.id ?: UUID.randomUUID().toString(),
                                title = title.trim(),
                                url = formattedUrl.trim(),
                                groupName = groupName.trim()
                            )
                        )
                    }
                },
                enabled = url.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
            ) {
                Text("حفظ الرابط", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun AddEditGroupDialog(
    existingGroupName: String?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(existingGroupName ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Text(
                if (existingGroupName != null) "تعديل اسم المجموعة" else "إضافة مجموعة روابط جديدة",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم المجموعة*", fontSize = 11.sp) },
                    placeholder = { Text("مثال: حسابات العميل، روابط الأدلة", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim())
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
            ) {
                Text("حفظ", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditIdentifierDialog(
    existing: DraftIdentifier?,
    isMasked: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (DraftIdentifier) -> Unit
) {
    var selectedPresetType by remember {
        mutableStateOf(
            if (existing != null && !existing.isCustomType) existing.type
            else IdentifierTypePresets.PRESETS.first()
        )
    }
    var customTypeName by remember {
        mutableStateOf(
            if (existing != null && existing.isCustomType) existing.type
            else ""
        )
    }
    var value by remember { mutableStateOf(existing?.value ?: "") }
    var isCustomType by remember { mutableStateOf(existing?.isCustomType ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Text(
                if (existing != null) "تعديل المعرف" else "إضافة معرف جديد",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("نوع المعرف:", color = TextSecondary, fontSize = 11.sp)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IdentifierTypePresets.PRESETS.forEach { preset ->
                        val isSelected = if (preset == "نوع معرف مخصص") isCustomType else (!isCustomType && selectedPresetType == preset)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (preset == "نوع معرف مخصص") {
                                    isCustomType = true
                                } else {
                                    isCustomType = false
                                    selectedPresetType = preset
                                }
                            },
                            label = { Text(preset, fontSize = 11.sp) }
                        )
                    }
                }

                if (isCustomType) {
                    OutlinedTextField(
                        value = customTypeName,
                        onValueChange = { customTypeName = it },
                        label = { Text("اكتب مسمى المعرف المخصص*", fontSize = 11.sp) },
                        placeholder = { Text("مثال: سناب شات ID، معرف Telegram", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("قيمة المعرف*", fontSize = 11.sp) },
                    placeholder = { Text("اكتب الرقم أو الرمز أو المعرف هنا...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (isMasked && value.isNotEmpty()) PasswordVisualTransformation('•') else VisualTransformation.None,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalType = if (isCustomType) customTypeName.trim().ifBlank { "معرف مخصص" } else selectedPresetType
                    if (value.isNotBlank()) {
                        onSave(
                            DraftIdentifier(
                                id = existing?.id ?: UUID.randomUUID().toString(),
                                type = finalType,
                                value = value.trim(),
                                isCustomType = isCustomType
                            )
                        )
                    }
                },
                enabled = value.isNotBlank() && (!isCustomType || customTypeName.isNotBlank()),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
            ) {
                Text("حفظ المعرف", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun FullScreenImagePreviewDialog(
    image: DraftCaseImage,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
        ) {
            AsyncImage(
                model = image.localFile,
                contentDescription = image.displayName,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(image.displayName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text("${image.fileSizeFormatted}${if (image.width > 0) " • ${image.width}×${image.height}" else ""}", color = Color.LightGray, fontSize = 11.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onDownload) {
                        Icon(Icons.Default.Download, contentDescription = "تحميل", tint = CyberSuccess)
                    }
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = "مشاركة", tint = CyberSecondary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun RenameImageDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Text("إعادة تسمية الصورة", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("اسم الملف الجديد*", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newName.isNotBlank()) {
                        onSave(newName.trim())
                    }
                },
                enabled = newName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary)
            ) {
                Text("حفظ التسمية", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        }
    )
}

// Helpers
private fun openUrlInBrowser(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "تعذر فتح الرابط: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
    }
}

private fun copyToClipboard(context: Context, text: String, message: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

private fun formatBytesHelper(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, 3)
    return String.format(java.util.Locale.US, "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
