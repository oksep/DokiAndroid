package com.dokiwa.dokidoki.center.ext

/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes
import com.dokiwa.dokidoki.center.Log
import com.dokiwa.dokidoki.center.api.exception.ApiException

/**
 * Creates and shows a [Toast] with the given [text]
 *
 * @param duration Toast duration, defaults to [Toast.LENGTH_SHORT]
 */
fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(this, text, duration).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

/**
 * Creates and shows a [Toast] with text from a resource
 *
 * @param resId Resource id of the string resource to use
 * @param duration Toast duration, defaults to [Toast.LENGTH_SHORT]
 */
fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(this, resId, duration).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}

fun Context.toastApiException(throwable: Throwable, @StringRes resId: Int): Toast {
    Log.e("API", "request error", throwable)
    return if (throwable is ApiException && throwable.message != null) {
        toast(throwable.message!!)
    } else {
        toast(resId)
    }
}