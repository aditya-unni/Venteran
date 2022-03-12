package com.example.venteran;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatBoxActivity extends Fragment implements TextWatcher,GlobalChatAdapter.OnUserClickListenerglobal{
    private String username;
    private String role;



    private WebSocket webSocket;
    private String SERVER_PATH = "ws://venteran-backend.herokuapp.com";
    private EditText messageEdit;
    private ImageButton sendBtn,backbutton;
    private RecyclerView recyclerView;


    private GlobalChatAdapter messageAdapter;
    private List<JSONObject> messagesArrayList;
    private int selectedposition;

    int points;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String displayName = "";
    Intent intent;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    String user_role;
    String ImageURIacessToken;

    View globalview;
    private ActionMode actionMode;



    FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        globalview=inflater.inflate(R.layout.activity_chat_box,container,false);
        Toast.makeText(getContext(), "Loaded ChatActivity", Toast.LENGTH_SHORT).show();
        username = "Slowqueso";

        recyclerView = globalview.findViewById(R.id.global_messagelist);

        messageAdapter = new GlobalChatAdapter(getLayoutInflater(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);
        initiateSocketConnection();
        firebaseStorage = FirebaseStorage.getInstance();


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();


        firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        JSONObject user_data = new JSONObject(document.getData());
                        Log.d("document", document.getData().toString());
                        try {
                            String test_uid = user_data.getString("uid");
                            if(test_uid.equals(firebaseAuth.getUid())){
                                username = user_data.getString("username");
                                Log.d("username", username);
                                user_role = user_data.getString("role");
                                Log.d("role", user_role);
                                ImageURIacessToken = user_data.getString("image");
                                Log.d("fetched_image",ImageURIacessToken);
                                break;
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
        return globalview;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.activity_chat_box);
//        Toast.makeText(ChatBoxActivity.this,
//                "Loaded ChatActivity",
//                Toast.LENGTH_SHORT).show();
//        username = "Slowqueso";
//        intent = getIntent();
//        recyclerView = findViewById(R.id.global_messagelist);
//        backbutton=findViewById(R.id.backbutton);
//        messageAdapter = new GlobalChatAdapter(getLayoutInflater());
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(messageAdapter);
//        initiateSocketConnection();
//        firebaseStorage = FirebaseStorage.getInstance();
//
//
//        firebaseAuth=FirebaseAuth.getInstance();
//        firebaseFirestore=FirebaseFirestore.getInstance();
//
//
//
//
//
//
////        DocumentReference dref=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
////        dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
////            @Override
////            public void onSuccess(DocumentSnapshot documentSnapshot) {
////                firebasemodel Firebasemodel=documentSnapshot.toObject(firebasemodel.class);
////                username=Firebasemodel.getUsername();
////                user_role=Firebasemodel.getRole();
////                ImageURIacessToken=Firebasemodel.getImage();
////                Log.d("CUSTOM_IMAGE", Firebasemodel.getImage().toString());
////                Log.d("CUSTOM",Firebasemodel.getUsername());
////            }
////        });
//        firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        JSONObject user_data = new JSONObject(document.getData());
//                        Log.d("document", document.getData().toString());
//                        try {
//                            String test_uid = user_data.getString("uid");
//                            if(test_uid.equals(firebaseAuth.getUid())){
//                                username = user_data.getString("username");
//                                Log.d("username", username);
//                                user_role = user_data.getString("role");
//                                Log.d("role", user_role);
//                                ImageURIacessToken = user_data.getString("image");
//                                Log.d("fetched_image",ImageURIacessToken);
//                                break;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
////                                    Log.d("User_Role", document.getData().toString());
//                    }
//
//                } else {
//                    Log.d("Document_Error", "Error getting documents: ", task.getException());
//                }
//            }
//        });
//
//        backbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
//    }
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
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(),
                        "Socket Connection Successful!",
                        Toast.LENGTH_SHORT).show();
                initializeView();
            });
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
                getActivity().runOnUiThread(()->{
                    try{
                        firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        JSONObject user_data = new JSONObject(document.getData());
                                        Log.d("document", document.getData().toString());
                                        try {
                                            String test_uid = user_data.getString("uid");
                                            if(test_uid.equals(firebaseAuth.getUid())){
                                                username = user_data.getString("username");
                                                Log.d("username", username);
                                                user_role = user_data.getString("role");
                                                Log.d("role", user_role);
                                                ImageURIacessToken = user_data.getString("image");
                                                Log.d("fetched_image",ImageURIacessToken);
                                                break;
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

                        JSONObject textData = new JSONObject(text);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("uid", textData.getString("uid"));
                        jsonObject.put("imageUrl", textData.getString("imageUrl"));
                        jsonObject.put("role", textData.getString("role"));
                        jsonObject.put("username",textData.getString("username"));
                        jsonObject.put("message", textData.getString("message"));
                        jsonObject.put("timeStamp", textData.getString("timeStamp"));
                        jsonObject.put("isSent", false);
                        jsonObject.put("isSelected",false);
                        messageAdapter.addItem(jsonObject);
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                        messagesArrayList=messageAdapter.getList();
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

        messageEdit = globalview.findViewById(R.id.edit_text_global);
        sendBtn = globalview.findViewById(R.id.text_send_gchat_button);
        sendBtn.setOnClickListener(v -> {
            if(!messageEdit.getText().toString().isEmpty()){
                JSONObject jsonObject = new JSONObject();
                Toast.makeText(getContext(), "Clicked ",Toast.LENGTH_SHORT).show();
                Date date = new Date();
                DateFormat format = new SimpleDateFormat("HH:mm");
                try {
                    jsonObject.put("username", username);
                    jsonObject.put("uid", firebaseAuth.getUid());
                    jsonObject.put("role", user_role);
                    jsonObject.put("message", messageEdit.getText().toString());
                    Log.d("imageurl", ImageURIacessToken);
                    jsonObject.put("timeStamp", format.format(date));
                    jsonObject.put("imageUrl", ImageURIacessToken);
                    jsonObject.put("isSent", true);
                    jsonObject.put("isSelected",false);
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


    ActionMode.Callback actionModeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getActivity().getMenuInflater().inflate(R.menu.global_user_action,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()){
                case R.id.reply_pvt:
                    Toast.makeText(getContext(), "Reply private", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getContext(),SpecificChat.class);
                    try {
                        intent.putExtra("username",messagesArrayList.get(selectedposition).getString("username"));
                        intent.putExtra("receiveruid",messagesArrayList.get(selectedposition).getString("uid"));
                        intent.putExtra("imageuri",messagesArrayList.get(selectedposition).getString("imageUrl"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    return true;

                case R.id.report:
                    firebaseFirestore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    JSONObject user_data = new JSONObject(document.getData());
                                    try {
                                        String userId = messagesArrayList.get(selectedposition).getString("uid");
                                        String test_uid = user_data.getString("uid");
                                        if(test_uid.equals(userId)){
                                            points = user_data.getInt("points");
                                            points -= 1;
                                            if(points < 10) {
                                                document.getReference().update("role", "General").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        user_role = "General";
                                                    }
                                                });
                                            }
                                            else {
                                                document.getReference().update("role", "Counsellor").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        user_role = "Counsellor";
                                                    }
                                                });
                                            }
                                            document.getReference().update("points",points).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "User Reported!",Toast.LENGTH_SHORT);
                                                }
                                            });
                                            break;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else {
                                Log.d("Document_Error", "Error getting documents: ", task.getException());
                            }
                        }
                    });



                    Toast.makeText(getContext(), "Reported", Toast.LENGTH_SHORT).show();
                    return true;

            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            for(int i=0;i<messagesArrayList.size();i++){
                try {
                    messagesArrayList.get(i).put("isSelected",false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            messageAdapter.notifyDataSetChanged();
            actionMode=null;
        }
    };



    @Override
    public void onUserLongClick(int position) {
        if (actionMode==null){
            actionMode=getActivity().startActionMode(actionModeCallBack);
        }
        JSONObject selectedmessage= messageAdapter.getItem(position);

        messagesArrayList=messageAdapter.getList();
        selectedposition=position;


        try {
            messagesArrayList.get(position).put("isSelected",!selectedmessage.getBoolean("isSelected"));
            int totalnumberofmessageselected=0;
            for (JSONObject messages:messagesArrayList){
                if (messages.getBoolean("isSelected")){
                    totalnumberofmessageselected++;
                }
            }
            actionMode.setTitle(String.valueOf(totalnumberofmessageselected));
            if (totalnumberofmessageselected==0||totalnumberofmessageselected==2){
                actionMode.finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageAdapter.notifyDataSetChanged();


    }


}