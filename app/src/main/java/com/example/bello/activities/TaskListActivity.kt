package com.example.bello.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.bello.R
import com.example.bello.adaptors.TaskListAdaptor
import com.example.bello.databinding.ActivityTaskListBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Board
import com.google.android.material.badge.BadgeDrawable
import com.example.bello.utils.Constants
import com.google.android.material.tabs.TabLayout


class TaskListActivity : AppCompatActivity() {
    private lateinit var adaptor: TaskListAdaptor
//    private lateinit var tabLayout: TabLayout
//    private lateinit var viewPager2: ViewPager2
    private lateinit var task_binding: ActivityTaskListBinding
    private lateinit var mBoardDetail:Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var boardDocumentId:String=""
        task_binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(task_binding.root)

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        adaptor = TaskListAdaptor(supportFragmentManager,lifecycle, boardDocumentId)
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

        FirestoreClass().getBoardDetails(this, boardDocumentId)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_board -> Toast.makeText(this,"You clicked deleted action.",Toast.LENGTH_LONG).show()
            R.id.add_member -> Toast.makeText(this,"You clicked add members action.",Toast.LENGTH_SHORT).show()
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

    fun addTaskListSuccess(){
        FirestoreClass().getBoardDetails(this, mBoardDetail.documentId)
    }

    //  for tasklist title
    fun boardDetail(board: Board?){
        setUpActionBar(board!!.name)
        mBoardDetail = board
    }
}