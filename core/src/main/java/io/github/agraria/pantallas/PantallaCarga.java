package io.github.agraria.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.agraria.Agraria;

public class PantallaCarga extends ScreenAdapter {
   
	private Agraria juego;
	private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture texturaMarco;
    private Texture texturaRelleno;
    
    private static final float MAP_WIDTH = 480f;
    private static final float MAP_HEIGHT = 320f;

    private float tiempoCargando = 0f;
    private final float TIEMPO_MINIMO_CARGA = 2.0f; // Tiempo en segundos que dura la pantalla
    private boolean cambioRealizado = false;

    public PantallaCarga(Agraria juego) {
        this.juego = juego;
    }
    
    @Override
    public void show() {
    	
    	map = new TmxMapLoader().load("pantallas/menu/pantallaMenu.tmx");
    	renderer = new OrthogonalTiledMapRenderer(map);
    	camera = new OrthographicCamera();
    	viewport = new StretchViewport(MAP_WIDTH, MAP_HEIGHT, camera);
    	viewport.apply();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = juego.getGestorFuentes().getFuentePrincipal();
        
        texturaMarco = new Texture(
        	    Gdx.files.internal("pantallas/menu/progress_frame_64x10.png")
        	);

        	texturaRelleno = new Texture(
        	    Gdx.files.internal("pantallas/menu/progress_fill_60x6.png")
        	);

        	texturaMarco.setFilter(
        	    Texture.TextureFilter.Nearest,
        	    Texture.TextureFilter.Nearest
        	);

        	texturaRelleno.setFilter(
        	    Texture.TextureFilter.Nearest,
        	    Texture.TextureFilter.Nearest
        	);
    }


@Override
public void render(float delta) {

    Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    tiempoCargando += delta;
    float progreso = Math.min(1f, tiempoCargando / TIEMPO_MINIMO_CARGA);

    viewport.apply();
    camera.update();
    renderer.setView(camera);
    renderer.render();
    // Dimensiones y posición de la barra
    float anchoBarraMax = 240f;
    float altoBarra = 12f;

    float xBarra = (MAP_WIDTH - anchoBarraMax) / 2f;
    float yBarra = 80f;

    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    // Primero dibujamos el marco
    batch.draw(
        texturaMarco,
        xBarra,
        yBarra,
        240f,
        20f
    );

    // Después dibujamos el relleno verde
    batch.draw(
        texturaRelleno,
        xBarra + 7f,
        yBarra + 4f,
        225f * progreso,
        12f
    );

    batch.end();

    // Determinar texto
    String textoCarga = "Hola...";

    if (progreso > 0.6f) {
        textoCarga = "Klk gente...";
    }

    if (progreso >= 1f) {
        textoCarga = "Listo";
    }

    // Dibujar texto
    batch.setProjectionMatrix(camera.combined);
    batch.begin();

    
    font.setColor(new Color(0.30f, 0.17f, 0.10f, 1f));
    font.getData().setScale(1.1f);

    font.draw(batch, textoCarga, xBarra, yBarra + 45);

    font.draw(
        batch,
        (int)(progreso * 100) + "%",
        xBarra + anchoBarraMax + 8,
        yBarra + 21
    );

    font.getData().setScale(1f);
    
    

    batch.end();

    // Cambiar a la pantalla de granja
    if (tiempoCargando >= TIEMPO_MINIMO_CARGA && !cambioRealizado) {
        cambioRealizado = true;
        juego.setScreen(new PantallaGranja(juego));
    }
}


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}