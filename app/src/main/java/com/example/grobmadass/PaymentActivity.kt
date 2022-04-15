package com.example.grobmadass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.grobmadass.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Pricing"

        //TODO: Change text view value follow by data car type etc from here
        val pricingCarType : String = binding.textViewPricingCar.text.toString()
        val pricingDistance : String = binding.textViewPricingKM.text.toString()
        val pricingTime : String = binding.textViewPricingTime.text.toString()
        val pricingCost : String = binding.textViewPricingCost.text.toString()

        val buttonAddPromo : ImageView = binding.btnApplyPromoCodeIcon
        val buttonMakePayment : Button = binding.btnMakePayment

        val passObj : String = pricingCarType+"_"+pricingDistance+"_"+pricingTime+"_"+pricingCost

        buttonAddPromo.setOnClickListener {
            val bIntent = Intent(this, PromoCodeActivity::class.java)
            bIntent.putExtra("receiveObj",passObj) //pass obj
            startActivity(bIntent)
            //Toast.makeText(this, passObj, Toast.LENGTH_SHORT).show()
        }

        buttonMakePayment.setOnClickListener {

            val bIntent = Intent(this, PaymentSummaryActivity::class.java)
            bIntent.putExtra("receiveObj",passObj) //pass obj
            startActivity(bIntent)
            //Toast.makeText(this, passObj, Toast.LENGTH_SHORT).show()
        }

    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}