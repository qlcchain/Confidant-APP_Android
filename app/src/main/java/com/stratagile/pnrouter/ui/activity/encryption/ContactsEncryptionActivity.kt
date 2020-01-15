package com.stratagile.pnrouter.ui.activity.encryption

import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.Contacts.Data
import android.provider.ContactsContract.RawContacts
import android.widget.Toast
import com.alibaba.fastjson.JSONObject
import com.hyphenate.easeui.utils.PathUtils
import com.luck.picture.lib.entity.LocalMedia
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.LocalFileItem
import com.stratagile.pnrouter.db.LocalFileMenu
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.FileStatus
import com.stratagile.pnrouter.entity.file.FileOpreateType
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerContactsEncryptionComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.ContactsEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.module.ContactsEncryptionModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.ContactsEncryptionPresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import kotlinx.android.synthetic.main.picencry_contacts_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import javax.inject.Inject


/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2020/01/07 15:46:53
 */

class ContactsEncryptionActivity : BaseActivity(), ContactsEncryptionContract.View , PNRouterServiceMessageReceiver.BakAddrUserNumCallback{
    override fun bakFileBack(jBakFileRsp: JBakFileRsp) {
        if(jBakFileRsp.params.retCode == 0)
        {
            runOnUiThread {
                closeProgressDialog()
                toast(R.string.success)
            }

        }else{
            runOnUiThread {
                closeProgressDialog()
                toast(R.string.fail)
            }
        }
    }

