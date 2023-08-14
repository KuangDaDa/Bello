package com.example.bello.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.bello.R
import com.example.bello.databinding.ActivityCreateBoardBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.Board
import com.example.bello.utils.Constants
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.util.Calendar
import java.util.zip.Inflater

class CreateBoardActivity : AppCompatActivity() {
    private lateinit var binding_board: ActivityCreateBoardBinding
    private var mSelectedImageFileUri: Uri?=null
    private var mSelectedBoardImageURL:String=""

    // need to solve
    private var mUsername:String=FirestoreClass().getCurrentUserId()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding_board = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding_board.root)

        setUpActionBar()

        val board_image: ImageView = binding_board.createBoardImg
        board_image.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        binding_board.createBoardBtn.setOnClickListener{
            if(mSelectedBoardImageURL != null){
                uploadBardImage()
            }else{
                createBoard()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBoard(){
        val assignedUsersArrayList:ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(FirestoreClass().getCurrentUserId())

        val calendar = Calendar.getInstance().time.toInstant().toEpochMilli()

        var board = Board(
            binding_board.createBoardName.text.toString(),
            mSelectedBoardImageURL,
            mUsername,
            calendar,
            assignedUsersArrayList,
        )
        FirestoreClass().createBoard(this,board)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadBardImage(){
        val sRef: StorageReference? = Firebase.storage
            .reference.child("BOARD_IMAGE"+System.currentTimeMillis()
                    +"."+Constants.getFileExtension(this,mSelectedImageFileUri))

        sRef?.putFile(mSelectedImageFileUri!!)?.addOnSuccessListener { taskSnapshot ->
            Log.i(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.i("Downloadable Image URL", uri.toString())
                mSelectedBoardImageURL = uri.toString()
                createBoard()
            }
        }?.addOnFailureListener{ exception->
            Toast.makeText(this,
                exception.message,Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpActionBar(){
        val toolbar = binding_board.toolbarCreateBoard
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24)
            actionBar.title= resources.getString(R.string.create_board_title)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //To Show a image choose
                Log.d("Permission","Permission allowed.")
                Constants.showImageChooser(this)
            }else{
                Log.d("Permission","Permission denied.")
                Toast.makeText(this,
                    "Oops, You just deny the permission, the app can not access to your storage.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val board_image: ImageView = binding_board.createBoardImg
        Log.d("MYProfile","Error has occured.")
        if (resultCode == Activity.RESULT_OK &&
            requestCode == Constants.PICK_IMAGE_REQUEST_CODE &&
            data!!.data != null){
            mSelectedImageFileUri=data.data
            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.baseline_add_24)
                    .into(board_image)
            }catch (e: IOException){
                Log.d("MYProfile","Error has occured.")
                e.printStackTrace()
            }
        }
    }

    fun boardCreatedSuccessfully(){
        setResult(Activity.RESULT_OK)
        finish()
    }
}