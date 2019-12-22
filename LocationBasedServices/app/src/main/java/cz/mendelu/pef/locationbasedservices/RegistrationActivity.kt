package cz.mendelu.pef.locationbasedservices

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_place.*
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.content_add_place.*
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.preference.PreferenceManager
import android.view.View
import android.graphics.Bitmap
import android.provider.MediaStore
import android.R.attr.data
import android.app.PendingIntent.getActivity
import android.graphics.ImageDecoder
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.Build
import android.os.PersistableBundle
import androidx.core.graphics.scale
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.ByteArrayOutputStream
import java.io.File


class RegistrationActivity : AppCompatActivity() {

    private lateinit var firebaseServer: FirebaseServer
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //toolbar.setNavigationOnClickListener { finish() }

        firebaseServer = FirebaseServer(this)

        registrate_user_image.setOnClickListener(){
            println("uso u onclick")
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 989)
        }

        doRegistration.setOnClickListener(){
            register()
        }

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    var selectedPhoto: Uri? = null
    var bitmap: Bitmap? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        Log.d("TAG", "activity result")
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode === 989 && resultCode === Activity.RESULT_OK && data != null) {
            selectedPhoto = data.getData()
            bitmap = getResizedBitmap(selectedPhoto)
            if(bitmap!=null){
                registrate_user_image.setImageBitmap(bitmap)
            }
        }
    }

    private fun getResizedBitmap(selectedPhoto: Uri?): Bitmap? {
        try {
            if(Build.VERSION.SDK_INT < 28) {
                var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPhoto)
                var resized: Bitmap
                if (bitmap.getWidth() > 150 && bitmap.getHeight() > 150) {
                    resized = bitmap.scale(150, 150, true)
                    bitmap = resized
                    return bitmap
                }
            } else {
                val uriString = selectedPhoto.toString()
                var uri = Uri.parse(uriString)
                val source = ImageDecoder.createSource(this.contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                return bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    fun register(){
        var isValid = true;
        registerError.text = "";
        if(registrateName.text.isEmpty()){
            registrateName.requestFocus()
            addRegisterError("Name is empty!")
            isValid = false
        }
        if(registrateSurname.text.isEmpty()){
            registrateName.requestFocus()
            addRegisterError("Surname is empty!")
            isValid = false
        }
        if(!(Patterns.EMAIL_ADDRESS.matcher(registrateMail.text.toString()).matches())){
            registrateName.requestFocus()
            addRegisterError("Email is not valid!")
            isValid = false
        }
        if(registratePassword.text.toString() != registratePassConfirm.text.toString() || registratePassword.text.isEmpty()){
            registratePassword.requestFocus()
            registratePassConfirm.requestFocus()
            addRegisterError("Passwords do not match!")
            isValid = false
        }

        if(isValid){
            mAuth.createUserWithEmailAndPassword(registrateMail.text.toString(), registratePassword.text.toString())
                .addOnCompleteListener(this){task ->
                    if(task.isSuccessful)
                    {
                        Toast.makeText(baseContext,"Registration complete!", Toast.LENGTH_SHORT).show()
                        if(bitmap==null){
                            println("bitmapa je null")
                            selectedPhoto = Uri.parse("android.resource://"+ packageName +"/drawable/mendelu");
                            bitmap = getResizedBitmap(selectedPhoto)
                            println(selectedPhoto.toString())
                        }
                        bitmap?.let { uploadImageToFirebase(it) }
                        val User = User(registrateName.text.toString(), registrateSurname.text.toString(), registrateMail.text.toString(), FirebaseAuth.getInstance().uid)
                        val databaseRef = FirebaseDatabase.getInstance().getReference("users")
                        val id = databaseRef.push().key
                        if(id!=null)
                            databaseRef.child(id).setValue(User)
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    else
                        Toast.makeText(baseContext, "Registration unsuccessful, please try again.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {
        var imageName = registrateMail.text.toString()
        println("prije storage refa")
        val storageRef = FirebaseStorage.getInstance().reference
        println("prije refa")
        val ref = storageRef?.child("profileImages/" + registrateMail.text.toString())
        println("poslije oboje")
        val baos: ByteArrayOutputStream = ByteArrayOutputStream()
        if(ref==null)
            println("ref je null")
        if(bitmap==null)
            println("bitmap i dalje null")
        println("prije compressa")
        println("referenca:"+ref.toString())
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        val bytedata: ByteArray
        println("prije ifa")
        if(baos!=null){
            println("baos nije null")
            bytedata = baos.toByteArray()
            ref.putBytes(bytedata)
                .addOnSuccessListener(){task->
                    Toast.makeText(baseContext, "Image uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener(){task ->
                    Toast.makeText(baseContext, "Unsuccessfull image upload!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun addRegisterError(error: String){
        registerError.text = registerError.text.toString()+"\n"+error
    }
}

