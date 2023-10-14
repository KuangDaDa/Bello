package com.example.bello.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bello.activities.CreateBoardActivity
import com.example.bello.activities.CreateTaskActivity
import com.example.bello.activities.MainActivity
import com.example.bello.activities.MemeberActivity
import com.example.bello.activities.MyProfileActivity
import com.example.bello.activities.SignInActivity
import com.example.bello.activities.SignUpActivity
import com.example.bello.activities.TaskActivity
import com.example.bello.activities.TaskListActivity
import com.example.bello.activities.UpdateTaskActivity
import com.example.bello.dialog.MemberListDialog
import com.example.bello.fragments.CollabratedFragment
import com.example.bello.fragments.CreatorFragment
import com.example.bello.fragments.DoingFragment
import com.example.bello.fragments.DoneFragment
import com.example.bello.fragments.ToDoFragment
import com.example.bello.model.Board
import com.example.bello.model.SelectedMembers
import com.example.bello.model.Task
import com.example.bello.model.User
import com.example.bello.utils.Constants
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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

    fun updateUserProfileData(activity:Activity,userHashMap:HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Update profile successfully.")
                Toast.makeText(activity,"Update profile successfully.",Toast.LENGTH_SHORT).show()
                when(activity){
                    is MainActivity ->{
                        activity.tokenUpdateSuccess()
                    }
                    is MyProfileActivity->{
                        activity.profileUpdateSuccess()
                    }
                }
            }.addOnFailureListener{
                e->
                Log.e(activity.javaClass.simpleName,"Failure on updating profile.")
                Toast.makeText(activity,"Update profile fail.",Toast.LENGTH_SHORT).show()
            }
    }

    fun loadUserData(activity:Activity,readBoardsList: Boolean = false){
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
                        activity.updateNavigationUserDetails(loggedUser,true)
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
    }

    fun getBoardList(activity: CreatorFragment){
        mFireStore.collection(Constants.BOARDS)
            .whereEqualTo("createdBy",getCurrentUserId())
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

//    fun createTask(activity:CreateTaskActivity,board:Board){
//        val taskListHashMap= HashMap<String,Any>()
//        taskListHashMap[Constants.TASK_LIST]= board.taskList
//        mFireStore.collection(Constants.BOARDS)
//            .document(board.documentId)
//            .update(taskListHashMap)
//            .addOnSuccessListener {
//                Log.e(activity.javaClass.simpleName,"task create successfully.")
//                Toast.makeText(activity,"create task successfully.",Toast.LENGTH_SHORT).show()
//                activity.getBoardDetail(board)
//            }
//            .addOnFailureListener{
//                Log.e(activity.javaClass.simpleName,"Error with get board lists")
//            }
//    }

    fun createTasks(activity:CreateTaskActivity,board:Board,task: Task){
//        val taskListHashMap= HashMap<String,Any>()
//        taskListHashMap[Constants.TASK_LIST]= board.taskList[0]
        val ref = mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
        ref.update("taskList",FieldValue.arrayUnion(task))
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"task create successfully.")
                Toast.makeText(activity,"create task successfully.",Toast.LENGTH_SHORT).show()
//                activity.getBoardDetail(board)
                activity.taskCreatedSuccessfully()
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

    // corret one
// get task list
//    fun getTaskList(activity:ToDoFragment,boardID:String){
//        Log.d("Board","$boardID si here.")
//        mFireStore.collection(Constants.BOARDS)
//            .document(boardID)
//            .get()
//            .addOnSuccessListener {
//                document->
//                val taskLister:ArrayList<Task> = ArrayList()
//                if(document.exists()){
//                    val taskList = document.get("taskList") as ArrayList<*>
//                    for(taskObject in taskList){
//                        if(taskObject is Map<*,*>){
//                            val task = (taskObject["description"] as? String)?.let {
//                                (taskObject["dueDate"] as? Long)?.let { it1 ->
//                                    Task(
//                                        taskObject["title"] as String,
//                                        it,
//                                        it1,
//                                        taskObject["createdBy"] as String,
//                                        taskObject["createdDate"] as Long,
//                                        taskObject["task_status"] as String,
//                                        taskObject["task_priority"] as String
//                                        )
//                                }
//                            }
//                            if (task != null) {
//                                if(task.task_status.equals("TODO"))
//                                    taskLister.add(task)
//                            }
//                        }
//                    }
//                }
//                Log.d("Task","$taskLister si here.")
//                activity.PopulateTaskList(taskLister)
//            }.addOnFailureListener{
//                    e->
//                Log.e(activity.javaClass.simpleName,"Error with get board lists")
//            }
//    }

    fun getTaskList(activity:Fragment,boardID:String) {
        Log.d("Board", "$boardID si here.")
        mFireStore.collection(Constants.BOARDS)
            .document(boardID)
            .get()
            .addOnSuccessListener { document ->
                val taskLister: ArrayList<Task> = ArrayList()
                val taskDoing: ArrayList<Task> = ArrayList()
                val taskDone: ArrayList<Task> = ArrayList()
                if (document.exists()) {
                    val taskList = document.get("taskList") as ArrayList<*>
                    for (taskObject in taskList) {
                        if (taskObject is Map<*, *>) {
                            val task = (taskObject["description"] as? String)?.let {
                                (taskObject["dueDate"] as? Long)?.let { it1 ->
                                    Task(
                                        taskObject["title"] as String,
                                        it,
                                        it1,
                                        taskObject["createdBy"] as String,
                                        taskObject["createdDate"] as Long,
                                        taskObject["task_status"] as String,
                                        taskObject["task_priority"] as String
                                    )
                                }
                            }
                            if (task != null) {
                                when(task.task_status) {
                                    "TODO" -> {
                                        taskLister.add(task)
                                    }

                                    "DOING" -> {
                                        taskDoing.add(task)
                                    }

                                    "DONE" -> {
                                        taskDone.add(task)
                                    }
                                }
                            }
                        }
                    }
                }
                Log.d("Task", "$taskLister si here.")
                when(activity){
                    is ToDoFragment->{
                        activity.PopulateTaskList(taskLister)
                    }
                    is DoingFragment->{
                        activity.PopulateTaskList(taskDoing )
                    }
                    is DoneFragment->{
                        Log.d("DONe","$activity ssszzzz")
                        activity.PopulateTaskList(taskDone)
                    }
                }

            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error with get board lists")
            }
    }

    fun getTaskDetail(activity:Activity,boardID:String,createdDate:Long) {
        mFireStore.collection(Constants.BOARDS)
            .document(boardID)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.d("AAA","$boardID si here.")
                if(document.exists()){
                    val taskList = document.get("taskList") as ArrayList<HashMap<*,*>>
                    for(taskObject in taskList){
                        val checkDate = taskObject["createdDate"] as Long
                        if( checkDate.equals(createdDate)){
                            val assignedMembersData =
                                (taskObject["assignedMembers"] as ArrayList<HashMap<String, Any>>?) ?: ArrayList()
                            val assignedMembersList: ArrayList<SelectedMembers> = ArrayList()

                            for (memberData in assignedMembersData) {
                                val member = SelectedMembers(
                                    id = memberData["id"].toString(),
                                    image = memberData["image"].toString(),
                                    email = memberData["email"].toString()
                                )
                                assignedMembersList.add(member)
                            }
                            val task = Task(
                                taskObject["title"] as String,
                                taskObject["description"] as String,
                                taskObject["dueDate"] as Long,
                                taskObject["createdBy"] as String,
                                taskObject["createdDate"] as Long,
                                taskObject["task_status"] as String,
                                taskObject["task_priority"] as String,
                                (taskObject["task_order"] as Long).toInt(),
                                taskObject["fileAttachment"] as String,
                                assignedMembersList,
                            )
                            when(activity){
                                is TaskActivity ->{
                                    Log.d("TTTT","I am in Task activity")
                                    activity.taskDetail(task)
                                }
                                is UpdateTaskActivity ->{
                                    Log.d("TTTT","I am in Task update")
                                    activity.updateTask(task)
                                }
                            }
//                            activity.taskDetail(task)
//                            Log.d("EEE","$task si si.")
                            break
                        }else{
                            Log.d("BBB","$boardID si here.")
                        }
//                        if(taskObject is Map<*,*>){
//                            var task =Task(
//                                    taskObject["title"] as String,
//                                    taskObject["description"] as String,
//                                    taskObject["dueDate"] as Long,
//                                    taskObject["createdBy"] as String,
//                                    taskObject["createdDate"] as Long
//                                    )
//
//                            if(task.createdDate.equals(createdDate)){
//                                Log.d("CCC","CreatedDate work!!!")
//                            }
//                        }
                    }

                }

//                Log.i("BoardDetail","In the boardDetail firestore.")
//                val board = document.toObject(Board::class.java)
//                if (board != null) {
//                    board.documentId = document.id
//                }
//                if (board != null) {
//                    activity.getBoardDetail(board)
//                }
            }
            .addOnFailureListener{
                    e->
                Log.e("BoardDetail","Error with get board lists")
            }
    }

    fun getMemberListDetails(activity:Activity,assignedTo:ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.USER_ID,assignedTo)
            .get()
            .addOnSuccessListener {
                document ->
                Log.d(activity.javaClass.simpleName,document.documents.toString())

                val userList:ArrayList<User> = ArrayList()
                for(i in document.documents){
                    val user=i.toObject(User::class.java)!!
                    userList.add(user)
                }
                if(activity is MemeberActivity){
                    activity.setUpMemberList(userList)
                }
                else if(activity is TaskListActivity){
                    activity.boardMembersDetailsList(userList)
                }else if(activity is CreateTaskActivity){
                    activity.getBoardAssignedList(userList)
                }
            }
            .addOnFailureListener{
                Log.e(activity.javaClass.simpleName,"Error while fetching userList.")
            }
    }

    fun getMemberDetail(activity:MemeberActivity,email:String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {
                document->
                if(document.documents.size > 0){
                    val user = document.documents[0].toObject(User::class.java)!!
                    activity.memberDetail(user)
                }else{
                    activity.showMemberNotFoundDialog()
                }
            }
    }

    fun assignMemberToBoard(activity: MemeberActivity,board:Board,user:User){

        val assignedToHashMap = HashMap<String,Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignedSuccess(user)
            }
            .addOnFailureListener{
                Log.e("MemXX","Add member to assign Failed.")
            }
    }

    fun getAssignedMemberList(fragment:ToDoFragment,boardID: String){
//        val assignedToHashMap = HashMap<String,Any>()
//        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFireStore.collection(Constants.BOARDS)
            .document(boardID)
            .get()
            .addOnSuccessListener {
                document->
                if(document.exists()){
                    val taskList = document.get("assignedTo") as ArrayList<*>
                    for(task in taskList){
                        Log.d("XXX", task.toString())
                        fragment.assignedMember(task.toString())
                    }
//                    for(taskObject in taskList){
//                        val checkDate = taskObject["createdDate"] as Long
//                        Log.d("AAA","$checkDate si here.")
//                        Log.d("DDD","$createdDate si here.")
//                        if( checkDate.equals(createdDate)){
//                            var task = Task(
//                                taskObject["title"] as String,
//                                taskObject["description"] as String,
//                                taskObject["dueDate"] as Long,
//                                taskObject["createdBy"] as String,
//                                taskObject["createdDate"] as Long,
//                                taskObject["task_status"] as String,
//                                taskObject["task_priority"] as String,
////                                (taskObject["task_order"]).toInt() as Int,
////                                taskObject["assignedMembers"] as ArrayList<SelectedMembers>,
////                                taskObject["fileAttachment"] as String,
//                            )
//                    Log.e("AAA","No such email")
            } else {
                    Log.e("AAA","No such assigned list")
                }
            }
            .addOnFailureListener{
                Log.e("EEE","Failure on assgined memeber.")
            }
    }

    fun getAssignedBoard(fragment:CollabratedFragment){
        val currentUser = getCurrentUserId()
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains("assignedTo",currentUser)
            .get()
            .addOnSuccessListener {
                    document->
                val boardList:ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    var board=i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    if(board.createdBy != currentUser){
                        boardList.add(board)
                    }
                }
                Log.d("ZZZZ","$boardList si here.")
                fragment.populateBoardListToUI(boardList)
            }.addOnFailureListener{
                    e->
                Log.e(fragment.javaClass.simpleName,"Error with get assigend board lists")
            }
        }

    fun deleteBoard(activity:TaskListActivity,boardID:String){
        mFireStore.collection(Constants.BOARDS)
            .document(boardID)
            .delete()
            .addOnSuccessListener {
               Log.d("DELETE","You have successfully delete the board.")
            }
            .addOnFailureListener{
                Log.d("DELETE","You have failed to delete the board.")
            }
    }

