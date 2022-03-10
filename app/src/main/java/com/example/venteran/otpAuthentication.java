package com.example.venteran;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class otpAuthentication extends AppCompatActivity {

    TextView mchangenumber;
    EditText mgetotp;
    android.widget.Button mverifyotp;
    String enteredotp;

    FirebaseAuth firebaseAuth;
    ProgressBar mprogressbarofotpauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_authentication);

        mchangenumber=findViewById(R.id.changenumber);
        mverifyotp=findViewById(R.id.verifyotp);
        mgetotp=findViewById(R.id.getotp);
        mgetotp.requestFocus();
        mprogressbarofotpauth=findViewById(R.id.progreesbarforauth);

        firebaseAuth=FirebaseAuth.getInstance();




        mchangenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(otpAuthentication.this,Registration.class);
                startActivity(intent);
                finish();
            }
        });

        mverifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredotp=mgetotp.getText().toString();
                if (enteredotp.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter your OTP First",Toast.LENGTH_LONG).show();
                }
                else {
                    mprogressbarofotpauth.setVisibility(View.VISIBLE);
                    String coderecieved=getIntent().getStringExtra("otp");
                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(coderecieved,enteredotp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mprogressbarofotpauth.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_LONG).show();

                    //skip if user has already an account
                    DatabaseReference rootRef = FirebaseDatabase.getInstance("https://venteran-56fbc-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
                    DatabaseReference userNameRef = rootRef.child(firebaseAuth.getUid());
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                Intent intent = new Intent(otpAuthentication.this,navigation_drawer.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                            else {
                                Intent intent=new Intent(otpAuthentication.this,setProfile.class);
                                intent.putExtra("phone", getIntent().getStringExtra("phone"));
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("DATABASE ERROR", databaseError.getMessage()); //Don't ignore errors!
                        }
                    };
                    userNameRef.addValueEventListener(eventListener);


                }
                else{
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        mprogressbarofotpauth.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}