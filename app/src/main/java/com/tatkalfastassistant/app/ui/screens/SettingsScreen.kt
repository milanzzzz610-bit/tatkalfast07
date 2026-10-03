package com.tatkalfastassistant.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tatkalfastassistant.app.data.model.AppSettings
import com.tatkalfastassistant.app.data.model.BookingProfile
import com.tatkalfastassistant.app.data.security.CryptoManager
import com.tatkalfastassistant.app.notification.TatkalAlarmScheduler

@Composable
fun SettingsScreen(
    cryptoManager: CryptoManager,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    activeProfile: BookingProfile
) {
    val context = LocalContext.current
    var settings by remember { mutableStateOf(cryptoManager.getSettings()) }
    val scrollState = rememberScrollState()

    fun updateSettings(newSettings: AppSettings) {
        settings = newSettings
        cryptoManager.saveSettings(newSettings)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SETTINGS & PREFERENCES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Configure booking alarms, language, appearance, and local cryptographic security.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Appearance & Language Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Appearance & Language",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Dark Theme", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text("High contrast dark theme for booking", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme() }
                    )
                }

                Divider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Language", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text("Current: ${if (settings.language == "hi") "हिन्दी (Hindi)" else "English"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = settings.language == "en",
                            onClick = {
                                updateSettings(settings.copy(language = "en"))
                                Toast.makeText(context, "English selected", Toast.LENGTH_SHORT).show()
                            },
                            label = { Text("EN") }
                        )
                        FilterChip(
                            selected = settings.language == "hi",
                            onClick = {
                                updateSettings(settings.copy(language = "hi"))
                                Toast.makeText(context, "हिन्दी चुनी गई", Toast.LENGTH_SHORT).show()
                            },
                            label = { Text("हिन्दी") }
                        )
                    }
                }
            }
        }

        // Tatkal Alarms & Reminder Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Tatkal Window Alarms",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tatkal Alarm Reminders", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text("Exact alarm 10:00 AM (AC) / 11:00 AM (Non-AC)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.alarmEnabled,
                        onCheckedChange = { isEnabled ->
                            updateSettings(settings.copy(alarmEnabled = isEnabled))
                            if (isEnabled) {
                                TatkalAlarmScheduler.scheduleTatkalAlarm(
                                    context,
                                    activeProfile,
                                    settings.alarmLeadMinutes
                                )
                                Toast.makeText(context, "Tatkal reminder alarm scheduled", Toast.LENGTH_SHORT).show()
                            } else {
                                TatkalAlarmScheduler.cancelTatkalAlarm(context, activeProfile.id)
                                Toast.makeText(context, "Alarm cancelled", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                Text("Alarm Lead Time:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(15, 5, 1).forEach { min ->
                        FilterChip(
                            selected = settings.alarmLeadMinutes == min,
                            onClick = {
                                updateSettings(settings.copy(alarmLeadMinutes = min))
                                if (settings.alarmEnabled) {
                                    TatkalAlarmScheduler.scheduleTatkalAlarm(context, activeProfile, min)
                                }
                                Toast.makeText(context, "Alarm set $min min before Tatkal", Toast.LENGTH_SHORT).show()
                            },
                            label = { Text("$min min prior") }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Audio Alert Chimes", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        Text("Chime upon clipboard copy and window opening", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = settings.soundAlerts,
                        onCheckedChange = { updateSettings(settings.copy(soundAlerts = it)) }
                    )
                }

                Button(
                    onClick = {
                        val scheduled = TatkalAlarmScheduler.scheduleTatkalAlarm(
                            context = context,
                            profile = activeProfile,
                            leadMinutes = settings.alarmLeadMinutes
                        )
                        if (scheduled) {
                            Toast.makeText(context, "Test Tatkal alarm scheduled successfully!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Permission needed to schedule exact alarm in Android Settings.", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test / Re-sync Alarm Reminder", fontSize = 12.sp)
                }
            }
        }

        // IRCTC Preferred Payment Recommendation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("IRCTC eWallet Preferred", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            "Fastest checkout during Tatkal rush. Bypasses external bank gateway hops and does not require SMS OTP.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = settings.eWalletPreferred,
                        onCheckedChange = { updateSettings(settings.copy(eWalletPreferred = it)) }
                    )
                }
            }
        }

        // Local Hardware-Backed Security Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Hardware-Backed Keystore Active",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF10B981)
                    )
                }
                Text(
                    text = "All profiles and passenger records are encrypted locally using Android Jetpack Security MasterKey (AES-256 GCM). No data ever leaves your device.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Strict IRCTC Compliance: This assistant does NOT store your IRCTC password, does NOT bypass CAPTCHA, and does NOT automate financial transactions.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
