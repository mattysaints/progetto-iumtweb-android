package com.ium.unito.progetto_ium_tweb1.ui.home;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ium.unito.progetto_ium_tweb1.model.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.model.Utente;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * ViewModel condiviso dal fragment della homepage e dello storico
 */
public class StoricoViewModel extends ViewModel {
    private static final Gson gson = new Gson();

    private SharedPreferences preferences;
    private MutableLiveData<List<Prenotazione>> prenotazioni;

    /**
     * Restituisce il mutablelivedata delle prenotazioni
     *
     * @return prenotazioni
     */
    public MutableLiveData<List<Prenotazione>> getPrenotazioni() {
        if (prenotazioni == null) {
            prenotazioni = new MutableLiveData<>();
            loadPrenotazioni();
        }
        return prenotazioni;
    }

    /**
     * Carica le prenotazioni mediante una richiesta http asincrona
     */
    private void loadPrenotazioni() {
        Map<String, String> params = new HashMap<>();
        Utente utente = new Utente(preferences.getString("username", "ospite"), null, null);
        params.put("utente", gson.toJson(utente, Utente.class));

        AsyncHttpRequest task = new AsyncHttpRequest();
        task.execute(new AsyncHttpRequest.Ajax(AsyncHttpRequest.URL_STORICO_PRENOTAZIONI, "POST", params));
        String result = null;
        try {
            result = task.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        prenotazioni.setValue(gson.fromJson(result, new TypeToken<List<Prenotazione>>() {
        }.getType()));
    }

    /**
     * Inizializza le SharedPreferences
     *
     * @param preferences: preferences
     */
    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }
}