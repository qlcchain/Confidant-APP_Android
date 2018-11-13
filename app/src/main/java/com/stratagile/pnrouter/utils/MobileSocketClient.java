package com.stratagile.pnrouter.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;


public class MobileSocketClient {
    private static MobileSocketClient mobileSocketClient = null;
    private static String BROADCAST_IP = "224.0.0.254";//239.0.0.1   224.0.0.254
    private static int BROADCAST_PORT = 18000;
    private static final String TAG = "MobileSocketClient";
    private MulticastSocket multicastSocket;
    private InetAddress inetAddress;
    private Handler handler;
    private WifiManager.MulticastLock multicastLock;
    public static final int MSG_UPD_DATA = 104;

    private MobileSocketClient() {
        try {

            //初始化组播
            inetAddress = InetAddress.getByName(BROADCAST_IP);
            multicastSocket = new MulticastSocket(BROADCAST_PORT);
            multicastSocket.setTimeToLive(1);
            multicastSocket.joinGroup(inetAddress);
            multicastSocket.setLoopbackMode(false);// 必须是false才能开启广播功能
            multicastSocket.setNetworkInterface(NetworkInterface.getByName("wlan0"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void init(Handler handler,Context context) {
        this.handler = handler;
        WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        multicastLock = wifiManager.createMulticastLock("multicast.test");

    }

    public static MobileSocketClient getInstance() {
        if (mobileSocketClient == null) {
            mobileSocketClient = new MobileSocketClient();
        }
        return mobileSocketClient;
    }

    //发送数据
    public void send(final String content) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... paramVarArgs) {
                byte[] data = paramVarArgs[0].getBytes();
                //构造要发送的数据
                DatagramPacket dataPacket = new DatagramPacket(data,
                        data.length, inetAddress, BROADCAST_PORT);
                try {
                    multicastSocket.send(dataPacket);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "send failed";
                }
                return "send success";
            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                Message msg = new Message();
                //msg.what = TuringHandler.STATUS;
                msg.obj = result;
                //handler.sendMessage(msg);
            }
        }.execute(content);

    }

    public void receive ()
    {
        byte[] data = new byte[256];
        /*try {
            DatagramPacket packet = new DatagramPacket(data, data.length);
            //receive()是阻塞方法，会等待客户端发送过来的信息
            multicastSocket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println(message);
            multicastSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... paramVarArgs) {
                try {

                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    //receive()是阻塞方法，会等待客户端发送过来的信息
                    while(true){
                        multicastLock.acquire();
                        multicastSocket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("ipdizhi:"+message);
                        multicastLock.release();
                        multicastSocket.close();
                        return message;
                    }


                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "";
                }
                //return "send success";
            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                Message msg = new Message();
                msg.what = MSG_UPD_DATA;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        }.execute();
    }
    public void destroy()
    {
        multicastLock.release();
        multicastSocket.close();
        multicastLock = null;
        multicastSocket = null;
    }
}

