package com.example.kunal.kunchapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar logintoolbar;

    private TextInputLayout memail;
    private TextInputLayout mpassword;
    private Button mloginbutton;

    private ProgressDialog mloginProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        logintoolbar = (Toolbar)findViewById(R.id.login_toolbar); //toolbar
        setSupportActionBar(logintoolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        memail =  (TextInputLayout)findViewById(R.id.textInputLayoutlogin1);    //loginemail
        mpassword =  (TextInputLayout)findViewById(R.id.textInputLayoutlogin2);  //loginpassword
        mloginbutton = (Button)findViewById(R.id.loginbutton);

        mloginProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();


        mloginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email  = memail.getEditText().getText().toString();
                String password  = mpassword.getEditText().getText().toString();

                if( TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {

                }
                else
                {
                    mloginProgress.setTitle("login User");
                    mloginProgress.setMessage("processing");
                    mloginProgress.setCanceledOnTouchOutside(false);
                    mloginProgress.show();

                    login_user(email,password);
                }
            }
        });

    }

    private void login_user(String email, String password)
    {
       mAuth.signInWithEmailAndPassword(email,password)
               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {

                       if(task.isSuccessful())
                       {    mloginProgress.dismiss();
                           Intent  mainintent= new Intent(LoginActivity.this,MainActivity.class);
                           mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(mainintent);
                           finish();
                       }
                       else
                       {
                           mloginProgress.hide();
                           Toast.makeText(LoginActivity.this,"invalid username or incorrect password",Toast.LENGTH_LONG).show();


                       }
                   }
               });
    }
}
