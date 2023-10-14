package com.example.bello.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.bello.databinding.ActivitySignUpBinding
import com.example.bello.firebase.FirestoreClass
import com.example.bello.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    private lateinit var binding:ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usernameListener()
        emailListener()
        passwordListener()

        binding.signUpBtn.setOnClickListener{
            registerUser()
        }
    }

    private fun usernameListener(){
        binding.signupUsernameEditText.setOnFocusChangeListener{_,focused->
            if(!focused){
                Log.d("Username","On working..")
                binding.username.helperText=validUsername()
            }else
            {
                binding.username.helperText=null
            }
        }
    }

    private fun validUsername():String?{
        val username = binding.signupUsernameEditText.text.toString()
        if(username.isEmpty()){
            return "Username can not be empty."
        }else if(username.length <= 5){
            return "At least 5 letters."
        }
        return null
    }

    private fun emailListener(){
        binding.signupEmailEditText.setOnFocusChangeListener{_,focused->
            if(!focused){
                binding.email.helperText=validEmail()
            }else
            {
                binding.email.helperText=null
            }
        }
    }

    private fun validEmail():String?{
        val emailText = binding.signupEmailEditText.text.toString()
        if(emailText.isEmpty()){
            return "Email can not be empty."
        }else if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            return "Invalid email address."
        }
        return null
    }

    private fun passwordListener(){
        binding.signupPasswordEditText.setOnFocusChangeListener{_,focused->
            if(!focused){
                binding.password.helperText=validPassword()
            }else{
                binding.password.helperText=null
            }
        }
    }

    private fun validPassword():String?{
        val password = binding.signupPasswordEditText.text.toString()
        if(password.isEmpty()){
            return "Password can not be empty."
        }else if(password.length <= 6){
            return "At least 6 letter."
        }
        return null
    }

    fun userRegisterSuccess(){
        Toast.makeText(this,
            "You have been successfully register.",
            Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    private fun registerUser(){
        val validUsername = binding.username.helperText==null
        val validPassword = binding.password.helperText == null
        val validEmail = binding.email.helperText == null

        val username = binding.signupUsernameEditText.text.toString()
        val email = binding.signupEmailEditText.text.toString()
        val password = binding.signupPasswordEditText.text.toString()

        if(validUsername && validEmail && validPassword){
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registerEmail = firebaseUser.email!!
                        val intent = Intent()
                        intent.putExtra("email", email)
                        intent.putExtra("password", password)
                        setResult(RESULT_OK, intent)
                        val user = User(firebaseUser.uid, username, registerEmail,)
                        FirestoreClass().registerUser(this, user)
                        finish()
                    } else {
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                        binding.email.helperText="The email has been registered, please use another."
                    }
                }
        }else{
            Toast.makeText(this,"Sign up failed !!",Toast.LENGTH_LONG).show()
        }
    }

}