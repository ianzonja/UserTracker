package cz.mendelu.pef.locationbasedservices

import android.Manifest
import android.Manifest.permission
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import androidx.core.app.ActivityCompat
import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


/**
 * The utility for checking permissions. Since Android 6.0, google introduces runtime permissions.
 * This means, that we need to ask a user for the permission when a scecific permission is needed.
 * https://developer.android.com/training/permissions/requesting
 */
class PermissionUtil {

    companion object {

        /**
         * Requests location permission
         */
        fun requestLocationPermission(context: AppCompatActivity, requestCode: Int) {
            requestPermissions(context, requestCode, Manifest.permission.ACCESS_FINE_LOCATION)
        }

        /**
         * Checks for location permission
         */
        fun checkLocationPermission(context: AppCompatActivity): Boolean
                = checkPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)


        /**
         * Checks permission
         * @param context context
         * @return returns true if permission is granted
         */
        private fun checkPermissions(context: AppCompatActivity, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }


        /**
         * Requests permission from the user.
         * @param context context
         * @param requestCode the permission request code.
         */
        private fun requestPermissions(context: Activity, requestCode: Int, permission: String) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(permission),
                requestCode
            )

        }
    }

}