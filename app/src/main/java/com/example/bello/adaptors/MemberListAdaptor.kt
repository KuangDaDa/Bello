package com.example.bello.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bello.R
import com.example.bello.model.User
import com.example.bello.utils.Constants

class MemberListAdaptor(private val context: Context, private var list:ArrayList<User>):
RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener:OnClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MemberListViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.member_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MemberListViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_nav_user)
                .into(holder.itemView.findViewById(R.id.user_image))
            holder.itemView.findViewById<TextView>(R.id.member_name).setText(model.name)
            holder.itemView.findViewById<TextView>(R.id.member_email).setText(model.email)

            if(model.selected){
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_icon).visibility=View.VISIBLE
            }else{
                holder.itemView.findViewById<ImageView>(R.id.iv_selected_icon).visibility=View.GONE
            }

            holder.itemView.setOnClickListener{
                if(onClickListener !=null){
                    if(model.selected){
                        onClickListener!!.onClick(position,model,Constants.UN_SELECT)
                    }else{
                        onClickListener!!.onClick(position,model,Constants.SELECT)
                    }
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int,user:User,action:String)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


    private class MemberListViewHolder(view: View):RecyclerView.ViewHolder(view)
}