package com.stratagile.pnrouter.utils

import android.app.Activity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.*

import com.chad.library.adapter.base.BaseQuickAdapter
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.RecentFile
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.MyFile
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.entity.file.Arrange
import com.stratagile.pnrouter.entity.file.FileOpreateType
import com.stratagile.pnrouter.ui.adapter.file.SelectPictureAdapter
import com.stratagile.pnrouter.ui.adapter.popwindow.FileChooseOpreateAdapter
import com.stratagile.pnrouter.ui.adapter.login.SelectRouterAdapter
import com.stratagile.pnrouter.ui.adapter.popwindow.FileMenuOpreateAdapter
import com.stratagile.pnrouter.ui.adapter.popwindow.FileSortAdapter
import com.stratagile.pnrouter.ui.adapter.popwindow.MenuOpreateAdapter
import com.stratagile.pnrouter.ui.adapter.user.ShareSelfAdapter
import com.stratagile.pnrouter.view.CustomPopWindow
import kotlinx.android.synthetic.main.ease_chat_menu_item.view.*
import kotlinx.android.synthetic.main.email_send_edit.*
import kotlinx.android.synthetic.main.emailpassword_bar.*

import java.util.ArrayList


/**
 * 作者：hu on 2017/6/8
 * 邮箱：365941593@qq.com
 * 描述：
 */

/**
 * 公共的popwindow弹出类。所有的popwindow都可以封装在这个类里边
 */
