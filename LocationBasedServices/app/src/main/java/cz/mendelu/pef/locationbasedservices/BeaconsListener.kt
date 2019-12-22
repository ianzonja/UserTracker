package cz.mendelu.pef.locationbasedservices

import java.util.ArrayList

/**
 * The main interface for communication between activity and beacons.
 */
interface BeaconsListener {

    /**
     * Called when added beacons are found
     */
    fun foundBeacons(ssri: Int, txPower: Int)

}