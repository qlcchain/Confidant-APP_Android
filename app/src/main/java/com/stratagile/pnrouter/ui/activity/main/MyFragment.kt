package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.main.component.DaggerMyComponent
import com.stratagile.pnrouter.ui.activity.main.contract.MyContract
import com.stratagile.pnrouter.ui.activity.main.module.MyModule
import com.stratagile.pnrouter.ui.activity.main.presenter.MyPresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.events.EditNickName
import com.stratagile.pnrouter.entity.events.ResetAvatar
import com.stratagile.pnrouter.ui.activity.user.MyDetailActivity
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.fragment_my.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 17:34:05
 */

class MyFragment : BaseFragment(), MyContract.View {

    @Inject
    lateinit internal var mPresenter: MyPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_my, null);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        toDetail.setOnClickListener {
            startActivity(Intent(activity, MyDetailActivity::class.java))
        }
        nickName.text = SpUtil.getString(activity!!, ConstantValue.username, "")
        avatar.setText(SpUtil.getString(activity!!, ConstantValue.username, "")!!)
        avatar.setImageFile(SpUtil.getString(activity!!, ConstantValue.selfImageName, "")!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNikNameChange(editnickName : EditNickName) {
        nickName.text = SpUtil.getString(activity!!, ConstantValue.username, "")
        avatar.setText(SpUtil.getString(activity!!, ConstantValue.username, "")!!)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun reSetAvatar(resetAvatar: ResetAvatar) {
        nickName.text = SpUtil.getString(activity!!, ConstantValue.username, "")
        avatar.setText(SpUtil.getString(activity!!, ConstantValue.username, "")!!)
        avatar.setImageFile(SpUtil.getString(activity!!, ConstantValue.selfImageName, "")!!)
    }


    override fun setupFragmentComponent() {
        DaggerMyComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .myModule(MyModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: MyContract.MyContractPresenter) {
        mPresenter = presenter as MyPresenter
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