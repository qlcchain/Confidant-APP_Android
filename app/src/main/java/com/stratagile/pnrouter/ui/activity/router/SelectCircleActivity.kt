package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.os.Bundle
import android.view.View
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.alibaba.fastjson.JSONObject
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.LogOutReq
import com.stratagile.pnrouter.entity.MyFile
import com.stratagile.pnrouter.ui.activity.router.component.DaggerSelectCircleComponent
import com.stratagile.pnrouter.ui.activity.router.contract.SelectCircleContract
import com.stratagile.pnrouter.ui.activity.router.module.SelectCircleModule
import com.stratagile.pnrouter.ui.activity.router.presenter.SelectCirclePresenter
import com.stratagile.pnrouter.ui.adapter.router.RouterListAdapter
import com.stratagile.pnrouter.utils.FileMangerDownloadUtils
import com.stratagile.pnrouter.utils.LocalFileUtils
import com.stratagile.pnrouter.utils.MutableListToArrayList
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.tox.toxcore.KotlinToxService
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.activity_select_circle.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/03/28 13:52:55
 */

class SelectCircleActivity : BaseActivity(), SelectCircleContract.View {

    @Inject
    internal lateinit var mPresenter: SelectCirclePresenter

    var routerListAdapter : RouterListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    var routerEntity : RouterEntity? = null

    override fun initView() {
        setContentView(R.layout.activity_select_circle)
        tvTitle.text = "Select a Circle"
        llCancel.setOnClickListener {
            finish()
        }
    }
    override fun initData() {
        var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        routerListAdapter = RouterListAdapter(routerList.MutableListToArrayList())
        routerList.forEachIndexed { index, it ->
            if (it.lastCheck) {
                routerEntity = it
                routerListAdapter?.selectedItem = index
            }
        }
        recyclerView.adapter = routerListAdapter
        routerListAdapter?.setOnItemClickListener { adapter, view, position ->
            if (routerListAdapter!!.isCkeckMode) {
                routerListAdapter!!.data[position].isMultChecked = !routerListAdapter!!.data[position].isMultChecked
                routerListAdapter!!.notifyItemChanged(position)
            } else {
                routerListAdapter!!.selectedItem = position
                routerListAdapter!!.notifyDataSetChanged()
                logOutRouter(routerListAdapter!!.data[position])
            }
        }
        multiSelectBtn.setOnClickListener {
            if (routerListAdapter != null) {
                routerListAdapter?.isCkeckMode = !routerListAdapter!!.isCkeckMode
                if (routerListAdapter!!.isCkeckMode) {
                    tvLeaveCircle.visibility = View.VISIBLE
                } else {
                    tvLeaveCircle.visibility = View.GONE
                }
                routerListAdapter!!.notifyDataSetChanged()
            }
        }
    }

    //先退出当前的路由器
    fun logOutRouter(router : RouterEntity) {
        var selfUserId = SpUtil.getString(this!!, ConstantValue.userId, "")
        var msgData = LogOutReq(routerEntity!!.routerId,selfUserId!!,routerEntity!!.userSn)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,msgData))
        } else if (ConstantValue.isToxConnected) {
            val baseData = BaseData(2,msgData)
            val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(routerEntity!!.routerId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, routerEntity!!.routerId.substring(0, 64))
            }
        }

        ConstantValue.isHasWebsocketInit = true
        if(AppConfig.instance.messageReceiver != null)
            AppConfig.instance.messageReceiver!!.close()

        ConstantValue.loginOut = true
        ConstantValue.logining = false
        ConstantValue.isHeart = false
        ConstantValue.currentRouterIp = ""
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
        connectRouter(router)
    }


    /**
     * 重新登录路由的方法
     */
    fun connectRouter(router : RouterEntity) {

    }

    /**
     * 批量删除圈子
     */
    fun leaveCircles() {

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

    override fun setupActivityComponent() {
       DaggerSelectCircleComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .selectCircleModule(SelectCircleModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SelectCircleContract.SelectCircleContractPresenter) {
            mPresenter = presenter as SelectCirclePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}