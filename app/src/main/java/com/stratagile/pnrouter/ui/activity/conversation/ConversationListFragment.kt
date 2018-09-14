package com.stratagile.pnrouter.ui.activity.conversation

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.socks.library.KLog

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.conversation.component.DaggerConversationListComponent
import com.stratagile.pnrouter.ui.activity.conversation.contract.ConversationListContract
import com.stratagile.pnrouter.ui.activity.conversation.module.ConversationListModule
import com.stratagile.pnrouter.ui.activity.conversation.presenter.ConversationListPresenter

import javax.inject.Inject;

import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageSender
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.LoginReq
import com.stratagile.pnrouter.ui.activity.main.MainViewModel
import com.stratagile.pnrouter.ui.adapter.conversation.ConversationListAdapter
import com.stratagile.pnrouter.utils.baseDataToJson
import kotlinx.android.synthetic.main.fragment_conversation_list.*
import kotlinx.android.synthetic.main.search_layout.*
import com.stratagile.pnrouter.ui.activity.main.MainActivity
import com.noober.menu.FloatMenu
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.ui.activity.user.AddFreindActivity
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity
import java.util.*


/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: $description
 * @date 2018/09/10 17:25:57
 */

class ConversationListFragment : BaseFragment(), ConversationListContract.View {

    @Inject
    lateinit internal var mPresenter: ConversationListPresenter

    var  messageSender : PNRouterServiceMessageSender? = null

    var coversationListAdapter : ConversationListAdapter? = null

    lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_conversation_list, null)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        viewModel.toAddUserId.observe(this, Observer<String> { toAddUserId ->
            KLog.i(toAddUserId)
            if (!"".equals(toAddUserId)) {
                var intent  = Intent(activity!!, UserInfoActivity::class.java)
                var useEntityList = AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.loadAll()
                for (i in useEntityList) {
                    if (i.userId.equals(toAddUserId)) {
                        intent.putExtra("user", i)
                        startActivity(intent)
                        return@Observer
                    }
                }
                var userEntity = UserEntity()
                userEntity.friendStatus = 7
                userEntity.userId = toAddUserId
                userEntity.nickName = ""
                userEntity.timestamp = Calendar.getInstance().timeInMillis
                AppConfig.instance.mDaoMaster!!.newSession().userEntityDao.insert(userEntity)
                intent.putExtra("user", userEntity)
                startActivity(intent)
            }
        })
        var list = arrayListOf("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        coversationListAdapter = ConversationListAdapter(list)
        coversationListAdapter!!.setOnItemLongClickListener { adapter, view, position ->
            val floatMenu = FloatMenu(activity)
            floatMenu.items("菜单1", "菜单2", "菜单3")
            floatMenu.show((activity!! as BaseActivity).point)
            true
        }
        recyclerView.adapter = coversationListAdapter
        coversationListAdapter!!.setOnItemClickListener { adapter, view, position ->
            var intent = Intent(activity!!, ConversationActivity::class.java)
            startActivity(intent)
        }
//        send.setOnClickListener {
//            if (messageSender == null) {
//                messageSender = AppConfig.instance.getPNRouterServiceMessageSender()
//            }
//            var login = LoginReq("Login", "F574DB6D9136090C75C8CD4132E70CA9938568A6308AD5B2AE00CA86C5E7CA3FF8345E8AE31F", "", 0)
//            var jsonStr = BaseData( login)
//            Log.i("MainActivity", jsonStr.baseDataToJson())
//            messageSender!!.send(jsonStr)
//        }
    }

    override fun setupFragmentComponent() {
        DaggerConversationListComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .conversationListModule(ConversationListModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: ConversationListContract.ConversationListContractPresenter) {
        mPresenter = presenter as ConversationListPresenter
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