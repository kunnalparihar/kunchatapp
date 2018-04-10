package com.example.kunal.kunchapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mtoolbar;

    private TextInputLayout mstatuslayout;
    private Button mchangestatusbtn;


    private DatabaseReference mStatusDatabase;
    private FirebaseUser mcurrentuser;

    private ProgressDialog mprogressstatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = mcurrentuser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuid);




        mtoolbar = (Toolbar)findViewById(R.id.status_appbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setHomeButtonEnabled(true);


        String status_value = getIntent().getStringExtra("status_value");

        mstatuslayout = (TextInputLayout)findViewById(R.id.status_textinputlayout);
        mstatuslayout.getEditText().setText(status_value);
        mchangestatusbtn = (Button)findViewById(R.id.statusChangebutton);

        mchangestatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mprogressstatus = new  ProgressDialog(StatusActivity.this);
                mprogressstatus.setTitle("Updating your Status");
                mprogressstatus.setMessage("processing");
                mprogressstatus.show();

                String newstatus = mstatuslayout.getEditText().getText().toString();
                mStatusDatabase.child("status").setValue(newstatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            mprogressstatus.dismiss();
                            Toast.makeText(getApplicationContext()," status updated",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"error in updating status",Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });


    }
}
