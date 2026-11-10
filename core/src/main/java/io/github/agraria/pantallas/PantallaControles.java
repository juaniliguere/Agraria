package io.github.agraria.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import io.github.agraria.Agraria;
import io.github.agraria.sonidos.GestorAudio;
import io.github.agraria.ui.GestorFuentes;

public class PantallaControles extends ScreenAdapter {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture texturaVolver;
    private ImageButton btnVolver;
    private Stage stage;
    
    private Texture texturaSliderTrack;
    private Texture texturaSliderFill;
    private Texture texturaSliderKnobNormal;
    private Texture texturaSliderKnobHover;
    private Texture texturaSliderKnobPresionado;
    
    
 // Recursos del panel
    private Texture texturaPanel;
    private NinePatch panelConfiguracion;

    // Botones
    private Texture texturaBotonNormal;
    private Texture texturaBotonPresionado;
    private Texture texturaBotonHover;

    // Fuente
    private GestorFuentes gestorFuentes;
    private BitmapFont fuente;

    // Tablas
    private Table panelPrincipal;
    private Table tablaConfiguracion;
    
    private Label lblMusica;
    private Label lblSonidos;
    
    private Screen pantallaAnterior;
    
    private static final float MAP_WIDTH = 480; 
    private static final float MAP_HEIGHT = 320;

    public PantallaControles(Screen pantallaAnterior) {
        this.pantallaAnterior = pantallaAnterior;
    }
    
    @Override
    public void show() {
        map = new TmxMapLoader().load("pantallas/menu/pantallaMenu.tmx");

        camera = new OrthographicCamera();
        viewport = new StretchViewport(MAP_WIDTH, MAP_HEIGHT, camera);
        viewport.apply();

        // --- ACÁ ESTÁ EL SECRETO: Centrar la cámara en la mitad del mapa ---
        camera.position.set(MAP_WIDTH / 2f, MAP_HEIGHT / 2f, 0);
        camera.update();

        renderer = new OrthogonalTiledMapRenderer(map);
        
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        
        texturaVolver = new Texture(Gdx.files.internal("pantallas/menu/back_dark_12x12.png"));

        	texturaVolver.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        	
        	TextureRegionDrawable drawableVolver = new TextureRegionDrawable(new TextureRegion(texturaVolver));

        	drawableVolver.setMinWidth(36);
        	drawableVolver.setMinHeight(36);

        	btnVolver = new ImageButton(drawableVolver);
        	btnVolver.getImage().setScaling(com.badlogic.gdx.utils.Scaling.fit);

        		
        		btnVolver.addListener(new ClickListener() {
        		    @Override
        		    public void clicked(InputEvent event, float x, float y) {
        		        ((Game) Gdx.app.getApplicationListener())
        		            .setScreen(pantallaAnterior);
        		    }
        		});


     
        		// Textura del panel
        		texturaPanel = new Texture(
        		    Gdx.files.internal("pantallas/menu/panel_48x48.png")
        		);

        		// Texturas de los botones
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
        		
        		// Crear el panel NinePatch
        		panelConfiguracion = new NinePatch(texturaPanel, 12, 12, 12, 12);

        		// Cargar fuente
        		gestorFuentes = new GestorFuentes();
        		gestorFuentes.cargar();
        		fuente = gestorFuentes.getFuentePrincipal();
        		
        		// Panel principal
        		panelPrincipal = new Table();
        		panelPrincipal.setBackground(new NinePatchDrawable(panelConfiguracion));

        		// Dimensiones del panel
        		panelPrincipal.setSize(280, 200);

        		// Centrar el panel en la pantalla
        		panelPrincipal.setPosition(
        		    MAP_WIDTH / 2f - panelPrincipal.getWidth() / 2f,
        		    MAP_HEIGHT / 2f - panelPrincipal.getHeight() / 2f
        		);

        		// Tabla interna
        		tablaConfiguracion = new Table();

        		// Agregar tabla interna al panel
        		panelPrincipal.add(tablaConfiguracion).expand().fill();

        		// Agregar panel al Stage
        		stage.addActor(panelPrincipal);
        		
        		btnVolver.setSize(25, 25);
        		btnVolver.setPosition(
        		    panelPrincipal.getX() + 8,
        		    panelPrincipal.getY() + panelPrincipal.getHeight() - 30
        		);
        		stage.addActor(btnVolver);
        		
        		// Estilo de los textos
        		Label.LabelStyle estiloTexto = crearEstiloTexto();

        		// Título
        		Label titulo = new Label("CONTROLES", estiloTexto);

        		// Etiquetas de volumen
        		lblMusica = new Label("MÚSICA", estiloTexto);
        		lblSonidos = new Label("SONIDOS", estiloTexto);
        		
        		// Agregar título
        		tablaConfiguracion.add(titulo).colspan(2).padBottom(20).row();
        		
    }

    private Label.LabelStyle crearEstiloTexto() {
        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fuente;
        estilo.fontColor = new Color(0.30f, 0.17f, 0.10f, 1f);
        return estilo;
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
    
    private Slider.SliderStyle crearEstiloSlider() {

        Slider.SliderStyle estilo = new Slider.SliderStyle();

        // Barra de fondo
        estilo.background = new TextureRegionDrawable(
            new TextureRegion(texturaSliderTrack)
        );

        // Relleno de la barra
        estilo.knobBefore = new TextureRegionDrawable(
            new TextureRegion(texturaSliderFill)
        );

        // Perilla normal
        estilo.knob = new TextureRegionDrawable(
            new TextureRegion(texturaSliderKnobNormal)
        );

        // Perilla al pasar el cursor
        estilo.knobOver = new TextureRegionDrawable(
            new TextureRegion(texturaSliderKnobHover)
        );

        // Perilla al mantener presionado
        estilo.knobDown = new TextureRegionDrawable(
            new TextureRegion(texturaSliderKnobPresionado)
        );

        return estilo;
    }
    
    
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Asegurate de actualizar la cámara antes de pasarla al renderer
        camera.update();
        renderer.setView(camera);
        renderer.render();
        
        stage.act(delta);
        stage.draw();
    }



    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (renderer != null) renderer.dispose();
        
        if (texturaVolver != null) texturaVolver.dispose();

        if (texturaSliderTrack != null) texturaSliderTrack.dispose();
        if (texturaSliderFill != null) texturaSliderFill.dispose();
        if (texturaSliderKnobNormal != null) texturaSliderKnobNormal.dispose();
        if (texturaSliderKnobHover != null) texturaSliderKnobHover.dispose();
        if (texturaSliderKnobPresionado != null) texturaSliderKnobPresionado.dispose();

        if (stage != null) stage.dispose();
        
        if (texturaPanel != null) texturaPanel.dispose();
        if (texturaBotonNormal != null) texturaBotonNormal.dispose();
        if (texturaBotonPresionado != null) texturaBotonPresionado.dispose();
        if (texturaBotonHover != null) texturaBotonHover.dispose();

        if (gestorFuentes != null) gestorFuentes.dispose();
        
    }
}