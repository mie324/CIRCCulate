package com.example.circculate;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circculate.Model.UserModel;
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
    private ProgressDialog progressDialog;
    private TextInputEditText pickerColorResult;
    private int colorCode;
    private int currentRed = 127, currentGreen = 127, currentBlue = 118;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        toHomePage(mAuth.getCurrentUser());
        pickerColorResult = findViewById(R.id.pick_color_text);
        initPicker();
        addButtonLisner();
    }

    private void toHomePage(FirebaseUser currentUser){
        if(currentUser != null){
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);
        }
    }

    private void initPicker(){
        ((Button)findViewById(R.id.color_picker)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPickerDialog();
            }
        });

        pickerColorResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPickerDialog();
            }
        });
    }

    private void openPickerDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_color_picker);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final View viewResult = (View) dialog.findViewById(R.id.view_result);
        final AppCompatSeekBar seekbarRed = (AppCompatSeekBar) dialog.findViewById(R.id.seekbar_red);
        final AppCompatSeekBar seekbarGreen = (AppCompatSeekBar) dialog.findViewById(R.id.seekbar_green);
        final AppCompatSeekBar seekbarBlue = (AppCompatSeekBar) dialog.findViewById(R.id.seekbar_blue);

        final TextView tvRed = (TextView) dialog.findViewById(R.id.tv_red);
        final TextView tvGreen = (TextView) dialog.findViewById(R.id.tv_green);
        final TextView tvBlue = (TextView) dialog.findViewById(R.id.tv_blue);

        tvRed.setText(currentRed + "");
        tvGreen.setText(currentGreen + "");
        tvBlue.setText(currentBlue + "");

        seekbarRed.setProgress(currentRed);
        seekbarGreen.setProgress(currentGreen);
        seekbarBlue.setProgress(currentBlue);

        viewResult.setBackgroundColor(Color.rgb(currentRed, currentGreen, currentBlue));

        seekbarRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRed.setText(progress + "");
                viewResult.setBackgroundColor(Color.rgb(seekbarRed.getProgress(), seekbarGreen.getProgress(), seekbarBlue.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbarGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvGreen.setText(progress + "");
                viewResult.setBackgroundColor(Color.rgb(seekbarRed.getProgress(), seekbarGreen.getProgress(), seekbarBlue.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbarBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvBlue.setText(progress + "");
                viewResult.setBackgroundColor(Color.rgb(seekbarRed.getProgress(), seekbarGreen.getProgress(), seekbarBlue.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ((Button) dialog.findViewById(R.id.bt_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                currentRed = seekbarRed.getProgress();
                currentGreen = seekbarGreen.getProgress();
                currentBlue = seekbarBlue.getProgress();

//                pickerColorResult.setText("RGB(" + currentRed + ", " + currentGreen + ", " + currentBlue + ")");
                pickerColorResult.setText(Integer.toString(Color.rgb(currentRed, currentGreen, currentBlue)));
                pickerColorResult.setBackgroundColor(Color.rgb(currentRed, currentGreen, currentBlue));
                pickerColorResult.setTextColor(getResources().getColor(R.color.grey_3));

            }
        });
        ((Button) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void addButtonLisner(){
        Button signupButton = findViewById(R.id.signup_bt);
        progressDialog = new ProgressDialog(SignUp.this);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyInputs()){
                    progressDialog.setMessage("signing up...");
                    progressDialog.show();

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
        progressDialog.dismiss();
    }

    private void addUserToDB(final FirebaseUser currentUser){
        if(currentUser == null){
            return;
        }

        UserModel newUser = new UserModel(emailText, usernameText, colorCode);
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

        } else{
            passwordText = password.getText().toString();
        }

        //missing password double check.

        if(TextUtils.isEmpty(username.getText().toString())){
            username.setError("Required");
            return false;
        }else{
            usernameText = username.getText().toString();

        }

        if(TextUtils.isEmpty(pickerColorResult.getText().toString())){
            pickerColorResult.setError("Required.");
            return false;
        }else {
            colorCode = Integer.parseInt(pickerColorResult.getText().toString());
        }

        return true;
    }

    public void toSignIn(View view) {
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
