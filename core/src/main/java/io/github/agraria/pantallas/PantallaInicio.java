
package io.github.agraria.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.agraria.Agraria;
import com.badlogic.gdx.Input;

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
    
    private OrthographicCamera camara;
    private FitViewport viewport;

    private static final float ANCHO_MUNDO = 800f;
    private static final float ALTO_MUNDO = 600f;
    
    public PantallaInicio(Agraria juego) {
        this.juego = juego;
    }


    
    @Override
    public void show() {
        batch = new SpriteBatch();

        camara = new OrthographicCamera();
        viewport = new FitViewport(ANCHO_MUNDO, ALTO_MUNDO, camara);
        viewport.apply();

        camara.position.set(ANCHO_MUNDO / 2f, ALTO_MUNDO / 2f, 0);
        camara.update();

        logo = new Texture(
            Gdx.files.internal("pantallas/inicio/agrarialogo.png")
        );
    }

    
    private void saltarIntro() {
        juego.setScreen(new PantallaMenu(juego));
    }
    

	@Override
	public void render(float delta) {
	
	    Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    
	    if (Gdx.input.justTouched()
	            || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
	
	        saltarIntro();
	        return;
	    }
	
	    tiempo += delta;
	
	    if (tiempo < DURACION_FADE_IN) {
	
	        alpha = tiempo / DURACION_FADE_IN;
	
	    } else if (tiempo < DURACION_FADE_IN + DURACION_ESPERA) {
	
	        alpha = 1f;
	
	    } else if (tiempo < DURACION_FADE_IN
	            + DURACION_ESPERA + DURACION_FADE_OUT) {
	
	                float tiempoFadeOut =
	            tiempo - DURACION_FADE_IN - DURACION_ESPERA;
	
	        alpha = 1f - (tiempoFadeOut / DURACION_FADE_OUT);
	
	    } else {
	
	        alpha = 0f;
	        saltarIntro();
	        return;
	    }
	
	    
	
	    
	    float progreso = Math.min(tiempo / DURACION_CRECIMIENTO, 1f);
	    float ancho = TAMANO_INICIAL +
	    	    (TAMANO_FINAL - TAMANO_INICIAL) * progreso;
	    float alto = ancho * logo.getHeight() / logo.getWidth();
	
	    float centroX = Gdx.graphics.getWidth() / 2f;
	    float centroY = Gdx.graphics.getHeight() / 2f;
	
	    float x = (ANCHO_MUNDO - ancho) / 2f;
	    float y = (ALTO_MUNDO - alto) / 2f;
	
	    batch.setProjectionMatrix(camara.combined);
	
	    batch.begin();
	
	    batch.setColor(1f, 1f, 1f, alpha);
	    batch.draw(logo, x, y, ancho, alto);
	    batch.setColor(1f, 1f, 1f, 1f);
	
	    batch.end();
	}
	
	
	@Override
	public void resize(int width, int height) {
	    viewport.update(width, height, true);
	}


    @Override
    public void dispose() {
        batch.dispose();
        logo.dispose();
    }
}
