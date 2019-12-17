package com.stratagile.pnrouter.ui.activity.encryption

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import com.floatball.FloatPermissionManager
import com.huxq17.floatball.libarary.FloatBallManager
import com.huxq17.floatball.libarary.floatball.FloatBallCfg
import com.huxq17.floatball.libarary.menu.FloatMenuCfg
import com.huxq17.floatball.libarary.menu.MenuItem
import com.huxq17.floatball.libarary.utils.BackGroudSeletor
import com.hyphenate.easeui.ui.EaseShowFileVideoActivity
import com.hyphenate.easeui.utils.OpenFileUtil
import com.hyphenate.easeui.utils.PathUtils
import com.hyphenate.util.DensityUtil
import com.luck.picture.lib.PicturePreviewActivity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.observable.ImagesObservable
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.LocalFileItem
import com.stratagile.pnrouter.db.LocalFileItemDao
import com.stratagile.pnrouter.db.LocalFileMenu
import com.stratagile.pnrouter.db.LocalFileMenuDao
import com.stratagile.pnrouter.entity.Sceen
import com.stratagile.pnrouter.entity.events.AddLocalEncryptionItemEvent
import com.stratagile.pnrouter.entity.events.AddWxLocalEncryptionItemEvent
import com.stratagile.pnrouter.entity.file.FileOpreateType
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerWeXinEncryptionListComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.WeXinEncryptionListContract
import com.stratagile.pnrouter.ui.activity.encryption.module.WeXinEncryptionListModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.WeXinEncryptionListPresenter
import com.stratagile.pnrouter.ui.activity.user.MyDetailActivity
import com.stratagile.pnrouter.ui.adapter.conversation.PicItemEncryptionAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.encryption_file_wx_list.*
import kotlinx.android.synthetic.main.layout_encryption_file_list_item.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.*
import java.util.ArrayList

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/11/21 15:27:44
 */

class WeXinEncryptionListActivity : BaseActivity(), WeXinEncryptionListContract.View {

