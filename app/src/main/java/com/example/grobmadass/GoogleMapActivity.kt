package com.example.grobmadass

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.location.Address
import android.location.Geocoder
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import java.io.IOException

import android.graphics.Color
import android.icu.text.DecimalFormat
import android.location.LocationRequest
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import com.example.grobmadass.dataModels.PrivateCarData
import com.example.grobmadass.databinding.ActivityPrivateCarBinding
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.format
import org.slf4j.Marker
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var binding: ActivityPrivateCarBinding

    private var mMap: GoogleMap? = null

    //default at pv15
    private var originLatitude: Double = 3.2023
    private var originLongitude: Double = 101.7166
    private var destinationLatitude: Double = 3.2023
    private var destinationLongitude: Double = 101.7166

    private var markerDest: Marker? = null
    private lateinit var database: DatabaseReference

    //get user database
    private lateinit var auth: FirebaseAuth
    private var databaseReference : DatabaseReference? =null
    private var database2 : FirebaseDatabase? =null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)


        val btnSearchLocation = findViewById<Button>(R.id.btnSearchLocation)
        val locationSearch = findViewById<EditText>(R.id.etLocation)

        val rlCarPax = findViewById<RelativeLayout>(R.id.rlCarPax)
        val btn4paxCar = findViewById<Button>(R.id.btn4paxCar)
        val btn6paxCar = findViewById<Button>(R.id.btn6paxCar)

        val tvDistCost = findViewById<TextView>(R.id.tvBookInfo)
        val btnCancelBooking = findViewById<Button>(R.id.btnCancelBooking)
        val btnBookCar = findViewById<Button>(R.id.btnBookCar)

        var paxCarNo:Int = 0
        var totalDistance:Double = 0.0
        var totalCost:Double = 0.0
        var totalTime:Int = 0
        var location: String? = null

        //user auth
        auth = FirebaseAuth.getInstance()
        database2 = FirebaseDatabase.getInstance()
        databaseReference = database2?.reference!!.child("userProfile")


        database = FirebaseDatabase.getInstance().getReference("PrivateCar")


        //open search location page
        btnSearchLocation.visibility = View.VISIBLE
        locationSearch.visibility = View.VISIBLE

        // Fetching API_KEY which we wrapped
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // Map Fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnSearchLocation.setOnClickListener{
            closeKeyBoard()

            mapFragment.getMapAsync {
                mMap = it

                //search
                location = locationSearch.text.toString()
                var addressList: List<Address>? = null

                if (location == null || location == "") {
                    Toast.makeText(applicationContext,"provide location",Toast.LENGTH_SHORT).show()
                }
                else {
                    val geoCoder = Geocoder(this)
                    try {
                        addressList = geoCoder.getFromLocationName(location, 1)

                        //mark location
                        val address = addressList!![0]
                        destinationLatitude = address.latitude.toDouble()
                        destinationLongitude = address.longitude.toDouble()

                        //mark origin
                        val originLocation = LatLng(originLatitude, originLongitude)
                        mMap!!.addMarker(MarkerOptions().position(originLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))

                        //mark destination
                        val destinationLocation = LatLng(destinationLatitude, destinationLongitude)
                        mMap!!.addMarker(MarkerOptions().position(destinationLocation))

                        //get direction
                        val routeMap = getDirectionURL(originLocation, destinationLocation, apiKey)
                        GetDirection(routeMap).execute()
                        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 14F))

                        //open select car pax page
                        rlCarPax.visibility = View.VISIBLE
                        //btn4paxCar.visibility = View.VISIBLE
                        //btn6paxCar.visibility = View.VISIBLE

                        //close search location page
                        btnSearchLocation.visibility = View.INVISIBLE
                        locationSearch.visibility = View.INVISIBLE

                    } catch (e: IOException) {
                        //e.printStackTrace()
                        Toast.makeText(applicationContext, "Location not found! \n Please Try Again.", Toast.LENGTH_SHORT).show()
                    }

                }
                //locationSearch.text.clear()
            }
        }
        btn4paxCar.setOnClickListener{
            paxCarNo = 4

            var dist = calcDistance(originLatitude, originLongitude, destinationLatitude, destinationLongitude)
            var formatKm = DecimalFormat("#0.0000")
            totalDistance = formatKm.format(dist).toDouble()
            var strDis = "" + totalDistance + "km"

            var cost = calcCost(totalDistance, paxCarNo)
            var formatRm = DecimalFormat("##0.00")
            totalCost = formatRm.format(cost).toDouble()
            var strCost = "RM " + totalCost

            totalTime = calcTime(totalDistance)
            var strTime = "" +totalTime + " minutes"


            //open confirm book page
            tvDistCost.text = location + "\n" + strDis + "\n" + strCost + "\n" + strTime
            tvDistCost.visibility = View.VISIBLE
            btnCancelBooking.visibility = View.VISIBLE
            btnBookCar.visibility = View.VISIBLE

            //close select car pax page
            rlCarPax.visibility = View.INVISIBLE
            //btn4paxCar.visibility = View.INVISIBLE
            //btn6paxCar.visibility = View.INVISIBLE

        }

        btn6paxCar.setOnClickListener {
            paxCarNo = 6

            var dist = calcDistance(originLatitude, originLongitude, destinationLatitude, destinationLongitude)
            var formatKm = DecimalFormat("#0.0000")
            totalDistance = formatKm.format(dist).toDouble()
            var strDis = "" + totalDistance + "km"

            var cost = calcCost(totalDistance, paxCarNo)
            var formatRm = DecimalFormat("##0.00")
            totalCost = formatRm.format(cost).toDouble()
            var strCost = "RM " + totalCost

            totalTime = calcTime(totalDistance)
            var strTime = "" +totalTime + " minutes"


            //open confirm book page
            tvDistCost.text = location + "\n" + strDis + "\n" + strCost + "\n" + strTime
            tvDistCost.visibility = View.VISIBLE
            btnCancelBooking.visibility = View.VISIBLE
            btnBookCar.visibility = View.VISIBLE

            //close select car pax page
            rlCarPax.visibility = View.INVISIBLE
            //btn4paxCar.visibility = View.INVISIBLE
            //btn6paxCar.visibility = View.INVISIBLE
        }

        val btnCnlBook = findViewById<Button>(R.id.btnCancelBooking)
        btnCnlBook.setOnClickListener{

            val locationSearch = findViewById<EditText>(R.id.etLocation)
            val tvDNC = findViewById<TextView>(R.id.tvBookInfo)
            val btnBkCar = findViewById<Button>(R.id.btnBookCar)

            mapFragment.getMapAsync {
                mMap = it

                mMap!!.clear()
                tvDNC.visibility = View.INVISIBLE
                btnCnlBook.visibility = View.INVISIBLE
                btnBkCar.visibility = View.INVISIBLE

                //open search location page
                btnSearchLocation.visibility = View.VISIBLE
                locationSearch.visibility = View.VISIBLE

                val originLocation = LatLng(originLatitude, originLongitude)
                mMap!!.addMarker(MarkerOptions().position(originLocation)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 15F))
            }
        }
        val btnBkCar = findViewById<Button>(R.id.btnBookCar)
        btnBkCar.setOnClickListener{

            //get user id
            val user = auth.currentUser
            val userReference = databaseReference?.child(user?.uid!!)

            //success to payment
            var privateCarId = "PCID00006"

            val newPrivateCar = PrivateCarData(privateCarId,originLatitude
                ,originLongitude,destinationLatitude,destinationLongitude
                ,paxCarNo, totalTime, totalDistance,totalCost,1, userReference.toString(), false, false)
            addNewPrivateCar(newPrivateCar)

            val intent = Intent(this@GoogleMapActivity, BookingCarActivity::class.java)
            intent.putExtra("privateCarId", privateCarId)
            startActivity(intent)

        }

        val btnUserProfile = findViewById<Button>(R.id.btnUserProfile)
        btnUserProfile.setOnClickListener {
            val intent = Intent(this@GoogleMapActivity, UserProfileActivity::class.java)
            startActivity(intent)

        }


        //here
        tvDistCost.visibility = View.INVISIBLE
        btnCancelBooking.visibility = View.INVISIBLE
        btnBookCar.visibility = View.INVISIBLE

        val rlDriverProfile = findViewById<RelativeLayout>(R.id.rlDriverProfile)
        val tvMsg = findViewById<TextView>(R.id.tvMsg)
        val ivDriverPic = findViewById<ImageView>(R.id.ivDriverPic)
        val tvMsg2 = findViewById<TextView>(R.id.tvMsg2)
        val tvDriverName = findViewById<TextView>(R.id.tvDriverName)
        val tvCarPlate = findViewById<TextView>(R.id.tvCarPlate)
        val tvCarModel = findViewById<TextView>(R.id.tvCarModel)
        val btnCallDriver = findViewById<ImageButton>(R.id.btnCallDriver)
        val btnGoToPayment = findViewById<Button>(R.id.btnGoToPayment)

        //rlDriverProfile.visibility = View.VISIBLE
        /*
        tvMsg.visibility = View.VISIBLE
        ivDriverPic.visibility = View.VISIBLE
        tvMsg2.visibility = View.VISIBLE
        tvDriverName.visibility = View.VISIBLE
        tvCarPlate.visibility = View.VISIBLE
        tvCarModel.visibility = View.VISIBLE
        btnCallDriver.visibility = View.VISIBLE
        btnGoToPayment.visibility = View.VISIBLE
        */

    }
    private fun addNewPrivateCar(newPrivateCar: PrivateCarData) {
//        database.child("PrivateCar").child(newPrivateCar.privateCarId)
//            .setValue(newPrivateCar).addOnSuccessListener {
//                binding.tvStatus.text = "Added"
//            }
//            .addOnFailureListener { e ->
//                binding.tvStatus.text = e.message
//            }
        database.child(newPrivateCar.privateCarId)
            .setValue(newPrivateCar).addOnSuccessListener {
                Toast.makeText(applicationContext, "Added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        val originLocation = LatLng(originLatitude, originLongitude)
        mMap!!.clear()
        mMap!!.addMarker(MarkerOptions().position(originLocation)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 15F))

    }


    private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
            "&destination=${dest.latitude},${dest.longitude}" +
            "&sensor=false" +
            "&mode=driving" +
            "&key=$secret"
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()

            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.GREEN)
                lineoption.geodesic(true)
            }
            mMap!!.addPolyline(lineoption)
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    private fun calcDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515 * 2
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    private fun calcCost(distance: Double, pax: Int): Double{
        val cost = distance * 1.2 * pax
        return cost
    }

    private fun calcTime(distance: Double): Int{
        val timeH = distance / 25//25km/h
        val timeM = timeH * 60 //hour to mins
        val timeT = timeM + 10 //starting point

        val time = timeT.toInt()
        return time
    }


    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun loadUserProfile() {

        val user = auth.currentUser
        val userReference = databaseReference?.child(user?.uid!!)


    }

}