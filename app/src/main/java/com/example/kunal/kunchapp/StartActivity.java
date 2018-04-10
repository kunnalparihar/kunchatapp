package com.example.kunal.kunchapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button  regbutton,loginbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        regbutton = (Button)findViewById(R.id.start_reg_button);
        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent RegIntent = new Intent(StartActivity.this,registeractivity.class);
                startActivity(RegIntent);


            }
        });
        loginbutton = (Button)findViewById(R.id.startloginbutton);
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent RegIntent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(RegIntent);


            }
        });
    }
}
