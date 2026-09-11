package com.example.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberInfo
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberPrimaryLight
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSuccess
import com.example.ui.theme.CyberWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ForensicViewModel

@Composable
fun GlobalSearchScreen(
    viewModel: ForensicViewModel,
    onNavigateToCases: () -> Unit,
    onNavigateToEvidence: () -> Unit,
    onNavigateToClients: () -> Unit,
    onNavigateToContent: () -> Unit,
    onNavigateToKnowledge: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToSupportForms: () -> Unit = {},
    onNavigateToInvestigationHub: () -> Unit = {}
) {
    val query by viewModel.globalSearchQuery.collectAsState()
    val results by viewModel.searchResults.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "البحث الشامل",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "محرك استعلام فوري في القضايا، العملاء، الملفات، المنشورات، والمعرفة",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }

        // Search bar
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.globalSearchQuery.value = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("اكتب كلمة مفتاحية، رقم قضية، اسم العميل، أو تجزئة هاش...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.globalSearchQuery.value = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "مسح", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberPrimary,
                    unfocusedBorderColor = CyberBorder,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )
        }

        if (query.isBlank()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "ابدأ بكتابة أي نص للبحث في سجلات المنظومة كاملة",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else if (results.totalCount == 0) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لم يتم العثور على أي نتائج مطابقة لـ \"$query\"",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            // Results Summary
            item {
                Text(
                    text = "تم العثور على ${results.totalCount} نتيجة مطابقة:",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Cases Results
            if (results.cases.isNotEmpty()) {
                item {
                    SearchSectionHeader(title = "القضايا (${results.cases.size})", onClick = onNavigateToCases)
                }
                items(results.cases) { c ->
                    SearchResultCard(
                        title = "${c.caseNumber} - ${c.title}",
                        subtitle = "العميل: ${c.clientName} | التصنيف: ${c.threatType} | الحالة: ${c.status}",
                        category = "قضية",
                        categoryColor = CyberPrimary,
                        onClick = onNavigateToCases
                    )
                }
            }

            // Clients Results
            if (results.clients.isNotEmpty()) {
                item {
                    SearchSectionHeader(title = "العملاء (${results.clients.size})", onClick = onNavigateToClients)
                }
                items(results.clients) { cl ->
                    SearchResultCard(
                        title = cl.fullName,
                        subtitle = "هاتف: ${cl.phoneNumber} | تقييم الخطر: ${cl.riskLevel}",
                        category = "عميل",
                        categoryColor = CyberSuccess,
                        onClick = onNavigateToClients
                    )
                }
            }

            // Evidence Results
            if (results.evidence.isNotEmpty()) {
                item {
                    SearchSectionHeader(title = "المرفقات والملفات (${results.evidence.size})", onClick = onNavigateToEvidence)
                }
                items(results.evidence) { e ->
                    SearchResultCard(
                        title = e.evidenceName,
                        subtitle = "ملف: ${e.originalFilename} | SHA-256: ${e.sha256Hash.take(16)}...",
                        category = "مرفق",
                        categoryColor = CyberSecondary,
                        onClick = onNavigateToEvidence
                    )
                }
            }

            // Tasks Results
            if (results.tasks.isNotEmpty()) {
                item {
                    SearchSectionHeader(title = "المهام (${results.tasks.size})", onClick = onNavigateToTasks)
                }
                items(results.tasks) { t ->
                    SearchResultCard(
                        title = t.title,
                        subtitle = "الأولوية: ${t.priority} | الموعد: ${t.dueDate} | الحالة: ${t.status}",
                        category = "مهمة",
                        categoryColor = CyberWarning,
                        onClick = onNavigateToTasks
                    )
                }
            }

            // Content Results
            if (results.content.isNotEmpty()) {
                item {
                    SearchSectionHeader(title = "استوديو المحتوى (${results.content.size})", onClick = onNavigateToContent)
                }
                items(results.content) { co ->
                    SearchResultCard(
                        title = co.title,
                        subtitle = "المنصة: ${co.platform} | الحالة: ${co.status}",
                        category = "منشور",
                        categoryColor = CyberInfo,
                        onClick = onNavigateToContent
                    )
                }
            }

            // Knowledge Results
            if (results.knowledge.isNotEmpty()) {
                item {
                    SearchSectionHeader(title = "الموسوعة المعرفية (${results.knowledge.size})", onClick = onNavigateToKnowledge)
                }
                items(results.knowledge) { k ->
                    SearchResultCard(
                        title = k.title,
                        subtitle = "التصنيف: ${k.category} | ${k.summary}",
                        category = "معرفة",
                        categoryColor = CyberPrimaryLight,
                        onClick = onNavigateToKnowledge
                    )
                }
            }

            // Support Forms Results
            if (results.supportForms.isNotEmpty()) {
                item {
                    SearchSectionHeader(title = "نماذج الدعم المباشرة (${results.supportForms.size})", onClick = onNavigateToSupportForms)
                }
                items(results.supportForms) { sf ->
                    SearchResultCard(
                        title = sf.formName,
                        subtitle = "${sf.company} | ${sf.problemType} | ${sf.formUrl}",
                        category = "نموذج دعم",
                        categoryColor = CyberPrimary,
                        onClick = onNavigateToSupportForms
                    )
                }
            }

            // Investigation Tools Results
            if (results.investigationTools.isNotEmpty()) {
                item {
                    SearchSectionHeader(title = "أدوات الفحص والتحقق (${results.investigationTools.size})", onClick = onNavigateToInvestigationHub)
                }
                items(results.investigationTools) { itool ->
                    SearchResultCard(
                        title = itool.name,
                        subtitle = "${itool.category} | ${itool.officialDomain} | ${itool.freeOrPaid}",
                        category = "أداة فحص",
                        categoryColor = CyberSecondary,
                        onClick = onNavigateToInvestigationHub
                    )
                }
            }
        }
    }
}

@Composable
fun SearchSectionHeader(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "عرض القسم",
            color = CyberPrimaryLight,
            fontSize = 12.sp,
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
fun SearchResultCard(
    title: String,
    subtitle: String,
    category: String,
    categoryColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(categoryColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = category,
                        color = categoryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
