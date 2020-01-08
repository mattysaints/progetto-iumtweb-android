package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;

import java.io.PrintStream;
import java.io.Serializable;

public class DettailsActivity extends AppCompatActivity {

    private Prenotazione prenotazione;
    private TextView docente;
    private TextView corso;
    private TextView giorno;
    private TextView ora;
    private CollapsingToolbarLayout toolbar_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettails);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout toolbar_layout = findViewById(R.id.toolbar_layout);
        toolbar_layout.setTitle("Ripetizione");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        docente = findViewById(R.id.docDettails);
        corso = findViewById(R.id.corDettails);
        giorno = findViewById(R.id.giorDettails);
        ora = findViewById(R.id.oraDettails);

        prenotazione = (Prenotazione) getIntent().getExtras().getSerializable("prenotazione");
        assert prenotazione != null;
        StringBuilder docenteNC = new StringBuilder(prenotazione.getDocente().getNome());
        docenteNC.append(prenotazione.getDocente().getCognome());
        docente.setText(docenteNC);
        corso.setText(prenotazione.getCorso().getTitolo());
    }




}
