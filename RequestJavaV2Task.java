package com.example.smokebot;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.DetectIntentRequest;

import android.app.Activity;

import android.os.AsyncTask;

public class RequestJavaV2Task extends AsyncTask<Void, Void, DetectIntentResponse>{

    Activity activity;
    private SessionName session;
    private SessionsClient sessionsClient;
    private QueryInput queryInput;

    //Constructor
    RequestJavaV2Task(Activity activity, SessionName session, SessionsClient sessionsClient, QueryInput queryInput) {
        this.activity = activity;
        this.session = session;
        this.sessionsClient = sessionsClient;
        this.queryInput = queryInput;
    }


    @Override
    protected DetectIntentResponse doInBackground(Void... voids) {

        try{
            DetectIntentRequest detectIntentRequest =
                    DetectIntentRequest.newBuilder()
                            .setSession(session.toString())
                            .setQueryInput(queryInput)
                            .build();
            return sessionsClient.detectIntent(detectIntentRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //Fetch response and call callbackV2 method from MainActivity to print them
    @Override
    protected void onPostExecute(DetectIntentResponse response) {
        ((MainActivity) activity).callbackV2(response);
    }

}
