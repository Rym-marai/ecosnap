package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<JournalEntry> allEntries;
    private RecyclerView rvNearby;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_container);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        rvNearby = view.findViewById(R.id.rvNearby);
        if (rvNearby != null && getContext() != null) {
            rvNearby.setLayoutManager(new LinearLayoutManager(getContext()));
            allEntries = JsonUtils.loadListFromRaw(getContext(), R.raw.mock_journal, JournalEntry.class);
            updateList(allEntries);
        }

        ChipGroup chipGroup = view.findViewById(R.id.chipGroupFilter);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipAll) {
                filterMap("all");
            } else if (checkedId == R.id.chipPlants) {
                filterMap("plant");
            } else if (checkedId == R.id.chipAnimals) {
                filterMap("animal");
            }
        });
    }

    private void updateList(List<JournalEntry> entries) {
        if (entries != null) {
            rvNearby.setAdapter(new JournalAdapter(entries));
        }
    }

    private void filterMap(String type) {
        if (mMap == null || allEntries == null) return;
        mMap.clear();
        List<JournalEntry> filteredList = new ArrayList<>();

        for (JournalEntry entry : allEntries) {
            boolean matches = false;
            if (type.equals("all")) {
                matches = true;
            } else if (type.equals("plant")) {
                matches = isPlant(entry.speciesName);
            } else if (type.equals("animal")) {
                matches = !isPlant(entry.speciesName);
            }

            if (matches) {
                filteredList.add(entry);
                addMarkerForEntry(entry);
            }
        }
        updateList(filteredList);
    }

    private boolean isPlant(String speciesName) {
        if (speciesName == null) return false;
        String name = speciesName.toLowerCase();
        return name.contains("pine") || name.contains("plant") || name.contains("cedar");
    }

    private void addMarkerForEntry(JournalEntry entry) {
        LatLng location = new LatLng(entry.latitude, entry.longitude);
        boolean plant = isPlant(entry.speciesName);
        int drawableRes = plant ? R.drawable.marker_plant : R.drawable.marker_animal;

        mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(entry.speciesName)
                .snippet(entry.location)
                .icon(bitmapDescriptorFromVector(getContext(), drawableRes)));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (allEntries != null && !allEntries.isEmpty()) {
            filterMap("all");
            JournalEntry first = allEntries.get(0);
            LatLng firstPos = new LatLng(first.latitude, first.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPos, 6));
        } else {
            LatLng tunisia = new LatLng(33.8869, 9.5375);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tunisia, 6));
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}

