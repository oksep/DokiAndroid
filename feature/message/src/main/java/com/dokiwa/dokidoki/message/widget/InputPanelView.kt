package com.dokiwa.dokidoki.message.widget

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.dokiwa.dokidoki.center.base.activity.SelectImageDelegate
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.message.Log
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.message.widget.emoction.MoonUtil
import com.dokiwa.dokidoki.message.widget.emoction.StringUtil
import com.dokiwa.dokidoki.ui.ext.hideKeyboard
import com.dokiwa.dokidoki.ui.ext.showKeyboard
import com.dokiwa.dokidoki.ui.util.KeyboardHeightObserver
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import com.netease.nimlib.sdk.media.record.AudioRecorder
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback
import com.netease.nimlib.sdk.media.record.RecordType
import kotlinx.android.synthetic.main.include_input_panel_edit.view.*
import kotlinx.android.synthetic.main.include_input_panel_options.view.*
import kotlinx.android.synthetic.main.merge_input_panel.view.*
import java.io.File

/**
 * Created by Septenary on 2019-07-15.
 * 输入面板
 */
private const val TAG = "InputPanelView"

class InputPanelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), SelectImageDelegate, KeyboardHeightObserver {

    private var keyboardHeight = 0

    init {
        orientation = VERTICAL
        setBackgroundColor(Color.WHITE)
        View.inflate(context, R.layout.merge_input_panel, this)
        micBtn.setOnClickListener {
            toggleSubPanel(micBtn, subPanelRecordView)
        }
        emojiBtn.setOnClickListener {
            toggleSubPanel(emojiBtn, subPanelEmoticon)
        }
        picBtn.setOnClickListener {
            toggleSubPanel(picBtn, null)
            selectImageByMatisse()
        }
        cameraBtn.setOnClickListener {
            toggleSubPanel(cameraBtn, null)
            selectImageByMatisse()
        }
        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        editText.addTextChangedListener(object : SimpleTextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendBtn.isEnabled = s?.length ?: 0 > 0
            }
        })
        sendBtn.setOnClickListener {
            onRequestSendTxt?.invoke(editText.text.toString())
        }
    }

    init {
        subPanelEmoticon.setUp({
            if (it == "/DEL") {
                editText.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
            } else {
                val editable = editText.text
                var start = editText.selectionStart
                var end = editText.selectionEnd
                start = if (start < 0) 0 else start
                end = if (start < 0) 0 else end
                editable.replace(start, end, it)
            }
        }, {
            onRequestSendSticker?.invoke(it)
        })
        editText.addTextChangedListener(object : TextWatcher {
            private var start: Int = 0
            private var count: Int = 0

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                this.start = start
                this.count = count
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                MoonUtil.replaceEmoticons(context, s, start, count)
                var editEnd = editText.selectionEnd
                editText.removeTextChangedListener(this)
                while (StringUtil.counterChars(s.toString()) > 300 && editEnd > 0) {
                    s.delete(editEnd - 1, editEnd)
                    editEnd--
                }
                editText.setSelection(editEnd)
                editText.addTextChangedListener(this)
            }
        })
    }

    private var onRequestSendTxt: ((String) -> Unit)? = null
    private var onRequestSendImage: ((List<Uri>) -> Unit)? = null
    private var onRequestSendSticker: ((String) -> Unit)? = null
    private var onRequestSendAudio: ((File, Long) -> Unit)? = null

    fun setInputPanelCallback(
        onRequestSendTxt: (String) -> Unit,
        onRequestSendImage: (List<Uri>) -> Unit,
        onRequestSendAudio: (File, Long) -> Unit,
        onRequestSendSticker: (String) -> Unit
    ) {
        this.onRequestSendTxt = onRequestSendTxt
        this.onRequestSendImage = onRequestSendImage
        this.onRequestSendAudio = onRequestSendAudio
        this.onRequestSendSticker = onRequestSendSticker
    }

    private fun unSelectAll() {
        micBtn.isSelected = false
        picBtn.isSelected = false
        cameraBtn.isSelected = false
        emojiBtn.isSelected = false

        subPanelContainer.children.forEach {
            it.visibility = View.GONE
        }
    }

    private fun toggleSubPanel(btn: View, subPanel: View?) {
        val keyboardResolved: Boolean
        if (btn.isSelected) {
            keyboardResolved = changeKeyboardSpace(keyboardHeight)
            editText.requestFocus()
            editText.showKeyboard()
        } else {
            unSelectAll()
            keyboardResolved = changeKeyboardSpace(0)
            subPanel?.visibility = View.VISIBLE
            btn.isSelected = true
            editText.clearFocus()
            editText.hideKeyboard()
        }
        if (!keyboardResolved) {
            animTransition()
        }
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        changeKeyboardSpace(height)
    }

    private fun changeKeyboardSpace(height: Int): Boolean {
        val p = layoutParams as LayoutParams
        if (p.bottomMargin == height) return false
        Log.d(TAG, "changeKeyboardHeight -> $height")
        if (height != 0) {
            unSelectAll()
            keyboardHeight = height
        }
        p.bottomMargin = height
        animTransition()
        requestLayout()
        return true
    }

    private fun animTransition() {
        TransitionManager.beginDelayedTransition(parent as ViewGroup, ChangeBounds().setDuration(85))
    }

    fun clearText() {
        editText.text = null
    }

    //region 图片选择
    override var selectImageDelegate = context as? SelectImageDelegate

    init {
        selectImageDelegate?.selectImageDelegate = this
    }

    override fun selectImageByMatisse(max: Int) {
        selectImageDelegate?.selectImageByMatisse(max)
    }

    override fun selectImageByCamera() {
        selectImageDelegate?.selectImageByCamera()
    }

    override fun onSelectImageFromMatisse(list: List<Uri>) {
        onRequestSendImage?.invoke(list)
    }

    override fun onSelectImageFromCamera(uri: Uri) {
        onRequestSendImage?.invoke(listOf(uri))
    }
    //endregion

    //region recorder
    private var recorder: AudioRecorder? = null

    init {
        subPanelRecordView.recordControlListener = object : InputRecordView.RecordViewListener {
            override fun onRequestStartRecorder() {
                Log.d(TAG, "onRequestStartRecorder")
                startRecord()
            }

            override fun onRequestCancelRecorder() {
                Log.d(TAG, "onRequestCancelRecorder")
                cancelRecord()
            }

            override fun onRequestFinishRecorder() {
                Log.d(TAG, "onRequestFinishRecorder")
                stopRecord()
            }

        }
    }

    fun startRecord() {
        val callback = object : IAudioRecordCallback {
            override fun onRecordSuccess(audioFile: File, audioLength: Long, recordType: RecordType) {
                Log.d(TAG, "IAudioRecordCallback onRecordSuccess $audioFile, $audioLength, $recordType")
                onRequestSendAudio?.invoke(audioFile, audioLength)
                subPanelRecordView.stopCountDown()
            }

            override fun onRecordReady() {
                Log.d(TAG, "IAudioRecordCallback onRecordReady")
            }

            override fun onRecordStart(audioFile: File?, recordType: RecordType?) {
                Log.d(TAG, "IAudioRecordCallback onRecordStart")
                subPanelRecordView.startCountDown()
            }

            override fun onRecordCancel() {
                Log.d(TAG, "IAudioRecordCallback onRecordCancel")
                subPanelRecordView.stopCountDown()
                releaseRecord()
            }

            override fun onRecordFail() {
                Log.d(TAG, "IAudioRecordCallback onRecordFail")
                context.toast(R.string.message_input_panel_record_failed)
                subPanelRecordView.stopCountDown()
                releaseRecord()
            }

            override fun onRecordReachedMaxTime(maxTime: Int) {
                Log.d(TAG, "IAudioRecordCallback onRecordReady")
                recorder?.handleEndRecord(true, maxTime)
                subPanelRecordView.stopCountDown()
                releaseRecord()
            }
        }

        recorder = AudioRecorder(context, RecordType.AAC, 60, callback)
        recorder?.startRecord()
    }

    fun stopRecord() {
        recorder?.completeRecord(false)
    }

    fun cancelRecord() {
        recorder?.completeRecord(true)
    }

    fun releaseRecord() {
        recorder?.destroyAudioRecorder()
        recorder = null
    }
    //endregion
}