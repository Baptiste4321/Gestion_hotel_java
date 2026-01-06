package gestionhotel;

// classe abstraite pour les chambres
public abstract class Chambre {
    protected int numero;
    protected double prixParNuit;
    protected boolean occupee;
    protected int capacite;

    // constructeur de base pour une chambre
    public Chambre(int numero, double prixParNuit, int capacite) {
        this.numero = numero;
        this.prixParNuit = prixParNuit;
        this.capacite = capacite;
        this.occupee = false;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public double getPrixParNuit() {
        return prixParNuit;
    }

    public void setPrixParNuit(double prixParNuit) {
        this.prixParNuit = prixParNuit;
    }

    public boolean isOccupee() {
        return occupee;
    }

    public void setOccupee(boolean occupee) {
        this.occupee = occupee;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    @Override
    public String toString() {
        return String.format("Chambre %d - %s - Capacité: %d - Prix/nuit: %.2f %s - %s",
                numero, getType(), capacite, prixParNuit, (prixParNuit <= 1 ? "euro" : "euros"), (occupee ? "Occupée" : "Disponible"));
    }

    public abstract String getType();

    // calcul le prix total pour un nombre de nuits
    public double calculerPrix(int nbNuits) {
        return prixParNuit * nbNuits;
    }
}

