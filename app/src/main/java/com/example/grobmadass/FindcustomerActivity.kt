package com.example.grobmadass

import android.app.assist.AssistContent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grobmadass.adapter.PrivatecarcustomerAdapter
import com.example.grobmadass.dataModels.PrivateCarData
import com.example.grobmadass.databinding.ActivityFindcustomerBinding
import com.google.firebase.database.*


class FindcustomerActivity : AppCompatActivity(), PrivatecarcustomerAdapter.onItemClcikListener {
    private lateinit var binding: ActivityFindcustomerBinding
    private lateinit var database: DatabaseReference
    private lateinit var privateCarList: ArrayList<PrivateCarData>

    private var context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindcustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        privateCarList = arrayListOf()
        readData()


        binding.rvPrivateCarFCA.layoutManager = LinearLayoutManager(applicationContext)
        binding.rvPrivateCarFCA.setHasFixedSize(true)

        database = FirebaseDatabase.getInstance().getReference("PrivateCar")

    }
    private fun readData() {
        database = FirebaseDatabase.getInstance().getReference("PrivateCar")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    privateCarList = arrayListOf()
                    for (privateCarSnapshot in snapshot.children){
                        if(privateCarSnapshot.child("privateCarStatus").value.toString() == "1"){
                            val priCar = privateCarSnapshot.getValue(PrivateCarData::class.java)
                            privateCarList.add(priCar!!)
                        }
                    }
                    val adapter = PrivatecarcustomerAdapter(privateCarList, context)
                    binding.rvPrivateCarFCA.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    override fun itemClick(position: Int) {
        val selectPrivateCar = privateCarList[position]
        Toast.makeText(applicationContext, selectPrivateCar.privateCarId, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, PrivateCarActivity::class.java)
        intent.putExtra("privateCarId",selectPrivateCar.privateCarId)
        startActivity(intent)
    }


    private fun addNewPrivateCar(newPrivateCar: PrivateCarData) {
        database.child(newPrivateCar.privateCarId)
            .setValue(newPrivateCar).addOnSuccessListener {
                Toast.makeText(applicationContext, "added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "no", Toast.LENGTH_SHORT).show()

            }
    }

}