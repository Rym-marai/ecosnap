package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * HomeFragment - Main dashboard for species discovery
 */
public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ── Profile Click Handling ──
        View profileClickArea = view.findViewById(R.id.profileClickArea);
        if (profileClickArea != null) {
            profileClickArea.setOnClickListener(v -> openProfile());
        }

        // ── Capture Photo ──
        Button btnCamera = view.findViewById(R.id.btnCapturePhoto);
        btnCamera.setOnClickListener(v -> openCamera());

        // ── Search Expandable Logic ──
        Button btnSearchToggle = view.findViewById(R.id.btnSearch);
        LinearLayout searchContainer = view.findViewById(R.id.searchContainer);
        EditText etSearch = view.findViewById(R.id.etSearchSpecies);
        Button btnGoSearch = view.findViewById(R.id.btnGoSearch);

        btnSearchToggle.setOnClickListener(v -> {
            if (searchContainer.getVisibility() == View.GONE) {
                searchContainer.setVisibility(View.VISIBLE);
                etSearch.requestFocus();
            } else {
                searchContainer.setVisibility(View.GONE);
            }
        });

        btnGoSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                openSpeciesDetail(query);
            } else {
                Toast.makeText(getContext(), "Enter a species name to search", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void openProfile() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).loadFragment(new ProfileFragment());
            // Clear selection in bottom nav since Profile is not a tab
            BottomNavigationView nav = getActivity().findViewById(R.id.bottomNavigation);
            if (nav != null) {
                nav.getMenu().setGroupCheckable(0, false, true);
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 100);
        } else {
            Toast.makeText(getContext(), "No camera available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSpeciesDetail(String speciesName) {
        Intent intent = new Intent(getContext(), SpeciesDetailActivity.class);
        intent.putExtra("SPECIES_NAME", speciesName);
        startActivity(intent);
    }
}
