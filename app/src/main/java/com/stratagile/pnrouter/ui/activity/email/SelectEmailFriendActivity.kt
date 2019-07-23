package com.stratagile.pnrouter.ui.activity.email

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.email.component.DaggerSelectEmailFriendComponent
import com.stratagile.pnrouter.ui.activity.email.contract.SelectEmailFriendContract
import com.stratagile.pnrouter.ui.activity.email.module.SelectEmailFriendModule
import com.stratagile.pnrouter.ui.activity.email.presenter.SelectEmailFriendPresenter
import com.stratagile.pnrouter.ui.activity.email.view.*
import kotlinx.android.synthetic.main.email_selectfriend.*
import java.util.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.email
 * @Description: $description
 * @date 2019/07/23 17:37:47
 */

class SelectEmailFriendActivity : BaseActivity(), SelectEmailFriendContract.View {

    @Inject
    internal lateinit var mPresenter: SelectEmailFriendPresenter
    private var adapter: SortAdapter? = null
    private var SourceDateList: List<ContactSortModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.email_selectfriend)
    }
    override fun initData() {
        initDatas()
        initEvents()
        setAdapter()
    }

    private fun setAdapter() {
        SourceDateList = filledData(resources.getStringArray(R.array.contacts))
        Collections.sort(SourceDateList, PinyinComparator())
        adapter = SortAdapter(this, SourceDateList)
        lv_contact.setAdapter(adapter)
    }

    private fun initEvents() {
        //设置右侧触摸监听
        sidrbar.setOnTouchingLetterChangedListener(object : SideBar.OnTouchingLetterChangedListener {
          override fun onTouchingLetterChanged(s: String) {
                //该字母首次出现的位置
                val position = adapter!!.getPositionForSection(s.get(0).toInt())
                if (position != -1) {
                    lv_contact.setSelection(position + 1)
                }
            }
        })

        //ListView的点击事件
        lv_contact.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            tv_title.setText((adapter!!.getItem(position - 1) as ContactSortModel).getName())
            Toast.makeText(application, (adapter!!.getItem(position) as ContactSortModel).getName(), Toast.LENGTH_SHORT).show()
        })

        //根据输入框输入值的改变来过滤搜索
        et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString())
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun initDatas() {
        sidrbar.setTextView(dialog)
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private fun filterData(filterStr: String) {
        var mSortList: MutableList<ContactSortModel> = ArrayList<ContactSortModel>()
        if (TextUtils.isEmpty(filterStr)) {
            mSortList = SourceDateList as MutableList<ContactSortModel>
        } else {
            mSortList.clear()
            var tempSourceDateList = SourceDateList as MutableList<ContactSortModel>
            for (sortModel in tempSourceDateList) {
                val name = sortModel.getName()
                if (name.toUpperCase().indexOf(filterStr.toUpperCase()) != -1 || PinyinUtils.getPingYin(name).toUpperCase().startsWith(filterStr.toUpperCase())) {
                    mSortList.add(sortModel)
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(mSortList, PinyinComparator())
        adapter!!.updateListView(mSortList)
    }

    private fun filledData(date: Array<String>): List<ContactSortModel> {
        val mSortList = ArrayList<ContactSortModel>()
        val indexString = ArrayList<String>()

        for (i in date.indices) {
            val sortModel = ContactSortModel()
            sortModel.setName(date[i])
            val pinyin = PinyinUtils.getPingYin(date[i])
            val sortString = pinyin.substring(0, 1).toUpperCase()
            if (sortString.matches("[A-Z]".toRegex())) {
                sortModel.setSortLetters(sortString.toUpperCase())
                if (!indexString.contains(sortString)) {
                    indexString.add(sortString)
                }
            }
            mSortList.add(sortModel)
        }
        Collections.sort(indexString)
        sidrbar.setIndexText(indexString)
        return mSortList
    }
    override fun setupActivityComponent() {
       DaggerSelectEmailFriendComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .selectEmailFriendModule(SelectEmailFriendModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SelectEmailFriendContract.SelectEmailFriendContractPresenter) {
            mPresenter = presenter as SelectEmailFriendPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}