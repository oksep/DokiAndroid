package com.dokiwa.dokidoki.center.rx

import io.reactivex.disposables.Disposable

/**
 * Created by Septenary on 2018/11/22.
 */
fun Disposable.bind(context: CompositeDisposableContext) {
    context.addDispose(this)
}
