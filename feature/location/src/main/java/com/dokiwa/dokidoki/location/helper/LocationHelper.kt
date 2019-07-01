package com.dokiwa.dokidoki.location.helper

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationQualityReport
import com.dokiwa.dokidoki.location.Log

/**
 * Created by Septenary on 2019-07-01.
 * ref: https://lbs.amap.com/api/android-location-sdk/guide/android-location/getlocation
 */
object LocationHelper {

    private const val TAG = "LocationHelper"

    private fun getDefaultOption(): AMapLocationClientOption {
        val mOption = AMapLocationClientOption()
        //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.isGpsFirst = false
        //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.httpTimeOut = 30000
        //可选，设置定位间隔。默认为2秒
        mOption.interval = 2000
        //可选，设置是否返回逆地理地址信息。默认是true
        mOption.isNeedAddress = true
        //可选，设置是否单次定位。默认是false
        mOption.isOnceLocation = false
        //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        mOption.isOnceLocationLatest = false
        //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP)
        //可选，设置是否使用传感器。默认是false
        mOption.isSensorEnable = false
        //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.isWifiScan = true
        //可选，设置是否使用缓存定位，默认为true
        mOption.isLocationCacheEnable = true
        //可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        mOption.geoLanguage = AMapLocationClientOption.GeoLanguage.DEFAULT
        return mOption
    }

    fun toMessage(location: AMapLocation?): String {
        return if (null != location) {
            val report =
                """
                ***定位质量报告***
                * WIFI开关：	${if (location.locationQualityReport.isWifiAble) "开启" else "关闭"}
                * GPS状态：	${getGPSStatusString(location.locationQualityReport.gpsStatus)}
                * GPS星数：	${location.locationQualityReport.gpsSatellites}
                * 网络类型：	${location.locationQualityReport.networkType}
                * 网络耗时：	${location.locationQualityReport.netUseTime}
                ****************
                """
            if (location.errorCode == 0) {
                """
                == 定位成功 ==
                定位类型	${location.locationType}
                经	度 	${location.longitude}
                纬	度 	${location.latitude}
                精	度 	${location.accuracy}
                提供者 	${location.provider}
                速	度 	${location.speed}
                角	度 	${location.bearing}
                星	数 	${location.satellites}
                国	家 	${location.country}
                省		${location.province}
                市		${location.city}
                城市编 	${location.cityCode}
                区		${location.district}
                区域码 	${location.adCode}
                地址	 	${location.address}
                兴趣点 	${location.poiName}
                $report
                """
            } else {
                """
                == 定位失败 ==
                错误码  	${location.errorCode}
                错误信息	${location.errorInfo}
                错误描述	${location.locationDetail}
                $report
                """
            }
        } else {
            "定位失败，loc is null"
        }.trimIndent()
    }

    private fun getGPSStatusString(statusCode: Int): String {
        return when (statusCode) {
            AMapLocationQualityReport.GPS_STATUS_OK -> {
                "GPS状态正常"
            }
            AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER -> {
                "手机中没有GPS Provider，无法进行GPS定位"
            }
            AMapLocationQualityReport.GPS_STATUS_OFF -> {
                "GPS关闭，建议开启GPS，提高定位质量"
            }
            AMapLocationQualityReport.GPS_STATUS_MODE_SAVING -> {
                "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量"
            }
            AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION -> {
                "没有GPS定位权限，建议开启gps定位权限"
            }
            else -> ""
        }
    }

    private val option by lazy {
        getDefaultOption().apply {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            isNeedAddress = true
            isMockEnable = true
            httpTimeOut = 20000
            isLocationCacheEnable = false
            isOnceLocation = true
            isOnceLocationLatest = true
        }
    }

    @SuppressLint("StaticFieldLeak")
    private var client: AMapLocationClient? = null

    private val callbacks: MutableList<(AMapLocation?) -> Unit> = mutableListOf()

    fun getLocation(context: Context, lifecycle: Lifecycle, callback: (AMapLocation?) -> Unit) {

        if (!callbacks.contains(callback)) {
            callbacks.add(callback)
        }

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
                setLocationListener { location: AMapLocation? ->
                    Log.d(TAG, "onGetLocation -> ${toMessage(location)}")
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

    class LocationException(message: String) : Exception(message)
}
