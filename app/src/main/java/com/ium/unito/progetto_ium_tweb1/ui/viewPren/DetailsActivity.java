package com.ium.unito.progetto_ium_tweb1.ui.viewPren;

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
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class DetailsActivity extends AppCompatActivity {

   public static final int ATTIVA = 0;
   public static final int DISDETTA = 1;
   public static final int EFFETTUATA = 2;
   private TextView docente;
   private TextView corso;
   private TextView giorno;
   private TextView ora;
   private RadioGroup stato;
   private Prenotazione prenotazione;
   private Gson gson = new Gson();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_dettagli_prenotazione);
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      CollapsingToolbarLayout toolbar_layout = findViewById(R.id.toolbar_layout);
      toolbar_layout.setTitle("Prenotazione");

      prenotazione = (Prenotazione) getIntent().getExtras().getSerializable("prenotazione");

       FloatingActionButton fab = findViewById(R.id.fab); //per salvare lo stato della prenotazione
       fab.setOnClickListener(view -> {
           String op = "disdire";
           switch (stato.getCheckedRadioButtonId()) {
               case R.id.radioButtonAttiva:
                   Toast.makeText(getApplicationContext(), "Lo tua prenotazione è già attiva", Toast.LENGTH_LONG).show();
                   break;
               case R.id.radioButtonEffettuata:
                   op = "effettuare";
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

                       } else {
                           Toast.makeText(getApplicationContext(), "Errore durante il cambio di stato della prenotazione ", Toast.LENGTH_LONG).show();
                       }
                   } catch (ExecutionException | InterruptedException e) {
                       e.printStackTrace();
                   }
                   onBackPressed();
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
