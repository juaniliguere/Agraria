package io.github.agraria.pantallas;

import io.github.agraria.cultivos.ControlCultivos;
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
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class PantallaGranja extends ScreenAdapter {

    // =========================
    // JUGADOR
    // =========================

    private SpriteBatch batch;
    private Personaje jugador;


    // =========================
    // MAPA
    // =========================

    private TiledMap mapa;
    private OrthogonalTiledMapRenderer mapRenderer;

    private OrthographicCamera camara;
    private Viewport viewport;

    // Colisiones obtenidas desde Tiled
    private Array<Polygon> colisionesMapa;

    // Dimensiones del mapa en píxeles
    private float anchoMapaPixels;
    private float altoMapaPixels;

    // Dimensiones del mapa en tiles
    private int mapWidthTiles;
    private int mapHeightTiles;

    // Tamaño de cada tile
    private int tileWidth;
    private int tileHeight;

    public static final int V_WIDTH = 800;
    public static final int V_HEIGHT = 600;

    // Índices de las capas que se renderizan antes y después del jugador
    private int idxAbajo;
    private int idxArriba;


    // =========================
    // CULTIVOS
    // =========================

    private ControlCultivos controlCultivos;

    // Guarda las etapas de cada tipo de cultivo.
    //
    // Ejemplo:
    // ZANAHORIA → [etapa0, etapa1, etapa2, etapa3]
    // PAPA       → [etapa0, etapa1, etapa2, etapa3]
    private ObjectMap<TipoCultivo, TextureRegion[]> etapasCultivos;

    // Texturas originales que debemos liberar al cerrar la pantalla
    private ObjectMap<TipoCultivo, Texture> texturasCultivos;


    // =========================
    // TIEMPO
    // =========================



    private Reloj reloj;


    public PantallaGranja() {

        // =========================
        // JUGADOR
        // =========================

        batch = new SpriteBatch();

        jugador = new Personaje(125, 125);


        // =========================
        // CÁMARA Y VIEWPORT
        // =========================

        camara = new OrthographicCamera();

        viewport = new FitViewport(
                V_WIDTH,
                V_HEIGHT,
                camara
        );

        viewport.apply();

        // Zoom inicial
        camara.zoom = 0.5f;


        // =========================
        // MAPA
        // =========================

        TmxMapLoader.Parameters params =
                new TmxMapLoader.Parameters();

        params.textureMinFilter =
                Texture.TextureFilter.Nearest;

        params.textureMagFilter =
                Texture.TextureFilter.Nearest;

        mapa = new TmxMapLoader().load(
                "pantallas/zona1/AgrariaMapa.tmx",
                params
        );

        mapRenderer = new OrthogonalTiledMapRenderer(mapa);


        // =========================
        // INFORMACIÓN DEL MAPA
        // =========================

        MapProperties prop = mapa.getProperties();

        mapWidthTiles =
                prop.get("width", Integer.class);

        mapHeightTiles =
                prop.get("height", Integer.class);

        tileWidth =
                prop.get("tilewidth", Integer.class);

        tileHeight =
                prop.get("tileheight", Integer.class);

        anchoMapaPixels =
                mapWidthTiles * tileWidth;

        altoMapaPixels =
                mapHeightTiles * tileHeight;


        // =========================
        // CONTROL DE CULTIVOS
        // =========================

        boolean[][] matrizPlantable =
                ControlCultivos.generarMatrizPlantableDesdeMapa(
                        mapa,
                        mapWidthTiles,
                        mapHeightTiles
                );

        controlCultivos =
                new ControlCultivos(
                        mapWidthTiles,
                        mapHeightTiles,
                        tileWidth,
                        matrizPlantable
                );


        // =========================
        // COLISIONES
        // =========================

        colisionesMapa = new Array<>();

        if (mapa.getLayers().get("Colisiones") != null) {

            for (MapObject objeto :
                    mapa.getLayers().get("Colisiones").getObjects()) {

                // Colisión dibujada como polígono
                if (objeto instanceof PolygonMapObject) {

                    colisionesMapa.add(
                            ((PolygonMapObject) objeto).getPolygon()
                    );

                }

                // Colisión dibujada como rectángulo
                else if (objeto instanceof RectangleMapObject) {

                    Rectangle rect =
                            ((RectangleMapObject) objeto).getRectangle();

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


        // =========================
        // CAPAS DEL MAPA
        // =========================

        idxAbajo =
                mapa.getLayers().getIndex("abajo");

        idxArriba =
                mapa.getLayers().getIndex("arriba");


        // =========================
        // TEXTURAS DE CULTIVOS
        // =========================

        cargarTexturasCultivos();


        // =========================
        // RELOJ
        // =========================

        reloj =
                new Reloj();
    }


    /**
     * Carga automáticamente las texturas de todos
     * los tipos de cultivo existentes.
     *
     * Si mañana agregamos PAPA o TRIGO a TipoCultivo,
     * este código los detecta automáticamente.
     */
    private void cargarTexturasCultivos() {

        etapasCultivos = new ObjectMap<>();
        texturasCultivos = new ObjectMap<>();

        for (TipoCultivo tipo : TipoCultivo.values()) {

            Texture textura =
                    new Texture(tipo.getRutaTextura());

            textura.setFilter(
                    Texture.TextureFilter.Nearest,
                    Texture.TextureFilter.Nearest
            );

            texturasCultivos.put(tipo, textura);


            // Actualmente todos los cultivos
            // tienen 4 etapas.
            int cantidadEtapas = tipo.getCantidadEtapas();

            int anchoEtapa =
                    textura.getWidth() / cantidadEtapas;

            int altoEtapa =
                    textura.getHeight();

            TextureRegion[] etapas =
                    new TextureRegion[cantidadEtapas];


            // Cortamos el spritesheet en regiones
            for (int i = 0; i < cantidadEtapas; i++) {

                etapas[i] = new TextureRegion(
                        textura,
                        i * anchoEtapa,
                        0,
                        anchoEtapa,
                        altoEtapa
                );
            }

            etapasCultivos.put(tipo, etapas);
        }
    }


    @Override
    public void show() {

        ControlJugador controlador =
                new ControlJugador(jugador, this);

        Gdx.input.setInputProcessor(controlador);
    }


    /**
     * Se ejecuta cuando el jugador hace clic en el mapa.
     */
    public void hacerClicEn(int screenX, int screenY) {

        // Convertimos coordenadas de pantalla
        // a coordenadas del mundo.
        com.badlogic.gdx.math.Vector3 posMundo =
                camara.unproject(
                        new com.badlogic.gdx.math.Vector3(
                                screenX,
                                screenY,
                                0
                        )
                );


        Parcela parcela =
                controlCultivos.getParcelaEnPx(
                        posMundo.x,
                        posMundo.y
                );


        if (parcela == null) {

            System.out.println(
                    "-> La parcela está fuera del mapa."
            );

            return;
        }


        System.out.println(
                "-> Parcela encontrada. Plantable: "
                        + parcela.isEsPlantable()
                        + " | Cultivo: "
                        + parcela.tieneCultivo()
        );


        // Por ahora seguimos plantando zanahoria
        // porque todavía no tenemos selección de cultivo.
        if (!parcela.tieneCultivo()
                && parcela.isEsPlantable()) {

            boolean exito =
                    controlCultivos.plantarEn(
                            posMundo.x,
                            posMundo.y,
                            TipoCultivo.ZANAHORIA
                    );

            System.out.println(
                    "-> PlantarEn devolvió: " + exito
            );
        }
    }


    @Override
    public void render(float delta) {


        // =========================
        // ZOOM
        // =========================

        if (Gdx.input.isKeyPressed(Keys.PLUS)
                || Gdx.input.isKeyPressed(Keys.EQUALS)) {

            camara.zoom -= 0.5f * delta;
        }

        if (Gdx.input.isKeyPressed(Keys.MINUS)) {

            camara.zoom += 0.5f * delta;
        }

        camara.zoom =
                MathUtils.clamp(
                        camara.zoom,
                        0.2f,
                        1.5f
                );


        // =========================
        // TIEMPO
        // =========================

        int minutosPasados =
                reloj.actualizar(delta);

        if (minutosPasados > 0) {

            controlCultivos.pasarTiempo(
                    minutosPasados
            );
        }


        // =========================
        // JUGADOR
        // =========================

        jugador.actualizar(
                delta,
                colisionesMapa,
                anchoMapaPixels,
                altoMapaPixels
        );


        // =========================
        // CÁMARA
        // =========================

        actualizarCamara();


        // =========================
        // DIBUJAR
        // =========================

        ScreenUtils.clear(
                0f,
                0f,
                0f,
                1f
        );


        mapRenderer.setView(camara);

        // Primero se dibuja el suelo
        mapRenderer.render(
                new int[]{idxAbajo}
        );


        // Después cultivos y jugador
        batch.setProjectionMatrix(
                camara.combined
        );

        batch.begin();

        renderizarCultivos();

        jugador.renderizar(batch);

        batch.end();


        // Finalmente se dibuja lo que va por encima
        mapRenderer.render(
                new int[]{idxArriba}
        );
    }


    /**
     * Actualiza la posición de la cámara
     * siguiendo al jugador.
     */
    private void actualizarCamara() {

        float medioAnchoCamara =
                (camara.viewportWidth * camara.zoom) / 2f;

        float medioAltoCamara =
                (camara.viewportHeight * camara.zoom) / 2f;


        float camX =
                MathUtils.clamp(
                        jugador.getX(),
                        medioAnchoCamara,
                        anchoMapaPixels - medioAnchoCamara
                );

        float camY =
                MathUtils.clamp(
                        jugador.getY(),
                        medioAltoCamara,
                        altoMapaPixels - medioAltoCamara
                );


        camara.position.set(
                camX,
                camY,
                0
        );

        camara.update();
    }


    /**
     * Dibuja todos los cultivos existentes en el mapa.
     */
    private void renderizarCultivos() {

        for (int fila = 0;
             fila < mapHeightTiles;
             fila++) {

            for (int col = 0;
                 col < mapWidthTiles;
                 col++) {


                Parcela parcela =
                        controlCultivos.getParcela(
                                col,
                                fila
                        );


                if (parcela == null
                        || !parcela.tieneCultivo()) {

                    continue;
                }


                Cultivo cultivo =
                        parcela.getCultivoActual();


                TipoCultivo tipo =
                        cultivo.getTipo();


                int etapa =
                        cultivo.getEtapaActual();


                TextureRegion[] etapas =
                        etapasCultivos.get(tipo);


                // Evita intentar acceder a una etapa
                // que no exista.
                if (etapas == null
                        || etapa < 0
                        || etapa >= etapas.length) {

                    continue;
                }


                float xPx =
                        col * tileWidth;

                float yPx =
                        fila * tileHeight;


                batch.draw(
                        etapas[etapa],
                        xPx,
                        yPx,
                        tileWidth,
                        tileHeight
                );
            }
        }
    }


    @Override
    public void resize(
            int width,
            int height) {

        viewport.update(
                width,
                height
        );
    }


    @Override
    public void dispose() {

        batch.dispose();

        jugador.liberarRecursos();

        mapa.dispose();

        mapRenderer.dispose();


        // Liberamos todas las texturas
        // cargadas automáticamente.
        for (Texture textura :
                texturasCultivos.values()) {

            textura.dispose();
        }
    }
}