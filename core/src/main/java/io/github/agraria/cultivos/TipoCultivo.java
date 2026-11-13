package io.github.agraria.cultivos;

import io.github.agraria.inventario.*;
import io.github.agraria.elementos.Item;

public enum TipoCultivo {



	ZANAHORIA("Zanahoria", 10, 15, 720, 15, "cultivos/zanahoriaEtapas.png", "cultivos/zanahoriaItem.png", "cultivos/cultivoPodrido.png", 4, 1),
	PAPA("Papa", 20, 30, 840, 21, "cultivos/papaEtapas.png", "cultivos/papaItem.png", "cultivos/cultivoPodrido.png", 5, 3),
	ARROZ("Arroz", 15, 22, 780, 18, "cultivos/arrozEtapas.png", "cultivos/arrozItem.png", "cultivos/cultivoPodrido.png", 5, 2),
	BROCOLI("Brocoli", 25, 38, 1080, 27, "cultivos/brocoliEtapas.png", "cultivos/brocoliItem.png", "cultivos/cultivoPodrido.png", 5, 5),
	COLRIZADO("Col rizado", 20, 30, 960, 24, "cultivos/colrizadoEtapas.png", "cultivos/colrizadoItem.png", "cultivos/cultivoPodrido.png", 5, 4),
	CALABAZA("Calabaza", 35, 55, 1200, 31, "cultivos/calabazaEtapas.png", "cultivos/calabazaItem.png", "cultivos/cultivoPodrido.png", 6, 6),
	MELON("Melon", 40, 65, 1080, 35, "cultivos/melonEtapas.png", "cultivos/melonItem.png", "cultivos/cultivoPodrido.png", 6, 7);

    private final String nombre;
    private final int precioCompra;
    private final int precioVenta;
    private final int minutosPorEtapa;
    private final int xpRecompensa;
    private final String rutaTextura;
	private String rutaTexturaItem;
	private final String rutaTexturaPodrido;
    private final int cantidadEtapas;
    private final int nivelRequerido;

    TipoCultivo(String nombre, int precioCompra, int precioVenta, int minutosPorEtapa, int xpRecompensa, String rutaTextura, String rutaTexturaItem, String rutaTexturaPodrido, int cantidadEtapas, int nivelRequerido) {

        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.minutosPorEtapa = minutosPorEtapa;
        this.xpRecompensa = xpRecompensa;
        this.rutaTextura = rutaTextura;
        this.rutaTexturaItem = rutaTexturaItem;
        this.rutaTexturaPodrido = rutaTexturaPodrido;
        this.cantidadEtapas = cantidadEtapas;
        this.nivelRequerido = nivelRequerido;

    }
    
    
    public String getRutaTexturaItem() { return rutaTexturaItem; }

    
    
    public Item crearItemSemilla() {
        String id = "semilla_" + this.name().toLowerCase();
        return new Item(id, "Semilla de " + nombre, TipoItem.SEMILLA, this, "cultivos/sobre_" + this.name().toLowerCase() + ".png", 64);
    }

    
    
    public Item crearItemCosecha() {
        String id = "cosecha_" + this.name().toLowerCase();
        return new Item(id, nombre, TipoItem.COSECHA, this, rutaTexturaItem, 64);
    }

    
    
    public Item crearItemPodrido() {
        String id = "cultivo_podrido";
        return new Item(id, "Cultivo Podrido", TipoItem.COSECHA, null, "cultivos/cultivoPodrido.png", 64);
    }

    
    
    public String getNombre() { return nombre; }
    public int getPrecioCompra() { return precioCompra; }
    public int getPrecioVenta() { return precioVenta; }
    public int getMinutosPorEtapa() { return minutosPorEtapa; }
    public int getXpRecompensa() { return xpRecompensa; }
    public int getXpPlantar() { return Math.max(1, xpRecompensa / 3); }
    public int getNivelRequerido() { return nivelRequerido; }
    public String getRutaTextura() { return rutaTextura; }
    public String getRutaTexturaPodrido() { return rutaTexturaPodrido; }
    public int getCantidadEtapas() { return cantidadEtapas; }

}