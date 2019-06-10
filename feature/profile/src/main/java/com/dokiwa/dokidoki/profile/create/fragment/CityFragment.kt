package com.dokiwa.dokidoki.profile.create.fragment

import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.create.model.CityData
import com.dokiwa.dokidoki.profile.create.model.Province
import com.dokiwa.dokidoki.profile.create.model.SharedViewModel
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_create_profile_city.*
import retrofit2.http.GET

/**
 * Created by Septenary on 2019/1/2.
 */
private const val TAG = "CityFragment"

internal class CityFragment : BaseStepFragment() {

    override val layoutId: Int = R.layout.fragment_create_profile_city

    override val skipable: Boolean = false

    override fun updateContent(viewModel: SharedViewModel) {
        cityTextContainer.setOnClickListener {
            cityPickerView.show()
        }

        confirmBtn.setOnClickListener {
            requestNextStep()
        }

        loadData(viewModel)
    }

    private fun loadData(viewModel: SharedViewModel) {
        Api.getLocalAsset(requireContext(), LocalAssetApi::class.java)
            .getUserConfig()
            .subscribeApi(this, {
                initCityPickerView(it.provinceList, viewModel)
            }, {
                Log.e(TAG, "local asset api error $it")
                requestNextStep()
            })
    }

    private fun initCityPickerView(provinceList: List<Province>, viewModel: SharedViewModel) {
        cityPickerView.initView(provinceList, { p, c ->
            province.text = p.name
            city.text = c.name
            viewModel.city.value = c.code
        })
    }

    override fun reset() {

    }

    interface LocalAssetApi {
        @GET("/api/city.json")
        fun getUserConfig(): Single<CityData>
    }
}