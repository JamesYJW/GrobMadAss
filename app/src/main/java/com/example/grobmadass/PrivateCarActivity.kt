package com.example.grobmadass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grobmadass.data.PrivateCarData
import com.example.grobmadass.databinding.ActivityPrivateCarBinding
import com.google.firebase.database.*

class PrivateCarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivateCarBinding
    private lateinit var database: DatabaseReference
    private lateinit var privateCarList: ArrayList<PrivateCarData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateCarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val customerId = "CTID00001"
        val privateCarStatus = 1
        database = FirebaseDatabase.getInstance().getReference("PrivateCar")

        readPrivateCarData(customerId,privateCarStatus)

    }

    private fun readPrivateCarData(customerId: String, privateCarStatus: Int) {

    }
}