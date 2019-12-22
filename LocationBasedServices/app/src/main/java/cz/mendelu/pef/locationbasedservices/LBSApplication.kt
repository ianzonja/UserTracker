package cz.mendelu.pef.locationbasedservices

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class LBSApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

    }
}