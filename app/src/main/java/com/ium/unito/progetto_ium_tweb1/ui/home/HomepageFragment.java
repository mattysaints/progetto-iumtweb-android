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
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.card.MaterialCardView;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.model.Giorno;
import com.ium.unito.progetto_ium_tweb1.model.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.model.Slot;
import com.ium.unito.progetto_ium_tweb1.model.Stato;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Fragment della homepage
 */
public class HomepageFragment extends Fragment {
    private static final Calendar calendar = Calendar.getInstance();

    private StoricoViewModel storicoViewModel;

    private TextView userTextView;
    private TextView numPrenotazioni;
    private TextView numPrenotazioniEffettuate;
    private TextView numPrenotazioniAttive;
    private TextView numPrenotazioniDisdette;
    private TextView numCorsi;

    private MaterialCardView cardInfoPrentazioni;
    private RelativeLayout cardProssimaPrenotazione;
    private TextView infoProssimaPrenotazione;
    private ImageView image;
    private TextView docenteTextView;
    private TextView corsoTextView;
    private TextView giornoTextView;
    private TextView oraTextView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        SharedPreferences preferences = null;
        String username = null;
        boolean ospite = false;

        if (activity != null) {
            preferences = activity.getSharedPreferences("user_information", AppCompatActivity.MODE_PRIVATE);
            storicoViewModel = ViewModelProviders.of(activity).get(StoricoViewModel.class);
        }
        if (preferences != null) {
            storicoViewModel.setPreferences(preferences);
            username = preferences.getString("username", "Ospite");
            ospite = preferences.getBoolean("ospite", false);
        }

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        infoProssimaPrenotazione = root.findViewById(R.id.info_prossima_prenotazione);
        numPrenotazioni = root.findViewById(R.id.num_pren);
        numPrenotazioniAttive = root.findViewById(R.id.num_attive);
        numPrenotazioniEffettuate = root.findViewById(R.id.num_effettuate);
        numPrenotazioniDisdette = root.findViewById(R.id.num_disdette);
        numCorsi = root.findViewById(R.id.num_corsi);
        image = root.findViewById(R.id.image_books);
        docenteTextView = root.findViewById(R.id.docente_text_view);
        corsoTextView = root.findViewById(R.id.corso_text_view);
        giornoTextView = root.findViewById(R.id.giorno_text_view);
        oraTextView = root.findViewById(R.id.ora_text_view);
        cardProssimaPrenotazione = root.findViewById(R.id.card_prossima_prenotazione);
        cardInfoPrentazioni = root.findViewById(R.id.info_prenotazioni);
        userTextView = root.findViewById(R.id.user_textView);
        StringBuilder user = new StringBuilder(userTextView.getText().toString());
        user.append(" ").append(username);
        userTextView.setText(user);


        if (!ospite) {
            storicoViewModel.getPrenotazioni().observe(this, this::setInfo);
        } else {
            infoProssimaPrenotazione.setText(getString(R.string.ospite_welcome));
            infoProssimaPrenotazione.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            cardProssimaPrenotazione.setVisibility(View.INVISIBLE);
            cardInfoPrentazioni.setVisibility(View.INVISIBLE);
        }
        return root;
    }

    /**
     * Dato lo stato corrente delle prenotazioni, calcola le statistiche e fa update della UI
     *
     * @param prenotazioni: prenotazioni correnti
     */
    private void setInfo(List<Prenotazione> prenotazioni) {
        Supplier<Stream<Prenotazione>> supplier = prenotazioni::parallelStream;

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
            infoProssimaPrenotazione.setText(getString(R.string.no_prenotazioni));
            cardProssimaPrenotazione.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Calcola il prossimo giorno in cui potrebbe esserci una prenotazione
     * @return giorno
     */
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

    /**
     * Calcola il prossimo slot in cui potrebbe esserci una prenotazione
     * @return slot
     */
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