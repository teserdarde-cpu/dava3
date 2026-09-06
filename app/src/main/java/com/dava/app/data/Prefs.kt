package com.dava.app.data

import android.content.Context

class Prefs(context: Context) {
    private val p = context.getSharedPreferences("dava2_prefs", Context.MODE_PRIVATE)
    fun hasPin(): Boolean = p.contains("pin")
    fun setPin(pin: String) { p.edit().putString("pin", pin).apply() }
    fun verifyPin(pin: String): Boolean = p.getString("pin", "") == pin
    fun seeded(): Boolean = p.getBoolean("seeded", false)
    fun setSeeded(value: Boolean = true) { p.edit().putBoolean("seeded", value).apply() }
    fun themeMode(): String = p.getString("theme", "dark") ?: "dark"
    fun setThemeMode(mode: String) { p.edit().putString("theme", mode).apply() }
}
