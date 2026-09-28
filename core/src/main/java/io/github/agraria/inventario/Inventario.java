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

        // Inicializamos las casillas vacías para evitar NullPointerException
        for (int i = 0; i < capacidad; i++) {
            casillas[i] = new CasillaInventario();
        }
    }

    // Devuelve el ítem de la casilla activa en la mano del personaje
    public Item getItemSeleccionado() {
        CasillaInventario casilla = casillas[casillaSeleccionada];
        if (!casilla.estaVacia()) {
            return casilla.getItem();
        }
        return null;
    }

    // Si el ítem seleccionado es una semilla, devuelve su TipoCultivo
    public TipoCultivo getCultivoSeleccionado() {
        Item item = getItemSeleccionado();
        if (item != null && item.esSemilla()) {
            return item.getTipoCultivo();
        }
        return null;
    }

    // Consume 1 unidad del ítem que se acaba de usar/plantar
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
    
    public boolean agregarItem(Item item, int cantidad) {

    	    int cantidadRestante = cantidad;

    	    // Primero intentamos completar casillas que ya tienen este Item
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

    	    // Si todavía queda cantidad, buscamos casillas vacías
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

    	    // No hubo espacio suficiente para guardar todo
    	    return false;
    	
    }
}