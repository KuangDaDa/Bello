package com.example.bello.activities

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bello.R
import com.example.bello.databinding.ActivityCreateTaskBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Board
import com.example.bello.model.Task
import com.example.bello.utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateTaskActivity : AppCompatActivity() {
    private lateinit var binding_task:ActivityCreateTaskBinding
    private lateinit var mboard: Board
    private lateinit var boardDocumentId:String

    private var dueDate:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_task = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding_task.root)

        setUpActionBar()

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        Log.d("Todoo", "$boardDocumentId is in here.")

        FirestoreClass().boardDetail(this,boardDocumentId)

//        binding_task.startDate.setOnClickListener{
//            val getDate = Calendar.getInstance()
//            val datePicker = DatePickerDialog(this,R.style.MyDatePickerDialogStyle,
//                { datePicker, i, i2, i3 ->
//                    val selectDate:Calendar = Calendar.getInstance()
//                    selectDate.set(Calendar.YEAR,i)
//                    selectDate.set(Calendar.MONTH,i2)
//                    selectDate.set(Calendar.DAY_OF_MONTH,i3)
//
//                    var formatDate = SimpleDateFormat("dd MMMM YYYY", Locale.US)
//                    val date = formatDate.format(selectDate.time)
//                    dueDate = formatDate.parse(date)!!.time
//                    binding_task.startDate.setText(date)
//                    Toast.makeText(this,"Date: "+date,Toast.LENGTH_SHORT).show()
//                }
//            ,getDate.get(Calendar.YEAR),getDate.get(Calendar.MONTH),getDate.get(Calendar.DAY_OF_MONTH))
//            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
//            datePicker.show()
//        }

        binding_task.endDate.setOnClickListener{
            val getDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(this,R.style.MyDatePickerDialogStyle,
                { datePicker, year, monthOfyear, dayOfMonth ->
                    val selectDate:Calendar = Calendar.getInstance()
                    selectDate.set(Calendar.YEAR,year)
                    selectDate.set(Calendar.MONTH,monthOfyear)
                    selectDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                    var formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date = formatDate.format(selectDate.time)
                    var dateToMili = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val dateInMills = dateToMili.parse(date)
                    dueDate = dateInMills!!.time
                    binding_task.endDate.setText(date)
                    Log.d("DueDate","$dueDate is here.")
                    Toast.makeText(this,"Date: "+date,Toast.LENGTH_SHORT).show()
                }
                ,getDate.get(Calendar.YEAR),getDate.get(Calendar.MONTH),getDate.get(Calendar.DAY_OF_MONTH))
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }

        binding_task.taskFile.setOnClickListener{
            Toast.makeText(this,"You clicked the file attachement.",Toast.LENGTH_SHORT).show()
        }

        binding_task.button.setOnClickListener{
            var task=Task(
                binding_task.taskName.text.toString(),
                binding_task.taskDescription.text.toString(),
                dueDate,
                FirestoreClass().getCurrentUserId()
            )
//            mboard.taskList.add(0,task)
            FirestoreClass().createTasks(this,mboard,task)

            Toast.makeText(this,"In create mode of the todolist",Toast.LENGTH_SHORT).show()
            binding_task.taskName.setText("")
            binding_task.taskDescription.setText("")
//            binding_task.endDate.setText("Due date")
        }
    }

    fun getBoardDetail(board:Board){
        mboard=board
    }

    private fun setUpActionBar(){
        val toolbar = binding_task.toolbarCreateTask
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            actionBar.title= resources.getString(R.string.task_create)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}