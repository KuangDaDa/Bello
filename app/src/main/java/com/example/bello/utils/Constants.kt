package com.example.bello.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.bello.activities.MyProfileActivity

object Constants{
    const val USERS:String = "users"
    const val BOARDS:String = "boards"
    const val TASKS:String="tasks"


    const val IMAGE:String="image"
    const val NAME:String="name"
    const val MOBILE:String = "mobile"

    const val DOCUMENT_ID:String="documentId"
    const val ASSIGNED_TO:String="assignedTo"

    const val READ_STORAGE_PERMISSION_CODE=1
    const val PICK_IMAGE_REQUEST_CODE=2

    const val TASK_LIST = "task_list"

    const val BOARD_DETAIL = "board_detail"

    const val USER_ID = "id"

    const val EMAIL:String="email"

    const val CREATED_BY = "created_by"

    const val BORAD_MEMBER_LIST = "board_member_list"
    const val TASK_MEMBER_ASSING = "assignedMembers"

    const val SELECT:String = "Select"
    const val UN_SELECT:String = "UnSelect"

    const val BOARD_DELETED="board_deleted"

    const val TASK_CREATED="task_created"

    const val TASK_CREATED_BY="task_created_by"

    const val STATUS_FRAGMENT="status"


    fun showImageChooser(activity:Activity){
        var galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity:Activity,uri: Uri?):String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}
