package com.dokiwa.dokidoki

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.os.HandlerCompat
import com.dokiwa.dokidoki.center.activity.BaseActivity
import com.dokiwa.dokidoki.center.api.Api
import com.dokiwa.dokidoki.center.rx.subscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.http.GET
import retrofit2.http.Query

class LaunchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

    }

    fun onBtnClick(view: View) {
        Api.get(MockApi::class.java, "http://192.168.45.136:3000")
            .getUserList(10, 20)
            .subscribeOn(Schedulers.single())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this,
                {
                    Log.d("MockApi", it.toString())
                },
                {
                    Log.e("MockApi", "登录失败", it)
                }
            )
    }

    private fun delayToHome() {
        HandlerCompat.postDelayed(Handler(), {
            HomeActivity.launch(this@LaunchActivity)
            finish()
        }, null, 1000)
    }
}

data class User(val name: String, val address: String)

interface MockApi {
    @GET("/api/list")
    fun getUserList(@Query("from") from: Int, @Query("limit") limit: Int): Observable<List<User>>
}
