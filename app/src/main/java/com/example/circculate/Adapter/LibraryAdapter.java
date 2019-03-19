package com.example.circculate.Adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.example.circculate.R;
import com.example.circculate.utils.Helper;
import com.example.circculate.utils.MusicUtils;
import com.example.circculate.utils.Tools;
import com.example.circculate.utils.ViewAnimation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.circculate.Model.RecordingModel;
import android.media.MediaPlayer;
import com.example.circculate.Model.AudioModel;
import android.util.Log;
import android.app.Dialog;
import android.view.Window;
import java.net.URLConnection;
import java.net.URL;
import java.net.HttpURLConnection;
import android.os.Handler;
import java.util.concurrent.TimeUnit;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.audioViewHolder>{

    public LibraryAdapter() {}
    private List<AudioModel> recordList;
    private StorageReference firebaseref;
    private FirebaseFirestore db;
    private Context context;
    private ProgressBar progress_bar;
    private MediaPlayer player;
    private static final String TAG = "playtest";
    public LibraryAdapter(Context context, List<AudioModel> recordList, MediaPlayer player){
        this.context = context;
        this.recordList = recordList;
        this.firebaseref = FirebaseStorage.getInstance().getReference();
        this.db = FirebaseFirestore.getInstance();
        this.player = player;

    }

    class audioViewHolder extends RecyclerView.ViewHolder{
        TextView recordingText;
        TextView total_duration;
        ImageButton toggle_button;
        ImageView bt_translate;
        ImageView bt_delete;
        ImageView bt_play;
        ImageButton bt_prev;
        ImageButton bt_next;
        AppCompatSeekBar seek_song_progressbar;
        MusicUtils utils = new MusicUtils();
//        MediaPlayer player = new MediaPlayer();
        Button bt_hide_text;
        View lyt_expand_text;
        View parent_view;
        public boolean isFirstTouch = true;
        private Handler mHandler = new Handler();
        private Runnable mUpdateTimeTask;
        MediaPlayer player1= new MediaPlayer();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

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
            total_duration = itemView.findViewById(R.id.song_total_duration);


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
//        updateTimeerandSeeker(holder.total_duration,holder.utils,holder.seek_song_progressbar);
//        holder.retriever.setDataSource("https://firebasestorage.googleapis.com/v0/b/circculate.appspot.com/o/DAWcN2mG7GZ37bI3Zu6r86rMCGa2%2Fdo_not_delete.wav?alt=media&token=86d96505-af66-40a5-ab04-28aa87128808");
//        String duration = holder(.retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//        int duration = player.getDuration();
//        String time = String.format("%02d min, %02d sec",
//                TimeUnit.MILLISECONDS.toMinutes(duration),
//                TimeUnit.MILLISECONDS.toSeconds(duration) -
//                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
//        );
//        Log.d("duration", duration);
//        holder.total_duration.setText(player.getDuration());
//        holder.lyt_expand_text.setVisibility(View.GONE);

        //get audio file
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onCompletion:  play complete");
//                player.stop();
//                player.release();
//                try {
//                    player.setDataSource("https://firebasestorage.googleapis.com/v0/b/circculate.appspot.com/o/DAWcN2mG7GZ37bI3Zu6r86rMCGa2%2Fdo_not_delete.wav?alt=media&token=86d96505-af66-40a5-ab04-28aa87128808");
////                    player.prepare();
//                }catch (IOException e){
//
//                }
                Log.d(TAG, "onCompletion: before change icon");
                holder.bt_play.setImageResource(R.drawable.ic_arrow);
                Log.d(TAG, "onCompletion: before change flag");
                holder.isFirstTouch = true;

            }
        });
        final StorageReference audioRef = firebaseref.child(newRecording.getAudioRef());





        holder.recordingText.setText(newRecording.getTitle());
        holder.bt_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newRecording.getTextRef()==null){
                    showNoticeDialog();


                }else{
                    showFullScreenDialog(view, newRecording);
                }

            }
        });

        holder.toggle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isExpanded = toggleLayoutExpand(!newRecording.expanded, view, holder.lyt_expand_text);
                newRecording.expanded = isExpanded;
                if(newRecording.expanded == true){
                    if(holder.isFirstTouch) {
                        audioRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("uri", uri.toString());
                                if (uri != null) {
                                    prepareAudio(uri.toString());
                                }


                            }

                            private void prepareAudio(String uri) {
                                try {
                                    player.reset();
                                    player.setDataSource(uri);
                                    player.prepare();
                                    holder.mHandler.post(holder.mUpdateTimeTask);
                                } catch (IOException e) {
                                    Snackbar.make(holder.parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("download", "failed");
                            }
                        });
                    }

                }else{
                    holder.isFirstTouch = true;
                    player.stop();
                }

            }
        });

        //void recycling view
        if(newRecording.expanded){
            Log.d("expanded_test","uri");
            holder.lyt_expand_text.setVisibility(View.VISIBLE);
        }else{
            holder.lyt_expand_text.setVisibility(View.GONE);
            holder.isFirstTouch = true;
        }
        Tools.toggleArrow(newRecording.expanded, (View)holder.toggle_button, false);
        //delete button
        holder.bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecording(newRecording);


            }
        });
        holder.bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.isFirstTouch){
                    player.start();
                    holder.bt_play.setImageResource(R.drawable.ic_pause);
                    holder.isFirstTouch = false;
                    holder.mHandler.post(holder.mUpdateTimeTask);
//                    audioRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Log.d("uri", uri.toString());
//                            if(uri!=null){
//                                prepareAudio(uri.toString());
//                                holder.isFirstTouch = false;
//                            }
//
//
//                        }
//
//                        private void prepareAudio(String uri) {
//                            try{
//                                player.reset();
//                                player.setDataSource(uri);
//                                player.prepare();
//                                player.start();
//                                Log.d("test", "1");
//                                holder.mHandler.post(holder.mUpdateTimeTask);
//                                holder.bt_play.setImageResource(R.drawable.ic_pause);
//                            }catch (IOException e){
//                                Snackbar.make(holder.parent_view, "Cannot load audio file", Snackbar.LENGTH_SHORT).show();
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("download","failed");
//                        }
//                    });
                }else {
                    if(player.isPlaying()){
                        player.pause();
                        holder.bt_play.setImageResource(R.drawable.ic_arrow);
                    }else{
                        player.start();
                        holder.bt_play.setImageResource(R.drawable.ic_pause);
                        holder.mHandler.post(holder.mUpdateTimeTask);

                    }
                }



            }

        });

        holder.mUpdateTimeTask = new Runnable() {
            @Override
            public void run() {
                long totalDuration = player.getDuration();
                long currentDuration = player.getCurrentPosition();
                holder.total_duration.setText(holder.utils.milliSecondsToTimer(totalDuration));
                    // Updating progress bar
                int progress = (int) (holder.utils.getProgressSeekBar(currentDuration, totalDuration));
                holder.seek_song_progressbar.setProgress(progress);
                if (player.isPlaying()) {
                        holder.mHandler.postDelayed(this, 100);
                }

            }
        };

