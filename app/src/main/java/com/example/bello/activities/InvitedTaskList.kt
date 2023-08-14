package com.example.bello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bello.R
import com.example.bello.adaptors.TaskListAdaptor
import com.example.bello.databinding.ActivityInvitedTaskListBinding
import com.example.bello.databinding.ActivityTaskListBinding
import com.example.bello.model.Board
import com.example.bello.utils.Constants

class InvitedTaskList : AppCompatActivity() {
    private lateinit var invited_task_binding: ActivityInvitedTaskListBinding
    private lateinit var mBoardDetail: Board
    private lateinit var adaptor: TaskListAdaptor
    private lateinit var mBoardDocumentID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invited_task_binding = ActivityInvitedTaskListBinding.inflate(layoutInflater)
        setContentView(invited_task_binding.root)

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

//        adaptor = TaskListAdaptor(supportFragmentManager,lifecycle)
//        invited_task_binding.tabInvitedLayoutTask.addTab(invited_task_binding.tabInvitedLayoutTask.newTab().setText("To Do"))
//        invited_task_binding.tabInvitedLayoutTask.addTab(invited_task_binding.tabInvitedLayoutTask.newTab().setText("Doing"))
//        invited_task_binding.tabInvitedLayoutTask.addTab(invited_task_binding.tabInvitedLayoutTask.newTab().setText("Done"))
//
//        invited_task_binding.viewPager2InvitedTask.adapter = adaptor

    }
}