package droidninja.filepicker.models

import android.net.Uri
import kotlinx.android.parcel.Parcelize

@Parcelize
class Media @JvmOverloads constructor(override var id: Long = 0,
                                      override var name: String,
                                      override var path: String,
                                      var mediaType: Int = 0) : BaseFile(id, name, path)





