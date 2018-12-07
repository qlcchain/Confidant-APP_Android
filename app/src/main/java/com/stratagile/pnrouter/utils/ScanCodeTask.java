package com.stratagile.pnrouter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;

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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class ScanCodeTask extends AsyncTask<Void, Integer, Bitmap> {
	private final String TAG = "FileDownLoaderTask";
	private final int PER = 1024;
	private String userIdRoot;
	private ImageView viewRoot;

	public ScanCodeTask(String userId, ImageView view){
		super();

		userIdRoot = userId;
		viewRoot = view;
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return download();
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (bitmap != null) {
			viewRoot.setImageBitmap(bitmap);
		} else {
			Toast.makeText(AppConfig.instance, "Generation failure", Toast.LENGTH_SHORT).show();
		}
	}

	private Bitmap download(){

		return QRCodeEncoder.syncEncodeQRCode(userIdRoot, BGAQRCodeUtil.dp2px(AppConfig.instance, 150f), AppConfig.instance.getResources().getColor(R.color.mainColor));
	}


}

