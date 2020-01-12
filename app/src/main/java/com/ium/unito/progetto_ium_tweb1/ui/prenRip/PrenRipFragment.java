package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
    public static View viewLayout;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

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
            builder = new AlertDialog.Builder(root.getContext());
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
            case R.id.filtra:
                builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_layout,null);
                builder.setView(dialogView);

                final EditText Docente = dialogView.findViewById(R.id.DocPText);
                final EditText Corso = dialogView.findViewById(R.id.CorPtext);
                final Spinner Ora = dialogView.findViewById(R.id.spinner2);
                final Spinner Giorno = dialogView.findViewById(R.id.spinner3);

                String[] ore = new String[] {
                        "15-16", "16-17", "17-18", "18-19"
                };
                String[] giorni = new String[] {
                        "Lun", "Mar", "Mer", "Gio" , "Ven"
                };

                Spinner s2 = (Spinner) dialogView.findViewById(R.id.spinner2);
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, ore);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s2.setAdapter(adapter2);

                Spinner s3 = (Spinner) dialogView.findViewById(R.id.spinner3);
                ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, giorni);
                adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s3.setAdapter(adapter3);

                builder.setMessage("Filtra Ripetizioni");

                builder.setPositiveButton("Cerca", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        filtraRipetizioni(Docente.getText().toString(),Corso.getText().toString(),Ora.getSelectedItem().toString(),Giorno.getSelectedItem().toString());
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
                break;
        }

        return false;
    }

    private void filtraRipetizioni(String docente, String corso, String ora, String giorno) {
        //CODICE FILTRAGGIO PRENOTAZIONI
    }
}