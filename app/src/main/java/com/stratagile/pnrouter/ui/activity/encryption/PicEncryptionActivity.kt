package com.stratagile.pnrouter.ui.activity.encryption

import android.os.Bundle
import com.mcxtzhang.swipemenulib.SwipeMenuLayout
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.entity.JPullFileListRsp
import com.stratagile.pnrouter.entity.PicMenu
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicEncryptionComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.PicMenuEncryptionAdapter
import kotlinx.android.synthetic.main.picencry_menu_list.*

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.encryption
 * @Description: $description
 * @date 2019/11/21 15:26:11
 */

class PicEncryptionActivity : BaseActivity(), PicEncryptionContract.View {

    @Inject
    internal lateinit var mPresenter: PicEncryptionPresenter
    var picMenuEncryptionAdapter: PicMenuEncryptionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.picencry_menu_list)
        //setContentView(R.layout.item_cst_swipe)

        title.text = getString(R.string.setting_)

    }
    override fun initData() {
        var emailMessageEntityList = mutableListOf<PicMenu>()
        var waitDeleteDat =  PicMenu();
        emailMessageEntityList.add(0,waitDeleteDat)
        emailMessageEntityList.add(1,waitDeleteDat)
        picMenuEncryptionAdapter = PicMenuEncryptionAdapter(emailMessageEntityList)
        recyclerViewPicEncry.adapter = picMenuEncryptionAdapter
        picMenuEncryptionAdapter!!.setOnItemClickListener { adapter, view, position ->
            var taskFile = picMenuEncryptionAdapter!!.getItem(position)
            //startActivity(Intent(activity!!, PdfViewActivity::class.java).putExtra("fileMiPath", taskFile!!.fileName).putExtra("file", fileListChooseAdapter!!.data[position]))
        }
        picMenuEncryptionAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.menuItem ->
                {
                    view
                }
                R.id.btnDelete ->
                {
                    var parentRoot = view.parent as SwipeMenuLayout
                    parentRoot.quickClose()
                }
                R.id.btnRename ->
                {
                    var parentRoot = view.parent as SwipeMenuLayout
                    parentRoot.quickClose()
                }
            }
        }
    }

    override fun setupActivityComponent() {
       DaggerPicEncryptionComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .picEncryptionModule(PicEncryptionModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: PicEncryptionContract.PicEncryptionContractPresenter) {
            mPresenter = presenter as PicEncryptionPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}