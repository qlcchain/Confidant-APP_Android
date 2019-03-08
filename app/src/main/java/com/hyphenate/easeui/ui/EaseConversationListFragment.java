package com.hyphenate.easeui.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.PathUtils;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.message.CacheMessage;
import com.message.Message;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.constant.UserDataManger;
import com.stratagile.pnrouter.db.DraftEntity;
import com.stratagile.pnrouter.db.DraftEntityDao;
import com.stratagile.pnrouter.db.FriendEntity;
import com.stratagile.pnrouter.db.FriendEntityDao;
import com.stratagile.pnrouter.db.UserEntity;
import com.stratagile.pnrouter.db.UserEntityDao;
import com.stratagile.pnrouter.entity.UnReadEMMessage;
import com.stratagile.pnrouter.utils.GsonUtil;
import com.stratagile.pnrouter.utils.LogUtil;
import com.stratagile.pnrouter.utils.SpUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * conversation list fragment
 */
public class EaseConversationListFragment extends EaseBaseFragment {
    private final static int MSG_REFRESH = 2;
    protected EditText query;
    protected ImageButton clearSearch;
    protected boolean hidden;
    protected List<UnReadEMMessage> conversationList = new ArrayList<UnReadEMMessage>();
    protected EaseConversationList conversationListView;
    protected FrameLayout errorItemContainer;

    protected boolean isConflict;

