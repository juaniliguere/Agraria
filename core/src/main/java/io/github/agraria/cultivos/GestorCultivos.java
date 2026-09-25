package io.github.agraria.cultivos;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class GestorCultivos {

    private final Parcela[][] grilla;

    private final int columnas;
    private final int filas;

    private final int tamanoTilePixels;


    public GestorCultivos(int columnas, int filas, int tamanoTilePixels, boolean[][] matrizPlantable) {

        this.columnas = columnas;
        this.filas = filas;
        this.tamanoTilePixels = tamanoTilePixels;

        // [fila][columna]
        this.grilla = new Parcela[filas][columnas];

        inicializarGrilla(matrizPlantable);
        
    }

    /*
     * Genera una matriz indicando qué celdas del mapa son plantables.
     * true  = hay un tile en esa posición → se puede plantar
     * false = no hay tile → no se puede plantar
     */
    public static boolean[][] generarMatrizPlantableDesdeMapa(TiledMap mapa, int columnas, int filas) {

        boolean[][] matriz = new boolean[filas][columnas];

        MapLayer capaAbajo = mapa.getLayers().get("abajo");

        TiledMapTileLayer capaPlantable = null;

        if (capaAbajo instanceof MapGroupLayer) {

        	MapGroupLayer grupoAbajo = (MapGroupLayer) capaAbajo;
            MapLayer capa = grupoAbajo.getLayers().get("plantable");

            if (capa instanceof TiledMapTileLayer) {
                capaPlantable = (TiledMapTileLayer) capa;
            }
            
        }

        // Recorremos todas las posiciones de la grilla
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {

                // Si existe una celda con un tile, esa posición es plantable
                matriz[f][c] =
                        capaPlantable != null
                        && capaPlantable.getCell(c, f) != null;
            }
        }

        return matriz;
    }

    /**
     * Crea una Parcela para cada posición de la matriz.
     */
    private void inicializarGrilla(boolean[][] matrizPlantable) {

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {

                boolean esPlantable = matrizPlantable[f][c];

                // Cada posición del mapa tiene su propia Parcela
                grilla[f][c] = new Parcela(c, f, esPlantable);
            }
        }
    }

    public boolean plantarEn(float xPixel, float yPixel, TipoCultivo tipo) {

        int col = xPixelAColumna(xPixel);
        int fila = yPixelAFila(yPixel);

        if (!esCoordenadaValida(col, fila)) {
            return false;
        }

        return grilla[fila][col].plantar(tipo);
    }

    /**
     * Intenta cosechar usando coordenadas del mundo en píxeles. Devuelve el Cultivo cosechado o null si no se pudo cosechar.
     */
    public Cultivo cosecharEn(float xPixel, float yPixel) {

        int col = xPixelAColumna(xPixel);
        int fila = yPixelAFila(yPixel);

        if (!esCoordenadaValida(col, fila)) {
            return null;
        }

        return grilla[fila][col].cosechar();
    }

    public void pasarTiempo(int minutosPasados) {

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {

                grilla[f][c].pasarTiempo(minutosPasados);
            }
        }
    }

    public Parcela getParcelaEnPx(float xPixel, float yPixel) {

        int col = xPixelAColumna(xPixel);
        int fila = yPixelAFila(yPixel);

        if (!esCoordenadaValida(col, fila)) {
            return null;
        }

        return grilla[fila][col];
    }

    public Parcela getParcela(int col, int fila) {

        if (!esCoordenadaValida(col, fila)) {
            return null;
        }

        return grilla[fila][col];
    }

    // Convierte una coordenada X del mundo en una columna de la grilla
    private int xPixelAColumna(float xPixel) {
        return (int) (xPixel / tamanoTilePixels);
    }

    // Convierte una coordenada Y del mundo en una fila de la grilla
    private int yPixelAFila(float yPixel) {
        return (int) (yPixel / tamanoTilePixels);
    }

    // Comprueba que la posición esté dentro de los límites de la matriz
    private boolean esCoordenadaValida(int col, int fila) {
    	
        return col >= 0 && col < columnas && fila >= 0 && fila < filas;
        
    }

    public int getColumnas() {
        return columnas;
    }

    public int getFilas() {
        return filas;
    }

    public int getTamanoTilePixels() {
        return tamanoTilePixels;
    }
}

