package com.dokiwa.dokidoki.profile.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.dokiwa.dokidoki.center.base.activity.BaseActivity
import com.dokiwa.dokidoki.center.ext.toast
import com.dokiwa.dokidoki.profile.Log
import com.dokiwa.dokidoki.profile.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_search_user.*
import java.util.concurrent.TimeUnit

private const val TAG = "SearchUserActivity"

class SearchUserActivity : BaseActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, SearchUserActivity::class.java))
            // context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)
        toast("to search page")

        Observable.create<String> { source ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    source.onNext(s.toString())
                }
            })
        }.debounce(500, TimeUnit.MILLISECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it) {
                    "1" -> refreshRecyclerView.showLoading()
                    "2" -> refreshRecyclerView.showSuccess()
                    "3" -> refreshRecyclerView.showError(
                        R.drawable.ic_gender_female,
                        R.string.search_user_error_network
                    )
                }
            }, {
                Log.e(TAG, "edit text observer error", it)
            }).also {
                addDispose(it)
            }
    }

    fun onSearchCancelClick(view: View) {
        onBackPressed()
    }

    fun doSearch() {

    }
}