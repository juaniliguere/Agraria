package io.github.agraria.pantallas;

import io.github.agraria.control.ControlMapa;
import io.github.agraria.cultivos.ControlCultivos;
import io.github.agraria.cultivos.Cultivo;
import io.github.agraria.cultivos.Parcela;
import io.github.agraria.cultivos.TipoCultivo;
import io.github.agraria.control.ControlJugador;
import io.github.agraria.personajes.Personaje;
import io.github.agraria.eventos.ControlClima;
import io.github.agraria.eventos.ControlSueno;
import io.github.tiempo.Reloj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PantallaGranja extends ScreenAdapter {

    private SpriteBatch batch;
    private Personaje jugador;
    private ControlMapa controlMapa;

    private OrthographicCamera camara;
    private Viewport viewport;

    public static final int V_WIDTH = 800;
    public static final int V_HEIGHT = 600;

    private ControlCultivos controlCultivos;
    private ObjectMap<TipoCultivo, TextureRegion[]> etapasCultivos;
    private ObjectMap<TipoCultivo, Texture> texturasCultivos;

    private Reloj reloj;
    private ControlClima controlClima;
    private ControlSueno controlSueno;

    // --- HUD Y TEXTO ---
    private BitmapFont font;
    private OrthographicCamera hudCamara;
    private ShapeRenderer shapeRenderer;

    public PantallaGranja() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        jugador = new Personaje(125, 125);

        camara = new OrthographicCamera();
        viewport = new FitViewport(V_WIDTH, V_HEIGHT, camara);
        viewport.apply();
        camara.zoom = 0.5f;

        hudCamara = new OrthographicCamera();
        hudCamara.setToOrtho(false, V_WIDTH, V_HEIGHT);
        font = new BitmapFont();

        controlMapa = new ControlMapa("pantallas/zona1/AgrariaMapa.tmx");

        boolean[][] matrizPlantable = ControlCultivos.generarMatrizPlantableDesdeMapa(
                controlMapa.getMapa(),
                controlMapa.getMapWidthTiles(),
                controlMapa.getMapHeightTiles()
        );

        controlCultivos = new ControlCultivos(
                controlMapa.getMapWidthTiles(),
                controlMapa.getMapHeightTiles(),
                controlMapa.getTileWidth(),
                matrizPlantable
        );

        cargarTexturasCultivos();
        reloj = new Reloj();
        controlClima = new ControlClima();
        controlSueno = new ControlSueno();
    }

    private void cargarTexturasCultivos() {
        etapasCultivos = new ObjectMap<>();
        texturasCultivos = new ObjectMap<>();

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
        }
    }

    @Override
    public void show() {
        ControlJugador controlador = new ControlJugador(jugador, this);
        Gdx.input.setInputProcessor(controlador);
    }

    public void hacerClicEn(int screenX, int screenY) {
        com.badlogic.gdx.math.Vector3 posMundo = camara.unproject(new com.badlogic.gdx.math.Vector3(screenX, screenY, 0));
        Parcela parcela = controlCultivos.getParcelaEnPx(posMundo.x, posMundo.y);

        if (parcela == null) return;

        if (!parcela.tieneCultivo() && parcela.isEsPlantable()) {
            controlCultivos.plantarEn(posMundo.x, posMundo.y, TipoCultivo.ZANAHORIA);
        }
    }
    
    public void procesarDormir() {
        controlSueno.iniciarDormir(reloj);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Keys.PLUS) || Gdx.input.isKeyPressed(Keys.EQUALS)) {
            camara.zoom -= 0.5f * delta;
        }
        if (Gdx.input.isKeyPressed(Keys.MINUS)) {
            camara.zoom += 0.5f * delta;
        }
        camara.zoom = MathUtils.clamp(camara.zoom, 0.2f, 1.5f);

        controlClima.actualizar(delta);
        controlSueno.actualizar(delta, reloj, controlCultivos);

        if (!controlSueno.estaDurmiendo()) {
            int minutosPasados = reloj.actualizar(delta);
            if (minutosPasados > 0) {
                controlCultivos.pasarTiempo(minutosPasados);
            }
        }

        jugador.actualizar(delta, controlMapa.getColisionesMapa(), controlMapa.getAnchoMapaPixels(), controlMapa.getAltoMapaPixels());
        actualizarCamara();

        // =========================
        // DIBUJAR MUNDO
        // =========================
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        controlMapa.setView(camara);
        controlMapa.renderAbajo();

        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        renderizarCultivos();
        jugador.renderizar(batch);
        batch.end();

        controlMapa.renderArriba();

        // =========================
        // EFECTOS VISUALES (Clima)
        // =========================
        controlClima.renderizarOscurecimiento(shapeRenderer, camara, viewport.getWorldWidth(), viewport.getWorldHeight());
        controlClima.renderizarEfectoVisual(batch, hudCamara, V_WIDTH, V_HEIGHT);

        // =========================
        // DIBUJAR HUD NORMAL
        // =========================
        batch.setProjectionMatrix(hudCamara.combined);
        batch.begin();
        font.getData().setScale(1.2f);
        font.draw(batch, "Dia: " + reloj.getDias() + " | Hora: " + reloj.getHoraFormateada() + " | [L] Lluvia: " + (controlClima.isLluviaActiva() ? "ON" : "OFF"), 20, V_HEIGHT - 20);
        font.getData().setScale(1f);
        batch.end();

        // =========================
        // TRANSICIÓN GRÁFICA DE SUEÑO
        // =========================
        controlSueno.renderizarEfectoVisual(shapeRenderer, batch, camara, hudCamara, viewport, font, reloj, V_WIDTH, V_HEIGHT);
    }

    private void actualizarCamara() {
        float medioAnchoCamara = (camara.viewportWidth * camara.zoom) / 2f;
        float medioAltoCamara = (camara.viewportHeight * camara.zoom) / 2f;

        float camX = MathUtils.clamp(jugador.getX(), medioAnchoCamara, controlMapa.getAltoMapaPixels() - medioAnchoCamara);
        float camY = MathUtils.clamp(jugador.getY(), medioAltoCamara, controlMapa.getAltoMapaPixels() - medioAltoCamara);

        camara.position.set(camX, camY, 0);
        camara.update();
    }

    private void renderizarCultivos() {
        for (int fila = 0; fila < controlMapa.getMapHeightTiles(); fila++) {
            for (int col = 0; col < controlMapa.getMapWidthTiles(); col++) {
                Parcela parcela = controlCultivos.getParcela(col, fila);
                if (parcela == null || !parcela.tieneCultivo()) continue;

                Cultivo cultivo = parcela.getCultivoActual();
                TipoCultivo tipo = cultivo.getTipo();
                int etapa = cultivo.getEtapaActual();
                TextureRegion[] etapas = etapasCultivos.get(tipo);

                if (etapas == null || etapa < 0 || etapa >= etapas.length) continue;

                float xPx = col * controlMapa.getTileWidth();
                float yPx = fila * controlMapa.getTileHeight();

                batch.draw(etapas[etapa], xPx, yPx, controlMapa.getTileWidth(), controlMapa.getTileHeight());
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        jugador.liberarRecursos();
        controlMapa.dispose();
        controlClima.dispose();

        for (Texture textura : texturasCultivos.values()) {
            textura.dispose();
        }
    }
}