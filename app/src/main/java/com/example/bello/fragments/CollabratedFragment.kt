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
import com.example.bello.activities.InvitedTaskList
import com.example.bello.activities.TaskListActivity
import com.example.bello.adaptors.BoardItemsAdaptor
import com.example.bello.databinding.FragmentCollabratedBinding
import com.example.bello.databinding.FragmentCreatorBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Board
import com.example.bello.utils.Constants

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CollabratedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CollabratedFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val binding get() =_binding!!
    private var _binding: FragmentCollabratedBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCollabratedBinding.inflate(inflater,container,false)
        val view = binding.root
        FirestoreClass().getAssignedBoard(this)
        return view
    }

    fun populateBoardListToUI(boardList:ArrayList<Board>) {
        val no_board_message = binding.assigendNoBoardMessage
        val recycle_board_list = binding.assignedRecycleBoardList
        if (boardList.size > 0) {
            recycle_board_list.visibility = View.VISIBLE
            no_board_message.visibility = View.GONE
            recycle_board_list.layoutManager = LinearLayoutManager(context)
            recycle_board_list.setHasFixedSize(true)
            val boardAdapter = context?.let { BoardItemsAdaptor(it, boardList) }
            recycle_board_list.adapter = boardAdapter

            boardAdapter?.setOnClickListener(object : BoardItemsAdaptor.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(activity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    intent.putExtra(Constants.CREATED_BY, model.createdBy)
                    Log.d("GGG","${model}")
                    startActivity(intent)
                }
            })
        } else {
            recycle_board_list.visibility = View.GONE
            no_board_message.visibility = View.VISIBLE
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CollabratedFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CollabratedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}