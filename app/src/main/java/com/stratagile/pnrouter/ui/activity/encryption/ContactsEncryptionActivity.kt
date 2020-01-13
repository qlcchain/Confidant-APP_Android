package com.stratagile.pnrouter.ui.activity.encryption

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import com.hyphenate.easeui.utils.PathUtils
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.FileUploadItem
import com.stratagile.pnrouter.db.FileUploadItemDao
import com.stratagile.pnrouter.db.LocalFileItem
import com.stratagile.pnrouter.db.LocalFileMenu
import com.stratagile.pnrouter.entity.BakAddrUserNumReq
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.FilesListPullReq
import com.stratagile.pnrouter.entity.JBakAddrUserNumRsp
import com.stratagile.pnrouter.entity.events.FileStatus
import com.stratagile.pnrouter.entity.file.FileOpreateType
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerContactsEncryptionComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.ContactsEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.module.ContactsEncryptionModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.ContactsEncryptionPresenter
import com.stratagile.pnrouter.ui.activity.file.FileTaskListActivity
import com.stratagile.pnrouter.utils.*
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.picencry_contacts_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/01/07 15:46:53
 */

class ContactsEncryptionActivity : BaseActivity(), ContactsEncryptionContract.View , PNRouterServiceMessageReceiver.BakAddrUserNumCallback{
    override fun bakAddrUserNum(jBakAddrUserNumRsp: JBakAddrUserNumRsp) {

        runOnUiThread {
            closeProgressDialog()
        }
        if(jBakAddrUserNumRsp.params.retCode == 0)
        {
            runOnUiThread {
                localContacts.text = jBakAddrUserNumRsp.params.num.toString();
            }
        }else{

        }
    }

    override fun getScanPermissionSuccess() {


        var toPath = PathUtils.getInstance().getEncryptionContantsLocalPath().toString()+"/contants.vcf";
        var result = FileUtil.exportContacts(this,toPath);
        if(result)
        {
            var fromPath = toPath;
            val addressBeans = ImportVCFUtil.importVCFFileContact(fromPath)
            if(addressBeans!= null)
            {
                localContacts.text = addressBeans!!.size.toString();
            }
        }
    }

    @Inject
    internal lateinit var mPresenter: ContactsEncryptionPresenter
    var localMediaUpdate: LocalMedia? = null

