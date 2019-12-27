package com.stratagile.pnrouter.ui.activity.encryption

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import com.hyphenate.easeui.utils.PathUtils
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.db.LocalFileItemDao
import com.stratagile.pnrouter.db.LocalFileMenuDao
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerWexinChatComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.WexinChatContract
import com.stratagile.pnrouter.ui.activity.encryption.module.WexinChatModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.WexinChatPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.PicMenuEncryptionAdapter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.picencry_wechat_list.*
import java.io.File

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/12/27 16:17:50
 */

class WexinChatActivity : BaseActivity(), WexinChatContract.View {

    @Inject
    internal lateinit var mPresenter: WexinChatPresenter
    var picMenuEncryptionAdapter: PicMenuEncryptionAdapter? = null
    var sharedTextContent:String? = ""
    var sharedText:String? = ""
    var imageUris:ArrayList<Uri>? = null
    var zipFileSoucePath:MutableList<String> = java.util.ArrayList()
    var zipCompressTask: ZipCompressTask? = null
    var zipSavePath =""
    var zipSavePathTemp =""

    override fun onCreate(savedInstanceState: Bundle?) {
        if(intent != null)
        {
            val action = intent.action
            val type = intent.type
            sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if(sharedText != null && sharedText!!.contains("\n"))
            {
                sharedTextContent = sharedText!!.substring(0,sharedText!!.indexOf("\n"))
                if(sharedTextContent!!.contains("如下"))
                {
                    sharedTextContent = sharedTextContent!!.substring(0,sharedTextContent!!.indexOf("如下"))
                }
            }
            imageUris = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
            var size = 0;
            if(imageUris != null)
            {
                size = imageUris!!.size
            }
            KLog.i("微信,EmailLoginActivity" + action+"#####"+type+"#####"+sharedText +"#####"+ size)
        }
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.picencry_wechat_list)
    }
    override fun initData() {
        chatName.text = sharedTextContent
        var base58files_dir =  PathUtils.getInstance().tempPath.toString() + "/"
        var  path = PathUtils.generateWechatMessagePath("temp")+"htmlContent.txt";
        var  result = FileUtil.writeStr_to_txt(path,sharedText)
        if(result)
        {
            zipFileSoucePath.add(path)

            zipSavePath = PathUtils.generateWechatMessagePath("temp")+"htmlContent.zip";
            zipCompressTask = ZipCompressTask(zipFileSoucePath!!, zipSavePath, this, false, handlerCompressZip!!)
            zipCompressTask!!.execute()
        }
        var picMenuList = AppConfig.instance.mDaoMaster!!.newSession().localFileMenuDao.queryBuilder().where(LocalFileMenuDao.Properties.Type.eq("0")).list()
        picMenuEncryptionAdapter = PicMenuEncryptionAdapter(picMenuList)
        recyclerViewNodeMenu.adapter = picMenuEncryptionAdapter
        picMenuEncryptionAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.menuItem -> {

                }
            }
        }
    }
    internal var handlerCompressZip: Handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            when (msg.what) {
                0x404 -> {

                    toast(R.string.Compression_failure)
                }
                0x56 -> {

                }
            }//goMain();
            //goMain();
        }
    }
    override fun setupActivityComponent() {
       DaggerWexinChatComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .wexinChatModule(WexinChatModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: WexinChatContract.WexinChatContractPresenter) {
            mPresenter = presenter as WexinChatPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}