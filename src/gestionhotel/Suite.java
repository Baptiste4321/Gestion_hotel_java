package gestionhotel;

public class Suite extends Chambre {
    private boolean jacuzzi;
    private boolean balcon;

    public Suite(int numero, boolean jacuzzi, boolean balcon) {
        super(numero, 150.0 + (jacuzzi ? 30.0 : 0.0) + (balcon ? 20.0 : 0.0), 4);
        this.jacuzzi = jacuzzi;
        this.balcon = balcon;
    }

    public boolean hasJacuzzi() {
        return jacuzzi;
    }

    public void setJacuzzi(boolean jacuzzi) {
        this.jacuzzi = jacuzzi;
        recomputePrix();
    }

    public boolean hasBalcon() {
        return balcon;
    }

    public void setBalcon(boolean balcon) {
        this.balcon = balcon;
        recomputePrix();
    }

    private void recomputePrix() {
        this.prixParNuit = 150.0 + (jacuzzi ? 30.0 : 0.0) + (balcon ? 20.0 : 0.0);
    }

    @Override
    public String getType() {
        return "Suite" + (jacuzzi ? " (jacuzzi)" : "") + (balcon ? " (balcon)" : "");
    }
}

