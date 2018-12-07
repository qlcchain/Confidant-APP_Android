package com.stratagile.pnrouter.ui.activity.router

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.data.service.MessageRetrievalService
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.events.RouterChange
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerRouterInfoComponent
import com.stratagile.pnrouter.ui.activity.router.contract.RouterInfoContract
import com.stratagile.pnrouter.ui.activity.router.module.RouterInfoModule
import com.stratagile.pnrouter.ui.activity.router.presenter.RouterInfoPresenter
import com.stratagile.pnrouter.ui.activity.user.EditNickNameActivity
import com.stratagile.pnrouter.utils.LocalRouterUtils
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.activity_router_info.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject



/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/09/27 16:07:17
 */

class RouterInfoActivity : BaseActivity(), RouterInfoContract.View {

    @Inject
    internal lateinit var mPresenter: RouterInfoPresenter
    lateinit var routerEntity: RouterEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_router_info)
        routerEntity = intent.getParcelableExtra("router")
        if(routerEntity != null && routerEntity.userSn.indexOf("01")== 0)
        {
            llRouterManagement.visibility =  View.VISIBLE
        }else{
            llRouterManagement.visibility =  View.GONE
        }
    }
    override fun initData() {
        llRouterQRCode.setOnClickListener {
            var intent = Intent(this, RouterQRCodeActivity::class.java)
            intent.putExtra("router", routerEntity)
            startActivity(intent)
        }
        tvRouterAlias.text = routerEntity.routerName
        title.text = routerEntity.routerName
        if (routerEntity.lastCheck) {
            tvLogOut.visibility = View.VISIBLE
            tvDeleteRouter.visibility = View.GONE
            tvSwitchRouter.visibility = View.GONE
        }else{
            tvDeleteRouter.visibility = View.VISIBLE
        }
        tvLogOut.setOnClickListener {
//            onLogOutSuccess()
            showDialog()
        }
        tvDeleteRouter.setOnClickListener {
            //            onLogOutSuccess()
            showDeleteDialog()
        }
        tvSwitchRouter.setOnClickListener {

        }
        llRouterAlias.setOnClickListener {
            var intent = Intent(this, EditNickNameActivity::class.java)
            intent.putExtra("flag", "Alias")
            intent.putExtra("hint", "Edit alias")
            intent.putExtra("alias", routerEntity.routerName)
            startActivityForResult(intent, 1)
        }
        llRouterManagement.setOnClickListener {
            var intent = Intent(this, RouterAddUserActivity::class.java)
            intent.putExtra("routerEntity", routerEntity)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            routerEntity.routerName = data!!.getStringExtra("alias")
            AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.update(routerEntity)
            initData()
            EventBus.getDefault().post(RouterChange())
        }
    }

    fun showDialog() {
        SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                .setTitleText("Log Out")
                .setConfirmClickListener {
                    showProgressDialog()
                    AppConfig.instance.messageReceiver!!.shutdown()
                    MessageRetrievalService.registerActivityStopped(this)
                    closeProgressDialog()
                    onLogOutSuccess()
                }
                .show()

    }
    fun showDeleteDialog() {
        SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                .setTitleText(getString(R.string.delete)).
                setContentText(getString(R.string.askdelete))
                .setConfirmClickListener {
                    onDeleteSuccess()
                }
                .show()

    }
    fun onDeleteSuccess() {
        var deleteRouterEntity:RouterEntity =  LocalRouterUtils.deleteLocalAssets(routerEntity.userSn)
        AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.delete(deleteRouterEntity)
        finish()
    }
    fun onLogOutSuccess() {
        AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
        var intent = Intent(this, LoginActivityActivity::class.java)
        intent.putExtra("flag", "logout")
        startActivity(intent)
        finish()
    }

    override fun setupActivityComponent() {
       DaggerRouterInfoComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .routerInfoModule(RouterInfoModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: RouterInfoContract.RouterInfoContractPresenter) {
            mPresenter = presenter as RouterInfoPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}