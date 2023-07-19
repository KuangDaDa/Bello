package com.example.bello.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.bello.R
import com.example.bello.adaptors.BoardItemsAdaptor
import com.example.bello.adaptors.FragmentPageAdaptor
import com.example.bello.firebase.FirestoreClass
import com.example.bello.fragments.CreatorFragment
import com.example.bello.model.Board
import com.example.bello.model.User
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : BaseActivity() ,NavigationView.OnNavigationItemSelectedListener{

    companion object{
        const val MY_PROFILE_REQUEST_CODE :Int=11
    }

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var adaptor: FragmentPageAdaptor
//    private var mUsername:String = FirestoreClass().getCurrentUser()

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setUpActionBar()


        val nav_view = findViewById<NavigationView>(R.id.nav_view)
        nav_view.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this)


        tabLayout = findViewById<ConstraintLayout>(R.id.main_content_tab).findViewById(R.id.tab_layout)
        viewPager2 = findViewById<ConstraintLayout>(R.id.main_content_tab).findViewById(R.id.viewPager2)

//        val mUsername = Firebase.auth.currentUser
//        Log.d("Username","$mUsername is here.")


        adaptor = FragmentPageAdaptor(supportFragmentManager,lifecycle)
        tabLayout.addTab(tabLayout.newTab().setText("Creator"))
        tabLayout.addTab(tabLayout.newTab().setText("Collaborated"))

        viewPager2.adapter = adaptor

        tabLayout.addOnTabSelectedListener(object:OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        viewPager2.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

//    fun populateBoardListToUI(boardList:ArrayList<Board>){
//        val no_board_message = findViewById<TextView>(R.id.no_board_message)
//        val recycle_board_list = findViewById<RecyclerView>(R.id.recycle_board_list)
//        if(boardList.size > 0){
//            recycle_board_list.visibility=View.VISIBLE
//            no_board_message.visibility=View.GONE
//            recycle_board_list.layoutManager = LinearLayoutManager(this)
//            recycle_board_list.setHasFixedSize(true)
//            val boardAdapter = BoardItemsAdaptor(this,boardList)
//            recycle_board_list.adapter=adaptor
//        }else{
//            recycle_board_list.visibility=View.GONE
//            no_board_message.visibility=View.VISIBLE
//        }
//    }


    private fun setUpActionBar(){
        val toolbar:Toolbar = findViewById<CoordinatorLayout>(R.id.toolbar).findViewById(R.id.toolbar_main_activity)
        Log.d("Toolbar","Setting up working......")
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }
//
//
    private fun toggleDrawer(){
        val drawer:DrawerLayout = findViewById(R.id.drawer_layout)
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        val drawer:DrawerLayout = findViewById(R.id.drawer_layout)
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
           dobleBackToExit()
        }
    }

    fun updateNavigationUserDetails(user:User){
//        adaptor.mUsername = user.name
//        Log.d("USERNAME","In updateNavigation ${adaptor.mUsername}")
//        adaptor.notifyDataSetChanged()
//        mUsername = user.name

        val nav_user_image: ImageView = findViewById(R.id.nav_user_image)
        val username: TextView = findViewById(R.id.bello_username)
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_nav_user)
            .into(nav_user_image)

        username.setText(user.name)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE ){
            FirestoreClass().loadUserData(this)
        }else{
            Log.e("Cancelled", "Cancelled.")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawer:DrawerLayout = findViewById(R.id.drawer_layout)
        when(item.itemId){
            R.id.nav_my_profile ->{
//                Toast.makeText(this, "MY PROFILE", Toast.LENGTH_SHORT).show()
                startActivityForResult(Intent(this,MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}