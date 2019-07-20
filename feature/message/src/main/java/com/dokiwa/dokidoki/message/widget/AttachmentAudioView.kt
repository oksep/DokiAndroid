package com.dokiwa.dokidoki.message.widget

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.dokiwa.dokidoki.message.R
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment

/**
 * Created by Septenary on 2019-07-20.
 */

open class AttachmentAudioView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    fun play() {
        val icon = findViewById<ImageView>(R.id.audioIcon)
        icon.setImageResource(R.drawable.msg_ic_audio_playing)
        (icon.drawable as? AnimationDrawable)?.start()
    }

    fun stop() {
        val icon = findViewById<ImageView>(R.id.audioIcon)
        icon.setImageResource(R.drawable.msg_ic_audio_play_0)
    }

    fun setAttachment(attachment: AudioAttachment, onAudioAttachmentClick: OnClickListener) {
        val seconds = Math.ceil(attachment.duration / 1000.0).toInt()
        findViewById<TextView>(R.id.audioDuration)?.text = context.getString(
            R.string.message_chat_room_audio_duration,
            seconds
        )
        (layoutParams as ConstraintLayout.LayoutParams).apply {
            matchConstraintPercentWidth = seconds / 60f
        }
        setOnClickListener {
            onAudioAttachmentClick.onClick(it)
        }
    }
}

class AttachmentAudioLeftView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AttachmentAudioView(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.merge_audio_view_left, this)
        setBackgroundResource(R.drawable.msg_selector_bubble_left)
    }
}

class AttachmentAudioRightView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AttachmentAudioView(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.merge_audio_view_right, this)
        setBackgroundResource(R.drawable.msg_selector_bubble_right)
    }
}