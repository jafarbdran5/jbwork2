package com.example.ui.screens.investigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.ui.components.CollapsibleHeaderContainer
import com.example.ui.components.rememberScrollHeaderVisibility
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.CaseEntity
import com.example.data.local.entities.InvestigationToolEntity
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberInfo
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberPrimaryLight
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSuccess
import com.example.ui.theme.CyberWarning
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.ForensicViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

val TOOL_CATEGORIES = listOf(
    "الكل",
    "فحص الروابط والملفات الخبيثة",
    "البحث العكسي عن الصور",
    "فحص البريد والبيانات المسربة",
    "فحص أرقام الهواتف والحسابات",
    "فحص النطاقات وعناوين IP",
    "تتبع الميتاداتا والأرشيف الرقمي",
    "أدوات التوثيق والتحقق الرقمي"
)

val TOOL_COSTS = listOf(
    "الكل",
    "مجاني",
    "مجاني جزئياً",
    "مدفوع"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestigationToolsScreen(
    viewModel: ForensicViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val tools by viewModel.filteredInvestigationTools.collectAsStateWithLifecycle()
    val rawCases by viewModel.rawCases.collectAsStateWithLifecycle()

    val searchQuery by viewModel.investigationSearchQuery.collectAsStateWithLifecycle()
    val categoryFilter by viewModel.investigationCategoryFilter.collectAsStateWithLifecycle()
    val costFilter by viewModel.investigationCostFilter.collectAsStateWithLifecycle()
    val activeTab by viewModel.investigationActiveTab.collectAsStateWithLifecycle()

    var selectedToolForCaseLink by remember { mutableStateOf<InvestigationToolEntity?>(null) }
    var selectedToolForDetails by remember { mutableStateOf<InvestigationToolEntity?>(null) }
    var showAddToolDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val isHeaderVisible by rememberScrollHeaderVisibility(
        listState = listState,
        onVisibilityChanged = { viewModel.setGlobalTopBarVisible(it) }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddToolDialog = true },
                containerColor = CyberPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_investigation_tool_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة أداة فحص")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Dynamic Collapsible Header (Scroll Down -> Hide, Scroll Up -> Show)
            CollapsibleHeaderContainer(visible = isHeaderVisible) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Banner
                    InvestigationToolsHeader(
                        totalCount = tools.size,
                        favoritesCount = tools.count { it.isFavorite }
                    )

                    // Tabs: All / Favorites / Recent
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = CyberPrimaryLight,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                                color = CyberPrimaryLight
                            )
                        }
                    ) {
                        Tab(
                            selected = activeTab == 0,
                            onClick = { viewModel.investigationActiveTab.value = 0 },
                            text = { Text("جميع الأدوات", fontSize = 12.sp, fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) }
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = { viewModel.investigationActiveTab.value = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = CyberWarning, modifier = Modifier.size(15.dp))
                                    Text("المفضلة", fontSize = 12.sp, fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        )
                        Tab(
                            selected = activeTab == 2,
                            onClick = { viewModel.investigationActiveTab.value = 2 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.History, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(15.dp))
                                    Text("المستخدمة مؤخراً", fontSize = 12.sp, fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        )
                    }

                    // Search Box
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.investigationSearchQuery.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("investigation_search_field"),
                        placeholder = {
                            Text(
                                "ابحث باسم الأداة، النطاق، فحص الروابط، الصور العكسية، التسريبات...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = CyberPrimaryLight
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.investigationSearchQuery.value = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "مسح",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberBorder,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    // Categories Filter Scroll
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TOOL_CATEGORIES.forEach { cat ->
                            val isSelected = categoryFilter == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.investigationCategoryFilter.value = cat },
                                label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberPrimary.copy(alpha = 0.2f),
                                    selectedLabelColor = CyberPrimaryLight,
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) CyberPrimary else CyberBorder
                                )
                            )
                        }
                    }

                    // Cost Filter Scroll
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TOOL_COSTS.forEach { cost ->
                            val isSelected = costFilter == cost
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.investigationCostFilter.value = cost },
                                label = { Text(cost, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberSecondary.copy(alpha = 0.2f),
                                    selectedLabelColor = CyberSecondary,
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) CyberSecondary else CyberBorder
                                )
                            )
                        }
                    }
                }
            }

            // Tools List
            if (tools.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = if (activeTab == 1) "لا توجد أدوات في المفضلة حالياً" else "لم يتم العثور على أدوات مطابقة",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (activeTab == 1) "انقر على رمز النجمة بجانب أي أداة لإضافتها لمفضلاتك" else "جرّب تغيير التصنيف أو مصطلح البحث",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tools, key = { it.id }) { tool ->
                        InvestigationToolCard(
                            tool = tool,
                            onClick = { selectedToolForDetails = tool },
                            onOpen = { viewModel.openUrl(context, tool.url, tool.id) },
                            onCopy = { viewModel.copyToClipboard(context, tool.url, tool.name) },
                            onShare = { viewModel.shareUrl(context, tool.url, "${tool.name} - أداة فحص رقمي") },
                            onToggleFavorite = { viewModel.toggleToolFavorite(tool.id, tool.isFavorite) },
                            onLinkToCase = { selectedToolForCaseLink = tool }
                        )
                    }
                }
            }
        }

        // Tool Details BottomSheet
        if (selectedToolForDetails != null) {
            val detailTool = selectedToolForDetails!!
            InvestigationToolDetailSheet(
                tool = detailTool,
                onOpen = { viewModel.openUrl(context, detailTool.url, detailTool.id) },
                onCopy = { viewModel.copyToClipboard(context, detailTool.url, detailTool.name) },
                onShare = { viewModel.shareUrl(context, detailTool.url, "${detailTool.name} - أداة فحص رقمي") },
                onToggleFavorite = { viewModel.toggleToolFavorite(detailTool.id, detailTool.isFavorite) },
                onLinkToCase = {
                    selectedToolForCaseLink = detailTool
                    selectedToolForDetails = null
                },
                onDismiss = { selectedToolForDetails = null }
            )
        }

        // Link Tool to Case BottomSheet
        if (selectedToolForCaseLink != null) {
            LinkToolToCaseSheet(
                tool = selectedToolForCaseLink!!,
                cases = rawCases,
                onDismiss = { selectedToolForCaseLink = null },
                onConfirmLink = { caseId, notes ->
                    viewModel.linkItemToCase(
                        caseId = caseId,
                        itemType = "INVESTIGATION_TOOL",
                        itemId = selectedToolForCaseLink!!.id,
                        itemTitle = selectedToolForCaseLink!!.name,
                        itemUrl = selectedToolForCaseLink!!.url,
                        itemPlatformOrCategory = "${selectedToolForCaseLink!!.category} | ${selectedToolForCaseLink!!.officialDomain}",
                        notes = notes
                    )
                    selectedToolForCaseLink = null
                }
            )
        }

        // Add Custom Tool BottomSheet
        if (showAddToolDialog) {
            AddInvestigationToolSheet(
                onDismiss = { showAddToolDialog = false },
                onSave = { newTool ->
                    scope.launch {
                        viewModel.repository.insertOrUpdateInvestigationTool(newTool)
                        viewModel.showHud("تمت إضافة أداة الفحص والتحقق بنجاح", com.example.ui.components.HudType.SUCCESS)
                        showAddToolDialog = false
                    }
                }
            )
        }
    }
}

