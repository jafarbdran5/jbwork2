package com.example.ui.screens.support

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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.entities.SupportFormEntity
import com.example.ui.components.CyberBadge
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
import java.util.UUID

val SUPPORT_COMPANIES = listOf(
    "الكل",
    "Meta",
    "Facebook",
    "Instagram",
    "WhatsApp",
    "Google",
    "Apple",
    "Telegram",
    "X (Twitter)",
    "TikTok",
    "Snapchat",
    "Discord",
    "Microsoft"
)

val SUPPORT_PROBLEM_TYPES = listOf(
    "الكل",
    "اختراق",
    "ابتزاز وتشويه",
    "انتحال شخصية",
    "حظر وتجميد",
    "استعادة وصول",
    "انتهاك خصوصية",
    "احتيال مالي",
    "طلب بيانات قانوني"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportFormsScreen(
    viewModel: ForensicViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val forms by viewModel.filteredSupportForms.collectAsStateWithLifecycle()
    val rawCases by viewModel.rawCases.collectAsStateWithLifecycle()
    val favoriteFormIds by viewModel.favoriteSupportFormIds.collectAsStateWithLifecycle()

    val searchQuery by viewModel.supportFormSearchQuery.collectAsStateWithLifecycle()
    val companyFilter by viewModel.supportFormCompanyFilter.collectAsStateWithLifecycle()
    val problemFilter by viewModel.supportFormProblemFilter.collectAsStateWithLifecycle()
    val directOnly by viewModel.supportFormDirectOnly.collectAsStateWithLifecycle()
    val verifiedOnly by viewModel.supportFormVerifiedOnly.collectAsStateWithLifecycle()

    var favoritesOnly by remember { mutableStateOf(false) }
    var selectedFormForDetails by remember { mutableStateOf<SupportFormEntity?>(null) }
    var selectedFormForCaseLink by remember { mutableStateOf<SupportFormEntity?>(null) }
    var showAddFormDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val isHeaderVisible by rememberScrollHeaderVisibility(
        listState = listState,
        onVisibilityChanged = { viewModel.setGlobalTopBarVisible(it) }
    )

    val displayedForms = remember(forms, favoritesOnly, favoriteFormIds) {
        if (favoritesOnly) forms.filter { favoriteFormIds.contains(it.id) } else forms
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddFormDialog = true },
                containerColor = CyberPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_support_form_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة نموذج دعم")
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
                    // Header Banner - Compact
                    SupportFormsHeader(
                        totalCount = forms.size,
                        verifiedCount = forms.count { it.verified }
                    )

                    // Search Box - Compact & Responsive
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.supportFormSearchQuery.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag("support_form_search_field"),
                        placeholder = {
                            Text(
                                "ابحث بالمنصة، نوع المشكلة (ابتزاز، اختراق...) أو الرابط",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = CyberPrimaryLight,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.supportFormSearchQuery.value = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "مسح",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberBorder,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    // Horizontal Filters: Companies (Compact)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        SUPPORT_COMPANIES.forEach { company ->
                            val isSelected = companyFilter == company
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.supportFormCompanyFilter.value = company },
                                label = { Text(company, fontSize = 10.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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

                    // Horizontal Filters: Problem Types (Compact)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        SUPPORT_PROBLEM_TYPES.forEach { problem ->
                            val isSelected = problemFilter == problem
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.supportFormProblemFilter.value = problem },
                                label = { Text(problem, fontSize = 10.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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

                    // Quick Toggles Row (Compact & Functional)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Official Filter
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = CyberSuccess,
                                modifier = Modifier.size(15.dp)
                            )
                            Text("روابط رسمية فقط", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Switch(
                                checked = verifiedOnly,
                                onCheckedChange = { viewModel.supportFormVerifiedOnly.value = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberSuccess,
                                    checkedTrackColor = CyberSuccess.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.size(width = 40.dp, height = 24.dp)
                            )
                        }

                        // Favorites Filter
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (favoritesOnly) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = CyberWarning,
                                modifier = Modifier.size(15.dp)
                            )
                            Text("المفضلة (${favoriteFormIds.size})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            Switch(
                                checked = favoritesOnly,
                                onCheckedChange = { favoritesOnly = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberWarning,
                                    checkedTrackColor = CyberWarning.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.size(width = 40.dp, height = 24.dp)
                            )
                        }
                    }
                }
            }

            // Forms List
            if (displayedForms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = if (favoritesOnly) "لا توجد نماذج مضافة للمفضلة" else "لم يتم العثور على نماذج مطابقة لبحثك",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (favoritesOnly) "اضغط على نجمة أي نموذج لإضافته إلى قائمة المفضلة السريعة" else "جرّب تغيير الفلاتر أو إزالة شروط البحث للعثور على النموذج المطلوب",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedForms, key = { it.id }) { form ->
                        SupportFormCard(
                            form = form,
                            isFavorite = favoriteFormIds.contains(form.id),
                            onToggleFavorite = { viewModel.toggleSupportFormFavorite(form.id) },
                            onClick = { selectedFormForDetails = form },
                            onOpen = { viewModel.openUrl(context, form.formUrl) },
                            onCopy = { viewModel.copyToClipboard(context, form.formUrl, form.formName) },
                            onShare = { viewModel.shareUrl(context, form.formUrl, form.formName) },
                            onLinkToCase = { selectedFormForCaseLink = form }
                        )
                    }
                }
            }
        }

        // Details BottomSheet
        if (selectedFormForDetails != null) {
            val detailForm = selectedFormForDetails!!
            SupportFormDetailSheet(
                form = detailForm,
                isFavorite = favoriteFormIds.contains(detailForm.id),
                onToggleFavorite = { viewModel.toggleSupportFormFavorite(detailForm.id) },
                onOpen = { viewModel.openUrl(context, detailForm.formUrl) },
                onCopy = { viewModel.copyToClipboard(context, detailForm.formUrl, detailForm.formName) },
                onShare = { viewModel.shareUrl(context, detailForm.formUrl, detailForm.formName) },
                onLinkToCase = {
                    selectedFormForCaseLink = detailForm
                    selectedFormForDetails = null
                },
                onDismiss = { selectedFormForDetails = null }
            )
        }

        // Link To Case BottomSheet
        if (selectedFormForCaseLink != null) {
            LinkSupportFormToCaseSheet(
                form = selectedFormForCaseLink!!,
                cases = rawCases,
                onDismiss = { selectedFormForCaseLink = null },
                onConfirmLink = { caseId, notes ->
                    viewModel.linkItemToCase(
                        caseId = caseId,
                        itemType = "SUPPORT_FORM",
                        itemId = selectedFormForCaseLink!!.id,
                        itemTitle = selectedFormForCaseLink!!.formName,
                        itemUrl = selectedFormForCaseLink!!.formUrl,
                        itemPlatformOrCategory = "${selectedFormForCaseLink!!.company} | ${selectedFormForCaseLink!!.problemType}",
                        notes = notes
                    )
                    selectedFormForCaseLink = null
                }
            )
        }

        // Add Custom Form BottomSheet
        if (showAddFormDialog) {
            AddSupportFormSheet(
                onDismiss = { showAddFormDialog = false },
                onSave = { newForm ->
                    scope.launch {
                        viewModel.repository.insertOrUpdateSupportForm(newForm)
                        viewModel.showHud("تم حفظ نموذج الدعم المباشر بنجاح", com.example.ui.components.HudType.SUCCESS)
                        showAddFormDialog = false
                    }
                }
            )
        }
    }
}

