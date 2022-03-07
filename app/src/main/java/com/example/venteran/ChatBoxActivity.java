package com.example.venteran;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatBoxActivity extends AppCompatActivity implements TextWatcher{
    private String username;
    private WebSocket webSocket;
    private String SERVER_PATH = "ws://venteran-backend.herokuapp.com";
    private EditText messageEdit;
    private ImageButton sendBtn;
    private RecyclerView recyclerView;
    private GlobalChatAdapter messageAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String displayName = "";
    Intent intent;

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
        messageAdapter = new GlobalChatAdapter(getLayoutInflater());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
        initiateSocketConnection();
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
                        jsonObject.put("username","Random");
                        jsonObject.put("message", textData.getString("message"));
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
                try {
                    jsonObject.put("username", username);
                    jsonObject.put("message", messageEdit.getText().toString());
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