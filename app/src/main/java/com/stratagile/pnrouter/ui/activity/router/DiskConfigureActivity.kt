package com.stratagile.pnrouter.ui.activity.router

import android.os.Bundle
import android.view.View
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerDiskConfigureComponent
import com.stratagile.pnrouter.ui.activity.router.contract.DiskConfigureContract
import com.stratagile.pnrouter.ui.activity.router.module.DiskConfigureModule
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskConfigurePresenter
import kotlinx.android.synthetic.main.activity_disk_configure.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/01/28 14:51:22
 */

class DiskConfigureActivity : BaseActivity(), DiskConfigureContract.View {

    @Inject
    internal lateinit var mPresenter: DiskConfigurePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_disk_configure)
        checkRaid1.visibility = View.VISIBLE
        checkBasic.visibility = View.INVISIBLE
        checkRaid0.visibility = View.INVISIBLE
        checkLvm.visibility = View.INVISIBLE
        checkAddToLvm.visibility = View.INVISIBLE
        checkAddToRaid1.visibility = View.INVISIBLE
        ivRaid1.setOnClickListener {
            if (detailRaid1.visibility == View.VISIBLE) {
                detailRaid1.visibility = View.GONE
                ivRaid1.setImageDrawable(resources.getDrawable(R.mipmap.arrow_down))
            } else {
                detailRaid1.visibility = View.VISIBLE
                ivRaid1.setImageDrawable(resources.getDrawable(R.mipmap.arrow_upper))
            }
        }
        ivBasic.setOnClickListener {
            if (detailBasic.visibility == View.VISIBLE) {
                detailBasic.visibility = View.GONE
            } else {
                detailBasic.visibility = View.VISIBLE
            }
        }
        ivRaid0.setOnClickListener {
            if (detailRaid0.visibility == View.VISIBLE) {
                detailRaid0.visibility = View.GONE
            } else {
                detailRaid0.visibility = View.VISIBLE
            }
        }
        ivLvm.setOnClickListener {
            if (detailLvm.visibility == View.VISIBLE) {
                detailLvm.visibility = View.GONE
            } else {
                detailLvm.visibility = View.VISIBLE
            }
        }
    }
    override fun initData() {
        title.text = "Configuration Disk"
    }

    override fun setupActivityComponent() {
       DaggerDiskConfigureComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .diskConfigureModule(DiskConfigureModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: DiskConfigureContract.DiskConfigureContractPresenter) {
            mPresenter = presenter as DiskConfigurePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}