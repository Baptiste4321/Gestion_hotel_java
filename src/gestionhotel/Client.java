package gestionhotel;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class Client {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private int numeroClient;

    public Client(String nom, String prenom, String email, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.numeroClient = COUNTER.getAndIncrement();
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getNumeroClient() {
        return numeroClient;
    }

    public String getNomComplet() {
        return String.format("%s %s", prenom, nom).trim();
    }

    public boolean validerEmail() {
        if (email == null) return false;
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(regex).matcher(email).matches();
    }

    @Override
    public String toString() {
        return String.format("Client #%d - %s - Email: %s - Tel: %s", numeroClient, getNomComplet(), email, telephone);
    }
}

