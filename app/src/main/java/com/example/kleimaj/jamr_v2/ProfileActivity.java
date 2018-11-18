package com.example.kleimaj.jamr_v2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;

public class ProfileActivity extends Fragment implements View.OnClickListener {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Adnroid Layout
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mIdentity;

    // Storage Firebase
    private FirebaseStorage storage;
    private StorageReference mImageStorage;

    // Thumbnail bitmap
    private Bitmap thumb_bitmap;


    public ProfileActivity() {
        // Required empty public constructor
    }

    public static ProfileActivity newInstance() {
        ProfileActivity fragment = new ProfileActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_profile, container, false);


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child
                ("Users").child(current_uid);

        mDisplayImage = view.findViewById(R.id.profile_image_circle);
        mName = view.findViewById(R.id.ArtistName);
        mIdentity = view.findViewById(R.id.profile_identity);

        storage = FirebaseStorage.getInstance();
        mImageStorage = storage.getReference();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue()
                        .toString();
                String thumb_image = dataSnapshot.child("thumb_image")
                        .getValue().toString();
                String identity = dataSnapshot.child("music_identity")
                        .getValue().toString()
                        .replace("[", "")  //remove the right bracket
                        .replace("]", "")  //remove the left bracket
                        .trim()
                        .replaceAll(",$", ""); // remove the last comma

                mName.setText(name);
                mIdentity.setText(identity);

                // display the default avatar if no image uploaded
                if (!image.equals("default")) {
                    Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // click listerer
        Button settingButton = view.findViewById(R.id.profile_setting_button);
        settingButton.setOnClickListener(this);

        Button collectionButton = view.findViewById(R.id.profile_collection_button);
        collectionButton.setOnClickListener(this);

        CircleImageView profileImage = view.findViewById(R.id.profile_image_circle);
        profileImage.setOnClickListener(this);

        return view;
    }


    // onlick method for buttoms
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_collection_button:
                Intent infoIntent = new Intent(v.getContext(), MyInfoActivity.class);
                this.startActivity(infoIntent);
                break;
            case R.id.profile_setting_button:
                Intent settingIntent = new Intent(v.getContext(), MySettingsActivity.class);
                this.startActivity(settingIntent);
                break;
            case R.id.profile_image_circle:

                Intent intent = CropImage.activity()
                        .setAspectRatio(1, 1)
                        .getIntent(getContext());

                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

//    https://stackoverflow.com/questions/38457826/image-crop-not-working-in-fragment
//    https://code.tutsplus.com/tutorials/image-upload-to-firebase-in-android-application--cms-29934

    // Upload the image to Firebase Storage
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                final Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();

                try {
                    thumb_bitmap = new Compressor(getActivity())
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                final StorageReference thumb_filepath = mImageStorage.
                        child("profile_images")
                        .child("thumbs").child(current_user_id + ".jpg");


                final StorageReference ref = mImageStorage.child("profile_images")
                        .child(current_user_id + ".jpg");

                // upload this image to storage
                ref.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // store the online url to user image database
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = uri.toString();

                                        UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);

                                        Task<Uri> urlTask = uploadTask
                                                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }
                                                // Continue with the task to get the download URL
                                                return ref.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {

                                                    // Here we successfully
                                                    // fetched
                                                    // image_download_url
                                                    // and
                                                    // thumb_download_url
                                                    String thumb_downloadUri =
                                                            task.getResult().toString();

                                                    Map update_hashMap = new
                                                            HashMap();

                                                    update_hashMap.put
                                                            ("image",
                                                                    downloadUrl);
                                                    update_hashMap.put
                                                            ("thumb_image",
                                                                    thumb_downloadUri);
                                                    mUserDatabase.updateChildren
                                                            (update_hashMap)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    // dismiss the dialog after everything is complete
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(getActivity(),
                                                                            "Uploaded", Toast.LENGTH_SHORT).show();

                                                                }
                                                            });



                                                } else {
                                                    // Handle failures
                                                    progressDialog.dismiss();
                                                    Toast.makeText
                                                            (getActivity(),
                                                                    "Store " +
                                                                            "URL Failed",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("error ->", String.valueOf(error));
            }
        }
    }

}
