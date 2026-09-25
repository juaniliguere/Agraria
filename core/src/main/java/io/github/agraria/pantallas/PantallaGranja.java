package io.github.agraria.pantallas;

import io.github.agraria.cultivos.GestorCultivos;
import io.github.agraria.cultivos.Cultivo;
import io.github.agraria.cultivos.Parcela;
import io.github.agraria.cultivos.TipoCultivo;

import io.github.agraria.control.ControlJugador;
import io.github.agraria.personajes.Personaje;
import io.github.tiempo.Reloj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.math.MathUtils;

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
    

    public PantallaGranja() {

        batch = new SpriteBatch();
        jugador = new Personaje(125, 125);

        // =========================
        // CÁMARA Y VIEWPORT
        // =========================

        camara = new OrthographicCamera();
        vp = new FitViewport(V_WIDTH, V_HEIGHT, camara);
        vp.apply();
        // Zoom inicial
        camara.zoom = 0.5f;

        controlMapa = new GestorMapa("pantallas/zona1/AgrariaMapa.tmx");

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
    }

    //Carga automáticamente las texturas de todos los tipos de cultivo existentes.
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

            // Cortamos el spritesheet en regiones
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

    public void procesarCultivar(int screenX, int screenY) {

        com.badlogic.gdx.math.Vector3 posMundo = camara.unproject(new com.badlogic.gdx.math.Vector3(screenX, screenY, 0));

        Parcela parcela = gestorCultivos.getParcelaEnPx(posMundo.x, posMundo.y);

        if (parcela == null) {

            System.out.println("-> La parcela está fuera del mapa.");
            return;
            
        }

        System.out.println("-> Parcela encontrada. Plantable: " + parcela.isEsPlantable() + " | Cultivo: " + parcela.tieneCultivo());

        if (!parcela.tieneCultivo() && parcela.isEsPlantable()) {

            boolean exito = gestorCultivos.plantarEn(posMundo.x, posMundo.y, TipoCultivo.ZANAHORIA);

            System.out.println("-> PlantarEn devolvió: " + exito);
            
        }
    }
    
    public void procesarCosechar() {
    	

        // Posición de los pies/centro del personaje.
        float x = jugador.getCentroX();
        float y = jugador.getPiesY();

        // Intentamos cosechar en esa posición.
        Cultivo cosechado = gestorCultivos.cosecharEn(x, y);

        if (cosechado != null) {
            System.out.println("-> Cultivo cosechado: " + cosechado.getTipo());
        } else {
            System.out.println("-> No hay un cultivo cosechable delante.");
        }
    	
    }
    
    public void procesarDormir() {
    	
    	int minutosDormidos = reloj.dormir(6);
    	gestorCultivos.pasarTiempo(minutosDormidos);
    	
        System.out.println("-> durmio" + minutosDormidos + " minutos.");
        System.out.println("-> Día: " + reloj.getDias() + " | Hora: " + reloj.getHoraFormateada());
    	
    }

    @Override
    public void render(float delta) {

        // =========================
        // ZOOM
        // =========================

//        if (Gdx.input.isKeyPressed(Keys.M) || Gdx.input.isKeyPressed(Keys.EQUALS)) {
//
//            camara.zoom -= 0.5f * delta;
//            System.out.println(camara.zoom);
//            
//        }
//
//        if (Gdx.input.isKeyPressed(Keys.MINUS)) {
//        	
//            camara.zoom += 0.5f * delta;
//            System.out.println(camara.zoom);
//            
//        }
//
//        camara.zoom = MathUtils.clamp(camara.zoom, 0.2f, 1.5f);

        int minutosPasados = reloj.actualizar(delta);

        if (minutosPasados > 0) {

            gestorCultivos.pasarTiempo( minutosPasados);
            
        }

        jugador.actualizar(delta, controlMapa.getColisionesMapa(), controlMapa.getAnchoMapaPixels(), controlMapa.getAltoMapaPixels());


        actualizarCamara();

        // =========================
        // DIBUJAR
        // =========================

        ScreenUtils.clear(0f, 0f, 0f, 1f);

        controlMapa.setView(camara);
        
        controlMapa.renderAbajo();


        // Después cultivos y jugador
        batch.setProjectionMatrix(camara.combined);

        batch.begin();

        renderizarCultivos();
        jugador.renderizar(batch);

        batch.end();

        controlMapa.renderArriba();

    }

    /*Actualiza la posición de la cámara siguiendo al jugador*/
    private void actualizarCamara() {

        float medioAnchoCamara = (camara.viewportWidth * camara.zoom) / 2f;
        float medioAltoCamara = (camara.viewportHeight * camara.zoom) / 2f;

        float camX = MathUtils.clamp(jugador.getX(), medioAnchoCamara, controlMapa.getAltoMapaPixels() - medioAnchoCamara);
        float camY = MathUtils.clamp(jugador.getY(), medioAltoCamara, controlMapa.getAltoMapaPixels() - medioAltoCamara);

        camara.position.set(camX, camY, 0);
        camara.update();
    }


    /*Dibuja todos los cultivos existentes en el mapa*/
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

                // Evita intentar acceder a una etapa que no exista.
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
        
    }


    @Override
    public void dispose() {

        batch.dispose();

        jugador.liberarRecursos();
        
        controlMapa.dispose();

        // Liberamos todas las texturas cargadas automáticamente.
        for (Texture textura : texturasCultivos.values()) {

            textura.dispose();
            
        }
    }
}