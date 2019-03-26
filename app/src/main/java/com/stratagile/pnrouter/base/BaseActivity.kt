package com.stratagile.pnrouter.base

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.hyphenate.easeui.EaseUI
import com.jaeger.library.StatusBarUtil
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.ui.activity.main.SplashActivity
import com.stratagile.pnrouter.utils.LogUtil
import com.stratagile.pnrouter.utils.UIUtils
import com.stratagile.pnrouter.utils.swipeback.BGASwipeBackHelper
import com.stratagile.pnrouter.view.RxDialogLoading

/**
 * 作者：Android on 2017/8/1
 * 邮箱：365941593@qq.com
 * 描述：
 */

abstract class BaseActivity : AppCompatActivity(), ActivityDelegate {

    var toolbar: Toolbar? = null
    var needFront = false   //toolBar 是否需要显示在最上面的返还标题栏 true 不显示
    var rootLayout: RelativeLayout? = null
    lateinit var relativeLayout_root: RelativeLayout
    lateinit var view: View
    lateinit var progressDialog: RxDialogLoading
    lateinit var title: TextView
    val point = Point()

    var inputMethodManager: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
//        initSwipeBackFinish()
        super.onCreate(savedInstanceState)
        // 这句很关键，注意是调用父类的方法
        super.setContentView(R.layout.activity_base)
        StatusBarUtil.setColor(this, resources.getColor(R.color.white), 0)
//        StatusBarUtil.setTranslucent(this, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//设置状态栏黑色字体
        }
        window.navigationBarColor = resources.getColor(R.color.white)
        AppConfig.instance.mAppActivityManager.addActivity(this)
        if (!isTaskRoot) {
            val intent = intent
            val action = intent.action
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action == Intent.ACTION_MAIN) {
                finish()
                return
            }
        }
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        initToolbar()
        setupActivityComponent()
        if (savedInstanceState != null) {
            KLog.i("保存的东西不为空," + savedInstanceState.getString("baseSave"))
            LogUtil.addLog("保存的东西不为空," + savedInstanceState.getString("baseSave"))
            //这里走重新启动app的流程。
            if ("base".equals(savedInstanceState.getString("baseSave"))) {
                AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
                val intent = Intent(this, SplashActivity::class.java)
                intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }

        initView()
        initData()
    }

    fun exitToastBase() {
        AppConfig.instance.stopAllService()
        //android进程完美退出方法。
        var intent = Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
        this.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        //System.runFinalizersOnExit(true);
        System.exit(0);
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("baseSave", "base")

    }

    override fun onDestroy() {
        AppConfig.instance.mAppActivityManager.removeActivity(this)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        // cancel the notification
        EaseUI.getInstance().notifier.reset()
    }

    protected fun hideSoftKeyboard() {
        if (currentFocus != null)
            inputMethodManager?.hideSoftInputFromWindow(currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun setContentView(layoutId: Int) {
        setContentView(View.inflate(this, layoutId, null))
    }

    override fun setTitle(title: CharSequence?) {
        this.title.text = title.toString()
    }

    override fun setContentView(view: View) {
        rootLayout = findViewById(R.id.root_layout)
        relativeLayout_root = findViewById(R.id.root_rl)
        progressDialog = RxDialogLoading(this)
        progressDialog.setmDialogColor(resources.getColor(R.color.mainColor))
        progressDialog.setDialogText(resources.getString(R.string.apploading))
        if (rootLayout == null) {
            return
        }
        if (needFront) {
            toolbar?.setBackgroundColor(resources.getColor(R.color.color_00000000))
            relativeLayout_root.setBackgroundColor(resources.getColor(R.color.color_00000000))
            val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            rootLayout?.addView(view, params)
            toolbar?.bringToFront()
            toolbar?.setVisibility(View.GONE)
        } else {
            val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            params.addRule(RelativeLayout.BELOW, R.id.root_rl)
            rootLayout?.addView(view, params)
            initToolbar()
        }
    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        title = toolbar?.findViewById(R.id.title)!!
        relativeLayout_root = findViewById<View>(R.id.root_rl) as RelativeLayout
        view = findViewById(R.id.view)
        view.setLayoutParams(RelativeLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this) as Int))
        //        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.dip2px(getResources().getDimension(R.dimen.dp_69), this) - (UIUtils.getStatusBarHeight(this)));
        //        toolbar.setLayoutParams(rlp);
        toolbar?.setTitle("")
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }
    }

    fun showView() {
        StatusBarUtil.setTransparent(this)
        view.visibility = View.VISIBLE
        relativeLayout_root.setLayoutParams(RelativeLayout.LayoutParams(UIUtils.getDisplayWidth(this), (UIUtils.getStatusBarHeight(this) + resources.getDimension(R.dimen.x84).toInt())))
    }

    fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.height
            val right = left + v.width
            return if (event.x > left && event.x < right
                    && event.y > top && event.y < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                false
            } else {
                true
            }
        }
        return false
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 初始化数据
     */
    protected abstract fun initData()

    /**
     * 初始化view
     */
    protected abstract fun initView()

    /**
     * 初始化dagger2
     */
    protected abstract fun setupActivityComponent()


    override fun destoryContainer() {
        finish()
    }

    override fun getContainerActivity(): BaseActivity {
        return this
    }


    override fun isContainerDead(): Boolean {
        return if (Build.VERSION.SDK_INT > 16) {
            this.isDestroyed
        } else {
            this.isFinishing
        }
    }

    fun setToorBar(isVisitiy: Boolean) {
        if (toolbar != null) {
            if (isVisitiy) {
                toolbar!!.visibility = View.VISIBLE
            } else {
                toolbar!!.visibility = View.GONE
            }
        }
    }

    /*override fun onBackPressed() {
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper.isSliding) {
            return
        }
        mSwipeBackHelper.backward()
    }*/


//    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        if (ev.action == MotionEvent.ACTION_DOWN) {
//            point.x = ev.rawX.toInt()
//            point.y = ev.rawY.toInt()
//            val v = currentFocus
//            if (isShouldHideInput(v, ev)) {
//
//                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm?.hideSoftInputFromWindow(v!!.windowToken, 0)
//                closeKeyboad()
//            }
//            return super.dispatchTouchEvent(ev)
//        }
//
//        // 必不可少，否则所有的组件都不会有TouchEvent了
//        return if (window.superDispatchTouchEvent(ev)) {
//            true
//        } else onTouchEvent(ev)
//    }

    open fun closeKeyboad() {

    }

    fun showProgressDialog(text: String) {
        progressDialog.hide()
        progressDialog.setDialogText(text)
        progressDialog.show()
        progressDialog.setOnTouchOutside(false)
    }

    fun showProgressDialog(text: String, canCancel: Boolean) {
        progressDialog.hide()
        progressDialog.setDialogText(text)
        progressDialog.setNoCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    fun showProgressNoCanelDialog(text: String) {
        progressDialog.hide()
        progressDialog.setDialogText(text)
        progressDialog.show()
        progressDialog.setNoCanceledOnTouchOutside(false)
    }

    fun showProgressDialog(text: String, onKeyListener: DialogInterface.OnKeyListener) {
        progressDialog.hide()
        progressDialog.setDialogText(text)
        progressDialog.show()
        progressDialog.setCanceledOnBack(false, onKeyListener)
    }
}
