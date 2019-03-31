package com.example.circculate.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.circculate.Model.CarePreferenceAnswerModel;
import com.example.circculate.R;

import java.util.List;

public class CarePreferenceAdapter extends RecyclerView.Adapter<CarePreferenceAdapter.PreferenceViewHolder> {
    private Context ctx;
    private List<CarePreferenceAnswerModel> carePreferences;

    public CarePreferenceAdapter(Context ctx, List<CarePreferenceAnswerModel> carePreferences){
        this.ctx = ctx;
        this.carePreferences = carePreferences;

    }

    @NonNull
    @Override
    public CarePreferenceAdapter.PreferenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_acp, parent, false);
        PreferenceViewHolder preferenceViewHolder = new PreferenceViewHolder(itemView);
        return preferenceViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CarePreferenceAdapter.PreferenceViewHolder holder, int position) {
        CarePreferenceAnswerModel carePreference = carePreferences.get(position);
        final String username = carePreference.getUsername();
        holder.usernameHolder.setText(username);
        holder.typeIndicator.setText("'s care preference");
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, "Now show detail of " + username, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return carePreferences.size();
    }


    public class PreferenceViewHolder extends RecyclerView.ViewHolder{
        public TextView usernameHolder, typeIndicator;
        public LinearLayout parentLayout;

        public PreferenceViewHolder(View itemView){
            super(itemView);
            usernameHolder = itemView.findViewById(R.id.username);
            typeIndicator = itemView.findViewById(R.id.type_indicator);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }

    }
}
