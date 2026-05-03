package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class JournalFragment extends Fragment {
    
    private JournalAdapter adapter;
    private TextView tvCount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);
        
        tvCount = view.findViewById(R.id.tvCount);
        RecyclerView rvJournal = view.findViewById(R.id.rvJournal);
        rvJournal.setLayoutManager(new LinearLayoutManager(getContext()));

        List<JournalEntry> entries = JsonUtils.loadListFromRaw(getContext(), R.raw.mock_journal, JournalEntry.class);
        if (entries != null) {
            adapter = new JournalAdapter(entries);
            rvJournal.setAdapter(adapter);
            tvCount.setText(entries.size() + " sightings");
        } else {
            tvCount.setText("0 sightings");
        }

        EditText etSearch = view.findViewById(R.id.etSearchJournal);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                    tvCount.setText(adapter.getItemCount() + " sightings");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }
}
