package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FeedFragment extends Fragment {
    private FeedAdapter adapter;
    private List<FeedEntry> allEntries;
    private List<FeedEntry> filteredEntries;
    private Spinner spinnerSpeciesFilter;
    private Spinner spinnerStatusFilter;
    private MaterialButton btnResetFilter;
    private String selectedSpecies = "All Species";
    private String selectedStatus = "All Status";
    private List<SaveCategory> categories = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        RecyclerView rvFeed = view.findViewById(R.id.rvFeed);
        spinnerSpeciesFilter = view.findViewById(R.id.spinnerSpeciesFilter);
        spinnerStatusFilter = view.findViewById(R.id.spinnerStatusFilter);
        btnResetFilter = view.findViewById(R.id.btnResetFilter);

        if (rvFeed != null && getContext() != null) {
            rvFeed.setLayoutManager(new LinearLayoutManager(getContext()));

            allEntries = JsonUtils.loadListFromRaw(getContext(), R.raw.mock_feed, FeedEntry.class);
            if (allEntries != null) {
                // Ensure each entry has proper default values
                for (FeedEntry entry : allEntries) {
                    if (entry.commentCount == 0) entry.commentCount = 0;
                    if (entry.userBadgeLevel == 0) entry.userBadgeLevel = (entry.id % 3); // distribute badges
                }

                filteredEntries = new ArrayList<>(allEntries);
                adapter = new FeedAdapter(filteredEntries);
                rvFeed.setAdapter(adapter);

                // Set up save listener
                adapter.setOnSaveClickListener((entry, holder) -> {
                    showSaveDialog(entry, holder);
                });

                // Setup filter spinners
                setupFilterSpinners();

                // Reset filter button
                btnResetFilter.setOnClickListener(v -> {
                    selectedSpecies = "All Species";
                    selectedStatus = "All Status";
                    spinnerSpeciesFilter.setSelection(0);
                    spinnerStatusFilter.setSelection(0);
                    filteredEntries.clear();
                    filteredEntries.addAll(allEntries);
                    adapter.notifyDataSetChanged();
                });
            }
        }

        return view;
    }

    private void setupFilterSpinners() {
        if (getContext() == null || allEntries == null) return;

        // Get unique species from entries
        Set<String> speciesSet = new HashSet<>();
        speciesSet.add("All Species");
        for (FeedEntry entry : allEntries) {
            if (entry.speciesName != null) {
                speciesSet.add(entry.speciesName);
            }
        }
        List<String> speciesList = new ArrayList<>(speciesSet);

        // Get unique statuses
        List<String> statusList = new ArrayList<>();
        statusList.add("All Status");
        statusList.add("LC");
        statusList.add("VU");
        statusList.add("EN");

        // Setup species spinner
        ArrayAdapter<String> speciesAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, speciesList);
        speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpeciesFilter.setAdapter(speciesAdapter);

        // Setup status spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, statusList);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatusFilter.setAdapter(statusAdapter);

        // Species filter listener
        spinnerSpeciesFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSpecies = (String) parent.getItemAtPosition(position);
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Status filter listener
        spinnerStatusFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = (String) parent.getItemAtPosition(position);
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void applyFilters() {
        filteredEntries.clear();

        for (FeedEntry entry : allEntries) {
            boolean matchesSpecies = selectedSpecies.equals("All Species") ||
                    (entry.speciesName != null && entry.speciesName.equals(selectedSpecies));
            boolean matchesStatus = selectedStatus.equals("All Status") ||
                    (entry.conservationStatus != null && entry.conservationStatus.toUpperCase().equals(selectedStatus));

            if (matchesSpecies && matchesStatus) {
                filteredEntries.add(entry);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void showSaveDialog(FeedEntry entry, FeedAdapter.FeedViewHolder holder) {
        SaveDialog saveDialog = new SaveDialog(getContext(), entry.id, categories,
            new SaveDialog.OnSaveListener() {
                @Override
                public void onSave(SaveCategory category) {
                    entry.isSaved = true;
                    Toast.makeText(getContext(), "Saved to \"" + category.name + "\"", Toast.LENGTH_SHORT).show();
                    // Here you would normally save to a database
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCreateCategory(String categoryName, String color) {
                    SaveCategory newCategory = new SaveCategory(categoryName, color);
                    categories.add(newCategory);
                    entry.isSaved = true;
                    Toast.makeText(getContext(), "Saved to new category \"" + categoryName + "\"", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            });

        saveDialog.show();
    }
}
