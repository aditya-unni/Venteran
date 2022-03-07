package com.example.venteran;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GlobalChatAdapter extends RecyclerView.Adapter{
    private static final int TYPE_MESSAGE_SENT = 0;
    private static final int TYPE_MESSAGE_RECEIVED = 1;

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
        TextView timeofmessage;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt=itemView.findViewById(R.id.text_reciever);
            receiverTxt=itemView.findViewById(R.id.text_bubblereciver);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);
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
            } else {
                    Log.d("message_text", message.getString("message"));
                    ReceivedMessageHolder messageHolder = (ReceivedMessageHolder) holder;
                    messageHolder.nameTxt.setText(message.getString("username"));
                    messageHolder.receiverTxt.setText(message.getString("message"));
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
