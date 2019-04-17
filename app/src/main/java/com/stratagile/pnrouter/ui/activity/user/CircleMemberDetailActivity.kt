package com.stratagile.pnrouter.ui.activity.user

import android.content.Intent
import android.os.Bundle
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterUserEntity
import com.stratagile.pnrouter.entity.JRemoveMemberRsp
import com.stratagile.pnrouter.entity.JResetRouterNameRsp
import com.stratagile.pnrouter.ui.activity.user.component.DaggerCircleMemberDetailComponent
import com.stratagile.pnrouter.ui.activity.user.contract.CircleMemberDetailContract
import com.stratagile.pnrouter.ui.activity.user.module.CircleMemberDetailModule
import com.stratagile.pnrouter.ui.activity.user.presenter.CircleMemberDetailPresenter
import com.stratagile.pnrouter.utils.RxEncodeTool
import kotlinx.android.synthetic.main.activity_circlemember_detail.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/04/17 10:21:23
 */

class CircleMemberDetailActivity : BaseActivity(), CircleMemberDetailContract.View , PNRouterServiceMessageReceiver.RemoveMemberCallBack {
    override fun removeMember(jRemoveMemberRsp: JRemoveMemberRsp) {

       /* val intent = Intent()
        intent.putExtra("result", "1")
        setResult(RESULT_OK, intent)
        finish()*/
    }

    @Inject
    internal lateinit var mPresenter: CircleMemberDetailPresenter
    lateinit var routerUserEntity: RouterUserEntity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_circlemember_detail)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver!!.removeMemberCallBack = this
        title.text = resources.getString(R.string.Contact_Details)
        routerUserEntity = intent.getParcelableExtra("user")

        if(!routerUserEntity.nickName.equals(""))
        {
            var nickNameSouce = String(RxEncodeTool.base64Decode(routerUserEntity.nickName))
            nickName.tvContent.text = nickNameSouce
        }else{
            var nickNameSouce = String(RxEncodeTool.base64Decode(routerUserEntity.mnemonic))
            nickName.tvContent.text = nickNameSouce
        }

        if(!routerUserEntity.nickName.equals(""))
        {
            var nickNameSouce = String(RxEncodeTool.base64Decode(routerUserEntity.nickName))
            ivAvatar.setText(nickNameSouce)
        }else{
            var nickNameSouce = String(RxEncodeTool.base64Decode(routerUserEntity.mnemonic))
            ivAvatar.setText(nickNameSouce)
        }
        removeMember.setOnClickListener {

        }
    }

    override fun setupActivityComponent() {
        DaggerCircleMemberDetailComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .circleMemberDetailModule(CircleMemberDetailModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: CircleMemberDetailContract.CircleMemberDetailContractPresenter) {
        mPresenter = presenter as CircleMemberDetailPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver!!.removeMemberCallBack = null
        super.onDestroy()
    }
}