package io.github.agraria.pantallas;

import io.github.agraria.cultivos.GestorCultivos;
import io.github.agraria.sonidos.*;
import io.github.agraria.cultivos.TipoCultivo;
import io.github.agraria.eventos.*;
import io.github.agraria.inventario.GestorInventario;
import io.github.agraria.inventario.Inventario;
import io.github.agraria.economia.Billetera;
import io.github.agraria.ui.GestorHud;
import io.github.agraria.ui.GestorPrompt;
import io.github.agraria.Agraria;
import io.github.agraria.control.ControlCultivos;
import io.github.agraria.control.ControlEntrada;
import io.github.agraria.control.ControlJugador;
import io.github.agraria.control.ControlTienda;
import io.github.agraria.personajes.Personaje;
import io.github.agraria.progreso.SistemaNiveles;
import io.github.tiempo.Reloj;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.GL20;

public class PantallaGranja extends ScreenAdapter {

	private Agraria juego;
	
    private SpriteBatch batch;
    private Personaje jugador;

    private GestorMapa controlMapa;
    
    private GestorMapa mapaGranja;
    private PantallaTienda pantallaTienda;

    private boolean enCasa = false;

   
    private OrthographicCamera camara;
    private Viewport vp;

    public static final int V_WIDTH = 800;
    public static final int V_HEIGHT = 600;

    private GestorCultivos gestorCultivos;

    private Reloj reloj;
    private ControlSueno controlSueno;
    private ControlClima controlClima;
    private ControlCultivos controlCultivos;
    

    private BitmapFont font;
    private OrthographicCamera hudCamara;
    private ShapeRenderer shapeRenderer;
    
    private Rectangle zonaDormir;
    private Rectangle zonaComprar;
    
    private Inventario inventario;
    private GestorInventario gestorInventario;

    private Billetera billetera;
    private Stage stageUi; 
    private Skin skinUi;  
    private ControlJugador controlJugador;
    private ControlTienda controlTienda;
    
    private GestorAudio gestorAudio;

    private boolean isSaliendoAlMenu = false;

    private GestorPrompt gestorPrompt;
    
    private ControlEntrada controlEntrada;
    
    private GestorHud gestorHud;
    
    private SistemaNiveles sistemaNiveles;
    
    private float tiempoFadeEntrada = 0f;
    private static final float DURACION_FADE_ENTRADA = 1.2f;

    
    
    public PantallaGranja(Agraria juego) {

    	this.juego = juego;
        batch = new SpriteBatch();
        jugador = new Personaje(125, 125);
        shapeRenderer = new ShapeRenderer();

        
        // -----------------------------------
        // CÁMARA Y VIEWPORT
        // -----------------------------------

        camara = new OrthographicCamera();
        vp = new FitViewport(V_WIDTH, V_HEIGHT, camara);
        vp.apply();
        camara.zoom = 0.5f;
        
        hudCamara = new OrthographicCamera();
        hudCamara.setToOrtho(false, V_WIDTH, V_HEIGHT);

        FreeTypeFontGenerator generador = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/VT323-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parametros = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parametros.size = 22;
        parametros.minFilter = Texture.TextureFilter.Nearest;
        parametros.magFilter = Texture.TextureFilter.Nearest;

        font = generador.generateFont(parametros);

        generador.dispose();

        mapaGranja = new GestorMapa("pantallas/zona1/AgrariaMapa.tmx");
        pantallaTienda = new PantallaTienda();
        
        controlMapa = mapaGranja;
        
        zonaDormir = controlMapa.getZonaDormir();
        zonaComprar = controlMapa.getZonaComprar();
        
        this.inventario = new Inventario(10);
        this.gestorInventario = new GestorInventario(inventario);

        this.billetera = new Billetera(100);
        
        stageUi = new Stage(new FitViewport(V_WIDTH, V_HEIGHT, hudCamara));
        skinUi = new Skin(Gdx.files.internal("uiskin.json"));
        skinUi.get("default", Label.LabelStyle.class).font = font;

        this.gestorPrompt = new GestorPrompt(stageUi, skinUi);

        // =========================
        // CONTROL DE CULTIVOS
        // =========================

        boolean[][] matrizPlantable = GestorCultivos.generarMatrizPlantableDesdeMapa(
                controlMapa.getMapa(),
                controlMapa.getMapWidthTiles(),
                controlMapa.getMapHeightTiles()
        );

        gestorCultivos = new GestorCultivos(controlMapa.getMapWidthTiles(), controlMapa.getMapHeightTiles(), controlMapa.getTileWidth(),  matrizPlantable);
        gestorCultivos.cargarTexturas();

        gestorAudio = new GestorAudio();
        
        reloj = new Reloj();
        controlClima = new ControlClima(gestorCultivos, gestorAudio);

        // =========================
        // SISTEMA DE NIVELES
        // =========================

        sistemaNiveles = new SistemaNiveles();

        gestorHud = new GestorHud(inventario, V_WIDTH, V_HEIGHT, font, sistemaNiveles);
        controlSueno = new ControlSueno(gestorHud, billetera, sistemaNiveles);

        controlSueno.setAlPonerseNegraPorDesmayo(() -> llevarJugadorACasa());

        sistemaNiveles.setAlSubirNivel((anterior, nuevo) -> {
        	
            StringBuilder msg = new StringBuilder("¡Nivel " + nuevo + "!");
            for (TipoCultivo t : TipoCultivo.values()) {
                if (t.getNivelRequerido() > anterior && t.getNivelRequerido() <= nuevo) {
                    msg.append(" Semilla desbloqueada: ").append(t.getNombre());
                }
            }
            gestorHud.mostrarMensaje(msg.toString());
        });
        
        controlCultivos = new ControlCultivos(gestorCultivos, inventario, gestorAudio, sistemaNiveles);   
    }
    

