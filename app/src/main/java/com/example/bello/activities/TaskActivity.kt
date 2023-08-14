package com.example.bello.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bello.R
import com.example.bello.adaptors.TaskMemberAdapter
import com.example.bello.databinding.ActivityTaskBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.SelectedMembers
import com.example.bello.model.Task
import com.example.bello.utils.Constants
import java.text.SimpleDateFormat
import java.util.Locale

class TaskActivity : AppCompatActivity() {

    private lateinit var boardDocumentId:String
    private lateinit var binding_single_task:ActivityTaskBinding
    private lateinit var mtask:Task
    private lateinit var adaptor:TaskMemberAdapter
    private lateinit var taskCreatedBy:String
    private var taskCreated:Long= 0L
    private lateinit var createBy:String

    private var anyChangeMade: Boolean=false


    override fun onCreate(savedInstanceState: Bundle?) {
        var taskCreated:Long=0
        super.onCreate(savedInstanceState)
        binding_single_task = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding_single_task.root)

        if(intent.hasExtra(Constants.DOCUMENT_ID) and intent.hasExtra("task_created")
        and intent.hasExtra(Constants.CREATED_BY)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
            taskCreated = intent.extras?.getLong("task_created")!!
            createBy = intent.getStringExtra(Constants.CREATED_BY).toString()
            Log.d("TSS","$taskCreated si si.")
        }else{
            Toast.makeText(this,"No string from parent",Toast.LENGTH_SHORT).show()
        }


        Log.d("TTT", "$taskCreated si in here.")

        setUpActionBar()

        FirestoreClass().getTaskDetail(this,boardDocumentId, taskCreated)

    }

    private fun setUpActionBar(){
        val toolbar = binding_single_task.toolbarTask
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            actionBar.title=""
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var status:String=""
        if(intent.hasExtra(Constants.STATUS_FRAGMENT)){
            status= intent.getStringExtra(Constants.STATUS_FRAGMENT).toString()
            when(status){
                "todo"->{
                    if(FirestoreClass().getCurrentUserId()== createBy)
                    {
                        menuInflater.inflate(R.menu.option_task_menu,menu)
                    }
                }
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.edit_task->{
                Toast.makeText(this,"Add member si selected",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,UpdateTaskActivity::class.java)
                intent.putExtra(Constants.TASK_CREATED,taskCreated)
                intent.putExtra(Constants.DOCUMENT_ID,boardDocumentId)
                startActivityForResult(intent, UPDATE_TASK_CODE)
                return true
            }
            R.id.delete_task ->{
                FirestoreClass().deleteTask(this,boardDocumentId,taskCreated)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == UPDATE_TASK_CODE){
            FirestoreClass().getTaskDetail(this,boardDocumentId, taskCreated)
        }else{
            Log.e("Task Update","Task Updated.")
        }
    }

    fun taskDetail(task: Task){
        mtask=task
        taskCreated = mtask.createdDate
        taskCreatedBy = mtask.createdBy
        var mtaskMember:ArrayList<SelectedMembers> = mtask.assignedMembers
        binding_single_task.taskTitle.setText(mtask.title)
        binding_single_task.taskDesc.setText(mtask.description)
        val fd = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        val dueDate = fd.format(mtask.dueDate)
        binding_single_task.taskDue.text = dueDate
        if(mtask.fileAttachment == ""){
            binding_single_task.taskFileAttachText.visibility= View.GONE
        }
        if(mtask.assignedMembers.size > 0){
            binding_single_task.recycleTaskMemberList.visibility=View.VISIBLE
        }else{
            binding_single_task.tvMemberListImg.visibility=View.GONE
            binding_single_task.recycleTaskMemberList.visibility=View.GONE
        }

        when (mtask.task_status) {
            "DOING" -> {
                Log.d("DPNE","${mtask.task_status} zzzzz")
                binding_single_task.statusTask.text = "DOING"
            }
            "DONE" -> {
                Log.d("DPNE","${mtask.task_status} zzzzz")
                binding_single_task.statusTask.text = "DONE"
            }
            else -> {
                binding_single_task.statusTask.text = "START"
            }
        }

        adaptor = TaskMemberAdapter(this, mtaskMember)
        binding_single_task.recycleTaskMemberList.layoutManager = GridLayoutManager(this,4)
        binding_single_task.recycleTaskMemberList.setHasFixedSize(true)
        binding_single_task.recycleTaskMemberList.adapter = adaptor

        binding_single_task.statusTask.setOnClickListener {
            val task = task
            var status = binding_single_task.statusTask.text
            when(status){
                "START"->{
                    Log.d("ACCC","${status} ssszzzz.")
                    task.task_status="DOING"
                    FirestoreClass().updateTaskStatus(this,boardDocumentId,taskCreated,task)
                    binding_single_task.statusTask.text="DOING"
                    anyChangeMade= true
                }
                "DOING" ->{
                    Log.d("ACCC","${status} ssszzzz.")
                    task.task_status="DONE"
                    FirestoreClass().updateTaskStatus(this,boardDocumentId,taskCreated,task)
                    binding_single_task.statusTask.text="DONE"
                    anyChangeMade= true
                }
            }
        }
    }

    fun deleteTaskSuccessfully(){
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onBackPressed() {
        if(anyChangeMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
//        if (anyChangeMade) {
//            val resultIntent = Intent()
//            resultIntent.putExtra("status", status) // Add extra data if needed
//            setResult(Activity.RESULT_OK, resultIntent)
//        }
//        super.onBackPressed()
    }

    fun updateStatusSuccessfully(){
        setResult(Activity.RESULT_OK)
        finish()
    }

    companion object{
        const val UPDATE_TASK_CODE: Int = 20
    }
}