package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentDialog extends Dialog {
    private Context context;
    private int feedEntryId;
    private List<FeedComment> commentsList;
    private OnCommentListener onCommentListener;

    public interface OnCommentListener {
        void onCommentAdded(FeedComment comment);
    }

    public CommentDialog(Context context, int feedEntryId, List<FeedComment> commentsList, OnCommentListener listener) {
        super(context);
        this.context = context;
        this.feedEntryId = feedEntryId;
        this.commentsList = commentsList;
        this.onCommentListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_comments);

        // Set up dialog properties
        setCancelable(true);

        TextView tvCommentTitle = findViewById(R.id.tvCommentTitle);
        EditText etCommentText = findViewById(R.id.etCommentText);
        MaterialButton btnPostComment = findViewById(R.id.btnPostComment);
        MaterialButton btnCancelComment = findViewById(R.id.btnCancelComment);

        tvCommentTitle.setText("Comments (" + commentsList.size() + ")");

        btnPostComment.setOnClickListener(v -> {
            String commentText = etCommentText.getText().toString().trim();
            if (commentText.isEmpty()) {
                Toast.makeText(context, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new comment
            FeedComment newComment = new FeedComment(
                    feedEntryId,
                    "You",  // Current user name
                    "YOU",  // Initials
                    commentText
            );

            commentsList.add(newComment);

            if (onCommentListener != null) {
                onCommentListener.onCommentAdded(newComment);
            }

            Toast.makeText(context, "Comment posted!", Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btnCancelComment.setOnClickListener(v -> dismiss());
    }
}

