package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AppSectionConfigEntity
import com.example.ui.components.CyberBadge
import com.example.ui.theme.*

@Composable
fun AdminSectionsTab(
    sections: List<AppSectionConfigEntity>,
    onToggleVisibility: (String, Boolean, String) -> Unit,
    onEditSection: (AppSectionConfigEntity) -> Unit,
    onDeleteSection: (String, String) -> Unit,
    onAddNewSection: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "أقسام وقوائم المنظومة (${sections.size} قسم)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Button(
                onClick = onAddNewSection,
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة قسم مخصص", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sections, key = { it.id }) { s ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberCard),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (s.isVisible) CyberBorder else CyberDanger.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (s.isVisible) CyberPrimary.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getSectionIconByName(s.iconName),
                                    contentDescription = null,
                                    tint = if (s.isVisible) CyberPrimaryLight else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = s.displayName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (s.isVisible) MaterialTheme.colorScheme.onSurface else TextMuted
                                    )
                                    if (s.isCustom) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        CyberBadge(text = "مخصص", accentColor = CyberSecondary)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    CyberBadge(text = s.category, accentColor = CyberInfo)
                                }
                                Text(
                                    text = s.description.ifBlank { "الترتيب: #${s.sortOrder}" },
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onToggleVisibility(s.id, !s.isVisible, s.displayName) }) {
                                Icon(
                                    imageVector = if (s.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "تبديل الظهور",
                                    tint = if (s.isVisible) CyberSuccess else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(onClick = { onEditSection(s) }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "تعديل",
                                    tint = CyberPrimaryLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            if (s.isCustom) {
                                IconButton(onClick = { onDeleteSection(s.id, s.displayName) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "حذف",
                                        tint = CyberDanger,
                                        modifier = Modifier.size(18.dp)
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

fun getSectionIconByName(name: String): ImageVector {
    return when (name.lowercase()) {
        "home" -> Icons.Default.Home
        "folder" -> Icons.Default.Folder
        "clouddownload" -> Icons.Default.CloudDownload
        "attachfile" -> Icons.Default.AttachFile
        "assignment" -> Icons.Default.Assignment
        "contactsupport" -> Icons.Default.ContactSupport
        "travelexplore" -> Icons.Default.TravelExplore
        "autoawesome" -> Icons.Default.AutoAwesome
        "people" -> Icons.Default.People
        "menubook" -> Icons.AutoMirrored.Filled.MenuBook
        "assessment" -> Icons.Default.Assessment
        "search" -> Icons.Default.Search
        "delete" -> Icons.Default.Delete
        "settings" -> Icons.Default.Settings
        "security" -> Icons.Default.Security
        else -> Icons.Default.Folder
    }
}
