package com.example.kunal.kunchapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class registeractivity extends AppCompatActivity {

    private TextInputLayout mdisplayname;
    private TextInputLayout memail;
    private TextInputLayout mpassword;
    private Button mcreatebutton;
    private FirebaseAuth mAuth;
    private Toolbar regtoolbar;

    private ProgressDialog mregProgress;

    private DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeractivity);


        mAuth = FirebaseAuth.getInstance();

        mdisplayname =  (TextInputLayout)findViewById(R.id.textInputLayout1);  // regdisplayname
        memail =  (TextInputLayout)findViewById(R.id.textInputLayout2);    //regemail
        mpassword =  (TextInputLayout)findViewById(R.id.textInputLayout3);  //regpassword
        mcreatebutton = (Button)findViewById(R.id.regCreateaccbutton);

        regtoolbar = (Toolbar)findViewById(R.id.register_toolbar); //toolbar
        setSupportActionBar(regtoolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mregProgress = new ProgressDialog(this);


        mcreatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name  = mdisplayname.getEditText().getText().toString();
                String email  = memail.getEditText().getText().toString();
                String password  = mpassword.getEditText().getText().toString();

                if(TextUtils.isEmpty(display_name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {

                }
                else
                {
                    mregProgress.setTitle("Registering User");
                    mregProgress.setMessage("processing");
                    mregProgress.setCanceledOnTouchOutside(false);
                    mregProgress.show();

                register_user(display_name,email,password);
                }
            }
        });

    }

    private void register_user(final String display_name, String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();

                            String uid = currentuser.getUid();

                            mdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String ,String> userMap = new HashMap<String, String>();
                            userMap.put("name",display_name);
                            userMap.put("status","hii there I am using kunchapp");
                            userMap.put("image","default");
                            userMap.put("thumb_image","default");

                            mdatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                      mregProgress.dismiss();  // remove progress bar
                            Intent mainIntent = new Intent(registeractivity.this,MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(mainIntent);
                            finish(); // doesnt allow to come back when user press back button

                                    }
                                }
                            });



                        }
                        else
                        {
                            Toast.makeText(registeractivity.this,"got some error",Toast.LENGTH_LONG).show();
                             mregProgress.hide();
                        }

                    }
                });
    }
}
