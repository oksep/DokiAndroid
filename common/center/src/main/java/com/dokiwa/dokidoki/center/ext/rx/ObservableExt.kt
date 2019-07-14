package com.dokiwa.dokidoki.center.ext.rx

import com.dokiwa.dokidoki.center.Log
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * 扩展带 dispose 上下文的订阅方法
 */
fun <T> Observable<T>.subscribe(
    context: CompositeDisposableContext?,
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
    context: CompositeDisposableContext?,
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
 */
fun <T> Observable<T>.composeIoMain(): Observable<T> {
    return this.compose { upstream ->
        upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Observable<T>.mainMain(): Observable<T> {
    return this.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.ioMain(): Observable<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.mainIo(): Observable<T> {
    return this.observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.subscribeLog(tag: String, message: String = "subscribeLog"): Disposable {
    return this.subscribe({
        Log.d(tag, "$message $it")
    }, {
        Log.e(tag, "$message error", it)
    })
}