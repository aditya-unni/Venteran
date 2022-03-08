package com.example.venteran;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatBoxActivity extends AppCompatActivity implements TextWatcher{
    private String username;
    private String role;



    private WebSocket webSocket;
    private String SERVER_PATH = "ws://venteran-backend.herokuapp.com";
    private EditText messageEdit;
    private ImageButton sendBtn,backbutton;
    private RecyclerView recyclerView;
    private GlobalChatAdapter messageAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String displayName = "";
    Intent intent;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    String ImageURIacessToken;




    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_box);
        Toast.makeText(ChatBoxActivity.this,
                "Loaded ChatActivity",
                Toast.LENGTH_SHORT).show();
        username = "Slowqueso";
        intent = getIntent();
        recyclerView = findViewById(R.id.global_messagelist);
        backbutton=findViewById(R.id.backbutton);
        messageAdapter = new GlobalChatAdapter(getLayoutInflater());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
        initiateSocketConnection();
        firebaseStorage = FirebaseStorage.getInstance();


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();



        DocumentReference dref=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                firebasemodel Firebasemodel=documentSnapshot.toObject(firebasemodel.class);
                username=Firebasemodel.getUsername();
                role=Firebasemodel.getRole();
                Log.d("CUSTOM",Firebasemodel.getUsername());
            }
        });
        storageReference=firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageURIacessToken=uri.toString();


            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void initiateSocketConnection() {

        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(SERVER_PATH).build();
            webSocket = client.newWebSocket(request, new SocketListener());
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Toast.makeText(ChatBoxActivity.this,
                "Breh",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void afterTextChanged(Editable s) {

        String string = s.toString().trim();
        if(string.isEmpty()){
            resetMessageEdit();
        }
    }

    private void resetMessageEdit() {
        messageEdit.setText("");
    }

    private final class SocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            runOnUiThread(() -> {
                Toast.makeText(ChatBoxActivity.this,
                        "Socket Connection Successful!",
                        Toast.LENGTH_SHORT).show();
                initializeView();
            });
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
                runOnUiThread(()->{
                    try{
                        JSONObject textData = new JSONObject(text);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/venteran-56fbc.appspot.com/o/Images%2F83yoCesmvFXcQmcEgJmEHbuZEti2%2FProfile%20Pic?alt=media&token=bee68a0a-1427-4f9c-b9ac-042761de7fcb");
                        jsonObject.put("username",textData.getString("username"));
                        jsonObject.put("message", textData.getString("message"));
                        jsonObject.put("timeStamp", textData.getString("timeStamp"));
                        jsonObject.put("isSent", false);
                        messageAdapter.addItem(jsonObject);
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                });

        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            t.printStackTrace();
        }


    }
    private void initializeView() {

        messageEdit = findViewById(R.id.edit_text_global);
        sendBtn = findViewById(R.id.text_send_gchat_button);
        sendBtn.setOnClickListener(v -> {
            if(messageEdit.getText().toString() != ""){
                JSONObject jsonObject = new JSONObject();
                Toast.makeText(ChatBoxActivity.this, "Clicked ",Toast.LENGTH_SHORT).show();
                Date date = new Date();
                DateFormat format = new SimpleDateFormat("HH:mm");
                try {
                    jsonObject.put("username", username);
                    jsonObject.put("message", messageEdit.getText().toString());
                    jsonObject.put("timeStamp", format.format(date));
                    jsonObject.put("imageUrl", "https://firebasestorage.googleapis.com/v0/b/venteran-56fbc.appspot.com/o/Images%2F83yoCesmvFXcQmcEgJmEHbuZEti2%2FProfile%20Pic?alt=media&token=bee68a0a-1427-4f9c-b9ac-042761de7fcb");
                    jsonObject.put("isSent", true);
                    webSocket.send(jsonObject.toString());
                    messageAdapter.addItem(jsonObject);
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                    resetMessageEdit();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }else{
                resetMessageEdit();
            }
        });





    }


}