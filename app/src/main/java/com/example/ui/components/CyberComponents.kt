package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberInfo
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSuccess
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.security.MessageDigest

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CyberBorder,
    backgroundColor: Color = CyberCard,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick ?: {},
        enabled = onClick != null,
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Box(modifier = Modifier.padding(14.dp)) {
            content()
        }
    }
}

@Composable
fun CyberBadge(
    text: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(accentColor.copy(alpha = 0.14f))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = accentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PriorityBadge(priority: String) {
    val color = when (priority) {
        "حرجة", "Critical" -> CyberDanger
        "عالية", "High" -> CyberWarning
        "متوسطة", "Medium" -> CyberInfo
        else -> CyberSuccess
    }
    CyberBadge(text = priority, accentColor = color)
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "جديدة", "New" -> CyberPrimary
        "قيد المتابعة", "قيد التحقيق", "Under Review", "Investigation" -> CyberSecondary
        "قيد التنفيذ", "In Progress" -> CyberInfo
        "إحالة للجهات المختصة", "إحالة للجهات الأمنية", "Transferred" -> CyberWarning
        "تم الإنجاز بنجاح", "تم الحل بنجاح", "Resolved", "Completed" -> CyberSuccess
        else -> TextSecondary
    }
    CyberBadge(text = status, accentColor = color)
}

@Composable
fun PlatformBadge(platform: String) {
    val color = when (platform) {
        "Instagram" -> Color(0xFFE1306C)
        "X", "Twitter" -> Color(0xFF1DA1F2)
        "Telegram" -> Color(0xFF229ED9)
        "TikTok" -> Color(0xFF00F2FE)
        "Facebook" -> Color(0xFF1877F2)
        "YouTube" -> Color(0xFFFF0000)
        "LinkedIn" -> Color(0xFF0A66C2)
        "Snapchat" -> Color(0xFFFFFC00)
        else -> CyberPrimary
    }
    CyberBadge(text = platform, accentColor = color)
}

/**
 * Non-blocking in-app confirmation bottom sheet for destructive operations
 * (Avoids any native OS dialogs or window token leaks).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppConfirmationSheet(
    title: String,
    description: String,
    confirmLabel: String = "تأكيد الحذف",
    isDestructive: Boolean = true,
    sheetState: SheetState = rememberModalBottomSheetState(),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CyberSurface,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(CyberBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .testTag("in_app_confirmation_sheet")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isDestructive) CyberDanger.copy(alpha = 0.15f) else CyberWarning.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDestructive) Icons.Default.Delete else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isDestructive) CyberDanger else CyberWarning,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("confirm_sheet_cancel"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("إلغاء", fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("confirm_sheet_action"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDestructive) CyberDanger else CyberPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(confirmLabel, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Forensic Hashing Utilities
 */
object ForensicCrypto {
    fun calculateMd5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun calculateSha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun launchReverseSearch(context: Context, engine: String, query: String = "") {
        val url = when (engine) {
            "Google Lens" -> "https://lens.google.com/"
            "Yandex" -> "https://yandex.com/images/"
            "Bing Visual" -> "https://www.bing.com/visualsearch"
            else -> "https://images.google.com/"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun openWhatsApp(context: Context, phone: String, message: String) {
        val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    fun openDialer(context: Context, phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(phone)}"))
        context.startActivity(intent)
    }
}
