package com.stratagile.pnrouter.ui.activity.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.hyphenate.chat.EMMessage
import com.pawegio.kandroid.runOnUiThread
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.FriendEntity
import com.stratagile.pnrouter.db.GroupEntity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.db.UserEntityDao
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JPullFriendRsp
import com.stratagile.pnrouter.entity.MyFriend
import com.stratagile.pnrouter.entity.PullFriendReq_V4
import com.stratagile.pnrouter.ui.activity.main.component.DaggerContactAndGroupComponent
import com.stratagile.pnrouter.ui.activity.main.contract.ContactAndGroupContract
import com.stratagile.pnrouter.ui.activity.main.module.ContactAndGroupModule
import com.stratagile.pnrouter.ui.activity.main.presenter.ContactAndGroupPresenter
import com.stratagile.pnrouter.ui.adapter.group.GroupAdapter
import com.stratagile.pnrouter.ui.adapter.user.ContactAdapter
import com.stratagile.pnrouter.ui.adapter.user.UserHead
import com.stratagile.pnrouter.ui.adapter.user.UserItem
import com.stratagile.pnrouter.utils.LogUtil
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.ease_search_bar.*
import kotlinx.android.synthetic.main.fragment_contact_group.*
import org.greenrobot.eventbus.EventBus
import org.libsodium.jni.Sodium
import java.util.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2019/03/26 11:19:29
 */

class ContactAndGroupFragment : BaseFragment(), ContactAndGroupContract.View , PNRouterServiceMessageReceiver.PullFriendCallBack{
    override fun firendList(jPullFriendRsp: JPullFriendRsp) {
        //用户表，每个手机是一个用户
        var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        //关系表，我和其他用户的关系
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        if (jPullFriendRsp.params.payload == null || jPullFriendRsp.params.payload.size == 0) {
            /*for (i in localFriendList) {
                //是否为本地多余的好友
                if (i.friendStatus == 3 || i.friendStatus == 1) {
                    //等待验证的好友，不能处理
                    continue
                } else {
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.delete(i)
                }
            }*/
            var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
            for (i in localFriendStatusList) {
                if (i.userId.equals(userId)) {
                    if (i.friendLocalStatus == 3 || i.friendLocalStatus == 1) {
                        continue
                    } else {
                        i.friendLocalStatus = 7
                        AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(i)
                    }
                }

            }
            runOnUiThread {
                initData()
            }
            return
        }
        //添加新的好友
        for (i in jPullFriendRsp.params.payload) {
            //是否为本地好友
            var isLocalFriend = false
            for (j in localFriendList) {
                if (i.id.equals(j.userId)) {
                    isLocalFriend = true
                    //j.friendStatus = 0
                    j.nickName = i.name
                    j.index = i.index
                    j.remarks = i.remarks
                    j.signPublicKey = i.userKey
                    j.routeId = i.routeId
                    j.routeName = i.routeName
                    var dst_public_MiKey_Friend = ByteArray(32)
                    var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend, RxEncodeTool.base64Decode(i.userKey))
                    if (crypto_sign_ed25519_pk_to_curve25519_result == 0) {
                        j.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
                    }
                    AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(j)
                    break
                }
            }
            if (!isLocalFriend) {
                var userEntity = UserEntity()
                userEntity.nickName = i.name
                userEntity.userId = i.id
                userEntity.index = i.index
                userEntity.signPublicKey = i.userKey
                userEntity.routeId = i.routeId
                userEntity.routeName = i.routeName
                var dst_public_MiKey_Friend = ByteArray(32)
                var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey_Friend, RxEncodeTool.base64Decode(i.userKey))
                if (crypto_sign_ed25519_pk_to_curve25519_result == 0) {
                    userEntity.miPublicKey = RxEncodeTool.base64Encode2String(dst_public_MiKey_Friend)
                }
                userEntity.remarks = i.remarks
                //userEntity.friendStatus = 0
                userEntity.timestamp = Calendar.getInstance().timeInMillis
                var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                userEntity.routerUserId = selfUserId
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)
            }
            var isLocalFriendStatus = false
            for (j in localFriendStatusList) {
                if (i.id.equals(j.friendId) && j.userId.equals(userId)) {
                    isLocalFriendStatus = true
                    j.friendLocalStatus = 0
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(j)
                    break
                }
            }
            if (!isLocalFriendStatus) {
                var friendEntity = FriendEntity()
                friendEntity.userId = userId
                friendEntity.index = i.index
                friendEntity.friendId = i.id
                friendEntity.friendLocalStatus = 0
                friendEntity.timestamp = Calendar.getInstance().timeInMillis
                AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.insert(friendEntity)
            }
        }
        //把本地的多余好友清除
        /*  localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
          for (i in localFriendList) {
              //是否为本地多余的好友
              if (i.friendStatus == 3 || i.friendStatus == 1) {
                  //等待验证的S好友，不能处理
                  continue
              }
              var isLocalDeletedFriend = true
              for (j in jPullFriendRsp.params.payload) {
                  if (i.userId.equals(j.id)) {
                      isLocalDeletedFriend = false
                  }
              }
              if (isLocalDeletedFriend) {
                  i.friendStatus = 7
                  AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.update(i)
              }
          }*/
        //把本地的多余好友清除
        localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        LogUtil.addLog("localFriendStatusList:" + localFriendStatusList.size, "ContactFragment")
        for (i in localFriendStatusList) {
            if (i.userId.equals(userId)) {
                LogUtil.addLog("freindStatus:" + i.userId + "_" + i.friendId + "_" + i.friendLocalStatus, "ContactFragment")
                //是否为本地多余的好友
                if (i.friendLocalStatus == 3 || i.friendLocalStatus == 1) {
                    //等待验证的S好友，不能处理
                    continue
                }

                var isLocalDeletedFriend = true
                for (j in jPullFriendRsp.params.payload) {
                    if (i.friendId.equals(j.id)) {
                        LogUtil.addLog("freindName:" + j.name, "ContactFragment")
                        isLocalDeletedFriend = false
                    }
                }
                if (isLocalDeletedFriend) {
                    LogUtil.addLog("deletefreindName:" + i.friendId, "ContactFragment")
                    i.friendLocalStatus = 7
                    AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.update(i)
                }
            } else {
                LogUtil.addLog("freindStatusOther:" + i.userId + "_" + i.friendId + "_" + i.friendLocalStatus, "ContactFragment")
            }

        }
        runOnUiThread {
            initData()
        }
    }
    @Inject
    lateinit internal var mPresenter: ContactAndGroupPresenter
