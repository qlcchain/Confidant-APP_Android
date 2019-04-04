/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.model.EaseCompat;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.utils.EaseImageUtils;
import com.hyphenate.easeui.widget.photoview.EasePhotoView;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.ImageUtils;
import com.stratagile.pnrouter.R;
import com.stratagile.pnrouter.application.AppConfig;
import com.stratagile.pnrouter.constant.ConstantValue;
import com.stratagile.pnrouter.db.FriendEntity;
import com.stratagile.pnrouter.db.FriendEntityDao;
import com.stratagile.pnrouter.db.UserEntity;
import com.stratagile.pnrouter.entity.events.SaveMsgEvent;
import com.stratagile.pnrouter.ui.activity.main.WebViewActivity;
import com.stratagile.pnrouter.ui.activity.selectfriend.selectFriendActivity;
import com.stratagile.pnrouter.ui.activity.user.SendAddFriendActivity;
import com.stratagile.pnrouter.utils.AlbumNotifyHelper;
import com.stratagile.pnrouter.utils.FileUtil;
import com.stratagile.pnrouter.utils.PopWindowUtil;
import com.stratagile.pnrouter.utils.SpUtil;
import com.stratagile.pnrouter.view.CustomPopWindow;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.transform.Result;

import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import scalaz.Alpha;

/**
 * download and show original image
 *
 */
