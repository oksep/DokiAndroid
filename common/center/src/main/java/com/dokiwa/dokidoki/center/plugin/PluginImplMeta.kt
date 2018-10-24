package com.dokiwa.dokidoki.center.plugin

/**
 * Created by Septenary on 2018/8/11.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class PluginImplMeta(val implClassName: String)
