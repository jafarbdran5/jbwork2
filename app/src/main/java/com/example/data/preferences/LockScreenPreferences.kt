package com.example.data.preferences

import org.json.JSONObject

/**
 * نصوص شاشة قفل التطبيق وكلمة المرور القابلة للتخصيص الكامل
 * يدعم اللغتين العربية والإنجليزية بشكل مستقل، مع خيارات التعطيل والتفعيل
 */
data class LockScreenCustomTexts(
    val language: String = "ar",

    // 1. عنوان الشاشة
    val screenTitle: String = if (language == "en") "Jafar Bdran Forensics Platform" else "منظومة جعفر بدران للأدلة الجنائية",
    val isScreenTitleEnabled: Boolean = true,

    // 2. النص الترحيبي الرئيسي
    val greetingText: String = if (language == "en") "Welcome Back" else "مرحبًا بعودتك",
    val isGreetingEnabled: Boolean = true,

    // 3. النص الموجود أسفل العنوان
    val subtitleText: String = if (language == "en") "The system is fully encrypted and secured. Enter PIN to continue" else "المنظومة مشفرة ومؤمنة بالكامل. أدخل رمز PIN للمتابعة",
    val isSubtitleEnabled: Boolean = true,

    // 4. النص فوق حقل كلمة المرور / مؤشر الرمز
    val pinPromptText: String = if (language == "en") "Enter PIN Code" else "أدخل رمز PIN للمتابعة",
    val isPinPromptEnabled: Boolean = true,

    // 5. النص الإرشادي داخل حقل كلمة المرور / تلميح الرمز
    val pinPlaceholderText: String = if (language == "en") "4-digit security PIN" else "رمز الدخول المكون من 4 أرقام",
    val isPinPlaceholderEnabled: Boolean = true,

    // 6. النص الذي يظهر عند إدخال كلمة مرور خاطئة
    val errorMessageWrongPin: String = if (language == "en") "Incorrect PIN code. Please try again." else "رمز PIN غير صحيح. يرجى المحاولة ثانية.",

    // 7. النص عند فشل المصادقة
    val authFailedText: String = if (language == "en") "Biometric authentication failed. Please use PIN." else "فشلت المصادقة الحيوية. يرجى استخدام رمز PIN.",

    // 8. النص عند استخدام البصمة / زر البصمة
    val biometricButtonText: String = if (language == "en") "Biometric Authentication" else "المصادقة بالبصمة الحيوية",
    val isBiometricButtonEnabled: Boolean = true,
    val biometricKeypadLabel: String = if (language == "en") "Use Biometrics" else "استخدام البصمة",

    // 9. النص عند نجاح المصادقة أو فتح التطبيق
    val successText: String = if (language == "en") "Platform unlocked successfully" else "تم التحقق من الهوية وفتح المنظومة بنجاح",
    val isSuccessTextEnabled: Boolean = true,

    // 10. نصوص لوحة المفاتيح والتلميحات الثابتة
    val clearButtonText: String = if (language == "en") "Clear" else "مسح",
    val backspaceDescription: String = if (language == "en") "Delete Digit" else "حذف الرقم",
    val defaultPinHintText: String = if (language == "en") "Default PIN: 1234 (Can be changed in Security Log)" else "رمز PIN الافتراضي: 1234 (يمكن تعديله من سجل الأمان)",
    val isDefaultPinHintEnabled: Boolean = true
) {
    companion object {
        fun defaultForLanguage(lang: String): LockScreenCustomTexts {
            return if (lang == "en") {
                LockScreenCustomTexts(
                    language = "en",
                    screenTitle = "Jafar Bdran Forensics Platform",
                    isScreenTitleEnabled = true,
                    greetingText = "Welcome Back",
                    isGreetingEnabled = true,
                    subtitleText = "The system is fully encrypted and secured. Enter PIN to continue",
                    isSubtitleEnabled = true,
                    pinPromptText = "Enter PIN Code",
                    isPinPromptEnabled = true,
                    pinPlaceholderText = "4-digit security PIN",
                    isPinPlaceholderEnabled = true,
                    errorMessageWrongPin = "Incorrect PIN code. Please try again.",
                    authFailedText = "Biometric authentication failed. Please use PIN.",
                    biometricButtonText = "Biometric Authentication",
                    isBiometricButtonEnabled = true,
                    biometricKeypadLabel = "Use Biometrics",
                    clearButtonText = "Clear",
                    backspaceDescription = "Delete Digit",
                    successText = "Platform unlocked successfully",
                    isSuccessTextEnabled = true,
                    defaultPinHintText = "Default PIN: 1234 (Can be changed in Security Log)",
                    isDefaultPinHintEnabled = true
                )
            } else {
                LockScreenCustomTexts(
                    language = "ar",
                    screenTitle = "منظومة جعفر بدران للأدلة الجنائية",
                    isScreenTitleEnabled = true,
                    greetingText = "مرحبًا بعودتك",
                    isGreetingEnabled = true,
                    subtitleText = "المنظومة مشفرة ومؤمنة بالكامل. أدخل رمز PIN للمتابعة",
                    isSubtitleEnabled = true,
                    pinPromptText = "أدخل رمز PIN للمتابعة",
                    isPinPromptEnabled = true,
                    pinPlaceholderText = "رمز الدخول المكون من 4 أرقام",
                    isPinPlaceholderEnabled = true,
                    errorMessageWrongPin = "رمز PIN غير صحيح. يرجى المحاولة ثانية.",
                    authFailedText = "فشلت المصادقة الحيوية. يرجى استخدام رمز PIN.",
                    biometricButtonText = "المصادقة بالبصمة الحيوية",
                    isBiometricButtonEnabled = true,
                    biometricKeypadLabel = "استخدام البصمة",
                    clearButtonText = "مسح",
                    backspaceDescription = "حذف الرقم",
                    successText = "تم التحقق من الهوية وفتح المنظومة بنجاح",
                    isSuccessTextEnabled = true,
                    defaultPinHintText = "رمز PIN الافتراضي: 1234 (يمكن تعديله من سجل الأمان)",
                    isDefaultPinHintEnabled = true
                )
            }
        }

        fun toJson(config: LockScreenCustomTexts): String {
            val json = JSONObject()
            json.put("language", config.language)
            json.put("screenTitle", config.screenTitle)
            json.put("isScreenTitleEnabled", config.isScreenTitleEnabled)
            json.put("greetingText", config.greetingText)
            json.put("isGreetingEnabled", config.isGreetingEnabled)
            json.put("subtitleText", config.subtitleText)
            json.put("isSubtitleEnabled", config.isSubtitleEnabled)
            json.put("pinPromptText", config.pinPromptText)
            json.put("isPinPromptEnabled", config.isPinPromptEnabled)
            json.put("pinPlaceholderText", config.pinPlaceholderText)
            json.put("isPinPlaceholderEnabled", config.isPinPlaceholderEnabled)
            json.put("errorMessageWrongPin", config.errorMessageWrongPin)
            json.put("authFailedText", config.authFailedText)
            json.put("biometricButtonText", config.biometricButtonText)
            json.put("isBiometricButtonEnabled", config.isBiometricButtonEnabled)
            json.put("biometricKeypadLabel", config.biometricKeypadLabel)
            json.put("clearButtonText", config.clearButtonText)
            json.put("backspaceDescription", config.backspaceDescription)
            json.put("successText", config.successText)
            json.put("isSuccessTextEnabled", config.isSuccessTextEnabled)
            json.put("defaultPinHintText", config.defaultPinHintText)
            json.put("isDefaultPinHintEnabled", config.isDefaultPinHintEnabled)
            return json.toString()
        }

        fun fromJson(jsonStr: String?, language: String): LockScreenCustomTexts {
            if (jsonStr.isNullOrBlank()) return defaultForLanguage(language)
            return try {
                val obj = JSONObject(jsonStr)
                val def = defaultForLanguage(language)
                LockScreenCustomTexts(
                    language = language,
                    screenTitle = obj.optString("screenTitle", def.screenTitle),
                    isScreenTitleEnabled = obj.optBoolean("isScreenTitleEnabled", def.isScreenTitleEnabled),
                    greetingText = obj.optString("greetingText", def.greetingText),
                    isGreetingEnabled = obj.optBoolean("isGreetingEnabled", def.isGreetingEnabled),
                    subtitleText = obj.optString("subtitleText", def.subtitleText),
                    isSubtitleEnabled = obj.optBoolean("isSubtitleEnabled", def.isSubtitleEnabled),
                    pinPromptText = obj.optString("pinPromptText", def.pinPromptText),
                    isPinPromptEnabled = obj.optBoolean("isPinPromptEnabled", def.isPinPromptEnabled),
                    pinPlaceholderText = obj.optString("pinPlaceholderText", def.pinPlaceholderText),
                    isPinPlaceholderEnabled = obj.optBoolean("isPinPlaceholderEnabled", def.isPinPlaceholderEnabled),
                    errorMessageWrongPin = obj.optString("errorMessageWrongPin", def.errorMessageWrongPin),
                    authFailedText = obj.optString("authFailedText", def.authFailedText),
                    biometricButtonText = obj.optString("biometricButtonText", def.biometricButtonText),
                    isBiometricButtonEnabled = obj.optBoolean("isBiometricButtonEnabled", def.isBiometricButtonEnabled),
                    biometricKeypadLabel = obj.optString("biometricKeypadLabel", def.biometricKeypadLabel),
                    clearButtonText = obj.optString("clearButtonText", def.clearButtonText),
                    backspaceDescription = obj.optString("backspaceDescription", def.backspaceDescription),
                    successText = obj.optString("successText", def.successText),
                    isSuccessTextEnabled = obj.optBoolean("isSuccessTextEnabled", def.isSuccessTextEnabled),
                    defaultPinHintText = obj.optString("defaultPinHintText", def.defaultPinHintText),
                    isDefaultPinHintEnabled = obj.optBoolean("isDefaultPinHintEnabled", def.isDefaultPinHintEnabled)
                )
            } catch (e: Exception) {
                defaultForLanguage(language)
            }
        }
    }
}
