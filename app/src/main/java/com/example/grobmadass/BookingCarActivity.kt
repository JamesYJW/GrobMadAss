package com.example.grobmadass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.grobmadass.databinding.ActivityBookingCarBinding
import com.example.grobmadass.databinding.ActivityUserProfileBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BookingCarActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBookingCarBinding
    private lateinit var database: DatabaseReference

    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 1000
    var priID = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding= ActivityBookingCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val privateCarId = intent.getStringExtra("privateCarId")!!//<<-- change to driver id
        readPrivateCarData(privateCarId)
        priID = privateCarId
        //val customHandler = Handler()
        //customHandler.postDelayed(updateTimerThread, 0)

        //finish()


        binding.btnCancelFinding.setOnClickListener(){

            database = FirebaseDatabase.getInstance().getReference("PrivateCar")
            database.child(privateCarId).child("findCancel")
                .setValue(true).addOnSuccessListener {
                    Toast.makeText(applicationContext, "Booking Cancelled", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@BookingCarActivity, GoogleMapActivity::class.java)
                    startActivity(intent)

                }
                .addOnFailureListener { e ->
                    Toast.makeText(applicationContext, "Cancelled Fail", Toast.LENGTH_SHORT).show()
                }

        }

        //val intent = Intent(this@BookingCarActivity, GoogleMapActivity::class.java)
        //startActivity(intent)
    }

    override fun onResume() {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            if(priID != null){
                readStatus(priID)

            }
        }.also { runnable = it }, delay.toLong())
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }

    private fun readStatus(privateCarId: String){
        database = FirebaseDatabase.getInstance().getReference("PrivateCar")
        database.child(privateCarId).child("privateCarStatus").get().addOnSuccessListener { rec->
            if (rec.value.toString() == "2"){
                finish()
            }else{
                //Toast.makeText(applicationContext, "Error!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readPrivateCarData(privateCarId: String) {
        database = FirebaseDatabase.getInstance().getReference("PrivateCar")

        database.child(privateCarId).get().addOnSuccessListener { rec->
            if(rec != null){
                val customerId = rec.child("customerId").value.toString()

                binding.tvOriginLocation.text= "From " + rec.child("privateCarWaitLoc").value.toString()
                binding.tvDistinationLocation.text= "To " + rec.child("privateCarDecLoc").value.toString()
                binding.tvCarPaxNo.text= rec.child("privateCarTotalPax").value.toString() + " pax"
                binding.tvTotalTime.text= rec.child("privateCarTotalTime").value.toString() + " minutes"
                binding.tvTotalDistance.text= rec.child("privateCarTotalDistance").value.toString() + " km"
                binding.tvTotalCash.text= "RM " + rec.child("privateCarTotalPrice").value.toString()

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

