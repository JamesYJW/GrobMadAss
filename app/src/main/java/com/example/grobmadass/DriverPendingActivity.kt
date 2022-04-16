package com.example.grobmadass

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.example.grobmadass.databinding.ActivityDriverPendingBinding
import com.example.grobmadass.databinding.ActivityFindcustomerBinding
import com.google.firebase.database.*
import java.util.concurrent.Executors

class DriverPendingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverPendingBinding
    private lateinit var database: DatabaseReference
    private lateinit var custPhoneNum: String
    private lateinit var custWaitGeo: String
    private lateinit var custDesGeo: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverPendingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val privateCarId = intent.getStringExtra("privateCarId")!!

        readPrivateCarData(privateCarId)

        binding.btnStartDPA.setOnClickListener(){
            val geoCurret = Uri.parse("http://maps.google.com/maps?saddr=${custWaitGeo}&daddr=${custDesGeo}")
            val intentMap: Intent = Intent(Intent.ACTION_VIEW, geoCurret )
            startActivity(intentMap)
        }
        binding.btnMarkAsDoneDPA.setOnClickListener(){

        }
        binding.btnCancelDPA.setOnClickListener(){

        }
        binding.btnCallDPA.setOnClickListener(){
            val telNo = Uri.parse("tel: $custPhoneNum")
            val intentCall: Intent = Intent(Intent.ACTION_DIAL, telNo)
            startActivity(intentCall)
        }
    }

    private fun readPrivateCarData(privateCarId: String) {
        database = FirebaseDatabase.getInstance().getReference("PrivateCar")

        database.child(privateCarId).get().addOnSuccessListener { rec->
            if(rec != null){
                val customerId = rec.child("customerId").value.toString()
                binding.tvWaitLocDPA.text= rec.child("privateCarWaitGeoN").value.toString()+" "+rec.child("privateCarWaitGeoE").value.toString()
                binding.tvDestinationLocDPA.text= rec.child("privateCarDesGeoN").value.toString()+", "+rec.child("privateCarDesGeoE").value.toString()
                binding.tvTotalPaxDPA.text= rec.child("privateCarTotalPax").value.toString()
                binding.tvTotalPriceDPA.text= rec.child("privateCarTotalPrice").value.toString()

                database = FirebaseDatabase.getInstance().getReference("userProfile")
                readUserProfileData(customerId)
            }
            else{
                Toast.makeText(applicationContext, "Fail to get data!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readUserProfileData(customerId: String) {
        val userReference = FirebaseDatabase.getInstance().getReference("userProfile").child(customerId)

        database.child(customerId).get().addOnSuccessListener { rec->
            if(rec != null){
                binding.tvCustPhoneNumDPA.text = rec.child("Phone Number").value.toString()
                binding.tvCustNameDPA.text = rec.child("Username").value.toString()
                custPhoneNum = rec.child("Phone Number").value.toString()

                binding.btnCallDPA.visibility = View.VISIBLE
                binding.btnCancelDPA.visibility = View.VISIBLE
                binding.btnMarkAsDoneDPA.visibility = View.VISIBLE
                binding.btnStartDPA.visibility = View.VISIBLE
            }
            else{
                Toast.makeText(applicationContext, "Fail to get data!", Toast.LENGTH_SHORT).show()
            }
        }
        userReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var image: Bitmap? = null
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    val imgURL = snapshot.child("ImageUri").value.toString()

                    // get the image and post it in the ImageView
                    try {
                        val `in` = java.net.URL(imgURL).openStream()
                        image = BitmapFactory.decodeStream(`in`)
                        handler.post {
                            binding.ivUserProfileDPA.setImageBitmap(image)
                        }

                    } catch (e: Exception) {
                        binding.ivUserProfileDPA.setImageResource(R.drawable.user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}