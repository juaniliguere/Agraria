package io.github.agraria.economia;

public class Billetera {
    private int monedas;

    public Billetera(int monedasIniciales) {
        this.monedas = monedasIniciales;
    }

    public int getMonedas() {
        return monedas;
    }

    public boolean gastar(int cantidad) {
        if (monedas >= cantidad) {
            monedas -= cantidad;
            return true;
        }
        return false;
    }

    public void ganar(int cantidad) {
        if (cantidad > 0) {
            monedas += cantidad;
        }
    }
}