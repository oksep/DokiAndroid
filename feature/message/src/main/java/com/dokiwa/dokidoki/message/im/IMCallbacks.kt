package com.dokiwa.dokidoki.message.im

import com.dokiwa.dokidoki.message.Log
import com.netease.nimlib.sdk.RequestCallback
import io.reactivex.SingleEmitter

/**
 * Created by Septenary on 2019-07-14.
 */

internal class EmitterRequestCallback<T>(
    logMessage: String,
    emitter: SingleEmitter<T>,
    val default: () -> T
) : EmitterAdaptRequestCallback<T, T>(logMessage, emitter, { it ?: default() })

internal open class EmitterAdaptRequestCallback<T, A>(
    private val logMessage: String,
    private val emitter: SingleEmitter<A>,
    private val onAdapt: (T?) -> A
) : RequestCallback<T> {
    override fun onSuccess(t: T) {
        emitter.onSuccess(onAdapt(t))
    }

    override fun onFailed(code: Int) {
        val e = Exception("request callback code -> $code")
        Log.e(IMService.TAG, "$logMessage onFailed", e)
        emitter.onError(e)
    }

    override fun onException(exception: Throwable) {
        Log.e(IMService.TAG, "$logMessage onException", exception)
        emitter.onError(exception)
    }
}

internal class DummyAdaptRequestCallback<T>(private val logMessage: String) : RequestCallback<T> {
    override fun onSuccess(t: T) {
        Log.d(IMService.TAG, "$logMessage success $t")
    }

    override fun onFailed(code: Int) {
        val e = Exception("request callback code -> $code")
        Log.e(IMService.TAG, "$logMessage onFailed", e)
    }

    override fun onException(exception: Throwable) {
        Log.e(IMService.TAG, "$logMessage onException", exception)
    }
}