    protected EMConversationListener convListener = new EMConversationListener() {

        @Override
        public void onCoversationUpdate() {
            refresh();
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ease_fragment_conversation_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        conversationListView = (EaseConversationList) getView().findViewById(R.id.list);
        query = (EditText) getView().findViewById(R.id.query);
        // button to clear content in search bar
        clearSearch = (ImageButton) getView().findViewById(R.id.search_clear);
        errorItemContainer = (FrameLayout) getView().findViewById(R.id.fl_error_item);
    }

    @Override
    protected void setUpView() {
        conversationList.addAll(loadLocalConversationList());
        conversationListView.init(conversationList);

        if (listItemClickListener != null) {
            conversationListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    KLog.i("点击会话层:");
                    LogUtil.addLog("点击会话层:","EaseConversationListFragment");
                    UnReadEMMessage conversation = conversationListView.getItem(position);
                    UnReadEMMessage lastMessage = conversation;
                    UserEntity friendInfo = null;
                    List<UserEntity> localFriendList = null;
                    KLog.i("点击会话层lastMessage:"+lastMessage.getEmMessage() +"_from:"+lastMessage.getEmMessage().getFrom()+"_to:"+lastMessage.getEmMessage().getTo());
                    LogUtil.addLog("点击会话层lastMessage:"+lastMessage.getEmMessage() +"_from:"+lastMessage.getEmMessage().getFrom()+"_to:"+lastMessage.getEmMessage().getTo(),"EaseConversationListFragment");
                    if (!lastMessage.getEmMessage().getTo().equals(UserDataManger.myUserData.getUserId())) {
                        localFriendList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().queryBuilder().where(UserEntityDao.Properties.UserId.eq(lastMessage.getEmMessage().getTo())).list();
                        KLog.i("点击会话层1_localFriendList:"+localFriendList.size());
                        LogUtil.addLog("点击会话层1_localFriendList:"+localFriendList.size(),"EaseConversationListFragment");
                        if (localFriendList.size() > 0)
                            friendInfo = localFriendList.get(0);
                    } else {
                        localFriendList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().queryBuilder().where(UserEntityDao.Properties.UserId.eq(lastMessage.getEmMessage().getFrom())).list();
                        KLog.i("点击会话层2_localFriendList:"+localFriendList.size());
                        LogUtil.addLog("点击会话层2_localFriendList:"+localFriendList.size(),"EaseConversationListFragment");
                        if (localFriendList.size() > 0)
                            friendInfo = localFriendList.get(0);
                    }
                    KLog.i("点击会话层:"+friendInfo);
                    LogUtil.addLog("点击会话层:"+friendInfo,"EaseConversationListFragment");
                    lastMessage.getEmMessage().setUnread(false);
                    UserDataManger.curreantfriendUserData = friendInfo;
                    if (friendInfo != null)
                        listItemClickListener.onListItemClicked(friendInfo.getUserId());
                }
            });

//            conversationListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    FloatMenu floatMenu = new  FloatMenu(AppConfig.instance.getApplicationContext(),view);
//                    floatMenu.inflate(R.menu.conversation_popup_menu);
//                    int[] loc1=new int[2];
//                    view.getLocationOnScreen(loc1);
//                    floatMenu.show(new Point(loc1[0]-50,loc1[1]-100));
//                    floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {
//                        @Override
//                        public void onClick(View v, int position) {
//                            switch (position)
//                            {
//                                case 0:
//                                    String isTop = conversationList.get(position).getExtField();
//                                    if ("toTop".equals(isTop)) {
//                                        conversationList.get(position).setExtField("false");
//                                    } else {
//                                        conversationList.get(position).setExtField("toTop");
//                                    }
//                                    refresh();
//                                    break;
//                                case 1:
//
//                                    break;
//
//                                default:
//                                    break;
//                            }
//                        }
//                    });
//                    return true;
//                }
//            });
        }

        EMClient.getInstance().addConnectionListener(connectionListener);

        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                conversationListView.filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

        conversationListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });
    }


    protected EMConnectionListener connectionListener = new EMConnectionListener() {

        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE || error == EMError.SERVER_SERVICE_RESTRICTED
                    || error == EMError.USER_KICKED_BY_CHANGE_PASSWORD || error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                isConflict = true;
            } else {
                handler.sendEmptyMessage(0);
            }
        }

        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };
    private EaseConversationListItemClickListener listItemClickListener;

    protected Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    onConnectionDisconnected();
                    break;
                case 1:
                    onConnectionConnected();
                    break;

                case MSG_REFRESH: {
                    KLog.i("刷新会话列表");
                    conversationList.clear();
                    conversationList.addAll(loadLocalConversationList());
                    if (conversationListView != null)
                        conversationListView.refresh();
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * connected to server
     */
    protected void onConnectionConnected() {
        errorItemContainer.setVisibility(View.GONE);
    }

    /**
     * disconnected with server
     */
    protected void onConnectionDisconnected() {
        errorItemContainer.setVisibility(View.VISIBLE);
    }


    /**
     * refresh ui
     */
    public void refresh() {

        if (!handler.hasMessages(MSG_REFRESH)) {
            handler.sendEmptyMessage(MSG_REFRESH);
        }
    }

    public int removeFriend() {
        conversationList.clear();
        List<UnReadEMMessage> list = loadLocalConversationList();
        conversationList.addAll(list);
        conversationListView.init(conversationList);
        refresh();
        return list.size();
    }

    /**
     * load conversation list
     *
     * @return +
     */
    protected List<UnReadEMMessage> loadLocalConversationList() {
        // get all conversations
        List<UnReadEMMessage> list = new ArrayList<UnReadEMMessage>();
        try {
            Map<String, UnReadEMMessage> conversations = new HashMap<>();
            List<Pair<Long, UnReadEMMessage>> sortList = new ArrayList<Pair<Long, UnReadEMMessage>>();
            /**
             * lastMsgTime will change if there is new message during sorting
             * so use synchronized to make sure timestamp of last message won't change.
             */
            Map<String, Object> keyMap = SpUtil.INSTANCE.getAll(AppConfig.instance);
            String userId = SpUtil.INSTANCE.getString(getActivity(), ConstantValue.INSTANCE.getUserId(), "");
            for (String key : keyMap.keySet()) {

                if (key.contains(ConstantValue.INSTANCE.getMessage()) && key.contains(userId + "_")) {
                    String toChatUserId = key.substring(key.lastIndexOf("_") + 1, key.length());
                    if (toChatUserId != null && !toChatUserId.equals("") && !toChatUserId.equals("null")) {
                        List<UserEntity> localFriendList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().queryBuilder().where(UserEntityDao.Properties.UserId.eq(toChatUserId)).list();
                        if (localFriendList.size() == 0)//如果找不到用户
                        {
                            SpUtil.INSTANCE.putString(getActivity(), key, "");
                            continue;
                        }
                        FriendEntity freindStatusData = new FriendEntity();
                        freindStatusData.setFriendLocalStatus(7);
                        List<FriendEntity> localFriendStatusList = AppConfig.instance.getMDaoMaster().newSession().getFriendEntityDao().queryBuilder().where(FriendEntityDao.Properties.UserId.eq(userId), FriendEntityDao.Properties.FriendId.eq(toChatUserId)).list();
                        if (localFriendStatusList.size() > 0)
                            freindStatusData = localFriendStatusList.get(0);
                        if (freindStatusData.getFriendLocalStatus() != 0) {
                            SpUtil.INSTANCE.putString(getActivity(), key, "");
                            continue;
                        }
                        String cachStr = SpUtil.INSTANCE.getString(AppConfig.instance, key, "");
                        List<DraftEntity> drafts = AppConfig.instance.getMDaoMaster().newSession().getDraftEntityDao().queryBuilder().where(DraftEntityDao.Properties.UserId.eq(userId)).where(DraftEntityDao.Properties.ToUserId.eq(toChatUserId)).list();
                        DraftEntity draftEntity = null;
                        if (drafts != null && drafts.size() > 0) {
                            draftEntity = drafts.get(0);
                        }
                        if (!"".equals(cachStr)) {
                            Gson gson = GsonUtil.getIntGson();
                            Message Message = gson.fromJson(cachStr, Message.class);
                            EMMessage message = null;
                            if (Message != null) {
                                switch (Message.getMsgType()) {
                                    case 0:
                                        if (draftEntity != null && !draftEntity.getContent().equals("")) {
                                            message = EMMessage.createTxtSendMessage("//[draft]//" + draftEntity.getContent(), toChatUserId);
                                        } else {
                                            message = EMMessage.createTxtSendMessage(Message.getMsg().replace("/[draft]/", ""), toChatUserId);
                                        }
                                        break;
                                    case 1:
                                        String ease_default_image = PathUtils.getInstance().getImagePath() + "/" + "ease_default_image.png";
                                        message = EMMessage.createImageSendMessage(ease_default_image, true, toChatUserId);
                                        break;
                                    case 2:
                                        String ease_default_amr = PathUtils.getInstance().getVoicePath() + "/" + "ease_default_amr.amr";
                                        message = EMMessage.createVoiceSendMessage(ease_default_amr, 1, toChatUserId);
                                        break;
                                    case 3:
                                        break;
                                    case 4:
                                        String thumbPath = PathUtils.getInstance().getImagePath() + "/" + "ease_default_image.png";
                                        String videoPath = PathUtils.getInstance().getVideoPath() + "/" + "ease_default_vedio.mp4";
                                        message = EMMessage.createVideoSendMessage(videoPath, thumbPath, 1000, toChatUserId);
                                        break;
                                    case 5:
                                        String ease_default_file = PathUtils.getInstance().getImagePath() + "/" + "file_downloading.*";
                                        message = EMMessage.createFileSendMessage(ease_default_file, toChatUserId);
                                        break;
                                }
                                if (message == null) {
                                    continue;
                                }
                                //message.setTo(Message.getTo());
                                message.setUnread(false);
                                switch (Message.getStatus()) {
                                    case 0:
                                        message.setDelivered(true);
                                        message.setAcked(false);
                                        message.setUnread(true);
                                        break;
                                    case 1:
                                        message.setDelivered(true);
                                        message.setAcked(true);
                                        message.setUnread(true);
                                        break;
                                    case 2:
                                        message.setDelivered(true);
                                        message.setAcked(true);
                                        message.setUnread(false);
                                        break;
                                    default:
                                        break;
                                }
                                if (Message.getSender() == 0) {
                                    message.setFrom(userId);
                                    message.setTo(toChatUserId);

                                    message.setDirection(EMMessage.Direct.SEND);
                                } else {
                                    message.setFrom(toChatUserId);
                                    message.setTo(userId);
                                    message.setDirection(EMMessage.Direct.RECEIVE);
                                }
                                message.setMsgTime(Message.getTimeStatmp());
                                message.setMsgId(Message.getMsgId() + "");
                                conversations.put(toChatUserId, new UnReadEMMessage(message,Message.getUnReadCount()));
                            }

                        }
                    }

                }
            }
            synchronized (conversations) {
                for (UnReadEMMessage conversation : conversations.values()) {
                    if (conversation.getEmMessage().getMsgTime() /1000000000 > 2) {
                        LogUtil.addLog("用户最后一条的时间为：" + conversation.getEmMessage().getMsgTime());
                        sortList.add(new Pair<Long, UnReadEMMessage>(conversation.getEmMessage().getMsgTime() / 1000, conversation));
                    } else {
                        sortList.add(new Pair<Long, UnReadEMMessage>(conversation.getEmMessage().getMsgTime(), conversation));
                    }
                }
            }
            try {
                // Internal is TimSort algorithm, has bug
                sortConversationByLastChatTime(sortList);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Pair<Long, UnReadEMMessage> sortItem : sortList) {
                list.add(sortItem.second);
            }

        } catch (Exception e) {

        }
        return list;
    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, UnReadEMMessage>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, UnReadEMMessage>>() {
            @Override
            public int compare(final Pair<Long, UnReadEMMessage> con1, final Pair<Long, UnReadEMMessage> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden && !isConflict) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().removeConnectionListener(connectionListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isConflict) {
            outState.putBoolean("isConflict", true);
        }
    }

    public interface EaseConversationListItemClickListener {
        /**
         * click event for conversation list
         *
         * @param usersid -- clicked item
         */
        void onListItemClicked(String usersid);
    }

    /**
     * set conversation list item click listener
     *
     * @param listItemClickListener
     */
    public void setConversationListItemClickListener(EaseConversationListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

}