//    fun updateTask(activity:UpdateTaskActivity,boardID:String,taskCreated:Long,task:Task){
//        val ref=mFireStore.collection(Constants.BOARDS)
//            .document(boardID)
//       val updateTask = HashMap<String,Any>()
//        updateTask["title"] = task.title
//        updateTask["description"] = task.description
//        updateTask["task_priority"] = task.task_priority
//        updateTask["dueDate"] = task.dueDate
//
//
//        Log.d("UTask","SSSSSSSSSSSSSSSSSSS")
//        mFireStore.collection(Constants.BOARDS)
//            .document(boardID)
//            .get()
//            .addOnSuccessListener { document ->
//                Log.d("AAA", "$boardID si here.")
//                if (document.exists()) {
//                    val taskList = document.get("taskList") as ArrayList<HashMap<*, *>>
//                    val index = taskList.indexOfFirst { it["createdDate"] == task.createdDate }
//                    if(index != -1){
//                        taskList[index] = updateTask
//                        ref.update("taskList",taskList)
//                            .addOnSuccessListener {
//                               Log.d("UTask","Task update successfully..")
//                            }
//                            .addOnFailureListener{
//                                Log.d("UTask","Task update Failuere...")
//                            }
//                    }
//                }
//            }
//
////        val resultArray = documentSnapshot.get("taskList")
//
////        val ref = mFireStore.collection(Constants.BOARDS)
////            .document(board.documentId)
////        ref.update("taskList",FieldValue.arrayUnion(task))
////            .addOnSuccessListener {
////                Log.e(activity.javaClass.simpleName,"task create successfully.")
////                Toast.makeText(activity,"create task successfully.",Toast.LENGTH_SHORT).show()
//////                activity.getBoardDetail(board)
////                activity.taskCreatedSuccessfully()
////            } .addOnFailureListener{
////                Log.e(activity.javaClass.simpleName,"Error with get board lists")
////            }
//    }

