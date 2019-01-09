
package com.stratagile.pnrouter.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;


public class MobileSocketClient {
    private static MobileSocketClient mobileSocketClient = null;
    private static String BROADCAST_IP = "255.255.255.255";//239.0.0.1   224.0.0.254
    private static String RECEIVERBROADCAST_IP = "0.0.0.0";//239.0.0.1   224.0.0.254
    private static int BROADCAST_PORT = 18000;
    private static final String TAG = "MobileSocketClient";
    private InetAddress inetAddress;
    private InetAddress inetAddressReceiver;
    private Handler handler;
    private WifiManager.MulticastLock multicastLock;
    private DatagramSocket socket;
    private List<AsyncTask> asyncList= new ArrayList<>();
    private List<Thread> threadList= new ArrayList<>();
    public static final int MSG_UPD_DATA = 104;

    private MobileSocketClient() {
        try {

            //初始化组播
            inetAddress = InetAddress.getByName(BROADCAST_IP);
            inetAddressReceiver = InetAddress.getByName(RECEIVERBROADCAST_IP);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void init(Handler handler,Context context) {
        this.handler = handler;
    }

    public static MobileSocketClient getInstance() {
        if (mobileSocketClient == null) {
            mobileSocketClient = new MobileSocketClient();
        }
        return mobileSocketClient;
    }

    //发送数据
    public void send(final String content) {
        AsyncTask temp =  new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... paramVarArgs) {
                byte[] data = paramVarArgs[0].getBytes();
                try {
                    if(socket == null)
                        socket = new DatagramSocket();
                    DatagramPacket sendPack = new DatagramPacket(data,
                            data.length, inetAddress,
                            BROADCAST_PORT);
                    if(socket != null)
                        socket.send(sendPack);
                    System.out.println("Client send msg complete");
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
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,content);
        asyncList.add(temp);
    }

    public void receive ()
    {
        Thread temp = new Thread(new Runnable() {
            @Override
            public void run() {
                String message ="";
                try
                {
                    byte[] data = new byte[256];
                    int count = 0;
                    if(socket == null)
                        socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    //receive()是阻塞方法，会等待客户端发送过来的信息
                    while(true){
                        try
                        {
                            socket.receive(packet);
                        }catch (Exception e ) {
                            if(socket != null)
                            {
                                socket.close();
                                socket = null;
                            }
                            System.out.println( "Thread interrupted..." );
                            break;
                        }
                        String dataStr = new String(packet.getData(), 0, packet.getLength());
                        if(!dataStr.contains("QLC"))
                            message = dataStr;
                        count ++;
                        if(count >= 3)
                        {
                            if(socket != null)
                            {
                                socket.close();
                                socket = null;
                            }

                        }else{
                            if(!message.equals(""))
                            {
                                if(socket != null)
                                {
                                    socket.close();
                                    socket = null;
                                }

                            }
                        }
                        System.out.println("ipdizhi:"+message);
                        break;
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("ipdizhi:no");
                    Message msg = new Message();
                    msg.what = MSG_UPD_DATA;
                    msg.obj = "";
                    if(handler != null )
                        handler.sendMessage(msg);
                }finally {
                    Message msg = new Message();
                    msg.what = MSG_UPD_DATA;
                    msg.obj = message;
                    if(handler!=null)
                        handler.sendMessage(msg);
                }

            }
        });
        temp.start();
        threadList.add(temp);
    }
    public void destroy()
    {
        for (int i = 0 ; i < asyncList.size() ;i++)
        {
            if(asyncList.get(i) != null)
            {
                asyncList.get(i).cancel(true);
            }
        }
        for (int i = 0 ; i < asyncList.size() ;i++)
        {
            System.out.println("asyncList"+asyncList.get(i).isCancelled());
        }
        asyncList = new ArrayList<>();

        try{
            System.out.println("threadList leng"+ threadList.size());
            for (int i = 0 ; i < threadList.size() ;i++)
            {
                if(threadList.get(i) != null)
                {
                    threadList.get(i).interrupt();
                }
            }
            for (int i = 0 ; i < threadList.size() ;i++)
            {
                System.out.println("threadList"+threadList.get(i).isInterrupted()+"_"+threadList.get(i).isAlive());
                Thread aa= threadList.get(i);
                aa = null;
            }
            threadList = new ArrayList<>();
        }catch (Exception e)
        {

        }

    }

}

