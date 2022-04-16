package com.example.grobmadass

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.grobmadass.databinding.ActivityGoogleMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.model.Marker
import android.location.Location
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import java.io.IOException

import android.graphics.Color
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class GoogleMapActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private var mMap: GoogleMap? = null
    private lateinit var mLastLocation: Location
    private var mCurrLocationMarker: Marker? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var mLocationRequest: LocationRequest


    private var originLatitude: Double = 3.2167087
    private var originLongitude: Double = 101.72497
    private var destinationLatitude: Double = 3.2057
    private var destinationLongitude: Double = 101.7318
    /*

    private lateinit var m1Map: GoogleMap
    private lateinit var binding: ActivityGoogleMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)
/*
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding = ActivityGoogleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

 */

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

        val gd = findViewById<Button>(R.id.directions)
        gd.setOnClickListener{


            mapFragment.getMapAsync {
                mMap = it
                val originLocation = LatLng(originLatitude, originLongitude)
                mMap!!.addMarker(MarkerOptions().position(originLocation))

                //search
                val locationSearch:EditText = findViewById<EditText>(R.id.s_location)
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

                    //hardcode
                    val destinationLocation = LatLng(destinationLatitude, destinationLongitude)
                    mMap!!.addMarker(MarkerOptions().position(destinationLocation))
                    val urll = getDirectionURL(originLocation, destinationLocation, apiKey)
                    GetDirection(urll).execute()
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 14F))
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


        /*
        mMap = googleMap
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }
        */

    }

    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(bundle: Bundle?) {

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this)
        }
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onLocationChanged(location: Location) {

        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
        //Place current location marker
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        mCurrLocationMarker = mMap!!.addMarker(markerOptions)

        //move map camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15f))

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.getFusedLocationProviderClient(this)
        }

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }
/*
    fun searchLocation(view: View) {
        val locationSearch:EditText = findViewById<EditText>(R.id.s_location)
        var location: String? = null
        location = locationSearch.text.toString()
        var addressList: List<Address>? = null

        if (location == null || location == "") {
            Toast.makeText(applicationContext,"provide location",Toast.LENGTH_SHORT).show()
        }
        else{
            val geoCoder = Geocoder(this)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)

            } catch (e: IOException) {
                e.printStackTrace()
            }
            val address = addressList!![0]
            //val latLng = LatLng(address.latitude, address.longitude)

            //destinationLatitude = address.latitude
            //destinationLongitude = address.longitude

            //mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
            //mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            //Toast.makeText(applicationContext, address.latitude.toString() + " " + address.longitude, Toast.LENGTH_LONG).show()
        }
    }
*/
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

}