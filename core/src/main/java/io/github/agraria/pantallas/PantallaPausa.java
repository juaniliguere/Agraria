package io.github.agraria.pantallas;

import com.badlogic.gdx.Game;
import io.github.agraria.Agraria;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.agraria.sonidos.GestorAudio;
import io.github.agraria.ui.GestorFuentes;

public class PantallaPausa extends ScreenAdapter {

    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
	private PantallaGranja pantallaGranja;
	
	private Texture texturaPanel;
	private Texture texturaBotonNormal;
	private Texture texturaBotonPresionado;
	private Texture texturaBotonHover;

	private NinePatch panelPausa;

	private GestorFuentes gestorFuentes;
	private BitmapFont fuente;

	private Table panelPrincipal;
	private Table tablaMenu;

	private TextButton btnContinuar;
	private TextButton btnConfiguracion;
	private TextButton btnControles;
	private TextButton btnSalir;
	private GestorAudio gestorAudio;
	
	
    public PantallaPausa(PantallaGranja pantallaGranja) {
        this.pantallaGranja = pantallaGranja;
        this.gestorAudio = new GestorAudio();
    }
    
    @Override
    public void show() {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        stage = new Stage(new FitViewport(800, 600, camera));
        shapeRenderer = new ShapeRenderer();
        
        texturaPanel = new Texture(
            Gdx.files.internal("pantallas/menu/panel_48x48.png")
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

        panelPausa = new NinePatch(texturaPanel, 12, 12, 12, 12);

        gestorFuentes = new GestorFuentes();
        gestorFuentes.cargar();
        fuente = gestorFuentes.getFuentePrincipal();

        Gdx.input.setInputProcessor(stage);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {

                    gestorAudio.reproducirApretarBoton();

                    ((Game) Gdx.app.getApplicationListener()).setScreen(pantallaGranja);
                    return true;
                }
                return false;
            }
        });

        panelPrincipal = new Table();
        panelPrincipal.setBackground(new NinePatchDrawable(panelPausa));

        panelPrincipal.setSize(240, 280);

        panelPrincipal.setPosition(
            800 / 2f - panelPrincipal.getWidth() / 2f,
            600 / 2f - panelPrincipal.getHeight() / 2f
        );

                tablaMenu = new Table();
        tablaMenu.defaults().padBottom(10);

        panelPrincipal.add(tablaMenu).expand().fill();

        stage.addActor(panelPrincipal);
        
             TextButton.TextButtonStyle estiloBoton = crearEstiloBoton();

                btnContinuar = new TextButton("CONTINUAR", estiloBoton);
        btnConfiguracion = new TextButton("CONFIGURACIÓN", estiloBoton);
        btnControles = new TextButton("CONTROLES", estiloBoton);
        btnSalir = new TextButton("SALIR AL MENÚ", estiloBoton);

        tablaMenu.add(btnContinuar).width(160).height(40).row();
        tablaMenu.add(btnConfiguracion).width(160).height(40).row();
        tablaMenu.add(btnControles).width(160).height(40).row();
        tablaMenu.add(btnSalir).width(160).height(40).row();
        

        btnContinuar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                gestorAudio.reproducirApretarBoton();

                ((Game) Gdx.app.getApplicationListener()).setScreen(pantallaGranja);
            }
        });

        btnConfiguracion.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                gestorAudio.reproducirApretarBoton();

                ((Game) Gdx.app.getApplicationListener())
                    .setScreen(new PantallaConfiguracion(PantallaPausa.this));
            }
        });

        btnControles.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                gestorAudio.reproducirApretarBoton();

                ((Game) Gdx.app.getApplicationListener())
                    .setScreen(new PantallaControles(PantallaPausa.this));
            }
        });

        btnSalir.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                gestorAudio.reproducirApretarBoton();

                pantallaGranja.detenerClima();
                
                GestorAudio.apagarMusicaGlobal();

                ((Game) Gdx.app.getApplicationListener())
                    .setScreen(new PantallaMenu((Agraria) Gdx.app.getApplicationListener()));
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
        estilo.fontColor = Color.WHITE;
        estilo.downFontColor = Color.WHITE;
        estilo.overFontColor = Color.WHITE;

        return estilo;
    }
    
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    	
        pantallaGranja.renderizarPausada();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(
            GL20.GL_SRC_ALPHA,
            GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0, 0, 0, 0.55f);
        shapeRenderer.rect(0, 0, 800, 600);

        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {

        if (stage != null) stage.dispose();

        if (texturaPanel != null) texturaPanel.dispose();
        if (texturaBotonNormal != null) texturaBotonNormal.dispose();
        if (texturaBotonPresionado != null) texturaBotonPresionado.dispose();
        if (texturaBotonHover != null) texturaBotonHover.dispose();

        if (gestorFuentes != null) gestorFuentes.dispose();

        if (shapeRenderer != null) shapeRenderer.dispose();
        
        if (gestorAudio != null) {
            gestorAudio.dispose();
        }
    }
    
}