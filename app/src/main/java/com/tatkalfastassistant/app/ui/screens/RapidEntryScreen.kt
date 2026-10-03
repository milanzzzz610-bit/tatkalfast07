package com.tatkalfastassistant.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tatkalfastassistant.app.data.model.BookingProfile
import com.tatkalfastassistant.app.data.model.Passenger

@Composable
fun RapidEntryScreen(profile: BookingProfile) {
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun copyToClipboard(text: String, label: String) {
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "$label copied", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1-TAP RAPID ENTRY ASSISTANT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Tap any field to copy to clipboard for rapid pasting in official IRCTC flow.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val allPassengers = profile.passengers.mapIndexed { idx, p ->
                                "P${idx + 1}: ${p.name}, Age: ${p.age}, Gender: ${p.gender}, Berth: ${p.berthPreference}"
                            }.joinToString("\n")
                            copyToClipboard(allPassengers, "All Passengers")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy All Passengers")
                    }
                }
            }
        }

        itemsIndexed(profile.passengers) { index, passenger ->
            PassengerCopyCard(
                index = index + 1,
                passenger = passenger,
                onCopy = { text, label -> copyToClipboard(text, label) }
            )
        }
    }
}

@Composable
fun PassengerCopyCard(
    index: Int,
    passenger: Passenger,
    onCopy: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Passenger $index: ${passenger.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                TextButton(onClick = {
                    onCopy(passenger.name, "Name")
                }) {
                    Text("Copy Name", fontSize = 11.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onCopy(passenger.age.toString(), "Age") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Age: ${passenger.age}", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { onCopy(passenger.gender, "Gender") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Gender: ${passenger.gender}", fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = { onCopy(passenger.berthPreference, "Berth") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Berth: ${passenger.berthPreference}", fontSize = 11.sp)
                }
            }
        }
    }
}
