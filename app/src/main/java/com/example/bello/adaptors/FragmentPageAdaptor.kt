package com.example.bello.adaptors

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bello.firebase.FirestoreClass
import com.example.bello.fragments.CollabratedFragment
import com.example.bello.fragments.CreatorFragment

class FragmentPageAdaptor(fragmentManager: FragmentManager, lifecycle:Lifecycle):
FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if(position==0){
            CreatorFragment()
        }else{
            CollabratedFragment()
        }
    }

}