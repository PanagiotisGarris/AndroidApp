package com.example.smokebot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import ai.api.AIListener;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import ai.api.android.AIService;
import ai.api.android.AIConfiguration;
import ai.api.model.ResponseMessage;

import android.view.View.OnClickListener;

import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;

import java.sql.Time;
import java.text.SimpleDateFormat;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import java.util.List;
import java.util.ArrayList;

import java.io.InputStream;
import java.util.UUID;
import java.util.Date;

import com.google.api.Distribution;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.Intent.Message;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.Intent.Message.QuickReplies;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private String uuid = UUID.randomUUID().toString();
    private SessionsClient sessionsClient;
    private SessionName session;
    private EditText queryEditText;
    private TextView timestamp;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    List<MessageClass> messageList;
    List<String> QuickReplies = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initializing queryEditText and imageview(sendBtn)
        ImageView sendBtn = findViewById(R.id.sendBtn);
        queryEditText = findViewById(R.id.queryEditText);

        //on sendBtn click listener to call sendMessage() method
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retrieving the query written by user
                String msg = queryEditText.getText().toString();
                //Calling sendMessage method
                sendMessage(v, msg);

            }
        });
        //Setting OnKelistener to queryEditText
        queryEditText.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        sendMessage(sendBtn, queryEditText.getText().toString());
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });

        //Java V2 initialize chatbot
        initV2Chatbot();

        //Defining and initializing RecyclerView and MessageListAdapter
        messageList = new ArrayList<MessageClass>();
        mMessageRecycler = findViewById(R.id.recyclerview);
        mMessageAdapter = new MessageListAdapter(this, messageList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mMessageRecycler.setLayoutManager(llm);
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.setItemViewCacheSize(50);

    }
    //Method for initializing the ChatBot
    private void initV2Chatbot(){
        try{
            //V2 api
            //Initializing the chatbot from json
            InputStream stream = getResources().openRawResource(R.raw.test_agent_credentials);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials)credentials).getProjectId();
            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            session = SessionName.of(projectId, uuid);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendMessage(View view, String msg){

        //if queryEditText is empty show the warning!
        if(msg.trim().isEmpty()){
            Toast.makeText(MainActivity.this, "Please enter your query!", Toast.LENGTH_SHORT).show();
        }
        else{
            //adding user message
            MessageClass msg_To = new MessageClass(MessageClass.FROM_USER, msg, QuickReplies);
            messageList.add(msg_To);
            int NewMsgPosition = messageList.size() - 1;
            // Notify recycler view insert one new data.
            mMessageAdapter.notifyItemInserted(NewMsgPosition);
            // Scroll RecyclerView to the last message.
            mMessageRecycler.scrollToPosition(NewMsgPosition);
            // Empty the input edit text box.
            queryEditText.setText("");
            // Java V2 sending query to cloud
            QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(msg).setLanguageCode("en")).build();
            new com.example.smokebot.RequestJavaV2Task(MainActivity.this, session, sessionsClient, queryInput).execute();
        }
    }

    public void callbackV2(DetectIntentResponse response){
        //Emptying the Quick Replies List in order to add the new ones
        QuickReplies.clear();
        if(response != null){
            //process aiResponse here
            int responsesCount = response.getQueryResult().getFulfillmentMessagesCount();
            String botReply= "";
            QuickReplies hasQuickReply;

            //Checking if Fullfillment has more than one responses in order to view them all
            if(responsesCount >=2){
                //Looping through Fullfillment responses
                for(int i=0; i < responsesCount; i++){
                    Message botReplies = response.getQueryResult().getFulfillmentMessages(i);
                    //Counting the text amount of each response and adding responses to BotReply
                    int TextResponseCount = botReplies.getText().getTextCount();
                    for(int y=0; y < TextResponseCount; y++){

                        botReply += botReplies.getText().getText(y) + "\n";

                    }
                    //checking if response contains any QuickReply
                    hasQuickReply = response.getQueryResult().getFulfillmentMessages(i).getQuickReplies();
                    if(hasQuickReply.toString() != " "){
                        //counting the quick replies existing
                        int QuickRepliesCount = hasQuickReply.getQuickRepliesCount();
                        //Looping through quickreplies to fetch them all
                        for (int q = 0; q < QuickRepliesCount; q++){
                            //Updating the list with the new QuickReplies
                            QuickReplies.add(hasQuickReply.getQuickReplies(q));
                        }
                    }
                }
                //adding responses and sending to Adapter
                MessageClass msg_To = new MessageClass(MessageClass.FROM_BOT, botReply, QuickReplies);
                messageList.add(msg_To);
                int NewMsgPosition = messageList.size() - 1;
                // Notify recycler view insert one new data.
                mMessageAdapter.notifyItemInserted(NewMsgPosition);
                // Scroll RecyclerView to the last message.
                mMessageRecycler.scrollToPosition(NewMsgPosition);
            }
            //if response is only one
            else{
                //Fetching it
                botReply = response.getQueryResult().getFulfillmentText();
                //adding response and sending to Adapter
                MessageClass msg_To = new MessageClass(MessageClass.FROM_BOT, botReply, QuickReplies);
                messageList.add(msg_To);
                int NewMsgPosition = messageList.size() - 1;
                // Notify recycler view insert one new data.
                mMessageAdapter.notifyItemChanged(NewMsgPosition);
                // Scroll RecyclerView to the last message.
                mMessageRecycler.scrollToPosition(NewMsgPosition);
            }
        }
        else{
            String ErrorMessage = "There was some communication issue. Please Try again!";
            MessageClass msg_To = new MessageClass(MessageClass.FROM_BOT, ErrorMessage, QuickReplies);
            messageList.add(msg_To);
            int NewMsgPosition = messageList.size() - 1;
            // Notify recycler view insert one new data.
            mMessageAdapter.notifyItemInserted(NewMsgPosition);
            // Scroll RecyclerView to the last message.
            mMessageRecycler.scrollToPosition(NewMsgPosition);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Logout method called when Logout Menu option is clicked
    public void Logout(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
