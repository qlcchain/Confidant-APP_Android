package com.stratagile.pnrouter.ui.activity.encryption

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicMenuNodeComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicMenuNodeContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicMenuNodeModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicMenuNodePresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.hyphenate.easeui.utils.PathUtils
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.pawegio.kandroid.i
import com.pawegio.kandroid.runOnUiThread
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.LocalFileMenu
import com.stratagile.pnrouter.db.LocalFileMenuDao
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.ui.adapter.conversation.PicMenuEncryptionAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.picencry_menu_list.*
import java.io.File

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/12/02 16:04:58
 */

class PicMenuNodeFragment : BaseFragment(), PicMenuNodeContract.View, PNRouterServiceMessageReceiver.NodeFileCallback {
    override fun fileAction(jFileActionRsp: JFileActionRsp) {
        runOnUiThread {
            closeProgressDialog();
        }
        if(jFileActionRsp.params.retCode == 0)
        {
            when(jFileActionRsp.params.react)
            {
                1->
                {

                }
                2->
                {
                    runOnUiThread {
                        DeleteUtils.deleteDirectory(currentPath)
                        picMenuEncryptionAdapter!!.remove(currentPosition)
                        picMenuEncryptionAdapter!!.notifyDataSetChanged()
                        toast(R.string.success)
                    }
                }
                3->
                {
                    var localFileMenu:LocalFileMenu = LocalFileMenu();
                    var souceName = String(Base58.decode(jFileActionRsp.params!!.name))
                    var defaultfolder  = PathUtils.getInstance().getEncryptionWeChatPath().toString() +"/"+ souceName
                    localFileMenu.id  = jFileActionRsp.params!!.fileId.toLong();
                    localFileMenu.nodeId = jFileActionRsp.params!!.fileId;
                    localFileMenu.fileName = souceName;
                    localFileMenu.fileNum = 0;
                    localFileMenu.size = 0;
                    localFileMenu.path = defaultfolder
                    localFileMenu.type = "1";
                    localFileMenu.lastModify = System.currentTimeMillis();
                   runOnUiThread {
                       picMenuEncryptionAdapter!!.addData(localFileMenu)
                       picMenuEncryptionAdapter!!.notifyDataSetChanged()
                   }
                    try {
                        var defaultfolderFile = File(defaultfolder)
                        if(!defaultfolderFile.exists())
                        {
                            defaultfolderFile.mkdirs();
                        }
                    }catch (e:Exception)
                    {

                    }
                }
            }
        }else{
            runOnUiThread {
                toast(R.string.fail)
            }
        }
    }

