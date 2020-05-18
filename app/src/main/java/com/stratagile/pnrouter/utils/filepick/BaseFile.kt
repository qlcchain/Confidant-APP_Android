package droidninja.filepicker.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by droidNinja on 29/07/16.
 */
@Parcelize
open class BaseFile(open var id: Long = 0,
                    open var name: String,
                    open var path: String
) : Parcelable