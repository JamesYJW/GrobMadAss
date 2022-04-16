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
import android.os.AsyncTask
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.IOException

import android.graphics.Color
import android.icu.text.DecimalFormat
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback{

    private var mMap: GoogleMap? = null
    //private lateinit var mLastLocation: Location
    //private var mCurrLocationMarker: Marker? = null
    //private var mGoogleApiClient: GoogleApiClient? = null
    //private lateinit var mLocationRequest: LocationRequest



    private var originLatitude: Double = 3.2023
    private var originLongitude: Double = 101.7166
    private var destinationLatitude: Double = 3.2023
    private var destinationLongitude: Double = 101.7166


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)


        // Fetching API_KEY which we wrapped
        /*
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["com.google.android.geo.API_KEY"]
        val apiKey = value.toString()

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
*/
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // Map Fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val gd = findViewById<Button>(R.id.btnDirection)
        gd.setOnClickListener{

            closeKeyBoard()

            mapFragment.getMapAsync {
                mMap = it
                val originLocation = LatLng(originLatitude, originLongitude)
                mMap!!.addMarker(MarkerOptions().position(originLocation))

                //search
                val locationSearch:EditText = findViewById<EditText>(R.id.edLocation)
                var location: String? = null
                location = locationSearch.text.toString()
                var addressList: List<Address>? = null

                if (location == null || location == "") {
                    Toast.makeText(applicationContext,"provide location",Toast.LENGTH_SHORT).show()
                }
                else {
                    val geoCoder = Geocoder(this)
                    try {
                        addressList = geoCoder.getFromLocationName(location, 1)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val address = addressList!![0]
                    destinationLatitude = address.latitude.toDouble()
                    destinationLongitude = address.longitude.toDouble()

                    //destination
                    val destinationLocation = LatLng(destinationLatitude, destinationLongitude)
                    mMap!!.addMarker(MarkerOptions().position(destinationLocation))

                    //get direction
                    //val urll = getDirectionURL(originLocation, destinationLocation, apiKey)
                    val urll = getDirectionURL(originLocation, destinationLocation)
                    GetDirection(urll).execute()
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 14F))


                    var distance = calcDistance(originLatitude, originLongitude, destinationLatitude, destinationLongitude)
                    val formatKm = DecimalFormat("#0.0000")
                    var strDis = "" + formatKm.format(distance) + "km"

                    var cost = calcCost(distance)
                    val formatRm = DecimalFormat("##0.00")
                    var strCost = "RM " + formatRm.format(cost)

                    val d2 = findViewById<TextView>(R.id.tvDistCost)
                    d2.visibility = View.VISIBLE
                    d2.text = strDis + "\n" + strCost


                    //Log.e("TAG", strDis);
                    /*
                    Snackbar.make(
                        findViewById(R.id.mapRel),
                        strDis,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    */

                }

            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap!!
        val originLocation = LatLng(originLatitude, originLongitude)
        mMap!!.clear()
        mMap!!.addMarker(MarkerOptions().position(originLocation))
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 15F))

    }

    //private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{
    private fun getDirectionURL(origin:LatLng, dest:LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
            "&destination=${dest.latitude},${dest.longitude}" +
            "&sensor=false" +
            "&mode=driving"
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
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    private fun calcCost(distance: Double): Double{
        val cost = distance * 0.7
        return cost
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}