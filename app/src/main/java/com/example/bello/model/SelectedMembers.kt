package com.example.bello.model

import android.os.Parcel
import android.os.Parcelable

data class SelectedMembers(
    val id:String="",
    val image:String="",
    val email:String=""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(image)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SelectedMembers> {
        override fun createFromParcel(parcel: Parcel): SelectedMembers {
            return SelectedMembers(parcel)
        }

        override fun newArray(size: Int): Array<SelectedMembers?> {
            return arrayOfNulls(size)
        }
    }
}