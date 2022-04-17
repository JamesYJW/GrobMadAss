package com.example.grobmadass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.example.grobmadass.databinding.ActivityBookingCarBinding
import com.example.grobmadass.databinding.ActivityUserProfileBinding

class BookingCarActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBookingCarBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding= ActivityBookingCarBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.tvOriginLocation.text = ""
        binding.tvDistinationLocation.text = ""
        binding.tvCarPaxNo.text = ""
        binding.tvTotalDistance.text = ""
        binding.tvTotalTime.text = ""
        binding.tvTotalCash.text = ""

        val i = 0
        if(i == 0){

        }
    }

}

