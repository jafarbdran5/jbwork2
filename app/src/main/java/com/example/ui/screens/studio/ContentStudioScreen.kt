package com.example.ui.screens.studio

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import com.example.data.local.entities.ContentEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.HudType
import com.example.ui.components.InAppConfirmationSheet
import com.example.ui.components.PlatformBadge
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberPrimaryLight
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSuccess
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ForensicViewModel

val POST_TEMPLATES = listOf(
    Pair(
        "خطة طوارئ الابتزاز",
        """🚨 ماذا تفعل إذا تعرضت للابتزاز بصورك أو محادثاتك الآن؟
1. لا تدفع أي مبلغ مالي إطلاقاً.
2. لا تقم بحذف المحادثات أو الحسابات.
3. وثّق أرقام ومعرفات المبتز فوراً.
4. تواصل مع المختصين أو الرقم الموحد للدعم الفني والأمني.
5. سجّل بصمة الصور في منصة StopNCII.org لمنع نشرها.
#أمن_سيبراني #مكافحة_الابتزاز #جعفر_بدران"""
    ),
    Pair(
        "فخ روابط التصيد (Phishing)",
        """⚠️ كيف تكتشف رابط التصيد الاحتيالي قبل الضغط عليه؟
• افحص اسم النطاق بدقة (Domain Name) وابحث عن الحروف المكررة أو المستبدلة مثل g00gle أو paypa1.
• الجهات الرسمية لا تطلب منك رمز الـ OTP عبر مكالمة هاتفية أبداً.
• لا تقم بتحميل ملفات apk من خارج المتاجر الرسمية.
#أمن_المعلومات #تصيد_احتيالي #وعي_سيبراني"""
    ),
    Pair(
        "تفعيل التحقق بخطوتين (2FA)",
        """🔒 تأمين حساباتك بـ 3 خطوات حاسمة:
1. فعّل المصادقة الثنائية باستخدام تطبيق مستقل مثل Google Authenticator أو 1Password بدلاً من الـ SMS.
2. احفظ الرموز الاحتياطية (Backup Codes) في مكان غير متصل بالإنترنت.
3. تفقّد الأجهزة المتصلة بحسابك شهرياً وألغِ الجلسات المجهولة.
#حماية_الحسابات #أمان_رقمي #CyberSecurity"""
    ),
    Pair(
        "احتيال مبادلة الشريحة (SIM-Swap)",
        """📲 كيف تحمي خطك من سرقة الشريحة وهجمات الـ SIM-Swap؟
• ضع رمز PIN مخصص لشريحة الاتصال (SIM PIN).
• لا تجعل رقم هاتفك هو الوسيلة الوحيدة لاسترداد بريدك الإلكتروني.
• إذا انقطعت إشارة الشبكة فجأة عن هاتفك، اتصل بمزود الخدمة فوراً.
#تقنية #اتصالات #أمن_سيبراني"""
    )
)

