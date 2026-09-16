package com.example.ui.screens.studio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.local.entities.VideoIdeaEntity
import com.example.data.local.entities.VideoScriptEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.HudType
import com.example.ui.components.PriorityBadge
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

val VIDEO_IDEA_STATUSES = listOf("الكل", "فكرة جديدة", "قيد التطوير", "جاهزة للتصوير", "تم التصوير", "قيد المونتاج", "جاهزة للنشر", "تم النشر")
val VIDEO_PLATFORMS = listOf("الكل", "YouTube", "TikTok", "Instagram Reels", "X (Twitter)", "LinkedIn")
val VIDEO_CONTENT_TYPES = listOf("توعية أمنية", "شرح تقني وتحليل", "كشف احتيال ونصب", "نصيحة سريعة", "بودكاست / حوار", "تحليل قضية حقيقية")
val VIDEO_SCRIPT_STATUSES = listOf("الكل", "مسودة", "قيد المراجعة", "معتمد للتصوير", "تم الإنتاج", "مؤرشف")

@Composable
fun VideoIdeasTabContent(
    ideas: List<VideoIdeaEntity>,
    activeStatus: String,
    onStatusChange: (String) -> Unit,
    onAddIdeaClick: () -> Unit,
    onEditIdea: (VideoIdeaEntity) -> Unit,
    onDeleteIdea: (VideoIdeaEntity) -> Unit,
    onConvertToScript: (VideoIdeaEntity) -> Unit,
    listState: LazyListState = rememberLazyListState()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VIDEO_IDEA_STATUSES.forEach { status ->
                val isSelected = activeStatus == status
                FilterChip(
                    selected = isSelected,
                    onClick = { onStatusChange(status) },
                    label = { Text(status, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberSecondary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        if (ideas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("لا توجد أفكار فيديوهات مسجلة في هذا التصنيف", color = TextSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onAddIdeaClick) {
                        Text("تسجيل فكرة فيديو جديدة", color = CyberSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ideas, key = { it.id }) { idea ->
                    VideoIdeaCard(
                        idea = idea,
                        onEdit = { onEditIdea(idea) },
                        onDelete = { onDeleteIdea(idea) },
                        onConvertToScript = { onConvertToScript(idea) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun VideoIdeaCard(
    idea: VideoIdeaEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onConvertToScript: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Movie, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    CyberBadge(text = idea.platform, accentColor = CyberSecondary)
                    Spacer(modifier = Modifier.width(6.dp))
                    CyberBadge(text = idea.contentType, accentColor = CyberInfo)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PriorityBadge(priority = idea.priority)
                    Spacer(modifier = Modifier.width(6.dp))
                    CyberBadge(text = idea.status, accentColor = if (idea.status.contains("نشر") || idea.status.contains("تصوير")) CyberSuccess else CyberWarning)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = idea.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            if (idea.concept.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = idea.concept,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (idea.hook.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberBg.copy(alpha = 0.5f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "الخطاف (Hook): ${idea.hook}",
                        color = CyberPrimaryLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الجمهور: ${idea.targetAudience}",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Convert to Script button
                    TextButton(
                        onClick = onConvertToScript,
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.Article, contentDescription = null, tint = CyberPrimaryLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("كتابة اسكربت", fontSize = 11.sp, color = CyberPrimaryLight, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = TextSecondary, modifier = Modifier.size(15.dp))
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger.copy(alpha = 0.7f), modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun VideoScriptsTabContent(
    scripts: List<VideoScriptEntity>,
    activeStatus: String,
    onStatusChange: (String) -> Unit,
    onAddScriptClick: () -> Unit,
    onEditScript: (VideoScriptEntity) -> Unit,
    onDeleteScript: (VideoScriptEntity) -> Unit,
    onViewScriptReader: (VideoScriptEntity) -> Unit,
    listState: LazyListState = rememberLazyListState()
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VIDEO_SCRIPT_STATUSES.forEach { status ->
                val isSelected = activeStatus == status
                FilterChip(
                    selected = isSelected,
                    onClick = { onStatusChange(status) },
                    label = { Text(status, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        if (scripts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Article, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("لا توجد اسكربتات ونصوص مسجلة في هذا التصنيف", color = TextSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onAddScriptClick) {
                        Text("كتابة اسكربت فيديو جديد", color = CyberPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(scripts, key = { it.id }) { script ->
                    VideoScriptCard(
                        script = script,
                        onViewReader = { onViewScriptReader(script) },
                        onEdit = { onEditScript(script) },
                        onDelete = { onDeleteScript(script) },
                        onCopyAll = {
                            val fullText = buildString {
                                appendLine("🎬 اسكربت: ${script.title}")
                                appendLine("⏱ المدة التقديرية: ${script.estimatedDuration} | المنصة: ${script.platform}")
                                appendLine()
                                appendLine("🎯 الخطاف (Hook):")
                                appendLine(script.hook)
                                appendLine()
                                appendLine("📌 المقدمة (Intro):")
                                appendLine(script.intro)
                                appendLine()
                                appendLine("💡 صلب المحتوى (Body):")
                                appendLine(script.mainContent)
                                appendLine()
                                appendLine("🏁 الخاتمة (Outro):")
                                appendLine(script.outro)
                                appendLine()
                                appendLine("📢 الدعوة لاتخاذ إجراء (CTA):")
                                appendLine(script.callToAction)
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Video Script", fullText))
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun VideoScriptCard(
    script: VideoScriptEntity,
    onViewReader: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopyAll: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CyberBadge(text = script.platform, accentColor = CyberPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    CyberBadge(text = script.estimatedDuration, accentColor = CyberInfo)
                }
                CyberBadge(
                    text = script.status,
                    accentColor = when (script.status) {
                        "معتمد للتصوير" -> CyberSuccess
                        "تم الإنتاج" -> CyberPrimaryLight
                        else -> CyberWarning
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = script.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            if (script.hook.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🎯 ${script.hook}",
                    color = CyberPrimaryLight,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reader mode button
                Button(
                    onClick = onViewReader,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyberPrimaryLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("وضع الملقن / القراءة", fontSize = 11.sp, color = CyberPrimaryLight, fontWeight = FontWeight.Bold)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onCopyAll, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ النص", tint = TextSecondary, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = TextSecondary, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger.copy(alpha = 0.7f), modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoIdeaEditorSheet(
    idea: VideoIdeaEntity?,
    onDismiss: () -> Unit,
    onSave: (VideoIdeaEntity) -> Unit
) {
    var title by remember { mutableStateOf(idea?.title ?: "") }
    var concept by remember { mutableStateOf(idea?.concept ?: "") }
    var platform by remember { mutableStateOf(idea?.platform ?: "YouTube") }
    var contentType by remember { mutableStateOf(idea?.contentType ?: "توعية أمنية") }
    var targetAudience by remember { mutableStateOf(idea?.targetAudience ?: "عامة المستخدمين") }
    var goal by remember { mutableStateOf(idea?.goal ?: "زيادة الوعي وبناء الثقة") }
    var hook by remember { mutableStateOf(idea?.hook ?: "") }
    var keyPoints by remember { mutableStateOf(idea?.keyPoints ?: "") }
    var status by remember { mutableStateOf(idea?.status ?: "فكرة جديدة") }
    var priority by remember { mutableStateOf(idea?.priority ?: "متوسطة") }
    var notes by remember { mutableStateOf(idea?.notes ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                text = if (idea == null) "تسجيل فكرة فيديو جديدة" else "تعديل فكرة الفيديو",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("عنوان / موضوع الفكرة") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberSecondary,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = hook,
                onValueChange = { hook = it },
                label = { Text("الخطاف الافتتاحي (Hook - أول 3 ثوانٍ)") },
                placeholder = { Text("جملة صادمة أو سؤال يجذب الانتباه فوراً") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberSecondary,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = concept,
                onValueChange = { concept = it },
                label = { Text("شرح الفكرة وزاوية الطرح (Angle & Concept)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberSecondary,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text("المنصة المستهدفة:", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                VIDEO_PLATFORMS.filter { it != "الكل" }.forEach { p ->
                    FilterChip(
                        selected = platform == p,
                        onClick = { platform = p },
                        label = { Text(p, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("نوع المحتوى:", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                VIDEO_CONTENT_TYPES.forEach { ct ->
                    FilterChip(
                        selected = contentType == ct,
                        onClick = { contentType = ct },
                        label = { Text(ct, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("حالة الفكرة:", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("فكرة جديدة", "قيد التطوير", "جاهزة للتصوير", "تم التصوير", "قيد المونتاج", "تم النشر").forEach { st ->
                    FilterChip(
                        selected = status == st,
                        onClick = { status = st },
                        label = { Text(st, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = keyPoints,
                onValueChange = { keyPoints = it },
                label = { Text("النقاط والمحاور الرئيسية للفيديو") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberSecondary,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val entity = VideoIdeaEntity(
                            id = idea?.id ?: "idea_${System.currentTimeMillis()}",
                            title = title,
                            concept = concept,
                            platform = platform,
                            contentType = contentType,
                            targetAudience = targetAudience,
                            goal = goal,
                            hook = hook,
                            keyPoints = keyPoints,
                            status = status,
                            priority = priority,
                            notes = notes,
                            createdDate = idea?.createdDate ?: System.currentTimeMillis()
                        )
                        onSave(entity)
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (idea == null) "حفظ فكرة الفيديو" else "تحديث الفكرة",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScriptEditorSheet(
    script: VideoScriptEntity?,
    initialIdea: VideoIdeaEntity? = null,
    onDismiss: () -> Unit,
    onSave: (VideoScriptEntity) -> Unit
) {
    var title by remember { mutableStateOf(script?.title ?: initialIdea?.title ?: "") }
    var hook by remember { mutableStateOf(script?.hook ?: initialIdea?.hook ?: "") }
    var intro by remember { mutableStateOf(script?.intro ?: "") }
    var mainContent by remember { mutableStateOf(script?.mainContent ?: initialIdea?.keyPoints ?: "") }
    var outro by remember { mutableStateOf(script?.outro ?: "") }
    var callToAction by remember { mutableStateOf(script?.callToAction ?: "تابع الحساب للمزيد من النصائح الأمنية والتقنية") }
    var estimatedDuration by remember { mutableStateOf(script?.estimatedDuration ?: "60 ثانية") }
    var platform by remember { mutableStateOf(script?.platform ?: initialIdea?.platform ?: "YouTube") }
    var toneAndStyle by remember { mutableStateOf(script?.toneAndStyle ?: "مباشر واحترافي") }
    var status by remember { mutableStateOf(script?.status ?: "مسودة") }
    var notes by remember { mutableStateOf(script?.notes ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                text = if (script == null) "كتابة اسكربت فيديو جديد" else "تعديل الاسكربت",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("عنوان الفيديو / الاسكربت") },
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = estimatedDuration,
                    onValueChange = { estimatedDuration = it },
                    label = { Text("المدة التقديرية") },
                    placeholder = { Text("مثال: 60 ثانية") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = platform,
                    onValueChange = { platform = it },
                    label = { Text("المنصة") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = hook,
                onValueChange = { hook = it },
                label = { Text("1. الخطاف الافتتاحي (Hook)") },
                placeholder = { Text("الجملة التي توقف التمرير فوراً") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = intro,
                onValueChange = { intro = it },
                label = { Text("2. المقدمة والتمهيد (Intro)") },
                placeholder = { Text("توضيح المشكلة أو القضية") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = mainContent,
                onValueChange = { mainContent = it },
                label = { Text("3. صلب المحتوى والخطوات العملية (Body)") },
                placeholder = { Text("النقاط والمعلومات والنصائح بالتفصيل...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = outro,
                onValueChange = { outro = it },
                label = { Text("4. الخاتمة والتلخيص (Outro)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = callToAction,
                onValueChange = { callToAction = it },
                label = { Text("5. الدعوة للتفاعل (CTA)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("حالة الاسكربت:", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("مسودة", "قيد المراجعة", "معتمد للتصوير", "تم الإنتاج", "مؤرشف").forEach { st ->
                    FilterChip(
                        selected = status == st,
                        onClick = { status = st },
                        label = { Text(st, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val entity = VideoScriptEntity(
                            id = script?.id ?: "script_${System.currentTimeMillis()}",
                            ideaId = script?.ideaId ?: initialIdea?.id,
                            title = title,
                            hook = hook,
                            intro = intro,
                            mainContent = mainContent,
                            outro = outro,
                            callToAction = callToAction,
                            estimatedDuration = estimatedDuration,
                            platform = platform,
                            toneAndStyle = toneAndStyle,
                            status = status,
                            notes = notes,
                            createdDate = script?.createdDate ?: System.currentTimeMillis()
                        )
                        onSave(entity)
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (script == null) "حفظ الاسكربت رسمياً" else "تحديث الاسكربت",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScriptReaderSheet(
    script: VideoScriptEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        scrimColor = Color.Black.copy(alpha = 0.8f),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "وضع القراءة والتصوير (Teleprompter)",
                        color = CyberPrimaryLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = script.title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        val fullText = "${script.hook}\n\n${script.intro}\n\n${script.mainContent}\n\n${script.outro}\n\n${script.callToAction}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Script Text", fullText))
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ كامل", tint = CyberPrimaryLight)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hook
            if (script.hook.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSecondary.copy(alpha = 0.15f))
                        .padding(14.dp)
                ) {
                    Column {
                        Text("🎯 الخطاف (Hook):", color = CyberSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(script.hook, color = TextPrimary, fontSize = 17.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Intro
            if (script.intro.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCardElevated)
                        .padding(14.dp)
                ) {
                    Column {
                        Text("📌 المقدمة:", color = CyberInfo, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(script.intro, color = TextPrimary, fontSize = 16.sp, lineHeight = 26.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Main Content
            if (script.mainContent.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCardElevated)
                        .padding(14.dp)
                ) {
                    Column {
                        Text("💡 صلب المحتوى:", color = CyberSuccess, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(script.mainContent, color = TextPrimary, fontSize = 17.sp, lineHeight = 28.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Outro
            if (script.outro.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCardElevated)
                        .padding(14.dp)
                ) {
                    Column {
                        Text("🏁 الخاتمة:", color = CyberWarning, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(script.outro, color = TextPrimary, fontSize = 16.sp, lineHeight = 26.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // CTA
            if (script.callToAction.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberPrimary.copy(alpha = 0.15f))
                        .padding(14.dp)
                ) {
                    Column {
                        Text("📢 الدعوة للتفاعل (CTA):", color = CyberPrimaryLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(script.callToAction, color = TextPrimary, fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
