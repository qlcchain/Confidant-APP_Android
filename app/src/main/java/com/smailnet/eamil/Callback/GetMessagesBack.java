package com.smailnet.eamil.Callback;

import com.smailnet.eamil.EmailMessage;

import java.util.HashMap;
import java.util.List;

public interface GetMessagesBack {
    void onBack(HashMap<String, Object>  messageList);

    void onPreBack(HashMap<String, Object>  messageList);
}
