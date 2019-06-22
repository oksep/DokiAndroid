package com.dokiwa.dokidoki.profile.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.view.ViewGroup
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.dialog.BottomDialog
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.LocalAssetApi
import com.dokiwa.dokidoki.profile.create.model.CityData
import com.dokiwa.dokidoki.profile.view.CityPickerView
import com.dokiwa.dokidoki.ui.util.ViewUtil

/**
 * Created by Septenary on 2019-06-08.
 */
class CityPickerDialog(
    context: Activity,
    private val onCityChoose: (String, String, String) -> Unit
) : BottomDialog(context) {

    companion object {
        private const val TAG = "CityPickerDialog"

        fun create(context: Activity, callback: (String, String, String) -> Unit): Dialog {
            return CityPickerDialog(context, callback)
        }
    }

    init {
        val pickerView = CityPickerView(context)
        pickerView.setBackgroundColor(Color.WHITE)
        setContentView(
            pickerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                context.resources.getDimensionPixelSize(R.dimen.profile_picker_view_height)
            )
        )

        fun initCityPickerView(data: CityData) {
            val provinceList = data.provinceList
            pickerView.initView(provinceList, { province, city ->
                onCityChoose.invoke(province.name, city.name, city.code)
            }) {
                dismiss()
                true
            }
        }

        Api.getLocalAsset(context, LocalAssetApi::class.java)
            .getCityConfig()
            .subscribeApi(
                context as? CompositeDisposableContext,
                ::initCityPickerView
            ) {
                Log.e(TAG, "local asset api error $it")
            }
    }
}

