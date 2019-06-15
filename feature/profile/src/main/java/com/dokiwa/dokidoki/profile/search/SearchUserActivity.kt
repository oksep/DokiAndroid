package com.dokiwa.dokidoki.profile.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.center.ext.loadAvatar
import com.dokiwa.dokidoki.center.ext.rx.subscribeApi
import com.dokiwa.dokidoki.center.plugin.model.UserProfile
import com.dokiwa.dokidoki.center.plugin.profile.IProfilePlugin
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import com.dokiwa.dokidoki.profile.api.ProfileApi
import com.dokiwa.dokidoki.ui.util.SimpleTextWatcher
import com.dokiwa.dokidoki.ui.view.RoundImageView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_search_user.*
import java.util.concurrent.TimeUnit

private const val TAG = "SearchUserActivity"

class SearchUserActivity : TranslucentActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, SearchUserActivity::class.java))
        }
    }

    private val adapter by lazy { SearchUserAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarDark()
        setContentView(R.layout.activity_search_user)
        initView()
    }

    private fun initView() {
        Observable.create<String> { source ->
            refreshRecyclerView.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                source.onNext(editText.text.toString())
            })
            editText.addTextChangedListener(object : SimpleTextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    source.onNext(s.toString())
                }
            })
        }.debounce(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                loadData(it)
            }, {
                Log.e(TAG, "edit text observer error", it)
            }).also {
                addDispose(it)
            }
        refreshRecyclerView.setAdapter(adapter)
        adapter.setOnItemClickListener { adapter, _, position ->
            (adapter.getItem(position) as? UserProfile)?.let {
                IProfilePlugin.get().launchProfileActivity(this, it)
            }
        }
    }

    fun onSearchCancelClick(view: View) {
        onBackPressed()
    }

    private fun loadData(keyword: String?) {
        if (keyword.isNullOrEmpty()) {
            refreshRecyclerView.showError(R.drawable.ui_ic_oops_paper, R.string.search_user_empty)
        } else {
            refreshRecyclerView.showLoading()
            Api.get(ProfileApi::class.java).searchUser(keyword).subscribeApi(this, {
                if (it.list.isNullOrEmpty()) {
                    refreshRecyclerView.showError(R.drawable.ui_ic_oops_paper, R.string.search_user_empty)
                } else {
                    refreshRecyclerView.showSuccess()
                    adapter.setNewData(it.list)
                }
            }, {
                Log.e(TAG, "search failed", it)
                refreshRecyclerView.showError(R.drawable.ui_ic_oops_network, R.string.search_user_error)
            })
        }
    }
}

class SearchUserAdapter : BaseQuickAdapter<UserProfile, BaseViewHolder>(R.layout.view_item_search_user, null) {
    override fun convert(helper: BaseViewHolder, profile: UserProfile) {
        // 官方认证
        helper.getView<ImageView>(R.id.officialVerify).visibility =
            if (profile.verify != null) View.VISIBLE else View.GONE

        // 实名认证
        helper.getView<ImageView>(R.id.certifyRealName).visibility =
            if (profile.certification.identification != null) View.VISIBLE else View.GONE

        // 学历认证
        helper.getView<ImageView>(R.id.certifyAcademic).visibility =
            if (profile.certification.education != null) View.VISIBLE else View.GONE

        // 昵称
        helper.getView<TextView>(R.id.name).text = profile.nickname

        // 年龄 | 身高 | 教育程度
        helper.getView<TextView>(R.id.ageHeightEdu).text = profile.assembleAgeHeightEdu(helper.itemView.context)

        // 地点 | 职位
        helper.getView<TextView>(R.id.addressPosition).text = profile.assembleAddressPosition()

        // 头像
        (helper.getView<RoundImageView>(R.id.avatar) as ImageView).loadAvatar(profile)
    }
}