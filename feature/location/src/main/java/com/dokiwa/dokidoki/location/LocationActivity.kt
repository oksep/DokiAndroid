package com.dokiwa.dokidoki.location

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.amap.api.location.AMapLocation
import com.dokiwa.dokidoki.center.ext.rx.mainMain
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_location.*
import java.util.concurrent.TimeUnit

/**
 * Created by Septenary on 2019-06-30.
 */
class LocationActivity : BaseLocationActivity() {

    companion object {
        private const val TAG = "LocationActivity"

        fun launch(context: Activity) {
            context.startActivity(Intent(context, LocationActivity::class.java))
            context.overridePendingTransition(R.anim.ui_anim_activity_bottom_in, R.anim.ui_anim_none)
        }
    }

    private val adapter by lazy { PoiAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarDark()
        setContentView(R.layout.activity_location)
        initView()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.ui_anim_none, R.anim.ui_anim_activity_bottom_out)
    }

    private fun initView() {

        toolBar.leftTextView.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        toolBar.rightTextView.setOnClickListener {
            requsetLocation()
        }

        Observable.create<String> { source ->
            //            toolBar.rightTextView.setOnClickListener {
//                source.onNext(editText.text.toString())
//            }
            editText.addTextChangedListener(object : SimpleTextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    source.onNext(s.toString())
                }
            })
        }.debounce(500, TimeUnit.MILLISECONDS).mainMain().subscribe({
            loadPoiData(it)
        }, {
            Log.e(TAG, "edit text observer error", it)
        }).also {
            addDispose(it)
        }

        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { adapter, _, position ->
            // callback
        }
    }

    override fun onGetLocation(location: AMapLocation) {
        Log.d(TAG, "get location -> $location")
    }

    private fun loadPoiData(keyword: String?) {
        PoiHelper.t(this, "", "", 0)
    }

    private fun onLoadPoiSucess(keyword: String?) {
        if (keyword.isNullOrEmpty()) {
            adapter.setNewData(mutableListOf())
        } else {
            val list = mutableListOf<Location>()
            repeat(50) {
                list.add(Location("", ""))
            }
            adapter.setNewData(list)
        }
    }
}