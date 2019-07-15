package com.dokiwa.dokidoki.message.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.contains
import com.dokiwa.dokidoki.message.Log
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.ui.ext.onceLayoutThen
import kotlinx.android.synthetic.main.merge_input_panel_record.view.*

/**
 * Created by Septenary on 2019-07-15.
 * 输入控件 -> 录音按钮
 */
class InputRecordView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var recordListener: RecordViewListener? = null

    private val countDownInterval = 1_000L
    private val millisInFuture = 60 * countDownInterval

    private var countDownTimer: CountDownTimer? = null

    private var downY = 0f
    private var willCancel = false

    init {
        View.inflate(context, R.layout.merge_input_panel_record, this)
        recordListener = object : RecordViewListener {
            override fun onRecordStart() {
                Log.d("InputRecordView", "Start")
            }

            override fun onRecordCancel() {
                Log.d("InputRecordView", "Cancel")
            }

            override fun onRecordFinish() {
                Log.d("InputRecordView", "Finish")
            }

        }
        initView()
    }

    private fun initView() {
        recordMicView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    onMotionDown(event)
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    onMotionCancel()
                }
                MotionEvent.ACTION_MOVE -> {
                    onMotionMove(event)
                }
            }
            true
        }
        recordCountDownView.alpha = 0f
    }

    private fun onMotionDown(event: MotionEvent) {
        downY = event.y
        showCountDownView()
        startCountDown()
        recordListener?.onRecordStart()
        willCancel = false
        showCancelTip(false)
    }

    private fun onMotionCancel() {
        hideCountDownView()
        stopCountDown()
        if (willCancel) {
            recordListener?.onRecordCancel()
        } else {
            recordListener?.onRecordFinish()
        }
        willCancel = false
        showCancelTip(false)
    }

    private fun onMotionMove(event: MotionEvent) {
        willCancel = if ((downY - event.y) > height / 2) {
            if (!willCancel) showCancelTip(true)
            true
        } else {
            if (willCancel) showCancelTip(false)
            false
        }
    }

    private fun showCancelTip(willCancel: Boolean) {
        val tipView = recordCountDownView.recordCountDownTip
        if (willCancel) {
            tipView.setBackgroundResource(R.drawable.msg_bg_record_count_down_tip)
            tipView.setText(R.string.message_input_panel_record_cancel2)
        } else {
            tipView.setBackgroundResource(R.color.transparent)
            tipView.setText(R.string.message_input_panel_record_cancel1)
        }
    }

    private fun stopCountDown() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun startCountDown() {
        countDownTimer = object : CountDownTimer(millisInFuture, countDownInterval) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val seconds = ((millisInFuture - millisUntilFinished) / countDownInterval) + 1
                recordCountDownView.recordCountDownText.text = "00:${String.format("%02d", seconds)}"
            }

            override fun onFinish() {
                onMotionCancel()
            }
        }
        countDownTimer?.start()
    }

    private fun showCountDownView() {
        (rootView as ViewGroup).overlay.add(recordCountDownView)
        recordCountDownView.translationY = -height * 3f
        recordCountDownView.animate().alpha(1f).setDuration(150).start()
    }

    private fun hideCountDownView() {
        (rootView as ViewGroup).overlay.remove(recordCountDownView)
        if (!contains(recordCountDownView)) {
            addView(recordCountDownView)
        }
        recordCountDownView.animate().alpha(0f).setDuration(150).start()
    }

    interface RecordViewListener {
        fun onRecordStart()
        fun onRecordCancel()
        fun onRecordFinish()
    }
}