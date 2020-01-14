package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.entities.Utente;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class DettailsActivity extends AppCompatActivity {

    private TextView docente;
    private TextView corso;
    private TextView giorno;
    private TextView ora;
    private CollapsingToolbarLayout toolbar_layout;
    private Gson gson = new Gson();
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettails);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout toolbar_layout = findViewById(R.id.toolbar_layout);
        toolbar_layout.setTitle("Ripetizione");

        final Prenotazione prenotazione = (Prenotazione) getIntent().getExtras().getSerializable("prenotazione");

        pref = getApplicationContext().getSharedPreferences("user_information", Context.MODE_PRIVATE);  //va in crash se fai il login e poi vai per prenotare
        String user = pref.getString("username","");
        Utente utente = new Utente(user, null, null);
        prenotazione.setUtente(utente);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            try {
                HashMap<String, String> params = new HashMap<>();
                //params.put("op", "prenotare");
                //params.put("prenotazioni", gson.toJson(prenotazione));
                String[] ops = {"prenotare"};
                params.put("ops", gson.toJson(ops));
                Prenotazione[] prens = {prenotazione};
                params.put("prenotazioni", gson.toJson(prens));

                AsyncTask<AsyncHttpRequest.Ajax, Void, String> task = new AsyncHttpRequest().execute(new AsyncHttpRequest.Ajax("http://10.0.2.2:8080/progetto_ium_tweb2/OpSuPrenotazioni", "POST", params));
                try {
                    String result = task.get();
                    if (Boolean.parseBoolean(result)) {
                        PrenRipFragment.delete(prenotazione);
                        Toast.makeText(getApplicationContext(), "Prenotazione Avvenuta con successo", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(getApplicationContext(), "Errore durante la prenotazione ", Toast.LENGTH_LONG).show();
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        docente = findViewById(R.id.docDettailsText);
        corso = findViewById(R.id.corDettailsText);
        giorno = findViewById(R.id.giorDettailsText);
        ora = findViewById(R.id.oraDettailsText);

        docente.setText(prenotazione.getDocente().toString());
        corso.setText(prenotazione.getCorso().getTitolo());
        giorno.setText(prenotazione.getGiorno().toString());
        ora.setText(prenotazione.getSlot().toString());
    }


}
