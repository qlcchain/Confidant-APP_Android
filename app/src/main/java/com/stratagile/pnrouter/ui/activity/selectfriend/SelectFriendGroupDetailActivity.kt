package com.stratagile.pnrouter.ui.activity.selectfriend

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.widget.LinearLayout
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.entity.JGroupUserPullRsp
import com.stratagile.pnrouter.entity.events.SelectFriendChange
import com.stratagile.pnrouter.ui.activity.main.ContactFragment
import com.stratagile.pnrouter.ui.activity.selectfriend.component.DaggerSelectFriendGroupDetailComponent
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.SelectFriendGroupDetailContract
import com.stratagile.pnrouter.ui.activity.selectfriend.module.SelectFriendGroupDetailModule
import com.stratagile.pnrouter.ui.activity.selectfriend.presenter.SelectFriendGroupDetailPresenter
import com.stratagile.pnrouter.utils.UIUtils
import kotlinx.android.synthetic.main.activity_select_friend.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: $description
 * @date 2019/03/21 10:15:49
 */

class SelectFriendGroupDetailActivity : BaseActivity(), SelectFriendGroupDetailContract.View {

    @Inject
    internal lateinit var mPresenter: SelectFriendGroupDetailPresenter

    var fragment: ContactFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_select_friend_create_group)
    }

    override fun initData() {
        EventBus.getDefault().register(this)
        setToorBar(false)
        tvTitle.text = "Add Group Members"
        fragment = ContactFragment();
        val bundle = Bundle()
        bundle.putString(ConstantValue.selectFriend, "select")
        bundle.putString("routerId", ConstantValue.currentRouterId)
        fragment!!.setArguments(bundle)
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragment!!
            }

            override fun getCount(): Int {
                return 1
            }
        }
        viewPager.offscreenPageLimit = 1
        llCancel.setOnClickListener {
            onBackPressed()
        }
        send.setOnClickListener {
            var contactSelectedList: ArrayList<UserEntity> = fragment!!.getAllSelectedFriend()
            if (contactSelectedList.size == 0) {
                toast(R.string.noSelected)
            } else {
                var intent = Intent()
                intent.putParcelableArrayListExtra("person", contactSelectedList)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        multiSelectBtn.setOnClickListener {

            fragment!!.selectOrCancelAll()
        }
        fragment!!.setRefreshEnable(false)
        var list = intent.getParcelableArrayListExtra<JGroupUserPullRsp.ParamsBean.PayloadBean>("person")
        var toAddList = arrayListOf<UserEntity>()
        list.forEach {
            if ("1".equals(it.toxId) || "0".equals(it.toxId)) {

            } else {
                var user = UserEntity()
                user.userId = it.toxId
                user.signPublicKey = it.userKey
                user.nickName = it.nickname
                toAddList.add(user)
            }
        }
        fragment!!.setUnShowPerson(toAddList)
    }

    override fun setupActivityComponent() {
        DaggerSelectFriendGroupDetailComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .selectFriendGroupDetailModule(SelectFriendGroupDetailModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: SelectFriendGroupDetailContract.SelectFriendGroupDetailContractPresenter) {
        mPresenter = presenter as SelectFriendGroupDetailPresenter
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun selectFriendChange(selectFriendChange: SelectFriendChange) {
        if (selectFriendChange.friendNum > 1) {
            selectTxt.text = getString(R.string.selected) +" "+ selectFriendChange.friendNum  +" "+ getString(R.string.person) + "s"
        } else {
            selectTxt.text = getString(R.string.selected) +" "+ selectFriendChange.friendNum  +" "+ getString(R.string.person)
        }
        if (selectFriendChange.friendNum == 0) {
            send.text = "Confirm"
        } else {
            send.text = "Confirm (" + selectFriendChange.friendNum + ")"
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}