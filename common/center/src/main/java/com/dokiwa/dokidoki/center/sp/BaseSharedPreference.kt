package com.dokiwa.dokidoki.center.sp

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin

abstract class BaseSharedPreferences(private val moduleName: String) {

    private var mode = Context.MODE_PRIVATE

    abstract val context: Context

    val all: Map<String, *>
        get() {
            return context.getSharedPreferences(moduleName, mode).all
        }

    open val isAsUserData: Boolean
        get() = false

    fun setMode(mode: Int) {
        this.mode = mode
    }

    fun save(key: String, value: String): Boolean {
        val settings = context.getSharedPreferences(moduleName, mode)
        val editor = settings.edit()
        editor.putString(wrapKey(key), value)
        return editor.commit()
    }

    fun asyncSave(key: String, value: String) {
        val settings = context.getSharedPreferences(moduleName, mode)
        val editor = settings.edit()
        editor.putString(wrapKey(key), value)
        editor.apply()
    }

    fun asyncSave(key: String, value: Boolean) {
        val settings = context.getSharedPreferences(moduleName, mode)
        val editor = settings.edit()
        editor.putBoolean(wrapKey(key), value)
        editor.apply()
    }

    fun save(key: String, value: Float): Boolean {
        val settings = context.getSharedPreferences(moduleName, mode)
        val editor = settings.edit()
        editor.putFloat(wrapKey(key), value)
        return editor.commit()
    }

    fun getString(key: String): String {
        val settings = context.getSharedPreferences(moduleName, mode)
        return settings.getString(wrapKey(key), "")
    }

    fun getString(key: String, defValue: String): String? {
        val settings = context.getSharedPreferences(moduleName, mode)
        return settings.getString(wrapKey(key), defValue)
    }

    fun save(key: String, value: Boolean): Boolean {
        val settings = context.getSharedPreferences(moduleName, mode)
        val editor = settings.edit()
        editor.putBoolean(wrapKey(key), value)
        return editor.commit()
    }

    fun save(key: String, value: Int): Boolean {
        val settings = context.getSharedPreferences(moduleName, mode)
        val editor = settings.edit()
        editor.putInt(wrapKey(key), value)
        return editor.commit()
    }

    fun save(key: String, value: Long): Boolean {
        val settings = context.getSharedPreferences(moduleName, mode)
        val editor = settings.edit()
        editor.putLong(wrapKey(key), value)
        return editor.commit()
    }

    fun getBoolean(key: String): Boolean {
        return getBoolean(wrapKey(key), false)

    }

    fun getBoolean(key: String, defaultVal: Boolean): Boolean {
        val settings = context.getSharedPreferences(moduleName, mode)
        return settings.getBoolean(wrapKey(key), defaultVal)

    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        val settings = context.getSharedPreferences(moduleName, mode)

        return settings.getInt(wrapKey(key), defaultValue)
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        val settings = context.getSharedPreferences(moduleName, mode)

        return settings.getFloat(wrapKey(key), defaultValue)
    }

    fun getLong(key: String, defaultValue: Long): Long {
        val settings = context.getSharedPreferences(moduleName, mode)

        return settings.getLong(wrapKey(key), defaultValue)
    }

    fun remove(key: String) {
        val settings = context.getSharedPreferences(moduleName, mode)
        val editor = settings.edit()
        editor.remove(key)
        editor.commit()
    }

    fun saveSet(key: String, value: Set<String>): Boolean {
        val settings = context.getSharedPreferences(moduleName, mode)
        val editor = settings.edit()
        editor.putStringSet(wrapKey(key), value)
        return editor.commit()
    }

    fun getSet(key: String): Set<String>? {
        val pref = context.getSharedPreferences(moduleName, mode)
        return pref.getStringSet(wrapKey(key), null)
    }

    fun clearAll() {
        val pref = context.getSharedPreferences(moduleName, mode)
        pref.edit().clear().apply()
    }

    open fun wrapKey(key: String): String {
        return if (isAsUserData) {
            "$key${FeaturePlugin.get(ILoginPlugin::class.java).getUser()?.id ?: ""}"
        } else {
            key
        }
    }
}
