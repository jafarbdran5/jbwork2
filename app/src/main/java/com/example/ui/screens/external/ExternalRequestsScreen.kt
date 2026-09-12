package com.example.ui.screens.external

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.ui.platform.testTag
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ExternalRequestEntity
import com.example.data.local.entities.ExternalRequestSourceEntity
import com.example.data.local.entities.ExternalSheetEntity
import com.example.ui.viewmodel.ForensicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalRequestsScreen(
    viewModel: ForensicViewModel,
    onOpenCase: (String) -> Unit = {}
) {
    val context = LocalContext.current

    // State from ViewModel
    val rawRequests by viewModel.rawExternalRequests.collectAsState()
    val filteredRequests by viewModel.filteredExternalRequests.collectAsState()
    val sources by viewModel.rawExternalSources.collectAsState()
    val sheets by viewModel.rawExternalSheets.collectAsState()
    val isSyncing by viewModel.isExternalSyncing.collectAsState()

    val searchQuery by viewModel.externalRequestSearchQuery.collectAsState()
    val activeTab by viewModel.externalRequestActiveTab.collectAsState()
    val statusFilter by viewModel.externalRequestStatusFilter.collectAsState()
    val sourceFilter by viewModel.externalRequestSourceFilter.collectAsState()
    val sheetFilter by viewModel.externalRequestSheetFilter.collectAsState()
    val urgencyFilter by viewModel.externalRequestUrgencyFilter.collectAsState()

    // Dialog controllers
    var selectedRequestForDetails by remember { mutableStateOf<ExternalRequestEntity?>(null) }
    var selectedRequestForConversion by remember { mutableStateOf<ExternalRequestEntity?>(null) }
    var isAddSourceOpen by remember { mutableStateOf(false) }
    var isManageSourcesOpen by remember { mutableStateOf(false) }
    var sourceForSheetsManager by remember { mutableStateOf<ExternalRequestSourceEntity?>(null) }

    // Tab definitions
    val tabs = listOf(
        "الكل" to rawRequests.size,
        "جديدة" to rawRequests.count { it.status == "جديد" },
        "قيد المراجعة" to rawRequests.count { it.status == "قيد المراجعة" },
        "قيد المعالجة" to rawRequests.count { it.status == "قيد المعالجة" },
        "محولة" to rawRequests.count { it.status == "تم تحويله إلى قضية" },
        "مكتملة" to rawRequests.count { it.status == "مكتمل" },
        "مرفوضة" to rawRequests.count { it.status == "مرفوض" },
        "مصادر البيانات" to sources.size
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Banner Card
            item(key = "header_banner") {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "الطلبات الخارجية (Google Sheets)",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "استقبال ومعالجة قضايا وطلبات العملاء بدون تعديل على الملف الأصلي",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.syncAllExternalSources() },
                                    shape = RoundedCornerShape(10.dp),
                                    enabled = !isSyncing,
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    if (isSyncing) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("مزامنة...", fontSize = 11.sp)
                                    } else {
                                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("مزامنة", fontSize = 11.sp)
                                    }
                                }

                                Button(
                                    onClick = { isManageSourcesOpen = true },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Icon(Icons.Default.Layers, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("المصادر (${sources.size})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Responsive 2x2 Metric Counters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricCard(
                                label = "إجمالي الطلبات",
                                count = rawRequests.size.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                label = "طلبات جديدة",
                                count = rawRequests.count { it.status == "جديد" }.toString(),
                                color = Color(0xFFE65100),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricCard(
                                label = "قيد المعالجة",
                                count = rawRequests.count { it.status == "قيد المراجعة" || it.status == "قيد المعالجة" }.toString(),
                                color = Color(0xFF0288D1),
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                label = "تم تحويلها لقضايا",
                                count = rawRequests.count { it.status == "تم تحويله إلى قضية" }.toString(),
                                color = Color(0xFF673AB7),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Tabs Row (Horizontal Filter Chips)
            item(key = "tabs_row") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEach { (title, count) ->
                        val isSelected = activeTab == title
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.externalRequestActiveTab.value = title },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = count.toString(),
                                            fontSize = 10.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            if (activeTab == "مصادر البيانات") {
                // Section Header for Sources
                item(key = "sources_banner_row") {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "قائمة مصادر Google Sheets المربوطة (${sources.size})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "اضغط على أي بطاقة للدخول إلى أوراقها وإدارتها وتعيين أعمدتها",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = { isAddSourceOpen = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إضافة مصدر", fontSize = 11.sp)
                            }
                        }
                    }
                }

                if (sources.isEmpty()) {
                    item(key = "empty_sources_card") {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "لا توجد مصادر Google Sheets مضافة حالياً",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "أضف رابط Google Sheet عام للبدء في استيراد طلبات وقضايا العملاء مباشرة للقراءة فقط.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { isAddSourceOpen = true },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("إضافة مصدر Google Sheet الآن", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    items(sources, key = { it.id }) { src ->
                        val sourceSheets = sheets.filter { it.sourceId == src.id }
                        val activeSheetsCount = sourceSheets.count { it.enabled && !it.ignored }
                        val reqsCount = rawRequests.count { it.sourceId == src.id }

                        SourceCardItem(
                            src = src,
                            sourceSheets = sourceSheets,
                            activeSheetsCount = activeSheetsCount,
                            reqsCount = reqsCount,
                            viewModel = viewModel,
                            onOpenSheetsManager = { sourceForSheetsManager = src }
                        )
                    }
                }
            } else {
                // Search Input Field
                item(key = "search_field_item") {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.externalRequestSearchQuery.value = it },
                        placeholder = { Text("بحث باسم العميل، الهاتف، تفاصيل المشكلة، الملاحظات...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.externalRequestSearchQuery.value = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "مسح", modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                // Sources filter chips
                if (sources.isNotEmpty()) {
                    item(key = "sources_filter_row") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("المصدر:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            FilterChip(
                                selected = sourceFilter == "الكل",
                                onClick = { viewModel.externalRequestSourceFilter.value = "الكل" },
                                label = { Text("كل المصادر (${rawRequests.size})", fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )

                            sources.forEach { src ->
                                val srcRequestsCount = rawRequests.count { it.sourceId == src.id }
                                val isSelected = sourceFilter == src.id

                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.externalRequestSourceFilter.value = if (isSelected) "الكل" else src.id
                                    },
                                    label = {
                                        Text("${src.name} ($srcRequestsCount)", fontSize = 11.sp)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                }

                // Sheets filter chips
                val relevantSheets = if (sourceFilter != "الكل") sheets.filter { it.sourceId == sourceFilter } else sheets
                if (relevantSheets.isNotEmpty()) {
                    item(key = "sheets_filter_row") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("الورقة:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            FilterChip(
                                selected = sheetFilter == "الكل",
                                onClick = { viewModel.externalRequestSheetFilter.value = "الكل" },
                                label = { Text("كل الأوراق (${filteredRequests.size})", fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )

                            relevantSheets.forEach { sh ->
                                val count = rawRequests.count {
                                    it.sourceId == sh.sourceId && (it.sheetId == sh.sheetId || it.sheetId == sh.id || it.sheetName == sh.sheetName)
                                }
                                val isSelected = sheetFilter == sh.sheetId || sheetFilter == sh.id || sheetFilter == sh.sheetName

                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        viewModel.externalRequestSheetFilter.value = if (isSelected) "الكل" else sh.sheetId
                                    },
                                    label = {
                                        Text("${sh.customDisplayName ?: sh.sheetName} ($count)", fontSize = 11.sp)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }
                }

                // Urgency filter chips
                item(key = "urgency_filter_row") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("الأهمية:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        listOf("الكل", "حرجة", "عالية", "متوسطة", "منخفضة").forEach { urg ->
                            FilterChip(
                                selected = urgencyFilter == urg,
                                onClick = { viewModel.externalRequestUrgencyFilter.value = urg },
                                label = { Text(urg, fontSize = 11.sp) },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                // Requests List or Empty State
                if (filteredRequests.isEmpty()) {
                    item(key = "empty_requests_box") {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "لا توجد طلبات تطابق الفلاتر المحددة",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "تأكد من مزامنة مصادر Google Sheets أو تعديل عبارة البحث والفلاتر.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { isAddSourceOpen = true },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("إضافة مصدر Google Sheet الآن", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    items(filteredRequests, key = { it.id }) { request ->
                        ExternalRequestCard(
                            request = request,
                            viewModel = viewModel,
                            onOpenDetails = { selectedRequestForDetails = request },
                            onConvertToCase = { selectedRequestForConversion = request },
                            onOpenCase = onOpenCase
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { isAddSourceOpen = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_external_source_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "إضافة مصدر Google Sheet")
        }
    }

    // Details Dialog
    selectedRequestForDetails?.let { req ->
        ExternalRequestDetailsDialog(
            request = req,
            viewModel = viewModel,
            onDismiss = { selectedRequestForDetails = null },
            onConvertToCase = { selectedRequestForConversion = it },
            onOpenCase = onOpenCase
        )
    }

    // Convert to Case Dialog
    selectedRequestForConversion?.let { req ->
        ConvertRequestToCaseDialog(
            request = req,
            viewModel = viewModel,
            onDismiss = { selectedRequestForConversion = null },
            onSuccess = { caseEntity ->
                selectedRequestForConversion = null
                onOpenCase(caseEntity.id)
            }
        )
    }

    // Add Source Dialog
    if (isAddSourceOpen) {
        AddExternalSourceDialog(
            viewModel = viewModel,
            onDismiss = { isAddSourceOpen = false },
            onSourceAdded = {
                // Auto sync
                viewModel.syncAllExternalSources()
            }
        )
    }

    // Manage Sources Sheet
    if (isManageSourcesOpen) {
        ExternalSourcesManagementSheet(
            viewModel = viewModel,
            onDismiss = { isManageSourcesOpen = false },
            onOpenAddSource = {
                isManageSourcesOpen = false
                isAddSourceOpen = true
            }
        )
    }

    // Sheets Manager Dialog for Selected Source
    sourceForSheetsManager?.let { src ->
        ExternalSheetsManagerDialog(
            source = src,
            viewModel = viewModel,
            onDismiss = { sourceForSheetsManager = null },
            onViewSheetRequests = { sheetId ->
                viewModel.externalRequestSheetFilter.value = sheetId
                viewModel.externalRequestActiveTab.value = "الكل"
            }
        )
    }
}

@Composable
private fun MetricCard(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExternalRequestCard(
    request: ExternalRequestEntity,
    viewModel: ForensicViewModel,
    onOpenDetails: () -> Unit,
    onConvertToCase: () -> Unit,
    onOpenCase: (String) -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }

    Card(
        onClick = onOpenDetails,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Request Number, Sheet badge, Urgency, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = request.requestNumber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = request.sheetName,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Urgency Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (request.urgency) {
                            "حرجة" -> Color(0xFFD32F2F).copy(alpha = 0.15f)
                            "عالية" -> Color(0xFFF57C00).copy(alpha = 0.15f)
                            "منخفضة" -> Color(0xFF388E3C).copy(alpha = 0.15f)
                            else -> Color(0xFF1976D2).copy(alpha = 0.15f)
                        },
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Text(
                            text = request.urgency,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (request.urgency) {
                                "حرجة" -> Color(0xFFD32F2F)
                                "عالية" -> Color(0xFFF57C00)
                                "منخفضة" -> Color(0xFF388E3C)
                                else -> Color(0xFF1976D2)
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (request.status) {
                            "جديد" -> Color(0xFFE65100).copy(alpha = 0.15f)
                            "تم تحويله إلى قضية" -> Color(0xFF673AB7).copy(alpha = 0.15f)
                            "مكتمل" -> Color(0xFF388E3C).copy(alpha = 0.15f)
                            "مرفوض" -> Color(0xFF757575).copy(alpha = 0.15f)
                            else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        }
                    ) {
                        Text(
                            text = request.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (request.status) {
                                "جديد" -> Color(0xFFE65100)
                                "تم تحويله إلى قضية" -> Color(0xFF673AB7)
                                "مكتمل" -> Color(0xFF388E3C)
                                "مرفوض" -> Color(0xFF757575)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Client Name & Contact Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.clientName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row {
                    if (request.clientPhone.isNotBlank()) {
                        IconButton(
                            onClick = { viewModel.copyToClipboard(context, request.clientPhone, "رقم الهاتف") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "الهاتف", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    if (request.clientEmail.isNotBlank()) {
                        IconButton(
                            onClick = { viewModel.copyToClipboard(context, request.clientEmail, "البريد") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = "البريد", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // Description Snippet
            Text(
                text = request.description,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Received Date & Row info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تاريخ الاستلام: ${request.receivedAt.ifBlank { "اليوم" }}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!request.associatedCaseId.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF673AB7), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "مرتبط بقضية رسمية",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF673AB7)
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onConvertToCase,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (request.associatedCaseId.isNullOrBlank()) MaterialTheme.colorScheme.primary else Color(0xFF673AB7)
                        ),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (request.associatedCaseId.isNullOrBlank()) "تحويل إلى قضية" else "إنشاء قضية ثانية",
                            fontSize = 11.sp
                        )
                    }

                    if (!request.associatedCaseId.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = { onOpenCase(request.associatedCaseId) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("فتح القضية", fontSize = 11.sp)
                        }
                    }
                }

                Row {
                    OutlinedButton(
                        onClick = onOpenDetails,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("التفاصيل", fontSize = 11.sp)
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "خيارات إضافية", modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("تعيين كـ جديد") },
                                onClick = {
                                    showMenu = false
                                    viewModel.updateExternalRequestStatus(request.id, "جديد")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("تعيين كـ قيد المراجعة") },
                                onClick = {
                                    showMenu = false
                                    viewModel.updateExternalRequestStatus(request.id, "قيد المراجعة")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("تعيين كـ قيد المعالجة") },
                                onClick = {
                                    showMenu = false
                                    viewModel.updateExternalRequestStatus(request.id, "قيد المعالجة")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("تعيين كـ مكتمل") },
                                onClick = {
                                    showMenu = false
                                    viewModel.updateExternalRequestStatus(request.id, "مكتمل")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("تعيين كـ مرفوض") },
                                onClick = {
                                    showMenu = false
                                    viewModel.updateExternalRequestStatus(request.id, "مرفوض")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SourceCardItem(
    src: ExternalRequestSourceEntity,
    sourceSheets: List<ExternalSheetEntity>,
    activeSheetsCount: Int,
    reqsCount: Int,
    viewModel: ForensicViewModel,
    onOpenSheetsManager: () -> Unit
) {
    Card(
        onClick = onOpenSheetsManager,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (src.enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (src.status.contains("خطأ")) Color(0xFFD32F2F).copy(alpha = 0.15f) else Color(0xFF388E3C).copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = src.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (src.status.contains("خطأ")) Color(0xFFD32F2F) else Color(0xFF388E3C),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Column {
                        Text(
                            text = src.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Spreadsheet ID: ${src.spreadsheetId.take(16)}...",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (src.enabled) "نشط" else "معطل",
                        fontSize = 11.sp,
                        color = if (src.enabled) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = src.enabled,
                        onCheckedChange = { viewModel.toggleExternalSource(src.id, it) },
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metric Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("الأوراق المكتشفة", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${sourceSheets.size} (مفعلة: $activeSheetsCount)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("الطلبات المستوردة", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$reqsCount طلب", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("آخر مزامنة", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        val syncTime = src.lastSync ?: 0L
                        val lastSyncFormatted = if (syncTime > 0L) {
                            val diffMinutes = (System.currentTimeMillis() - syncTime) / (60 * 1000)
                            if (diffMinutes < 1) "الآن" else "منذ $diffMinutes دقيقة"
                        } else "لم تتم بعد"
                        Text(lastSyncFormatted, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onOpenSheetsManager,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("دخول وإدارة الأوراق (${sourceSheets.size})", fontSize = 12.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.refreshSheetsForSource(src.id) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("فحص جديد", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.syncExternalSource(src.id) },
                        shape = RoundedCornerShape(8.dp),
                        enabled = src.enabled,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مزامنة", fontSize = 11.sp)
                    }
                }
            }

            // Quick Sheet Preview if sheets are available
            if (sourceSheets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "معاينة أوراق العمل المكتشفة:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        sourceSheets.take(4).forEach { sheet ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.TableChart,
                                        contentDescription = null,
                                        tint = if (sheet.enabled && !sheet.ignored) MaterialTheme.colorScheme.primary else Color.Gray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = sheet.customDisplayName ?: sheet.sheetName,
                                        fontSize = 12.sp,
                                        fontWeight = if (sheet.enabled && !sheet.ignored) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (sheet.ignored) Color.Gray else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    if (sheet.ignored) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("(متجاهلة)", fontSize = 10.sp, color = Color(0xFFD32F2F))
                                    } else if (!sheet.enabled) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("(معطلة)", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${sheet.rowCount} صف",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = sheet.enabled && !sheet.ignored,
                                        onCheckedChange = { viewModel.toggleSheetEnabled(sheet.id, it) },
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        if (sourceSheets.size > 4) {
                            Text(
                                text = "+ ${sourceSheets.size - 4} أوراق أخرى... اضغط 'دخول وإدارة الأوراق' لعرض الجميع",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clickable { onOpenSheetsManager() }
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
