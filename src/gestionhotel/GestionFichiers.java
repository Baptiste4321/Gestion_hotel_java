import java.io.*;

// ... dans la classe Hotel

public void sauvegarderDonnees() {
    // Exemple pour les clients : id;nom;prenom;email;tel
    try (PrintWriter writer = new PrintWriter(new FileWriter("clients.csv"))) {
        for (Client c : clients) {
            writer.println(String.format("%d;%s;%s;%s;%s",
                    c.getNumeroClient(), c.getNom(), c.getPrenom(), c.getEmail(), c.getTelephone()));
        }
        System.out.println("Clients sauvegardés !");
    } catch (IOException e) {
        System.out.println("Erreur sauvegarde clients : " + e.getMessage());
    }

    // Répétez la logique pour les Chambres et Réservations...
}

public void chargerDonnees() {
    File fichier = new File("clients.csv");
    if (!fichier.exists()) return; // Rien à charger

    try (BufferedReader reader = new BufferedReader(new FileReader(fichier))) {
        String ligne;
        int maxId = 0;
        clients.clear(); // On vide la liste avant de charger

        while ((ligne = reader.readLine()) != null) {
            String[] parts = ligne.split(";");
            if (parts.length >= 5) {
                // On recrée le client sans utiliser le constructeur qui auto-incrémente
                // Note : Vous devrez peut-être adapter votre constructeur ou utiliser des setters
                Client c = new Client(parts[1], parts[2], parts[3], parts[4]);
                // Astuce : Forcez l'ID lu dans le fichier (nécessite un setter pour numeroClient ou un constructeur spécial)
                // c.setNumeroClient(Integer.parseInt(parts[0]));
                clients.add(c);

                int id = Integer.parseInt(parts[0]);
                if (id > maxId) maxId = id;
            }
        }
        // Important : Mettre à jour le compteur pour que les prochains clients aient le bon ID
        Client.setCompteur(maxId + 1);
        System.out.println("Clients chargés !");
    } catch (IOException e) {
        System.out.println("Erreur chargement : " + e.getMessage());
    }
}