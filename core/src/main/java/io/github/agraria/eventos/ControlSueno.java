package io.github.agraria.eventos;

import io.github.agraria.cultivos.ControlCultivos;
import io.github.tiempo.Reloj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ControlSueno {

    public enum EstadoTransicion { IDLE, FADE_OUT, FAST_FORWARD, FADE_IN }
    private EstadoTransicion estadoTransicion = EstadoTransicion.IDLE;

    private float fadeAlpha = 0f;
    private int targetDiaDormir = 0;

    public void iniciarDormir(Reloj reloj) {
        if (estadoTransicion == EstadoTransicion.IDLE) {
            targetDiaDormir = reloj.getDias() + 1;
            estadoTransicion = EstadoTransicion.FADE_OUT;
            System.out.println("-> Acostándose a dormir...");
        }
    }

    public void actualizar(float delta, Reloj reloj, ControlCultivos controlCultivos) {
        if (estadoTransicion == EstadoTransicion.IDLE) return;

        if (estadoTransicion == EstadoTransicion.FADE_OUT) {
            fadeAlpha += delta * 2f; 
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                estadoTransicion = EstadoTransicion.FAST_FORWARD; 
            }
        } 
        else if (estadoTransicion == EstadoTransicion.FAST_FORWARD) {
            int minutosAvanzadosEsteFrame = 0;
            
            for (int i = 0; i < 25; i++) {
                if (reloj.getDias() > targetDiaDormir || (reloj.getDias() == targetDiaDormir && reloj.getHoras() >= 12)) {
                    break;
                }
                reloj.avanzarUnMinuto();
                minutosAvanzadosEsteFrame++;
            }
            
            if (minutosAvanzadosEsteFrame > 0) {
                controlCultivos.pasarTiempo(minutosAvanzadosEsteFrame);
            }

            if (reloj.getDias() > targetDiaDormir || (reloj.getDias() == targetDiaDormir && reloj.getHoras() >= 12)) {
                System.out.println("-> ¡Despertando a mediodía!");
                estadoTransicion = EstadoTransicion.FADE_IN;
            }
        } 
        else if (estadoTransicion == EstadoTransicion.FADE_IN) {
            fadeAlpha -= delta * 2f; 
            if (fadeAlpha <= 0f) {
                fadeAlpha = 0f;
                estadoTransicion = EstadoTransicion.IDLE; 
            }
        }
    }

    public void renderizarEfectoVisual(ShapeRenderer shapeRenderer, SpriteBatch batch, OrthographicCamera camara, OrthographicCamera hudCamara, Viewport viewport, BitmapFont font, Reloj reloj, int vWidth, int vHeight) {
        if (estadoTransicion == EstadoTransicion.IDLE) return;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, fadeAlpha);
        shapeRenderer.rect(
            camara.position.x - (viewport.getWorldWidth() * camara.zoom),
            camara.position.y - (viewport.getWorldHeight() * camara.zoom),
            viewport.getWorldWidth() * camara.zoom * 2f,
            viewport.getWorldHeight() * camara.zoom * 2f
        );
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(hudCamara.combined);
        batch.begin();
        font.getData().setScale(2f);
        
        GlyphLayout layout = new GlyphLayout();
        float centroX = vWidth / 2f;
        float centroY = vHeight / 2f;

        if (estadoTransicion == EstadoTransicion.FAST_FORWARD || estadoTransicion == EstadoTransicion.FADE_OUT) {
            layout.setText(font, "Durmiendo...");
            font.draw(batch, layout, centroX - layout.width / 2f, centroY + 60);

            layout.setText(font, "Hora: " + reloj.getHoraFormateada());
            font.draw(batch, layout, centroX - layout.width / 2f, centroY + 10);

            layout.setText(font, "Dia: " + reloj.getDias());
            font.draw(batch, layout, centroX - layout.width / 2f, centroY - 40);

        } else if (estadoTransicion == EstadoTransicion.FADE_IN) {
            layout.setText(font, "¡Buenos días!");
            font.draw(batch, layout, centroX - layout.width / 2f, centroY + 30);

            layout.setText(font, "Dia " + reloj.getDias() + " - Mediodia");
            font.draw(batch, layout, centroX - layout.width / 2f, centroY - 20);
        }
        
        font.getData().setScale(1f);
        batch.end();
    }

    public boolean estaDurmiendo() {
        return estadoTransicion != EstadoTransicion.IDLE;
    }
}