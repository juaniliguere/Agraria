
package io.github.agraria.ui;

import io.github.agraria.inventario.GestorInventario;
import io.github.agraria.inventario.Inventario;
import io.github.agraria.economia.Billetera;
import io.github.tiempo.Reloj;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GestorHud {

    private BitmapFont font;
    private OrthographicCamera hudCamara;
    private Inventario inventario;
    private GestorInventario gestorInventario;

    // Mensajes temporales
    private String mensajeActual = "";
    private float tiempoMensaje = 0f;

    private static final float DURACION_MENSAJE = 1.8f;

    private GlyphLayout layout = new GlyphLayout();

    public GestorHud(Inventario inventario, int vWidth, int vHeight, BitmapFont font) {
        this.font = font;

        this.hudCamara = new OrthographicCamera();
        this.hudCamara.setToOrtho(false, vWidth, vHeight);

        this.inventario = inventario;
        this.gestorInventario = new GestorInventario(inventario);
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

        // Información superior
        font.getData().setScale(1.2f);

        String textoHud = "Dia: " + reloj.getDias()
                + " | Hora: " + reloj.getHoraFormateada()
                + " | Monedas: $" + billetera.getMonedas();

        font.draw(batch, textoHud, 20, vHeight - 20);

        // Mensaje temporal
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
    }
}
