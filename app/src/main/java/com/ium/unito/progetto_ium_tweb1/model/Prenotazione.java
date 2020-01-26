package com.ium.unito.progetto_ium_tweb1.model;

import java.io.Serializable;
import java.util.Objects;

public class Prenotazione implements Serializable {
    private Docente docente;
    private Corso corso;
    private Utente utente;
    private Slot slot;
    private Giorno giorno;
    private Stato stato;

    public Prenotazione(Docente docente, Corso corso, Utente utente, Slot slot, Giorno giorno, Stato stato) {
        this.docente = docente;
        this.corso = corso;
        this.utente = utente;
        this.slot = slot;
        this.giorno = giorno;
        this.stato = stato;
    }

    public Docente getDocente() { return docente; }

    public Corso getCorso() { return corso; }

    public Utente getUtente() { return utente; }

    public Slot getSlot() { return slot; }

    public Giorno getGiorno() { return giorno; }

    public Stato getStato() { return stato; }

    @Override
    public String toString() {
        return "Prenotazione{" +
                "docente=" + docente +
                ", corso=" + corso +
                ", utente=" + utente +
                ", slot=" + slot +
                ", giorno=" + giorno +
                ", stato=" + stato +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Prenotazione that = (Prenotazione) o;
        return Objects.equals(docente, that.docente) &&
              Objects.equals(corso, that.corso) &&
              Objects.equals(utente, that.utente) &&
              slot == that.slot &&
              giorno == that.giorno &&
              stato == that.stato;
    }

    @Override
    public int hashCode() {
        return Objects.hash(docente, slot, giorno, stato);
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public void setStato(Stato newStato) {
        this.stato = newStato;
    }
}