    @Override
    public void show() {
        gestorAudio.reproducirMusicaFondo();

        if (controlJugador == null) {
            controlJugador = new ControlJugador(jugador, inventario);

            controlJugador.setAccionDormir(() -> procesarDormir());
            controlJugador.setAccionTienda(() -> procesarAbrirTienda());
            controlJugador.setAccionCultivar((screenX, screenY) -> procesarCultivar(screenX, screenY));
            controlJugador.setAccionCosechar((screenX, screenY) -> procesarCosechar(screenX, screenY));
            
        }

        controlEntrada = new ControlEntrada();

        if (controlTienda == null) {
            controlTienda = new ControlTienda(stageUi, billetera, inventario, skinUi, controlJugador, gestorHud, sistemaNiveles, reloj);
        }
        
        
    	controlJugador.setAccionDormir(() -> procesarDormir());
    	controlJugador.setAccionTienda(() -> procesarAbrirTienda());
    	controlJugador.setAccionCultivar((screenX, screenY) -> procesarCultivar(screenX, screenY));
    	controlJugador.setAccionCosechar((screenX, screenY) -> procesarCosechar(screenX, screenY));
    	
        
        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(stageUi);   
        multiplexer.addProcessor(controlEntrada);
        multiplexer.addProcessor(controlJugador);
        
        Gdx.input.setInputProcessor(multiplexer);
    }

    
    
    
    public void procesarCultivar(int screenX, int screenY) {
    	
        Vector3 posicionMundo = camara.unproject(new Vector3(screenX, screenY, 0));
        controlCultivos.procesarCultivar(posicionMundo.x, posicionMundo.y);
    }
    
    
    
    public void procesarCosechar(int screenX, int screenY) {
    	
        Vector3 posicionMundo = camara.unproject(new Vector3(screenX, screenY, 0));
        controlCultivos.procesarCosechar(posicionMundo.x, posicionMundo.y);    
    }
     
    
    
    public void procesarDormir() {
    	
        if (!estaCercaDeLaCasa()) {
            return;
        }
        if (!controlSueno.puedeDormir(reloj)) {
        	gestorHud.mostrarMensaje("Todavía es muy temprano para dormir.");
            return;
        }
        controlSueno.iniciarDormir(reloj, 6);
        controlClima.sortearClimaDiario();  
    }
    

    
    
    
    public boolean estaCercaDeLaCasa() {
    	
        if (zonaDormir == null) {
            return false;
        }
        return zonaDormir.contains(jugador.getCentroX(), jugador.getPiesY());
    }
    
    
    
    private void llevarJugadorACasa() {
    	
        if (pantallaTienda.estaDentro()) {
            entrarSalirTienda();
        }

        if (zonaDormir == null) {
            return;
        }

        float offsetPies = jugador.getPiesY() - jugador.getY();
        float baseX = zonaDormir.x + (zonaDormir.width / 2f) - (jugador.getAncho() / 2f);
        float baseY = zonaDormir.y + (zonaDormir.height / 2f) - offsetPies;

        Array<Polygon> colisiones = controlMapa.getColisionesMapa();

        float maxX = Math.max(0, controlMapa.getAnchoMapaPixels() - jugador.getAncho());
        float maxY = Math.max(0, controlMapa.getAltoMapaPixels() - jugador.getAlto());

        float destinoX = baseX;
        float destinoY = baseY;

        if (jugador.chocaEn(baseX, baseY, colisiones)) {
            boolean encontrado = false;
            for (float radio = 4f; radio <= 300f && !encontrado; radio += 4f) {
                for (int paso = 0; paso < 24; paso++) {
                	
                    int grados = (270 + paso * 15) % 360;
                    float px = MathUtils.clamp(baseX + MathUtils.cosDeg(grados) * radio, 0, maxX);
                    float py = MathUtils.clamp(baseY + MathUtils.sinDeg(grados) * radio, 0, maxY);

                    if (!jugador.chocaEn(px, py, colisiones)) {
                        destinoX = px;
                        destinoY = py;
                        encontrado = true;
                        break;
                    }
                    
                }
            }
        }

        jugador.setPosicion(destinoX, destinoY);
    }
    
    
    
    
    public void intentarEntrarSalirTienda() {
    	
        if (!pantallaTienda.estaDentro()) {
            if (pantallaTienda.estaCercaDeLaTienda(jugador, mapaGranja)) {
                entrarSalirTienda();  
            } 
            
        } else {
            entrarSalirTienda();
        }
        
    }
    
    
    
