package com.example.circculate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.circculate.DetailPage;
import com.example.circculate.Helper;
import com.example.circculate.Model.EventModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.List;

public class AlleventsAdapter extends RecyclerView.Adapter<AlleventsAdapter.EventViewHolder> {
    private Context ctx;
    private List<EventModel> eventList;
    private FirebaseFirestore db;

    public AlleventsAdapter(Context ctx, List<EventModel> eventList) {
        this.eventList = eventList;
        this.ctx = ctx;
        this.db = FirebaseFirestore.getInstance();

    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        TextView monthText, dayText, eventTitle, timeText, personName, detailText;

        public EventViewHolder(View itemView) {
            super(itemView);
//            calenderIcon = itemView.findViewById(R.id.calender_icon);
            monthText = itemView.findViewById(R.id.Month_text);
            dayText = itemView.findViewById(R.id.Day_text);
            eventTitle = itemView.findViewById(R.id.Event_title);
            timeText = itemView.findViewById(R.id.Time_text);
            personName = itemView.findViewById(R.id.Person_name);
            detailText = itemView.findViewById(R.id.Detail_text);
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
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        final EventModel newEvent = eventList.get(position);
        String hour = Helper.getTime(newEvent.getTimestamp()).substring(0,2);
        String minute = Helper.getTime(newEvent.getTimestamp()).substring(2,4);
        String day = Helper.getTimedate(newEvent.getTimestamp()).substring(6,8);
        String month = Helper.getTimedate(newEvent.getTimestamp()).substring(4,6);
        String mon = Helper.transformMon(month);
        holder.eventTitle.setText(newEvent.getTitle());
        holder.timeText.setText(hour+":"+minute);
        String signedName = newEvent.getUserName();
        if(signedName == null){
            holder.personName.setText("No one signed up yet.");
        }else{
            holder.personName.setText(signedName);
        }

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

//        holder.locText.setText(newEvent.getUserId().get);


    }


//    private String getUsername(EventModel newEvent) {
//        final UserModel queryUser;
//        final DocumentReference docRef = db.collection("users").document(newEvent.getUserId());
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                DocumentSnapshot doc =  task.getResult();
//                if(doc.exists()){
//                    queryUser = doc.toObject(UserModel.class);
//
//                }
//
//            }
//        });
//        return queryUser.getUsername();
//    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }


}