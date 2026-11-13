package io.github.agraria.elementos;

import io.github.agraria.cultivos.TipoCultivo;
import io.github.agraria.inventario.TipoItem;

public class Item {

    private String id;
    private String nombre;
    private TipoItem tipo;
    private TipoCultivo tipoCultivo; 
    private String rutaTextura;
    private int maxApilable;

    public Item(String id, String nombre, TipoItem tipo, String rutaTextura, int maxApilable) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.rutaTextura = rutaTextura;
        this.maxApilable = maxApilable;
        this.tipoCultivo = null;
    }

    public Item(String id, String nombre, TipoItem tipo, TipoCultivo tipoCultivo, String rutaTextura, int maxApilable) {
        this(id, nombre, tipo, rutaTextura, maxApilable);
        this.tipoCultivo = tipoCultivo;
    }
    
    public boolean esSemilla() {
        return tipo == TipoItem.SEMILLA;
    }

    public boolean esHerramienta() {
        return tipo == TipoItem.HERRAMIENTA;
    }

    public boolean esCosecha() {
        return tipo == TipoItem.COSECHA;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoItem getTipo() {
        return tipo;
    }

    public TipoCultivo getTipoCultivo() {
        return tipoCultivo;
    }

    public String getRutaTextura() {
        return rutaTextura;
    }

    public int getMaxApilable() {
        return maxApilable;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return id != null ? id.equals(item.id) : item.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}