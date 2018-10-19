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
package com.hyphenate.easeui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;

import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.stratagile.pnrouter.utils.RxFileTool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EaseImageUtils extends com.hyphenate.util.ImageUtils{
	
	public static String getImagePath(String remoteUrl)
	{
		String imageName= remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1, remoteUrl.length());
		String path =PathUtils.getInstance().getImagePath()+"/"+ imageName;
        EMLog.d("msg", "image path:" + path);
        return path;
		
	}
	
	public static String getThumbnailImagePath(String thumbRemoteUrl) {
		String thumbImageName= thumbRemoteUrl.substring(thumbRemoteUrl.lastIndexOf("/") + 1, thumbRemoteUrl.length());
		String path =PathUtils.getInstance().getImagePath()+"/"+ "th"+thumbImageName;
        EMLog.d("msg", "thum image path:" + path);
        return path;
    }
	/**
	 * 获取bitmap
	 *
	 * @param file 文件
	 * @return bitmap
	 */
	public static Bitmap getBitmap(File file) {
		if (file == null) return null;
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			return BitmapFactory.decodeStream(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			RxFileTool.closeIO(is);
		}
	}
	//根据路径得到视频缩略图
	private Bitmap getVideoPhoto(String videoPath) {
		MediaMetadataRetriever media = new MediaMetadataRetriever();
		Bitmap bitmap = null;
		try {
			media.setDataSource(videoPath);
			bitmap = media.getFrameAtTime();
		}catch (Exception e)
		{

		}

		return bitmap;
	}
	/**
	 * 图片旋转
	 * @param tmpBitmap
	 * @param degrees
	 * @return
	 */
	public static Bitmap rotateToDegrees(Bitmap tmpBitmap, float degrees) {
		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.setRotate(degrees);
		return tmpBitmap =
				Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix,
						true);
	}

	/**
	 * 读取照片exif信息中的旋转角度
	 * @param path 照片路径
	 * @return角度          
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation =
					exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	//获取视频总时长
	private int getVideoDuration(String path){
		MediaMetadataRetriever media = new MediaMetadataRetriever();
		media.setDataSource(path);
		String duration = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); //
		return Integer.parseInt(duration);
	}

}
