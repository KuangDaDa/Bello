package com.example.bello.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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

    // for test

    // Permission for notification
//    @RequiresApi(Build.VERSION_CODES.O)
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission(),
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            // FCM SDK (and your app) can post notifications.
//            Log.d("FCMXXX","${isGranted}Fucking FCM.")
//
//            val intent= if(FirestoreClass().getCurrentUserId().isNotEmpty()){
//                Intent(this, TaskListActivity::class.java)
//            }else{
//                Intent(this, SignInActivity::class.java)
//            }
//            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
//                    Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            val pendingIntent = PendingIntent.getActivity(
//                this,
//                0, intent,
//                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
//            )
//
//            val channelId = this.resources.getString(R.string.default_notification_channel_id)
//
//            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//            val notificationBuilder = NotificationCompat.Builder(
//                this, channelId
//            ).setSmallIcon(R.drawable.ic_stat_ic_notification)
//                .setContentTitle(title)
//                .setContentText("Worlding")
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//
//            val notificationManager = getSystemService(
//                Context.NOTIFICATION_SERVICE
//            ) as NotificationManager
//
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//                val channel = NotificationChannel(channelId,
//                    "Channel Project Bello title",
//                    NotificationManager.IMPORTANCE_DEFAULT
//                )
//                notificationManager.createNotificationChannel(channel)
//            }
//            notificationManager.notify(0,notificationBuilder.build())
//        } else {
//            // TODO: Inform user that that your app will not show notifications.
//            Log.d("${isGranted} FCMXXX","Fucking in esle FCM.")
//            askNotificationPermission()
//        }
//    }
//
//    private fun askNotificationPermission() {
//        // This is only necessary for API level >= 33 (TIRAMISU)
//        Log.d("FCMXXX","Fucking FCM.")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Log.d("FCMXXX","Fucking FCM.")
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//                PackageManager.PERMISSION_GRANTED
//            ) {
//
//                Log.d("FCMXXX","Fucking FCM in granted..")
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                // TODO: display an educational UI explaining to the user the features that will be enabled
//                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
//                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//                //       If the user selects "No thanks," allow the user to continue without notifications.
//                showEducationalDialog()
//            } else {
//                // Directly ask for the permission
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//        }
//    }
//
//    private fun showEducationalDialog() {
//        val dialogBuilder = AlertDialog.Builder(this)
//        dialogBuilder.setTitle("Allow Notifications")
//        dialogBuilder.setMessage("By granting notifications, you'll receive important updates and information.")
//        dialogBuilder.setPositiveButton("OK") { _, _ ->
//            // Request the notification permission
//            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//        }
//        dialogBuilder.setNegativeButton("No thanks") { _, _ ->
//            // User declines notifications, continue without requesting permission
//            // ... (handle the scenario where the user declines notifications)
//        }
//        val dialog = dialogBuilder.create()
//        dialog.show()
//    }
////    // end of permission notification


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

    private fun DeleteBoardDialog(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_confirm_box)
        Log.d("AAA","Text in Dialog work")
        dialog.findViewById<TextView>(R.id.dialog_confirm_title).text= "Delete Board"
        dialog.findViewById<TextView>(R.id.dialog_confirm_message).text="Are you sure to delete the board."
        dialog.findViewById<TextView>(R.id.dialog_confirm_btn).text="Delete"
        dialog.findViewById<TextView>(R.id.dialog_confirm_btn).setOnClickListener {
            deleteSuccessfully()
        }
        dialog.findViewById<TextView>(R.id.dialog_mb_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_board -> {
                DeleteBoardDialog()
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