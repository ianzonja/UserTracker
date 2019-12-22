package cz.mendelu.pef.locationbasedservices

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseServer: FirebaseServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //toolbar.setNavigationOnClickListener { finish() }

        firebaseServer = FirebaseServer(this)
        loginButton.setOnClickListener(){
            login()
        }

        withoutAccount.setOnClickListener(){
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    fun login(){
        var mAuth = FirebaseAuth.getInstance()
        loginError.text = ""
        var isValid = true
        if(loginMail.text.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(loginMail.text.toString()).matches()){
            loginMail.requestFocus()
            addLoginError("Invalid Email!")
            isValid = false
        }
        if(loginPassword.text.isEmpty()){
            loginPassword.requestFocus()
            addLoginError("Password is empty!")
            isValid = false
        }
        if(isValid){
            mAuth.signInWithEmailAndPassword(loginMail.text.toString(), loginPassword.text.toString()).addOnCompleteListener(){
                task->
                if(task.isSuccessful){
                    Toast.makeText(baseContext,"Login successful!", Toast.LENGTH_SHORT)
                    val sharedPreferences = getSharedPreferences("SP", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    var uuid = UUID.randomUUID()
                    editor.putString("email", loginMail.text.toString())
                    editor.putString("userId", FirebaseAuth.getInstance().uid)
                    editor.putString("beaconUUID", uuid.toString())
                    editor.apply()
                    val intent = Intent(this, FriendsRecyclerView::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(baseContext, "Login Unsuccessful, please try again.", Toast.LENGTH_SHORT)
                }
            }
        }
    }

    fun addLoginError(error: String){
        loginError.text = loginError.text.toString()+"\n"+error
    }
}