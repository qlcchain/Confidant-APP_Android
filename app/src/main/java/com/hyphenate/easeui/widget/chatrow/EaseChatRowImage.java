package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.socks.library.KLog;
import com.stratagile.pnrouter.R;
import com.hyphenate.easeui.model.EaseImageCache;
import com.hyphenate.easeui.utils.EaseImageUtils;

import java.io.File;

public class EaseChatRowImage extends EaseChatRowFile {

    protected ImageView imageView;
    private EMImageMessageBody imgBody;
    private ProgressBar progressBarShelf;
    private float width = 0;
    private float height = 0;

    int showWidth = 0;
    int showHeight = 0;

    public EaseChatRowImage(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_picture : R.layout.ease_row_sent_picture, this);
    }

    @Override
    protected void onFindViewById() {
        percentageView = (TextView) findViewById(R.id.percentage);
        imageView = (ImageView) findViewById(R.id.image);
        progressBarShelf = (ProgressBar) findViewById(R.id.progress_bar);
    }


    @Override
    protected void onSetUpView() {
        imgBody = (EMImageMessageBody) message.getBody();
        try {
            String wh = message.getStringAttribute("wh");
            if (wh != null && !"".equals(wh)) {
                wh = wh.replace(",","");
                String[] whs = wh.split("\\*");
                width = Float.valueOf(whs[0]);
                height = Float.valueOf(whs[1]);
                setImageViewSize();
                KLog.i("位置为：" + position);
                KLog.i("原始宽高为：" + wh);
                KLog.i("图片的宽为1：" + width);
                KLog.i("图片的高为1：" + height);
            }
        } catch (Exception e) {
            KLog.i("位置为：" + position);
            KLog.i("文件名字为：" + imgBody.getFileName());
            e.printStackTrace();
        }
        String localUrl = imgBody.getLocalUrl();
        if (localUrl.contains("ease_default_amr")) {
            progressBarShelf.setVisibility(View.VISIBLE);
        } else {
            progressBarShelf.setVisibility(View.INVISIBLE);
        }
        // received messages
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            return;
        }
        String filePath = imgBody.getLocalUrl();
        String thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
        showImageView(thumbPath, filePath, message);
    }

    private void setImageViewSize() {
        int windowWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        if (width >= height) {
            //以高为标准
            if (width / height >= 1.5) {
                showHeight = windowWidth / 4;
            } else {
                showHeight = windowWidth / 3;
            }
            showWidth = (int) ((width / height) * showHeight);
        } else {
            //以宽为标准
            if (height / width >= 1.5) {
                showWidth = windowWidth / 4;
            } else {
                showWidth = windowWidth / 3;
            }
            if (height / width >= 3) {
                showHeight = showWidth * 3;
            } else {
                showHeight = (int) ((height / width) * showWidth);
            }
        }
        KLog.i("设置图片的宽为：" + showWidth);
        KLog.i("设置图片的高为：" + showHeight);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(showWidth, showHeight);
        imageView.setLayoutParams(lp);
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        if (msg.direct() == EMMessage.Direct.SEND) {
            if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                super.onViewUpdate(msg);
            } else {
                if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                        imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING ||
                        imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
                    imageView.setImageResource(R.drawable.image_defalut_bg);
                } else {
                    String thumbPath = imgBody.thumbnailLocalPath();
                    if (!new File(thumbPath).exists()) {
                        // to make it compatible with thumbnail received in previous version
                        thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                    }
                    showImageView(thumbPath, imgBody.getLocalUrl(), message);
                }
            }
            return;
        }

        // received messages
        if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
            if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
                imageView.setImageResource(R.drawable.image_defalut_bg);
            } else {
               /* progressBar.setVisibility(View.INVISIBLE);
                percentageView.setVisibility(View.INVISIBLE);*/
                imageView.setImageResource(R.drawable.image_defalut_bg);
            }
        } else if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED) {
            if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
               /* progressBar.setVisibility(View.VISIBLE);
                percentageView.setVisibility(View.VISIBLE);*/
            } else {
                /*progressBar.setVisibility(View.INVISIBLE);
                percentageView.setVisibility(View.INVISIBLE);*/
            }
        } else {
           /* progressBar.setVisibility(View.GONE);
            percentageView.setVisibility(View.GONE);*/
            imageView.setImageResource(R.drawable.image_defalut_bg);
            String thumbPath = imgBody.thumbnailLocalPath();
            if (!new File(thumbPath).exists()) {
                // to make it compatible with thumbnail received in previous version
                thumbPath = EaseImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
            }
            showImageView(thumbPath, imgBody.getLocalUrl(), message);
        }
    }

    public Bitmap getRoundCornerImage(Bitmap bitmap_in, int widht, int height) {
        Bitmap bitmap_bg;
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            bitmap_bg = BitmapFactory.decodeResource(getResources(), R.mipmap.chat_box_left);
        } else {
            bitmap_bg = BitmapFactory.decodeResource(getResources(), R.mipmap.chat_box_right);
        }
        Bitmap roundConcerImage = Bitmap.createBitmap(widht, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, widht, height);
        Rect rectF = new Rect(0, 0, bitmap_in.getWidth(), bitmap_in.getHeight());
        paint.setAntiAlias(true);
        NinePatch patch = new NinePatch(bitmap_bg, bitmap_bg.getNinePatchChunk(), null);
        patch.draw(canvas, rect);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap_in, rectF, rect, paint);
        return roundConcerImage;
    }

    /**
     * load image into image view
     */
    private void showImageView(final String thumbernailPath, final String localFullSizePath, final EMMessage message) {
        // first check if the thumbnail image already loaded into cache s
        Bitmap bitmap = EaseImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            KLog.i("图片来源缓存");
            Bitmap bitmap2;
            if (bitmap.getHeight() / bitmap.getWidth() >= 3) {
                bitmap2 = getRoundCornerImage(bitmap, bitmap.getWidth(), bitmap.getWidth() * 3);
            } else {
                bitmap2 = getRoundCornerImage(bitmap, bitmap.getWidth(), bitmap.getHeight());
            }
            // thumbnail image is already loaded, reuse the drawable
            imageView.setImageBitmap(bitmap2);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            setImageViewSize();
        } else {
            try {
                new AsyncTask<Object, Void, Bitmap>() {

                    @Override
                    protected Bitmap doInBackground(Object... args) {
                        File file = new File(thumbernailPath);
                        if (file.exists()) {
                            KLog.i("图片来源1");
                            Bitmap bitmap2 = BitmapFactory.decodeFile(thumbernailPath);
                            int width;
                            int height;
                            if (bitmap2 != null && bitmap2.getWidth() != 0 && bitmap2.getHeight() != 0) {
                                if (bitmap2.getWidth() <= bitmap2.getHeight()) {
                                    //以宽为标准
                                    if (activity.getWindowManager().getDefaultDisplay().getWidth() / 3 >= bitmap2.getWidth()) {
                                        width = bitmap2.getWidth();
                                        height = bitmap2.getHeight();
                                    } else {
                                        width = activity.getWindowManager().getDefaultDisplay().getWidth() / 3;
                                        height = bitmap2.getHeight() / (bitmap2.getWidth() / width);
                                    }
                                } else {
                                    //以高为标准
                                    if (activity.getWindowManager().getDefaultDisplay().getWidth() / 4 >= bitmap2.getHeight()) {
                                        height = bitmap2.getHeight();
                                        width = bitmap2.getWidth();
                                    } else {
                                        height = activity.getWindowManager().getDefaultDisplay().getWidth() / 4;
                                        width = bitmap2.getWidth() / (bitmap2.getHeight() / height);
                                    }
                                }
                            } else {
                                width = activity.getWindowManager().getDefaultDisplay().getWidth() / 3;
                                height = activity.getWindowManager().getDefaultDisplay().getHeight() / 3;
                            }
                            imageView.setImageResource(R.drawable.image_defalut_bg);
                            if (showHeight != 0) {
                                return EaseImageUtils.decodeScaleImage(thumbernailPath, showWidth, showHeight);
                            } else {
                                return EaseImageUtils.decodeScaleImage(thumbernailPath, width, height);
                            }
                        } else if (new File(imgBody.thumbnailLocalPath()).exists()) {
                            Bitmap orgBit = BitmapFactory.decodeFile(localFullSizePath);
                            int width;
                            int height;
                            if (orgBit != null && orgBit.getWidth() != 0 && orgBit.getHeight() != 0) {
                                KLog.i("图片来源2");
                                if (orgBit.getWidth() <= orgBit.getHeight()) {
                                    //以宽为标准
                                    if (activity.getWindowManager().getDefaultDisplay().getWidth() / 3 >= orgBit.getWidth()) {
                                        width = orgBit.getWidth();
                                        height = orgBit.getHeight();
                                    } else {
                                        width = activity.getWindowManager().getDefaultDisplay().getWidth() / 3;
                                        height = orgBit.getHeight() / (orgBit.getWidth() / width);
                                    }
                                } else {
                                    //以高为标准
                                    if (activity.getWindowManager().getDefaultDisplay().getWidth() / 4 >= orgBit.getHeight()) {
                                        height = orgBit.getHeight();
                                        width = orgBit.getWidth();
                                    } else {
                                        height = activity.getWindowManager().getDefaultDisplay().getWidth() / 4;
                                        width = orgBit.getWidth() / (orgBit.getHeight() / height);
                                    }
                                }
                            } else {
                                width = activity.getWindowManager().getDefaultDisplay().getWidth() / 3;
                                height = activity.getWindowManager().getDefaultDisplay().getHeight() / 3;
                            }
                            if (orgBit != null) {
                                if (showHeight != 0) {
                                    return EaseImageUtils.decodeScaleImage(imgBody.thumbnailLocalPath(), showWidth, showHeight);
                                } else {
                                    return EaseImageUtils.decodeScaleImage(imgBody.thumbnailLocalPath(), width, height);
                                }
                            } else {
                                imageView.setImageResource(R.drawable.image_defalut_bg);
                                return null;
                            }
                        } else {
                            if (message.direct() == EMMessage.Direct.SEND) {
                                KLog.i("图片来源3");
                                if (localFullSizePath != null && new File(localFullSizePath).exists()) {
                                    Bitmap orgBit = BitmapFactory.decodeFile(localFullSizePath);
                                    int width;
                                    int height;
                                    if (orgBit != null && orgBit.getWidth() != 0 && orgBit.getHeight() != 0) {
                                        if (orgBit.getWidth() <= orgBit.getHeight()) {
                                            //以宽为标准
                                            if (activity.getWindowManager().getDefaultDisplay().getWidth() / 4 >= orgBit.getWidth()) {
                                                width = orgBit.getWidth();
                                                height = orgBit.getHeight();
                                            } else {
                                                width = activity.getWindowManager().getDefaultDisplay().getWidth() / 4;
                                                height = orgBit.getHeight() / (orgBit.getWidth() / width);
                                            }
                                        } else {
                                            //以高为标准
                                            if (activity.getWindowManager().getDefaultDisplay().getWidth() / 3 >= orgBit.getHeight()) {
                                                height = orgBit.getHeight();
                                                width = orgBit.getWidth();
                                            } else {
                                                height = activity.getWindowManager().getDefaultDisplay().getWidth() / 3;
                                                width = orgBit.getWidth() / (orgBit.getHeight() / height);
                                            }
                                        }
                                    } else {
                                        width = activity.getWindowManager().getDefaultDisplay().getWidth() / 3;
                                        height = activity.getWindowManager().getDefaultDisplay().getHeight() / 3;
                                    }
                                    if (orgBit != null) {
                                        if (showHeight != 0) {
                                            return EaseImageUtils.decodeScaleImage(localFullSizePath, showWidth, showHeight);
                                        } else {
                                            return EaseImageUtils.decodeScaleImage(localFullSizePath, width, height);
                                        }
                                    } else {
                                        imageView.setImageResource(R.drawable.image_defalut_bg);
                                        return null;
                                    }
                                } else {
                                    imageView.setImageResource(R.drawable.image_defalut_bg);
                                    return null;
                                }
                            } else {
                                imageView.setImageResource(R.drawable.image_defalut_bg);
                                return null;
                            }
                        }
                    }

                    @Override
                    protected void onPostExecute(Bitmap image) {
                        if (image != null) {
                            Bitmap bitmap2;
                            if (image.getHeight() / image.getWidth() >= 3) {
                                bitmap2 = getRoundCornerImage(image, image.getWidth(), image.getWidth() * 3);
                            } else {
                                bitmap2 = getRoundCornerImage(image, image.getWidth(), image.getHeight());
                            }
                            imageView.setImageBitmap(bitmap2);
                            EaseImageCache.getInstance().put(thumbernailPath, bitmap2);
                        }
                    }
                }.execute();
            } catch (Exception e) {

            }


        }
    }

}
