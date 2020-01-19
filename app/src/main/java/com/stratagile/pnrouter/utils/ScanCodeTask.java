package com.stratagile.pnrouter.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;

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
			Toast.makeText(AppConfig.instance, R.string.waitamoment, Toast.LENGTH_SHORT).show();
		}
	}

	private Bitmap download(){

		return QRCodeEncoder.syncEncodeQRCode(userIdRoot, BGAQRCodeUtil.dp2px(AppConfig.instance, 150f), AppConfig.instance.getResources().getColor(R.color.mainColor));
	}


}

