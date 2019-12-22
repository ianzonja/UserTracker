package cz.mendelu.pef.locationbasedservices

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.DisplayMetrics
import android.view.LayoutInflater
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import de.hdodenhof.circleimageview.CircleImageView

class MarkerManager {

    companion object {
        fun addMarkerToMap(context: Context, map: GoogleMap, place: SharedLocation): Marker {
            val options = MarkerOptions()
            options.position(LatLng(place.latitude as Double, place.longitude as Double))
            return map.addMarker(options)
        }

        fun addMarkerToMap(context: Activity, map: GoogleMap, place: SharedLocation, imageResource: Int): Marker {

            val options = MarkerOptions()
            options.position(LatLng((place.latitude.toDouble()), place.longitude.toDouble()))
            options.icon(
                BitmapDescriptorFactory.fromBitmap(createCustomMarker(context, imageResource))
            )
            var marker: Marker = map.addMarker(options)
            marker.tag = place.id
            return marker
        }

        fun addMarkerToMap(context: Activity, map: GoogleMap, latitude: Double, longitude: Double, imageResource: Int): Marker {

            val options = MarkerOptions()
            options.position(LatLng(latitude, longitude as Double))
            options.icon(
                BitmapDescriptorFactory.fromBitmap(createCustomMarker(context, imageResource))
            )
            var marker: Marker = map.addMarker(options)
            marker.tag = "your location"
            return marker
        }


        fun createCustomMarker(context: Activity, imageResource: Int): Bitmap {

            val markerView = LayoutInflater.from(context).inflate(R.layout.marker_layout, null)
            val image = markerView.findViewById<CircleImageView>(R.id.profile_image)
            image.setImageResource(imageResource)

            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)

            markerView.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
            markerView.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
            markerView.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(
                markerView.getMeasuredWidth(),
                markerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            markerView.draw(canvas)
            return bitmap
        }
    }


}


