package com.tatkalfastassistant.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tatkalfastassistant.app.data.model.BookingProfile

@Composable
fun PaymentConfirmationDialog(
    profile: BookingProfile,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var acknowledged by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        title = {
            Text(
                text = "Verify Payment Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Confirm train and passenger details before proceeding to the official IRCTC payment gateway.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Train: ${profile.preferredTrainNumber} - ${profile.preferredTrainName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Route: ${profile.fromStationCode} to ${profile.toStationCode}", fontSize = 12.sp)
                    Text("Class & Quota: ${profile.travelClass} (${profile.quota})", fontSize = 12.sp)
                    Text("Passengers: ${profile.passengers.size}", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // eWallet recommendation notice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF064E3B), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "IRCTC eWallet Preferred: No payment gateway redirect and no SMS OTP required on IRCTC.",
                        fontSize = 10.sp,
                        color = Color(0xFFA7F3D0)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = acknowledged,
                        onCheckedChange = { acknowledged = it }
                    )
                    Text(
                        text = "I have verified all booking details.",
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = acknowledged,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Proceed to IRCTC Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
