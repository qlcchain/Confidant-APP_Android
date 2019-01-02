package com.stratagile.pnrouter.ui.activity.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.stratagile.pnrouter.R
import android.view.MenuItem
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JUserInfoUpdateRsp
import com.stratagile.pnrouter.entity.UserInfoUpdateReq
import com.stratagile.pnrouter.entity.events.EditNickName
import com.stratagile.pnrouter.ui.activity.user.component.DaggerEditNickNameComponent
import com.stratagile.pnrouter.ui.activity.user.contract.EditNickNameContract
import com.stratagile.pnrouter.ui.activity.user.module.EditNickNameModule
import com.stratagile.pnrouter.ui.activity.user.presenter.EditNickNamePresenter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_edit_nick_name.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/12 13:20:58
 */

class EditNickNameActivity : BaseActivity(), EditNickNameContract.View, PNRouterServiceMessageReceiver.UserInfoUpdateCallBack {
    override fun UserInfoUpdateCallBack(jUserInfoUpdateRsp: JUserInfoUpdateRsp) {

        if(jUserInfoUpdateRsp.params.retCode == 0)
        {
            SpUtil.putString(this, ConstantValue.username, etNickName.text.toString().trim())
            var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
            var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
            routerList.forEach {
                if(it.userId.equals(selfUserId))
                {
                    it.username = etNickName.text.toString().trim()
                    AppConfig.instance.mDaoMaster!!.newSession().update(it)
                }
            }
            EventBus.getDefault().post(EditNickName())
            runOnUiThread()
            {
                closeProgressDialog()
                toast(getString(R.string.dl_update_success))
                onBackPressed()
            }

        }else{
            runOnUiThread()
            {
                closeProgressDialog()
                onBackPressed()
                toast(getString(R.string.dl_update_failed))
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: EditNickNamePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_edit_nick_name)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver!!.uerInfoUpdateCallBack = this
        if (intent.hasExtra("flag")) {
            title.text = intent.getStringExtra("flag")
            etNickName.setText(intent.getStringExtra("alias"))
            etNickName.setSelection(intent.getStringExtra("alias").length)
            etNickName.hint = intent.getStringExtra("hint")
        } else {
            title.text = "Edit NickName"
            var nickName = SpUtil.getString(this, ConstantValue.username, "")!!
            etNickName.setText(nickName)
            etNickName.setSelection(nickName.length)
        }
    }

    override fun onBackPressed() {
        if (intent.hasExtra("flag")) {
            if (etNickName.text.toString().equals(intent.getStringExtra("alias"))) {
                setResult(0)
            } else if (!etNickName.text.toString().equals("")){
                var intent = Intent()
                intent.putExtra("alias", etNickName.text.toString())
                setResult(Activity.RESULT_OK, intent)

            }
        }
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                if ("".equals(etNickName.text.toString().trim())) {
                    onBackPressed()
                    return true
                }
                if (!intent.hasExtra("flag")) {
                    var selfUserId = SpUtil.getString(this, ConstantValue.userId, "")
                    var nickName = SpUtil.getString(this, ConstantValue.username, "")
                    if(!nickName.equals(etNickName.text.toString()))
                    {
                        showProgressDialog("wait...")
                        val strBase64 = RxEncodeTool.base64Encode2String(etNickName.text.toString().toByteArray())
                        var userInfoUpdate = UserInfoUpdateReq( selfUserId!!, strBase64!!)
                        if (ConstantValue.isWebsocketConnected) {
                            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,userInfoUpdate))
                        } else if (ConstantValue.isToxConnected) {
                            var baseData = BaseData(2,userInfoUpdate)
                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                            var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                            MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        }
                    }else{
                        onBackPressed()
                    }

                }else{
                    onBackPressed()
                }
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setupActivityComponent() {
        DaggerEditNickNameComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .editNickNameModule(EditNickNameModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: EditNickNameContract.EditNickNameContractPresenter) {
        mPresenter = presenter as EditNickNamePresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver!!.uerInfoUpdateCallBack = null
    }
}