package com.stratagile.pnrouter.ui.activity.test

import android.os.Bundle
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.test.component.DaggerTestComponent
import com.stratagile.pnrouter.ui.activity.test.contract.TestContract
import com.stratagile.pnrouter.ui.activity.test.module.TestModule
import com.stratagile.pnrouter.ui.activity.test.presenter.TestPresenter
import com.stratagile.pnrouter.ui.adapter.user.ContactAdapter
import com.stratagile.pnrouter.ui.adapter.user.UserHead
import com.stratagile.pnrouter.ui.adapter.user.UserItem
import kotlinx.android.synthetic.main.activity_test.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.test
 * @Description: $description
 * @date 2018/09/05 11:10:38
 */

class TestActivity : BaseActivity(), TestContract.View {

    @Inject
    internal lateinit var mPresenter: TestPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_test)
        setTitle("TestActivity")
//        avi.setIndicator("BallSpinFadeLoaderIndicator")
//        avi.show()
    }
    override fun initData() {
        button.setOnClickListener {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
//                    .theme(themeId)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .maxSelectNum(2)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
//                    .selectionMode(if (cb_choose_mode.isChecked())
//                        PictureConfig.MULTIPLE
//                    else
//                        PictureConfig.SINGLE)// 多选 or 单选
//                    .previewImage(cb_preview_img.isChecked())// 是否可预览图片
//                    .previewVideo(cb_preview_video.isChecked())// 是否可预览视频
//                    .enablePreviewAudio(cb_preview_audio.isChecked()) // 是否可播放音频
//                    .isCamera(cb_isCamera.isChecked())// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                    //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
//                    .enableCrop(cb_crop.isChecked())// 是否裁剪
//                    .compress(cb_compress.isChecked())// 是否压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    //.compressSavePath(getPath())//压缩图片保存地址
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
//                    .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
//                    .hideBottomControls(if (cb_hide.isChecked()) false else true)// 是否显示uCrop工具栏，默认不显示
//                    .isGif(cb_isGif.isChecked())// 是否显示gif图片
//                    .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
//                    .circleDimmedLayer(cb_crop_circular.isChecked())// 是否圆形裁剪
//                    .showCropFrame(cb_showCropFrame.isChecked())// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
//                    .showCropGrid(cb_showCropGrid.isChecked())// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
//                    .openClickSound(cb_voice.isChecked())// 是否开启点击声音
//                    .selectionMedia(selectList)// 是否传入已选图片
                    //.isDragFrame(false)// 是否可拖动裁剪框(固定)
                    //                        .videoMaxSecond(15)
                    //                        .videoMinSecond(10)
                    //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    //.cropCompressQuality(90)// 裁剪压缩质量 默认100
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    //.rotateEnabled(true) // 裁剪是否可旋转图片
                    //.scaleEnabled(true)// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()//显示多少秒以内的视频or音频也可适用
                    //.recordVideoSecond()//录制视频秒数 默认60s
                    .forResult(PictureConfig.CHOOSE_REQUEST)//结果回调onActivityResult code
        }
//        val list = arrayListOf<MultiItemEntity>()
//        for (i in 0..9) {
//            var userHead = UserHead()
//            userHead.userName = "xx" + i + "yy"
//            val list1 = arrayListOf<MultiItemEntity>()
//            for (j in 0..3) {
////                userHead.addSubItem(UserItem())
//            }
//            list.add(userHead)
//        }
//        var contactAdapter1 = ContactAdapter(list)
//        recyclerView.adapter = contactAdapter1
//        contactAdapter1.expandAll()
    }


    override fun setupActivityComponent() {
       DaggerTestComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .testModule(TestModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: TestContract.TestContractPresenter) {
            mPresenter = presenter as TestPresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}