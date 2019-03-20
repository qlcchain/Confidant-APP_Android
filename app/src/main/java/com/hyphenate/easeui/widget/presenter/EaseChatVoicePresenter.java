package com.hyphenate.easeui.widget.presenter;

import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowVoice;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowVoicePlayer;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.noober.menu.FloatMenu;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.entity.BaseData;
import com.stratagile.pnrouter.entity.DelMsgReq;
import com.stratagile.pnrouter.entity.GroupDelMsgReq;
import com.stratagile.pnrouter.entity.events.DeleteMsgEvent;
import com.stratagile.pnrouter.utils.SpUtil;
import com.stratagile.tox.toxcore.ToxCoreJni;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import chat.tox.antox.tox.MessageHelper;
import chat.tox.antox.wrapper.FriendKey;
import im.tox.tox4j.core.enums.ToxMessageType;

/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatVoicePresenter extends EaseChatFilePresenter {
    private static final String TAG = "EaseChatVoicePresenter";
    private Context context;
    private View viewRoot;
    private EaseChatRowVoicePlayer voicePlayer;

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        voicePlayer = EaseChatRowVoicePlayer.getInstance(cxt);
        return new EaseChatRowVoice(cxt, message, position, adapter);
    }

    @Override
    public void onBubbleClick(final EMMessage message) {
        String msgId = message.getMsgId();
        String localUrl = ((EMVoiceMessageBody) message.getBody()).getLocalUrl();
        if(localUrl.contains("ease_default_amr"))
        {
            return;
        }
        if (voicePlayer.isPlaying()) {
            // Stop the voice play first, no matter the playing voice item is this or others.
            voicePlayer.stop();
            // Stop the voice play animation.
            ((EaseChatRowVoice) getChatRow()).stopVoicePlayAnimation();

            // If the playing voice item is this item, only need stop play.
            String playingId = voicePlayer.getCurrentPlayingId();
            if (msgId.equals(playingId)) {
                return;
            }
        }

        if (message.direct() == EMMessage.Direct.SEND) {
            // Play the voice
            String localPath = ((EMVoiceMessageBody) message.getBody()).getLocalUrl();
            File file = new File(localPath);
            if (file.exists() && file.isFile()) {
                playVoice(message);
                // Start the voice play animation.
                ((EaseChatRowVoice) getChatRow()).startVoicePlayAnimation();
            } else {
                asyncDownloadVoice(message);
            }
        } else {
            final String st = getContext().getResources().getString(R.string.Is_download_voice_click_later);
            if (message.status() == EMMessage.Status.SUCCESS) {
                if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                    play(message);
                } else {
                    EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
                    EMLog.i(TAG, "Voice body download status: " + voiceBody.downloadStatus());
                    switch (voiceBody.downloadStatus()) {
                        case PENDING:// Download not begin
                        case FAILED:// Download failed
                            getChatRow().updateView(getMessage());
                            asyncDownloadVoice(message);
                            break;
                        case DOWNLOADING:// During downloading
                            Toast.makeText(getContext(), st, Toast.LENGTH_SHORT).show();
                            break;
                        case SUCCESSED:// Download success
                            play(message);
                            break;
                    }
                }
            } else if (message.status() == EMMessage.Status.INPROGRESS) {
                Toast.makeText(getContext(), st, Toast.LENGTH_SHORT).show();
            } else if (message.status() == EMMessage.Status.FAIL) {
                Toast.makeText(getContext(), st, Toast.LENGTH_SHORT).show();
                asyncDownloadVoice(message);
            }
        }
    }
    @Override
    public void onBubbleLongClick(EMMessage message, View view) {
        super.onBubbleLongClick(message,view);
        if(!message.isAcked())
            return;
        String fromID = message.getFrom();
        viewRoot = view;
        String userId =   SpUtil.INSTANCE.getString(AppConfig.instance.getApplicationContext(), ConstantValue.INSTANCE.getUserId(), "");
        if(fromID.equals(userId))
        {
            FloatMenu floatMenu = new  FloatMenu(AppConfig.instance.getApplicationContext(),view);
            floatMenu.inflate(R.menu.popup_menu_voice);
            //floatMenu.items(AppConfig.instance.getResources().getString(R.string.withDraw), AppConfig.instance.getResources().getString(R.string.cancel));
            int[] loc1=new int[2];
            view.getLocationOnScreen(loc1);
            KLog.i(loc1[0]);
            KLog.i(loc1[1]);
            floatMenu.show(new Point(loc1[0],loc1[1]),0,65);
            floatMenu.setOnItemClickListener(new FloatMenu.OnItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    switch (position)
                    {
                        case 0:
                            String msgId = message.getMsgId();
                            ConstantValue.INSTANCE.setDeleteMsgId(message.getMsgId());
                            if(message.isAcked())
                            {
                                if(message.getChatType().equals( EMMessage.ChatType.GroupChat))
                                {
                                    GroupDelMsgReq msgData = new GroupDelMsgReq(0, userId, message.getTo(),Integer.valueOf(message.getMsgId()) ,"GroupDelMsg");
                                    BaseData baseData = new BaseData(4,msgData);
                                    if(ConstantValue.INSTANCE.isWebsocketConnected())
                                    {
                                        AppConfig.instance.getPNRouterServiceMessageSender().send(baseData);
                                    }else if(ConstantValue.INSTANCE.isToxConnected())
                                    {
                                        String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");
                                        if (ConstantValue.INSTANCE.isAntox()) {
                                            FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                                        }else{
                                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                        }
                                    }
                                }else{
                                    DelMsgReq msgData = new DelMsgReq( message.getFrom(), message.getTo(),Integer.valueOf(message.getMsgId()) ,"DelMsg");
                                    if(ConstantValue.INSTANCE.isWebsocketConnected())
                                    {
                                        AppConfig.instance.getPNRouterServiceMessageSender().send(new BaseData(msgData));
                                    }else if(ConstantValue.INSTANCE.isToxConnected())
                                    {
                                        BaseData baseData = new BaseData(msgData);
                                        String baseDataJson = JSONObject.toJSON(baseData).toString().replace("\\", "");

                                        if(ConstantValue.INSTANCE.isAntox())
                                        {
                                            FriendKey friendKey  = new FriendKey( ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                            MessageHelper.sendMessageFromKotlin(AppConfig.instance, friendKey, baseDataJson, ToxMessageType.NORMAL);
                                        }else{
                                            ToxCoreJni.getInstance().senToxMessage(baseDataJson, ConstantValue.INSTANCE.getCurrentRouterId().substring(0, 64));
                                        }


                                    }
                                }

                            }else{
                                EventBus.getDefault().post(new DeleteMsgEvent(msgId));
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (voicePlayer.isPlaying()) {
            voicePlayer.stop();
        }
    }

    private void asyncDownloadVoice(final EMMessage message) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                EMClient.getInstance().chatManager().downloadAttachment(message);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                getChatRow().updateView(getMessage());
            }
        }.execute();
    }

    private void play(EMMessage message) {
        String localPath = ((EMVoiceMessageBody) message.getBody()).getLocalUrl();
        File file = new File(localPath);
        if (file.exists() && file.isFile()) {
            ackMessage(message);
            playVoice(message);
            // Start the voice play animation.
            ((EaseChatRowVoice) getChatRow()).startVoicePlayAnimation();
        } else {
            EMLog.e(TAG, "file not exist");
        }
    }

    private void ackMessage(EMMessage message) {
        EMMessage.ChatType chatType = message.getChatType();
        if (!message.isAcked() && chatType == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
        if (!message.isListened()) {
            EMClient.getInstance().chatManager().setVoiceMessageListened(message);
        }
    }

    private void playVoice(EMMessage msg) {
        voicePlayer.play(msg, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // Stop the voice play animation.
                ((EaseChatRowVoice) getChatRow()).stopVoicePlayAnimation();
            }
        });
    }
}
