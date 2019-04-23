package com.stratagile.pnrouter.ui.activity.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.alibaba.fastjson.JSONObject
import com.pawegio.kandroid.startActivity
import com.pawegio.kandroid.startActivityForResult
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.RouterEntityDao
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.LogOutReq
import com.stratagile.pnrouter.entity.MyFile
import com.stratagile.pnrouter.entity.Sceen
import com.stratagile.pnrouter.entity.events.ResetAvatar
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerMyDetailComponent
import com.stratagile.pnrouter.ui.activity.user.contract.MyDetailContract
import com.stratagile.pnrouter.ui.activity.user.module.MyDetailModule
import com.stratagile.pnrouter.ui.activity.user.presenter.MyDetailPresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.KotlinToxService
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_my_detail.*
import org.greenrobot.eventbus.EventBus

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
        var flag = intent.getIntExtra("flag",0)
        if(flag == 1)
        {
            llAvatar.visibility = View.GONE
            nickName.visibility = View.GONE
            title.text = getString(R.string.Settings)
            qrCode.visibility= View.GONE
            fingerprintBtn.visibility = View.VISIBLE
            screenshotsBtn.visibility = View.VISIBLE
            PrivacyPolicy.visibility = View.VISIBLE
            qrCode.setTitleText(getString(R.string.Exportaccount))
        }else{
            llAvatar.visibility = View.VISIBLE
            nickName.visibility = View.VISIBLE
            title.text = getString(R.string.details)
            qrCode.visibility= View.VISIBLE
            fingerprintBtn.visibility = View.GONE
            screenshotsBtn.visibility = View.GONE
            PrivacyPolicy.visibility = View.GONE
            qrCode.setTitleText(getString(R.string.invite_friends_to_conifdant))
        }

        qrCode.setOnClickListener {
            var intent = Intent(this, QRCodeActivity::class.java)
            intent.putExtra("flag",flag)
            startActivity(intent)
        }
        PrivacyPolicy.setOnClickListener {
            var intent = Intent(this, PrivacyActivity::class.java)
            startActivity(intent)
        }
        nickName.tvContent.text = SpUtil.getString(this, ConstantValue.username, "")
        ivAvatar.setText(SpUtil.getString(this, ConstantValue.username, "")!!)
        var avatarPath = Base58.encode( RxEncodeTool.base64Decode(ConstantValue.libsodiumpublicSignKey))+".jpg"
        ivAvatar.setImageFile(avatarPath)
        nickName.setOnClickListener {
            startActivityForResult(Intent(this, EditNickNameActivity::class.java), 1)
        }
        llAvatar.setOnClickListener {
            startActivityForResult(Intent(this, ModifyAvatarActivity::class.java), 2)
        }
        var fingerprintSwitchFlag = SpUtil.getString(this, ConstantValue.fingerprintSetting, "1")
        fingerprintSwitch.isChecked = fingerprintSwitchFlag.equals("1")
        fingerprintSwitch.setOnClickListener {
            if (fingerprintSwitch.isChecked) {
                SpUtil.putString(this, ConstantValue.fingerprintSetting, "1")
            } else {
                SpUtil.putString(this, ConstantValue.fingerprintSetting, "0")
            }
        }

        var screenshotsFlag = SpUtil.getString(this, ConstantValue.screenshotsSetting, "1")
        screenshotsSwitch.isChecked = screenshotsFlag.equals("1")
        screenshotsSwitch.setOnClickListener {
            if (screenshotsSwitch.isChecked) {
                SpUtil.putString(this, ConstantValue.screenshotsSetting, "1")
            } else {
                SpUtil.putString(this, ConstantValue.screenshotsSetting, "0")
            }
            EventBus.getDefault().post(Sceen())
        }
        tvLogOut.setOnClickListener {
            //            onLogOutSuccess()
            showDialog()
        }
    }
    fun showDialog() {
        var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.RouterId.eq(ConstantValue.currentRouterId)).list()

        if(routerList .size  > 0)
        {
            var routerEntity = routerList[0]
            SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                    .setTitleText("Log Out")
                    .setConfirmClickListener {
                        var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
                        var msgData = LogOutReq(routerEntity.routerId,selfUserId!!,routerEntity.userSn)
                        if (ConstantValue.isWebsocketConnected) {
                            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
                        } else if (ConstantValue.isToxConnected) {
                            val baseData = BaseData(2,msgData)
                            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                            if (ConstantValue.isAntox) {
                                var friendKey: FriendKey = FriendKey(routerEntity.routerId.substring(0, 64))
                                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                            }else{
                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, routerEntity.routerId.substring(0, 64))
                            }
                        }

                        ConstantValue.isHasWebsocketInit = true
                        if(AppConfig.instance.messageReceiver != null)
                            AppConfig.instance.messageReceiver!!.close()

                        ConstantValue.loginOut = true
                        ConstantValue.logining = false
                        ConstantValue.isHeart = false
                        ConstantValue.currentRouterIp = ""
                        //isUserExit = true
                        resetUnCompleteFileRecode()
                        if (ConstantValue.isWebsocketConnected) {
                            FileMangerDownloadUtils.init()
                            ConstantValue.webSockeFileMangertList.forEach {
                                it.disconnect(true)
                                //ConstantValue.webSockeFileMangertList.remove(it)
                            }
                            ConstantValue.webSocketFileList.forEach {
                                it.disconnect(true)
                                //ConstantValue.webSocketFileList.remove(it)
                            }
                        }else{
                            val intentTox = Intent(this, KotlinToxService::class.java)
                            this.stopService(intentTox)
                        }
                        ConstantValue.isWebsocketConnected = false
                        onLogOutSuccess()
                        /*ConstantValue.isHasWebsocketInit = true
                        if(AppConfig.instance.messageReceiver != null)
                            AppConfig.instance.messageReceiver!!.close()
                        ConstantValue.isWebsocketConnected = false*/
                        //onLogOutSuccess()
                    }
                    .show()
        }


    }
    fun onLogOutSuccess() {
        ConstantValue.loginReq = null
        ConstantValue.isWebsocketReConnect = false
        AppConfig.instance.mAppActivityManager.finishAllActivityWithoutThis()
        var intent = Intent(this, LoginActivityActivity::class.java)
        intent.putExtra("flag", "logout")
        startActivity(intent)
        finish()
    }
    fun resetUnCompleteFileRecode()
    {
        var localFilesList = LocalFileUtils.localFilesList
        for (myFie in localFilesList)
        {
            if(myFie.upLoadFile.isComplete == false)
            {
                myFie.upLoadFile.SendGgain = true
                myFie.upLoadFile.isStop = "1"
                myFie.upLoadFile.segSeqResult = 0
                val myRouter = MyFile()
                myRouter.type = 0
                myRouter.userSn = ConstantValue.currentRouterSN
                myRouter.upLoadFile = myFie.upLoadFile
                LocalFileUtils.updateLocalAssets(myRouter)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        EventBus.getDefault().post(ResetAvatar())
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