package com.dokiwa.dokidoki.center.util

/**
 * Created by Septenary on 2018/12/1.
 * ref: https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e
 *
 * ```
 *   class Manager private constructor(context: Context) {
 *      init {
 *          // Init using context argument
 *      }
 *
 *      companion object : SingletonHolder<Manager, Context>(::Manager)
 *   }
 * ```
 */
open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}