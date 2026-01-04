package gestionhotel;

public class ChambreSimple extends Chambre {
    public ChambreSimple(int numero) {
        super(numero, 50.0, 1);
    }

    @Override
    public String getType() {
        return "Simple";
    }
}

