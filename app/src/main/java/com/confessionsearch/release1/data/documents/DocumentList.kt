package com.confessionsearch.release1.data.documents

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.*

//DocumentList Class for Confessionsearchapp
class DocumentList() : ArrayList<Document>(), Parcelable, Serializable {
    var title: String? = null

    constructor(parcel: Parcel) : this() {
        title = parcel.readString()
    }

    init {
        this.title = ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DocumentList> {
        override fun createFromParcel(parcel: Parcel): DocumentList {
            return DocumentList(parcel)
        }

        override fun newArray(size: Int): Array<DocumentList?> {
            return arrayOfNulls(size)
        }
    }


}
