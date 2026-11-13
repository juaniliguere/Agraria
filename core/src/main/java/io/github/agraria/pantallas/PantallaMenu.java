package io.github.agraria.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;

import io.github.agraria.ui.GestorFuentes;
import io.github.agraria.Agraria;
import io.github.agraria.sonidos.GestorAudio;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

// Importaciones de Scene2D y UI
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class PantallaMenu implements Screen {

	
	private Texture texturaPanel;
	private Texture texturaBotonNormal;
	private Texture texturaBotonPresionado;
	private Texture texturaBotonHover;
	private NinePatch panelMenu;
	private GestorFuentes gestorFuentes;
	private BitmapFont fuente;

	private TextButton btnJugar;
	private TextButton btnConfiguracion;
	private TextButton btnControles;
	private TextButton btnSalir;
	
	private Agraria juego;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    
    private Table panelPrincipal;
    private Table tablaMenu;

    private Stage stage;

    private ShapeRenderer shapeRenderer;
    private float fadeAlpha = 0f;          
    private boolean isTransitioning = false; 

    private static final float MAP_WIDTH = 480; 
    private static final float MAP_HEIGHT = 320;
    
    private GestorAudio gestorAudio;

    public PantallaMenu(Agraria juego) {
        this.juego = juego;

        this.gestorAudio = new GestorAudio();
    }
    
    @Override
    public void show() {

    	gestorAudio.reproducirMusicaFondo();
    	
        map = new TmxMapLoader().load("pantallas/menu/pantallaMenu.tmx");

        camera = new OrthographicCamera();
        viewport = new StretchViewport(MAP_WIDTH, MAP_HEIGHT, camera);
        viewport.apply();

        camera.position.set(MAP_WIDTH / 2f, MAP_HEIGHT / 2f, 0);
        camera.update();

        renderer = new OrthogonalTiledMapRenderer(map);

        shapeRenderer = new ShapeRenderer();
        
     
        texturaPanel = new Texture(
            Gdx.files.internal("pantallas/menu/panel_48x48.png")
        );
        
        panelMenu = new NinePatch(
        	    texturaPanel,
        	    12, 12, 12, 12
        	);

        texturaBotonNormal = new Texture(
            Gdx.files.internal("pantallas/menu/button_L_normal.png")
        );

        texturaBotonPresionado = new Texture(
            Gdx.files.internal("pantallas/menu/button_L_pressed.png")
        );

        texturaBotonHover = new Texture(
            Gdx.files.internal("pantallas/menu/button_L_hover.png")
        );

        texturaPanel.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        texturaBotonNormal.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        texturaBotonPresionado.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        texturaBotonHover.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        gestorFuentes = new GestorFuentes();
        gestorFuentes.cargar();
        fuente = gestorFuentes.getFuentePrincipal();

        stage = new Stage(viewport);

        Gdx.input.setInputProcessor(stage);
        
             panelPrincipal = new Table();
        panelPrincipal.setBackground(new NinePatchDrawable(panelMenu));

        panelPrincipal.setSize(190, 220);
        panelPrincipal.setPosition(
            MAP_WIDTH / 2f - panelPrincipal.getWidth() / 2f,
            MAP_HEIGHT / 2f - panelPrincipal.getHeight() / 2f
        );

        tablaMenu = new Table();
        tablaMenu.defaults().padBottom(10);

        panelPrincipal.add(tablaMenu).expand().fill();

        stage.addActor(panelPrincipal);
        
        TextButton.TextButtonStyle estiloBoton = crearEstiloBoton();

        btnJugar = new TextButton("JUGAR", estiloBoton);
        btnConfiguracion = new TextButton("CONFIGURACIÓN", estiloBoton);
        btnControles = new TextButton("CONTROLES", estiloBoton);
        btnSalir = new TextButton("SALIR", estiloBoton);

        tablaMenu.add(btnJugar).width(160).height(40).row();
        tablaMenu.add(btnConfiguracion).width(160).height(40).row();
        tablaMenu.add(btnControles).width(160).height(40).row();
        tablaMenu.add(btnSalir).width(160).height(40).row();
        
        
        btnJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                gestorAudio.reproducirApretarBoton();

                isTransitioning = true;
            }
        });

        btnConfiguracion.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                gestorAudio.reproducirApretarBoton();

                juego.setScreen(new PantallaConfiguracion(PantallaMenu.this));
            }
        });

        btnControles.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                gestorAudio.reproducirApretarBoton();

                juego.setScreen(new PantallaControles(PantallaMenu.this));
            }
        });

        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                gestorAudio.reproducirApretarBoton();

                Gdx.app.exit();
            }
        });
        
    }

    
    private TextButton.TextButtonStyle crearEstiloBoton() {

        TextButton.TextButtonStyle estilo = new TextButton.TextButtonStyle();

        estilo.up = new TextureRegionDrawable(
            new TextureRegion(texturaBotonNormal)
        );

        estilo.down = new TextureRegionDrawable(
            new TextureRegion(texturaBotonPresionado)
        );

        estilo.over = new TextureRegionDrawable(
            new TextureRegion(texturaBotonHover)
        );

        estilo.font = fuente;
        estilo.fontColor = new Color(0.30f, 0.17f, 0.10f, 1f);
        estilo.downFontColor = new Color(0.30f, 0.17f, 0.10f, 1f);
        estilo.overFontColor = new Color(0.42f, 0.25f, 0.14f, 1f);

        return estilo;
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        renderer.setView(camera);
        renderer.render();

        stage.act(delta);
        stage.draw();

        if (isTransitioning) {
            fadeAlpha += delta * 1.5f; 
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                
                juego.setScreen(new PantallaCarga(juego));
            }
        }

        if (fadeAlpha > 0f) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            
            shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, fadeAlpha);
            shapeRenderer.rect(0, 0, MAP_WIDTH, MAP_HEIGHT);
            shapeRenderer.end();
            
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(MAP_WIDTH / 2f, MAP_HEIGHT / 2f, 0);
        camera.update();
    }

    @Override public void pause() {}
    @Override public void resume() {}

    @Override 
    public void hide() {

        Gdx.input.setInputProcessor(null);
    }


    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        stage.dispose();

        if (shapeRenderer != null) {
            shapeRenderer.dispose(); 
        }
        
        Gdx.input.setInputProcessor(null);

    }

}



