package cz.mendelu.pef.locationbasedservices

import android.os.Parcel
import android.os.Parcelable

data class Friend(
    var image: String,
    var name: String,
    var surname: String,
    var email: String,
    var id: String
){

}