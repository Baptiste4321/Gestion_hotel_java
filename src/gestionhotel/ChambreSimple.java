package gestionhotel;

// chambre basique a 50 euros
public class ChambreSimple extends Chambre {
    // chambre simple pour 1 personne
    public ChambreSimple(int numero) {
        super(numero, 50.0, 1);
    }

    @Override
    public String getType() {
        return "Simple";
    }
}

