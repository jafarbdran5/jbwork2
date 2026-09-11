package com.example.ui.screens.clients

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.ClientEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.ForensicCrypto
import com.example.ui.components.HudType
import com.example.ui.components.InAppConfirmationSheet
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberBorder
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
import java.util.UUID

val RISK_LEVELS = listOf("ابتزاز نشط", "عالي الخطورة", "خطر متوسط", "متابعة دورية", "مؤمن بالكامل")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    viewModel: ForensicViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clientsList by viewModel.filteredClients.collectAsStateWithLifecycle()
    val searchQuery by viewModel.clientSearchQuery.collectAsStateWithLifecycle()

    var activeClientDetail by remember { mutableStateOf<ClientEntity?>(null) }
    var clientToEdit by remember { mutableStateOf<ClientEntity?>(null) }
    var clientToDelete by remember { mutableStateOf<ClientEntity?>(null) }
    var showEditorSheet by remember { mutableStateOf(false) }

    // Form fields
    var formName by remember { mutableStateOf("") }
    var formPhone by remember { mutableStateOf("+966") }
    var formEmail by remember { mutableStateOf("") }
    var formRisk by remember { mutableStateOf("ابتزاز نشط") }
    var formNotes by remember { mutableStateOf("") }

    fun openNewClient() {
        clientToEdit = null
        formName = ""
        formPhone = "+966"
        formEmail = ""
        formRisk = "ابتزاز نشط"
        formNotes = ""
        showEditorSheet = true
    }

    fun openEditClient(client: ClientEntity) {
        clientToEdit = client
        formName = client.fullName
        formPhone = client.phoneNumber
        formEmail = client.encryptedEmail
        formRisk = client.riskLevel
        formNotes = client.notes
        showEditorSheet = true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openNewClient() },
                containerColor = CyberPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_client_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "تسجيل عميل")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "سجل العملاء وإدارة المتابعة والتواصل",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "حماية البيانات، سجل التواصل، وإدارة وتحديث بيانات العملاء",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    CyberBadge(text = "${clientsList.size} عملاء", accentColor = CyberPrimary)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.clientSearchQuery.value = it },
                    placeholder = { Text("بحث باسم العميل، الهاتف، أو مستوى الأولوية...", color = TextSecondary, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clientSearchQuery.value = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_search_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
            }

            // Clients List
            if (clientsList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا يوجد عملاء مسجلون مطابقون للبحث.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(clientsList, key = { it.id }) { client ->
                        ClientCard(
                            client = client,
                            onClick = { activeClientDetail = client },
                            onEdit = { openEditClient(client) },
                            onDelete = { clientToDelete = client },
                            onCall = { ForensicCrypto.openDialer(context, client.phoneNumber) },
                            onWhatsApp = {
                                val msg = "مرحباً ${client.fullName}، معك المستشار جعفر بدران. نتابع حالتك وسنقدم لك الدعم الكامل بكل سرية واحترافية."
                                ForensicCrypto.openWhatsApp(context, client.phoneNumber, msg)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Client Details Sheet
    if (activeClientDetail != null) {
        val client = activeClientDetail!!
        ModalBottomSheet(
            onDismissRequest = { activeClientDetail = null },
            containerColor = CyberSurface,
            scrimColor = Color.Black.copy(alpha = 0.7f),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                        text = client.fullName,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    CyberBadge(
                        text = client.riskLevel,
                        accentColor = if (client.riskLevel == "ابتزاز نشط") CyberDanger else CyberWarning
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Contact Details Box
                CyberCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CyberCardElevated) {
                    Column {
                        Text("بيانات التواصل المؤمنة:", color = CyberPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• رقم الهاتف: ${client.phoneNumber}", fontSize = 13.sp, color = TextPrimary)
                        Text("• البريد المشفر: ${client.encryptedEmail.ifEmpty { "غير محدد" }}", fontSize = 13.sp, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Psychological Evaluation
                CyberCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CyberCardElevated) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ملاحظات الدعم والمتابعة:", color = CyberSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = client.notes.ifEmpty { "لم تُسجل ملاحظات إضافية بعد." },
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { ForensicCrypto.openDialer(context, client.phoneNumber) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSuccess),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("اتصال هاتفي")
                    }

                    Button(
                        onClick = {
                            val msg = "السلام عليكم ${client.fullName}، بخصوص متابعة ملف العمل والطلب المسجل."
                            ForensicCrypto.openWhatsApp(context, client.phoneNumber, msg)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberInfo),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مراسلة واتساب")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Delete Confirmation
    if (clientToDelete != null) {
        val target = clientToDelete!!
        InAppConfirmationSheet(
            title = "حذف سجل العميل",
            description = "هل أنت متأكد من حذف سجل '${target.fullName}'؟ لن يتم حذف القضايا والملفات المرتبطة به ولكن سيتم إخفاء بيانات العميل.",
            confirmLabel = "حذف السجل",
            onConfirm = {
                viewModel.deleteClient(target)
                clientToDelete = null
            },
            onDismiss = { clientToDelete = null }
        )
    }

    // Add / Edit Client Sheet
    if (showEditorSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditorSheet = false },
            containerColor = CyberSurface,
            scrimColor = Color.Black.copy(alpha = 0.65f),
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (clientToEdit == null) "تسجيل عميل جديد" else "تعديل بيانات العميل",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = formName,
                    onValueChange = { formName = it },
                    label = { Text("الاسم الكامل للعميل") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = formPhone,
                    onValueChange = { formPhone = it },
                    label = { Text("رقم الهاتف (الواتساب / الاتصال)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = formEmail,
                    onValueChange = { formEmail = it },
                    label = { Text("البريد الإلكتروني المشفر") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("مستوى تقييم الخطر والابتزاز:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RISK_LEVELS.forEach { risk ->
                        FilterChip(
                            selected = formRisk == risk,
                            onClick = { formRisk = risk },
                            label = { Text(risk, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = formNotes,
                    onValueChange = { formNotes = it },
                    label = { Text("ملاحظات الدعم النفسي وحالة التهديد") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (formName.isNotBlank() && formPhone.isNotBlank()) {
                            val isNew = clientToEdit == null
                            val id = clientToEdit?.id ?: "client_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
                            val entity = ClientEntity(
                                id = id,
                                fullName = formName,
                                phoneNumber = formPhone,
                                encryptedEmail = formEmail,
                                riskLevel = formRisk,
                                notes = formNotes,
                                createdDate = clientToEdit?.createdDate ?: System.currentTimeMillis()
                            )
                            viewModel.saveClient(entity, isNew)
                            showEditorSheet = false
                        } else {
                            viewModel.showHud("يرجى ملء الاسم ورقم الهاتف", HudType.WARNING)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (clientToEdit == null) "تسجيل العميل وحفظ البيانات" else "حفظ التعديلات",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ClientCard(
    client: ClientEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    CyberCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("client_card_${client.id}"),
        backgroundColor = CyberCardElevated,
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CyberPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = CyberPrimary, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = client.fullName,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                CyberBadge(
                    text = client.riskLevel,
                    accentColor = if (client.riskLevel == "ابتزاز نشط") CyberDanger else CyberSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "الهاتف: ${client.phoneNumber}",
                color = TextSecondary,
                fontSize = 12.sp
            )

            if (client.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = client.notes,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    IconButton(onClick = onWhatsApp, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Message, contentDescription = "واتساب", tint = CyberSuccess, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = onCall, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Call, contentDescription = "اتصال", tint = CyberInfo, modifier = Modifier.size(15.dp))
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = TextSecondary, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = CyberDanger.copy(alpha = 0.7f), modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}
