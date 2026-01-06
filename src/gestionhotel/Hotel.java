package gestionhotel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

// classe principale de l'hotel avec toute la gestion
public class Hotel {
    private String nom;
    private String adresse;
    private ArrayList<Chambre> chambres;
    private ArrayList<Client> clients;
    private ArrayList<Reservation> reservations;
    private ArrayList<Service> servicesDisponibles;

    public String getNom() {
        return nom;
    }
    private String getNomFichier() {
        return "hotel_" + nom.replaceAll("[^a-zA-Z0-9.-]", "_") + ".csv";
    }

    public Hotel(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
        this.chambres = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.servicesDisponibles = new ArrayList<>();
    }
    // verifie si une chambre est dispo entre 2 dates
    public boolean estDisponible(Chambre ch, String debut, String fin) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d/M/yyyy");
        LocalDate newDeb = LocalDate.parse(debut, dtf);
        LocalDate newFin = LocalDate.parse(fin, dtf);

        for (Reservation r : reservations) {
            if (r.getChambre().getNumero() == ch.getNumero() &&
                    (r.getStatut().equals("Confirmée") || r.getStatut().equals("En cours"))) {

                LocalDate rDeb = LocalDate.parse(r.getDateDebut(), dtf);
                LocalDate rFin = LocalDate.parse(r.getDateFin(), dtf);

                if (newDeb.isBefore(rFin) && newFin.isAfter(rDeb)) {
                    return false;
                }
            }
        }
        return true;
    }

    // retourne les chambres libres pour une periode
    public ArrayList<Chambre> getChambresDisponibles(String debut, String fin) {
        ArrayList<Chambre> disponibles = new ArrayList<>();
        for (Chambre ch : chambres) {
            if (estDisponible(ch, debut, fin)) {
                disponibles.add(ch);
            }
        }
        return disponibles;
    }

    // affiche les chambres dispo pour des dates données
    public void afficherChambresDisponiblesPourDates(String debut, String fin) {
        ArrayList<Chambre> disponibles = getChambresDisponibles(debut, fin);
        if (disponibles.isEmpty()) {
            System.out.println("Aucune chambre disponible pour ces dates.");
        } else {
            System.out.println("Chambres disponibles du " + debut + " au " + fin + " :");
            for (Chambre c : disponibles) {
                System.out.println(c);
            }
        }
    }

    // recherche avec plusieur criteres en meme temps
    public ArrayList<Chambre> rechercherChambresMultiCriteres(String type, double prixMax) {
        String t = type == null ? "" : type.toLowerCase();
        return chambres.stream()
                .filter(c -> c.getType().toLowerCase().contains(t))
                .filter(c -> c.getPrixParNuit() <= prixMax)
                .filter(c -> !c.isOccupee())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ajout d'une chambre (verifie qu'elle existe pas deja)
    public void ajouterChambre(Chambre c) {
        if (c == null) return;
        if (rechercherChambre(c.getNumero()) != null) {
            System.out.println("Une chambre avec ce numéro existe déjà: " + c.getNumero());
            return;
        }
        chambres.add(c);
    }

    // affiche toute les chambres de l'hotel
    public void afficherToutesLesChambres() {
        if (chambres.isEmpty()) {
            System.out.println("Aucune chambre disponible.");
            return;
        }
        for (Chambre c : chambres) System.out.println(c);
    }

    // montre que les chambres pas occupées
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

    // cherche une chambre par son numero
    public Chambre rechercherChambre(int numero) {
        for (Chambre c : chambres) if (c.getNumero() == numero) return c;
        return null;
    }

    // filtre les chambres par type (simple, double, suite)
    public ArrayList<Chambre> rechercherChambresParType(String type) {
        String t = type == null ? "" : type.toLowerCase();
        return chambres.stream().filter(c -> c.getType().toLowerCase().contains(t)).collect(Collectors.toCollection(ArrayList::new));
    }

    // trouve les chambres sous un certain prix
    public ArrayList<Chambre> rechercherChambresParPrix(double prixMax) {
        return chambres.stream().filter(c -> c.getPrixParNuit() <= prixMax).collect(Collectors.toCollection(ArrayList::new));
    }

    // ajoute un client (verifie l'email en double)
    public boolean ajouterClient(Client c) {
        if (c == null) return false;
        if (c.getEmail() != null && rechercherClientParEmail(c.getEmail()) != null) {
            System.out.println("Un client avec cet email existe déjà: " + c.getEmail());
            return false;
        }
        clients.add(c);
        return true;
    }

    // liste tout les clients avec leur nb de reservations
    public void afficherTousLesClients() {
        if (clients.isEmpty()) {
            System.out.println("Aucun client enregistré.");
            return;
        }
        for (Client cl : clients) {
            int nbReservations = compterReservationsClient(cl);
            System.out.println(cl + " - Réservations: " + nbReservations + (cl.isVip() ? " [VIP]" : ""));
        }
    }

    // cherche un client par son numero
    public Client rechercherClient(int numero) {
        for (Client cl : clients) if (cl.getNumeroClient() == numero) return cl;
        return null;
    }

    // cherche un client par email
    public Client rechercherClientParEmail(String email) {
        if (email == null) return null;
        for (Client cl : clients) if (email.equalsIgnoreCase(cl.getEmail())) return cl;
        return null;
    }

    // recupere le dernier client ajouté
    public Client getDernierClient() {
        if (clients.isEmpty()) return null;
        return clients.get(clients.size() - 1);
    }

    // ajoute un service au catalogue
    public void ajouterServiceDisponible(Service s) {
        if (s != null) servicesDisponibles.add(s);
    }

    // affiche tout les services dispo
    public void afficherServicesDisponibles() {
        if (servicesDisponibles.isEmpty()) {
            System.out.println("Aucun service disponible.");
            return;
        }
        for (int i = 0; i < servicesDisponibles.size(); i++) {
            System.out.printf("%d) %s\n", i + 1, servicesDisponibles.get(i));
        }
    }

    // recupere un service par son index dans la liste
    public Service getServiceParIndex(int index) {
        if (index < 0 || index >= servicesDisponibles.size()) return null;
        return servicesDisponibles.get(index);
    }

    // crée une nouvelle reservation
    public Reservation creerReservation(Client c, Chambre ch, String debut, String fin) {
        if (c == null || ch == null) return null;
        if (!estDisponible(ch, debut, fin)) {
            System.out.println("La chambre n'est pas disponible à ces dates.");
            return null;
        }
        Reservation r = new Reservation(c, ch, debut, fin);
        reservations.add(r);
        
        // Vérifier si le client atteint 10 réservations pour devenir VIP
        if (!c.isVip() && compterReservationsClient(c) >= 10) {
            c.setVip(true);
            System.out.println("Félicitations " + c.getNomComplet() + " ! Vous êtes maintenant client VIP !");
        }
        
        return r;
    }

    // compte le nombre de resa d'un client
    public int compterReservationsClient(Client c) {
        if (c == null) return 0;
        int count = 0;
        for (Reservation r : reservations) {
            if (r.getClient().getNumeroClient() == c.getNumeroClient()) {
                count++;
            }
        }
        return count;
    }

    // affiche toute les reservations
    public void afficherToutesLesReservations() {
        if (reservations.isEmpty()) {
            System.out.println("Aucune réservation.");
            return;
        }
        for (Reservation r : reservations) System.out.println(r);
    }

    // affiche les reservations d'un client spécifique
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

    // trouve une resa par son numero
    public Reservation rechercherReservation(int numero) {
        for (Reservation r : reservations) if (r.getNumeroReservation() == numero) return r;
        return null;
    }

    // annule une reservation
    public void annulerReservation(int numero) {
        Reservation r = rechercherReservation(numero);
        if (r != null) r.annuler();
    }

    // supprime une resa et libere la chambre
    public boolean supprimerReservation(int numero) {
        Reservation r = rechercherReservation(numero);
        if (r == null) {
            System.out.println("Réservation introuvable.");
            return false;
        }
        // Libérer la chambre si la réservation était en cours ou confirmée
        if (r.getChambre() != null && 
            (r.getStatut().equals("Confirmée") || r.getStatut().equals("En cours"))) {
            r.getChambre().setOccupee(false);
        }
        reservations.remove(r);
        System.out.println("Réservation #" + numero + " supprimée avec succès.");
        return true;
    }

    // termine une resa et genere la facture
    public void terminerReservation(int numero) {
        Reservation r = rechercherReservation(numero);
        if (r != null) {
            r.terminer();
            System.out.println("Réservation terminée.");

            genererFacture(r);
        } else {
            System.out.println("Réservation introuvable.");
        }
    }

    // calcule le CA total (sans les annulées)
    public double calculerChiffreAffaires() {
        return reservations.stream().filter(r -> !"Annulée".equalsIgnoreCase(r.getStatut())).mapToDouble(Reservation::calculerPrixTotal).sum();
    }

    // calcul le pourcentage de chambres occupées
    public double calculerTauxOccupation() {
        if (chambres.isEmpty()) return 0.0;
        long occ = chambres.stream().filter(Chambre::isOccupee).count();
        return (occ * 100.0) / chambres.size();
    }

    // affiche les stats de l'hotel (CA, taux, etc)
    public void afficherStatistiques() {
        double ca = calculerChiffreAffaires();
        System.out.printf("Chiffre d'affaires: %.2f %s\n", ca, (ca <= 1 ? "euro" : "euros"));
        System.out.printf("Taux d'occupation: %.2f%%\n", calculerTauxOccupation());
        Chambre top = getChambrePlusReservee();
        if (top != null) System.out.println("Chambre la plus réservée: " + top.getNumero() + " (" + top.getType() + ")");
        else System.out.println("Pas assez de données pour déterminer la chambre la plus réservée.");
    }

    // trouve la chambre la plus populaire
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
    // sauvegarde tout dans un fichier csv
    public void sauvegarderDonnees() {
        String fichier = getNomFichier();
        try (PrintWriter writer = new PrintWriter(new FileWriter(fichier))) {
            writer.println("---HOTEL---");
            writer.printf("%s;%s\n", nom, adresse);
            
            writer.println("---CLIENTS---");
            for (Client c : clients) {
                writer.printf("%d;%s;%s;%s;%s;%b\n", c.getNumeroClient(), c.getNom(), c.getPrenom(), c.getEmail(), c.getTelephone(), c.isVip());
            }

            writer.println("---CHAMBRES---");
            for (Chambre c : chambres) {
                String type = "SIMPLE";
                String extra = "";
                if (c instanceof ChambreDouble) {
                    type = "DOUBLE";
                    extra = String.valueOf(((ChambreDouble) c).isLitsJumeaux());
                } else if (c instanceof Suite) {
                    type = "SUITE";
                    extra = ((Suite) c).hasJacuzzi() + ";" + ((Suite) c).hasBalcon();
                }

                writer.printf("%s;%d;%.2f;%b;%s\n", type, c.getNumero(), c.getPrixParNuit(), c.isOccupee(), extra);
            }
            writer.println("---SERVICES_CATALOG---");
            for (Service s : servicesDisponibles) {
                writer.printf("%s;%.2f;%s\n", s.getNom(), s.getPrix(), s.getDescription());
            }

            writer.println("---RESERVATIONS---");
            for (Reservation r : reservations) {

                writer.printf("%d;%d;%d;%s;%s;%s\n",
                        r.getNumeroReservation(),
                        r.getClient().getNumeroClient(),
                        r.getChambre().getNumero(),
                        r.getDateDebut(),
                        r.getDateFin(),
                        r.getStatut());
            }
            writer.println("---RESERVATION_SERVICES---");
            for (Reservation r : reservations) {
                for (Service s : r.getServices()) {
                    writer.printf("%d;%s;%.2f;%s\n", r.getNumeroReservation(), s.getNom(), s.getPrix(), s.getDescription());
                }
            }

            System.out.println("Données sauvegardées dans hotel_data.csv !");
        } catch (IOException e) {
            System.out.println("Erreur sauvegarde : " + e.getMessage());
        }
    }

    // charge les données depuis le csv
    public void chargerDonnees() {
        File fichier = new File(getNomFichier());
        if (!fichier.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            String section = "";
            int maxIdClient = 0;

            clients.clear();
            chambres.clear();
            reservations.clear();

            while ((ligne = reader.readLine()) != null) {
                if (ligne.startsWith("---")) {
                    section = ligne;
                    continue;
                }

                String[] parts = ligne.split(";");

                if (section.equals("---HOTEL---") && parts.length >= 2) {
                    this.nom = parts[0];
                    this.adresse = parts[1];
                }
                else if (section.equals("---CLIENTS---") && parts.length >= 5) {
                    Client c = new Client(parts[1], parts[2], parts[3], parts[4]);
                    if (parts.length >= 6) {
                        c.setVip(Boolean.parseBoolean(parts[5]));
                    }

                    try {
                        int idLuu = Integer.parseInt(parts[0]);
                        if (idLuu > maxIdClient) maxIdClient = idLuu;
                    } catch(NumberFormatException e) { }
                    clients.add(c);
                }
                else if (section.equals("---CHAMBRES---") && parts.length >= 4) {
                    String type = parts[0];
                    int num = Integer.parseInt(parts[1]);
                    boolean occupee = Boolean.parseBoolean(parts[3]);

                    if (type.equals("SIMPLE")) {
                        ChambreSimple c = new ChambreSimple(num);
                        c.setOccupee(occupee);
                        chambres.add(c);
                    } else if (type.equals("DOUBLE") && parts.length >= 5) {
                        boolean litsJumeaux = Boolean.parseBoolean(parts[4]);
                        ChambreDouble c = new ChambreDouble(num, litsJumeaux);
                        c.setOccupee(occupee);
                        chambres.add(c);
                    } else if (type.equals("SUITE") && parts.length >= 6) {
                        boolean jacuzzi = Boolean.parseBoolean(parts[4]);
                        boolean balcon = Boolean.parseBoolean(parts[5]);
                        Suite c = new Suite(num, jacuzzi, balcon);
                        c.setOccupee(occupee);
                        chambres.add(c);
                    }
                }
                else if (section.equals("---SERVICES_CATALOG---") && parts.length >= 3) {
                    try {
                        String nom = parts[0];
                        double prix = Double.parseDouble(parts[1]);
                        String desc = parts[2];
                        servicesDisponibles.add(new Service(nom, prix, desc));
                    } catch (Exception e) { }
                }

                else if (section.equals("---RESERVATIONS---") && parts.length >= 6) {
                    int idClient = Integer.parseInt(parts[1]);
                    int numChambre = Integer.parseInt(parts[2]);

                    Client cl = null;
                    for(Client c : clients) {
                        if(clients.indexOf(c) + 1 == idClient) { cl = c; break; }
                    }

                    if(cl == null && !clients.isEmpty() && idClient <= clients.size()) {
                        cl = clients.get(idClient - 1);
                    }

                    Chambre ch = rechercherChambre(numChambre);

                    if (cl != null && ch != null) {
                        Reservation r = new Reservation(cl, ch, parts[3], parts[4]);
                        r.setStatut(parts[5]);
                        reservations.add(r);
                    }
                }

                else if (section.equals("---RESERVATION_SERVICES---") && parts.length >= 4) {
                    try {
                        int idRes = Integer.parseInt(parts[0]);
                        Reservation r = rechercherReservation(idRes);
                        if (r != null) {
                            Service s = new Service(parts[1], Double.parseDouble(parts[2]), parts[3]);
                            r.ajouterService(s);
                        }
                    } catch (Exception e) { }
                }
            }

            if (maxIdClient > 0) {
                Client.setCompteur(maxIdClient + 1);
            }

            System.out.println("Données chargées avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur chargement : " + e.getMessage());
        }
    }
    // genere une facture en fichier txt
    public void genererFacture(Reservation r) {
        if (r == null) {
            System.out.println("Erreur : réservation invalide.");
            return;
        }

        // Nom du fichier unique basé sur le nom de l'hôtel et le numéro de réservation
        String nomFichierFacture = "facture_" + nom.replaceAll("[^a-zA-Z0-9]", "_") + "_" + r.getNumeroReservation() + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(nomFichierFacture))) {
            // En-tête de l'hôtel (utilise le nom et l'adresse de l'hôtel actuel)
            writer.println("=========================================");
            writer.println("           " + nom.toUpperCase() + "           ");
            writer.println("          " + adresse + "           ");
            writer.println("              FACTURE                    ");
            writer.println("=========================================");
            writer.println();

            // Informations Client
            writer.println("CLIENT :");
            writer.println("Nom   : " + r.getClient().getNomComplet());
            writer.println("Email : " + r.getClient().getEmail());
            writer.println("-----------------------------------------");

            // Détails du séjour
            writer.println("DÉTAILS DU SÉJOUR :");
            writer.println("Réservation N° : " + r.getNumeroReservation());
            if (r.getChambre() != null) {
                writer.println("Chambre N°     : " + r.getChambre().getNumero() + " (" + r.getChambre().getType() + ")");
            }
            writer.println("Arrivée        : " + r.getDateDebut());
            writer.println("Départ         : " + r.getDateFin());
            writer.println("Durée          : " + r.calculerNombreNuits() + " nuit(s)");
            writer.println("-----------------------------------------");

            // Détail des réductions sur la facture
            if (r.estEnBasseSaison()) {
                writer.println("Réduction Basse Saison   : -20%");
            }
            if (r.calculerNombreNuits() > 7) {
                writer.println("Réduction Long Séjour    : -10%");
            }
            if (r.getClient().isVip()) {
                writer.println("Réduction Client VIP     : -5%");
            }

            // Détails financiers
            writer.println("CONSOMMATION :");
            writer.printf("Hébergement              : %8.2f€\n", r.calculerPrixChambre());

            if (!r.getServices().isEmpty()) {
                writer.println("Services supplémentaires :");
                for (Service s : r.getServices()) {
                    writer.printf(" - %-20s  : %8.2f€\n", s.getNom(), s.getPrix());
                }
                writer.printf("Sous-total Services      : %8.2f€\n", r.calculerPrixServices());
            }

            writer.println("-----------------------------------------");
            writer.printf("TOTAL À PAYER            : %8.2f€\n", r.calculerPrixTotal());
            writer.println("=========================================");
            writer.println("      Merci de votre visite !            ");
            writer.println("         " + nom + "                     ");

            System.out.println("Facture générée avec succès : " + nomFichierFacture);

        } catch (IOException e) {
            System.out.println("Erreur lors de la création de la facture : " + e.getMessage());
        }
    }

    // fichier qui contient la liste de tout les hotels
    private static final String FICHIER_HOTELS = "hotels_liste.csv";
    
    // sauvegarde la liste des hotels dans le fichier central
    public static void sauvegarderListeHotels(ArrayList<Hotel> hotels) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FICHIER_HOTELS))) {
            for (Hotel h : hotels) {
                writer.printf("%s;%s;%s\n", h.getNom(), h.getAdresse(), h.getNomFichier());
            }
            System.out.println("Liste des hôtels sauvegardée !");
        } catch (IOException e) {
            System.out.println("Erreur sauvegarde liste hôtels : " + e.getMessage());
        }
    }
    
    // charge tout les hotels depuis le fichier
    public static ArrayList<Hotel> chargerListeHotels() {
        ArrayList<Hotel> hotels = new ArrayList<>();
        File fichier = new File(FICHIER_HOTELS);
        
        if (!fichier.exists()) {
            return hotels;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parts = ligne.split(";");
                if (parts.length >= 2) {
                    Hotel h = new Hotel(parts[0], parts[1]);
                    h.chargerDonnees();
                    hotels.add(h);
                }
            }
            System.out.println(hotels.size() + " hôtel(s) chargé(s) !");
        } catch (IOException e) {
            System.out.println("Erreur chargement liste hôtels : " + e.getMessage());
        }
        
        return hotels;
    }
    
    // sauvegarde tout les hotels d'un coup
    public static void sauvegarderTousLesHotels(ArrayList<Hotel> hotels) {
        for (Hotel h : hotels) {
            h.sauvegarderDonnees();
        }
        sauvegarderListeHotels(hotels);
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
}
