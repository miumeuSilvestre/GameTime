package com.example.gametime

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class Game : AppCompatActivity(){
    var gameCover:ImageView?=null;
    var gameName: TextView?=null
    var store: FirebaseFirestore? = null;
    var fauth: FirebaseAuth? = null;
    var add: Button?=null
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game)
        gameCover=findViewById(R.id.gameCover)
        gameName=findViewById(R.id.gameName)
        add=findViewById(R.id.addGame)
        store = FirebaseFirestore.getInstance()
        fauth = FirebaseAuth.getInstance()
        val bundle=intent.extras
        val id=bundle?.getString("id")
        store?.collection("games")?.document(id!!)?.get()?.addOnSuccessListener {
            Picasso.get().load(Uri.parse(it.getString("photo"))).into(gameCover)
            //gameCover?.setImageURI(Uri.parse(it.getString("photo") ))
            gameName?.setText(it.getString("name"))
        }
        isGameInCollection(id!!)
        add?.setOnClickListener{
            addGame(id!!)
            add?.setText("✓")
            add?.isEnabled=false
        }



    }

    fun addGame(id: String){
        val userID=fauth?.currentUser?.uid
        store?.collection("users")?.document(userID!!)?.get()?.addOnSuccessListener {
            val games=it.get("games") as ArrayList<String>
            games.add(id)
            store?.collection("users")?.document(userID)?.update("games",games)
        }

    }
    fun isGameInCollection(id: String){
        val userID=fauth?.currentUser?.uid
        store?.collection("users")?.document(userID!!)?.get()?.addOnSuccessListener {
            val games=it.get("games") as ArrayList<String>
            for(game in games){
                if(game.equals(id)){
                    add?.setText("✓")
                    add?.isEnabled=false
                }
            }
        }
    }




}