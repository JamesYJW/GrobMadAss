package com.example.grobmadass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.grobmadass.databinding.ActivityFindcustomerBinding


class FindcustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindcustomerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindcustomerBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_findcustomer)

    }
}