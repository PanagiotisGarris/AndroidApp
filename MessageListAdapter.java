package com.example.smokebot;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Initializing the needed variables for
    // getting Context, MessageList, and declaring if the message has came FROM_BOT || FROM_USER
    public Context mContext;
    private List<MessageClass> mMessageList;
    private static final int FROM_BOT = 0;
    private static final int FROM_USER = 1;

    //Constructor for the MessageListAdapter Class
    //That getting as arguments the current context and the List with the messages
    public MessageListAdapter(Context context, List<MessageClass> messageList) {
        this.mContext = context;
        mMessageList = messageList;
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        MessageClass msg_obj = this.mMessageList.get(position);

        if (MessageClass.FROM_BOT == msg_obj.getMsgType()) {
            // If the message was sent from the BOT
            return FROM_BOT;
        } else {
            // If the message was sent from the USER
            return FROM_USER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Declaring a view in order to pass it as an argument to the ViewHolders classes
        View view;
        //if statement for checking if the viewtype that will be inflated, will be the BOT one or the USER one
        if (viewType == FROM_BOT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bot_msg_layout, parent, false);
            view.getLayoutParams().width = parent.getWidth();
            return new BotMessageViewHolder(view);
        } else if (viewType == FROM_USER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_msg_layout, parent, false);
            view.getLayoutParams().width = parent.getWidth();
            return new UserMessageViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Creating an object from MessageClass in order to get the current position in messageList and call the appropriate methods
        MessageClass msg_obj = this.mMessageList.get(position);
        //Using the msg_obj object to get the QuickReplies List
        List<String> QuickRepliesList = msg_obj.getQuickR();
        //Switch Case depending on the sender of the message
        switch (holder.getItemViewType()){
            case FROM_BOT:
                //Creating BotMessageViewHolder instance
                BotMessageViewHolder botHolder = (BotMessageViewHolder) holder;
                //Calling the method from ViewHolder that sets the text to the appropriate TextView
                botHolder.bind(msg_obj.getMsgContent());
                //Checking if QuickRepliesList isn't empty and Calling the addButtons method to dynamically create Buttons for each Quick Reply
                if(QuickRepliesList.size() != 0){
                    addButtons(QuickRepliesList, botHolder, mContext);
                }
                break;
            case FROM_USER:
                //Creating UserMessageViewHolder instance
                UserMessageViewHolder userHolder = (UserMessageViewHolder) holder;
                //Calling the method from ViewHolder that sets the text to the appropriate TextView
                userHolder.bind(msg_obj.getMsgContent());
                break;
            default:
                break;
        }
    }

    @Override //getItemCount() Method for getting the size of the list each time
    public int getItemCount() {
        if(mMessageList==null)
        {
            mMessageList = new ArrayList<MessageClass>();
        }
        return mMessageList.size();
    }

    //Method for dynamically creating Buttons for each Quick Reply
    void addButtons(List<String> QR, BotMessageViewHolder botHolder, Context context){
        //Counting the QuickRepliesList Size
        int QRcount = QR.size();
        //Declaring LinearLayout and fetching the bot_layout from BotMessageViewHolder
        LinearLayout ll = botHolder.bot_layout;
        //Fixing the Layout Parameters
        LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout_params.setMargins(4,4,4,4);
        layout_params.gravity = Gravity.CENTER_HORIZONTAL;
        //for loop through the QuickRepliesList
        for (int i = 0; i< QRcount; i ++){
            //Creating Button for each Quick Reply
            Button qr_btn = new Button(ll.getContext());
            qr_btn.setId(i);
            qr_btn.setText(QR.get(i));
            //Attaching the trasparent border only drawable
            qr_btn.setBackgroundResource(R.drawable.quick_replies_buttons);
            //Define Layout Parameters in order to fix them
            qr_btn.setPadding(10,0,10,0);
            LinearLayout.LayoutParams Button_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            Button_params.setMargins(0, 0, 0, 10);
            Button_params.gravity = Gravity.CENTER;
            //Adding the button to BOT Layout
            ll.addView(qr_btn, Button_params);
            //Method when Button is clicked
            qr_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Getting Button Text
                    String ButtonText = qr_btn.getText().toString();
                    //Checking if current context is instance of MainActivity and then calling sendMesage method with Button Text as users response
                    if(mContext instanceof MainActivity){
                        ((MainActivity)mContext).sendMessage(v,ButtonText);
                    }
                }
            });
        }
    }
}
