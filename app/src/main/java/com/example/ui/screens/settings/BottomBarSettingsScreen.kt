package com.example.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.preferences.BottomBarItemConfig
import com.example.data.preferences.BottomBarSettings
import com.example.ui.navigation.ScreenDestination
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCard
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BottomBarSettingsScreen(
    viewModel: ForensicViewModel,
    onNavigateBack: () -> Unit
) {
    val savedSettings by viewModel.bottomBarSettings.collectAsStateWithLifecycle()

    // Local Draft state for interactive, instantaneous changes before hitting save
    var isEnabled by remember { mutableStateOf(savedSettings.isEnabled) }
    var maxItems by remember { mutableIntStateOf(savedSettings.maxItems) }
    var defaultDestinationId by remember { mutableIntStateOf(savedSettings.defaultDestinationId) }
    var draftItems by remember { mutableStateOf(savedSettings.items) }

    // Dialog for renaming section title
    var editingItem by remember { mutableStateOf<BottomBarItemConfig?>(null) }
    var editingTitleInput by remember { mutableStateOf("") }

    // Sync draft if savedSettings changes externally
    LaunchedEffect(savedSettings) {
        isEnabled = savedSettings.isEnabled
        maxItems = savedSettings.maxItems
        defaultDestinationId = savedSettings.defaultDestinationId
        draftItems = savedSettings.items
    }

    // Calculated current preview items
    val liveActiveItems = remember(draftItems, maxItems, isEnabled) {
        if (!isEnabled) emptyList()
        else draftItems
            .filter { it.isVisibleInBar && it.destination != null }
            .sortedBy { it.order }
            .take(maxItems)
    }

    Scaffold(
        containerColor = CyberBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "تخصيص الشريط السفلي",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "الإعدادات ← تخصيص الواجهة ← الشريط السفلي",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("bottom_bar_settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = CyberPrimaryLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberSurface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==============================================================
            // 1. LIVE PREVIEW CARD (معاينة حية فورية)
            // ==============================================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, CyberPrimary, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberCardElevated),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(CyberPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ViewStream,
                                        contentDescription = null,
                                        tint = CyberPrimaryLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "معاينة حية للشريط السفلي (Live Preview)",
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "تحديث لحظي وفوري لأي إضافة، حذف، ترتيب، أو تسمية",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isEnabled) CyberSuccess.copy(alpha = 0.15f) else CyberDanger.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isEnabled) "الشريط نشط" else "الشريط مخفي",
                                    color = if (isEnabled) CyberSuccess else CyberDanger,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Preview Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        ) {
                            if (!isEnabled) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = CyberDanger,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "الشريط السفلي مخفي بالكامل في التطبيق",
                                        color = CyberDanger,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "يمكنك التبديل بين الأقسام من خلال القائمة الجانبية (Drawer)",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            } else if (liveActiveItems.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "لا توجد أقسام محددة للشريط بعد، يرجى تفعيل الأقسام من القائمة أدناه",
                                        color = CyberWarning,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 0.dp
                                ) {
                                    liveActiveItems.forEachIndexed { index, item ->
                                        val dest = item.destination ?: return@forEachIndexed
                                        val isDefault = dest.id == defaultDestinationId
                                        NavigationBarItem(
                                            selected = index == 0, // Just simulated active state on first item
                                            onClick = { /* Preview only */ },
                                            icon = {
                                                Icon(
                                                    imageVector = dest.icon,
                                                    contentDescription = item.displayTitle,
                                                    modifier = Modifier.size(19.dp)
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = item.displayTitle,
                                                    fontSize = 10.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = CyberPrimaryLight,
                                                selectedTextColor = CyberPrimaryLight,
                                                unselectedIconColor = TextSecondary,
                                                unselectedTextColor = TextSecondary,
                                                indicatorColor = CyberPrimary.copy(alpha = 0.15f)
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "العناصر المعروضة: ${liveActiveItems.size} من أصل $maxItems المسموح بها",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // ==============================================================
            // 2. GENERAL CONTROLS (التحكم العام بالشريط)
            // ==============================================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CyberBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = CyberCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "خيارات العرض العامة",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Toggle bottom bar visibility
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "إظهار الشريط السفلي بالكامل",
                                    color = TextPrimary,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "يمكنك إخفاء الشريط لتوفير مساحة إضافية أو إعادة إظهاره متى شئت",
                                    color = TextSecondary,
                                    fontSize = 11.5.sp
                                )
                            }
                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { isEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = CyberPrimary,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = CyberSurface
                                ),
                                modifier = Modifier.testTag("toggle_bottom_bar_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Max items count selector
                        Text(
                            text = "الحد الأقصى لعدد العناصر في الشريط:",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(3, 4, 5, 6, 7).forEach { count ->
                                val isSelected = maxItems == count
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { maxItems = count },
                                    label = {
                                        Text(
                                            text = "$count عناصر",
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberPrimary,
                                        selectedLabelColor = Color.Black,
                                        containerColor = CyberSurface,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) CyberPrimary else CyberBorder
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Default screen on launch selector
                        Text(
                            text = "القسم الذي يفتح تلقائياً عند تشغيل التطبيق:",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "اختر الشاشة الافتراضية التي تظهر لك أولاً بمجرد فتح المنظومة",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ScreenDestination.entries.forEach { dest ->
                                val isSelected = defaultDestinationId == dest.id
                                val matchingItem = draftItems.find { it.destinationId == dest.id }
                                val label = matchingItem?.customTitle?.ifBlank { dest.title } ?: dest.title

                                FilterChip(
                                    selected = isSelected,
                                    onClick = { defaultDestinationId = dest.id },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.Star else dest.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = if (isSelected) CyberWarning else TextSecondary
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberSecondary.copy(alpha = 0.25f),
                                        selectedLabelColor = CyberSecondary,
                                        containerColor = CyberSurface,
                                        labelColor = TextSecondary
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
            }

            // ==============================================================
            // 3. SECTIONS MANAGEMENT & ORDERING (ترتيب الأقسام والإضافة والحذف)
            // ==============================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ترتيب الأقسام والتحكم بالعرض (${draftItems.size} قسماً)",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "غيّر ترتيب الأقسام، أضف أو احذف من الشريط، وخصّص الاسم المعروض",
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }

            itemsIndexed(draftItems, key = { _, item -> item.destinationId }) { index, item ->
                val dest = item.destination ?: return@itemsIndexed
                val isInBar = item.isVisibleInBar
                val isDefault = dest.id == defaultDestinationId

                // Determine position status
                val visibleItemsBefore = draftItems
                    .filter { it.isVisibleInBar }
                    .sortedBy { it.order }
                val positionInActive = visibleItemsBefore.indexOfFirst { it.destinationId == item.destinationId }
                val isActuallyInBar = isInBar && positionInActive >= 0 && positionInActive < maxItems

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (isActuallyInBar) 1.2.dp else 1.dp,
                            color = if (isActuallyInBar) CyberPrimary.copy(alpha = 0.6f) else CyberBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActuallyInBar) CyberSurface else CyberCard
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Drag Handle + Order badge + Icon + Title
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                // Order Badge with Drag Icon
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberSurface)
                                        .border(1.dp, CyberBorder, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DragHandle,
                                        contentDescription = "ترتيب",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "#${index + 1}",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Icon
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isActuallyInBar) CyberPrimary.copy(alpha = 0.15f)
                                            else CyberBorder.copy(alpha = 0.3f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = dest.icon,
                                        contentDescription = null,
                                        tint = if (isActuallyInBar) CyberPrimaryLight else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Display Name & Original Name
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.displayTitle,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (isDefault) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "القسم الافتراضي لبدء التشغيل",
                                                tint = CyberWarning,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    if (item.customTitle.isNotBlank()) {
                                        Text(
                                            text = "الاسم الأصلي: ${dest.title}",
                                            color = TextMuted,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                }
                            }

                            // Reorder Arrows (Move Up / Down)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (index > 0) {
                                            val list = draftItems.toMutableList()
                                            val current = list.removeAt(index)
                                            list.add(index - 1, current)
                                            // Re-index orders
                                            draftItems = list.mapIndexed { idx, itm -> itm.copy(order = idx) }
                                        }
                                    },
                                    enabled = index > 0,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = "تقديم للأعلى",
                                        tint = if (index > 0) CyberPrimaryLight else TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        if (index < draftItems.size - 1) {
                                            val list = draftItems.toMutableList()
                                            val current = list.removeAt(index)
                                            list.add(index + 1, current)
                                            // Re-index orders
                                            draftItems = list.mapIndexed { idx, itm -> itm.copy(order = idx) }
                                        }
                                    },
                                    enabled = index < draftItems.size - 1,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = "تأخير للأسفل",
                                        tint = if (index < draftItems.size - 1) CyberPrimaryLight else TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Badges & Action Buttons (Rename + Add/Remove)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Status Badge
                            if (isActuallyInBar) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberSuccess.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = CyberSuccess,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "ظاهر في الشريط (موقع ${positionInActive + 1})",
                                            color = CyberSuccess,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else if (isInBar) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberWarning.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "مؤهل (يتجاوز حد $maxItems عناصر)",
                                        color = CyberWarning,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyberBorder.copy(alpha = 0.3f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "مخفي من الشريط (متاح في القائمة)",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            // Actions: Rename and Toggle In/Out
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Rename button
                                OutlinedButton(
                                    onClick = {
                                        editingItem = item
                                        editingTitleInput = item.customTitle.ifBlank { dest.title }
                                    },
                                    modifier = Modifier.height(34.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberPrimaryLight),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تغيير الاسم", fontSize = 11.sp)
                                }

                                // Toggle in/out of bar
                                if (isInBar) {
                                    OutlinedButton(
                                        onClick = {
                                            draftItems = draftItems.map {
                                                if (it.destinationId == item.destinationId) it.copy(isVisibleInBar = false)
                                                else it
                                            }
                                        },
                                        modifier = Modifier.height(34.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberDanger),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("إزالة من الشريط", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            draftItems = draftItems.map {
                                                if (it.destinationId == item.destinationId) it.copy(isVisibleInBar = true)
                                                else it
                                            }
                                        },
                                        modifier = Modifier.height(34.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("إظهار في الشريط", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==============================================================
            // 4. BOTTOM ACTION BUTTONS (حفظ التغييرات وإعادة الافتراضي)
            // ==============================================================
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Save Button
                    Button(
                        onClick = {
                            val newSettings = BottomBarSettings(
                                isEnabled = isEnabled,
                                maxItems = maxItems,
                                defaultDestinationId = defaultDestinationId,
                                items = draftItems
                            )
                            viewModel.saveBottomBarSettings(newSettings)
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_bottom_bar_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حفظ التغييرات",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Reset Button
                    OutlinedButton(
                        onClick = {
                            viewModel.resetBottomBarSettings()
                            // Re-align local draft to defaults
                            val defaults = ScreenDestination.entries.map { dest ->
                                BottomBarItemConfig(
                                    destinationId = dest.id,
                                    customTitle = "",
                                    isVisibleInBar = dest.defaultInBottomBar,
                                    order = dest.defaultOrder
                                )
                            }.sortedBy { it.order }

                            isEnabled = true
                            maxItems = 5
                            defaultDestinationId = ScreenDestination.DASHBOARD.id
                            draftItems = defaults
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberWarning),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("reset_bottom_bar_defaults_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = CyberWarning,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إعادة الإعداد الافتراضي",
                            color = CyberWarning,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // ==============================================================
    // DIALOG: EDIT SECTION DISPLAY NAME
    // ==============================================================
    editingItem?.let { item ->
        val dest = item.destination ?: return@let
        Dialog(onDismissRequest = { editingItem = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CyberSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = dest.icon, contentDescription = null, tint = CyberPrimaryLight)
                            Text(
                                text = "تعديل اسم العرض للقسم",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { editingItem = null }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "إلغاء", tint = TextMuted)
                        }
                    }

                    Text(
                        text = "الاسم الداخلي والمسار: ${dest.title} (ID: ${dest.name}). لن يتأثر النظام الداخلي أو الـ Routes بتغيير هذا الاسم.",
                        color = TextSecondary,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )

                    OutlinedTextField(
                        value = editingTitleInput,
                        onValueChange = { editingTitleInput = it },
                        label = { Text("اسم العرض في الشريط السفلي") },
                        placeholder = { Text(dest.title) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Reset to original title button
                        OutlinedButton(
                            onClick = {
                                editingTitleInput = dest.title
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("الاسم الأصلي", fontSize = 12.sp)
                        }

                        // Apply button
                        Button(
                            onClick = {
                                val trimmed = editingTitleInput.trim()
                                val finalTitle = if (trimmed == dest.title) "" else trimmed
                                draftItems = draftItems.map {
                                    if (it.destinationId == item.destinationId) {
                                        it.copy(customTitle = finalTitle)
                                    } else it
                                }
                                editingItem = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("تطبيق", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
