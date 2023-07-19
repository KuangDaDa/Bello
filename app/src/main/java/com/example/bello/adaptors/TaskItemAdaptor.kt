package com.example.bello.adaptors

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bello.R
import com.example.bello.model.Board
import com.example.bello.model.Task
import java.text.SimpleDateFormat
import java.util.Locale


class TaskItemAdaptor(private val context: Context, private var list:ArrayList<Task>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskItemViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.task_item,
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
//        if(holder is TaskItemViewHolder){
//
//        }
        holder.itemView.findViewById<TextView>(R.id.task_name).setText(model.title)
        val sf = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        holder.itemView.findViewById<TextView>(R.id.task_date).setText(sf.format(model.dueDate))

        Log.d("Tssk","si I am in TaskItem!!")

        holder.itemView.setOnClickListener{
            if(onClickListener!=null){
                Log.d("Tssk","si I am in TaskIte!!")
                onClickListener!!.onClick(position,model)
            }
        }
    }

    interface OnClickListener{
        fun onClick(position:Int, model: Task)
    }

    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener = onClickListener
    }

    private class TaskItemViewHolder(view: View):RecyclerView.ViewHolder(view)

}