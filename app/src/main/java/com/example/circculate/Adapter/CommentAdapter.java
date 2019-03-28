package com.example.circculate.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.circculate.Model.CommentModel;
import com.example.circculate.Model.EventModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.R;
import com.example.circculate.utils.Helper;
import com.example.circculate.utils.SwipeItemTouchHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.rpc.Help;
import com.mikhaellopez.circularimageview.CircularImageView;
import android.util.Log;

import java.util.List;
import android.content.Intent;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context ctx;
    private List<CommentModel> commentList;
    private FirebaseFirestore db;
    private UserModel currentUser;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    public CommentAdapter(){}

    public CommentAdapter(Context ctx, List<CommentModel> commentList){
        this.ctx = ctx;
        this.commentList = commentList;
        this.db = FirebaseFirestore.getInstance();
        this.storageReference = FirebaseStorage.getInstance().getReference();

    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_comment, parent,false);
        CommentAdapter.CommentViewHolder commentViewHolder = new CommentAdapter.CommentViewHolder(itemView);

        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.CommentViewHolder holder, final int position) {
        final CommentModel thisComment = commentList.get(position);
        //set up user icon
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storageReference.child(thisComment.getUserPhoto());
        imageRef.getBytes(ONE_MEGABYTE*2).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()){
                    if(task.getResult()!=null){
                        Bitmap bitmap = BitmapFactory.decodeByteArray(task.getResult(),0, task.getResult().length);
                        if(bitmap != null){
                            holder.user_icon.setImageBitmap(bitmap);
                        }

                    }else{

                    }

                }else{

                }
            }
        });
        //set up username and content
        holder.username_text.setText(thisComment.getUserName());
        holder.content_text.setText(thisComment.getContent());
        holder.commentTime.setText(Helper.getRelativePostTime(thisComment.getTimestamp()));
        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(ctx, holder.buttonViewOption);
                popup.inflate(R.menu.menu_delete);
                popup.show();
                Log.d("clicked","menu");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_delete:
                                db.collection("comments").document(thisComment.getTimestamp()).delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                 commentList.remove(position);
                                                 db.collection("timelines").document(thisComment.getTimeline_ref()).update("listOfComment", commentList.size());
                                                 notifyDataSetChanged();
                                                 Intent intent = new Intent("custom-message");
                                                 intent.putExtra("size", Integer.toString(commentList.size()));
                                                 LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

                                            }
                                        });

//                                db.collection("timelines").whereArrayContains("listOfComment", thisComment.getTimestamp()).

                        }
                        return false;
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }



    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTime;
        CircularImageView user_icon;
        TextView username_text, content_text;
        ImageButton buttonViewOption;

        public CommentViewHolder(View itemView) {
            super(itemView);
            user_icon = itemView.findViewById(R.id.user_icon);
            username_text = itemView.findViewById(R.id.username_text);
            content_text = itemView.findViewById(R.id.content_text);
            commentTime = itemView.findViewById(R.id.comment_time);
            buttonViewOption = itemView.findViewById(R.id.buttonViewOption);
        }
    }
}
