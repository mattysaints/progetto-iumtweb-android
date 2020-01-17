package com.ium.unito.progetto_ium_tweb1.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Giorno;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.entities.Slot;
import com.ium.unito.progetto_ium_tweb1.entities.Stato;
import com.ium.unito.progetto_ium_tweb1.entities.Utente;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class HomeFragment extends Fragment {
    private static final Calendar calendar = Calendar.getInstance();
    private static final Gson gson = new Gson();

    private HomeViewModel homeViewModel;
    private SharedPreferences preferences;
    private List<Prenotazione> prenotazioniUtente;

    private TextView userTextView;
    private TextView numPrenotazioni;
    private TextView numPrenotazioniEffettuate;
    private TextView numPrenotazioniAttive;
    private TextView numPrenotazioniDisdette;
    private TextView numCorsi;

    private RelativeLayout cardProssimaPrenotazione;
    private TextView infoProssimaPrenotazione;
    private ImageView image;
    private TextView docenteTextView;
    private TextView corsoTextView;
    private TextView giornoTextView;
    private TextView oraTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        preferences = getActivity().getSharedPreferences("user_information", AppCompatActivity.MODE_PRIVATE);

        userTextView = root.findViewById(R.id.user_textView);
        userTextView.setText(preferences.getString("username", "Ospite"));
        numPrenotazioni = root.findViewById(R.id.num_pren);
        numPrenotazioniAttive = root.findViewById(R.id.num_attive);
        numPrenotazioniEffettuate = root.findViewById(R.id.num_effettuate);
        numPrenotazioniDisdette = root.findViewById(R.id.num_disdette);
        numCorsi = root.findViewById(R.id.num_corsi);
        cardProssimaPrenotazione = root.findViewById(R.id.card_prossima_prenotazione);
        infoProssimaPrenotazione = root.findViewById(R.id.info_prossima_prenotazione);
        image = root.findViewById(R.id.image_books);
        docenteTextView = root.findViewById(R.id.docente_text_view);
        corsoTextView = root.findViewById(R.id.corso_text_view);
        giornoTextView = root.findViewById(R.id.giorno_text_view);
        oraTextView = root.findViewById(R.id.ora_text_view);

        String url = "http://10.0.2.2:8080/progetto_ium_tweb2/StoricoPrenotazioni";
        Map<String, String> params = new HashMap<>();
        Utente utente = new Utente(preferences.getString("username", "ospite"), null, null);
        params.put("utente", gson.toJson(utente, Utente.class));
        try {
            String result = new AsyncHttpRequest().execute(new AsyncHttpRequest.Ajax(url, "POST", params)).get();
            prenotazioniUtente = gson.fromJson(result, new TypeToken<List<Prenotazione>>() {
            }.getType());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        setInfo();
        return root;
    }

    private void setInfo() {
        Supplier<Stream<Prenotazione>> supplier = () -> prenotazioniUtente.parallelStream();
        long countPrenotazioniEffettuate = supplier.get().filter(p -> p.getStato() == Stato.EFFETTUATA).count();
        long countPrenotazioniAttive = supplier.get().filter(p -> p.getStato() == Stato.ATTIVA).count();
        long countPrenotazioniDisdette = supplier.get().filter(p -> p.getStato() == Stato.DISDETTA).count();
        long countPrenotazioni = supplier.get().count();
        long countCorsi = supplier.get().map(Prenotazione::getCorso).distinct().count();

        Giorno nextGiorno = nextGiorno();
        Slot nextSlot = nextSlot();
        Optional<Prenotazione> optionalPrenotazione = supplier.get()
                .filter(p -> p.getStato() == Stato.ATTIVA
                        && p.getGiorno().compareTo(nextGiorno) >= 0
                        && p.getSlot().compareTo(nextSlot) >= 0)
                .min((p1, p2) -> {
                    int compareGiorno = p1.getGiorno().compareTo(p2.getGiorno());
                    return compareGiorno == 0
                            ? p1.getSlot().compareTo(p2.getSlot())
                            : compareGiorno;
                });

        numPrenotazioni.setText(String.valueOf(countPrenotazioni));
        numPrenotazioniEffettuate.setText(String.valueOf(countPrenotazioniEffettuate));
        numPrenotazioniAttive.setText(String.valueOf(countPrenotazioniAttive));
        numPrenotazioniDisdette.setText(String.valueOf(countPrenotazioniDisdette));
        numCorsi.setText(String.valueOf(countCorsi));

        if (optionalPrenotazione.isPresent()) {
            Prenotazione prossima = optionalPrenotazione.get();
            image.setImageResource(R.drawable.info);
            docenteTextView.setText(prossima.getDocente().toString());
            corsoTextView.setText(prossima.getCorso().getTitolo());
            oraTextView.setText(prossima.getSlot().toString());
            giornoTextView.setText(prossima.getGiorno().toString());
        } else {
            infoProssimaPrenotazione.setText("Non hai prenotazioni attive");
            cardProssimaPrenotazione.setVisibility(View.INVISIBLE);
        }

    }

    private Giorno nextGiorno() {
        calendar.setTime(new Date());
        int giornoIndex = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        Giorno giorno;
        if (giornoIndex > 5)
            giorno = Giorno.LUN;
        else
            giorno = Giorno.values()[giornoIndex];

        return giorno;
    }

    private Slot nextSlot() {
        calendar.setTime(new Date());
        int ora = calendar.get(Calendar.HOUR_OF_DAY);

        Slot slot;
        if (ora < 15 || ora > 19)
            slot = Slot.SLOT1;
        else
            slot = Slot.fromInt(ora);
        return slot;
    }

}