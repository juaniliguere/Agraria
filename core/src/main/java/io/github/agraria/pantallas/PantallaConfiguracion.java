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
import com.badlogic.gdx.scenes.scene2d.InputListener;

import io.github.agraria.Agraria;
import io.github.agraria.sonidos.GestorAudio;
import io.github.agraria.ui.GestorFuentes;

public class PantallaConfiguracion extends ScreenAdapter {

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

    private Slider sliderMusica;
    private Slider sliderSonidos;

    private Texture texturaPanel;
    private NinePatch panelConfiguracion;

    private Texture texturaBotonNormal;
    private Texture texturaBotonPresionado;
    private Texture texturaBotonHover;

    private GestorFuentes gestorFuentes;
    private BitmapFont fuente;

    private Table panelPrincipal;
    private Table tablaConfiguracion;

    private Label lblMusica;
    private Label lblSonidos;

    private Screen pantallaAnterior;

    private static final float MAP_WIDTH = 480;
    private static final float MAP_HEIGHT = 320;

    public PantallaConfiguracion(Screen pantallaAnterior) {
        this.pantallaAnterior = pantallaAnterior;
    }

    @Override
    public void show() {

        map = new TmxMapLoader().load("pantallas/menu/pantallaMenu.tmx");

        camera = new OrthographicCamera();
        viewport = new StretchViewport(MAP_WIDTH, MAP_HEIGHT, camera);
        viewport.apply();

        camera.position.set(MAP_WIDTH / 2f, MAP_HEIGHT / 2f, 0);
        camera.update();

        renderer = new OrthogonalTiledMapRenderer(map);

        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    ((Game) Gdx.app.getApplicationListener())
                        .setScreen(pantallaAnterior);
                    return true;
                }
                return false;
            }
        });

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

        texturaSliderTrack = new Texture(Gdx.files.internal("pantallas/menu/slider_track_64x8.png"));

        texturaSliderFill = new Texture(Gdx.files.internal("pantallas/menu/slider_fill_64x8.png"));

        texturaSliderKnobNormal = new Texture(Gdx.files.internal("pantallas/menu/slider_knob_normal_8x12.png"));

        texturaSliderKnobHover = new Texture(Gdx.files.internal("pantallas/menu/slider_knob_hover_8x12.png"));

        texturaSliderKnobPresionado = new Texture(Gdx.files.internal("pantallas/menu/slider_knob_pressed_8x12.png"));

        texturaSliderTrack.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        texturaSliderFill.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        texturaSliderKnobNormal.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        texturaSliderKnobHover.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        texturaSliderKnobPresionado.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Slider.SliderStyle estiloSlider = crearEstiloSlider();

        sliderMusica = new Slider(0f, 1f, 0.01f, false, estiloSlider);
        sliderSonidos = new Slider(0f, 1f, 0.01f, false, estiloSlider);

        sliderMusica.setValue(GestorAudio.getVolumenMusica());
        sliderSonidos.setValue(GestorAudio.getVolumenSonidos());

        sliderMusica.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GestorAudio.setVolumenMusica(sliderMusica.getValue());
            }
        });

        sliderSonidos.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GestorAudio.setVolumenSonidos(sliderSonidos.getValue());
            }
        });

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

        panelConfiguracion = new NinePatch(texturaPanel, 12, 12, 12, 12);

        gestorFuentes = new GestorFuentes();
        gestorFuentes.cargar();
        fuente = gestorFuentes.getFuentePrincipal();

        panelPrincipal = new Table();
        panelPrincipal.setBackground(new NinePatchDrawable(panelConfiguracion));

        panelPrincipal.setSize(280, 200);

        panelPrincipal.setPosition(
            MAP_WIDTH / 2f - panelPrincipal.getWidth() / 2f,
            MAP_HEIGHT / 2f - panelPrincipal.getHeight() / 2f
        );

        tablaConfiguracion = new Table();

        panelPrincipal.add(tablaConfiguracion).expand().fill();

        stage.addActor(panelPrincipal);

        btnVolver.setSize(25, 25);
        btnVolver.setPosition(
            panelPrincipal.getX() + 8,
            panelPrincipal.getY() + panelPrincipal.getHeight() - 30
        );

        stage.addActor(btnVolver);

        Label.LabelStyle estiloTexto = crearEstiloTexto();

        Label titulo = new Label("CONFIGURACIÓN", estiloTexto);

        lblMusica = new Label("MÚSICA", estiloTexto);
        lblSonidos = new Label("SONIDOS", estiloTexto);

        tablaConfiguracion.add(titulo).colspan(2).padBottom(20).row();

        tablaConfiguracion.add(lblMusica).left().padRight(15);
        tablaConfiguracion.add(sliderMusica).width(100).row();

        tablaConfiguracion.add(lblSonidos).left().padRight(15);
        tablaConfiguracion.add(sliderSonidos).width(100);
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

        estilo.background = new TextureRegionDrawable(
            new TextureRegion(texturaSliderTrack)
        );

        estilo.knobBefore = new TextureRegionDrawable(
            new TextureRegion(texturaSliderFill)
        );

        estilo.knob = new TextureRegionDrawable(
            new TextureRegion(texturaSliderKnobNormal)
        );

        estilo.knobOver = new TextureRegionDrawable(
            new TextureRegion(texturaSliderKnobHover)
        );

        estilo.knobDown = new TextureRegionDrawable(
            new TextureRegion(texturaSliderKnobPresionado)
        );

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