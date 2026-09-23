package com.example.gametime

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FriendList:AppCompatActivity() {
    var friendsReq:LinearLayout?=null
    var fauth:FirebaseAuth?=null;
    var store: FirebaseFirestore?=null;
    var userId: String? = null;
    var request: ArrayList<String>?=null
    var friends:ArrayList<String>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_requests)
        friendsReq=findViewById(R.id.friendRequests);
        fauth= FirebaseAuth.getInstance();
        store= FirebaseFirestore.getInstance()
        userId=fauth?.currentUser?.uid!!
        store?.collection("users")?.document(userId!!)?.get()?.addOnSuccessListener {
            request = it.get("requests") as ArrayList<String>
            friends=it.get("friends") as ArrayList<String>
             for(i in request!!){
                 val horizontal=LinearLayout(this)
                 val accept=Button(this)
                 val refuse=Button(this)
                 val name= TextView(this)
                 horizontal.orientation=LinearLayout.HORIZONTAL
                 accept.textSize=26f
                 refuse.textSize=26f
                 name.textSize=26f
                 accept.setText("+")
                 refuse.setText("X")
                 store?.collection("users")?.document(i)?.get()?.addOnSuccessListener {
                     name?.setText(it.getString("username"))
                 }
                 horizontal.addView(accept)
                 horizontal.addView(refuse)
                 horizontal.addView(name)
                 friendsReq?.addView(horizontal)
                 accept.setOnClickListener{
                     store?.collection("users")?.document(i)?.get()?.addOnSuccessListener {
                         val friends_other=it.get("friends") as ArrayList<String>
                         friends!!.add(i)
                         friends_other.add(userId!!)
                         request?.remove(i)
                         store?.collection("users")?.document(userId!!)?.update("friends",friends)
                         store?.collection("users")?.document(it.id)?.update("friends",friends_other)
                         store?.collection("users")?.document(userId!!)?.update("requests",request)
                         friendsReq?.removeView(horizontal)
                         //horizontal.removeAllViews()
                     }
                 }
                 refuse.setOnClickListener{
                     store?.collection("users")?.document(i)?.get()?.addOnSuccessListener {
                         val friends=it.get("friends") as ArrayList<String>
                         friends.add(i)
                         request?.remove(i)
                         store?.collection("users")?.document(userId!!)?.update("requests",request)
                         //horizontal.removeAllViews()
                         friendsReq?.removeView(horizontal)
                     }
                 }

           }
        }

    }
    override fun onBackPressed() {
        finish()
    }
}