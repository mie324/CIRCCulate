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
import com.example.circculate.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class yourEventsAdapter extends RecyclerView.Adapter<yourEventsAdapter.EventsViewHolder> {
    private Context ctx;
    private List<EventModel> eventList;
    private FirebaseFirestore db;
    @NonNull
    @Override
    public yourEventsAdapter.EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_your_events,parent,false);
        EventsViewHolder eventViewHolder = new EventsViewHolder(itemView);
        return eventViewHolder;
    }

    public yourEventsAdapter(Context ctx, List<EventModel> eventList) {
        this.ctx = ctx;
        this.eventList = eventList;
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onBindViewHolder(@NonNull yourEventsAdapter.EventsViewHolder holder, int position) {
        EventModel newEvent = eventList.get(position);
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
                gotoDetailPage();
            }
        });

    }

    private void gotoDetailPage() {
        Intent intent = new Intent(ctx, DetailPage.class);
        ctx.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventsViewHolder extends RecyclerView.ViewHolder {
        TextView monthText, dayText, eventTitle, timeText, personName, detailText;
        public EventsViewHolder(View itemView) {
            super(itemView);
            monthText = itemView.findViewById(R.id.Month_text1);
            dayText = itemView.findViewById(R.id.Day_text1);
            eventTitle = itemView.findViewById(R.id.Event_title1);
            timeText = itemView.findViewById(R.id.Time_text1);
            personName = itemView.findViewById(R.id.Person_name1);
            detailText = itemView.findViewById(R.id.Detail_text1);
        }
    }
}
