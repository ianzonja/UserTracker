package cz.mendelu.pef. locationbasedservices

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.activity_add_place.*
import kotlinx.android.synthetic.main.content_add_place.*
import kotlinx.android.synthetic.main.content_place_detail.*

class AddPlaceActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context, latitude: Double, longitude: Double): Intent {
            val intent: Intent = Intent(context, AddPlaceActivity::class.java)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longitude", longitude)
            return intent
        }
    }

    private lateinit var firebaseServer: FirebaseServer
    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        firebaseServer = FirebaseServer(this)

        latitude = intent.getDoubleExtra("latitude", -1.toDouble())
        longitude = intent.getDoubleExtra("longitude", -1.toDouble())

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_add_place, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_done -> {
                savePlace()
                true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun savePlace(){

        if (!latitude?.equals(-1)!!
            && !longitude?.equals(-1)!!
            && placeNameInput.text.isNotEmpty()){

            val place = Place(placeNameInput.text.toString())
            place.latitude = latitude
            place.longitude = longitude

            val key = "place" + System.currentTimeMillis()
            firebaseServer.sendDataToFirebase(
                firebaseServer.getDatabaseReference().child("places_list"),
                key,
                place,
                object : ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        if (placeDescriptionInput.text.isNotEmpty()){
                            place.description = placeDescriptionInput.text.toString()
                        }
                        firebaseServer.sendDataToFirebase(firebaseServer.getDatabaseReference().child("places"), key, place,
                            object : ValueEventListener{
                            override fun onDataChange(p0: DataSnapshot) {
                                val intent: Intent = Intent()
                                intent.putExtra("placeID", key)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }
                        })
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }
                })

        }
    }



}
