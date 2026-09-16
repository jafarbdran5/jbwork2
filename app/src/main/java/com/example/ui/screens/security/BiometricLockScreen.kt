package com.example.ui.screens.security

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.LockScreenCustomTexts
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberPrimaryLight
import com.example.ui.theme.CyberSuccess
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun BiometricLockScreen(
    onVerifyPin: (String) -> Boolean,
    onTriggerBiometric: () -> Unit,
    isBiometricAvailable: Boolean = true,
    customTexts: LockScreenCustomTexts = LockScreenCustomTexts(),
    isPreview: Boolean = false
) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    fun handleDigit(digit: String) {
        if (isSuccess) return
        if (enteredPin.length < 4) {
            val newPin = enteredPin + digit
            enteredPin = newPin
            isError = false
            errorMessage = ""

            if (newPin.length == 4) {
                if (isPreview) {
                    // Preview verification: 1234 or any 4 digits simulates success if matches, or can test wrong pin
                    val isDemoValid = (newPin == "1234")
                    if (isDemoValid) {
                        isSuccess = true
                        scope.launch {
                            delay(1400)
                            isSuccess = false
                            enteredPin = ""
                        }
                    } else {
                        isError = true
                        errorMessage = customTexts.errorMessageWrongPin
                        scope.launch {
                            for (i in 0..2) {
                                shakeOffset.animateTo(20f, tween(50))
                                shakeOffset.animateTo(-20f, tween(50))
                            }
                            shakeOffset.animateTo(0f, tween(50))
                            delay(600)
                            enteredPin = ""
                        }
                    }
                } else {
                    val success = onVerifyPin(newPin)
                    if (success) {
                        if (customTexts.isSuccessTextEnabled && customTexts.successText.isNotBlank()) {
                            isSuccess = true
                        }
                    } else {
                        isError = true
                        errorMessage = customTexts.errorMessageWrongPin
                        scope.launch {
                            // Shake animation
                            for (i in 0..2) {
                                shakeOffset.animateTo(20f, tween(50))
                                shakeOffset.animateTo(-20f, tween(50))
                            }
                            shakeOffset.animateTo(0f, tween(50))
                            delay(400)
                            enteredPin = ""
                        }
                    }
                }
            }
        }
    }

    fun handleBackspace() {
        if (isSuccess) return
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            isError = false
            errorMessage = ""
        }
    }

    fun handleClear() {
        if (isSuccess) return
        enteredPin = ""
        isError = false
        errorMessage = ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header Security Shield / Success Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isSuccess -> CyberSuccess.copy(alpha = 0.15f)
                            isError -> CyberDanger.copy(alpha = 0.12f)
                            else -> CyberPrimary.copy(alpha = 0.12f)
                        }
                    )
                    .border(
                        2.dp,
                        when {
                            isSuccess -> CyberSuccess
                            isError -> CyberDanger
                            else -> CyberPrimary
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        isSuccess -> Icons.Default.CheckCircle
                        isError -> Icons.Default.Lock
                        else -> Icons.Default.Security
                    },
                    contentDescription = "أمان المنظومة",
                    tint = when {
                        isSuccess -> CyberSuccess
                        isError -> CyberDanger
                        else -> CyberPrimary
                    },
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. عنوان الشاشة
            if (customTexts.isScreenTitleEnabled && customTexts.screenTitle.isNotBlank()) {
                Text(
                    text = customTexts.screenTitle,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 2. النص الترحيبي الرئيسي
            if (customTexts.isGreetingEnabled && customTexts.greetingText.isNotBlank()) {
                Text(
                    text = customTexts.greetingText,
                    color = CyberPrimaryLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 3. النص الموجود أسفل العنوان
            if (customTexts.isSubtitleEnabled && customTexts.subtitleText.isNotBlank()) {
                Text(
                    text = customTexts.subtitleText,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            // 4. النص فوق حقل كلمة المرور / رمز PIN
            if (customTexts.isPinPromptEnabled && customTexts.pinPromptText.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = customTexts.pinPromptText,
                    color = TextPrimary.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // PIN Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.testTag("pin_dots_row")
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < enteredPin.length
                    val dotColor = when {
                        isSuccess -> CyberSuccess
                        isError -> CyberDanger
                        isFilled -> CyberPrimary
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                            .border(
                                width = 1.5.dp,
                                color = when {
                                    isSuccess -> CyberSuccess
                                    isError -> CyberDanger
                                    else -> CyberPrimaryLight
                                },
                                shape = CircleShape
                            )
                    )
                }
            }

            // 5. النص الإرشادي داخل حقل كلمة المرور (عندما لا يوجد إدخال)
            Box(
                modifier = Modifier.height(26.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isSuccess && customTexts.isSuccessTextEnabled && customTexts.successText.isNotBlank()) {
                    Text(
                        text = customTexts.successText,
                        color = CyberSuccess,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                } else if (isError && errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        color = CyberDanger,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                } else if (enteredPin.isEmpty() && customTexts.isPinPlaceholderEnabled && customTexts.pinPlaceholderText.isNotBlank()) {
                    Text(
                        text = customTexts.pinPlaceholderText,
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Numeric Keypad Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Row 1: 1, 2, 3
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    KeypadNumberButton(number = "1", onClick = { handleDigit("1") })
                    KeypadNumberButton(number = "2", onClick = { handleDigit("2") })
                    KeypadNumberButton(number = "3", onClick = { handleDigit("3") })
                }

                // Row 2: 4, 5, 6
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    KeypadNumberButton(number = "4", onClick = { handleDigit("4") })
                    KeypadNumberButton(number = "5", onClick = { handleDigit("5") })
                    KeypadNumberButton(number = "6", onClick = { handleDigit("6") })
                }

                // Row 3: 7, 8, 9
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    KeypadNumberButton(number = "7", onClick = { handleDigit("7") })
                    KeypadNumberButton(number = "8", onClick = { handleDigit("8") })
                    KeypadNumberButton(number = "9", onClick = { handleDigit("9") })
                }

                // Row 4: Biometric/Clear, 0, Backspace
                Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    if (isBiometricAvailable) {
                        KeypadActionButton(
                            icon = Icons.Default.Fingerprint,
                            contentDescription = customTexts.biometricKeypadLabel,
                            onClick = {
                                if (isPreview) {
                                    isSuccess = true
                                    scope.launch {
                                        delay(1200)
                                        isSuccess = false
                                    }
                                } else {
                                    onTriggerBiometric()
                                }
                            },
                            tag = "biometric_prompt_button",
                            tint = CyberPrimaryLight
                        )
                    } else {
                        KeypadTextActionButton(
                            text = customTexts.clearButtonText,
                            onClick = { handleClear() },
                            tag = "pin_clear_button"
                        )
                    }

                    KeypadNumberButton(number = "0", onClick = { handleDigit("0") })

                    KeypadActionButton(
                        icon = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = customTexts.backspaceDescription,
                        onClick = { handleBackspace() },
                        tag = "pin_backspace_button",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 8. زر المصادقة بالبصمة الحيوية
            if (isBiometricAvailable && customTexts.isBiometricButtonEnabled && customTexts.biometricButtonText.isNotBlank()) {
                Button(
                    onClick = {
                        if (isPreview) {
                            isSuccess = true
                            scope.launch {
                                delay(1200)
                                isSuccess = false
                            }
                        } else {
                            onTriggerBiometric()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberPrimary.copy(alpha = 0.15f),
                        contentColor = CyberPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(48.dp)
                        .testTag("biometric_direct_auth_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = customTexts.biometricButtonText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 10. تلميح الرمز الافتراضي
            if (customTexts.isDefaultPinHintEnabled && customTexts.defaultPinHintText.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = customTexts.defaultPinHintText,
                    color = TextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun KeypadNumberButton(
    number: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = CyberCardElevated,
        modifier = Modifier
            .size(68.dp)
            .testTag("pin_key_$number")
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), CircleShape)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = number,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun KeypadActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tag: String,
    tint: Color
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = CyberCardElevated,
        modifier = Modifier
            .size(68.dp)
            .testTag(tag)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), CircleShape)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun KeypadTextActionButton(
    text: String,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = CyberCardElevated,
        modifier = Modifier
            .size(68.dp)
            .testTag(tag)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), CircleShape)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
