package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private List<JournalEntry> entries;
    private final List<JournalEntry> entriesFull;

    public JournalAdapter(List<JournalEntry> entries) {
        this.entries = entries;
        this.entriesFull = new ArrayList<>(entries);
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_journal_entry, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        JournalEntry entry = entries.get(position);
        Context context = holder.itemView.getContext();

        holder.tvSpeciesName.setText(entry.speciesName);
        holder.tvScientificName.setText(entry.scientificName);
        holder.tvDate.setText(entry.date);

        // Load Species Image
        if (entry.speciesName != null) {
            String drawableName = entry.speciesName.toLowerCase().replace(" ", "_");
            int resId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
            if (resId != 0) {
                holder.ivSpeciesPhoto.setImageResource(resId);
            } else {
                holder.ivSpeciesPhoto.setImageResource(R.color.green_light);
            }
        }

        String status = entry.conservationStatus != null ? entry.conservationStatus.toUpperCase() : "";
        holder.tvStatus.setText(status);

        // Set colors based on conservation status
        int bgColor, textColor;
        switch (status) {
            case "LC":
                bgColor = R.color.status_lc_bg;
                textColor = R.color.status_lc_text;
                break;
            case "VU":
                bgColor = R.color.status_vu_bg;
                textColor = R.color.status_vu_text;
                break;
            case "EN":
                bgColor = R.color.status_en_bg;
                textColor = R.color.status_en_text;
                break;
            default:
                bgColor = R.color.gray_bg;
                textColor = R.color.text_secondary;
                break;
        }

        holder.tvStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, bgColor));
        holder.tvStatus.setTextColor(ContextCompat.getColor(context, textColor));
    }

    @Override
    public int getItemCount() {
        return entries != null ? entries.size() : 0;
    }

    public void filter(String text) {
        List<JournalEntry> filteredList = new ArrayList<>();
        for (JournalEntry item : entriesFull) {
            if (item.speciesName.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        this.entries = filteredList;
        notifyDataSetChanged();
    }

    static class JournalViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSpeciesPhoto;
        TextView tvSpeciesName, tvScientificName, tvDate, tvStatus;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSpeciesPhoto = itemView.findViewById(R.id.ivSpeciesPhoto);
            tvSpeciesName = itemView.findViewById(R.id.tvSpeciesName);
            tvScientificName = itemView.findViewById(R.id.tvScientificName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
