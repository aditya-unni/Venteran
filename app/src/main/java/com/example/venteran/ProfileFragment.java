package com.example.venteran;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    EditText mviewusername;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    TextView mmovetoupdateprofile;

    FirebaseFirestore firebaseFirestore;

    ImageView mviewuserimageinimageview;

    StorageReference storageReference;

    private String ImageURIacessToken;

    androidx.appcompat.widget.Toolbar mtoolbarofviewprofile;
    ImageButton mbackbuttonofviewprofile;


    FirebaseStorage firebaseStorage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_profile,container,false);
        mviewuserimageinimageview=v.findViewById(R.id.viewuserimageinimageview);
        mviewusername=v.findViewById(R.id.viewusername);
        mmovetoupdateprofile=v.findViewById(R.id.movetoupdateprofile);
        firebaseFirestore=FirebaseFirestore.getInstance();

        firebaseDatabase=FirebaseDatabase.getInstance("https://venteran-56fbc-default-rtdb.asia-southeast1.firebasedatabase.app/");
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();







        storageReference=firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageURIacessToken=uri.toString();
                Picasso.get().load(uri).into(mviewuserimageinimageview);

            }
        });

        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userprofile muserprofile=snapshot.getValue(userprofile.class);
                mviewusername.setText(muserprofile.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(),"Failed To Fetch",Toast.LENGTH_SHORT).show();
            }
        });


        mmovetoupdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(ProfileActivity.this,UpdateProfile.class);
//                intent.putExtra("nameofuser",mviewusername.getText().toString());
//                startActivity(intent);
            }
        });
        return v;
    }
}
