package com.stratagile.pnrouter.ui.activity.user

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.user.component.DaggerPrivacyComponent
import com.stratagile.pnrouter.ui.activity.user.contract.PrivacyContract
import com.stratagile.pnrouter.ui.activity.user.module.PrivacyModule
import com.stratagile.pnrouter.ui.activity.user.presenter.PrivacyPresenter
import kotlinx.android.synthetic.main.activity_privacy.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule
import java.util.ArrayList

import javax.inject.Inject;

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2019/04/22 18:22:12
 */

class PrivacyActivity : BaseActivity(), PrivacyContract.View {

    @Inject
    internal lateinit var mPresenter: PrivacyPresenter
    lateinit var commonNavigator : CommonNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        commonNavigator = CommonNavigator(this)
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_privacy)
    }
    override fun initData() {
        title.text = getString(R.string.Terms_Privacy_Policy)
        var titles = ArrayList<String>()
        titles.add(getString(R.string.Terms_of_Service))
            titles.add(getString(R.string.Privacy_Policy))
        viewPager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                if (position == 0) {
                    return TermsOfServiceFragment()
                } else {
                    return PrivacyPolicyFragment()
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
                val badgePagerTitleView = BadgePagerTitleView(context)

                val simplePagerTitleView = ColorTransitionPagerTitleView(context)
                simplePagerTitleView.setText(titles.get(index))
                simplePagerTitleView.normalColor = resources.getColor(R.color.color_999999)
                simplePagerTitleView.selectedColor = resources.getColor(R.color.color_2B2B2B)
                simplePagerTitleView.setOnClickListener {
                    viewPager.setCurrentItem(index)
//                    badgePagerTitleView.badgeView = null // cancel badge when click tab
                }
                badgePagerTitleView.innerPagerTitleView = simplePagerTitleView

                val badgeImageView = LayoutInflater.from(context).inflate(R.layout.simple_red_dot_badge_layout, null) as ImageView
                badgePagerTitleView.badgeView = badgeImageView

                // set badge position
                badgePagerTitleView.xBadgeRule = BadgeRule(BadgeAnchor.CONTENT_RIGHT, 0)
                badgePagerTitleView.yBadgeRule = BadgeRule(BadgeAnchor.CONTENT_TOP, 0)

                // don't cancel badge when tab selected
                badgePagerTitleView.isAutoCancelBadge = false

                return badgePagerTitleView
//                val colorTransitionPagerTitleView = ColorTransitionPagerTitleView(context)
//                colorTransitionPagerTitleView.normalColor = Color.GRAY
//                colorTransitionPagerTitleView.selectedColor = Color.BLACK
//                colorTransitionPagerTitleView.setText(titles.get(index))
//                colorTransitionPagerTitleView.setOnClickListener(object : View.OnClickListener {
//                    override fun onClick(view: View) {
//                        viewPager.setCurrentItem(index)
//                    }
//                })
//                return colorTransitionPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                indicator.lineHeight = resources.getDimension(R.dimen.x4)
                return indicator
            }
        }
        indicator.setNavigator(commonNavigator)
        val titleContainer = commonNavigator.titleContainer // must after setNavigator
        titleContainer.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        titleContainer.dividerDrawable = object : ColorDrawable() {
            override fun getIntrinsicWidth(): Int {
                return resources.getDimension(R.dimen.x140).toInt()
            }
        }
        ViewPagerHelper.bind(indicator, viewPager);
    }

    override fun setupActivityComponent() {
       DaggerPrivacyComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .privacyModule(PrivacyModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: PrivacyContract.PrivacyContractPresenter) {
            mPresenter = presenter as PrivacyPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}