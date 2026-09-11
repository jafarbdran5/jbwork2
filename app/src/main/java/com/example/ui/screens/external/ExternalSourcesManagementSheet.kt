package com.example.ui.screens.external

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.ExternalRequestSourceEntity
import com.example.ui.viewmodel.ForensicViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExternalSourcesManagementSheet(
    viewModel: ForensicViewModel,
    onDismiss: () -> Unit,
    onOpenAddSource: () -> Unit
) {
    val sources by viewModel.rawExternalSources.collectAsState()
    val allSheets by viewModel.rawExternalSheets.collectAsState()
    val allRequests by viewModel.rawExternalRequests.collectAsState()

    var managingSheetsForSource by remember { mutableStateOf<ExternalRequestSourceEntity?>(null) }
    var sourceToDelete by remember { mutableStateOf<ExternalRequestSourceEntity?>(null) }
    var sourceToEdit by remember { mutableStateOf<ExternalRequestSourceEntity?>(null) }

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
                    Column {
                        Text(
                            text = "إدارة مصادر Google Sheets",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "إجمالي المصادر المسجلة: ${sources.size}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onOpenAddSource,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إضافة مصدر جديد", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.syncAllExternalSources() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مزامنة الكل", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (sources.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "لا توجد أي مصادر Google Sheets مضافة حالياً.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = onOpenAddSource, shape = RoundedCornerShape(8.dp)) {
                                Text("إضافة أول مصدر الآن", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(sources, key = { it.id }) { src ->
                            val sheetsCount = allSheets.count { it.sourceId == src.id }
                            val requestsCount = allRequests.count { it.sourceId == src.id }

                            SourceItemCard(
                                source = src,
                                sheetsCount = sheetsCount,
                                requestsCount = requestsCount,
                                onToggleEnabled = { viewModel.toggleExternalSource(src.id, it) },
                                onSyncNow = { viewModel.syncExternalSource(src.id) },
                                onManageSheets = { managingSheetsForSource = src },
                                onEdit = { sourceToEdit = src },
                                onDelete = { sourceToDelete = src }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("إغلاق", fontSize = 13.sp)
                }
            }
        }
    }

    // Sheets Manager Dialog
    managingSheetsForSource?.let { src ->
        ExternalSheetsManagerDialog(
            source = src,
            viewModel = viewModel,
            onDismiss = { managingSheetsForSource = null }
        )
    }

    // Delete Confirmation Dialog
    sourceToDelete?.let { src ->
        AlertDialog(
            onDismissRequest = { sourceToDelete = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("حذف المصدر من التطبيق") },
            text = {
                Text(
                    text = "هل أنت متأكد من رغبتك في إزالة المصدر «${src.name}»؟\n\nتأكيد أمني: سيتم حذف بيانات المصدر والطلبات التابعة له من داخل التطبيق فقط، ولن يتم تعديل أو حذف ملف Google Sheet الأصلي نهائياً.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExternalSource(src.id, src.name)
                        sourceToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("تأكيد الحذف من التطبيق")
                }
            },
            dismissButton = {
                TextButton(onClick = { sourceToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Edit Source Dialog
    sourceToEdit?.let { src ->
        EditSourceDialog(
            source = src,
            viewModel = viewModel,
            onDismiss = { sourceToEdit = null }
        )
    }
}

@Composable
private fun SourceItemCard(
    source: ExternalRequestSourceEntity,
    sheetsCount: Int,
    requestsCount: Int,
    onToggleEnabled: (Boolean) -> Unit,
    onSyncNow: () -> Unit,
    onManageSheets: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (source.enabled) MaterialTheme.colorScheme.outlineVariant else Color.Gray.copy(alpha = 0.2f),
                RoundedCornerShape(14.dp)
            )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Title & Enabled Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (source.status.contains("خطأ")) Color(0xFFD32F2F).copy(alpha = 0.15f) else Color(0xFF388E3C).copy(alpha = 0.15f),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = source.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (source.status.contains("خطأ")) Color(0xFFD32F2F) else Color(0xFF388E3C),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = source.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "ID: ${source.spreadsheetId.take(15)}...",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = source.enabled,
                    onCheckedChange = onToggleEnabled,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "الأوراق: $sheetsCount • الطلبات المستوردة: $requestsCount",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (source.lastSync != null) {
                        val fmt = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US)
                        "آخر مزامنة: ${fmt.format(Date(source.lastSync))}"
                    } else "لم تتم المزامنة بعد",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onSyncNow,
                        shape = RoundedCornerShape(8.dp),
                        enabled = source.enabled
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مزامنة", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onManageSheets,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إدارة الأوراق", fontSize = 11.sp)
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EditSourceDialog(
    source: ExternalRequestSourceEntity,
    viewModel: ForensicViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(source.name) }
    var url by remember { mutableStateOf(source.publicUrl) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = "تعديل بيانات المصدر", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Text("اسم المصدر:", fontSize = 12.sp)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("رابط Google Sheet العام:", fontSize = 12.sp)
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("إلغاء")
                    }
                    Button(
                        onClick = {
                            val newSpreadsheetId = com.example.data.remote.GoogleSheetsPublicService.extractSpreadsheetId(url)
                            viewModel.updateExternalSource(
                                source.copy(
                                    name = name.ifBlank { source.name },
                                    publicUrl = url.ifBlank { source.publicUrl },
                                    spreadsheetId = newSpreadsheetId.ifBlank { source.spreadsheetId }
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("حفظ التعديل")
                    }
                }
            }
        }
    }
}