//        buttonPlayerAction(holder.bt_play, holder.player);
        holder.seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                holder.mHandler.removeCallbacks(holder.mUpdateTimeTask);

            }

            private int progressToTimer(int progress, int totalDuration) {
                int currentDuration = 0;
                totalDuration = (int) (totalDuration / 1000);
                currentDuration = (int) ((((double)progress) / 100) * totalDuration);

                // return current duration in milliseconds
                return currentDuration * 1000;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                holder.mHandler.removeCallbacks(holder.mUpdateTimeTask);
                int totalDuration = player.getDuration();

                int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);
                player.seekTo(currentPosition);
                holder.mHandler.post(holder.mUpdateTimeTask);

            }

        });


//

    }

    private void updateTimeerandSeeker(TextView total_duration, MusicUtils utils, AppCompatSeekBar seek_song_progressbar) {
        long totalDuration = player.getDuration();
        long currentDuration = player.getCurrentPosition();
        total_duration.setText(utils.milliSecondsToTimer(totalDuration));
        // Updating progress bar
        int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
        seek_song_progressbar.setProgress(progress);
    }

    private void showNoticeDialog() {
        final Dialog nagDialog = new Dialog(context,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        nagDialog.setCancelable(true);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setContentView(R.layout.dialog_notification);
        FloatingActionButton close_btn = nagDialog.findViewById(R.id.del_fab);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nagDialog.dismiss();
            }
        });
        nagDialog.show();

    }

    private void showFullScreenDialog(View view, AudioModel newRecording) {
        final Dialog nagDialog = new Dialog(context,android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        nagDialog.setCancelable(true);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setContentView(R.layout.full_screen_text);
        Log.d("title", newRecording.getTitle());
        TextView text_title = nagDialog.findViewById(R.id.text_title);
        TextView text_time = nagDialog.findViewById(R.id.text_time);
        text_title.setText(newRecording.getTitle());
        text_time.setText(Helper.MonthDayTime(newRecording.getTimestamp()));
        StorageReference textRef = firebaseref.child(newRecording.getTextRef());
        final long ONE_MEGABYTE = 1024 * 1024;
        textRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String s = new String(bytes);
                Log.d("test", s);
                TextView text_content = nagDialog.findViewById(R.id.text_content);
                text_content.setText(s);
                nagDialog.show();

            }
        });
    }

    private void buttonPlayerAction(final ImageView bt_play, final MediaPlayer player) {
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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



    private void deleteRecording(final AudioModel newRecording) {
//        recordList.remove(newRecording);
//        notifyDataSetChanged();

        if(newRecording.getTextRef()!=null){
            firebaseref.child(newRecording.getTextRef()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("delete", "text success");
                    deleteDbRef(newRecording);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("delele", "text Delete failed.");
                }
            });
        }
        firebaseref.child(newRecording.getAudioRef()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("delete", "audio delete success");
                deleteDbRef(newRecording);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("delete", "audio Delete failed.");
            }
        });






    }

    private void deleteDbRef(final AudioModel newRecording){
        db.collection("recordings").document(newRecording.getTimestamp()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("delete", "onComplete: delete db refs");
                    recordList.remove(newRecording);
                    notifyDataSetChanged();

                }else {
                    Log.d("delete", "onComplete: " + task.getException().toString());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return recordList.size();
    }
}
