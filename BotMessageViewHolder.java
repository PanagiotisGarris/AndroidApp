package com.example.smokebot;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BotMessageViewHolder extends RecyclerView.ViewHolder {
    //Defining needed views for the BOT messages and QuickRepliesList that contains the quickreplies fetched by Dialogflow.
    TextView bot_msg, timestamp;
    LinearLayout bot_layout;
    List<String> QRList = new ArrayList<>();
    Boolean was_clicked =true;

    public BotMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        //From bot_msg_layout fetching the chatMessage widget and the layout
        bot_msg = itemView.findViewById(R.id.chatMessage);
        timestamp = itemView.findViewById(R.id.timestamp);
        bot_layout = itemView.findViewById(R.id.bot_msg_layout);
    }

    //Method that is called on MessageListAdapter.java inside onBindViewHolder in order to set to bot_msg(TextView) the BOT's message
    void bind(String message){
        //Getting Timestamp
        String TimeStamp = new SimpleDateFormat("HH:mm").format(new Date());
        bot_msg.setText(message);
        timestamp.setText(TimeStamp);
        bot_msg.setOnClickListener(new View.OnClickListener() {
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
