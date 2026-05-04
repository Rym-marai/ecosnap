package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "save_categories")
public class SaveCategory {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String color; // hex color code
    public long createdAt;

    public SaveCategory(String name, String color) {
        this.name = name;
        this.color = color;
        this.createdAt = System.currentTimeMillis();
    }

    public SaveCategory() {}
}

