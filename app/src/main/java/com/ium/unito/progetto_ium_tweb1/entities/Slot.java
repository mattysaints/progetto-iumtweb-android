package com.ium.unito.progetto_ium_tweb1.entities;

public enum Slot{
    //Per utilizzare il nome di una enumerazione (ad esempio per la coincidenza con i nomi nel database) usare il campo name
    //es. Slot.SLOT1.name --> LUN
    //Mentre per visualizzare il risultato nell'interfaccia grafica useremo il metodo toString

    SLOT1(15),
    SLOT2(16),
    SLOT3(17),
    SLOT4(18);

    private final int value;

    int getValue() {
        return value;
    }

    Slot(int newValue) {
        value = newValue;
    }

    @Override
    public String toString() {
        return "Orario: " + value;
    }
    //public int getValue() { return value; }

    public static Slot fromInt(int hour) {
        for (Slot ora : Slot.values()) {
            if (ora.value == hour) {
                return ora;
            }
        }
        return null;
    }

}
