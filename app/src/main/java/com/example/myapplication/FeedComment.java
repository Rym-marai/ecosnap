package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "feed_comments",
        foreignKeys = @ForeignKey(entity = FeedEntry.class,
                parentColumns = "id",
                childColumns = "feedEntryId",
                onDelete = ForeignKey.CASCADE))
public class FeedComment {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int feedEntryId;
    public String userName;
    public String userInitials;
    public String text;
    public long createdAt;

    public FeedComment(int feedEntryId, String userName, String userInitials, String text) {
        this.feedEntryId = feedEntryId;
        this.userName = userName;
        this.userInitials = userInitials;
        this.text = text;
        this.createdAt = System.currentTimeMillis();
    }

    public FeedComment() {}
}

