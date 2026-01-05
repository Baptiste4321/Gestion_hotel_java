package gestionhotel;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.io.File;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static Hotel hotel;

    public static void main(String[] args) {
        hotel = new Hotel("Hôtel Java", "1 Rue du Code");

        File f = new File("hotel_data.csv");
        if (f.exists()) {
            hotel.chargerDonnees();
        } else {
            seedData();
        }
        mainMenu();
    }

    private static void seedData() {
        // Chambres
        hotel.ajouterChambre(new ChambreSimple(101));
        hotel.ajouterChambre(new ChambreSimple(102));
        hotel.ajouterChambre(new ChambreDouble(201, false));
        hotel.ajouterChambre(new ChambreDouble(202, true));
        hotel.ajouterChambre(new Suite(301, true, true));

        // Services
        hotel.ajouterServiceDisponible(new Service("Petit-déjeuner", 15.0, "Buffet"));
        hotel.ajouterServiceDisponible(new Service("Dîner au restaurant", 35.0, "Menu du chef"));
        hotel.ajouterServiceDisponible(new Service("Spa (1h)", 50.0, "Accès spa 1 heure"));
        hotel.ajouterServiceDisponible(new Service("Parking", 10.0, "Par jour"));
        hotel.ajouterServiceDisponible(new Service("Wifi Premium", 5.0, "Par jour"));

        // Clients (test)
        hotel.ajouterClient(new Client("Dupont", "Jean", "jean.dupont@example.com", "0600000001"));
        hotel.ajouterClient(new Client("Martin", "Alice", "alice.martin@example.com", "0600000002"));
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n=== Menu principal ===");
            System.out.println("1) Gestion des chambres");
            System.out.println("2) Gestion des clients");
            System.out.println("3) Gestion des réservations");
            System.out.println("4) Gestion des services");
            System.out.println("5) Statistiques");
            System.out.println("0) Quitter");
            System.out.print("Choix: ");
            int choix = readInt();
            switch (choix) {
                case 1: menuChambres(); break;
                case 2: menuClients(); break;
                case 3: menuReservations(); break;
                case 4: menuServices(); break;
                case 5: menuStats(); break;
                case 0:
                    hotel.sauvegarderDonnees();
                    System.out.println("Données sauvegardées. Au revoir.");
                    return;
                default: System.out.println("Choix invalide.");
            }
        }
    }

    private static void menuChambres() {
        while (true) {
            System.out.println("\n--- Gestion des chambres ---");
            System.out.println("1) Ajouter une chambre");
            System.out.println("2) Afficher toutes les chambres");
            System.out.println("3) Afficher les chambres disponibles");
            System.out.println("4) Rechercher une chambre par numéro");
            System.out.println("5) Rechercher par type");
            System.out.println("6) Rechercher par prix maximum");
            System.out.println("7) Recherche avancée");
            System.out.println("8) Retour");
            System.out.print("Choix: ");
            int c = readInt();
            switch (c) {
                case 1: ajouterChambreInteractive(); break;
                case 2: hotel.afficherToutesLesChambres(); break;
                case 3: hotel.afficherChambresDisponibles(); break;
                case 4:
                    System.out.print("Numéro: ");
                    int num = readInt();
                    Chambre ch = hotel.rechercherChambre(num);
                    System.out.println(ch == null ? "Chambre non trouvée." : ch);
                    break;
                case 5:
                    System.out.print("Type (Simple/Double/Suite): ");
                    String t = readLine();
                    var list = hotel.rechercherChambresParType(t);
                    if (list.isEmpty()) System.out.println("Aucune chambre de ce type.");
                    else list.forEach(System.out::println);
                    break;
                case 6:
                    System.out.print("Prix maximum: ");
                    double p = readDouble();
                    var lp = hotel.rechercherChambresParPrix(p);
                    if (lp.isEmpty()) System.out.println("Aucune chambre sous ce prix.");
                    else lp.forEach(System.out::println);
                    break;
                case 7:
                    System.out.println("--- Recherche avancée ---");
                    System.out.print("Type souhaité (ou vide) : ");
                    String type = readLine();
                    System.out.print("Budget maximum : ");
                    double maxPrix = readDouble();

                    var resultats = hotel.rechercherChambresMultiCriteres(type, maxPrix);
                    if (resultats.isEmpty()) {
                        System.out.println("Aucune chambre ne correspond à vos critères.");
                    } else {
                        resultats.forEach(System.out::println);
                    }
                    break;
                case 8: return;
                default: System.out.println("Choix invalide.");
            }
        }
    }

    private static void ajouterChambreInteractive() {
        try {
            System.out.print("Numéro: ");
            int num = readInt();
            System.out.print("Type (1=Simple,2=Double,3=Suite): ");
            int type = readInt();
            switch (type) {
                case 1:
                    hotel.ajouterChambre(new ChambreSimple(num));
                    System.out.println("Chambre simple ajoutée.");
                    break;
                case 2:
                    System.out.print("Lits jumeaux? (true/false): ");
                    boolean lj = readBoolean();
                    hotel.ajouterChambre(new ChambreDouble(num, lj));
                    System.out.println("Chambre double ajoutée.");
                    break;
                case 3:
                    System.out.print("Jacuzzi? (true/false): ");
                    boolean j = readBoolean();
                    System.out.print("Balcon? (true/false): ");
                    boolean b = readBoolean();
                    hotel.ajouterChambre(new Suite(num, j, b));
                    System.out.println("Suite ajoutée.");
                    break;
                default:
                    System.out.println("Type invalide.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout de la chambre.");
        }
    }

    private static void menuClients() {
        while (true) {
            System.out.println("\n--- Gestion des clients ---");
            System.out.println("1) Ajouter un client");
            System.out.println("2) Afficher tous les clients");
            System.out.println("3) Rechercher un client");
            System.out.println("4) Modifier un client");
            System.out.println("5) Retour");
            System.out.print("Choix: ");
            int c = readInt();
            switch (c) {
                case 1: ajouterClientInteractive(); break;
                case 2: hotel.afficherTousLesClients(); break;
                case 3:
                    System.out.print("Numéro client: ");
                    int num = readInt();
                    Client cl = hotel.rechercherClient(num);
                    System.out.println(cl == null ? "Client non trouvé." : cl);
                    break;
                case 4:
                    System.out.print("Numéro client à modifier: ");
                    int id = readInt();
                    Client cmod = hotel.rechercherClient(id);
                    if (cmod == null) { System.out.println("Client non trouvé."); break; }
                    System.out.print("Nouveau prénom (vide = inchangé): ");
                    String pr = readLine(); if (!pr.isEmpty()) cmod.setPrenom(pr);
                    System.out.print("Nouveau nom (vide = inchangé): ");
                    String nm = readLine(); if (!nm.isEmpty()) cmod.setNom(nm);
                    System.out.print("Nouvel email (vide = inchangé): ");
                    String em = readLine();
                    if (!em.isEmpty()) {
                        while (!em.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                            System.out.print("Email invalide. Veuillez entrer un email valide (ex: exemple@mail.com): ");
                            em = readLine();
                        }
                        cmod.setEmail(em);
                    }
                    System.out.print("Nouveau téléphone (vide = inchangé): ");
                    String tel = readLine();
                    if (!tel.isEmpty()) {
                        while (!tel.matches("\\d{10}")) {
                            System.out.print("Numéro invalide. Veuillez entrer exactement 10 chiffres: ");
                            tel = readLine();
                        }
                        cmod.setTelephone(tel);
                    }
                    System.out.println("Client mis à jour: " + cmod);
                    break;
                case 5: return;
                default: System.out.println("Choix invalide.");
            }
        }
    }

    private static void ajouterClientInteractive() {
        System.out.print("Prénom: ");
        String prenom = readLine();
        System.out.print("Nom: ");
        String nom = readLine();
        System.out.print("Email: ");
        String email = lireEmailValide();
        System.out.print("Téléphone : ");
        String tel = lireTelephoneValide();
        Client c = new Client(nom, prenom, email, tel);
        if (hotel.ajouterClient(c)) {
            System.out.println("Client ajouté: " + c);
        }
    }

    private static void menuReservations() {
        while (true) {
            System.out.println("\n--- Gestion des réservations ---");
            System.out.println("1) Créer une réservation");
            System.out.println("2) Afficher toutes les réservations");
            System.out.println("3) Afficher les réservations d'un client");
            System.out.println("4) Rechercher une réservation");
            System.out.println("5) Ajouter des services à une réservation");
            System.out.println("6) Annuler une réservation");
            System.out.println("7) Terminer une réservation");
            System.out.println("8) Retour");
            System.out.print("Choix: ");
            int c = readInt();
            switch (c) {
                case 1: creerReservationInteractive(); break;
                case 2: hotel.afficherToutesLesReservations(); break;
                case 3:
                    System.out.print("Numéro client: ");
                    int id = readInt();
                    Client cl = hotel.rechercherClient(id);
                    hotel.afficherReservationsClient(cl);
                    break;
                case 4:
                    System.out.print("Numéro réservation: ");
                    int num = readInt();
                    Reservation r = hotel.rechercherReservation(num);
                    System.out.println(r == null ? "Réservation non trouvée." : r);
                    break;
                case 5:
                    ajouterServicesReservationInteractive(); break;
                case 6:
                    System.out.print("Numéro réservation à annuler: ");
                    int na = readInt(); hotel.annulerReservation(na); System.out.println("Opération effectuée."); break;
                case 7:
                    System.out.print("Numéro réservation à terminer: ");
                    int nt = readInt(); hotel.terminerReservation(nt); System.out.println("Opération effectuée."); break;
                case 8: return;
                default: System.out.println("Choix invalide.");
            }
        }
    }

    private static void creerReservationInteractive() {
        try {
            System.out.print("Numéro client existant (0 = nouveau client): ");
            int id = readInt();
            Client client = null;
            if (id == 0) {
                ajouterClientInteractive();
                client = hotel.getDernierClient();
                if (client == null) { System.out.println("Erreur: aucun client trouvé."); return; }
            } else {
                client = hotel.rechercherClient(id);
                if (client == null) { System.out.println("Client non trouvé."); return; }
            }

            System.out.print("Numéro de chambre: ");
            int numCh = readInt();
            Chambre ch = hotel.rechercherChambre(numCh);
            if (ch == null) { System.out.println("Chambre non trouvée."); return; }
            if (ch.isOccupee()) { System.out.println("Chambre déjà occupée."); return; }

            System.out.print("Date début (jj/mm/aaaa): ");
            String debut = lireDateValide("Date début");
            System.out.print("Date fin (jj/mm/aaaa): ");
            String fin = lireDateValide("Date fin");

            Reservation r = hotel.creerReservation(client, ch, debut, fin);
            if (r == null) System.out.println("Impossible de créer la réservation.");
            else System.out.println("Réservation créée:\n" + r);
        } catch (Exception e) {
            System.out.println("Erreur lors de la création de la réservation.");
        }
    }

    private static void ajouterServicesReservationInteractive() {
        System.out.print("Numéro réservation: ");
        int num = readInt();
        Reservation r = hotel.rechercherReservation(num);
        if (r == null) { System.out.println("Réservation non trouvée."); return; }
        while (true) {
            hotel.afficherServicesDisponibles();
            System.out.print("Index du service à ajouter (0 = terminer): ");
            int idx = readInt();
            if (idx == 0) break;
            Service s = hotel.getServiceParIndex(idx - 1);
            if (s == null) System.out.println("Index invalide.");
            else { r.ajouterService(s); System.out.println("Service ajouté."); }
        }
        System.out.println("Total services: " + r.calculerPrixServices() + "€");
    }

    private static void menuServices() {
        while (true) {
            System.out.println("\n--- Gestion des services ---");
            System.out.println("1) Afficher les services disponibles");
            System.out.println("2) Ajouter un nouveau service");
            System.out.println("3) Modifier un service");
            System.out.println("4) Retour");
            System.out.print("Choix: ");
            int c = readInt();
            switch (c) {
                case 1: hotel.afficherServicesDisponibles(); break;
                case 2:
                    System.out.print("Nom: "); String nom = readLine();
                    System.out.print("Prix: "); double prix = readDouble();
                    System.out.print("Description: "); String desc = readLine();
                    hotel.ajouterServiceDisponible(new Service(nom, prix, desc));
                    System.out.println("Service ajouté.");
                    break;
                case 3:
                    hotel.afficherServicesDisponibles();
                    System.out.print("Index du service à modifier: "); int idx = readInt();
                    Service s = hotel.getServiceParIndex(idx - 1);
                    if (s == null) { System.out.println("Index invalide."); break; }
                    System.out.print("Nouveau nom (vide = inchangé): "); String nn = readLine(); if (!nn.isEmpty()) s.setNom(nn);
                    System.out.print("Nouveau prix (-1 = inchangé): "); double np = readDouble(); if (np >= 0) s.setPrix(np);
                    System.out.print("Nouvelle description (vide = inchangé): "); String nd = readLine(); if (!nd.isEmpty()) s.setDescription(nd);
                    System.out.println("Service mis à jour.");
                    break;
                case 4: return;
                default: System.out.println("Choix invalide.");
            }
        }
    }

    private static void menuStats() {
        while (true) {
            System.out.println("\n--- Statistiques ---");
            System.out.println("1) Chiffre d'affaires");
            System.out.println("2) Taux d'occupation");
            System.out.println("3) Chambre la plus réservée");
            System.out.println("4) Afficher statistiques complètes");
            System.out.println("5) Retour");
            System.out.print("Choix: ");
            int c = readInt();
            switch (c) {
                case 1: System.out.printf("CA: %.2f€\n", hotel.calculerChiffreAffaires()); break;
                case 2: System.out.printf("Taux: %.2f%%\n", hotel.calculerTauxOccupation()); break;
                case 3:
                    Chambre ch = hotel.getChambrePlusReservee();
                    if (ch == null) System.out.println("Aucune donnée."); else System.out.println("Chambre la plus réservée: " + ch);
                    break;
                case 4: hotel.afficherStatistiques(); break;
                case 5: return;
                default: System.out.println("Choix invalide.");
            }
        }
    }

    // Helpers for input
    private static int readInt() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Entrée invalide, réessayez: ");
            }
        }
    }

    private static double readDouble() {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.print("Entrée invalide, réessayez: ");
            }
        }
    }

    private static boolean readBoolean() {
        while (true) {
            String s = scanner.nextLine().trim().toLowerCase();
            if (s.equals("true") || s.equals("t") || s.equals("oui") || s.equals("o") || s.equals("1")) return true;
            if (s.equals("false") || s.equals("f") || s.equals("non") || s.equals("n") || s.equals("0")) return false;
            System.out.print("Entrée invalide (true/false), réessayez: ");
        }
    }
    private static String readLine() {
        return scanner.nextLine().trim();
    }

    private static String lireTelephoneValide() {
        while (true) {
            String tel = readLine();
            if (tel.matches("\\d{10}")) {
                return tel;
            }
            System.out.print("Numéro invalide. Veuillez entrer exactement 10 chiffres: ");
        }
    }

    private static String lireEmailValide() {
        while (true) {
            String email = readLine();
            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return email;
            }
            System.out.print("Email invalide. Veuillez entrer un email valide (ex: exemple@mail.com): ");
        }
    }


    private static String lireDateValide(String message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        while (true) {
            System.out.print(message + " (jj/mm/aaaa) : ");
            String dateStr = readLine();
            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("Erreur : La date ne peut pas être dans le passé.");
                    continue;
                }
                return dateStr;
            } catch (DateTimeParseException e) {
                System.out.println("Format invalide. Merci d'utiliser le format jour/mois/année (ex: 25/12/2024).");
            }
        }
    }
}