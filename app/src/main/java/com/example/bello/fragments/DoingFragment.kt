package com.example.bello.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bello.R
import com.example.bello.activities.TaskActivity
import com.example.bello.adaptors.TaskItemAdaptor
import com.example.bello.databinding.FragmentDoingBinding
import com.example.bello.databinding.FragmentToDoBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Task
import com.example.bello.utils.Constants

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "boardID"
private const val ARG_PARAM2 = "createdBy"

/**
 * A simple [Fragment] subclass.
 * Use the [DoingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */



class DoingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var tcreatedBy:String?=null
    private var _binding: FragmentDoingBinding? = null
    lateinit var boardID:String
    lateinit var mCreatedBy:String

//    private var mBoardAssigned:ArrayList<String> = ArrayList()

    private val binding get() =_binding!!

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.e("RResult","${Activity.RESULT_OK} in Doing Fragment.")
            FirestoreClass().getTaskList(this,boardID)
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            // 处理取消操作
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            tcreatedBy = it.getString(ARG_PARAM2)
        }

        Log.d("DDDD","$param1")
        boardID = param1!!
        mCreatedBy = tcreatedBy!!

        Log.d("Board","${param1} is in Doing Board")

//        FirestoreClass().getAssignedMemberList(this, boardID)

        Log.d("III","I am after getAssignedMemberList")

        FirestoreClass().getTaskList(this,boardID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_doing, container, false)
        _binding = FragmentDoingBinding.inflate(inflater,container,false)
        val view = binding.root
        FirestoreClass().getTaskList(this,boardID)
        return view
    }

    fun PopulateTaskList(taskList:ArrayList<Task>){
        val noTaskMessage = binding.noTaskDoingMessage
        val recycleTaskList = binding.recycleTaskDoingList
        if(taskList.size > 0){
            noTaskMessage.visibility=View.GONE
            recycleTaskList.visibility = View.VISIBLE

            recycleTaskList.layoutManager = LinearLayoutManager(context)
            recycleTaskList.setHasFixedSize(true)
            val taskAdaptor = context?.let { TaskItemAdaptor(it,taskList) }
            recycleTaskList.adapter=taskAdaptor

            taskAdaptor?.setOnClickListener(object: TaskItemAdaptor.OnClickListener{
                override fun onClick(position: Int, model: Task) {
                    val intent = Intent(activity, TaskActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,boardID)
                    intent.putExtra("task_created",model.createdDate)
                    intent.putExtra(Constants.STATUS_FRAGMENT,"doing")
                    intent.putExtra(Constants.CREATED_BY,model.createdBy)
                    launcher.launch(intent)
                }

            })
        }else{
            recycleTaskList.visibility=View.GONE
            noTaskMessage.visibility=View.VISIBLE
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        Log.d("DAAA","$resultCode szzzeex")
//        if(resultCode == Activity.RESULT_OK){
//            if(requestCode == TASK_STATUS_CODE){
//                FirestoreClass().getTaskList(this,boardID)
//            }
//        }
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DoingFragment.
         */
        // TODO: Rename and change types and number of parameters

        const val TASK_STATUS_CODE:Int = 27

        @JvmStatic
        fun newInstance(boardID: String, createdBy: String) =
            DoingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, boardID)
                    putString(ARG_PARAM2, createdBy)
                }
            }
    }
}