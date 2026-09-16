package com.example.ui.screens.cases.creation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageEditorDialog(
    image: DraftCaseImage,
    onDismiss: () -> Unit,
    onSaveAsNew: (DraftCaseImage) -> Unit,
    onReplaceOriginal: (DraftCaseImage) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var rotationDegrees by remember { mutableIntStateOf(0) }
    var isFlippedHorizontal by remember { mutableStateOf(false) }
    var selectedCropRatio by remember { mutableStateOf("الأصل") } // الأصل, 1:1, 4:3, 16:9, 9:16
    var selectedScalePercent by remember { mutableIntStateOf(100) } // 100, 75, 50, 25

    var isProcessing by remember { mutableStateOf(false) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showReplaceConfirmDialog by remember { mutableStateOf(false) }

    // Load initial bitmap in background
    LaunchedEffect(image.localFile) {
        withContext(Dispatchers.IO) {
            try {
                val opts = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeFile(image.localFile.absolutePath, opts)
                var sampleSize = 1
                while ((opts.outWidth / sampleSize) > 1600 || (opts.outHeight / sampleSize) > 1600) {
                    sampleSize *= 2
                }
                val decodeOpts = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }
                val loaded = BitmapFactory.decodeFile(image.localFile.absolutePath, decodeOpts)
                originalBitmap = loaded
                previewBitmap = loaded
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Update preview when transformations change
    fun updatePreview() {
        val src = originalBitmap ?: return
        coroutineScope.launch(Dispatchers.Default) {
            try {
                val matrix = Matrix().apply {
                    postRotate(rotationDegrees.toFloat())
                    if (isFlippedHorizontal) {
                        postScale(-1f, 1f)
                    }
                }
                val transformed = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)

                // Apply crop aspect ratio if not "الأصل"
                val cropped = if (selectedCropRatio != "الأصل") {
                    applyAspectRatioCrop(transformed, selectedCropRatio)
                } else {
                    transformed
                }

                // Apply scale if needed
                val finalBitmap = if (selectedScalePercent != 100) {
                    val factor = selectedScalePercent / 100f
                    val targetW = (cropped.width * factor).toInt().coerceAtLeast(10)
                    val targetH = (cropped.height * factor).toInt().coerceAtLeast(10)
                    Bitmap.createScaledBitmap(cropped, targetW, targetH, true)
                } else {
                    cropped
                }

                withContext(Dispatchers.Main) {
                    previewBitmap = finalBitmap
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(rotationDegrees, isFlippedHorizontal, selectedCropRatio, selectedScalePercent) {
        updatePreview()
    }

    fun renderAndSave(isReplacing: Boolean) {
        val src = originalBitmap ?: return
        isProcessing = true
        coroutineScope.launch {
            try {
                val resultImage = withContext(Dispatchers.IO) {
                    // Full fidelity processing using original file
                    val fullOpts = BitmapFactory.Options()
                    val fullBmp = BitmapFactory.decodeFile(image.localFile.absolutePath, fullOpts) ?: src

                    val matrix = Matrix().apply {
                        postRotate(rotationDegrees.toFloat())
                        if (isFlippedHorizontal) {
                            postScale(-1f, 1f)
                        }
                    }
                    val transformed = Bitmap.createBitmap(fullBmp, 0, 0, fullBmp.width, fullBmp.height, matrix, true)

                    val cropped = if (selectedCropRatio != "الأصل") {
                        applyAspectRatioCrop(transformed, selectedCropRatio)
                    } else {
                        transformed
                    }

                    val finalBmp = if (selectedScalePercent != 100) {
                        val factor = selectedScalePercent / 100f
                        val targetW = (cropped.width * factor).toInt().coerceAtLeast(10)
                        val targetH = (cropped.height * factor).toInt().coerceAtLeast(10)
                        Bitmap.createScaledBitmap(cropped, targetW, targetH, true)
                    } else {
                        cropped
                    }

                    val draftDir = File(context.cacheDir, "draft_cases")
                    if (!draftDir.exists()) draftDir.mkdirs()

                    val targetFile = if (isReplacing) {
                        image.localFile
                    } else {
                        val base = image.displayName.substringBeforeLast('.')
                        val ext = image.displayName.substringAfterLast('.', "jpg")
                        File(draftDir, "edited_${System.currentTimeMillis()}_$base.$ext")
                    }

                    FileOutputStream(targetFile).use { out ->
                        finalBmp.compress(Bitmap.CompressFormat.JPEG, 92, out)
                    }

                    val fileSize = targetFile.length()
                    val formattedSize = formatBytesLocal(fileSize)

                    val newDisplayName = if (isReplacing) {
                        image.displayName
                    } else {
                        "معدلة_${image.displayName}"
                    }

                    DraftCaseImage(
                        id = if (isReplacing) image.id else UUID.randomUUID().toString(),
                        uri = Uri.fromFile(targetFile),
                        localFile = targetFile,
                        originalFileName = image.originalFileName,
                        displayName = newDisplayName,
                        fileSizeFormatted = formattedSize,
                        fileSizeBytes = fileSize,
                        mimeType = "image/jpeg",
                        width = finalBmp.width,
                        height = finalBmp.height,
                        isEdited = true
                    )
                }

                withContext(Dispatchers.Main) {
                    isProcessing = false
                    if (isReplacing) {
                        Toast.makeText(context, "تم استبدال الصورة الأصلية بالنسخة المعدلة بنجاح", Toast.LENGTH_SHORT).show()
                        onReplaceOriginal(resultImage)
                    } else {
                        Toast.makeText(context, "تم حفظ النسخة كصورة جديدة بنجاح", Toast.LENGTH_SHORT).show()
                        onSaveAsNew(resultImage)
                    }
                    onDismiss()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isProcessing = false
                    Toast.makeText(context, "خطأ أثناء حفظ التعديل: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = CyberPrimaryLight)
                        Column {
                            Text(
                                text = "تحرير الصورة",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = image.displayName,
                                color = TextMuted,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Image Preview Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, CyberBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (previewBitmap != null) {
                        androidx.compose.foundation.Image(
                            bitmap = previewBitmap!!.asImageBitmap(),
                            contentDescription = "معاينة التحرير",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        CircularProgressIndicator(color = CyberPrimary, modifier = Modifier.size(36.dp))
                    }

                    // Overlay Specs Badge
                    if (previewBitmap != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${previewBitmap!!.width}×${previewBitmap!!.height} px (${selectedScalePercent}%)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tool Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Rotation and Flip Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "التدوير والقلب:",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    rotationDegrees = (rotationDegrees - 90 + 360) % 360
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.RotateLeft, contentDescription = "تدوير لليسار", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("90°-", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    rotationDegrees = (rotationDegrees + 90) % 360
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.RotateRight, contentDescription = "تدوير لليمين", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("90°+", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { isFlippedHorizontal = !isFlippedHorizontal },
                                colors = if (isFlippedHorizontal) ButtonDefaults.outlinedButtonColors(containerColor = CyberPrimary.copy(alpha = 0.15f)) else ButtonDefaults.outlinedButtonColors(),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = "قلب", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("قلب", fontSize = 11.sp)
                            }
                        }
                    }

                    // 2. Crop Aspect Ratio Presets
                    Column {
                        Text(
                            text = "نسبة القص (Crop Ratio):",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("الأصل", "1:1", "4:3", "16:9", "9:16").forEach { ratio ->
                                val isSelected = selectedCropRatio == ratio
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCropRatio = ratio },
                                    label = {
                                        Text(
                                            text = when (ratio) {
                                                "الأصل" -> "الأصل (كامل)"
                                                "1:1" -> "1:1 مربع"
                                                "4:3" -> "4:3 قياسي"
                                                "16:9" -> "16:9 عريض"
                                                "9:16" -> "9:16 ستوري"
                                                else -> ratio
                                            },
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberPrimary.copy(alpha = 0.25f),
                                        selectedLabelColor = CyberPrimaryLight
                                    )
                                )
                            }
                        }
                    }

                    // 3. Scaling / Resize Presets
                    Column {
                        Text(
                            text = "تغيير الحجم والدقة (Scale / Resize):",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(100 to "100% كامل", 75 to "75%", 50 to "50% خفيف", 25 to "25% مضغوط").forEach { (scale, label) ->
                                val isSelected = selectedScalePercent == scale
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedScalePercent = scale },
                                    label = { Text(label, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberSecondary.copy(alpha = 0.25f),
                                        selectedLabelColor = CyberSecondary
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Buttons: Save As New vs Replace Original
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("إلغاء", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { showReplaceConfirmDialog = true },
                        enabled = !isProcessing,
                        modifier = Modifier.weight(1.1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberWarning)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp), tint = CyberWarning)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("استبدال الأصل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { renderAndSave(isReplacing = false) },
                        enabled = !isProcessing,
                        modifier = Modifier.weight(1.4f),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("حفظ كصورة جديدة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog before replacing the original file
    if (showReplaceConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showReplaceConfirmDialog = false },
            containerColor = CyberSurface,
            title = {
                Text("تأكيد استبدال الصورة الأصلية", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            },
            text = {
                Text(
                    "هل أنت متأكد من استبدال الصورة الأصلية «${image.displayName}» بالنسخة المعدلة الحالية؟ لن تتمكن من التراجع عن هذا التعديل للصورة الأصلية.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReplaceConfirmDialog = false
                        renderAndSave(isReplacing = true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberWarning)
                ) {
                    Text("نعم، استبدل الأصل", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReplaceConfirmDialog = false }) {
                    Text("إلغاء", color = TextSecondary)
                }
            }
        )
    }
}

private fun applyAspectRatioCrop(source: Bitmap, ratio: String): Bitmap {
    val (targetRatioW, targetRatioH) = when (ratio) {
        "1:1" -> 1f to 1f
        "4:3" -> 4f to 3f
        "16:9" -> 16f to 9f
        "9:16" -> 9f to 16f
        else -> return source
    }

    val sourceRatio = source.width.toFloat() / source.height.toFloat()
    val desiredRatio = targetRatioW / targetRatioH

    var cropW = source.width
    var cropH = source.height
    var cropX = 0
    var cropY = 0

    if (sourceRatio > desiredRatio) {
        // Source is wider than target ratio
        cropW = (source.height * desiredRatio).toInt().coerceAtMost(source.width)
        cropX = (source.width - cropW) / 2
    } else {
        // Source is taller than target ratio
        cropH = (source.width / desiredRatio).toInt().coerceAtMost(source.height)
        cropY = (source.height - cropH) / 2
    }

    return Bitmap.createBitmap(source, cropX, cropY, cropW, cropH)
}

private fun formatBytesLocal(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, 3)
    return String.format(java.util.Locale.US, "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
