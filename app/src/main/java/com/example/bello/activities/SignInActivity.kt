package com.example.bello.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.bello.databinding.ActivitySignInBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        emailListener()

        binding.signin.setOnClickListener{
            SignInRegisteredUser()
        }

        binding.linkSignin.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivityForResult(intent,1)
        }
    }

    fun signInSuccess(user:User){
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun SignInRegisteredUser(){
        val email:String = binding.signinEmailEditText.text.toString()
        val password:String = binding.signinPasswordEditText.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                   FirestoreClass().loadUserData(this)
                } else {
                    // If sign in fails, display a message to the user.
//                    Log.w("Sign in", "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        this,
//                        "Authentication failed.",
//                        Toast.LENGTH_SHORT,
//                    ).show()
                    binding.errorSignUpMsg.visibility = View.VISIBLE
                }
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1-> if(resultCode == RESULT_OK){
                val email = data?.getStringExtra("email")
                binding.signinEmailEditText.setText(email)
                binding.emailLayout.helperText=null
                binding.signinPasswordEditText.setText(data?.getStringExtra("password"))
                binding.passwordLayout.helperText=null
            }
        }
    }


    private fun emailListener(){
        binding.signinEmailEditText.setOnFocusChangeListener{_,focused->
            if(!focused){
                binding.emailLayout.helperText = validEmail()
            }
        }
    }

    private fun validEmail():String?{
        val email = binding.signinEmailEditText.text.toString()
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return "Invalid email address"
        }
        return null
    }
}