package com.dokiwa.dokidoki.center.ext.rx

import android.app.Activity
import com.dokiwa.dokidoki.center.Log
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.api.exception.SignatureException
import com.dokiwa.dokidoki.center.api.exception.toApiException
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.dialog.RxDialog
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

/**
 * 扩展带 dispose 上下文的订阅方法
 */
fun <T> Single<T>.subscribe(
    context: CompositeDisposableContext?,
    onSuccess: Consumer<T>,
    onError: Consumer<Throwable>
) {
    this.composeIoMain().subscribe(onSuccess, onError).bind(context)
}

/**
 * 扩展带 dispose 上下文的订阅方法，同时允许重试一次
 */
fun <T> Single<T>.retryOnShotSubscribe(
    context: CompositeDisposableContext?,
    onSuccess: Consumer<T>,
    onError: Consumer<Throwable>
) {
    this.retry().subscribe(context, onSuccess, onError)
}

/**
 * 统一封装线程切换
 * job    ==> io
 * result ==> main
 */
fun <T> Single<T>.composeIoMain(): Single<T> {
    return this.compose { upstream ->
        upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun <T> Single<T>.composeMainMain(): Single<T> {
    return this.compose { upstream ->
        upstream.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
    }
}

/**
 * 封装网络接口请求订阅方法，如果是签算错误，矫正时间戳，重试一次
 */
fun <T> Single<T>.subscribeApi(
    context: CompositeDisposableContext? = null,
    onSuccess: Consumer<T>,
    onError: Consumer<Throwable>
) {
    var retried = false
    this.retryWhen { errors ->
        errors.flatMap { e ->
            if (e is HttpException) {
                val apiException = e.toApiException()
                if (apiException is SignatureException && !retried) {
                    Api.correctTimestamp(apiException.timeDif)
                    Log.e(Api.TAG, "Single.subscribeApi: ", apiException)
                    Flowable.just(Unit)
                } else {
                    Flowable.error(apiException)
                }
            } else {
                Flowable.error(e)
            }.also {
                retried = true
            }
        }
    }.subscribe(context, onSuccess, onError)
}

/**
 * 封装网络接口请求订阅方法，如果是签算错误，矫正时间戳，重试一次
 */
fun <T> Single<T>.subscribeApi(
    context: CompositeDisposableContext? = null,
    onSuccess: ((T) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null
) {
    this.subscribeApi(
        context,
        Consumer { onSuccess?.invoke(it) },
        Consumer { onError?.invoke(it) }
    )
}

fun <T> Single<T>.subscribeApiWithDialog(
    activity: Activity,
    context: CompositeDisposableContext,
    onSuccess: ((T) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null
) {
    val observable = RxDialog.progressDialog(activity)
    context.addDispose(observable.subscribe { dialog ->
        this.subscribeApi(
            context,
            Consumer {
                dialog.cancel()
                onSuccess?.invoke(it)
            },
            Consumer {
                dialog.cancel()
                onError?.invoke(it)
            }
        )
    })
}

fun <T> Single<T>.subscribeLog(tag: String, message: String = "subscribeLog"): Disposable {
    return this.subscribe({
        Log.d(tag, "$message $it")
    }, {
        Log.e(tag, "$message error", it)
    })
}