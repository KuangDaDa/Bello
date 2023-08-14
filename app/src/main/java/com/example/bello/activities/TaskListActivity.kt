package com.example.bello.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.bello.R
import com.example.bello.adaptors.FragmentPageAdaptor
import com.example.bello.adaptors.TaskListAdaptor
import com.example.bello.databinding.ActivityTaskListBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Board
import com.example.bello.model.User
import com.google.android.material.badge.BadgeDrawable
import com.example.bello.utils.Constants
import com.google.android.material.tabs.TabLayout


class TaskListActivity : AppCompatActivity() {
    private lateinit var adaptor: TaskListAdaptor
    private lateinit var task_binding: ActivityTaskListBinding
    private lateinit var mBoardDetail:Board

    private lateinit var mBoardDocumentID:String

    private lateinit var mBoardCreatedBy:String

    private lateinit var mAssignedMemberList:ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        task_binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(task_binding.root)


        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentID = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        if(intent.hasExtra(Constants.CREATED_BY)){
            mBoardCreatedBy = intent.getStringExtra(Constants.CREATED_BY).toString()
        }

        // testingh....
        Log.d("TTT", "$mBoardCreatedBy si si.")

        adaptor = TaskListAdaptor(supportFragmentManager,lifecycle,mBoardDocumentID,mBoardCreatedBy)
        task_binding.tabLayoutTask.addTab(task_binding.tabLayoutTask.newTab().setText("To Do"))
        task_binding.tabLayoutTask.addTab(task_binding.tabLayoutTask.newTab().setText("Doing"))
        task_binding.tabLayoutTask.addTab(task_binding.tabLayoutTask.newTab().setText("Done"))

        task_binding.viewPager2Task.adapter = adaptor

        task_binding.tabLayoutTask.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    task_binding.viewPager2Task.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        task_binding.viewPager2Task.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                task_binding.tabLayoutTask.selectTab(task_binding.tabLayoutTask.getTabAt(position))
            }
        })

//        task_binding.tabLayoutTask.getTabAt(0)?.setIcon(R.drawable.ic_nav_user)

//        val badgeDrawable: BadgeDrawable? = task_binding.tabLayoutTask.getTabAt(0)?.getOrCreateBadge()
//        badgeDrawable?.isVisible = true
//        badgeDrawable?.number = 5

        FirestoreClass().getBoardDetails(this, mBoardDocumentID)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MEMBER_REQUEST_CODE){
            FirestoreClass().getBoardDetails(this, mBoardDocumentID)
        }else{
            Log.e("Cancelled","Cancelled")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(FirestoreClass().getCurrentUserId().equals(mBoardCreatedBy)){
            menuInflater.inflate(R.menu.option_menu,menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_board -> {
               deleteSuccessfully()
            }
            R.id.add_member -> {
                val intent = Intent(this,MemeberActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetail)
                startActivityForResult(intent, MEMBER_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpActionBar(title:String){
        setSupportActionBar(task_binding.toolbarTask)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            actionBar.title=title
        }

        task_binding.toolbarTask.setNavigationOnClickListener {
            onBackPressed()
        }
    }

//    fun addTaskListSuccess(){
//        FirestoreClass().getBoardDetails(this, mBoardDetail.documentId)
//    }

    //  for tasklist title
    fun boardDetail(board: Board?){
        setUpActionBar(board!!.name)
        mBoardDetail = board
        FirestoreClass().getMemberListDetails(this,board.assignedTo)
    }

    fun boardMembersDetailsList(list:ArrayList<User>){
        mAssignedMemberList = list
    }

    private fun deleteSuccessfully(){
        FirestoreClass().deleteBoard(this,mBoardDocumentID)
        setResult(Activity.RESULT_OK, intent)
        finish()

    }

    companion object{
        const val MEMBER_REQUEST_CODE: Int = 12
    }
}