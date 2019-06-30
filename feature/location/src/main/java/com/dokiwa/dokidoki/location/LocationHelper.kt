package com.dokiwa.dokidoki.location

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener

/**
 * Created by Septenary on 2019-07-01.
 * ref: https://lbs.amap.com/api/android-location-sdk/guide/android-location/getlocation
 */
object LocationHelper {

    // 声明 AMapLocationClientOption 对象
    private val option by lazy {
        AMapLocationClientOption().apply {

            // 设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy

            //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
            // locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving

            //设置定位模式为AMapLocationMode.Device_Sensors，仅设备模式。
            // locationMode = AMapLocationClientOption.AMapLocationMode.Device_Sensors

            // 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
            // locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn

            // 设置是否返回地址信息（默认返回地址信息）
            isNeedAddress = true

            // 设置是否允许模拟位置,默认为true，允许模拟位置
            isMockEnable = true

            // 单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
            httpTimeOut = 20000

            // 关闭缓存机制
            isLocationCacheEnable = false

            // 获取一次定位结果：该方法默认为false。
            isOnceLocation = true

            // 获取最近3s内精度最高的一次定位结果：
            // 设置 setOnceLocationLatest(boolean b) 接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
            // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，
            // 反之不会，默认为false。
            isOnceLocationLatest = true

            // 设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
            // interval = 1000
        }
    }

    @SuppressLint("StaticFieldLeak")
    private var client: AMapLocationClient? = null

    private val callbacks: MutableList<(AMapLocation) -> Unit> = mutableListOf()

    fun getLocation(context: Context, lifecycle: Lifecycle, callback: (AMapLocation) -> Unit) {

        callbacks.add(callback)

        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                callbacks.remove(callback)
                if (callbacks.isEmpty()) {
                    destroyLocation()
                }
            }
        })

        if (client == null) {
            client = AMapLocationClient(context.applicationContext).apply {
                setLocationListener { location ->
                    callbacks.forEach {
                        it.invoke(location)
                    }
                    callbacks.clear()
                    destroyLocation()
                }
                setLocationOption(option)
            }.also {
                it.stopLocation()
                it.startLocation()
            }
        }
    }

    // 销毁定位客户端，同时销毁本地定位服务。
    private fun destroyLocation() {
        stopLocation()
        client?.onDestroy()
        client = null
    }

    // 停止定位后，本地定位服务并不会被销毁
    private fun stopLocation() {
        client?.stopLocation()
    }

    // 声明定位回调监听器
    private val listener by lazy {
        AMapLocationListener { location ->
            if (location != null) {
                if (location.errorCode == 0) {
                    location.locationType // 获取当前定位结果来源，如网络定位结果，详见定位类型表
                    location.latitude // 获取纬度
                    location.longitude // 获取经度
                    location.accuracy // 获取精度信息
                    location.address // 地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    location.country // 国家信息
                    location.province // 省信息
                    location.city // 城市信息
                    location.district // 城区信息
                    location.street // 街道信息
                    location.streetNum // 街道门牌号信息
                    location.cityCode // 城市编码
                    location.adCode // 地区编码
                    location.aoiName // 获取当前定位点的AOI信息
                    location.buildingId // 获取当前室内定位的建筑物Id
                    location.floor // 获取当前室内定位的楼层
                    location.gpsAccuracyStatus // 获取GPS的当前状态
                    location.time // milliseconds UTC time
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e(
                        "LocationHelper",
                        "location Error",
                        RuntimeException("ErrCode: ${location.errorCode}, errInfo: ${location.errorInfo}")
                    )
                }
            }
        }
    }

}
