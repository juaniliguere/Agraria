package io.github.agraria.cultivos;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class GestorCultivos {

    private final Parcela[][] grilla;

    private final int columnas;
    private final int filas;

    private final int tamanoTilePixels;

    private ObjectMap<TipoCultivo, TextureRegion[]> etapasCultivos;
    private ObjectMap<TipoCultivo, Texture> texturasCultivos;
    private ObjectMap<TipoCultivo, Texture> texturasPodridas;

    public GestorCultivos(int columnas, int filas, int tamanoTilePixels, boolean[][] matrizPlantable) {

        this.columnas = columnas;
        this.filas = filas;
        this.tamanoTilePixels = tamanoTilePixels;
        this.grilla = new Parcela[filas][columnas];

        inicializarGrilla(matrizPlantable);
        
    }

    
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

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                matriz[f][c] = capaPlantable != null && capaPlantable.getCell(c, f) != null;
                
            }
        }

        return matriz;
    }
    
    
    private void inicializarGrilla(boolean[][] matrizPlantable) {

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {

                boolean esPlantable = matrizPlantable[f][c];
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


    private int xPixelAColumna(float xPixel) {
        return (int) (xPixel / tamanoTilePixels);
    }


    private int yPixelAFila(float yPixel) {
        return (int) (yPixel / tamanoTilePixels);
    }


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
    

    public void hacerLlover() {
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                grilla[f][c].recibirLluvia();
            }
        }
    }
    
    
    public void cargarTexturas() {
        etapasCultivos = new ObjectMap<>();
        texturasCultivos = new ObjectMap<>();
        texturasPodridas = new ObjectMap<>();

        for (TipoCultivo tipo : TipoCultivo.values()) {
            Texture textura = new Texture(tipo.getRutaTextura());
            textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            texturasCultivos.put(tipo, textura);

            int cantidadEtapas = tipo.getCantidadEtapas();
            int anchoEtapa = textura.getWidth() / cantidadEtapas;
            int altoEtapa = textura.getHeight();

            TextureRegion[] etapas = new TextureRegion[cantidadEtapas];
            for (int i = 0; i < cantidadEtapas; i++) {
                etapas[i] = new TextureRegion(textura, i * anchoEtapa, 0, anchoEtapa, altoEtapa);
            }
            etapasCultivos.put(tipo, etapas);
            
            Texture texturaPodrida = new Texture(tipo.getRutaTexturaPodrido());
            texturaPodrida.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            texturasPodridas.put(tipo, texturaPodrida);
        }
    }
    
    
    public void procesarPudrir() {

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                grilla[f][c].procesarPudrir();
            }
        }
    }
    
    
    public void renderizar(SpriteBatch batch) {
    	
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                Parcela parcela = grilla[fila][col];

                if (parcela == null || !parcela.tieneCultivo()) {
                    continue;
                }

                Cultivo cultivo = parcela.getCultivoActual();
                TipoCultivo tipo = cultivo.getTipo();

                float xPx = col * tamanoTilePixels;
                float yPx = fila * tamanoTilePixels;

                if (cultivo.isPodrido()) {
                    Texture texturaPodrida = texturasPodridas.get(tipo);
                    if (texturaPodrida != null) {
                        batch.draw(texturaPodrida, xPx, yPx, tamanoTilePixels, tamanoTilePixels);
                    }
                } else {
                    int etapa = cultivo.getEtapaActual();
                    TextureRegion[] etapas = etapasCultivos.get(tipo);

                    if (etapas != null && etapa >= 0 && etapa < etapas.length) {
                        batch.draw(etapas[etapa], xPx, yPx, tamanoTilePixels, tamanoTilePixels);
                    }
                }
            }
        }
    }

    
	public void dispose() {
        for (Texture textura : texturasCultivos.values()) {
            textura.dispose();
        }
        for (Texture textura : texturasPodridas.values()) {
            textura.dispose();
        }
	}
	
}


