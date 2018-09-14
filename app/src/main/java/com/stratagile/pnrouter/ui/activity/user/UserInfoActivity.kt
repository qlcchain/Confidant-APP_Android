package com.stratagile.pnrouter.ui.activity.user

import android.content.Intent
import android.os.Bundle
import com.hyphenate.easeui.EaseConstant
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import com.stratagile.pnrouter.ui.activity.conversation.ConversationActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerUserInfoComponent
import com.stratagile.pnrouter.ui.activity.user.contract.UserInfoContract
import com.stratagile.pnrouter.ui.activity.user.module.UserInfoModule
import com.stratagile.pnrouter.ui.activity.user.presenter.UserInfoPresenter
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.activity_user_info.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/13 22:03:00
 */

class UserInfoActivity : BaseActivity(), UserInfoContract.View {

    @Inject
    internal lateinit var mPresenter: UserInfoPresenter

    var userInfo : UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_user_info)
    }
    override fun initData() {
        title.text = getString(R.string.details)
        userInfo = intent.getParcelableExtra("user")
        nickName.text = userInfo!!.nickName
        avatar.setText(userInfo!!.nickName)
        tvRefuse.setOnClickListener {
            if (userInfo!!.friendStatus == 0) {
                deleteFriend()
            } else if (userInfo!!.friendStatus == 3) {
                refuseFriend()
            }
        }

        tvAccept.setOnClickListener {
            if (userInfo!!.friendStatus == 0) {
                //send message
                /*var intent = Intent(this, ConversationActivity::class.java)
                intent.putExtra("user", userInfo!!)
                startActivity(intent)*/
                startActivity(Intent(this@UserInfoActivity, ChatActivity::class.java).putExtra(EaseConstant.EXTRA_USER_ID, userInfo!!))
            } else if (userInfo!!.friendStatus == 3) {
                acceptFriend()
            }
        }


        when (userInfo!!.friendStatus) {
            //好友状态， 0 好友， 1 等待对方同意，2 对方决绝， 3 等待我同意， 4 对方删除我， 5 我拒绝， 6 我删除对方
            0-> {
                tvRefuse.text = "Delete Friend"
                tvAccept.text = "Send a message"
            }
            1-> {

            }
            2-> {

            }
            3-> {
                tvRefuse.text = getString(R.string.refuse)
                tvAccept.text = getString(R.string.accept)
            }
            4-> {

            }
            5-> {

            }
            6-> {

            }
        }

    }

    /**
     * 删除好友
     */
    fun deleteFriend() {

    }

    /**
     * 同意添加好友
     */
    fun acceptFriend() {

    }

    fun refuseFriend() {

    }

    override fun setupActivityComponent() {
       DaggerUserInfoComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .userInfoModule(UserInfoModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: UserInfoContract.UserInfoContractPresenter) {
            mPresenter = presenter as UserInfoPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}