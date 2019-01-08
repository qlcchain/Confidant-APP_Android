package com.stratagile.pnrouter.data.web.java;

import okio.ByteString;

import okhttp3.WebSocket;

interface IWsManager {

    WebSocket getWebSocket();

    void startConnect();

    void stopConnect();

    boolean isWsConnected();

    int getCurrentStatus();

    void setCurrentStatus(int currentStatus);

    boolean sendMessage(String msg);

    boolean sendMessage(ByteString byteString);
}
