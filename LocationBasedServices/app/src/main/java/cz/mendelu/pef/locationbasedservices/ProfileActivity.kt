package cz.mendelu.pef.locationbasedservices

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_place.*
import kotlinx.android.synthetic.main.content_add_place.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var firebaseServer: FirebaseServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //toolbar.setNavigationOnClickListener { finish() }

        firebaseServer = FirebaseServer(this)


    }
}