val PLATFORMS = listOf("الكل", "Instagram", "X", "Telegram", "TikTok", "Facebook", "YouTube", "LinkedIn", "Snapchat", "Website")
val STATUSES = listOf("الكل", "فكرة", "مسودة", "مجدول", "منشور", "مؤرشف")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ContentStudioScreen(
    viewModel: ForensicViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val contentList by viewModel.filteredContent.collectAsStateWithLifecycle()
    val activePlatform by viewModel.contentPlatformFilter.collectAsStateWithLifecycle()
    val activeStatus by viewModel.contentStatusFilter.collectAsStateWithLifecycle()

    var postToEdit by remember { mutableStateOf<ContentEntity?>(null) }
    var postToDelete by remember { mutableStateOf<ContentEntity?>(null) }
    var showEditorSheet by remember { mutableStateOf(false) }
    var showTemplatesSheet by remember { mutableStateOf(false) }

    // State for create/edit
    var formTitle by remember { mutableStateOf("") }
    var formBody by remember { mutableStateOf("") }
    var formPlatform by remember { mutableStateOf("Instagram") }
    var formStatus by remember { mutableStateOf("مسودة") }
    var formTags by remember { mutableStateOf("") }

    fun openNewPost() {
        postToEdit = null
        formTitle = ""
        formBody = ""
        formPlatform = "Instagram"
        formStatus = "مسودة"
        formTags = "#أمن_سيبراني, #جعفر_بدران"
        showEditorSheet = true
    }

    fun openEditPost(item: ContentEntity) {
        postToEdit = item
        formTitle = item.title
        formBody = item.body
        formPlatform = item.platform
        formStatus = item.status
        formTags = item.tagsJson
        showEditorSheet = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openNewPost() },
                containerColor = CyberPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_content_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة منشور")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header & Studio Banner
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
                            text = "استوديو صناعة المحتوى والنشر",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "إدارة الحملات التوعوية والمنشورات الأمنية المتخصصة",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // Button to open Ready-made Templates
                    Button(
                        onClick = { showTemplatesSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSecondary.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("open_templates_button")
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyberSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("قوالب أمنية", color = CyberSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Platforms Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PLATFORMS.forEach { platform ->
                        val isSelected = activePlatform == platform
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.contentPlatformFilter.value = platform },
                            label = { Text(platform, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = CyberSurface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) CyberPrimary else CyberBorder
                            )
                        )
                    }
                }
            }

            // Posts List
            if (contentList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد منشورات مطابقة للتصفية",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { openNewPost() }) {
                            Text("أنشئ أول منشور الآن", color = CyberPrimary)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(contentList, key = { it.id }) { item ->
                        ContentPostCard(
                            item = item,
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Post Content", "${item.title}\n\n${item.body}\n\n${item.tagsJson}")
                                clipboard.setPrimaryClip(clip)
                                viewModel.showHud("تم نسخ المنشور بالكامل مع الهاشتاجات إلى الحافظة", HudType.SUCCESS)
                            },
                            onEdit = { openEditPost(item) },
                            onDelete = { postToDelete = item }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Confirmation Sheet for Deletion (Non-blocking Compose Modal)
    if (postToDelete != null) {
        val target = postToDelete!!
        InAppConfirmationSheet(
            title = "حذف المنشور نهائياً",
            description = "هل أنت متأكد من حذف منشور '${target.title}'؟ سيتم حذفه من الاستوديو محلياً وسحابياً مع إمكانية التراجع الفوري.",
            confirmLabel = "حذف المنشور",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteContent(target)
                postToDelete = null
            },
            onDismiss = { postToDelete = null }
        )
    }

    // Ready-made Security Templates Sheet
    if (showTemplatesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTemplatesSheet = false },
            containerColor = CyberSurface,
            scrimColor = Color.Black.copy(alpha = 0.65f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "قوالب التوعية الأمنية الجاهزة",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "انقر فوق أي قالب لإدراجه مباشرة في المحرر بنقرة واحدة",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(POST_TEMPLATES) { (templateTitle, templateBody) ->
                        CyberCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                formTitle = templateTitle
                                formBody = templateBody
                                formPlatform = "Instagram"
                                formStatus = "مسودة"
                                formTags = "#أمن_سيبراني, #جعفر_بدران, #حماية_رقمية"
                                postToEdit = null
                                showTemplatesSheet = false
                                showEditorSheet = true
                            }
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = templateTitle,
                                        color = CyberPrimaryLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text("استخدام القالب ←", color = CyberSecondary, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = templateBody.lines().firstOrNull() ?: "",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Add / Edit Content Sheet
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
                    text = if (postToEdit == null) "إنشاء منشور توعوي جديد" else "تعديل منشور الاستوديو",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = formTitle,
                    onValueChange = { formTitle = it },
                    label = { Text("عنوان المنشور / الفكرة") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("content_input_title"),
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
                    value = formBody,
                    onValueChange = { formBody = it },
                    label = { Text("نص المنشور والتفاصيل") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .testTag("content_input_body"),
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
                    value = formTags,
                    onValueChange = { formTags = it },
                    label = { Text("الهاشتاجات (مفصولة بفواصل)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("content_input_tags"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Platform picker
                Text("اختر المنصة المستهدفة:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PLATFORMS.filter { it != "الكل" }.forEach { platform ->
                        val isSelected = formPlatform == platform
                        FilterChip(
                            selected = isSelected,
                            onClick = { formPlatform = platform },
                            label = { Text(platform, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Status picker
                Text("حالة المنشور:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    STATUSES.filter { it != "الكل" }.forEach { st ->
                        val isSelected = formStatus == st
                        FilterChip(
                            selected = isSelected,
                            onClick = { formStatus = st },
                            label = { Text(st, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberSecondary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (formTitle.isNotBlank()) {
                            viewModel.saveContent(
                                id = postToEdit?.id,
                                title = formTitle,
                                body = formBody,
                                platform = formPlatform,
                                status = formStatus,
                                tags = formTags,
                                scheduledTimestamp = if (formStatus == "مجدول") System.currentTimeMillis() + 86400000 else null
                            )
                            showEditorSheet = false
                        } else {
                            viewModel.showHud("يرجى إدخال عنوان للمنشور", HudType.WARNING)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("content_save_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (postToEdit == null) "حفظ ونشر في الاستوديو" else "حفظ التعديلات",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ContentPostCard(
    item: ContentEntity,
    onCopy: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("content_card_${item.id}"),
        backgroundColor = CyberCardElevated
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlatformBadge(platform = item.platform)
                    Spacer(modifier = Modifier.width(8.dp))
                    CyberBadge(text = item.status, accentColor = CyberSecondary)
                }

                Row {
                    // One-click copy
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("copy_content_btn")
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "نسخ النص",
                            tint = CyberPrimaryLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Edit
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("edit_content_btn")
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "تعديل",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Delete
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("delete_content_btn")
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "حذف",
                            tint = CyberDanger.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.body,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            if (item.tagsJson.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.tagsJson,
                    color = CyberPrimaryLight,
                    fontSize = 11.sp
                )
            }
        }
    }
}
