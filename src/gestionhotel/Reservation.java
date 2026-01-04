package gestionhotel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Reservation {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private int numeroReservation;
    private Client client;
    private Chambre chambre;
    private String dateDebut; // format jj/mm/aaaa
    private String dateFin;
    private ArrayList<Service> services;
    private String statut; // "En cours", "Confirmée", "Annulée", "Terminée"

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");

    public Reservation(Client client, Chambre chambre, String dateDebut, String dateFin) {
        this.numeroReservation = COUNTER.getAndIncrement();
        this.client = client;
        this.chambre = chambre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.services = new ArrayList<>();
        this.statut = "Confirmée";
        if (this.chambre != null) this.chambre.setOccupee(true);
    }

    public int getNumeroReservation() {
        return numeroReservation;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Chambre getChambre() {
        return chambre;
    }

    public void setChambre(Chambre chambre) {
        if (this.chambre != null) this.chambre.setOccupee(false);
        this.chambre = chambre;
        if (this.chambre != null) this.chambre.setOccupee(true);
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int calculerNombreNuits() {
        try {
            LocalDate debut = LocalDate.parse(dateDebut, FORMAT);
            LocalDate fin = LocalDate.parse(dateFin, FORMAT);
            long nuits = ChronoUnit.DAYS.between(debut, fin);
            return (int) Math.max(nuits, 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public double calculerPrixChambre() {
        int nuits = calculerNombreNuits();
        if (chambre == null) return 0.0;
        return chambre.calculerPrix(nuits);
    }

    public double calculerPrixServices() {
        return services.stream().mapToDouble(Service::getPrix).sum();
    }

    public double calculerPrixTotal() {
        return calculerPrixChambre() + calculerPrixServices();
    }

    public void ajouterService(Service s) {
        if (s != null) services.add(s);
    }

    public void annuler() {
        this.statut = "Annulée";
        if (this.chambre != null) this.chambre.setOccupee(false);
    }

    public void terminer() {
        this.statut = "Terminée";
        if (this.chambre != null) this.chambre.setOccupee(false);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Réservation #%d - Client: %s - Chambre: %s\n", numeroReservation, client.getNomComplet(), (chambre == null ? "-" : String.valueOf(chambre.getNumero()))));
        sb.append(String.format("Période: %s -> %s (%d nuits) - Statut: %s\n", dateDebut, dateFin, calculerNombreNuits(), statut));
        sb.append(String.format("Prix chambre: %.2f€ - Prix services: %.2f€ - Total: %.2f€\n", calculerPrixChambre(), calculerPrixServices(), calculerPrixTotal()));
        if (!services.isEmpty()) {
            sb.append("Services:\n");
            for (Service s : services) sb.append(" - ").append(s.toString()).append("\n");
        }
        return sb.toString();
    }
}

