package com.dokiwa.dokidoki.ui.util

import android.text.Editable
import android.text.TextWatcher

/**
 * Created by Septenary on 2019-06-15.
 */
interface SimpleTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}