    override fun filePathsPull(jFilePathsPulRsp: JFilePathsPulRsp) {

        if(jFilePathsPulRsp.params.retCode == 0)
        {
            var picMenuList = mutableListOf<LocalFileMenu>()
            for (item in jFilePathsPulRsp.params.payload)
            {

                var localFileMenu:LocalFileMenu = LocalFileMenu();
                var souceName = String(Base58.decode(item!!.pathName))
                var defaultfolder  = PathUtils.getInstance().getEncryptionWeChatPath().toString() +"/"+ souceName
                localFileMenu.id  = item!!.id.toLong();
                localFileMenu.nodeId = item!!.id;
                localFileMenu.fileName = souceName;
                localFileMenu.fileNum = item!!.filesNum.toLong();
                localFileMenu.size = item!!.size.toLong();
                localFileMenu.path = defaultfolder
                localFileMenu.type = "1";
                localFileMenu.lastModify = item!!.lastModify.toLong();
                picMenuList.add(localFileMenu)
                try {
                    var defaultfolderFile = File(defaultfolder)
                    if(!defaultfolderFile.exists())
                    {
                        defaultfolderFile.mkdirs();
                    }
                }catch (e:Exception)
                {

                }
            }
            runOnUiThread {
                picMenuEncryptionAdapter = PicMenuEncryptionAdapter(picMenuList)
                recyclerViewPicEncry.adapter = picMenuEncryptionAdapter
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
                                        showProgressDialog()
                                        var data = picMenuEncryptionAdapter!!.getItem(position)
                                        var foldername = data!!.fileName
                                        var base58Name = Base58.encode(foldername.toByteArray())
                                        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                        var filePathsPullReq = FileActionReq( selfUserId!!, 1,2,2,0,data.nodeId,base58Name,"")
                                        var sendData = BaseData(6, filePathsPullReq);
                                        if (ConstantValue.isWebsocketConnected) {
                                            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
                                        }else if (ConstantValue.isToxConnected) {
                                            var baseData = sendData
                                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                            if (ConstantValue.isAntox) {
                                                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                            }else{
                                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                            }
                                        }
                                        //以下是本地操作数据
                                        currentPath = data!!.path;
                                        currentPosition = position
                                    }
                                    .show()
                        }
                        R.id.btnRename ->
                        {
                            var parentRoot = view.parent as SwipeMenuLayout
                            parentRoot.quickClose()
                            var choosePosition = position
                            var data = picMenuEncryptionAdapter!!.getItem(choosePosition)
                            PopWindowUtil.showRenameFolderWindow(parent!!, addMenuItem,data!!.fileName, object : PopWindowUtil.OnSelectListener {
                                override fun onSelect(position: Int, obj: Any) {
                                    var map = obj as HashMap<String,String>
                                    var foldername = map.get("foldername") as String
                                    var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.FileName.eq(foldername)).list()
                                    if(picMenuList != null && picMenuList.size > 0)
                                    {
                                        toast(R.string.This_name_folder_already_exists)
                                        return;
                                    }
                                    data!!.fileName = foldername
                                    AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.update(data)
                                    picMenuEncryptionAdapter!!.notifyItemChanged(choosePosition)
                                }
                            })
                        }
                    }
                }
            }
        }
    }

    @Inject
    lateinit internal var mPresenter: PicMenuNodePresenter
    var picMenuEncryptionAdapter: PicMenuEncryptionAdapter? = null
    var parent:Activity?= null;
    var currentData:LocalFileMenu? = null;
    var currentPath:String? = null;
    var currentPosition = 0;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.picencry_menu_list, null);
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AppConfig.instance.messageReceiver?.nodeFileCallback = this
        parent = this.activity as Activity;

        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var filePathsPullReq = FilePathsPullReq( selfUserId!!, 1)
        var sendData = BaseData(6, filePathsPullReq);
        if (ConstantValue.isWebsocketConnected) {
            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
        }else if (ConstantValue.isToxConnected) {
            var baseData = sendData
            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
            if (ConstantValue.isAntox) {
                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
            }else{
                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
            }
        }
        var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.Type.eq("0")).list()

        addMenuItem.setOnClickListener()
        {
            PopWindowUtil.showCreateFolderWindow(parent!!, addMenuItem, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    var map = obj as HashMap<String,String>
                    var foldername = map.get("foldername") as String
                    var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                    var base58Name = Base58.encode(foldername.toByteArray())
                    var filePathsPullReq = FileActionReq( selfUserId!!, 1,2,3,0,0,base58Name,"")
                    var sendData = BaseData(6, filePathsPullReq);
                    if (ConstantValue.isWebsocketConnected) {
                        AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
                    }else if (ConstantValue.isToxConnected) {
                        var baseData = sendData
                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                        if (ConstantValue.isAntox) {
                            //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                            //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                        }else{
                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                        }
                    }
                    /*var localFileMenu = LocalFileMenu();
                    try {
                        var defaultfolder  = PathUtils.getInstance().getEncryptionPath().toString() +"/"+ foldername
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
                    }*/

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
        DaggerPicMenuNodeComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .picMenuNodeModule(PicMenuNodeModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: PicMenuNodeContract.PicMenuNodeContractPresenter) {
        mPresenter = presenter as PicMenuNodePresenter
    }

    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.nodeFileCallback = null
        super.onDestroy()
    }
}