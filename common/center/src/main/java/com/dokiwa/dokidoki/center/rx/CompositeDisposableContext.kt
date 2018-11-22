package com.dokiwa.dokidoki.center.rx

import io.reactivex.disposables.Disposable

/**
 * Created by Septenary on 2018/11/22.
 */
interface CompositeDisposableContext {
    fun addDispose(dispose: Disposable)
}