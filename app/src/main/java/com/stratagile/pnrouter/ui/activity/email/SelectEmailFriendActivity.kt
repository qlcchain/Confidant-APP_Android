package com.stratagile.pnrouter.ui.activity.email

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v4.view.LayoutInflaterFactory
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.EmailContactsEntity
import com.stratagile.pnrouter.ui.activity.email.component.DaggerSelectEmailFriendComponent
import com.stratagile.pnrouter.ui.activity.email.contract.SelectEmailFriendContract
import com.stratagile.pnrouter.ui.activity.email.module.SelectEmailFriendModule
import com.stratagile.pnrouter.ui.activity.email.presenter.SelectEmailFriendPresenter
import com.stratagile.pnrouter.ui.activity.email.view.*
import kotlinx.android.synthetic.main.abc_list_menu_item_checkbox.*
import kotlinx.android.synthetic.main.email_selectfriend.*
import java.util.*

import javax.inject.Inject;
import android.text.method.Touch.onTouchEvent
import android.view.*
import com.stratagile.pnrouter.ui.activity.router.RouterCreateUserActivity


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
    var oldAdress = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), LayoutInflaterFactory { parent, name, context, attrs ->
            if (name.equals("com.android.internal.view.menu.IconMenuItemView", ignoreCase = true) || name.equals("com.android.internal.view.menu.ActionMenuItemView", ignoreCase = true) || name.equals("android.support.v7.view.menu.ActionMenuItemView", ignoreCase = true)) {
                try {
                    val f = layoutInflater
                    val view = f.createView(name, null, attrs)
                    if (view is TextView) {
                        view.setTextColor(resources.getColor(R.color.mainColor))
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
        setContentView(R.layout.email_selectfriend)
    }
    override fun initData() {
        oldAdress= intent.getStringExtra("oldAdress")
        if(oldAdress != null)
        {
            title.text = getString(R.string.contacts)
        }else{
            title.text = getString(R.string.Forward_to)
        }
        initDatas()
        initEvents()
        setAdapter()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.next, menu)
        //val itemSubmit = menu.findItem(R.id.nextBtn)
        return true
    }

    /*override fun onPrepareOptionsMenu(menu: Menu): Boolean {

        val itemSubmit = menu.findItem(R.id.nextBtn)
        var menuStr = getString(R.string.next)
        itemSubmit.setTitle(Html.fromHtml("<font color='#FF2B2B2B'>"+menuStr+"</font>"));
        invalidateOptionsMenu()
        return super.onPrepareOptionsMenu(menu)
    }*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nextBtn) {
            var count =  lv_contact.childCount  -1
            var selectAdressStr = ""
            var nameAdressStr = ""
            for (position in 0..count)
            {
                var data = adapter!!.getItem(position) as ContactSortModel
                if(data != null && data.isChoose)
                {
                    selectAdressStr += data.account +","
                    nameAdressStr += data.name +","
                }
            }
            var intent = Intent()
            intent.putExtra("selectAdressStr", selectAdressStr)
            intent.putExtra("nameAdressStr", nameAdressStr)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setAdapter() {
        var emailContactsList = AppConfig.instance.mDaoMaster!!.newSession().emailContactsEntityDao.loadAll()
        for (item in emailContactsList)
        {
            if(oldAdress != null && oldAdress.contains(item.account))
            {
                item.choose = true;
            }
        }
        SourceDateList = filledData(emailContactsList)
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
                    lv_contact.setSelection(position)
                }
            }
        })

        //ListView的点击事件
        lv_contact.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            var data = adapter!!.getItem(position) as ContactSortModel
            val checkBox = view!!.findViewById(R.id.checkBox) as CheckBox
            checkBox.isChecked = !checkBox.isChecked
            data.isChoose = checkBox.isChecked
            tv_title.setText((adapter!!.getItem(position) as ContactSortModel).getName())
            //hideSoftInput(this)
            //Toast.makeText(application, (adapter!!.getItem(position) as ContactSortModel).getName(), Toast.LENGTH_SHORT).show()
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

    private fun filledData(date:  List<EmailContactsEntity>): List<ContactSortModel> {
        val mSortList = ArrayList<ContactSortModel>()
        val indexString = ArrayList<String>()

        for (i in date.indices) {
            val sortModel = ContactSortModel()
            sortModel.setName(date[i].name)
            sortModel.setAccount(date[i].account)
            sortModel.isChoose = date[i].choose
            val pinyin = PinyinUtils.getPingYin(date[i].name)
            val sortString = pinyin.substring(0, 1).toUpperCase()
            if (sortString.matches("[A-Z]".toRegex())) {
                sortModel.setSortLetters(sortString.toUpperCase())
                if (!indexString.contains(sortString)) {
                    indexString.add(sortString)
                }
            }else{
                sortModel.setSortLetters("A")
            }
            mSortList.add(sortModel)
        }
        Collections.sort(indexString)
        //sidrbar.setIndexText(indexString)
        return mSortList
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.getAction() === MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(v!!.windowToken, 0)
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)
    }
    override fun setupActivityComponent() {
       DaggerSelectEmailFriendComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .selectEmailFriendModule(SelectEmailFriendModule(this))
               .build()
               .inject(this)
    }
    /**
     * 动态隐藏软键盘
     *
     * @param activity activity
     */
    fun hideSoftInput(activity: Activity) {
        val view = activity.window.peekDecorView()
        if (view != null) {
            val inputmanger = activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputmanger.hideSoftInputFromWindow(view.windowToken, 0)
        }
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