public class EaseShowBigImageActivity extends EaseBaseActivity {
	private static final String TAG = "ShowBigImage";
	private ProgressDialog pd;
	private EasePhotoView image;
	private int default_res = R.drawable.image_defalut_bg;
	private String fileUrl;
	private String localFilePath;
	private Bitmap bitmap;
	private boolean isDownloaded;
	private String hasQRCode = "";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ease_activity_show_big_image);
		super.onCreate(savedInstanceState);

		image = (EasePhotoView) findViewById(R.id.image);
		ProgressBar loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
		default_res = getIntent().getIntExtra("default_image", R.drawable.ease_default_avatar);
		Uri uri = getIntent().getParcelableExtra("uri");
		fileUrl = getIntent().getExtras().getString("fileUrl");
		localFilePath = getIntent().getExtras().getString("localUrl");
		String msgId = getIntent().getExtras().getString("messageId");
		EMLog.d(TAG, "show big deleteMsgId:" + msgId );

		//show the image if it exist in local path
		if (uri != null && new File(uri.getPath()).exists()) {
			EMLog.d(TAG, "showbigimage file exists. directly show it");
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			// int screenWidth = metrics.widthPixels;
			// int screenHeight =metrics.heightPixels;
			bitmap = EaseImageCache.getInstance().get(uri.getPath());
//			if (bitmap == null) {
//				EaseLoadLocalBigImgTask task = new EaseLoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
//						ImageUtils.SCALE_IMAGE_HEIGHT);
//				if (android.os.Build.VERSION.SDK_INT > 10) {
//					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				} else {
//					task.execute();
//				}
//			} else {
//				Bitmap bigData = EaseImageUtils.getBitmap(new File(uri.getPath()));
//				int degree = EaseImageUtils.readPictureDegree(uri.getPath());
//				if(degree != 0)
//				{
//					Bitmap bmpOk = EaseImageUtils.rotateToDegrees(bigData, degree);
//					image.setImageBitmap(bmpOk);
//				}else{
//					image.setImageBitmap(bigData);
//				}
//
//			}
			Bitmap bigData = EaseImageUtils.getBitmap(new File(uri.getPath()));
			int degree = EaseImageUtils.readPictureDegree(uri.getPath());
			if(degree != 0)
			{
				Bitmap bmpOk = EaseImageUtils.rotateToDegrees(bigData, degree);
				image.setImageBitmap(bmpOk);
			}else{
				image.setImageBitmap(bigData);
			}
		} else if(msgId != null) {
			downloadImage(msgId);
		}else {
			image.setImageResource(default_res);
		}

		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				finish();
			}
		});
		Bitmap obmp = ((BitmapDrawable) (image).getDrawable()).getBitmap();
		new Thread(new Runnable() {
			public void run() {
				hasQRCode = QRCodeDecoder.syncDecodeQRCode(obmp);
			}
		}).start();
		image.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				ArrayList<String> list = new ArrayList<>();
				list.add("Save Image");
				if(hasQRCode != null && !hasQRCode.equals(""))
				{
					list.add("Scan QR Code in Image");
				}

				PopWindowUtil.INSTANCE.showSelecMenuPopWindow(EaseShowBigImageActivity.this, image,list, new PopWindowUtil.OnSelectListener()
				{
					@Override
					public void onSelect(int position, Object obj) {

						String choose = obj.toString();
						switch (choose)
						{
							case "Save Image":
								String galleryPath = Environment.getExternalStorageDirectory()
										+ File.separator + Environment.DIRECTORY_DCIM
										+ File.separator + "Confidant" + File.separator;
								File galleryPathFile = new File(galleryPath);
								if(!galleryPathFile.exists())
								{
									galleryPathFile.mkdir();
								}
								String imagePath = localFilePath;
								if(fileUrl != null)
								{
									imagePath = fileUrl;
								}
								galleryPath += System.currentTimeMillis()+imagePath.substring(imagePath.lastIndexOf("."),imagePath.length());
								int result = FileUtil.copyAppFileToSdcard(imagePath, galleryPath);
								if (result == 1) {

									AlbumNotifyHelper.insertImageToMediaStore(AppConfig.instance,galleryPath,System.currentTimeMillis());
								}
								EventBus.getDefault().post(new SaveMsgEvent("",result));
								break;
							case "Scan QR Code in Image":
								if(hasQRCode.indexOf("http://") >-1 || hasQRCode.indexOf("https://") >-1)
								{
									Intent intent = new Intent(AppConfig.instance, WebViewActivity.class);
									intent.putExtra("url", hasQRCode);
									intent.putExtra("title","Other websites");
									startActivity(intent);
								}else  if(hasQRCode.contains("type_0"))
								{
									String toAddUserId =  hasQRCode.substring(7, hasQRCode.length());
									String selfUserId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
									if (toAddUserId.contains(selfUserId)) {
										return;
									}
									if (!"".equals(toAddUserId)) {
										String toAddUserIdTemp = toAddUserId.substring(0, toAddUserId.indexOf(","));
										Intent intent = new Intent(AppConfig.instance, SendAddFriendActivity.class);
										List<UserEntity> useEntityList = AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().loadAll();
										for (UserEntity i : useEntityList) {
											if (i.getUserId().equals(toAddUserIdTemp)) {
												FriendEntity freindStatusData = new FriendEntity();
												freindStatusData.setFriendLocalStatus(7);
												List<FriendEntity> localFriendStatusList = AppConfig.instance.getMDaoMaster().newSession().getFriendEntityDao().queryBuilder().where(FriendEntityDao.Properties.UserId.eq(selfUserId), FriendEntityDao.Properties.FriendId.eq(toAddUserIdTemp)).list();
												if (localFriendStatusList.size() > 0)
													freindStatusData = localFriendStatusList.get(0);

												if (freindStatusData.getFriendLocalStatus() == 0) {
													intent.putExtra("user", i);
													startActivity(intent);
												} else {
													intent = new Intent(AppConfig.instance, SendAddFriendActivity.class);
													intent.putExtra("user", i);
													startActivity(intent);
												}

												return;
											}
										}
										intent = new Intent(AppConfig.instance, SendAddFriendActivity.class);
										UserEntity userEntity = new UserEntity();
										//userEntity.friendStatus = 7
										userEntity.setUserId(toAddUserId.substring(0, toAddUserId.indexOf(",")));
										userEntity.setNickName(toAddUserId.substring(toAddUserId.indexOf(",") + 1, toAddUserId.lastIndexOf(",")));
										userEntity.setSignPublicKey(toAddUserId.substring(toAddUserId.lastIndexOf(",") + 1, toAddUserId.length()));
										userEntity.setTimestamp(Calendar.getInstance().getTimeInMillis());

										userEntity.setRouterUserId(selfUserId);
										AppConfig.instance.getMDaoMaster().newSession().getUserEntityDao().insert(userEntity);


										String userId = SpUtil.INSTANCE.getString(AppConfig.instance, ConstantValue.INSTANCE.getUserId(), "");
										FriendEntity newFriendStatus = new FriendEntity();
										newFriendStatus.setUserId(userId);
										newFriendStatus.setFriendId(toAddUserId);
										newFriendStatus.setFriendLocalStatus(7);
										newFriendStatus.setTimestamp(Calendar.getInstance().getTimeInMillis());
										AppConfig.instance.getMDaoMaster().newSession().getFriendEntityDao().insert(newFriendStatus);
										intent.putExtra("user", userEntity);
										startActivity(intent);
									}
								}
								break;
							default:
								break;

						}
						String aa = "";
					}
				});
				return true;
			}
		});
	}


	/**
	 * download image
	 *
	 * @param
	 */
	@SuppressLint("NewApi")
	private void downloadImage(final String msgId) {
		EMLog.e(TAG, "download with messageId: " + msgId);
		String str1 = getResources().getString(R.string.Download_the_pictures);
		pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCanceledOnTouchOutside(false);
		pd.setMessage(str1);
		pd.show();
		File temp = new File(localFilePath);
		final String tempPath = temp.getParent() + "/temp_" + temp.getName();
		final EMCallBack callback = new EMCallBack() {
			public void onSuccess() {
				EMLog.e(TAG, "onSuccess" );
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						new File(tempPath).renameTo(new File(localFilePath));

						DisplayMetrics metrics = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(metrics);
						int screenWidth = metrics.widthPixels;
						int screenHeight = metrics.heightPixels;

						bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
						if (bitmap == null) {
							image.setImageResource(default_res);
						} else {
							int degree = EaseImageUtils.readPictureDegree(localFilePath);
							if(degree !=0)
							{
								Bitmap bmpOk = EaseImageUtils.rotateToDegrees(bitmap, degree);
								image.setImageBitmap(bmpOk);
								EaseImageCache.getInstance().put(localFilePath, bmpOk);
							}else{
								image.setImageBitmap(bitmap);
								EaseImageCache.getInstance().put(localFilePath, bitmap);
							}

							isDownloaded = true;
						}
						if (isFinishing() || isDestroyed()) {
							return;
						}
						if (pd != null) {
							pd.dismiss();
						}
					}
				});
			}

			public void onError(int error, String msg) {
				EMLog.e(TAG, "offline file transfer error:" + msg);
				File file = new File(tempPath);
				if (file.exists()&&file.isFile()) {
					file.delete();
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (EaseShowBigImageActivity.this.isFinishing() || EaseShowBigImageActivity.this.isDestroyed()) {
							return;
						}
						image.setImageResource(default_res);
						pd.dismiss();
					}
				});
			}

			public void onProgress(final int progress, String status) {
				EMLog.d(TAG, "Progress: " + progress);
				final String str2 = getResources().getString(R.string.Download_the_pictures_new);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (EaseShowBigImageActivity.this.isFinishing() || EaseShowBigImageActivity.this.isDestroyed()) {
							return;
						}
						pd.setMessage(str2 + progress + "%");
					}
				});
			}
		};

		EMMessage msg = EMClient.getInstance().chatManager().getMessage(msgId);
		msg.setMessageStatusCallback(callback);

		EMLog.e(TAG, "downloadAttachement");
		EMClient.getInstance().chatManager().downloadAttachment(msg);
	}

	@Override
	public void onBackPressed() {
		if (CustomPopWindow.onBackPressed()) {

		} else {
			if (isDownloaded)
				setResult(RESULT_OK);
			finish();
		}
	}
}
