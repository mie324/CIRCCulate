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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circculate.Model.TimelineItemModel;
import com.example.circculate.R;
import com.example.circculate.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import com.example.circculate.commentPage;
import android.os.Bundle;
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {
    private Context ctx;
    private List<TimelineItemModel> timelineList;
    private FirebaseStorage storage;
    private static final String TAG = "TimelineAdapter";
    private final long ONE_MB = 1024 * 1024;

    public TimelineAdapter(Context ctx, List<TimelineItemModel> timelineList){
        this.ctx = ctx;
        this.timelineList = timelineList;
        this.storage = FirebaseStorage.getInstance();
    }

    public class TimelineViewHolder extends RecyclerView.ViewHolder{
        public TextView usernameText, contentText, commentNumText, postTimeText;
        public ImageView commentImage, timelineImg;
        public CircularImageView userIconImage;
        public TimelineViewHolder(View itemView){
            super(itemView);
            usernameText = itemView.findViewById(R.id.username);
            contentText = itemView.findViewById(R.id.timeline_content);
            commentNumText = itemView.findViewById(R.id.comment_num);
            postTimeText = itemView.findViewById(R.id.post_time);
            commentImage = itemView.findViewById(R.id.comment_bt);
            userIconImage = itemView.findViewById(R.id.user_icon);
            timelineImg = itemView.findViewById(R.id.time_img);
        }
    }

    @NonNull
    @Override
    public TimelineAdapter.TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        TimelineViewHolder timelineViewHolder = new TimelineViewHolder(itemView);
        return timelineViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TimelineAdapter.TimelineViewHolder holder, int position) {
        final TimelineItemModel timeline = timelineList.get(position);
        if(timeline.getImgRef() == null){
            ViewGroup.LayoutParams params = holder.timelineImg.getLayoutParams();
            params.height = 0;
            holder.timelineImg.setLayoutParams(params);
        }else {
            try {
                StorageReference imgRef = storage.getReference().child(timeline.getImgRef());
                final File localImg = File.createTempFile(timeline.getTimestamp(), "jpg");
                imgRef.getFile(localImg).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Bitmap image = BitmapFactory.decodeFile(localImg.getAbsolutePath());
                            holder.timelineImg.setImageBitmap(image);
                        }
                    }
                });
            }catch (IOException e){
                Log.d(TAG, "onBindViewHolder: " + e.toString());
            }

        }

        StorageReference iconRef = storage.getReference().child(timeline.getUserIconRef());
        iconRef.getBytes(ONE_MB).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                if(task.isSuccessful()){
                    Bitmap userIcon = BitmapFactory.decodeByteArray(task.getResult(),
                            0, task.getResult().length);
                    holder.userIconImage.setImageBitmap(userIcon);
                }else {
                    Log.d(TAG, "onComplete: cannot get user icon");
                }
            }
        });

        holder.usernameText.setText(timeline.getUserName());
        holder.contentText.setText(timeline.getContent());
        final String commentString = timeline.getListOfComment().size() == 0 ? "No comments" :
                timeline.getListOfComment().size() + " comment(s)";
        holder.commentNumText.setText(commentString);
        String relativePostTime = Helper.getRelativePostTime(timeline.getTimestamp());
        holder.postTimeText.setText(relativePostTime);

        holder.commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, "Now will go to all comments", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putSerializable("TimeLine", timeline);
                Intent intent = new Intent(ctx, commentPage.class);
                intent.putExtras(bundle);
                ctx.startActivity(intent);
            }
        });

        holder.commentNumText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, "Now will go to all comments", Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putSerializable("TimeLine", timeline);
                Intent intent = new Intent(ctx, commentPage.class);
                intent.putExtras(bundle);
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timelineList.size();
    }
}
