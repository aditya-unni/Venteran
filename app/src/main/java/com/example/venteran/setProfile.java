package com.example.venteran;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.backendless.DataPermission;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class setProfile extends AppCompatActivity {

    private CardView mgetuserimage;
    private ImageView mgetuserimageinimageview;
    private static int PICK_IMAGE=123;
    private Uri imagepath=null;

    private EditText mgetusername;

    private android.widget.Button msaveprofile;

    private FirebaseAuth firebaseAuth;
    private String username;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private String ImageUriAccessToken=null;
    String newname;
    private boolean available = false;
    TextView setprofile_availablemessage;

    ProgressBar mprogressbarofsetprofile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_set_profile);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();

        mgetusername=findViewById(R.id.getusername);
        mgetuserimage=findViewById(R.id.getuserimage);
        mgetuserimageinimageview=findViewById(R.id.getuserimageinimageview);
        msaveprofile=findViewById(R.id.saveProfile);
        mprogressbarofsetprofile=findViewById(R.id.progreesbarofsetprofile);

        setprofile_availablemessage = findViewById(R.id.setprofile_availablemessage);


        DatabaseReference rootRef = FirebaseDatabase.getInstance("https://venteran-56fbc-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        DatabaseReference userNameRef = rootRef.child(firebaseAuth.getUid());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Intent intent = new Intent(setProfile.this,navigation_drawer.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("DATABASE ERROR", databaseError.getMessage()); //Don't ignore errors!
            }
        };
        userNameRef.addValueEventListener(eventListener);



        mgetuserimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        msaveprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=mgetusername.getText().toString();
                if(username.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Name is Empty",Toast.LENGTH_LONG).show();
                }
//                else if (imagepath==null){
//                    Toast.makeText(getApplicationContext(),"Image is Empty",Toast.LENGTH_LONG).show();
//                }
                else {
                    mprogressbarofsetprofile.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                    mprogressbarofsetprofile.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent(setProfile.this,navigation_drawer.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mgetusername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                com.google.firebase.firestore.Query query = firebaseFirestore.collection("Users");
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            newname = mgetusername.getText().toString().replaceAll(" ", "");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                JSONObject user_data = new JSONObject(document.getData());
                                try {
                                    String fuser_name = user_data.getString("username");
                                    if (newname.equals(fuser_name)) {
                                        available = false;
                                        break;
                                    }
                                    else if(newname.isEmpty()) {
                                        available = false;
                                    }
                                    else{
                                        available = true;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            if(available){
                                msaveprofile.setClickable(true);
                                setprofile_availablemessage.setText("");
                            }
                            else{
                                msaveprofile.setClickable(false);
                                setprofile_availablemessage.setText("Username Unavailable");
                            }
                        } else {
                            Log.d("Document_Error", "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });

    }

    private void sendDataForNewUser(){
        sendDataToRealTimeDatabase();
    }
    private void sendDataToRealTimeDatabase(){
        username=mgetusername.getText().toString().trim();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance("https://venteran-56fbc-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        userprofile muserprofile=new userprofile(username,firebaseAuth.getUid());
        databaseReference.setValue(muserprofile);
        Toast.makeText(getApplicationContext(), "User Profile Added Successfully", Toast.LENGTH_SHORT).show();
        if (imagepath==null){
            sendDataTocloudFirestore();
        }
        else {
            sendImagetoStorage();
        }
    }

    private void sendImagetoStorage(){
        StorageReference imageref=storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");

        //Image Compression

        Bitmap bitmap=null;
        try{
            bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
        }
        catch (IOException e){
            e.printStackTrace();

        }
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data=byteArrayOutputStream.toByteArray();

        //putting image to storage

        UploadTask uploadTask=imageref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageUriAccessToken=uri.toString();
                        Toast.makeText(getApplicationContext(), "URI get Success", Toast.LENGTH_SHORT).show();
                        sendDataTocloudFirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "URI get Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(), "Image is Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image not Uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendDataTocloudFirestore() {
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String , Object> userdata=new HashMap<>();
        userdata.put("username",username);
        userdata.put("image",ImageUriAccessToken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Offline");
        userdata.put("phone", getIntent().getStringExtra("phone"));
        userdata.put("role", "General");
        userdata.put("points", 0);

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Data on Cloud", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==PICK_IMAGE && resultCode==RESULT_OK){
            imagepath=data.getData();
            mgetuserimageinimageview.setImageURI(imagepath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        DatabaseReference rootRef = FirebaseDatabase.getInstance("https://venteran-56fbc-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
//        DatabaseReference userNameRef = rootRef.child(firebaseAuth.getUid());
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()) {
//                    Intent intent = new Intent(setProfile.this,navigation_drawer.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("DATABASE ERROR", databaseError.getMessage()); //Don't ignore errors!
//            }
//        };
//        userNameRef.addValueEventListener(eventListener);
//    }
}