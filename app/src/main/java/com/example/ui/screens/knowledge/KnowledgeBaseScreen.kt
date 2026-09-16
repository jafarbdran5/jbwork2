package com.example.ui.screens.knowledge

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.local.entities.KnowledgeEntity
import com.example.ui.components.CollapsibleHeaderContainer
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.HudType
import com.example.ui.components.InAppConfirmationSheet
import com.example.ui.components.rememberScrollHeaderVisibility
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberDanger
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

val KNOWLEDGE_CATEGORIES = listOf(
    "الكل",
    "سياسات الشركات",
    "استخبارات المصادر المفتوحة",
    "الإسعاف النفسي وإدارة القضايا",
    "الصلابة المهنية لمسؤول المتابعة",
    "إجراءات التعامل الميدانية"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeBaseScreen(
    viewModel: ForensicViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val knowledgeList by viewModel.filteredKnowledge.collectAsStateWithLifecycle()
    val searchQuery by viewModel.knowledgeSearchQuery.collectAsStateWithLifecycle()
    val activeCategory by viewModel.knowledgeCategoryFilter.collectAsStateWithLifecycle()

    var activeReadingGuide by remember { mutableStateOf<KnowledgeEntity?>(null) }
    var guideToEdit by remember { mutableStateOf<KnowledgeEntity?>(null) }
    var guideToDelete by remember { mutableStateOf<KnowledgeEntity?>(null) }
    var showEditorSheet by remember { mutableStateOf(false) }

    // Form inputs for add/edit
    var formTitle by remember { mutableStateOf("") }
    var formCategory by remember { mutableStateOf("إجراءات التعامل الميدانية") }
    var formSummary by remember { mutableStateOf("") }
    var formContent by remember { mutableStateOf("") }
    var formUrl by remember { mutableStateOf("") }
    var formTags by remember { mutableStateOf("") }

    fun openNewGuide() {
        guideToEdit = null
        formTitle = ""
        formCategory = "إجراءات التعامل الميدانية"
        formSummary = ""
        formContent = ""
        formUrl = ""
        formTags = ""
        showEditorSheet = true
    }

    fun openEditGuide(guide: KnowledgeEntity) {
        guideToEdit = guide
        formTitle = guide.title
        formCategory = guide.category
        formSummary = guide.summary
        formContent = guide.content
        formUrl = guide.officialUrl
        formTags = guide.tags
        showEditorSheet = true
    }

    val listState = rememberLazyListState()
    val isHeaderVisible by rememberScrollHeaderVisibility(
        listState = listState,
        onVisibilityChanged = { viewModel.setGlobalTopBarVisible(it) }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openNewGuide() },
                containerColor = CyberPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_knowledge_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة إجراء")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Dynamic Collapsible Header (Scroll Down -> Hide, Scroll Up -> Show)
            CollapsibleHeaderContainer(visible = isHeaderVisible) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "موسوعة المعرفة والإجراءات المعتمدة",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+40 إجراءً ومرجعاً معتمداً لسياسات المنصات، التحقق، والدعم",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        // Reload Official Library Button
                        Button(
                            onClick = { viewModel.reloadOfficialLibrary() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("reload_library_button")
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                tint = CyberPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إعادة تحميل المكتبة", color = CyberPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.knowledgeSearchQuery.value = it },
                        placeholder = { Text("ابحث في الإجراءات والسياسات والـ OSINT...", color = TextSecondary, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.knowledgeSearchQuery.value = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("knowledge_search_input"),
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

                    // Categories Filter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KNOWLEDGE_CATEGORIES.forEach { cat ->
                            val isSelected = activeCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.knowledgeCategoryFilter.value = cat },
                                label = { Text(cat, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberSecondary,
                                    selectedLabelColor = Color.White,
                                    containerColor = CyberSurface,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) CyberSecondary else CyberBorder
                                )
                            )
                        }
                    }
                }
            }

            // Guides List
            if (knowledgeList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("لا توجد مقالات مطابقة لبحثك", color = TextSecondary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.reloadOfficialLibrary() }) {
                            Text("استعادة الموسوعة الرسمية المعتمدة (+40 مرجعاً)", color = CyberPrimary)
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(knowledgeList, key = { it.id }) { guide ->
                        KnowledgeGuideCard(
                            guide = guide,
                            onClick = { activeReadingGuide = guide },
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Guide Text", "${guide.title}\n\n${guide.summary}\n\n${guide.content}")
                                clipboard.setPrimaryClip(clip)
                                viewModel.showHud("تم نسخ محتوى المرجع بالكامل للحافظة", HudType.SUCCESS)
                            },
                            onShare = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "【${guide.title}】\n\n${guide.summary}\n\n${guide.content}\n\nمرجع رسمي: ${guide.officialUrl}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "مشاركة الدليل المعرفي")
                                context.startActivity(shareIntent)
                            },
                            onOpenUrl = if (guide.officialUrl.isNotBlank()) {
                                {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(guide.officialUrl))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        viewModel.showHud("تعذر فتح الرابط", HudType.ERROR)
                                    }
                                }
                            } else null,
                            onEdit = { openEditGuide(guide) },
                            onDelete = { guideToDelete = guide }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Reading Modal / Full Article Viewer
    if (activeReadingGuide != null) {
        val guide = activeReadingGuide!!
        ModalBottomSheet(
            onDismissRequest = { activeReadingGuide = null },
            containerColor = MaterialTheme.colorScheme.surface,
            scrimColor = Color.Black.copy(alpha = 0.7f),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("reading_guide_modal")
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CyberBadge(text = guide.category, accentColor = CyberSecondary)
                        if (guide.isOfficialGuide) {
                            CyberBadge(text = "معتمد", accentColor = CyberSuccess)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Share guide
                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "【${guide.title}】\n\n${guide.summary}\n\n${guide.content}\n\nمرجع رسمي: ${guide.officialUrl}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "مشاركة الدليل المعرفي")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "مشاركة", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                        }

                        // Copy content
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Guide Text", "${guide.title}\n\n${guide.summary}\n\n${guide.content}")
                                clipboard.setPrimaryClip(clip)
                                viewModel.showHud("تم نسخ محتوى المرجع بالكامل للحافظة", HudType.SUCCESS)
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = CyberPrimaryLight, modifier = Modifier.size(18.dp))
                        }

                        // Close
                        IconButton(
                            onClick = { activeReadingGuide = null },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title
                Text(
                    text = guide.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )

                if (guide.summary.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الموجز السريع",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Guide Summary", guide.summary))
                                        viewModel.showHud("تم نسخ الموجز للحافظة", HudType.SUCCESS)
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ الموجز", tint = CyberPrimaryLight, modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = guide.summary,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.5.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Full Procedure Content Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "الخطوات الفنية والشرح الكامل",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Guide Content", guide.content))
                                    viewModel.showHud("تم نسخ الخطوات الفنية للحافظة", HudType.SUCCESS)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "نسخ المحتوى", tint = CyberPrimaryLight, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = guide.content,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            lineHeight = 22.sp
                        )
                    }
                }

                if (guide.tags.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "الكلمات المفتاحية: ${guide.tags}",
                        color = CyberPrimaryLight,
                        fontSize = 11.sp
                    )
                }

                if (guide.officialUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(guide.officialUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.showHud("تعذر فتح الرابط", HudType.ERROR)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("فتح المرجع الرسمي للبوابة", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action buttons: Edit, Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val target = guide
                            activeReadingGuide = null
                            openEditGuide(target)
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSecondary)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تعديل", color = CyberSecondary, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            val target = guide
                            activeReadingGuide = null
                            guideToDelete = target
                        },
                        modifier = Modifier.weight(1f).height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberDanger.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = CyberDanger, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حذف", color = CyberDanger, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Deletion In-App BottomSheet
    if (guideToDelete != null) {
        val target = guideToDelete!!
        InAppConfirmationSheet(
            title = "حذف الإجراء المعرفي",
            description = "هل تريد إزالة '${target.title}' من الموسوعة؟ يمكنك دائماً إعادة استعادته عبر زر 'إعادة تحميل المكتبة'.",
            confirmLabel = "حذف المقال",
            onConfirm = {
                viewModel.deleteKnowledge(target)
                guideToDelete = null
            },
            onDismiss = { guideToDelete = null }
        )
    }

    // Add / Edit Guide Sheet
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
            ) {
                Text(
                    text = if (guideToEdit == null) "إضافة دليل أو إجراء معتمد جديد" else "تعديل الدليل أو الإجراء",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = formTitle,
                    onValueChange = { formTitle = it },
                    label = { Text("عنوان الإجراء / السياسة") },
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
                    value = formSummary,
                    onValueChange = { formSummary = it },
                    label = { Text("الملخص التنفيذي السريع") },
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
                    value = formContent,
                    onValueChange = { formContent = it },
                    label = { Text("الخطوات الفنية والشرح التفصيلي") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
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
                    value = formUrl,
                    onValueChange = { formUrl = it },
                    label = { Text("الرابط المرجعي الرسمي (اختياري)") },
                    modifier = Modifier.fillMaxWidth(),
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
                        if (formTitle.isNotBlank()) {
                            val id = guideToEdit?.id ?: "kb_custom_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
                            val entity = KnowledgeEntity(
                                id = id,
                                title = formTitle,
                                category = formCategory,
                                summary = formSummary,
                                content = formContent,
                                officialUrl = formUrl,
                                isOfficialGuide = guideToEdit?.isOfficialGuide ?: false,
                                tags = formTags,
                                updatedDate = System.currentTimeMillis()
                            )
                            viewModel.saveKnowledge(entity, isNew = guideToEdit == null)
                            showEditorSheet = false
                        } else {
                            viewModel.showHud("يرجى إدخال عنوان للإجراء", HudType.WARNING)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (guideToEdit == null) "حفظ الدليل في الموسوعة" else "حفظ التعديلات",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun KnowledgeGuideCard(
    guide: KnowledgeEntity,
    onClick: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onOpenUrl: (() -> Unit)?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp))
            .testTag("knowledge_guide_card_${guide.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Category Badge & Official Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CyberBadge(text = guide.category, accentColor = CyberSecondary)
                    if (guide.isOfficialGuide) {
                        CyberBadge(text = "معتمد", accentColor = CyberSuccess)
                    }
                }

                // Quick copy button in header
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(32.dp).testTag("quick_copy_guide_${guide.id}")
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "نسخ الدليل",
                        tint = CyberPrimaryLight,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Title
            Text(
                text = guide.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Quick Summary
            if (guide.summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = guide.summary,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row: Read/Details, Share, Open Link (if available), Edit, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Read / Details Button
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("read_guide_btn_${guide.id}"),
                    shape = RoundedCornerShape(7.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "قراءة الدليل",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Share Button
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(34.dp)
                        .border(1.dp, CyberBorder, RoundedCornerShape(7.dp))
                        .testTag("share_guide_btn_${guide.id}")
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "مشاركة",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Open Link Button (if official URL present)
                if (onOpenUrl != null) {
                    IconButton(
                        onClick = onOpenUrl,
                        modifier = Modifier
                            .size(34.dp)
                            .border(1.dp, CyberBorder, RoundedCornerShape(7.dp))
                            .testTag("open_guide_url_${guide.id}")
                    ) {
                        Icon(
                            Icons.Default.OpenInBrowser,
                            contentDescription = "فتح الرابط الرسمي",
                            tint = CyberPrimaryLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Edit Button
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(34.dp)
                        .border(1.dp, CyberBorder, RoundedCornerShape(7.dp))
                        .testTag("edit_guide_btn_${guide.id}")
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "تعديل",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(34.dp)
                        .border(1.dp, CyberDanger.copy(alpha = 0.3f), RoundedCornerShape(7.dp))
                        .testTag("delete_guide_btn_${guide.id}")
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = CyberDanger.copy(alpha = 0.8f),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
