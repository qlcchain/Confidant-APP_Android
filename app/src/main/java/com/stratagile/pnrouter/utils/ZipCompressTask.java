package com.stratagile.pnrouter.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.luck.picture.lib.tools.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipCompressTask extends AsyncTask<Void, Integer, Long> {
	private final int PER = 1024;
	private final String TAG = "ZipCompressTask";
	private String zipPath;
	private final List<String> filePaths;
	private final String zipFilePath;
	private final ProgressDialog mDialog;
	private int mProgress = 0;
	private final Context mContext;
	private boolean mReplaceAll;
	private Handler handler;
	private Long unZipSize;
	public ZipCompressTask(List<String> filePath, String toZipFilePath, Context context, boolean replaceAll, Handler message){
		super();
		filePaths = filePath;
		zipFilePath = toZipFilePath;
		if(context!=null){
			mDialog = new ProgressDialog(context);
		}
		else{
			mDialog = null;
		}
		mContext = context;
		mReplaceAll = replaceAll;
		handler = message;
	}
	@Override
	protected Long doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return compress(filePaths,zipFilePath,false);
	}

	@Override
	protected void onPostExecute(Long result) {
		// TODO Auto-generated method stub
		//super.onPostExecute(result);
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
		}
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Message msg = new Message();
				if(isCancelled())
				{
					msg.what = 0x404;
					handler.sendMessage(msg);
					return;
				}
				if(unZipSize != 0L)
					msg.what = 0x56;
				else
					msg.what = 0x404;
				handler.sendMessage(msg);

			}
		}, 500);

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		//super.onPreExecute();
		try {
			if(mDialog!=null){
				mDialog.setTitle("打包中……");
				mDialog.setMessage("");
				//mDialog.setMessage(filePaths.getName());
				mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mDialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						cancel(true);
					}
				});
				mDialog.setCancelable(false);
				mDialog.show();
			}
		}catch (Exception e)
		{

		}

	}
	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		//super.onProgressUpdate(values);
		if(mDialog==null)
			return;
		if(values.length>1){
			int max=values[1];
			mDialog.setMax(Integer.parseInt((max / PER) +""));
		}
		else
			mDialog.setProgress(Integer.parseInt((values[0].intValue() / PER) +""));
	}
	/**
	 * @Title: compress
	 * @Description: TODO
	 * @param filePaths 需要压缩的文件地址列表（绝对路径）
	 * @param zipFilePath 需要压缩到哪个zip文件（无需创建这样一个zip，只需要指定一个全路径）
	 * @param keepDirStructure 压缩后目录是否保持原目录结构
	 * @throws IOException
	 * @return int   压缩成功的文件个数
	 */
	public long compress(List<String> filePaths, String zipFilePath, Boolean keepDirStructure){
		long fileCount = 0;//记录压缩了几个文件？
		byte[] buf = new byte[1024];
		File zipFile = new File(zipFilePath);
		//zip文件不存在，则创建文件，用于压缩
		try {
			if(!zipFile.exists())
				zipFile.createNewFile();
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
			for(int i = 0; i < filePaths.size(); i++){
				String relativePath = filePaths.get(i);
				if(TextUtils.isEmpty(relativePath)){
					continue;
				}
				File sourceFile = new File(relativePath);//绝对路径找到file
				if(sourceFile == null || !sourceFile.exists()){
					continue;
				}

				FileInputStream fis = new FileInputStream(sourceFile);
				if(keepDirStructure!=null && keepDirStructure){
					//保持目录结构
					zos.putNextEntry(new ZipEntry(relativePath));
				}else{
					//直接放到压缩包的根目录
					zos.putNextEntry(new ZipEntry(sourceFile.getName()));
				}
				//System.out.println("压缩当前文件："+sourceFile.getName());
				int len;
				while((len = fis.read(buf)) > 0){
					zos.write(buf, 0, len);
				}
				zos.closeEntry();
				fis.close();
				fileCount++;
			}
			zos.close();
			//System.out.println("压缩完成");
		} catch (Exception e) {
			e.printStackTrace();
		}
		unZipSize = fileCount;
		return fileCount;
	}
	private long getOriginalSize(ZipFile file){
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
		long originalSize = 0l;
		while(entries.hasMoreElements()){
			ZipEntry entry = entries.nextElement();
			if(entry.getSize()>=0){
				originalSize+=entry.getSize();
			}
		}
		return originalSize;
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
}