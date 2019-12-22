package cz.mendelu.pef.locationbasedservices

/**
 * The interface for starting and stopping the search for beacons.
 */
interface BeaconConnection {

    /**
     * Starts looking for beacons
     */
    fun startDiscovering(uuid: String)

    /**
     * Stops looking for beacons
     */
    fun stopDiscovering()

    /**
     * Kills the looking for beacons completely.
     */
    fun killDiscovering()
}