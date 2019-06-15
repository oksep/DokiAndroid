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
import com.dokiwa.dokidoki.profile.api.LocalAssetApi
import com.dokiwa.dokidoki.profile.create.model.IndustryData
import com.dokiwa.dokidoki.profile.view.IndustryPickerView
import com.dokiwa.dokidoki.ui.util.ViewUtil

/**
 * Created by Septenary on 2019-06-08.
 */
class IndustryPickerDialog(
    context: Activity,
    private val onIndustryChoose: (Int, String, Int, String) -> Unit
) : BottomDialog(context) {

    companion object {
        private const val TAG = "IndustryPickerDialog"

        fun create(context: Activity, callback: (Int, String, Int, String) -> Unit): Dialog {
            return IndustryPickerDialog(context, callback)
        }
    }

    init {
        val pickerView = IndustryPickerView(context)
        pickerView.setBackgroundColor(Color.WHITE)
        setContentView(
            pickerView,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewUtil.dp2px(context, 260f))
        )

        fun initIndustryPickerView(data: IndustryData) {
            val industryList = data.industryList
            pickerView.initView(industryList, { industry, sub ->
                onIndustryChoose.invoke(industry.id, industry.name, sub.id, sub.name)
            }) {
                dismiss()
                true
            }
        }

        Api.getLocalAsset(context, LocalAssetApi::class.java)
            .getIndustryListData()
            .subscribeApi(
                context as? CompositeDisposableContext,
                ::initIndustryPickerView
            ) {
                Log.e(TAG, "local asset api error $it")
            }
    }
}