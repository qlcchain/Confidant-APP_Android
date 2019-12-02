package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle
import com.hyphenate.easeui.utils.PathUtils
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.LocalFileMenu
import com.stratagile.pnrouter.db.LocalFileMenuDao
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.PicMenu
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicEncryptionComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.PicMenuEncryptionAdapter
import com.stratagile.pnrouter.utils.DeleteUtils
import com.stratagile.pnrouter.utils.PopWindowUtil
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.picencry_menu_list.*
import java.io.File

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/11/21 15:26:11
 */

class PicEncryptionActivity : BaseActivity(), PicEncryptionContract.View {

    @Inject
    internal lateinit var mPresenter: PicEncryptionPresenter
    var picMenuEncryptionAdapter: PicMenuEncryptionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.picencry_menu_list)
        title.text = getString(R.string.Default_album)

    }
    override fun initData() {
        var _this = this;
        var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.Type.eq("0")).list()
        picMenuEncryptionAdapter = PicMenuEncryptionAdapter(picMenuList)
        recyclerViewPicEncry.adapter = picMenuEncryptionAdapter
        picMenuEncryptionAdapter!!.setOnItemClickListener { adapter, view, position ->
            var taskFile = picMenuEncryptionAdapter!!.getItem(position)
            //startActivity(Intent(activity!!, PdfViewActivity::class.java).putExtra("fileMiPath", taskFile!!.fileName).putExtra("file", fileListChooseAdapter!!.data[position]))
        }
        picMenuEncryptionAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.menuItem ->
                {

                }
                R.id.btnDelete ->
                {
                    var parentRoot = view.parent as SwipeMenuLayout
                    parentRoot.quickClose()
                    runOnUiThread {
                        SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                                .setContentText(getString(R.string.Are_you_sure_you_want_to_delete_the_folder))
                                .setConfirmClickListener {
                                    var data = picMenuEncryptionAdapter!!.getItem(position)
                                    var filePath = data!!.path;
                                    DeleteUtils.deleteDirectory(filePath)
                                    AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.delete(data)
                                    picMenuEncryptionAdapter!!.remove(position)
                                    picMenuEncryptionAdapter!!.notifyDataSetChanged()
                                }
                                .show()
                    }
                }
                R.id.btnRename ->
                {
                    var parentRoot = view.parent as SwipeMenuLayout
                    parentRoot.quickClose()
                    var choosePosition = position
                    PopWindowUtil.showRenameFolderWindow(this@PicEncryptionActivity, addMenuItem, object : PopWindowUtil.OnSelectListener {
                        override fun onSelect(position: Int, obj: Any) {
                            var map = obj as HashMap<String,String>
                            var foldername = map.get("foldername") as String
                            var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.FileName.eq(foldername)).list()
                           if(picMenuList != null && picMenuList.size > 0)
                           {
                               toast(R.string.This_name_folder_already_exists)
                               return;
                           }
                            var data = picMenuEncryptionAdapter!!.getItem(choosePosition)
                            data!!.fileName = foldername
                            AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.update(data)
                            picMenuEncryptionAdapter!!.notifyItemChanged(choosePosition)
                        }
                    })
                }
            }
        }
        addMenuItem.setOnClickListener()
        {
            PopWindowUtil.showCreateFolderWindow(this@PicEncryptionActivity, addMenuItem, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    var map = obj as HashMap<String,String>
                    var foldername = map.get("foldername") as String
                    var localFileMenu = LocalFileMenu();
                    try {
                        var defaultfolder  = PathUtils.getInstance().getEncryptionPath().toString() + foldername
                        var defaultfolderFile = File(defaultfolder)
                        if(!defaultfolderFile.exists())
                        {
                            defaultfolderFile.mkdirs();
                            localFileMenu.creatTime = System.currentTimeMillis();
                            localFileMenu.fileName = foldername
                            localFileMenu.path = defaultfolder
                            localFileMenu.fileNum = 0L
                            localFileMenu.type = "0"
                            AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.insert(localFileMenu)
                        }
                    }catch (e:Exception)
                    {
                        toast(R.string.This_name_folder_already_exists)
                    }
                    if(localFileMenu.fileName != null)
                    {
                        picMenuEncryptionAdapter!!.addData(localFileMenu)
                        picMenuEncryptionAdapter!!.notifyDataSetChanged()
                    }else{
                        toast(R.string.This_name_folder_already_exists)
                    }

                }
            })
        }
    }

    override fun setupActivityComponent() {
        DaggerPicEncryptionComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .picEncryptionModule(PicEncryptionModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: PicEncryptionContract.PicEncryptionContractPresenter) {
        mPresenter = presenter as PicEncryptionPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}