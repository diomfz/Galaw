package com.example.galaw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class actEditProfile extends AppCompatActivity {
    public static final String TAG = "Tag";
    TextView name,email,phone, verifyMsg;
    Button resendCode, changeUserData, resetPass;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    FirebaseUser user;
    ImageView profileImage;
    StorageReference storageReference;

    Button backProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_edit_profile);

        name = findViewById(R.id.nameProfile);
        email = findViewById(R.id.emailProfile);
        phone = findViewById(R.id.phoneProfile);
        resetPass = findViewById(R.id.resetPassword );

        profileImage = findViewById(R.id.profileImage);
        changeUserData = findViewById(R.id.editProfile);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("Users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.verifyMsg);


        userID = fAuth.getCurrentUser().getUid();
        final FirebaseUser user = fAuth.getCurrentUser();

        if (!user.isEmailVerified()) {
            resendCode.setVisibility(View.VISIBLE);
            verifyMsg.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(v.getContext(), "Verification Email Has Been Sent", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent " + e.getMessage());

                        }
                    });

                }
            });
        }

        DocumentReference documentReference = fStore.collection("Users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                name.setText(documentSnapshot.getString("Name"));
                email.setText(documentSnapshot.getString("Email"));
                phone.setText(documentSnapshot.getString("Phone"));
            }
        });

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetPassword = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialong = new AlertDialog.Builder(v.getContext());
                passwordResetDialong.setTitle("Reset Password");
                passwordResetDialong.setMessage("Enter New Password > 6 Characters long");
                passwordResetDialong.setView(resetPassword);

                passwordResetDialong.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //extract the email and send reset link
                        String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(actEditProfile.this, "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(actEditProfile.this, "Password Reset Failed", Toast.LENGTH_SHORT).show();
                            }
                        });



                    }
                });

                passwordResetDialong.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close

                    }
                });

                passwordResetDialong.create().show();
            }
        });

        changeUserData.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 // open gallery
                 Intent i = new Intent(v.getContext(),actEdit.class);
                 i.putExtra("fullName",name.getText().toString());
                 i.putExtra("email", email.getText().toString());
                 i.putExtra("phone", phone.getText().toString());
                 startActivity(i);

             }
         });

        backProfile =findViewById(R.id.backProfile);


        backProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent( actEditProfile.this, actHome.class);
                startActivity(intent);


            }
        });

            }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),actLogin.class));
        finish();
    }

}