    override fun bakAddrUserNum(jBakAddrUserNumRsp: JBakAddrUserNumRsp) {


        if(jBakAddrUserNumRsp.params.retCode == 0)
        {
            runOnUiThread {
                nodeContacts.text = jBakAddrUserNumRsp.params.num.toString();
            }
            if(isRecover)
            {
                var filledUri = "https://" + ConstantValue.currentRouterIp + ConstantValue.port + jBakAddrUserNumRsp.params.fpath
                var fileSavePath =   PathUtils.getInstance().getEncryptionContantsNodePath().toString()
                var fileName = "contants.vcf"
                var fileNameBase58 = Base58.encode(fileName.toByteArray())
                var msgID = (System.currentTimeMillis() / 1000) .toInt()
                FileDownloadUtils.doDownLoadWork(filledUri,fileNameBase58, fileSavePath, this, msgID, handlerDownLoad, jBakAddrUserNumRsp.params.fkey,"0")
                isRecover = false;
            }else{
                runOnUiThread {
                    closeProgressDialog()
                }
            }
        }else{
            runOnUiThread {
                closeProgressDialog()
            }
        }
    }
    internal var handlerDownLoad: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {
                    var data: Bundle = msg.data;
                    var msgId = data.getInt("msgID")
                    runOnUiThread {
                        toast(getString(R.string.Download_failure))
                    }
                }
                0x55 -> {
                    var data: Bundle = msg.data;
                    var msgId = data.getInt("msgID")
                    var fileSavePath =   PathUtils.getInstance().getEncryptionContantsNodePath().toString() +"/contants.vcf"
                    addContact(fileSavePath)
                    //restore(this@ContactsEncryptionActivity,fileSavePath)
                }
            }//goMain();
            //goMain();
        }
    }

    fun restore(context: Context, filePath: String) {
        val intent = Intent()
        intent.setPackage("com.android.contacts")
        val uri = Uri.fromFile(File(filePath))
        intent.setAction(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "text/x-vcard")
        context.startActivity(intent)
    }

    /**
     * 导入联系人入口
     */
    private fun addContact(vcfPath:String ) {
        if (!File(vcfPath).exists()) {
            Toast.makeText(this, "文件不存在!", Toast.LENGTH_SHORT).show()
            return
        }
        if (addThread != null) {
            addThread!!.interrupt()
            addThread = null
        }
        addThread = Thread(AddRunnable(this, vcfPath))
        createDialog(this, "警告", "确保你是第一次导入，重复导入会创建新的联系人，请慎用！")
    }

    /**
     * 创建提示对话框
     *
     * @param context
     * @param title
     * @param message
     */
    private fun createDialog(context: Context, title: String, message: String) {
        SweetAlertDialog(this, SweetAlertDialog.BUTTON_NEUTRAL)
                .setContentText(getString(R.string.Delete_original_file))
                .setConfirmClickListener {

                    startAddContact()
                }
                .show()
    }

    /**
     * 开启导入线程
     */
    private fun startAddContact() {
        setAddWidgetEnabled(false)
        if (addThread != null) {
            addThread!!.start()
        }
    }

    internal inner class AddRunnable(private val context: Context, private val path: String) : Runnable {

        override fun run() {
            val result = importContact(context, path)
            if (result) {
                handler.sendEmptyMessage(ADD_SUCCESS)
            } else {
                handler.sendEmptyMessage(ADD_FAIL)
            }
        }
    }

    /**
     * 处理UI相关的handler
     */
    private val handler = object : Handler() {
        override  fun handleMessage(msg: Message) {
            when (msg.what) {
                ADD_FAIL -> {
                    //show.setText("导入联系人失败")
                    setAddWidgetEnabled(true)
                }
                ADD_SUCCESS -> {
                    //show.setText(String.format("导入联系人成功 %d 条，失败 %d 条",successCount, failCount))
                    setAddWidgetEnabled(true)
                }
            }
        }
    }

    /**
     * 设置导入组件的可用性
     *
     * @param enabled
     */
    private fun setAddWidgetEnabled(enabled: Boolean) {
        //btn.setEnabled(enabled)
        if (!enabled) {
            //show.setText("")
        }
    }

    /**
     * 导入联系人
     *
     * @param context
     * @param path
     * @return
     */
    private fun importContact(context: Context, path: String): Boolean {
        successCount = 0
        failCount = 0
        try {
            val addressBeans = ImportVCFUtil.importVCFFileContact(path)
            for (i in 0 until addressBeans.size) {
                val info = addressBeans.get(i)
                if (doAddContact(context, info)) {
                    successCount++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    /**
     * 读取联系人并封装成ContactInfo对象集合
     *
     * @param path
     * @return contactsList
     */
    private fun readFromFile(path: String): ArrayList<ContactInfo>? {
        val strsList = doReadFile(path) ?: return null
        return handleReadStrs(strsList)
    }

    /**
     * 将读出来的内容封装成ContactInfo对象集合
     *
     * @param strsList
     * @return
     */
    private fun handleReadStrs(strsList: ArrayList<String>): ArrayList<ContactInfo> {
        val contactsList = ArrayList<ContactInfo>()
        for (i in 0 until strsList.size) {
            val info = strsList[i]
            val infos = info.split("\\s{2,}".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            var displayName: String? = null
            var mobileNum: String? = null
            var homeNum: String? = null
            if(infos.size == 0)
            {
                continue
            }
            when (infos.size) {
                1 -> displayName = infos[0]
                2 -> {
                    displayName = infos[0]
                    if (infos[1].length >= 11) {
                        mobileNum = infos[1]
                    } else {
                        homeNum = infos[1]
                    }
                }
                else -> {
                    // length >= 3
                    displayName = infos[0]
                    mobileNum = infos[1]
                    homeNum = infos[2]
                }
            }
            if (displayName == null || "" == displayName) {
                failCount++
                continue
            }
            contactsList.add(ContactInfo(displayName, mobileNum, homeNum))
        }
        return contactsList
    }

    /**
     * 读取文件内容
     *
     * @param path
     * @return
     */
    private fun doReadFile(path: String): ArrayList<String>? {
        var `in`: FileInputStream? = null
        val arrayList = ArrayList<String>()
        try {
            val tempbytes = ByteArray(1 shl 24)
            `in` = FileInputStream(path)
            while (`in`!!.read(tempbytes) !== -1) {
                var length = 0
                var first = length
                for (i in tempbytes.indices) {
                    if (tempbytes[i] == '\n'.toByte()) {
                        length = i
                        val nowBytes = ByteArray(length - first)
                        System.arraycopy(tempbytes, first, nowBytes, 0, length - first)
                        arrayList.add(String(nowBytes, Charset.forName("UTF-8")).trim { it <= ' ' })
                        first = i + 1
                    }
                }

            }
        } catch (e1: Exception) {
            return null
        } finally {
            if (`in` != null) {
                try {
                    `in`!!.close()
                } catch (e1: IOException) {
                    return null
                }

            }
        }
        return arrayList
    }

    /**
     * 向数据库表插入联系人信息
     *
     * @param context
     * @param contactInfo
     * @return
     */
    private fun doAddContact(context: Context, contactInfo: AddressBean): Boolean {
        try {
            val contentValues = ContentValues()
            val uri = context.contentResolver.insert(
                    RawContacts.CONTENT_URI, contentValues)
            val rowId = ContentUris.parseId(uri)

            val name = contactInfo.trueName
            val mobileNum = contactInfo.mobile
            val homeNum = contactInfo.mobile

            // 插入姓名
            if (name != null) {
                contentValues.clear()
                contentValues.put(Data.RAW_CONTACT_ID, rowId)
                contentValues.put(Data.MIMETYPE,
                        StructuredName.CONTENT_ITEM_TYPE)
                val index = name!!.length / 2
                var givenName: String? = null
                var familyName: String? = null
                // 检查是否是英文名称
                if (checkEnglishName(name!!) == false) {
                    givenName = name!!.substring(index)
                    familyName = name!!.substring(0, index)
                } else {
                    familyName = name
                    givenName = familyName
                }
                contentValues.put(StructuredName.DISPLAY_NAME, name)
                contentValues.put(StructuredName.GIVEN_NAME, givenName)
                contentValues.put(StructuredName.FAMILY_NAME, familyName)
                context.contentResolver.insert(
                        ContactsContract.Data.CONTENT_URI, contentValues)
            }

            if (mobileNum != null) {
                // 插入手机电话
                contentValues.clear()
                contentValues.put(Data.RAW_CONTACT_ID, rowId)
                contentValues.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                contentValues.put(Phone.NUMBER, mobileNum)
                contentValues.put(Phone.TYPE, Phone.TYPE_MOBILE)
                context.contentResolver.insert(
                        ContactsContract.Data.CONTENT_URI, contentValues)
            }

            if (homeNum != null) {
                // 插入家庭号码
                contentValues.clear()
                contentValues.put(Data.RAW_CONTACT_ID, rowId)
                contentValues.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                contentValues.put(Phone.NUMBER, homeNum)
                contentValues.put(Phone.TYPE, Phone.TYPE_HOME)
                context.contentResolver.insert(
                        ContactsContract.Data.CONTENT_URI, contentValues)
            }
        } catch (e: Exception) {
            return false
        }

        return true
    }

    /**
     * 检查是否是英文名称
     *
     * @param name
     * @return
     */
    private fun checkEnglishName(name: String): Boolean {
        val nameChars = name.toCharArray()
        for (i in nameChars.indices) {
            if (nameChars[i] >= 'a' && nameChars[i] <= 'z' || nameChars[i] >= 'A' && nameChars[i] <= 'Z') {
                continue
            }
            return false
        }
        return true
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
    var msgID = 0
    var fileAESKey:String? = null;
    var isRecover = false;
    var addThread: Thread? = null// 增加联系人线程
    var ADD_FAIL = 0// 导入失败标识
    var ADD_SUCCESS = 1// 导入成功标识
    var successCount = 0// 导入成功的计数
    var failCount = 0// 导入失败的计数

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

            if(fileStatus.complete)
            {
                var fileID = fileStatus.fileKey.substring(fileStatus.fileKey.indexOf("##")+2,fileStatus.fileKey.indexOf("__"))
                var toPath = PathUtils.getInstance().getEncryptionContantsLocalPath().toString()+"/contants.vcf";
                var file = File(toPath)
                if (file.exists()) {
                    var fileId = fileID;
                    var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                    var fileMD5 = ""

                    var fileTempPath  = PathUtils.getInstance().getEncryptionContantsLocalPath().toString() +"/"+ "temp"
                    var fileTempPathFile = File(fileTempPath)
                    if(!fileTempPathFile.exists()) {
                        fileTempPathFile.mkdirs();
                        DeleteUtils.deleteDirectorySubs(PathUtils.getInstance().getEncryptionContantsLocalPath().toString() +"/"+ "temp")//删除外部查看文件的临时路径
                    }
                    fileTempPath += "/contants.vcf"
                    val code = FileUtil.copySdcardToxFileAndEncrypt(toPath, fileTempPath, fileAESKey!!.substring(0, 16))
                    if (code == 1) {
                        fileMD5 = FileUtil.getFileMD5(File(fileTempPath))
                    }
                    val fileNameBase58 = Base58.encode("contants.vcf".toByteArray())
                    var SrcKey = ByteArray(256)
                    SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileAESKey!!, ConstantValue.libsodiumpublicMiKey!!))
                    val bakFileReq = BakFileReq(4, selfUserId!!, 6, fileId.toInt(), file.length(), fileMD5, fileNameBase58, String(SrcKey), localContacts.text.toString(), 0xF0, "AddrBook", "BakFile")
                    if (ConstantValue.isWebsocketConnected) {
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(6, bakFileReq))
                    } else if (ConstantValue.isToxConnected) {
                        val baseData = BaseData(6, bakFileReq)
                        val baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "")
                        if (ConstantValue.isAntox) {
                        } else {
                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.currentRouterId.substring(0, 64))
                        }
                    }
                }

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
            iconArray = arrayListOf<String>("sheet_added","sheet_cover")
            PopWindowUtil.showPopMenuWindow(this@ContactsEncryptionActivity, selectNodeBtn,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    KLog.i("" + position)
                    var data = obj as FileOpreateType
                    when (data.icon) {
                        "sheet_added" -> {

                        }
                        "sheet_cover" -> {
                            var toPath = PathUtils.getInstance().getEncryptionContantsLocalPath().toString()+"/contants.vcf";
                            var file = File(toPath)
                            if (file.exists()) {
                                runOnUiThread { showProgressDialog() }
                                msgID = (System.currentTimeMillis() / 1000).toInt()
                                fileAESKey = RxEncryptTool.generateAESKey()
                                FileMangerUtil.sendContantsFile(toPath,msgID, false,6,fileAESKey)
                            }
                        }
                    }
                }

            })


        }
        recoveryBtn.setOnClickListener {


            var menuArray = arrayListOf<String>()
            var iconArray = arrayListOf<String>()
            menuArray = arrayListOf<String>(getString(R.string.Recover_merge),getString(R.string.Recover_replace))
            iconArray = arrayListOf<String>("sheet_added","sheet_cover")
            PopWindowUtil.showPopMenuWindow(this@ContactsEncryptionActivity, selectNodeBtn,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                override fun onSelect(position: Int, obj: Any) {
                    KLog.i("" + position)
                    var data = obj as FileOpreateType
                    when (data.icon) {
                        "sheet_added" -> {

                        }
                        "sheet_cover" -> {
                            isRecover = true;
                            getNodeData();
                        }
                    }
                }

            })


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