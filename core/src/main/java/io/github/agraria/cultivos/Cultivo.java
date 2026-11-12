package io.github.agraria.cultivos;

import io.github.agraria.elementos.Item;
import io.github.agraria.inventario.Inventario;

public class Cultivo {

    private final TipoCultivo tipo;

    private int etapaActual;
    private int minutosAcumulados;
    private boolean podrido;

    public Cultivo(TipoCultivo tipo) {
        this.tipo = tipo;
        this.etapaActual = 0;
        this.minutosAcumulados = 0;
        this.podrido = false;
    }

    public void hacerCrecer(int minutosPasados) {

        if (minutosPasados <= 0 || isCosechable() || podrido) {
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
        // cosechable cuando llega a la ultima etapa (ej: si tiene 4 etapas van a ir d 0 a 3)
    	return !podrido && (etapaActual >= tipo.getCantidadEtapas() - 1);
    }

    
    public void pudrir() {
        this.podrido = true;
    }

    public boolean isPodrido() {
        return podrido;
    }
    
    public boolean cosechar(Inventario inventario) {
        if (!isCosechable()) {
            return false;
        }

        // 1. Generamos el item d la cosecha
        Item producto = tipo.crearItemCosecha();
        int cantidadObtenida = 1; 

        // 2. Intentamos guardar en el inventario
        boolean guardado = inventario.agregarItem(producto, cantidadObtenida);

        return guardado;
    }

    public TipoCultivo getTipo() {
        return tipo;
    }

    public int getEtapaActual() {
        return etapaActual;
    }

}