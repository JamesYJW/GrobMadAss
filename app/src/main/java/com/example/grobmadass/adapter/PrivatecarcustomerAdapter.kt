package com.example.grobmadass.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grobmadass.R
import com.example.grobmadass.dataModels.PrivateCarData
import com.google.firebase.database.*
import java.util.concurrent.Executors




class PrivatecarcustomerAdapter(private val privateCarlist: ArrayList<PrivateCarData>, private val listener:onItemClcikListener) : RecyclerView.Adapter<PrivatecarcustomerAdapter.myViewHolder>() {
    private lateinit var database: DatabaseReference

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        val tvName: TextView = itemView.findViewById(R.id.tvCustName_PCCV)
        val tvDestionation: TextView = itemView.findViewById(R.id.tvDestination_PCCV)
        val tvTotalPax: TextView = itemView.findViewById(R.id.tvTotalPax_PCCV)
        val tvCustPic: ImageView = itemView.findViewById(R.id.picCust_PCCV)

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            val position = adapterPosition

            if(position != RecyclerView.NO_POSITION){
                listener.itemClick(position)
            }
        }

    }


    interface onItemClcikListener {
        fun itemClick(position:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.privatecarcustomer_view, parent, false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentPrivateCar = privateCarlist[position]
        val custID = currentPrivateCar.customerId
        holder.tvDestionation.text = currentPrivateCar.privateCarDecLoc.toString()
        holder.tvTotalPax.text = currentPrivateCar.privateCarTotalPax.toString() + " PAX"

        // get customer name
        database = FirebaseDatabase.getInstance().getReference("userProfile")
        database.child(custID).get().addOnSuccessListener { rec->
            if(rec != null){
                holder.tvName.text= rec.child("Username").value.toString()
            }
            else{
                holder.tvName.text = "Fail to get data!"
            }
        }
        // get customer image
        val userReference = FirebaseDatabase.getInstance().getReference("userProfile").child(custID)
        userReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var image: Bitmap? = null
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                val imgURL = snapshot.child("ImageUri").value.toString()
                    try {
                        val `in` = java.net.URL(imgURL).openStream()
                        image = BitmapFactory.decodeStream(`in`)
                        handler.post {
                            holder.tvCustPic.setImageBitmap(image)
                        }
                    }
                    catch (e: Exception) {
                        handler.post {
                            holder.tvCustPic.setImageResource(R.drawable.user)
                        }
                        e.printStackTrace()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    override fun getItemCount(): Int {
        return privateCarlist.size
    }



}