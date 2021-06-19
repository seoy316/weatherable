package kr.ac.kumoh.s.weatherable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kr.ac.kumoh.s.weatherable.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    var tour_x : Double? =  0.0// 경도 lon
    var tour_y : Double? = 0.0// 위도 lat
    var my_x : Double? =  0.0// 경도 lon
    var my_y : Double? = 0.0// 위도 lat
    var tour_name : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        tour_x = intent.getDoubleExtra("tour_x", 128.39352332859156)
        tour_y = intent.getDoubleExtra("tour_y",36.146244417703564)
        my_x = intent.getDoubleExtra("my_x", 128.39352332859156)
        my_y = intent.getDoubleExtra("my_y",36.146244417703564)
        tour_name = intent.getStringExtra("name").toString()
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

        // Add a marker in Sydney and move the camera
        val tour_location = LatLng(tour_y!!, tour_x!!)
        mMap.addMarker(MarkerOptions().position(tour_location).title(tour_name))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tour_location))

        val my_location = LatLng(my_y!!, my_x!!)
        mMap.addMarker(MarkerOptions()
            .position(my_location).title("내 위치")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tour_location,13F))
    }
}