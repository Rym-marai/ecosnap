package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class SaveDialog extends Dialog {
    private Context context;
    private List<SaveCategory> categories;
    private OnSaveListener onSaveListener;
    private int feedEntryId;

    public interface OnSaveListener {
        void onSave(SaveCategory category);
        void onCreateCategory(String categoryName, String color);
    }

    public SaveDialog(Context context, int feedEntryId, List<SaveCategory> categories, OnSaveListener listener) {
        super(context);
        this.context = context;
        this.feedEntryId = feedEntryId;
        this.categories = categories;
        this.onSaveListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_save_with_categories);

        LinearLayout categoriesContainer = findViewById(R.id.categoriesContainer);
        MaterialButton btnCreateCategory = findViewById(R.id.btnCreateCategory);

        // Display existing categories
        if (categories != null && !categories.isEmpty()) {
            for (SaveCategory category : categories) {
                MaterialButton categoryBtn = new MaterialButton(context);
                categoryBtn.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                categoryBtn.setText(category.name);
                try {
                    categoryBtn.setBackgroundColor(android.graphics.Color.parseColor(category.color));
                } catch (Exception e) {
                    categoryBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.green_light));
                }
                categoryBtn.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
                categoryBtn.setCornerRadius(12);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) categoryBtn.getLayoutParams();
                params.setMargins(0, 8, 0, 8);
                categoryBtn.setLayoutParams(params);

                categoryBtn.setOnClickListener(v -> {
                    if (onSaveListener != null) {
                        onSaveListener.onSave(category);
                    }
                    dismiss();
                });
                categoriesContainer.addView(categoryBtn);
            }
        } else {
            // Show empty state
            View emptyState = new View(context);
            Toast.makeText(context, "No categories yet. Create one!", Toast.LENGTH_SHORT).show();
        }

        btnCreateCategory.setOnClickListener(v -> showCreateCategoryDialog());
    }

    private void showCreateCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Create New Category");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 40);

        EditText etCategoryName = new EditText(context);
        etCategoryName.setHint("Category name");
        etCategoryName.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        etCategoryName.setHintTextColor(ContextCompat.getColor(context, R.color.text_hint));
        layout.addView(etCategoryName);

        builder.setView(layout);
        builder.setPositiveButton("Create", (dialog, which) -> {
            String categoryName = etCategoryName.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                if (onSaveListener != null) {
                    // Default color - can expand this later
                    onSaveListener.onCreateCategory(categoryName, "#d4edda");
                }
                dismiss();
            } else {
                Toast.makeText(context, "Please enter a category name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}

