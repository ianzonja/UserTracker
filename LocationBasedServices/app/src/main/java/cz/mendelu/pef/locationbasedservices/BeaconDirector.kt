package cz.mendelu.pef.locationbasedservices

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.RemoteException
import android.util.Log
import org.altbeacon.beacon.*
import java.util.*
import org.altbeacon.beacon.MonitorNotifier



/**
 * The main class for managing the beacons. It uses the Android Beacon Library
 * (https://altbeacon.github.io/android-beacon-library/) to find the beacons.
 *
 */
class BeaconDirector (private val context: Context,
                      private var beaconsListener: BeaconsListener?) : BeaconConsumer, BeaconConnection{

    private var beaconManager: BeaconManager
    private var beaconList: MutableList<Beacon>? = mutableListOf()
    private var beaconUUID: String = ""

    private var region: Region? = null
    private var rangeNotifier: RangeNotifier? = null
    var monitorInBackground: Boolean = false

    init {
        this.beaconManager = BeaconManager.getInstanceForApplication(context)
        this.beaconManager.beaconParsers.clear()
        this.beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconTech.ALTBEACON.code))


    }

    /**
     * Start discovering beacons in region
     */
    override fun startDiscovering(uuid: String) {
        beaconUUID = uuid
        beaconManager.bind(this)
        /*var region = Region("region", Identifier.parse("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6"), Identifier.parse("1"), Identifier.parse("2"))
        beaconManager.startMonitoringBeaconsInRegion(region);*/
    }

    /**
     * Stop discovering beacons
     */
    override fun stopDiscovering() {
        beaconManager.unbind(this)
        if (!monitorInBackground) {
            if (region != null){
                try {
                    beaconManager.stopRangingBeaconsInRegion(region!!)
                    beaconManager.removeRangeNotifier(rangeNotifier!!)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Stop discovering beacons, ignore @param monitorInBackground
     */
    override fun killDiscovering() {
        monitorInBackground = false
        stopDiscovering()
    }

    override fun onBeaconServiceConnect() {
        beaconManager.removeAllMonitorNotifiers()
        beaconManager.addMonitorNotifier(object : MonitorNotifier {
            override fun didEnterRegion(region: Region) {
                Log.i(TAG, "I just saw an beacon for the first time!")
            }

            override fun didExitRegion(region: Region) {
                Log.i(TAG, "I no longer see an beacon")
            }

            override fun didDetermineStateForRegion(state: Int, region: Region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: $state")
            }
        })

        rangeNotifier = RangeNotifier { beacons, region ->
            val foundBeacons = ArrayList<Beacon>()

            if (beacons.isEmpty() || beaconList == null) {
//                beaconsListener!!.userLocationFound(null, null)
                return@RangeNotifier
            }


            for (beacon in beacons) {
                for (myBeacon in beaconList!!) {
                    println("adssad")
                    if (beacon.id1.toString().equals(beaconUUID)) {
                        beaconsListener!!.foundBeacons(beacon.rssi, beacon.txPower)
                    }
                }
            }
        }
        try {
            region = Region("region", null, null, null)
            beaconManager.startRangingBeaconsInRegion(region!!)
            beaconManager.addRangeNotifier(rangeNotifier!!)
//            beaconsListener!!.onServiceConnected()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }

    override fun getApplicationContext(): Context {
        return context.applicationContext
    }

    override fun unbindService(serviceConnection: ServiceConnection) {}

    override fun bindService(intent: Intent, serviceConnection: ServiceConnection, i: Int): Boolean {
        return false
    }

    fun addBeacon(beacon: Beacon) {
        if (beaconList == null) {
            beaconList = mutableListOf()
        }
        beaconList!!.add(beacon)
    }

    fun addBeacons(beacons: List<Beacon>) {
        if (beaconList == null) {
            beaconList = mutableListOf()
        }
        beaconList!!.addAll(beacons)
    }

    fun clearBeacons() {
        if (beaconList != null) {
            beaconList!!.clear()
        }
    }

    fun removeBeaconsListener() {
        this.beaconsListener = null
    }


}
