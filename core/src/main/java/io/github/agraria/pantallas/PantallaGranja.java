package io.github.agraria.pantallas;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.agraria.personajes.Personaje;

// Importá tus otras clases (SpriteBatch, Texture, Camera, etc.)

public class PantallaGranja extends ScreenAdapter {

	private SpriteBatch batch;
	private Personaje jugador;

	private TiledMap mapa;
	private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camara;
    private Viewport viewport;

    	// Lista de Polígonos de colisión (cubre rectángulos y formas con diagonales)
    private Array<Polygon> colisionesMapa;

   	// Dimensiones en píxeles del mapa completo
    private float anchoMapaPixels;
    private float altoMapaPixels;

    public static final int V_WIDTH = 800;
    public static final int V_HEIGHT = 600;
    
    private int idxAbajo;
    private int idxArriba;


    public PantallaGranja() {

        batch = new SpriteBatch();
        jugador = new Personaje(125, 125);

        // 1. Configuración de Cámara y Viewport
        camara = new OrthographicCamera();
        viewport = new FitViewport(V_WIDTH, V_HEIGHT, camara);
        viewport.apply();

        // Configurar zoom inicial (0.5f es más cerca, ideal para Pixel Art)
        camara.zoom = 0.5f;

        // 2. Cargar el mapa con filtros de textura nítidos (Nearest)
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMinFilter = Texture.TextureFilter.Nearest;
        params.textureMagFilter = Texture.TextureFilter.Nearest;

        mapa = new TmxMapLoader().load("pantallas/zona1/AgrariaMapa.tmx", params);
        mapRenderer = new OrthogonalTiledMapRenderer(mapa);

        // 3. Obtener tamaño total del mapa
        MapProperties prop = mapa.getProperties();
        int mapWidthTiles = prop.get("width", Integer.class);
        int mapHeightTiles = prop.get("height", Integer.class);
        int tileWidth = prop.get("tilewidth", Integer.class);
        int tileHeight = prop.get("tileheight", Integer.class);

        anchoMapaPixels = mapWidthTiles * tileWidth;
        altoMapaPixels = mapHeightTiles * tileHeight;

        // 4. Cargar colisiones (Polígonos y Rectángulos) desde Tiled
        colisionesMapa = new Array<>();
        if (mapa.getLayers().get("Colisiones") != null) {
            for (MapObject objeto : mapa.getLayers().get("Colisiones").getObjects()) {
                
                // Si dibujaste un Polígono en Tiled
                if (objeto instanceof PolygonMapObject) {
                    colisionesMapa.add(((PolygonMapObject) objeto).getPolygon());
                } 
                // Si dibujaste un Rectángulo en Tiled, lo convertimos a Polígono
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
        
        idxAbajo = mapa.getLayers().getIndex("abajo");
        idxArriba = mapa.getLayers().getIndex("arriba");

    }

    @Override
    public void show() {
        // Opcional: Se ejecuta justo cuando esta pantalla pasa a ser la activa.
        // Podés dejarlo vacío o poner la carga si preferís no usar el constructor.
    }

	@Override
	public void render(float delta) {

        // 1. Control de Zoom con Teclado (+ y -)
        if (Gdx.input.isKeyPressed(Keys.PLUS) || Gdx.input.isKeyPressed(Keys.EQUALS)) {
            camara.zoom -= 0.5f * delta;
        }
        if (Gdx.input.isKeyPressed(Keys.MINUS)) {
            camara.zoom += 0.5f * delta;
        }
        camara.zoom = MathUtils.clamp(camara.zoom, 0.2f, 1.5f);

        // 2. Movimiento y colisiones del jugador
        jugador.actualizar(delta, colisionesMapa, anchoMapaPixels, altoMapaPixels);

        // 3. Centrar cámara en el personaje y limitar a los bordes del mapa
        actualizarCamara();

        // 4. Dibujar Pantalla
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        mapRenderer.setView(camara);
        mapRenderer.render(new int[] {idxAbajo});

        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        jugador.renderizar(batch);
        batch.end();
        
        mapRenderer.render(new int[] {idxArriba});
    }

    private void actualizarCamara() {
        float medioAnchoCamara = (camara.viewportWidth * camara.zoom) / 2f;
        float medioAltoCamara = (camara.viewportHeight * camara.zoom) / 2f;

        float camX = MathUtils.clamp(jugador.getX(), medioAnchoCamara, anchoMapaPixels - medioAnchoCamara);
        float camY = MathUtils.clamp(jugador.getY(), medioAltoCamara, altoMapaPixels - medioAltoCamara);

        camara.position.set(camX, camY, 0);
        camara.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        jugador.liberarRecursos();
        mapa.dispose();
        mapRenderer.dispose();
    }
}