    var chooseFileData: LocalFileItem? = null;
    var chooseFolderData: LocalFileMenu? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.picencry_contacts_list)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileStatusChange(fileStatus: FileStatus) {
        if (fileStatus.result == 1) {
            toast(R.string.File_does_not_exist)
        } else if (fileStatus.result == 2) {
            toast(R.string.Files_100M)
        } else if (fileStatus.result == 3) {
            toast(R.string.Files_0M)
        }else {

            var  fileKey = fileStatus.fileKey;
            var fileId = fileKey.substring(fileKey.indexOf("__")+2,fileKey.length);
            var picItemList = AppConfig.instance.mDaoMaster!!.newSession().fileUploadItemDao.queryBuilder().where(FileUploadItemDao.Properties.FileId.eq(fileId)).list()
            if(picItemList ==null || picItemList.size == 0)
            {
                var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                var fileUploadItem = FileUploadItem();
                fileUploadItem.localFileItemId = 0L;
                fileUploadItem.depens = 4;
                fileUploadItem.userId = selfUserId;
                fileUploadItem.type = 6
                fileUploadItem.fileId = fileId
                fileUploadItem.size = chooseFileData!!.fileSize
                val fileMD5 = FileUtil.getFileMD5(File(chooseFileData!!.filePath))
                fileUploadItem.md5 = fileMD5;
                val fileNameBase58 = Base58.encode(chooseFileData!!.fileName.toByteArray())
                fileUploadItem.setfName(fileNameBase58);
                fileUploadItem.setfKey(chooseFileData!!.srcKey);
                if(chooseFileData!!.fileInfo == null)
                {
                    fileUploadItem.setfInfo("");
                }else{
                    fileUploadItem.setfInfo(chooseFileData!!.fileInfo);
                }
                fileUploadItem.pathId = chooseFolderData!!.nodeId;
                val folderNameBase58 = Base58.encode(chooseFolderData!!.fileName.toByteArray())
                fileUploadItem.pathName =folderNameBase58;
                AppConfig.instance.mDaoMaster!!.newSession().fileUploadItemDao.insert(fileUploadItem)
            }

        }
    }
    override fun initData() {
        EventBus.getDefault().register(this)
        AppConfig.instance.messageReceiver?.bakAddrUserNumCallback = this
        title.text = getString(R.string.Album_Contacts)
        selectNodeBtn.setOnClickListener {
            var menuArray = arrayListOf<String>()
            var iconArray = arrayListOf<String>()
            menuArray = arrayListOf<String>(getString(R.string.Incremental_updating),getString(R.string.Coverage_update))
            iconArray = arrayListOf<String>("statusbar_download_node","sheet_rename")
            PopWindowUtil.showPopMenuWindow(this@ContactsEncryptionActivity, selectNodeBtn,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    KLog.i("" + position)
                    var data = obj as FileOpreateType
                    when (data.icon) {
                        "statusbar_download_node" -> {

                        }
                        "sheet_rename" -> {
                            var toPath = PathUtils.getInstance().getEncryptionContantsLocalPath().toString()+"/contants.vcf";
                            var file = File(toPath)
                            if (file.exists()) {
                                var fileTempPath = toPath;
                                localMediaUpdate = LocalMedia()
                                localMediaUpdate!!.path = fileTempPath
                                localMediaUpdate!!.pictureType = "file"
                                var list = arrayListOf<LocalMedia>()
                                list.add(localMediaUpdate!!)
                                chooseFileData = LocalFileItem()
                                chooseFileData!!.filePath = toPath
                                chooseFileData!!.fileSize = file.length()
                                var startIntent = Intent(this@ContactsEncryptionActivity, FileTaskListActivity::class.java)
                                startIntent.putParcelableArrayListExtra(PictureConfig.EXTRA_RESULT_SELECTION, list)
                                startIntent.putExtra("fromPorperty", 4)
                                val fileKey = RxEncryptTool.generateAESKey()
                                var SrcKey = ByteArray(256)
                                SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))
                                chooseFileData!!.srcKey = String(SrcKey);
                                chooseFileData!!.fileInfo = localContacts.text.toString();
                                chooseFolderData!!.nodeId = 0;
                                chooseFileData!!.fileName = "contants.vcf"
                                startIntent.putExtra("aesKey", fileKey)
                                startActivity(startIntent)
                            }
                        }
                    }
                }

            })


        }
        recoveryBtn.setOnClickListener {
            var fromPath = Environment.getExternalStorageDirectory().toString()+"/contants.vcf";
            val addressBeans = ImportVCFUtil.importVCFFileContact(fromPath)
            println(addressBeans.size)
            for (addressBean in addressBeans) {
                println("tureName : " + addressBean.getTrueName())
                println("mobile : " + addressBean.getMobile())
                println("workMobile : " + addressBean.getWorkMobile())
                println("Email : " + addressBean.getEmail())
                println("--------------------------------")
            }
        }
        mPresenter.getScanPermission()
        getNodeData();
    }
    fun getNodeData()
    {
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var filesListPullReq = BakAddrUserNumReq( selfUserId!!, 0)
        var sendData = BaseData(6, filesListPullReq);
        showProgressDialog();
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
    override fun setupActivityComponent() {
        DaggerContactsEncryptionComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .contactsEncryptionModule(ContactsEncryptionModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: ContactsEncryptionContract.ContactsEncryptionContractPresenter) {
        mPresenter = presenter as ContactsEncryptionPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        AppConfig.instance.messageReceiver?.bakAddrUserNumCallback = null
        super.onDestroy()
    }
}