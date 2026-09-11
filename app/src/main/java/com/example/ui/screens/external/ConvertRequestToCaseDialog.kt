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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.local.entities.CaseEntity
import com.example.data.local.entities.ExternalRequestEntity
import com.example.ui.viewmodel.ForensicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvertRequestToCaseDialog(
    request: ExternalRequestEntity,
    viewModel: ForensicViewModel,
    onDismiss: () -> Unit,
    onSuccess: (CaseEntity) -> Unit
) {
    var title by remember {
        mutableStateOf(
            if (request.description.length > 50) request.description.take(50) + "..."
            else request.description.ifBlank { "طلب خارجي: ${request.clientName}" }
        )
    }

    var threatType by remember {
        mutableStateOf(
            when {
                request.description.contains("ابتزاز") || request.internalNotes.contains("ابتزاز") -> "ابتزاز إلكتروني"
                request.description.contains("اختراق") || request.description.contains("تهكير") -> "اختراق حسابات"
                request.description.contains("احتيال") || request.description.contains("نصب") -> "احتيال مالي"
                request.description.contains("تشهير") || request.description.contains("سب") -> "تشهير وإساءة"
                request.description.contains("انتحال") -> "انتحال صفة"
                else -> "ابتزاز إلكتروني"
            }
        )
    }
    var isThreatMenuOpen by remember { mutableStateOf(false) }

    val threatTypes = listOf(
        "ابتزاز إلكتروني",
        "اختراق حسابات",
        "احتيال مالي",
        "تشهير وإساءة",
        "انتحال صفة",
        "استعادة وصول",
        "تحليل وفحص ملفات",
        "استجابة لحوادث سيبرانية"
    )

    var priority by remember {
        mutableStateOf(
            if (request.urgency in listOf("حرجة", "عالية", "متوسطة", "منخفضة")) request.urgency else "عالية"
        )
    }
    var isPriorityMenuOpen by remember { mutableStateOf(false) }
    val priorities = listOf("حرجة", "عالية", "متوسطة", "منخفضة")

    var investigator by remember { mutableStateOf("جعفر بدران (المسؤول)") }
    var notes by remember {
        mutableStateOf(
            buildString {
                if (request.internalNotes.isNotBlank()) {
                    append(request.internalNotes.trim())
                    append("\n")
                }
                append("تفاصيل المشكلة الأصلية: ")
                append(request.description)
            }
        )
    }

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
                            text = "تحويل إلى قضية رسمية",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "ربط طلب ${request.requestNumber} بنظام القضايا والعمليات",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Duplicate Warning if already converted
                if (!request.associatedCaseId.isNullOrBlank()) {
                    Surface(
                        color = Color(0xFFF57C00).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .border(1.dp, Color(0xFFF57C00).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF57C00))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تنبيه: هذا الطلب تم تحويله مسبقاً إلى قضية. سيؤدي هذا الإجراء إلى تسجيل قضية ثانية إضافية لنفس الطلب.",
                                fontSize = 11.sp,
                                color = Color(0xFFF57C00),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Client Summary Box
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("العميل:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(request.clientName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        if (request.clientPhone.isNotBlank()) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("رقم الهاتف:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(request.clientPhone, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Case Title
                Text("عنوان القضية:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Threat Type Dropdown
                Text("نوع التهديد / القضية:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = isThreatMenuOpen,
                    onExpandedChange = { isThreatMenuOpen = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = threatType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isThreatMenuOpen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = isThreatMenuOpen,
                        onDismissRequest = { isThreatMenuOpen = false }
                    ) {
                        threatTypes.forEach { tt ->
                            DropdownMenuItem(
                                text = { Text(tt) },
                                onClick = {
                                    threatType = tt
                                    isThreatMenuOpen = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Priority Dropdown
                Text("مستوى الأولوية:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = isPriorityMenuOpen,
                    onExpandedChange = { isPriorityMenuOpen = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isPriorityMenuOpen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = isPriorityMenuOpen,
                        onDismissRequest = { isPriorityMenuOpen = false }
                    ) {
                        priorities.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    priority = p
                                    isPriorityMenuOpen = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes
                Text("ملاحظات القضية وسجل التوثيق المبدئي:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3,
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
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
                            viewModel.convertExternalRequestToCase(
                                request = request,
                                title = title,
                                threatType = threatType,
                                priority = priority,
                                assignedInvestigator = investigator,
                                notes = notes,
                                onSuccess = { caseEntity ->
                                    onDismiss()
                                    onSuccess(caseEntity)
                                }
                            )
                        },
                        modifier = Modifier.weight(1.6f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إنشاء القضية الآن", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
