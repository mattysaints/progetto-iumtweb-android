package com.ium.unito.progetto_ium_tweb1.ui.viewPren;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.model.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.model.Stato;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class DetailsActivity extends AppCompatActivity {
    public static final String STATO_EXTRA = "storico.stato";
    public static final String PRENOTAZIONE_EXTRA = "storico.prenotazione";
    public static final int CODE_STORICO = 2;
    public static final int CODE_OK = 0;

    private static final Gson gson = new Gson();

    private TextView docente;
    private TextView corso;
    private TextView giorno;
    private TextView ora;
    private RadioGroup stato;
    private Prenotazione prenotazione;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_prenotazione);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle("Prenotazione");

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            prenotazione = (Prenotazione) extra.getSerializable(PRENOTAZIONE_EXTRA);
        }

        FloatingActionButton fab = findViewById(R.id.fab); //per salvare lo stato della prenotazione
        fab.setOnClickListener(view -> {
            boolean result = false;
            String op = "disdire";
            Stato newStato = Stato.DISDETTA;
            switch (stato.getCheckedRadioButtonId()) {
                case R.id.radioButtonAttiva:
                    Toast.makeText(getApplicationContext(), "Lo tua prenotazione è già attiva", Toast.LENGTH_LONG).show();
                    newStato = Stato.ATTIVA;
                    break;
                case R.id.radioButtonEffettuata:
                    op = "effettuare";
                    newStato = Stato.EFFETTUATA;
                case R.id.radioButtonDisdetta:
                    HashMap<String, String> params = new HashMap<>();
                    String[] ops = {op};
                    params.put("ops", gson.toJson(ops));
                    Prenotazione[] prens = {prenotazione};
                    params.put("prenotazioni", gson.toJson(prens));

                    AsyncTask<AsyncHttpRequest.Ajax, Void, String> task = new AsyncHttpRequest().execute(new AsyncHttpRequest.Ajax("http://10.0.2.2:8080/progetto_ium_tweb2/OpSuPrenotazioni", "POST", params));
                    try {
                        if (task.get().equals("true")) {
                            Toast.makeText(getApplicationContext(), "Stato della prenotazione cambiato con successo", Toast.LENGTH_LONG).show();
                            result = true;
                        } else {
                            Toast.makeText(getApplicationContext(), "Errore durante il cambio di stato della prenotazione ", Toast.LENGTH_LONG).show();
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
            }
            if (result) {
                Intent res = new Intent();
                res.putExtra(PRENOTAZIONE_EXTRA, prenotazione);
                res.putExtra(STATO_EXTRA, newStato);
                setResult(CODE_OK, res);
            }
            finish();
        });

        docente = findViewById(R.id.docDettailsText);
        corso = findViewById(R.id.corDettailsText);
        giorno = findViewById(R.id.giorDettailsText);
        ora = findViewById(R.id.oraDettailsText);
        stato = findViewById(R.id.statoDetailsRadio);

        assert prenotazione != null;
        docente.setText(prenotazione.getDocente().toString());
        corso.setText(prenotazione.getCorso().getTitolo());
        giorno.setText(prenotazione.getGiorno().toString());
        ora.setText(prenotazione.getSlot().toString());
        switch (prenotazione.getStato()) {
            case EFFETTUATA:
                stato.check(R.id.radioButtonEffettuata);
                for (int i = 0; i < 3; i++) {
                    stato.getChildAt(i).setEnabled(false);
                }
                fab.setEnabled(false);
                break;
            case ATTIVA:
                stato.check(R.id.radioButtonAttiva);
                for (int i = 0; i < 3; i++) {
                    stato.getChildAt(i).setEnabled(true);
                }
                fab.setEnabled(true);
                break;
            case DISDETTA:
                stato.check(R.id.radioButtonDisdetta);
                for (int i = 0; i < 3; i++) {
                    stato.getChildAt(i).setEnabled(false);
                }
                fab.setEnabled(false);
                break;
        }

        stato.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButtonEffettuata:
                    break;
                case R.id.radioButtonAttiva:
                    break;
                case R.id.radioButtonDisdetta:
                    break;
            }
        });
    }
}
