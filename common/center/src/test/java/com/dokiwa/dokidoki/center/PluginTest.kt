package com.dokiwa.dokidoki.center

import com.dokiwa.dokidoki.center.plugin.FeaturePlugin
import org.junit.Assert
import org.junit.Test


class PluginTest {

    fun <T> safeGet(apiClass: Class<T>): T {
        return JavaShadowProxy.newInstance(apiClass)
    }

    @Test
    fun testPlugin() {
        Assert.assertEquals(
            safeGet(HelloApi::class.java).getIntValue(),
            FeaturePlugin.get(HelloApi::class.java).getIntValue()
        )
    }
}

interface HelloApi : FeaturePlugin {
    fun getIntValue(): Int
}
