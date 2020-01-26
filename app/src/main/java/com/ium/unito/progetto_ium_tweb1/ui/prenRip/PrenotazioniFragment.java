package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.model.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.model.Stato;
import com.ium.unito.progetto_ium_tweb1.ui.home.StoricoViewModel;

import java.util.List;

public class PrenotazioniFragment extends Fragment {
    private PrenotazioniViewModel prenotazioniViewModel;
    private StoricoViewModel storicoViewModel;
    private PrenotazioniAdapter adapter;
    private AlertDialog.Builder builder;

    private CharSequence lastFilterDocente;
    private CharSequence lastFilterCorso;
    private int lastFilterOra;
    private int lastFilterGiorno;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        SharedPreferences preferences = null;
        String username = null;
        boolean ospite = false;

        if (activity != null) {
            preferences = activity.getSharedPreferences("user_information", AppCompatActivity.MODE_PRIVATE);
            prenotazioniViewModel = ViewModelProviders.of(activity).get(PrenotazioniViewModel.class);
            storicoViewModel = ViewModelProviders.of(activity).get(StoricoViewModel.class);
        }
        if (preferences != null) {
            prenotazioniViewModel.setPreferences(preferences);
            username = preferences.getString("username", "Ospite");
            ospite = preferences.getBoolean("ospite", false);
        }

        View root = inflater.inflate(R.layout.fragment_list_prenotazioni, container, false);
        setHasOptionsMenu(true);

        builder = new AlertDialog.Builder(root.getContext());

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new PrenotazioniAdapter(this, prenotazioniViewModel);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        lastFilterDocente = "";
        lastFilterCorso = "";
        lastFilterOra = 0;
        lastFilterGiorno = 0;

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.prenrip, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filtra) {
            builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_layout, null);
            builder.setView(dialogView);

            final EditText docente = dialogView.findViewById(R.id.docente_et);
            docente.setText(lastFilterDocente);
            final EditText corso = dialogView.findViewById(R.id.corso_et);
            corso.setText(lastFilterCorso);
            final Spinner ora = dialogView.findViewById(R.id.ora_spinner);
            ora.setSelection(lastFilterOra);
            final Spinner giorno = dialogView.findViewById(R.id.giorno_spinner);
            giorno.setSelection(lastFilterGiorno);
            dialogView.findViewById(R.id.stato_spinner).setVisibility(View.GONE);

            builder.setMessage("Filtra Ripetizioni");
            builder.setPositiveButton("Cerca", (dialogInterface, i) -> {
                lastFilterDocente = docente.getText();
                lastFilterCorso = corso.getText();
                lastFilterOra = (int) ora.getSelectedItemId();
                lastFilterGiorno = (int) giorno.getSelectedItemId();
                filterPrenotazioni();
                dialogInterface.dismiss();
            }).setNegativeButton("Cancella", (dialogInterface, i) -> {
                lastFilterDocente = "";
                lastFilterCorso = "";
                lastFilterOra = 0;
                lastFilterGiorno = 0;
                filterPrenotazioni();
                dialogInterface.dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == DetailsActivity.CODE_OK && requestCode == DetailsActivity.CODE_PRENOTA && data != null) {
            Bundle extras = data.getExtras();
            List<Prenotazione> storico = storicoViewModel.getPrenotazioni().getValue();
            List<Prenotazione> disponibili = prenotazioniViewModel.getPrenotazioni().getValue();
            if (extras != null && storico != null && disponibili != null) {
                Prenotazione prenotazione = (Prenotazione) extras.getSerializable(DetailsActivity.DELETED_ITEM);
                if (prenotazione != null) {
                    prenotazione.setStato(Stato.ATTIVA);
                    storico.add(prenotazione);
                    disponibili.remove(new Prenotazione(
                            prenotazione.getDocente(),
                            prenotazione.getCorso(),
                            null,
                            prenotazione.getSlot(),
                            prenotazione.getGiorno(),
                            null));
                    filterPrenotazioni();
                }
            }
        }
    }

    private void filterPrenotazioni() {
        StringBuilder filterBuilder = new StringBuilder();
        filterBuilder
            .append(lastFilterDocente.toString().replace(" ", "+")).append("_")
            .append(lastFilterCorso).append("_")
            .append(lastFilterOra).append("_")
            .append(lastFilterGiorno);
        adapter.getFilter().filter(filterBuilder);
    }
}