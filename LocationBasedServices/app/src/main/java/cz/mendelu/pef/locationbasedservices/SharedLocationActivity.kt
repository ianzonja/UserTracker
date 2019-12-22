package cz.mendelu.pef.locationbasedservices

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import kotlinx.android.synthetic.main.shared_location_content.*
import java.util.ArrayList


class SharedLocationActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMapClickListener, BeaconsListener {

    override fun foundBeacons(rssi: Int, txPower: Int) {
        val ratio = rssi * 1.0 / txPower
        if (ratio < 1.0) {
            result = Math.pow(ratio, 10.0)
        } else {
            result = 0.89976 * Math.pow(ratio, 7.7095) + 0.111
        }
        if(result!=0.0)
            distance_estimation.setText("Estimated distance: "+result.toString()+" metres.")
    }

    override fun onMapClick(p0: LatLng?) {
    }

    private lateinit var mMap: GoogleMap
    private lateinit var firebaseServer: FirebaseServer
    private val ADD_PLACE_REQUEST_CODE = 100
    private var friendId: String? = null
    private var userId: String? = null
    private var sharedLocation: SharedLocation? = null
    private var locationManager : LocationManager? = null
    private var myLocation: Location? = null
    private lateinit var myMarker: Marker
    private lateinit var friendMarker: Marker
    private lateinit var beaconDirector: BeaconDirector
    private var result: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_location)
        setSupportActionBar(toolbar)
        val bundle :Bundle ?=intent.extras
        if(bundle!=null){
            friendId = bundle.getString("friendId")
        }

        firebaseServer = FirebaseServer(this)
        val sharedPreferences = getSharedPreferences("SP", MODE_PRIVATE)
        userId = sharedPreferences.getString("userId", "userId")
        beaconDirector = BeaconDirector(this, this)
        beaconDirector.addBeacon(Beacon(1, 2))

        /*val intent = Intent(this, FriendsRecyclerView::class.java)
        startActivity(intent)*/
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getLocationData(listener: OnFirebaseCallback) {
        val reference = FirebaseDatabase.getInstance().getReference("sharing/$userId/$friendId")
        var sharedLocation: SharedLocation? = null
        println(friendId)
        reference.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                sharedLocation = null
                if(dataSnapshot.exists()){
                    sharedLocation = SharedLocation(dataSnapshot.child("beacon").getValue().toString(), dataSnapshot.child("latitude").getValue().toString(), dataSnapshot.child("longitude").getValue().toString(), dataSnapshot.key.toString())
                }
                listener.onCallBack(sharedLocation)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Nisam nasao mail")
            }
        })
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)

        mMap.uiSettings.isMapToolbarEnabled = false


        /*firebaseServer.getDatabaseReference().child("places_list").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (placeDS in dataSnapshot.children){
                    val place: Place? = placeDS.getValue(Place::class.java)
                    place!!.placeId = placeDS.key
                    MarkerManager.addMarkerToMap(this@SharedLocationActivity, mMap, place, R.drawable.mendelu)
                }

                firebaseServer.getDatabaseReference().removeEventListener(this)
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })*/

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        if (PermissionUtil.checkLocationPermission(this)) {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2500L, 0F, locationListener)
        } else {
            PermissionUtil.requestLocationPermission(this, 100)
        }

        getLocationData(object: OnFirebaseCallback{
            override fun onCallBack(firebaseSharedLocation: Any?) {
                if(firebaseSharedLocation == null){
                    println("sharedLocation null")
                }else{
                    sharedLocation = firebaseSharedLocation as SharedLocation
                    if(::friendMarker.isInitialized){
                        if(friendMarker != null)
                            friendMarker.remove()
                    }
                    friendMarker = MarkerManager.addMarkerToMap(this@SharedLocationActivity, mMap, sharedLocation!!, R.drawable.mendelu)
                    start()
                    calculateDistance()
                }
            }

        })


        val brno = LatLng(49.210739, 16.618021)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brno, 16.0F))
    }

    private fun calculateDistance() {
        if(::friendMarker.isInitialized && ::myMarker.isInitialized){
            if(friendMarker!=null && myMarker!=null){
                var myLat: Float = myLocation!!.latitude.toFloat()
                var myLon: Float = myLocation!!.longitude.toFloat()
                var friendLat: Float = sharedLocation!!.latitude.toFloat()
                var friendLon: Float = sharedLocation!!.longitude.toFloat()
                var distance: Double = meterDistanceBetweenPoints(myLat, myLon, friendLat, friendLon)
                distance_estimation.setText("Estimated distance: "+distance+" metres.")
                if(result > 60.0){
                    tracking_mode.setText("Tracking mode: Google maps.")
                    tracking_result.setText("Buddy not in bluetooth range. Come closer!")
                    distance_estimation.setText("Estimated distance: "+distance.toString()+" metres.")
                }else{
                    tracking_mode.setText("Tracking mode: Bluetooth scanning.")
                    tracking_result.setText("Buddy is in range. You are close!")
                    result = Math.round(result*100.0)/100.0
                    distance_estimation.setText("Estimated distance: "+result.toString()+" metres.")
                }
            }
        }
    }

    fun start(){
        beaconDirector.startDiscovering(sharedLocation!!.uuid)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtil.checkLocationPermission(this)) {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2500L, 0F, locationListener)
        }
    }
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            myLocation = location
            if(::myMarker.isInitialized){
                if(myMarker != null)
                    myMarker.remove()
            }
            myMarker = MarkerManager.addMarkerToMap(this@SharedLocationActivity, mMap, location.latitude, location.longitude, R.drawable.mendelu)
            calculateDistance()
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun meterDistanceBetweenPoints(
        lat_a: Float,
        lng_a: Float,
        lat_b: Float,
        lng_b: Float
    ): Double {
        val pk = (180f / Math.PI).toFloat()

        val a1 = lat_a / pk
        val a2 = lng_a / pk
        val b1 = lat_b / pk
        val b2 = lng_b / pk

        val t1 =
            Math.cos(a1.toDouble()) * Math.cos(a2.toDouble()) * Math.cos(b1.toDouble()) * Math.cos(
                b2.toDouble()
            )
        val t2 =
            Math.cos(a1.toDouble()) * Math.sin(a2.toDouble()) * Math.cos(b1.toDouble()) * Math.sin(
                b2.toDouble()
            )
        val t3 = Math.sin(a1.toDouble()) * Math.sin(b1.toDouble())
        val tt = Math.acos(t1 + t2 + t3)

        return 6366000 * tt
    }

    /*override fun onMarkerClick(marker: Marker?): Boolean {
        startActivity(PlaceDetailActivity.createIntent(this, marker!!.tag as String))
        return false
    }

    override fun onMapClick(position: LatLng?) {

        startActivityForResult(
            AddPlaceActivity.createIntent(this, position!!.latitude, position!!.longitude), ADD_PLACE_REQUEST_CODE

        )
    }*/

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            firebaseServer.getDatabaseReference().child("places_list").child(data!!.getStringExtra("placeID")).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val place: Place? = dataSnapshot.getValue(Place::class.java)
                    place!!.placeId = dataSnapshot.key
                    MarkerManager.addMarkerToMap(this@SharedLocationActivity, mMap, place, R.drawable.mendelu)
                    firebaseServer.getDatabaseReference().removeEventListener(this)
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })
        }
    }*/

    /*private fun getSharingLocationStatus(listener: OnFirebaseCallback){
        val reference = FirebaseDatabase.getInstance().getReference("sharing/$userId/$friendId")
        var messageList: MutableList<FirebaseMessage> = ArrayList()
        reference.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                }
                listener.onCallBack(messageList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Nisam nasao mail")
            }
        })
    }*/
}