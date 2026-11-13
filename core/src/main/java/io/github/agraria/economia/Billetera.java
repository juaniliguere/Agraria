
package io.github.agraria.economia;

public class Billetera {

    private int monedas;
    private int monedasGanadas;
    private int monedasGastadas;

    public Billetera(int monedasIniciales) {
        this.monedas = monedasIniciales;
        this.monedasGanadas = 0;
        this.monedasGastadas = 0;
    }

    public int getMonedas() {
        return monedas;
    }

    public boolean gastar(int cantidad) {

        if (cantidad > 0 && monedas >= cantidad) {
            monedas -= cantidad;
            monedasGastadas += cantidad;
            return true;
        }

        return false;
    }

    public void ganar(int cantidad) {

        if (cantidad > 0) {
            monedas += cantidad;
            monedasGanadas += cantidad;
        }
    }

    public int getMonedasGanadas() {
        return monedasGanadas;
    }

    public int getMonedasGastadas() {
        return monedasGastadas;
    }

    public void reiniciarRegistroDiario() {
        monedasGanadas = 0;
        monedasGastadas = 0;
    }
}
