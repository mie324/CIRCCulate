package com.example.circculate.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.circculate.Model.CarePreferenceAnswerModel;
import com.example.circculate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CarePreferenceAdapter extends RecyclerView.Adapter<CarePreferenceAdapter.PreferenceViewHolder> {
    private Context ctx;
    private List<String> answers;
    private FirebaseAuth mAuth;
    private String[] titleArray = {"What do I need to think about or do before I feel ready to have the conversation?",
            "What makes my life meaningful?",
            "What do I value most?",
            "What are the three most important things that I want my SDM, family, friends and/or health care providers to understand about my future personal or health care wishes?",
            "What concerns do I have about how my health may change in the future?",
            "Other thoughts:"};
    private String[] desArray = {
            "",
            "e.g. time with family or friends, faith, love for garden, music, art, work, hobbies, pet",
            "e.g. live independently, make my own decisions, enjoy a good meal, have my privacy upheld, recognize or talk with others",
            "",
            "",
            ""
    };
    private String[] constructArray;
    private static final String PATIENT_ID = "rUSzZ8NTnthxkk36ItXBRGWRkRr2";
    private FirebaseFirestore db;
    private String username;

    public CarePreferenceAdapter(Context ctx, List<String> answers, String username){
        this.ctx = ctx;
        this.answers = answers;
        this.username = username;
        db = FirebaseFirestore.getInstance();
        constructArray = new String[answers.size()];
        mAuth = FirebaseAuth.getInstance();
        for(int i = 0; i < answers.size(); i++){
            constructArray[i] = answers.get(i);
        }
    }

    @NonNull
    @Override
    public CarePreferenceAdapter.PreferenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_acp, parent, false);
        PreferenceViewHolder preferenceViewHolder = new PreferenceViewHolder(itemView);
        return preferenceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CarePreferenceAdapter.PreferenceViewHolder holder, final int position) {
        final String userAnswer = answers.get(position);
        holder.title.setText(titleArray[position]);
        holder.description.setText(desArray[position]);
        holder.patientAnswer.setText(userAnswer);

        holder.switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getUid().equals(PATIENT_ID)){
                    // only patient can edit answers
                    if(!holder.isHolderEditable){
                        holder.isHolderEditable = true;
                        holder.switcher.showNext();
                        holder.editText.setText(userAnswer);
                    }
                }else {
                    Toast.makeText(ctx, "Only the patient can edit answers", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if not editable, then the button will no show
                if(holder.isHolderEditable){
                    final String newAnswer = holder.editText.getText().toString();
                    constructArray[position] = newAnswer;
                    CarePreferenceAnswerModel newPreference = new CarePreferenceAnswerModel(mAuth.getUid(),
                            username, constructArray);
                    db.collection("carepreferences").document(newPreference.getUserId())
                            .set(newPreference).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                answers.set(position, newAnswer);
                                notifyDataSetChanged();
                                holder.switcher.showPrevious();
                                holder.isHolderEditable = false;
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }


    public class PreferenceViewHolder extends RecyclerView.ViewHolder{
        public TextView title, description, patientAnswer;
        public ViewSwitcher switcher;
        public AppCompatEditText editText;
        public AppCompatButton submitBtn;
        public boolean isHolderEditable = false;

        public PreferenceViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            patientAnswer = itemView.findViewById(R.id.answer_textview);
            switcher = itemView.findViewById(R.id.switcher);
            editText = itemView.findViewById(R.id.answer_edittext);
            submitBtn = itemView.findViewById(R.id.answer_submit_btn);
        }

    }
}
