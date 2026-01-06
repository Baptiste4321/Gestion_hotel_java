package gestionhotel;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.io.File;

// classe principale avec le menu console
public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    // tout les hotels de la chaine
    private static ArrayList<Hotel> chaineHotels = new ArrayList<>();
    // l'hotel selectionné actuellement
    private static Hotel currentHotel;

    public static void main(String[] args) {
        chaineHotels = Hotel.chargerListeHotels();
        
        if (chaineHotels.isEmpty()) {
            System.out.println("Bienvenue dans le système de gestion Multi-Hôtels.");
            System.out.println("Aucun hôtel trouvé. Créez-en un pour commencer.");
        } else {
            System.out.println("Bienvenue ! " + chaineHotels.size() + " hôtel(s) chargé(s).");
        }

        menuChaine();
    }

    // menu pour choisir l'hotel ou en creer un nouveau
    private static void menuChaine() {
        while (true) {
            System.out.println("\n=== GESTION CHAÎNE HÔTELIÈRE ===");
            System.out.println("1) Sélectionner un hôtel existant");
            System.out.println("2) Créer un nouvel hôtel");
            System.out.println("3) Sauvegarder tous les hôtels");
            System.out.println("0) Quitter le programme");
            System.out.print("Choix: ");

            int choix = readInt();
            switch (choix) {
                case 1:
                    selectionnerHotel();
                    break;
                case 2:
                    creerNouvelHotel();
                    break;
                case 3:
                    Hotel.sauvegarderTousLesHotels(chaineHotels);
                    System.out.println("Tous les hôtels ont été sauvegardés !");
                    break;
                case 0:
                    Hotel.sauvegarderTousLesHotels(chaineHotels);
                    System.out.println("Données sauvegardées. Au revoir !");
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    // cree un nouvel hotel et l'ajoute a la chaine
    private static void creerNouvelHotel() {
        System.out.print("Nom du nouvel hôtel : ");
        String nom = readLine();
        System.out.print("Adresse : ");
        String adresse = readLine();

        Hotel h = new Hotel(nom, adresse);

        System.out.print("Voulez-vous générer les chambres/services par défaut ? (oui/non) : ");
        if (readBoolean()) {
            seedData(h);
        }

        chaineHotels.add(h);
        Hotel.sauvegarderListeHotels(chaineHotels);
        System.out.println("Hôtel '" + nom + "' créé avec succès !");
    }

    // permet de choisir un hotel existant
    private static void selectionnerHotel() {
        if (chaineHotels.isEmpty()) {
            System.out.println("Aucun hôtel dans la chaîne. Créez-en un d'abord.");
            return;
        }

        System.out.println("\n--- Liste des hôtels ---");
        for (int i = 0; i < chaineHotels.size(); i++) {
            System.out.println((i + 1) + ") " + chaineHotels.get(i).getNom());
        }
        System.out.print("Numéro de l'hôtel (0 pour annuler) : ");
        int choix = readInt();

        if (choix > 0 && choix <= chaineHotels.size()) {
            currentHotel = chaineHotels.get(choix - 1);

            currentHotel.chargerDonnees();

            System.out.println("Vous gérez maintenant : " + currentHotel.getNom());
            mainMenu();
        }
    }

    // menu principal de l'hotel selectionné
    private static void mainMenu() {
        while (true) {
            System.out.println("\n=== Menu Hôtel : " + currentHotel.getNom() + " ===");
            System.out.println("1) Gestion des chambres");
            System.out.println("2) Gestion des clients");
            System.out.println("3) Gestion des réservations");
            System.out.println("4) Gestion des services");
            System.out.println("5) Statistiques");
            System.out.println("0) Retour au choix de l'hôtel (Sauvegarde auto)");
            System.out.print("Choix: ");
            int choix = readInt();
            switch (choix) {
                case 1: menuChambres(); break;
                case 2: menuClients(); break;
                case 3: menuReservations(); break;
                case 4: menuServices(); break;
                case 5: menuStats(); break;
                case 0:
                    currentHotel.sauvegarderDonnees();
                    System.out.println("Données de " + currentHotel.getNom() + " sauvegardées.");
                    currentHotel = null;
                    return;
                default: System.out.println("Choix invalide.");
            }
        }
    }

    // genere des donnees de base pour tester
    private static void seedData(Hotel h) {
        h.ajouterChambre(new ChambreSimple(101));
        h.ajouterChambre(new ChambreSimple(102));
        h.ajouterChambre(new ChambreDouble(201, false));
        h.ajouterChambre(new ChambreDouble(202, true));
        h.ajouterChambre(new Suite(301, true, true));

        h.ajouterServiceDisponible(new Service("Petit-déjeuner", 15.0, "Buffet"));
        h.ajouterServiceDisponible(new Service("Dîner au restaurant", 35.0, "Menu du chef"));
        h.ajouterServiceDisponible(new Service("Spa (1h)", 50.0, "Accès spa 1 heure"));
        h.ajouterServiceDisponible(new Service("Parking", 10.0, "Par jour"));
        h.ajouterServiceDisponible(new Service("Wifi Premium", 5.0, "Par jour"));

        System.out.println("Données de base générées pour " + h.getNom());
    }

    // sous-menu pour gerer les chambres
    private static void menuChambres() {
        while (true) {
            System.out.println("\n--- Gestion des chambres (" + currentHotel.getNom() + ") ---");
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
                case 2: currentHotel.afficherToutesLesChambres(); break;
                case 3: currentHotel.afficherChambresDisponibles(); break;
                case 4:
                    System.out.print("Numéro: ");
                    int num = readInt();
                    Chambre ch = currentHotel.rechercherChambre(num);
                    System.out.println(ch == null ? "Chambre non trouvée." : ch);
                    break;
                case 5:
                    System.out.print("Type (Simple/Double/Suite): ");
                    String t = readLine();
                    var list = currentHotel.rechercherChambresParType(t);
                    if (list.isEmpty()) System.out.println("Aucune chambre de ce type.");
                    else list.forEach(System.out::println);
                    break;
                case 6:
                    System.out.print("Prix maximum: ");
                    double p = readDouble();
                    var lp = currentHotel.rechercherChambresParPrix(p);
                    if (lp.isEmpty()) System.out.println("Aucune chambre sous ce prix.");
                    else lp.forEach(System.out::println);
                    break;
                case 7:
                    System.out.println("--- Recherche avancée ---");
                    System.out.print("Type souhaité (Simple/Double/Suite ou vide) : ");
                    String type = readLine();
                    System.out.print("Budget maximum : ");
                    double maxPrix = readDouble();

                    var resultats = currentHotel.rechercherChambresMultiCriteres(type, maxPrix);
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

    // ajoute une chambre en demandant les infos
    private static void ajouterChambreInteractive() {
        try {
            System.out.print("Numéro: ");
            int num = readInt();
            System.out.print("Type (1=Simple,2=Double,3=Suite): ");
            int type = readInt();
            switch (type) {
                case 1:
                    currentHotel.ajouterChambre(new ChambreSimple(num));
                    System.out.println("Chambre simple ajoutée.");
                    break;
                case 2:
                    System.out.print("Lits jumeaux? (true/false): ");
                    boolean lj = readBoolean();
                    currentHotel.ajouterChambre(new ChambreDouble(num, lj));
                    System.out.println("Chambre double ajoutée.");
                    break;
                case 3:
                    System.out.print("Jacuzzi? (true/false): ");
                    boolean j = readBoolean();
                    System.out.print("Balcon? (true/false): ");
                    boolean b = readBoolean();
                    currentHotel.ajouterChambre(new Suite(num, j, b));
                    System.out.println("Suite ajoutée.");
                    break;
                default:
                    System.out.println("Type invalide.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout de la chambre.");
        }
    }

    // sous-menu pour gerer les clients
    private static void menuClients() {
        while (true) {
            System.out.println("\n--- Gestion des clients (" + currentHotel.getNom() + ") ---");
            System.out.println("1) Ajouter un client");
            System.out.println("2) Afficher tous les clients");
            System.out.println("3) Rechercher un client");
            System.out.println("4) Modifier un client");
            System.out.println("5) Retour");
            System.out.print("Choix: ");
            int c = readInt();
            switch (c) {
                case 1: ajouterClientInteractive(); break;
                case 2: currentHotel.afficherTousLesClients(); break;
                case 3:
                    System.out.print("Numéro client: ");
                    int num = readInt();
                    Client cl = currentHotel.rechercherClient(num);
                    System.out.println(cl == null ? "Client non trouvé." : cl);
                    break;
                case 4:
                    System.out.print("Numéro client à modifier: ");
                    int id = readInt();
                    Client cmod = currentHotel.rechercherClient(id);
                    if (cmod == null) { System.out.println("Client non trouvé."); break; }

                    System.out.print("Nouveau prénom (vide = inchangé): ");
                    String pr = readLine(); if (!pr.isEmpty()) cmod.setPrenom(pr);

                    System.out.print("Nouveau nom (vide = inchangé): ");
                    String nm = readLine(); if (!nm.isEmpty()) cmod.setNom(nm);

                    System.out.print("Nouvel email (vide = inchangé): ");
                    String em = readLine();
                    if (!em.isEmpty()) {
                        while (!em.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                            System.out.print("Email invalide. Réessayez : ");
                            em = readLine();
                        }
                        cmod.setEmail(em);
                    }

                    System.out.print("Nouveau téléphone (vide = inchangé): ");
                    String tel = readLine();
                    if (!tel.isEmpty()) {
                        while (!tel.matches("\\d{10}")) {
                            System.out.print("Numéro invalide (10 chiffres) : ");
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

    // ajoute un client en demandant les infos
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
        if (currentHotel.ajouterClient(c)) {
            System.out.println("Client ajouté: " + c);
        }
    }

    // sous-menu pour gerer les reservations
    private static void menuReservations() {
        while (true) {
            System.out.println("\n--- Gestion des réservations (" + currentHotel.getNom() + ") ---");
            System.out.println("1) Créer une réservation");
            System.out.println("2) Afficher toutes les réservations");
            System.out.println("3) Afficher les réservations d'un client");
            System.out.println("4) Rechercher une réservation");
            System.out.println("5) Ajouter des services à une réservation");
            System.out.println("6) Annuler une réservation");
            System.out.println("7) Terminer une réservation (Check-out & Facture)");
            System.out.println("8) Générer une facture pour une réservation");
            System.out.println("9) Retour");
            System.out.print("Choix: ");
            int c = readInt();
            switch (c) {
                case 1: creerReservationInteractive(); break;
                case 2: currentHotel.afficherToutesLesReservations(); break;
                case 3:
                    System.out.print("Numéro client: ");
                    int id = readInt();
                    Client cl = currentHotel.rechercherClient(id);
                    currentHotel.afficherReservationsClient(cl);
                    break;
                case 4:
                    System.out.print("Numéro réservation: ");
                    int num = readInt();
                    Reservation r = currentHotel.rechercherReservation(num);
                    System.out.println(r == null ? "Réservation non trouvée." : r);
                    break;
                case 5:
                    ajouterServicesReservationInteractive(); break;
                case 6:
                    System.out.print("Numéro réservation à annuler: ");
                    int na = readInt();
                    currentHotel.annulerReservation(na);
                    System.out.println("Opération effectuée.");
                    break;
                case 7:
                    System.out.print("Numéro réservation à terminer: ");
                    int nt = readInt();
                    currentHotel.terminerReservation(nt);
                    break;
                case 8:
                    System.out.print("Numéro réservation pour facture : ");
                    int numFact = readInt();
                    Reservation rFact = currentHotel.rechercherReservation(numFact);
                    if (rFact != null) {
                        currentHotel.genererFacture(rFact);
                    } else {
                        System.out.println("Réservation non trouvée.");
                    }
                    break;
                case 9: return;
                default: System.out.println("Choix invalide.");
            }
        }
    }

    // cree une resa en demandant tout les details
    private static void creerReservationInteractive() {
        try {
            System.out.print("Numéro client existant (0 = nouveau client): ");
            int id = readInt();
            Client client = null;
            if (id == 0) {
                ajouterClientInteractive();
                client = currentHotel.getDernierClient();
                if (client == null) { System.out.println("Erreur: aucun client trouvé."); return; }
            } else {
                client = currentHotel.rechercherClient(id);
                if (client == null) { System.out.println("Client non trouvé."); return; }
            }

            System.out.print("Numéro de chambre: ");
            int numCh = readInt();
            Chambre ch = currentHotel.rechercherChambre(numCh);
            if (ch == null) { System.out.println("Chambre non trouvée."); return; }
            if (ch.isOccupee()) { System.out.println("Chambre déjà occupée."); return; }

            System.out.print("Date début (jj/mm/aaaa): ");
            String debut = lireDateValide("Date début");
            System.out.print("Date fin (jj/mm/aaaa): ");
            String fin = lireDateValide("Date fin");

            Reservation r = currentHotel.creerReservation(client, ch, debut, fin);
            if (r == null) System.out.println("Impossible de créer la réservation.");
            else System.out.println("Réservation créée:\n" + r);
        } catch (Exception e) {
            System.out.println("Erreur lors de la création de la réservation.");
        }
    }

    // ajoute des services a une resa existante
    private static void ajouterServicesReservationInteractive() {
        System.out.print("Numéro réservation: ");
        int num = readInt();
        Reservation r = currentHotel.rechercherReservation(num);
        if (r == null) { System.out.println("Réservation non trouvée."); return; }

        while (true) {
            currentHotel.afficherServicesDisponibles();
            System.out.print("Index du service à ajouter (0 = terminer): ");
            int idx = readInt();
            if (idx == 0) break;

            Service s = currentHotel.getServiceParIndex(idx - 1);
            if (s == null) System.out.println("Index invalide.");
            else {
                r.ajouterService(s);
                System.out.println("Service ajouté.");
            }
        }
        double totalServices = r.calculerPrixServices();
        System.out.println("Total services: " + totalServices + (totalServices <= 1 ? " euro" : " euros"));
    }

    // sous-menu pour gerer les services
    private static void menuServices() {
        while (true) {
            System.out.println("\n--- Gestion des services (" + currentHotel.getNom() + ") ---");
            System.out.println("1) Afficher les services disponibles");
            System.out.println("2) Ajouter un nouveau service");
            System.out.println("3) Modifier un service");
            System.out.println("4) Retour");
            System.out.print("Choix: ");
            int c = readInt();
            switch (c) {
                case 1: currentHotel.afficherServicesDisponibles(); break;
                case 2:
                    System.out.print("Nom: "); String nom = readLine();
                    System.out.print("Prix: "); double prix = readDouble();
                    System.out.print("Description: "); String desc = readLine();
                    currentHotel.ajouterServiceDisponible(new Service(nom, prix, desc));
                    System.out.println("Service ajouté.");
                    break;
                case 3:
                    currentHotel.afficherServicesDisponibles();
                    System.out.print("Index du service à modifier: "); int idx = readInt();
                    Service s = currentHotel.getServiceParIndex(idx - 1);
                    if (s == null) { System.out.println("Index invalide."); break; }

                    System.out.print("Nouveau nom (vide = inchangé): ");
                    String nn = readLine(); if (!nn.isEmpty()) s.setNom(nn);

                    System.out.print("Nouveau prix (-1 = inchangé): ");
                    double np = readDouble(); if (np >= 0) s.setPrix(np);

                    System.out.print("Nouvelle description (vide = inchangé): ");
                    String nd = readLine(); if (!nd.isEmpty()) s.setDescription(nd);

                    System.out.println("Service mis à jour.");
                    break;
                case 4: return;
                default: System.out.println("Choix invalide.");
            }
        }
    }

    // affiche les statistiques de l'hotel
    private static void menuStats() {
        while (true) {
            System.out.println("\n--- Statistiques (" + currentHotel.getNom() + ") ---");
            System.out.println("1) Chiffre d'affaires");
            System.out.println("2) Taux d'occupation");
            System.out.println("3) Chambre la plus réservée");
            System.out.println("4) Afficher statistiques complètes");
            System.out.println("5) Retour");
            System.out.print("Choix: ");
            int c = readInt();
            switch (c) {
                case 1:
                    double ca = currentHotel.calculerChiffreAffaires();
                    System.out.printf("CA: %.2f %s\n", ca, (ca <= 1 ? "euro" : "euros"));
                    break;
                case 2: System.out.printf("Taux: %.2f%%\n", currentHotel.calculerTauxOccupation()); break;
                case 3:
                    Chambre ch = currentHotel.getChambrePlusReservee();
                    if (ch == null) System.out.println("Aucune donnée.");
                    else System.out.println("Chambre la plus réservée: " + ch);
                    break;
                case 4: currentHotel.afficherStatistiques(); break;
                case 5: return;
                default: System.out.println("Choix invalide.");
            }
        }
    }

    // lit un entier au clavier
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

    // lit un double au clavier
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

    // lit un boolean (oui/non, true/false)
    private static boolean readBoolean() {
        while (true) {
            String s = scanner.nextLine().trim().toLowerCase();
            if (s.equals("true") || s.startsWith("t") || s.equals("oui") || s.startsWith("o") || s.equals("1")) return true;
            if (s.equals("false") || s.startsWith("f") || s.equals("non") || s.startsWith("n") || s.equals("0")) return false;
            System.out.print("Entrée invalide (true/false), réessayez: ");
        }
    }

    // lit une ligne de texte
    private static String readLine() {
        return scanner.nextLine().trim();
    }

    // verifie que le tel fait 10 chiffres
    private static String lireTelephoneValide() {
        while (true) {
            String tel = readLine();
            if (tel.matches("\\d{10}")) {
                return tel;
            }
            System.out.print("Numéro invalide. Veuillez entrer exactement 10 chiffres: ");
        }
    }

    // verifie le format de l'email
    private static String lireEmailValide() {
        while (true) {
            String email = readLine();
            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return email;
            }
            System.out.print("Email invalide. Veuillez entrer un email valide (ex: exemple@mail.com): ");
        }
    }

    // verifie que la date est valide et pas dans le passé
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