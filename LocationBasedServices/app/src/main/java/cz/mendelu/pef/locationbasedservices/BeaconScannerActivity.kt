package cz.mendelu.pef.locationbasedservices

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_beacon_content.*
import kotlinx.android.synthetic.main.activity_beacon_scanner.*
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import java.util.*


class BeaconScannerActivity: AppCompatActivity(), BeaconsListener {


    private val LOCATION_PERMISSION_REQUEST_CODE: Int = 100
    private lateinit var beaconDirector: BeaconDirector
    private var result: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon_scanner)
        setSupportActionBar(toolbar)

        beaconDirector = BeaconDirector(this, this)
        beaconDirector.addBeacon(Beacon(1, 2))
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

    /*override fun onServiceConnected() {
        println("im here")
    }

    override fun userLocationFound(latitude: Double?, longitude: Double?) {
        println("im here")
    }*/

    override fun foundBeacons(rssi: Int, txPower: Int) {
        val ratio = rssi * 1.0 / txPower
        if (ratio < 1.0) {
            result = Math.pow(ratio, 10.0)
        } else {
            result = 0.89976 * Math.pow(ratio, 7.7095) + 0.111
        }
        if(result!=0.0)
            distance.setText(result.toString())
    }

    fun start(v: View){
        if (PermissionUtil.checkLocationPermission(this)) {
//            beaconDirector.startDiscovering()
        } else {
            PermissionUtil.requestLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    fun stop(v: View){
        beaconDirector.stopDiscovering()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//            beaconDirector.startDiscovering()
        }
    }

    fun share(view: View) {
        var long : Long
        long = 0L
        val beacon = org.altbeacon.beacon.Beacon.Builder()
            .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
            .setId2("1")
            .setId3("2")
            .setManufacturer(0x0118)
            .setTxPower(-59)
            .setDataFields(Arrays.asList(long))
            .build()
        val beaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")
        val beaconTransmitter = BeaconTransmitter(this, beaconParser)
        beaconTransmitter.startAdvertising(beacon)
    }
}