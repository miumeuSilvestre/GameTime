package com.example.gametime

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class Search : AppCompatActivity() {
    var searchBar: EditText? = null;
    var searchButton: ImageButton? = null;
    var fauth: FirebaseAuth? = null;
    var store: FirebaseFirestore? = null;
    var layoutGames:LinearLayout?=null;
    var layoutUsers:LinearLayout?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search)
        searchBar = findViewById(R.id.searchbar)
        searchButton = findViewById(R.id.searchButton)
        fauth = FirebaseAuth.getInstance()
        store = FirebaseFirestore.getInstance()
        layoutGames=findViewById(R.id.linearLayoutGames)
        layoutUsers=findViewById(R.id.linearLayoutusers)
        searchButton?.setOnClickListener {
            store?.collection("games")?.get()?.addOnSuccessListener {
                if(it.isEmpty){
                    layoutGames?.removeAllViews()
                }
                layoutGames?.removeAllViews()
                for(i in it){
                    if(i.getString("name")?.toUpperCase()?.startsWith(
                           searchBar?.text.toString()?.toUpperCase())==true) {
                            val gameIcon=CircleImageView(this)
                            val text=TextView(this)
                            val horizontal=LinearLayout(this)

                            horizontal.orientation=LinearLayout.HORIZONTAL
                            text.setText(i.getString("name"))
                            text.isClickable=true
                            //text.typeface=resources.getFont(R.font.abril_fatface)
                            text.gravity=Gravity.CENTER
                            text.textSize=36f
                            Picasso.get().load(Uri.parse(i.getString("photo"))).into(gameIcon)
                            horizontal?.addView(gameIcon)
                            horizontal?.addView(text)
                            gameIcon.layoutParams.width=150
                            gameIcon.layoutParams.height=150
                            layoutGames?.addView(horizontal)
                            text.setOnClickListener{
                                val intent= Intent(applicationContext,Game::class.java)
                                intent.putExtra("id",i.id)
                                startActivity(intent)
                            }
                    }
                }
            }
            store?.collection("users")?.get()?.addOnSuccessListener {
                if(it.isEmpty){
                    layoutUsers?.removeAllViews()
                }
                layoutUsers?.removeAllViews()
                for(i in it) {
                    if (i.id !== fauth?.currentUser?.uid) {
                        if (i.getString("username")?.toUpperCase()?.startsWith(
                                        searchBar?.text.toString()?.toUpperCase()) == true) {

                            val text = TextView(this)
                            text.setText(i.getString("username"))
                            text.isClickable = true
                            layoutUsers?.addView(text)
                            text.setOnClickListener {
                                val intent = Intent(applicationContext, other_user::class.java)
                                intent.putExtra("id", i.id)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }

        }

    }

    override fun onBackPressed() {
        finish()
    }
}