package com.stratagile.pnrouter.base

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.hyphenate.easeui.EaseUI
import com.jaeger.library.StatusBarUtil
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.qmui.InnerBaseActivity
import com.stratagile.pnrouter.qmui.QMUISwipeBackActivityManager
import com.stratagile.pnrouter.qmui.SwipeBackLayout
import com.stratagile.pnrouter.qmui.SwipeBackLayout.EDGE_LEFT
import com.stratagile.pnrouter.qmui.SwipeBackgroundView
import com.stratagile.pnrouter.utils.UIUtils
import com.stratagile.pnrouter.utils.swipeback.BGASwipeBackHelper
import com.stratagile.pnrouter.view.RxDialogLoading

/**
 * 作者：Android on 2017/8/1
 * 邮箱：365941593@qq.com
 * 描述：
 */

abstract class BaseActivity : InnerBaseActivity(), ActivityDelegate {

    var toolbar: Toolbar? = null
    var needFront = false   //toolBar 是否需要显示在最上面的返还标题栏 true 不显示
    var rootLayout: RelativeLayout? = null
    lateinit var relativeLayout_root: RelativeLayout
    lateinit var view: View
    lateinit var progressDialog: RxDialogLoading
    lateinit var title: TextView
    val point = Point()

    var inputMethodManager: InputMethodManager? = null

    private var mListenerRemover: SwipeBackLayout.ListenerRemover? = null
    private var mSwipeBackgroundView: SwipeBackgroundView? = null
    private var mIsInSwipeBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 这句很关键，注意是调用父类的方法
        val swipeBackLayout = SwipeBackLayout.wrap(this,
                R.layout.activity_base, dragBackEdge(), mSwipeCallback)
        if (translucentFull()) {
            swipeBackLayout.contentView.fitsSystemWindows = false
        } else {
            swipeBackLayout.contentView.fitsSystemWindows = true
        }
        mListenerRemover = swipeBackLayout.addSwipeListener(mSwipeListener)
        super.setContentView(newSwipeBackLayout(View.inflate(this, R.layout.activity_base, null)))
//        super.setContentView(R.layout.activity_base)
//        if (android.os.Build.MANUFACTURER.toUpperCase() == "MEIZU") {
//            val localLayoutParams = window.attributes
//            localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or localLayoutParams.flags
//        } else {
//        }
        StatusBarUtil.setTransparent(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//设置状态栏黑色字体
        }
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
        initView()
        initData()
    }

    override fun onBackPressed() {
        if (!mIsInSwipeBack) {
            doOnBackPressed()
        }
    }

    override fun onDestroy() {
        AppConfig.instance.mAppActivityManager.removeActivity(this)
        if (mListenerRemover != null) {
            mListenerRemover?.remove()
        }
        if (mSwipeBackgroundView != null) {
            mSwipeBackgroundView?.unBind()
            mSwipeBackgroundView = null
        }
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

    private fun newSwipeBackLayout(view: View): View {
        if (translucentFull()) {
            view.fitsSystemWindows = false
        } else {
            view.fitsSystemWindows = true
        }
        val swipeBackLayout = SwipeBackLayout.wrap(view, dragBackEdge(), mSwipeCallback)
        mListenerRemover = swipeBackLayout.addSwipeListener(mSwipeListener)
        return swipeBackLayout
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

//    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
//        super.setContentView(newSwipeBackLayout(view), params)
//    }

    private fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        title = toolbar?.findViewById(R.id.title)!!
        relativeLayout_root = findViewById<View>(R.id.root_rl) as RelativeLayout
        view = findViewById(R.id.view)
        view.setLayoutParams(RelativeLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this) as Int))
