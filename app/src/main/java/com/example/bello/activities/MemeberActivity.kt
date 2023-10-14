package com.example.bello.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bello.R
import com.example.bello.adaptors.MemberListAdaptor
import com.example.bello.databinding.ActivityMemeberBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Board
import com.example.bello.model.User
import com.example.bello.utils.Constants
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.URL
import java.nio.Buffer

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

    @Deprecated("Deprecated in Java")
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

        SendNotificationToUserAsyncTask(mBoardDetail.name, user.fcmToken).execute()
        Log.d("WXX","Send Notification worl!!!!")
    }

    fun showMemberNotFoundDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("No such user found, please enter correct user email.")
            setIcon(R.drawable.baseline_error_outline_24)
            setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class SendNotificationToUserAsyncTask(val boardName:String, val token:String)
        :AsyncTask<Any,Void,String>(){

        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
        }
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg p0: Any?): String {
            var result:String
            var connection:HttpURLConnection?=null
            try {
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput=true
                connection.doInput=true
                connection.instanceFollowRedirects=false
                connection.requestMethod = "POST"

                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION,
                    "${Constants.FCM_KEY} = ${Constants.FCM_SERVER_KEY}"
                )

                connection.useCaches=false

                val wr = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()

                dataObject.put(Constants.FCM_KEY_TITLE,"Assigned to the board - $boardName")
                dataObject.put(Constants.FCM_KEY_MESSAGE,"You have been assigned to the board - $boardName by ${mBoardAssignedMemberList[0].name}")
                jsonRequest.put(Constants.FCM_KEY_DATA,dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO,token)

                Log.d("FCCM","$token sssssi")

                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                Log.d("WXXXX","workin in http request.")

                val httpResult:Int = connection.responseCode

                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))

                    val sb = StringBuilder()
                    var line:String?

                    try{
                        while(reader.readLine().also{line=it} != null){
                            sb.append(line+"\n")

                            Log.d("WXXXX","workin in http http ok.")
                        }
                    }catch(e:IOException){
                        e.printStackTrace()
                    }finally {
                        try{
                            inputStream.close()
                            Log.d("WXXXX","workin in http http close.")
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                        result = sb.toString()
                    }
                }else{
                    result = connection.responseMessage
                }

            }catch (e:SocketTimeoutException){
                result = "Connection timeout."
            }catch (e: Exception){
                result = "Error: "+e.message
            }finally {
                connection?.disconnect()
            }

            Log.d("WXXXX","workin in http resulttttt $result.")
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result != null) {
                Log.e("JSON Response Result", result)
            }
        }

    }
}