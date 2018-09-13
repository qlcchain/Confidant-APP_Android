package com.stratagile.pnrouter.ui.activity.main

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.main.component.DaggerFileComponent
import com.stratagile.pnrouter.ui.activity.main.contract.FileContract
import com.stratagile.pnrouter.ui.activity.main.module.FileModule
import com.stratagile.pnrouter.ui.activity.main.presenter.FilePresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.ui.activity.conversation.FileListFragment
import kotlinx.android.synthetic.main.fragment_file.*
import java.util.ArrayList

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 17:32:58
 */

class FileFragment : BaseFragment(), FileContract.View {

    @Inject
    lateinit internal var mPresenter: FilePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_file, null);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var titles = ArrayList<String>()
        titles.add("all files")
        titles.add("received file")
        titles.add("sent file")
        viewPager.setAdapter(object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return FileListFragment()
            }

            override fun getCount(): Int {
                return titles.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles.get(position)
            }
        })
        tabLayout.setupWithViewPager(viewPager)
    }


    override fun setupFragmentComponent() {
        DaggerFileComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .fileModule(FileModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: FileContract.FileContractPresenter) {
        mPresenter = presenter as FilePresenter
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