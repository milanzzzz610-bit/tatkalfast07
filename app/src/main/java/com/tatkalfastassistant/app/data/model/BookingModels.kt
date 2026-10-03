package com.tatkalfastassistant.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Passenger(
    val id: String,
    val name: String,
    val age: Int,
    val gender: String, // "M", "F", "T"
    val berthPreference: String, // "LB", "MB", "UB", "SL", "SU", "WS", "NO"
    val foodPreference: String = "V",
    val seniorCitizen: Boolean = false
) : Parcelable

@Parcelize
data class BackupTrain(
    val trainNumber: String,
    val trainName: String
) : Parcelable

@Parcelize
data class BookingProfile(
    val id: String,
    val name: String,
    val fromStationCode: String,
    val fromStationName: String,
    val toStationCode: String,
    val toStationName: String,
    val journeyDate: String, // YYYY-MM-DD
    val quota: String, // "TQ", "PT", "GN"
    val travelClass: String, // "3A", "2A", "SL", "CC"
    val preferredTrainNumber: String,
    val preferredTrainName: String,
    val backupTrains: List<BackupTrain> = emptyList(),
    val passengers: List<Passenger> = emptyList(),
    val preferredPayment: String = "E_WALLET",
    val notificationMinutesBefore: Int = 5
) : Parcelable {
    val isAcClass: Boolean
        get() = travelClass in listOf("1A", "2A", "3A", "3E", "CC", "EC")

    val openingHour: Int
        get() = if (isAcClass) 10 else 11
}

@Parcelize
data class BookingHistoryItem(
    val id: String,
    val pnr: String,
    val trainNumber: String,
    val trainName: String,
    val fromStation: String,
    val toStation: String,
    val journeyDate: String,
    val travelClass: String,
    val status: String,
    val coachBerth: String = "",
    val totalFare: Double = 0.0,
    val bookedAt: String = ""
) : Parcelable

@Parcelize
data class AppSettings(
    val language: String = "en",
    val isDarkTheme: Boolean = true,
    val alarmEnabled: Boolean = true,
    val alarmLeadMinutes: Int = 5,
    val soundAlerts: Boolean = true,
    val eWalletPreferred: Boolean = true
) : Parcelable

