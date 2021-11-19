package com.pablodb.excercisemap.magia

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.pablodb.excercisemap.utils.Constantes
import org.json.JSONObject

class MagiaControlador {
    companion object{
        private val TAG = "MagiaControlador"

        fun getOptions(context : Context, text : String, next : (JSONObject) -> Unit){
            val url = "${Constantes.URL_AUTOCOMPLETE_REQUEST}input=${text}" +
                    "&types=geocode" +
                    "&key=${Constantes.GOOGLE_API_KEY}"
            Log.i(TAG, "url = $url")

            Volley.newRequestQueue( context ).add( JsonObjectRequest(Request.Method.GET, url, JSONObject(),{ result ->
                Log.i(TAG, "result = $result")
                next( result )
            },{ error ->
                Log.i(TAG, "error = $error")
            }) )
        }

        fun getDetail( context : Context, obj : JSONObject, next: (JSONObject) -> Unit){
            val url = "${Constantes.URL_DETAILS_REQUEST}" +
                    "place_id=${obj.getString("place_id")}" +
                    "&key=${Constantes.GOOGLE_API_KEY}"
            Log.i(TAG, "url = $url")

            Volley.newRequestQueue( context ).add( JsonObjectRequest(Request.Method.GET, url, JSONObject(),{ result ->
                Log.i(TAG, "result = $result")
                next( result )

            },{ error ->
                Log.i(TAG, "error = $error")
            }) )
        }

        fun getRoute( context : Context, destination : String, origin : String, next : (ArrayList<LatLng>) -> Unit){
            val locs = ArrayList<LatLng>()
            val url2 = "${Constantes.URL_DIRECTIONS_REQUEST}" +
                    "destination=$destination" +
                    "&origin=$origin" +
                    "&key=${Constantes.GOOGLE_API_KEY}"
            Volley.newRequestQueue( context ).add( JsonObjectRequest(Request.Method.GET, url2, JSONObject(),
                {
                    Log.i(TAG, "result = $it")
                    val steps = ((it.getJSONArray("routes")[0] as JSONObject)
                    .getJSONArray("legs")[0] as JSONObject).getJSONArray("steps")

                    for( i in 0..(steps.length()-1)){
                        val step = (steps[i] as JSONObject).getJSONObject("end_location")
                        locs.add( LatLng( step.getDouble("lat"), step.getDouble("lng") ) )
                    }

                    next( locs )
                },{
                    Log.i(TAG, "error = $it")
                }) )
        }
    }
}