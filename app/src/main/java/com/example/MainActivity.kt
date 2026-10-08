package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.TetherRepository
import com.example.ui.components.AppBottomNavigation
import com.example.ui.components.AppHeader
import com.example.ui.components.NotificationDialog
import com.example.ui.components.PushNotificationSnackbar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MarketAnalyticsScreen
import com.example.ui.screens.PlansScreen
import com.example.ui.screens.ReferralScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        val repository = TetherRepository(
            userDao = db.userDao(),
            investmentDao = db.investmentDao(),
            transactionDao = db.transactionDao(),
            referralDao = db.referralDao(),
            notificationDao = db.notificationDao(),
            adminTreasuryDao = db.adminTreasuryDao()
        )
        val factory = MainViewModelFactory(repository)

        setContent {
            val mainViewModel: MainViewModel = viewModel(factory = factory)
            TetherApp(viewModel = mainViewModel)
        }
    }
}

@Composable
fun TetherApp(viewModel: MainViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val notifications by viewModel.userNotifications.collectAsState()

    var showNotificationsDialog by remember { mutableStateOf(false) }

    MyApplicationTheme(themeMode = themeMode) {
        if (!isLoggedIn || currentUser == null) {
            AuthScreen(viewModel = viewModel, language = language)
        } else {
            // Handle Android Back button
            BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
                if (currentScreen == AppScreen.ADMIN) {
                    viewModel.navigateTo(AppScreen.DASHBOARD)
                } else {
                    viewModel.navigateTo(AppScreen.DASHBOARD)
                }
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    AppHeader(
                        currentScreen = currentScreen,
                        currentLanguage = language,
                        unreadNotificationsCount = notifications.count { !it.isRead },
                        isAdminUser = currentUser?.role == "ADMIN",
                        onLanguageSelected = { viewModel.setLanguage(it) },
                        onNotificationClick = { showNotificationsDialog = true },
                        onAdminToggleClick = {
                            if (currentScreen == AppScreen.ADMIN) {
                                viewModel.navigateTo(AppScreen.DASHBOARD)
                            } else {
                                viewModel.navigateTo(AppScreen.ADMIN)
                            }
                        }
                    )
                },
                bottomBar = {
                    AppBottomNavigation(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) },
                        language = language
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        AppScreen.DASHBOARD -> DashboardScreen(
                            viewModel = viewModel,
                            user = currentUser!!,
                            language = language,
                            onNavigate = { viewModel.navigateTo(it) }
                        )

                        AppScreen.PLANS -> PlansScreen(
                            viewModel = viewModel,
                            language = language
                        )

                        AppScreen.WALLET -> WalletScreen(
                            viewModel = viewModel,
                            user = currentUser!!,
                            language = language
                        )

                        AppScreen.MARKETS -> MarketAnalyticsScreen(
                            viewModel = viewModel,
                            language = language
                        )

                        AppScreen.AFFILIATE -> ReferralScreen(
                            viewModel = viewModel,
                            user = currentUser!!,
                            language = language
                        )

                        AppScreen.ADMIN -> AdminScreen(
                            viewModel = viewModel,
                            language = language,
                            onExitAdmin = { viewModel.navigateTo(AppScreen.DASHBOARD) }
                        )

                        AppScreen.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            user = currentUser!!,
                            currentTheme = themeMode,
                            currentLanguage = language,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }

                    // Push Notification / In-App Feedback Banner
                    PushNotificationSnackbar(
                        message = uiMessage,
                        onDismiss = { viewModel.dismissMessage() }
                    )
                }
            }

            if (showNotificationsDialog) {
                NotificationDialog(
                    notifications = notifications,
                    onDismiss = { showNotificationsDialog = false }
                )
            }
        }
    }
}
