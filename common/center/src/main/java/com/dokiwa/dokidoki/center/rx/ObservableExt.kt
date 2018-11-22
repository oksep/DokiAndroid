package com.dokiwa.dokidoki.center.rx

import io.reactivex.Observable

/**
 * Created by Septenary on 2018/11/22.
 */

fun <T> Observable<T>.subscribe(
    context: CompositeDisposableContext,
    onNext: (t: T) -> Unit
) {
    this.subscribe { t -> onNext.invoke(t) }.bind(context)
}

fun <T> Observable<T>.subscribe(
    context: CompositeDisposableContext,
    onNext: (t: T) -> Unit,
    onError: (e: Throwable) -> Unit
) {
    this.subscribe({ t -> onNext.invoke(t) }, { e -> onError.invoke(e) }).bind(context)
}

fun <T> Observable<T>.subscribe(
    context: CompositeDisposableContext,
    onNext: (t: T) -> Unit,
    onError: (e: Throwable) -> Unit,
    onComplete: () -> Unit
) {
    this.subscribe({ t -> onNext.invoke(t) }, { e -> onError.invoke(e) }, { onComplete.invoke() }).bind(context)
}
