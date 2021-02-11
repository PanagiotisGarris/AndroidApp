package com.example.smokebot;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserMessageViewHolder extends RecyclerView.ViewHolder {
    //Defining needed views for USER
    TextView user_msg, timestamp;
    LinearLayout user_layout;
    Boolean was_clicked = true;
    public UserMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        //From user_msg_layout fetching the chatMessage widget and the layout
        user_msg = itemView.findViewById(R.id.chatMessage);
        user_layout = itemView.findViewById(R.id.user_msg_layout);
        timestamp = itemView.findViewById(R.id.timestamp);
    }

    //Method that is called on MessageListAdapter.java inside onBindViewHolder in order to set to user_msg(TextView) the user's message
    void bind(String message){
        //Getting Timestamp
        String TimeStamp = new SimpleDateFormat("HH:mm").format(new Date());
        user_msg.setText(message);
        timestamp.setText(TimeStamp);
        user_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(was_clicked){
                    timestamp.setVisibility(View.VISIBLE);
                    was_clicked = false;
                }
                else{
                    timestamp.setVisibility(View.INVISIBLE);
                    was_clicked = true;
                }

            }
        });
    }
}
