package com.example.venteran;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.venteran.Notification.Client;
import com.example.venteran.Notification.Data;
import com.example.venteran.Notification.MyResponse;
import com.example.venteran.Notification.Sender;
import com.example.venteran.Notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomEmergencyDialog extends Dialog implements View.OnClickListener{

    public Activity c;
    public Dialog d;
    public Button yes,no;
    public RadioButton radio;

    FirebaseUser fuser;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    boolean notify = false;

    String msenderuid, msendername, mrecieveruid;
    APIService apiService;

    List<String> list = new ArrayList<>();

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

        msenderuid = FirebaseAuth.getInstance().getUid();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


        DatabaseReference tempReference = FirebaseDatabase.getInstance("https://venteran-56fbc-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child(msenderuid);

        tempReference.child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                msendername = (String) snapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        updateToken(FirebaseInstanceId.getInstance().getToken());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

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
                    com.google.firebase.firestore.Query query = firebaseFirestore.collection("Users").whereNotEqualTo("uid",firebaseAuth.getUid());
                    firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    JSONObject user_data = new JSONObject(document.getData());
                                    try {
                                        String user_role = user_data.getString("role");
                                        mrecieveruid = user_data.getString("uid");
                                        if (user_role.equals("Counselor")) {
                                            sendNotification(mrecieveruid, msendername);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
//                                    Log.d("User_Role", document.getData().toString());
                                }

                            } else {
                                Log.d("Document_Error", "Error getting documents: ", task.getException());
                            }
                        }
                    });
                    Toast.makeText(c , "Counselors has been notified", Toast.LENGTH_SHORT).show();
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

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://venteran-56fbc-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Tokens");
        Token tokenl = new Token(token);
        reference.child(fuser.getUid()).setValue(tokenl);
    }


    private void sendNotification(String receiver, String username) {
        DatabaseReference tokens = FirebaseDatabase.getInstance("https://venteran-56fbc-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.drawable.ic_image_venteranlogo, username + " needs help. ASAP!",
                            "Emergency!! Attention Required!", receiver);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200) {
                                        if(response.body().success != 1) {
                                            Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
