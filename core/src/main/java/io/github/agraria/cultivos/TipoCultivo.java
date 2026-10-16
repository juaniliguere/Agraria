package io.github.agraria.cultivos;

import io.github.agraria.inventario.*;
import io.github.agraria.elementos.Item;

public enum TipoCultivo {

	    // Nombre, Precio Compra, Precio Venta, Minutos por Etapa (4 etapas), XP, Ruta de Textura
	    ZANAHORIA("Zanahoria", 10, 12, 60, 15, "cultivos/zanahoriaEtapas.png", "cultivos/zanahoriaItem.png", 4),
	    PAPA("Papa", 25, 30, 60, 15, "cultivos/papaEtapas.png", "cultivos/papaItem.png", 5);

	    private final String nombre;
	    private final int precioCompra;
	    private final int precioVenta;
	    private final int minutosPorEtapa;
	    private final int xpRecompensa;
	    private final String rutaTextura;
		private String rutaTexturaItem;
	    private final int cantidadEtapas;

	    TipoCultivo(String nombre, int precioCompra, int precioVenta, int minutosPorEtapa, int xpRecompensa, String rutaTextura, String rutaTexturaItem, int cantidadEtapas) {
	    	
	        this.nombre = nombre;
	        this.precioCompra = precioCompra;
	        this.precioVenta = precioVenta;
	        this.minutosPorEtapa = minutosPorEtapa;
	        this.xpRecompensa = xpRecompensa;
	        this.rutaTextura = rutaTextura;
	        this.rutaTexturaItem = rutaTexturaItem;
	        this.cantidadEtapas = cantidadEtapas;
	        
	    }
	    public String getRutaTexturaItem() { return rutaTexturaItem; }
	    
	    public Item crearItemSemilla() {
	        String id = "semilla_" + this.name().toLowerCase();
	        // Acá le pasás la ruta de la imagen del sobre de semillas
	        return new Item(id, "Semilla de " + nombre, TipoItem.SEMILLA, this, "cultivos/sobre_" + this.name().toLowerCase() + ".png", 64);
	    }

	    public Item crearItemCosecha() {
	        String id = "cosecha_" + this.name().toLowerCase();
	        // Acá le pasás la ruta de la imagen del cultivo cosechado (la que ya usás)
	        return new Item(id, nombre, TipoItem.COSECHA, this, rutaTexturaItem, 64);
	    }

	    public String getNombre() { return nombre; }
	    public int getPrecioCompra() { return precioCompra; }
	    public int getPrecioVenta() { return precioVenta; }
	    public int getMinutosPorEtapa() { return minutosPorEtapa; }
	    public int getXpRecompensa() { return xpRecompensa; }
	    public String getRutaTextura() { return rutaTextura; }
	    public int getCantidadEtapas() {return cantidadEtapas; }
	    
	}
