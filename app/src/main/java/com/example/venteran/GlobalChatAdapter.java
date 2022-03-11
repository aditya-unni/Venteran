package com.example.venteran;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GlobalChatAdapter extends RecyclerView.Adapter{
    private static final int TYPE_MESSAGE_SENT = 0;
    private static final int TYPE_MESSAGE_RECEIVED = 1;
    ImageView receiverImage;

    private LayoutInflater inflater;
    private List<JSONObject> messages = new ArrayList<>();

    public GlobalChatAdapter (LayoutInflater inflater) {
        this.inflater = inflater;
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder{
        TextView messageTxt;
        TextView timeofmessage;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageTxt = itemView.findViewById(R.id.sendermessage);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);
        }
    }
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        TextView receiverTxt;
        TextView nameTxt;
        TextView receiverTime;
        TextView receiverRole;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt=itemView.findViewById(R.id.text_reciever);
            receiverTxt=itemView.findViewById(R.id.text_bubblereciver);
            receiverTime=itemView.findViewById(R.id.text_Receivertime);
            receiverImage=itemView.findViewById(R.id.image_reciever);
            receiverRole=itemView.findViewById(R.id.text_role);

        }
    }
    @Override
    public int getItemViewType(int position) {

        JSONObject message = messages.get(position);

        try {
            if (message.getBoolean("isSent")) {

                    return TYPE_MESSAGE_SENT;

            } else {
                    return TYPE_MESSAGE_RECEIVED;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        Log.d("view_type", String.valueOf(viewType));
        switch (viewType) {
            case TYPE_MESSAGE_SENT:
                view = inflater.inflate(R.layout.senderchatlayout, parent, false);
                return new SentMessageHolder(view);
            case TYPE_MESSAGE_RECEIVED:
                view = inflater.inflate(R.layout.demo2, parent, false);
                return new ReceivedMessageHolder(view);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        JSONObject message = messages.get(position);
        try {
            if (message.getBoolean("isSent")) {
                    SentMessageHolder messageHolder = (SentMessageHolder) holder;
                    messageHolder.messageTxt.setText(message.getString("message"));
                    messageHolder.timeofmessage.setText(message.getString("timeStamp"));
            } else {
                    Log.d("message_text", message.getString("message"));
                    ReceivedMessageHolder messageHolder = (ReceivedMessageHolder) holder;

                    messageHolder.nameTxt.setText(message.getString("username"));
                    messageHolder.receiverTxt.setText(message.getString("message"));
                    messageHolder.receiverTime.setText(message.getString("timeStamp"));
//                    messageHolder.receiverRole.setText(message.getString("role"));
                    Log.d("imageUrl", message.getString("imageUrl"));
                    if(message.getString("imageUrl")!=null || message.getString("imageUrl").equals("")){
                        Picasso.get().load(message.getString("imageUrl")).into(receiverImage);
                    }else{
                        receiverImage.setImageResource(R.drawable.pfp_user);
                    }
                }
            }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
            return messages.size();

    }
    public void addItem (JSONObject jsonObject) {
        messages.add(jsonObject);
        notifyDataSetChanged();
    }
}
