package com.example.galaw;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class actSignUp extends AppCompatActivity {

    public static final String TAG = "TAG";


    EditText mpassword;
    EditText memail;
    EditText mphone;
    EditText mname;
    Button msignup, mloginButton;
    RadioButton male, female;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, outputString;
    String AES = "AES";
    RadioGroup gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_sign_up);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        fAuth = FirebaseAuth.getInstance();

        memail = findViewById(R.id.email);
        mpassword = findViewById(R.id.password);
        mname = findViewById(R.id.name);
        mphone = findViewById(R.id.phoneNumber);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        msignup = findViewById(R.id.signup);
        mloginButton = findViewById(R.id.buttonLogin);
        gender = findViewById(R.id.gender);
        fStore = FirebaseFirestore.getInstance();


        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),actHome.class));
            finish();

        }

        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = memail.getText().toString().trim();
                final String password = mpassword.getText().toString().trim();
                final String name = mname.getText().toString();
                final String phoneNumber = mphone.getText().toString();
                final String m1 = male.getText().toString();
                final String m2 = female.getText().toString();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";



                if (gender.getCheckedRadioButtonId() == -1 ){
                    female.setError("Gender is Required");
                    return;
                }



                if (TextUtils.isEmpty(email)) {
                    memail.setError("Email is Required");
                    return;
                }
                if (email.matches(emailPattern)){
                    Toast.makeText(getApplicationContext(),"Valid email address", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Invalid email address", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)) {
                    mpassword.setError("Password is Required");
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    mname.setError("Name is Required");
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    mphone.setError("Phone Number is Required");
                    return;
                }

                if (phoneNumber.length() < 10) {
                    mphone.setError("Phone Number harus lebih dari 9 karakter");
                    return;
                }
                if (phoneNumber.length() > 13) {
                    mphone.setError("Phone Number harus kurang dari 14 karakter");
                    return;
                }

                if (password.length() < 6) {
                    mpassword.setError("Password harus lebih dari 6 karakter");
                    return;
                }
                try {
                    outputString = encrypt(password);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                // register the user in firebase

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // send verification link

                            FirebaseUser fuser = fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(actSignUp.this, "Verification Email Has Been Sent", Toast.LENGTH_SHORT).show();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());

                                }
                            });

                            Toast.makeText(actSignUp.this, "User Created", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference docref = fStore.collection("Quiz").document(userID);
                            Map<String, Object> user1 = new HashMap<>();
                            user1.put("StressScore", null);
                            user1.put("AnxietyScore", null);
                            user1.put("DepressionScore", null);
                            user1.put("Name", name);
                            docref.set(user1);

                            DocumentReference docref1 = fStore.collection("Diary").document(userID);
                            Map<String, Object> user2 = new HashMap<>();
                            user2.put("Judul", null);
                            user2.put("Isi", null);
                            user2.put("Tanggal", null);
                            user2.put("Name", name);
                            docref1.set(user2);


                            DocumentReference documentReference = fStore.collection("Users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("Name", name);
                            user.put("Email", email);
                            user.put("Phone", phoneNumber);

                            if (male.isChecked()) {
                                user.put("Gender", m1);
                            } else {
                                user.put("Gender", m2);
                            }



                            user.put("Password", outputString);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: User Profile is Created for" + userID);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), actHome.class));

                        }else{
                            Toast.makeText(actSignUp.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

        mloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),actLogin.class));
            }
        });
    }

    private String encrypt(String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(password.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }
}