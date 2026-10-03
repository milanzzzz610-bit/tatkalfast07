package com.tatkalfastassistant.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tatkalfastassistant.app.data.model.BookingProfile
import com.tatkalfastassistant.app.ui.components.PaymentConfirmationDialog
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.TimeZone

@Composable
fun DashboardScreen(
    profile: BookingProfile,
    onOpenIrctc: () -> Unit,
    onNavigateToRapid: () -> Unit
) {
    var showPaymentDialog by remember { mutableStateOf(false) }

    // Synchronized IST clock calculation
    var currentTimeString by remember { mutableStateOf("") }
    var countdownHours by remember { mutableIntStateOf(0) }
    var countdownMinutes by remember { mutableIntStateOf(0) }
    var countdownSeconds by remember { mutableIntStateOf(0) }
    var isWindowOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            val tz = TimeZone.getTimeZone("Asia/Kolkata")
            val cal = Calendar.getInstance(tz)

            val currentHour = cal.get(Calendar.HOUR_OF_DAY)
            val currentMin = cal.get(Calendar.MINUTE)
            val currentSec = cal.get(Calendar.SECOND)

            currentTimeString = String.format("%02d:%02d:%02d IST", currentHour, currentMin, currentSec)

            val targetHour = profile.openingHour // 10 for AC, 11 for Non-AC

            if (currentHour == targetHour && currentMin < 30) {
                isWindowOpen = true
                countdownHours = 0
                countdownMinutes = 0
                countdownSeconds = 0
            } else {
                isWindowOpen = false
                val targetCal = Calendar.getInstance(tz).apply {
                    set(Calendar.HOUR_OF_DAY, targetHour)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                    if (timeInMillis <= cal.timeInMillis) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
                val diffSecs = (targetCal.timeInMillis - cal.timeInMillis) / 1000
                countdownHours = (diffSecs / 3600).toInt()
                countdownMinutes = ((diffSecs % 3600) / 60).toInt()
                countdownSeconds = (diffSecs % 60).toInt()
            }
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Countdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INDIAN STANDARD TIME",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = currentTimeString,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val targetTitle = if (profile.isAcClass) "AC Tatkal (10:00 AM IST)" else "Non-AC Tatkal (11:00 AM IST)"
                Text(
                    text = if (isWindowOpen) "TATKAL BOOKING IS OPEN NOW!" else "Time Remaining to $targetTitle",
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isWindowOpen) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isWindowOpen) {
                    Text(
                        text = "OPEN NOW",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF10B981)
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimeBox(value = String.format("%02d", countdownHours), label = "HRS")
                        Text(":", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        TimeBox(value = String.format("%02d", countdownMinutes), label = "MIN")
                        Text(":", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        TimeBox(value = String.format("%02d", countdownSeconds), label = "SEC")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onOpenIrctc,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open Official IRCTC Flow", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ACTIVE PROFILE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${profile.travelClass} (${profile.quota})",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${profile.fromStationCode} → ${profile.toStationCode}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${profile.preferredTrainNumber} - ${profile.preferredTrainName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Date: ${profile.journeyDate} • ${profile.passengers.size} Passenger(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToRapid,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Rapid Assistant", fontSize = 12.sp)
                    }
                    Button(
                        onClick = { showPaymentDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Verify Payment", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showPaymentDialog) {
        PaymentConfirmationDialog(
            profile = profile,
            onDismiss = { showPaymentDialog = false },
            onConfirm = {
                showPaymentDialog = false
                onOpenIrctc()
            }
        )
    }
}

@Composable
fun TimeBox(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0F172A))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = value,
            fontFamily = FontFamily.Monospace,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF59E0B)
        )
        Text(text = label, fontSize = 9.sp, color = Color.Gray)
    }
}
