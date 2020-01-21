package com.stratagile.pnrouter.ui.activity.encryption

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.hyphenate.easeui.utils.PathUtils
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.LocalFileMenu
import com.stratagile.pnrouter.db.LocalFileMenuDao
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerSelectNodeMenuComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.SelectNodeMenuContract
import com.stratagile.pnrouter.ui.activity.encryption.module.SelectNodeMenuModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.SelectNodeMenuPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.NodeMenuEncryptionAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.picencry_nodemenu_list.*
import java.io.File

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/12/18 09:47:54
 */

class SelectNodeMenuActivity : BaseActivity(), SelectNodeMenuContract.View , PNRouterServiceMessageReceiver.NodeFileCallback{
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
                    runOnUiThread {
                        //想命名的原文件夹的路径
                        val file1 = File(oldPath)
                        //将原文件夹更改为A，其中路径是必要的。注意
                        if (file1.exists()) {
                            file1.renameTo(File(newPath))
                        }
                        picMenuEncryptionAdapter!!.notifyItemChanged(currentPosition)
                        toast(R.string.success)
                    }
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
                    var localFileMenu: LocalFileMenu = LocalFileMenu();
                    var souceName = String(Base58.decode(jFileActionRsp.params!!.name))
                    var defaultfolder  = PathUtils.getInstance().getEncryptionWeChatPath().toString() +"/"+ souceName
                    localFileMenu.id  = jFileActionRsp.params!!.pathId.toLong();
                    localFileMenu.nodeId = jFileActionRsp.params!!.pathId;
                    localFileMenu.fileName = souceName;
                    localFileMenu.fileNum = 0;
                    localFileMenu.size = 0;
                    localFileMenu.path = defaultfolder
                    localFileMenu.type = "1";
                    localFileMenu.isChoose = false
                    localFileMenu.lastModify = System.currentTimeMillis();
                    runOnUiThread {
                        picMenuEncryptionAdapter!!.addData(localFileMenu)
                        picMenuEncryptionAdapter!!.notifyDataSetChanged()
                        toast(R.string.success)
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
            var flag = 0;
            for (item in jFilePathsPulRsp.params.payload)
            {

                var localFileMenu: LocalFileMenu = LocalFileMenu();
                var souceName = String(Base58.decode(item!!.pathName))
                var defaultfolder  = PathUtils.getInstance().getEncryptionAlbumNodePath().toString() +"/"+ souceName
                localFileMenu.id  = item!!.id.toLong();
                localFileMenu.nodeId = item!!.id;
                localFileMenu.fileName = souceName;
                localFileMenu.fileNum = item!!.filesNum.toLong();
                localFileMenu.size = item!!.size.toLong();
                localFileMenu.path = defaultfolder
                localFileMenu.type = "1";
                localFileMenu.isChoose = false;
                localFileMenu.lastModify = item!!.lastModify.toLong();
                var chooseId = SpUtil.getInt(this,"chooseId",-1);
                if(chooseId != -1)
                {
                    if(item!!.id == chooseId)
                    {
                        localFileMenu.isChoose = true;
                        currentData = localFileMenu;
                    }
                }else{
                    if(flag == 0)
                    {
                        localFileMenu.isChoose = true;
                        currentData = localFileMenu;
                    }
                }

                picMenuList.add(localFileMenu)
                flag++;
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
                llOperate.visibility = View.VISIBLE
                picMenuEncryptionAdapter = NodeMenuEncryptionAdapter(picMenuList)
                recyclerViewNodeMenu.adapter = picMenuEncryptionAdapter
                picMenuEncryptionAdapter!!.setOnItemChildClickListener { adapter, view, position ->
                    when (view.id) {
                        R.id.menuItem -> {
                            var dataList = picMenuEncryptionAdapter!!.data;
                            for(item in dataList)
                            {
                                item.isChoose = false;
                            }
                            var data = picMenuEncryptionAdapter!!.getItem(position)
                            data!!.isChoose = true;
                            picMenuEncryptionAdapter!!.notifyDataSetChanged()
                            currentData = data;
                        }
                        /*R.id.btnDelete -> {
                            var parentRoot = view.parent as SwipeMenuLayout
                            parentRoot.quickClose()

                            SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                                    .setContentText(getString(R.string.Are_you_sure_you_want_to_delete_the_folder))
                                    .setConfirmClickListener {
                                        showProgressDialog()
                                        var data = picMenuEncryptionAdapter!!.getItem(position)
                                        var foldername = data!!.fileName
                                        var base58Name = Base58.encode(foldername.toByteArray())
                                        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                        var filePathsPullReq = FileActionReq(selfUserId!!, 1, 2, 2, 0, data.nodeId, base58Name, "")
                                        var sendData = BaseData(6, filePathsPullReq);
                                        if (ConstantValue.isWebsocketConnected) {
                                            AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
                                        } else if (ConstantValue.isToxConnected) {
                                            var baseData = sendData
                                            var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                            if (ConstantValue.isAntox) {
                                                //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                                //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                            } else {
                                                ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                            }
                                        }
                                        //以下是本地操作数据
                                        currentPath = data!!.path;
                                        currentPosition = position
                                    }
                                    .show()
                        }
                        R.id.btnRename -> {
                            var parentRoot = view.parent as SwipeMenuLayout
                            parentRoot.quickClose()
                            var choosePosition = position
                            var data = picMenuEncryptionAdapter!!.getItem(choosePosition)
                            PopWindowUtil.showRenameFolderWindow(parent!!, addMenuItem, data!!.fileName, object : PopWindowUtil.OnSelectListener {
                                override fun onSelect(position: Int, obj: Any) {
                                    var map = obj as HashMap<String, String>
                                    var folderNewname = map.get("foldername") as String

                                    var foldername = data!!.fileName
                                    var base58NameOld = Base58.encode(foldername.toByteArray())
                                    var base58NameNew = Base58.encode(folderNewname.toByteArray())
                                    var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                                    var filePathsPullReq = FileActionReq(selfUserId!!, 1, 2, 1, 0, data.nodeId, base58NameNew, base58NameOld)
                                    var sendData = BaseData(6, filePathsPullReq);
                                    if (ConstantValue.isWebsocketConnected) {
                                        AppConfig.instance.getPNRouterServiceMessageSender().send(sendData)
                                    } else if (ConstantValue.isToxConnected) {
                                        var baseData = sendData
                                        var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                        if (ConstantValue.isAntox) {
                                            //var friendKey: FriendKey = FriendKey(ConstantValue.currentRouterId.substring(0, 64))
                                            //MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                        } else {
                                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                                        }
                                    }
                                    oldPath = data!!.path
                                    var pathPre = data!!.path.substring(0, data!!.path.lastIndexOf("/") + 1)
                                    newPath = pathPre + folderNewname
                                    currentPosition = choosePosition;
                                    data!!.fileName = folderNewname
                                }
                            })
                        }*/
                    }
                }
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: SelectNodeMenuPresenter
    var picMenuEncryptionAdapter: NodeMenuEncryptionAdapter? = null
    var parentTarget: Activity?= null;
    var currentData:LocalFileMenu? = null;
    var currentPath:String? = null;
    var currentPosition = 0;
    var oldPath:String? = null
    var newPath:String? = null
    var fromType = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.picencry_nodemenu_list)
        title.text = getString(R.string.Select_Folder)
    }
    override fun initData() {
        AppConfig.instance.messageReceiver?.nodeSelectFileCallback = this
        parentTarget = this;
        fromType  = intent.getIntExtra("fromType",1)
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var filePathsPullReq = FilePathsPullReq( selfUserId!!, fromType)
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

        createFolder.setOnClickListener()
        {
            PopWindowUtil.showCreateFolderWindow(parentTarget!!, addMenuItem, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    var map = obj as HashMap<String,String>
                    var foldername = map.get("foldername") as String
                    var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                    var base58Name = Base58.encode(foldername.toByteArray())
                    var filePathsPullReq = FileActionReq( selfUserId!!, fromType,2,3,0,0,base58Name,"")
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
                }
            })
        }
        selectbtn.setOnClickListener()
        {
            if(currentData == null)
            {
                toast(R.string.Please_select_a_folder)
                return@setOnClickListener
            }
            SpUtil.putInt(this,"chooseId",currentData!!.nodeId)
            val intent = Intent()
            intent.putExtra("folderInfo", currentData)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun setupActivityComponent() {
       DaggerSelectNodeMenuComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .selectNodeMenuModule(SelectNodeMenuModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: SelectNodeMenuContract.SelectNodeMenuContractPresenter) {
            mPresenter = presenter as SelectNodeMenuPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.nodeSelectFileCallback = null
        super.onDestroy()
    }
}