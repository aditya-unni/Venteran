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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.concurrent.TimeUnit;

public class UpdateProfile extends AppCompatActivity {

    private  EditText mnewusername;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;


    private FirebaseFirestore firebaseFirestore;

    private  ImageView mgetnewimageinimageview;

    private StorageReference storageReference;

    private String ImageURIacessToken;

    private androidx.appcompat.widget.Toolbar mtoolbarofupdateprofile;
    private ImageButton mbackbuttonofupdateprofile;


    private FirebaseStorage firebaseStorage;


    ProgressBar mprogressbarofupdateprofile;

    private Uri imagepath;

    Intent intent;

    private static int PICK_IMAGE=123;

    android.widget.Button mupdateprofilebutton;
    String newname, msenderuid;
    TextView update_availablemessage;

    private boolean available = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        mtoolbarofupdateprofile=findViewById(R.id.toolbarofupdateprofile);
        mbackbuttonofupdateprofile=findViewById(R.id.backbuttonofupdateprofile);
        mgetnewimageinimageview=findViewById(R.id.getnewuserimageinimageview);
        mprogressbarofupdateprofile=findViewById(R.id.progressbarofupdateprofile);
        mnewusername=findViewById(R.id.getnewusername);
        mupdateprofilebutton=findViewById(R.id.updateprofilebutton);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance("https://venteran-56fbc-default-rtdb.asia-southeast1.firebasedatabase.app/");
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        update_availablemessage = findViewById(R.id.update_availablemessage);

        intent=getIntent();

        msenderuid = FirebaseAuth.getInstance().getUid();

        setSupportActionBar(mtoolbarofupdateprofile);

        mbackbuttonofupdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        mnewusername.setText(intent.getStringExtra("nameofuser"));


        mgetnewimageinimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        storageReference=firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageURIacessToken=uri.toString();
                Picasso.get().load(uri).into(mgetnewimageinimageview);
            }
        });

        mnewusername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                com.google.firebase.firestore.Query query = firebaseFirestore.collection("Users").whereNotEqualTo("uid", msenderuid);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            newname = mnewusername.getText().toString().trim();
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
                                mupdateprofilebutton.setClickable(true);
                                update_availablemessage.setText("");
                            }
                            else{
                                mupdateprofilebutton.setClickable(false);
                                update_availablemessage.setText("Username Unavailable");
                            }
                        } else {
                            Log.d("Document_Error", "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });

        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        mupdateprofilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newname = mnewusername.getText().toString();
                if(newname.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Name is Empty",Toast.LENGTH_SHORT).show();
                }
                else if(imagepath!=null)
                {
                    mprogressbarofupdateprofile.setVisibility(View.VISIBLE);
                    userprofile muserprofile =new userprofile(newname,firebaseAuth.getUid());
                    databaseReference.setValue(muserprofile);

                    updateimagetostorage();

                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                    mprogressbarofupdateprofile.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent(UpdateProfile.this,navigation_drawer.class);
                    startActivity(intent);
                    finish();

                }
                else
                {

                    mprogressbarofupdateprofile.setVisibility(View.VISIBLE);
                    userprofile muserprofile =new userprofile(newname,firebaseAuth.getUid());
                    databaseReference.setValue(muserprofile);
                    updatenameoncloudfirestore();
                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();
                    mprogressbarofupdateprofile.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent(UpdateProfile.this,navigation_drawer.class);
                    startActivity(intent);
                    finish();

                }

            }
        });



    }





    private void updatenameoncloudfirestore() {

        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("username",newname)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        documentReference.update("image",ImageURIacessToken)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

//        Map<String , Object> userdata=new HashMap<>();
//        userdata.put("username",newname);
//        userdata.put("image",ImageURIacessToken);
//        userdata.put("uid",firebaseAuth.getUid());
//        userdata.put("status","Online");
//        userdata.put("phone", getIntent().getStringExtra("phone"));
//        userdata.put("role", "General");
//        userdata.put("points", 0);
//
//
//
//
//        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getApplicationContext(),"Profile Update Succusfully",Toast.LENGTH_SHORT).show();
//
//            }
//        });

    }

    private void updateimagetostorage() {


        StorageReference imageref=storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");

        //Image compresesion

        Bitmap bitmap=null;
        try {
            bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data=byteArrayOutputStream.toByteArray();

        ///putting image to storage

        UploadTask uploadTask=imageref.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageURIacessToken=uri.toString();
                        Toast.makeText(getApplicationContext(),"URI get sucess",Toast.LENGTH_SHORT).show();
                        updatenameoncloudfirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"URI get Failed",Toast.LENGTH_SHORT).show();
                    }


                });
                Toast.makeText(getApplicationContext(),"Image is Updated",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Image Not Updated",Toast.LENGTH_SHORT).show();
            }
        });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            imagepath=data.getData();
            mgetnewimageinimageview.setImageURI(imagepath);
        }




        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });

    }




}