//    fun updateTask(activity: UpdateTaskActivity, boardID: String, taskCreated: Long, task: Task) {
//        val ref = mFireStore.collection(Constants.BOARDS).document(boardID)
//        val updateTask = HashMap<String, Any>()
//        updateTask["title"] = task.title
//        updateTask["description"] = task.description
//        updateTask["dueDate"] = task.dueDate
//        updateTask["createdDate"] = task.createdDate
//        updateTask["createdBy"] = task.createdBy
//        updateTask["task_order"]= task.task_order
//        updateTask["task_status"] = task.task_status
//        updateTask["task_priority"] = task.task_priority
//        updateTask["fileAttachment"] = task.fileAttachment
//        updateTask["assignedMembers"] = task.assignedMembers
//
//        mFireStore.collection(Constants.BOARDS)
//            .document(boardID)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val taskList = document.get("taskList") as? ArrayList<HashMap<*, *>>
//                    if (taskList != null) {
//                        val index = taskList.indexOfFirst { it["createdDate"] == taskCreated }
//                        if (index != -1) {
//                            taskList[index] = updateTask
//                            ref.update("taskList", taskList)
//                                .addOnSuccessListener {
//                                    Log.d("UTask", "Task updated successfully.")
//                                }
//                                .addOnFailureListener {
//                                    Log.d("UTask", "Task update failure.")
//                                }
//                        }
//                    }
//                }
//            }
//            .addOnFailureListener {
//                Log.d("UTask", "Error fetching document.")
//            }
//    }

