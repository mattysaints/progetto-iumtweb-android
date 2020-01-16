package com.ium.unito.progetto_ium_tweb1.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ium.unito.progetto_ium_tweb1.R;
import com.ium.unito.progetto_ium_tweb1.entities.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.entities.Stato;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class HomeFragment extends Fragment {
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        preferences = getActivity().getSharedPreferences("user_information", AppCompatActivity.MODE_PRIVATE);

        userTextView = root.findViewById(R.id.user_textView);
        numPrenotazioni = root.findViewById(R.id.num_pren);
        numPrenotazioniAttive = root.findViewById(R.id.num_attive);
        numPrenotazioniEffettuate = root.findViewById(R.id.num_effettuate);
        numPrenotazioniDisdette = root.findViewById(R.id.num_disdette);
        numCorsi = root.findViewById(R.id.num_corsi);
        userTextView.setText(preferences.getString("username", "Ospite"));

        String url = "http://10.0.2.2:8080/progetto_ium_tweb2/StoricoPrenotazioni";
        Map<String, String> params = new HashMap<>();
        try {
            String result = new AsyncHttpRequest().execute(new AsyncHttpRequest.Ajax(url, "POST", params)).get();
            prenotazioniUtente = gson.fromJson(result, new TypeToken<List<Prenotazione>>() {
            }.getType());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        setStatistics();
        return root;
    }

    private void setStatistics() {
        Supplier<Stream<Prenotazione>> supplier = () -> prenotazioniUtente.parallelStream();
        long countPrenotazioniEffettuate = supplier.get().filter(p -> p.getStato() == Stato.EFFETTUATA).count();
        long countPrenotazioniAttive = supplier.get().filter(p -> p.getStato() == Stato.ATTIVA).count();
        long countPrenotazioniDisdette = supplier.get().filter(p -> p.getStato() == Stato.DISDETTA).count();
        long countPrenotazioni = supplier.get().count();
        long countCorsi = supplier.get().map(Prenotazione::getCorso).distinct().count();
        Prenotazione prossima = supplier.get().max((p1, p2) -> p1.getGiorno().compareTo(p2.getGiorno())).get();

        numPrenotazioni.setText(countPrenotazioni + "");
        numPrenotazioniEffettuate.setText(countPrenotazioniAttive + "");
        numPrenotazioniAttive.setText(countPrenotazioniEffettuate + "");
        numPrenotazioniDisdette.setText(countPrenotazioniDisdette + "");
        numCorsi.setText(countCorsi + "");
    }

}