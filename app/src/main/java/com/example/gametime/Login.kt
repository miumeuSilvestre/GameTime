package com.example.gametime

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth

class Login: AppCompatActivity() {

    var email: EditText?=null;
    var pass : EditText?=null;
    var firebaseAuth: FirebaseAuth?=null;
    var progressBar: ProgressBar?=null;
    var logBtn: AppCompatButton?=null;
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login);
        email=findViewById(R.id.login_email);
        pass=findViewById(R.id.login_password);
        firebaseAuth= FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBarId);
        logBtn=findViewById(R.id.login);
        logBtn?.setOnClickListener(object : View.OnClickListener {
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
                firebaseAuth?.signInWithEmailAndPassword(mail!!,passowrd)?.addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(applicationContext,"Logado com sucesso", Toast.LENGTH_SHORT).show();
                        val intent= Intent(applicationContext,User::class.java)
                        startActivity(intent)

                    }
                    else{
                        Toast.makeText(applicationContext,"Algo correu mal", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        })


    }
}