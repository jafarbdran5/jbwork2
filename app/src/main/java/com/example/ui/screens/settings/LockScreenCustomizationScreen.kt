package com.example.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.preferences.LockScreenCustomTexts
import com.example.ui.screens.security.BiometricLockScreen
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberPrimaryLight
import com.example.ui.theme.CyberSuccess
import com.example.ui.theme.CyberWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ForensicViewModel

/**
 * شاشة إعدادات تخصيص نصوص شاشة كلمة المرور / قفل التطبيق بالكامل
 * المسار: الإعدادات -> الأمان والخصوصية -> تخصيص شاشة كلمة المرور
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockScreenCustomizationScreen(
    viewModel: ForensicViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedLanguage by remember { mutableStateOf("ar") }
    val savedTextsAr by viewModel.lockScreenTextsAr.collectAsState()
    val savedTextsEn by viewModel.lockScreenTextsEn.collectAsState()

    // Local draft editable state
    var currentConfig by remember {
        mutableStateOf(if (selectedLanguage == "en") savedTextsEn else savedTextsAr)
    }

    // Keep draft updated when switching language
    LaunchedEffect(selectedLanguage, savedTextsAr, savedTextsEn) {
        currentConfig = if (selectedLanguage == "en") savedTextsEn else savedTextsAr
    }

    var showPreviewModal by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "تخصيص شاشة كلمة المرور",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "التحكم بكافة العناوين ورسائل واجهة القفل",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("lock_customization_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // Preview Button
                    IconButton(
                        onClick = { showPreviewModal = true },
                        modifier = Modifier.testTag("lock_customization_preview_top_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "معاينة شاشة القفل",
                            tint = CyberPrimaryLight
                        )
                    }

                    // Save Button
                    IconButton(
                        onClick = {
                            viewModel.saveLockScreenTexts(currentConfig)
                        },
                        modifier = Modifier.testTag("lock_customization_save_top_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "حفظ التعديلات",
                            tint = CyberSuccess
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
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
                        onClick = { showPreviewModal = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("lock_customization_preview_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CyberPrimaryLight
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "معاينة حية",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.saveLockScreenTexts(currentConfig)
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp)
                            .testTag("lock_customization_save_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberPrimary,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "حفظ التعديلات",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Language Selector Tabs
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberCard),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "لغة نصوص شاشة القفل:",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "تخصيص مستقل لكل لغة",
                                color = CyberPrimaryLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TabRow(
                            selectedTabIndex = if (selectedLanguage == "ar") 0 else 1,
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = CyberPrimary,
                            indicator = { tabPositions ->
                                SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[if (selectedLanguage == "ar") 0 else 1]),
                                    color = CyberPrimary
                                )
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, CyberPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        ) {
                            Tab(
                                selected = selectedLanguage == "ar",
                                onClick = { selectedLanguage = "ar" },
                                text = {
                                    Text(
                                        text = "🇸🇦 العربية (Arabic)",
                                        fontWeight = if (selectedLanguage == "ar") FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                },
                                modifier = Modifier.testTag("lang_tab_ar")
                            )
                            Tab(
                                selected = selectedLanguage == "en",
                                onClick = { selectedLanguage = "en" },
                                text = {
                                    Text(
                                        text = "🇺🇸 English",
                                        fontWeight = if (selectedLanguage == "en") FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                },
                                modifier = Modifier.testTag("lang_tab_en")
                            )
                        }
                    }
                }
            }

            // Quick Reset Banner
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberPrimary.copy(alpha = 0.08f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedLanguage == "ar") "تعديل نصوص واجهة القفل العربية" else "Editing English Lock Screen Texts",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    OutlinedButton(
                        onClick = {
                            val defaultTexts = LockScreenCustomTexts.defaultForLanguage(selectedLanguage)
                            currentConfig = defaultTexts
                        },
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("reset_all_texts_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberWarning)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "استعادة الافتراضي للكل",
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // ==========================================
            // القسم 1: عناوين ورسائل الترحيب
            // ==========================================
            item {
                SectionHeader(
                    icon = Icons.Default.Title,
                    title = "عناوين ورسائل الترحيب",
                    subtitle = "الواجهة العلوية لشاشة القفل والرسائل الترحيبية"
                )
            }

            // 1. عنوان الشاشة
            item {
                TextItemEditorCard(
                    title = "عنوان شاشة القفل",
                    roleDescription = "العنوان الرئيسي أعلى واجهة القفل تحت الشعار",
                    value = currentConfig.screenTitle,
                    onValueChange = { currentConfig = currentConfig.copy(screenTitle = it) },
                    hasToggle = true,
                    isEnabled = currentConfig.isScreenTitleEnabled,
                    onToggleChange = { currentConfig = currentConfig.copy(isScreenTitleEnabled = it) },
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).screenTitle,
                    tag = "lock_screen_title"
                )
            }

            // 2. النص الترحيبي الرئيسي
            item {
                TextItemEditorCard(
                    title = "الرسالة الترحيبية",
                    roleDescription = "نص الترحيب الظاهر أسفل العنوان وشعار المنظومة",
                    value = currentConfig.greetingText,
                    onValueChange = { currentConfig = currentConfig.copy(greetingText = it) },
                    hasToggle = true,
                    isEnabled = currentConfig.isGreetingEnabled,
                    onToggleChange = { currentConfig = currentConfig.copy(isGreetingEnabled = it) },
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).greetingText,
                    tag = "lock_greeting_text"
                )
            }

            // 3. النص الموجود أسفل العنوان
            item {
                TextItemEditorCard(
                    title = "النص التوضيحي أسفل العنوان",
                    roleDescription = "النص الإرشادي لتأكيد التشفير وحماية النظام",
                    value = currentConfig.subtitleText,
                    onValueChange = { currentConfig = currentConfig.copy(subtitleText = it) },
                    hasToggle = true,
                    isEnabled = currentConfig.isSubtitleEnabled,
                    onToggleChange = { currentConfig = currentConfig.copy(isSubtitleEnabled = it) },
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).subtitleText,
                    tag = "lock_subtitle_text"
                )
            }

            // ==========================================
            // القسم 2: حقل كلمة المرور والإرشادات
            // ==========================================
            item {
                SectionHeader(
                    icon = Icons.Default.Key,
                    title = "حقل كلمة المرور / رمز PIN",
                    subtitle = "النصوص الإرشادية المرتبطة بمؤشر إدخال الرمز"
                )
            }

            // 4. النص فوق حقل كلمة المرور
            item {
                TextItemEditorCard(
                    title = "النص فوق حقل كلمة المرور",
                    roleDescription = "العبارة الإرشادية الظاهرة مباشرة فوق نقاط الرمز",
                    value = currentConfig.pinPromptText,
                    onValueChange = { currentConfig = currentConfig.copy(pinPromptText = it) },
                    hasToggle = true,
                    isEnabled = currentConfig.isPinPromptEnabled,
                    onToggleChange = { currentConfig = currentConfig.copy(isPinPromptEnabled = it) },
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).pinPromptText,
                    tag = "lock_pin_prompt"
                )
            }

            // 5. النص الإرشادي داخل حقل كلمة المرور
            item {
                TextItemEditorCard(
                    title = "النص الإرشادي داخل الحقل",
                    roleDescription = "التلميح الظاهر عند عدم كتابة أي رقم في حقل الإدخال",
                    value = currentConfig.pinPlaceholderText,
                    onValueChange = { currentConfig = currentConfig.copy(pinPlaceholderText = it) },
                    hasToggle = true,
                    isEnabled = currentConfig.isPinPlaceholderEnabled,
                    onToggleChange = { currentConfig = currentConfig.copy(isPinPlaceholderEnabled = it) },
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).pinPlaceholderText,
                    tag = "lock_pin_placeholder"
                )
            }

            // 10. تلميح الرمز الافتراضي
            item {
                TextItemEditorCard(
                    title = "تلميح الرمز الافتراضي",
                    roleDescription = "الإرشاد الموجود أسفل الشاشة حول رمز 1234 الافتراضي",
                    value = currentConfig.defaultPinHintText,
                    onValueChange = { currentConfig = currentConfig.copy(defaultPinHintText = it) },
                    hasToggle = true,
                    isEnabled = currentConfig.isDefaultPinHintEnabled,
                    onToggleChange = { currentConfig = currentConfig.copy(isDefaultPinHintEnabled = it) },
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).defaultPinHintText,
                    tag = "lock_default_pin_hint"
                )
            }

            // ==========================================
            // القسم 3: رسائل التحقق والتنبيهات
            // ==========================================
            item {
                SectionHeader(
                    icon = Icons.Default.NotificationsActive,
                    title = "رسائل التحقق والتنبيهات",
                    subtitle = "الرسائل التفاعلية للخطأ والنجاح وفشل المصادقة"
                )
            }

            // 6. النص عند إدخال كلمة مرور خاطئة
            item {
                TextItemEditorCard(
                    title = "رسالة كلمة المرور الخاطئة",
                    roleDescription = "النص الأحمر الذي يظهر عند إدخال رمز PIN خاطئ واهتزاز الشاشة",
                    value = currentConfig.errorMessageWrongPin,
                    onValueChange = { currentConfig = currentConfig.copy(errorMessageWrongPin = it) },
                    hasToggle = false,
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).errorMessageWrongPin,
                    tag = "lock_error_wrong_pin"
                )
            }

            // 7. النص عند فشل المصادقة
            item {
                TextItemEditorCard(
                    title = "رسالة فشل المصادقة البيومترية",
                    roleDescription = "النص التنبيهي عند تعذر التعرف على بصمة الإصبع",
                    value = currentConfig.authFailedText,
                    onValueChange = { currentConfig = currentConfig.copy(authFailedText = it) },
                    hasToggle = false,
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).authFailedText,
                    tag = "lock_auth_failed"
                )
            }

            // 9. النص عند نجاح المصادقة أو فتح التطبيق
            item {
                TextItemEditorCard(
                    title = "رسالة نجاح فتح التطبيق",
                    roleDescription = "التأكيد الأخضر الذي يظهر فور إدخال الرمز الصحيح أو قبول البصمة",
                    value = currentConfig.successText,
                    onValueChange = { currentConfig = currentConfig.copy(successText = it) },
                    hasToggle = true,
                    isEnabled = currentConfig.isSuccessTextEnabled,
                    onToggleChange = { currentConfig = currentConfig.copy(isSuccessTextEnabled = it) },
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).successText,
                    tag = "lock_success_text"
                )
            }

            // ==========================================
            // القسم 4: أزرار البصمة ولوحة المفاتيح
            // ==========================================
            item {
                SectionHeader(
                    icon = Icons.Default.Fingerprint,
                    title = "أزرار البصمة ولوحة المفاتيح",
                    subtitle = "نصوص الأزرار وخيارات لوحة الأرقام"
                )
            }

            // 8. نص زر المصادقة بالبصمة
            item {
                TextItemEditorCard(
                    title = "نص زر المصادقة بالبصمة",
                    roleDescription = "الزر الرئيسي العريض أسفل لوحة الأرقام",
                    value = currentConfig.biometricButtonText,
                    onValueChange = { currentConfig = currentConfig.copy(biometricButtonText = it) },
                    hasToggle = true,
                    isEnabled = currentConfig.isBiometricButtonEnabled,
                    onToggleChange = { currentConfig = currentConfig.copy(isBiometricButtonEnabled = it) },
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).biometricButtonText,
                    tag = "lock_biometric_button_text"
                )
            }

            // تسمية زر البصمة في لوحة الأرقام
            item {
                TextItemEditorCard(
                    title = "تسمية زر البصمة في لوحة المفاتيح",
                    roleDescription = "الوصف الصوتي وتسمية زر البصمة في الزاوية السفلية من لوحة الأرقام",
                    value = currentConfig.biometricKeypadLabel,
                    onValueChange = { currentConfig = currentConfig.copy(biometricKeypadLabel = it) },
                    hasToggle = false,
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).biometricKeypadLabel,
                    tag = "lock_biometric_keypad_label"
                )
            }

            // نص زر المسح
            item {
                TextItemEditorCard(
                    title = "نص زر المسح",
                    roleDescription = "الزر المخصص لمسح الرمز المدخل بالكامل في لوحة المفاتيح",
                    value = currentConfig.clearButtonText,
                    onValueChange = { currentConfig = currentConfig.copy(clearButtonText = it) },
                    hasToggle = false,
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).clearButtonText,
                    tag = "lock_clear_button_text"
                )
            }

            // وصف زر الحذف
            item {
                TextItemEditorCard(
                    title = "وصف زر الحذف (Backspace)",
                    roleDescription = "وصف زر حذف الرقم الأخير في لوحة المفاتيح للمستخدمين وقارئات الشاشة",
                    value = currentConfig.backspaceDescription,
                    onValueChange = { currentConfig = currentConfig.copy(backspaceDescription = it) },
                    hasToggle = false,
                    defaultValue = LockScreenCustomTexts.defaultForLanguage(selectedLanguage).backspaceDescription,
                    tag = "lock_backspace_desc"
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // ==========================================
    // حوار المعاينة الحية (Live Interactive Preview)
    // ==========================================
    if (showPreviewModal) {
        Dialog(
            onDismissRequest = { showPreviewModal = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Biometric Lock Screen with currentConfig in PREVIEW mode
                BiometricLockScreen(
                    onVerifyPin = { pin ->
                        // In preview, 1234 succeeds
                        pin == "1234"
                    },
                    onTriggerBiometric = {
                        // Handled internally in preview
                    },
                    isBiometricAvailable = currentConfig.isBiometricButtonEnabled,
                    customTexts = currentConfig,
                    isPreview = true
                )

                // Top Floating Banner for Preview Controls
                Surface(
                    color = CyberCardElevated.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(CyberSuccess)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "معاينة تفاعلية حية لشاشة القفل",
                                    color = CyberPrimaryLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "يمكنك تجربة الضغط على الأرقام واختبار رمز 1234",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        IconButton(
                            onClick = { showPreviewModal = false },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .testTag("close_preview_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق المعاينة",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * بطاقة تعديل نص فردي مع خيارات الإظهار/الإخفاء والاستعادة للافتراضي
 */
