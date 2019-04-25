package com.luck.picture.lib;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.compress.OnCompressListener;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.entity.EventEntity;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.immersive.ImmersiveManage;
import com.luck.picture.lib.rxbus2.RxBus;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
//import com.yalantis.ucrop.UCrop;
//import com.yalantis.ucrop.UCropMulti;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: Activity基类
 */
public class PictureBaseActivity extends FragmentActivity {
    protected Context mContext;
    protected PictureSelectionConfig pictureSelectionConfig;
    protected boolean openWhiteStatusBar, numComplete;
    protected int colorPrimary, colorPrimaryDark;
    protected String cameraPath, outputCameraPath;
    protected String originalPath;
    protected PictureDialog dialog;
    protected PictureDialog compressDialog;
    protected List<LocalMedia> selectionMedias;

    /**
     * 是否使用沉浸式，子类复写该方法来确定是否采用沉浸式
     *
     * @return 是否沉浸式，默认true
     */
    @Override
    public boolean isImmersive() {
        return true;
    }

    /**
     * 具体沉浸的样式，可以根据需要自行修改状态栏和导航栏的颜色
     */
    public void immersive() {
        ImmersiveManage.immersiveAboveAPI23(this
                , colorPrimaryDark
                , colorPrimary
                , openWhiteStatusBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            pictureSelectionConfig = savedInstanceState.getParcelable(PictureConfig.EXTRA_CONFIG);
            cameraPath = savedInstanceState.getString(PictureConfig.BUNDLE_CAMERA_PATH);
            originalPath = savedInstanceState.getString(PictureConfig.BUNDLE_ORIGINAL_PATH);
        } else {
            pictureSelectionConfig = PictureSelectionConfig.getInstance();
        }
        int themeStyleId = pictureSelectionConfig.themeStyleId;
        setTheme(themeStyleId);
        super.onCreate(savedInstanceState);
        mContext = this;
        initConfig();
        if (isImmersive()) {
            immersive();
        }
    }

    /**
     * 获取配置参数
     */
    private void initConfig() {
        outputCameraPath = pictureSelectionConfig.outputCameraPath;
        // 是否开启白色状态栏
        openWhiteStatusBar = AttrsUtils.getTypeValueBoolean
                (this, R.attr.picture_statusFontColor);
        // 是否是0/9样式
        numComplete = AttrsUtils.getTypeValueBoolean(this,
                R.attr.picture_style_numComplete);
        // 是否开启数字勾选模式
        pictureSelectionConfig.checkNumMode = AttrsUtils.getTypeValueBoolean
                (this, R.attr.picture_style_checkNumMode);
        // 标题栏背景色
        colorPrimary = AttrsUtils.getTypeValueColor(this, R.attr.colorPrimary);
        // 状态栏背景色
        colorPrimaryDark = AttrsUtils.getTypeValueColor(this, R.attr.colorPrimaryDark);
        // 已选图片列表
        selectionMedias = pictureSelectionConfig.selectionMedias;
        if (selectionMedias == null) {
            selectionMedias = new ArrayList<>();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PictureConfig.BUNDLE_CAMERA_PATH, cameraPath);
        outState.putString(PictureConfig.BUNDLE_ORIGINAL_PATH, originalPath);
        outState.putParcelable(PictureConfig.EXTRA_CONFIG, pictureSelectionConfig);
    }