//    var contactAdapter : ContactListAdapter? = null

    var contactAdapter0: GroupAdapter? = null
    var contactAdapter1: ContactAdapter? = null

    lateinit var viewModel: MainViewModel

    var toAddList: ArrayList<UserEntity>? = null
    var toReduceList: ArrayList<UserEntity>? = null

    var fromId: String? = null
    var message: EMMessage? = null
    var refreshEnable1 = true
    var routerId: String? = null
    var onViewCreated = false;

    //   @Nullable
    //   @Override
    //   public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    //       View view = inflater.inflate(R.layout.fragment_contactAndGroup, null);
    //       ButterKnife.bind(this, view);
    //       Bundle mBundle = getArguments();
    //       return view;
    //   }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fromId = null
        if (arguments != null && arguments!!.get("fromId") != null) {
            fromId = arguments!!.get("fromId") as String
            //message = arguments!!.get("message") as EMMessage
        }
        if (arguments != null && arguments!!.get("routerId") != null) {
            routerId = arguments!!.get("routerId") as String
        }
        var view = inflater.inflate(R.layout.fragment_contact_group, null);
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        try {
            AppConfig.instance.messageReceiver!!.pullFriendCallBack = this
        } catch (e: Exception) {
            var aa = ""
        }
        initData()
        /*refreshLayout.setOnRefreshListener {
            pullFriendList()
            KLog.i("拉取好友列表")
        }*/
        refreshLayout.isEnabled = refreshEnable1
        pullFriendList()
    }

    fun setRefreshEnable(enable: Boolean) {
        refreshEnable1 = enable
    }

    fun setSelectedPerson(list: ArrayList<UserEntity>) {
        if (list == null || list.size == 0) {
            return
        }
        toAddList = list
        KLog.i("将要添加的数量为：" + toAddList?.size)
    }

    fun setUnShowPerson(list: ArrayList<UserEntity>) {
        if (list == null || list.size == 0) {
            return
        }
        toReduceList = list
        KLog.i("将要不显示的数量为：" + toReduceList?.size)
    }
    fun pullFriendList() {
        Log.i("pullFriendList", "webosocket" + ConstantValue.isWebsocketConnected)
        var selfUserId = SpUtil.getString(activity!!, ConstantValue.userId, "")
        var pullFriend = PullFriendReq_V4(selfUserId!!)
        var sendData = BaseData(pullFriend)
        if (ConstantValue.encryptionType.equals("1")) {
            sendData = BaseData(4, pullFriend)
        }
        Log.i("pullFriendList", "tox " + ConstantValue.isToxConnected)
        if (ConstantValue.isWebsocketConnected) {
            Log.i("pullFriendList", "webosocket" + AppConfig.instance.getPNRouterServiceMessageSender())
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
        } else if (ConstantValue.isToxConnected) {
            Log.i("pullFriendList", "tox")
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            } else {
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }

    }

    fun updata() {
        pullFriendList()
    }

    fun updataUI() {
        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var contactList = arrayListOf<UserEntity>()
        var selfUserId = SpUtil.getString(activity!!, ConstantValue.userId, "")
        var newFriendCount = 0
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        for (i in localFriendStatusList) {
            if (i.userId.equals(userId)) {
                if (i.friendLocalStatus == 0) {
                    var it = UserEntity()
                    var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(i.friendId)).list()
                    if (localFriendList.size > 0)
                        it = localFriendList.get(0)
                    contactList.add(it)
                }
                if (i.friendLocalStatus == 3) {
                    newFriendCount++
                }
            }

        }
        for (i in contactList) {
            if (i?.remarks != null && i?.remarks != "") {
                i.nickSouceName = String(RxEncodeTool.base64Decode(i.remarks)).toLowerCase()
            } else {
                i.nickSouceName = String(RxEncodeTool.base64Decode(i.nickName)).toLowerCase()
            }
        }
        contactList.sortBy {
            it.nickSouceName
        }
        updateAdapterData(contactList)
