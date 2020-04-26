package com.stratagile.pnrouter.ui.activity.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.ui.EaseConversationListFragment
import com.pawegio.kandroid.setHeight
import com.pawegio.kandroid.setWidth
import com.socks.library.KLog

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.main.component.DaggerChatAndEmailComponent
import com.stratagile.pnrouter.ui.activity.main.contract.ChatAndEmailContract
import com.stratagile.pnrouter.ui.activity.main.module.ChatAndEmailModule
import com.stratagile.pnrouter.ui.activity.main.presenter.ChatAndEmailPresenter

import javax.inject.Inject;

import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.entity.events.ChangFragmentMenu
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import com.stratagile.pnrouter.ui.activity.chat.GroupChatActivity
import com.stratagile.pnrouter.ui.activity.user.PrivacyPolicyFragment
import com.stratagile.pnrouter.ui.activity.user.TermsOfServiceFragment
import kotlinx.android.synthetic.main.activity_privacy.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2019/07/08 14:57:30
 */

class ChatAndEmailFragment : BaseFragment(), ChatAndEmailContract.View {

    @Inject
    lateinit internal var mPresenter: ChatAndEmailPresenter
    lateinit var commonNavigator : CommonNavigator
    private var conversationListFragment: EaseConversationListFragment? = null
    private var emailMessageFragment: EmailMessageFragment? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        commonNavigator = CommonNavigator(this.activity)
        //commonNavigator.setAdjustMode(true)
        var view = inflater.inflate(R.layout.activity_chat_email, null);
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        conversationListFragment = EaseConversationListFragment()
        conversationListFragment?.hideTitleBar()
        emailMessageFragment = EmailMessageFragment()
        var titles = ArrayList<String>()
        titles.add(getString(R.string.Message))
        titles.add(getString(R.string.Email))
        var icon = ArrayList<Drawable>()
        icon.add(getResources().getDrawable(R.mipmap.tabbar_circle_selected))
        icon.add(getResources().getDrawable(R.mipmap.tabbar_email_selected))
        commonNavigator.isAdjustMode = true
        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                if (position == 0) {
                    val args = Bundle()
                    args.putString("from", "")
                    conversationListFragment!!.arguments = args
                    return conversationListFragment!!
                } else {
                    val args = Bundle()
                    args.putString("from", "")
                    emailMessageFragment!!.arguments = args
                    return emailMessageFragment!!
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
                var commonPagerTitleView = CommonPagerTitleView(context)
                var view = layoutInflater.inflate(R.layout.main_indicator_layout, null, false)
                var ivAvatar = view.findViewById<ImageView>(R.id.ivAvatar)
                var tvContent = view.findViewById<TextView>(R.id.tvContent)
                if (index == 0) {
                    ivAvatar.setImageResource(R.mipmap.tabbar_circle_selected)
                    tvContent.text = getString(R.string.Message)
                } else {
                    ivAvatar.setImageResource(R.mipmap.tabbar_email_selected)
                    tvContent.text = getString(R.string.Email)
                }
                commonPagerTitleView.setContentView(view)
                commonPagerTitleView.setOnClickListener { viewPager.setCurrentItem(index) }
                commonPagerTitleView.onPagerTitleChangeListener = object : CommonPagerTitleView.OnPagerTitleChangeListener {
                    override fun onDeselected(p0: Int, p1: Int) {

                    }

                    override fun onSelected(p0: Int, p1: Int) {

                    }

                    override fun onLeave(p0: Int, p1: Int, p2: Float, p3: Boolean) {

                    }

                    override fun onEnter(p0: Int, p1: Int, p2: Float, p3: Boolean) {

                    }

                }
                return commonPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.setColors(Color.parseColor("#FF6646F7"))
                indicator.lineHeight = UIUtil.dip2px(context, 2.toDouble()).toFloat()
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
        conversationListFragment?.setConversationListItemClickListener(
                EaseConversationListFragment.EaseConversationListItemClickListener
                { userid, chatType ->
                    if (chatType.equals("Chat")) {
                        startActivity(Intent(this.activity, ChatActivity::class.java).putExtra(EaseConstant.EXTRA_USER_ID, userid))
                    } else {

                        val intent = Intent(AppConfig.instance, GroupChatActivity::class.java)
                        intent.putExtra(EaseConstant.EXTRA_USER_ID, userid)
                        intent.putExtra(EaseConstant.EXTRA_CHAT_GROUP, UserDataManger.currentGroupData)
                        startActivity(intent)
                    }
                    KLog.i("进入聊天页面，好友id为：" + userid)
                })
    }
    override fun setupFragmentComponent() {
        DaggerChatAndEmailComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .chatAndEmailModule(ChatAndEmailModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: ChatAndEmailContract.ChatAndEmailContractPresenter) {
        mPresenter = presenter as ChatAndEmailPresenter
    }
    fun setCurrentItem(position:Int)
    {
        viewPager.setCurrentItem(position)
    }
    fun getConversationListFragment():EaseConversationListFragment
    {
        return conversationListFragment!!
    }
    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
}