package com.dokiwa.dokidoki.location.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.amap.api.services.core.PoiItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import com.dokiwa.dokidoki.center.ext.rx.mainMain
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.util.ActivityHelper
import com.dokiwa.dokidoki.location.Log
import com.dokiwa.dokidoki.location.R
import com.dokiwa.dokidoki.location.helper.PoiHelper
import com.dokiwa.dokidoki.ui.ext.setRefreshListenerHaptic
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_poi.*
import kotlinx.android.synthetic.main.view_item_poi.view.*
import java.util.concurrent.TimeUnit

/**
 * Created by Septenary on 2019-06-30.
 */

private const val TAG = "PoiActivity"

class PoiActivity : BaseLocationActivity() {

    companion object {

        private const val EXTRA_RESULT_NAME = "extra.result_name"
        private const val EXTRA_RESULT_LATITUDE = "extra.result_latitude"
        private const val EXTRA_RESULT_LONGITUDE = "extra.result_longitude"

        private const val REQUEST_CODE = 0x0001

        fun launch(context: FragmentActivity, onGetPoi: (String, Double, Double) -> Unit) {
            context.overridePendingTransition(R.anim.ui_anim_activity_bottom_in, R.anim.ui_anim_none)

            ActivityHelper(context).startForResult(
                Intent(context, PoiActivity::class.java),
                REQUEST_CODE
            ).subscribe { result, _ ->
                if (result?.data != null) {
                    val intent = result.data!!
                    onGetPoi.invoke(
                        intent.getStringExtra(EXTRA_RESULT_NAME),
                        intent.getDoubleExtra(EXTRA_RESULT_LATITUDE, 0.0),
                        intent.getDoubleExtra(EXTRA_RESULT_LONGITUDE, 0.0)
                    )
                }
            }.also {
                (context as? CompositeDisposableContext)?.addDispose(it)
            }
        }
    }

    private val adapter by lazy { PoiAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarDark()
        setContentView(R.layout.activity_poi)
        initView()
        loadPoiData("")
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.ui_anim_none,
            R.anim.ui_anim_activity_bottom_out
        )
    }

    private fun initView() {

        toolBar.leftTextView.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        Observable.create<String> { source ->
            refreshRecyclerView.getRefreshLayout().setRefreshListenerHaptic {
                source.onNext(editText.text.toString())
            }
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
        refreshRecyclerView.setAdapter(adapter)
        adapter.setOnItemClickListener { adapter, _, position ->
            (adapter.getItem(position) as? PoiItem)?.let {
                setResult(
                    Activity.RESULT_OK,
                    Intent()
                        .putExtra(EXTRA_RESULT_NAME, it.title)
                        .putExtra(EXTRA_RESULT_LATITUDE, it.latLonPoint.latitude)
                        .putExtra(EXTRA_RESULT_LONGITUDE, it.latLonPoint.longitude)
                )
                finish()
            }
        }
    }

    private fun loadPoiData(keyword: String?) {
        refreshRecyclerView.showLoading()
        requestLocation().flatMap {
            PoiHelper.getPoiData(this, it, keyword)
        }.subscribeApi(this, {
            Log.d(TAG, "get poi success -> $it")
            adapter.setNewData(it)
            refreshRecyclerView.showSuccess()
        }, {
            Log.e(TAG, "get poi failed", it)
            refreshRecyclerView.showError(R.drawable.ui_ic_oops_illegal, R.string.location_failed)
        })
    }
}

class PoiAdapter : BaseQuickAdapter<PoiItem, BaseViewHolder>(R.layout.view_item_poi, null) {
    override fun convert(helper: BaseViewHolder, item: PoiItem) {
        helper.itemView.poiTitle.text = item.title
        helper.itemView.poiSubTitle.text = item.snippet
    }
}