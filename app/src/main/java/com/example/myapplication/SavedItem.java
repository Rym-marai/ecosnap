package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_items",
        foreignKeys = @ForeignKey(entity = SaveCategory.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE))
public class SavedItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int feedEntryId;
    public int categoryId;
    public long savedAt;

    public SavedItem(int feedEntryId, int categoryId) {
        this.feedEntryId = feedEntryId;
        this.categoryId = categoryId;
        this.savedAt = System.currentTimeMillis();
    }

    public SavedItem() {}
}

