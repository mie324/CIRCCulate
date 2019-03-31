package com.example.circculate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circculate.Model.ChoicemakerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChoiceMaker extends AppCompatActivity {
    private EditText first_name, last_name, relation_ship, phone_number, address_1, address_2, city_box, province_box, zip_code, location_box, email_address;
    private Button bt_submit;
    private RadioGroup radio_group;
    private RadioButton radio_pirates;
    private RadioButton radio_ninjas;
    private String isAppointed;
    private String username;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_maker);
        username = getIntent().getStringExtra("username");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        relation_ship = findViewById(R.id.relation_ship);
        phone_number = findViewById(R.id.phone_number);
        address_1 = findViewById(R.id.address_1);
        address_2 = findViewById(R.id.address_2);
        city_box = findViewById(R.id.city_box);
        province_box = findViewById(R.id.province_box);
        zip_code = findViewById(R.id.zip_code);
        location_box = findViewById(R.id.location_box);
        email_address = findViewById(R.id.email_address);
        bt_submit = findViewById(R.id.bt_submit);
        radio_group = findViewById(R.id.radio_group);
        radio_ninjas = findViewById(R.id.radio_ninjas);
        radio_pirates = findViewById(R.id.radio_pirates);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyInput() == false)
                    return;
                addChoiceToDb();


            }
        });
    }

    private void addChoiceToDb() {
        userId = mAuth.getUid();
        ChoicemakerModel newChoice;
        if(address_2.getText().toString().trim().length()==0){
            newChoice= new ChoicemakerModel(username, userId, first_name.getText().toString(), last_name.getText().toString(), relation_ship.getText().toString(), phone_number.getText().toString(),
                    address_1.getText().toString(), city_box.getText().toString(), province_box.getText().toString(), zip_code.getText().toString(),location_box.getText().toString(), email_address.getText().toString(),
                    isAppointed);


        }else{
            String address2= address_2.getText().toString();
            newChoice= new ChoicemakerModel(username, userId, first_name.getText().toString(), last_name.getText().toString(), relation_ship.getText().toString(), phone_number.getText().toString(),
                    address_1.getText().toString(), address2, city_box.getText().toString(), province_box.getText().toString(), zip_code.getText().toString(),location_box.getText().toString(), email_address.getText().toString(),
                    isAppointed);

        }
        db.collection("choicemakers").document(userId).set(newChoice).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(intent);

                }else{

                }
            }
        });


    }

    private boolean verifyInput() {
        boolean flag = true;
        List<EditText> mustList = Arrays.asList(first_name, last_name, relation_ship, phone_number, address_1, city_box, province_box, zip_code, location_box, email_address);
        for(int i=0;i<mustList.size();i++){
            if(mustList.get(i).getText().toString().trim().length()==0){
//                displayToast("You must fill out all information.");
                mustList.get(i).setError("This section cannot be empty.");
                flag=false;
                return flag;
            }
        }
        if(radio_group.getCheckedRadioButtonId() == -1){
            flag = false;
            displayToast("You should choose either yes or no.");
            return flag;
        }else{
            int selectedId = radio_group.getCheckedRadioButtonId();
            if(selectedId == radio_ninjas.getId()){
                isAppointed = "No";

            }else{
                isAppointed = "Yes";

            }
        }
        return flag;



    }

    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();;
    }


}
