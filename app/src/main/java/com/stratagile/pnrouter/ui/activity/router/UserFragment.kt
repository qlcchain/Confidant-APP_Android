package com.stratagile.pnrouter.ui.activity.router

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pawegio.kandroid.runOnUiThread
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.RouterUserEntity
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JPullUserRsp
import com.stratagile.pnrouter.entity.PullUserReq
import com.stratagile.pnrouter.ui.activity.router.component.DaggerUserComponent
import com.stratagile.pnrouter.ui.activity.router.contract.UserContract
import com.stratagile.pnrouter.ui.activity.router.module.UserModule
import com.stratagile.pnrouter.ui.activity.router.presenter.UserPresenter
import com.stratagile.pnrouter.ui.activity.user.CircleMemberDetailActivity
import com.stratagile.pnrouter.ui.adapter.user.UsertListAdapter
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.pnrouter.view.TempRouterAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.ease_search_bar.*
import kotlinx.android.synthetic.main.fragment_user.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2018/12/06 14:25:44
 */

class UserFragment: BaseFragment(), UserContract.View , PNRouterServiceMessageReceiver.PullUserCallBack{
    override fun userList(jPullUserRsp: JPullUserRsp) {

        comUserList = arrayListOf<RouterUserEntity>()
        tempUserList = arrayListOf<RouterUserEntity>()
        for (i in jPullUserRsp.params.payload) {
            //是否为本地多余的好友
            if (i.userType == 2) {
                comUserList.add(i)
            } else if (i.userType == 3){
                if(i.nickName == null || i.nickName.equals(""))
                    i.nickName ="dGVtcFVzZXI="
                tempUserList.add(i)
            }
        }
        for (i in comUserList) {
            if(!i.nickName.equals(""))
            {
                i.nickSouceName = String(RxEncodeTool.base64Decode(i.nickName)).toLowerCase()
            }else{
                i.nickSouceName = i.mnemonic
            }

        }
        comUserList.sortBy {
            it.nickSouceName
        }
        for (i in tempUserList) {
            if(!i.nickName.equals(""))
            {
                i.nickSouceName = String(RxEncodeTool.base64Decode(i.nickName)).toLowerCase()
            }else{
                i.nickSouceName = i.mnemonic
            }
        }
        tempUserList.sortBy {
            it.nickSouceName
        }
        runOnUiThread {
            contactAdapter = UsertListAdapter(comUserList,false)
            recyclerViewUser.adapter = contactAdapter
            usersTips.text = "User("+jPullUserRsp.params.normalUserNum.toString() +"/"+ (jPullUserRsp.params.normalUserNum + jPullUserRsp.params.tempUserNum ).toString()+")"
            tempUsersTips.text = "Temporary("+jPullUserRsp.params.tempUserNum.toString() +"/"+ (jPullUserRsp.params.normalUserNum + jPullUserRsp.params.tempUserNum ).toString()+")"
            contactTempAdapter = UsertListAdapter(tempUserList,false)
            recyclerViewTempUser.adapter = contactTempAdapter

            contactAdapter!!.setOnItemClickListener { adapter, view, position ->
              /*  var intent = Intent(activity!!, UserQRCodeActivity::class.java)
                intent.putExtra("user", contactAdapter!!.getItem(position))
                startActivity(intent)*/
                var intent = Intent(activity!!, CircleMemberDetailActivity::class.java)
                intent.putExtra("user", contactAdapter!!.getItem(position))
                startActivityForResult(intent,1)
            }
            contactTempAdapter!!.setOnItemClickListener { adapter, view, position ->
                //showDialog()
                routerUserTempEntity = contactTempAdapter!!.getItem(position) as RouterUserEntity

                var intent = Intent(activity!!, CircleMemberDetailActivity::class.java)
                intent.putExtra("user", routerUserTempEntity)
                startActivityForResult(intent,1)
                /* var intent = Intent(activity!!, UserQRCodeActivity::class.java)
                 intent.putExtra("user", contactTempAdapter!!.getItem(position))
                 startActivity(intent)*/
            }
            closeProgressDialog()
        }

    }

