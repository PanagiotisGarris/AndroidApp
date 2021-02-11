package com.example.smokebot;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {

    RelativeLayout click_to_start_chatting;
    TextView WelcomeTV, CongratsTV;
    DatabaseReference db_ref;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //initializing the widgets and set onclick method to open MainActivity
        WelcomeTV = findViewById(R.id.WelcomeTV);
        CongratsTV = findViewById(R.id.CongratsTV);
        click_to_start_chatting = findViewById(R.id.go_to_chat_layout);

        //getting users instance
        fAuth = FirebaseAuth.getInstance();
        //retriveing users credentials fron Firebase Database
        db_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(fAuth.getCurrentUser().getUid());
        db_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fullname = dataSnapshot.child("fullname").getValue().toString();
                String[] nameAndSurname = fullname.split(" ");
                String name = nameAndSurname[0];
                String rootActivity = getIntent().getStringExtra("RootActivity");
                if(rootActivity.equals("Login")){
                    WelcomeTV.setText("Welcome back to SmokeBot, " +name);
                    CongratsTV.setText(getResources().getString(R.string.WelcomeFromLogin));
                    RelativeLayout RL = (RelativeLayout) findViewById(R.id.welcomelayout);
                    RL.setVisibility(View.VISIBLE);


                }
                else{
                    WelcomeTV.setText(name + ", " + WelcomeTV.getText());
                    RelativeLayout RL = (RelativeLayout) findViewById(R.id.welcomelayout);
                    RL.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        click_to_start_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });




    }
}
