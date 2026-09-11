package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CyberBadge
import com.example.ui.components.HudType
import com.example.ui.components.NonBlockingHudOverlay
import com.example.ui.screens.cases.CasesScreen
import com.example.ui.screens.clients.ClientsScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.evidence.EvidenceScreen
import com.example.ui.screens.external.ExternalRequestsScreen
import com.example.ui.screens.knowledge.KnowledgeBaseScreen
import com.example.ui.screens.reports.ReportsScreen
import com.example.ui.screens.search.GlobalSearchScreen
import com.example.ui.screens.security.AuditAndSecurityScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.studio.ContentStudioScreen
import com.example.ui.screens.support.SupportFormsScreen
import com.example.ui.screens.investigation.InvestigationToolsScreen
import com.example.ui.screens.tasks.TasksScreen
import com.example.ui.screens.trash.TrashScreen
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDanger
import com.example.ui.theme.CyberPrimary
import com.example.ui.theme.CyberPrimaryLight
import com.example.ui.theme.CyberSecondary
import com.example.ui.theme.CyberSuccess
import com.example.ui.theme.CyberWarning
import com.example.ui.theme.JaffarForensicsTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ForensicViewModel
import kotlinx.coroutines.launch

enum class ScreenDestination(val id: Int, val title: String, val icon: ImageVector) {
    DASHBOARD(0, "الرئيسية", Icons.Default.Home),
    CASES(1, "القضايا", Icons.Default.Folder),
    EXTERNAL_REQUESTS(14, "الطلبات الخارجية", Icons.Default.CloudDownload),
    EVIDENCE(2, "المرفقات والملفات", Icons.Default.AttachFile),
    TASKS(3, "المهام", Icons.Default.Assignment),
    SUPPORT_FORMS(12, "نماذج الدعم المباشرة", Icons.Default.ContactSupport),
    INVESTIGATION_HUB(13, "أدوات ومصادر العمل", Icons.Default.TravelExplore),
    STUDIO(4, "استوديو المحتوى", Icons.Default.AutoAwesome),
    CLIENTS(5, "العملاء", Icons.Default.People),
    KNOWLEDGE(6, "الموسوعة المعرفية", Icons.Default.MenuBook),
    REPORTS(7, "تقارير العمل والمتابعة", Icons.Default.Assessment),
    SEARCH(8, "البحث الشامل", Icons.Default.Search),
    TRASH(9, "سلة المحذوفات", Icons.Default.Delete),
    SETTINGS(10, "الإعدادات والمزامنة", Icons.Default.Settings),
    SECURITY(11, "سجل الأمان والتدقيق", Icons.Default.Security)
}

class MainActivity : ComponentActivity() {

