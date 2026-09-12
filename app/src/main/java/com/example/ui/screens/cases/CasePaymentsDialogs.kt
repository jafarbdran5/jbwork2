package com.example.ui.screens.cases

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.CaseAuditLogEntity
import com.example.data.local.entities.CaseEntity
import com.example.data.local.entities.CasePaymentEntity
import com.example.ui.components.CyberBadge
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCardElevated
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberInfo
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberPrimaryLight
import com.example.ui.theme.CyberSuccess
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val PAYMENT_METHODS = listOf("تحويل بنكي", "STC Pay", "نقدي", "بطاقة مدى / ائتمان", "PayPal", "أخرى")

@Composable
fun PaymentStatusBadge(status: String, modifier: Modifier = Modifier) {
    val (color, text) = when (status) {
        "مدفوع بالكامل" -> CyberSuccess to "مدفوع بالكامل"
        "مدفوع جزئيًا" -> CyberWarning to "مدفوع جزئيًا"
        "معفى" -> CyberInfo to "معفى"
        else -> CyberDanger to "غير مدفوع"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AddPaymentDialog(
    caseEntity: CaseEntity,
    onDismiss: () -> Unit,
    onConfirmPayment: (amount: Double, method: String, date: String, notes: String, receipt: String) -> Unit
) {
    val todayDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    var amountText by remember { mutableStateOf(if (caseEntity.remainingAmount > 0) caseEntity.remainingAmount.toInt().toString() else "") }
    var selectedMethod by remember { mutableStateOf(PAYMENT_METHODS[0]) }
    var paymentDate by remember { mutableStateOf(todayDate) }
    var receiptNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AttachMoney, contentDescription = null, tint = CyberSuccess)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("تسجيل دفعة مالية جديدة", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("القضية: ${caseEntity.caseNumber} - ${caseEntity.clientName}", color = TextSecondary, fontSize = 12.sp)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Summary card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCardElevated)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("المبلغ الإجمالي المتفق عليه", color = TextSecondary, fontSize = 11.sp)
                            Text("${caseEntity.totalAmount} ${caseEntity.currency}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("المتبقي حالياً", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                "${caseEntity.remainingAmount} ${caseEntity.currency}",
                                color = if (caseEntity.remainingAmount > 0) CyberWarning else CyberSuccess,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Amount input
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorMessage = null
                    },
                    label = { Text("المبلغ المدفوع (${caseEntity.currency})") },
                    placeholder = { Text("أدخل القيمة المسددة") },
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it, color = CyberDanger) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payment_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberSuccess,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                // Quick presets
                if (caseEntity.remainingAmount > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { amountText = caseEntity.remainingAmount.toString() },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("كامل المتبقي", fontSize = 10.sp)
                        }
                        if (caseEntity.remainingAmount > 1) {
                            OutlinedButton(
                                onClick = { amountText = (caseEntity.remainingAmount / 2).toString() },
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("50% من المتبقي", fontSize = 10.sp)
                            }
                        }
                    }
                }

                // Payment Method
                Text("طريقة الدفع:", color = TextSecondary, fontSize = 12.sp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PAYMENT_METHODS.forEach { method ->
                        FilterChip(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            label = { Text(method, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyberSuccess,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Receipt / Ref Number
                OutlinedTextField(
                    value = receiptNumber,
                    onValueChange = { receiptNumber = it },
                    label = { Text("رقم الإيصال / الحوالة (اختياري)") },
                    placeholder = { Text("مثال: TRX-98231") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                // Payment Date
                OutlinedTextField(
                    value = paymentDate,
                    onValueChange = { paymentDate = it },
                    label = { Text("تاريخ السداد") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات الدفعة") },
                    placeholder = { Text("دفعة أولى عند توقيع العقد، أو سداد نهائي...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        errorMessage = "يرجى إدخال مبلغ صحيح أكبر من صفر"
                        return@Button
                    }
                    onConfirmPayment(amt, selectedMethod, paymentDate, notes, receiptNumber)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSuccess),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_payment_btn")
            ) {
                Text("تأكيد تسجيل الدفعة", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        }
    )
}

@Composable
fun UpdateCasePriceDialog(
    caseEntity: CaseEntity,
    onDismiss: () -> Unit,
    onConfirmUpdate: (newPrice: Double, notes: String) -> Unit
) {
    var newPriceText by remember { mutableStateOf(caseEntity.totalAmount.toString()) }
    var updateNotes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = CyberPrimaryLight)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("تعديل أتعاب القضية / السعر", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("القضية: ${caseEntity.caseNumber}", color = TextSecondary, fontSize = 12.sp)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCardElevated)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "السعر الحالي: ${caseEntity.totalAmount} ${caseEntity.currency} (المدفوع: ${caseEntity.paidAmount})",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                OutlinedTextField(
                    value = newPriceText,
                    onValueChange = {
                        newPriceText = it
                        errorMessage = null
                    },
                    label = { Text("السعر الإجمالي الجديد (${caseEntity.currency})") },
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it, color = CyberDanger) } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = updateNotes,
                    onValueChange = { updateNotes = it },
                    label = { Text("سبب التعديل (مطلوب للتوثيق)") },
                    placeholder = { Text("اتفاق مع العميل على تخفيض/زيادة نطاق العمل...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = newPriceText.toDoubleOrNull()
                    if (price == null || price < 0) {
                        errorMessage = "يرجى إدخال سعر صحيح"
                        return@Button
                    }
                    if (price < caseEntity.paidAmount) {
                        errorMessage = "السعر الجديد أقل من المبلغ المدفوع بالفعل (${caseEntity.paidAmount})"
                        return@Button
                    }
                    onConfirmUpdate(price, updateNotes)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("حفظ وتوثيق السعر الجديد", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = TextSecondary)
            }
        }
    )
}

@Composable
fun CasePaymentsSection(
    payments: List<CasePaymentEntity>,
    onAddPaymentClick: () -> Unit,
    currency: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = CyberSuccess, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "سجل الدفعات المستلمة (${payments.size}):",
                    color = CyberSuccess,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(onClick = onAddPaymentClick) {
                Text("+ إضافة دفعة", color = CyberSuccess, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (payments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberCardElevated)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("لم يتم تسجيل أي دفعات مالية لهذه القضية بعد.", color = TextMuted, fontSize = 12.sp)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                payments.forEach { pay ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCardElevated)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "+${pay.amount} ${pay.currency}",
                                        color = CyberSuccess,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CyberBadge(text = pay.paymentMethod, accentColor = CyberInfo)
                                }
                                if (pay.receiptNumber.isNotBlank()) {
                                    Text("إيصال: ${pay.receiptNumber}", color = TextSecondary, fontSize = 11.sp)
                                }
                                if (pay.notes.isNotBlank()) {
                                    Text(pay.notes, color = TextMuted, fontSize = 11.sp)
                                }
                            }
                            Text(pay.paymentDate, color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CaseAuditLogsSection(
    auditLogs: List<CaseAuditLogEntity>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, contentDescription = null, tint = CyberPrimaryLight, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "سجل التغييرات والتدقيق (${auditLogs.size}):",
                color = CyberPrimaryLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (auditLogs.isEmpty()) {
            Text("لا توجد سجلات تدقيق سابقة لهذه القضية.", color = TextMuted, fontSize = 12.sp)
        } else {
            val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US) }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                auditLogs.take(5).forEach { log ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberCardElevated.copy(alpha = 0.6f))
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.operation, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(dateFormat.format(Date(log.timestamp)), color = TextMuted, fontSize = 10.sp)
                            }
                            if (log.oldValue.isNotBlank() || log.newValue.isNotBlank()) {
                                Text("${log.oldValue} ➔ ${log.newValue}", color = TextSecondary, fontSize = 11.sp)
                            }
                            Text("بواسطة: ${log.performedBy}", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