//    fun updateTask(activity: UpdateTaskActivity, boardID: String, taskCreated:Long, updatedTask: Task) {
//        val boardRef = mFireStore.collection(Constants.BOARDS).document(boardID)
//
//        boardRef.get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val taskList = document.get("taskList") as? ArrayList<HashMap<*, *>>
//                    val index = taskList?.indexOfFirst { it["createdDate"] == taskCreated }
//                    if (index != null) {
//                        taskList?.set(index, hashMapOf(
//                            "title" to updatedTask.title,
//                            "description" to updatedTask.description,
//                            "dueDate" to updatedTask.dueDate,
//                            "createdDate" to updatedTask.createdDate,
//                            "createdBy" to updatedTask.createdBy,
//                            "task_order" to updatedTask.task_order,
//                            "task_status" to updatedTask.task_status,
//                            "task_priority" to updatedTask.task_priority,
//                            "fileAttachment" to updatedTask.fileAttachment,
//                            "assignedMembers" to updatedTask.assignedMembers
//                        )
//                        )
//                    }
//
//                        boardRef.update("taskList", taskList)
//                            .addOnSuccessListener {
//                                Log.d("UTask", "Task updated successfully.")
//                            }
//                            .addOnFailureListener {
//                                Log.d("UTask", "Task update failure.")
//                            }
//                }
//            }
//            .addOnFailureListener {
//                Log.d("UTask", "Error fetching document.")
//            }
//    }

    fun updateTask(activity: UpdateTaskActivity, boardID: String, taskCreated: Long, updatedTask: Task) {
        val boardRef = mFireStore.collection(Constants.BOARDS).document(boardID)

        boardRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val taskList = document.get("taskList") as? ArrayList<HashMap<*, *>>
                    val index = taskList?.indexOfFirst { it["createdDate"] == taskCreated }
                    Log.d("Index","$index sssssss")
                    if (index != null) {
                        taskList[index] = hashMapOf(
                            "title" to updatedTask.title,
                            "description" to updatedTask.description,
                            "dueDate" to updatedTask.dueDate,
                            "createdDate" to updatedTask.createdDate,
                            "createdBy" to updatedTask.createdBy,
                            "task_order" to updatedTask.task_order,
                            "task_status" to updatedTask.task_status,
                            "task_priority" to updatedTask.task_priority,
                            "fileAttachment" to updatedTask.fileAttachment,
                            "assignedMembers" to updatedTask.assignedMembers
                        )

                        boardRef.update("taskList", taskList)
                            .addOnSuccessListener {
                                Log.d("UTask", "Task updated successfully.")
                                activity.updateSuccessfully()
                            }
                            .addOnFailureListener {
                                Log.d("UTask", "Task update failure.")
                            }
                    }
                }
            }
            .addOnFailureListener {
                Log.d("UTask", "Error fetching document.")
            }
    }

    fun deleteTask(activity: TaskActivity, boardID: String, taskCreated: Long) {
        val boardRef = mFireStore.collection(Constants.BOARDS).document(boardID)

        boardRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val taskList = document.get("taskList") as? ArrayList<HashMap<*, *>>
                    val index = taskList?.indexOfFirst { it["createdDate"] == taskCreated }
                    if (index != null) {
                        taskList.removeAt(index)

                        boardRef.update("taskList", taskList)
                            .addOnSuccessListener {
                                Log.d("UTask", "Task deleted successfully.")
                                activity.deleteTaskSuccessfully()
                            }
                            .addOnFailureListener {
                                Log.d("UTask", "Task deletion failure.")
                            }
                    }
                }
            }
            .addOnFailureListener {
                Log.d("UTask", "Error fetching document.")
            }
    }

    fun updateTaskStatus(activity: TaskActivity, boardID: String, taskCreated: Long, updatedTask: Task) {
        val boardRef = mFireStore.collection(Constants.BOARDS).document(boardID)

        boardRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val taskList = document.get("taskList") as? ArrayList<HashMap<*, *>>
                    val index = taskList?.indexOfFirst { it["createdDate"] == taskCreated }
                    Log.d("Index","$index sssssss")
                    if (index != null) {
                        taskList[index] = hashMapOf(
                            "title" to updatedTask.title,
                            "description" to updatedTask.description,
                            "dueDate" to updatedTask.dueDate,
                            "createdDate" to updatedTask.createdDate,
                            "createdBy" to updatedTask.createdBy,
                            "task_order" to updatedTask.task_order,
                            "task_status" to updatedTask.task_status,
                            "task_priority" to updatedTask.task_priority,
                            "fileAttachment" to updatedTask.fileAttachment,
                            "assignedMembers" to updatedTask.assignedMembers
                        )

                        boardRef.update("taskList", taskList)
                            .addOnSuccessListener {
                                Log.d("UTask", "Task status updated successfully.")
                                activity.updateStatusSuccessfully()
                            }
                            .addOnFailureListener {
                                Log.d("UTask", "Task status update failure.")
                            }
                    }
                }
            }
            .addOnFailureListener {
                Log.d("UTask", "Error fetching document.")
            }
    }
}