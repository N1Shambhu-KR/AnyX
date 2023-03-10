package com.a.anyx.content

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class ContentData(
    val name: String?, val uri: Uri?, val path:String?, val length:Long?, val date:Long?,
    val mimeType: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(path)
        parcel.writeValue(length)
        parcel.writeValue(date)
        parcel.writeString(mimeType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContentData> {
        override fun createFromParcel(parcel: Parcel): ContentData {
            return ContentData(parcel)
        }

        override fun newArray(size: Int): Array<ContentData?> {
            return arrayOfNulls(size)
        }
    }
}
