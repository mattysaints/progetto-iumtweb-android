package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class PrenRipFragment extends Fragment {

    private static List<Prenotazione> prenotazioni;
    private static RecyclerView recyclerView;
    private static RecyclerViewAdapter adapter;
    private static final Gson gson = new Gson();
    public static Context baseContext;
    public static View viewLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_prenrip, container, false);
        setHasOptionsMenu(true);

        String url = "http://10.0.2.2:8080/progetto_ium_tweb2/RipetizioniDisponibili"; //testato e funziona anche la class taskjson

        try {
            String p = new AsyncHttpRequest().execute(new AsyncHttpRequest.Ajax(url, "POST", null)).get();
            prenotazioni = gson.fromJson(p, new TypeToken<List<Prenotazione>>() {
            }.getType());
            //System.out.println("le prenotazioni: " + prenotazioni);

            viewLayout = root.findViewById(R.id.viewLayout);
            recyclerView = root.findViewById(R.id.recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new RecyclerViewAdapter(prenotazioni,root.getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            adapter.notifyDataSetChanged();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return root;
    }

    public static void delete(Prenotazione prenotazione) {
        prenotazioni.remove(prenotazione);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.prenrip, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_filtra:
                Intent filter = new Intent(getActivity(),FilterActivity.class);
                startActivity(filter);
                return true;
        }

        return false;
    }
}