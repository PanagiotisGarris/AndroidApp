package com.example.smokebot;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText Email, Password, Fullname;
    Button loginButton;
    TextView register;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    boolean show_pass = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initializing widgets from layout
        register = findViewById(R.id.RegisterTV);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Fullname = findViewById(R.id.FullNameET);
        Email = findViewById(R.id.EmailET);
        Password = findViewById(R.id.passwordET);
        loginButton = findViewById(R.id.LoginButton);
        progressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();

        //Checking if the user is already logged in
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }

        //on PassWord EditText click
        Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(show_pass){
                    Password.setTransformationMethod(new PasswordTransformationMethod());
                    show_pass = false;
                }
                else{
                    Password.setTransformationMethod(null);
                    show_pass = true;
                }

            }
        });
        //on Login Button clicked
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting users credentials
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

                //Authenticate the user
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Logged In Successfully!",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                            intent.putExtra("RootActivity", "Login");
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Error! " + task.getException().getMessage() ,Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);

                        }

                    }
                });
            }
        });
    }
}
