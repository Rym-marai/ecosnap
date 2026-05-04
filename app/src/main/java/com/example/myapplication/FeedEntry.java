package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "feed_entries")
public class FeedEntry {
    @PrimaryKey
    public int id;
    public String userId;
    public String userInitials;
    public String userName;
    public String location;
    public String timeAgo;
    public int speciesId;
    public String speciesName;
    public String emoji;
    public String conservationStatus;
    public String caption;
    public int likes;
    public boolean isLiked;
    public boolean isSaved;
    public int commentCount;
    public int userBadgeLevel; // 0 = no badge, 1 = bronze, 2 = silver, 3 = gold
    public String userBadgeTitle; // Last unlocked badge title (e.g., "Rare Find", "Mapper")

    @Ignore
    public boolean isExpanded; // for UI state
    @Ignore
    public List<FeedComment> comments = new ArrayList<>(); // For storing comments
}
