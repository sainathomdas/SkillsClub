package com.example.saimouni.bottomnavigationbar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Date;
import java.util.Random;

public class AddPostActivity extends AppCompatActivity {



    ImageButton addPostImage;
    EditText title, desc, addPostDate;
    Button uploadBtn;

    Uri imageUri = null;
    StorageReference storageReference;
    private RelativeLayout addPostActivityLayout;
    private NotificationHelper helper;
    private String token;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private String mTitle, mDesc, mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        addPostImage = findViewById(R.id.addPostImage);
        title = findViewById(R.id.addPostTitle);
        desc = findViewById(R.id.addPostDesc);
        addPostActivityLayout = findViewById(R.id.addPostActivityLayout);
        uploadBtn = findViewById(R.id.addPostUploadBtn);
        addPostDate = findViewById(R.id.addPostDate);
        helper = new NotificationHelper(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");

        addPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cropping Image
                CropImage.activity()
                        .setAspectRatio(5, 3)
                        .start(AddPostActivity.this);

            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });


    }

    private void upload() {
        mTitle = title.getText().toString().trim();
        mDesc = desc.getText().toString().trim();
        mDate = addPostDate.getText().toString().trim();
        if (!TextUtils.isEmpty(mTitle) && (!TextUtils.isEmpty(mDesc)) && imageUri != null && (!TextUtils.isEmpty(mDate))) {


            final StorageReference filepath = storageReference.child("Blog_Images").child(imageUri.getLastPathSegment());
            progressDialog.show();
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;        //wait until task completes
                    Uri downloadUrl = urlTask.getResult();
                    DatabaseReference newPost = databaseReference.child(mTitle);
                    newPost.child("title").setValue(mTitle);
                    newPost.child("desc").setValue(mDesc);
                    newPost.child("date").setValue(mDate);
                    newPost.child("image").setValue(downloadUrl.toString());
                    newPost.child("timestamp").setValue(new Date().getTime() * -1);

                    progressDialog.dismiss();

                    Toast.makeText(AddPostActivity.this, "Successfully uploaded", Toast.LENGTH_LONG).show();


                    sendNotification(mTitle);


                    Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AddPostActivity.this, "ERROR while uploading", Toast.LENGTH_LONG).show();
                }
            });
        } else
            Snackbar.make(addPostActivityLayout, "Please enter all details", Snackbar.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageUri = resultUri;
                addPostImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(AddPostActivity.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendNotification(String title) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = helper.getChannelNotification(title, "Successfully added.");
            helper.getManager().notify(new Random().nextInt(), builder.build());

        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

            notificationBuilder.setContentTitle("SkillsClub notification");
            notificationBuilder.setContentText("New Event : '" + title + "' has been added");
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSmallIcon(R.drawable.skillsclub);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setSound(defaultSoundUri);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, notificationBuilder.build());
        }


    }
}
