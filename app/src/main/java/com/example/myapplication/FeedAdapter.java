package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private final List<FeedEntry> entries;
    private OnSaveClickListener onSaveClickListener;
    private OnCommentClickListener onCommentClickListener;

    public interface OnSaveClickListener {
        void onSaveClick(FeedEntry entry, FeedViewHolder holder);
    }

    public interface OnCommentClickListener {
        void onCommentClick(FeedEntry entry, FeedViewHolder holder);
    }

    public FeedAdapter(List<FeedEntry> entries) {
        this.entries = entries;
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.onSaveClickListener = listener;
    }

    public void setOnCommentClickListener(OnCommentClickListener listener) {
        this.onCommentClickListener = listener;
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

        // Set username with badge title if available
        if (entry.userBadgeLevel > 0 && entry.userBadgeTitle != null) {
            holder.tvUsername.setText(entry.userName + " 🏅 " + entry.userBadgeTitle);
            holder.tvUsername.setTextColor(ContextCompat.getColor(context, R.color.green_primary));
        } else {
            holder.tvUsername.setText(entry.userName);
            holder.tvUsername.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        }

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

        // Hide badge from avatar (no longer needed)
        if (holder.ivUserBadge != null) {
            holder.ivUserBadge.setVisibility(View.GONE);
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

        // Like Button
        updateLikeIcon(holder.btnLike, entry.isLiked, entry.likes);
        holder.btnLike.setOnClickListener(v -> {
            entry.isLiked = !entry.isLiked;
            entry.likes += entry.isLiked ? 1 : -1;
            updateLikeIcon(holder.btnLike, entry.isLiked, entry.likes);
        });

        // Comment Button
        holder.btnComment.setText(entry.commentCount > 0 ? entry.commentCount + "" : "Comment");
        holder.btnComment.setOnClickListener(v -> {
            // Scroll to comments section (can be done by scrolling parent RecyclerView)
        });

        // Share Button
        holder.btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, entry.speciesName + " sighting");
            shareIntent.putExtra(Intent.EXTRA_TEXT, 
                    entry.userName + " found " + entry.speciesName + " at " + entry.location + "! Check out the EcoSnap app.");
            context.startActivity(Intent.createChooser(shareIntent, "Share sighting"));
        });

        // Save Button
        updateSaveIcon(holder.btnSave, entry.isSaved);
        holder.btnSave.setOnClickListener(v -> {
            if (onSaveClickListener != null) {
                onSaveClickListener.onSaveClick(entry, holder);
            }
        });

        // Display existing comments
        displayComments(holder, entry, context);

        // Handle new comment input
        holder.btnPostComment.setOnClickListener(v -> {
            String commentText = holder.etCommentInput.getText().toString().trim();
            if (!commentText.isEmpty()) {
                FeedComment newComment = new FeedComment(entry.id, "You", "YOU", commentText);
                entry.comments.add(newComment);
                entry.commentCount++;
                holder.etCommentInput.setText("");
                displayComments(holder, entry, context);
                notifyItemChanged(position);
            }
        });
    }

    private void displayComments(FeedViewHolder holder, FeedEntry entry, Context context) {
        holder.commentsContainer.removeAllViews();

        if (entry.comments == null || entry.comments.isEmpty()) {
            TextView emptyView = new TextView(context);
            emptyView.setText("No comments yet");
            emptyView.setTextSize(11);
            emptyView.setTextColor(ContextCompat.getColor(context, R.color.text_hint));
            emptyView.setPadding(0, 4, 0, 4);
            holder.commentsContainer.addView(emptyView);
            return;
        }

        for (FeedComment comment : entry.comments) {
            LinearLayout commentView = new LinearLayout(context);
            commentView.setOrientation(LinearLayout.VERTICAL);
            commentView.setPadding(0, 8, 0, 8);

            TextView userNameView = new TextView(context);
            userNameView.setText(comment.userName);
            userNameView.setTextSize(11);
            userNameView.setTypeface(null, Typeface.BOLD);
            userNameView.setTextColor(ContextCompat.getColor(context, R.color.text_primary));

            TextView commentTextView = new TextView(context);
            commentTextView.setText(comment.text);
            commentTextView.setTextSize(10);
            commentTextView.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            commentTextView.setLineSpacing(1.2f, 1.2f);

            commentView.addView(userNameView);
            commentView.addView(commentTextView);

            holder.commentsContainer.addView(commentView);
        }
    }

    private void updateLikeIcon(MaterialButton button, boolean isLiked, int likeCount) {
        String likeText = likeCount > 0 ? likeCount + " " + (likeCount == 1 ? "Like" : "Likes") : "Like";
        button.setText(likeText);
        if (isLiked) {
            button.setIcon(ContextCompat.getDrawable(button.getContext(), android.R.drawable.ic_dialog_info));
            button.setIconTint(ContextCompat.getColorStateList(button.getContext(), R.color.status_en));
            button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.status_en));
        } else {
            button.setIcon(ContextCompat.getDrawable(button.getContext(), android.R.drawable.ic_dialog_info));
            button.setIconTint(ContextCompat.getColorStateList(button.getContext(), android.R.color.darker_gray));
            button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.text_secondary));
        }
    }

    private void updateSaveIcon(MaterialButton button, boolean isSaved) {
        if (isSaved) {
            button.setText("Saved");
            button.setIcon(ContextCompat.getDrawable(button.getContext(), android.R.drawable.ic_menu_save));
            button.setIconTint(ContextCompat.getColorStateList(button.getContext(), R.color.green_primary));
            button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.green_primary));
        } else {
            button.setText("Save");
            button.setIcon(ContextCompat.getDrawable(button.getContext(), android.R.drawable.ic_menu_add));
            button.setIconTint(ContextCompat.getColorStateList(button.getContext(), android.R.color.darker_gray));
            button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.text_secondary));
        }
    }

    private void loadUserBadge(ImageView badgeView, int badgeLevel, Context context) {
        // Badge is now shown next to username instead
        badgeView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return entries != null ? entries.size() : 0;
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvUserLocation, tvStatusBadge, tvSpeciesName, tvCaption;
        ImageView ivUserAvatar, ivSpeciesPhoto, ivUserBadge;
        MaterialButton btnLike, btnComment, btnShare, btnSave, btnPostComment;
        EditText etCommentInput;
        LinearLayout commentsContainer;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            ivUserBadge = itemView.findViewById(R.id.ivUserBadge);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvUserLocation = itemView.findViewById(R.id.tvUserLocation);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvSpeciesName = itemView.findViewById(R.id.tvSpeciesName);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            ivSpeciesPhoto = itemView.findViewById(R.id.ivSpeciesPhoto);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnSave = itemView.findViewById(R.id.btnSave);
            btnPostComment = itemView.findViewById(R.id.btnPostComment);
            etCommentInput = itemView.findViewById(R.id.etCommentInput);
            commentsContainer = itemView.findViewById(R.id.commentsContainer);
        }
    }
}
