package com.stratagile.pnrouter.ui.activity.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.conversation.FileListFragment
import com.stratagile.pnrouter.ui.activity.main.component.DaggerSearchComponent
import com.stratagile.pnrouter.ui.activity.main.contract.SearchContract
import com.stratagile.pnrouter.ui.activity.main.module.SearchModule
import com.stratagile.pnrouter.ui.activity.main.presenter.SearchPresenter
import kotlinx.android.synthetic.main.activity_search.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2019/08/13 14:06:03
 */

class SearchActivity : BaseActivity(), SearchContract.View {

    @Inject
    internal lateinit var mPresenter: SearchPresenter
    private var chatAndEmailFragment:ChatAndEmailSearchFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_search)
        chatAndEmailFragment = ChatAndEmailSearchFragment()
        val args = Bundle()
        args.putString("from", ConstantValue.chooseFragMentMenu)
        chatAndEmailFragment!!.arguments = args
    }
    override fun initData() {
        if(ConstantValue.chooseFragMentMenu == "Circle")
        {
            title.text = getString(R.string.searchMessage)
        }else{
            title.text = getString(R.string.searchEmail)
        }

        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                when (position) {
                    0 -> {
                        return chatAndEmailFragment!!
                    }
                    else -> return chatAndEmailFragment!!
                }
            }

            override fun getCount(): Int {
                return 1
            }
        }
    }

    override fun setupActivityComponent() {
       DaggerSearchComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .searchModule(SearchModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SearchContract.SearchContractPresenter) {
            mPresenter = presenter as SearchPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}