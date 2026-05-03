package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * MainActivity
 * Hosts the bottom navigation and swaps fragments accordingly.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load HomeFragment on startup
        loadFragment(new HomeFragment());

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selected = new HomeFragment();
            } else if (id == R.id.nav_journal) {
                selected = new JournalFragment();
            } else if (id == R.id.nav_feed) {
                selected = new FeedFragment();
            } else if (id == R.id.nav_map) {
                selected = new MapFragment();
            }

            if (selected != null) {
                loadFragment(selected);
                return true;
            }
            return false;
        });

        FloatingActionButton fabCamera = findViewById(R.id.fabCamera);
        fabCamera.setOnClickListener(v -> openCamera());
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 100);
        } else {
            Toast.makeText(this, "No camera available", Toast.LENGTH_SHORT).show();
        }
    }
}
