package com.example.ui.screens.trash

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.TaskEntity
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberDanger
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TrashItemDisplay(
    val id: String,
    val module: String, // CASES, CLIENTS, EVIDENCE, STUDIO, KNOWLEDGE, TASKS
    val moduleLabel: String,
    val title: String,
    val subtitle: String,
    val deletedDate: Long?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(viewModel: ForensicViewModel) {
    val deletedCases by viewModel.deletedCases.collectAsState()
    val deletedClients by viewModel.deletedClients.collectAsState()
    val deletedEvidence by viewModel.deletedEvidence.collectAsState()
    val deletedContent by viewModel.deletedContent.collectAsState()
    val deletedKnowledge by viewModel.deletedKnowledge.collectAsState()
    val deletedTasks by viewModel.deletedTasks.collectAsState()

    var selectedModuleFilter by remember { mutableStateOf("الكل") }
    val moduleFilters = listOf("الكل", "القضايا", "العملاء", "المرفقات", "المحتوى", "المعرفة", "المهام")

    // Permanent delete confirmation bottomsheet
    var itemToDeletePermanently by remember { mutableStateOf<TrashItemDisplay?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Aggregate all deleted items
    val allTrashItems = remember(
        deletedCases,
        deletedClients,
        deletedEvidence,
        deletedContent,
        deletedKnowledge,
        deletedTasks
    ) {
        val list = mutableListOf<TrashItemDisplay>()
        deletedCases.forEach {
            list.add(TrashItemDisplay(it.id, "CASES", "قضية", "${it.caseNumber}: ${it.title}", "العميل: ${it.clientName}", it.deletedAt))
        }
        deletedClients.forEach {
            list.add(TrashItemDisplay(it.id, "CLIENTS", "عميل", it.fullName, "هاتف: ${it.phoneNumber} | ${it.riskLevel}", it.deletedAt))
        }
        deletedEvidence.forEach {
            list.add(TrashItemDisplay(it.id, "EVIDENCE", "مرفق", it.evidenceName, "ملف: ${it.originalFilename} (${it.fileType})", it.deletedAt))
        }
        deletedContent.forEach {
            list.add(TrashItemDisplay(it.id, "STUDIO", "منشور", it.title, "المنصة: ${it.platform} | ${it.status}", it.deletedAt))
        }
        deletedKnowledge.forEach {
            list.add(TrashItemDisplay(it.id, "KNOWLEDGE", "مرجع معرفي", it.title, "التصنيف: ${it.category}", it.deletedAt))
        }
        deletedTasks.forEach {
            list.add(TrashItemDisplay(it.id, "TASKS", "مهمة", it.title, "الأولوية: ${it.priority} | ${it.dueDate}", it.deletedAt))
        }
        list.sortedByDescending { it.deletedDate ?: 0L }
    }

    val filteredTrash = if (selectedModuleFilter == "الكل") allTrashItems else {
        allTrashItems.filter {
            when (selectedModuleFilter) {
                "القضايا" -> it.module == "CASES"
                "العملاء" -> it.module == "CLIENTS"
                "المرفقات" -> it.module == "EVIDENCE"
                "المحتوى" -> it.module == "STUDIO"
                "المعرفة" -> it.module == "KNOWLEDGE"
                "المهام" -> it.module == "TASKS"
                else -> true
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "سلة المحذوفات المؤقتة",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "يمكنك استعادة أي عنصر أو حذفه وتطهير أثره نهائياً",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberWarning.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${allTrashItems.size} محذوفات",
                        color = CyberWarning,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Filter chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(moduleFilters) { filter ->
                    val isSelected = selectedModuleFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CyberPrimary else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if (isSelected) CyberPrimaryLight else CyberBorder, RoundedCornerShape(10.dp))
                            .clickable { selectedModuleFilter = filter }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        if (filteredTrash.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "سلة المحذوفات فارغة حالياً.",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(filteredTrash, key = { "${it.module}_${it.id}" }) { item ->
                TrashCard(
                    item = item,
                    onRestore = { viewModel.restoreItem(item.module, item.id) },
                    onPermanentDelete = { itemToDeletePermanently = item }
                )
            }
        }
    }

    // Permanent Delete Confirmation Modal Bottom Sheet (non-blocking)
    if (itemToDeletePermanently != null) {
        ModalBottomSheet(
            onDismissRequest = { itemToDeletePermanently = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberDanger.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = CyberDanger,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "تأكيد الحذف النهائي وغير القابل للاسترجاع",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "هل أنت متأكد من رغبتك في حذف '${itemToDeletePermanently?.title}' نهائياً من قاعدة البيانات المشفرة؟ لن يمكن استرجاع هذا السجل أو بياناته لاحقاً.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                itemToDeletePermanently = null
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("إلغاء", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = {
                            itemToDeletePermanently?.let {
                                viewModel.permanentDeleteItem(it.module, it.id)
                            }
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                itemToDeletePermanently = null
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberDanger),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("حذف نهائي", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TrashCard(
    item: TrashItemDisplay,
    onRestore: () -> Unit,
    onPermanentDelete: () -> Unit
) {
    val dateStr = item.deletedDate?.let {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(it))
    } ?: "غير محدد"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (item.module) {
                                "CASES" -> CyberPrimary.copy(alpha = 0.15f)
                                "EVIDENCE" -> CyberSecondary.copy(alpha = 0.15f)
                                "CLIENTS" -> CyberSuccess.copy(alpha = 0.15f)
                                "STUDIO" -> CyberInfo.copy(alpha = 0.15f)
                                "TASKS" -> CyberWarning.copy(alpha = 0.15f)
                                else -> CyberPrimaryLight.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = item.moduleLabel,
                        color = when (item.module) {
                            "CASES" -> CyberPrimaryLight
                            "EVIDENCE" -> CyberSecondary
                            "CLIENTS" -> CyberSuccess
                            "STUDIO" -> CyberInfo
                            "TASKS" -> CyberWarning
                            else -> CyberPrimaryLight
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "تاريخ الحذف: $dateStr",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPermanentDelete,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberDanger),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("حذف نهائي", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onRestore,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("استعادة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
