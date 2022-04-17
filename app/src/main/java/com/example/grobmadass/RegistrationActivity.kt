package com.example.grobmadass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.grobmadass.databinding.ActivityMainBinding
import com.example.grobmadass.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private var databaseReference : DatabaseReference? =null
    private var database : FirebaseDatabase? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setTitle("RegistrationActivity")

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("userProfile")



        binding.btnRegister.setOnClickListener(){

            if(TextUtils.isEmpty(binding.usernameInput.text.toString())){
                binding.usernameInput.setError("User Name is required!")
                return@setOnClickListener
            }else if(TextUtils.isEmpty(binding.userEmailInput.text.toString())) {
                binding.userEmailInput.setError("Email Address is required!")
                return@setOnClickListener
            }else if(TextUtils.isEmpty(binding.userPhoneNoInput.text.toString())) {
                binding.userPhoneNoInput.setError("Phone Number is required!")
                return@setOnClickListener
            }else if(!binding.userPhoneNoInput.text.toString().matches("^(\\+?6?01)[02-46-9]-*[0-9]{7}\$|^(\\+?6?01)[1]-*[0-9]{8}\$".toRegex())){
                binding.userPhoneNoInput.setError("Invalid Phone Number format")
                return@setOnClickListener
            }else if(TextUtils.isEmpty(binding.passwordInput.text.toString())) {
                binding.passwordInput.setError("Password is required!")
                return@setOnClickListener
            }else if(TextUtils.isEmpty(binding.confPasswordInput.text.toString())) {
                binding.confPasswordInput.setError("Confirm Password is required!")
                return@setOnClickListener
            }else if(!binding.confPasswordInput.text.toString().equals(binding.passwordInput.text.toString())){
                binding.confPasswordInput.setError("Passwords do not match!")
                return@setOnClickListener
            }

            registerUser(binding.usernameInput.text.toString(),binding.userEmailInput.text.toString()
                ,binding.userPhoneNoInput.text.toString(),binding.passwordInput.text.toString()
                ,binding.confPasswordInput.text.toString())
        }

        binding.userLoginText.setOnClickListener{
            startActivity(Intent(this@RegistrationActivity,LoginActivity::class.java))
        }
    }

    private fun registerUser(username: String, email: String, phoneNo: String, psw: String, confPsw: String) {
        auth.createUserWithEmailAndPassword(email,psw)
            .addOnCompleteListener{
                if(it.isSuccessful)
                {
                    val currentUser = auth.currentUser
                    val currentUserDb = databaseReference?.child((currentUser?.uid!!))
                    currentUserDb?.child("Username")?.setValue(username)
                    currentUserDb?.child("Phone Number")?.setValue(phoneNo)

                    Toast.makeText(this@RegistrationActivity, "Registration Successful", Toast.LENGTH_LONG).show()
                    finish()
                }
                else
                {
                    Toast.makeText(this@RegistrationActivity, it.exception?.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
    }


}