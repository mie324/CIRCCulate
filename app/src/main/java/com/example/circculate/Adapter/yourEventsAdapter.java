package com.example.circculate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circculate.DetailPage;
import com.example.circculate.Helper;
import com.example.circculate.Model.EventModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;

public class yourEventsAdapter extends RecyclerView.Adapter<yourEventsAdapter.EventViewHolder> {
    private Context ctx;
    private List<EventModel> eventList;
    private FirebaseFirestore db;
    private UserModel currentUser;
    private FirebaseAuth mAuth;

    public yourEventsAdapter(Context ctx, List<EventModel> eventList, UserModel currentUser) {
        this.eventList = eventList;
        mAuth = FirebaseAuth.getInstance();
        this.ctx = ctx;
        this.db = FirebaseFirestore.getInstance();
        this.currentUser = currentUser;

    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView monthText, dayText, eventTitle, timeText, personName, detailText;
        Switch signupSW;

        public EventViewHolder(View itemView) {
            super(itemView);
//            calenderIcon = itemView.findViewById(R.id.calender_icon);
            monthText = itemView.findViewById(R.id.Month_text);
            dayText = itemView.findViewById(R.id.Day_text);
            eventTitle = itemView.findViewById(R.id.Event_title);
            timeText = itemView.findViewById(R.id.Time_text);
            personName = itemView.findViewById(R.id.Person_name);
            detailText = itemView.findViewById(R.id.Detail_text);
            signupSW = itemView.findViewById(R.id.signup_switch);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_all_events,parent,false);
        EventViewHolder eventViewHolder = new EventViewHolder(itemView);
        return eventViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder holder, int position) {
        final EventModel newEvent = eventList.get(position);
        String hour = newEvent.getTimestamp().substring(8, 10);
        String minute = newEvent.getTimestamp().substring(10);
        String day = newEvent.getTimestamp().substring(6, 8);
        String month = newEvent.getTimestamp().substring(4, 6);
        String mon = Helper.transformMon(month);
        holder.eventTitle.setText(newEvent.getTitle());
        holder.timeText.setText(hour + ":" + minute);
//        String signedName = newEvent.getUserName();
//        if(signedName == null){
//            holder.personName.setText("No one signed up yet.");
//        }else{
//            holder.personName.setText(signedName);
//        }
        holder.personName.setText(currentUser.getUsername());

        holder.dayText.setText(day);
        holder.monthText.setText(mon);
        holder.detailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, DetailPage.class);
                intent.putExtra("clickedEvent",newEvent);
                ctx.startActivity(intent);
            }
        });

        if(currentUser.getUsername().equals(newEvent.getUserName())){
            holder.signupSW.setChecked(true);
        }

        holder.signupSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(!isChecked){
                    //user cancel the sign up
                    newEvent.setUserName(null);
                    newEvent.setUserId(null);
                    db.collection("events").document(newEvent.getTimestamp())
                            .set(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                eventList.remove(newEvent);
                                notifyDataSetChanged();

                                Toast.makeText(ctx, "You have cancel the sign up.", Toast.LENGTH_SHORT).show();
                                holder.personName.setText("No one signed up yet.");
                            }else {
                                Toast.makeText(ctx, "Fail to cancel the sign up.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


//                if(newEvent.getUserId() != null){
//                    //someone sign up for the event.
//                    if(isChecked){
//                        compoundButton.setChecked(false);
//                        Toast.makeText(ctx, "Someone else has already sign up.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }else {
//                        //uncheck
//                        if(newEvent.getUserName().equals(currentUser.getUsername())){
//                            //cancel the sign up.
//                            newEvent.setUserName(null);
//                            newEvent.setUserId(null);
//                            db.collection("events").document(newEvent.getTimestamp())
//                                    .set(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()){
//                                        eventList.remove(newEvent);
//                                        notifyDataSetChanged();
//                                        Toast.makeText(ctx, "You have cancel the sign up.", Toast.LENGTH_SHORT).show();
//                                        holder.personName.setText("No one signed up yet.");
//                                    }else {
//                                        Toast.makeText(ctx, "Fail to cancel the sign up.", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }
//                    }
//                }
            }
        });
//        holder.locText.setText(newEvent.getUserId().get);


    }



    @Override
    public int getItemCount() {
        return eventList.size();
    }


}
