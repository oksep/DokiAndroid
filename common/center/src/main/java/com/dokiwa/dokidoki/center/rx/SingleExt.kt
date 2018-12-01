package com.dokiwa.dokidoki.center.rx

import com.dokiwa.dokidoki.center.Log
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.api.exception.SignatureException
import com.dokiwa.dokidoki.center.api.exception.toApiException
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

/**
 * 扩展带 dispose 上下文的订阅方法
 */
fun <T> Single<T>.subscribe(
    context: CompositeDisposableContext?,
    onSuccess: Consumer<T>? = null,
    onError: Consumer<Throwable>? = null
) {
    this.composeIoMain().subscribe(onSuccess, onError).bind(context)
}

/**
 * 扩展带 dispose 上下文的订阅方法，同时允许重试一次
 */
fun <T> Single<T>.retryOnShotSubscribe(
    context: CompositeDisposableContext?,
    onSuccess: Consumer<T>? = null,
    onError: Consumer<Throwable>? = null
) {
    var retried = false
    this.retryWhen { errors ->
        errors.flatMap { e ->
            if (retried) {
                Flowable.error<Throwable>(e)
            } else {
                Flowable.just(Unit)
            }.also {
                retried = true
            }
        }
    }.subscribe(context, onSuccess, onError)
}

/**
 * 统一封装线程切换
 * job    ==> io
 * result ==> main
 */
fun <T> Single<T>.composeIoMain(): Single<T> {
    return this.compose { upstream ->
        upstream.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
    }
}

/**
 * 封装网络接口请求订阅方法，如果是签算错误，矫正时间戳，重试一次
 */
fun <T> Single<T>.subscribeApi(
    context: CompositeDisposableContext? = null,
    onSuccess: Consumer<T>? = null,
    onError: Consumer<Throwable>? = null
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