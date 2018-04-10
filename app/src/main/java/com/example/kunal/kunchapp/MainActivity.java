package com.example.kunal.kunchapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Toolbar mtoolbar;

    private ViewPager mviewpager;

    private SectionPagerAdapter mSectionPagerAdapter;

    private TabLayout mtablayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mtoolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("kunchapp");

        mviewpager = (ViewPager)findViewById(R.id.main_viewpager);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        mviewpager.setAdapter(mSectionPagerAdapter);

        mtablayout = (TabLayout)findViewById(R.id.main_tabs);
        mtablayout.setupWithViewPager(mviewpager);


    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser== null)
        {
           sendtoStart();
        }
    }

    private void sendtoStart()
    {
        Intent startIntent = new Intent( MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.mainLogoutbutton)
        {
            FirebaseAuth.getInstance().signOut();
            sendtoStart();
        }
        if(item.getItemId()== R.id.main_settingbutton)
        {
            Intent settingsintent = new Intent (MainActivity.this,Settingaccount.class);
            startActivity(settingsintent);
        }
        if(item.getItemId()== R.id.main_alluser_button)
        {
            Intent usersintent = new Intent (MainActivity.this,UsersActivity.class);
            startActivity(usersintent);
        }

        return true;
  //  <ImageView
    //    android:layout_width="170dp"
    //    android:layout_height="170dp"
    //    android:src="@drawable/butterfly"
    //    android:id="@+id/butterfly"
      //  android:layout_alignParentTop="true"
     //   android:layout_alignEnd="@+id/settings_image_change"/>
    }
}
