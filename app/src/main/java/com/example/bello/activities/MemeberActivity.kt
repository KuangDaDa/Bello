package com.example.bello.activities

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bello.R
import com.example.bello.adaptors.MemberListAdaptor
import com.example.bello.databinding.ActivityMemeberBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Board
import com.example.bello.model.User
import com.example.bello.utils.Constants

class MemeberActivity : AppCompatActivity() {
    private lateinit var mBoardDetail:Board
    private lateinit var binding_member:ActivityMemeberBinding
    private lateinit var mBoardAssignedMemberList:ArrayList<User>
    private var anyChangeMade: Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_member = ActivityMemeberBinding.inflate(layoutInflater)
        setContentView(binding_member.root)

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetail = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setUpActionBar()

        FirestoreClass().getMemberListDetails(this,mBoardDetail.assignedTo)
    }

    fun setUpMemberList(list:ArrayList<User>){
        mBoardAssignedMemberList = list

        binding_member.memberList.layoutManager = LinearLayoutManager(this)
        binding_member.memberList.setHasFixedSize(true)

        val adaptor = MemberListAdaptor(this,list)
        binding_member.memberList.adapter = adaptor

    }

    fun memberDetail(user:User){
        mBoardDetail.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this,mBoardDetail,user)
    }

    private fun setUpActionBar(){
        setSupportActionBar(binding_member.toolbarMember)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            actionBar.title=resources.getString(R.string.member)
        }

        binding_member.toolbarMember.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member->{
                Toast.makeText(this,"Add member si selected",Toast.LENGTH_SHORT).show()
                DialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun DialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        Log.d("AAA","Text in Dialog work")
        dialog.findViewById<TextView>(R.id.mb_add).setOnClickListener {
            val email = dialog.findViewById<EditText>(R.id.member_email_search).text.toString()
            if(email.isNotEmpty()){
                dialog.dismiss()
                FirestoreClass().getMemberDetail(this,email)
            }else
            {
                Toast.makeText(this,"Please enter member email address.",Toast.LENGTH_SHORT).show()
            }
        }
        dialog.findViewById<TextView>(R.id.mb_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if(anyChangeMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    fun memberAssignedSuccess(user:User){
        mBoardAssignedMemberList.add(user)
        anyChangeMade = true
        setUpMemberList(mBoardAssignedMemberList)
    }

}