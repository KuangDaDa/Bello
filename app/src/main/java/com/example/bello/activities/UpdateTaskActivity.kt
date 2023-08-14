package com.example.bello.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bello.R
import com.example.bello.adaptors.TaskMemberAdapter
import com.example.bello.databinding.ActivityCreateTaskBinding
import com.example.bello.databinding.ActivityUpdateTaskBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Task
import com.example.bello.utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

interface TaskPriorityDialogCallback {
    fun onStatusUpdate(status: String, drawable: Drawable?)
}

class UpdateTaskActivity : AppCompatActivity(),TaskPriorityDialogCallback {
    private lateinit var binding_updat_task:ActivityUpdateTaskBinding
    private lateinit var boardDocumentId:String
    private var taskCreated:Long=0L
    private lateinit var taskDetail:Task
    private lateinit var adaptor:TaskMemberAdapter

    private lateinit var status: String
    private var dueDate:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_updat_task = ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding_updat_task.root)

        if(intent.hasExtra(Constants.TASK_CREATED) && intent.hasExtra(Constants.DOCUMENT_ID)){
            taskCreated = intent.extras?.getLong(Constants.TASK_CREATED)!!
            boardDocumentId = intent.extras?.getString(Constants.DOCUMENT_ID).toString()
        }

        setUpActionBar()

        FirestoreClass().getTaskDetail(this,boardDocumentId, taskCreated)
    }

    private fun setUpActionBar(){
        val toolbar = binding_updat_task.editToolbarCreateTask
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            actionBar.title= resources.getString(R.string.task_update)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun updateTask(task:Task){
        taskDetail = task
        binding_updat_task.editTaskName.setText(taskDetail.title)
        binding_updat_task.editTaskDescription.setText(taskDetail.description)
        //binding_updat_task.endDate.setText(task.dueDate)
        val fd = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
        val due_date = fd.format(taskDetail.dueDate)
        binding_updat_task.editEndDate.setText(due_date)
        binding_updat_task.editTaskPriority.setText(taskDetail.task_priority)

        when(taskDetail.task_priority){
            "LOW" -> {
                binding_updat_task.editTaskPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.task_priority_circle, 0, 0, 0)
            }
            "MEDIUM" ->{
                binding_updat_task.editTaskPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.task_priority_circle_medium, 0, 0, 0)
            }
            "HIGH" -> {
                binding_updat_task.editTaskPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.task_priority_circle_high, 0, 0, 0)
            }
        }

        binding_updat_task.editTaskPriority.setOnClickListener{
            DialogTaskPriority(this)
        }

        binding_updat_task.editEndDate.setOnClickListener {
            val getDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(this,R.style.MyDatePickerDialogStyle,
                { datePicker, year, monthOfyear, dayOfMonth ->
                    val selectDate: Calendar = Calendar.getInstance()
                    selectDate.set(Calendar.YEAR,year)
                    selectDate.set(Calendar.MONTH,monthOfyear)
                    selectDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                    val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date = formatDate.format(selectDate.time)
                    val dateToMili = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val dateInMills = dateToMili.parse(date)
                    dueDate = dateInMills!!.time
                    binding_updat_task.editEndDate.setText(date)
                    Toast.makeText(this,"Date: "+date, Toast.LENGTH_SHORT).show()
                }
                ,getDate.get(Calendar.YEAR),getDate.get(Calendar.MONTH),getDate.get(Calendar.DAY_OF_MONTH))
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }

        binding_updat_task.editButton.setOnClickListener {
            Log.d("DDDA","${taskDetail.dueDate} ssssss")
            Log.d("DDDA","${dueDate} ssssss")
            var newDate:Long=0
            if(dueDate < taskDetail.dueDate){
                newDate = taskDetail.dueDate
            }else
            {
                newDate = dueDate
            }
            val task =Task()
            task.title = binding_updat_task.editTaskName.text.toString()
            task.description = binding_updat_task.editTaskDescription.text.toString()
            task.task_priority = binding_updat_task.editTaskPriority.text.toString()
            task.dueDate = newDate
            task.createdDate = taskDetail.createdDate
            task.createdBy = taskDetail.createdBy
            task.task_order = taskDetail.task_order
            task.task_status = taskDetail.task_status
            task.fileAttachment = taskDetail.fileAttachment
            task.assignedMembers = taskDetail.assignedMembers

            FirestoreClass().updateTask(this,boardDocumentId,taskCreated,task)
        }
    }

    private fun DialogTaskPriority(callback: TaskPriorityDialogCallback) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.priority_dialog)

        val low = dialog.findViewById<LinearLayout>(R.id.low_color)
        val medium = dialog.findViewById<LinearLayout>(R.id.medium_color)
        val high = dialog.findViewById<LinearLayout>(R.id.high_color)

        fun resetCompoundDrawables() {
            low.findViewById<TextView>(R.id.low_txt).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            medium.findViewById<TextView>(R.id.medium_txt).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            high.findViewById<TextView>(R.id.high_txt).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }

        high.setOnClickListener {
            val status="HIGH"
            val drawable =  ContextCompat.getDrawable(this,R.drawable.task_priority_circle_high)
            resetCompoundDrawables()
            high.findViewById<TextView>(R.id.high_txt).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_check_24, 0)
            callback.onStatusUpdate(status,drawable)
        }

        medium.setOnClickListener {
            val status="MEDIUM"
            resetCompoundDrawables()
            val drawable =  ContextCompat.getDrawable(this,R.drawable.task_priority_circle_medium)
            medium.findViewById<TextView>(R.id.medium_txt).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_check_24, 0)
            callback.onStatusUpdate(status,drawable)
        }

        low.setOnClickListener {
            val status="LOW"
            val drawable =  ContextCompat.getDrawable(this,R.drawable.task_priority_circle)
            resetCompoundDrawables()
            low.findViewById<TextView>(R.id.low_txt).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_check_24, 0)
            callback.onStatusUpdate(status,drawable)
        }

        dialog.show()
    }

    fun updateSuccessfully(){
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onStatusUpdate(status: String, drawable: Drawable?) {
        binding_updat_task.editTaskPriority.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        binding_updat_task.editTaskPriority.text = status
        this.status =status
    }
}