package com.example.grobmadass

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.net.toFile
import com.example.grobmadass.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUserProfileBinding
    private lateinit var auth: FirebaseAuth
    private var databaseReference : DatabaseReference? =null
    private var database : FirebaseDatabase? =null
    private lateinit var ImageUri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setTitle("UserProfileActivity")

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference!!.child("userProfile")

        loadUserProfile()
        loadDriverDetails()

        //select userProfile image and show image
        binding.btnSelectUserImg.setOnClickListener(){
            selectUserImg()
        }

        //update data to firebase and storage
        binding.btnUpdate.setOnClickListener(){

            if(TextUtils.isEmpty(binding.usernameInput.text.toString())){
                binding.usernameInput.setError("User Name is required!")
                return@setOnClickListener
            }else if(TextUtils.isEmpty(binding.userPhoneNoInput.text.toString())){
                binding.userPhoneNoInput.setError("Phone Number is required!")
                return@setOnClickListener
            }else if(!binding.userPhoneNoInput.text.toString().matches("^(\\+?6?01)[02-46-9]-*[0-9]{7}\$|^(\\+?6?01)[1]-*[0-9]{8}\$".toRegex())){
                binding.userPhoneNoInput.setError("Invalid Phone Number format")
                return@setOnClickListener
            }
            updateUserData(binding.usernameInput.text.toString(), binding.userPhoneNoInput.text.toString())
        }

        binding.btnDriverRegistration.setOnClickListener(){
            registerDriver()
        }

    }

    private fun loadDriverDetails(){
        val user = auth.currentUser
        val userReference = databaseReference?.child(user?.uid!!)

        userReference?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("Driving License Number").exists())
                {
                    binding.driverInfo.setText("--" + snapshot.child("Driving License Number").value.toString())
                    binding.driverInfo2.setText("--" +snapshot.child("Car Plate Number").value.toString())
                    binding.driverInfo3.setText("--" +snapshot.child("Car Model").value.toString())
                    binding.driverInfo4.setText("--" +snapshot.child("Car Colour").value.toString())
                }
                else{

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun registerDriver(){
        val user = auth.currentUser
        val userReference = databaseReference?.child(user?.uid!!)

        userReference?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.child("Driving License Number").exists())
               {
                   //this one need to change it to JAMES DRIVER PAGE
                   startActivity(Intent(this@UserProfileActivity,FindcustomerActivity::class.java))
                   //finish()
               }
                else{
                   startActivity(Intent(this@UserProfileActivity,DriverRegistrationActivity::class.java))
               }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun selectUserImg() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK)
        {
            ImageUri = data?.data!!
            binding.profilePicIcon.setImageURI(ImageUri)
        }
    }

    private fun updateUserData(username: String, phoneNumber: String) {

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val currentTime = Date()
        val filename = formatter.format(currentTime)
        val storageReference = FirebaseStorage.getInstance().getReference("images/${filename}.png")

        val currentUser = auth.currentUser
        val currentUserDb = databaseReference?.child((currentUser?.uid!!))

        //got error if no input image, click update btn
        if(::ImageUri.isInitialized)
        {
            storageReference.putFile(ImageUri)
                .addOnSuccessListener {
                    val result = it.metadata!!.reference!!.downloadUrl;
                    result.addOnSuccessListener {

                        val imageLink = it.toString()
                        currentUserDb?.child("ImageUri")?.setValue(imageLink)

                    }
                }
        }
        else{

        }




        currentUserDb?.child("Username")?.setValue(username)
        currentUserDb?.child("Phone Number")?.setValue(phoneNumber)
            ?.addOnSuccessListener {
                Toast.makeText(this@UserProfileActivity, "Update Successful", Toast.LENGTH_LONG).show()
            }

    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        val userReference = databaseReference?.child(user?.uid!!)

        userReference?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                var image: Bitmap? = null
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    val imgURL = snapshot.child("ImageUri").value.toString()

                    // get the image and post it in the ImageView
                    try {
                        val `in` = java.net.URL(imgURL).openStream()
                        image = BitmapFactory.decodeStream(`in`)
                        handler.post {
                            binding.profilePicIcon.setImageBitmap(image)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                    //load user email,username,phoneNumber
                binding.userEmailInput.text= user?.email
                binding.usernameInput.setText(snapshot.child("Username").value.toString())
                binding.userPhoneNoInput.setText(snapshot.child("Phone Number").value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.btnLogOut.setOnClickListener(){
            auth.signOut()
            startActivity(Intent(this@UserProfileActivity,LoginActivity::class.java))
            finish()
        }
    }
}