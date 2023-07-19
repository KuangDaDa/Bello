package com.example.bello.firebase

import android.app.Activity
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import com.example.bello.activities.CreateBoardActivity
import com.example.bello.activities.CreateTaskActivity
import com.example.bello.activities.MainActivity
import com.example.bello.activities.MyProfileActivity
import com.example.bello.activities.SignInActivity
import com.example.bello.activities.SignUpActivity
import com.example.bello.activities.TaskListActivity
import com.example.bello.fragments.CreatorFragment
import com.example.bello.fragments.ToDoFragment
import com.example.bello.model.Board
import com.example.bello.model.Task
import com.example.bello.model.User
import com.example.bello.utils.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.dataObjects
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity:SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisterSuccess()
            }
            .addOnFailureListener{
               e->
                Log.e(activity.javaClass.simpleName,"Error occuring in insert user document.")
            }
    }

    fun createBoard(activity:CreateBoardActivity, board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
//                activity.userRegisterSuccess()
                Log.d(activity.javaClass.simpleName,"board has been successfully created.")
                Toast.makeText(activity,"Board has been created successfully.",Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener{
                    e->
                Log.e(activity.javaClass.simpleName,"Error occuring in creationg board.")
            }
    }

    fun updateUserProfileData(activity:MyProfileActivity,userHashMap:HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Update profile successfully.")
                Toast.makeText(activity,"Update profile successfully.",Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener{
                e->
                Log.e(activity.javaClass.simpleName,"Failure on updating profile.")
                Toast.makeText(activity,"Update profile fail.",Toast.LENGTH_SHORT).show()
            }
    }

    fun loadUserData(activity:Activity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document->
                val loggedUser = document.toObject(User::class.java)!!

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedUser)
                    }
                    is MainActivity->{
                        Log.d("mUsername","Load in LoadUserData")
                        activity.updateNavigationUserDetails(loggedUser)
                    }
                    is MyProfileActivity->{
                        activity.setUserDataInProfile(loggedUser)
                    }
                }

            }
            .addOnFailureListener{
                    e->
                Log.e(activity.javaClass.simpleName,"Error occuring in insert user document.")
            }
    }

    fun getCurrentUserId():String{
        var currentUser = Firebase.auth.currentUser
        var currentUserID=""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
//        return Firebase.auth.currentUser!!.uid
//        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun getBoardList(activity: CreatorFragment){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains("assignedTo",getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName,document.documents.toString())
                val boardList:ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    Log.d("Board","Hello this is board.")
                    val board = i.toObject(Board::class.java)
                    board!!.documentId = i.id
                    boardList.add(board)
                    Log.d("Board","${board.documentId} is here.")
                }
                activity.populateBoardListToUI(boardList)
            }
            .addOnFailureListener{
                e->
                Log.e(activity.javaClass.simpleName,"Error with get board lists")
            }
    }

    fun getBoardDetails(activity:TaskListActivity,documentId:String){
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName,document.toString())
                val board = document.toObject(Board::class.java)
                if (board != null) {
                    board.documentId = document.id
                }
                activity.boardDetail(board)
            }
            .addOnFailureListener{
                    e->
                Log.e(activity.javaClass.simpleName,"Error with get board lists")
            }
    }

    fun boardDetail(activity:CreateTaskActivity,documentId: String){
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i("BoardDetail","In the boardDetail firestore.")
                val board = document.toObject(Board::class.java)
                if (board != null) {
                    board.documentId = document.id
                }
                if (board != null) {
                      activity.getBoardDetail(board)
                }
            }
            .addOnFailureListener{
                    e->
                Log.e("BoardDetail","Error with get board lists")
            }
    }

    fun createTask(activity:CreateTaskActivity,board:Board){
        val taskListHashMap= HashMap<String,Any>()
        taskListHashMap[Constants.TASK_LIST]= board.taskList
        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"task create successfully.")
                Toast.makeText(activity,"create task successfully.",Toast.LENGTH_SHORT).show()
                activity.getBoardDetail(board)
            }
            .addOnFailureListener{
                Log.e(activity.javaClass.simpleName,"Error with get board lists")
            }
    }

    fun createTasks(activity:CreateTaskActivity,board:Board,task: Task){
//        val taskListHashMap= HashMap<String,Any>()
//        taskListHashMap[Constants.TASK_LIST]= board.taskList[0]
        val ref = mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
        ref.update("taskList",FieldValue.arrayUnion(task))
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"task create successfully.")
                Toast.makeText(activity,"create task successfully.",Toast.LENGTH_SHORT).show()
                activity.getBoardDetail(board)
            } .addOnFailureListener{
                Log.e(activity.javaClass.simpleName,"Error with get board lists")
            }
