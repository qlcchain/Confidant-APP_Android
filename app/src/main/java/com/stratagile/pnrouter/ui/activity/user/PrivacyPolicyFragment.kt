package com.stratagile.pnrouter.ui.activity.user

import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.user.component.DaggerPrivacyPolicyComponent
import com.stratagile.pnrouter.ui.activity.user.contract.PrivacyPolicyContract
import com.stratagile.pnrouter.ui.activity.user.module.PrivacyPolicyModule
import com.stratagile.pnrouter.ui.activity.user.presenter.PrivacyPolicyPresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.message.UserProvider
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import kotlinx.android.synthetic.main.activity_web_view.*

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/04/22 18:24:47
 */

class PrivacyPolicyFragment : BaseFragment(), PrivacyPolicyContract.View {

    @Inject
    lateinit internal var mPresenter: PrivacyPolicyPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.activity_web_view, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    fun initData() {
        var url = "file:///android_asset/privacypolicy.html"
        KLog.i(url)
        val webSettings = webView.getSettings()
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)//加载缓存否则网络
        }

        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true)//图片自动缩放 打开
        } else {
            webSettings.setLoadsImagesAutomatically(false)//图片自动缩放 关闭
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)//软件解码
        }
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)//硬件解码
        webSettings.javaScriptEnabled = true // 设置支持javascript脚本
//        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setSupportZoom(true)// 设置可以支持缩放
        webSettings.builtInZoomControls = true// 设置出现缩放工具 是否使用WebView内置的缩放组件，由浮动在窗口上的缩放控制和手势缩放控制组成，默认false

        webSettings.displayZoomControls = false//隐藏缩放工具
        webSettings.useWideViewPort = true// 扩大比例的缩放

        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN//自适应屏幕
        webSettings.loadWithOverviewMode = true

        webSettings.databaseEnabled = true//
        webSettings.savePassword = true//保存密码
        webSettings.domStorageEnabled = true//是否开启本地DOM存储  鉴于它的安全特性（任何人都能读取到它，尽管有相应的限制，将敏感数据存储在这里依然不是明智之举），Android 默认是关闭该功能的。

        webView.setSaveEnabled(true)
        webView.setKeepScreenOn(true)


        webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title1: String?) {
                super.onReceivedTitle(view, title1)
                if (title1 != null) {

                }
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    KLog.i("进度：" + newProgress)
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                }
                super.onProgressChanged(view, newProgress)
            }

        }
        webView.webViewClient = object  : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return false
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError) {
                if (error.getPrimaryError() == SslError.SSL_DATE_INVALID
                        || error.getPrimaryError() == SslError.SSL_EXPIRED
                        || error.getPrimaryError() == SslError.SSL_INVALID
                        || error.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                    handler.proceed();
                } else {
                    handler.cancel();
                }
                super.onReceivedSslError(view, handler, error)
            }

            override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
                KLog.i("ddddddd")
                super.onReceivedHttpError(view, request, errorResponse)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                KLog.i("ddddddd")
                super.onReceivedError(view, request, error)
            }
        }
        webView.loadUrl(url)
    }
    override fun setupFragmentComponent() {
        DaggerPrivacyPolicyComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .privacyPolicyModule(PrivacyPolicyModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: PrivacyPolicyContract.PrivacyPolicyContractPresenter) {
        mPresenter = presenter as PrivacyPolicyPresenter
    }

    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
}