package com.stratagile.pnrouter.ui.activity.encryption

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import com.alibaba.fastjson.JSONObject
import com.hyphenate.easeui.utils.PathUtils
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.FileStatus
import com.stratagile.pnrouter.entity.events.ForegroundCallBack
import com.stratagile.pnrouter.entity.file.FileOpreateType
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerContactsEncryptionComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.ContactsEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.module.ContactsEncryptionModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.ContactsEncryptionPresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import com.stratagile.tox.toxcore.ToxCoreJni
import ezvcard.Ezvcard
import ezvcard.VCard
import kotlinx.android.synthetic.main.picencry_contacts_list.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
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
                isNeedDownLoad = false;
                getNodeData(false);
            }

        }else{
            runOnUiThread {
                closeProgressDialog()
                toast(R.string.fail)
                isNeedDownLoad = false;
                getNodeData(false);
            }
        }
    }

    override fun bakAddrUserNum(jBakAddrUserNumRsp: JBakAddrUserNumRsp) {


        if(jBakAddrUserNumRsp.params.retCode == 0)
        {
            runOnUiThread {
                nodeContacts.text = jBakAddrUserNumRsp.params.num.toString();
            }
            if(isNeedDownLoad)
            {
                if(jBakAddrUserNumRsp.params.fpath != "")
                {
                    var filledUri = "https://" + ConstantValue.currentRouterIp + ConstantValue.port + jBakAddrUserNumRsp.params.fpath
                    var fileSavePath =   PathUtils.getInstance().getEncryptionContantsNodePath().toString()
                    var fileName = "contants.vcf"
                    var fileNameBase58 = Base58.encode(fileName.toByteArray())
                    var msgID = (System.currentTimeMillis() / 1000) .toInt()
                    var fileSavePathDelete =   PathUtils.getInstance().getEncryptionContantsNodePath().toString() +"/contants.vcf"
                    DeleteUtils.deleteFile(fileSavePathDelete);
                    FileDownloadUtils.doDownLoadWork(filledUri,fileNameBase58, fileSavePath, this, msgID, handlerDownLoad, jBakAddrUserNumRsp.params.fkey,"0")
                    isNeedDownLoad = false;
                }else{
                    if(isAddUploadToNode)
                    {
                        isAddUploadToNode = false;
                        msgID = (System.currentTimeMillis() / 1000).toInt()
                        fileAESKey = RxEncryptTool.generateAESKey()
                        var fileLocalPath =   PathUtils.getInstance().getEncryptionContantsLocalPath().toString() +"/contants.vcf"
                        FileMangerUtil.sendContantsFile(fileLocalPath,msgID, false,6,fileAESKey)
                    }else{
                        runOnUiThread {
                            closeProgressDialog()
                            toast(R.string.There_is_no_Node)
                        }
                    }
                    isNeedDownLoad = false;
                }
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
                        closeProgressDialog()
                        toast(getString(R.string.Download_failure))
                    }
                }
                0x55 -> {
                    var data: Bundle = msg.data;
                    var msgId = data.getInt("msgID")
                    if(isAddUploadToNode)//如果是上传
                    {
                        var  vcardsAdd = arrayListOf<VCard>()
                        var fileLocalPath =   PathUtils.getInstance().getEncryptionContantsLocalPath().toString() +"/contants.vcf"
                        var fileSavePath =   PathUtils.getInstance().getEncryptionContantsNodePath().toString() +"/contants.vcf"
                        try {
                            var file = File(fileSavePath!!)
                            if(file.exists())
                            {
                                var  vcards = ImportVCFUtil.importVCFFileContact(fileSavePath)
                                VCardmapLocalToNode = HashMap<String,VCard>();
                                for (vCard in vcards)
                                {
                                    var firstTelephone = vCard.telephoneNumbers.get(0)
                                    var phoneNum = "";
                                    if(firstTelephone!= null)
                                    {
                                        phoneNum = firstTelephone.text;
                                    }
                                    var familyName = "";
                                    var givenName = ""
                                    if(vCard.structuredName!= null)
                                    {
                                        if(vCard.structuredName.family!= null)
                                        {
                                            familyName = vCard.structuredName.family
                                        }
                                        if(vCard.structuredName.given!= null)
                                        {
                                            givenName = vCard.structuredName.given
                                        }
                                    }
                                    VCardmapLocalToNode.put(familyName +"_"+givenName+"_"+phoneNum,vCard);
                                }
                                var  vcardsLocal = ImportVCFUtil.importVCFFileContact(fileLocalPath)
                                if(vcardsLocal.size ==0)
                                {
                                    runOnUiThread {
                                        toast(R.string.There_is_no_contacts)
                                        closeProgressDialog()
                                    }
                                    return;
                                }
                                for (vCard in vcardsLocal)
                                {
                                    var firstTelephone = vCard.telephoneNumbers.get(0)
                                    var phoneNum = "";
                                    if(firstTelephone!= null)
                                    {
                                        phoneNum = firstTelephone.text;
                                    }
                                    var familyName = "";
                                    var givenName = ""
                                    if(vCard.structuredName!= null)
                                    {
                                        if(vCard.structuredName.family!= null)
                                        {
                                            familyName = vCard.structuredName.family
                                        }
                                        if(vCard.structuredName.given!= null)
                                        {
                                            givenName = vCard.structuredName.given
                                        }
                                    }
                                    if(VCardmapLocalToNode.get(familyName +"_"+givenName+"_"+phoneNum) == null)
                                    {
                                        vcardsAdd.add(vCard)
                                    }
                                }
                                if(vcardsAdd.size ==0)
                                {
                                    runOnUiThread {
                                        toast(R.string.No_need_to_update)
                                        closeProgressDialog()
                                    }
                                    return;
                                }
                                for (vCard in vcards)
                                {
                                    vcardsAdd.add(vCard)
                                }
                                if(vcardsAdd.size >0)
                                {
                                    Ezvcard.write(vcardsAdd).go(file);
                                    msgID = (System.currentTimeMillis() / 1000).toInt()
                                    fileAESKey = RxEncryptTool.generateAESKey()
                                    FileMangerUtil.sendContantsFile(fileSavePath,msgID, false,6,fileAESKey)
                                }else{
                                    runOnUiThread {
                                        toast(R.string.success)
                                        closeProgressDialog()
                                    }
                                }

                            }
                        }catch (e:Exception)
                        {

                        }


                    }else{
                        if(isNeedRecoverToLocal)
                        {
                            val uri = Uri.parse("content://com.android.contacts/raw_contacts")
                            contentResolver.delete(uri, "_id!=-1", null)
                            runOnUiThread {
                                localContacts.text = "0";
                            }
                            successCount = 0
                        }else{
                            successCount =  localContacts.text.toString().toInt();
                        }
                        var fileSavePath =   PathUtils.getInstance().getEncryptionContantsNodePath().toString() +"/contants.vcf"
                        addContact(fileSavePath)
                    }
                }
            }//goMain();
            //goMain();
        }
    }


    override fun getScanPermissionSuccess() {
        var count = FileUtil.getContactCount(this@ContactsEncryptionActivity)
        runOnUiThread {
            localContacts.text = count.toString();
        }
    }

    @Inject
    internal lateinit var mPresenter: ContactsEncryptionPresenter
    var msgID = 0
    var fileAESKey:String? = null;
    var isNeedDownLoad = false;
    var isNeedRecoverToLocal = false;//是否覆盖，true 覆盖，false 新增
    var isAddUploadToNode = false;//增量上传 true 增量，false 覆盖
    var addThread: Thread? = null// 增加联系人线程
    var ADD_FAIL = 0// 导入失败标识
    var ADD_SUCCESS = 1// 导入成功标识
    var successCount = 0// 导入成功的计数
    var failCount = 0// 导入失败的计数
    var VCardmapLocalToNode = HashMap<String,VCard>();
    var VCardmapNodeToLocal = HashMap<String,VCard>();
    private lateinit var standaloneCoroutine: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.picencry_contacts_list)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileStatusChange(foregroundCallBack: ForegroundCallBack) {
        if(foregroundCallBack.isForeground)
        {
            mPresenter.getScanPermission()
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileStatusChange(fileStatus: FileStatus) {
        if (fileStatus.result == 1) {
            runOnUiThread {
                toast(R.string.There_is_no_contacts)
                closeProgressDialog()
            }
        } else if (fileStatus.result == 2) {
            runOnUiThread {
                toast(R.string.Files_100M)
                closeProgressDialog()
            }

        } else if (fileStatus.result == 3) {
            runOnUiThread {
                toast(R.string.Files_0M)
                closeProgressDialog()
            }

        }else {

            if(fileStatus.complete)
            {
                var fileID = fileStatus.fileKey.substring(fileStatus.fileKey.indexOf("##")+2,fileStatus.fileKey.indexOf("__"))
                var toPath = PathUtils.getInstance().getEncryptionContantsLocalPath().toString()+"/contants.vcf";
                if(isAddUploadToNode)
                {
                    toPath = PathUtils.getInstance().getEncryptionContantsNodePath().toString()+"/contants.vcf";
                    isAddUploadToNode = false;
                }
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
                    val addressBeans = ImportVCFUtil.importVCFFileContact(toPath)
                    val bakFileReq = BakFileReq(4, selfUserId!!, 6, fileId.toInt(), file.length(), fileMD5, fileNameBase58, String(SrcKey), addressBeans.size.toString(), 0xF0, "AddrBook", "BakFile")
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
        standaloneCoroutine = launch(CommonPool) {
            delay(1000)
        }
        VCardmapNodeToLocal = HashMap<String,VCard>();
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
                            VCardmapLocalToNode = HashMap<String,VCard>();
                            runOnUiThread { showProgressNoCanelDialog(getString(R.string.waiting)) }
                            if (standaloneCoroutine != null)
                                standaloneCoroutine.cancel()
                            standaloneCoroutine = launch(CommonPool) {
                                delay(120000)
                                if (progressDialog.isShow) {
                                    runOnUiThread {
                                        closeProgressDialog()
                                    }
                                }
                            }
                            Thread(Runnable() {
                                run() {
                                    var toPath = PathUtils.getInstance().getEncryptionContantsLocalPath().toString()+"/contants.vcf";
                                    var result = FileUtil.exportContacts(this@ContactsEncryptionActivity,toPath);
                                    if(result)
                                    {
                                        isNeedDownLoad = true;
                                        isNeedRecoverToLocal = false;
                                        isAddUploadToNode = true;
                                        getNodeData(false);
                                    }else{
                                        runOnUiThread {
                                            toast(R.string.fail)
                                            closeProgressDialog()
                                        }
                                    }
                                }
                            }).start()
                        }
                        "sheet_cover" -> {
                            isAddUploadToNode = false;
                            VCardmapLocalToNode = HashMap<String,VCard>();
                            runOnUiThread { showProgressNoCanelDialog(getString(R.string.waiting)) }
                            if (standaloneCoroutine != null)
                                standaloneCoroutine.cancel()
                            standaloneCoroutine = launch(CommonPool) {
                                delay(30000)
                                if (progressDialog.isShow) {
                                    runOnUiThread {
                                        closeProgressDialog()
                                    }
                                }
                            }
                            Thread(Runnable() {
                                run() {

                                    var toPath = PathUtils.getInstance().getEncryptionContantsLocalPath().toString()+"/contants.vcf";
                                    var result = FileUtil.exportContacts(this@ContactsEncryptionActivity,toPath);
                                    if(result)
                                    {
                                        msgID = (System.currentTimeMillis() / 1000).toInt()
                                        fileAESKey = RxEncryptTool.generateAESKey()
                                        FileMangerUtil.sendContantsFile(toPath,msgID, false,6,fileAESKey)
                                    }else{
                                        runOnUiThread {
                                            toast(R.string.fail)
                                            closeProgressDialog()
                                        }
                                    }
                                }
                            }).start()

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
                            SweetAlertDialog(this@ContactsEncryptionActivity, SweetAlertDialog.BUTTON_NEUTRAL)
                                    .setContentText(getString(R.string.Recover_merge_tisp))
                                    .setConfirmClickListener {
                                        runOnUiThread {
                                            showProgressNoCanelDialog(getString(R.string.waiting));
                                        }
                                        if (standaloneCoroutine != null)
                                            standaloneCoroutine.cancel()
                                        standaloneCoroutine = launch(CommonPool) {
                                            delay(120000)
                                            if (progressDialog.isShow) {
                                                runOnUiThread {
                                                    closeProgressDialog()
                                                }
                                            }
                                        }
                                        isNeedDownLoad = true;
                                        isNeedRecoverToLocal = false;
                                        isAddUploadToNode = false;
                                        getNodeData(false);

                                    }
                                    .show()
                        }
                        "sheet_cover" -> {
                            SweetAlertDialog(this@ContactsEncryptionActivity, SweetAlertDialog.BUTTON_NEUTRAL)
                                    .setContentText(getString(R.string.Recover_replace_tisp))
                                    .setConfirmClickListener {
                                        VCardmapNodeToLocal = HashMap<String,VCard>();
                                        isNeedDownLoad = true;
                                        isNeedRecoverToLocal = true;
                                        isAddUploadToNode = false;
                                        getNodeData(true);
                                    }
                                    .show()

                        }
                    }
                }

            })


            var fromPath = Environment.getExternalStorageDirectory().toString()+"/contants.vcf";
            val addressBeans = ImportVCFUtil.importVCFFileContact(fromPath)
            println(addressBeans.size)
            for (addressBean in addressBeans) {
                println("tureName : " + addressBean.structuredName)

                println("--------------------------------")
            }
        }
        mPresenter.getScanPermission()
        getNodeData(true);
    }
    fun getNodeData(show: Boolean)
    {
        var selfUserId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        var filesListPullReq = BakAddrUserNumReq( selfUserId!!, 0)
        var sendData = BaseData(6, filesListPullReq);
        if(show)
        {
            showProgressNoCanelDialog(getString(R.string.waiting));
            if(isNeedRecoverToLocal)
            {
                if (standaloneCoroutine != null)
                    standaloneCoroutine.cancel()
                standaloneCoroutine = launch(CommonPool) {
                    delay(120000)
                    if (progressDialog.isShow) {
                        runOnUiThread {
                            closeProgressDialog()
                        }
                    }
                }
            }else{
                if (standaloneCoroutine != null)
                    standaloneCoroutine.cancel()
                standaloneCoroutine = launch(CommonPool) {
                    delay(30000)
                    if (progressDialog.isShow) {
                        runOnUiThread {
                            closeProgressDialog()
                        }
                    }
                }
            }

        }
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
    /**
     * 导入联系人入口
     */
    private fun addContact(vcfPath:String ) {
        if (!File(vcfPath).exists()) {
            toast(R.string.nofile)
            closeProgressDialog()
            return
        }
        if (addThread != null) {
            addThread!!.interrupt()
            addThread = null
        }
        addThread = Thread(AddRunnable(this, vcfPath))
        startAddContact()
    }
    /**
     * 开启导入线程
     */
    private fun startAddContact() {
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

                    isNeedRecoverToLocal = false;
                    runOnUiThread {
                        toast(R.string.fail)
                        closeProgressDialog()
                    }
                }
                ADD_SUCCESS -> {
                    isNeedRecoverToLocal = false;
                    runOnUiThread {
                        toast(R.string.success)
                        closeProgressDialog()
                    }
                }
            }
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
        failCount = 0
        try {
            val addressBeans = ImportVCFUtil.importVCFFileContact(path)
            if(!isNeedRecoverToLocal)
            {
                var toPath = PathUtils.getInstance().getEncryptionContantsLocalPath().toString()+"/contants.vcf";
                var result = FileUtil.exportContacts(this@ContactsEncryptionActivity,toPath);
                if(result)
                {
                    var  vcards = ImportVCFUtil.importVCFFileContact(toPath)
                    VCardmapNodeToLocal = HashMap<String,VCard>();
                    for (vCard in vcards)
                    {
                        var firstTelephone = vCard.telephoneNumbers.get(0)
                        var phoneNum = "";
                        if(firstTelephone!= null)
                        {
                            phoneNum = firstTelephone.text;
                        }
                        var familyName = "";
                        var givenName = ""
                        if(vCard.structuredName!= null)
                        {
                            if(vCard.structuredName.family!= null)
                            {
                                familyName = vCard.structuredName.family
                            }
                            if(vCard.structuredName.given!= null)
                            {
                                givenName = vCard.structuredName.given
                            }
                        }

                        VCardmapNodeToLocal.put(familyName +"_"+givenName+"_"+phoneNum,vCard);
                    }
                }
            }
            for (i in 0 until addressBeans.size) {
                val info = addressBeans.get(i)
                if(!isNeedRecoverToLocal)
                {

                    var firstTelephone = info.telephoneNumbers.get(0)
                    var phoneNum = "";
                    if(firstTelephone!= null)
                    {
                        phoneNum = firstTelephone.text;
                    }
                    var familyName = "";
                    var givenName = ""
                    if(info.structuredName!= null)
                    {
                        if(info.structuredName.family!= null)
                        {
                            familyName = info.structuredName.family
                        }
                        if(info.structuredName.given!= null)
                        {
                            givenName = info.structuredName.given
                        }
                    }
                    if(VCardmapNodeToLocal.get(familyName +"_"+givenName+"_"+phoneNum) != null)
                    {
                        continue;
                    }
                }
                if (doAddContact(context, info)) {
                    successCount++
                }
                var count = FileUtil.getContactCount(this@ContactsEncryptionActivity)
                runOnUiThread {
                    localContacts.text = count.toString();
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
    private fun doAddContact(context: Context, contactInfo: VCard): Boolean {
        try {
            var operations = ContactOperations(context);
            operations.insertContact(contactInfo);
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
        if (standaloneCoroutine != null)
            standaloneCoroutine.cancel()
    }
    override fun onDestroy() {
        if (standaloneCoroutine != null)
            standaloneCoroutine.cancel()
        EventBus.getDefault().unregister(this)
        AppConfig.instance.messageReceiver?.bakAddrUserNumCallback = null
        super.onDestroy()
    }
}