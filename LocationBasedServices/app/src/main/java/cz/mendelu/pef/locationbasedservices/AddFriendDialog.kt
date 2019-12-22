package cz.mendelu.pef.locationbasedservices

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.dialog_add_friend.*
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent


class AddFriendDialog(context: Context, email: String?) : Dialog(context) {
    val email : String?
    init {
        setCancelable(true)
        this.email = email
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_add_friend)
        add_friend_button.setOnClickListener(){
            if(!add_friend_input.text.equals(""))
                retrieveUserData();
        }

    }

    fun retrieveUserData(){
        val reference = FirebaseDatabase.getInstance().getReference("users")

        reference.orderByChild("email").equalTo(add_friend_input.text.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (datas in dataSnapshot.children) {
                        add_friend_input.setText("Nasao sam mail: "+datas.child("email").getValue().toString())
                        val databaseRef = FirebaseDatabase.getInstance().getReference("friends")
                        val id = databaseRef.push().key
                        if(id!=null){
                            databaseRef.child(id).child("1").setValue(datas.child("email").value.toString())
                            databaseRef.child(id).child("2").setValue(email)
                        }
                        val i = Intent(context, FriendsRecyclerView::class.java)
                        startActivity(context, i, null)
                    }
                    if(!dataSnapshot.children.any())
                        add_friend_input.setText("Nisam nasao mail")
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    add_friend_input.setText("Nisam nasao mail")
                }
            })
    }
}