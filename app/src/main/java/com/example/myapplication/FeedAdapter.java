package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private final List<FeedEntry> entries;

    public FeedAdapter(List<FeedEntry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_card, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        FeedEntry entry = entries.get(position);
        Context context = holder.itemView.getContext();

        holder.tvUsername.setText(entry.userName);
        holder.tvUserLocation.setText(entry.location + " · " + entry.timeAgo);
        holder.tvSpeciesName.setText(entry.speciesName);
        holder.tvCaption.setText(entry.caption);

        // Load User Avatar
        int profileIndex = (position % 5) + 1;
        String profileDrawableName = "p" + profileIndex;
        int profileResId = context.getResources().getIdentifier(profileDrawableName, "drawable", context.getPackageName());
        if (profileResId != 0) {
            holder.ivUserAvatar.setImageResource(profileResId);
        }

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

        // Status Badge Coloring
        String status = entry.conservationStatus != null ? entry.conservationStatus.toUpperCase() : "";
        holder.tvStatusBadge.setText(status);

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
        holder.tvStatusBadge.setBackgroundTintList(ContextCompat.getColorStateList(context, bgColor));
        holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, textColor));

        // Bookmark / Save Logic
        updateSaveIcon(holder.btnSave, entry.isSaved);

        holder.btnSave.setOnClickListener(v -> {
            entry.isSaved = !entry.isSaved;
            updateSaveIcon(holder.btnSave, entry.isSaved);

            if (entry.isSaved) {
                Toast.makeText(context, "Saved to Journal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSaveIcon(MaterialButton button, boolean isSaved) {
        if (isSaved) {
            button.setIcon(ContextCompat.getDrawable(button.getContext(), android.R.drawable.ic_menu_save));
            button.setIconTint(ContextCompat.getColorStateList(button.getContext(), R.color.green_primary));
        } else {
            button.setIcon(ContextCompat.getDrawable(button.getContext(), android.R.drawable.ic_menu_add));
            button.setIconTint(ContextCompat.getColorStateList(button.getContext(), android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return entries != null ? entries.size() : 0;
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvUserLocation, tvStatusBadge, tvSpeciesName, tvCaption;
        ImageView ivUserAvatar, ivSpeciesPhoto;
        MaterialButton btnSave;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvUserLocation = itemView.findViewById(R.id.tvUserLocation);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvSpeciesName = itemView.findViewById(R.id.tvSpeciesName);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            ivSpeciesPhoto = itemView.findViewById(R.id.ivSpeciesPhoto);
            btnSave = itemView.findViewById(R.id.btnSave);
        }
    }
}
