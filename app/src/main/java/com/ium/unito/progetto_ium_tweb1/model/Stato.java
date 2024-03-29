package com.ium.unito.progetto_ium_tweb1.model;

import java.io.Serializable;

public enum Stato implements Serializable {
    //Per utilizzare il nome di una enumerazione (ad esempio per la coincidenza con i nomi nel database) usare il campo name
    //es. Slot.SLOT1.name --> LUN
    //Mentre per visualizzare il risultato nell'interfaccia grafica useremo il metodo toString

    EFFETTUATA,
    ATTIVA, //prenotata
    DISDETTA;

    @Override
    public String toString() {
        return "Stato: " + this.name();
    }

}
