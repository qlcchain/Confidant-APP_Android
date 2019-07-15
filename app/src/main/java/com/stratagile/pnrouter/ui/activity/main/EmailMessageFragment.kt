package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pawegio.kandroid.runOnUiThread
import com.smailnet.eamil.Callback.GetReceiveCallback
import com.smailnet.eamil.EmailMessage
import com.smailnet.eamil.EmailReceiveClient
import com.smailnet.islands.Islands
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.db.EmailMessageEntity
import com.stratagile.pnrouter.ui.activity.email.EmailInfoActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerEmailMessageComponent
import com.stratagile.pnrouter.ui.activity.main.contract.EmailMessageContract
import com.stratagile.pnrouter.ui.activity.main.module.EmailMessageModule
import com.stratagile.pnrouter.ui.activity.main.presenter.EmailMessagePresenter
import com.stratagile.pnrouter.ui.adapter.conversation.EmaiMessageAdapter
import kotlinx.android.synthetic.main.fragment_mail_list.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2019/07/11 16:19:12
 */

class EmailMessageFragment : BaseFragment(), EmailMessageContract.View {

    @Inject
    lateinit internal var mPresenter: EmailMessagePresenter
    var emaiMessageChooseAdapter : EmaiMessageAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_mail_list, null);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var emailMessageEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.loadAll()
        emaiMessageChooseAdapter = EmaiMessageAdapter(emailMessageEntityList)
        emaiMessageChooseAdapter!!.setOnItemLongClickListener { adapter, view, position ->
            /* val floatMenu = FloatMenu(activity)
             floatMenu.items("菜单1", "菜单2", "菜单3")
             floatMenu.show((activity!! as BaseActivity).point,0,0)*/
            true
        }
        recyclerView.adapter = emaiMessageChooseAdapter
        emaiMessageChooseAdapter!!.setOnItemClickListener { adapter, view, position ->
             var intent = Intent(activity!!, EmailInfoActivity::class.java)
             intent.putExtra("emailMeaasgeData", emaiMessageChooseAdapter!!.getItem(position))
             startActivity(intent)
        }
        refreshLayout.setOnRefreshListener {
            pullMessageList()
            if (refreshLayout != null)
                refreshLayout.isRefreshing = false
        }
    }
    override fun onResume() {
        super.onResume()

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser)
        {
            //pullMessageList()
        }
    }
    override fun setupFragmentComponent() {
        DaggerEmailMessageComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .emailMessageModule(EmailMessageModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: EmailMessageContract.EmailMessageContractPresenter) {
        mPresenter = presenter as EmailMessagePresenter
    }
    fun pullMessageList() {
        Islands.circularProgress(this.activity)
                .setCancelable(false)
                .setMessage("同步中...")
                .show()
                .run { progressDialog ->
                    val emailReceiveClient = EmailReceiveClient(AppConfig.instance.emailConfig())
                    emailReceiveClient
                            .imapReceiveAsyn(this.activity, object : GetReceiveCallback {
                                override fun gainSuccess(messageList: List<EmailMessage>, count: Int) {
                                    var list = messageList;
                                    AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.deleteAll()
                                    for (item in messageList)
                                    {
                                        var eamilMessage = EmailMessageEntity()
                                        eamilMessage.from = item.from
                                        eamilMessage.to = item.to
                                        eamilMessage.subject = item.subject
                                        eamilMessage.content= item.content
                                        eamilMessage.contentText= item.contentText
                                        eamilMessage.date = item.date
                                        AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.insert(eamilMessage)
                                    }
                                    var emailMessageEntityList = AppConfig.instance.mDaoMaster!!.newSession().emailMessageEntityDao.loadAll()
                                    runOnUiThread {
                                        emaiMessageChooseAdapter!!.setNewData(emailMessageEntityList);
                                        progressDialog.dismiss()
                                    }

                                }

                                override fun gainFailure(errorMsg: String) {
                                    progressDialog.dismiss()

                                }
                            },"INBOX")
                }
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