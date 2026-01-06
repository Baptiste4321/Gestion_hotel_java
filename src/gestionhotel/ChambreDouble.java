package gestionhotel;

// chambre double avec option lits jumeaux
public class ChambreDouble extends Chambre {
    private boolean litsJumeaux;

    // chambre pour 2 avec option lits jumeaux
    public ChambreDouble(int numero, boolean litsJumeaux) {
        super(numero, 80.0, 2);
        this.litsJumeaux = litsJumeaux;
    }

    public boolean isLitsJumeaux() {
        return litsJumeaux;
    }

    public void setLitsJumeaux(boolean litsJumeaux) {
        this.litsJumeaux = litsJumeaux;
    }

    @Override
    public String getType() {
        return "Double" + (litsJumeaux ? " (lits jumeaux)" : " (lit double)");
    }
}
