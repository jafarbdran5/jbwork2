package com.example.ui.screens.security

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.AuditLogEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.ForensicCrypto
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
import com.example.ui.viewmodel.ForensicViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val EMERGENCY_HOTLINES = listOf(
    Pair("السعودية (مكافحة الجرائم المعلوماتية - كلنا أمن)", "991"),
    Pair("الإمارات (الشرطة الإلكترونية eCrime)", "901"),
    Pair("الكويت (إدارة مكافحة الجرائم الإلكترونية)", "+96525360400"),
    Pair("قطر (إدارة مكافحة الجرائم الاقتصادية والإلكترونية)", "66815757"),
    Pair("مصر (مكافحة جرائم الحاسبات وشبكات المعلومات)", "108"),
    Pair("الأردن (وحدة مكافحة الجرائم الإلكترونية)", "192")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditAndSecurityScreen(
    viewModel: ForensicViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val isBiometricUnlocked by viewModel.isBiometricUnlocked.collectAsStateWithLifecycle()
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsStateWithLifecycle()
    val lastSync by viewModel.lastSyncTimestamp.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("مركز التحكم والأمان", "سجل التدقيق الأمني (Audit Log)", "أرقام الطوارئ والمصادر")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen Header
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
                            text = "الأمان، سجل التدقيق، والصلاحيات",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "إدارة الصلاحيات، القفل الحيوي، ومتابعة سجل العمليات",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    CyberBadge(
                        text = if (isCloudSyncing) "جاري المزامنة..." else "متصل ومؤمّن",
                        accentColor = if (isCloudSyncing) CyberWarning else CyberSuccess
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = CyberSurface,
                    contentColor = CyberPrimary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontSize = 11.sp, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }

            // Tab 0: Security & Access Control
            if (selectedTabIndex == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Role Selector Card
                    CyberCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CyberCardElevated) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SupervisorAccount, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("صلاحيات الدور النشط (RBAC):", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("الدور الحالي: $currentRole", color = CyberPrimaryLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                viewModel.roles.forEach { role ->
                                    val isSelected = currentRole == role
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.setRole(role) },
                                        label = { Text(role, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = CyberPrimary,
                                            selectedLabelColor = Color.White
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // Biometric App Lock Card
                    CyberCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CyberCardElevated) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isBiometricUnlocked) CyberSuccess.copy(alpha = 0.15f) else CyberDanger.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isBiometricUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (isBiometricUnlocked) CyberSuccess else CyberDanger,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("القفل البيومتري وحماية الشاشة", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        text = if (isBiometricUnlocked) "المنصة مفعلة ومفتوحة" else "المنصة مقفلة بالبصمة",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Switch(
                                checked = isBiometricUnlocked,
                                onCheckedChange = { viewModel.toggleBiometricLock() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberSuccess,
                                    checkedTrackColor = CyberSuccess.copy(alpha = 0.3f),
                                    uncheckedThumbColor = CyberDanger,
                                    uncheckedTrackColor = CyberDanger.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }

                    // Language Switcher Card
                    CyberCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CyberCardElevated) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("لغة الواجهة (Interface Language)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("العربية (الأساسية) / English", color = TextSecondary, fontSize = 12.sp)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { viewModel.setLanguage("ar") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentLang == "ar") CyberPrimary else CyberSurface
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("العربية", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { viewModel.setLanguage("en") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentLang == "en") CyberPrimary else CyberSurface
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("English", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Offline-First Storage & Cloud Sync Status
                    CyberCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CyberSurface) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = CyberSuccess, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("التخزين الثنائي المتزامن (Dual-Layer Architecture):", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• الطبقة الأولى: قاعدة بيانات Room المحلية المشفرة (استجابة 0ms).\n• الطبقة الثانية: قناة المزامنة السحابية الخلفية مع Firestore لنسخ الحالات والملفات تلقائياً.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(lastSync))
                            Text("آخر نبضة مزامنة ناجحة: $dateStr", color = CyberPrimaryLight, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }

            // Tab 1: Audit Log Stream
            if (selectedTabIndex == 1) {
                if (auditLogs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("سجل التدقيق فارغ حالياً.", color = TextSecondary)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(auditLogs, key = { it.id }) { log ->
                            AuditLogItemCard(log = log)
                        }
                        item {
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }
            }

            // Tab 2: Emergency Hotlines & Portal Integrations
            if (selectedTabIndex == 2) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "خطوط الاستجابة للطوارئ والجرائم الإلكترونية",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "اتصال مباشر بأجهزة الأمن المعنية بالجرائم السيبرانية في العالم العربي",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    EMERGENCY_HOTLINES.forEach { (country, phone) ->
                        CyberCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = CyberCardElevated
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(country, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(phone, color = CyberPrimaryLight, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                }

                                Button(
                                    onClick = { ForensicCrypto.openDialer(context, phone) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberSuccess),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PhoneInTalk, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("اتصال", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("بوابات الحماية العالمية المعتمدة:", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://stopncii.org/"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberDanger),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("StopNCII.org - منع نشر الصور الخاصة دولياً")
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/records/login/"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Meta LERS - بوابة إنفاذ القانون لحظر الحسابات")
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun AuditLogItemCard(log: AuditLogEntity) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(log.timestamp))
    val actionColor = when (log.actionType) {
        "CREATE" -> CyberSuccess
        "EDIT" -> CyberInfo
        "DELETE" -> CyberDanger
        "RESTORE" -> CyberWarning
        "RELOAD_LIBRARY" -> CyberPrimaryLight
        else -> CyberSecondary
    }

    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("audit_log_${log.id}"),
        backgroundColor = CyberCardElevated
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CyberBadge(text = log.actionType, accentColor = actionColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    CyberBadge(text = log.module, accentColor = CyberSecondary)
                }
                Text(text = dateStr, color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = log.details,
                color = TextPrimary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "بواسطة: ${log.performedBy} (${log.userRole})",
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}
