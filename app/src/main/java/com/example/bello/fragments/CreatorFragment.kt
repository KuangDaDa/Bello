package com.example.bello.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bello.R
import com.example.bello.activities.CreateBoardActivity
import com.example.bello.activities.TaskListActivity
import com.example.bello.adaptors.BoardItemsAdaptor
import com.example.bello.databinding.FragmentCreatorBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Board
import com.example.bello.model.Task
import com.example.bello.utils.Constants

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val  ARG_PARAM1 = "param2"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreatorFragment : Fragment() {
    private var _binding: FragmentCreatorBinding? = null
    private val binding get() =_binding!!
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
//        FirestoreClass().getBoardList(this)
    }


    fun populateBoardListToUI(boardList:ArrayList<Board>){
        val no_board_message = binding.noBoardMessage
        val recycle_board_list = binding.recycleBoardList
        if(boardList.size > 0){
            recycle_board_list.visibility=View.VISIBLE
            no_board_message.visibility=View.GONE
            recycle_board_list.layoutManager = LinearLayoutManager(context)
            recycle_board_list.setHasFixedSize(true)
            val boardAdapter = context?.let { BoardItemsAdaptor(it,boardList) }
            recycle_board_list.adapter=boardAdapter

            boardAdapter?.setOnClickListener(object:BoardItemsAdaptor.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(activity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    intent.putExtra(Constants.CREATED_BY, model.createdBy)
                    startActivityForResult(intent, TASK_LIST_REQUEST_CODE)
                }
            })
        }else{
            recycle_board_list.visibility=View.GONE
            no_board_message.visibility=View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreatorBinding.inflate(inflater,container,false)
//        return inflater.inflate(R.layout.fragment_creator, container, false)
//        FirestoreClass().getBoardList(this)
        val view = binding.root

        FirestoreClass().getBoardList(this)

        binding.fabCreateBoard.setOnClickListener{
            val intent  = Intent(requireContext(),CreateBoardActivity::class.java)
            startActivityForResult(intent, CREATED_BOARD_REQUEST)
        }
        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CREATED_BOARD_REQUEST || requestCode == TASK_LIST_REQUEST_CODE)
            FirestoreClass().getBoardList(this)
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
         * @return A new instance of fragment CreatorFragment.
         */
        // TODO: Rename and change types and number of parameters

        const val CREATED_BOARD_REQUEST:Int = 1
        const val TASK_LIST_REQUEST_CODE:Int = 2
        @JvmStatic
        fun newInstance() =
            CreatorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1,"")
                }
            }
    }
}