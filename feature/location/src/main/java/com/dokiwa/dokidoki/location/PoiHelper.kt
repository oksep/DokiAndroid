package com.dokiwa.dokidoki.location

import android.content.Context
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch

/**
 * Created by Septenary on 2019-07-01.
 * ref: https://lbs.amap.com/api/android-sdk/guide/map-data/poi
 */
object PoiHelper {
    fun t(context: Context, keyWord: String, cityCode: String, currentPage: Int) {
        val query = PoiSearch.Query(keyWord, "", cityCode)
        // keyWord 表示搜索字符串
        // 第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        // cityCode 表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.pageSize = 10// 设置每页最多返回多少条 poiitem
        query.pageNum = currentPage//设置查询页码

        val poiSearch = PoiSearch(context, query)

        // 设置周边搜索的中心点以及半径
        val point = LatLonPoint(39.941711, 116.382248)
        poiSearch.bound = PoiSearch.SearchBound(point, 1000)

        poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
            override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {
                Log.d("AAAAA", "onPoiItemSearched $p0, $p1")
            }

            override fun onPoiSearched(p0: PoiResult?, p1: Int) {
                Log.d("AAAAA", "onPoiSearched $p0, $p1")
            }
        })
        poiSearch.searchPOIAsyn()
    }
}