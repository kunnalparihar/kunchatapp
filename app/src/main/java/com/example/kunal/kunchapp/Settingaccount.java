package com.example.kunal.kunchapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class Settingaccount extends AppCompatActivity {
    TextView mdisplaynamesetting;
    TextView mstatus;
    private CircleImageView mimageview;
    Button mChangeimagebutton;
    Button mChangestatusbutton;

    private DatabaseReference muserdatabase;
    private FirebaseUser mcurrentuser;

    private static final int gallerypics = 1;

    //storage firebase image
    private StorageReference mimagestorage;

    private ProgressDialog mprogressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingaccount);

        mdisplaynamesetting = (TextView)findViewById(R.id.settings_displayname);
       mstatus = (TextView)findViewById(R.id.settings_displayText);
        mimageview = (CircleImageView)findViewById(R.id.settingimage) ;
        mChangeimagebutton= (Button) findViewById(R.id.settings_image_change);
        mChangestatusbutton = (Button)findViewById(R.id.setting_status);

        mimagestorage = FirebaseStorage.getInstance().getReference();

       mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mcurrentuser.getUid();

        muserdatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        muserdatabase.keepSynced(true);

        muserdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
             final   String image = dataSnapshot.child("image").getValue().toString();

                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mdisplaynamesetting.setText(name);
                mstatus.setText(status);
                if(!image.equals("default")) {

                  //  Picasso.with(Settingaccount.this).load(image).placeholder(R.drawable.flowerss).into(mimageview);
                    Picasso.with(Settingaccount.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).
                            placeholder(R.drawable.flowerss).into(mimageview, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                           Picasso.with(Settingaccount.this).load(image).placeholder(R.drawable.flowerss).into(mimageview);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChangestatusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_value = mstatus.getText().toString();
                Intent statusintent= new Intent(Settingaccount.this,StatusActivity.class);
                statusintent.putExtra("status_value",status_value);
                startActivity(statusintent);


            }
        });

        mChangeimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryintent = new Intent();
                galleryintent.setType("image/*");
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(Intent.createChooser(galleryintent,"Select image"),gallerypics);

                // start picker to get image for cropping and then use the image in cropping activity
         /*       CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Settingaccount.this); */
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == gallerypics && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);

          //  Toast.makeText(Settingaccount.this,imageUri,Toast.LENGTH_LONG).show();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

               mprogressdialog  = new ProgressDialog(Settingaccount.this);
                mprogressdialog.setTitle("uploading image");
                mprogressdialog.setMessage("please wait");
                mprogressdialog.setCanceledOnTouchOutside(false);
                mprogressdialog.show();


                Uri resultUri = result.getUri();

                final File thumb_filepath = new File(resultUri.getPath());

                String current_userid = mcurrentuser.getUid();
                Bitmap thumbbitmap = BitmapFactory.decodeResource(getResources(), R.drawable.butterfly); ;
                try {

                    thumbbitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filepath);




                }
                catch (IOException e) {

                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumbbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);


                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = mimagestorage.child("profile_images").child(current_userid+".jpg");

             final   StorageReference thumbfilepath = mimagestorage.child("profile_images").child("thumbs").child(current_userid+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            @SuppressWarnings("VisibleForTests") final String downloadurl = task.getResult().getDownloadUrl().toString();


                            UploadTask uploadTask = thumbfilepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbtask) {

                                    @SuppressWarnings("VisibleForTests")  String thumbdownloadurl = thumbtask.getResult().getDownloadUrl().toString();

                                  if(thumbtask.isSuccessful())
                                  {
                                      Map updatehashMap = new HashMap<>();
                                      updatehashMap.put("image",downloadurl);
                                      updatehashMap.put("thumb_image",thumbdownloadurl);
                                      muserdatabase.updateChildren(updatehashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              if(task.isSuccessful())
                                              {
                                                  mprogressdialog.dismiss();
                                                  Toast.makeText(Settingaccount.this,"successful upload",Toast.LENGTH_LONG).show();
                                              }
                                          }
                                      }) ;
                                  }
                                }
                            });


                        }
                        else
                        {
                            Toast.makeText(Settingaccount.this,"error in uploading",Toast.LENGTH_LONG).show();
                            mprogressdialog.dismiss();
                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
