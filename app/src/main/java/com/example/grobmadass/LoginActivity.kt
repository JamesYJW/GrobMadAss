package com.example.grobmadass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.grobmadass.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setTitle("LoginActivity")

        auth = FirebaseAuth.getInstance()

        val currentuser = auth.currentUser
        if(currentuser!=null && binding.rememberMecheckBox.isChecked){
            startActivity(Intent(this@LoginActivity,UserProfileActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener(){

            if(TextUtils.isEmpty(binding.userEmailInput.text.toString())){
                binding.userEmailInput.setError("Email Address is required!")
                return@setOnClickListener
            }else if(TextUtils.isEmpty(binding.passwordInput.text.toString())) {
                binding.passwordInput.setError("Password is required!")
                return@setOnClickListener
            }

            loginUser(binding.userEmailInput.text.toString(),binding.passwordInput.text.toString())
        }

        binding.registerText.setOnClickListener(){
            startActivity(Intent(this@LoginActivity,RegistrationActivity::class.java))
        }

        binding.forgotPasswordText.setOnClickListener(){
            startActivity(Intent(this@LoginActivity,ForgotPasswordActivity::class.java))
        }
    }

    private fun loginUser(email: String, psw: String) {
        auth.signInWithEmailAndPassword(email,psw)
            .addOnCompleteListener{
                if(it.isSuccessful)
                {
                    Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity,UserProfileActivity::class.java))
                    finish()
                }
                else
                {
                    Toast.makeText(this@LoginActivity, "Incorrect Email Address or Password", Toast.LENGTH_LONG).show()
                }
            }
    }
}