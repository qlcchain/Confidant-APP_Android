package com.stratagile.pnrouter.ui.activity.email

import android.content.Intent
import android.os.Bundle
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.toast
import com.smailnet.eamil.EmailConfig
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.EmailAttachEntityDao
import com.stratagile.pnrouter.db.EmailConfigEntity
import com.stratagile.pnrouter.db.EmailConfigEntityDao
import com.stratagile.pnrouter.db.EmailMessageEntityDao
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.ChangeRemarksReq
import com.stratagile.pnrouter.entity.DelEmailConf
import com.stratagile.pnrouter.entity.JDelEmailConfRsp
import com.stratagile.pnrouter.entity.events.ChangeEmailConfig
import com.stratagile.pnrouter.ui.activity.email.component.DaggerEmailEditComponent
import com.stratagile.pnrouter.ui.activity.email.contract.EmailEditContract
import com.stratagile.pnrouter.ui.activity.email.module.EmailEditModule
import com.stratagile.pnrouter.ui.activity.email.presenter.EmailEditPresenter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.pnrouter.view.EditBoxAlertDialog
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_email_detail.*
import org.greenrobot.eventbus.EventBus

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2019/08/13 09:58:11
 */

class EmailEditActivity : BaseActivity(), EmailEditContract.View , PNRouterServiceMessageReceiver.DelEmailConfCallback{
    override fun DelEmailConfBack(jDelEmailConfRsp: JDelEmailConfRsp) {
        runOnUiThread {
            closeProgressDialog()
        }
        if(jDelEmailConfRsp.params.retCode == 0)
        {
            deleteLoaclData()
        }else{
            runOnUiThread {
                toast(R.string.fail)
            }
        }

    }

    @Inject
    internal lateinit var mPresenter: EmailEditPresenter
    var currentChooseConfig:EmailConfig? = null
    var accountName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_email_detail)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver!!.delEmailConfCallback = this
        currentChooseConfig =  AppConfig.instance.emailConfig()
        emailAccount.text = currentChooseConfig!!.account
        accountName = currentChooseConfig!!.account.substring(0,currentChooseConfig!!.account.indexOf("@"))
        if(currentChooseConfig!!.name != null && currentChooseConfig!!.name != "")
        {
            ivAvatar.setText(currentChooseConfig!!.name)
            nickName.tvContent.text =currentChooseConfig!!.name
        }else{

            ivAvatar.setText(accountName)
            nickName.tvContent.text = accountName
        }
        title.text = getString(R.string.EmailSettings)
        deleteBtn.setOnClickListener {
            SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                    .setContentText(getString(R.string.Confirm_to_delete_the_accout))
                    .setConfirmClickListener {
                        runOnUiThread {
                            showProgressDialog(getString(R.string.waiting))
                        }
                        var type = AppConfig.instance.emailConfig().emailType.toInt()
                        var accountBase64 = String(RxEncodeTool.base64Encode(AppConfig.instance.emailConfig().account))
                        var saveEmailConf = DelEmailConf(type,accountBase64)
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6,saveEmailConf))


                    }
                    .show()
        }
        updatePassword.setOnClickListener {
            if(currentChooseConfig!!.emailType == "255")
            {
                var Intent = Intent(this, EmailConfigActivity::class.java)
                Intent.putExtra("settings",1)
                startActivity(Intent)
            }else{
                var Intent = Intent(this, EmailLoginActivity::class.java)
                Intent.putExtra("emailType",currentChooseConfig!!.emailType)
                Intent.putExtra("settings",1)
                startActivity(Intent)
            }

        }
        nickName.setOnClickListener {
            var remarks = ""
            if(currentChooseConfig!!.name != null && currentChooseConfig!!.name != "") {
                remarks = currentChooseConfig!!.name
            }else {
                remarks = accountName
            }
            EditBoxAlertDialog(this, EditBoxAlertDialog.BUTTON_NEUTRAL)
                    .setContentText(remarks)
                    .setConfirmClickListener {
                        var content = it;
                        AppConfig.instance.emailConfig().name = content
                        nickName.tvContent.text = content
                        var emailConfigEntityChoose = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
                        if(emailConfigEntityChoose.size > 0) {

                            var emailConfigEntity: EmailConfigEntity = emailConfigEntityChoose.get(0);
                            emailConfigEntity.name = content
                            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(emailConfigEntity)
                        }
                    }
                    .show()
        }
    }

    fun deleteLoaclData()
    {
        var emailConfigEntityChoose = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.queryBuilder().where(EmailConfigEntityDao.Properties.IsChoose.eq(true)).list()
        if(emailConfigEntityChoose.size > 0)
        {
            var emailConfigEntityChooseTemp = emailConfigEntityChoose.get(0)
            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.delete(emailConfigEntityChooseTemp)
        }
        var attachList =  AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.queryBuilder().where(EmailAttachEntityDao.Properties.Account.eq(currentChooseConfig!!.account)).list()
        for (item in attachList)
        {
            AppConfig.instance.mDaoMaster!!.newSession().emailAttachEntityDao.delete(item)
        }
        var localEmailMessageList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.queryBuilder().where(EmailMessageEntityDao.Properties.Account.eq(currentChooseConfig!!.account)).list()
        for (item in localEmailMessageList)
        {
            AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.delete(item)
        }
        var emailConfigEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.loadAll()
        for (item in emailConfigEntityList)
        {
            item.choose = true;
            ConstantValue.currentEmailConfigEntity = item;
            AppConfig.instance.mDaoMaster!!.newSession().emailConfigEntityDao.update(item)
            break;
        }
        EventBus.getDefault().post(ChangeEmailConfig())
        finish()
    }
    override fun setupActivityComponent() {
        DaggerEmailEditComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .emailEditModule(EmailEditModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: EmailEditContract.EmailEditContractPresenter) {
        mPresenter = presenter as EmailEditPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver!!.delEmailConfCallback = null
        super.onDestroy()
    }
}