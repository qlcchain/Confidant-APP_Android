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
import com.stratagile.pnrouter.entity.events.SelectFriendChange
import com.stratagile.pnrouter.ui.activity.main.ContactFragment
import com.stratagile.pnrouter.ui.activity.selectfriend.component.DaggerSelectFriendCreateGroupComponent
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.SelectFriendCreateGroupContract
import com.stratagile.pnrouter.ui.activity.selectfriend.module.SelectFriendCreateGroupModule
import com.stratagile.pnrouter.ui.activity.selectfriend.presenter.SelectFriendCreateGroupPresenter
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
 * @date 2019/03/12 17:49:51
 */

class SelectFriendCreateGroupActivity : BaseActivity(), SelectFriendCreateGroupContract.View {

    @Inject
    internal lateinit var mPresenter: SelectFriendCreateGroupPresenter

    var fragment: ContactFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_select_friend_create_group)
        EventBus.getDefault().register(this)
        setToorBar(false)
        tvTitle.text = "Add Group Members"
        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        statusBar.setLayoutParams(llp)
    }
    override fun initData() {
        fragment = ContactFragment();
        val bundle = Bundle()
        bundle.putString(ConstantValue.selectFriend, "select")
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
        var list = intent.getParcelableArrayListExtra<UserEntity>("person")
        var toAddList = arrayListOf<UserEntity>()
        list.forEach {
            if ("1".equals(it.userId) || "0".equals(it.userId)) {

            } else {
                toAddList.add(it)
            }
        }
        fragment!!.setSelectedPerson(toAddList)
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

    override fun setupActivityComponent() {
       DaggerSelectFriendCreateGroupComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .selectFriendCreateGroupModule(SelectFriendCreateGroupModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SelectFriendCreateGroupContract.SelectFriendCreateGroupContractPresenter) {
            mPresenter = presenter as SelectFriendCreateGroupPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}