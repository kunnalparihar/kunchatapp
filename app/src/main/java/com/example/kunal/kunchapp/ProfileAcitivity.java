package com.example.kunal.kunchapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;

public class ProfileAcitivity extends AppCompatActivity {

    private TextView mdisplayname,mprofilefriendscount,mprofilestatus;
    private ImageView mprofileimage;
    private Button msendrequestbtn;
    private Button mdeclinerequestbtn;


   private DatabaseReference mdatabaseref;

    private DatabaseReference mFriendreqdatabase;

    private  DatabaseReference mnotificationdatabase;

    private FirebaseUser mcurrent_users;;

    private DatabaseReference mfriendsdatabas;

    private ProgressDialog mregProgress;

    private String mcurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_acitivity);

       final String userid =  getIntent().getStringExtra("user_idd");
        mprofilefriendscount = (TextView)findViewById(R.id.profilenooffriend);
        mprofilestatus = (TextView)findViewById(R.id.profilestatus);
        mprofileimage = (ImageView)findViewById(R.id.profileimageview);
        msendrequestbtn = (Button)findViewById(R.id.profileSendrequestbtn);
        mdeclinerequestbtn = (Button)findViewById(R.id.profiledeclinerequestbtn);
        mdisplayname = (TextView)findViewById(R.id.profiletextview);

        mcurrent_state = "not_friends";


        mdeclinerequestbtn.setEnabled(false);

        mregProgress = new ProgressDialog(this);
        mregProgress.setTitle(" Loading user");
        mregProgress.setMessage("processing");
        mregProgress.setCanceledOnTouchOutside(false);
        mregProgress.show();

        mFriendreqdatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mcurrent_users = FirebaseAuth.getInstance().getCurrentUser();


        mnotificationdatabase  = FirebaseDatabase.getInstance().getReference().child("notification");



        mfriendsdatabas = FirebaseDatabase.getInstance().getReference().child("Friends");
        mdatabaseref = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

        mdatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String displayname = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mdisplayname.setText(displayname);
                mprofilestatus.setText(status);
                Picasso.with(ProfileAcitivity.this).load(image).placeholder(R.drawable.butterfly).into(mprofileimage);
                //-------------------
                mFriendreqdatabase.child(mcurrent_users.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(userid))
                        {
                            String req_type = dataSnapshot.child(userid).child("request_type").getValue().toString();
                            if(req_type.equals("received"))
                            {
                                mcurrent_state = "req_received";
                                msendrequestbtn.setText("Accept Friend Request");
                                mdeclinerequestbtn.setVisibility(View.VISIBLE);
                                mdeclinerequestbtn.setEnabled(true);
                            }
                            else
                            {
                                if(req_type.equals("sent"))
                                {
                                    mcurrent_state = "req_sent";
                                    msendrequestbtn.setText("Cancel Friend Request");
                                    mdeclinerequestbtn.setVisibility(View.INVISIBLE);
                                    mdeclinerequestbtn.setEnabled(false);
                                }

                            }

                            mregProgress.dismiss();
                        }
                        else
                        {
                            mfriendsdatabas.child(mcurrent_users.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(userid))
                                    {
                                        mcurrent_state = "friends";
                                        msendrequestbtn.setText(" Unfriend ");
                                        mdeclinerequestbtn.setVisibility(View.INVISIBLE);
                                        mdeclinerequestbtn.setEnabled(false);
                                    }
                                    mregProgress.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mregProgress.dismiss();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        msendrequestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                msendrequestbtn.setEnabled(false);

               //------------------------
                if(mcurrent_state.equals("not_friends"))
                {
                    mFriendreqdatabase.child(mcurrent_users.getUid()).child(userid).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                            if(task.isSuccessful())
                            {
                             mFriendreqdatabase.child(userid).child(mcurrent_users.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {

                                     HashMap<String ,String > notificationData = new HashMap<String, String>();
                                     notificationData.put("from ",mcurrent_users.getUid());
                                     notificationData.put("type ","request ");
                                     mnotificationdatabase.child(userid).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                         @Override
                                         public void onSuccess(Void aVoid) {
                                             mcurrent_state = "req_sent";
                                             msendrequestbtn.setText("Cancel Friend Request");

                                             mdeclinerequestbtn.setVisibility(View.INVISIBLE);
                                             mdeclinerequestbtn.setEnabled(false);
                                             Toast.makeText(ProfileAcitivity.this,"request sent",Toast.LENGTH_SHORT).show();
                                         }
                                     });

                                 }
                             });

                            }else
                            {
                                Toast.makeText(ProfileAcitivity.this,"failed to send request",Toast.LENGTH_SHORT).show();
                            }
                            msendrequestbtn.setEnabled(true);

                        }
                    });
                }
                //------------------------
                if(mcurrent_state.equals("req_sent"))
                {
                    mFriendreqdatabase.child(mcurrent_users.getUid()).child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendreqdatabase.child(userid).child(mcurrent_users.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    msendrequestbtn.setEnabled(true);
                                    mcurrent_state = "not_friends";
                                    msendrequestbtn.setText("Send Friend Request");
                                    mdeclinerequestbtn.setVisibility(View.INVISIBLE);
                                    mdeclinerequestbtn.setEnabled(false);
                                    Toast.makeText(ProfileAcitivity.this,"request deleted",Toast.LENGTH_SHORT).show();
                                }
                            });
                       //     Toast.makeText(ProfileAcitivity.this,"deleted request ");
                        }
                    });
                }
                //-----------------------
                if(mcurrent_state.equals("req_received"))
                {
                    final String currentdate = java.text.DateFormat.getDateTimeInstance().format(new Date());
                    mfriendsdatabas.child(mcurrent_users.getUid()).child(userid).setValue(currentdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mfriendsdatabas.child(userid).child(mcurrent_users.getUid()).setValue(currentdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    mFriendreqdatabase.child(mcurrent_users.getUid()).child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendreqdatabase.child(userid).child(mcurrent_users.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    msendrequestbtn.setEnabled(true);
                                                    mcurrent_state = "friends";
                                                    msendrequestbtn.setText(" Unfriend ");

                                                    mdeclinerequestbtn.setVisibility(View.INVISIBLE);
                                                    mdeclinerequestbtn.setEnabled(false);
                                             //       Toast.makeText(ProfileAcitivity.this,"request deleted",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            //     Toast.makeText(ProfileAcitivity.this,"deleted request ");
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

                if(mcurrent_state.equals("friends"))
                {   msendrequestbtn.setEnabled(false);
                    mfriendsdatabas.child(mcurrent_users.getUid()).child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mfriendsdatabas.child(userid).child(mcurrent_users.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    msendrequestbtn.setEnabled(true);
                                    mcurrent_state = "not_friends";
                                    msendrequestbtn.setText(" Send Friend Request ");
                                    mdeclinerequestbtn.setVisibility(View.INVISIBLE);
                                    mdeclinerequestbtn.setEnabled(false);

                                    Toast.makeText(ProfileAcitivity.this," unfriend done",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }

            }
        });

      mdeclinerequestbtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              mdeclinerequestbtn.setEnabled(false);

              if(mcurrent_state.equals("req_received"))
              {
                  mFriendreqdatabase.child(mcurrent_users.getUid()).child(userid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                          mFriendreqdatabase.child(userid).child(mcurrent_users.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {


                                  mcurrent_state = "not_friends";
                                  msendrequestbtn.setText("Send Friend Request");

                                  Toast.makeText(ProfileAcitivity.this,"request declined",Toast.LENGTH_SHORT).show();
                              }
                          });

                      }
                  });
              }
          }
      });

    }
}
