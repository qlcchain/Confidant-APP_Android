package com.stratagile.pnrouter.utils;

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
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class FileDownLoaderTask extends AsyncTask<Void, Integer, Long> {
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

	/**
	 *
	 * @param url 资源路径
	 * @param out 保存目录
	 * @param context 上下文
	 * @param message 消息  0x55:表示成功 ，0x404:下载路径错误或者网络问题
	 */
	public FileDownLoaderTask(String url, String out, Context context,int msgId, Handler message){
		super();
		msgID = msgId;
		if(context!=null){
			mContext = context;
			handler = message;
		}
		try {
			mUrl = new URL(url);
			String fileName = new File(mUrl.getFile()).getName();
			String FileNameOld = new String(Base64.decode(fileName.getBytes(), Base64.DEFAULT));
			mFile = new File(out, FileNameOld);
			Log.d(TAG, "out="+out+", name="+FileNameOld+",mUrl.getFile()="+mUrl.getFile());
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
					handler.sendMessage(msg);
					return;
				}
				if(bytesCopiedFlag != 0)
					msg.what = 0x55;
				else
				{
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
				Log.d(TAG, "file "+mFile.getName()+" already exits!!");
				return 0l;
			}*/


			mOutputStream = new ProgressReportingOutputStream(mFile);
			publishProgress(0,length);
			bytesCopied =copy(conn.getInputStream(),mOutputStream);
			if(bytesCopied!=length&&length!=-1){
				Log.e(TAG, "Download incomplete bytesCopied="+bytesCopied+", length"+length);
			}
			mOutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			Log.d(TAG,"nopath");
		}
		bytesCopiedFlag = bytesCopied;
		return bytesCopied;
	}
	private int copy(InputStream input, OutputStream output){
		byte[] buffer = new byte[1024*8];
		BufferedInputStream in = new BufferedInputStream(input, 1024*8);
		BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
		int count =0,n=0;
		try {
			while((n=in.read(buffer, 0, 1024*8))!=-1){
				out.write(buffer, 0, n);
				count+=n;
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
			Log.i("RestUtilImpl", "Approving certificate for " + hostname);
			return true;
		}

	}

}

