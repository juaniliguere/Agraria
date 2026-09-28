package io.github.agraria.inventario;

import io.github.agraria.elementos.Item;

public class CasillaInventario {

    private Item item;
    private int cantidad;

    public CasillaInventario() {
        this.item = null;
        this.cantidad = 0;
    }

    public CasillaInventario(Item item, int cantidad) {
        this.item = item;
        this.cantidad = cantidad;
    }

    public boolean estaVacia() {
        return item == null || cantidad <= 0;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        if (this.cantidad <= 0) {
            limpiar();
        }
    }

    public void limpiar() {
        this.item = null;
        this.cantidad = 0;
    }
}