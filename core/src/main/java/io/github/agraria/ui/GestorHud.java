package io.github.agraria.ui;

import io.github.agraria.inventario.GestorInventario;
import io.github.agraria.inventario.Inventario;
import io.github.agraria.economia.Billetera;
import io.github.agraria.progreso.SistemaNiveles;
import io.github.tiempo.Reloj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GestorHud {

    private BitmapFont font;
    private OrthographicCamera hudCamara;
    private Inventario inventario;
    private GestorInventario gestorInventario;
    private SistemaNiveles sistemaNiveles;

    private Texture texturaBarraVacia;
    private Texture texturaBarraLlena;
    private TextureRegion regionBarraLlena;

    private static final float BARRA_X = 20f;
    private static final float ESCALA_BARRA = 2f; 

    private Texture texturaMonedaBase;
    private Texture texturaMoneda;

    private static final float ESCALA_PLACA = 2f; 
    private static final float MARGEN_ICONO = 8f; 
    private static final float ESPACIO_TEXTO = 8f;  

    private Texture texturaReloj;
    private TextureRegion[] framesReloj;

    private static final int RELOJ_COLUMNAS = 8;    
    private static final int RELOJ_FILAS = 1;       
    private static final float ESCALA_RELOJ = 2f;    
    private static final float MARGEN_RELOJ = 20f;  
    private static final float ESPACIO_RELOJ_TEXTO = 8f; 
    private static final float HORA_FRAME_0 = 6f;   

    private String mensajeActual = "";
    private float tiempoMensaje = 0f;

    private static final float DURACION_MENSAJE = 1.8f;

    private GlyphLayout layout = new GlyphLayout();

    public GestorHud(Inventario inventario, int vWidth, int vHeight, BitmapFont font, SistemaNiveles sistemaNiveles) {
        this.font = font;
        this.sistemaNiveles = sistemaNiveles;

        this.hudCamara = new OrthographicCamera();
        this.hudCamara.setToOrtho(false, vWidth, vHeight);

        this.inventario = inventario;
        this.gestorInventario = new GestorInventario(inventario);

        texturaBarraVacia = new Texture(Gdx.files.internal("ui/barraXPVacia.png"));
        texturaBarraLlena = new Texture(Gdx.files.internal("ui/barraXPllena.png"));
        texturaBarraVacia.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        texturaBarraLlena.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        regionBarraLlena = new TextureRegion(texturaBarraLlena);

        texturaMonedaBase = new Texture(Gdx.files.internal("ui/monedaBase.png"));
        texturaMoneda = new Texture(Gdx.files.internal("ui/moneda.png"));
        texturaMonedaBase.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        texturaMoneda.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        texturaReloj = new Texture(Gdx.files.internal("ui/reloj.png"));
        texturaReloj.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        int anchoFrame = texturaReloj.getWidth() / RELOJ_COLUMNAS;
        int altoFrame = texturaReloj.getHeight() / RELOJ_FILAS;

        framesReloj = new TextureRegion[RELOJ_COLUMNAS * RELOJ_FILAS];
        for (int fila = 0; fila < RELOJ_FILAS; fila++) {
            for (int col = 0; col < RELOJ_COLUMNAS; col++) {
                framesReloj[fila * RELOJ_COLUMNAS + col] =
                        new TextureRegion(texturaReloj, col * anchoFrame, fila * altoFrame, anchoFrame, altoFrame);
            }
        }
    }

    public void actualizar(float delta) {

        if (tiempoMensaje > 0) {
            tiempoMensaje -= delta;

            if (tiempoMensaje <= 0) {
                mensajeActual = "";
                tiempoMensaje = 0;
            }
        }
    }

    public void mostrarMensaje(String mensaje) {
        mensajeActual = mensaje;
        tiempoMensaje = DURACION_MENSAJE;
    }

    public void renderizar(SpriteBatch batch, Reloj reloj, Billetera billetera, int vWidth, int vHeight) {

        batch.setProjectionMatrix(hudCamara.combined);
        batch.begin();

        font.getData().setScale(1.2f);

        String textoHud = "Dia: " + reloj.getDias();

        font.draw(batch, textoHud, 20, vHeight - 20);

        renderizarBarraXp(batch, vHeight);

        renderizarMonedas(batch, billetera, vHeight);

        renderizarReloj(batch, reloj, vWidth, vHeight);

                if (tiempoMensaje > 0 && !mensajeActual.isEmpty()) {

            font.getData().setScale(1.3f);

            layout.setText(font, mensajeActual);

            float x = (vWidth - layout.width) / 2f;
            float y = vHeight * 0.25f;

            font.draw(batch, mensajeActual, x, y);
        }

        font.getData().setScale(1f);

        gestorInventario.renderizar(batch, font, vWidth, vHeight);

        batch.end();
    }


        private void renderizarBarraXp(SpriteBatch batch, int vHeight) {

        float anchoBarra = texturaBarraVacia.getWidth() * ESCALA_BARRA;
        float altoBarra = texturaBarraVacia.getHeight() * ESCALA_BARRA;

                String texto;
        if (sistemaNiveles.getNivel() >= SistemaNiveles.NIVEL_MAXIMO) {
            texto = "Nivel " + sistemaNiveles.getNivel() + " (MAX)";
        } else {
            texto = "Nivel " + sistemaNiveles.getNivel()
                    + "  -  " + sistemaNiveles.getXpActual()
                    + " / " + sistemaNiveles.xpParaSiguienteNivel() + " XP";
        }

        font.getData().setScale(0.9f);
        font.draw(batch, texto, BARRA_X, vHeight - 55);

                float barraY = vHeight - 80 - altoBarra;

                batch.draw(texturaBarraVacia, BARRA_X, barraY, anchoBarra, altoBarra);

        float progreso = sistemaNiveles.getProgreso();
        int pxVisibles = (int) (texturaBarraLlena.getWidth() * progreso);

        if (pxVisibles > 0) {
            regionBarraLlena.setRegion(0, 0, pxVisibles, texturaBarraLlena.getHeight());
            batch.draw(regionBarraLlena, BARRA_X, barraY, pxVisibles * ESCALA_BARRA, altoBarra);
        }
    }


    private void renderizarMonedas(SpriteBatch batch, Billetera billetera, int vHeight) {

        float anchoPlaca = texturaMonedaBase.getWidth() * ESCALA_PLACA;
        float altoPlaca = texturaMonedaBase.getHeight() * ESCALA_PLACA;

        float altoBarra = texturaBarraVacia.getHeight() * ESCALA_BARRA;
        float barraY = vHeight - 80 - altoBarra;

        float placaX = BARRA_X;
        float placaY = barraY - 10 - altoPlaca;

        batch.draw(texturaMonedaBase, placaX, placaY, anchoPlaca, altoPlaca);

        float anchoIcono = texturaMoneda.getWidth() * ESCALA_PLACA;
        float altoIcono = texturaMoneda.getHeight() * ESCALA_PLACA;
        float iconoX = placaX + MARGEN_ICONO;
        float iconoY = placaY + (altoPlaca - altoIcono) / 2f;

        batch.draw(texturaMoneda, iconoX, iconoY, anchoIcono, altoIcono);

        String texto = String.valueOf(billetera.getMonedas());

        font.getData().setScale(1f);
        layout.setText(font, texto);

        float textoX = iconoX + anchoIcono + ESPACIO_TEXTO;
        float textoY = placaY + (altoPlaca + layout.height) / 2f;

        font.draw(batch, texto, textoX, textoY);
    }


    private void renderizarReloj(SpriteBatch batch, Reloj reloj, int vWidth, int vHeight) {

        float horaActual = reloj.getHoras() + (reloj.getMinutos() / 60f);
        float horasPorFrame = 24f / framesReloj.length;
        float horasDesdeInicio = ((horaActual - HORA_FRAME_0) % 24f + 24f) % 24f;

        int indice = Math.min((int) (horasDesdeInicio / horasPorFrame), framesReloj.length - 1);
        TextureRegion frame = framesReloj[indice];

        float anchoIcono = frame.getRegionWidth() * ESCALA_RELOJ;
        float altoIcono = frame.getRegionHeight() * ESCALA_RELOJ;

        font.getData().setScale(1.2f);
        layout.setText(font, "00:00 AM");
        float anchoReservado = layout.width;

        float iconoX = vWidth - MARGEN_RELOJ - anchoReservado - ESPACIO_RELOJ_TEXTO - anchoIcono;
        float iconoY = vHeight - MARGEN_RELOJ - altoIcono;

        batch.draw(frame, iconoX, iconoY, anchoIcono, altoIcono);

        // Hora escrita al lado del reloj, centrada verticalmente
        String textoHora = reloj.getHoraFormateada();
        layout.setText(font, textoHora);

        float textoX = iconoX + anchoIcono + ESPACIO_RELOJ_TEXTO;
        float textoY = iconoY + (altoIcono + layout.height) / 2f;

        font.draw(batch, textoHora, textoX, textoY);
    }


    public void renderizarMensaje(SpriteBatch batch, int vWidth, int vHeight) {


        batch.setProjectionMatrix(hudCamara.combined);
        batch.begin();

        font.getData().setScale(1.3f);
        layout.setText(font, mensajeActual);

        float x = (vWidth - layout.width) / 2f;
        float y = vHeight * 0.25f;

        font.draw(batch, mensajeActual, x, y);

        font.getData().setScale(1f);
        batch.end();
    }


    public OrthographicCamera getHudCamara() {
        return hudCamara;
    }

    public void dispose() {

        if (gestorInventario != null) gestorInventario.dispose();
        if (texturaBarraVacia != null) texturaBarraVacia.dispose();
        if (texturaBarraLlena != null) texturaBarraLlena.dispose();
        if (texturaMonedaBase != null) texturaMonedaBase.dispose();
        if (texturaMoneda != null) texturaMoneda.dispose();
        if (texturaReloj != null) texturaReloj.dispose();
    }
}