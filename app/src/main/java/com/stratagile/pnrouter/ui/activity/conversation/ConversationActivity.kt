package com.stratagile.pnrouter.ui.activity.conversation

import android.graphics.Rect
import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.conversation.component.DaggerConversationComponent
import com.stratagile.pnrouter.ui.activity.conversation.contract.ConversationContract
import com.stratagile.pnrouter.ui.activity.conversation.module.ConversationModule
import com.stratagile.pnrouter.ui.activity.conversation.presenter.ConversationPresenter

import javax.inject.Inject;
import android.opengl.ETC1.getHeight
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.socks.library.KLog
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.utils.UIUtils
import kotlinx.android.synthetic.main.activity_conversation.*


/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: $description
 * @date 2018/09/13 16:38:48
 */

class ConversationActivity : BaseActivity(), ConversationContract.View {

    @Inject
    internal lateinit var mPresenter: ConversationPresenter

    var keyboardHeight = 0

    var userEntity : UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_conversation)
    }
    override fun initData() {
        userEntity = intent.getParcelableExtra("user")
        title.text = userEntity?.nickName
        var myLayout = getWindow().getDecorView();
        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            private var statusBarHeight: Int = 0
            override fun onGlobalLayout() {
                val r = Rect()
                // 使用最外层布局填充，进行测算计算
                parentLayout.getWindowVisibleDisplayFrame(r)
                val screenHeight = myLayout.getRootView().getHeight()
                val heightDiff = screenHeight - (r.bottom - r.top)
                if (heightDiff > 100) {
                    // 如果超过100个像素，它可能是一个键盘。获取状态栏的高度
                    statusBarHeight = 0
                }
                try {
                    val c = Class.forName("com.android.internal.R\$dimen")
                    val obj = c.newInstance()
                    val field = c.getField("status_bar_height")
                    val x = Integer.parseInt(field.get(obj).toString())
                    statusBarHeight = getResources().getDimensionPixelSize(x)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val realKeyboardHeight = heightDiff - statusBarHeight
                KLog.e("keyboard height(单位像素) = $realKeyboardHeight")
                if (keyboardHeight == 0) {
                    keyboardHeight = realKeyboardHeight
                } else {
                    option.visibility = View.GONE
                }
            }
        })

        iv_more.setOnClickListener {
            option.layoutParams = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), keyboardHeight)
            option.visibility = View.VISIBLE
        }

    }

    override fun setupActivityComponent() {
       DaggerConversationComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .conversationModule(ConversationModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: ConversationContract.ConversationContractPresenter) {
            mPresenter = presenter as ConversationPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun closeKeyboad() {
        option.visibility = View.GONE
    }

}