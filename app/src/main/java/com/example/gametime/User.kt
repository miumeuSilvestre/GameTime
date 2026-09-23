package com.example.gametime

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class User : AppCompatActivity() {
    var username: TextView? = null;
    var fauth: FirebaseAuth? = null;
    var profileImage: CircleImageView?=null
    var store: FirebaseFirestore? = null;
    var userId: String? = null;
    val IMAGE_REQUEST=1
    var imageURI:Uri?=null;
    var friendsLayout:LinearLayout?=null
    var gamesLayout:LinearLayout?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user);
        username = findViewById(R.id.name);
        profileImage=findViewById(R.id.profileImage_user)
        val message = intent.extras?.getString("username");
        username?.setText(message);
        fauth = FirebaseAuth.getInstance()
        store = FirebaseFirestore.getInstance()
        userId = fauth?.currentUser?.uid
        friendsLayout=findViewById(R.id.friendslistlayout)
        gamesLayout=findViewById(R.id.colectionGameLayout)
        val document = store?.collection("users")?.document(userId!!);
        document?.addSnapshotListener { snapshot, e ->
            username?.setText(snapshot?.getString("username"))
        }
        store?.collection("users")?.document(userId!!.toString())?.get()?.addOnSuccessListener {
            Picasso.get().load(Uri.parse(it.getString("profilepic"))).into(profileImage)
        }
        friendCollection()
        gameColection()



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.user_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId.equals(R.id.search)) {
            val intent = Intent(applicationContext, Search::class.java)
            startActivity(intent)
            return true
        }
        if(item.itemId.equals(R.id.profile)){
            val intent=Intent()
            intent.setType("image/")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent,IMAGE_REQUEST)
            return true
        }

        if(item.itemId.equals(R.id.requests)){
            val intent = Intent(applicationContext, FriendList::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1 && resultCode== RESULT_OK) {
             imageURI=data?.data
           // val bitmap=MediaStore.Images.Media.getBitmap(contentResolver,uri)
            val fileName=UUID.randomUUID().toString()
            val ref=FirebaseStorage.getInstance().getReference("/user/$fileName")
            val map=HashMap<String,Any>()
            ref.putFile(imageURI!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    store?.collection("users")?.document(userId!!.toString())?.update(
                        "profilepic",it.toString())

                    Log.d("hello",map.toString())


                }
            }
            store?.collection("users")?.document(userId!!.toString())?.get()?.addOnSuccessListener {
                Picasso.get().load(Uri.parse(it.getString("profilepic"))).into(profileImage)
            }





        }
    }

    fun friendCollection(){
        val document = store?.collection("users")?.document(userId!!);
        document?.get()?.addOnSuccessListener {
            val friends=it.get("friends") as ArrayList<String>
            for (f in friends){
                val icon=CircleImageView(this)
                val otherDoc=store?.collection("users")?.document(f)?.get()?.addOnSuccessListener {
                    Picasso.get().load(Uri.parse(it.getString("profilepic"))).into(icon)
                }
                friendsLayout?.addView(icon)
                icon.layoutParams.width=150
                icon.layoutParams.height=150

        }
        }
    }
    fun gameColection(){
        val document = store?.collection("users")?.document(userId!!);
        document?.get()?.addOnSuccessListener {
            val games=it.get("games") as ArrayList<String>
            for (g in games){
                val icon=CircleImageView(this)
                val otherDoc=store?.collection("games")?.document(g)?.get()?.addOnSuccessListener {
                    Picasso.get().load(Uri.parse(it.getString("photo"))).into(icon)
                }
                gamesLayout?.addView(icon)
                icon.layoutParams.width=150
                icon.layoutParams.height=150

            }
        }
    }

    override fun onBackPressed() {
        fauth?.signOut()
        finish()
    }




}