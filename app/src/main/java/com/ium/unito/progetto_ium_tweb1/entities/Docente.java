package com.ium.unito.progetto_ium_tweb1.entities;

import java.io.Serializable;
import java.util.Objects;

public class Docente implements Serializable {
    private String nome;
    private String cognome;

    public Docente(String nome, String cognome) {
        this.nome = nome;
        this.cognome = cognome;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    @Override
    public String toString() {
        return cognome + " " + nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Docente docente = (Docente) o;
        return Objects.equals(nome, docente.nome) &&
              Objects.equals(cognome, docente.cognome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, cognome);
    }
}
