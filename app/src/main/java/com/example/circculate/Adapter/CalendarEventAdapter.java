package com.example.circculate.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CalendarEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<EventModel> events;
    private Context context;
    private AdapterView.OnItemClickListener itemClickListener;
    private static final String TAG = "adapter";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private UserModel user;
    private String username;

    public interface OnItemClickListener {
        void onItemClick(View view, EventModel event, int position);
    }

    public CalendarEventAdapter(Context context, List<EventModel> events, UserModel user){
        this.events = events;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.username = null;
        this.user = user;
        if(user == null){
            Log.d(TAG, "CalendarEventAdapter: user null");
        }else {
            Log.d(TAG, "CalendarEventAdapter: user not null");
        }

    }

    public class EventViewHolder extends RecyclerView.ViewHolder{
        public TextView appointmentTitle;
        public TextView appointmentLocation;
        public TextView appointmentPerson;
        public Button detailButton;
        public Button signupButton;
        public LinearLayout itemLayout;

        public EventViewHolder(View view){
            super(view);
            appointmentTitle = view.findViewById(R.id.appointment_title);
            appointmentLocation = view.findViewById(R.id.appointment_location);
            appointmentPerson = view.findViewById(R.id.appointment_people);
            detailButton = view.findViewById(R.id.detail_button);
            signupButton = view.findViewById(R.id.signup_button);
            itemLayout = view.findViewById(R.id.item_layout);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_event, parent, false);
        viewHolder = new EventViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final EventModel event = events.get(position);
        final EventViewHolder eventViewHolder = (EventViewHolder) holder;
        eventViewHolder.appointmentPerson.setText(event.getUserId() == null ?
                "No one sign up for this event" : "Person:" + event.getUserName());
        eventViewHolder.appointmentTitle.setText(Helper.transformAppointTitle(event.getTitle(),
                event.getTimestamp()));
        eventViewHolder.appointmentLocation.setText(Helper.tranformAppointLoca(event.getLocation()));
        eventViewHolder.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(context, DetailPage.class);
                detailIntent.putExtra("clickedEvent", event);
                detailIntent.putExtra("loggedUser", user);
                context.startActivity(detailIntent);
            }
        });

        //change the color theme if no one sign up

        if(event.getUserId() == null){
            eventViewHolder.itemLayout.setBackgroundColor(context.getColor(R.color.red_900));//red_900
            eventViewHolder.appointmentTitle.setTextAppearance(R.style.TextAppearance_Headline_RedBG);
            eventViewHolder.appointmentLocation.setTextAppearance(R.style.TextAppearance_Subhead_RedBG);
            eventViewHolder.appointmentPerson.setTextAppearance(R.style.TextAppearance_Subhead_RedBG);
            eventViewHolder.detailButton.setTextColor(context.getColor(R.color.grey_3));

        }

        eventViewHolder.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //implement later
                if(event.getUserId() != null){
                    if(event.getUserId().equals(mAuth.getUid())){
                        Toast.makeText(context, "You have already signed up.", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "Someone else has already signed up.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    event.setUserId(mAuth.getUid());
//                    String username = getUserName();
                    event.setUserName(user.getUsername());
                    db.collection("events").document(event.getTimestamp())
                            .set(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                eventViewHolder.appointmentPerson.setText(user.getUsername());
                                //change the style of view holder
                                eventViewHolder.itemLayout.setBackgroundColor(context.getColor(R.color.grey_3));
                                eventViewHolder.appointmentTitle.setTextAppearance(R.style.TextAppearance_AppCompat_Headline);
                                eventViewHolder.appointmentLocation.setTextAppearance(R.style.TextAppearance_AppCompat_Subhead);
                                eventViewHolder.appointmentPerson.setTextAppearance(R.style.TextAppearance_AppCompat_Subhead);
                                eventViewHolder.detailButton.setTextColor(context.getColor(R.color.colorAccent));

                                //end of change style

                                Toast.makeText(context, "You have signed for the event.",
                                        Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context, "Fail to sign up for the event.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
//                    eventViewHolder.appointmentPerson.setText(user.getUsername());
                }
                Log.d(TAG, "onClick: sign up.");
            }
        });
    }



    @Override
    public int getItemCount() {
        return events.size();
    }
}