//            .update(taskListHashMap)
//            .addOnSuccessListener {
//                Log.e(activity.javaClass.simpleName,"task create successfully.")
//                Toast.makeText(activity,"create task successfully.",Toast.LENGTH_SHORT).show()
//                activity.getBoardDetail(board)
//            }
//            .addOnFailureListener{
//                Log.e(activity.javaClass.simpleName,"Error with get board lists")
//            }
    }


    //    fun creatTask(activity: CreatorFragment,task: Task, board: Board){
//        mFireStore.collection(Constants.BOARDS)
//            .document(board.documentId)
//            .set
//    }

// get task list
    fun getTaskList(activity:ToDoFragment,boardID:String){
        Log.d("Board","$boardID si here.")
        mFireStore.collection(Constants.BOARDS)
            .document(boardID)
            .get()
            .addOnSuccessListener {
                document->
                var taskLister:ArrayList<Task> = ArrayList()
                if(document.exists()){
                    val taskList = document.get("taskList") as ArrayList<*>
                    for(taskObject in taskList){
                        if(taskObject is Map<*,*>){
                            val task = (taskObject["description"] as? String)?.let {
                                (taskObject["dueDate"] as? Long)?.let { it1 ->
                                    Task(
                                        taskObject["title"] as String,
                                        it,
                                        it1,
                                        taskObject["createdBy"] as String,
                                        )
                                }
                            }
                            if (task != null) {
                                taskLister.add(task)
                            }
                        }
                    }
                }
                Log.d("Task","$taskLister si here.")
                activity.PopulateTaskList(taskLister)
            }.addOnFailureListener{
                    e->
                Log.e(activity.javaClass.simpleName,"Error with get board lists")
            }
//        .whereArrayContains("assignedTo",getCurrentUserId())
//            .whereArrayContains("task_list",getCurrentUserId())
//        .get()
//        .addOnSuccessListener {
//                document ->
//            Log.i(activity.javaClass.simpleName,document.documents.toString())
//            val taskList:ArrayList<Task> = ArrayList()
////            for(i in document.documents){
////                Log.d("Board","Hello this is board.")
////                val task = i.toObject(Task::class.java)
////                task = i.id
////                taskList.add(task)
////                Log.d("Board","${task.documentId} is here.")
////            }
//            for(i in document){
//                Log.d("Document","${i.getDocumentReference("task_list")} si")
////                val data = document.data
////                for(i in data!!)
////                {
////                    Log.d("Data","${data["task_list"]}")
////                }
//            }
//            activity.PopulateTaskList(taskList)
//        }
//        .addOnFailureListener{
//                e->
//            Log.e(activity.javaClass.simpleName,"Error with get board lists")
//        }
    }

    fun getCurrentUser():String{
        var mUsername:String=""
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedUser = document.toObject(User::class.java)!!
                mUsername= loggedUser.name
                Log.d("mUsername", "$mUsername in the success mode.")
            }.addOnFailureListener{
                Log.e("mUserename","$mUsername in the failure mode.")
            }
        Log.d("mUsername"," is the result.")
        return mUsername
    }
}