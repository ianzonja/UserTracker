package cz.mendelu.pef.locationbasedservices

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_place.*
import kotlinx.android.synthetic.main.friends_activity.*
import com.google.firebase.database.DataSnapshot


class FriendsRecyclerView : AppCompatActivity() {
    private lateinit var friendAdapter: FriendsCardviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends_activity)
        setSupportActionBar(toolbar)
        var friendsList: MutableList<Friend>
        getAllFriends(object: OnFirebaseCallback{
            override fun onCallBack(someObject: Any?) {
                friendsList = someObject as MutableList<Friend>
                friends_recycler_view.apply(){
                    layoutManager = LinearLayoutManager(this.context)
                    friendAdapter = FriendsCardviewAdapter(object: ClickListener{
                        override fun onPositionClicked(view: View, position: Int){
                            val intent = Intent(baseContext, ChatRecyclerView::class.java)
                            intent.putExtra("email",friendsList.get(position).email)
                            intent.putExtra("name", friendsList.get(position).name)
                            intent.putExtra("surname", friendsList.get(position).surname)
                            intent.putExtra("image", friendsList.get(position).image)
                            intent.putExtra("id", friendsList.get(position).id)
                            startActivity(intent)
                        }
                    })
                    friendAdapter.submitList(friendsList)
                    adapter = friendAdapter
                }
            }
        })
        addFriend.setOnClickListener(){
            val sharedPreferences = getSharedPreferences("SP", MODE_PRIVATE)
            val email = sharedPreferences.getString("email", "userEmail")
            val dialog = AddFriendDialog(this, email)
            dialog.show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //toolbar.setNavigationOnClickListener { finish() }
    }

    private fun getAllFriends(listener: OnFirebaseCallback) {
        var emailList: MutableList<String> = ArrayList()
        var friendsList: MutableList<Friend> = ArrayList()
        val sharedPreferences = getSharedPreferences("SP", MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "userEmail")
        val reference = FirebaseDatabase.getInstance().getReference("friends")
        reference.orderByChild("1").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (datas in dataSnapshot.children) {
                    emailList.add(datas.child("2").getValue().toString())
                }
                if (!dataSnapshot.children.any())
                    println("Nisam nasao mail")
                val reference2 = FirebaseDatabase.getInstance().getReference("friends")
                reference2.orderByChild("2").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (datas in dataSnapshot.children) {
                            emailList.add(datas.child("1").getValue().toString())
                        }
                        if (!dataSnapshot.children.any())
                            println("Nisam nasao mail")
                        val reference3 = FirebaseDatabase.getInstance().getReference("users")
                        var friendsList: MutableList<Friend> = ArrayList()
                        for(userEmail in emailList){
                            reference3.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (datas in dataSnapshot.children) {
                                        val friend = Friend("https://firebasestorage.googleapis.com/v0/b/localized-services.appspot.com/o/profileImages%2Fasdadada%40saddas.com?alt=media&token=4e7e4395-a383-4292-818b-603e4afa46f7", datas.child("name").getValue().toString(), datas.child("surname").getValue().toString(), datas.child("email").getValue().toString(), datas.child("id").getValue().toString())
                                        friendsList.add(friend)
                                    }
                                    if(!dataSnapshot.children.any())
                                        println("Nisam nasao mail")
                                    listener.onCallBack(friendsList)
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                }
                            })
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        println("Nisam nasao mail")
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Nisam nasao mail")
            }
        })
    }
}