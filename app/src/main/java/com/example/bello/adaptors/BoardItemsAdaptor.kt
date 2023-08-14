package com.example.bello.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bello.R
import com.example.bello.model.Board
import com.example.bello.model.Task
import java.text.SimpleDateFormat
import java.util.Locale

open class BoardItemsAdaptor(private val context: Context, private var list:ArrayList<Board>):
RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BoardItemViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_board,
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
        if(holder is BoardItemViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_nav_user)
                .into(holder.itemView.findViewById(R.id.board_image))
        }
        val fd = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
        val created_date = fd.format(model.createdDate)
        holder.itemView.findViewById<TextView>(R.id.title).setText(model.name)
        holder.itemView.findViewById<TextView>(R.id.board_date).setText(created_date)
        holder.itemView.findViewById<TextView>(R.id.content).setText(created_date)
        holder.itemView.setOnClickListener{
            if(onClickListener!=null){
                onClickListener!!.onClick(position,model)
            }
        }
    }

    interface OnClickListener{
        fun onClick(position:Int, model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }

    private class BoardItemViewHolder(view:View):RecyclerView.ViewHolder(view)
}