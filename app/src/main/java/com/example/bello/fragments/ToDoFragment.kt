package com.example.bello.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bello.activities.CreateTaskActivity
import com.example.bello.activities.TaskActivity
import com.example.bello.adaptors.TaskItemAdaptor
import com.example.bello.databinding.FragmentToDoBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Task
import com.example.bello.model.User
import com.example.bello.utils.Constants

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val CREATED_BY = "created_by"


/**
 * A simple [Fragment] subclass.
 * Use the [ToDoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ToDoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var tcreatedBy:String?=null
    private var _binding:FragmentToDoBinding? = null
    lateinit var BoardID:String
    lateinit var mCreatedBy:String

    private var mBoardAssigned:ArrayList<String> = ArrayList()

    private val binding get() =_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            tcreatedBy = it.getString(CREATED_BY)
        }
        BoardID = param1!!
        mCreatedBy = tcreatedBy!!
        Log.d("Board","${param1} is in ToDo Board")

        FirestoreClass().getAssignedMemberList(this, BoardID)

        Log.d("III","I am after getAssignedMemberList")

        FirestoreClass().getTaskList(this,BoardID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentToDoBinding.inflate(inflater,container,false)
        val view = binding.root
        if(FirestoreClass().getCurrentUserId().equals(mCreatedBy)){
            Log.d("TrueTrue","This is createor role.")
            binding.fabCreateTask.visibility=View.VISIBLE

        }else{
            binding.fabCreateTask.visibility=View.GONE
        }

        binding.fabCreateTask.setOnClickListener{
            val intent = Intent(requireContext(),CreateTaskActivity::class.java)
            intent.putExtra(com.example.bello.utils.Constants.DOCUMENT_ID,param1)
            intent.putExtra(Constants.BORAD_MEMBER_LIST,mBoardAssigned)
            startActivityForResult(intent,CREATE_TASK_REQUEST_CODE)
        }
        FirestoreClass().getTaskList(this,BoardID)
        return view
    }

    fun PopulateTaskList(taskList:ArrayList<Task>){
        val noTaskMessage = binding.noTaskMessage
        val recycleTaskList = binding.recycleTaskList
        if(taskList.size > 0){
            noTaskMessage.visibility=View.GONE
            recycleTaskList.visibility = View.VISIBLE

            recycleTaskList.layoutManager = LinearLayoutManager(context)
            recycleTaskList.setHasFixedSize(true)
            val taskAdaptor = context?.let { TaskItemAdaptor(it,taskList) }
            recycleTaskList.adapter=taskAdaptor

            taskAdaptor?.setOnClickListener(object:TaskItemAdaptor.OnClickListener{
                override fun onClick(position: Int, model: Task) {
                    val intent = Intent(activity,TaskActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,BoardID)
                    intent.putExtra("task_created",model.createdDate)
                    intent.putExtra(Constants.STATUS_FRAGMENT,"todo")
                    intent.putExtra(Constants.CREATED_BY,model.createdBy)
                    startActivityForResult(intent, DELETE_TASK_REQUEST_CODE)
                }

            })
        }else{
            recycleTaskList.visibility=View.GONE
            noTaskMessage.visibility=View.VISIBLE
        }
    }

    fun assignedMember(task:String){
        mBoardAssigned.add(task)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if(resultCode == Activity.RESULT_OK && requestCode == CREATE_TASK_REQUEST_CODE){
//            FirestoreClass().getTaskList(this,BoardID)
//        }
        Log.d("DAAA","$resultCode szzzeex")
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CREATE_TASK_REQUEST_CODE || requestCode == DELETE_TASK_REQUEST_CODE){
                FirestoreClass().getTaskList(this,BoardID)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ToDoFragment.
         */
        // TODO: Rename and change types and number of parameters

        const val CREATE_TASK_REQUEST_CODE:Int = 16
        const val DELETE_TASK_REQUEST_CODE:Int = 23
        @JvmStatic
        fun newInstance(param1: String,createdBy:String) =
            ToDoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(CREATED_BY,createdBy)
                }
            }
    }
}