package com.stratagile.pnrouter.utils.filepick

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.IntegerRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.ui.activity.email.EmailFileAttachmentShowActivity
import com.stratagile.pnrouter.utils.filepick.sort.SortingTypes
import droidninja.filepicker.models.FileType
import java.util.ArrayList

/**
 * Created by droidNinja on 29/07/16.
 */
class FilePickerBuilder {

    private val mPickerOptionsBundle: Bundle = Bundle()

    fun setMaxCount(maxCount: Int): FilePickerBuilder {
        PickerManager.setMaxCount(maxCount)
        return this
    }

    fun setActivityTitle(title: String): FilePickerBuilder {
        PickerManager.title = title
        return this
    }

    fun setSelectedFiles(selectedPhotos: ArrayList<Uri>): FilePickerBuilder {
        mPickerOptionsBundle.putParcelableArrayList(FilePickerConst.KEY_SELECTED_MEDIA, selectedPhotos)
        return this
    }

    fun enableVideoPicker(status: Boolean): FilePickerBuilder {
        PickerManager.setShowVideos(status)
        return this
    }

    fun enableImagePicker(status: Boolean): FilePickerBuilder {
        PickerManager.setShowImages(status)
        return this
    }

    fun enableSelectAll(status: Boolean): FilePickerBuilder {
        PickerManager.enableSelectAll(status)
        return this
    }

    fun showGifs(status: Boolean): FilePickerBuilder {
        PickerManager.isShowGif = status
        return this
    }

    fun showFolderView(status: Boolean): FilePickerBuilder {
        PickerManager.isShowFolderView = status
        return this
    }

    fun enableDocSupport(status: Boolean): FilePickerBuilder {
        PickerManager.isDocSupport = status
        return this
    }

    fun enableCameraSupport(status: Boolean): FilePickerBuilder {
        PickerManager.isEnableCamera = status
        return this
    }


    fun withOrientation(@IntegerRes orientation:  Int): FilePickerBuilder {
        PickerManager.orientation = orientation
        return this
    }

    @JvmOverloads
    fun addFileSupport(title: String, extensions: Array<String>, @DrawableRes drawable: Int = R.mipmap.sheet_file): FilePickerBuilder {
        mPickerOptionsBundle.putString(FilePickerConst.EXTRA_FILE_TYPE, title)
        PickerManager.addFileType(FileType(title, extensions, drawable))
        return this
    }

    fun sortDocumentsBy(type: SortingTypes): FilePickerBuilder {
        PickerManager.sortingType = type
        return this
    }

    fun pickPhoto(context: Activity) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER)
        start(context, FilePickerConst.REQUEST_CODE_PHOTO)
    }


    fun pickFile(context: Activity) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.DOC_PICKER)
        start(context, FilePickerConst.REQUEST_CODE_DOC)
    }

    fun pickPhoto(context: Activity, requestCode: Int) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER)
        start(context, requestCode)
    }

    fun pickFile(context: Activity, requestCode: Int) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.DOC_PICKER)
        start(context, requestCode)
    }


    private fun start(context: Activity, requestCode: Int) {

        val intent = Intent(context, EmailFileAttachmentShowActivity::class.java)
        intent.putExtras(mPickerOptionsBundle)

        context.startActivityForResult(intent, requestCode)
    }

    companion object {
        @JvmStatic
        val instance: FilePickerBuilder
            get() = FilePickerBuilder()
    }
}
