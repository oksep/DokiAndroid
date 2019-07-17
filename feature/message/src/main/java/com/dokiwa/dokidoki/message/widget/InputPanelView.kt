package com.dokiwa.dokidoki.message.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.ui.ext.hideKeyboard
import com.dokiwa.dokidoki.ui.ext.showKeyboard
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import kotlinx.android.synthetic.main.include_input_panel_edit.view.*
import kotlinx.android.synthetic.main.include_input_panel_options.view.*
import kotlinx.android.synthetic.main.merge_input_panel.view.*

/**
 * Created by Septenary on 2019-07-15.
 * 输入面板
 */
class InputPanelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var keyboardHeight = 0

    init {
        orientation = VERTICAL
        setBackgroundColor(Color.WHITE)
        View.inflate(context, R.layout.merge_input_panel, this)
        micBtn.setOnClickListener {
            showSubPanelRecord()
        }
        emojiBtn.setOnClickListener {
            showSubPanelEmoticon()
        }
        picBtn.setOnClickListener {
            showPicGalleryPicker()
        }
        cameraBtn.setOnClickListener {
            showPicCameraPicker()
        }
        editText.addTextChangedListener(object : SimpleTextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendBtn.isEnabled = s?.length ?: 0 > 0
            }
        })
        sendBtn.setOnClickListener {
            onSendMessageClick?.invoke(editText.text.toString())
        }
    }

    private var onSendMessageClick: ((String) -> Unit)? = null

    fun setInputPannelCallback(onSendMessageClick: (String) -> Unit) {
        this.onSendMessageClick = onSendMessageClick
    }

    private fun hideAllSubPanels() {
        subPanelRecordView.visibility = View.GONE
        subPanelEmoticon.visibility = View.GONE
    }

    private fun maybeAnim() {
        TransitionManager.beginDelayedTransition(rootView as ViewGroup, ChangeBounds().setStartDelay(0).setDuration(80))
    }

    private fun showPicGalleryPicker() {
        hideAllSubPanels()
        emojiBtn.isSelected = false
        micBtn.isSelected = false
        editText.clearFocus()
        editText.hideKeyboard()
        context.toast("TODO")
        maybeAnim()
    }

    private fun showPicCameraPicker() {
        hideAllSubPanels()
        emojiBtn.isSelected = false
        micBtn.isSelected = false
        editText.clearFocus()
        editText.hideKeyboard()
        context.toast("TODO")
        maybeAnim()
    }

    private fun showSubPanelRecord() {
        hideAllSubPanels()
        emojiBtn.isSelected = false
        if (micBtn.isSelected) {
            subPanelRecordView.visibility = View.GONE
            micBtn.isSelected = false
            editText.showKeyboard()
            ensureKeyboardSpace(keyboardHeight)
        } else {
            subPanelRecordView.visibility = View.VISIBLE
            micBtn.isSelected = true
            ensureKeyboardSpace(0)
            editText.clearFocus()
            editText.hideKeyboard()
        }
        maybeAnim()
    }

    private fun showSubPanelEmoticon() {
        hideAllSubPanels()
        micBtn.isSelected = false
        if (emojiBtn.isSelected) {
            subPanelEmoticon.visibility = View.GONE
            emojiBtn.isSelected = false
            editText.showKeyboard()
            ensureKeyboardSpace(keyboardHeight)
        } else {
            subPanelEmoticon.visibility = View.VISIBLE
            emojiBtn.isSelected = true
            ensureKeyboardSpace(0)
            editText.clearFocus()
            editText.hideKeyboard()
        }
        maybeAnim()
    }

    fun ensureKeyboardSpace(height: Int) {
        val params = subPanelSpace.layoutParams
        if (height > 0) {
            hideAllSubPanels()
            emojiBtn.isSelected = false
            micBtn.isSelected = false
            keyboardHeight = height
        }
        if (params.height != height) {
            params.height = height
            subPanelSpace.layoutParams = params
            maybeAnim()
        }
    }

    fun clearText() {
        editText.text = null
    }
}