    private val viewModel: ForensicViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val isScreenshotProtected by viewModel.isScreenshotProtection.collectAsStateWithLifecycle()
            val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
            val layoutDirection = if (currentLang == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

            // Android Hardware Flag: Anti-screenshot protection
            LaunchedEffect(isScreenshotProtected) {
                if (isScreenshotProtected) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                JaffarForensicsTheme(darkTheme = isDarkTheme) {
                    val hudMessage by viewModel.hudMessage.collectAsStateWithLifecycle()
                    val isBiometricUnlocked by viewModel.isBiometricUnlocked.collectAsStateWithLifecycle()
                    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
                    val isCloudSyncing by viewModel.isCloudSyncing.collectAsStateWithLifecycle()
                    val pendingSyncCount by viewModel.pendingSyncCount.collectAsStateWithLifecycle()

                    var currentScreen by remember { mutableIntStateOf(ScreenDestination.DASHBOARD.id) }
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val scope = rememberCoroutineScope()

                    val bottomNavItems = listOf(
                        ScreenDestination.DASHBOARD,
                        ScreenDestination.CASES,
                        ScreenDestination.EVIDENCE,
                        ScreenDestination.TASKS,
                        ScreenDestination.STUDIO
                    )

                    val drawerItems = listOf(
                        ScreenDestination.DASHBOARD,
                        ScreenDestination.CASES,
                        ScreenDestination.EXTERNAL_REQUESTS,
                        ScreenDestination.EVIDENCE,
                        ScreenDestination.TASKS,
                        ScreenDestination.SUPPORT_FORMS,
                        ScreenDestination.INVESTIGATION_HUB,
                        ScreenDestination.STUDIO,
                        ScreenDestination.CLIENTS,
                        ScreenDestination.KNOWLEDGE,
                        ScreenDestination.REPORTS,
                        ScreenDestination.SEARCH,
                        ScreenDestination.TRASH,
                        ScreenDestination.SETTINGS,
                        ScreenDestination.SECURITY
                    )

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet(
                                drawerContainerColor = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.width(300.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(CyberPrimary.copy(alpha = 0.2f))
                                                .border(1.dp, CyberPrimary, RoundedCornerShape(10.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Shield,
                                                contentDescription = null,
                                                tint = CyberPrimary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        Column {
                                            Text(
                                                text = "منظومة جعفر بدران",
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "إدارة العمل والقضايا والطلبات",
                                                color = CyberPrimaryLight,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CyberSuccess.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "المسؤول: جعفر بدران (مدير المنظومة)",
                                            color = CyberSuccess,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    items(drawerItems) { item ->
                                        val isSelected = currentScreen == item.id
                                        NavigationDrawerItem(
                                            icon = {
                                                Icon(
                                                    imageVector = item.icon,
                                                    contentDescription = item.title,
                                                    tint = if (isSelected) CyberPrimaryLight else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            },
                                            label = {
                                                Text(
                                                    text = item.title,
                                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            selected = isSelected,
                                            onClick = {
                                                currentScreen = item.id
                                                scope.launch { drawerState.close() }
                                            },
                                            colors = NavigationDrawerItemDefaults.colors(
                                                selectedContainerColor = CyberPrimary.copy(alpha = 0.15f),
                                                unselectedContainerColor = Color.Transparent
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                containerColor = MaterialTheme.colorScheme.background,
                                topBar = {
                                    TopAppBar(
                                        title = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(
                                                    onClick = { scope.launch { drawerState.open() } }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Menu,
                                                        contentDescription = "القائمة الجانبية",
                                                        tint = MaterialTheme.colorScheme.onSurface
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(4.dp))

                                                Column {
                                                    Text(
                                                        text = "منظومة جعفر بدران",
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "إدارة العمل والقضايا والطلبات",
                                                        color = CyberPrimaryLight,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }
                                        },
                                        actions = {
                                            // Search button
                                            IconButton(onClick = { currentScreen = ScreenDestination.SEARCH.id }) {
                                                Icon(
                                                    imageVector = Icons.Default.Search,
                                                    contentDescription = "بحث شامل",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            // Sync button
                                            IconButton(onClick = { viewModel.syncNowWithGoogleSheets() }) {
                                                Icon(
                                                    imageVector = if (isCloudSyncing) Icons.Default.Refresh else Icons.Default.CloudDone,
                                                    contentDescription = "مزامنة سحابية",
                                                    tint = if (isCloudSyncing) CyberWarning else CyberSuccess,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            // Theme toggle button
                                            IconButton(onClick = { viewModel.toggleTheme() }) {
                                                Icon(
                                                    imageVector = if (isDarkTheme) Icons.Default.Brightness4 else Icons.Default.Brightness7,
                                                    contentDescription = "تبديل المظهر",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            // Biometric lock button
                                            IconButton(
                                                onClick = { viewModel.toggleBiometricLock() },
                                                modifier = Modifier.testTag("app_lock_button")
                                            ) {
                                                Icon(
                                                    imageVector = if (isBiometricUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                                    contentDescription = "قفل التطبيق",
                                                    tint = if (isBiometricUnlocked) CyberPrimaryLight else CyberDanger,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        },
                                        colors = TopAppBarDefaults.topAppBarColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        )
                                    )
                                },
                                bottomBar = {
                                    NavigationBar(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        contentColor = CyberPrimary,
                                        tonalElevation = 8.dp
                                    ) {
                                        bottomNavItems.forEach { tab ->
                                            val isSelected = currentScreen == tab.id
                                            NavigationBarItem(
                                                selected = isSelected,
                                                onClick = { currentScreen = tab.id },
                                                icon = {
                                                    Icon(
                                                        imageVector = tab.icon,
                                                        contentDescription = tab.title,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = tab.title,
                                                        fontSize = 11.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = CyberPrimaryLight,
                                                    selectedTextColor = CyberPrimaryLight,
                                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    indicatorColor = CyberPrimary.copy(alpha = 0.15f)
                                                )
                                            )
                                        }
                                    }
                                }
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {
                                    when (currentScreen) {
                                        ScreenDestination.DASHBOARD.id -> DashboardScreen(
                                            viewModel = viewModel,
                                            onNavigateToCases = { currentScreen = ScreenDestination.CASES.id },
                                            onNavigateToEvidence = { currentScreen = ScreenDestination.EVIDENCE.id },
                                            onNavigateToClients = { currentScreen = ScreenDestination.CLIENTS.id },
                                            onNavigateToContent = { currentScreen = ScreenDestination.STUDIO.id },
                                            onNavigateToTasks = { currentScreen = ScreenDestination.TASKS.id },
                                            onNavigateToReports = { currentScreen = ScreenDestination.REPORTS.id },
                                            onNavigateToSettings = { currentScreen = ScreenDestination.SETTINGS.id },
                                            onOpenNewCaseDialog = { currentScreen = ScreenDestination.CASES.id },
                                            onOpenNewEvidenceDialog = { currentScreen = ScreenDestination.EVIDENCE.id },
                                            onOpenNewClientDialog = { currentScreen = ScreenDestination.CLIENTS.id },
                                            onOpenNewContentDialog = { currentScreen = ScreenDestination.STUDIO.id },
                                            onNavigateToSupportForms = { currentScreen = ScreenDestination.SUPPORT_FORMS.id },
                                            onNavigateToInvestigationHub = { currentScreen = ScreenDestination.INVESTIGATION_HUB.id },
                                            onNavigateToExternalRequests = { currentScreen = ScreenDestination.EXTERNAL_REQUESTS.id }
                                        )
                                        ScreenDestination.CASES.id -> CasesScreen(viewModel = viewModel)
                                        ScreenDestination.EXTERNAL_REQUESTS.id -> ExternalRequestsScreen(
                                            viewModel = viewModel,
                                            onOpenCase = { currentScreen = ScreenDestination.CASES.id }
                                        )
                                        ScreenDestination.EVIDENCE.id -> EvidenceScreen(viewModel = viewModel)
                                        ScreenDestination.TASKS.id -> TasksScreen(viewModel = viewModel)
                                        ScreenDestination.SUPPORT_FORMS.id -> SupportFormsScreen(viewModel = viewModel)
                                        ScreenDestination.INVESTIGATION_HUB.id -> InvestigationToolsScreen(viewModel = viewModel)
                                        ScreenDestination.STUDIO.id -> ContentStudioScreen(viewModel = viewModel)
                                        ScreenDestination.CLIENTS.id -> ClientsScreen(viewModel = viewModel)
                                        ScreenDestination.KNOWLEDGE.id -> KnowledgeBaseScreen(viewModel = viewModel)
                                        ScreenDestination.REPORTS.id -> ReportsScreen(viewModel = viewModel)
                                        ScreenDestination.SEARCH.id -> GlobalSearchScreen(
                                            viewModel = viewModel,
                                            onNavigateToCases = { currentScreen = ScreenDestination.CASES.id },
                                            onNavigateToEvidence = { currentScreen = ScreenDestination.EVIDENCE.id },
                                            onNavigateToClients = { currentScreen = ScreenDestination.CLIENTS.id },
                                            onNavigateToContent = { currentScreen = ScreenDestination.STUDIO.id },
                                            onNavigateToKnowledge = { currentScreen = ScreenDestination.KNOWLEDGE.id },
                                            onNavigateToTasks = { currentScreen = ScreenDestination.TASKS.id },
                                            onNavigateToSupportForms = { currentScreen = ScreenDestination.SUPPORT_FORMS.id },
                                            onNavigateToInvestigationHub = { currentScreen = ScreenDestination.INVESTIGATION_HUB.id }
                                        )
                                        ScreenDestination.TRASH.id -> TrashScreen(viewModel = viewModel)
                                        ScreenDestination.SETTINGS.id -> SettingsScreen(
                                            viewModel = viewModel,
                                            onNavigateToTrash = { currentScreen = ScreenDestination.TRASH.id }
                                        )
                                        ScreenDestination.SECURITY.id -> AuditAndSecurityScreen(viewModel = viewModel)
                                    }
                                }
                            }

                            // Floating Non-Blocking HUD Banner
                            NonBlockingHudOverlay(
                                message = hudMessage,
                                onDismiss = { viewModel.dismissHud() },
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 70.dp)
                            )

                            // Biometric Lock Overlay (Non-blocking screen cover when locked)
                            AnimatedVisibility(
                                visible = !isBiometricUnlocked,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                BiometricLockScreen(
                                    onUnlock = { viewModel.toggleBiometricLock() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BiometricLockScreen(onUnlock: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.98f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(CyberPrimary.copy(alpha = 0.15f))
                    .border(2.dp, CyberPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "بصمة الإصبع",
                    tint = CyberPrimary,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "المنظومة مقفلة بالأمان الحيوي",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "البيانات والملفات مشفرة وفق معايير الأمان المتقدمة. المس مستشعر البصمة للمتابعة.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onUnlock,
                colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
                    .testTag("biometric_unlock_button")
            ) {
                Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("تأكيد البصمة وفك القفل", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
