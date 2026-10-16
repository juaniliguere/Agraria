package io.github.agraria.economia;

public class Billetera {
    private int monedas;

    public Billetera(int monedasIniciales) {
        this.monedas = monedasIniciales;
    }

    public int getMonedas() {
        return monedas;
    }

    // Intenta gastar dinero (devuelve true si pudo, false si no alcanza)
    public boolean gastar(int cantidad) {
        if (monedas >= cantidad) {
            monedas -= cantidad;
            return true;
        }
        return false;
    }

    // Suma dinero cuando vendés algo
    public void ganar(int cantidad) {
        if (cantidad > 0) {
            monedas += cantidad;
        }
    }
}