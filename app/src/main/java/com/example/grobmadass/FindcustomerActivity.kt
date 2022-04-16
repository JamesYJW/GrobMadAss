package com.example.grobmadass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grobmadass.data.PrivateCarData
import com.example.grobmadass.databinding.ActivityFindcustomerBinding
import com.google.firebase.database.*


class FindcustomerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindcustomerBinding
    private lateinit var database: DatabaseReference
    private lateinit var privateCarList: ArrayList<PrivateCarData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindcustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        privateCarList = arrayListOf<PrivateCarData>()

//        database = FirebaseDatabase.getInstance().getReference("myDB")
//
//        binding.btnAddPrivateCar.setOnClickListener(){
//            val newPrivateCar =
//                PrivateCar("PC00004", "Wei Wei",
//                    3.216223,101.729000,
//                    3.204366, 101.720685)
//            addNewPrivateCar(newPrivateCar)
        readData()
        //val adapter = PrivateCarAdapter(privateCarList)


        }

    private fun readData() {
        database = FirebaseDatabase.getInstance().getReference("PrivateCar")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){

                    for (privateCarSnapshot in snapshot.children){
                        val pricar = privateCarSnapshot.getValue(PrivateCarData::class.java)
                        privateCarList.add(pricar!!)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }


}

//    private fun addNewPrivateCar(newPrivateCar: PrivateCar) {
//        database.child("PrivateCar").child(newPrivateCar.privateCarId)
//            .setValue(newPrivateCar).addOnSuccessListener {
//                binding.tvStatus.text = "Added"
//            }
//            .addOnFailureListener { e->
//                binding.tvStatus.text = e.message
//            }
//    }