@Composable
private fun InvestigationToolsHeader(totalCount: Int, favoritesCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.dp, CyberBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.TravelExplore,
                        contentDescription = null,
                        tint = CyberPrimaryLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "مركز أدوات الفحص والتحقق الرقمي",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "منظومة متكاملة لفحص الملفات والروابط، استعلام المصادر المفتوحة، والتحقق الرقمي",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberPrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$totalCount أداة", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyberPrimaryLight)
                    Text(text = "$favoritesCount مفضلة", fontSize = 9.sp, color = CyberWarning)
                }
            }
        }
    }
}

@Composable
fun InvestigationToolCard(
    tool: InvestigationToolEntity,
    onClick: () -> Unit,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onToggleFavorite: () -> Unit,
    onLinkToCase: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Category Badge + Cost + Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tool.category,
                            color = CyberPrimaryLight,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val costColor = when (tool.freeOrPaid) {
                        "مجاني" -> CyberSuccess
                        "مجاني جزئياً" -> CyberWarning
                        else -> CyberDanger
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(costColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tool.freeOrPaid,
                            color = costColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (tool.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "المفضلة",
                        tint = if (tool.isFavorite) CyberWarning else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tool Title & Domain
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tool.name,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = tool.officialDomain,
                    fontSize = 10.5.sp,
                    color = CyberPrimaryLight,
                    fontWeight = FontWeight.Medium
                )
            }

            // Concise Description
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tool.description,
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row - Clean & responsive
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Primary Action: Open Official Tool in browser
                Button(
                    onClick = onOpen,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("open_tool_button_${tool.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(7.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "تشغيل الأداة",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Details Action
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(7.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                ) {
                    Text(
                        text = "التفاصيل",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp
                    )
                }

                // Copy Action
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .size(34.dp)
                        .border(1.dp, CyberBorder, RoundedCornerShape(7.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ الرابط",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Share Action
                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(34.dp)
                        .border(1.dp, CyberBorder, RoundedCornerShape(7.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "مشاركة",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Link To Case Action
                IconButton(
                    onClick = onLinkToCase,
                    modifier = Modifier
                        .size(34.dp)
                        .border(1.dp, CyberSecondary.copy(alpha = 0.5f), RoundedCornerShape(7.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.AddLink,
                        contentDescription = "ربط بقضية",
                        tint = CyberSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InvestigationToolDetailSheet(
    tool: InvestigationToolEntity,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onToggleFavorite: () -> Unit,
    onLinkToCase: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row: Badges, Favorite & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = tool.category, color = CyberPrimaryLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    val costColor = when (tool.freeOrPaid) {
                        "مجاني" -> CyberSuccess
                        "مجاني جزئياً" -> CyberWarning
                        else -> CyberDanger
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(costColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = tool.freeOrPaid, color = costColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (tool.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "المفضلة",
                            tint = if (tool.isFavorite) CyberWarning else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tool Name
            Text(
                text = tool.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Domain Info
            Text(
                text = "النطاق الرسمي: ${tool.officialDomain}",
                fontSize = 12.sp,
                color = CyberPrimaryLight,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Full URL Box with Copy
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, tint = CyberPrimaryLight, modifier = Modifier.size(16.dp))
                        Text(
                            text = tool.url,
                            fontSize = 11.5.sp,
                            color = CyberPrimaryLight,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Full Description
            Text(
                text = "عن الأداة واستخداماتها:",
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tool.description,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Privacy / OpSec Warning Card
            if (tool.privacyRisk.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (tool.privacyRisk == "منخفض") CyberSuccess.copy(alpha = 0.08f) else CyberWarning.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (tool.privacyRisk == "منخفض") CyberSuccess.copy(alpha = 0.25f) else CyberWarning.copy(alpha = 0.25f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = if (tool.privacyRisk == "منخفض") CyberSuccess else CyberWarning,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "مستوى أمان الخصوصية (OpSec): ${tool.privacyRisk}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (tool.privacyRisk == "منخفض") CyberSuccess else CyberWarning
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (tool.privacyRisk == "منخفض") {
                                "هذه الأداة آمنة ولا تشارك بيانات الاستعلام بشكل علني، ولا تتطلب تسريب هوية المحقق."
                            } else {
                                "تنبيه أمني: قد تحتفظ هذه الأداة بسجلات الاستعلامات أو قد يتم إشعار صاحب الهدف. احرص على استخدام بروكسي أو عدم إدخال بيانات حساسة مباشرة."
                            },
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Last used timestamp if available
            if (tool.lastUsedAt != null) {
                Spacer(modifier = Modifier.height(10.dp))
                val formattedDate = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(tool.lastUsedAt))
                Text(
                    text = "آخر استخدام مسجل: $formattedDate",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big Action Button: Open
            Button(
                onClick = {
                    onOpen()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("تشغيل الأداة في المتصفح الرسمي", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Secondary Action Row: Link & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onLinkToCase()
                    },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberSecondary)
                ) {
                    Icon(Icons.Default.AddLink, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ربط بقضية", color = CyberSecondary, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        onShare()
                    },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مشاركة", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkToolToCaseSheet(
    tool: InvestigationToolEntity,
    cases: List<CaseEntity>,
    onDismiss: () -> Unit,
    onConfirmLink: (caseId: String, notes: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCaseId by remember { mutableStateOf(cases.firstOrNull()?.id ?: "") }
    var notes by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ربط أداة الفحص بملف القضية",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tool Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorder, RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = tool.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${tool.category} • ${tool.officialDomain}",
                        fontSize = 12.sp,
                        color = CyberPrimaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "اختر القضية المراد توثيق استخدام الأداة معها:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (cases.isEmpty()) {
                Text(
                    text = "لا توجد قضايا نشطة حالياً.",
                    fontSize = 12.sp,
                    color = CyberWarning
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(cases, key = { it.id }) { c ->
                        val isSelected = selectedCaseId == c.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCaseId = c.id }
                                .border(
                                    1.dp,
                                    if (isSelected) CyberPrimary else CyberBorder,
                                    RoundedCornerShape(8.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) CyberPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${c.caseNumber} - ${c.title}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "العميل: ${c.clientName} | التصنيف: ${c.threatType}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = CyberPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("ملاحظات الفحص والنتائج (اختياري)") },
                placeholder = { Text("مثال: تم فحص رابط التصيد وتبين اتصاله بسيرفر مشبوه في روسيا IP: 185.x.x.x") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onConfirmLink(selectedCaseId, notes) },
                enabled = selectedCaseId.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("تأكيد ربط الأداة بالقضية", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInvestigationToolSheet(
    onDismiss: () -> Unit,
    onSave: (InvestigationToolEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var officialUrl by remember { mutableStateOf("") }
    var officialDomain by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("فحص الروابط والملفات الخبيثة") }
    var freeOrPaid by remember { mutableStateOf("مجاني") }
    var description by remember { mutableStateOf("") }
    var privacyNotes by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "إضافة أداة فحص وتحقق جديدة",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("اسم الأداة") },
                placeholder = { Text("مثال: URLScan.io أو Shodan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = officialUrl,
                onValueChange = {
                    officialUrl = it
                    if (officialDomain.isBlank()) {
                        officialDomain = it.replace("https://", "").replace("http://", "").substringBefore("/")
                    }
                },
                label = { Text("الرابط الرسمي للأداة (URL)") },
                placeholder = { Text("https://urlscan.io") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("التصنيف") },
                    modifier = Modifier.weight(1.2f),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = freeOrPaid,
                    onValueChange = { freeOrPaid = it },
                    label = { Text("التكلفة (مجاني/مدفوع)") },
                    modifier = Modifier.weight(0.8f),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("وصف الأداة وطريقة استخدامها") },
                placeholder = { Text("فحص كود الصفحات والروابط بأمان...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = privacyNotes,
                onValueChange = { privacyNotes = it },
                label = { Text("ملاحظات الأمان والخصوصية (OpSec)") },
                placeholder = { Text("تحذير: لا تفحص روابط تحتوي توكنات سرية...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("الوسوم المفتاحية (مفصولة بفواصل)") },
                placeholder = { Text("فحص, تصيد, سكريبتات") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val parsedDomain = if (officialDomain.isNotBlank()) {
                        officialDomain.trim()
                    } else {
                        try {
                            android.net.Uri.parse(officialUrl.trim()).host ?: "tools"
                        } catch (e: Exception) {
                            "tools"
                        }
                    }
                    val newTool = InvestigationToolEntity(
                        id = "tool_custom_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                        name = name.trim(),
                        url = officialUrl.trim(),
                        category = category.trim(),
                        subcategory = category.trim(),
                        description = description.trim(),
                        officialDomain = parsedDomain,
                        freeOrPaid = freeOrPaid.trim(),
                        privacyRisk = privacyNotes.trim().ifBlank { "منخفض" },
                        tags = tags.trim(),
                        verified = true
                    )
                    onSave(newTool)
                },
                enabled = name.isNotBlank() && officialUrl.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ الأداة في مركز الأدوات", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
