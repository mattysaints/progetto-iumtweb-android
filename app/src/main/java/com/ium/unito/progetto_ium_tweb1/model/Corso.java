package com.ium.unito.progetto_ium_tweb1.model;

import java.io.Serializable;
import java.util.Objects;

public class Corso implements Serializable {

    private String titolo;

    public Corso(String titolo) {
        this.titolo = titolo;
    }

    @Override
    public String toString() {
        return "Corso{" +
                "titoloC='" + titolo + '\'' +
                '}';
    }

    public String getTitolo() {
        return titolo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Corso corso = (Corso) o;
        return Objects.equals(titolo, corso.titolo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titolo);
    }
}