    public void entrarSalirTienda() {
    	
        if (!pantallaTienda.estaDentro()) {
            pantallaTienda.entrar(jugador);
            controlMapa = pantallaTienda.getMapa();

        } else {
            pantallaTienda.salir(jugador);
            controlMapa = mapaGranja;
        }

        zonaDormir = controlMapa.getZonaDormir();
        zonaComprar = controlMapa.getZonaComprar();
    }
    
    
    
    
    
    public void procesarAbrirTienda() {
    	
    	if (pantallaTienda.estaCercaDeComprar(jugador)) {
            if (controlTienda.estaAbierta()) {
                controlTienda.cerrar();
            } else {
                controlTienda.abrir();
            }
        }
    	
    }
    
    
    
    
    
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        if (controlJugador != null) {
            controlJugador.setAccionesBloqueadas(controlSueno.estaDurmiendo());
            controlJugador.actualizarMovimiento();
        }

        if (controlEntrada != null && controlEntrada.isSolicitioSalirMenu()) {

            controlEntrada.limpiarSolicitudSalirMenu();

            if (controlTienda != null && controlTienda.estaAbierta()) {
                controlTienda.cerrar();
            } else {
                isSaliendoAlMenu = true;
            }
        }
    
        if (isSaliendoAlMenu) {
            isSaliendoAlMenu = false;
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PantallaPausa(this));
            return;
        }
        
        controlClima.actualizar(delta);
        controlSueno.actualizar(delta, reloj, gestorCultivos);
        gestorHud.actualizar(delta);

        controlSueno.verificarAvisoCansancio(reloj);
        
        if (controlSueno.verificarDesmayoPorHorario(reloj)) {

            if (controlTienda != null && controlTienda.estaAbierta()) {
                controlTienda.cerrar();
            }

            if (controlJugador != null) {
                controlJugador.setAccionesBloqueadas(true);
            }

            controlClima.sortearClimaDiario();
        }
        
        if (!controlSueno.estaDurmiendo()) {
            int minutosPasados = reloj.actualizar(delta);
            if (minutosPasados > 0) {
                gestorCultivos.pasarTiempo(minutosPasados);
            }
        }
        
        
        if (controlTienda != null) {
            controlTienda.actualizar();
        }
        
        jugador.actualizar(delta, controlMapa.getColisionesMapa(), controlMapa.getAnchoMapaPixels(), controlMapa.getAltoMapaPixels());
        
        verificarInteraccionesTiendaCasa();
        
        actualizarCamara();

        actualizarPromptsMundiales();
        renderizarPartida(delta, true);
        renderizarFadeEntrada(delta);
    }
    
    
    
    
    public void detenerClima() {
        if (controlClima != null) {
            controlClima.detenerLluvia();
        }
    }
    
    
    
    
    
    private void renderizarFadeEntrada(float delta) {

        if (tiempoFadeEntrada >= DURACION_FADE_ENTRADA) {
            return;
        }

        tiempoFadeEntrada += delta;

        float progreso = Math.min(
            tiempoFadeEntrada / DURACION_FADE_ENTRADA,
            1f
        );

        
        float alpha = 1f - progreso;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(hudCamara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, alpha);
        shapeRenderer.rect(0, 0, V_WIDTH, V_HEIGHT);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    
    
    
    
    
    
    private void verificarInteraccionesTiendaCasa() {

        if (enCasa && pantallaTienda.estaCercaDeSalir(jugador)) {
            entrarSalirTienda();
        }

        if (!pantallaTienda.estaDentro()) {

            if (pantallaTienda.estaCercaDeLaTienda(jugador, mapaGranja)) {

                if (reloj.getHoras() >= 21) {
                    gestorHud.mostrarMensaje("La tienda está cerrada.");
                    return;
                }

                entrarSalirTienda();
            }

        } else if (pantallaTienda.estaCercaDeSalir(jugador)) {
            entrarSalirTienda();
        }
    }

    
    
    
    
    
    
    private void actualizarPromptsMundiales() {
    	
        if (estaCercaDeLaCasa() && !controlSueno.estaDurmiendo()) {
        	
        	float casaX = zonaDormir.x + (zonaDormir.width / 2f);
        	float casaY = zonaDormir.y + zonaDormir.height;
            gestorPrompt.mostrarPromptMundial("Z", "Dormir", casaX, casaY, camara);
            
        } else if (pantallaTienda.estaDentro() && pantallaTienda.estaCercaDeComprar(jugador)) {
        	
            float comprarX = zonaComprar.x + (zonaComprar.width / 2f) + 10f;
            float comprarY = zonaComprar.y + zonaComprar.height;
            gestorPrompt.mostrarPromptMundial("T", "Tienda", comprarX, comprarY, camara);
            
        } else {
            gestorPrompt.ocultarPrompt();
        }
        
    }

    
    
    
    
    private void renderizarIluminacionNocturna() {
    	
        float horaActual = reloj.getHoras() + (reloj.getMinutos() / 60.0f);
        float alphaOscuridad = 0f;
        
        if (horaActual >= 18.0f && horaActual < 20.0f) {
            alphaOscuridad = (horaActual - 18.0f) / 2.0f * 0.5f;
        } else if (horaActual >= 20.0f || horaActual < 5.0f) {
            alphaOscuridad = 0.6f;
        } else if (horaActual >= 5.0f && horaActual < 7.0f) {
            alphaOscuridad = 0.6f - ((horaActual - 5.0f) / 2.0f * 0.6f);
        }

        if (alphaOscuridad > 0f) {
        	
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            
            shapeRenderer.setProjectionMatrix(hudCamara.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.05f, 0.05f, 0.2f, alphaOscuridad);
            shapeRenderer.rect(0, 0, V_WIDTH, V_HEIGHT);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            
        }
        
    }

    
    
    
    
    private void renderizarPartida(float delta, boolean actualizarInterfaz) {

        controlMapa.setView(camara);
        controlMapa.renderAbajo();

        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        if (!pantallaTienda.estaDentro() && !enCasa) {
            gestorCultivos.renderizar(batch);
        }

        jugador.renderizar(batch);
        batch.end();

        controlMapa.renderArriba();

        boolean mostrarClima = !pantallaTienda.estaDentro() && !enCasa;

        controlClima.renderizarOscurecimiento(shapeRenderer, camara, vp.getWorldWidth(), vp.getWorldHeight(), mostrarClima);
        controlClima.renderizarEfectoVisual(batch, hudCamara, V_WIDTH, V_HEIGHT, mostrarClima);

        renderizarIluminacionNocturna();

        gestorHud.renderizar(batch, reloj, billetera, V_WIDTH, V_HEIGHT);

        if (actualizarInterfaz) {
            stageUi.act(delta);
        }

        stageUi.draw();

        gestorHud.renderizarMensaje(batch, V_WIDTH, V_HEIGHT);
        
        controlSueno.renderizarEfectoVisual(
            shapeRenderer, batch, camara, hudCamara, vp,
            font, reloj, V_WIDTH, V_HEIGHT
        );
    }
    
    
    
    
    
    public void renderizarPausada() {
        renderizarPartida(0, false);
    }
    
    
    
    
    
    private void actualizarCamara() {

    	float medioAnchoCamara = (camara.viewportWidth * camara.zoom) / 2f;
        float medioAltoCamara = (camara.viewportHeight * camara.zoom) / 2f;

        float anchoMapa = controlMapa.getAnchoMapaPixels();
        float altoMapa = controlMapa.getAltoMapaPixels();
        
        float viewportAnchoEfectivo = camara.viewportWidth * camara.zoom;
        float viewportAltoEfectivo = camara.viewportHeight * camara.zoom;

        float camX, camY;

        if (anchoMapa < viewportAnchoEfectivo) {
            camX = anchoMapa / 2f;
        } else {
            camX = MathUtils.clamp(jugador.getX(), medioAnchoCamara, anchoMapa - medioAnchoCamara);
        }

        if (altoMapa < viewportAltoEfectivo) {
            camY = altoMapa / 2f;
        } else {
        	
            camY = MathUtils.clamp(jugador.getY(), medioAltoCamara, altoMapa - medioAltoCamara);
        }

        camara.position.set(camX, camY, 0);
        camara.update();
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
        mapaGranja.dispose();
        pantallaTienda.dispose();
        controlClima.dispose();
        gestorInventario.dispose();
        gestorHud.dispose();
        stageUi.dispose();
        skinUi.dispose();
        if (gestorAudio != null) {
            gestorAudio.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (gestorPrompt != null) {
            gestorPrompt.dispose();
        }

        if (gestorCultivos != null) gestorCultivos.dispose();
        
        if (font != null) {
            font.dispose();
        }
    }
    
    
}