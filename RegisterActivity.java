package com.example.smokebot;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

        EditText Fullname, Email, Password;
        Button Register_Button;
        TextView go_to_login;
        ProgressBar progressBar;
        FirebaseAuth fAuth;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);
            //initializing the widgets
            go_to_login = findViewById(R.id.AlreadyRegistered);
            go_to_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });

            Fullname = findViewById(R.id.FullNameET);
            Email = findViewById(R.id.EmailET);
            Password = findViewById(R.id.PasswordRegister);
            Register_Button = findViewById(R.id.RegisterButton);
            progressBar = findViewById(R.id.progressBar);
            //Fetching the instance of the firebase database
            fAuth = FirebaseAuth.getInstance();
        //Checking if the user is already logged in
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }

        //Register button on click
        Register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting users credentials
                String fullname = Fullname.getText().toString().trim();
                String email = Email.getText().toString().trim();
                String password = Password.getText().toString().trim();
                //if one of email or password is null user cannot register
                if(TextUtils.isEmpty(email)){
                    Email.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Password.setError("Password is Required");
                    return;
                }
                if(password.length() < 6){
                    Password.setError("Password must contain at least 6 Characters");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                //register the user in Firebase
                //checking whether or not the registration was succesful
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            //Creating an object from User class to store the users credentials in Firebase Real Time DB
                            User user = new User(fullname, email);
                            //Creating node in Firebase Database
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user);
                            Toast.makeText(RegisterActivity.this,"User Created!",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, WelcomeActivity.class);
                            intent.putExtra("RootActivity", "Register");
                            startActivity(intent);
                            finish();

                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"Error! " + task.getException().getMessage() ,Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
            }
        });


    }
}
