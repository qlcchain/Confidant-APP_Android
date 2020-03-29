/*
 * Copyright 2018 Lake Zhang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smailnet.eamil;


import android.app.Activity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.gmail.Gmail;
import com.smailnet.eamil.Callback.GetAttachCallback;
import com.smailnet.eamil.Callback.GetCountCallback;
import com.smailnet.eamil.Callback.GetGmailReceiveCallback;
import com.smailnet.eamil.Callback.GetReceiveCallback;
import com.smailnet.eamil.Callback.GmailAuthCallback;
import com.smailnet.eamil.Callback.MarkCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;

/**
 * Email for Android是基于JavaMail封装的电子邮件库，简化在Android客户端中编写
 * 发送和接收电子邮件的的代码。把它集成到你的Android项目中，只需简单配置邮件服务
 * 器，即可使用，所见即所得哦！
 *
 * @author
 * @author
 * @version 2.3
 */
public class EmailReceiveClient {

    private EmailConfig emailConfig;

    public EmailReceiveClient(EmailConfig emailConfig){
        this.emailConfig = emailConfig;
    }

    /**
     * 使用POP3协议异步接收邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void popReceiveAsyn(final Activity activity, final GetReceiveCallback getReceiveCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<EmailMessage> messageList = Operator.Core(emailConfig).popReceiveMail();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, messageList.size(),0,false,"","");
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 使用POP3协议异步接收邮件，接收完毕但不切回主线程
     * @param getReceiveCallback
     */
    public void popReceiveAsyn(final GetReceiveCallback getReceiveCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<EmailMessage> messageList = Operator.Core(emailConfig).popReceiveMail();
                    getReceiveCallback.gainSuccess(messageList, messageList.size(),0,false,"","");
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    getReceiveCallback.gainFailure(e.toString());
                } catch (final IOException e) {
                    e.printStackTrace();
                    getReceiveCallback.gainFailure(e.toString());
                }
            }
        }).start();
    }
    /**
     * 使用imap协议接收新邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void imapReceiveNewAsyn(final Activity activity, final GetReceiveCallback getReceiveCallback, final String menu, final long beginIndex, final int pageSize,final long lastTotalCount){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final HashMap<String, Object> messageMap = Operator.Core(emailConfig).imapReceiveNewMailByUUID(menu,beginIndex,pageSize,lastTotalCount);
                    final List<EmailMessage> messageList = (List<EmailMessage>)messageMap.get("emailMessageList");
                   /* messageMap.put("totalCount",totalSize);
                    messageMap.put("totalUnreadCount",totalUnreadCount);*/
                    final int totalCount = (int)messageMap.get("totalCount");
                    final int totalUnreadCount = (int)messageMap.get("totalUnreadCount");
                    final long minUIIDNew = (long)messageMap.get("minUIID");
                    final long maxUUIDNew = (long)messageMap.get("maxUUID");
                    final Boolean noMoreData = (Boolean)messageMap.get("noMoreData");
                    final String errorMsg = (String)messageMap.get("errorMsg");
                    final String menuFlag = (String)messageMap.get("menu");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, totalCount,maxUUIDNew,noMoreData,errorMsg,menuFlag);
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用imap协议接收历史邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void imapReceiveMoreAsyn(final Activity activity, final GetReceiveCallback getReceiveCallback, final String menu, final int beginIndex, final int pageSize,final int lastTotalCount){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final HashMap<String, Object> messageMap = Operator.Core(emailConfig).imapReceiveMoreMail(menu,beginIndex,pageSize,lastTotalCount);
                    final List<EmailMessage> messageList = (List<EmailMessage>)messageMap.get("emailMessageList");
                   /* messageMap.put("totalCount",totalSize);
                    messageMap.put("totalUnreadCount",totalUnreadCount);*/
                    final int totalCount = (int)messageMap.get("totalCount");
                    final int totalUnreadCount = (int)messageMap.get("totalUnreadCount");
                    final Boolean noMoreData = (Boolean)messageMap.get("noMoreData");
                    final String errorMsg = (String)messageMap.get("errorMsg");
                    final String menuFlag = (String)messageMap.get("menu");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, totalCount,totalUnreadCount,noMoreData,errorMsg,menuFlag);
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用imap协议接收历史邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void gmailReceiveNewAsyn(final Gmail gmailService, final String userId, final Activity activity, final GetGmailReceiveCallback getReceiveCallback, final String menu, final String pageToken, final long pageSize, final String firstMessageId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final HashMap<String, Object> messageMap = Operator.Core(emailConfig).gmailReceiveNewMail(gmailService,userId,menu,pageToken,pageSize,firstMessageId);
                    final List<EmailMessage> messageList = (List<EmailMessage>)messageMap.get("emailMessageList");
                   /* messageMap.put("totalCount",totalSize);
                    messageMap.put("totalUnreadCount",totalUnreadCount);*/
                    final int totalCount = (int)messageMap.get("totalCount");
                    final int totalUnreadCount = (int)messageMap.get("totalUnreadCount");
                    final Boolean noMoreData = (Boolean)messageMap.get("noMoreData");
                    final String errorMsg = (String)messageMap.get("errorMsg");
                    final String menuFlag = (String)messageMap.get("menu");
                    final String pageToken = (String)messageMap.get("pageToken");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, totalCount,totalUnreadCount,noMoreData,errorMsg,menuFlag,pageToken);
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final GooglePlayServicesAvailabilityIOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.googlePlayFailure(e);
                        }
                    });
                }catch (final UserRecoverableAuthIOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.authFailure(e);
                        }
                    });
                }catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用imap协议接收历史邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void gmailReceiveMoreAsyn(final Gmail gmailService, final String userId, final Activity activity, final GetGmailReceiveCallback getReceiveCallback, final String menu, final String pageToken, final long pageSize, final int lastTotalCount){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final HashMap<String, Object> messageMap = Operator.Core(emailConfig).gmailReceiveMoreMail(gmailService,userId,menu,pageToken,pageSize,lastTotalCount);
                    final List<EmailMessage> messageList = (List<EmailMessage>)messageMap.get("emailMessageList");
                   /* messageMap.put("totalCount",totalSize);
                    messageMap.put("totalUnreadCount",totalUnreadCount);*/
                    final int totalCount = (int)messageMap.get("totalCount");
                    final int totalUnreadCount = (int)messageMap.get("totalUnreadCount");
                    final Boolean noMoreData = (Boolean)messageMap.get("noMoreData");
                    final String errorMsg = (String)messageMap.get("errorMsg");
                    final String menuFlag = (String)messageMap.get("menu");
                    final String pageToken = (String)messageMap.get("pageToken");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, totalCount,totalUnreadCount,noMoreData,errorMsg,menuFlag,pageToken);
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }catch (final GooglePlayServicesAvailabilityIOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.googlePlayFailure(e);
                        }
                    });
                } catch (final UserRecoverableAuthIOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.authFailure(e);
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用imap协议接收历史邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void imapReceiveOneAsynByUUID(final Activity activity, final GetReceiveCallback getReceiveCallback, final String menu, final long minUUID, final int pageSize,final long maxUUID){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final HashMap<String, Object> messageMap = Operator.Core(emailConfig).imapReceiveOneMailByUUID(menu,minUUID,pageSize,maxUUID);
                    final List<EmailMessage> messageList = (List<EmailMessage>)messageMap.get("emailMessageList");
                   /* messageMap.put("totalCount",totalSize);
                    messageMap.put("totalUnreadCount",totalUnreadCount);*/
                    final int totalCount = (int)messageMap.get("totalCount");
                    final int totalUnreadCount = (int)messageMap.get("totalUnreadCount");
                    final long minUIIDNew = (long)messageMap.get("minUIID");
                    final long maxUUIDNew = (long)messageMap.get("maxUUID");
                    final Boolean noMoreData = (Boolean)messageMap.get("noMoreData");
                    final String errorMsg = (String)messageMap.get("errorMsg");
                    final String menuFlag = (String)messageMap.get("menu");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, minUIIDNew,maxUUIDNew,noMoreData,errorMsg,menuFlag);
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用imap协议接收历史邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void imapReceiveMoreAsynByUUID(final Activity activity, final GetReceiveCallback getReceiveCallback, final String menu, final long minUUID, final int pageSize,final long maxUUID){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final HashMap<String, Object> messageMap = Operator.Core(emailConfig).imapReceiveMoreMailByUUID(menu,minUUID,pageSize,maxUUID);
                    final List<EmailMessage> messageList = (List<EmailMessage>)messageMap.get("emailMessageList");
                   /* messageMap.put("totalCount",totalSize);
                    messageMap.put("totalUnreadCount",totalUnreadCount);*/
                    final int totalCount = (int)messageMap.get("totalCount");
                    final int totalUnreadCount = (int)messageMap.get("totalUnreadCount");
                    final long minUIIDNew = (long)messageMap.get("minUIID");
                    final long maxUUIDNew = (long)messageMap.get("maxUUID");
                    final Boolean noMoreData = (Boolean)messageMap.get("noMoreData");
                    final String errorMsg = (String)messageMap.get("errorMsg");
                    final String menuFlag = (String)messageMap.get("menu");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, minUIIDNew,maxUUIDNew,noMoreData,errorMsg,menuFlag);
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用imap协议接收邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void imapReceiveAsynCount(final Activity activity, final GetCountCallback getReceiveCallback,final ArrayList<String> menuList){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<EmailCount> messageList = Operator.Core(emailConfig).imapReceiveMailCountAndMenu(menuList);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, messageList.size());
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用gmail api 接收邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void gmaiApiAsynCount(final Activity activity, final GetCountCallback getReceiveCallback,final ArrayList<String> menuList,final Gmail gmailService,final String userId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<EmailCount> messageList = Operator.Core(emailConfig).gmailReceiveMailCountAndMenu(menuList,gmailService,userId);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, messageList.size());
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用gmail api 接收邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void gmaiApiToken(final Activity activity, final GmailAuthCallback getReceiveCallback, final Gmail gmailService, final String userId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<EmailCount> messageList = Operator.Core(emailConfig).gmailMailToken(gmailService,userId);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, messageList.size());
                        }
                    });
                }catch (final GooglePlayServicesAvailabilityIOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.googlePlayFailure(e);
                        }
                    });
                }catch (final UserRecoverableAuthIOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.authFailure(e);
                        }
                    });
                }catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用imap协议接收邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void imapDownloadEmailAttach(final Activity activity, final GetAttachCallback getReceiveCallback, final String menu, final String uid,final String path,final String aesKey){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<MailAttachment> messageList = Operator.Core(emailConfig).imapDownloadMailAttch(menu,uid,path,aesKey);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, messageList.size());
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用gmail API接收邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void gmailDownloadEmailAttach(final Activity activity, final GetAttachCallback getReceiveCallback, final String menu, final String uid,final String path,final String aesKey,final Gmail gmailService,final String userId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<MailAttachment> messageList = Operator.Core(emailConfig).gmailDownloadMailAttch(menu,uid,path,aesKey,gmailService,userId);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, messageList.size());
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用imap协议接收邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void gmailDownloadEmailCid(final Activity activity, final GetAttachCallback getReceiveCallback, final String menu, final String uid,final String path,final String aesKey,final Gmail gmailService,final String userId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<MailAttachment> messageList = Operator.Core(emailConfig).gmailDownloadMailCid(menu,uid,path,aesKey,gmailService,userId);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, messageList.size());
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用imap协议接收邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void imapDownloadEmailCid(final Activity activity, final GetAttachCallback getReceiveCallback, final String menu, final String uid,final String path,final String aesKey){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<MailAttachment> messageList = Operator.Core(emailConfig).imapDownloadMailCid(menu,uid,path,aesKey);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(messageList, messageList.size());
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用imap协议接收邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void imapMarkEmail(final Activity activity, final MarkCallback getReceiveCallback, final String menu, final String uid, final int flag,final boolean value, final String toMenu){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final boolean result = Operator.Core(emailConfig).imapMarkMail(menu,uid,flag,value,toMenu);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(result);
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用imap协议接收邮件，接收完毕并切回主线程
     * @param getReceiveCallback
     */
    public void gmailMarkEmail(final Activity activity, final MarkCallback getReceiveCallback, Gmail service, String userId, String messageId,
                               List<String> labelsToAdd, List<String> labelsToRemove){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final boolean result = Operator.Core(emailConfig).gmailMarkMail(service,userId,messageId,labelsToAdd,labelsToRemove);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(result);
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }
    /**
     * 使用gmail API 删除邮件
     * @param getReceiveCallback
     */
    public void gmailDeleteEmail(final Gmail gmailService, final String userId,final Activity activity, final MarkCallback getReceiveCallback, final String threadId,final Boolean delete){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final boolean result = Operator.Core(emailConfig).gmailDeleteMail(gmailService,userId,threadId,delete);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainSuccess(result);
                        }
                    });
                } catch (final MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getReceiveCallback.gainFailure(e.toString());
                        }
                    });
                }
            }
        }).start();
    }

}
