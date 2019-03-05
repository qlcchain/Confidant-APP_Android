package com.stratagile.pnrouter.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hyphenate.easeui.utils.PathUtils;
import com.socks.library.KLog;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.entity.MyFile;
import com.stratagile.pnrouter.entity.events.FileStatus;
import com.stratagile.pnrouter.entity.file.UpLoadFile;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class FileMangerDownLoaderTask extends AsyncTask<Void, Integer, Long> {
	private final String TAG = "FileDownLoaderTask";
	private final int PER = 1024;
	private URL mUrl;
	private File mFile;
	private int mProgress = 0;
	private ProgressReportingOutputStream mOutputStream;
	private Context mContext;
	private Handler handler;
	private int bytesCopiedFlag;
	private int msgID;
	private String keyStr;
	private String fileUlr;
	private static HashMap<String, String> downFilePathTaskMap = new HashMap<>();
	private HashMap<String,Boolean> progressReceiveMap = new HashMap<>();
	private int progressBarMaxSeg = 25;
	private int fileFrom = 0;
	private String outPath = "";
	private String files_Temp_dir = "";
	private String FileNameOld;

	/**
	 *
	 * @param url 资源路径
	 * @param out 保存目录
	 * @param context 上下文
	 * @param message 消息  0x55:表示成功 ，0x404:下载路径错误或者网络问题
	 */
	public FileMangerDownLoaderTask(String url, String out, Context context, int msgId, Handler message, String key, HashMap<String, String> downFilePathMap,int FileFrom){
		super();
		bytesCopiedFlag = 0;
		msgID = msgId;
		keyStr = key;
		fileUlr = url;
		fileFrom = FileFrom;
		outPath = out;
		downFilePathTaskMap = downFilePathMap;
		files_Temp_dir = PathUtils.getInstance().getTempPath().toString() + "/";
		if(context!=null){
			mContext = context;
			handler = message;
		}
		try {
			mUrl = new URL(url);
			String fileName = new File(mUrl.getFile()).getName();
			FileNameOld = new String(Base58.decode(fileName));
			mFile = new File(files_Temp_dir, FileNameOld);
			KLog.d(TAG+":out="+files_Temp_dir+", name="+FileNameOld+",mUrl.getFile()="+mUrl.getFile());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		//super.onPreExecute();
		try {

		} catch (Exception e)
		{

		}

	}

	@Override
	protected Long doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return download();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		//super.onProgressUpdate(values);

	}

	@Override
	protected void onPostExecute(Long result) {
		// TODO Auto-generated method stub
		//super.onPostExecute(result);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("msgID",msgID);
				msg.setData(data);
				if(isCancelled())
				{
					msg.what = 0x404;
					downFilePathTaskMap.remove(msgID+"");
					handler.sendMessage(msg);
					return;
				}
				if(bytesCopiedFlag != 0)
				{
					String temp = files_Temp_dir +FileNameOld;
					String out = outPath +FileNameOld;
                    String aesKey = "";
                    if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                    {
                        aesKey = LibsodiumUtil.INSTANCE.DecryptShareKey(keyStr);
                    }else{
                        aesKey = RxEncodeTool.getAESKey(keyStr);
                    }
					int result = FileUtil.copyTempFiletoFileAndDecrypt(temp,out,aesKey);
					downFilePathTaskMap.remove(msgID+"");
					if(result == 1)
					{
						DeleteUtils.deleteFile(temp);
						String fileNiName = fileUlr.substring(fileUlr.lastIndexOf("/")+1,fileUlr.length());
						UpLoadFile uploadFile = new UpLoadFile(fileNiName,fileUlr,bytesCopiedFlag, true, true, false,1,1,0,false,keyStr,fileFrom,0,msgID+"");
						MyFile myRouter = new MyFile();
						myRouter.setType(0);
						myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
						myRouter.setUpLoadFile(uploadFile);
						LocalFileUtils.INSTANCE.updateLocalAssets(myRouter);
						EventBus.getDefault().post(new FileStatus(fileNiName,bytesCopiedFlag, true, true, false,1,1,0,false,0));
						msg.what = 0x55;
					}else{
						msg.what = 0x404;
					}
				}
				else
				{
					downFilePathTaskMap.remove(msgID+"");
					msg.what = 0x404;
				}
				handler.sendMessage(msg);
			}
		}, 500);
	}

	private long download(){
		//URLConnection connection = null;
		HttpURLConnection conn = null;
		int bytesCopied = 0;
		try {
			if(mUrl == null)
			{
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("msgID",msgID);
				msg.setData(data);
				msg.what = 0x404;
				handler.sendMessage(msg);
				return 0L;
			}
			//connection = mUrl.openConnection();

			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
				public X509Certificate[] getAcceptedIssuers(){return null;}
				public void checkClientTrusted(X509Certificate[] certs, String authType){}
				public void checkServerTrusted(X509Certificate[] certs, String authType){}
			}};

			// Install the all-trusting trust manager
			try {// 注意这部分一定要
				HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(null, trustAllCerts, new SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				conn = (HttpURLConnection) mUrl.openConnection();
				conn.connect();
				System.out.println(conn.getResponseCode() + " " + conn.getResponseMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
			int length = conn.getContentLength();
			/*if(mFile.exists()&&length == mFile.length()){
				KLog.d(TAG, "file "+mFile.getName()+" already exits!!");
				return 0l;
			}*/


			mOutputStream = new ProgressReportingOutputStream(mFile);
			publishProgress(0,length);
			InputStream inputStream = conn.getInputStream();
			bytesCopied =copy(inputStream,mOutputStream,length);
			if(bytesCopied!=length&&length!=-1){
				KLog.e(TAG+":Download incomplete bytesCopied="+bytesCopied+", length"+length);
			}else{
				KLog.e(TAG+":Download complete");
			}
			mOutputStream.close();
		} catch (IOException e) {
			DeleteUtils.deleteFile(mFile.getPath());
			e.printStackTrace();
		}finally {
			KLog.d(TAG+":nopath");
		}
		bytesCopiedFlag = bytesCopied;
		return bytesCopied;
	}
	private int copy(InputStream input, OutputStream output,int length){
		/*InputStream newInput = input;
		try {
			String aesKey = "";
			if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
			{
				aesKey =  LibsodiumUtil.INSTANCE.DecryptShareKey(keyStr);
			}else{
				aesKey =  RxEncodeTool.getAESKey(keyStr);

			}
			byte[] fileBufferMi =  FileUtil.InputStreamTOByte(input);
			byte [] miFile = AESCipher.aesDecryptBytes(fileBufferMi,aesKey.getBytes("UTF-8"));
			newInput = FileUtil.byteTOInputStream(miFile);
		}catch (Exception e)
		{
			KLog.i("FileMangerDownLoaderTask jiemi  error ");
		}*/
		byte[] buffer = new byte[1024*1024*10];
		BufferedInputStream in = new BufferedInputStream(input, 1024*1024*10);
		BufferedOutputStream out  = new BufferedOutputStream(output, 1024*1024*10);
		int count =0,n=0;
		try {
			while((n=in.read(buffer, 0, 1024*1024*10))!=-1){
				if(ConstantValue.INSTANCE.getLoginOut())
				{
					return 0;
				}
				out.write(buffer, 0, n);
				count+=n;
				long progress = (count * 100) / length;
				String fileNiName = fileUlr.substring(fileUlr.lastIndexOf("/")+1,fileUlr.length());
				int average = length / progressBarMaxSeg;
				if(average <= 0)
				{
					average = 1;
				}
				int num = (int)(count / average) + 1;
				if(progressReceiveMap.get(fileNiName+"_"+num) == null)
				{
					UpLoadFile uploadFile = new UpLoadFile(fileNiName,fileUlr,length, true, false, false,count,length,0,false,keyStr,fileFrom,0,msgID+"");
					MyFile myRouter = new MyFile();
					myRouter.setType(0);
					myRouter.setUserSn(ConstantValue.INSTANCE.getCurrentRouterSN());
					myRouter.setUpLoadFile(uploadFile);
					LocalFileUtils.INSTANCE.insertLocalAssets(myRouter);
					EventBus.getDefault().post(new FileStatus(fileNiName,length, true, false, false,count,length,0,false,0));
					progressReceiveMap.put(fileNiName+"_"+num,true);
				}

				KLog.d(TAG+":downloading:"+ progress +"%");
			}
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return count;
	}
	private final class ProgressReportingOutputStream extends FileOutputStream{

		public ProgressReportingOutputStream(File file)
				throws FileNotFoundException {
			super(file);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void write(byte[] buffer, int byteOffset, int byteCount)
				throws IOException {
			// TODO Auto-generated method stub
			super.write(buffer, byteOffset, byteCount);
			mProgress += byteCount;
			publishProgress(mProgress);
		}

	}

	public class NullHostNameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			KLog.i("RestUtilImpl Approving certificate for " + hostname);
			return true;
		}

	}

}

