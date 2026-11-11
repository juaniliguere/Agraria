
package io.github.agraria.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.agraria.Agraria;

public class PantallaInicio extends ScreenAdapter {

    private Agraria juego;
    private SpriteBatch batch;
    private Texture logo;
    private static final float DURACION_FADE_IN = 1.8f;
    private static final float DURACION_ESPERA = 2.7f;
    private static final float DURACION_FADE_OUT = 1.5f;
    
    private float tiempo = 0f;
    private float alpha = 0f;

    private static final float TAMANO_INICIAL = 300f;
    private static final float TAMANO_FINAL = 450f;
    private static final float DURACION_CRECIMIENTO = 3.5f;
    
    public PantallaInicio(Agraria juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        logo = new Texture(
            Gdx.files.internal("pantallas/inicio/agrarialogo.png")
        );
    }


@Override
public void render(float delta) {

    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    tiempo += delta;

    // Calcular la transparencia del logo
    if (tiempo < DURACION_FADE_IN) {

        // Fade In
        alpha = tiempo / DURACION_FADE_IN;

    } else if (tiempo < DURACION_FADE_IN + DURACION_ESPERA) {

        // Mantener visible
        alpha = 1f;

    } else if (tiempo < DURACION_FADE_IN
            + DURACION_ESPERA + DURACION_FADE_OUT) {

        // Fade Out
        float tiempoFadeOut =
            tiempo - DURACION_FADE_IN - DURACION_ESPERA;

        alpha = 1f - (tiempoFadeOut / DURACION_FADE_OUT);

    } else {

        alpha = 0f;
        juego.setScreen(new PantallaMenu(juego));
        return;
    }

    // Dibujar logo
    
    float progreso = Math.min(tiempo / DURACION_CRECIMIENTO, 1f);
    float ancho = TAMANO_INICIAL +
    	    (TAMANO_FINAL - TAMANO_INICIAL) * progreso;
    float alto = ancho * logo.getHeight() / logo.getWidth();

    float x = (Gdx.graphics.getWidth() - ancho) / 2f;
    float y = (Gdx.graphics.getHeight() - alto) / 2f;

    batch.begin();

    batch.setColor(1f, 1f, 1f, alpha);
    batch.draw(logo, x, y, ancho, alto);
    batch.setColor(1f, 1f, 1f, 1f);

    batch.end();
}


    @Override
    public void dispose() {
        batch.dispose();
        logo.dispose();
    }
}
