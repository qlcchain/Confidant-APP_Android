package com.stratagile.pnrouter.ui.adapter.user;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.hyphenate.easeui.EaseConstant;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.UserDataManger;
import com.stratagile.pnrouter.db.UserEntity;
import com.stratagile.pnrouter.entity.events.SelectFriendChange;
import com.stratagile.pnrouter.ui.activity.chat.ChatActivity;
import com.stratagile.pnrouter.ui.activity.user.UserInfoActivity;
import com.stratagile.pnrouter.utils.Base58;
import com.stratagile.pnrouter.utils.RxEncodeTool;
import com.stratagile.pnrouter.view.ImageButtonWithText;
import com.stratagile.pnrouter.view.OptAnimationLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private boolean isCheckMode = false;

    public boolean isCheckMode() {
        return isCheckMode;
    }

    public void setCheckMode(boolean checkMode) {
        isCheckMode = checkMode;
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ContactAdapter(List<MultiItemEntity> data) {
        super(data);
        //头
        addItemType(0, R.layout.layout_contact_list_item);
        //子
        addItemType(1, R.layout.layout_contact_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        int pos = helper.getAdapterPosition();
        switch (helper.getItemViewType()) {
            case 0:
                helper.setGone(R.id.checkBox, isCheckMode);
                final UserHead lv0 = (UserHead) item;
                if (isCheckMode) {
                    helper.setVisible(R.id.checkBox, true);
                    helper.setChecked(R.id.checkBox, lv0.isChecked());
                    if (lv0.getSubItems() != null && lv0.getSubItems().size() > 1) {
                        helper.setVisible(R.id.checkBox, false);
                    }
                }
                String nickNameSouce = new String(RxEncodeTool.base64Decode(lv0.getUserName()));
                String remarks = lv0.getRemarks();
                if(remarks != null && !remarks.equals(""))
                {
                    nickNameSouce = new String(RxEncodeTool.base64Decode(remarks));
                }
                if (lv0.getSubItems() != null && lv0.getSubItems().size() > 1) {
                    helper.setVisible(R.id.ivArrow, true);
                    if (lv0.isExpanded()) {
                        helper.setImageResource(R.id.ivArrow, R.mipmap.arrow_upper);
                    } else {
                        helper.setImageResource(R.id.ivArrow, R.mipmap.arrow_down);
                    }
                    helper.setText(R.id.tvNickName, nickNameSouce + "(" + lv0.getSubItems().size() + ")");
                } else {
                    helper.setVisible(R.id.ivArrow, false);
                    helper.setText(R.id.tvNickName, nickNameSouce);
                }
                //helper.setText(R.id.ivAvatar, nickNameSouce);
                ImageButtonWithText imagebutton = helper.getView(R.id.ivAvatar);
                if (nickNameSouce != null) {
//                    imagebutton.setText(nickNameSouce);
                    String avatarPath = Base58.encode(RxEncodeTool.base64Decode(lv0.getUserEntity().getSignPublicKey())) + ".jpg";
                    imagebutton.setImageFileInChat(avatarPath, nickNameSouce);
                }
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isCheckMode) {
                            if (lv0.getSubItems() != null && lv0.getSubItems().size() > 1) {
                                if (lv0.isExpanded()) {
                                    KLog.i("将要关闭");
                                    collapse(helper.getAdapterPosition());
                                } else {
                                    KLog.i("将要展开");
                                    expand(helper.getAdapterPosition());
                                }
                            } else {
                                lv0.setChecked(!lv0.isChecked());
                                notifyItemChanged(helper.getAdapterPosition(), "checkChange");
                                getSelectedCount(lv0.isChecked(), lv0.getUserEntity());
                            }
                        } else {
                            KLog.i("点击头。。" + helper.getAdapterPosition());
                            if (lv0.getSubItems() != null && lv0.getSubItems().size() > 1) {
                                if (lv0.isExpanded()) {
                                    KLog.i("将要关闭");
                                    collapse(helper.getAdapterPosition());
                                } else {
                                    KLog.i("将要展开");
                                    expand(helper.getAdapterPosition());
                                }
                            } else {
                                int pos = helper.getAdapterPosition();
                                final UserHead data = (UserHead) getItem(pos);
                                Intent intent = new Intent(AppConfig.instance, UserInfoActivity.class);
                                intent.putExtra("user", data.getUserEntity());
                                mContext.startActivity(intent);
                            }
                        }
                    }
                });
                break;
            case 1:
                final UserItem lv1 = (UserItem) item;
                helper.setGone(R.id.checkBox, isCheckMode);
                if (isCheckMode) {
                    helper.setGone(R.id.tvChat, false);
                    helper.setChecked(R.id.checkBox, lv1.isChecked());
                }
                if (lv1.getUserEntity().getRouterAlias() == null || "".equals(lv1.getUserEntity().getRouterAlias())) {
                    String nickNameSouce1 = new String(RxEncodeTool.base64Decode(lv1.getUserEntity().getRouteName()));
                    helper.setText(R.id.routerName, nickNameSouce1);
                } else {
                    String nickNameSouce1 = lv1.getUserEntity().getRouterAlias();
                    helper.setText(R.id.routerName, nickNameSouce1);
                }
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isCheckMode) {
                            lv1.setChecked(!lv1.isChecked());
                            getSelectedCount(lv1.isChecked(), lv1.getUserEntity());
                            notifyItemChanged(helper.getAdapterPosition(), "checkChange");
                        } else {
                            final UserItem data = (UserItem) getItem(helper.getAdapterPosition());
                            Intent intent = new Intent(AppConfig.instance, UserInfoActivity.class);
                            intent.putExtra("user", data.getUserEntity());
                            mContext.startActivity(intent);
                        }
                    }
                });
                helper.setOnClickListener(R.id.tvChat, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = helper.getAdapterPosition();
                        final UserItem data = (UserItem) getItem(pos);
                        UserDataManger.curreantfriendUserData = data.getUserEntity();
                        mContext.startActivity(new Intent(mContext, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, data.getUserEntity().getUserId()));
                        KLog.i("进入聊天页面，好友id为：" + data.getUserEntity().getUserId());
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item, @NonNull List<Object> payloads) {
        KLog.i("哈哈哈");
        if (payloads.get(0) instanceof String) {
            KLog.i(payloads.get(0));
            ImageView imageView = helper.getView(R.id.ivArrow);
            switch (payloads.get(0).toString()) {
                case "checkChange":
                    switch (helper.getItemViewType()) {
                        case 0:
                            helper.setGone(R.id.checkBox, isCheckMode);
                            final UserHead lv0 = (UserHead) item;
                            if (isCheckMode) {
                                helper.setChecked(R.id.checkBox, lv0.isChecked());
                            }
                            break;
                        case 1:
                            final UserItem lv1 = (UserItem) item;
                            helper.setGone(R.id.checkBox, isCheckMode);
                            if (isCheckMode) {
                                helper.setChecked(R.id.checkBox, lv1.isChecked());
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case "expand":
                    //展开
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView,"rotation",0, -180f);
                    objectAnimator.setDuration(300);
                    objectAnimator.start();
//                    imageView.startAnimation(OptAnimationLoader.loadAnimation(mContext, R.anim.anim_contact_list_open));
                    break;
                case "collapse":
                    //关闭
                    ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(imageView,"rotation",-180f, 0);
                    objectAnimator1.setDuration(300);
                    objectAnimator1.start();
//                    imageView.startAnimation(OptAnimationLoader.loadAnimation(mContext, R.anim.anim_contact_list_close));
                    break;
                default:
                    break;
            }
        }
    }

    public ArrayList<UserEntity> selectedUser = new ArrayList<>();

    private void getSelectedCount(boolean isCheck, UserEntity userEntity) {
        boolean has = false;
        for (UserEntity user : selectedUser) {
            if (user.getUserId().equals(userEntity.getUserId())) {
                has = true;
                break;
            }
        }
        if (!has && isCheck) {
            selectedUser.add(userEntity);
        }
        if (has && !isCheck) {
            selectedUser.remove(userEntity);
        }
        int count = 0;
        for (int i = 0; i < getData().size(); i++) {
            if (getData().get(i).getItemType() == 0) {
                UserHead userHead = (UserHead) getData().get(i);
                if (userHead.getSubItems() == null || userHead.getSubItems().size() == 0) {
                    if (userHead.isChecked()) {
                        count++;
                    }
                } else {
                    for (int j = 0; j < userHead.getSubItems().size(); j++) {
                        UserItem userItem = userHead.getSubItems().get(j);
                        if (userItem.isChecked()) {
                            count++;
                        }
                    }
                }
            }
        }
        EventBus.getDefault().post(new SelectFriendChange(count, 0, isCheck, userEntity));
    }
}
