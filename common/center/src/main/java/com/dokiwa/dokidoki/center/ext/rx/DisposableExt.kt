package com.dokiwa.dokidoki.center.ext.rx

import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import io.reactivex.disposables.Disposable

/**
 * 绑定注销订阅上下文
 */
fun Disposable.bind(context: CompositeDisposableContext?) {
    context?.addDispose(this)
}
