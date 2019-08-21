package com.stratagile.pnrouter.ui.activity.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.main.component.DaggerChatAndEmailSearchComponent
import com.stratagile.pnrouter.ui.activity.main.contract.ChatAndEmailSearchContract
import com.stratagile.pnrouter.ui.activity.main.module.ChatAndEmailSearchModule
import com.stratagile.pnrouter.ui.activity.main.presenter.ChatAndEmailSearchPresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.hyphenate.easeui.EaseConstant
import com.hyphenate.easeui.ui.EaseConversationListFragment
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.entity.events.OnDrawerOpened
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity
import com.stratagile.pnrouter.ui.activity.chat.GroupChatActivity
import kotlinx.android.synthetic.main.activity_privacy.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2019/08/13 15:32:23
 */

class ChatAndEmailSearchFragment : BaseFragment(), ChatAndEmailSearchContract.View {

    @Inject
    lateinit internal var mPresenter: ChatAndEmailSearchPresenter
    private var emailMessageFragment: EmailMessageFragment? = null
    private var conversationListFragment: EaseConversationListFragment? = null
    var from = ""
    var menu = "INBOX"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //commonNavigator.setAdjustMode(true)
        var view = inflater.inflate(R.layout.activity_chat_email_search, null);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        from = arguments!!.getString("from")
        conversationListFragment = EaseConversationListFragment()
        conversationListFragment?.hideTitleBar()
        conversationListFragment?.refresh()
        conversationListFragment?.shouUI(true)
        emailMessageFragment = EmailMessageFragment()
        emailMessageFragment!!.updateMenu(ConstantValue.chooseEmailMenuServer)
        EventBus.getDefault().post(OnDrawerOpened())
        var titles = ArrayList<String>()
        if(from == "Circle")
        {
            titles.add(getString(R.string.Message))
        }else{
            titles.add(getString(R.string.Email))
        }
        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                if(from == "Circle")
                {
                    val args = Bundle()
                    args.putString("from", "ChatAndEmailSearchFragment")
                    conversationListFragment!!.arguments = args
                    return conversationListFragment!!
                }else{
                    val args = Bundle()
                    args.putString("from", "ChatAndEmailSearchFragment")
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

    override fun onStart() {
        //emailMessageFragment?.shouUI(true)
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun setupFragmentComponent() {
        DaggerChatAndEmailSearchComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .chatAndEmailSearchModule(ChatAndEmailSearchModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: ChatAndEmailSearchContract.ChatAndEmailSearchContractPresenter) {
        mPresenter = presenter as ChatAndEmailSearchPresenter
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