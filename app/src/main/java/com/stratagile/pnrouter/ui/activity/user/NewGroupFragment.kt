package com.stratagile.pnrouter.ui.activity.user

import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.user.component.DaggerNewGroupComponent
import com.stratagile.pnrouter.ui.activity.user.contract.NewGroupContract
import com.stratagile.pnrouter.ui.activity.user.module.NewGroupModule
import com.stratagile.pnrouter.ui.activity.user.presenter.NewGroupPresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.wrapper.FriendKey
import com.pawegio.kandroid.runOnUiThread
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.GroupVerifyEntity
import com.stratagile.pnrouter.db.GroupVerifyEntityDao
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.FriendChange
import com.stratagile.pnrouter.entity.events.SetBadge
import com.stratagile.pnrouter.ui.adapter.user.NewGroupMemberAdapter
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.baseDataToJson
import com.stratagile.tox.toxcore.ToxCoreJni
import im.tox.tox4j.core.enums.ToxMessageType
import kotlinx.android.synthetic.main.layout_fragment_recyclerview.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/03/21 19:41:45
 */

class NewGroupFragment : BaseFragment(), NewGroupContract.View, PNRouterServiceMessageReceiver.GroupMemberOpreateBack {

    /**
     * 同意或者拒绝成员入群操作的返回
     */
    override fun groupMemberOpreate(jGroupVerifyRsp: JGroupVerifyRsp) {
        KLog.i("操作返回了。。。")
        if (jGroupVerifyRsp.params.retCode == 0 && opreateGroupVerify != null) {
            KLog.i("更新数据库...")
            KLog.i(opreateGroupVerify.toString())
            AppConfig.instance.mDaoMaster!!.newSession().groupVerifyEntityDao.update(opreateGroupVerify)
        }
        runOnUiThread {
            initData()
        }
        EventBus.getDefault().post(FriendChange(""))
        EventBus.getDefault().post(SetBadge())
        opreateGroupVerify = null
    }

    @Inject
    lateinit internal var mPresenter: NewGroupPresenter

    var newGroupMemberAdapter : NewGroupMemberAdapter? = null

    var opreateGroupVerify : GroupVerifyEntity? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.layout_fragment_recyclerview, null);
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppConfig.instance.messageReceiver?.groupMemberOpreateBack = this
        initData()
    }

    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.groupMemberOpreateBack = null
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshData(jGroupVerifyPushRsp: JGroupVerifyPushRsp) {
        initData()
    }

    fun initData() {
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var verifyList = AppConfig.instance.mDaoMaster!!.newSession().groupVerifyEntityDao.queryBuilder().where(GroupVerifyEntityDao.Properties.UserId.eq(selfUserId)).list()
        if (newGroupMemberAdapter == null) {
            newGroupMemberAdapter = NewGroupMemberAdapter(verifyList)
            recyclerView.adapter = newGroupMemberAdapter
            newGroupMemberAdapter!!.setOnItemChildClickListener { adapter, view, position ->
                when(newGroupMemberAdapter!!.data[position].verifyType) {
                    1 -> {
                        allowUserJoinGroup(newGroupMemberAdapter!!.data[position])
                    }
                }
            }
        }else{
            newGroupMemberAdapter!!.setNewData(verifyList)
        }

    }

    /**
     * 接受用户的入群申请
     */
    fun allowUserJoinGroup(groupVerifyEntity: GroupVerifyEntity) {
        groupVerifyEntity.verifyType = 0
        opreateGroupVerify = groupVerifyEntity
        KLog.i(groupVerifyEntity.toString())
        var groupVerifyReq = GroupVerifyReq(groupVerifyEntity.from, groupVerifyEntity.to, groupVerifyEntity.aduit, groupVerifyEntity.gId, groupVerifyEntity.gname, 0, groupVerifyEntity.userGroupKey)
        var sendgroupVerifyReq = BaseData(groupVerifyReq)
        if(ConstantValue.encryptionType.equals("1"))
        {
            sendgroupVerifyReq = BaseData(4,groupVerifyReq)
        }
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendgroupVerifyReq)
        }else if (ConstantValue.isToxConnected) {
            var baseData = sendgroupVerifyReq
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
    }

    override fun setupFragmentComponent() {
        DaggerNewGroupComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .newGroupModule(NewGroupModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: NewGroupContract.NewGroupContractPresenter) {
        mPresenter = presenter as NewGroupPresenter
    }

    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
}