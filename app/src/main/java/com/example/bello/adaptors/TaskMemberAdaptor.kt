package com.example.bello.adaptors

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bello.R
import com.example.bello.model.SelectedMembers
import com.google.api.Distribution.BucketOptions.Linear

//class TaskMemberAdaptor(private val context: Context, private var list:ArrayList<SelectedMembers>) :
//    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return TaskAssignedMemberViewHolder(
//            LayoutInflater.from(context).inflate(
//                R.layout.task_member,
//                parent,
//                false
//            )
//        )
//    }
//
//    override fun getItemCount(): Int {
//         return list.size
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            val model = list[position]
//            Glide
//                .with(context)
//                .load(model.image)
//                .centerCrop()
//                .placeholder(R.drawable.ic_nav_user)
//                .into(holder.itemView.memberImage)
//
//    }
//
//    private class TaskAssignedMemberViewHolder(view: View):RecyclerView.ViewHolder(view){
//        val memberImage: ImageView = itemView.findViewById(R.id.task_member_img)
//    }
//}

class TaskMemberAdapter(private val context: Context, private val memberList: ArrayList<SelectedMembers>) :
    RecyclerView.Adapter<TaskMemberAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_member, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val member = memberList[position]
//        holder.memberName.text = member.name
//        holder.memberImage.setImageResource(member.imageResource)
        Glide
            .with(context)
            .load(member.image)
            .centerCrop()
            .placeholder(R.drawable.ic_nav_user)
            .into(holder.itemView.findViewById(R.id.task_member_img))
        Log.d("LLL","${member} mailllllll.")
        holder.itemView.findViewById<LinearLayout>(R.id.task_assigned_member_section).findViewById<TextView>(R.id.task_member_email).setText(member.email)
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}
