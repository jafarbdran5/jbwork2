package com.example.ui.screens.external

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.viewmodel.ForensicViewModel

@Composable
fun AddExternalSourceDialog(
    viewModel: ForensicViewModel,
    onDismiss: () -> Unit,
    onSourceAdded: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var sourceName by remember { mutableStateOf("") }
    var publicUrl by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    // CSV Direct Mode State
    var csvName by remember { mutableStateOf("جدول الحدائق الوطنية") }
    var csvContent by remember {
        mutableStateOf(
            """Name,State,Phone,Location
Acadia,Maine,207-288-3338,"44.35,-68.21"
American Samoa,American Samoa,684-633-7082,"14.25,-170.68"
Arches,Utah,435-719-2299,"38.68,-109.57"
Badlands,South Dakota,(605) 433-5361,"43.75,-102.5"
Big Bend,Texas,432-477-2251,"29.25,-103.25"
Biscayne,Florida,305-230-1144,"25.65,-80.08""""
        )
    }

    var isTesting by remember { mutableStateOf(false) }
    var testStatus by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var isAdding by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "إضافة مصدر طلبات خارجي",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "ربط Google Sheets حي أو استيراد جدول بيانات مباشر",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("رابط Google Sheet", fontSize = 12.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("لصق جدول / CSV مباشر", fontSize = 12.sp)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTab == 0) {
                    // Google Sheets URL Mode
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.WifiTethering,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "لا يتطلب تسجيل الدخول. يكتشف النظام جميع الأوراق تلقائياً بمجرد إتاحة الرابط العام: «أي شخص لديه الرابط يمكنه العرض».",
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("اسم المصدر التوصيفي:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = sourceName,
                        onValueChange = { sourceName = it },
                        placeholder = { Text("مثال: طلبات الموقع العام أو نموذج الشكاوى", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("رابط ملف Google Sheet العام:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = publicUrl,
                        onValueChange = {
                            publicUrl = it
                            testStatus = null
                        },
                        placeholder = { Text("https://docs.google.com/spreadsheets/d/...", fontSize = 11.sp) },
                        trailingIcon = {
                            IconButton(onClick = {
                                val clip = clipboardManager.getText()?.text
                                if (!clip.isNullOrBlank()) {
                                    publicUrl = clip.trim()
                                    testStatus = null
                                }
                            }) {
                                Icon(Icons.Default.ContentPaste, contentDescription = "لصق من الحافظة", modifier = Modifier.size(18.dp))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            if (publicUrl.isNotBlank()) {
                                isTesting = true
                                testStatus = null
                                viewModel.testExternalSourceConnection(publicUrl) { success, msg, detectedTitle ->
                                    isTesting = false
                                    testStatus = Pair(success, msg)
                                    if (success && sourceName.isBlank() && detectedTitle.isNotBlank()) {
                                        sourceName = detectedTitle
                                    }
                                }
                            }
                        },
                        enabled = publicUrl.isNotBlank() && !isTesting,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جاري فحص إمكانية الوصول للملف...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فحص الاتصال والقراءة الآن", fontSize = 12.sp)
                        }
                    }

                    testStatus?.let { (success, msg) ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = if (success) Color(0xFF388E3C).copy(alpha = 0.12f) else Color(0xFFD32F2F).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    if (success) Color(0xFF388E3C).copy(alpha = 0.4f) else Color(0xFFD32F2F).copy(alpha = 0.4f),
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (success) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = if (success) Color(0xFF388E3C) else Color(0xFFD32F2F),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    fontSize = 11.sp,
                                    color = if (success) Color(0xFF388E3C) else Color(0xFFD32F2F),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                } else {
                    // Direct CSV / Table Paste Mode
                    Surface(
                        color = Color(0xFF0288D1).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF0288D1),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "تجربة واختبار التعرف على البيانات والصفوف مباشرة",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "يمكنك لصق بيانات جدول أو CSV للتحقق من قراءة الحقول (الاسم، الهاتف، الموقع) دون الحاجة للاتصال بالإنترنت.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("اسم المصدر:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = csvName,
                        onValueChange = { csvName = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("محتوى الجدول (CSV):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row {
                            OutlinedButton(
                                onClick = {
                                    val clip = clipboardManager.getText()?.text
                                    if (!clip.isNullOrBlank()) {
                                        csvContent = clip
                                    }
                                },
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("لصق", fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = csvContent,
                        onValueChange = { csvContent = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        shape = RoundedCornerShape(10.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Detection Summary
                    val lines = csvContent.lines().filter { it.isNotBlank() }
                    val headers = if (lines.isNotEmpty()) lines[0].split(",").map { it.trim().removeSurrounding("\"") } else emptyList()
                    val rowCount = if (lines.size > 1) lines.size - 1 else 0

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("تحليل وتعيين الأعمدة التلقائي:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• الأعمدة المكتشفة: ${headers.joinToString(" | ")}", fontSize = 11.sp)
                            Text("• عمود الاسم/الجهة: ${headers.firstOrNull { it.equals("Name", ignoreCase = true) } ?: "غير محدد"}", fontSize = 11.sp)
                            Text("• عمود الهاتف: ${headers.firstOrNull { it.equals("Phone", ignoreCase = true) } ?: "غير محدد"}", fontSize = 11.sp)
                            Text("• الحقول الجغرافية/الإضافية: ${headers.filter { !it.equals("Name", ignoreCase = true) && !it.equals("Phone", ignoreCase = true) }.joinToString("، ")}", fontSize = 11.sp)
                            Text("• إجمالي الصفوف الجاهزة للاستيراد: $rowCount صفوف", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("إلغاء", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            if (selectedTab == 0) {
                                if (publicUrl.isNotBlank()) {
                                    isAdding = true
                                    val finalName = sourceName.ifBlank { "مصدر خارجي ${System.currentTimeMillis() % 1000}" }
                                    viewModel.addExternalSource(finalName, publicUrl) { success, _ ->
                                        isAdding = false
                                        if (success) {
                                            onSourceAdded()
                                            onDismiss()
                                        }
                                    }
                                }
                            } else {
                                if (csvContent.isNotBlank()) {
                                    isAdding = true
                                    val finalName = csvName.ifBlank { "جدول مستورد ${System.currentTimeMillis() % 1000}" }
                                    viewModel.addCsvSource(finalName, csvContent) { success, _ ->
                                        isAdding = false
                                        if (success) {
                                            onSourceAdded()
                                            onDismiss()
                                        }
                                    }
                                }
                            }
                        },
                        enabled = (if (selectedTab == 0) publicUrl.isNotBlank() else csvContent.isNotBlank()) && !isAdding,
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isAdding) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("جاري الحفظ...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (selectedTab == 0) "إضافة المصدر" else "استيراد وتجربة البيانات", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
