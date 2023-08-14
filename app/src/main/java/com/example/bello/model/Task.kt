package com.example.bello.model

import android.os.Parcel
import android.os.Parcelable

data class Task(
    var title:String="",
    var description:String="",
    var dueDate:Long = 0,
    var createdBy:String="",
    var createdDate:Long = 0,
    var task_status:String="ToDo",
    var task_priority:String="",
    var task_order:Int=0,
    var fileAttachment:String="",
    var assignedMembers: ArrayList<SelectedMembers> = ArrayList()
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.createTypedArrayList(SelectedMembers.CREATOR)!!,

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeLong(dueDate)
        parcel.writeString(createdBy)
        parcel.writeLong(createdDate)
        parcel.writeString(task_status)
        parcel.writeString(task_priority)
        parcel.writeInt(task_order)
        parcel.writeString(fileAttachment)
        parcel.writeTypedList(assignedMembers)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
