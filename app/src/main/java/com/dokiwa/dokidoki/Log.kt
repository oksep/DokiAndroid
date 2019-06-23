package com.dokiwa.dokidoki

internal object Log {

    private const val MODULE = "App"

    fun v(tag: String, message: String) {
        android.util.Log.v("$MODULE.$tag", message)
    }

    fun d(tag: String, message: String) {
        android.util.Log.d("$MODULE.$tag", message)
    }

    fun i(tag: String, message: String) {
        android.util.Log.i("$MODULE.$tag", message)
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        android.util.Log.w("$MODULE.$tag", message, throwable)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        android.util.Log.e("$MODULE.$tag", message, throwable)
    }
}
