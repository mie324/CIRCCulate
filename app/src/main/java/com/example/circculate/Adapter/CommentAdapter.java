package com.example.circculate.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.circculate.Model.CommentModel;
import com.example.circculate.Model.EventModel;
import com.example.circculate.Model.UserModel;
import com.example.circculate.R;
import com.example.circculate.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.rpc.Help;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private Context ctx;
    private List<CommentModel> commentList;
    private FirebaseFirestore db;
    private UserModel currentUser;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;



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
    public void onBindViewHolder(@NonNull final CommentAdapter.CommentViewHolder holder, int position) {
        CommentModel thisComment = commentList.get(position);
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

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTime;
        CircularImageView user_icon;
        TextView username_text, content_text;

        public CommentViewHolder(View itemView) {
            super(itemView);
            user_icon = itemView.findViewById(R.id.user_icon);
            username_text = itemView.findViewById(R.id.username_text);
            content_text = itemView.findViewById(R.id.content_text);
            commentTime = itemView.findViewById(R.id.comment_time);
        }
    }
}
