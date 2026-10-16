package io.github.agraria.pantallas;

import io.github.agraria.cultivos.GestorCultivos;
import io.github.agraria.cultivos.Cultivo;
import io.github.agraria.cultivos.Parcela;
import io.github.agraria.cultivos.TipoCultivo;
import io.github.agraria.elementos.Item;
import io.github.agraria.eventos.*;
import io.github.agraria.inventario.GestorInventario;
import io.github.agraria.inventario.Inventario;
import io.github.agraria.economia.Billetera;
import io.github.agraria.tienda.VentanaTienda;
import io.github.agraria.control.ControlJugador;
import io.github.agraria.personajes.Personaje;
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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class PantallaGranja extends ScreenAdapter {

    private SpriteBatch batch;
    private Personaje jugador;

    private GestorMapa controlMapa;
   
    private OrthographicCamera camara;
    private Viewport vp;

    public static final int V_WIDTH = 800;
    public static final int V_HEIGHT = 600;

    private GestorCultivos gestorCultivos;

    // Guarda las etapas de cada tipo de cultivo.
    private ObjectMap<TipoCultivo, TextureRegion[]> etapasCultivos;
    // Texturas originales que debemos liberar al cerrar la pantalla
    private ObjectMap<TipoCultivo, Texture> texturasCultivos;

    private Reloj reloj;
    private ControlSueno controlSueno;
    private ControlClima controlClima;
    
    // --- HUD Y TEXTO ---
    private BitmapFont font;
    private OrthographicCamera hudCamara;
    private ShapeRenderer shapeRenderer;
    
    private Rectangle zonaDormir;
    
    private Inventario inventario;
    private GestorInventario gestorInventario;

    // --- ECONOMÍA Y TIENDA ---
    private Billetera billetera;
    private Stage stageUi; // Stage independiente para manejar la UI de la tienda
    private Skin skinUi;   // Skin por defecto o la que uses para la UI

    public PantallaGranja() {

        batch = new SpriteBatch();
        jugador = new Personaje(125, 125);
        shapeRenderer = new ShapeRenderer();

        // =========================
        // CÁMARA Y VIEWPORT
        // =========================

        camara = new OrthographicCamera();
        vp = new FitViewport(V_WIDTH, V_HEIGHT, camara);
        vp.apply();
        camara.zoom = 0.5f;
        
        hudCamara = new OrthographicCamera();
        hudCamara.setToOrtho(false, V_WIDTH, V_HEIGHT);
        font = new BitmapFont();

        controlMapa = new GestorMapa("pantallas/zona1/AgrariaMapa.tmx");
        zonaDormir = controlMapa.getZonaDormir();
        
        this.inventario = new Inventario(10);
        this.gestorInventario = new GestorInventario(inventario);

        // Inicializamos la billetera con 100 monedas de base
        this.billetera = new Billetera(100);
        
     // Stage con la cámara fija del HUD para que no se mueva con el zoom del mapa
        stageUi = new Stage(new FitViewport(V_WIDTH, V_HEIGHT, hudCamara));
        skinUi = new Skin(Gdx.files.internal("uiskin.json"));

        // =========================
        // CONTROL DE CULTIVOS
        // =========================

        boolean[][] matrizPlantable = GestorCultivos.generarMatrizPlantableDesdeMapa(
                controlMapa.getMapa(),
                controlMapa.getMapWidthTiles(),
                controlMapa.getMapHeightTiles()
        );

        gestorCultivos = new GestorCultivos(
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

    // Carga automáticamente las texturas de todos los tipos de cultivo existentes.
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
        
        // Combinamos la UI y el control del jugador para que ambos reciban eventos
        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(stageUi);   // Primero la UI (botones de la tienda)
        multiplexer.addProcessor(controlador); // Después el juego
        
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void procesarCultivar(int screenX, int screenY) {
        Vector3 posMundo = camara.unproject(new Vector3(screenX, screenY, 0));
        Parcela parcela = gestorCultivos.getParcelaEnPx(posMundo.x, posMundo.y);

        if (parcela == null) return;

        if (!parcela.tieneCultivo() && parcela.isEsPlantable()) {
            
            // Obtenemos el tipo de cultivo que corresponde a la semilla seleccionada
            TipoCultivo cultivoSeleccionado = inventario.getCultivoSeleccionado();

            if (cultivoSeleccionado != null) {
                // Plantamos en la parcela
                boolean exito = gestorCultivos.plantarEn(posMundo.x, posMundo.y, cultivoSeleccionado);
                
                if (exito) {
                    // Consumimos 1 unidad de la semilla de la mano
                    inventario.consumirSeleccionado(1);
                    System.out.println("-> ¡Semilla plantada con éxito!");
                }
            } else {
                System.out.println("-> No tenés seleccionada ninguna semilla válida en la mano.");
            }
        }
    }
    
    public void procesarCosechar(int screenX, int screenY) {
        Vector3 posMundo = camara.unproject(new Vector3(screenX, screenY, 0));
        Parcela parcela = gestorCultivos.getParcelaEnPx(posMundo.x, posMundo.y);

        if (parcela == null) {
            System.out.println("-> No hay un cultivo cerca del clic.");
            return;
        }

        Cultivo cosechado = parcela.cosechar();

        if (cosechado != null) {
            TipoCultivo tipo = cosechado.getTipo();
            Item itemCosecha = tipo.crearItemCosecha();

            boolean guardado = inventario.agregarItem(itemCosecha, 1);

            if (guardado) {
                System.out.println("-> Cosechado y guardado en inventario: " + tipo.getNombre());
            } else {
                System.out.println("-> Cosechado pero el inventario está lleno.");
            }
        } else {
            System.out.println("-> El cultivo todavía no está listo.");
        }
    }
    
    public void procesarDormir() {
        if (estaCercaDeLaCasa()) {
            controlSueno.iniciarDormir(reloj);
        }
    }
    
    public boolean estaCercaDeLaCasa() {
        if (zonaDormir == null) {
            return false;
        }
        return zonaDormir.contains(
                jugador.getCentroX(),
                jugador.getPiesY()
        );
    }

    @Override
    public void render(float delta) {

    	// Cambiar casilla seleccionada con los números del 1 al 9 y el 0 (para el slot 10)
    	if (Gdx.input.isKeyJustPressed(Keys.NUM_1)) inventario.setCasillaSeleccionada(0);
    	if (Gdx.input.isKeyJustPressed(Keys.NUM_2)) inventario.setCasillaSeleccionada(1);
    	if (Gdx.input.isKeyJustPressed(Keys.NUM_3)) inventario.setCasillaSeleccionada(2);
    	if (Gdx.input.isKeyJustPressed(Keys.NUM_4)) inventario.setCasillaSeleccionada(3);
    	if (Gdx.input.isKeyJustPressed(Keys.NUM_5)) inventario.setCasillaSeleccionada(4);
    	if (Gdx.input.isKeyJustPressed(Keys.NUM_6)) inventario.setCasillaSeleccionada(5);
    	if (Gdx.input.isKeyJustPressed(Keys.NUM_7)) inventario.setCasillaSeleccionada(6);
    	if (Gdx.input.isKeyJustPressed(Keys.NUM_8)) inventario.setCasillaSeleccionada(7);
    	if (Gdx.input.isKeyJustPressed(Keys.NUM_9)) inventario.setCasillaSeleccionada(8);
    	if (Gdx.input.isKeyJustPressed(Keys.NUM_0)) inventario.setCasillaSeleccionada(9);
    	
        // Abrir la tienda al presionar la tecla P
        if (Gdx.input.isKeyJustPressed(Keys.P)) {
            new VentanaTienda(stageUi, billetera, inventario, skinUi);
        }

        controlClima.actualizar(delta);
        controlSueno.actualizar(delta, reloj, gestorCultivos);

        if (!controlSueno.estaDurmiendo()) {
            int minutosPasados = reloj.actualizar(delta);
            if (minutosPasados > 0) {
                gestorCultivos.pasarTiempo(minutosPasados);
            }
        }
        
        jugador.actualizar(delta, controlMapa.getColisionesMapa(), controlMapa.getAnchoMapaPixels(), controlMapa.getAltoMapaPixels());
        actualizarCamara();

        ScreenUtils.clear(0f, 0f, 0f, 1f);

        controlMapa.setView(camara);
        controlMapa.renderAbajo();

        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        renderizarCultivos();
        jugador.renderizar(batch);
        batch.end();

        controlMapa.renderArriba();

        // Efectos visuales de clima
        controlClima.renderizarOscurecimiento(shapeRenderer, camara, vp.getWorldWidth(), vp.getWorldHeight());
        controlClima.renderizarEfectoVisual(batch, hudCamara, V_WIDTH, V_HEIGHT);

        // =========================
        // DIBUJAR HUD (Reloj, Lluvia, Monedas)
        // =========================
        batch.setProjectionMatrix(hudCamara.combined);
        batch.begin();
        
        font.getData().setScale(1.2f);
        String textoHud = "Dia: " + reloj.getDias() + " | Hora: " + reloj.getHoraFormateada() + " | Monedas: $" + billetera.getMonedas() + " | [L] Lluvia: " + (controlClima.isLluviaActiva() ? "ON" : "OFF");
        font.draw(batch, textoHud, 20, V_HEIGHT - 20);
        font.getData().setScale(1f);
        
        gestorInventario.renderizar(batch, font, V_WIDTH, V_HEIGHT);
        
        batch.end();

        // =========================
        // RENDERIZAR STAGE DE UI (Tienda / Menús)
        // =========================
        stageUi.act(delta);
        stageUi.draw();

        // Transición de sueño
        controlSueno.renderizarEfectoVisual(shapeRenderer, batch, camara, hudCamara, vp, font, reloj, V_WIDTH, V_HEIGHT);
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
                Parcela parcela = gestorCultivos.getParcela(col, fila);

                if (parcela == null || !parcela.tieneCultivo()) {
                    continue;
                }

                Cultivo cultivo = parcela.getCultivoActual();
                TipoCultivo tipo = cultivo.getTipo();
                int etapa = cultivo.getEtapaActual();

                TextureRegion[] etapas = etapasCultivos.get(tipo);

                if (etapas == null || etapa < 0 || etapa >= etapas.length) {
                    continue;
                }

                float xPx = col * controlMapa.getTileWidth();
                float yPx = fila * controlMapa.getTileHeight();

                batch.draw(etapas[etapa], xPx, yPx, controlMapa.getTileWidth(), controlMapa.getTileHeight());
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        vp.update(width, height);
        stageUi.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        jugador.liberarRecursos();
        controlMapa.dispose();
        controlClima.dispose();
        gestorInventario.dispose();
        stageUi.dispose();
        skinUi.dispose();

        for (Texture textura : texturasCultivos.values()) {
            textura.dispose();
        }
    }
}