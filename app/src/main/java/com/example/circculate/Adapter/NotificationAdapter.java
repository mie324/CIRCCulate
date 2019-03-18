package com.example.circculate.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.circculate.Model.NotificationModel;
import com.example.circculate.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private Context ctx;
    private List<NotificationModel> notifications;

    public NotificationAdapter(Context ctx, List<NotificationModel> notifications){
        this.ctx = ctx;
        this.notifications = notifications;
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder{
        public TextView notifTitle, notifBody;

        public NotificationViewHolder(View itemView){
            super(itemView);
            notifTitle = itemView.findViewById(R.id.title);
            notifBody = itemView.findViewById(R.id.body);

        }
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);

        NotificationViewHolder notifViewHolder = new NotificationViewHolder(itemView);
        return notifViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel notification = notifications.get(position);
        String notifTitle = notification.getNotiTitle();
        String notifBody = notification.getNotiBody();

        holder.notifTitle.setText(notifTitle);
        holder.notifBody.setText(notifBody);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }


}