//        contactAdapter!!.setNewData(contactList);
    }

    fun initData() {
        var bundle = getArguments();
        var hasNewFriendRequest = false
        var list = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
        var contactList = arrayListOf<UserEntity>()
        var selfUserId = SpUtil.getString(activity!!, ConstantValue.userId, "")
        var newFriendCount = 0
        var localFriendStatusList = AppConfig.instance.mDaoMaster!!.newSession().friendEntityDao.loadAll()
        var userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        for (i in localFriendStatusList) {
            if (i.userId.equals(userId)) {
                if (i.friendLocalStatus == 0) {
                    var it = UserEntity()
                    var localFriendList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.queryBuilder().where(UserEntityDao.Properties.UserId.eq(i.friendId)).list()
                    if (localFriendList.size > 0)
                        it = localFriendList.get(0)

                    if(routerId != null)//群聊只能添加本路由器好友
                    {
                        if(it.routeId.equals(routerId))
                        {
                            if(!it.userId.equals(fromId))
                            {
                                contactList.add(it)
                            }

                        }
                    }else{
                        if(!it.userId.equals(fromId))
                        {
                            contactList.add(it)
                        }

                    }

                }
                if (i.friendLocalStatus == 3) {
                    hasNewFriendRequest = true
                    newFriendCount++
                }
            }

        }
        var localGroupMemberList = AppConfig.instance.mDaoMaster!!.newSession().groupVerifyEntityDao.loadAll()
        localGroupMemberList.forEach {
            if (it.userId.equals(selfUserId) && it.verifyType == 1) {
                newFriendCount++
                hasNewFriendRequest = true
            }
        }

        for (i in contactList) {
            if (i?.remarks != null && i?.remarks != "") {
                i.nickSouceName = String(RxEncodeTool.base64Decode(i.remarks)).toLowerCase()
            } else {
                i.nickSouceName = String(RxEncodeTool.base64Decode(i.nickName)).toLowerCase()
            }
        }
        contactList.sortBy {
            it.nickSouceName
        }
        //一对多数据处理begin
        var contactMapList = HashMap<String, MyFriend>()
        for (i in contactList) {
            if (contactMapList.get(i.signPublicKey) == null) {
                var myFriend = MyFriend()
                myFriend.userKey = i.signPublicKey
                myFriend.userName = i.nickName
                myFriend.userEntity = i
                var temp = ArrayList<UserEntity>()
                temp.add(i)
                myFriend.routerItemList = temp;
                contactMapList.put(i.signPublicKey, myFriend)
            } else {
                var temp = contactMapList.get(i.signPublicKey)
                var contactNewList = temp!!.routerItemList
                contactNewList.add(i)
            }

        }

        var contactNewList = arrayListOf<MyFriend>()
        var contactNewListValues = contactMapList.values
        for (i in contactNewListValues) {
            contactNewList.add(i)
        }
        contactNewList.sortBy {
            String(RxEncodeTool.base64Decode(it.userName)).toLowerCase()
        }
        val list0 =AppConfig.instance.mDaoMaster!!.newSession().groupEntityDao.loadAll()
        val list1 = arrayListOf<MultiItemEntity>()
        var isIn = false
        contactNewList.forEach {
            var userHead = UserHead()
            userHead.userName = it.userName
            userHead.userEntity = it.userEntity
            if (it.routerItemList.size > 1) {
                it.routerItemList?.forEach {
                    userHead.addSubItem(UserItem(it))
                }
            }
            list1.add(userHead)
        }

        if (toAddList != null) {
            //把从外面带过来的数据先设置为已经选中
            KLog.i("添加已经选中的")
            list1.forEach {
                var userHead = it as UserHead
                if (userHead.subItems != null && userHead.subItems.size > 0) {
                    userHead.subItems.forEach { userItem ->
                        toAddList?.forEach {
                            if (it.userId.equals(userItem.userEntity.userId)) {
                                KLog.i("添加选中")
                                userItem.isChecked = true
                            }
                        }
                    }
                } else {
                    toAddList?.forEach {
                        if (it.userId.equals(userHead.userEntity.userId)) {
                            KLog.i("添加选中")
                            userHead.isChecked = true
                        }
                    }
                }
            }
        }
        var list2 = arrayListOf<MultiItemEntity>()
        list2.addAll(list1)
        if (toReduceList != null) {
            list2.forEach {
                var userHead = it as UserHead
                if (userHead.subItems != null && userHead.subItems.size > 0) {
                    userHead.subItems.forEach { userItem ->
                        toReduceList?.forEach {
                            if (it.userId.equals(userItem.userEntity.userId)) {
                                KLog.i("删除。。")
//                                userItem.isChecked = true
                                list1.remove(userItem)
                            }
                        }
                    }
                } else {
                    toReduceList?.forEach {
                        if (it.userId.equals(userHead.userEntity.userId)) {
                            KLog.i("删除。。")
//                            userHead.isChecked = true
                            list1.remove(userHead)
                        }
                    }
                }
            }
        }
        contactAdapter0 = GroupAdapter(list0)
        recyclerGroupView.adapter = contactAdapter0
        if(list0.size > 4)
        {
            val layoutParams = recyclerGroupView.layoutParams
            layoutParams.height = 500
            recyclerGroupView.layoutParams = layoutParams;
        }

        if (bundle != null) {
            contactAdapter0!!.setCheckMode(true)
        }
        contactAdapter1 = ContactAdapter(list1)
        recyclerView.adapter = contactAdapter1
        if (bundle != null) {
            contactAdapter1!!.isCheckMode = true
        }
        query.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                fiter(s.toString(), contactList)
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

    fun updateAdapterData(list: ArrayList<UserEntity>) {
        var contactMapList = HashMap<String, MyFriend>()
        for (i in list) {

            if (contactMapList.get(i.signPublicKey) == null) {
                var myFriend = MyFriend()
                myFriend.userKey = i.signPublicKey
                myFriend.userName = i.nickName
                myFriend.userEntity = i
                var temp = ArrayList<UserEntity>()
                temp.add(i)
                myFriend.routerItemList = temp;
                contactMapList.put(i.signPublicKey, myFriend)
            } else {
                var temp = contactMapList.get(i.signPublicKey)
                var contactNewList = temp!!.routerItemList
                contactNewList.add(i)
            }

        }

        var contactNewList = arrayListOf<MyFriend>()
        var contactNewListValues = contactMapList.values
        for (i in contactNewListValues) {
            contactNewList.add(i)
        }
        contactNewList.sortBy {
            it.userName
        }
        val list1 = arrayListOf<MultiItemEntity>()
        var isIn = false
        contactNewList.forEach {
            var userHead = UserHead()
            userHead.userName = it.userName
            userHead.userEntity = it.userEntity
            if (it.routerItemList.size > 1) {
                it.routerItemList?.forEach {
                    userHead.addSubItem(UserItem(it))
                }
            }
            list1.add(userHead)
        }
        contactAdapter1!!.setNewData(list1)
    }


    fun fiter(key: String, contactList: ArrayList<UserEntity>) {
        var contactListTemp: ArrayList<UserEntity> = arrayListOf<UserEntity>()
        for (i in contactList) {
            if (i.nickSouceName.toLowerCase().contains(key)) {
                contactListTemp.add(i)
            }
        }
        updateAdapterData(contactListTemp)
//        contactAdapter!!.setNewData(contactListTemp)
    }

    fun selectOrCancelAll() {
//        var itemCount =  contactAdapter!!.itemCount -1
//
//        for (i in 0..itemCount) {
//            var checkBox =  contactAdapter!!.getViewByPosition(recyclerView,i,R.id.checkBox) as CheckBox
//            checkBox.setChecked(!checkBox.isChecked)
//        }
//        var count :Int = 0;
//        for (i in 0..itemCount) {
//            var checkBox =  contactAdapter!!.getViewByPosition(recyclerView,i,R.id.checkBox) as CheckBox
//            if(checkBox.isChecked)
//            {
//                count ++
//            }
//        }
//        EventBus.getDefault().post(SelectFriendChange(count,0))
    }

    fun getAllSelectedFriend(): ArrayList<UserEntity> {
        var contactList = arrayListOf<UserEntity>()
//        var itemCount =  contactAdapter1!!.itemCount -1
        for (i in 0 until contactAdapter1!!.data.size) {
            if (contactAdapter1!!.data.get(i).getItemType() == 0) {
                val userHead = contactAdapter1!!.data.get(i) as UserHead
                if (userHead.subItems == null || userHead.subItems.size == 0) {
                    if (userHead.isChecked) {
                        contactList.add(userHead.userEntity)
                    }
                } else {
                    for (j in 0 until userHead.subItems.size) {
                        val userItem = userHead.subItems[j]
                        if (userItem.isChecked) {
                            contactList.add(userItem.userEntity)
                        }
                    }
                }
            }
        }
        return contactList!!
    }
    fun getAllSelectedGroup(): ArrayList<GroupEntity> {
        var contactList = arrayListOf<GroupEntity>()
        contactAdapter0!!.data.forEachIndexed { index, it ->
                var checkBox =  contactAdapter0!!.getViewByPosition(recyclerGroupView,index,R.id.checkBox) as CheckBox
                if(checkBox.isChecked)
                {
                    contactList.add(it)
                }
        }
        return contactList!!
    }
    override fun setupFragmentComponent() {
        DaggerContactAndGroupComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .contactAndGroupModule(ContactAndGroupModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: ContactAndGroupContract.ContactAndGroupContractPresenter) {
        mPresenter = presenter as ContactAndGroupPresenter
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
        if (AppConfig.instance.messageReceiver != null)
            AppConfig.instance.messageReceiver!!.pullFriendCallBack = null
    }
}