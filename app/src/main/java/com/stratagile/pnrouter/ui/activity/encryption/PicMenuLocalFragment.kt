package com.stratagile.pnrouter.ui.activity.encryption

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hyphenate.easeui.utils.PathUtils
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.db.LocalFileItemDao
import com.stratagile.pnrouter.db.LocalFileMenu
import com.stratagile.pnrouter.db.LocalFileMenuDao
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicMenuLocalComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicMenuLocalContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicMenuLocalModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicMenuLocalPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.PicMenuEncryptionAdapter
import com.stratagile.pnrouter.utils.DeleteUtils
import com.stratagile.pnrouter.utils.FireBaseUtils
import com.stratagile.pnrouter.utils.PopWindowUtil
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.picencry_menu_list.*
import java.io.File
import javax.inject.Inject

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/12/02 16:00:46
 */

class PicMenuLocalFragment : BaseFragment(), PicMenuLocalContract.View {

    @Inject
    lateinit internal var mPresenter: PicMenuLocalPresenter
    var picMenuEncryptionAdapter: PicMenuEncryptionAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.picencry_menu_list, null);
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var parent = this.activity as Activity;
        var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.Type.eq("0")).list()
        picMenuEncryptionAdapter = PicMenuEncryptionAdapter(picMenuList)
        recyclerViewPicEncry.adapter = picMenuEncryptionAdapter
        /*picMenuEncryptionAdapter!!.setOnItemClickListener { adapter, view, position ->
            var taskFile = picMenuEncryptionAdapter!!.getItem(position)
            //startActivity(Intent(activity!!, PdfViewActivity::class.java).putExtra("fileMiPath", taskFile!!.fileName).putExtra("file", fileListChooseAdapter!!.data[position]))
        }*/
        picMenuEncryptionAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.menuItem ->
                {
                    var data = picMenuEncryptionAdapter!!.getItem(position)
                    var intent =  Intent(activity!!, PicEncryptionlListActivity::class.java)
                    intent.putExtra("folderInfo",data)
                    startActivity(intent)
                }
                R.id.btnDelete ->
                {
                    var parentRoot = view.parent as SwipeMenuLayout
                    parentRoot.quickClose()
                    SweetAlertDialog(this.context, SweetAlertDialog.BUTTON_NEUTRAL)
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
                R.id.btnRename ->
                {
                    var parentRoot = view.parent as SwipeMenuLayout
                    parentRoot.quickClose()
                    var choosePosition = position
                    var data = picMenuEncryptionAdapter!!.getItem(choosePosition)
                    PopWindowUtil.showRenameFolderWindow(parent, addMenuItem,data!!.fileName, object : PopWindowUtil.OnSelectListener {
                        override fun onSelect(position: Int, obj: Any) {
                            var map = obj as HashMap<String,String>
                            var foldername = map.get("foldername") as String
                            var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.FileName.eq(foldername)).list()
                            if(picMenuList != null && picMenuList.size > 0)
                            {
                                toast(R.string.This_name_folder_already_exists)
                                return;
                            }

                            //想命名的原文件夹的路径
                            val file1 = File(data!!.path)
                            //将原文件夹更改为A，其中路径是必要的。注意
                            if(file1.exists())
                            {
                                var path = data!!.path.substring(0,data!!.path.lastIndexOf("/")+1)
                                file1.renameTo(File(path +foldername))
                                data!!.path = path +foldername
                                data!!.fileName = foldername
                                AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.update(data)
                                picMenuEncryptionAdapter!!.notifyItemChanged(choosePosition)
                                var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.queryBuilder().where(LocalFileItemDao.Properties.FileId.eq(data!!.id)).list()
                                for (item in picMenuList)
                                {
                                    var name = item.fileName
                                    var newPath = data!!.path +"/" + name;
                                    item.filePath = newPath;
                                    AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.update(item)
                                }
                            }
                        }
                    })
                }
            }
        }
        addMenuItem.setOnClickListener()
        {
            PopWindowUtil.showCreateFolderWindow(parent, addMenuItem, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    FireBaseUtils.logEvent(activity!!, FireBaseUtils.FIR_FLODER_CREATE)
                    var map = obj as HashMap<String,String>
                    var foldername = map.get("foldername") as String
                    var localFileMenu = LocalFileMenu();
                    try {
                        var defaultfolder  = PathUtils.getInstance().getEncryptionAlbumPath().toString() +"/"+ foldername
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
    public fun upDateUI()
    {
        var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.Type.eq("0")).list()
        picMenuEncryptionAdapter!!.setNewData(picMenuList)
    }
    override fun setupFragmentComponent() {
        DaggerPicMenuLocalComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .picMenuLocalModule(PicMenuLocalModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: PicMenuLocalContract.PicMenuLocalContractPresenter) {
        mPresenter = presenter as PicMenuLocalPresenter
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