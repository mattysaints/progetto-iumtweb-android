package com.ium.unito.progetto_ium_tweb1.entities;

public enum Giorno {
    //Per utilizzare il nome di una enumerazione (ad esempio per la coincidenza con i nomi nel database) usare il campo name
    //es. Slot.SLOT1.name --> LUN
    //Mentre per visualizzare il risultato nell'interfaccia grafica useremo il metodo toString

    LUN("Lunedì"),
    MAR("Martedì"),
    MER("Mercoledì"),
    GIO("Giovedì"),
    VEN("Venerdì");

    @Override
    public String toString() {
        return value;
    }

    private final String value;

    Giorno(String newValue) {
        value = newValue;
    }

    public static Giorno fromString(String text) {
        for (Giorno giorno : Giorno.values()) {
            if (giorno.toString().equalsIgnoreCase(text)) {
                return giorno;
            }
        }
        return null;
    }


}
