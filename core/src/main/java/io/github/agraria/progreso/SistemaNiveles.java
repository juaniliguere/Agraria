package io.github.agraria.progreso;

import io.github.agraria.cultivos.TipoCultivo;

public class SistemaNiveles {

    public interface Listener {
        void alSubirNivel(int nivelAnterior, int nivelNuevo);
    }

    public static final int NIVEL_MAXIMO = 10;

    private int nivel = 1;
    private int xpActual = 0;
    private Listener listener;

    public void setAlSubirNivel(Listener listener) {
        this.listener = listener;
    }

    public void ganarXp(int cantidad) {
        if (cantidad <= 0 || nivel >= NIVEL_MAXIMO) {
            return;
        }

        int nivelAnterior = nivel;
        xpActual += cantidad;

        while (nivel < NIVEL_MAXIMO && xpActual >= xpParaSiguienteNivel()) {
            xpActual -= xpParaSiguienteNivel();
            nivel++;
        }

        if (nivel >= NIVEL_MAXIMO) {
            xpActual = 0;
        }

        if (nivel > nivelAnterior && listener != null) {
            listener.alSubirNivel(nivelAnterior, nivel);
        }
    }

    public int xpParaSiguienteNivel() {
        return (int) (100 * Math.pow(nivel, 1.6));
    }

    public boolean estaDesbloqueado(TipoCultivo tipo) {
        return nivel >= tipo.getNivelRequerido();
    }

    public int getNivel() { return nivel; }
    public int getXpActual() { return xpActual; }

    public float getProgreso() {
        return nivel >= NIVEL_MAXIMO ? 1f : (float) xpActual / xpParaSiguienteNivel();
    }
}