package com.ium.unito.progetto_ium_tweb1.ui.viewPren;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;

import java.util.List;

public class ViewPrenFragment extends Fragment {

    private static List<Prenotazione> prenotazioni;
    private static RecyclerView recyclerView;
    private static RecyclerViewAdapterStorico adapter;
    public static Context baseContext;
    public static View viewLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list_prenotazioni, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerViewAdapterStorico(root.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.notifyDataSetChanged();
        return root;
    }
}