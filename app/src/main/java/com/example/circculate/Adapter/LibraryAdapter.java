package com.example.circculate.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.audioViewHolder>{
    public LibraryAdapter() {
    }
    class audioViewHolder extends RecyclerView.ViewHolder{

        public audioViewHolder(View itemView) {
            super(itemView);
        }
    }



    @NonNull
    @Override
    public LibraryAdapter.audioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.audioViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
