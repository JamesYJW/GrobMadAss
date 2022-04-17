package com.example.grobmadass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.widget.Toast
import com.example.grobmadass.databinding.ActivityBookingCarBinding
import com.example.grobmadass.databinding.ActivityUserProfileBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BookingCarActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBookingCarBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding= ActivityBookingCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val privateCarId = intent.getStringExtra("privateCarId")!!//<<-- change to driver id
        readPrivateCarData(privateCarId)

        //val customHandler = Handler()
        //customHandler.postDelayed(updateTimerThread, 0)



        binding.btnCancelFinding.setOnClickListener(){

            database = FirebaseDatabase.getInstance().getReference("PrivateCar")
            database.child(privateCarId).child("findCancel")
                .setValue(true).addOnSuccessListener {
                    Toast.makeText(applicationContext, "Booking Cancelled", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(applicationContext, "Cancelled Fail", Toast.LENGTH_SHORT).show()
                }

        }

        //val intent = Intent(this@BookingCarActivity, GoogleMapActivity::class.java)
        //startActivity(intent)


    }


    private fun readPrivateCarData(privateCarId: String) {
        database = FirebaseDatabase.getInstance().getReference("PrivateCar")

        database.child(privateCarId).get().addOnSuccessListener { rec->
            if(rec != null){
                val customerId = rec.child("customerId").value.toString()
                binding.tvOriginLocation.text= rec.child("privateCarWaitGeoN").value.toString()+" "+rec.child("privateCarWaitGeoE").value.toString()
                binding.tvDistinationLocation.text= rec.child("privateCarDesGeoN").value.toString()+", "+rec.child("privateCarDesGeoE").value.toString()
                binding.tvCarPaxNo.text= rec.child("privateCarTotalPax").value.toString()
                binding.tvTotalTime.text= rec.child("privateCarTotalTime").value.toString()
                binding.tvTotalDistance.text= rec.child("privateCarTotalDistance").value.toString()
                binding.tvTotalCash.text= rec.child("privateCarTotalPrice").value.toString()

            }
            else{
                Toast.makeText(applicationContext,"Fail to get data!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {
            val customHandler = Handler()

            Toast.makeText(applicationContext, "Hey 67", Toast.LENGTH_SHORT).show()
            customHandler.postDelayed(this, 1000)
        }
    }

}

