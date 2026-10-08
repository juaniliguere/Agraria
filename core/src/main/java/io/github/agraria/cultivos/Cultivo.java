package io.github.agraria.cultivos;

import io.github.agraria.elementos.Item;
import io.github.agraria.inventario.Inventario;

public class Cultivo {

    private static final int ETAPA_COSECHABLE = 3;

    private final TipoCultivo tipo;

    private int etapaActual;
    private int minutosAcumulados;

    public Cultivo(TipoCultivo tipo) {
        this.tipo = tipo;
        this.etapaActual = 0;
        this.minutosAcumulados = 0;
    }

    public void hacerCrecer(int minutosPasados) {

        if (minutosPasados <= 0 || isCosechable()) {
            return;
        }

        minutosAcumulados += minutosPasados;

        while (minutosAcumulados >= tipo.getMinutosPorEtapa()
                && !isCosechable()) {

            minutosAcumulados -= tipo.getMinutosPorEtapa();
            etapaActual++;
        }
    }
    
    public boolean isCosechable() {
        // Cosechable cuando llega a la última etapa (ej: si tiene 4 etapas, las etapas van de 0 a 3)
        return etapaActual >= tipo.getCantidadEtapas() - 1;
    }

    /**
     * Intenta cosechar el cultivo agregando el item al inventario.
     * @param inventario El inventario del jugador
     * @return true si se cosechó con éxito (había espacio)
     */
    public boolean cosechar(Inventario inventario) {
        if (!isCosechable()) {
            return false;
        }

        // 1. Generamos el item de la cosecha
        Item producto = tipo.crearItemCosecha();
        int cantidadObtenida = 1; // Podés hacer un Math.random() acá si querés dar entre 1 y 3 frutos

        // 2. Intentamos guardar en el inventario
        boolean guardado = inventario.agregarItem(producto, cantidadObtenida);

        return guardado; // Si devuelve true, la lógica del mapa/tierra borra esta planta
    }

    public TipoCultivo getTipo() {
        return tipo;
    }

    public int getEtapaActual() {
        return etapaActual;
    }

}