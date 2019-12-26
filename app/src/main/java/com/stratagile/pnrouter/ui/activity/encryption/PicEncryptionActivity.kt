package com.stratagile.pnrouter.ui.activity.encryption

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.WindowManager
import android.widget.LinearLayout
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.entity.Sceen
import com.stratagile.pnrouter.entity.events.UpdateAlbumEncryptionItemEvent
import com.stratagile.pnrouter.entity.events.UpdateAlbumNodeEncryptionItemEvent
import com.stratagile.pnrouter.ui.activity.encryption.component.DaggerPicEncryptionComponent
import com.stratagile.pnrouter.ui.activity.encryption.contract.PicEncryptionContract
import com.stratagile.pnrouter.ui.activity.encryption.module.PicEncryptionModule
import com.stratagile.pnrouter.ui.activity.encryption.presenter.PicEncryptionPresenter
import com.stratagile.pnrouter.ui.adapter.conversation.PicMenuEncryptionAdapter
import com.stratagile.pnrouter.utils.SpUtil
import kotlinx.android.synthetic.main.activity_encrption_local.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import javax.inject.Inject

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
    lateinit var commonNavigator : CommonNavigator
    private var picMenuLocalFragment: PicMenuLocalFragment? = null
    private var picMenuNodeFragment: PicMenuNodeFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        commonNavigator = CommonNavigator(this)
        //setContentView(R.layout.picencry_menu_list)
        setContentView(R.layout.activity_encrption_local)
        title.text = getString(R.string.Album_encryption)

    }
    override fun initData() {
        var _this = this;
        EventBus.getDefault().register(this)
        picMenuLocalFragment = PicMenuLocalFragment()
        picMenuNodeFragment = PicMenuNodeFragment()
        var titles = ArrayList<String>()
        titles.add(getString(R.string.maillist_local))
        titles.add(getString(R.string.node_local))
        var icon = ArrayList<Drawable>()
        icon.add(getResources().getDrawable(R.mipmap.statusbar_local))
        icon.add(getResources().getDrawable(R.mipmap.statusbar_download_node))
        commonNavigator.isAdjustMode = true
        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                if (position == 0) {
                    val args = Bundle()
                    args.putString("from", "")
                    picMenuLocalFragment!!.arguments = args
                    return picMenuLocalFragment!!
                } else {
                    val args = Bundle()
                    args.putString("from", "")
                    picMenuNodeFragment!!.arguments = args
                    return picMenuNodeFragment!!
                }
            }

            override fun getCount(): Int {
                return titles.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles.get(position)
            }
        }
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return if (titles == null) 0 else titles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                //val badgePagerTitleView = BadgePagerTitleView(context)
                val simplePagerTitleView = ColorTransitionPagerTitleView(context)
                simplePagerTitleView.setText(titles.get(index))
                simplePagerTitleView.normalColor =  Color.parseColor("#FF9496A1")
                simplePagerTitleView.textSize = 17f
                var icon: Drawable = icon.get(index)
                 icon.setBounds(120,0,210,90);
                /*if(index == 0)
                {
                    icon.setBounds(resources.getDimension(R.dimen.x40).toInt(),0,resources.getDimension(R.dimen.x105).toInt(),resources.getDimension(R.dimen.x60).toInt());
                }else{
                    icon.setBounds(resources.getDimension(R.dimen.x40).toInt(),0,resources.getDimension(R.dimen.x125).toInt(),resources.getDimension(R.dimen.x60).toInt());
                }*/
                simplePagerTitleView.setCompoundDrawables(icon,null,null,null)
                simplePagerTitleView.selectedColor = Color.parseColor("#FF2B2B2B")
                simplePagerTitleView.setOnClickListener {  viewPager.setCurrentItem(index) }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.setColors(Color.parseColor("#FF6646F7"))
                indicator.lineHeight = UIUtil.dip2px(context, 1.5).toFloat()
                return indicator
            }

            override fun getTitleWeight(context: Context?, index: Int): Float {
                return if (index == 0) {
                    1.0f
                } else {
                    1.0f
                }
            }
        }
        indicator.setNavigator(commonNavigator)
        val titleContainer = commonNavigator.titleContainer // must after setNavigator
        titleContainer.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        titleContainer.dividerPadding = UIUtil.dip2px(AppConfig.instance, 15.0)
        titleContainer.dividerDrawable = resources.getDrawable(R.drawable.simple_splitter)
        ViewPagerHelper.bind(indicator, viewPager);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddLocalEncryptionItemEvent(statusChange: UpdateAlbumEncryptionItemEvent) {
        picMenuLocalFragment!!.upDateUI();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateNodelEncryptionItemEvent(statusChange: UpdateAlbumNodeEncryptionItemEvent) {
        picMenuNodeFragment!!.upDateUI();
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
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}