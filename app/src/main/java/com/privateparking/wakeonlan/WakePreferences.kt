package com.privateparking.wakeonlan

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wake_settings")

object WakePreferences {
    private val DEVICE_NAME = stringPreferencesKey("device_name")
    private val MAC_ADDRESS = stringPreferencesKey("mac_address")
    private val IP_ADDRESS = stringPreferencesKey("ip_address")

    suspend fun save(context: Context, name: String, mac: String, ip: String) {
        context.dataStore.edit {
            it[DEVICE_NAME] = name
            it[MAC_ADDRESS] = mac
            it[IP_ADDRESS] = ip
        }
    }

    suspend fun load(context: Context): Triple<String?, String?, String?> {
        val prefs = context.dataStore.data.first()
        val name = prefs[DEVICE_NAME]
        val mac = prefs[MAC_ADDRESS]
        val ip = prefs[IP_ADDRESS]
        return Triple(name, mac, ip)
    }
}
