package com.example.grobmadass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grobmadass.data.PrivateCarData
import com.example.grobmadass.databinding.ActivityPrivateCarBinding
import com.google.firebase.database.*

class PrivateCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivateCarBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateCarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val privateCarId = "PCID00001"
        val privateCarStatus = false
        database = FirebaseDatabase.getInstance().getReference("PrivateCar")

        readPrivateCarData(privateCarId)

    }

    private fun readPrivateCarData(privateCarId: String) {
        database.child(privateCarId).get().addOnSuccessListener { rec->
                if(rec != null){
                    binding.tvCustNamePCA.text = rec.child("customerId").value.toString()
                    binding.tvCustPhoneNumPCA.text= rec.child("privateCarTotalPrice").value.toString() //<-- getDataFromCustomerList
                    binding.tvWaitLocPCA.text= rec.child("privateCarWaitGeoN").value.toString()+", "+rec.child("privateCarWaitGeoE").value.toString()
                    binding.tvDestinationLocPCA.text= rec.child("privateCarDesGeoN").value.toString()+", "+rec.child("privateCarDesGeoE").value.toString()
                    binding.tvTotalPaxPCA.text= rec.child("privateCarTotalPax").value.toString()
                    binding.tvTotalTimePCA.text= rec.child("privateCarTotalTime").value.toString()
                    binding.tvTotalDistancePCA.text= rec.child("privateCarTotalDistance").value.toString()
                    binding.tvTotalPricePCA.text= rec.child("privateCarTotalPrice").value.toString()
                }
                else{
                    binding.tvCustNamePCA.text = "Fail"
                }
            }
    }
}