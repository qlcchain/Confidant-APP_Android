package com.stratagile.pnrouter.ui.activity.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.HttpData
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerImportAccountComponent
import com.stratagile.pnrouter.ui.activity.user.contract.ImportAccountContract
import com.stratagile.pnrouter.ui.activity.user.module.ImportAccountModule
import com.stratagile.pnrouter.ui.activity.user.presenter.ImportAccountPresenter
import com.stratagile.pnrouter.utils.*
import kotlinx.android.synthetic.main.activity_import_account.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/02/20 14:43:29
 */

class ImportAccountActivity : BaseActivity(), ImportAccountContract.View {

    @Inject
    internal lateinit var mPresenter: ImportAccountPresenter
    val REQUEST_SCAN_QRCODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_import_account)
    }
    override fun initData() {
        title.text = "Log in"
        ivScan.setOnClickListener {
            mPresenter.getScanPermission()
        }
    }
    override fun getScanPermissionSuccess() {
        val intent1 = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent1, REQUEST_SCAN_QRCODE)
    }
    override fun setupActivityComponent() {
       DaggerImportAccountComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .importAccountModule(ImportAccountModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: ImportAccountContract.ImportAccountContractPresenter) {
            mPresenter = presenter as ImportAccountPresenter
        }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var this_ = this
        if (requestCode == REQUEST_SCAN_QRCODE && resultCode == Activity.RESULT_OK) {
            var result = data!!.getStringExtra("result");
            try {
                if(!result.contains("type_3"))
                {
                    toast(R.string.code_error)
                    return
                }
                var type = result.substring(0,6);
                var left = result.substring(7,result.length)
                var signprivatek = left.substring(0,result.indexOf(","))
                left = left.substring(signprivatek.length+1,result.length)
                var usersn = left.substring(0,result.indexOf(","))
                left = left.substring(usersn.length+1,result.length)
                var username = left.substring(0,result.length)


            }catch (e:Exception)
            {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.code_error)
                }
            }

        }else{


        }
    }
    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}