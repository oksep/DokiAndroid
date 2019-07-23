package com.dokiwa.dokidoki.message.widget

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.dokiwa.dokidoki.message.R
import com.dokiwa.dokidoki.ui.util.ViewUtil
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment
import kotlin.math.ceil
import kotlin.math.max

/**
 * Created by Septenary on 2019-07-20.
 */

open class AttachmentAudioView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private val interpolator = DecelerateInterpolator(3f)
    }

    private val safeAreaWidth = ViewUtil.getScreenWidth() - ViewUtil.dp2px(context, 164f)
    private var widthPercent = 1f

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
        val seconds = ceil(attachment.duration / 1000.0).toInt()
        findViewById<TextView>(R.id.audioDuration)?.text = context.getString(
            R.string.message_chat_room_audio_duration,
            seconds
        )
        setOnClickListener {
            onAudioAttachmentClick.onClick(it)
        }

        widthPercent = interpolator.getInterpolation(seconds / 60f)
        // (1.0f - (1.0f - percent).toDouble().pow((2 * factor).toDouble())).toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val exceptWidth = max((safeAreaWidth * widthPercent).toInt(), suggestedMinimumWidth)
        val spec = MeasureSpec.makeMeasureSpec(exceptWidth, MeasureSpec.EXACTLY)
        super.onMeasure(spec, heightMeasureSpec)
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