package cz.mendelu.pef.locationbasedservices

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import java.util.*

class TransmitAsBeacon {
    companion object{
        fun start(context: Context, beaconUUID: String?){
            var long : Long
            long = 0L
            val beacon = Beacon.Builder()
                .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
                .setId2("1")
                .setId3("2")
                .setManufacturer(0x0118)
                .setTxPower(-59)
                .setDataFields(Arrays.asList(long))
                .build()
            val beaconParser = BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")
            val beaconTransmitter = BeaconTransmitter(context, beaconParser)
            beaconTransmitter.startAdvertising(beacon)
        }
    }
}