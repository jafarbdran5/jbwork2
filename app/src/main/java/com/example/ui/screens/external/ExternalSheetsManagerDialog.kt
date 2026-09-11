package com.example.ui.screens.external

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entities.ExternalRequestSourceEntity
import com.example.data.local.entities.ExternalSheetEntity
import com.example.data.remote.SheetReadResult
import com.example.ui.viewmodel.ForensicViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExternalSheetsManagerDialog(
    source: ExternalRequestSourceEntity,
    viewModel: ForensicViewModel,
    onDismiss: () -> Unit,
    onViewSheetRequests: ((sheetId: String) -> Unit)? = null
) {
    val allSheets by viewModel.rawExternalSheets.collectAsState()
    val allRequests by viewModel.rawExternalRequests.collectAsState()
    val sourceSheets = allSheets.filter { it.sourceId == source.id }

    var sheetFilter by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }

    var sheetForColumnMapping by remember { mutableStateOf<ExternalSheetEntity?>(null) }
    var sheetForDetails by remember { mutableStateOf<ExternalSheetEntity?>(null) }
    var sheetToDelete by remember { mutableStateOf<ExternalSheetEntity?>(null) }
    var sheetForPreview by remember { mutableStateOf<ExternalSheetEntity?>(null) }
    var showAddSheetDialog by remember { mutableStateOf(false) }

    val filteredSheets = sourceSheets.filter { sheet ->
        val matchesQuery = searchQuery.isBlank() ||
                sheet.sheetName.contains(searchQuery, ignoreCase = true) ||
                (sheet.customDisplayName?.contains(searchQuery, ignoreCase = true) == true)

        val matchesFilter = when (sheetFilter) {
            "الكل" -> true
            "المفعلة" -> sheet.enabled && !sheet.ignored
            "المعطلة" -> !sheet.enabled && !sheet.ignored
            "المتجاهلة" -> sheet.ignored
            else -> true
        }
        matchesQuery && matchesFilter
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 10.dp)
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
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "إدارة أوراق العمل (Sheets)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "المصدر: ${source.name}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Source summary card with quick actions
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "إجمالي الأوراق: ${sourceSheets.size} (مفعلة: ${sourceSheets.count { it.enabled && !it.ignored }})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "الحالة: ${source.status}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { showAddSheetDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إضافة ورقة", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.refreshSheetsForSource(source.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("فحص الأوراق الجديدة", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { viewModel.syncExternalSource(source.id) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("مزامنة المصدر", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search & Filter row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("بحث في الأوراق...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Filter chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("الكل", "المفعلة", "المعطلة", "المتجاهلة").forEach { filter ->
                        val isSelected = sheetFilter == filter
                        val count = when (filter) {
                            "الكل" -> sourceSheets.size
                            "المفعلة" -> sourceSheets.count { it.enabled && !it.ignored }
                            "المعطلة" -> sourceSheets.count { !it.enabled && !it.ignored }
                            "المتجاهلة" -> sourceSheets.count { it.ignored }
                            else -> 0
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { sheetFilter = filter },
                            label = { Text("$filter ($count)", fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sheets list
                if (filteredSheets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.TableChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (sourceSheets.isEmpty()) "لم يتم اكتشاف أوراق عمل بعد في هذا الملف." else "لا توجد أوراق تطابق خيارات البحث/التصفية.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            if (sourceSheets.isEmpty()) {
                                Button(
                                    onClick = { viewModel.refreshSheetsForSource(source.id) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("اضغط هنا لفحص أوراق Google Sheets", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredSheets, key = { it.id }) { sheet ->
                            val sheetRequestsCount = allRequests.count {
                                (it.sheetId == sheet.sheetId || it.sheetId == sheet.id || it.sheetName == sheet.sheetName) && it.sourceId == sheet.sourceId
                            }

                            SheetCardItem(
                                sheet = sheet,
                                requestsCount = sheetRequestsCount,
                                viewModel = viewModel,
                                onSyncNow = {
                                    viewModel.syncSingleSheet(source.id, sheet.id, sheet.customDisplayName ?: sheet.sheetName)
                                },
                                onPreviewTable = { sheetForPreview = sheet },
                                onConfigureColumns = { sheetForColumnMapping = sheet },
                                onEditDetails = { sheetForDetails = sheet },
                                onDelete = { sheetToDelete = sheet },
                                onViewRequests = {
                                    onViewSheetRequests?.invoke(sheet.sheetId)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("إغلاق", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // Column Mapping Dialog
    sheetForColumnMapping?.let { sheet ->
        SheetColumnMappingDialog(
            source = source,
            sheet = sheet,
            viewModel = viewModel,
            onDismiss = { sheetForColumnMapping = null }
        )
    }

    // Edit Sheet Details Dialog
    sheetForDetails?.let { sheet ->
        EditSheetDetailsDialog(
            source = source,
            sheet = sheet,
            viewModel = viewModel,
            onDismiss = { sheetForDetails = null },
            onOpenColumnMapping = { sheetForColumnMapping = sheet }
        )
    }

    // Raw Data Preview Dialog
    sheetForPreview?.let { sheet ->
        SheetRawPreviewDialog(
            source = source,
            sheet = sheet,
            viewModel = viewModel,
            onDismiss = { sheetForPreview = null }
        )
    }

    // Add Sheet Manually Dialog
    if (showAddSheetDialog) {
        AddManualSheetDialog(
            source = source,
            viewModel = viewModel,
            onDismiss = { showAddSheetDialog = false }
        )
    }

    // Delete Sheet Confirmation
    sheetToDelete?.let { sheet ->
        AlertDialog(
            onDismissRequest = { sheetToDelete = null },
            title = { Text("تأكيد إزالة الورقة") },
            text = {
                Text("هل أنت متأكد من رغبتك في إزالة ورقة «${sheet.customDisplayName ?: sheet.sheetName}» من التطبيق محلياً؟ لن يتم مس أي بيانات داخل Google Sheet الأصلي.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExternalSheet(sheet.id, sheet.sheetName)
                        sheetToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("إزالة من التطبيق")
                }
            },
            dismissButton = {
                TextButton(onClick = { sheetToDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun SheetCardItem(
    sheet: ExternalSheetEntity,
    requestsCount: Int,
    viewModel: ForensicViewModel,
    onSyncNow: () -> Unit,
    onPreviewTable: () -> Unit,
    onConfigureColumns: () -> Unit,
    onEditDetails: () -> Unit,
    onDelete: () -> Unit,
    onViewRequests: () -> Unit
) {
    val isMapped = sheet.columnMappingJson.isNotBlank() && sheet.columnMappingJson != "{}"

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                when {
                    sheet.ignored -> Color(0xFFD32F2F).copy(alpha = 0.3f)
                    !sheet.enabled -> Color.Gray.copy(alpha = 0.25f)
                    else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                },
                RoundedCornerShape(14.dp)
            )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Names & Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = sheet.customDisplayName ?: sheet.sheetName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (sheet.ignored) Color.Gray else MaterialTheme.colorScheme.onSurface
                        )
                        if (!sheet.customDisplayName.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "الأصل: ${sheet.sheetName}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "الصفوف في الورقة: ${sheet.rowCount} • الطلبات المسجلة محلياً: $requestsCount",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status Badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (sheet.ignored) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFD32F2F).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "متجاهلة",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else if (!sheet.enabled) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF757575).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "معطلة",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF757575),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF388E3C).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "مفعلة للاستيراد",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF388E3C),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    if (isMapped) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1976D2).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "أعمدة مخصصة",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Toggles Row: Direct Enable/Disable and Ignore
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("استيراد الطلبات:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = sheet.enabled && !sheet.ignored,
                        onCheckedChange = { viewModel.toggleSheetEnabled(sheet.id, it) },
                        modifier = Modifier.size(36.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("تجاهل واستثناء:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = sheet.ignored,
                        onCheckedChange = { viewModel.toggleSheetIgnored(sheet.id, it) },
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onSyncNow,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مزامنة الورقة", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onConfigureColumns,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ربط الأعمدة", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onPreviewTable,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("معاينة", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onViewRequests,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("الطلبات", fontSize = 11.sp)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEditDetails, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل اسم العرض والإعدادات", modifier = Modifier.size(16.dp))
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "إزالة الورقة", tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AddManualSheetDialog(
    source: ExternalRequestSourceEntity,
    viewModel: ForensicViewModel,
    onDismiss: () -> Unit
) {
    var sheetName by remember { mutableStateOf("") }
    var sheetGid by remember { mutableStateOf("0") }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة ورقة يدوياً إلى المصدر", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "أدخل اسم الورقة كما يظهر في علامة التبويب السفلية لـ Google Sheets بدقة.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = sheetName,
                    onValueChange = { sheetName = it },
                    label = { Text("اسم الورقة (مثال: الشكاوى، أوراق العمل)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = sheetGid,
                    onValueChange = { sheetGid = it },
                    label = { Text("معرّف الورقة GID (اختياري، الافتراضي: 0)") },
                    placeholder = { Text("0 أو الرقم بعد gid= في الرابط") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (sheetName.isNotBlank()) {
                        isSubmitting = true
                        viewModel.addSheetManually(
                            sourceId = source.id,
                            sheetName = sheetName.trim(),
                            sheetGid = sheetGid.trim().ifEmpty { "0" }
                        ) {
                            isSubmitting = false
                            onDismiss()
                        }
                    }
                },
                enabled = sheetName.isNotBlank() && !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text("إضافة الورقة")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun SheetRawPreviewDialog(
    source: ExternalRequestSourceEntity,
    sheet: ExternalSheetEntity,
    viewModel: ForensicViewModel,
    onDismiss: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var rawResult by remember { mutableStateOf<Result<SheetReadResult>?>(null) }

    androidx.compose.runtime.LaunchedEffect(sheet.id) {
        isLoading = true
        viewModel.readSheetRawData(source.id, sheet.id) { res ->
            rawResult = res
            isLoading = false
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "معاينة بيانات: ${sheet.customDisplayName ?: sheet.sheetName}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "المصدر: ${source.name} • المعرّف (GID): ${sheet.sheetId}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                if (isLoading) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("جاري قراءة الورقة من Google Sheets...", fontSize = 12.sp)
                        }
                    }
                } else {
                    val res = rawResult
                    if (res == null || res.isFailure) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("تعذر قراءة بيانات الورقة الحية", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                                Text(
                                    res?.exceptionOrNull()?.localizedMessage ?: "خطأ غير معروف في الاتصال",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(onClick = {
                                    isLoading = true
                                    viewModel.readSheetRawData(source.id, sheet.id) { r ->
                                        rawResult = r
                                        isLoading = false
                                    }
                                }) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("إعادة المحاولة")
                                }
                            }
                        }
                    } else {
                        val sheetData = res.getOrThrow()
                        val headers = sheetData.detectedHeaders
                        val rawTable = sheetData.rawTable
                        val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
                        val onSurfaceColor = MaterialTheme.colorScheme.onSurface

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                            ) {
                                Text(
                                    text = "الأعمدة المكتشفة: ${headers.size} • الصفوف المتاحة: ${rawTable.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            val horizontalScroll = rememberScrollState()
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).horizontalScroll(horizontalScroll)) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    item {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Row(modifier = Modifier.padding(8.dp)) {
                                                headers.forEachIndexed { idx, col ->
                                                    Text(
                                                        text = if (col.isBlank()) "عمود ${idx + 1}" else col,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = onPrimaryColor,
                                                        modifier = Modifier.width(140.dp).padding(horizontal = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    items(rawTable.take(50)) { rowMap ->
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Row(modifier = Modifier.padding(8.dp)) {
                                                headers.forEach { headerKey ->
                                                    val cell = rowMap[headerKey].orEmpty()
                                                    Text(
                                                        text = if (cell.isBlank()) "-" else cell,
                                                        fontSize = 11.sp,
                                                        color = onSurfaceColor,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.width(140.dp).padding(horizontal = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                        Text("إغلاق", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
