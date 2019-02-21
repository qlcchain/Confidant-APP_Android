package com.stratagile.pnrouter.ui.activity.test

import android.os.Bundle
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.test.component.DaggerTestComponent
import com.stratagile.pnrouter.ui.activity.test.contract.TestContract
import com.stratagile.pnrouter.ui.activity.test.module.TestModule
import com.stratagile.pnrouter.ui.activity.test.presenter.TestPresenter
import com.stratagile.pnrouter.ui.adapter.user.ContactAdapter
import com.stratagile.pnrouter.ui.adapter.user.UserHead
import com.stratagile.pnrouter.ui.adapter.user.UserItem
import kotlinx.android.synthetic.main.activity_test.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.test
 * @Description: $description
 * @date 2018/09/05 11:10:38
 */

class TestActivity : BaseActivity(), TestContract.View {

    @Inject
    internal lateinit var mPresenter: TestPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_test)
        setTitle("TestActivity")
//        avi.setIndicator("BallSpinFadeLoaderIndicator")
//        avi.show()
    }
    override fun initData() {
        val list = arrayListOf<MultiItemEntity>()
        for (i in 0..9) {
            var userHead = UserHead()
            userHead.userName = "xx" + i + "yy"
            val list1 = arrayListOf<MultiItemEntity>()
            for (j in 0..3) {
//                userHead.addSubItem(UserItem())
            }
            list.add(userHead)
        }
        var contactAdapter1 = ContactAdapter(list)
        recyclerView.adapter = contactAdapter1
        contactAdapter1.expandAll()
    }


    override fun setupActivityComponent() {
       DaggerTestComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .testModule(TestModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: TestContract.TestContractPresenter) {
            mPresenter = presenter as TestPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}