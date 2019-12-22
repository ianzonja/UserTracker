package cz.mendelu.pef.locationbasedservices

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_add_place.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.friends_activity.*
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.view.View
import com.google.firebase.database.*
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList
import org.altbeacon.beacon.BeaconTransmitter
import org.altbeacon.beacon.BeaconParser
import java.util.Arrays.asList
import org.altbeacon.beacon.Beacon
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class ChatRecyclerView : AppCompatActivity() {
    private lateinit var chatAdapter: ChatCardViewAdapter
    private var friendEmail: String? = null
    private var friendName: String? = null
    private var friendSurname: String? = null
    private var friendImage: String? = null
    private var friendId: String? = null
    private var userId: String? = null
    private var userEmail: String? = null
    private lateinit var messageList: MutableList<ChatMessage>
    private var locationManager : LocationManager? = null
    private var beaconUUID : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle :Bundle ?=intent.extras
        if(bundle!=null){
            friendEmail = bundle.getString("email")
            friendName = bundle.getString("name")
            friendSurname = bundle.getString("surname")
            friendImage = bundle.getString("image")
            friendId = bundle.getString("id")
        }
        val sharedPreferences = getSharedPreferences("SP", MODE_PRIVATE)
        userEmail = sharedPreferences.getString("email", "userEmail")
        userId = sharedPreferences.getString("userId", "userId")
        beaconUUID = sharedPreferences.getString("beaconUUID", "beaconUUID");
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)
        var firebaseMessages: MutableList<FirebaseMessage> = ArrayList()
        messageList = ArrayList()
        getAllMessages(object: OnFirebaseCallback{
            override fun onCallBack(someObject: Any?) {
                messageList.clear()
                firebaseMessages = someObject as MutableList<FirebaseMessage>
                for(fmessage in firebaseMessages){
                    if(fmessage.receiver.equals(friendEmail)){ //TODO: fix timestamp
                        var chatMessage = ChatMessage(fmessage.text, fmessage.timestamp, fmessage.sender, fmessage.receiver, "https://firebasestorage.googleapis.com/v0/b/localized-services.appspot.com/o/profileImages%2Fasdadada%40saddas.com?alt=media&token=4e7e4395-a383-4292-818b-603e4afa46f7", "Text", 0)
                        messageList.add(chatMessage)
                    }
                    if(fmessage.receiver.equals(userEmail)){
                        var chatMessage = ChatMessage(fmessage.text, fmessage.timestamp, fmessage.sender, fmessage.receiver, "https://firebasestorage.googleapis.com/v0/b/localized-services.appspot.com/o/profileImages%2Fasdadada%40saddas.com?alt=media&token=4e7e4395-a383-4292-818b-603e4afa46f7", "Text", 1)
                        messageList.add(chatMessage)
                    }
                }
                if(firebaseMessages.last().receiver.equals(userEmail) && firebaseMessages.last().text.equals("Hey, I am sharing a location. Check it out!")){
                    goToTracking.visibility = View.VISIBLE
                }else if(firebaseMessages.last().receiver.equals(userEmail) && firebaseMessages.last().text.equals("Stopped sharing the location.")){
                    goToTracking.visibility = View.INVISIBLE
                }
                recyclerview_chat.apply(){
                    var _layoutManager = LinearLayoutManager(this.context)
                    _layoutManager.stackFromEnd = true
                    layoutManager = _layoutManager
                    chatAdapter = ChatCardViewAdapter(object: ClickListener{
                        override fun onPositionClicked(view: View, position: Int){
                            /*val intent = Intent(baseContext, SharedLocationActivity::class.java)
                            startActivity(intent)*/
                        }
                    })
                    chatAdapter.submitList(messageList)
                    adapter = chatAdapter
                }
            }
        })
        //ListenForMessages()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //toolbar.setNavigationOnClickListener { finish() }
        send_button_chat_log.setOnClickListener(){
            if(!newMessageText.text.toString().equals(""))
                sendMessage(newMessageText.text.toString(), userEmail, friendEmail)
        }

        share.setOnClickListener(){
            if(share.getText().equals("Share location")){
                locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
                if (PermissionUtil.checkLocationPermission(this)) {
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2500L, 0F, locationListener)
                } else {
                    PermissionUtil.requestLocationPermission(this, 100)
                }
                sendMessage("Hey, I am sharing a location. Check it out!", userEmail, friendEmail)
                share.setText("Stop sharing");
            }else{
                sendMessage("Stopped sharing the location.", userEmail, friendEmail)
                share.setText("Share location");
            }
        }

        goToTracking.setOnClickListener(){
            if(goToTracking.visibility == View.VISIBLE){
                val intent = Intent(baseContext, SharedLocationActivity::class.java)
                intent.putExtra("friendId", friendId)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtil.checkLocationPermission(this)) {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2500L, 0F, locationListener)
        }
    }
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            shareLocation(location.longitude, location.latitude)
            println("Location: " + location.longitude + ":" + location.latitude)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun shareLocation(longitude: Double, latitude: Double){

        if (!latitude?.equals(-1)
            && !longitude?.equals(-1)){
            transmitAsBeacon()
            shareLocationAndTransmitingData(longitude, latitude, beaconUUID)
        }
    }

    private fun transmitAsBeacon() {
        var long : Long
        long = 0L
        newMessageText.setText(beaconUUID)
        val beacon = org.altbeacon.beacon.Beacon.Builder()
            .setId1(beaconUUID)
            .setId2("1")
            .setId3("2")
            .setManufacturer(0x0118)
            .setTxPower(-59)
            .setDataFields(Arrays.asList(long))
            .build()
        val beaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")
        val beaconTransmitter = BeaconTransmitter(this, beaconParser)
        beaconTransmitter.startAdvertising(beacon)
    }

    private fun shareLocationAndTransmitingData(longitude: Double, latitude: Double, beaconUUID: String?){
        checkifDataAlreadyShared(object: OnFirebaseCallback{
            override fun onCallBack(someObject: Any?) {
                var exists: Boolean = someObject as Boolean
                if(exists){
                    val databaseRef = FirebaseDatabase.getInstance().getReference("sharing/$friendId/$userId")
                    val updates = HashMap<String, Any>()
                    updates["beacon"] = beaconUUID as Any
                    updates["latitude"] = latitude as Any
                    updates["longitude"] = longitude as Any
                    databaseRef.updateChildren(updates)
                }else{
                    val databaseRef = FirebaseDatabase.getInstance().getReference("sharing/$friendId/")
                    val id = userId
                    databaseRef.child(id!!).setValue(id)
                    if(id!=null){
                        databaseRef.child(id).child("longitude").setValue(longitude)
                        databaseRef.child(id).child("latitude").setValue(latitude)
                        databaseRef.child(id).child("beacon").setValue(beaconUUID)
                    }
                }
            }
        })
    }

    private fun checkifDataAlreadyShared(listener: OnFirebaseCallback) {
        val reference = FirebaseDatabase.getInstance().getReference("sharing/$friendId/$userId")
        reference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists())
                    listener.onCallBack(true)
                else
                    listener.onCallBack(false)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Nisam nasao mail")
            }
        })
    }

    private fun sendMessage(content: String, senderMail: String?, receiverMail: String?) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("messages/$userId/$friendId")
        val id = databaseRef.push().key
        if(id!=null){
            databaseRef.child(id).child("message").setValue(content)
            databaseRef.child(id).child("sender").setValue(senderMail)
            databaseRef.child(id).child("receiver").setValue(receiverMail)
            val currentTimestamp = Timestamp(System.currentTimeMillis())
            databaseRef.child(id).child("timestamp").setValue(currentTimestamp)
        }

        val databaseRef2 = FirebaseDatabase.getInstance().getReference("messages/$friendId/$userId")
        val id2 = databaseRef2.push().key
        if(id2!=null){
            databaseRef2.child(id2).child("message").setValue(content)
            databaseRef2.child(id2).child("sender").setValue(senderMail)
            databaseRef2.child(id2).child("receiver").setValue(receiverMail)
            val currentTimestamp = Timestamp(System.currentTimeMillis())
            databaseRef2.child(id2).child("timestamp").setValue(currentTimestamp)
        }

    }

    //TODO: mozda napraviti novu funkciju koja ce samo vracati zadnji element snapshota, kako se radi o novoj poruci a u ovoj napraviti da samo jednom osluskuje stream
    private fun getAllMessages(listener: OnFirebaseCallback) {
        val reference = FirebaseDatabase.getInstance().getReference("messages/$userId/$friendId")
        var messageList: MutableList<FirebaseMessage> = ArrayList()
        reference.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messageList.clear()
                for (datas in dataSnapshot.children) {
                    val msg = FirebaseMessage(datas.child("message").getValue().toString(), datas.child("receiver").getValue().toString(), datas.child("sender").getValue().toString(), datas.child("timestamp").child("time").getValue().toString())
                    messageList.add(msg)
                }
                if (!dataSnapshot.children.any())
                    println("Nisam nasao mail")
                listener.onCallBack(messageList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Nisam nasao mail")
            }
        })
    }
}