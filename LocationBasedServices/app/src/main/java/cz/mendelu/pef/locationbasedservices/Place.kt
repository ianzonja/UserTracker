package cz.mendelu.pef.locationbasedservices

data class Place(var name: String? = null) {

    var latitude: Double? = null
    var longitude: Double? = null

    var description: String? = null

    var placeId: String? = null

    constructor() : this(null) {

    }
}