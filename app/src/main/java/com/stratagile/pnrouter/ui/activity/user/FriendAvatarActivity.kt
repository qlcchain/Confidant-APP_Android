package com.stratagile.pnrouter.ui.activity.user

import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.hyphenate.easeui.utils.PathUtils
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.ui.activity.user.component.DaggerFriendAvatarComponent
import com.stratagile.pnrouter.ui.activity.user.contract.FriendAvatarContract
import com.stratagile.pnrouter.ui.activity.user.module.FriendAvatarModule
import com.stratagile.pnrouter.ui.activity.user.presenter.FriendAvatarPresenter
import com.stratagile.pnrouter.utils.Base58
import com.stratagile.pnrouter.utils.RxEncodeTool
import com.stratagile.pnrouter.utils.SpUtil
import com.stratagile.pnrouter.utils.SystemUtil
import kotlinx.android.synthetic.main.activity_modify_friendavatar.*
import java.io.File

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/04/11 18:10:07
 */

class FriendAvatarActivity : BaseActivity(), FriendAvatarContract.View {

    @Inject
    internal lateinit var mPresenter: FriendAvatarPresenter
    var options = RequestOptions()
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .priority(Priority.HIGH)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_modify_friendavatar)
    }
    override fun initData() {
        title.text = "Profile Picture"
        var libsodiumpublicSignKey = intent.getStringExtra("libsodiumpublicSignKey")
        var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(libsodiumpublicSignKey))+".jpg"
        val lastFile = File(PathUtils.getInstance().filePath.toString() + "/" + fileBase58Name, "")
        if (lastFile.exists()) {
            Glide.with(this)
                    .load(lastFile)
                    .apply(options)
                    .into(ivPicture)
        }
        avatarParent.setOnClickListener {

            finish()
        }
    }

    override fun setupActivityComponent() {
       DaggerFriendAvatarComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .friendAvatarModule(FriendAvatarModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: FriendAvatarContract.FriendAvatarContractPresenter) {
            mPresenter = presenter as FriendAvatarPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}