    @Inject
    lateinit internal var mPresenter: UserPresenter
    var contactAdapter : UsertListAdapter? = null
    var routerEntity: RouterEntity? = null
    var routerUserTempEntity: RouterUserEntity? = null
    var contactTempAdapter : UsertListAdapter? = null

    var comUserList = arrayListOf<RouterUserEntity>()
    var tempUserList = arrayListOf<RouterUserEntity>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        routerEntity = arguments!!.get("userEntity") as RouterEntity
        var view = inflater.inflate(R.layout.fragment_user, null);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppConfig.instance.messageReceiver!!.pullUserCallBack = this
        newUser.setOnClickListener {
            var intent = Intent(activity!!, RouterCreateUserActivity::class.java)
            intent.putExtra("routerUserEntity", routerEntity)
            startActivityForResult(intent, 0)
        }
        newTempUser.setOnClickListener {
            toast("developing")
            //showDialog()
        }
        initData()
        refreshLayoutUser.setOnRefreshListener {
            pullFriendList()
            KLog.i("拉取用户列表")
        }
        pullFriendList()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 1)
        {
            if(data != null && data!!.hasExtra("result"))
            {
                var result = data!!.getStringExtra("result");
                if(result != null && !result.equals(""))
                {
                    pullFriendList()
                }
            }

        }else{
            pullFriendList()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
    fun showDialog() {
        TempRouterAlertDialog(activity!!, TempRouterAlertDialog.BUTTON_NEUTRAL)
                .setContentText(getString(R.string.fixedtwo))
                .setConfirmClickListener {
                    var intent = Intent(activity!!, UserQRCodeActivity::class.java)
                    intent.putExtra("user", routerUserTempEntity)
                    startActivity(intent)
                }
                .show()

    }
    fun showCode()
    {

    }
   override fun onResume()
    {
        super.onResume()
        //pullFriendList();
    }
    fun pullFriendList() {
        showProgressDialog()
        refreshLayoutUser.isRefreshing = false
        var pullFriend = PullUserReq(0,0,"", ConstantValue.libsodiumpublicSignKey!!)
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,pullFriend))
        }else if (ConstantValue.isToxConnected) {
            var baseData = BaseData(2,pullFriend)
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

    }

    fun initData() {

        query.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                fiter(s.toString(),comUserList,tempUserList)
                if (s.length > 0) {
                    search_clear.setVisibility(View.VISIBLE)
                } else {
                    search_clear.setVisibility(View.INVISIBLE)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {

            }
        })
        search_clear.setOnClickListener(View.OnClickListener {
            query.getText().clear()
            //hideSoftKeyboard()
        })
    }
    fun fiter(key:String,comUserList:ArrayList<RouterUserEntity>,tempUserList:ArrayList<RouterUserEntity>)
    {
        var comUserListTemp:ArrayList<RouterUserEntity> = arrayListOf<RouterUserEntity>()
        var tempUserListTemp:ArrayList<RouterUserEntity> = arrayListOf<RouterUserEntity>()
        for (i in comUserList) {
            if(i.nickSouceName.toLowerCase().contains(key))
            {
                comUserListTemp.add(i)
            }
        }
        for (i in tempUserList) {
            if(i.nickSouceName.toLowerCase().contains(key))
            {
                tempUserListTemp.add(i)
            }
        }
        contactAdapter!!.setNewData(comUserListTemp)
        contactTempAdapter!!.setNewData(tempUserListTemp)
    }
    override fun setupFragmentComponent() {
        DaggerUserComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .userModule(UserModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: UserContract.UserContractPresenter) {
        mPresenter = presenter as UserPresenter
    }

    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppConfig.instance.messageReceiver!!.pullUserCallBack = null
    }
}