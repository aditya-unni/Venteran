package com.example.venteran;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    OnUserClickListener listener;

    ArrayList<Messages> messagesArrayList;

    int ITEM_SEND=1;
    int ITEM_RECIEVE=2;


    ActionMode actionMode=null;
    View customview;
    Toolbar toolbar;


    public interface OnUserClickListener{
        void onUserLongClick(int position);
//        void onDelete(int position);
    }


    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList,OnUserClickListener listener) {
        this.context = context;
        toolbar=((Activity)context).findViewById(R.id.toolbarofspecificchat);
        this.messagesArrayList = messagesArrayList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SEND)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.senderchatlayout,parent,false);
            return new SenderViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.recieverchatlayout,parent,false);
            return new RecieverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages=messagesArrayList.get(position);
        if(holder.getClass()==SenderViewHolder.class)
        {
            SenderViewHolder viewHolder=(SenderViewHolder)holder;
            viewHolder.textViewmessaage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());


            if (messages.isSelected()){
                viewHolder.senderlayout.setBackgroundResource(R.drawable.onholdsender);
            }
            else {
                viewHolder.senderlayout.setBackgroundResource(R.drawable.senderchatdrawable);
            }

            if (messages.isDeleted()){
                messagesArrayList.remove(position);
            }

            viewHolder.senderlayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    if (actionMode != null) {
//                        return false;
//                    }
//
//                    // Start the CAB using the ActionMode.Callback defined above
//                    actionMode = toolbar.startActionMode(actionModeCallback);
//                    toolbar.setSelected(true);
                    listener.onUserLongClick(viewHolder.getAdapterPosition());
                    return true;
                }
            });

        }
        else
        {
            RecieverViewHolder viewHolder=(RecieverViewHolder)holder;
            viewHolder.textViewmessaage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());





            if (messages.isSelected()){
                viewHolder.receiverlayout.setBackgroundResource(R.drawable.onholdsender);
            }
            else {
                viewHolder.receiverlayout.setBackgroundResource(R.drawable.recieverchatdrawable);
            }

            if (messages.isDeleted()){
                messagesArrayList.remove(position);
            }

            viewHolder.receiverlayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                    if (actionMode != null) {
//                        return false;
//                    }
//
//                    // Start the CAB using the ActionMode.Callback defined above
//                    actionMode = toolbar.startActionMode(actionModeCallback);
//                    toolbar.setSelected(true);
                    listener.onUserLongClick(viewHolder.getAdapterPosition());
                    return true;
                }
            });
        }


//        delete message function



    }


    @Override
    public int getItemViewType(int position) {
        Messages messages=messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId()))

        {
            return  ITEM_SEND;
        }
        else
        {
            return ITEM_RECIEVE;
        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }








    class SenderViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewmessaage;
        TextView timeofmessage;
        RelativeLayout senderlayout;


        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessaage=itemView.findViewById(R.id.sendermessage);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);
            senderlayout=itemView.findViewById(R.id.layoutformessage);


        }
    }

    class RecieverViewHolder extends RecyclerView.ViewHolder
    {

        TextView textViewmessaage;
        TextView timeofmessage;
        RelativeLayout receiverlayout;


        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessaage=itemView.findViewById(R.id.sendermessage);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);
            receiverlayout=itemView.findViewById(R.id.layoutformessage);
        }
    }
//
//    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
//
//        // Called when the action mode is created; startActionMode() was called
//        @Override
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            // Inflate a menu resource providing context menu items
//            MenuInflater inflater = mode.getMenuInflater();
//            inflater.inflate(R.menu.user_action, menu);
//            return true;
//        }
//
//
//        // Called each time the action mode is shown. Always called after onCreateActionMode, but
//        // may be called multiple times if the mode is invalidated.
//        @Override
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            return false; // Return false if nothing is done
//        }
//
//        // Called when the user selects a contextual menu item
//        @Override
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.bin:
//                    Toast.makeText(context.getApplicationContext(), "Message deleted", Toast.LENGTH_SHORT).show();
//                    mode.finish(); // Action picked, so close the CAB
//                    return true;
//                default:
//                    return false;
//            }
//        }
//
//        // Called when the user exits the action mode
//        @Override
//        public void onDestroyActionMode(ActionMode mode) {
//            actionMode = null;
//        }
//    };
}