object PopWindowUtil {
    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showSharePopWindow(activity: Activity, showView: View) {
        val maskView = LayoutInflater.from(activity).inflate(R.layout.share_pop_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.open_fade)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = linearLayoutManager
        val arrayList = ArrayList<ShareBean>()
        arrayList.add(ShareBean("icon_copylink", "Copy Link"))
        arrayList.add(ShareBean("icon_google", "Google"))
        arrayList.add(ShareBean("icon_twitter", "Twitter"))
        arrayList.add(ShareBean("icon_facebook", "Facebook"))
        arrayList.add(ShareBean("icon_linked", "LinkedIn"))
        val shareSelfAdapter = ShareSelfAdapter(arrayList)
        recyclerView.adapter = shareSelfAdapter
        shareSelfAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            AppConfig.instance.toast(arrayList[position].name)
            when (arrayList[position].avatar) {
                "icon_copylink" -> {
                }
                "icon_google" -> {
                }
                "icon_twitter" -> {
                }
                "icon_facebook" -> {
                }
                "icon_linked" -> {
                }
                else -> {
                }
            }
        }
        //对具体的view的事件的处理
        maskView.setOnClickListener { CustomPopWindow.onBackPressed() }

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(false)
                .setContenView(contentView)
                .setFocusable(false)
                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
                .create()
                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0)
    }
    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showSelectRouterPopWindow(activity: Activity, showView: View, onRouterSelectListener : OnSelectListener) {
        val maskView = LayoutInflater.from(activity).inflate(R.layout.select_router_pop_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.open_fade)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        var list = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.loadAll()
        val selecRouterAdapter = SelectRouterAdapter(arrayListOf())
        selecRouterAdapter.setNewData(list)
        recyclerView.adapter = selecRouterAdapter
        selecRouterAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            onRouterSelectListener.onSelect(position, selecRouterAdapter.data[position])
            CustomPopWindow.onBackPressed()
        }
        //对具体的view的事件的处理
        maskView.setOnClickListener { CustomPopWindow.onBackPressed() }

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(false)
                .setContenView(contentView)
                .setFocusable(false)
                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
                .create()
                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0)
    }

    var selecRouterAdapter:SelectPictureAdapter? = null
    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showSelecMenuPopWindow(activity: Activity, showView: View,list:ArrayList<String>, onRouterSelectListener : OnSelectListener) {
        val maskView = LayoutInflater.from(activity).inflate(R.layout.select_router_pop_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.open_fade)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        selecRouterAdapter = SelectPictureAdapter(arrayListOf())
        selecRouterAdapter!!.setNewData(list)
        recyclerView.adapter = selecRouterAdapter
        selecRouterAdapter!!.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            onRouterSelectListener.onSelect(position, selecRouterAdapter!!.data[position])
            CustomPopWindow.onBackPressed()
            selecRouterAdapter = null
        }
        //对具体的view的事件的处理
        maskView.setOnClickListener { CustomPopWindow.onBackPressed() }

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(false)
                .setContenView(contentView)
                .setFocusable(false)
                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
                .create()
                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0)
    }
    fun showSelecMenuPopWindowNotice(list:ArrayList<String>)
    {
        if(selecRouterAdapter != null)
        {
            selecRouterAdapter!!.setNewData(list)
        }

    }
    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showFileOpreatePopWindow(activity: Activity, showView: View, fileName : Any, onRouterSelectListener : OnSelectListener) {
        val maskView = LayoutInflater.from(activity).inflate(R.layout.opreate_file_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.open_fade)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        val tvFileName = contentView.findViewById<TextView>(R.id.fileName)
        tvFileName.text = String(Base58.decode((fileName as JPullFileListRsp.ParamsBean.PayloadBean).fileName))
        recyclerView.layoutManager = linearLayoutManager
        val selecRouterAdapter = FileChooseOpreateAdapter(getFileOpreateType(activity))
        recyclerView.adapter = selecRouterAdapter
        selecRouterAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            onRouterSelectListener.onSelect(position, fileName)
            CustomPopWindow.onBackPressed()
        }
        //对具体的view的事件的处理
        maskView.setOnClickListener { CustomPopWindow.onBackPressed() }

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(false)
                .setContenView(contentView)
                .setFocusable(false)
                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
                .create()
                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0)
    }

    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showFileRecentPopWindow(activity: Activity, showView: View, fileName : Any, onRouterSelectListener : OnSelectListener) {
        val maskView = LayoutInflater.from(activity).inflate(R.layout.opreate_file_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.open_fade)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        val tvFileName = contentView.findViewById<TextView>(R.id.fileName)
        tvFileName.text = (fileName as RecentFile).fileName.substring((fileName as RecentFile).fileName.lastIndexOf("/") + 1)
        recyclerView.layoutManager = linearLayoutManager
        var list = arrayListOf<FileOpreateType>()
        list.add(FileOpreateType("delete_h1", activity.getString(R.string.delete)))
        val selecRouterAdapter = FileChooseOpreateAdapter(list)
        recyclerView.adapter = selecRouterAdapter
        selecRouterAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            onRouterSelectListener.onSelect(position, fileName)
            CustomPopWindow.onBackPressed()
        }
        //对具体的view的事件的处理
        maskView.setOnClickListener { CustomPopWindow.onBackPressed() }

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(false)
                .setContenView(contentView)
                .setFocusable(false)
                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
                .create()
                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0)
    }

    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showFileUploadPopWindow(activity: Activity, showView: View, onRouterSelectListener : OnSelectListener) {
        val maskView = LayoutInflater.from(activity).inflate(R.layout.opreate_file_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        var ll_file = contentView.findViewById<LinearLayout>(R.id.ll_file)
        ll_file.visibility = View.GONE
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        var list = arrayListOf<FileOpreateType>();
        list.add(FileOpreateType("doc_img", activity.getString(R.string.upload_photos)))
        list.add(FileOpreateType("video", activity.getString(R.string.upload_video)))
        list.add(FileOpreateType("ic_upload_document", activity.getString(R.string.upload_document)))
        val selecRouterAdapter = FileChooseOpreateAdapter(list)
        recyclerView.adapter = selecRouterAdapter
        selecRouterAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            onRouterSelectListener.onSelect(position, selecRouterAdapter.data[position])
            CustomPopWindow.onBackPressed()
        }
        maskView.setOnSystemUiVisibilityChangeListener {
            KLog.i("改变了。。。")
        }
        //对具体的view的事件的处理
        maskView.setOnClickListener {
            CustomPopWindow.onBackPressed()
        }

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(false)
                .setContenView(contentView)
                .setFocusable(false)
                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
                .create()
                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0)
    }
    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showPopMenuWindow(activity: Activity, showView: View,menuList: ArrayList<String>,iconList: ArrayList<String>, onRouterSelectListener : OnSelectListener) {
        val maskView = LayoutInflater.from(activity).inflate(R.layout.opreate_file_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        var ll_file = contentView.findViewById<LinearLayout>(R.id.ll_file)
        ll_file.visibility = View.GONE
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        var list = arrayListOf<FileOpreateType>();
        var size = menuList.size
        for (index in 0..size -1){
            list.add(FileOpreateType(iconList[index], menuList[index]))
        }
        /* list.add(FileOpreateType("doc_img", activity.getString(R.string.upload_photos)))
         list.add(FileOpreateType("video", activity.getString(R.string.upload_video)))
         list.add(FileOpreateType("ic_upload_document", activity.getString(R.string.upload_document)))*/
        val selecRouterAdapter = FileChooseOpreateAdapter(list)
        recyclerView.adapter = selecRouterAdapter
        selecRouterAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            CustomPopWindow.onBackPressed()
            onRouterSelectListener.onSelect(position, selecRouterAdapter.data[position])
        }
        maskView.setOnSystemUiVisibilityChangeListener {
            KLog.i("改变了。。。")
        }
        //对具体的view的事件的处理
        maskView.setOnClickListener {
            CustomPopWindow.onBackPressed()
        }

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(false)
                .setContenView(contentView)
                .setFocusable(false)
                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
                .create()
                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0)
    }
    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showPopAddMenuWindow(activity: Activity, showView: View,menuList: ArrayList<String>,iconList: ArrayList<String>, onRouterSelectListener : OnSelectListener) {
        val maskView = LayoutInflater.from(activity).inflate(R.layout.opreate_addmenu_layout, null)
        val contentView = maskView.findViewById<View>(R.id.cardView)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
        //contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        var list = arrayListOf<FileOpreateType>();
        var size = menuList.size
        for (index in 0..size -1){
            list.add(FileOpreateType(iconList[index], menuList[index]))
        }
        /* list.add(FileOpreateType("doc_img", activity.getString(R.string.upload_photos)))
         list.add(FileOpreateType("video", activity.getString(R.string.upload_video)))
         list.add(FileOpreateType("ic_upload_document", activity.getString(R.string.upload_document)))*/
        val selecRouterAdapter = MenuOpreateAdapter(list)
        recyclerView.adapter = selecRouterAdapter
        selecRouterAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            CustomPopWindow.onBackPressed()
            onRouterSelectListener.onSelect(position, selecRouterAdapter.data[position])
        }
        maskView.setOnSystemUiVisibilityChangeListener {
            KLog.i("改变了。。。")
        }
        //对具体的view的事件的处理
        maskView.setOnClickListener {
            CustomPopWindow.onBackPressed()
        }

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(false)
                .setContenView(contentView)
                .setFocusable(false)
                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
                .create()
                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0)
    }
    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showPopKeyMenuWindow(activity: Activity, showView: View,password: String,passTips: String, onRouterSelectListener : OnSelectListener) {
        var isShow = false
        var isShow2 = false
        val maskView = LayoutInflater.from(activity).inflate(R.layout.email_key_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        /* list.add(FileOpreateType("doc_img", activity.getString(R.string.upload_photos)))
         list.add(FileOpreateType("video", activity.getString(R.string.upload_video)))
         list.add(FileOpreateType("ic_upload_document", activity.getString(R.string.upload_document)))*/
        maskView.setOnSystemUiVisibilityChangeListener {
            KLog.i("改变了。。。")
        }
        //对具体的view的事件的处理
        var titleSetPassClose= maskView.findViewById<View>(R.id.titleSetPassClose)
        titleSetPassClose.setOnClickListener {
            CustomPopWindow.onBackPressed()
        }
        var bt_remove= maskView.findViewById<View>(R.id.bt_remove)
        var password_editText= maskView.findViewById<EditText>(R.id.password_editText)
        var password_editText2= maskView.findViewById<EditText>(R.id.password_editText2)
        var passTips_editText= maskView.findViewById<EditText>(R.id.passTips_editText)

        var showandhide= maskView.findViewById<ImageButton>(R.id.showandhide)
        showandhide.setOnClickListener {
            isShow = !isShow
            if (isShow) {
                //如果选中，显示密码
                password_editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showandhide.setImageResource(R.mipmap.tabbar_open)
            } else {
                //否则隐藏密码
                password_editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showandhide.setImageResource(R.mipmap.tabbar_shut)
            }
        }
        var showandhide2= maskView.findViewById<ImageButton>(R.id.showandhide2)
        showandhide2.setOnClickListener {
            isShow2 = !isShow2
            if (isShow2) {
                //如果选中，显示密码
                password_editText2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                showandhide2.setImageResource(R.mipmap.tabbar_open)
            } else {
                //否则隐藏密码
                password_editText2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                showandhide2.setImageResource(R.mipmap.tabbar_shut)
            }
        }
        password_editText!!.setText(password)
        password_editText2!!.setText(password)
        passTips_editText!!.setText(passTips)
        bt_remove.setOnClickListener {
            password_editText!!.setText("")
            password_editText2!!.setText("")
            passTips_editText!!.setText("")
            var map = HashMap<String,String>()
            map.put("password","")
            map.put("passTips","")
            onRouterSelectListener.onSelect(0, map)
            CustomPopWindow.onBackPressed()
        }
        var bt_set= maskView.findViewById<View>(R.id.bt_set)
        bt_set.setOnClickListener {
            var password1= password_editText.text.toString()
            var password2= password_editText2.text.toString()
            var passTips_editText= passTips_editText.text.toString()
            if(password1!= password2)
            {
                activity.toast(R.string.Password_inconsistent)
                return@setOnClickListener
            }
            var map = HashMap<String,String>()
            map.put("password",password1)
            map.put("passTips",passTips_editText)
            onRouterSelectListener.onSelect(0, map)
            CustomPopWindow.onBackPressed()
        }
//对具体的view的事件的处理

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(true)
                .setContenView(contentView)
                .setFocusable(true)
                .setClickCloseEnable(false)
                .setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                .create()
                .showAtLocation(showView, Gravity.BOTTOM, 0, 0)
    }
    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showCreateFolderWindow(activity: Activity, showView: View, onRouterSelectListener : OnSelectListener) {
        var isShow = false
        var isShow2 = false
        val maskView = LayoutInflater.from(activity).inflate(R.layout.createfolder_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        /* list.add(FileOpreateType("doc_img", activity.getString(R.string.upload_photos)))
         list.add(FileOpreateType("video", activity.getString(R.string.upload_video)))
         list.add(FileOpreateType("ic_upload_document", activity.getString(R.string.upload_document)))*/
        maskView.setOnSystemUiVisibilityChangeListener {
            KLog.i("改变了。。。")
        }
        //对具体的view的事件的处理
        var titleSetPassClose= maskView.findViewById<View>(R.id.titleSetPassClose)
        titleSetPassClose.setOnClickListener {
            CustomPopWindow.onBackPressed()
        }

        var bt_set= maskView.findViewById<View>(R.id.bt_set)
        var foldername= maskView.findViewById<EditText>(R.id.foldername)
        bt_set.setOnClickListener {
            var foldername= foldername.text.toString()
            if(foldername == "")
            {
                activity.toast(R.string.Name_cannot_be_empty)
                return@setOnClickListener
            }
            var map = HashMap<String,String>()
            map.put("foldername",foldername)
            onRouterSelectListener.onSelect(0, map)
            CustomPopWindow.onBackPressed()
        }
//对具体的view的事件的处理

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(true)
                .setContenView(contentView)
                .setFocusable(true)
                .setClickCloseEnable(false)
                .setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                .create()
                .showAtLocation(showView, Gravity.BOTTOM, 0, 0)
    }
    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showRenameFolderWindow(activity: Activity, showView: View, onRouterSelectListener : OnSelectListener) {
        var isShow = false
        var isShow2 = false
        val maskView = LayoutInflater.from(activity).inflate(R.layout.renamefolder_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        /* list.add(FileOpreateType("doc_img", activity.getString(R.string.upload_photos)))
         list.add(FileOpreateType("video", activity.getString(R.string.upload_video)))
         list.add(FileOpreateType("ic_upload_document", activity.getString(R.string.upload_document)))*/
        maskView.setOnSystemUiVisibilityChangeListener {
            KLog.i("改变了。。。")
        }
        //对具体的view的事件的处理
        var titleSetPassClose= maskView.findViewById<View>(R.id.titleSetPassClose)
        titleSetPassClose.setOnClickListener {
            CustomPopWindow.onBackPressed()
        }

        var bt_set= maskView.findViewById<View>(R.id.bt_set)
        var foldername= maskView.findViewById<EditText>(R.id.foldername)
        bt_set.setOnClickListener {
            var foldername= foldername.text.toString()
            if(foldername == "")
            {
                activity.toast(R.string.Name_cannot_be_empty)
                return@setOnClickListener
            }
            var map = HashMap<String,String>()
            map.put("foldername",foldername)
            onRouterSelectListener.onSelect(0, map)
            CustomPopWindow.onBackPressed()
        }
//对具体的view的事件的处理

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(true)
                .setContenView(contentView)
                .setFocusable(true)
                .setClickCloseEnable(false)
                .setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                .create()
                .showAtLocation(showView, Gravity.BOTTOM, 0, 0)
    }
    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showPopAttachMenuWindow(activity: Activity, showView: View,menuList: ArrayList<String>,iconList: ArrayList<String>, onRouterSelectListener : OnSelectListener) {
        val maskView = LayoutInflater.from(activity).inflate(R.layout.opreate_menu_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        var ll_file = contentView.findViewById<LinearLayout>(R.id.ll_file)
        ll_file.visibility = View.GONE
        recyclerView.layoutManager = GridLayoutManager(AppConfig.instance, 4)
        var list = arrayListOf<FileOpreateType>();
        var size = menuList.size
        for (index in 0..size -1){
            list.add(FileOpreateType(iconList[index], menuList[index]))
        }
        /* list.add(FileOpreateType("doc_img", activity.getString(R.string.upload_photos)))
         list.add(FileOpreateType("video", activity.getString(R.string.upload_video)))
         list.add(FileOpreateType("ic_upload_document", activity.getString(R.string.upload_document)))*/
        val selecRouterAdapter = FileMenuOpreateAdapter(list)
        recyclerView.adapter = selecRouterAdapter
        selecRouterAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            onRouterSelectListener.onSelect(position, selecRouterAdapter.data[position])
            CustomPopWindow.onBackPressed()
        }
        maskView.setOnSystemUiVisibilityChangeListener {
            KLog.i("改变了。。。")
        }
        //对具体的view的事件的处理
        maskView.setOnClickListener {
            CustomPopWindow.onBackPressed()
        }

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(false)
                .setContenView(contentView)
                .setFocusable(false)
                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
                .create()
                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0)
    }
    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showPopMoveMenuWindow(activity: Activity, showView: View,title:String ,menuList: ArrayList<String>,iconList: ArrayList<String>, onRouterSelectListener : OnSelectListener) {
        val maskView = LayoutInflater.from(activity).inflate(R.layout.opreate_file_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        var ll_file = contentView.findViewById<LinearLayout>(R.id.ll_file)
        ll_file.visibility = View.VISIBLE
        val tvFileName = contentView.findViewById<TextView>(R.id.fileName)
        tvFileName.text = title
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        var list = arrayListOf<FileOpreateType>();
        var size = menuList.size
        for (index in 0..size -1){
            list.add(FileOpreateType(iconList[index], menuList[index]))
        }
        /* list.add(FileOpreateType("doc_img", activity.getString(R.string.upload_photos)))
         list.add(FileOpreateType("video", activity.getString(R.string.upload_video)))
         list.add(FileOpreateType("ic_upload_document", activity.getString(R.string.upload_document)))*/
        val selecRouterAdapter = FileChooseOpreateAdapter(list)
        recyclerView.adapter = selecRouterAdapter
        selecRouterAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            CustomPopWindow.onBackPressed()
            onRouterSelectListener.onSelect(position, selecRouterAdapter.data[position])

        }
        maskView.setOnSystemUiVisibilityChangeListener {
            KLog.i("改变了。。。")
        }
        //对具体的view的事件的处理
        maskView.setOnClickListener {
            CustomPopWindow.onBackPressed()
        }

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(false)
                .setContenView(contentView)
                .setFocusable(false)
                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
                .create()
                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0)
    }
    fun getFileOpreateType(context: Activity) : ArrayList<FileOpreateType> {
        var list = ArrayList<FileOpreateType>()
        list.add(FileOpreateType("forward_h", context.getString(R.string.send_to_friend)))
        list.add(FileOpreateType("download_h", context.getString(R.string.download)))
//        list.add(FileOpreateType("open", context.getString(R.string.other_application_open)))
        list.add(FileOpreateType("details", context.getString(R.string.detailed_information)))
        list.add(FileOpreateType("rename", context.getString(R.string.rename)))
        list.add(FileOpreateType("delete_h1", context.getString(R.string.delete)))
        return list
    }


    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showFileSortWindow(activity: Activity, showView: View, onRouterSelectListener : OnSelectListener) {
        val maskView = LayoutInflater.from(activity).inflate(R.layout.sort_file_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
//        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.open_fade)
//        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = 200
        contentView.animation = translate
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        val selecRouterAdapter = FileSortAdapter(getFileSortType(activity))
        recyclerView.adapter = selecRouterAdapter
        selecRouterAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            onRouterSelectListener.onSelect(position, selecRouterAdapter.data[position])
            CustomPopWindow.onBackPressed()
        }
        //对具体的view的事件的处理
        maskView.setOnClickListener { CustomPopWindow.onBackPressed() }

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(false)
                .setContenView(contentView)
                .setFocusable(false)
                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
                .create()
                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0)
    }

    fun getFileSortType(context: Activity) : ArrayList<Arrange> {
        var list = ArrayList<Arrange>()
        list.add(Arrange(context.getString(R.string.arrange_by_name), SpUtil.getInt(AppConfig.instance, ConstantValue.currentArrangeType, 1) == 0))
        list.add(Arrange(context.getString(R.string.arrange_by_time), SpUtil.getInt(AppConfig.instance, ConstantValue.currentArrangeType, 1) == 1))
        list.add(Arrange(context.getString(R.string.arrange_by_size), SpUtil.getInt(AppConfig.instance, ConstantValue.currentArrangeType, 1) == 2))
        list.add(Arrange(context.getString(R.string.arrange_by_contact), SpUtil.getInt(AppConfig.instance, ConstantValue.currentArrangeType, 1) == 3))
        return list
    }

    interface OnSelectListener {
        fun onSelect(position : Int, obj : Any)
    }
}
