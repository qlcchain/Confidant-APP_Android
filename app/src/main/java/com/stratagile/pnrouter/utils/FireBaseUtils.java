package com.stratagile.pnrouter.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FireBaseUtils {
    //url = https://github.com/huzhipeng111/Confidant-APP_Android.git
    public static String eventLogin = "login";
    public static String eventStartApp = "startApp";
    public static String eventRegiester = "regiester";
    public static String eventTest = "test";

    public static String FIR_ADD_NEW_CHAT = "main_Add_newchat";
    public static String FIR_ADD_NEW_EMAIL = "main_Add_email";
    public static String FIR_ADD_CONTACTS = "main_Add_addcontacts";
    public static String FIR_ADD_INVITE_FRIENDS = "main_Add_invite_friends";
    public static String FIR_ADD_MEMBERS = "main_Add_members";
    public static String FIR_IMPORT_ACCOUNT = "import_account";
    public static String FIR_LOGIN = "start_login";
    public static String FIR_REGISTER = "start_register";
    public static String FIR_EMAIL_CONFIG = "start_email_config";
    public static String FIR_EMAIL_SEND = "start_emailSend";
    public static String FIR_CHAT_SEND_TEXT = "start_chat_send_text";
    public static String FIR_CHAT_DEL = "start_chat_delete";
    public static String FIR_CHAT_SEND_FILE = "start_chat_sendFile";
    public static String FIR_CHAT_ADD_FRIEND = "start_chat_AddFriend";
    public static String FIR_CHAT_ADD_GROUP = "start_chat_AddGroup";
    public static String FIR_CHAT_SEND_GROUP_TEXT = "start_chat_SendGroupText";
    public static String FIR_CHAT_SEND_GROUP_FILE = "start_chat_SendGroupFile";
    public static String FIR_FLODER_CREATE = "start_floderCreate";
    public static String FIR_FLODER_UPLOAD_FILE = "start_floderUploadFile";
    public static String FIR_CONTACTS_SYNC = "start_contactsSync";
    public static String FIR_CONTACTS_RECOVER = "start_contactsRecover";
    public static String FIR_CONTACT_DEL = "start_contactDelete";

    public static void logEvent(Context context, String event) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, event);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, event);
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle);
    }
}
