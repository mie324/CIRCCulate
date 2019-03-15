package com.example.circculate.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.example.circculate.R;
import com.example.circculate.utils.MusicUtils;
import com.example.circculate.utils.Tools;
import com.example.circculate.utils.ViewAnimation;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.circculate.Model.RecordingModel;
import android.media.MediaPlayer;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.audioViewHolder>{

    public LibraryAdapter() {}
    private List<RecordingModel> recordList;
    private StorageReference firebaseref;
    private Context context;
    public LibraryAdapter(Context context, List<RecordingModel> recordList){
        this.context = context;
        this.recordList = recordList;
        this.firebaseref = FirebaseStorage.getInstance().getReference();

    }

    class audioViewHolder extends RecyclerView.ViewHolder{
        TextView recordingText;
        ImageButton toggle_button;
        ImageView bt_translate;
        ImageView bt_delete;
        ImageView bt_play;
        ImageButton bt_prev;
        ImageButton bt_next;
        AppCompatSeekBar seek_song_progressbar;
        MusicUtils utils = new MusicUtils();
        MediaPlayer player = new MediaPlayer();
        Button bt_hide_text;
        View lyt_expand_text;

        public audioViewHolder(View itemView) {
            super(itemView);
            lyt_expand_text = (View) itemView.findViewById(R.id.lyt_expand_text);
            recordingText = itemView.findViewById(R.id.recording_title);
            toggle_button = itemView.findViewById(R.id.bt_toggle_text);
            bt_translate = itemView.findViewById(R.id.bt_translate);
            bt_delete = itemView.findViewById(R.id.bt_shuffle);
            bt_play = itemView.findViewById(R.id.bt_play);
            bt_prev = itemView.findViewById(R.id.bt_prev);
            bt_next = itemView.findViewById(R.id.bt_next);
            seek_song_progressbar = itemView.findViewById(R.id.seek_song_progressbar);
            bt_hide_text = itemView.findViewById(R.id.bt_hide_text);
            //Media Player
//            InitProgressbar();
//            playAudio();


        }
    }



    @NonNull
    @Override
    public LibraryAdapter.audioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_library, parent, false);
        audioViewHolder viewholder = new audioViewHolder(itemView);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull final LibraryAdapter.audioViewHolder holder, int position) {
        final RecordingModel newRecording = recordList.get(position);
//        holder.lyt_expand_text.setVisibility(View.GONE);
        holder.recordingText.setText(newRecording.getTitle());
        holder.toggle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSectionText(holder.toggle_button, holder.lyt_expand_text);

            }
        });

        holder.toggle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isExpanded = toggleLayoutExpand(!newRecording.expanded, view, holder.lyt_expand_text);
                newRecording.expanded = isExpanded;
            }
        });

        //void recycling view
        if(newRecording.expanded){
            holder.lyt_expand_text.setVisibility(View.VISIBLE);
        }else{
            holder.lyt_expand_text.setVisibility(View.GONE);
        }
        Tools.toggleArrow(newRecording.expanded, (View)holder.toggle_button, false);
        //delete button
        holder.bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecording();
            }
        });

    }

    private boolean toggleLayoutExpand(boolean b, View view, View lyt_expand_text) {
        Tools.toggleArrow(b, view);
        if(b){
            ViewAnimation.expand(lyt_expand_text);
        }else{
            ViewAnimation.collapse(lyt_expand_text);
        }
        return b;
    }

    private void toggleSectionText(View view1, View view2) {
        boolean show = toggleArrow(view1);

    }


    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }


    private void deleteRecording() {
    }


    @Override
    public int getItemCount() {
        return recordList.size();
    }
}
