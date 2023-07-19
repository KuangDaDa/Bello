package com.example.bello.adaptors

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bello.fragments.DoingFragment
import com.example.bello.fragments.ToDoFragment

class TaskListAdaptor(fragmentManager: FragmentManager, lifecycle: Lifecycle,boardDocumentID:String):
    FragmentStateAdapter(fragmentManager,lifecycle) {
    var boardDocumentID = boardDocumentID
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        if(position==0){
            return ToDoFragment.newInstance(boardDocumentID)
        }else if(position == 1){
            return DoingFragment()
        }else{
            return DoingFragment()
        }
    }

}