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
        holder.itemView.findViewById<TextView>(R.id.task_name).text = model.title
        val sf = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        holder.itemView.findViewById<TextView>(R.id.task_date).text = sf.format(model.createdDate)

        when(model.task_priority){
            "LOW" -> {
                holder.itemView.findViewById<TextView>(R.id.task_priority_status).setCompoundDrawablesWithIntrinsicBounds(R.drawable.task_priority_circle, 0, 0, 0)
            }
            "MEDIUM" ->{
                holder.itemView.findViewById<TextView>(R.id.task_priority_status).setCompoundDrawablesWithIntrinsicBounds(R.drawable.task_priority_circle_medium, 0, 0, 0)
            }
            "HIGH" -> {
                holder.itemView.findViewById<TextView>(R.id.task_priority_status).setCompoundDrawablesWithIntrinsicBounds(R.drawable.task_priority_circle_high, 0, 0, 0)
            }
        }



        Log.d("Tssk","${model} zi zi.")

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