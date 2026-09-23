package com.example.gametime

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class other_user : AppCompatActivity() {
    var username: TextView? = null;
    var fauth: FirebaseAuth? = null;
    var profileImage: CircleImageView? = null
    var store: FirebaseFirestore? = null;
    var userId: String? = null;
    var add: Button? = null
    var request: ArrayList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.other_user);
        add = findViewById(R.id.add)
        username = findViewById(R.id.name_other)
        fauth = FirebaseAuth.getInstance()
        store = FirebaseFirestore.getInstance()
        profileImage = findViewById(R.id.profileImage_other)
        var bundle = intent.extras
        userId = bundle?.getString("id")
        val document = store?.collection("users")?.document(userId!!);
        document?.get()?.addOnSuccessListener {
            val r = it.get("requests") as ArrayList<String>
            val f = it.get("friends") as ArrayList<String>
            for (x in r) {
                if (x?.equals(fauth?.currentUser?.uid!!)) {
                    add?.isEnabled= false
                    add?.setText("✓")
                }
            }
            for (x in f) {
                if (x?.equals(fauth?.currentUser?.uid!!)) {
                    add?.isEnabled = false
                    add?.setText("✓")
                }
            }
            document?.addSnapshotListener { snapshot, e ->
                username?.setText(snapshot?.getString("username"))
            }
            store?.collection("users")?.document(userId!!.toString())?.get()?.addOnSuccessListener {
                Picasso.get().load(Uri.parse(it.getString("profilepic"))).into(profileImage)
            }
            add?.setOnClickListener {

                store?.collection("users")?.document(userId!!)?.get()?.addOnSuccessListener {
                    request = it.get("requests") as ArrayList<String>
                    request?.add(fauth?.currentUser?.uid!!)
                    store?.collection("users")?.document(userId!!)?.update("requests", request)
                    add?.isEnabled= false
                    add?.setText("✓")
                }

            }

        }
    }
    override fun onBackPressed() {
        finish()
    }
}