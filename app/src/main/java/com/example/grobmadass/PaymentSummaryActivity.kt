package com.example.grobmadass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.grobmadass.databinding.ActivityPaymentSummaryBinding
import com.example.grobmadass.databinding.ActivityPromoCodeBinding
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.Order
import com.paypal.checkout.order.PurchaseUnit
import com.paypal.checkout.paymentbutton.PayPalButton
import com.paypal.checkout.order.*

class PaymentSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSummaryBinding
    private lateinit var config: CheckoutConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receiveObj = intent.getStringExtra("receiveObj").toString()
        val paymentInfoArray: List<String> = receiveObj.split("_")
        binding.textViewPsumCar.text = paymentInfoArray[1]
        binding.textViewPsumKM.text = paymentInfoArray[2]

        binding.textViewPsumMin.text = paymentInfoArray[3]
        binding.textViewPsumEstCost.text = paymentInfoArray[4]
        val discount = paymentInfoArray[5]
        val promoDiscount = ( binding.textViewPsumEstCost.text.toString().toFloat() - discount.toFloat() ).toString()
        binding.promoAmount.text=   discount
        binding.SumTotalcal.text=promoDiscount
        binding.subtotalPrice.text=paymentInfoArray[4]
        val textview = findViewById<TextView>(R.id.CarType).apply{
            //text = message
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Payment Summary"

        config = CheckoutConfig(
            application = this.application,
            clientId = "AcVwCX_OEqjpOg1foY8nHk2h0D_CdtOEm8g_v9RvllxGalXCiGF0DKTeSUOhy4UE1NV07596JGLHKy23",
            environment = Environment.SANDBOX,
            returnUrl = "com.example.grobmadass://paypalpay",
            currencyCode = CurrencyCode.MYR,
            userAction = UserAction.PAY_NOW,
            settingsConfig = SettingsConfig(
                loggingEnabled = true
            )
        )


        PayPalCheckout.setConfig(config)
        val checkout: PayPalButton = binding.checkOutButton
        checkout.setup(
            createOrder =
            CreateOrder { createOrderActions ->
                val order =
                    Order(
                        intent = OrderIntent.CAPTURE,
                        appContext = AppContext(userAction = UserAction.PAY_NOW),
                        purchaseUnitList =
                        listOf(
                            PurchaseUnit(
                                amount =
                                Amount(currencyCode = CurrencyCode.MYR, value =binding.SumTotalcal.text.toString())
                            )
                        )
                    )
                createOrderActions.create(order)
            },
            onApprove =
            OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    //After Success Payment will run here
                    Log.i("CaptureOrder", "CaptureOrderResult: $captureOrderResult")
                }
            }
        )

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}