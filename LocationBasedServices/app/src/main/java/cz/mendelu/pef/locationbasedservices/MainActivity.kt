package cz.mendelu.pef.locationbasedservices

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.material.navigation.NavigationView
import android.R.id.toggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.friends_activity.*


class MainActivity : AppCompatActivity()/*, OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener */{

    private lateinit var mMap: GoogleMap
    private lateinit var firebaseServer: FirebaseServer
    private val ADD_PLACE_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        firebaseServer = FirebaseServer(this)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
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
    /*override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener(this)

        mMap.uiSettings.isMapToolbarEnabled = false


        firebaseServer.getDatabaseReference().child("places_list").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (placeDS in dataSnapshot.children){
                    val place: Place? = placeDS.getValue(Place::class.java)
                    place!!.placeId = placeDS.key
                    MarkerManager.addMarkerToMap(this@MainActivity, mMap, place, R.drawable.mendelu)
                }

                firebaseServer.getDatabaseReference().removeEventListener(this)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })


        val brno = LatLng(49.210739, 16.618021)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brno, 16.0F))
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        startActivity(PlaceDetailActivity.createIntent(this, marker!!.tag as String))
        return false
    }

    override fun onMapClick(position: LatLng?) {

        startActivityForResult(
            AddPlaceActivity.createIntent(this, position!!.latitude, position!!.longitude), ADD_PLACE_REQUEST_CODE

        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            firebaseServer.getDatabaseReference().child("places_list").child(data!!.getStringExtra("placeID")).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val place: Place? = dataSnapshot.getValue(Place::class.java)
                    place!!.placeId = dataSnapshot.key
                    MarkerManager.addMarkerToMap(this@MainActivity, mMap, place, R.drawable.mendelu)
                    firebaseServer.getDatabaseReference().removeEventListener(this)
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }
    }*/
    /*private lateinit var mMap: GoogleMap
    private lateinit var firebaseServer: FirebaseServer
    private val ADD_PLACE_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setSupportActionBar(toolbar)


        firebaseServer = FirebaseServer(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val intent = Intent(this, AddPlaceActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.*/

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapClickListener(this)

        mMap.uiSettings.isMapToolbarEnabled = false


        firebaseServer.getDatabaseReference().child("places_list").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (placeDS in dataSnapshot.children){
                    val place: Place? = placeDS.getValue(Place::class.java)
                    place!!.placeId = placeDS.key
                    MarkerManager.addMarkerToMap(this@MainActivity, mMap, place, R.drawable.mendelu)
                }

                firebaseServer.getDatabaseReference().removeEventListener(this)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })


        val brno = LatLng(49.210739, 16.618021)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brno, 16.0F))
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        startActivity(PlaceDetailActivity.createIntent(this, marker!!.tag as String))
        return false
    }

    override fun onMapClick(position: LatLng?) {

        startActivityForResult(
            AddPlaceActivity.createIntent(this, position!!.latitude, position!!.longitude), ADD_PLACE_REQUEST_CODE

        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            firebaseServer.getDatabaseReference().child("places_list").child(data!!.getStringExtra("placeID")).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val place: Place? = dataSnapshot.getValue(Place::class.java)
                    place!!.placeId = dataSnapshot.key
                    MarkerManager.addMarkerToMap(this@MainActivity, mMap, place, R.drawable.mendelu)
                    firebaseServer.getDatabaseReference().removeEventListener(this)
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }
    }*/

}
