package com.dokiwa.dokidoki.center.base

import io.reactivex.disposables.Disposable

/**
 * 注销订阅上下文
 */
interface CompositeDisposableContext {
    fun addDispose(dispose: Disposable)
}