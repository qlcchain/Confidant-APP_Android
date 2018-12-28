package com.hyphenate.easeui.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.stratagile.pnrouter.R;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.EaseUI.EaseUserProfileProvider;
import com.hyphenate.easeui.domain.EaseUser;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.UserDataManger;
import com.stratagile.pnrouter.db.UserEntity;
import com.stratagile.pnrouter.utils.RxEncodeTool;
import com.stratagile.pnrouter.view.ImageButtonWithText;

import java.util.List;

public class EaseUserUtils {

    static EaseUserProfileProvider userProvider;

    static {
        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * get EaseUser according username
     *
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username) {
        if (userProvider != null)
            return userProvider.getUser(username);

        return null;
    }

    /**
     * set user avatar
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        EaseUser user = getUserInfo(username);
        if (user != null && user.getAvatar() != null) {
            try {
                int avatarResId = Integer.parseInt(user.getAvatar());
                Glide.with(context).load(avatarResId).into(imageView);
            } catch (Exception e) {
                //use default avatar
                Glide.with(context)
                        .load(user.getAvatar())
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.ease_default_avatar)
                        .into(imageView);
            }
        } else {
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
        }
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username, TextView textView) {
        if (textView != null) {
            EaseUser user = getUserInfo(username);
            if (user != null && user.getNick() != null) {
                textView.setText(user.getNick());
            } else {
                textView.setText(username);
            }
        }
    }

    public static void setUserAvatar(String username, ImageButtonWithText textView) {
        if (textView != null) {
            List<UserEntity> userEntityList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().loadAll();
            for (int i = 0; i < userEntityList.size(); i++) {
                if (userEntityList.get(i).getUserId().equals(username)) {
                    String usernameSouce = new String(RxEncodeTool.base64Decode(userEntityList.get(i).getNickName()));
                    if(userEntityList.get(i).getRemarks() != null && !userEntityList.get(i).getRemarks().equals(""))
                    {
                        usernameSouce = new  String(RxEncodeTool.base64Decode(userEntityList.get(i).getRemarks()));
                    }
                    textView.setText(usernameSouce);
                    return;
                }
            }
            textView.setText(username);
        }
    }

}
