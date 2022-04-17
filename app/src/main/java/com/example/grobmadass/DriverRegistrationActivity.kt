package com.example.grobmadass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.grobmadass.databinding.ActivityDriverRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DriverRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDriverRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private var databaseReference : DatabaseReference? =null
    private var database : FirebaseDatabase? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setTitle("DriverRegistrationActivity")

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("userProfile")

        binding.btnRegisterDriver.setOnClickListener(){

            if(TextUtils.isEmpty(binding.carlicenseNumberInput.text.toString())){
                binding.carlicenseNumberInput.setError("Driving License Number is required!")
                return@setOnClickListener
            }else if(TextUtils.isEmpty(binding.carplateNumberInput.text.toString())){
                binding.carplateNumberInput.setError("Car Plate Number is required!")
                return@setOnClickListener
            }else if(TextUtils.isEmpty(binding.carModelInput.text.toString())){
                binding.carModelInput.setError("Car Model is required!")
                return@setOnClickListener
            }else if(TextUtils.isEmpty(binding.carColourInput.text.toString())){
                binding.carColourInput.setError("Car Colour is required!")
                return@setOnClickListener
            }

            registerDriver(binding.carlicenseNumberInput.text.toString(),binding.carplateNumberInput.text.toString(),
            binding.carModelInput.text.toString(),binding.carColourInput.text.toString())
        }

        binding.btnCancelRegisterDriver.setOnClickListener(){
            startActivity(Intent(this@DriverRegistrationActivity,UserProfileActivity::class.java))
        }
    }

    private fun registerDriver(carlicenseNumber: String, carplateNumber: String, carModel: String, carColour: String) {
        val currentUser = auth.currentUser
        val currentUserDb = databaseReference?.child((currentUser?.uid!!))
        currentUserDb?.child("Driving License Number")?.setValue(carlicenseNumber)
        currentUserDb?.child("Car Plate Number")?.setValue(carplateNumber)
        currentUserDb?.child("Car Model")?.setValue(carModel)
        currentUserDb?.child("Car Colour")?.setValue(carColour)

        Toast.makeText(this@DriverRegistrationActivity, "Driver Registration Successful", Toast.LENGTH_LONG).show()
        finish()
    }
}