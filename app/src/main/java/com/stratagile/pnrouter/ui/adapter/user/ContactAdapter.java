package com.stratagile.pnrouter.ui.adapter.user;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.hyphenate.easeui.EaseConstant;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.UserDataManger;
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity;
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity;
import com.stratagile.pnrouter.utils.RxEncodeTool;
import com.stratagile.pnrouter.view.ImageButtonWithText;

import java.util.List;

public class ContactAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ContactAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(0, R.layout.layout_contact_list_item);
        addItemType(1, R.layout.layout_contact_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case 0:
                helper.setGone(R.id.checkBox, false);
                final UserHead lv0 = (UserHead) item;
                String nickNameSouce = new String(RxEncodeTool.base64Decode(lv0.getUserName()));
                if(lv0.getSubItems() != null && lv0.getSubItems().size() > 1)
                {
                    helper.setVisible(R.id.ivArrow, true);
                    helper.setText(R.id.tvNickName, nickNameSouce + "(" + lv0.getSubItems().size() + ")");
                }else{
                    helper.setVisible(R.id.ivArrow, false);
                    helper.setText(R.id.tvNickName, nickNameSouce);
                }
                //helper.setText(R.id.ivAvatar, nickNameSouce);
                ImageButtonWithText imagebutton = helper.getView(R.id.ivAvatar);
                if (nickNameSouce != null) {
                    imagebutton.setText(nickNameSouce);
                }
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(lv0.getSubItems() != null && lv0.getSubItems().size() > 1)
                        {
                            int pos = helper.getAdapterPosition();
                            if (lv0.isExpanded()) {
                                helper.setImageResource(R.id.ivArrow, R.mipmap.arrow_down);
                                collapse(pos);
                            } else {
                                helper.setImageResource(R.id.ivArrow, R.mipmap.arrow_upper);
                                expand(pos);
                            }
                        }else{
                            int pos = helper.getAdapterPosition();
                            final UserHead data = (UserHead) getItem(pos);
                            Intent intent = new Intent(AppConfig.instance, UserInfoActivity.class);
                            intent.putExtra("user", data.getUserEntity());
                            mContext.startActivity(intent);
                        }

                    }
                });
                break;
            case 1:
                final UserItem lv1 = (UserItem) item;
                String nickNameSouce1 = new String(RxEncodeTool.base64Decode(lv1.getUserEntity().getRouteName()));
                helper.setText(R.id.routerName, nickNameSouce1);
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = helper.getAdapterPosition();
                        final UserItem data = (UserItem) getItem(pos);
                        Intent intent = new Intent(AppConfig.instance, UserInfoActivity.class);
                        intent.putExtra("user", data.getUserEntity());
                        mContext.startActivity(intent);
                    }
                });
                helper.setOnClickListener(R.id.tvChat, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = helper.getAdapterPosition();
                        final UserItem data = (UserItem) getItem(pos);
                        UserDataManger.curreantfriendUserData = data.getUserEntity();
                        mContext.startActivity(new Intent(mContext, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, data.getUserEntity().getUserId()));
                    }
                });
                break;
            default:
                break;
        }
    }
}
