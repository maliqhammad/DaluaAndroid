package com.dalua.app.models

import android.os.Parcel
import android.os.Parcelable

class ABCValuesModel(val a: String, val b: String, val c: String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(a)
        parcel.writeString(b)
        parcel.writeString(c)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ABCValuesModel> {
        override fun createFromParcel(parcel: Parcel): ABCValuesModel {
            return ABCValuesModel(parcel)
        }

        override fun newArray(size: Int): Array<ABCValuesModel?> {
            return arrayOfNulls(size)
        }
    }
}