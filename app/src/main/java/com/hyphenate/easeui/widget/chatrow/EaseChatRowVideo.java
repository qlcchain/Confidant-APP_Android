package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMVideoMessageBody;
import com.stratagile.pnrouter.R;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.ui.EaseShowVideoActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.DateUtils;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.ImageUtils;
import com.hyphenate.util.TextFormater;

import java.io.File;

public class EaseChatRowVideo extends EaseChatRowFile{
    private static final String TAG = "EaseChatRowVideo";

    private ImageView imageView;
    private TextView sizeView;
    private TextView timeLengthView;
    private ProgressBar progressBarShelf;

    public EaseChatRowVideo(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

	@Override
	protected void onInflateView() {
		inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
				R.layout.ease_row_received_video : R.layout.ease_row_sent_video, this);
	}

	@Override
	protected void onFindViewById() {
	    imageView = ((ImageView) findViewById(R.id.chatting_content_iv));
        sizeView = (TextView) findViewById(R.id.chatting_size_iv);
        timeLengthView = (TextView) findViewById(R.id.chatting_length_iv);
        ImageView playView = (ImageView) findViewById(R.id.chatting_status_btn);
        percentageView = (TextView) findViewById(R.id.percentage);
        progressBarShelf = (ProgressBar) findViewById(R.id.progress_bar);
        String aa = "";
	}

	@Override
	protected void onSetUpView() {
	    EMVideoMessageBody videoBody = (EMVideoMessageBody) message.getBody();
        String localThumb = videoBody.getLocalThumb();
        String localUrl = videoBody.getLocalUrl();
        if(localUrl.contains("ease_default_vedio"))
        {
            progressBarShelf.setVisibility(View.VISIBLE);
        }else{
            progressBarShelf.setVisibility(View.INVISIBLE);
        }
        if (localThumb != null) {

            showVideoThumbView(localThumb, imageView, videoBody.getThumbnailUrl(), message);
        }
        if (videoBody.getDuration() > 0) {
            String time = DateUtils.toTime(videoBody.getDuration());
            timeLengthView.setText(time);
        }

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (videoBody.getVideoFileLength() > 0) {
                String size = TextFormater.getDataSize(videoBody.getVideoFileLength());
                sizeView.setText(size);
            }
        } else {
            if (videoBody.getLocalUrl() != null && new File(videoBody.getLocalUrl()).exists()) {
                String size = TextFormater.getDataSize(new File(videoBody.getLocalUrl()).length());
                sizeView.setText(size);
            }
        }

        EMLog.d(TAG,  "video thumbnailStatus:" + videoBody.thumbnailDownloadStatus());
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                imageView.setImageResource(R.drawable.image_defalut_bg);
            } else {
                // System.err.println("!!!! not back receive, show image directly");
                imageView.setImageResource(R.drawable.image_defalut_bg);
                if (localThumb != null) {
                    showVideoThumbView(localThumb, imageView, videoBody.getThumbnailUrl(), message);
                }
            }
            return;
        }else{
            if (videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING ||
                        videoBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
               /* progressBar.setVisibility(View.INVISIBLE);
                percentageView.setVisibility(View.INVISIBLE);*/
                imageView.setImageResource(R.drawable.image_defalut_bg);
            } else {
               /* progressBar.setVisibility(View.GONE);
                percentageView.setVisibility(View.GONE);*/
                imageView.setImageResource(R.drawable.image_defalut_bg);
                showVideoThumbView(localThumb, imageView, videoBody.getThumbnailUrl(), message);
            }
        }
	}



	/**
     * show video thumbnails
     * 
     * @param localThumb
     *            local path for thumbnail
     * @param iv
     * @param thumbnailUrl
     *            Url on server for thumbnails
     * @param message
     */
    private void showVideoThumbView(final String localThumb, final ImageView iv, String thumbnailUrl, final EMMessage message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = EaseImageCache.getInstance().get(localThumb);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.image_defalut_bg);

            try {
                new AsyncTask<Void, Void, Bitmap>() {

                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        if (new File(localThumb).exists()) {
                            return ImageUtils.decodeScaleImage(localThumb, activity.getWindowManager().getDefaultDisplay().getWidth() / 3, activity.getWindowManager().getDefaultDisplay().getWidth() / 3);
                        } else {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(Bitmap result) {
                        super.onPostExecute(result);
                        if (result != null) {
                            EaseImageCache.getInstance().put(localThumb, result);
                            Bitmap bitmap2 = getRoundCornerImage(result, result.getWidth(), result.getHeight());
                            iv.setImageBitmap(bitmap2);

                        } else {
                            if (message.status() == EMMessage.Status.FAIL) {
                                if (EaseCommonUtils.isNetWorkConnected(activity)) {
                                    EMClient.getInstance().chatManager().downloadThumbnail(message);
                                }
                            }

                        }
                    }
                }.execute();
            }catch (Exception e)
            {

            }

        }
        
    }

    public Bitmap getRoundCornerImage(Bitmap bitmap_in, int widht, int height)
    {
        Bitmap bitmap_bg;
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            bitmap_bg = BitmapFactory.decodeResource(getResources(), R.mipmap.chat_box_left);
        } else {
            bitmap_bg = BitmapFactory.decodeResource(getResources(), R.mipmap.chat_box_right);
        }
        Bitmap roundConcerImage = Bitmap.createBitmap(widht,height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);
        Paint paint = new Paint();
        Rect rect = new Rect(0,0,widht,height);
        Rect rectF = new Rect(0, 0, bitmap_in.getWidth(), bitmap_in.getHeight());
        paint.setAntiAlias(true);
        NinePatch patch = new NinePatch(bitmap_bg, bitmap_bg.getNinePatchChunk(), null);
        patch.draw(canvas, rect);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap_in, rectF, rect, paint);
        return roundConcerImage;
    }

}