@Composable
private fun TextItemEditorCard(
    title: String,
    roleDescription: String,
    value: String,
    onValueChange: (String) -> Unit,
    hasToggle: Boolean = false,
    isEnabled: Boolean = true,
    onToggleChange: (Boolean) -> Unit = {},
    defaultValue: String,
    tag: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (hasToggle && !isEnabled) CyberCard.copy(alpha = 0.5f) else CyberCard
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (hasToggle && !isEnabled) Color.Transparent else CyberPrimary.copy(alpha = 0.15f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("text_card_$tag")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = roleDescription,
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }

                if (hasToggle) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isEnabled) "مفعّل" else "معطّل",
                            color = if (isEnabled) CyberSuccess else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = onToggleChange,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberPrimary,
                                checkedTrackColor = CyberPrimary.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.testTag("toggle_$tag")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Text Input Field
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_$tag"),
                enabled = !hasToggle || isEnabled,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Bottom row: Reset to default button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (value != defaultValue) {
                    Text(
                        text = "تم التعديل",
                        color = CyberPrimaryLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "القيمة الافتراضية",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = "استعادة الافتراضي",
                    color = CyberPrimaryLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { onValueChange(defaultValue) }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                        .testTag("reset_$tag")
                )
            }
        }
    }
}

/**
 * ترويسة قسم منظم داخل شاشة التخصيص
 */
@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CyberPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}
