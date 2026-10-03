package com.tatkalfastassistant.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tatkalfastassistant.app.data.model.BookingHistoryItem
import com.tatkalfastassistant.app.data.security.CryptoManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(
    cryptoManager: CryptoManager
) {
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    var historyList by remember {
        mutableStateOf(
            cryptoManager.getHistory().ifEmpty {
                listOf(
                    BookingHistoryItem(
                        id = "hist_1",
                        pnr = "2458917240",
                        trainNumber = "12952",
                        trainName = "Mumbai Tejas Rajdhani",
                        fromStation = "NDLS",
                        toStation = "CSMT",
                        journeyDate = "2026-09-12",
                        travelClass = "3A",
                        status = "CONFIRMED",
                        coachBerth = "B4-23 (MB), B4-24 (UB)",
                        totalFare = 4920.0,
                        bookedAt = "10:01:14 AM IST"
                    ),
                    BookingHistoryItem(
                        id = "hist_2",
                        pnr = "4810293847",
                        trainNumber = "12004",
                        trainName = "Lucknow Swarna Shatabdi",
                        fromStation = "NDLS",
                        toStation = "LKO",
                        journeyDate = "2026-08-28",
                        travelClass = "CC",
                        status = "CONFIRMED",
                        coachBerth = "C2-45 (WS)",
                        totalFare = 1165.0,
                        bookedAt = "10:00:52 AM IST"
                    )
                )
            }
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }

    fun copyToClipboard(text: String, label: String) {
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied", Toast.LENGTH_SHORT).show()
    }

    fun openPnrStatusPortal(pnr: String) {
        val pnrUrl = "https://www.indianrail.gov.in/enquiry/PNR/PnrEnquiry.html?locale=en"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pnrUrl))
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Booking Record")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BOOKING HISTORY & PNR",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${historyList.size} Record(s)",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Encrypted local record of confirmed and waitlisted Tatkal reservations. One-tap verify on official Indian Railways portal.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No booking records saved yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tap + to log your PNR or completed Tatkal bookings.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(historyList, key = { it.id }) { item ->
                        HistoryCard(
                            item = item,
                            onCopyPnr = { copyToClipboard(item.pnr, "PNR ${item.pnr}") },
                            onVerifyPnr = { openPnrStatusPortal(item.pnr) },
                            onDelete = {
                                val updated = historyList.filterNot { it.id == item.id }
                                historyList = updated
                                cryptoManager.saveHistory(updated)
                                Toast.makeText(context, "Record removed", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBookingDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newItem ->
                val updated = listOf(newItem) + historyList
                historyList = updated
                cryptoManager.saveHistory(updated)
                showAddDialog = false
                Toast.makeText(context, "Booking record saved", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun HistoryCard(
    item: BookingHistoryItem,
    onCopyPnr: () -> Unit,
    onVerifyPnr: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PNR: ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.pnr,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                StatusBadge(status = item.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${item.trainNumber} • ${item.trainName}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )

            Text(
                text = "${item.fromStation} → ${item.toStation} | ${item.journeyDate} | Class: ${item.travelClass}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (item.coachBerth.isNotEmpty()) {
                Text(
                    text = "Coach/Berth: ${item.coachBerth}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF10B981)
                )
            }

            if (item.totalFare > 0) {
                Text(
                    text = "Fare: ₹${String.format("%.2f", item.totalFare)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopyPnr,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy PNR", fontSize = 12.sp)
                }

                Button(
                    onClick = onVerifyPnr,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Verify IRCTC", fontSize = 12.sp)
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status.uppercase()) {
        "CONFIRMED" -> Color(0xFF065F46) to Color(0xFF6EE7B7)
        "RAC" -> Color(0xFF78350F) to Color(0xFFFDE68A)
        "WAITLISTED" -> Color(0xFF831843) to Color(0xFFFBCFE8)
        else -> Color(0xFF374151) to Color(0xFFE5E7EB)
    }

    Box(
        modifier = Modifier
            .background(bgColor, shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookingDialog(
    onDismiss: () -> Unit,
    onAdd: (BookingHistoryItem) -> Unit
) {
    var pnr by remember { mutableStateOf("") }
    var trainNumber by remember { mutableStateOf("12952") }
    var trainName by remember { mutableStateOf("Mumbai Tejas Rajdhani") }
    var fromStation by remember { mutableStateOf("NDLS") }
    var toStation by remember { mutableStateOf("CSMT") }
    var journeyDate by remember { mutableStateOf("2026-09-12") }
    var travelClass by remember { mutableStateOf("3A") }
    var status by remember { mutableStateOf("CONFIRMED") }
    var coachBerth by remember { mutableStateOf("B4-23") }
    var totalFare by remember { mutableStateOf("2450") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Booking Record", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = pnr,
                    onValueChange = { pnr = it },
                    label = { Text("10-digit PNR Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = trainNumber,
                        onValueChange = { trainNumber = it },
                        label = { Text("Train No") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = travelClass,
                        onValueChange = { travelClass = it },
                        label = { Text("Class") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = trainName,
                    onValueChange = { trainName = it },
                    label = { Text("Train Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = fromStation,
                        onValueChange = { fromStation = it },
                        label = { Text("From Code") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = toStation,
                        onValueChange = { toStation = it },
                        label = { Text("To Code") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = journeyDate,
                        onValueChange = { journeyDate = it },
                        label = { Text("Journey Date") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Status (CNF/RAC/WL)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = coachBerth,
                        onValueChange = { coachBerth = it },
                        label = { Text("Coach/Berth") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = totalFare,
                        onValueChange = { totalFare = it },
                        label = { Text("Fare (₹)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (pnr.isNotBlank()) {
                        val newItem = BookingHistoryItem(
                            id = "hist_${System.currentTimeMillis()}",
                            pnr = pnr.trim(),
                            trainNumber = trainNumber.trim(),
                            trainName = trainName.trim(),
                            fromStation = fromStation.trim().uppercase(),
                            toStation = toStation.trim().uppercase(),
                            journeyDate = journeyDate.trim(),
                            travelClass = travelClass.trim().uppercase(),
                            status = status.trim().uppercase(),
                            coachBerth = coachBerth.trim(),
                            totalFare = totalFare.toDoubleOrNull() ?: 0.0,
                            bookedAt = "Just now"
                        )
                        onAdd(newItem)
                    }
                }
            ) {
                Text("Save Record")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
