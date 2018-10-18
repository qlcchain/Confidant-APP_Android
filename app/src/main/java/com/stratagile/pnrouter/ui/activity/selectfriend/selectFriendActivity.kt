package com.stratagile.pnrouter.ui.activity.selectfriend

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.widget.LinearLayout
import butterknife.ButterKnife
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.events.SelectFriendChange
import com.stratagile.pnrouter.ui.activity.main.ContactFragment
import com.stratagile.pnrouter.ui.activity.selectfriend.component.DaggerselectFriendComponent
import com.stratagile.pnrouter.ui.activity.selectfriend.contract.selectFriendContract
import com.stratagile.pnrouter.ui.activity.selectfriend.module.selectFriendModule
import com.stratagile.pnrouter.ui.activity.selectfriend.presenter.selectFriendPresenter
import com.stratagile.pnrouter.utils.UIUtils
import kotlinx.android.synthetic.main.activity_select_friend.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject



/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.selectfriend
 * @Description: $description
 * @date 2018/09/25 14:58:33
 */

class selectFriendActivity : BaseActivity(), selectFriendContract.View {

    @Inject
    internal lateinit var mPresenter: selectFriendPresenter
    var fragment: ContactFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        //        setContentView(R.layout.activity_selectFriend);
        ButterKnife.bind(this)
        setToorBar(false)
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_select_friend)
        tvTitle.text = getString(R.string.Contacts)
        val llp = LinearLayout.LayoutParams(UIUtils.getDisplayWidth(this), UIUtils.getStatusBarHeight(this))
        statusBar.setLayoutParams(llp)
    }

    override fun initData() {
        EventBus.getDefault().register(this)
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

           finish();
        }
        send.setOnClickListener {
            showSendDialog()
        }
    }

    /**
     * 显示转发的弹窗
     */
    fun showSendDialog() {

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun selectFriendChange(selectFriendChange: SelectFriendChange) {
        selectTxt.text = getString(R.string.selected) +" "+ selectFriendChange.friendNum  +" "+ getString(R.string.people)
    }
    override fun setupActivityComponent() {
        DaggerselectFriendComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .selectFriendModule(selectFriendModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: selectFriendContract.selectFriendContractPresenter) {
        mPresenter = presenter as selectFriendPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}