package com.example.bello.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bello.R
import com.example.bello.adaptors.MemberListAdaptor
import com.example.bello.model.User

abstract class MemberListDialog(context: Context, private var list:ArrayList<User>, private val title:String="")
    :Dialog(context){

    private var adaptor:MemberListAdaptor?=null
//    private var memberListDialogDismissListener: MemberListDialogDismissListener? = null

//    fun setMemberListDialogDismissListener(listener: MemberListDialogDismissListener) {
//        memberListDialogDismissListener = listener
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_member_list,null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }

//    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
//        super.setOnDismissListener(listener)
//        memberListDialogDismissListener?.onMemberListDialogDismiss()
//    }

//    override fun dismiss() {
//        super.dismiss()
//        // Notify the listener that the dialog is dismissed
//        memberListDialogDismissListener?.onMemberListDialogDismiss( )
//    }



    private fun setUpRecyclerView(view: View){
        view.findViewById<TextView>(R.id.tvTitle).text=title
        Log.d("EEE","$list sisisisis")
        if(list.size > 0){
            view.findViewById<RecyclerView>(R.id.tv_member_list).layoutManager = LinearLayoutManager(context)
            adaptor = MemberListAdaptor(context,list)
            view.findViewById<RecyclerView>(R.id.tv_member_list).adapter = adaptor

            adaptor!!.setOnClickListener(
                object:MemberListAdaptor.OnClickListener{
                    override fun onClick(position: Int, user: User, action: String) {
                        dismiss()
                        onItemSelected(user,action)
                    }

                }
            )

        }
    }
    protected abstract fun onItemSelected(user: User, action:String)
}