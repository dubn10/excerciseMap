package com.pablodb.excercisemap.activitys

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pablodb.excercisemap.R
import com.pablodb.excercisemap.adapters.SearchOptionsAdapter
import com.pablodb.excercisemap.databinding.ActivityMapsBinding
import com.pablodb.excercisemap.magia.MagiaControlador
import com.pablodb.excercisemap.utils.Constantes
import org.json.JSONArray
import org.json.JSONObject
import com.google.android.gms.maps.model.PolylineOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = "MapsActivity"
    private lateinit var context : Context
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var queue : RequestQueue
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var predictions = JSONArray()
    private var optionsAdapter : SearchOptionsAdapter? = null
    private var location : Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                .requestPermissions( this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION) ,
                    123 );
        }
        else {
            Toast.makeText( context, "Permission already granted", Toast.LENGTH_SHORT).show();
            initElements()
        }

        initListeners()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        initElements()
    }

    fun initElements(){
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        queue = Volley.newRequestQueue( context )
        binding.agvOptions.numColumns = 1
        optionsAdapter = SearchOptionsAdapter( context, predictions )
        binding.agvOptions.adapter = optionsAdapter
    }

    fun initListeners(){
        binding.agvOptions.setOnItemClickListener { parent, view, position, id ->
            Log.i(TAG, "click item")
            val obj = predictions[position] as JSONObject
            mMap.clear()

            MagiaControlador.getDetail( context, obj){ result ->
                val geometry_location = result.getJSONObject("result")
                    .getJSONObject("geometry").getJSONObject("location")
                val lat = geometry_location.getDouble("lat")
                val lng = geometry_location.getDouble("lng")

                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng( lat, lng )
                    ).title(result.getJSONObject("result").getString("formatted_address")))

                MagiaControlador.getRoute( context, "$lat%2C$lng",
                    "${location?.latitude}%2C${location?.longitude}"){
                    val options = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
                    options.addAll( it )
                    mMap.addPolyline( options )
                }
            }

            predictions = JSONArray()
            updateOptions()
        }

        binding.etSearchBox.addTextChangedListener { text ->
            MagiaControlador.getOptions( context, text.toString() ){ result ->
                predictions = result.getJSONArray("predictions")
                updateOptions()
            }
        }
    }

    fun updateOptions(){
        runOnUiThread {
            optionsAdapter!!.dataSet = predictions
            optionsAdapter!!.notifyDataSetChanged()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        val sydney = LatLng(19.432241, -99.177254)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(8f))

        if (mMap != null) {
            mMap.setOnMyLocationChangeListener { arg0 ->
                Log.i(TAG, "arg0 = ${arg0.latitude}, ${arg0.longitude}")
                location = arg0
            }
        }
        //mMap.moveCamera( CameraUpdateFactory.newLatLng(LatLng(mMap.myLocation.latitude, mMap.myLocation.longitude)) )
    }
}