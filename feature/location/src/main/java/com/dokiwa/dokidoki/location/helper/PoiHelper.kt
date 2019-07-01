package com.dokiwa.dokidoki.location.helper

import android.content.Context
import com.amap.api.location.AMapLocation
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.dokiwa.dokidoki.location.Log
import io.reactivex.Single

/**
 * Created by Septenary on 2019-07-01.
 * ref: https://lbs.amap.com/api/android-sdk/guide/map-data/poi
 */
object PoiHelper {
    private const val TAG = "PoiHelper"

    private val shPoint = LatLonPoint(31.22222, 121.45806)

    fun getPoiData(
        context: Context,
        location: AMapLocation,
        keyWord: String?,
        currentPage: Int = 0,
        pageSize: Int = 100
    ): Single<List<PoiItem>> {
        return Single.create { emitter ->
            // TODO: 2019-07-02 @Septenary
            // val cityCode = location.cityCode ?: ""
            val cityCode = ""

            val query = PoiSearch.Query(keyWord ?: "", "", cityCode).apply {
                setPageSize(pageSize)
                pageNum = currentPage
            }

            val poiSearch = PoiSearch(context, query)

            // TODO: 2019-07-02 @Septenary
            // val point = LatLonPoint(location.latitude, location.longitude)
            val point = shPoint

            poiSearch.bound = PoiSearch.SearchBound(point, 1000)

            Log.d(
                TAG,
                "search poi: " +
                        "point -> [${point.latitude}, ${point.longitude}], " +
                        "cityCode -> $cityCode, " +
                        "keyWord -> $keyWord"
            )

            poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
                override fun onPoiItemSearched(item: PoiItem?, code: Int) {
                    Log.d(TAG, "onPoiItemSearched $item, $code")
                }

                override fun onPoiSearched(result: PoiResult?, code: Int) {
                    Log.d(TAG, "onPoiSearched $result, $code")
                    if (code == AMapException.CODE_AMAP_SUCCESS && result?.pois?.isNotEmpty() == true) {
                        emitter.onSuccess(result.pois)
                    } else {
                        emitter.onError(toError(code))
                    }
                }
            })
            poiSearch.searchPOIAsyn()
        }
    }

    private fun toError(code: Int): AMapException {
        return when (code) {
            //服务错误码
            1001 -> AMapException(AMapException.AMAP_SIGNATURE_ERROR)
            1002 -> AMapException(AMapException.AMAP_INVALID_USER_KEY)
            1003 -> AMapException(AMapException.AMAP_SERVICE_NOT_AVAILBALE)
            1004 -> AMapException(AMapException.AMAP_DAILY_QUERY_OVER_LIMIT)
            1005 -> AMapException(AMapException.AMAP_ACCESS_TOO_FREQUENT)
            1006 -> AMapException(AMapException.AMAP_INVALID_USER_IP)
            1007 -> AMapException(AMapException.AMAP_INVALID_USER_DOMAIN)
            1008 -> AMapException(AMapException.AMAP_INVALID_USER_SCODE)
            1009 -> AMapException(AMapException.AMAP_USERKEY_PLAT_NOMATCH)
            1010 -> AMapException(AMapException.AMAP_IP_QUERY_OVER_LIMIT)
            1011 -> AMapException(AMapException.AMAP_NOT_SUPPORT_HTTPS)
            1012 -> AMapException(AMapException.AMAP_INSUFFICIENT_PRIVILEGES)
            1013 -> AMapException(AMapException.AMAP_USER_KEY_RECYCLED)
            1100 -> AMapException(AMapException.AMAP_ENGINE_RESPONSE_ERROR)
            1101 -> AMapException(AMapException.AMAP_ENGINE_RESPONSE_DATA_ERROR)
            1102 -> AMapException(AMapException.AMAP_ENGINE_CONNECT_TIMEOUT)
            1103 -> AMapException(AMapException.AMAP_ENGINE_RETURN_TIMEOUT)
            1200 -> AMapException(AMapException.AMAP_SERVICE_INVALID_PARAMS)
            1201 -> AMapException(AMapException.AMAP_SERVICE_MISSING_REQUIRED_PARAMS)
            1202 -> AMapException(AMapException.AMAP_SERVICE_ILLEGAL_REQUEST)
            1203 -> AMapException(AMapException.AMAP_SERVICE_UNKNOWN_ERROR)
            //sdk返回错误
            1800 -> AMapException(AMapException.AMAP_CLIENT_ERRORCODE_MISSSING)
            1801 -> AMapException(AMapException.AMAP_CLIENT_ERROR_PROTOCOL)
            1802 -> AMapException(AMapException.AMAP_CLIENT_SOCKET_TIMEOUT_EXCEPTION)
            1803 -> AMapException(AMapException.AMAP_CLIENT_URL_EXCEPTION)
            1804 -> AMapException(AMapException.AMAP_CLIENT_UNKNOWHOST_EXCEPTION)
            1806 -> AMapException(AMapException.AMAP_CLIENT_NETWORK_EXCEPTION)
            1900 -> AMapException(AMapException.AMAP_CLIENT_UNKNOWN_ERROR)
            1901 -> AMapException(AMapException.AMAP_CLIENT_INVALID_PARAMETER)
            1902 -> AMapException(AMapException.AMAP_CLIENT_IO_EXCEPTION)
            1903 -> AMapException(AMapException.AMAP_CLIENT_NULLPOINT_EXCEPTION)
            //云图和附近错误码
            2000 -> AMapException(AMapException.AMAP_SERVICE_TABLEID_NOT_EXIST)
            2001 -> AMapException(AMapException.AMAP_ID_NOT_EXIST)
            2002 -> AMapException(AMapException.AMAP_SERVICE_MAINTENANCE)
            2003 -> AMapException(AMapException.AMAP_ENGINE_TABLEID_NOT_EXIST)
            2100 -> AMapException(AMapException.AMAP_NEARBY_INVALID_USERID)
            2101 -> AMapException(AMapException.AMAP_NEARBY_KEY_NOT_BIND)
            2200 -> AMapException(AMapException.AMAP_CLIENT_UPLOADAUTO_STARTED_ERROR)
            2201 -> AMapException(AMapException.AMAP_CLIENT_USERID_ILLEGAL)
            2202 -> AMapException(AMapException.AMAP_CLIENT_NEARBY_NULL_RESULT)
            2203 -> AMapException(AMapException.AMAP_CLIENT_UPLOAD_TOO_FREQUENT)
            2204 -> AMapException(AMapException.AMAP_CLIENT_UPLOAD_LOCATION_ERROR)
            //路径规划
            3000 -> AMapException(AMapException.AMAP_ROUTE_OUT_OF_SERVICE)
            3001 -> AMapException(AMapException.AMAP_ROUTE_NO_ROADS_NEARBY)
            3002 -> AMapException(AMapException.AMAP_ROUTE_FAIL)
            3003 -> AMapException(AMapException.AMAP_OVER_DIRECTION_RANGE)
            //短传分享
            4000 -> AMapException(AMapException.AMAP_SHARE_LICENSE_IS_EXPIRED)
            4001 -> AMapException(AMapException.AMAP_SHARE_FAILURE)
            else -> {
                AMapException("查询失败 $code")
            }
        }
    }
}