package com.dokiwa.dokidoki.center.ext.rx

import android.app.Activity
import com.dokiwa.dokidoki.center.Log
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.api.exception.SignatureException
import com.dokiwa.dokidoki.center.api.exception.toApiException
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.dialog.RxDialog
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

fun Completable.subscribe(
    context: CompositeDisposableContext?,
    onComplete: Action,
    onError: Consumer<Throwable>
) {
    this.composeIoMain().subscribe(onComplete, onError).bind(context)
}

/**
 * 统一封装线程切换
 * job    ==> io
 * result ==> main
 */
fun Completable.composeIoMain(): Completable {
    return this.compose { upstream ->
        upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}

fun Completable.composeMainMain(): Completable {
    return this.compose { upstream ->
        upstream.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
    }
}

/**
 * 封装网络接口请求订阅方法，如果是签算错误，矫正时间戳，重试一次
 */
fun Completable.subscribeApi(
    context: CompositeDisposableContext? = null,
    onComplete: Action,
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
    }.subscribe(context, onComplete, onError)
}

/**
 * 封装网络接口请求订阅方法，如果是签算错误，矫正时间戳，重试一次
 */
fun Completable.subscribeApi(
    context: CompositeDisposableContext? = null,
    onSuccess: (() -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null
) {
    this.subscribeApi(
        context,
        Action { onSuccess?.invoke() },
        Consumer { onError?.invoke(it) }
    )
}

fun Completable.subscribeApiWithDialog(
    activity: Activity,
    context: CompositeDisposableContext,
    onSuccess: (() -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null
) {
    val observable = RxDialog.progressDialog(activity)
    context.addDispose(observable.subscribe { dialog ->
        this.subscribeApi(
            context,
            Action {
                dialog.cancel()
                onSuccess?.invoke()
            },
            Consumer {
                dialog.cancel()
                onError?.invoke(it)
            }
        )
    })
}