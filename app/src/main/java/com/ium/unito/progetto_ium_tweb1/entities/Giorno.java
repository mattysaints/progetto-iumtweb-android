package com.ium.unito.progetto_ium_tweb1.entities;

enum Giorno {
    //Per utilizzare il nome di una enumerazione (ad esempio per la coincidenza con i nomi nel database) usare il campo name
    //es. Slot.SLOT1.name --> LUN
    //Mentre per visualizzare il risultato nell'interfaccia grafica useremo il metodo toString

    LUN("lun"),
    MAR("mar"),
    MER("mer"),
    GIO("gio"),
    VEN("ven");

    @Override
    public String toString() {
        return this.name().toLowerCase();
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