    @Inject
    internal lateinit var mPresenter: WeXinEncryptionListPresenter
    var picItemEncryptionAdapter: PicItemEncryptionAdapter? = null
    var folderInfo: LocalFileMenu? = null
    protected val REQUEST_CODE_MAP = 1
    protected val REQUEST_CODE_CAMERA = 2
    protected val REQUEST_CODE_LOCAL = 3
    protected val REQUEST_CODE_DING_MSG = 4
    protected val REQUEST_CODE_FILE = 5
    protected val REQUEST_CODE_VIDEO = 6
    internal var previewImages: MutableList<LocalMedia> = ArrayList()
    private var mFloatballManager: FloatBallManager? = null
    private var mFloatPermissionManager: FloatPermissionManager? = null
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mImageReader: ImageReader? = null
    private var mWindowManager: WindowManager? = null
    private var mLayoutParams: WindowManager.LayoutParams? = null
    private var mGestureDetector: GestureDetector? = null
    private var resumed: Int = 0
    private var mResultData: Intent? = null
    private var mScreenWidth: Int = 0
    private var mScreenHeight: Int = 0
    private var mScreenDensity: Int = 0
    var REQUEST_MEDIA_PROJECTION = 1008

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.encryption_file_wx_list)

    }
    override fun initData() {
        var _this = this;
        EventBus.getDefault().register(this)
        folderInfo = intent.getParcelableExtra("folderInfo")
        titleShow.text = folderInfo!!.fileName
        initPicPlug()
        var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.queryBuilder().where(LocalFileItemDao.Properties.FileId.eq(folderInfo!!.id)).orderDesc(LocalFileItemDao.Properties.CreatTime).list()
        picItemEncryptionAdapter = PicItemEncryptionAdapter(picMenuList)
        recyclerView.adapter = picItemEncryptionAdapter
        /*picItemEncryptionAdapter!!.setOnItemClickListener { adapter, view, position ->
            var taskFile = picItemEncryptionAdapter!!.getItem(position)
            //startActivity(Intent(activity!!, PdfViewActivity::class.java).putExtra("fileMiPath", taskFile!!.fileName).putExtra("file", fileListChooseAdapter!!.data[position]))
        }*/
        initFlatBall()
        if(mFloatballManager != null)
        {
            mFloatballManager!!.showIFHasPermission()
        }
        picItemEncryptionAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.itemTypeIcon, R.id.itemInfo ->
                {
                    var emaiAttach = picItemEncryptionAdapter!!.getItem(position)
                    var fileName = emaiAttach!!.fileName
                    var filePath= emaiAttach.filePath
                    var fileTempPath  = PathUtils.getInstance().getEncryptionWeChatPath().toString() +"/"+ "temp"
                    var fileTempPathFile = File(fileTempPath)
                    if(!fileTempPathFile.exists()) {
                        fileTempPathFile.mkdirs();
                    }
                    fileTempPath += "/"+fileName;
                    var aesKey = LibsodiumUtil.DecryptShareKey(emaiAttach.srcKey, ConstantValue.libsodiumpublicMiKey!!, ConstantValue.libsodiumprivateMiKey!!)
                    var code = FileUtil.copySdcardToxFileAndDecrypt(filePath,fileTempPath,aesKey)
                    if(code == 1)
                    {
                        if (fileName.contains("jpg") || fileName.contains("JPG")  || fileName.contains("png")) {
                            showImagList(fileTempPath)
                        }else if(fileName.contains("mp4"))
                        {
                            val intent = Intent(AppConfig.instance, EaseShowFileVideoActivity::class.java)
                            intent.putExtra("path", fileTempPath)
                            startActivity(intent)
                        }else{
                            OpenFileUtil.getInstance(AppConfig.instance)
                            val intent = OpenFileUtil.openFile(fileTempPath)
                            startActivity(intent)
                        }
                    }

                }
                R.id.opMenu ->
                {
                    var menuArray = arrayListOf<String>()
                    var iconArray = arrayListOf<String>()
                    menuArray = arrayListOf<String>(getString(R.string.Node_back_up),getString(R.string.Delete))
                    iconArray = arrayListOf<String>("statusbar_download_node","statusbar_delete")
                    PopWindowUtil.showPopMenuWindow(this@WeXinEncryptionListActivity, opMenu,menuArray,iconArray, object : PopWindowUtil.OnSelectListener {
                        override fun onSelect(position: Int, obj: Any) {
                            KLog.i("" + position)
                            var data = obj as FileOpreateType
                            when (data.name) {
                                "Node back up" -> {

                                }
                                "Delete" -> {
                                    SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                                            .setContentText(getString(R.string.Are_you_sure_you_want_to_delete_the_file))
                                            .setConfirmClickListener {
                                                var data = picItemEncryptionAdapter!!.getItem(position)
                                                var filePath = data!!.filePath;
                                                DeleteUtils.deleteFile(filePath)
                                                AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.delete(data)
                                                picItemEncryptionAdapter!!.remove(position)
                                                picItemEncryptionAdapter!!.notifyDataSetChanged()
                                                var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.Id.eq(folderInfo!!.id)).list()
                                                if(picMenuList != null && picMenuList.size > 0)
                                                {
                                                    var picMenuItem = picMenuList.get(0)
                                                    picMenuItem.fileNum -= 1;
                                                    if(picMenuItem.fileNum < 0)
                                                        picMenuItem.fileNum = 0
                                                    AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.update(picMenuItem);
                                                }
                                                EventBus.getDefault().post(AddWxLocalEncryptionItemEvent())
                                            }
                                            .show()
                                }

                            }
                        }

                    })
                }

            }
        }
        allMenu.setOnClickListener()
        {

        }
        addMenu.setOnClickListener()
        {
            setFloatballVisible(true)
        }
        backBtn.setOnClickListener {
            onBackPressed()
        }
    }
    fun showImagList(localPath:String)
    {
        previewImages = ArrayList()
        val selectedImages = ArrayList<LocalMedia>()

        val localMedia = LocalMedia()
        localMedia.isCompressed = false
        localMedia.duration = 0
        localMedia.height = 100
        localMedia.width = 100
        localMedia.isChecked = false
        localMedia.isCut = false
        localMedia.mimeType = 0
        localMedia.num = 0
        localMedia.path = localPath
        localMedia.pictureType = "image/jpeg"
        localMedia.setPosition(0)
        localMedia.sortIndex = 0
        previewImages.add(localMedia)
        ImagesObservable.getInstance().saveLocalMedia(previewImages, "chat")

        val previewImages = ImagesObservable.getInstance().readLocalMedias("chat")
        if (previewImages != null && previewImages.size > 0) {
            val intentPicturePreviewActivity = Intent(this, PicturePreviewActivity::class.java)
            val bundle = Bundle()
            //ImagesObservable.getInstance().saveLocalMedia(previewImages);
            bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, selectedImages as Serializable)
            bundle.putInt(PictureConfig.EXTRA_POSITION, 0)
            bundle.putString("from", "chat")
            intentPicturePreviewActivity.putExtras(bundle)
            startActivity(intentPicturePreviewActivity)
        }
    }
    /**
     * select local image
     * //todo
     */
    protected fun initPicPlug() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())
                .maxSelectNum(9)
                .minSelectNum(1)
                .imageSpanCount(3)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .previewVideo(true)
                .enablePreviewAudio(false)
                .isCamera(false)
                .imageFormat(PictureMimeType.PNG)
                .isZoomAnim(true)
                .sizeMultiplier(0.5f)
                .setOutputCameraPath("/CustomPath")
                .enableCrop(false)
                .compress(false)
                .glideOverride(160, 160)
                .hideBottomControls(false)
                .isGif(false)
                .openClickSound(false)
                .minimumCompressSize(100)
                .synOrAsy(true)
                .rotateEnabled(true)
                .scaleEnabled(true)
                .videoMaxSecond(60 * 60 * 3)
                .videoMinSecond(1)
                .isDragFrame(false)
    }
    /**
     * select local image
     * //todo
     */
    protected fun selectPicFromLocal() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())
                .maxSelectNum(9)
                .minSelectNum(1)
                .imageSpanCount(3)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .previewVideo(true)
                .enablePreviewAudio(false)
                .isCamera(false)
                .imageFormat(PictureMimeType.PNG)
                .isZoomAnim(true)
                .sizeMultiplier(0.5f)
                .setOutputCameraPath("/CustomPath")
                .enableCrop(false)
                .compress(false)
                .glideOverride(160, 160)
                .hideBottomControls(false)
                .isGif(false)
                .openClickSound(false)
                .minimumCompressSize(100)
                .synOrAsy(true)
                .rotateEnabled(true)
                .scaleEnabled(true)
                .videoMaxSecond(60 * 60 * 3)
                .videoMinSecond(1)
                .isDragFrame(false)
                .forResult(REQUEST_CODE_LOCAL)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_LOCAL)
            { // send local image
                KLog.i("选照片或者视频返回。。。")
                val list = data!!.getParcelableArrayListExtra<LocalMedia>(PictureConfig.EXTRA_RESULT_SELECTION)
                KLog.i(list)
                if (list != null && list.size > 0) {
                    var len = list.size
                    for (i in 0 until len) {
                        var file = File(list.get(i).path);
                        var isHas = file.exists();
                        if (isHas) {
                            var filePath = list.get(i).path
                            val imgeSouceName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length)
                            val fileMD5 = FileUtil.getFileMD5(File(filePath))
                            var picItemList = AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.queryBuilder().where(LocalFileItemDao.Properties.FileMD5.eq(fileMD5), LocalFileItemDao.Properties.FileId.eq(folderInfo!!.id)).list()
                            if(picItemList != null && picItemList.size > 0)
                            {
                                toast(imgeSouceName+" "+getString( R.string.file_already_exists))
                                continue;
                            }

                            var fileSize = file.length();
                            val fileKey = RxEncryptTool.generateAESKey()
                            var SrcKey = ByteArray(256)
                            SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))

                            val base58files_dir = folderInfo!!.path +"/"+imgeSouceName
                            val code = FileUtil.copySdcardToxFileAndEncrypt(list.get(i).path, base58files_dir, fileKey.substring(0, 16))
                            if (code == 1) {
                                var localFileItem = LocalFileItem();
                                localFileItem.filePath = base58files_dir;
                                localFileItem.fileName = imgeSouceName;
                                localFileItem.fileSize = fileSize;
                                localFileItem.creatTime = System.currentTimeMillis()
                                localFileItem.fileMD5 = fileMD5;
                                val MsgType = imgeSouceName.substring(imgeSouceName.lastIndexOf(".") + 1)
                                when (MsgType) {
                                    "png", "jpg", "jpeg", "webp" ->  localFileItem.fileType = 1
                                    "amr" ->  localFileItem.fileType = 2
                                    "mp4" ->  localFileItem.fileType = 3
                                    else ->  localFileItem.fileType = 4
                                }
                                localFileItem.fileFrom = 0;
                                localFileItem.autor = "";
                                localFileItem.fileId = folderInfo!!.id;
                                localFileItem.srcKey = String(SrcKey)
                                AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.insert(localFileItem)
                                var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.Id.eq(folderInfo!!.id)).list()
                                if(picMenuList != null && picMenuList.size > 0)
                                {
                                    var picMenuItem = picMenuList.get(0)
                                    picMenuItem.fileNum += 1;
                                    AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.update(picMenuItem);
                                }
                                picItemEncryptionAdapter!!.addData(0,localFileItem)
                                picItemEncryptionAdapter!!.notifyItemChanged(0)
                                toast(imgeSouceName+" "+getString( R.string.Encryption_succeeded))
                                EventBus.getDefault().post(AddWxLocalEncryptionItemEvent())
                            }
                        }

                    }

                } else {
                    Toast.makeText(this, getString(R.string.select_resource_error), Toast.LENGTH_SHORT).show()
                }
            }else if (requestCode == REQUEST_MEDIA_PROJECTION) {
                mResultData = data;

            }
        }
    }

    fun initFlatBall()
    {
        //1 初始化悬浮球配置，定义好悬浮球大小和icon的drawable
        val ballSize = DensityUtil.dip2px(this, 45f)
        val ballIcon = BackGroudSeletor.getdrawble("ic_floatball", this)
        //可以尝试使用以下几种不同的config。
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon);
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.LEFT_CENTER,false);
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.LEFT_BOTTOM, -100);
//        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.RIGHT_TOP, 100);
        val ballCfg = FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.RIGHT_CENTER)
        //设置悬浮球不半隐藏
        ballCfg.setHideHalfLater(false)
        //2 需要显示悬浮菜单
        //2.1 初始化悬浮菜单配置，有菜单item的大小和菜单item的个数
        val menuSize = DensityUtil.dip2px(this, 180f)
        val menuItemSize = DensityUtil.dip2px(this, 40f)
        val menuCfg = FloatMenuCfg(menuSize, menuItemSize)
        //3 生成floatballManager
        mFloatballManager = FloatBallManager(applicationContext, ballCfg, menuCfg)
        addFloatMenuItem()
        setFloatPermission()

        //5 如果没有添加菜单，可以设置悬浮球点击事件
        if (mFloatballManager!!.getMenuItemSize() == 0) {
            mFloatballManager!!.setOnFloatBallClickListener(FloatBallManager.OnFloatBallClickListener { toast("点击了悬浮球") })
        }
        //6 如果想做成应用内悬浮球，可以添加以下代码。
        //application.registerActivityLifecycleCallbacks(mActivityLifeCycleListener)
        createFloatView()
        createImageReader()
        requestCapturePermission()
    }

    private fun createFloatView() {
        mGestureDetector = GestureDetector(applicationContext, FloatGestrueTouchListener())
        mLayoutParams = WindowManager.LayoutParams()
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val metrics = DisplayMetrics()
        mWindowManager!!.getDefaultDisplay().getMetrics(metrics)
        mScreenDensity = metrics.densityDpi
        mScreenWidth = metrics.widthPixels
        mScreenHeight = metrics.heightPixels

        mLayoutParams!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        mLayoutParams!!.format = PixelFormat.RGBA_8888
        // 设置Window flag
        mLayoutParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        mLayoutParams!!.gravity = Gravity.LEFT or Gravity.TOP
        mLayoutParams!!.x = mScreenWidth
        mLayoutParams!!.y = 100
        mLayoutParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT


        //startScreenShot();
    }

    private inner class FloatGestrueTouchListener : GestureDetector.OnGestureListener {
        internal var lastX: Int = 0
        internal var lastY: Int = 0
        internal var paramX: Int = 0
        internal var paramY: Int = 0

        override fun onDown(event: MotionEvent): Boolean {
            lastX = event.rawX.toInt()
            lastY = event.rawY.toInt()
            paramX = mLayoutParams!!.x
            paramY = mLayoutParams!!.y
            return true
        }

        override fun onShowPress(e: MotionEvent) {

        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            startScreenShot()
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            val dx = e2.rawX.toInt() - lastX
            val dy = e2.rawY.toInt() - lastY
            mLayoutParams!!.x = paramX + dx
            mLayoutParams!!.y = paramY + dy
            // 更新悬浮窗位置
            // 更新悬浮窗位置

            return true
        }

        override fun onLongPress(e: MotionEvent) {

        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return false
        }
    }
    private fun createImageReader() {

        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 1)

    }

    fun startScreenShot() {
        /*val handler1 = Handler()
        handler1.postDelayed({
            //start virtual
            startVirtual()
        }, 5)

        handler1.postDelayed({
            //capture the screen
            startCapture()
        }, 30)*/
        if(mFloatballManager != null)
        {
            mFloatballManager!!.hide()
        }
        val handler1 = Handler()
        handler1.postDelayed({
            startVirtual()
            startCapture()
        }, 5)

    }

    fun startVirtual() {
        if (mMediaProjection != null) {
            virtualDisplay()
        } else {
            setUpMediaProjection()
            virtualDisplay()
        }
    }
    private fun addFloatMenuItem() {
        var _this = this;
        val personItem = object : MenuItem(BackGroudSeletor.getdrawble("levitation_desktop", this)) {
            override fun action() {
                goToDesktop(AppConfig.instance)
                mFloatballManager!!.closeMenu()
            }
        }
        val walletItem = object : MenuItem(BackGroudSeletor.getdrawble("levitation_screenshot", this)) {
            override fun action() {

                if(!AppConfig.instance.isBackGroud)//截应用内屏
                {
                    var screenshotsFlag = SpUtil.getString(AppConfig.instance, ConstantValue.screenshotsSetting, "1")
                    if(screenshotsFlag.equals("1"))
                    {
                        runOnUiThread {
                            SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                                    .setContentText(getString(R.string.screen_security))
                                    .setConfirmClickListener {
                                        var intent= Intent(_this, MyDetailActivity::class.java)
                                        intent.putExtra("flag",1)
                                        startActivity(intent)
                                    }
                                    .show()
                        }
                    }else{
                        startScreenShot()
                    }
                }else{
                    startScreenShot()
                }
                mFloatballManager!!.closeMenu()
            }
        }
        val settingItem = object : MenuItem(BackGroudSeletor.getdrawble("levitation_return", this)) {
            override fun action() {
                goToApp(AppConfig.instance)
                mFloatballManager!!.closeMenu()
            }
        }
        val closeItem = object : MenuItem(BackGroudSeletor.getdrawble("levitation_close", this)) {
            override fun action() {
                mFloatballManager!!.hide()
               /* runOnUiThread {
                    SweetAlertDialog(_this, SweetAlertDialog.BUTTON_NEUTRAL)
                            .setContentText(getString(R.string.Are_you_colse))
                            .setConfirmClickListener {

                            }
                            .show()
                }*/

            }
        }
        mFloatballManager!!.addMenuItem(personItem)
                .addMenuItem(settingItem)
                .addMenuItem(walletItem)
                .addMenuItem(closeItem)
                .buildMenu()
    }

    fun requestCapturePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //5.0 之后才允许使用屏幕截图

            return
        }

        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION)
    }
    private fun setFloatballVisible(visible: Boolean) {
        if(mFloatballManager != null)
        {
            if (visible) {
                mFloatballManager!!.show()
            } else {
                mFloatballManager!!.hide()
            }
        }

    }

    fun isApplicationInForeground(): Boolean {
        return resumed > 0
    }
    fun setUpMediaProjection() {
        if (mResultData == null) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            startActivity(intent)
        } else {
            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, mResultData)
        }
    }
    private fun getMediaProjectionManager(): MediaProjectionManager {

        return getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(mFloatballManager != null)
        {
            mFloatballManager!!.showIFHasPermission()
        }
        //mFloatballManager!!.onFloatBallClick()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }
    inner class ActivityLifeCycleListener : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {}

        override fun onActivityStarted(activity: Activity) {}

        override fun onActivityResumed(activity: Activity) {
            ++resumed
            setFloatballVisible(true)
        }

        override fun onActivityPaused(activity: Activity) {
            --resumed
            if (!isApplicationInForeground()) {
                //setFloatballVisible(false);
            }
        }

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivityDestroyed(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    }
    private fun setFloatPermission() {
        // 设置悬浮球权限，用于申请悬浮球权限的，这里用的是别人写好的库，可以自己选择
        //如果不设置permission，则不会弹出悬浮球
        mFloatPermissionManager = FloatPermissionManager()
        mFloatballManager!!.setPermission(object : FloatBallManager.IFloatBallPermission {
            override fun onRequestFloatBallPermission(): Boolean {
                requestFloatBallPermission(this@WeXinEncryptionListActivity)
                return true
            }

            override fun hasFloatBallPermission(context: Context): Boolean {
                return mFloatPermissionManager!!.checkPermission(context)
            }

            override fun requestFloatBallPermission(activity: Activity) {
                mFloatPermissionManager!!.applyPermission(activity)
            }

        })
    }
    private fun startCapture() {

        val image = mImageReader!!.acquireLatestImage()

        if (image == null) {
            startScreenShot()
        } else {
            val mSaveTask = SaveTask()
            mSaveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, image)
            //AsyncTaskCompat.executeParallel(mSaveTask, image);
        }
    }


    inner class SaveTask : AsyncTask<Image, Void, Bitmap>() {

        override fun doInBackground(vararg params: Image): Bitmap? {

            if (params == null || params.size < 1 || params[0] == null) {

                return null
            }

            val image = params[0]

            val width = image.width
            val height = image.height
            val planes = image.planes
            val buffer = planes[0].buffer
            //每个像素的间距
            val pixelStride = planes[0].pixelStride
            //总的间距
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width
            var bitmap: Bitmap? = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
            bitmap!!.copyPixelsFromBuffer(buffer)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
            image.close()
            var fileTempPathFile:File? = null
            var filePic:File? = null
            var fileScreenShotName = com.stratagile.pnrouter.screencapture.FileUtil.getOnlyScreenShotsName();
            var fileTempPath  = PathUtils.getInstance().getEncryptionWeChatPath().toString() +"/"+ "sreenshots"
            if (bitmap != null) {
                try {
                    fileTempPathFile = File(fileTempPath)
                    if(!fileTempPathFile.exists()) {
                        fileTempPathFile.mkdirs();
                    }
                    filePic = File(fileTempPath +"/"+fileScreenShotName)
                    val out = FileOutputStream(filePic)
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                        out.flush()
                        out.close()
                        /*val media = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                        val contentUri = Uri.fromFile(filePic)
                        media.data = contentUri
                        sendBroadcast(media)*/
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    filePic = null
                } catch (e: IOException) {
                    e.printStackTrace()
                    filePic = null
                }
                var needPicPath = fileTempPath +"/"+fileScreenShotName
                var file = File(needPicPath);
                var isHas = file.exists();
                if (isHas) {
                    var filePath = needPicPath
                    val imgeSouceName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length)
                    val fileMD5 = FileUtil.getFileMD5(File(filePath))
                    var picItemList = AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.queryBuilder().where(LocalFileItemDao.Properties.FileMD5.eq(fileMD5),LocalFileItemDao.Properties.FileId.eq(folderInfo!!.id)).list()
                    if(picItemList != null && picItemList.size > 0)
                    {
                        runOnUiThread {
                            DeleteUtils.deleteFile(filePath)
                            toast(imgeSouceName+" "+getString( R.string.file_already_exists))
                        }

                    }else{
                        var fileSize = file.length();
                        val fileKey = RxEncryptTool.generateAESKey()
                        var SrcKey = ByteArray(256)
                        SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))

                        val base58files_dir = folderInfo!!.path +"/"+imgeSouceName
                        val code = FileUtil.copySdcardToxFileAndEncrypt(needPicPath, base58files_dir, fileKey.substring(0, 16))
                        if (code == 1) {
                            DeleteUtils.deleteFile(filePath)
                            var localFileItem = LocalFileItem();
                            localFileItem.filePath = base58files_dir;
                            localFileItem.fileName = imgeSouceName;
                            localFileItem.fileSize = fileSize;
                            localFileItem.creatTime = System.currentTimeMillis()
                            localFileItem.fileMD5 = fileMD5;
                            val MsgType = imgeSouceName.substring(imgeSouceName.lastIndexOf(".") + 1)
                            when (MsgType) {
                                "png", "jpg", "jpeg", "webp" ->  localFileItem.fileType = 1
                                "amr" ->  localFileItem.fileType = 2
                                "mp4" ->  localFileItem.fileType = 3
                                else ->  localFileItem.fileType = 4
                            }
                            localFileItem.fileFrom = 0;
                            localFileItem.autor = "";
                            localFileItem.fileId = folderInfo!!.id;
                            localFileItem.srcKey = String(SrcKey)
                            AppConfig.instance.mDaoMaster!!.newSession().localFileItemDao.insert(localFileItem)
                            var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.Id.eq(folderInfo!!.id)).list()
                            if(picMenuList != null && picMenuList.size > 0)
                            {
                                var picMenuItem = picMenuList.get(0)
                                picMenuItem.fileNum += 1;
                                AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.update(picMenuItem);
                            }
                            runOnUiThread {
                                picItemEncryptionAdapter!!.addData(0,localFileItem)
                                picItemEncryptionAdapter!!.notifyItemChanged(0)
                                toast(imgeSouceName+" "+getString( R.string.Encryption_succeeded))
                            }
                            EventBus.getDefault().post(AddWxLocalEncryptionItemEvent())
                        }
                    }
                }
            }

            return if (filePic != null) {
                bitmap
            } else null
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            super.onPostExecute(bitmap)
            //预览图片
            if (bitmap != null) {

                (application as AppConfig).setmScreenCaptureBitmap(bitmap)
                Log.e("ryze", "获取图片成功")
                if(mFloatballManager != null)
                {
                    mFloatballManager!!.show()
                }
                //toast(R.string.screenshots_success)
                //startActivity(PreviewPictureActivity.newIntent(applicationContext))
            }

        }
    }

    private fun virtualDisplay() {
        if (mMediaProjection != null) {
            mVirtualDisplay = mMediaProjection!!.createVirtualDisplay("screen-mirror",
                    mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader!!.getSurface(), null, null)
        }
    }

    override fun setupActivityComponent() {
        DaggerWeXinEncryptionListComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .weXinEncryptionListModule(WeXinEncryptionListModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: WeXinEncryptionListContract.WeXinEncryptionListContractPresenter) {
        mPresenter = presenter as WeXinEncryptionListPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    /**
     * 跳转到桌面
     */
    fun goToDesktop(context: Context) {

        try {
            var intentWX = Intent(Intent.ACTION_MAIN);
            var cmp =  ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intentWX.addCategory(Intent.CATEGORY_LAUNCHER);
            intentWX.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentWX.setComponent(cmp);
            context.startActivity(intentWX)
        }catch (e:Exception)
        {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(intent)
        }

    }
    /**
     * 跳转到桌面
     */
    fun goToApp(context: Context) {
        /*val intent = Intent("android.intent.action.MAIN")
        var currentActivity =  AppConfig.instance.mAppActivityManager.currentActivity() as Activity
        intent.component = ComponentName(applicationContext.packageName, MainActivity::class.java.name)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)*/
        var launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.stratagile.pnrouter");
        //launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(launchIntent);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun openSceen(screen: Sceen) {
        var screenshotsSettingFlag = SpUtil.getString(AppConfig.instance, ConstantValue.screenshotsSetting, "1")
        if (screenshotsSettingFlag.equals("1")) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
    override fun onDestroy() {
        if(mFloatballManager != null)
        {
            mFloatballManager!!.hide()
        }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}