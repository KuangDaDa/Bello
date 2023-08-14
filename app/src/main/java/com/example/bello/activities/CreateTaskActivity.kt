package com.example.bello.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.bello.R
import com.example.bello.activities.TaskListActivity.Companion.MEMBER_REQUEST_CODE
import com.example.bello.adaptors.TaskMemberAdapter
import com.example.bello.adaptors.TaskMemberListItemAdaptor
import com.example.bello.databinding.ActivityCreateTaskBinding
import com.example.bello.dialog.MemberListDialog
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Board
import com.example.bello.model.SelectedMembers
import com.example.bello.model.Task
import com.example.bello.model.User
import com.example.bello.utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

interface TaskPriorityDialogCallbackInCreate {
    fun onStatusUpdate(status: String, drawable: Drawable?)
}

//interface MemberListDialogDismissListener {
//    fun onMemberListDialogDismiss()
//}
class CreateTaskActivity : AppCompatActivity(), TaskPriorityDialogCallbackInCreate {
    private lateinit var binding_task:ActivityCreateTaskBinding
    private lateinit var mboard: Board
    private lateinit var boardDocumentId:String
    private lateinit var status: String
    private var dueDate:Long=0
    private lateinit var adaptor: TaskMemberListItemAdaptor



    private lateinit var mMembersDetailList:ArrayList<User>

    private lateinit var assignedMember:ArrayList<String>

    private var assignedMembers:ArrayList<SelectedMembers> = ArrayList()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_task = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding_task.root)


        setUpActionBar()

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        if(intent.hasExtra(Constants.BORAD_MEMBER_LIST)){
            assignedMember = intent.getStringArrayListExtra(Constants.BORAD_MEMBER_LIST) as ArrayList<String>
        }

        FirestoreClass().getMemberListDetails(this,assignedMember)

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
                    val formatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date = formatDate.format(selectDate.time)
                    val dateToMili = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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

        binding_task.taskPriority.setOnClickListener{
            DialogTaskPriority(this)
//            binding_task.taskPriority.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24,0,0,0)
//            binding_task.taskPriority.setText(status)
        }

        binding_task.addMemberSection.setOnClickListener{
            AddMemberListDialog()
        }

        binding_task.recycleAddTaskMemberList.setOnClickListener{
            Log.d("RRR","Recycler view work!!!")
            AddMemberListDialog()
        }

        val createdTime = Calendar.getInstance().time.toInstant().toEpochMilli()

        binding_task.button.setOnClickListener{
            val task=Task(
                binding_task.taskName.text.toString(),
                binding_task.taskDescription.text.toString(),
                dueDate,
                FirestoreClass().getCurrentUserId(),
                createdTime,
                task_priority = status,
                task_status = "TODO",
                fileAttachment = "",
                assignedMembers = assignedMembers
            )
            FirestoreClass().createTasks(this,mboard,task)

            Toast.makeText(this,"In create mode of the todolist",Toast.LENGTH_SHORT).show()
//            binding_task.taskName.setText("")
//            binding_task.taskDescription.setText("")
        }
    }

    fun  getBoardDetail(board:Board){
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


    // Original
    fun AddMemberListDialog(){
        val dialog = object :MemberListDialog(this,mMembersDetailList,"Assign members to task"){
            override fun onItemSelected(user: User, action: String) {
                Log.d("YYY","${user.name} si si ${action}")
                var selected = SelectedMembers(user.id,user.image,user.email)
                assignedMembers.add(selected)
            }
        }
//        dialog.setMemberListDialogDismissListener(this)
        dialog.setOnDismissListener{
            updateAssignedMembersUI()
        }
        dialog.show()
    }

    private fun updateAssignedMembersUI() {
//        val layoutInflater = LayoutInflater.from(this)
//        binding_task.addMemberSection.removeAllViews() // Clear the container first
//
//        for (member in assignedMembers) {
//            val view = layoutInflater.inflate(
//                R.layout.task_selected_member,
//                binding_task.addMemberSection,
//                false
//            )
//            // Assuming the layout_assigned_member has an ImageView to display the member image
//            val memberImageView = view.findViewById<ImageView>(R.id.iv_add_member)
//            // Load the member image into the ImageView using a library like Glide or Picasso
//            // For example:
//            Glide.with(this).load(member.image).into(memberImageView)
//
//            // Add the view to the container
//            binding_task.addMemberSection.addView(view)

//        }

        adaptor = TaskMemberListItemAdaptor(this, assignedMembers)
        binding_task.recycleAddTaskMemberList.layoutManager = GridLayoutManager(this,4)
        adaptor.setOnClickListener(object : TaskMemberListItemAdaptor.OnClickListener{
            override fun onClick() {
                AddMemberListDialog()
            }

        })
        binding_task.recycleAddTaskMemberList.setHasFixedSize(true)
        binding_task.recycleAddTaskMemberList.adapter=adaptor

        if(assignedMembers.size < 4){
            binding_task.recycleAddTaskMemberList.visibility = View.VISIBLE
            binding_task.addMemberIcon.visibility = View.VISIBLE
        }
        else{
            binding_task.recycleAddTaskMemberList.visibility=View.VISIBLE
            binding_task.addMemberIcon.visibility = View.GONE
        }


    }

    //testing
/*    fun AddMemberListDialog() {
        val intent = Intent(this, MemeberActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mboard)
        startActivityForResult(intent, MEMBER_REQUEST_CODE)
    }*/

//    fun AddMemberListDialog(){
//        val dialog = object :MemberListDialog(this,mMembersDetailList,"Assign members to task"){
//            override fun onItemSelected(user: User, action: String) {
//                Log.d("YYY","${user.name} si si ${action}")
//                var selected = SelectedMembers(user.id,user.image)
//                assignedMembers.add(selected)
//            }
//        }
//        dialog.show()
//    }

//    TODO("add selected status in member list")
//    private fun membersListDialog(){
//        var taskAssigend = mboard.taskList[0].assignedMembers
//    }

    private fun DialogTaskPriority(callback: TaskPriorityDialogCallbackInCreate) {
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

    fun getBoardAssignedList(list:ArrayList<User>){
        mMembersDetailList=list
    }

    fun taskCreatedSuccessfully(){
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onStatusUpdate(status: String, drawable: Drawable?) {
        binding_task.taskPriority.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        binding_task.taskPriority.text = status
        this.status =status
    }

//    override fun onMemberListDialogDismiss() {
//        updateAssignedMembersUI()
//    }
}