package com.privateparking.wakeonlan

import android.content.Context
import android.content.SharedPreferences

class WakeOnLanPrefs(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("wake_on_lan_prefs", Context.MODE_PRIVATE)

    var ipAddress: String?
        get() = prefs.getString("ip_address", null)
        set(value) = prefs.edit().putString("ip_address", value).apply()

    var macAddress: String?
        get() = prefs.getString("mac_address", null)
        set(value) = prefs.edit().putString("mac_address", value).apply()
}
