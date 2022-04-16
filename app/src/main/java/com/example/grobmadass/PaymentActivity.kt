package com.example.grobmadass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.grobmadass.databinding.ActivityPaymentBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PaymentActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPaymentBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().getReference("PrivateCar")

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Pricing"

        //TODO: Change text view value follow by data car type etc from here


        val buttonAddPromo : ImageView = binding.btnApplyPromoCodeIcon
        val buttonMakePayment : Button = binding.btnMakePayment
        val pcID:String ="PCID00001"
        database.child(pcID).get().addOnSuccessListener {
            binding.textViewPricingCar.text = it.child("privateCarId").value.toString()
            binding.textViewPricingKM.text = it.child("privateCarTotalDistance").value.toString()
            binding.textViewPricingTime.text = it.child("privateCarTotalTime").value.toString()
            binding.textViewPricingCost.text= it.child("privateCarTotalPrice").value.toString()


        }

        buttonAddPromo.setOnClickListener {

            val pricingCarType : String = binding.textViewPricingCar.text.toString()
            val pricingDistance : String = binding.textViewPricingKM.text.toString()
            val pricingTime : String = binding.textViewPricingTime.text.toString()
            val pricingCost : String = binding.textViewPricingCost.text.toString()
            val passObj : String = pcID+"_"+pricingCarType+"_"+pricingDistance+"_"+pricingTime+"_"+pricingCost
            val bIntent = Intent(this, PromoCodeActivity::class.java)
            bIntent.putExtra("receiveObj",passObj) //pass obj
            startActivity(bIntent)
            //Toast.makeText(this, passObj, Toast.LENGTH_SHORT).show()
        }

        buttonMakePayment.setOnClickListener {
            val pricingCarType : String = binding.textViewPricingCar.text.toString()
            val pricingDistance : String = binding.textViewPricingKM.text.toString()
            val pricingTime : String = binding.textViewPricingTime.text.toString()
            val pricingCost : String = binding.textViewPricingCost.text.toString()
            val passObj : String = pcID+"_"+pricingCarType+"_"+pricingDistance+"_"+pricingTime+"_"+pricingCost+"_0"

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