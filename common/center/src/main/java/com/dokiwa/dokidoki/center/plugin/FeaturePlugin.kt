package com.dokiwa.dokidoki.center.plugin

import android.content.Context
import android.util.Log
import com.dokiwa.dokidoki.center.plugin.admin.IAdminPlugin
import com.dokiwa.dokidoki.center.plugin.feed.IFeedPlugin
import com.dokiwa.dokidoki.center.plugin.home.IHomePlugin
import com.dokiwa.dokidoki.center.plugin.location.ILocationPlugin
import com.dokiwa.dokidoki.center.plugin.login.ILoginPlugin
import com.dokiwa.dokidoki.center.plugin.message.IMessagePlugin
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.center.plugin.relationship.IRelationshipPlugin
import com.dokiwa.dokidoki.center.plugin.timeline.ITimelinePlugin
import com.dokiwa.dokidoki.center.plugin.update.IUpdatePlugin
import com.dokiwa.dokidoki.center.plugin.web.IWebPlugin
import com.dokiwa.dokidoki.center.util.AppUtil
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Created by Septenary on 2018/8/11.
 */

const val TAG = "FeaturePlugin"

interface FeaturePlugin {
    fun onInit(context: Context) {
    }

    companion object {
        val map = HashMap<Class<*>, FeaturePlugin?>()

        private fun <T : FeaturePlugin> inMap(context: Context, clazz: Class<T>) {
            val processName = AppUtil.getProcessName(context)
            val implClassName = clazz.getAnnotation(PluginImplMeta::class.java).implClassName
            try {
                map[clazz] = (Class.forName(implClassName).newInstance() as? FeaturePlugin)?.apply {
                    onInit(context)
                }
                Log.d(TAG, "$processName plugin loaded -> [$clazz].")
            } catch (ex: Throwable) {
                ex.printStackTrace()
                Log.e(TAG, "$processName plugin not found -> [$clazz].")
            }
        }

        fun initMainProcessPlugin(context: Context) {
            inMap(context, IAdminPlugin::class.java)
            inMap(context, ILoginPlugin::class.java)
            inMap(context, IHomePlugin::class.java)
            inMap(context, IWebPlugin::class.java)
            inMap(context, IProfilePlugin::class.java)
            inMap(context, ITimelinePlugin::class.java)
            inMap(context, IFeedPlugin::class.java)
            inMap(context, IUpdatePlugin::class.java)
            inMap(context, ILocationPlugin::class.java)
            inMap(context, IRelationshipPlugin::class.java)
        }

        fun initMultiProcessPlugin(context: Context) {
            inMap(context, IMessagePlugin::class.java)
        }

        internal fun <T> get(clazz: Class<T>): T {
            return map[clazz] as? T ?: ShadowPlugin.newInstance(clazz)
        }
    }
}

internal class ShadowPlugin : InvocationHandler {

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