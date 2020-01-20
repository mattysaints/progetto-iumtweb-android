package com.ium.unito.progetto_ium_tweb1.ui.prenRip;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ium.unito.progetto_ium_tweb1.model.Prenotazione;
import com.ium.unito.progetto_ium_tweb1.utils.AsyncHttpRequest;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * ViewModel per il fragment di ripetizioni disponibili
 */
public class PrenotazioniViewModel extends ViewModel {
    private static final Gson gson = new Gson();

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
     * Carica la lista di ripetizioni disponibili tramite una richiesta http asincrona
     */
    private void loadPrenotazioni() {
        AsyncHttpRequest task = new AsyncHttpRequest();
        task.execute(new AsyncHttpRequest.Ajax(AsyncHttpRequest.URL_RIPETIZIONI_DISPONIBILI, "POST", null));
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
     * Elimina una ripetizione dato l'oggetto
     *
     * @param prenotazione: prenotazione da eliminare
     */
    public void deletePrenotazione(Prenotazione prenotazione) {
        prenotazioni.getValue().remove(prenotazione);
    }

    /**
     * Elimina una ripetizione data la posizione
     *
     * @param position: posizione dell'oggetto da eliminare
     */
    public void deletePrenotazione(int position) {
        prenotazioni.getValue().remove(position);
    }
}