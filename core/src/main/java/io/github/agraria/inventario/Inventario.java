package io.github.agraria.inventario;

import io.github.agraria.cultivos.TipoCultivo;
import io.github.agraria.elementos.Item;


public class Inventario {

    private CasillaInventario[] casillas;
    private int capacidad;
    private int casillaSeleccionada;

    public Inventario(int capacidad) {
        this.capacidad = capacidad;
        this.casillas = new CasillaInventario[capacidad];
        this.casillaSeleccionada = 0;

        for (int i = 0; i < capacidad; i++) {
            casillas[i] = new CasillaInventario();
        }
    }

    public Item getItemSeleccionado() {
        CasillaInventario casilla = casillas[casillaSeleccionada];
        if (!casilla.estaVacia()) {
            return casilla.getItem();
        }
        return null;
    }

    public TipoCultivo getCultivoSeleccionado() {
        Item item = getItemSeleccionado();
        if (item != null && item.esSemilla()) {
            return item.getTipoCultivo();
        }
        return null;
    }

    public void consumirSeleccionado(int cantidad) {
        CasillaInventario casilla = casillas[casillaSeleccionada];
        if (!casilla.estaVacia()) {
            casilla.setCantidad(casilla.getCantidad() - cantidad);
        }
    }

    public void setCasillaSeleccionada(int indice) {
        if (indice >= 0 && indice < capacidad) {
            this.casillaSeleccionada = indice;
        }
    }

    public int getCasillaSeleccionada() {
        return casillaSeleccionada;
    }
    
    public int getCapacidad() {
        return capacidad;
    }

    public void moverSeleccion(int pasos) {
        casillaSeleccionada = (casillaSeleccionada + pasos + capacidad) % capacidad;
    }
    
    public boolean agregarItem(Item item, int cantidad) {

    	    int cantidadRestante = cantidad;

    	    for (int i = 0; i < capacidad; i++) {

    	        CasillaInventario casilla = casillas[i];

    	        if (!casilla.estaVacia() && casilla.getItem().equals(item)) {

    	            int espacioDisponible =
    	                    item.getMaxApilable() - casilla.getCantidad();

    	            if (espacioDisponible > 0) {

    	                int cantidadAAgregar =
    	                        Math.min(cantidadRestante, espacioDisponible);

    	                casilla.setCantidad(
    	                        casilla.getCantidad() + cantidadAAgregar
    	                );

    	                cantidadRestante -= cantidadAAgregar;

    	                if (cantidadRestante == 0) {
    	                    return true;
    	                }
    	            }
    	        }
    	    }

    	    for (int i = 0; i < capacidad; i++) {

    	        CasillaInventario casilla = casillas[i];

    	        if (casilla.estaVacia()) {

    	            int cantidadAAgregar =
    	                    Math.min(cantidadRestante, item.getMaxApilable());

    	            casilla.setItem(item);
    	            casilla.setCantidad(cantidadAAgregar);

    	            cantidadRestante -= cantidadAAgregar;

    	            if (cantidadRestante == 0) {
    	                return true;
    	            }
    	        }
    	    }

    	    return false;
    	
    }
    public CasillaInventario getCasilla(int indice) {
        if (indice >= 0 && indice < capacidad) {
            return casillas[indice];
        }
        return null;
    }
}