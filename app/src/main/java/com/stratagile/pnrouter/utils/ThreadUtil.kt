package com.stratagile.pnrouter.utils

import android.graphics.Bitmap
import android.os.AsyncTask
import android.widget.ImageView
import android.widget.Toast
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig

class ThreadUtil {
    companion object {
        class CreateEnglishQRCode(var userId : String, var view: ImageView) : AsyncTask<Void, Void, Bitmap>() {
            override fun doInBackground(vararg p0: Void?): Bitmap {
                return QRCodeEncoder.syncEncodeQRCode(userId, BGAQRCodeUtil.dp2px(AppConfig.instance, 150f), AppConfig.instance.resources.getColor(R.color.mainColor))
            }

            override fun onPostExecute(bitmap: Bitmap?) {
                if (bitmap != null) {
                    view.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(AppConfig.instance, "Generation failure", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}