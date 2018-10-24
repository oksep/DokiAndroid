package com.dokiwa.dokidoki.center.plugin

import android.content.Context
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Created by Septenary on 2018/8/11.
 */
interface FeaturePlugin {
    fun onInit(context: Context) {
    }

    companion object {
        val map = HashMap<Class<*>, FeaturePlugin?>()

        fun init(context: Context) {
            fun <T : FeaturePlugin> inMap(clazz: Class<T>) {
                val implClassName = clazz.getAnnotation(PluginImplMeta::class.java).implClassName
                try {
                    map[clazz] = (Class.forName(implClassName)?.newInstance() as? FeaturePlugin)?.apply {
                        onInit(context)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            inMap(IAdminPlugin::class.java)
            inMap(ILoginPlugin::class.java)
        }

        inline fun <reified T : FeaturePlugin> get(clazz: Class<T>): T {
            return map[clazz] as? T ?: ShadowPlugin.newInstance(clazz)
        }
    }
}

class ShadowPlugin : InvocationHandler {

    companion object {
        fun <T> newInstance(clazz: Class<T>): T {
            return Proxy.newProxyInstance(clazz.classLoader, arrayOf<Class<*>>(clazz), ShadowPlugin()) as T
        }
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        return when (method?.returnType) {
            Boolean::class.javaPrimitiveType -> false
            Int::class.javaPrimitiveType -> 0
            Short::class.javaPrimitiveType -> 0.toShort()
            Char::class.javaPrimitiveType -> 0.toChar()
            Byte::class.javaPrimitiveType -> 0.toByte()
            Long::class.javaPrimitiveType -> 0L
            Float::class.javaPrimitiveType -> 0f
            Double::class.javaPrimitiveType -> 0.0
            String::class.java -> ""
            else -> Unit
        }
    }

}