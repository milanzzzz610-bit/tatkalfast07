package com.tatkalfastassistant.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.tatkalfastassistant.app.data.model.BookingProfile
import com.tatkalfastassistant.app.data.model.Passenger
import com.tatkalfastassistant.app.data.security.CryptoManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingProfileScreen(
    cryptoManager: CryptoManager,
    activeProfile: BookingProfile,
    onSetActiveProfile: (BookingProfile) -> Unit
) {
    val context = LocalContext.current
    var profileList by remember {
        mutableStateOf(
            cryptoManager.getProfiles().ifEmpty {
                listOf(activeProfile)
            }
        )
    }

    var editingProfile by remember { mutableStateOf<BookingProfile?>(null) }
    var isNewProfile by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingProfile = BookingProfile(
                        id = "prof_${System.currentTimeMillis()}",
                        name = "New Journey Profile",
                        fromStationCode = "NDLS",
                        fromStationName = "New Delhi",
                        toStationCode = "BCT",
                        toStationName = "Mumbai Central",
                        journeyDate = "2026-09-15",
                        quota = "TQ",
                        travelClass = "3A",
                        preferredTrainNumber = "12952",
                        preferredTrainName = "Tejas Rajdhani",
                        passengers = listOf(
                            Passenger("p_${System.currentTimeMillis()}", "PASSENGER NAME", 30, "M", "LB", "V")
                        )
                    )
                    isNewProfile = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Profile")
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
                    Text(
                        text = "BOOKING PROFILES & PASSENGERS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Pre-fill train numbers, station codes, and master passenger lists for fast 10:00 AM / 11:00 AM Tatkal booking.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(profileList, key = { it.id }) { prof ->
                    val isActive = prof.id == activeProfile.id
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActive) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = prof.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isActive) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFF065F46), shape = RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("ACTIVE", color = Color(0xFF6EE7B7), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${prof.fromStationCode} (${prof.fromStationName}) → ${prof.toStationCode} (${prof.toStationName})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Train: ${prof.preferredTrainNumber} - ${prof.preferredTrainName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Date: ${prof.journeyDate} • Class: ${prof.travelClass} • Quota: ${prof.quota}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Passengers (${prof.passengers.size}): ${prof.passengers.joinToString { it.name }}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!isActive) {
                                    Button(
                                        onClick = {
                                            onSetActiveProfile(prof)
                                            cryptoManager.saveActiveProfileId(prof.id)
                                            Toast.makeText(context, "Activated: ${prof.name}", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Set Active", fontSize = 12.sp)
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        editingProfile = prof
                                        isNewProfile = false
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Profile", fontSize = 12.sp)
                                }

                                if (profileList.size > 1 && !isActive) {
                                    IconButton(
                                        onClick = {
                                            val updated = profileList.filterNot { it.id == prof.id }
                                            profileList = updated
                                            cryptoManager.saveProfiles(updated)
                                            Toast.makeText(context, "Profile deleted", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    editingProfile?.let { profToEdit ->
        ProfileEditDialog(
            profile = profToEdit,
            onDismiss = { editingProfile = null },
            onSave = { savedProfile ->
                val updated = if (isNewProfile) {
                    profileList + savedProfile
                } else {
                    profileList.map { if (it.id == savedProfile.id) savedProfile else it }
                }
                profileList = updated
                cryptoManager.saveProfiles(updated)
                if (savedProfile.id == activeProfile.id || isNewProfile && profileList.size == 1) {
                    onSetActiveProfile(savedProfile)
                }
                editingProfile = null
                Toast.makeText(context, "Profile saved successfully", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditDialog(
    profile: BookingProfile,
    onDismiss: () -> Unit,
    onSave: (BookingProfile) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var fromCode by remember { mutableStateOf(profile.fromStationCode) }
    var fromName by remember { mutableStateOf(profile.fromStationName) }
    var toCode by remember { mutableStateOf(profile.toStationCode) }
    var toName by remember { mutableStateOf(profile.toStationName) }
    var journeyDate by remember { mutableStateOf(profile.journeyDate) }
    var quota by remember { mutableStateOf(profile.quota) }
    var travelClass by remember { mutableStateOf(profile.travelClass) }
    var trainNumber by remember { mutableStateOf(profile.preferredTrainNumber) }
    var trainName by remember { mutableStateOf(profile.preferredTrainName) }
    var passengers by remember { mutableStateOf(profile.passengers) }

    var showAddPassengerDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Booking Profile", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Profile Label") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fromCode,
                            onValueChange = { fromCode = it },
                            label = { Text("From Code") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = toCode,
                            onValueChange = { toCode = it },
                            label = { Text("To Code") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
                item {
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
                            label = { Text("Class (3A/2A/SL)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = trainName,
                        onValueChange = { trainName = it },
                        label = { Text("Train Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = journeyDate,
                            onValueChange = { journeyDate = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = quota,
                            onValueChange = { quota = it },
                            label = { Text("Quota (TQ/PT)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Passengers (${passengers.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        TextButton(onClick = { showAddPassengerDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Add", fontSize = 12.sp)
                        }
                    }
                }

                items(passengers) { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(p.name, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            Text("Age: ${p.age} • ${p.gender} • Berth: ${p.berthPreference}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(
                            onClick = { passengers = passengers.filterNot { it.id == p.id } },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = profile.copy(
                        name = name.trim(),
                        fromStationCode = fromCode.trim().uppercase(),
                        fromStationName = fromName.trim(),
                        toStationCode = toCode.trim().uppercase(),
                        toStationName = toName.trim(),
                        journeyDate = journeyDate.trim(),
                        quota = quota.trim().uppercase(),
                        travelClass = travelClass.trim().uppercase(),
                        preferredTrainNumber = trainNumber.trim(),
                        preferredTrainName = trainName.trim(),
                        passengers = passengers
                    )
                    onSave(updated)
                }
            ) {
                Text("Save Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showAddPassengerDialog) {
        var pName by remember { mutableStateOf("") }
        var pAge by remember { mutableStateOf("30") }
        var pGender by remember { mutableStateOf("M") }
        var pBerth by remember { mutableStateOf("LB") }

        AlertDialog(
            onDismissRequest = { showAddPassengerDialog = false },
            title = { Text("Add Passenger", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pName,
                        onValueChange = { pName = it },
                        label = { Text("Full Name (as on Govt ID)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = pAge,
                            onValueChange = { pAge = it },
                            label = { Text("Age") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = pGender,
                            onValueChange = { pGender = it },
                            label = { Text("Gender (M/F/T)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = pBerth,
                        onValueChange = { pBerth = it },
                        label = { Text("Berth Preference (LB/MB/UB/SL/SU/WS)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pName.isNotBlank()) {
                            val newPassenger = Passenger(
                                id = "p_${System.currentTimeMillis()}",
                                name = pName.trim().uppercase(),
                                age = pAge.toIntOrNull() ?: 30,
                                gender = pGender.trim().uppercase(),
                                berthPreference = pBerth.trim().uppercase(),
                                foodPreference = "V"
                            )
                            passengers = passengers + newPassenger
                            showAddPassengerDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPassengerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
