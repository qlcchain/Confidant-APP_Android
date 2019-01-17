package com.stratagile.pnrouter.ui.activity.conversation

import android.os.Bundle
import com.message.ui.ChatFragment
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.UserEntity
import com.stratagile.pnrouter.ui.activity.conversation.component.DaggerConversationComponent
import com.stratagile.pnrouter.ui.activity.conversation.contract.ConversationContract
import com.stratagile.pnrouter.ui.activity.conversation.module.ConversationModule
import com.stratagile.pnrouter.ui.activity.conversation.presenter.ConversationPresenter
import javax.inject.Inject


/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: $description
 * @date 2018/09/13 16:38:48
 */

class ConversationActivity : BaseActivity(), ConversationContract.View {

    @Inject
    internal lateinit var mPresenter: ConversationPresenter

    var keyboardHeight = 0

    var userEntity: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    lateinit var fragment : ChatFragment

    override fun initView() {
        setContentView(R.layout.activity_conversation)
        userEntity = intent.getParcelableExtra("user")
        fragment = ChatFragment.newInstance(userEntity!!)
        var transaction = getSupportFragmentManager ().beginTransaction();
        transaction.add(R.id.fragment, fragment, "tag")
        transaction.commit()
    }

    override fun onBackPressed() {
        fragment.onBackPressed()
    }

    override fun initData() {
        KLog.i("")

    }

    override fun setupActivityComponent() {
        DaggerConversationComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .conversationModule(ConversationModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: ConversationContract.ConversationContractPresenter) {
        mPresenter = presenter as ConversationPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun closeKeyboad() {

    }

}