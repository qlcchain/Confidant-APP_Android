package com.stratagile.pnrouter.ui.activity.conversation

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.conversation.component.DaggerFileEncryptionComponent
import com.stratagile.pnrouter.ui.activity.conversation.contract.FileEncryptionContract
import com.stratagile.pnrouter.ui.activity.conversation.module.FileEncryptionModule
import com.stratagile.pnrouter.ui.activity.conversation.presenter.FileEncryptionPresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.ui.activity.encryption.ContactsEncryptionActivity
import com.stratagile.pnrouter.ui.activity.encryption.PicEncryptionActivity
import com.stratagile.pnrouter.ui.activity.encryption.WeiXinEncryptionActivity
import kotlinx.android.synthetic.main.fragment_file_encryption.*

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.conversation
 * @Description: $description
 * @date 2019/11/20 10:12:15
 */

class FileEncryptionFragment : BaseFragment(), FileEncryptionContract.View {

    @Inject
    lateinit internal var mPresenter: FileEncryptionPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_file_encryption, null);

        return view
    }


    override fun setupFragmentComponent() {
        DaggerFileEncryptionComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .fileEncryptionModule(FileEncryptionModule(this))
                .build()
                .inject(this)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        albumMenuRoot.setOnClickListener {
            var intent =  Intent(activity!!, PicEncryptionActivity::class.java)
            startActivity(intent);
        }
        wechatMenu.setOnClickListener {
            var intent =  Intent(activity!!, WeiXinEncryptionActivity::class.java)
            startActivity(intent);
        }
        contactsParent.setOnClickListener {
            var intent =  Intent(activity!!, ContactsEncryptionActivity::class.java)
            startActivity(intent);
        }
    }
    override fun setPresenter(presenter: FileEncryptionContract.FileEncryptionContractPresenter) {
        mPresenter = presenter as FileEncryptionPresenter
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