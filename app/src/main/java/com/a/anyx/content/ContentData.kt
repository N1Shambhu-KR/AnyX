package com.a.anyx.content

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class ContentData(val name: String?, val uri: Uri?, val path:String?, val length:Long?, val date:Long?)
