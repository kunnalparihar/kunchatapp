package com.example.kunal.kunchapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView muserslist;

    private DatabaseReference muserdatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
      mtoolbar = (Toolbar)findViewById( R.id.usersappbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Users list");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        muserslist = (RecyclerView)findViewById(R.id.usersRecycleview);
        muserslist.setHasFixedSize(true);
        muserslist.setLayoutManager(new LinearLayoutManager(this));




    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,UsersviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersviewHolder>(
                Users.class,R.layout.users_single_layout,UsersviewHolder.class,muserdatabase)
        {
            @Override
            protected void populateViewHolder(UsersviewHolder viewHolder, Users model, int position)
            {

               viewHolder.setname(model.getName());
                viewHolder.setstatus(model.getStatus());
               // String thumbi= model.getThumb_image();
            //    Toast.makeText(UsersActivity.this,thumbi,Toast.LENGTH_LONG).show();
                viewHolder.setuserimage(model.getThumb_image(),getApplicationContext());


                final String user_id = getRef(position).getKey();


                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileintent = new Intent(UsersActivity.this,ProfileAcitivity.class);
                        profileintent.putExtra("user_idd",user_id);
                        startActivity(profileintent);
                    }
                });
            }
        };

        muserslist.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersviewHolder extends RecyclerView.ViewHolder
    {

        View mview;
        public UsersviewHolder(View itemView) {
            super(itemView);
            mview  = itemView;
        }


        public void setname(String name)
        {
            TextView musernameview  = (TextView) mview.findViewById(R.id.usersinglename);
            musernameview.setText(name);
        }

        public void setstatus(String status)
        {
            TextView muserstatusview  = (TextView) mview.findViewById(R.id.usersinglestatus);
            muserstatusview.setText(status);

        }

        public void setuserimage(String thumbimage, Context ctx)

        {
            CircleImageView usersimageview  = (CircleImageView)mview.findViewById(R.id.user_singleimage);

         //   Toast.makeText(ctx,thumbimage,Toast.LENGTH_LONG).show();
            Picasso.with(ctx).load(thumbimage).placeholder(R.drawable.downloadss).into(usersimageview);
        }
    }
}
