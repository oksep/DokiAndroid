package com.dokiwa.dokidoki.admin.util

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Vibrator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.dokiwa.dokidoki.admin.AdminActivity
import com.dokiwa.dokidoki.admin.Log
import com.dokiwa.dokidoki.center.AppCenter

/**
 * Created by Septenary on 2018/11/20.
 */
object ShakeHelper : SensorEventListener, LifecycleObserver {

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private var lastShakeNanosecond = 0L

    private const val MIN_TIME = 3_000_000_000 // 5 seconds

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        val values = sensorEvent.values
        val x = Math.abs(values[0])
        val y = Math.abs(values[1])
        val z = Math.abs(values[2])
        if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER &&
            x > 19 && y > 19 && z > 19 &&
            sensorEvent.timestamp - lastShakeNanosecond > MIN_TIME
        ) {
            lastShakeNanosecond = sensorEvent.timestamp
            maybeShaking()
        }
    }

    private fun maybeShaking() {
        Log.d("ShakeHelper", "shaking~")
        vibrate()
        playSound()
        startAdmin()
    }

    private fun startAdmin() {
        AppCenter.get().context.let {
            it.startActivity(
                Intent(it, AdminActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    private fun vibrate() {
        (AppCenter.get().context.getSystemService(Service.VIBRATOR_SERVICE) as? Vibrator)?.vibrate(200)
    }

    private fun playSound() {
        // TODO: 2018/12/1 @Septenary
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun registerShakeHelper() {
        (AppCenter.get().context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager)?.apply {
            registerListener(
                this@ShakeHelper,
                getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unRegisterShakerHelper() {
        (AppCenter.get().context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager)?.apply {
            unregisterListener(this@ShakeHelper)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release() {
        // release player
    }

    fun attach(lifecycle: Lifecycle) {
        lifecycle.addObserver(this)
        // init player
    }
}
