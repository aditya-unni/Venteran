package com.example.venteran;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class CustomEmergencyDialog extends Dialog implements View.OnClickListener{

    public Activity c;
    public Dialog d;
    public Button yes,no;
    public RadioButton radio;


    public CustomEmergencyDialog(@NonNull Activity context) {
        super(context);
        this.c = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_emergency);
        yes = (Button) findViewById(R.id.proceedbtn);
        no = (Button) findViewById(R.id.cancelbtn3);
        radio = (RadioButton) findViewById(R.id.radiobtn);
        yes.setOnClickListener(this);
        yes.setClickable(false);
        no.setOnClickListener(this);
        radio.setOnClickListener(this);
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.radiobtn:
//                switch (v.getId())
//                {
//                    case R.id.cancelbtn3:
//                        Toast.makeText(getOwnerActivity(), "Emergency Cancelled", Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.proceedbtn:
//                        Toast.makeText(getOwnerActivity(), "Radio Clicked and Proceed", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            case R.id.btn_no:
//                dismiss();
//                break;
//            default:
//                break;
//        }
//        dismiss();
//    }

    @Override
    public void onClick(View v) {
        if (radio.isChecked()) {
            yes.setClickable(true);
            switch (v.getId())
            {
                case R.id.cancelbtn3:
                    Toast.makeText(c, "Emergency Cancelled", Toast.LENGTH_SHORT).show();
                    dismiss();
                    break;
                case R.id.proceedbtn:
                    Toast.makeText(c , "Emergency notified. Please wait for some time.", Toast.LENGTH_SHORT).show();
                    dismiss();
                    break;
            }
        }
        else {
            yes.setClickable(false);
            switch (v.getId())
            {
                case R.id.cancelbtn3:
                    Toast.makeText(c , "Emergency Cancelled", Toast.LENGTH_SHORT).show();
                    dismiss();
                    break;
            }
        }

    }

}
