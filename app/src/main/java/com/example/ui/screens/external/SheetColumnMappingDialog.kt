package com.example.ui.screens.external

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.ExternalRequestSourceEntity
import com.example.data.local.entities.ExternalSheetEntity
import com.example.data.remote.GoogleSheetsPublicService
import com.example.ui.viewmodel.ForensicViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

@Composable
fun SheetColumnMappingDialog(
    source: ExternalRequestSourceEntity,
    sheet: ExternalSheetEntity,
    viewModel: ForensicViewModel,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var detectedHeaders by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoadingHeaders by remember { mutableStateOf(true) }
    var previewRowsCount by remember { mutableStateOf(0) }

    // Mapping state: Standard Field -> Selected Header Name
    val mapping = remember { mutableStateMapOf<String, String>() }

    // Initialize mapping from existing JSON
    LaunchedEffect(sheet.columnMappingJson) {
        try {
            if (sheet.columnMappingJson.isNotBlank() && sheet.columnMappingJson != "{}") {
                val obj = JSONObject(sheet.columnMappingJson)
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    mapping[k] = obj.optString(k)
                }
            }
        } catch (_: Exception) {}

        // Fetch sheet headers via service
        withContext(Dispatchers.IO) {
            val service = GoogleSheetsPublicService()
            val result = service.readSheetData(
                publicUrl = source.publicUrl,
                sheetName = sheet.sheetName,
                sheetGid = sheet.sheetId
            )
            withContext(Dispatchers.Main) {
                isLoadingHeaders = false
                if (result.isSuccess) {
                    val data = result.getOrThrow()
                    detectedHeaders = data.detectedHeaders
                    previewRowsCount = data.rowCount
                }
            }
        }
    }

    val standardFields = listOf(
        "clientName" to "اسم العميل / صاحب الطلب",
        "clientPhone" to "رقم الهاتف / الجوال / الواتساب",
        "clientEmail" to "البريد الإلكتروني",
        "description" to "تفاصيل المشكلة / محتوى الطلب",
        "urgency" to "درجة الأهمية / مستوى الخطر",
        "receivedAt" to "تاريخ ووقت استلام الطلب",
        "internalNotes" to "الملاحظات أو التعليقات"
    )

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
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ربط أعمدة الورقة يدويًا",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = sheet.customDisplayName ?: sheet.sheetName,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "حدد أي عمود من ملف Google Sheet يناظر كل حقل في النظام. الحقول غير المعينة سيتم اكتشافها تلقائياً أو حفظها كبيانات إضافية.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoadingHeaders) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("جاري فحص أعمدة الورقة من Google Sheets...", fontSize = 12.sp)
                    }
                } else if (detectedHeaders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("تعذر قراءة عناوين الأعمدة من الورقة. يرجى التأكد من اتصال الإنترنت وصلاحيات الملف.", fontSize = 12.sp)
                    }
                } else {
                    Text(
                        text = "الأعمدة المكتشفة في الورقة (${detectedHeaders.size} عمود):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(standardFields) { (fieldKey, fieldLabel) ->
                            val currentMapped = mapping[fieldKey] ?: ""
                            var dropdownExpanded by remember { mutableStateOf(false) }

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = fieldLabel,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (currentMapped.isNotBlank()) "مرتبط بالعمود: «$currentMapped»" else "تلقائي (ذكاء اصطناعي)",
                                            fontSize = 11.sp,
                                            color = if (currentMapped.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Box {
                                        OutlinedButton(
                                            onClick = { dropdownExpanded = true },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text(
                                                text = if (currentMapped.isNotBlank()) currentMapped else "اختر العمود",
                                                fontSize = 11.sp
                                            )
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                        }

                                        DropdownMenu(
                                            expanded = dropdownExpanded,
                                            onDismissRequest = { dropdownExpanded = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("تلقائي (التعرف الذكي)", fontWeight = FontWeight.Bold) },
                                                onClick = {
                                                    dropdownExpanded = false
                                                    mapping.remove(fieldKey)
                                                }
                                            )
                                            HorizontalDivider()
                                            detectedHeaders.forEach { header ->
                                                DropdownMenuItem(
                                                    text = { Text(header) },
                                                    onClick = {
                                                        dropdownExpanded = false
                                                        mapping[fieldKey] = header
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("إلغاء", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.updateSheetColumnMapping(sheet.id, mapping.toMap())
                            viewModel.syncSingleSheet(source.id, sheet.id, sheet.customDisplayName ?: sheet.sheetName)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حفظ ومزامنة الورقة", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
