package com.stratagile.pnrouter.ui.activity.main

import android.os.Bundle
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.main.component.DaggerEncryptMsgComponent
import com.stratagile.pnrouter.ui.activity.main.contract.EncryptMsgContract
import com.stratagile.pnrouter.ui.activity.main.module.EncryptMsgModule
import com.stratagile.pnrouter.ui.activity.main.presenter.EncryptMsgPresenter
import kotlinx.android.synthetic.main.encrypt_reg_activity.*

import javax.inject.Inject;
import qlc.rpc.impl.*
import qlc.network.QlcClient



/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2020/02/19 16:13:12
 */

class EncryptMsgActivity : BaseActivity(), EncryptMsgContract.View {

    @Inject
    internal lateinit var mPresenter: EncryptMsgPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.encrypt_reg_activity)
    }
    override fun initData() {

       title.text = "Encrypt And Decrypt"
        regeditBtn.setOnClickListener {
            val qlcClient = QlcClient(ConstantValue.qlcNode)
            val rpc = DpkiRpc(qlcClient)
            Thread(Runnable() {
                run() {
                    var getAllVerifiersResult =  rpc.getAllVerifiers(null)
                    var aa = ""
                }
            }).start()


        }
        decryptBtn.setOnClickListener {

        }
        encryptBtn.setOnClickListener {

        }
    }

    override fun setupActivityComponent() {
        DaggerEncryptMsgComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .encryptMsgModule(EncryptMsgModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: EncryptMsgContract.EncryptMsgContractPresenter) {
        mPresenter = presenter as EncryptMsgPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}