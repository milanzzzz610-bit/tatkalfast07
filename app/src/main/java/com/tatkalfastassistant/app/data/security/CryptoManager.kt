package com.tatkalfastassistant.app.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tatkalfastassistant.app.data.model.AppSettings
import com.tatkalfastassistant.app.data.model.BookingHistoryItem
import com.tatkalfastassistant.app.data.model.BookingProfile
import com.tatkalfastassistant.app.data.model.Passenger

/**
 * Manages hardware-backed local encrypted storage using Android Jetpack Security.
 * Encrypts keys with AES256_SIV and values with AES256_GCM.
 * Never stores or logs IRCTC credentials.
 */
class CryptoManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "tatkal_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()

    fun saveProfiles(profiles: List<BookingProfile>) {
        val json = gson.toJson(profiles)
        encryptedPrefs.edit().putString(KEY_PROFILES, json).apply()
    }

    fun getProfiles(): List<BookingProfile> {
        val json = encryptedPrefs.getString(KEY_PROFILES, null) ?: return emptyList()
        val type = object : TypeToken<List<BookingProfile>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveActiveProfileId(id: String) {
        encryptedPrefs.edit().putString(KEY_ACTIVE_ID, id).apply()
    }

    fun getActiveProfileId(): String? {
        return encryptedPrefs.getString(KEY_ACTIVE_ID, null)
    }

    fun saveHistory(history: List<BookingHistoryItem>) {
        val json = gson.toJson(history)
        encryptedPrefs.edit().putString(KEY_HISTORY, json).apply()
    }

    fun getHistory(): List<BookingHistoryItem> {
        val json = encryptedPrefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val type = object : TypeToken<List<BookingHistoryItem>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveSettings(settings: AppSettings) {
        val json = gson.toJson(settings)
        encryptedPrefs.edit().putString(KEY_SETTINGS, json).apply()
    }

    fun getSettings(): AppSettings {
        val json = encryptedPrefs.getString(KEY_SETTINGS, null) ?: return AppSettings()
        return try {
            gson.fromJson(json, AppSettings::class.java)
        } catch (e: Exception) {
            AppSettings()
        }
    }

    fun saveMasterPassengers(passengers: List<Passenger>) {
        val json = gson.toJson(passengers)
        encryptedPrefs.edit().putString(KEY_PASSENGERS, json).apply()
    }

    fun getMasterPassengers(): List<Passenger> {
        val json = encryptedPrefs.getString(KEY_PASSENGERS, null) ?: return emptyList()
        val type = object : TypeToken<List<Passenger>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    companion object {
        private const val KEY_PROFILES = "sec_booking_profiles"
        private const val KEY_ACTIVE_ID = "sec_active_profile_id"
        private const val KEY_HISTORY = "sec_booking_history"
        private const val KEY_SETTINGS = "sec_app_settings"
        private const val KEY_PASSENGERS = "sec_master_passengers"
    }
}