    protected void startActivity(Class clz, Bundle bundle) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Intent intent = new Intent();
            intent.setClass(this, clz);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    protected void startActivity(Class clz, Bundle bundle, int requestCode) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Intent intent = new Intent();
            intent.setClass(this, clz);
            intent.putExtras(bundle);
            startActivityForResult(intent, requestCode);
        }
    }

    /**
     * loading dialog
     */
    protected void showPleaseDialog() {
        if (!isFinishing()) {
            dismissDialog();
            dialog = new PictureDialog(this);
            dialog.show();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * compress loading dialog
     */
    protected void showCompressDialog() {
        if (!isFinishing()) {
            dismissCompressDialog();
            compressDialog = new PictureDialog(this);
            compressDialog.show();
        }
    }

    /**
     * dismiss compress dialog
     */
    protected void dismissCompressDialog() {
        try {
            if (!isFinishing()
                    && compressDialog != null
                    && compressDialog.isShowing()) {
                compressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * compressImage
     */
    protected void compressImage(final List<LocalMedia> result) {
        showCompressDialog();
        if (pictureSelectionConfig.synOrAsy) {
            Flowable.just(result)
                    .observeOn(Schedulers.io())
                    .map(new Function<List<LocalMedia>, List<File>>() {
                        @Override
                        public List<File> apply(@NonNull List<LocalMedia> list) throws Exception {
                            List<File> files = Luban.with(mContext)
                                    .setTargetDir(pictureSelectionConfig.compressSavePath)
                                    .ignoreBy(pictureSelectionConfig.minimumCompressSize)
                                    .loadLocalMedia(list).get();
                            if (files == null) {
                                files = new ArrayList<>();
                            }
                            return files;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<File>>() {
                        @Override
                        public void accept(@NonNull List<File> files) throws Exception {
                            handleCompressCallBack(result, files);
                        }
                    });
        } else {
            Luban.with(this)
                    .loadLocalMedia(result)
                    .ignoreBy(pictureSelectionConfig.minimumCompressSize)
                    .setTargetDir(pictureSelectionConfig.compressSavePath)
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(List<LocalMedia> list) {
                            RxBus.getDefault().post(new EventEntity(PictureConfig.CLOSE_PREVIEW_FLAG));
                            onResult(list);
                        }

                        @Override
                        public void onError(Throwable e) {
                            RxBus.getDefault().post(new EventEntity(PictureConfig.CLOSE_PREVIEW_FLAG));
                            onResult(result);
                        }
                    }).launch();
        }
    }

    /**
     * 重新构造已压缩的图片返回集合
     *
     * @param images
     * @param files
     */
    private void handleCompressCallBack(List<LocalMedia> images, List<File> files) {
        if (files.size() == images.size()) {
            for (int i = 0, j = images.size(); i < j; i++) {
                // 压缩成功后的地址
                String path = files.get(i).getPath();
                LocalMedia image = images.get(i);
                // 如果是网络图片则不压缩
                boolean http = PictureMimeType.isHttp(path);
                boolean eqTrue = !TextUtils.isEmpty(path) && http;
                image.setCompressed(eqTrue ? false : true);
                image.setCompressPath(eqTrue ? "" : path);
            }
        }
        RxBus.getDefault().post(new EventEntity(PictureConfig.CLOSE_PREVIEW_FLAG));
        onResult(images);
    }

    /**
     * 去裁剪
     *
     * @param originalPath
     */
    protected void startCrop(String originalPath) {
//        UCrop.Options options = new UCrop.Options();
//        int toolbarColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_toolbar_bg);
//        int statusColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_status_color);
//        int titleColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_title_color);
//        options.setToolbarColor(toolbarColor);
//        options.setStatusBarColor(statusColor);
//        options.setToolbarWidgetColor(titleColor);
//        options.setCircleDimmedLayer(pictureSelectionConfig.circleDimmedLayer);
//        options.setShowCropFrame(pictureSelectionConfig.showCropFrame);
//        options.setShowCropGrid(pictureSelectionConfig.showCropGrid);
//        options.setDragFrameEnabled(pictureSelectionConfig.isDragFrame);
//        options.setScaleEnabled(pictureSelectionConfig.scaleEnabled);
//        options.setRotateEnabled(pictureSelectionConfig.rotateEnabled);
//        options.setCompressionQuality(pictureSelectionConfig.cropCompressQuality);
//        options.setHideBottomControls(pictureSelectionConfig.hideBottomControls);
//        options.setFreeStyleCropEnabled(pictureSelectionConfig.freeStyleCropEnabled);
//        boolean isHttp = PictureMimeType.isHttp(originalPath);
//        String imgType = PictureMimeType.getLastImgType(originalPath);
//        Uri uri = isHttp ? Uri.parse(originalPath) : Uri.fromFile(new File(originalPath));
//        UCrop.of(uri, Uri.fromFile(new File(PictureFileUtils.getDiskCacheDir(this),
//                System.currentTimeMillis() + imgType)))
//                .withAspectRatio(pictureSelectionConfig.aspect_ratio_x, pictureSelectionConfig.aspect_ratio_y)
//                .withMaxResultSize(pictureSelectionConfig.cropWidth, pictureSelectionConfig.cropHeight)
//                .withOptions(options)
//                .start(this);
    }

    /**
     * 多图去裁剪
     *
     * @param list
     */
    protected void startCrop(ArrayList<String> list) {
//        UCropMulti.Options options = new UCropMulti.Options();
//        int toolbarColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_toolbar_bg);
//        int statusColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_status_color);
//        int titleColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_title_color);
//        options.setToolbarColor(toolbarColor);
//        options.setStatusBarColor(statusColor);
//        options.setToolbarWidgetColor(titleColor);
//        options.setCircleDimmedLayer(pictureSelectionConfig.circleDimmedLayer);
//        options.setShowCropFrame(pictureSelectionConfig.showCropFrame);
//        options.setDragFrameEnabled(pictureSelectionConfig.isDragFrame);
//        options.setShowCropGrid(pictureSelectionConfig.showCropGrid);
//        options.setScaleEnabled(pictureSelectionConfig.scaleEnabled);
//        options.setRotateEnabled(pictureSelectionConfig.rotateEnabled);
//        options.setHideBottomControls(true);
//        options.setCompressionQuality(pictureSelectionConfig.cropCompressQuality);
//        options.setCutListData(list);
//        options.setFreeStyleCropEnabled(pictureSelectionConfig.freeStyleCropEnabled);
//        String path = list.size() > 0 ? list.get(0) : "";
//        boolean isHttp = PictureMimeType.isHttp(path);
//        String imgType = PictureMimeType.getLastImgType(path);
//        Uri uri = isHttp ? Uri.parse(path) : Uri.fromFile(new File(path));
//        UCropMulti.of(uri, Uri.fromFile(new File(PictureFileUtils.getDiskCacheDir(this),
//                System.currentTimeMillis() + imgType)))
//                .withAspectRatio(pictureSelectionConfig.aspect_ratio_x, pictureSelectionConfig.aspect_ratio_y)
//                .withMaxResultSize(pictureSelectionConfig.cropWidth, pictureSelectionConfig.cropHeight)
//                .withOptions(options)
//                .start(this);
    }


    /**
     * 判断拍照 图片是否旋转
     *
     * @param degree
     * @param file
     */
    protected void rotateImage(int degree, File file) {
        if (degree > 0) {
            // 针对相片有旋转问题的处理方式
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();//获取缩略图显示到屏幕上
                opts.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                Bitmap bmp = PictureFileUtils.rotaingImageView(degree, bitmap);
                PictureFileUtils.saveBitmapFile(bmp, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * compress or callback
     *
     * @param result
     */
    protected void handlerResult(List<LocalMedia> result) {
        if (pictureSelectionConfig.isCompress) {
            compressImage(result);
        } else {
            onResult(result);
        }
    }


    /**
     * 如果没有任何相册，先创建一个最近相册出来
     *
     * @param folders
     */
    protected void createNewFolder(List<LocalMediaFolder> folders) {
        if (folders.size() == 0) {
            // 没有相册 先创建一个最近相册出来
            LocalMediaFolder newFolder = new LocalMediaFolder();
            String folderName = pictureSelectionConfig.mimeType == PictureMimeType.ofAudio() ?
                    getString(R.string.picture_all_audio) : getString(R.string.picture_camera_roll);
            newFolder.setName(folderName);
            newFolder.setPath("");
            newFolder.setFirstImagePath("");
            folders.add(newFolder);
        }
    }

    /**
     * 将图片插入到相机文件夹中
     *
     * @param path
     * @param imageFolders
     * @return
     */
    protected LocalMediaFolder getImageFolder(String path, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        for (LocalMediaFolder folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        newFolder.setFirstImagePath(path);
        imageFolders.add(newFolder);
        return newFolder;
    }

    /**
     * return image result
     *
     * @param images
     */
    protected void onResult(List<LocalMedia> images) {
        dismissCompressDialog();
        if (pictureSelectionConfig.camera
                && pictureSelectionConfig.selectionMode == PictureConfig.MULTIPLE
                && selectionMedias != null) {
            images.addAll(images.size() > 0 ? images.size() - 1 : 0, selectionMedias);
        }
        Intent intent = PictureSelector.putIntentResult(images);
        setResult(RESULT_OK, intent);
        closeActivity();
    }

    /**
     * Close Activity
     */
    protected void closeActivity() {
        finish();
        if (pictureSelectionConfig.camera) {
            overridePendingTransition(0, R.anim.pic_fade_out);
        } else {
            overridePendingTransition(0, R.anim.activity_translate_out_1);
        }
    }

    @Override
    protected void onDestroy() {
        dismissCompressDialog();
        dismissDialog();
        super.onDestroy();
    }


    /**
     * 获取DCIM文件下最新一条拍照记录
     *
     * @return
     */
    protected int getLastImageId(boolean eqVideo) {
        try {
            //selection: 指定查询条件
            String absolutePath = PictureFileUtils.getDCIMCameraPath();
            String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
            String selection = eqVideo ? MediaStore.Video.Media.DATA + " like ?" :
                    MediaStore.Images.Media.DATA + " like ?";
            //定义selectionArgs：
            String[] selectionArgs = {absolutePath + "%"};
            Cursor imageCursor = this.getContentResolver().query(eqVideo ?
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            : MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                    selection, selectionArgs, ORDER_BY);
            if (imageCursor.moveToFirst()) {
                int id = imageCursor.getInt(eqVideo ?
                        imageCursor.getColumnIndex(MediaStore.Video.Media._ID)
                        : imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
                long date = imageCursor.getLong(eqVideo ?
                        imageCursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                        : imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                int duration = DateUtils.dateDiffer(date);
                imageCursor.close();
                // DCIM文件下最近时间30s以内的图片，可以判定是最新生成的重复照片
                return duration <= 30 ? id : -1;
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 删除部分手机 拍照在DCIM也生成一张的问题
     *
     * @param id
     * @param eqVideo
     */
    protected void removeImage(int id, boolean eqVideo) {
        try {
            ContentResolver cr = getContentResolver();
            Uri uri = eqVideo ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String selection = eqVideo ? MediaStore.Video.Media._ID + "=?"
                    : MediaStore.Images.Media._ID + "=?";
            cr.delete(uri,
                    selection,
                    new String[]{Long.toString(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 录音
     *
     * @param data
     */
    protected String getAudioPath(Intent data) {
        boolean compare_SDK_19 = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
        if (data != null && pictureSelectionConfig.mimeType == PictureMimeType.ofAudio()) {
            try {
                Uri uri = data.getData();
                final String audioPath;
                if (compare_SDK_19) {
                    audioPath = uri.getPath();
                } else {
                    audioPath = getAudioFilePathFromUri(uri);
                }
                return audioPath;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 获取刚录取的音频文件
     *
     * @param uri
     * @return
     */
    protected String getAudioFilePathFromUri(Uri uri) {
        String path = "";
        try {
            Cursor cursor = getContentResolver()
                    .query(uri, null, null, null, null);
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
            path = cursor.getString(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
}
