package cz.mendelu.pef.locationbasedservices

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseServer(context: Context) {

    private lateinit var databaseReference: DatabaseReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReference = FirebaseDatabase
            .getInstance()
            .getReferenceFromUrl("https://localized-services.firebaseio.com/")
        databaseReference.keepSynced(true)

    }


    fun getDatabaseReference(): DatabaseReference {
        return databaseReference
    }

    fun sendDataToFirebase(
        reference: DatabaseReference, key: String?, objectToSend: Any, valueEventListener: ValueEventListener) {
            var firebaseKey: String? = null
            if (key == null) {
                firebaseKey = reference.push().key
            } else {
                firebaseKey = key
            }

            reference.child(firebaseKey!!).setValue(objectToSend)
            reference.addValueEventListener(valueEventListener)
    }

}