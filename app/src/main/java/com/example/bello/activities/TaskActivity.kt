package com.example.bello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bello.R
import com.example.bello.databinding.ActivityTaskBinding
import com.example.bello.utils.Constants

class TaskActivity : AppCompatActivity() {

    private lateinit var boardDocumentId:String
    private lateinit var binding_single_task:ActivityTaskBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_single_task = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding_single_task.root)

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }else{
            Toast.makeText(this,"No string from parent",Toast.LENGTH_SHORT).show()
        }

        Log.d("Toddo", "$boardDocumentId si in here.")

        setUpActionBar()

    }

    private fun setUpActionBar(){
        val toolbar = binding_single_task.toolbarTask
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