package com.dokiwa.dokidoki.center.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import androidx.annotation.StringRes
import io.reactivex.Observable

class RxDialog {
    companion object {

        fun progressDialog(context: Context): Observable<Dialog> {
            return Observable.create { subscriber ->
                LoadingDialog.create(context, true)
                    .also {
                        subscriber.onNext(it)
                        it.setOnDismissListener {
                            subscriber.onComplete()
                        }
                        it.show()
                    }
            }
        }

        fun dialog(
            context: Context,
            @StringRes titleResId: Int,
            @StringRes messageResId: Int,
            @StringRes positiveButtonMessageResId: Int?,
            @StringRes negativeButtonMessageResId: Int?
        ): Observable<Dialog> = dialog(
            context = context,
            title = context.getString(titleResId) ?: "",
            message = context.getString(messageResId) ?: "",
            positiveButtonMessage = positiveButtonMessageResId?.let { context.getString(positiveButtonMessageResId) },
            negativeButtonMessage = negativeButtonMessageResId?.let { context.getString(negativeButtonMessageResId) }
        )

        fun dialog(
            context: Context,
            title: String,
            message: String,
            positiveButtonMessage: String? = null,
            negativeButtonMessage: String? = null
        ): Observable<Dialog> = Observable.create { subscriber ->
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .apply {
                    if (positiveButtonMessage != null) {
                        setPositiveButton(positiveButtonMessage) { _, _ ->
                            subscriber.onComplete()
                        }
                    }
                }
                .apply {
                    if (negativeButtonMessage != null) {
                        setNegativeButton(negativeButtonMessage) { _, _ ->
                            subscriber.onComplete()
                        }
                    }
                }
                .create()
                .also {
                    subscriber.onNext(it)
                    it.setOnDismissListener {
                        subscriber.onComplete()
                    }
                    it.show()
                }
        }
    }
}