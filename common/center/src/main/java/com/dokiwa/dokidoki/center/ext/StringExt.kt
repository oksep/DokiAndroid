package com.dokiwa.dokidoki.center.ext

import android.net.Uri
import java.security.MessageDigest

/**
 * Created by Septenary on 2018/11/23.
 */

fun String.sha1(): String {
    return this.hashWithAlgorithm("SHA-1")
}

fun String.md5(): String {
    return this.hashWithAlgorithm("MD5")
}

private fun String.hashWithAlgorithm(algorithm: String): String {
    val digest = MessageDigest.getInstance(algorithm)
    val bytes = digest.digest(this.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { String.format("%02x", it) }
}

fun String.capitalize(): String {
    val first = this[0]
    return if (Character.isUpperCase(first)) {
        this
    } else {
        Character.toUpperCase(first) + this.substring(1)
    }
}

fun String.toRetrofitQueryMap(): Map<String, String?>? {
    return Uri.parse(this).run {
        val map = mutableMapOf<String, String?>()
        queryParameterNames.forEach { key ->
            map[key] = getQueryParameter(key)
        }
        map
    }
}