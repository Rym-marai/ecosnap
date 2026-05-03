package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SpeciesDetailActivity - Shows species information
 */
public class SpeciesDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species_detail);

        // ── Back Button ──
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // ── Save to Journal ──
        Button btnSave = findViewById(R.id.btnSaveToJournal);
        btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "Saved to Journal!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
