package io.github.agraria.cultivos;

public class Parcela {

    // Posición de esta parcela dentro de la grilla
    private final int x;
    private final int y;

    // Indica si en esta posición del mapa se puede plantar
    private final boolean esPlantable;

    // Si no hay nada plantado, es null
    private Cultivo cultivoActual;

    public Parcela(int x, int y, boolean esPlantable) {
        this.x = x;
        this.y = y;
        this.esPlantable = esPlantable;

        // La parcela comienza vacía
        this.cultivoActual = null;
    }

    // Se puede plantar solamente si el terreno es válido y está vacío
    public boolean puedePlantar() {
        return esPlantable && cultivoActual == null;
    }

    // Intenta plantar un cultivo en esta parcela
    public boolean plantar(TipoCultivo tipo) {

        if (!puedePlantar()) {
            return false;
        }

        cultivoActual = new Cultivo(tipo);
        return true;
    }

    // Intenta cosechar el cultivo si ya está listo
    public Cultivo cosechar() {

        if (cultivoActual != null && cultivoActual.isCosechable()) {

            // Guardamos el cultivo antes de vaciar la parcela
            Cultivo cosechado = cultivoActual;

            // La parcela vuelve a quedar vacía
            cultivoActual = null;

            return cosechado;
        }

        // No había cultivo o todavía no estaba listo
        return null;
    }

    // Le informa al cultivo cuánto tiempo pasó
    public void pasarTiempo(int minutosPasados) {

        if (cultivoActual != null) {
            cultivoActual.hacerCrecer(minutosPasados);
        }
    }

    public boolean isEsPlantable() {
        return esPlantable;
    }

    public boolean tieneCultivo() {
        return cultivoActual != null;
    }

    public Cultivo getCultivoActual() {
        return cultivoActual;
    }
}