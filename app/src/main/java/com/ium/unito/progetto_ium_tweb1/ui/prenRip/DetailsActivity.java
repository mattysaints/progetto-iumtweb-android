package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.model.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.model.Utente;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class DetailsActivity extends AppCompatActivity {
    public static final String PRENOTAZIONE_EXTRA = "prenotazioni.prenotazione";
    public static final String INDEX_PRENOTAZIONE_EXTRA = "prenotazioni.index_prenotazione";
    public static final String DELETED_ITEM_INDEX_EXTRA = "prenotazioni.deleted_item_index";
    public static final String DELETED_ITEM = "prenotazioni.deleted_item";
    public static final int CODE_PRENOTA = 1;
    public static final int CODE_OK = 0;

    private static final Gson gson = new Gson();

    private Prenotazione prenotazione;
    private TextView docente;
    private TextView corso;
    private TextView giorno;
    private TextView ora;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle("Ripetizione");

        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            throw new RuntimeException("Il bundle è null");
        final int position = bundle.getInt(INDEX_PRENOTAZIONE_EXTRA);
        prenotazione = (Prenotazione) bundle.getSerializable(PRENOTAZIONE_EXTRA);
        if (prenotazione == null)
            throw new RuntimeException("La prenotazione è null");

        preferences = getApplicationContext().getSharedPreferences("user_information", Context.MODE_PRIVATE);
        String user = preferences.getString("username", "");
        Utente utente = new Utente(user, null, null);
        prenotazione.setUtente(utente);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            HashMap<String, String> params = new HashMap<>();
            String[] ops = {"prenotare"};
            params.put("ops", gson.toJson(ops));
            Prenotazione[] prens = {prenotazione};
            params.put("prenotazioni", gson.toJson(prens));

            AsyncHttpRequest task = new AsyncHttpRequest();
            task.execute(new AsyncHttpRequest.Ajax(
                    AsyncHttpRequest.URL_OP_SU_PRENOTAZIONI, "POST", params));
            try {
                String result = task.get();
                if (Boolean.parseBoolean(result)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(DELETED_ITEM_INDEX_EXTRA, position);
                    resultIntent.putExtra(DELETED_ITEM, prenotazione);
                    setResult(CODE_OK, resultIntent);
                    Toast.makeText(getApplicationContext(), "Prenotazione Avvenuta con successo", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Errore durante la prenotazione ", Toast.LENGTH_LONG).show();
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            finish();
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
