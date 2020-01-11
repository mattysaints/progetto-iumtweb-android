package com.ium.unito.progetto_ium_tweb1.entities;

import java.io.Serializable;
import java.util.Objects;

public class Utente implements Serializable {

    private String account;
    private String password;
    private Boolean admin;

    public Utente(String account, String password, Boolean admin) {
        this.account = account;
        this.password = password;
        this.admin = admin;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public Boolean isAdmin() {
        return admin;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Utente{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", admin='" + admin + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Utente utente = (Utente) o;
        return admin == utente.admin &&
              Objects.equals(account, utente.account) &&
              Objects.equals(password, utente.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account);
    }
}
