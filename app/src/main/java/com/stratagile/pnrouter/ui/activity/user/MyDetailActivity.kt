package com.stratagile.pnrouter.ui.activity.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.pawegio.kandroid.startActivity
import com.pawegio.kandroid.startActivityForResult
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.user.component.DaggerMyDetailComponent
import com.stratagile.pnrouter.ui.activity.user.contract.MyDetailContract
import com.stratagile.pnrouter.ui.activity.user.module.MyDetailModule
import com.stratagile.pnrouter.ui.activity.user.presenter.MyDetailPresenter
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_my_detail.*
import kotlinx.android.synthetic.main.fragment_my.view.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/11 11:06:30
 */

class MyDetailActivity : BaseActivity(), MyDetailContract.View {

    @Inject
    internal lateinit var mPresenter: MyDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_my_detail)
    }
    override fun initData() {
        title.text = getString(R.string.details)
        qrCode.setOnClickListener {
            startActivity(Intent(this, QRCodeActivity::class.java))
        }
        nickName.tvContent.text = SpUtil.getString(this, ConstantValue.username, "")
        ivAvatar.setText(SpUtil.getString(this, ConstantValue.username, ""))
        nickName.setOnClickListener {
            startActivityForResult(Intent(this, EditNickNameActivity::class.java), 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        initData()
    }

    override fun setupActivityComponent() {
       DaggerMyDetailComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .myDetailModule(MyDetailModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: MyDetailContract.MyDetailContractPresenter) {
            mPresenter = presenter as MyDetailPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}