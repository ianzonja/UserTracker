package cz.mendelu.pef.locationbasedservices

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.content_place_detail.*

class PlaceDetailActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context, placeId: String): Intent{
            val intent: Intent = Intent(context, PlaceDetailActivity::class.java)
            intent.putExtra("placeID", placeId)
            return intent
        }
    }

    private lateinit var place: Place
    private lateinit var firebaseServer: FirebaseServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        supportActionBar.apply {
            title = ""
        }

        firebaseServer = FirebaseServer(this)
        var placeId = intent.getStringExtra("placeID")
        firebaseServer.getDatabaseReference().child("places").child(placeId).addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                place = p0.getValue(Place::class.java)!!
                setLayoutValues()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun setLayoutValues(){

        supportActionBar.apply {
            title = place.name
        }

        place.name?.let {
            placeName.text = it
        }

        place.description?.let {
            placeDescription.text = it
        }?:kotlin.run {
            placeDescription.visibility = View.GONE
        }
    }

}
