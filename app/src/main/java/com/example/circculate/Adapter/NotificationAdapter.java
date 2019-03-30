package com.example.circculate.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.circculate.Model.DbNotificationModel;
import com.example.circculate.Model.NotificationModel;
import com.example.circculate.Model.TimelineItemModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.R;
import com.example.circculate.utils.SwipeItemTouchHelper;
import com.example.circculate.utils.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> implements SwipeItemTouchHelper.SwipeHelperAdapter {
    private Context ctx;
    private List<String> notifications;
    private List<String> notificationSwiped = new ArrayList<>();
    private List<Boolean> swipeMark = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private UserModel currentUser;
    private FirebaseAuth mAuth;
    private static final String TAG = "NotificationAdapter";
    private static final int ONE_MB = 1024*1024;

    public NotificationAdapter(Context ctx, List<String> notifications, UserModel currentUser){
        this.ctx = ctx;
        this.notifications = notifications;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        this.currentUser = currentUser;
        mAuth = FirebaseAuth.getInstance();
        for(int i = 0; i < notifications.size(); i++){
            swipeMark.add(false);
        }
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder implements SwipeItemTouchHelper.TouchViewHolder {
        public TextView notifTitle, notifBody;
        public CircularImageView userIcon;
        public Button undoButton;
        public View layoutParent;

        public NotificationViewHolder(View itemView){
            super(itemView);
            notifTitle = itemView.findViewById(R.id.title);
            notifBody = itemView.findViewById(R.id.body);
            userIcon = itemView.findViewById(R.id.user_icon);
            undoButton = itemView.findViewById(R.id.bt_undo);
            layoutParent = itemView.findViewById(R.id.layout_parent);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(ctx.getResources().getColor(R.color.grey_5));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
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
    public void onBindViewHolder(@NonNull final NotificationViewHolder holder, final int position) {
        db.collection("notifications").document(notifications.get(position))
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DbNotificationModel newNotice = task.getResult().toObject(DbNotificationModel.class);

                    holder.notifTitle.setText(newNotice.getUserName());
                    holder.notifBody.setText(newNotice.getContent());
                    StorageReference iconRef = storage.getReference().child(newNotice.getUserIconRef());
                    iconRef.getBytes(ONE_MB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                        @Override
                        public void onComplete(@NonNull Task<byte[]> task) {
                            if(task.isSuccessful()){
                                Bitmap userIcon = BitmapFactory.decodeByteArray(task.getResult(),
                                        0, task.getResult().length);
                                holder.userIcon.setImageBitmap(userIcon);

                            }
                        }
                    });
                }
            }
        });

        holder.undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeMark.set(position, false);
                notificationSwiped.remove(notifications.get(position));
                notifyItemChanged(position);
            }
        });

        Log.d(TAG, "onBindViewHolder: bind holder at position" + position);
        Log.d(TAG, "onBindViewHolder: " + notifications.size());
        Log.d(TAG, "onBindViewHolder: " + swipeMark.size());
        if(swipeMark.get(position)){
            holder.layoutParent.setVisibility(View.GONE);
        }else {
            holder.layoutParent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                for(String notificationId : notificationSwiped){
                    final int indexRemoved = notifications.indexOf(notificationId);
                    if(indexRemoved != -1){
                        notifications.remove(indexRemoved);
                        swipeMark.remove(indexRemoved);
                        currentUser.setListOfNotification((ArrayList<String>) notifications);
                        db.collection("users").document(mAuth.getUid())
                                .set(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    notifyItemRemoved(indexRemoved);
                                }
                            }
                        });
                    }
                }

                notificationSwiped.clear();

                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public void onItemDismiss(final int position) {
        if(swipeMark.get(position)){
            notificationSwiped.remove(notifications.get(position));
            notifications.remove(position);
            swipeMark.remove(position);
            currentUser.setListOfNotification((ArrayList<String>) notifications);
            db.collection("users").document(mAuth.getUid())
                    .set(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        notifyItemRemoved(position);
                    }
                }
            });
            return;
        }
        Log.d(TAG, "onItemDismiss: swipe at " + position);
        swipeMark.set(position, true);
        notificationSwiped.add(notifications.get(position));
        notifyItemChanged(position);
    }
}
