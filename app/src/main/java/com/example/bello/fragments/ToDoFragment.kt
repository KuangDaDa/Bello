package com.example.bello.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bello.R
import com.example.bello.activities.CreateTaskActivity
import com.example.bello.activities.TaskActivity
import com.example.bello.adaptors.BoardItemsAdaptor
import com.example.bello.adaptors.TaskItemAdaptor
import com.example.bello.databinding.FragmentToDoBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Board
import com.example.bello.model.Task
import com.example.bello.utils.Constants

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [ToDoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ToDoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var _binding:FragmentToDoBinding? = null
    lateinit var BoardID:String

    private val binding get() =_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
        BoardID = param1!!
        Log.d("Board","${param1} is in ToDo Board")

        FirestoreClass().getTaskList(this,BoardID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentToDoBinding.inflate(inflater,container,false)
        val view = binding.root
        binding.fabCreateTask.setOnClickListener{
            val intent = Intent(requireContext(),CreateTaskActivity::class.java)
            intent.putExtra(com.example.bello.utils.Constants.DOCUMENT_ID,param1)
            startActivity(intent)
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
                    Log.d("Tssk","You clicked the task.")
                    val intent = Intent(activity,TaskActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,BoardID)
                    startActivity(intent)
                }

            })
        }else{
            recycleTaskList.visibility=View.GONE
            noTaskMessage.visibility=View.VISIBLE
        }
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
        @JvmStatic
        fun newInstance(param1: String) =
            ToDoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}