package com.dokiwa.dokidoki.center.base.activity

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.dokiwa.dokidoki.center.base.CompositeDisposableContext
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Septenary on 2018/11/12.
 */
open class BaseActivity : AppCompatActivity(), CompositeDisposableContext {

    /////////////////////////////////////////////
    // rx dispose
    /////////////////////////////////////////////
    private var disposableContainer: CompositeDisposable? = null

    override fun addDispose(dispose: Disposable) {
        if (disposableContainer == null) {
            disposableContainer = CompositeDisposable()
        }
        disposableContainer?.add(dispose)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableContainer?.dispose()
    }

    /////////////////////////////////////////////
    // system UI
    /////////////////////////////////////////////
    protected fun translucentStatusBar() {
        fun setWindowFlag(bits: Int, on: Boolean) {
            val win = window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT
    }

    protected fun changeStatusBarDark() {
        var flag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flag = flag or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.decorView.systemUiVisibility = flag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
}