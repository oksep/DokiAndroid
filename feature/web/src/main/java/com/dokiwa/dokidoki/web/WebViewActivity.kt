package com.dokiwa.dokidoki.web

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.IdRes
import com.dokiwa.dokidoki.center.base.activity.TranslucentActivity
import com.dokiwa.dokidoki.ui.view.AppToolBar

class WebViewActivity : TranslucentActivity() {


    private fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
        return lazy { findViewById<T>(res) }
    }

    private val mNoDataView: NoDataSuit by bind(R.id.nodata_suit)
    private val mProgress: ProgressBar by bind(R.id.progress_bar)
    private val mWebView: WebView by lazy { chooseWebView() }
    private val mNavigationBar: AppToolBar by bind(R.id.toolBar)
    private val mHandler = Handler()

    private val mWebChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            result.confirm()
            return true
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            mProgress.progress = newProgress
            if (newProgress == 100) {
                mHandler.postDelayed({ mProgress.visibility = View.GONE }, 100)
            }
        }
    }

    private val mWebViewClient: WebViewClient = object : WebViewClient() {

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            if (!isFinishing) {
                mNoDataView.visibility = View.VISIBLE
                tryAgain()
            }
        }
    }

    companion object {

        const val EXTRA_URL = "url"
        const val EXTRA_TITLE = "title"
        const val EXTRA_WITHOUT_CACHE = "extra_without_cache"

        fun launch(
            context: Context,
            url: String,
            title: String = "",
            withoutCache: Boolean = false,
            clearTop: Boolean = false
        ) {
            val intent = Intent()
            intent.putExtra(EXTRA_URL, url)
            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_WITHOUT_CACHE, withoutCache)
            intent.setClass(context, WebViewActivity::class.java)
            if (clearTop) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
        }

        fun launch(activity: Activity, url: String, requestCode: Int) {
            val intent = Intent()
            intent.putExtra(EXTRA_URL, url)
            intent.setClass(activity, WebViewActivity::class.java)
            activity.startActivityForResult(intent, requestCode)
        }

        fun getIntent(activity: Activity, url: String): Intent {
            val intent = Intent()
            intent.putExtra(EXTRA_URL, url)
            intent.setClass(activity, WebViewActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        mNavigationBar.title.text = intent?.getStringExtra(EXTRA_TITLE)
        mNavigationBar.leftIconView.setOnClickListener { onBackPressed() }

        val url = intent?.getStringExtra(EXTRA_URL) ?: ""

        mNoDataView.visibility = View.GONE

        setupDownloadListener()

        findViewById<FrameLayout>(R.id.webview_wrapper).addView(
            mWebView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        setDefaultSetting(mWebView)

        mWebView.settings.javaScriptCanOpenWindowsAutomatically = true

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        mWebView.loadUrl(url)

        if (intent.getBooleanExtra(EXTRA_WITHOUT_CACHE, false)) {
            mWebView.settings.setAppCacheEnabled(false)
            mWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun setupDownloadListener() {
        mWebView.setDownloadListener { url, _, _, _, _ ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
            finish()
        }
    }

    private fun chooseWebView(): WebView {
        val webView = WebView(this)
        webView.webChromeClient = mWebChromeClient
        webView.webViewClient = mWebViewClient
        return webView
    }

    private fun tryAgain() {
        val builder = AlertDialog.Builder(this)
            .setTitle("获取数据失败，请稍后重试!")
            .setPositiveButton("取消") { _, _ -> finish() }
            .setNegativeButton("重试") { _, _ ->
                mNoDataView.visibility = View.GONE
                mWebView.reload()
            }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }
}

fun setDefaultSetting(webView: WebView) {
    val setting = webView.settings
    setting.cacheMode = WebSettings.LOAD_DEFAULT
    setting.setSupportZoom(true)
    setting.domStorageEnabled = true
    setting.useWideViewPort = true
    setting.loadWithOverviewMode = true
    setting.builtInZoomControls = false
    setting.pluginState = WebSettings.PluginState.ON_DEMAND
    setting.mediaPlaybackRequiresUserGesture = false
    setting.textZoom = 100
}