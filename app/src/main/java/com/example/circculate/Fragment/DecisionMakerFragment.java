package com.example.circculate.Fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.circculate.HomePage;
import com.example.circculate.Model.ChoicemakerModel;
import com.example.circculate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 */
public class DecisionMakerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "DecisionMaker";
    private TextView first_name, last_name, relation_ship, phone_number, address_1, address_2, city_box, province_box, zip_code, location_box, email_address;
    private Button edit_mode;
    private RadioGroup radio_group;
    private RadioButton radio_pirates;
    private RadioButton radio_ninjas;
    private String isAppointed;
    private String username;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;
    private Dialog dialog;
    private ViewSwitcher viewSwitcher;

    private EditText first_name_e, last_name_e, relation_ship_e, phone_number_e, address_1_e, address_2_e, city_box_e, province_box_e, zip_code_e, location_box_e, email_address_e;
    private RadioGroup radio_group_e;
    private RadioButton radio_pirates_e;
    private RadioButton radio_ninjas_e;
    private boolean isText = true;

    public DecisionMakerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRefresh() {
        displayDocument();
        Log.d(TAG, "onRefresh: ");
    }

    private void displayDocument() {
        db.collection("choicemakers").document("rUSzZ8NTnthxkk36ItXBRGWRkRr2").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        ChoicemakerModel newChoice = documentSnapshot.toObject(ChoicemakerModel.class);
                        first_name.setText(newChoice.getFirstName());
                        last_name.setText(newChoice.getLastName());
                        relation_ship.setText(newChoice.getRelationShip());
                        phone_number.setText(newChoice.getPhoneNumber());
                        address_1.setText(newChoice.getAddress1());
                        if(newChoice.getAddress2()!=null){
                            address_2.setText(newChoice.getAddress2());
                        }
                        city_box.setText(newChoice.getCity());
                        province_box.setText(newChoice.getProvince());
                        zip_code.setText(newChoice.getZipCode());
                        location_box.setText(newChoice.getLocation());
                        email_address.setText(newChoice.getEmailAddress());
                        if(newChoice.getIsAppointed().equals("Yes")){
                            radio_pirates.setChecked(true);

                        }else{
                            radio_ninjas.setChecked(true);

                        }
                    }else{

                    }

                }else{

                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_decision_maker, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        first_name = root.findViewById(R.id.first_name);
        last_name = root.findViewById(R.id.last_name);
        relation_ship = root.findViewById(R.id.relation_ship);
        phone_number = root.findViewById(R.id.phone_number);
        address_1 = root.findViewById(R.id.address_1);
        address_2 = root.findViewById(R.id.address_2);
        city_box = root.findViewById(R.id.city_box);
        province_box = root.findViewById(R.id.province_box);
        zip_code = root.findViewById(R.id.zip_code);
        location_box = root.findViewById(R.id.location_box);
        email_address = root.findViewById(R.id.email_address);
        radio_group = root.findViewById(R.id.radio_group);
        radio_ninjas = root.findViewById(R.id.radio_ninjas);
        radio_pirates = root.findViewById(R.id.radio_pirates);

        first_name_e = root.findViewById(R.id.first_name_e);
        last_name_e = root.findViewById(R.id.last_name_e);
        relation_ship_e = root.findViewById(R.id.relation_ship_e);
        phone_number_e = root.findViewById(R.id.phone_number_e);
        address_1_e = root.findViewById(R.id.address_1_e);
        address_2_e = root.findViewById(R.id.address_2_e);
        city_box_e = root.findViewById(R.id.city_box_e);
        province_box_e = root.findViewById(R.id.province_box_e);
        zip_code_e = root.findViewById(R.id.zip_code_e);
        location_box_e = root.findViewById(R.id.location_box_e);
        email_address_e = root.findViewById(R.id.email_address_e);
        radio_group_e = root.findViewById(R.id.radio_group_e);
        radio_ninjas_e = root.findViewById(R.id.radio_ninjas_e);
        radio_pirates_e = root.findViewById(R.id.radio_pirates_e);
        edit_mode = root.findViewById(R.id.edit_mode);
        viewSwitcher = root.findViewById(R.id.view_switch);
        displayDocument();
        edit_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isText){
                    if(!mAuth.getUid().equals("yNzxH6iEWrObxudQonPfqlES7Ze2")){
                        dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.dialog_no_rights);
                        dialog.setCancelable(true);
                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                        layoutParams.copyFrom(dialog.getWindow().getAttributes());
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                        dialog.show();

                    }else{
                        isText = false;
                        edit_mode.setText("Save");
                        switchMode();
                    }

                }else{
                    isText = true;
                    viewSwitcher.showNext();
                    edit_mode.setText("Edit Mode");
                    ChoicemakerModel newChoice;
                    int selectedId = radio_group_e.getCheckedRadioButtonId();
                    if(selectedId == radio_ninjas_e.getId()){
                        isAppointed = "No";

                    }else{
                        isAppointed = "Yes";

                    }
                    if(address_2_e.getText().toString().trim().length()==0){
                        newChoice= new ChoicemakerModel("Han", "rUSzZ8NTnthxkk36ItXBRGWRkRr2", first_name_e.getText().toString(), last_name_e.getText().toString(), relation_ship_e.getText().toString(), phone_number_e.getText().toString(),
                                address_1_e.getText().toString(), city_box_e.getText().toString(), province_box_e.getText().toString(), zip_code_e.getText().toString(),location_box_e.getText().toString(), email_address_e.getText().toString(),
                                isAppointed);


                    }else{
                        String address2= address_2_e.getText().toString();
                        newChoice= new ChoicemakerModel("Han", "rUSzZ8NTnthxkk36ItXBRGWRkRr2", first_name_e.getText().toString(), last_name_e.getText().toString(), relation_ship_e.getText().toString(), phone_number_e.getText().toString(),
                                address_1_e.getText().toString(), address2, city_box_e.getText().toString(), province_box_e.getText().toString(), zip_code_e.getText().toString(),location_box_e.getText().toString(), email_address_e.getText().toString(),
                                isAppointed);

                    }
                    Log.d("test", first_name_e.getText().toString());
                    db.collection("choicemakers").document("rUSzZ8NTnthxkk36ItXBRGWRkRr2").set(newChoice).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                displayDocument();

                            }else{

                            }
                        }
                    });


                }

            }
        });
        return root;
    }

    private void switchMode() {
        first_name_e.setText(first_name.getText().toString());
        last_name_e.setText(last_name.getText().toString());
        relation_ship_e.setText(relation_ship.getText().toString());
        phone_number_e.setText(phone_number.getText().toString());
        address_1_e.setText(address_1.getText().toString());
        if(address_2.getText()!=null){
            address_2_e.setText(address_2.getText().toString());
        }
        city_box_e.setText(city_box.getText().toString());
        province_box_e.setText(province_box.getText().toString());
        zip_code_e.setText(zip_code.getText().toString());
        location_box_e.setText(location_box.getText().toString());
        email_address_e.setText(email_address.getText().toString());
        if(radio_pirates.isChecked()){
            radio_pirates_e.setChecked(true);

        }else{
            radio_ninjas_e.setChecked(true);

        }
        viewSwitcher.showNext();
    }

}
