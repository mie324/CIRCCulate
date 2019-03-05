package com.example.circculate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class yourEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context ctx;
    private List<EventModel> eventList;
    private FirebaseFirestore db;
    private UserModel currentUser;
    private FirebaseAuth mAuth;
    private static int EVENT_TYPE = 0, NO_ITEM_TYPE = 1;

    public yourEventsAdapter(Context ctx, List<EventModel> eventList, UserModel currentUser) {
        this.eventList = eventList;
        mAuth = FirebaseAuth.getInstance();
        this.ctx = ctx;
        this.db = FirebaseFirestore.getInstance();
        this.currentUser = currentUser;

    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView monthText, dayText, eventTitle, timeText, personName, detailText;
        public Switch signupSW;
        public ImageView calendarBG;

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
            calendarBG = itemView.findViewById(R.id.calender_icon);
        }
    }

    public static class NoItemViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout noItemViewLayout;

        public NoItemViewHolder(View layoutView){
            super(layoutView);
            noItemViewLayout = layoutView.findViewById(R.id.no_event_layout);

        }
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if(viewType == EVENT_TYPE){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_event,parent,false);
            viewHolder = new EventViewHolder(itemView);
        }else {
            View noItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_no_item_layout, parent, false);
            viewHolder = new NoItemViewHolder(noItemView);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        final EventModel newEvent = eventList.get(position);
        if(holder instanceof EventViewHolder){

            String hour = newEvent.getTimestamp().substring(8, 10);
            String minute = newEvent.getTimestamp().substring(10);
            String day = newEvent.getTimestamp().substring(6, 8);
            String month = newEvent.getTimestamp().substring(4, 6);
            String mon = Helper.transformMon(month);
            ((EventViewHolder) holder).eventTitle.setText(newEvent.getTitle());
            ((EventViewHolder) holder).timeText.setText(hour + ":" + minute);
//        String signedName = newEvent.getUserName();
//        if(signedName == null){
//            holder.personName.setText("No one signed up yet.");
//        }else{
//            holder.personName.setText(signedName);
//        }
            ((EventViewHolder) holder).personName.setText(currentUser.getUsername());
            ((EventViewHolder) holder).calendarBG.setBackgroundColor(currentUser.getColorCode());
            ((EventViewHolder) holder).dayText.setText(day);
            ((EventViewHolder) holder).monthText.setText(mon);

            ((EventViewHolder) holder).detailText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, DetailPage.class);
                    intent.putExtra("clickedEvent",newEvent);
                    ctx.startActivity(intent);
                }
            });

            if(currentUser.getUsername().equals(newEvent.getUserName())){
                ((EventViewHolder) holder).signupSW.setChecked(true);
            }

            ((EventViewHolder) holder).signupSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if(!isChecked){
                        //user cancel the sign up
                        newEvent.setUserName(null);
                        newEvent.setUserId(null);
                        newEvent.setUserColorCode(0);
                        db.collection("events").document(newEvent.getTimestamp())
                                .set(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    eventList.remove(newEvent);
                                    notifyDataSetChanged();

                                    Toast.makeText(ctx, "You have cancel the sign up.", Toast.LENGTH_SHORT).show();
//                                    ((EventViewHolder) holder).personName.setText("No one signed up yet.");
                                }else {
                                    Toast.makeText(ctx, "Fail to cancel the sign up.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            });
        } else {
            // no item layout
            if(eventList.size() == 1){
                ((NoItemViewHolder) holder).noItemViewLayout.setVisibility(View.VISIBLE);
            }else {
                ((NoItemViewHolder) holder).noItemViewLayout.setVisibility(View.INVISIBLE);
            }

        }
//        holder.locText.setText(newEvent.getUserId().get);


    }



    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return this.eventList.get(position).getTimestamp().equals("NoItem") ? NO_ITEM_TYPE: EVENT_TYPE;
    }
}
