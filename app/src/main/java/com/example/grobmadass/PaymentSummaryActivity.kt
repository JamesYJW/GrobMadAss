package com.example.grobmadass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.grobmadass.databinding.ActivityPaymentSummaryBinding
import com.example.grobmadass.databinding.ActivityPromoCodeBinding

class PaymentSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receiveObj = intent.getStringExtra("receiveObj").toString()
        val paymentInfoArray: List<String> = receiveObj.split("_")
        binding.textViewPsumCar.text = paymentInfoArray[0]
        binding.textViewPsumKM.text = paymentInfoArray[1]

        binding.textViewPsumMin.text = paymentInfoArray[2]
        binding.textViewPsumEstCost.text = paymentInfoArray[3]

        val textview = findViewById<TextView>(R.id.CarType).apply{
            //text = message
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Payment Summary"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}