package com.example.circculate.Adapter;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.circculate.Model.RecordingModel;
import android.media.MediaPlayer;
import com.example.circculate.Model.AudioModel;
import android.util.Log;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.audioViewHolder>{

    public LibraryAdapter() {}
    private List<AudioModel> recordList;
    private StorageReference firebaseref;
    private Context context;
    public LibraryAdapter(Context context, List<AudioModel> recordList){
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
        View parent_view;

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
            parent_view = itemView.findViewById(R.id.parent_view);
//            bt_hide_text = itemView.findViewById(R.id.bt_hide_text);
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
        final AudioModel newRecording = recordList.get(position);
        holder.seek_song_progressbar.setProgress(0);
        holder.seek_song_progressbar.setMax(MusicUtils.MAX_PROGRESS);
//        holder.lyt_expand_text.setVisibility(View.GONE);

        //get audio file
        holder.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                holder.bt_play.setImageResource(R.drawable.ic_arrow);

            }
        });
        StorageReference audioRef = firebaseref.child(newRecording.getAudioRef());
        audioRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadedUri = task.getResult();
                    Log.d("prepare", "prepared");
//                    prepareAudio(downloadedUri);

                }else{

                }

            }

            private void prepareAudio(Uri downloadedUri) {
                try{
//                    holder.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    holder.player.setDataSource("https://firebasestorage.googleapis.com/v0/b/circculate.appspot.com/o/rUSzZ8NTnthxkk36ItXBRGWRkRr2%2Faudiorecord.3gp?alt=media&token=30046ad5-2e22-4f8e-bb72-89a2726e4cbb");
                    holder.player.prepare();


                }catch(Exception e){
                    Snackbar.make(holder.parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        holder.recordingText.setText(newRecording.getTitle());
//        holder.toggle_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggleSectionText(holder.toggle_button, holder.lyt_expand_text);
//
//            }
//        });

        //toggle button animation

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

            }
        });

        //play button listener
        buttonPlayerAction(holder.bt_play, holder.player);



    }

    private void buttonPlayerAction(final ImageView bt_play, final MediaPlayer player) {
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
//                    holder.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource("https://firebasestorage.googleapis.com/v0/b/circculate.appspot.com/o/rUSzZ8NTnthxkk36ItXBRGWRkRr2%2Faudiorecord.3gp?alt=media&token=30046ad5-2e22-4f8e-bb72-89a2726e4cbb");
                    player.prepare();
                    Log.d("prepare","prepared");


                }catch(Exception e){

                }
                if(player.isPlaying()){
                    player.pause();
                    bt_play.setImageResource(R.drawable.ic_arrow);
                }else{
                    player.start();
                    bt_play.setImageResource(R.drawable.ic_pause);
                    
                }
                
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
