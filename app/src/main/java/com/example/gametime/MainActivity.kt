package com.example.gametime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.algolia.search.saas.Client
import com.algolia.search.saas.Index
import com.google.android.gms.common.api.Api
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    var user: EditText? = null;
    var email: EditText?=null;
    var pass : EditText?=null;
    var firebaseAuth:FirebaseAuth?=null;
    var progressBar:ProgressBar?=null;
    var regButton:AppCompatButton?=null;
    var haveAccount:AppCompatButton?=null;
    var fstore:FirebaseFirestore?=null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        user=findViewById(R.id.register_username);
        email=findViewById(R.id.login_email);
        pass=findViewById(R.id.login_password);
        progressBar=findViewById(R.id.progressBarId);
        regButton=findViewById(R.id.btn_register);
        firebaseAuth= FirebaseAuth.getInstance();
        fstore= FirebaseFirestore.getInstance()
        haveAccount=findViewById(R.id.login);


        haveAccount?.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View?){
                val intent=Intent(applicationContext,Login::class.java)
                startActivity(intent);
            }
        })
        regButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                var mail:String?=email?.text.toString().trim();
                var passowrd:String?=pass?.text.toString().trim();
                if(TextUtils.isEmpty(mail)){
                    email?.setError("É preciso um email");
                    return;
                }
                if(TextUtils.isEmpty(passowrd)){
                    pass?.setError("É preciso uma password");
                    return;
                }
                if(passowrd?.length!! <6){
                    pass?.setError("Passord tem de ter mais de 6 caracteres");
                    return;
                }
                progressBar?.isVisible=true;
                firebaseAuth?.createUserWithEmailAndPassword(mail!!,passowrd)?.addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(applicationContext,"User criado com sucesso",Toast.LENGTH_SHORT).show();
                        val userID=firebaseAuth?.currentUser?.uid
                        val document=fstore?.collection("users")?.document(userID.toString())
                        val intent=Intent(applicationContext,User::class.java)
                        val map= HashMap<String,Any>()
                        map.put("username",user?.text.toString().trim());
                        map.put("games", ArrayList<Game>())
                        map.put("profilepic","")
                        map.put("requests",ArrayList<String>())
                        map.put("friends",ArrayList<String>())
                        document?.set(map)?.addOnSuccessListener {
                            Log.d("d","sucess")
                        }
                        //intent.putExtra("username",user?.text.toString().trim());
                        startActivity(intent)
                        finish()

                    }
                    else{
                        Toast.makeText(applicationContext,"Algo correu mal",Toast.LENGTH_SHORT).show();
                    }
                }

            }

        })


    }



}