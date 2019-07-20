package com.dokiwa.dokidoki.message.widget

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.dokiwa.dokidoki.center.base.activity.SelectImageDelegate
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.message.Log
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.ui.ext.hideKeyboard
import com.dokiwa.dokidoki.ui.ext.showKeyboard
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
) : LinearLayout(context, attrs, defStyleAttr), SelectImageDelegate {

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
            selectImageByMatisse()
        }
        cameraBtn.setOnClickListener {
            // TODO: 2019-07-18 @Septenary
            context.toast("TODO")
        }
        editText.addTextChangedListener(object : SimpleTextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendBtn.isEnabled = s?.length ?: 0 > 0
            }
        })
        sendBtn.setOnClickListener {
            onRequestSendTxt?.invoke(editText.text.toString())
        }
    }

    private var onRequestSendTxt: ((String) -> Unit)? = null
    private var onRequestSendImage: ((List<Uri>) -> Unit)? = null
    private var onRequestSendAudio: ((File, Long) -> Unit)? = null

    fun setInputPanelCallback(
        onRequestSendTxt: (String) -> Unit,
        onRequestSendImage: (List<Uri>) -> Unit,
        onRequestSendAudio: (File, Long) -> Unit
    ) {
        this.onRequestSendTxt = onRequestSendTxt
        this.onRequestSendImage = onRequestSendImage
        this.onRequestSendAudio = onRequestSendAudio
    }

    private fun hideAllSubPanels() {
        subPanelRecordView.visibility = View.GONE
        subPanelEmoticon.visibility = View.GONE
    }

    private fun maybeAnim() {
        // TransitionManager.beginDelayedTransition(rootView as ViewGroup, ChangeBounds().setStartDelay(0).setDuration(80))
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