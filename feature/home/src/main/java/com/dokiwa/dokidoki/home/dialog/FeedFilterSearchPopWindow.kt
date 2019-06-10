package com.dokiwa.dokidoki.home.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.dokiwa.dokidoki.center.plugin.model.Gender
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.home.R
import com.dokiwa.dokidoki.ui.view.SeekBarPressure
import kotlinx.android.synthetic.main.dialog_feed_filter_search.view.*

/**
 * Created by Septenary on 2019-06-07.
 */
class FeedFilterSearchPopWindow(
    private val context: Activity,
    feedFilter: FeedFilter,
    private val onFilterChange: (FeedFilter) -> Unit
) : PopupWindow(context) {

    private var feedFilter = feedFilter.copy()

    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_feed_filter_search, null)
        setContentView(contentView)
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isFocusable = true
        isOutsideTouchable = true
        isSplitTouchEnabled = false
        width = WindowManager.LayoutParams.MATCH_PARENT
        setOnDismissListener {
            setBackgroundAlpha(1f)
        }
        initViews()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        val contentView = contentView ?: return
        contentView.resetButton.setOnClickListener {
            feedFilter = FeedFilter()
            initViews()
        }

        contentView.confirmButton.setOnClickListener {
            dismiss()
            onFilterChange.invoke(feedFilter)
        }

        contentView.areaSelector.setOnClickListener {
            IProfilePlugin.get().getCityPickerDialog(context) { provinceName, cityName, cityCode ->
                feedFilter.provinceName = provinceName
                feedFilter.cityName = cityName
                feedFilter.cityCode = cityCode
                contentView.areaDescription.text = feedFilter.getCityAreaDescription()
            }.show()
        }

        contentView.areaDescription.text = feedFilter.getCityAreaDescription()

        contentView.ageSeekBar.setProgressLow(feedFilter.ageMin.toDouble())
        contentView.ageSeekBar.setProgressHigh(feedFilter.ageMax.toDouble())
        contentView.ageSeekBar.setOnSeekBarChangeListener(object : SeekBarPressure.OnSeekBarChangeListener {
            override fun onProgressBefore() {
            }

            override fun onProgressChanged(seekBar: SeekBarPressure?, progressLow: Double, progressHigh: Double) {
                feedFilter.ageMin = progressLow.toInt()
                feedFilter.ageMax = progressHigh.toInt()
                contentView.ageDescription.text = feedFilter.getAgeDescription()
            }

            override fun onProgressAfter() {
            }
        })

        contentView.ageDescription.text = feedFilter.getAgeDescription()

        val genderOptions = listOf(
            Pair(contentView.genderMale, Gender.MALE),
            Pair(contentView.genderFemale, Gender.FEMALE),
            Pair(contentView.genderAll, Gender.UNKNOWN)
        )
        genderOptions.forEach { opt1 ->
            val view = opt1.first
            view?.isSelected = feedFilter.gender == opt1.second
            view?.setOnClickListener {
                genderOptions.forEach { opt2 ->
                    opt2.first.isSelected = false
                }
                view.isSelected = true
                feedFilter.gender = opt1.second
            }
        }
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        setBackgroundAlpha(0.75f)
    }

    private fun setBackgroundAlpha(alpha: Float) {
        val lp = context.window.attributes
        lp.alpha = alpha
        context.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        context.window.attributes = lp
    }
}

data class FeedFilter(
    var gender: Int = Gender.UNKNOWN,
    var ageMin: Int = 0,
    var ageMax: Int = 100,
    var provinceName: String? = null,
    var cityName: String? = null,
    var cityCode: String? = null
) {
    fun asQueryMap() = mutableMapOf(
        "gender" to gender.toString(),
        "age_min" to ageMin.toString(),
        "age_max" to ageMax.toString()
    ).also {
        if (cityCode != null) {
            it["city_code"] = cityCode!!
        }
    }

    fun getCityAreaDescription(): String {
        return if (cityCode != null) {
            if (provinceName != cityName) {
                "$provinceName$cityName"
            } else {
                "$cityName"
            }
        } else {
            "所有地区"
        }
    }

    fun getAgeDescription(): String {
        return "$ageMin~$ageMax" + "岁"
    }
}