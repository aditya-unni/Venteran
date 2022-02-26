package com.example.venteran;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

public class Registration extends AppCompatActivity {

    EditText mgetphonenumber;
    android.widget.Button mregister;
    CountryCodePicker mcountrycodepicker;
    String countrycode;
    String phonenumber;

    FirebaseAuth firebaseAuth;
    ProgressBar mprogressbarofmain;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String codesent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mcountrycodepicker=findViewById(R.id.ccp);
        mregister=findViewById(R.id.regbtn);
        mgetphonenumber=findViewById(R.id.reg_mobile);
        mprogressbarofmain=findViewById(R.id.progreesbarforreg);

        firebaseAuth=FirebaseAuth.getInstance();
    }
}