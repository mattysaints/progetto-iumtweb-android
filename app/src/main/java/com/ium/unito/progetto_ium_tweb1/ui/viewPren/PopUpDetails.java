package com.ium.unito.progetto_ium_tweb1.ui.viewPren;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.ui.prenRip.PrenRipFragment;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttp;

import java.util.HashMap;

public class PopUpDetails extends AppCompatActivity {

    private TextView docente;
    private TextView corso;
    private TextView giorno;
    private TextView ora;
    private RadioGroup stato;
    private CollapsingToolbarLayout toolbar_layout;
    private Gson gson = new Gson();
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_prenotazione);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout toolbar_layout = findViewById(R.id.toolbar_layout);
        toolbar_layout.setTitle("Prenotazione");

        final Prenotazione prenotazione = (Prenotazione) getIntent().getExtras().getSerializable("prenotazione");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab); //per disdire la prenotazione
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    HashMap<String, String> params = new HashMap<>();
                    String[] ops = {"disdire"};
                    params.put("ops", gson.toJson(ops));
                    Prenotazione[] prens = {prenotazione};
                    params.put("prenotazioni", gson.toJson(prens));

                    AsyncTask<AsyncHttp.Ajax, Void, String> task = new AsyncHttp().execute(new AsyncHttp.Ajax("http://10.0.2.2:8080/progetto_ium_tweb2/OpSuPrenotazioni", "POST", params));
                    if(task.get().equals("true")) {
                        Toast.makeText(getApplicationContext(), "Prenotazione disdetta con successo", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "La prenotazione non può essere disdetta", Toast.LENGTH_SHORT).show();
                    }

                    onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
            case effettuata:
                stato.check(R.id.radioButtonEffettuata);
                stato.setEnabled(false);
                break;
            case attiva:
                stato.check(R.id.radioButtonAttiva);
                stato.setEnabled(true);
                break;
            case disdetta:
                stato.check(R.id.radioButtonDisdetta);
                stato.setEnabled(false);
                break;
        }

        stato.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonEffettuata:
                        break;
                    case R.id.radioButtonAttiva:
                        break;
                    case R.id.radioButtonDisdetta:
                        break;
                }
            }
        });
    }


}
