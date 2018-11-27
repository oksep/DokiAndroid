package com.dokiwa.dokidoki.center.rx

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 扩展带 dispose 上下文的订阅方法
 */
fun <T> Observable<T>.subscribe(
    context: CompositeDisposableContext,
    onNext: ((t: T) -> Unit)? = null,
    onError: ((e: Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
) {
    this.composeIoMain()
        .subscribe(
            { t -> onNext?.invoke(t) },
            { e -> onError?.invoke(e) },
            { onComplete?.invoke() }
        )
        .bind(context)
}

/**
 * 扩展带 dispose 上下文的订阅方法，同时允许重试一次
 */
fun <T> Observable<T>.retryOnShotSubscribe(
    context: CompositeDisposableContext,
    onNext: ((t: T) -> Unit)? = null,
    onError: ((e: Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
) {
    var retried = false
    this.retryWhen {
        it.flatMap { error ->
            if (retried) {
                Observable.error(error)
            } else {
                Observable.just(true)
            }.also {
                retried = true
            }
        }
    }.composeIoMain().subscribe(
        { t -> onNext?.invoke(t) },
        { e -> onError?.invoke(e) },
        { onComplete?.invoke() }
    ).bind(context)
}

/**
 * 统一封装线程切换
 * job    ==> io
 * result ==> main
 * TODO: scheduler (io -> network)
 */
fun <T> Observable<T>.composeIoMain(): Observable<T> {
    return this.compose { upstream ->
        upstream.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }
}