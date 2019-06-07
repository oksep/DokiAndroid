package com.dokiwa.dokidoki.profile.dialog

import android.app.Activity
import android.view.LayoutInflater
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.create.fragment.CityFragment
import com.dokiwa.dokidoki.profile.create.model.Province
import com.dokiwa.dokidoki.profile.view.CityPickerView
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Created by Septenary on 2019-06-08.
 */
class CityPickerDialog(context: Activity) : BottomSheetDialog(context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_city_picker, null)
        setContentView(view)
        loadData()
    }

    private fun loadData() {
        Api.getLocalAsset(context, CityFragment.LocalAssetApi::class.java)
            .getUserConfig()
            .subscribeApi(
                context as? CompositeDisposableContext,
                {
                    initCityPickerView(it.provinceList)
                },
                {
                    Log.e(TAG, "local asset api error $it")
                }
            )
    }

    private fun initCityPickerView(provinceList: List<Province>) {
        val cityPickerView = findViewById<CityPickerView>(R.id.cityPickerView)
        cityPickerView?.initView(provinceList) { p, c ->

        }
    }

    companion object {
        private const val TAG = "CityPickerDialog"

        fun create(context: Activity): CityPickerDialog {
            return CityPickerDialog(context)
        }
    }
}

