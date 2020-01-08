package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpPost;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DettailsActivity extends AppCompatActivity {

    private TextView docente;
    private TextView corso;
    private TextView giorno;
    private TextView ora;
    private CollapsingToolbarLayout toolbar_layout;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettails);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout toolbar_layout = findViewById(R.id.toolbar_layout);
        toolbar_layout.setTitle("Ripetizione");

        final Prenotazione prenotazione = (Prenotazione) getIntent().getExtras().getSerializable("prenotazione");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    HashMap<String, String> params = new HashMap<>();
                    params.put("op", "prenotare");
                    params.put("prenotazioni", gson.toJson(prenotazione));

                    AsyncTask<Void, Void, String> task = new AsyncHttpPost("http://10.0.2.2:8080/progetto_ium_tweb2/OpSuPrenotazioni", params);
                    task.execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        docente = findViewById(R.id.docDettailsText);
        corso = findViewById(R.id.corDettailsText);
        giorno = findViewById(R.id.giorDettailsText);
        ora = findViewById(R.id.oraDettailsText);

        StringBuilder docenteNC = new StringBuilder(prenotazione.getDocente().getNome());
        docenteNC.append(" ");
        docenteNC.append(prenotazione.getDocente().getCognome());
        docente.setText(docenteNC);
        corso.setText(prenotazione.getCorso().getTitolo());
        switch (prenotazione.getGiorno().toString()) {
            case "lun":
                giorno.setText("Lunedì");
            case "mar":
                giorno.setText("Martedì");
            case "mer":
                giorno.setText("Mercoledì");
            case "gio":
                giorno.setText("Giovedì");
            case "ven":
                giorno.setText("Venerdì");
        }

        switch (prenotazione.getSlot().toString()) {
            case "Orario: 15":
                ora.setText("15-16");
            case "Orario: 16":
                ora.setText("16-17");
            case "Orario: 17":
                ora.setText("17-18");
            case "Orario: 18":
                ora.setText("18-19");
        }
    }


}
