package com.dokiwa.dokidoki.center

import com.dokiwa.dokidoki.center.ext.sha1
import org.junit.Assert
import org.junit.Test
import java.net.URLEncoder

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ApiTest {
    @Test
    fun sign_is_correct() {
        val ts = 1543241108
        val nonce = "935ca"
        val ak = "123456"
        val secret = "654321"

        val map = mapOf(
            "_ts" to ts.toString(),
            "_nonce" to nonce,
            "_ak" to ak
        )

        val unSign = map.keys
            .toList()
            .sorted()
            .joinToString("&") {
                val value = map[it]
                val encodeValue = URLEncoder.encode(value, "UTF-8")
                "$it=$encodeValue"
            } + secret

        val sign = unSign.sha1()

        Assert.assertEquals("fd1f3cdf3e05eda52aa30e813c7d437505670ce0", sign)
    }
}
