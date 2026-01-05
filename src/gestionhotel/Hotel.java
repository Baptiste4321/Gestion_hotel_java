package gestionhotel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Hotel {
    private String nom;
    private String adresse;
    private ArrayList<Chambre> chambres;
    private ArrayList<Client> clients;
    private ArrayList<Reservation> reservations;
    private ArrayList<Service> servicesDisponibles;

    public Hotel(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
        this.chambres = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.servicesDisponibles = new ArrayList<>();
    }
    public ArrayList<Chambre> rechercherChambresMultiCriteres(String type, double prixMax) {
        String t = type == null ? "" : type.toLowerCase();
        return chambres.stream()
                .filter(c -> c.getType().toLowerCase().contains(t))
                .filter(c -> c.getPrixParNuit() <= prixMax)
                .filter(c -> !c.isOccupee())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Gestion des chambres
    public void ajouterChambre(Chambre c) {
        if (c == null) return;
        if (rechercherChambre(c.getNumero()) != null) {
            System.out.println("Une chambre avec ce numéro existe déjà: " + c.getNumero());
            return;
        }
        chambres.add(c);
    }

    public void afficherToutesLesChambres() {
        if (chambres.isEmpty()) {
            System.out.println("Aucune chambre disponible.");
            return;
        }
        for (Chambre c : chambres) System.out.println(c);
    }

    public void afficherChambresDisponibles() {
        boolean any = false;
        for (Chambre c : chambres) {
            if (!c.isOccupee()) {
                System.out.println(c);
                any = true;
            }
        }
        if (!any) System.out.println("Aucune chambre disponible actuellement.");
    }

    public Chambre rechercherChambre(int numero) {
        for (Chambre c : chambres) if (c.getNumero() == numero) return c;
        return null;
    }

    public ArrayList<Chambre> rechercherChambresParType(String type) {
        String t = type == null ? "" : type.toLowerCase();
        return chambres.stream().filter(c -> c.getType().toLowerCase().contains(t)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Chambre> rechercherChambresParPrix(double prixMax) {
        return chambres.stream().filter(c -> c.getPrixParNuit() <= prixMax).collect(Collectors.toCollection(ArrayList::new));
    }

    // Gestion des clients
    public void ajouterClient(Client c) {
        if (c == null) return;
        if (c.getEmail() != null && rechercherClientParEmail(c.getEmail()) != null) {
            System.out.println("Un client avec cet email existe déjà: " + c.getEmail());
            return;
        }
        clients.add(c);
    }

    public void afficherTousLesClients() {
        if (clients.isEmpty()) {
            System.out.println("Aucun client enregistré.");
            return;
        }
        for (Client cl : clients) System.out.println(cl);
    }

    public Client rechercherClient(int numero) {
        for (Client cl : clients) if (cl.getNumeroClient() == numero) return cl;
        return null;
    }

    public Client rechercherClientParEmail(String email) {
        if (email == null) return null;
        for (Client cl : clients) if (email.equalsIgnoreCase(cl.getEmail())) return cl;
        return null;
    }

    // Nouvelle méthode utilitaire
    public Client getDernierClient() {
        if (clients.isEmpty()) return null;
        return clients.get(clients.size() - 1);
    }

    // Gestion des services
    public void ajouterServiceDisponible(Service s) {
        if (s != null) servicesDisponibles.add(s);
    }

    public void afficherServicesDisponibles() {
        if (servicesDisponibles.isEmpty()) {
            System.out.println("Aucun service disponible.");
            return;
        }
        for (int i = 0; i < servicesDisponibles.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, servicesDisponibles.get(i));
        }
    }

    public Service getServiceParIndex(int index) {
        if (index < 0 || index >= servicesDisponibles.size()) return null;
        return servicesDisponibles.get(index);
    }

    // Gestion des réservations
    public Reservation creerReservation(Client c, Chambre ch, String debut, String fin) {
        if (c == null || ch == null) return null;
        if (ch.isOccupee()) return null; // simple check
        Reservation r = new Reservation(c, ch, debut, fin);
        reservations.add(r);
        return r;
    }

    public void afficherToutesLesReservations() {
        if (reservations.isEmpty()) {
            System.out.println("Aucune réservation.");
            return;
        }
        for (Reservation r : reservations) System.out.println(r);
    }

    public void afficherReservationsClient(Client c) {
        if (c == null) return;
        boolean any = false;
        for (Reservation r : reservations) {
            if (r.getClient().getNumeroClient() == c.getNumeroClient()) {
                System.out.println(r);
                any = true;
            }
        }
        if (!any) System.out.println("Aucune réservation pour ce client.");
    }

    public Reservation rechercherReservation(int numero) {
        for (Reservation r : reservations) if (r.getNumeroReservation() == numero) return r;
        return null;
    }

    public void annulerReservation(int numero) {
        Reservation r = rechercherReservation(numero);
        if (r != null) r.annuler();
    }

    public void terminerReservation(int numero) {
        Reservation r = rechercherReservation(numero);
        if (r != null) r.terminer();
    }

    // Statistiques
    public double calculerChiffreAffaires() {
        return reservations.stream().filter(r -> !"Annulée".equalsIgnoreCase(r.getStatut())).mapToDouble(Reservation::calculerPrixTotal).sum();
    }

    public double calculerTauxOccupation() {
        if (chambres.isEmpty()) return 0.0;
        long occ = chambres.stream().filter(Chambre::isOccupee).count();
        return (occ * 100.0) / chambres.size();
    }

    public void afficherStatistiques() {
        System.out.printf("Chiffre d'affaires: %.2f€\n", calculerChiffreAffaires());
        System.out.printf("Taux d'occupation: %.2f%%\n", calculerTauxOccupation());
        Chambre top = getChambrePlusReservee();
        if (top != null) System.out.println("Chambre la plus réservée: " + top.getNumero() + " (" + top.getType() + ")");
        else System.out.println("Pas assez de données pour déterminer la chambre la plus réservée.");
    }

    public Chambre getChambrePlusReservee() {
        if (reservations.isEmpty()) return null;
        Map<Integer, Integer> counts = new HashMap<>();
        for (Reservation r : reservations) {
            if (r.getChambre() == null) continue;
            int num = r.getChambre().getNumero();
            counts.put(num, counts.getOrDefault(num, 0) + 1);
        }
        if (counts.isEmpty()) return null;
        int bestNum = counts.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
        return rechercherChambre(bestNum);
    }
}
