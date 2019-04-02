package com.stratagile.pnrouter.ui.activity.main

import android.os.Bundle
import android.view.View
import android.webkit.*
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerWebViewComponent
import com.stratagile.pnrouter.ui.activity.main.contract.WebViewContract
import com.stratagile.pnrouter.ui.activity.main.module.WebViewModule
import com.stratagile.pnrouter.ui.activity.main.presenter.WebViewPresenter
import kotlinx.android.synthetic.main.activity_web_view.*

import javax.inject.Inject;
import com.stratagile.pnrouter.R.id.webView
import com.stratagile.pnrouter.R.id.tvTitle





/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2019/04/01 18:08:04
 */

class WebViewActivity : BaseActivity(), WebViewContract.View {

    @Inject
    internal lateinit var mPresenter: WebViewPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var url = ""

    override fun initView() {
        setContentView(R.layout.activity_web_view)
    }
    override fun initData() {
        var url = intent.getStringExtra("url")
        KLog.i(url)
        this.url = url
        var titleStr = intent.getStringExtra("title")
        title.text = titleStr
        webView.getSettings().setBuiltInZoomControls(true)
        webView.getSettings().setDefaultFontSize(16)
        webView.getSettings().setDisplayZoomControls(false)
        webView.getSettings().setSupportZoom(true)
        webView.getSettings().setLoadWithOverviewMode(true)
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true)
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)
        webView.getSettings().setDefaultTextEncodingName("UTF-8")
        webView.getSettings().setJavaScriptEnabled(true)
        webView.getSettings().setDomStorageEnabled(true)
        webView.getSettings().setDomStorageEnabled(true)
        webView.getSettings().setAllowContentAccess(true)
        webView.getSettings().setAppCacheEnabled(false)
        webView.getSettings().setUseWideViewPort(true)
        webView.getSettings().setLoadWithOverviewMode(true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title1: String?) {
                super.onReceivedTitle(view, title1)
                if (title1 != null) {
                    title.text = title1
                }
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                }
                super.onProgressChanged(view, newProgress)
            }

        }
        webView.webViewClient = object  : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, url)
            }
        }
        webView.loadUrl(url)

    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            if (webView.url.equals(url)) {
                super.onBackPressed()
            } else {
                webView.goBack()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun setupActivityComponent() {
       DaggerWebViewComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .webViewModule(WebViewModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: WebViewContract.WebViewContractPresenter) {
            mPresenter = presenter as WebViewPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}