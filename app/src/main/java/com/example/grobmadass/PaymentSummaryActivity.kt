package com.example.grobmadass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grobmadass.databinding.ActivityPaymentSummaryBinding
import com.example.grobmadass.databinding.ActivityPromoCodeBinding

class PaymentSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receiveObj = intent.getStringExtra("receiveObj").toString()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Payment Summary"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}