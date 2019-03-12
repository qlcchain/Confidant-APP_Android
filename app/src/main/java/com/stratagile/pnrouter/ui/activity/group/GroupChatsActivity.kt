package com.stratagile.pnrouter.ui.activity.group

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v4.view.LayoutInflaterFactory
import android.view.InflateException
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.group.component.DaggerGroupChatsComponent
import com.stratagile.pnrouter.ui.activity.group.contract.GroupChatsContract
import com.stratagile.pnrouter.ui.activity.group.module.GroupChatsModule
import com.stratagile.pnrouter.ui.activity.group.presenter.GroupChatsPresenter
import com.stratagile.pnrouter.utils.LogUtil
import kotlinx.android.synthetic.main.fragment_contact.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.group
 * @Description: $description
 * @date 2019/03/12 15:05:01
 */

class GroupChatsActivity : BaseActivity(), GroupChatsContract.View {

    @Inject
    internal lateinit var mPresenter: GroupChatsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), LayoutInflaterFactory { parent, name, context, attrs ->
            if (name.equals("com.android.internal.view.menu.IconMenuItemView", ignoreCase = true) || name.equals("com.android.internal.view.menu.ActionMenuItemView", ignoreCase = true) || name.equals("android.support.v7.view.menu.ActionMenuItemView", ignoreCase = true)) {
                try {
                    val f = layoutInflater
                    val view = f.createView(name, null, attrs)
                    if (view is TextView) {
                        view.setTextColor(resources.getColor(R.color.color_2c2c2c))
                        view.isAllCaps = false
                    }
                    return@LayoutInflaterFactory view
                } catch (e: InflateException) {
                    e.printStackTrace()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

            }
            null
        })
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_group_chats)
    }
    override fun initData() {
        title.text = getString(R.string.group_chat)
    }

    override fun setupActivityComponent() {
       DaggerGroupChatsComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .groupChatsModule(GroupChatsModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: GroupChatsContract.GroupChatsContractPresenter) {
            mPresenter = presenter as GroupChatsPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.create_group, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.create_chat -> {
                startActivity(Intent(this@GroupChatsActivity, CreateGroupActivity::class.java))
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

}