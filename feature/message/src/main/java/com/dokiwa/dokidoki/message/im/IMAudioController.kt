package com.dokiwa.dokidoki.message.im

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.dokiwa.dokidoki.center.AppCenter
import com.dokiwa.dokidoki.message.Log
import com.dokiwa.dokidoki.message.R
import com.netease.nimlib.sdk.NIMSDK
import com.netease.nimlib.sdk.media.player.AudioPlayer
import com.netease.nimlib.sdk.media.player.OnPlayListener
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import java.io.File

/**
 * Created by Septenary on 2019-07-20.
 */
object IMAudioController {

    private const val TAG = "IMAudioController"

    private var audioPlayer: AudioPlayer? = null

    private var audioStreamType = AudioManager.STREAM_MUSIC

    private var onAudioStateChange: ((IMSessionMessage) -> Unit)? = null

    private var currentMessage: IMSessionMessage? = null

    fun attach(context: AppCompatActivity, onAudioStateChange: ((IMSessionMessage) -> Unit)? = null) {

        this.onAudioStateChange = onAudioStateChange

        val lifecycle = context.lifecycle

        val sensorManager = ContextCompat.getSystemService(context, SensorManager::class.java)
        val proximitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun resume() {
                Log.d(TAG, "[im] register sensor")
                sensorManager?.registerListener(sensorEventListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun pause() {
                Log.d(TAG, "[im] unregister sensor")
                sensorManager?.unregisterListener(sensorEventListener)
                stopAudio()
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun destroy() {
                releaseAudio()
            }
        })
    }

    private val sensorEventListener = object : SensorEventListener {

        override fun onSensorChanged(event: SensorEvent) {
            val dis = event.values
            if (0.0f == dis[0]) { //靠近，设置为听筒模式
                updateAudioStreamType(AudioManager.STREAM_VOICE_CALL)
            } else { //离开，复原
                updateAudioStreamType(AudioManager.STREAM_MUSIC)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

        }
    }

    fun playAudio(context: Context, msg: IMSessionMessage, downloadFile: Boolean = true) {

        Log.d(TAG, "[im] play audio, $msg")

        stopAudio()

        val rawMsg = msg.rawMsg
        val attachment = msg.rawMsg.attachment as? AudioAttachment ?: return
        val file = File(attachment.pathForSave)

        this.currentMessage = msg

        if (!file.exists()) {
            if (downloadFile) {
                Log.d(TAG, "[im] down load audio attachment, $msg")
                changeAudioState(AudioState.Downloading)
                NIMSDK.getMsgService().downloadAttachment(rawMsg, false)
                    .setCallback(object : DummyAdaptRequestCallback<Void>("download audio attachment $msg") {
                        override fun onSuccess(t: Void) {
                            playAudio(context, msg, false)
                        }
                    })
            } else {
                Log.w(TAG, "[im] no audio attachment, $msg")
            }
        } else {
            Log.d(TAG, "[im] play audio impl, $msg")
            rawMsg.ensureRead()
            audioPlayer = AudioPlayer(context).apply {
                onPlayListener = playerCallback
                setDataSource(attachment.path)
                start(AudioManager.STREAM_MUSIC)
            }
        }
    }

    fun stopAudio() {
        Log.d(TAG, "[im] stop audio")
        audioPlayer?.stop()
        changeAudioState(AudioState.Complete)
    }

    fun releaseAudio() {
        Log.d(TAG, "[im] release audio")
        stopAudio()
        audioPlayer = null
        audioStreamType = AudioManager.STREAM_MUSIC
        currentMessage = null
    }

    private fun updateAudioStreamType(streamType: Int) {
        if (audioStreamType == streamType) return

        Log.d(TAG, "[im] updateAudioStreamType $streamType")

        audioStreamType = streamType

        this.audioPlayer?.apply {
            if (isPlaying) {
                val seekPosition = currentPosition.toInt()
                start(audioStreamType)
                seekTo(seekPosition)
            }
        }
    }

    private val playerCallback = object : OnPlayListener {
        override fun onPrepared() {
            Log.d(TAG, "[im] audioPlayer onPrepared")
            changeAudioState(AudioState.Prepare)
        }

        override fun onCompletion() {
            Log.d(TAG, "[im] audioPlayer onCompletion")
            changeAudioState(AudioState.Complete)
            playEndSound()
        }

        override fun onInterrupt() {
            Log.d(TAG, "[im] audioPlayer onInterrupt")
            changeAudioState(AudioState.Interrupt)
        }

        override fun onError(error: String?) {
            Log.d(TAG, "[im] audioPlayer onError")
            changeAudioState(AudioState.Error)
        }

        override fun onPlaying(curPosition: Long) {
            Log.d(TAG, "[im] audioPlayer onPlaying")
            changeAudioState(AudioState.Playing)
        }
    }

    private fun playEndSound() {
        MediaPlayer.create(AppCenter.get().context, R.raw.msg_audio_end_tip).apply {
            isLooping = false
            setAudioAttributes(AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build())
            setOnCompletionListener {
                release()
            }
            start()
        }
    }

    private fun changeAudioState(audioState: AudioState) {
        this.currentMessage?.let { msg ->
            if (msg.audioState != audioState) {
                msg.audioState = audioState
                this.onAudioStateChange?.invoke(msg)
            }
        }
    }

    enum class AudioState { Illegal, Downloading, Prepare, Complete, Interrupt, Error, Playing }

    private fun IMMessage.ensureRead(): Boolean {
        val notRead = (msgType == MsgTypeEnum.audio
                && direct == MsgDirectionEnum.In
                && attachStatus == AttachStatusEnum.transferred
                && status != MsgStatusEnum.read)

        if (notRead) {
            status = MsgStatusEnum.read
            NIMSDK.getMsgService().updateIMMessageStatus(this)
        }
        return notRead
    }
}