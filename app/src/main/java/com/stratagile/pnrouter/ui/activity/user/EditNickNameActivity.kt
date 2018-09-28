package com.stratagile.pnrouter.ui.activity.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.events.EditNickName
import com.stratagile.pnrouter.ui.activity.user.component.DaggerEditNickNameComponent
import com.stratagile.pnrouter.ui.activity.user.contract.EditNickNameContract
import com.stratagile.pnrouter.ui.activity.user.module.EditNickNameModule
import com.stratagile.pnrouter.ui.activity.user.presenter.EditNickNamePresenter
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.activity_edit_nick_name.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/09/12 13:20:58
 */

class EditNickNameActivity : BaseActivity(), EditNickNameContract.View {

    @Inject
    internal lateinit var mPresenter: EditNickNamePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_edit_nick_name)
    }
    override fun initData() {
        if (intent.hasExtra("flag")) {
            title.text = "Alias"
            etNickName.setText(intent.getStringExtra("alias"))
            etNickName.setSelection(intent.getStringExtra("alias").length)
            etNickName.hint = "Edit alias"
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
            } else {
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
                    return true
                }
                if (!intent.hasExtra("flag")) {
                    SpUtil.putString(this, ConstantValue.username, etNickName.text.toString().trim())
                    var routerList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
                    routerList.forEach {
                        it.username = etNickName.text.toString().trim()
                        AppConfig.instance.mDaoMaster!!.newSession().update(it)
                    }
                    EventBus.getDefault().post(EditNickName())
                }
                onBackPressed()
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

}