//        if (!SpUtil.getBoolean(this, ConstantValue.isMainNet, false)) {
//            view.setBackgroundColor(resources.getColor(R.color.color_f51818))
//            view.setText(getString(R.string.testnet))
//        }
        //        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.dip2px(getResources().getDimension(R.dimen.dp_69), this) - (UIUtils.getStatusBarHeight(this)));
        //        toolbar.setLayoutParams(rlp);
        toolbar?.setTitle("")
        relativeLayout_root.setLayoutParams(RelativeLayout.LayoutParams(UIUtils.getDisplayWidth(this), (UIUtils.getStatusBarHeight(this) + resources.getDimension(R.dimen.x110).toInt())))
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }
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

    val mSwipeListener = object : SwipeBackLayout.SwipeListener {

        override fun onScrollStateChange(state: Int, scrollPercent: Float) {
            Log.i("", "SwipeListener:onScrollStateChange: state = $state ;scrollPercent = $scrollPercent")
            mIsInSwipeBack = state != SwipeBackLayout.STATE_IDLE
            if (state == SwipeBackLayout.STATE_IDLE) {
                if (mSwipeBackgroundView != null) {
                    if (scrollPercent <= 0.0f) {
                        mSwipeBackgroundView?.unBind()
                        mSwipeBackgroundView = null
                    } else if (scrollPercent >= 1.0f) {
                        // unBind mSwipeBackgroundView until onDestroy
                        finish()
                        val exitAnim = if (mSwipeBackgroundView!!.hasChildWindow()) {
                            R.anim.swipe_back_exit_still
                        } else {
                            R.anim.swipe_back_exit
                        }
                        overridePendingTransition(R.anim.swipe_back_enter, exitAnim)
                    }
                }
            }
        }

        override fun onScroll(edgeFlag: Int, scrollPercent: Float) {
            KLog.i("滑动中...")
            var scrollPercent = scrollPercent
            if (mSwipeBackgroundView != null) {
                KLog.i("滑动中111")
                scrollPercent = Math.max(0f, Math.min(1f, scrollPercent))
                val targetOffset = (Math.abs(backViewInitOffset()) * (1 - scrollPercent)).toInt()
                SwipeBackLayout.offsetInScroll(mSwipeBackgroundView, edgeFlag, targetOffset)
            }
        }

        override fun onEdgeTouch(edgeFlag: Int) {
            Log.i("", "SwipeListener:onEdgeTouch: edgeFlag = $edgeFlag")
            val decorView = window.decorView as ViewGroup
            if (decorView != null) {
                val prevActivity = QMUISwipeBackActivityManager.getInstance()
                        .getPenultimateActivity(this@BaseActivity)
                if (decorView.getChildAt(0) is SwipeBackgroundView) {
                    mSwipeBackgroundView = decorView.getChildAt(0) as SwipeBackgroundView
                } else {
                    mSwipeBackgroundView = SwipeBackgroundView(this@BaseActivity)
                    decorView.addView(mSwipeBackgroundView, 0, FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                }
                mSwipeBackgroundView!!.bind(prevActivity, this@BaseActivity, restoreSubWindowWhenDragBack())
                SwipeBackLayout.offsetInEdgeTouch(mSwipeBackgroundView, edgeFlag,
                        Math.abs(backViewInitOffset()))
            }
        }

        override fun onScrollOverThreshold() {
            Log.i("", "SwipeListener:onEdgeTouch:onScrollOverThreshold")
        }
    }
    val mSwipeCallback = object : SwipeBackLayout.Callback {
        override fun canSwipeBack(): Boolean {
            return QMUISwipeBackActivityManager.getInstance().canSwipeBack() && canDragBack()
        }
    }

    protected fun doOnBackPressed() {
        super.onBackPressed()
    }

    fun isInSwipeBack(): Boolean {
        return mIsInSwipeBack
    }

    /**
     * disable or enable drag back
     *
     * @return
     */
    protected fun canDragBack(): Boolean {
        return true
    }

    /**
     * if enable drag back,
     *
     * @return
     */
    protected fun backViewInitOffset(): Int {
        return 0
    }


    protected fun dragBackEdge(): Int {
        return EDGE_LEFT
    }

    /**
     * Immersive processing
     *
     * @return if true, the area under status bar belongs to content; otherwise it belongs to padding
     */
    protected fun translucentFull(): Boolean {
        return false
    }

    /**
     * restore sub window(e.g dialog) when drag back to previous activity
     * @return
     */
    protected fun restoreSubWindowWhenDragBack(): Boolean {
        return true
    }

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
