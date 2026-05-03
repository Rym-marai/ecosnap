package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FeedFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        RecyclerView rvFeed = view.findViewById(R.id.rvFeed);
        if (rvFeed != null && getContext() != null) {
            rvFeed.setLayoutManager(new LinearLayoutManager(getContext()));

            List<FeedEntry> entries = JsonUtils.loadListFromRaw(getContext(), R.raw.mock_feed, FeedEntry.class);
            if (entries != null) {
                rvFeed.setAdapter(new FeedAdapter(entries));
            }
        }

        return view;
    }
}
