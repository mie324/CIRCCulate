package com.example.circculate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.circculate.Fragment.FavoritesFragment;
import com.example.circculate.Model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LogIn extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextInputEditText email, password;
    private ProgressDialog progressDialog;
    private FirebaseFirestore db;
    private UserModel user_u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            toHomeActivity();
        }
    }

    public void loginToHome(View view){
        if(verifyEmailPassword()){
            //start log in process
            if(email != null && password != null){
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Logging in...");
                progressDialog.show();
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    toHomeActivity();
                                }else {
                                    showToast("Invalid email or password");
                                }
                            }
                        });
            }
        }
    }

    public void toSignupActivity(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    private void toHomeActivity() {
        //get user and reconstruct
        // UserModel user = ...

//        final Bundle bundle = new Bundle();
        final Intent intent = new Intent(this, HomePage.class);
//        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot doc = task.getResult();
//                    if(doc.exists()){
////                        FavoritesFragment fragment1 = new FavoritesFragment();
//                        user_u = doc.toObject(UserModel.class);
////                        bundle.putSerializable("loggedInUser", user_u);
////                        fragment1.setArguments(bundle);
//                        intent.putExtra("loggedUser", user_u);
//                        Log.d("username1", user_u.getUsername());
////                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment1).commit();
//                        startActivity(intent);
//                    }
//                }
//
//            }
//        });
        startActivity(intent);

    }

//    private void getUser() {
//        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot doc = task.getResult();
//                    if(doc.exists()){
//                        user_u = doc.toObject(UserModel.class);
//
//
//                    }
//                }
//
//            }
//        });
//        Log.d("loggedinuser", user_u.getUsername());
//
//    }

    private boolean verifyEmailPassword(){
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        if(TextUtils.isEmpty(email.getText().toString())){
            email.setError("Required");
            return false;
        }
        if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("Required");
            return false;
        }

        return true;
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
