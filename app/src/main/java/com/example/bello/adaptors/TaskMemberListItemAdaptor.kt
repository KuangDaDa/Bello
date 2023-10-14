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

open class TaskMemberListItemAdaptor(private val context: Context, private val list:ArrayList<SelectedMembers>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return TaskMemberViewHolder(LayoutInflater.from(context).inflate(
           R.layout.task_member,
           parent,
           false
       ))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        Log.d("CCC","$position sis siss ${list.size}")
        if(holder is TaskMemberViewHolder){
//            if(position == 3){
//                Log.d("CCC1","${position} sis siss")
//                holder.itemView.findViewById<LinearLayout>(R.id.is_added_member_group).visibility = View.VISIBLE
//                holder.itemView.findViewById<ImageView>(R.id.is_selected_image).visibility = View.GONE
//            }else{
//                holder.itemView.findViewById<LinearLayout>(R.id.is_added_member_group).visibility = View.GONE
//                holder.itemView.findViewById<ImageView>(R.id.is_selected_image).visibility = View.VISIBLE
//                Glide
//                    .with(context)
//                    .load(model.image)
//                    .centerCrop()
//                    .placeholder(R.drawable.ic_nav_user)
//                    .into(holder.itemView.findViewById(R.id.is_selected_image))
//                Log.d("CCC2","$model sis siss")
//            }

//            Log.d("CCC2","${model.image} sis siss")
                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_nav_user)
                    .into(holder.itemView.findViewById(R.id.task_member_img))
                Log.d("JJJ","Glide work!!!")
                holder.itemView.findViewById<LinearLayout>(R.id.task_assigned_member_section).findViewById<TextView>(R.id.task_member_email).setText(model.email)
                Log.d("JJJ1","Hello world work!!!")


            holder.itemView.setOnClickListener{
                if(list.size < 4){
                    if(onClickListener != null){
                        onClickListener!!.onClick()
                    }
                }
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick()
    }

    class TaskMemberViewHolder(view: View):RecyclerView.ViewHolder(view)
}