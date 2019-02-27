package com.example.circculate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String emailText, passwordText, usernameText;
    private static final int MIN_PASSWORD_LENGTH = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        toHomePage(mAuth.getCurrentUser());
        addButtonLisner();
    }

    private void toHomePage(FirebaseUser currentUser){
        if(currentUser != null){
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        }
    }

    private void addButtonLisner(){
        Button signupButton = findViewById(R.id.signup_bt);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyInputs()){
                    //sign up user
                    mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        //sign up succeed, create user in database.
                                        addUserToDB(mAuth.getCurrentUser());
                                    }else{
                                        showToast("Fail to sign up.");
                                    }
                                }
                            });
                }
            }
        });
    }

    private void addUserToDB(final FirebaseUser currentUser){
        if(currentUser == null){
            return;
        }

        UserModel newUser = new UserModel(emailText, usernameText);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(currentUser.getUid())
                .set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    showToast("Sign up succeed.");
                    toHomePage(currentUser);
                }else {
                    showToast("Sign up fail.");

                }
            }
        });
    }

    private boolean verifyInputs(){
        TextInputEditText email, password, username, confirmPsd;
        email = findViewById(R.id.email_text);
        password = findViewById(R.id.password_text);
        username = findViewById(R.id.username_text);
        confirmPsd = findViewById(R.id.confirm_text);

        if(TextUtils.isEmpty(email.getText().toString())){
            email.setError("Required");
            return false;
        }else {
            emailText = email.getText().toString();
        }

        if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("Required");
            return false;
        } else if (password.getText().toString().length() < MIN_PASSWORD_LENGTH) {
            password.setError("Password too short.");
            return false;
        } else if(!password.getText().toString().equals(confirmPsd.getText().toString())){
            confirmPsd.setError("Password mush match!");
            return false;

        }
        else{
            passwordText = password.getText().toString();
        }

        //missing password double check.

        if(TextUtils.isEmpty(username.getText().toString())){
            username.setError("Required");
            return false;
        }else{
            usernameText = username.getText().toString();

        }

        return true;
    }

    public void GoToSignIn(View view) {
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }

    public void GoToHomePage(View view) {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
