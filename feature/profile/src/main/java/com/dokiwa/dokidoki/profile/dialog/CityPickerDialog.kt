package com.dokiwa.dokidoki.profile.dialog

import android.app.Activity
import android.app.Dialog
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.create.fragment.CityFragment
import com.dokiwa.dokidoki.profile.create.model.Province
import com.dokiwa.dokidoki.profile.view.CityPickerView
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * Created by Septenary on 2019-06-08.
 */
class CityPickerDialog(
    context: Activity,
    private val onCityChoose: (String, String, String) -> Unit
) : BottomSheetDialog(context) {

    companion object {
        private const val TAG = "CityPickerDialog"

        fun create(context: Activity, callback: (String, String, String) -> Unit): Dialog {
            return CityPickerDialog(context, callback)
        }
    }

    private val pickerView: CityPickerView = CityPickerView(context)

    init {
        setContentView(pickerView)
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
        pickerView.initView(provinceList, { province, city ->
            onCityChoose.invoke(province.name, city.name, city.code)
        }) {
            dismiss()
            true
        }
    }
}

