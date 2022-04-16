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
import com.example.grobmadass.databinding.ActivityPrivateCarBinding
import com.google.firebase.database.*
import java.util.concurrent.Executors

class PrivateCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivateCarBinding
    private lateinit var database: DatabaseReference
    private lateinit var custPhoneNum: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateCarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val privateCarId = intent.getStringExtra("privateCarId")!!//<<-- change to driver id

        readPrivateCarData(privateCarId)

        binding.btnCallPCA.setOnClickListener(){
            val telNo = Uri.parse("tel: $custPhoneNum")
            val intentCall: Intent = Intent(Intent.ACTION_DIAL, telNo)
            startActivity(intentCall)
        }

        binding.btnAcceptPCA.setOnClickListener(){
            changePrivateCarStatus(privateCarId)
            val intent = Intent(this, DriverPendingActivity::class.java)
            intent.putExtra("privateCarId",privateCarId)
            startActivity(intent)
        }
    }

    private fun changePrivateCarStatus(privateCarId: String) {
        database = FirebaseDatabase.getInstance().getReference("PrivateCar")
        database.child(privateCarId).child("privateCarStatus")
            .setValue(2).addOnSuccessListener {
                Toast.makeText(applicationContext, "Accepted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
            }
    }

    private fun readPrivateCarData(privateCarId: String) {
        database = FirebaseDatabase.getInstance().getReference("PrivateCar")

        database.child(privateCarId).get().addOnSuccessListener { rec->
            if(rec != null){
                val customerId = rec.child("customerId").value.toString()
                binding.tvWaitLocPCA.text= rec.child("privateCarWaitGeoN").value.toString()+" "+rec.child("privateCarWaitGeoE").value.toString()
                binding.tvDestinationLocPCA.text= rec.child("privateCarDesGeoN").value.toString()+", "+rec.child("privateCarDesGeoE").value.toString()
                binding.tvTotalPaxPCA.text= rec.child("privateCarTotalPax").value.toString()
                binding.tvTotalTimePCA.text= rec.child("privateCarTotalTime").value.toString()
                binding.tvTotalDistancePCA.text= rec.child("privateCarTotalDistance").value.toString()
                binding.tvTotalPricePCA.text= rec.child("privateCarTotalPrice").value.toString()

                database = FirebaseDatabase.getInstance().getReference("userProfile")
                readUserProfileData(customerId)
            }
            else{
                binding.tvWaitLocPCA.text = "Fail to get data!"
            }
        }
    }

    private fun readUserProfileData(customerId: String) {
        val userReference = FirebaseDatabase.getInstance().getReference("userProfile").child(customerId)

        database.child(customerId).get().addOnSuccessListener { rec->
            if(rec != null){
                binding.tvCustPhoneNumPCA.text = rec.child("Phone Number").value.toString()
                binding.tvCustNamePCA.text = rec.child("Username").value.toString()
                custPhoneNum = rec.child("Phone Number").value.toString()

                binding.btnCallPCA.visibility = View.VISIBLE
            }
            else{
                binding.tvWaitLocPCA.text = "Fail to get data!"
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
                            binding.ivUserProfilePCA.setImageBitmap(image)
                        }

                    } catch (e: Exception) {
                        binding.ivUserProfilePCA.setImageResource(R.drawable.user)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}