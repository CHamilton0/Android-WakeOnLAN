package com.privateparking.wakeonlan

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wake_settings")

object WakePreferences {
    private val MAC_ADDRESS = stringPreferencesKey("mac_address")
    private val IP_ADDRESS = stringPreferencesKey("ip_address")

    suspend fun save(context: Context, mac: String, ip: String) {
        context.dataStore.edit {
            it[MAC_ADDRESS] = mac
            it[IP_ADDRESS] = ip
        }
    }

    suspend fun load(context: Context): Pair<String?, String?> {
        val prefs = context.dataStore.data.first()
        val mac = prefs[MAC_ADDRESS]
        val ip = prefs[IP_ADDRESS]
        return mac to ip
    }
}
