package com.tatkalfastassistant.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tatkalfastassistant.app.data.model.BookingProfile
import com.tatkalfastassistant.app.data.model.Passenger
import com.tatkalfastassistant.app.data.security.CryptoManager
import com.tatkalfastassistant.app.ui.screens.BookingHistoryScreen
import com.tatkalfastassistant.app.ui.screens.BookingProfileScreen
import com.tatkalfastassistant.app.ui.screens.DashboardScreen
import com.tatkalfastassistant.app.ui.screens.RapidEntryScreen
import com.tatkalfastassistant.app.ui.screens.SettingsScreen
import com.tatkalfastassistant.app.ui.theme.TatkalFastAssistantTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cryptoManager = CryptoManager(this)

        setContent {
            var isDarkTheme by remember { mutableStateOf(true) }

            TatkalFastAssistantTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TatkalMainApp(
                        cryptoManager = cryptoManager,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TatkalMainApp(
    cryptoManager: CryptoManager,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Sample default profile if none exists in secure storage
    val defaultProfile = remember {
        BookingProfile(
            id = "default_1",
            name = "NDLS - CSMT Rajdhani",
            fromStationCode = "NDLS",
            fromStationName = "New Delhi",
            toStationCode = "CSMT",
            toStationName = "Mumbai CSMT",
            journeyDate = "2026-09-12",
            quota = "TQ",
            travelClass = "3A",
            preferredTrainNumber = "12952",
            preferredTrainName = "Mumbai Tejas Rajdhani",
            passengers = listOf(
                Passenger("p1", "RAHUL SHARMA", 32, "M", "LB", "V"),
                Passenger("p2", "PRIYA SHARMA", 29, "F", "MB", "V")
            ),
            preferredPayment = "E_WALLET"
        )
    }

    var activeProfile by remember {
        val saved = cryptoManager.getProfiles()
        val activeId = cryptoManager.getActiveProfileId()
        val found = saved.find { it.id == activeId } ?: saved.firstOrNull() ?: defaultProfile
        mutableStateOf(found)
    }
    var currentRoute by remember { mutableStateOf("dashboard") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tatkal Fast Assistant", style = MaterialTheme.typography.titleMedium) },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme"
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
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = currentRoute == "dashboard",
                    onClick = {
                        currentRoute = "dashboard"
                        navController.navigate("dashboard")
                    },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = currentRoute == "profiles",
                    onClick = {
                        currentRoute = "profiles"
                        navController.navigate("profiles")
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profiles") },
                    label = { Text("Profiles") }
                )
                NavigationBarItem(
                    selected = currentRoute == "rapid",
                    onClick = {
                        currentRoute = "rapid"
                        navController.navigate("rapid")
                    },
                    icon = { Icon(Icons.Default.ContentCopy, contentDescription = "Rapid Entry") },
                    label = { Text("Rapid") }
                )
                NavigationBarItem(
                    selected = currentRoute == "history",
                    onClick = {
                        currentRoute = "history"
                        navController.navigate("history")
                    },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "History") },
                    label = { Text("History") }
                )
                NavigationBarItem(
                    selected = currentRoute == "settings",
                    onClick = {
                        currentRoute = "settings"
                        navController.navigate("settings")
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                DashboardScreen(
                    profile = activeProfile,
                    onOpenIrctc = {
                        val irctcPackage = "cris.org.in.prs.ima"
                        val launchIntent = context.packageManager.getLaunchIntentForPackage(irctcPackage)
                            ?: Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.irctc.co.in/nget/booking/train-list?fromStation=${activeProfile.fromStationCode}&toStation=${activeProfile.toStationCode}&journeyDate=${activeProfile.journeyDate}&quotaCode=${activeProfile.quota}")
                            )
                        context.startActivity(launchIntent)
                    },
                    onNavigateToRapid = {
                        currentRoute = "rapid"
                        navController.navigate("rapid")
                    }
                )
            }
            composable("profiles") {
                BookingProfileScreen(
                    cryptoManager = cryptoManager,
                    activeProfile = activeProfile,
                    onSetActiveProfile = { activeProfile = it }
                )
            }
            composable("rapid") {
                RapidEntryScreen(profile = activeProfile)
            }
            composable("history") {
                BookingHistoryScreen(cryptoManager = cryptoManager)
            }
            composable("settings") {
                SettingsScreen(
                    cryptoManager = cryptoManager,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    activeProfile = activeProfile
                )
            }
        }
    }
}
