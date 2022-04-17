package com.example.grobmadass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.grobmadass.databinding.ActivityPaymentBinding
import com.example.grobmadass.databinding.ActivityPromoCodeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PromoCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPromoCodeBinding
        private lateinit var database: DatabaseReference
        private lateinit var receiveObj:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromoCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Promo Code"

        //TODO: DOWNLOAD DATABASE DATA, STORE IN ARRAY

        val promoCodeFirebase : ArrayList<String>
        val discountFirebase : ArrayList<String>
        // Received obj
        receiveObj = intent.getStringExtra("receiveObj").toString()


        val btnApply : Button = binding.btnApply

            btnApply.setOnClickListener {

                val inputPromoCode : String = binding.inputPromoCode.text.toString()
                if(inputPromoCode.isNotEmpty()){
                    readData(inputPromoCode)
                }else{

                    Toast.makeText(this,"Promo code does not exist.", Toast.LENGTH_SHORT).show()
                }
            //Toast.makeText(this, receiveObj, Toast.LENGTH_SHORT).show()
        }
    }

    private fun readData(inputPromoCode: String) {
        database = FirebaseDatabase.getInstance().getReference("PromoCode")
        database.child(inputPromoCode).get().addOnSuccessListener {
            if(it.exists()){
                val disc = it.value.toString()
                Toast.makeText(this,"Promo code read!", Toast.LENGTH_SHORT).show()
                binding.inputPromoCode.text.clear()
                receiveObj += "_"+disc

                val bIntent = Intent(this, PaymentSummaryActivity::class.java)
                bIntent.putExtra("receiveObj",receiveObj) //pass obj
                startActivity(bIntent)


            }else{
                Toast.makeText(this,"1Promo code does not exist.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}