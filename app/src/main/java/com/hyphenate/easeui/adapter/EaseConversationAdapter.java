package com.hyphenate.easeui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseConversationList.EaseConversationListHelper;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.constant.UserDataManger;
import com.stratagile.pnrouter.db.UserEntity;
import com.stratagile.pnrouter.db.UserEntityDao;
import com.stratagile.pnrouter.utils.DateUtil;
import com.stratagile.pnrouter.utils.RxEncodeTool;
import com.stratagile.pnrouter.utils.SpUtil;
import com.stratagile.pnrouter.view.ImageButtonWithText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



/**
 * conversation list adapter
 *
 */
public class EaseConversationAdapter extends ArrayAdapter<EMConversation> {
    private static final String TAG = "ChatAllHistoryAdapter";
    private List<EMConversation> conversationList;
    private List<EMConversation> copyConversationList;
    private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;

    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int primarySize;
    protected int secondarySize;
    protected float timeSize;

    public EaseConversationAdapter(Context context, int resource,
                                   List<EMConversation> objects) {
        super(context, resource, objects);
        conversationList = objects;
        copyConversationList = new ArrayList<EMConversation>();
        copyConversationList.addAll(objects);
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public EMConversation getItem(int arg0) {
        if (arg0 < conversationList.size()) {
            return conversationList.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ease_row_chat_history, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.avatar = (ImageButtonWithText) convertView.findViewById(R.id.avatar);
            holder.msgState = convertView.findViewById(R.id.msg_state);
            holder.list_itease_layout = (RelativeLayout) convertView.findViewById(R.id.list_itease_layout);
            holder.motioned = (TextView) convertView.findViewById(R.id.mentioned);
            convertView.setTag(holder);
        }
        holder.list_itease_layout.setBackgroundResource(R.drawable.ease_mm_listitem);

        // get conversation
        EMConversation conversation = getItem(position);
        // get username or group id
        String conversationId = conversation.conversationId();
        EMMessage lastMessage = conversation.getLastMessage();
        if(lastMessage == null)
        {
            return convertView;
        }
        UserEntity friendUser = null;
        List<UserEntity> localFriendList = null;
        if(UserDataManger.myUserData != null && !lastMessage.getTo().equals(UserDataManger.myUserData.getUserId()))
        {
            localFriendList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().queryBuilder().where(UserEntityDao.Properties.UserId.eq(lastMessage.getTo())).list();
            if(localFriendList.size() > 0)
                friendUser = localFriendList.get(0);
        }else{
            localFriendList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().queryBuilder().where(UserEntityDao.Properties.UserId.eq(lastMessage.getFrom())).list();
            if(localFriendList.size() > 0)
                friendUser = localFriendList.get(0);
        }
        if(friendUser == null)
        {
            return convertView;
        }
        String username = friendUser.getNickName();
        if(friendUser.getRemarks() != null && !friendUser.getRemarks().equals(""))
        {
            username = friendUser.getRemarks();
        }
        String usernameSouce = new  String(RxEncodeTool.base64Decode(username));
        if (conversation.getType() == EMConversationType.GroupChat) {
            String groupId = conversation.conversationId();
            if(EaseAtMessageHelper.get().hasAtMeMsg(groupId)){
                holder.motioned.setVisibility(View.VISIBLE);
            }else{
                holder.motioned.setVisibility(View.GONE);
            }
            // group message, show group avatar
//            holder.avatar.setImageResource(R.drawable.ease_group_icon);
            EMGroup group = EMClient.getInstance().groupManager().getGroup(conversationId);
            holder.name.setText(group != null ? group.getGroupName() : usernameSouce);
        } else if(conversation.getType() == EMConversationType.ChatRoom){
//            holder.avatar.setImageResource(R.drawable.ease_group_icon);
            EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(conversationId);
            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : usernameSouce);
            holder.motioned.setVisibility(View.GONE);
        }else {
//            EaseUserUtils.setUserAvatar(getContext(), conversationId, holder.avatar);
            //EaseUserUtils.setUserNick(username, holder.name);
            holder.avatar.setText(usernameSouce);
            holder.name.setText(usernameSouce);
            holder.motioned.setVisibility(View.GONE);
        }

//        EaseAvatarOptions avatarOptions = EaseUI.getInstance().getAvatarOptions();
//        if(avatarOptions != null && holder.avatar instanceof EaseImageView) {
//            EaseImageView avatarView = ((EaseImageView) holder.avatar);
//            if (avatarOptions.getAvatarShape() != 0)
//                avatarView.setShapeType(avatarOptions.getAvatarShape());
//            if (avatarOptions.getAvatarBorderWidth() != 0)
//                avatarView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
//            if (avatarOptions.getAvatarBorderColor() != 0)
//                avatarView.setBorderColor(avatarOptions.getAvatarBorderColor());
//            if (avatarOptions.getAvatarRadius() != 0)
//                avatarView.setRadius(avatarOptions.getAvatarRadius());
//        }
        String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
        if (lastMessage.isUnread() && !userId.equals(lastMessage.getFrom())) {
            // show unread message count
            //holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
            holder.unreadLabel.setText("");
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
        }
        if (lastMessage.isUnread()) {
            // show the content of latest message
            holder.time.setText(DateUtil.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }

        String content = null;
        if(cvsListHelper != null){
            content = cvsListHelper.onSetItemSecondaryText(lastMessage);
        }
        holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()))),
                TextView.BufferType.SPANNABLE);
        if(content != null){
            holder.message.setText(content);
        }

        //set property
        holder.name.setTextColor(primaryColor);
        holder.message.setTextColor(secondaryColor);
        holder.time.setTextColor(timeColor);
        if(primarySize != 0)
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if(secondarySize != 0)
            holder.message.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondarySize);
        if(timeSize != 0)
            holder.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeSize);

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(!notiyfyByFilter){
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
    }

    @Override
    public Filter getFilter() {
        if (conversationFilter == null) {
            conversationFilter = new ConversationFilter(conversationList);
        }else{
            conversationFilter.updata(conversationList);
        }
        return conversationFilter;
    }


    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }

    public void setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
    }

    public void setSecondarySize(int secondarySize) {
        this.secondarySize = secondarySize;
    }

    public void setTimeSize(float timeSize) {
        this.timeSize = timeSize;
    }


    private class ConversationFilter extends Filter {
        List<EMConversation> mOriginalValues = null;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        public void  updata(List<EMConversation> mList) {
            mOriginalValues = mList;
        }
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EMConversation>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyConversationList;
                results.count = copyConversationList.size();
            } else {
                if (copyConversationList.size() > mOriginalValues.size()) {
                    mOriginalValues = copyConversationList;
                }
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

                for (int i = 0; i < count; i++) {

                    final EMConversation value = mOriginalValues.get(i);
                    EMMessage lastMessage = value.getLastMessage();
                    UserEntity friendUser = null;
                    List<UserEntity> localFriendList = null;
                    if(!lastMessage.getTo().equals(UserDataManger.myUserData.getUserId()))
                    {
                        localFriendList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().queryBuilder().where(UserEntityDao.Properties.UserId.eq(lastMessage.getTo())).list();
                        if(localFriendList.size() > 0)
                            friendUser = localFriendList.get(0);
                    }else{
                        localFriendList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().queryBuilder().where(UserEntityDao.Properties.UserId.eq(lastMessage.getFrom())).list();
                        if(localFriendList.size() > 0)
                            friendUser = localFriendList.get(0);
                    }
                    String username = new  String(RxEncodeTool.base64Decode(friendUser.getNickName()));
                    if(friendUser.getRemarks() != null && !friendUser.getRemarks().equals(""))
                    {
                        username = new  String(RxEncodeTool.base64Decode(friendUser.getRemarks()));
                    }
                    // First match against the whole ,non-splitted value
                    if (username.toLowerCase().contains(prefixString.toLowerCase())) {
                        newValues.add(value);
                    } else{
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.toLowerCase().contains(prefixString.toLowerCase())) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            conversationList.clear();
            if (results.values != null) {
                conversationList.addAll((List<EMConversation>) results.values);
            }
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private EaseConversationListHelper cvsListHelper;

    public void setCvsListHelper(EaseConversationListHelper cvsListHelper){
        this.cvsListHelper = cvsListHelper;
    }

    private static class ViewHolder {
        /** who you chat with */
        TextView name;
        /** unread message count */
        TextView unreadLabel;
        /** content of last message */
        TextView message;
        /** time of last message */
        TextView time;
        /** avatar */
        ImageButtonWithText avatar;
        /** status of last message */
        View msgState;
        /** layout */
        RelativeLayout list_itease_layout;
        TextView motioned;
    }
}

