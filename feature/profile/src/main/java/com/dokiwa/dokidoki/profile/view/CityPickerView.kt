package com.dokiwa.dokidoki.profile.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.create.model.City
import com.dokiwa.dokidoki.profile.create.model.Province

/**
 * Created by Septenary on 2019/2/12.
 */
class CityPickerView : FrameLayout {

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // nothing
    }

    fun initView(
        provinceList: List<Province>,
        cb: (province: Province, city: City) -> Unit,
        topBarBtnInterceptor: () -> Boolean = { false }
    ) {
        var pickerView: OptionsPickerView<Any>? = null

        OptionsPickerBuilder(
            context,
            OnOptionsSelectListener { option1, option2, option3, v ->
                val province = provinceList[option1]
                val city = province.cityList[option2]
                cb(province, city)
            }
        )
            .setLayoutRes(R.layout.view_picker_multiple) { v ->
                v.findViewById<View>(R.id.pickerConfirmBtn).setOnClickListener {
                    pickerView?.returnData()
                    if (!topBarBtnInterceptor()) {
                        hide()
                    }
                }
                v.findViewById<View>(R.id.pickerCancelBtn).setOnClickListener {
                    if (!topBarBtnInterceptor()) {
                        hide()
                    }
                }
            }
            .setOutSideCancelable(false)
            .setOptionsSelectChangeListener { _, _, _ -> }
            .isCenterLabel(false)
            .isDialog(false)
            .setDecorView(this)
            .build<Any>()
            .also { pickerView = it }

        pickerView?.setPicker(
            provinceList,
            provinceList.map { it.cityList }
        )
        pickerView?.setKeyBackCancelable(false)
        pickerView?.show(false)
    }

    fun show() {
        this.animate().translationY(0f).start()
    }

    fun hide() {
        this.animate().translationY(height.toFloat()).start()
    }
}