package gestionhotel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.Month;

// classe pour gerer les reservations des clients
public class Reservation {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private int numeroReservation;
    private Client client;
    private Chambre chambre;
    private String dateDebut;
    private String dateFin;
    private ArrayList<Service> services;
    private String statut;

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");

    // cree une nouvelle resa et met la chambre en occupé
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

    // calcule le nb de nuits entre les 2 dates
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

    // prix de la chambre sans les services
    public double calculerPrixChambre() {
        int nuits = calculerNombreNuits();
        if (chambre == null) return 0.0;
        return chambre.calculerPrix(nuits);
    }

    // total des services commandés
    public double calculerPrixServices() {
        return services.stream().mapToDouble(Service::getPrix).sum();
    }

    // calcul du prix avec les reductions
    public double calculerPrixTotal() {
        double total = calculerPrixChambre() + calculerPrixServices();

        // reduc basse saison 20%
        if (estEnBasseSaison()) {
            total = total * 0.80;
        }
        // plus de 7 nuits = 10% de reduc
        if (calculerNombreNuits() > 7) {
            total = total * 0.90;
        }
        // les vip ont 5% en plus
        if (client.isVip()) {
            total = total * 0.95;
        }
        return total;
    }
    // on a mis janvier fevrier et novembre en basse saison
    public boolean estEnBasseSaison() {
        try {
            LocalDate debut = LocalDate.parse(dateDebut, FORMAT);
            Month mois = debut.getMonth();
            return mois == Month.JANUARY || mois == Month.FEBRUARY || mois == Month.NOVEMBER;
        } catch (Exception e) {
            return false;
        }
    }

    // ajoute un service a la resa
    public void ajouterService(Service s) {
        if (s != null) services.add(s);
    }

    // annule la resa et libere la chambre
    public void annuler() {
        this.statut = "Annulée";
        if (this.chambre != null) this.chambre.setOccupee(false);
    }

    // termine la resa (checkout)
    public void terminer() {
        this.statut = "Terminée";
        if (this.chambre != null) this.chambre.setOccupee(false);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Réservation #%d - Client: %s - Chambre: %s\n", numeroReservation, client.getNomComplet(), (chambre == null ? "-" : String.valueOf(chambre.getNumero()))));
        sb.append(String.format("Période: %s -> %s (%d nuits) - Statut: %s\n", dateDebut, dateFin, calculerNombreNuits(), statut));
        if (estEnBasseSaison()) {
            sb.append(">>> PROMOTION BASSE SAISON APPLIQUÉE (-20%) <<<\n");
        }
        double prixCh = calculerPrixChambre();
        double prixSv = calculerPrixServices();
        double prixTot = calculerPrixTotal();
        sb.append(String.format("Prix chambre: %.2f %s - Prix services: %.2f %s - Total: %.2f %s\n", 
                prixCh, (prixCh <= 1 ? "euro" : "euros"),
                prixSv, (prixSv <= 1 ? "euro" : "euros"),
                prixTot, (prixTot <= 1 ? "euro" : "euros")));
        if (!services.isEmpty()) {
            sb.append("Services:\n");
            for (Service s : services) sb.append(" - ").append(s.toString()).append("\n");
        }
        return sb.toString();
    }

}

