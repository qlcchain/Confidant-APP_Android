package com.stratagile.pnrouter.ui.activity.file

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.support.annotation.IntDef
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import butterknife.ButterKnife
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.data.fileInfo.FileInfo
import com.stratagile.pnrouter.ui.activity.file.component.DaggerFileChooseComponent
import com.stratagile.pnrouter.ui.activity.file.contract.FileChooseContract
import com.stratagile.pnrouter.ui.activity.file.module.FileChooseModule
import com.stratagile.pnrouter.ui.activity.file.presenter.FileChoosePresenter
import io.julian.common.Preconditions
import kotlinx.android.synthetic.main.activity_file_infos.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.qlink.ui.activity.file
 * @Description: $description
 * @date 2018/09/28 16:46:15
 */

class FileChooseActivity : BaseActivity(), FileChooseContract.View {

    var mDirectories = ArrayList<FileInfo>()
    var  mSelectedDirectory: FileInfo? = null

    var mFragmentManager: FragmentManager? = null

    var mMyHandler: MyHandler? = null

    @Inject
    internal lateinit var mPresenter: FileChoosePresenter

    //文件类型，0 代表所有文件，1 代表图片，2 代表文件,不包含图片
    var fileType = 0

    @IntDef(value = [OPERATION_NONE, OPERATION_BACK_PRESSED, OPERATION_SELECTED_TAB, OPERATION_CLICK_ITEM])
    @Retention(RetentionPolicy.SOURCE)
    annotation class Operation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_file_infos)
        ButterKnife.bind(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setTitle(getString(R.string.SELECT_PROFILE))
    }

    override fun initData() {
        fileType = intent.getIntExtra("fileType", 0)
        mMyHandler = MyHandler(this)
        mFragmentManager = supportFragmentManager
        showDirectory(null, OPERATIONROOT_NONE)
        tabLayout!!.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                showDirectory(tab.tag as FileInfo?, OPERATION_SELECTED_TAB)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        var fileInfo = FileInfo(File(Environment.getExternalStorageDirectory().absolutePath+"/Download"))
        showDirectory(fileInfo, OPERATION_CLICK_ITEM)
    }
   /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.choose_allfile, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.chooseAllFile) {
            //toast(getString(R.string.notsupported))
            showDirectory(null, OPERATIONROOT_NONE)
        }
        return super.onOptionsItemSelected(item)
    }*/
    override fun setupActivityComponent() {
        DaggerFileChooseComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .fileChooseModule(FileChooseModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: FileChooseContract.FileInfosContractPresenter) {
        mPresenter = presenter as FileChoosePresenter
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileInfo(fileInfo: FileInfo?) {
        if (fileInfo!!.isFile) {
            val intent = Intent()
            intent.putExtra("path", fileInfo.absolutePath)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            if (fileInfo == null) {

            } else {
                showDirectory(fileInfo, OPERATION_CLICK_ITEM)
            }
        }
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    fun showDirectory(fileInfo: FileInfo?, @Operation operation: Int) {
        KLog.d("showDirectory: $operation")
        when (operation) {
            OPERATION_NONE -> showDirectoryWithNone()
            OPERATION_BACK_PRESSED -> showDirectoryWithBackPressed()
            OPERATION_SELECTED_TAB -> showDirectoryWithSelectedTab(fileInfo)
            OPERATION_CLICK_ITEM -> showDirectoryWithClickItem(fileInfo!!)
            OPERATIONROOT_NONE -> showDirectoryRootWithNone()
            else -> throw IllegalArgumentException(operation.toString() + " is invalid")
        }
    }

    fun showDirectoryWithNone() {
        KLog.d("showDirectoryWithNone11111: " + (if (mSelectedDirectory == null) "null" else mSelectedDirectory!!.name) + ", size: " + mDirectories.size)
        if (mSelectedDirectory == null) {
            var file = File(Environment.getExternalStorageDirectory().absolutePath+"/Download")
            if(file.exists())
            {
                mSelectedDirectory = FileInfo(File(Environment.getExternalStorageDirectory().absolutePath+"/Download"))
            }else{
                mSelectedDirectory = FileInfo(Environment.getExternalStorageDirectory())
            }
            mDirectories.add(mSelectedDirectory!!)
        }

        tabLayout!!.removeAllTabs()
        val size = mDirectories.size
        for (i in 0 until size) {
            val directory = mDirectories[i]
            var directoryname = directory.name
            if(directoryname == "0")
            {
                directoryname = "root"
            }
            if (i == size - 1) {

                tabLayout!!.addTab(tabLayout!!.newTab().setCustomView(R.layout.directory_tab_view_without_arrow)
                        .setText(directoryname).setTag(directory), false)
            } else {
                tabLayout!!.addTab(tabLayout!!.newTab().setCustomView(R.layout.directory_tab_view)
                        .setText(directoryname).setTag(directory), false)
            }
        }

        val ft = mFragmentManager!!.beginTransaction()
        for (directory in mDirectories) {
            val fragment = mFragmentManager!!.findFragmentByTag(directory.absolutePath)
            if (fragment != null && !fragment.isDetached) {
                if (mSelectedDirectory != directory) {
                    ft.detach(fragment)
                }
            }
        }
        var selected = mFragmentManager!!.findFragmentByTag(mSelectedDirectory!!.absolutePath)
        if (selected == null) {
            selected = FileInfosFragment.newInstance(mSelectedDirectory!!.absolutePath, fileType)
            ft.add(R.id.contentFrame, selected!!, mSelectedDirectory!!.absolutePath)
        } else {
            ft.attach(selected)
        }
        ft.commit()

        val msg = mMyHandler!!.obtainMessage()
        msg.arg1 = mDirectories.indexOf(mSelectedDirectory!!)
        KLog.d("showDirectoryWithNone22222: " + (if (mSelectedDirectory == null) "null" else mSelectedDirectory!!.name) + ", size: " + mDirectories.size)
        mMyHandler!!.sendMessageDelayed(msg, 100L)
    }
    fun showDirectoryRootWithNone() {
        //mDirectories.remove(mSelectedDirectory!!)
        KLog.d("showDirectoryWithNone11111: " + (if (mSelectedDirectory == null) "null" else mSelectedDirectory!!.name) + ", size: " + mDirectories.size)
        if (mSelectedDirectory == null) {
            var file = File(Environment.getExternalStorageDirectory().absolutePath)
            if(file.exists())
            {
                mSelectedDirectory = FileInfo(File(Environment.getExternalStorageDirectory().absolutePath))
            }else{
                mSelectedDirectory = FileInfo(Environment.getExternalStorageDirectory())
            }
            mDirectories.add(mSelectedDirectory!!)
        }


        tabLayout!!.removeAllTabs()
        val size = mDirectories.size
        for (i in 0 until size) {
            val directory = mDirectories[i]
            var directoryname = directory.name
            if(directoryname == "0")
            {
                directoryname = "root"
            }
            if (i == size - 1) {
                tabLayout!!.addTab(tabLayout!!.newTab().setCustomView(R.layout.directory_tab_view_without_arrow)
                        .setText(directoryname).setTag(directory), false)
            } else {
                tabLayout!!.addTab(tabLayout!!.newTab().setCustomView(R.layout.directory_tab_view)
                        .setText(directoryname).setTag(directory), false)
            }
        }

        val ft = mFragmentManager!!.beginTransaction()
        for (directory in mDirectories) {
            val fragment = mFragmentManager!!.findFragmentByTag(directory.absolutePath)
            if (fragment != null && !fragment.isDetached) {
                if (mSelectedDirectory != directory) {
                    ft.detach(fragment)
                }
            }
        }
        var selected = mFragmentManager!!.findFragmentByTag(mSelectedDirectory!!.absolutePath)
        if (selected == null) {
            selected = FileInfosFragment.newInstance(mSelectedDirectory!!.absolutePath, fileType)
            ft.add(R.id.contentFrame, selected!!, mSelectedDirectory!!.absolutePath)
        } else {
            ft.attach(selected)
        }
        ft.commit()

        val msg = mMyHandler!!.obtainMessage()
        msg.arg1 = mDirectories.indexOf(mSelectedDirectory!!)
        KLog.d("showDirectoryWithNone22222: " + (if (mSelectedDirectory == null) "null" else mSelectedDirectory!!.name) + ", size: " + mDirectories.size)
        mMyHandler!!.sendMessageDelayed(msg, 100L)
        val len = mDirectories.size
       /* for (i in 0 until len) {
            val directory = mDirectories[i]
            if(directory.name.equals("Download"))
            {
                mDirectories.remove(directory)
                break;
            }
        }*/
    }
    fun showDirectoryWithBackPressed() {
        Preconditions.checkNotNull(mSelectedDirectory!!, "mSelectedDirectory == null")
        Preconditions.checkArgument(mDirectories.contains(mSelectedDirectory!!),
                "mDirectories not contain:" + mSelectedDirectory!!.absolutePath)
        val selectedPosition = mDirectories.indexOf(mSelectedDirectory!!)
        if (selectedPosition == 0) {
            finish()
        } else {
            val ft = mFragmentManager!!.beginTransaction()
            var f = mFragmentManager!!.findFragmentByTag(mSelectedDirectory!!.absolutePath)
            if (f != null && !f.isDetached) {
                ft.detach(f)
            }
            val previousPosition = selectedPosition - 1
            mSelectedDirectory = mDirectories[previousPosition]
            val previous = mSelectedDirectory
            f = mFragmentManager!!.findFragmentByTag(previous!!.getAbsolutePath())
            if (f == null) {
                f = FileInfosFragment.newInstance(previous!!.getAbsolutePath(), fileType)
                ft.add(R.id.contentFrame, f!!, previous.getAbsolutePath())
            } else {
                ft.attach(f)
            }
            ft.commit()

            val msg = mMyHandler!!.obtainMessage()
            msg.arg1 = previousPosition
            mMyHandler!!.sendMessageDelayed(msg, 100L)
        }
    }

    fun showDirectoryWithSelectedTab(fileInfo: FileInfo?) {
        Preconditions.checkNotNull(fileInfo!!, "fileInfo == null")
        Preconditions.checkNotNull(mSelectedDirectory!!, "mSelectedDirectory == null")
        KLog.i("showDirectoryWithSelectedTab: " + fileInfo.name)
        if (mSelectedDirectory !== fileInfo) {
            val ft = mFragmentManager!!.beginTransaction()
            var f = mFragmentManager!!.findFragmentByTag(mSelectedDirectory!!.absolutePath)
            if (f != null && !f.isDetached) {
                ft.detach(f)
            }

            mSelectedDirectory = fileInfo

            f = mFragmentManager!!.findFragmentByTag(fileInfo.absolutePath)
            if (f == null) {
                f = FileInfosFragment.newInstance(fileInfo.absolutePath, fileType)
                ft.add(R.id.contentFrame, f!!, fileInfo.absolutePath)
            } else {
                ft.attach(f)
            }
            ft.commit()
        }
    }

    fun showDirectoryWithClickItem(fileInfo: FileInfo) {
        Preconditions.checkNotNull(fileInfo, "fileInfo == null")
        // 如果用户当前选中的文件夹已经添加到 mDirectories 中
        val selectedPath = fileInfo.absolutePath
        val ft = mFragmentManager!!.beginTransaction()
        val iterator = mDirectories.iterator()
        while (iterator.hasNext()) {
            val child = iterator.next()
            val childPath = child.absolutePath
            if (!selectedPath.contains(childPath)) {
                val f = mFragmentManager!!.findFragmentByTag(childPath)
                if (f != null) {
                    ft.remove(f)
                }
                iterator.remove()
                val tabPosition = getPositionForTab(child)
                if (tabPosition != -1) {
                    tabLayout!!.removeTabAt(tabPosition)
                }
            }
        }
        if (mDirectories.contains(mSelectedDirectory)) {
            val f = mFragmentManager!!.findFragmentByTag(mSelectedDirectory!!.absolutePath)
            if (f != null && !f.isDetached) {
                ft.detach(f)
            }
        } else {
            val f = mFragmentManager!!.findFragmentByTag(mSelectedDirectory!!.absolutePath)
            if (f != null) {
                ft.remove(f)
            }
        }

        if (!mDirectories.contains(fileInfo)) {
            val insertedPosition = mDirectories.size
            if (insertedPosition > 0) {
                val lastPosition = insertedPosition - 1
                val lastTab = tabLayout!!.getTabAt(lastPosition)
                        ?: throw NullPointerException("lastTab == null")
                lastTab.customView = null
                lastTab.setCustomView(R.layout.directory_tab_view)
            }
            mDirectories.add(insertedPosition, fileInfo)
            var fileInfoName = fileInfo.name
            if(fileInfoName == "0")
            {
                fileInfoName = "root"
            }
            tabLayout!!.addTab(tabLayout!!.newTab().setCustomView(R.layout.directory_tab_view_without_arrow)
                    .setText(fileInfoName).setTag(fileInfo), insertedPosition)
        } else {
            val position = mDirectories.indexOf(fileInfo)
            if (position != mDirectories.size - 1) {
                throw IllegalStateException(position.toString() + " is not last one")
            }
            val lastTab = tabLayout!!.getTabAt(position)
                    ?: throw NullPointerException("lastTab == null")
            lastTab.customView = null
            lastTab.setCustomView(R.layout.directory_tab_view_without_arrow)
        }

        var f = mFragmentManager!!.findFragmentByTag(fileInfo.absolutePath)
        if (f == null) {
            f = FileInfosFragment.newInstance(fileInfo.absolutePath, fileType)
            ft.add(R.id.contentFrame, f!!, fileInfo.absolutePath)
        } else {
            ft.attach(f)
        }
        mSelectedDirectory = fileInfo
        ft.commit()

        val msg = mMyHandler!!.obtainMessage()
        msg.arg1 = mDirectories.indexOf(fileInfo)
        mMyHandler!!.sendMessageDelayed(msg, 100L)
    }

    fun getPositionForTab(directory: FileInfo): Int {
        val tabCount = tabLayout!!.tabCount
        for (i in 0 until tabCount) {
            val tag = getTabTag(tabLayout!!.getTabAt(i))
            if (tag == directory) {
                return i
            }
        }
        return -1
    }

    override fun onBackPressed() {
        showDirectory(null, OPERATION_BACK_PRESSED)
    }

    fun getTabTag(tab: TabLayout.Tab?): FileInfo {
        return tab!!.tag as FileInfo? ?: throw NullPointerException("tabTag == null")
    }

    fun selectTab(position: Int) {
        if (position < 0) {
            return
        }
        val tab = (if (position < tabLayout!!.tabCount)
            tabLayout!!.getTabAt(position)
        else
            null) ?: return
        try {
            val method = TabLayout::class.java.getDeclaredMethod("selectTab", TabLayout.Tab::class.java)
            method.isAccessible = true
            method.invoke(tabLayout, tab)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    class MyHandler  constructor(fileInfosActivity: FileChooseActivity) : Handler() {

        val mReference: WeakReference<FileChooseActivity>

        init {
            mReference = WeakReference(fileInfosActivity)
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val fileInfosActivity = mReference.get()
            if (fileInfosActivity != null) {
                val selectedTabPosition = msg.arg1
                fileInfosActivity.selectTab(selectedTabPosition)
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    companion object {

        open   val OPERATION_NONE = 0
        open   val OPERATION_BACK_PRESSED = 1
        open  val OPERATION_SELECTED_TAB = 2
        open  val OPERATION_CLICK_ITEM = 3
        open   val OPERATIONROOT_NONE = 4
        open   val REQUEST_CODE_OPEN_FILE = 10
    }

}