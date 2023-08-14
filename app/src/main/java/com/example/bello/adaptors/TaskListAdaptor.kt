package com.example.bello.adaptors

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bello.fragments.DoingFragment
import com.example.bello.fragments.DoneFragment
import com.example.bello.fragments.ToDoFragment
import com.example.bello.model.User

class TaskListAdaptor(fragmentManager: FragmentManager, lifecycle: Lifecycle,
                      var boardDocumentID: String, var mBoardCreatedBy: String
):
    FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        if(position==0){
            return ToDoFragment.newInstance(boardDocumentID, mBoardCreatedBy)
        }else if(position == 1){
            return DoingFragment.newInstance(boardDocumentID, mBoardCreatedBy)
        }else{
            return DoneFragment.newInstance(boardDocumentID, mBoardCreatedBy)
        }
    }

}