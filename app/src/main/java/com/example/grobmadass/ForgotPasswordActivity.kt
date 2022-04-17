package com.example.grobmadass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.grobmadass.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setTitle("ForgotPasswordActivity")

        binding.btnSubmitForgotPassword.setOnClickListener(){
            restUserPassword()
        }
    }

    private fun restUserPassword() {
        val email: String = binding.userEmailForgotPasswordInput.text.toString().trim{ it <= ' '}
        if(email.isEmpty()){
            Toast.makeText(this@ForgotPasswordActivity, "Please Enter Email Address", Toast.LENGTH_SHORT).show()
        }
        else{
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        Toast.makeText(this@ForgotPasswordActivity, "Email sent successfully to reset your password"
                            , Toast.LENGTH_LONG).show()
                        finish()
                    }
                    else{
                        Toast.makeText(this@ForgotPasswordActivity, task.exception!!.message.toString()
                            , Toast.LENGTH_LONG).show()
                    }
                }
        }
    }


}