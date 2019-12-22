package cz.mendelu.pef.locationbasedservices

/**
 * The class representing a single beacon.
 * We will be specifically using this class and not the class from Android Beacon Library.
 */
data class Beacon(
        var major: Int? = null, var minor: Int? = null) {

}