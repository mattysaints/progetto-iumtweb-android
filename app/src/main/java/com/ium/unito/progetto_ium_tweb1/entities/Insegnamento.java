package com.ium.unito.progetto_ium_tweb1.entities;

import java.io.Serializable;
import java.util.Objects;

public class Insegnamento implements Serializable {

    private Docente doc;
    private Corso corso;

    public Insegnamento(Docente doc, Corso corso) {
        this.doc = doc;
        this.corso = corso;
    }

    public Docente getDoc() {
        return doc;
    }

    public Corso getCorso() {
        return corso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Insegnamento that = (Insegnamento) o;
        return Objects.equals(doc, that.doc) &&
              Objects.equals(corso, that.corso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doc, corso);
    }
}