@Composable
private fun SupportFormsHeader(totalCount: Int, verifiedCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(1.dp, CyberBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = CyberPrimaryLight,
                    modifier = Modifier.size(16.dp)
                )
                Column {
                    Text(
                        text = "نماذج الدعم المباشرة الرسمية",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "روابط تقديم البلاغات واستعادة الحسابات دون وسيط",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberSuccess.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "$totalCount نموذج", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberSuccess)
                    Text(text = "•", fontSize = 9.sp, color = CyberSuccess)
                    Text(text = "$verifiedCount موثق", fontSize = 10.sp, color = CyberSuccess)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SupportFormCard(
    form: SupportFormEntity,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
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
            // Top Row: Company Badge, Problem Type Badge, Official Badge & Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = form.company,
                            color = CyberPrimaryLight,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberSecondary.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = form.problemType,
                            color = CyberSecondary,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (form.verified) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberSuccess.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "رسمي",
                                tint = CyberSuccess,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "رسمي",
                                color = CyberSuccess,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Favorite button (36dp with comfortable touch target)
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "المفضلة",
                        tint = if (isFavorite) CyberWarning else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Form Name
            Text(
                text = form.formName,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Direct URL (Safe single-line pill)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = CyberPrimaryLight,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = form.officialDomain.ifBlank { form.formUrl },
                    fontSize = 10.sp,
                    color = CyberPrimaryLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row - Compact & responsive
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Primary Action: Open Official URL directly
                Button(
                    onClick = onOpen,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .testTag("open_form_button_${form.id}"),
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
                            text = "فتح النموذج",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Details Button
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
fun SupportFormDetailSheet(
    form: SupportFormEntity,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
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
            // Header: Badges and Close
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
                        Text(text = form.company, color = CyberPrimaryLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberSecondary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = form.problemType, color = CyberSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    if (form.verified) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberSuccess.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = CyberSuccess, modifier = Modifier.size(13.dp))
                            Text("رابط رسمي معتمد", color = CyberSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "المفضلة",
                            tint = if (isFavorite) CyberWarning else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = form.formName,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category & Domain Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "التصنيف: ${form.category.ifBlank { "نماذج الدعم المباشرة" }}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "النطاق: ${form.officialDomain}",
                    fontSize = 12.sp,
                    color = CyberPrimaryLight,
                    fontWeight = FontWeight.Medium
                )
            }

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
                            text = form.formUrl,
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

            // Notes / Requirements
            if (form.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberWarning.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberWarning.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = CyberWarning, modifier = Modifier.size(16.dp))
                            Text("إرشادات ومتطلبات التقديم", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CyberWarning)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = form.notes,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
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
                Text("فتح النموذج في المتصفح الرسمي", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

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
fun LinkSupportFormToCaseSheet(
    form: SupportFormEntity,
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ربط نموذج الدعم بملف القضية",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق", modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Form Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = form.formName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${form.company} • ${form.problemType}",
                        fontSize = 11.sp,
                        color = CyberPrimaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "اختر القضية المراد ربط النموذج بها:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (cases.isEmpty()) {
                Text(
                    text = "لا توجد قضايا نشطة حالياً. يرجى إنشاء قضية أولاً.",
                    fontSize = 11.5.sp,
                    color = CyberWarning
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    cases.forEach { c ->
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
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${c.caseNumber} - ${c.title}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "العميل: ${c.clientName} | الأولوية: ${c.priority}",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = CyberPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("ملاحظات التوثيق (اختياري)", fontSize = 11.5.sp) },
                placeholder = { Text("مثال: تم إرسال البلاغ برقم تذكرة #49281 الساعة 11:30 صباحاً", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onConfirmLink(selectedCaseId, notes) },
                enabled = selectedCaseId.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("تأكيد ربط النموذج بالقضية", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSupportFormSheet(
    onDismiss: () -> Unit,
    onSave: (SupportFormEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var formName by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("Meta") }
    var platform by remember { mutableStateOf("Instagram") }
    var directUrl by remember { mutableStateOf("") }
    var problemType by remember { mutableStateOf("اختراق") }
    var requirements by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isVerified by remember { mutableStateOf(true) }

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
                    text = "إضافة نموذج دعم مباشر جديد",
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
                value = formName,
                onValueChange = { formName = it },
                label = { Text("اسم النموذج / الغرض") },
                placeholder = { Text("مثال: نموذج الإبلاغ عن ابتزاز إلكتروني مباشر") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("الشركة") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = platform,
                    onValueChange = { platform = it },
                    label = { Text("المنصة") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = directUrl,
                onValueChange = { directUrl = it },
                label = { Text("الرابط الرسمي المباشر (URL)") },
                placeholder = { Text("https://help.instagram.com/contact/...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = problemType,
                onValueChange = { problemType = it },
                label = { Text("نوع المشكلة") },
                placeholder = { Text("اختراق / ابتزاز / حظر...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = requirements,
                onValueChange = { requirements = it },
                label = { Text("المتطلبات قبل التقديم") },
                placeholder = { Text("مثال: البريد الأصلي المسجل، صورة الهوية الوطنية...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("ملاحظات إضافية للمسؤول") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("رابط رسمي معتمد ومتحقق منه؟", fontSize = 13.sp)
                Switch(checked = isVerified, onCheckedChange = { isVerified = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val parsedDomain = try {
                        android.net.Uri.parse(directUrl.trim()).host ?: company.trim().lowercase()
                    } catch (e: Exception) {
                        company.trim().lowercase()
                    }
                    val combinedNotes = if (requirements.isNotBlank()) {
                        "المتطلبات: ${requirements.trim()}\n${notes.trim()}".trim()
                    } else {
                        notes.trim()
                    }
                    val newEntity = SupportFormEntity(
                        id = "form_custom_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                        formName = formName.trim(),
                        company = company.trim(),
                        platform = platform.trim(),
                        formUrl = directUrl.trim(),
                        officialDomain = parsedDomain,
                        problemType = problemType.trim(),
                        category = "نماذج الدعم المباشرة",
                        notes = combinedNotes,
                        verified = isVerified,
                        urlType = "DIRECT_FORM"
                    )
                    onSave(newEntity)
                },
                enabled = formName.isNotBlank() && directUrl.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("حفظ النموذج في المنظومة", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
