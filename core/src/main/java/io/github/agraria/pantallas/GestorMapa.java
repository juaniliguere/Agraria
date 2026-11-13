package io.github.agraria.pantallas;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GestorMapa implements Disposable {

    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;

    private OrthographicCamera camara;
    private Viewport viewport;

    private Array<Polygon> colisionesMapa;
    private Rectangle zonaDormir;
    private Rectangle zonaTienda;
    private Rectangle zonaComprar;
    private Rectangle zonaSalir;

    private float anchoMapaPixels;
    private float altoMapaPixels;

    private int mapWidthTiles;
    private int mapHeightTiles;

    private int tileWidth;
    private int tileHeight;

    private int idxAbajo = -1;
    private int idxArriba = -1;

    public GestorMapa(String rutaMapa) {

        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();

        params.textureMinFilter = Texture.TextureFilter.Nearest;
        params.textureMagFilter = Texture.TextureFilter.Nearest;

        mapa = new TmxMapLoader().load(rutaMapa, params);
        mapRenderer = new OrthogonalTiledMapRenderer(mapa);

        cargarPropiedades();
        cargarColisiones();
        cargarZonaDormir();
        cargarZonaTienda();
        cargarZonaComprar();
        cargarZonaSalir();
        cargarCapas();
    }

    
    
    private void cargarPropiedades() {

        MapProperties prop = mapa.getProperties();

        mapWidthTiles = prop.get("width", Integer.class);
        mapHeightTiles = prop.get("height", Integer.class);

        tileWidth = prop.get("tilewidth", Integer.class);
        tileHeight = prop.get("tileheight", Integer.class);

        anchoMapaPixels = mapWidthTiles * tileWidth;
        altoMapaPixels = mapHeightTiles * tileHeight;
    }
    
    
    

    private void cargarColisiones() {

        colisionesMapa = new Array<>();
        if (mapa.getLayers().get("Colisiones") != null) {

            for (MapObject objeto :
                    mapa.getLayers().get("Colisiones").getObjects()) {

                if (objeto instanceof PolygonMapObject) {
                    colisionesMapa.add(((PolygonMapObject) objeto).getPolygon());
                }

                else if (objeto instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) objeto).getRectangle();

                    Polygon poly = new Polygon(new float[]{
                            0, 0,
                            rect.width, 0,
                            rect.width, rect.height,
                            0, rect.height
                    });

                    poly.setPosition(rect.x, rect.y);
                    colisionesMapa.add(poly);
                }
            }
        }
    }
    
    
    

    private void cargarZonaDormir() {

        if (mapa.getLayers().get("zonaDormir") == null) {
            return;
        }

        for (MapObject objeto :
                mapa.getLayers().get("zonaDormir").getObjects()) {

            if (objeto.getName() != null && objeto.getName().equals("zonaDormir") && objeto instanceof RectangleMapObject) {
                zonaDormir = ((RectangleMapObject) objeto).getRectangle();
                break;
            }
        }
    }

    
    
    
    private void cargarZonaTienda() {

        if (mapa.getLayers().get("zonaTienda") == null) {
            return;
        }

        for (MapObject objeto :
                mapa.getLayers().get("zonaTienda").getObjects()) {

            if (objeto.getName() != null &&
                objeto.getName().equals("zonaTienda") &&
                objeto instanceof RectangleMapObject) {

                zonaTienda =
                        ((RectangleMapObject) objeto).getRectangle();

                break;
            }
        }
    }

    
    
    
    private void cargarZonaComprar() {

        if (mapa.getLayers().get("zonaComprar") == null) {
            return;
        }

        for (MapObject objeto : mapa.getLayers().get("zonaComprar").getObjects()) {

            if (objeto.getName() != null &&
                objeto.getName().equals("zonaComprar") &&
                objeto instanceof RectangleMapObject) {

                zonaComprar = ((RectangleMapObject) objeto).getRectangle();
                break;
            }
        }
    }
    
    
    

    private void cargarZonaSalir() {

        if (mapa.getLayers().get("zonaSalir") == null) {
            return;
        }

        for (MapObject objeto : mapa.getLayers().get("zonaSalir").getObjects()) {

            if (objeto.getName() != null &&
                objeto.getName().equals("zonaSalir") &&
                objeto instanceof RectangleMapObject) {

                zonaSalir = ((RectangleMapObject) objeto).getRectangle();
                break;
            }
        }
    }
    
    
    

    private void cargarCapas() {

        if (mapa.getLayers().get("abajo") != null) {
            idxAbajo = mapa.getLayers().getIndex("abajo");
        }

        if (mapa.getLayers().get("arriba") != null) {
            idxArriba = mapa.getLayers().getIndex("arriba");
        }
    }
    
    
    

    public void setView(OrthographicCamera camara) {

        mapRenderer.setView(camara);
    }

    
    
    
    public void renderAbajo() {

        if (idxAbajo != -1) {
            mapRenderer.render(new int[]{idxAbajo});
        }
    }

    
    
    
    public void renderArriba() {

        if (idxArriba != -1) {
            mapRenderer.render(new int[]{idxArriba});
        }
    }

    
    
    
    public TiledMap getMapa() { return mapa; }
    public Array<Polygon> getColisionesMapa() { return colisionesMapa; }
    public Rectangle getZonaDormir() { return zonaDormir; }
    public Rectangle getZonaTienda() { return zonaTienda; }
    public Rectangle getZonaComprar() { return zonaComprar; }
    public Rectangle getZonaSalir() { return zonaSalir; }
    public float getAnchoMapaPixels() { return anchoMapaPixels; }
    public float getAltoMapaPixels() { return altoMapaPixels; }
    public int getMapWidthTiles() { return mapWidthTiles; }
    public int getMapHeightTiles() { return mapHeightTiles; }
    public int getTileWidth() { return tileWidth; }
    public int getTileHeight() { return tileHeight; }

    
    
    
    public void dispose() {
        mapa.dispose();
        mapRenderer.dispose();
    }
}