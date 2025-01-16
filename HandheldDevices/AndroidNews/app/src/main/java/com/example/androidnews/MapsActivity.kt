package com.example.androidnews

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var recyclerView: RecyclerView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sharedPrefs: SharedPreferences = getSharedPreferences(
            "Android News",
            Context.MODE_PRIVATE
        )

        val latitude: Long = sharedPrefs.getLong("SAVED_LATITUDE", 0L)
        val longitude: Long = sharedPrefs.getLong("SAVED_LONGITUDE", 0L)
        val lastAddress: String? = sharedPrefs.getString("SAVED_LOCATION", "")
        val lastLocation = LatLng(latitude.toDouble(), longitude.toDouble())
        mMap.addMarker(MarkerOptions().position(lastLocation).title(lastAddress))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation))

        mMap.setOnMapLongClickListener { coords: LatLng ->
            mMap.clear()

            doAsync {
                val geocoder = Geocoder(this@MapsActivity)
                val results: List<Address> = try {
                    geocoder.getFromLocation(
                        coords.latitude,
                        coords.longitude,
                        10
                    )
                } catch (exception: Exception) {
                    Log.e("MapsActivity", "Geocoding failed", exception)
                    listOf<Address>()
                }
                runOnUiThread{
                    if(results.isNotEmpty()) {
                        val firstResult: Address = results.first()
                        val streetAddress: String = firstResult.getAddressLine(0)
                        var areaSearch: String = ""
                        sharedPrefs.edit().putString("SAVED_LOCATION", streetAddress).apply()
                        sharedPrefs.edit().putLong("SAVED_LATITUDE", coords.latitude.toLong()).apply()
                        sharedPrefs.edit().putLong("SAVED_LONGITUDE", coords.longitude.toLong()).apply()

                        if(firstResult.countryName.toString() == "United States"){
                            val city = firstResult.locality ?: "Unknown"
                            areaSearch = city.toString()
                        }
                        else{
                            val country: String = firstResult.countryName ?: "Unknown"
                            areaSearch = country.toString()
                        }

                        mMap.addMarker(MarkerOptions().position(coords).title(streetAddress))

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(coords))

                        recyclerView = findViewById(R.id.mapRecyclerView)
                        val newsManager = NewsManager()
                        try {

                            val news = newsManager.getNews(
                                urlContent = "everything?qInTitle=$areaSearch&language=en"
                            )

                            textView = findViewById(R.id.mapTextView)
                            val adapter = ArticlesAdapter(news)

                            recyclerView.adapter = adapter
                            recyclerView.layoutManager = LinearLayoutManager(this@MapsActivity, LinearLayoutManager.HORIZONTAL,false)
                            val localSearch = "News Near $areaSearch"
                            textView.text = localSearch

                        } catch (exception: java.lang.Exception) {
                            Toast.makeText(
                                this@MapsActivity,
                                "Couldn't load news!",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
                    else {
                        Log.e("MapsActivity", "Geocoding failed or returned no results")
                        runOnUiThread {
                            Toast.makeText(
                                this@MapsActivity,
                                "No results for location!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

            }
        }
    }
}