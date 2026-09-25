package io.github.agraria.personajes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Personaje {
    private float x, y;
    private float velocidad = 150f;
    private Rectangle hitbox;

    // Variables de dirección controladas por ControlJugador
    public int dirX = 0;
    public int dirY = 0;

    // --- Carga de imágenes por dirección (2 por cada tecla) ---
    private Texture texIzq1, texIzq2;  // Tecla A / LEFT
    private Texture texDer1, texDer2;  // Tecla D / RIGHT
    private Texture texArr1, texArr2;  // Tecla W / UP
    private Texture texAba1, texAba2;  // Tecla S / DOWN

    private Texture texturaActual;
    private float stateTime = 0f; // Acumulador para la animación

    public Personaje(float xInicial, float yInicial) {
        this.x = xInicial;
        this.y = yInicial;

        // 1. Carga de las 2 imágenes para cada dirección
        this.texIzq1 = new Texture("personaje/pjPerfilIzquierdoMovIzquierdo.png");
        this.texIzq2 = new Texture("personaje/pjPerfilIzquierdoMovDerecho.png");

        this.texDer1 = new Texture("personaje/pjPerfilDerechoMovIzquierdo.png");
        this.texDer2 = new Texture("personaje/pjPerfilDerechoMovDerecho.png");

        this.texArr1 = new Texture("personaje/pjEspaldaMovIzquierdo.png");
        this.texArr2 = new Texture("personaje/pjEspaldaMovDerecho.png");

        this.texAba1 = new Texture("personaje/pjFrenteMovIzquierdo.png");
        this.texAba2 = new Texture("personaje/pjFrenteMovDerecho.png");

        // Aplicar el filtro Nearest para mantener nítido el Pixel Art
        aplicarFiltro(texIzq1, texIzq2, texDer1, texDer2, texArr1, texArr2, texAba1, texAba2);

        // Textura por defecto (vista frontal estática)
        this.texturaActual = texAba1;

        // Hitbox basada en las dimensiones de las imágenes
        this.hitbox = new Rectangle(x, y, texturaActual.getWidth(), texturaActual.getHeight() / 2f);
    }

    private void aplicarFiltro(Texture... texturas) {
        for (Texture t : texturas) {
            t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }

    public void actualizar(float delta, Array<Polygon> colisiones, float limiteAncho, float limiteAlto) {
        float xAnterior = x;
        float yAnterior = y;
        boolean estaMoviendose = false;

        // --- Movimiento basado en dirX y dirY provistos por ControlJugador ---
        if (dirX != 0 || dirY != 0) {
            estaMoviendose = true;
            stateTime += delta;

            // Desplazamiento
            x += dirX * velocidad * delta;
            y += dirY * velocidad * delta;

            // Selección de textura según la dirección activa
            if (dirX < 0) {
                texturaActual = (stateTime % 0.3f < 0.15f) ? texIzq1 : texIzq2;
            } else if (dirX > 0) {
                texturaActual = (stateTime % 0.3f < 0.15f) ? texDer1 : texDer2;
            } else if (dirY > 0) {
                texturaActual = (stateTime % 0.3f < 0.15f) ? texArr1 : texArr2;
            } else if (dirY < 0) {
                texturaActual = (stateTime % 0.3f < 0.15f) ? texAba1 : texAba2;
            }
        }

        // Si se detiene, se resetea el tiempo de animación
        if (!estaMoviendose) {
            stateTime = 0f;
        }

        // --- Sistema de Colisiones ---
        Polygon hitboxPoly = new Polygon(new float[]{
            0, 0,
            hitbox.width, 0,
            hitbox.width, hitbox.height,
            0, hitbox.height
        });

        // Colisión X
        hitboxPoly.setPosition(x, yAnterior);
        for (Polygon colision : colisiones) {
            if (Intersector.overlapConvexPolygons(hitboxPoly, colision)) {
                x = xAnterior;
                break;
            }
        }

        // Colisión Y
        hitboxPoly.setPosition(x, y);
        for (Polygon colision : colisiones) {
            if (Intersector.overlapConvexPolygons(hitboxPoly, colision)) {
                y = yAnterior;
                break;
            }
        }

        // Límites del mapa
        x = MathUtils.clamp(x, 0, limiteAncho - texturaActual.getWidth());
        y = MathUtils.clamp(y, 0, limiteAlto - texturaActual.getHeight());

        hitbox.setPosition(x, y);
    }

    public void renderizar(SpriteBatch batch) {
        batch.draw(texturaActual, x, y);
    }

    public void liberarRecursos() {
        texIzq1.dispose();
        texIzq2.dispose();
        texDer1.dispose();
        texDer2.dispose();
        texArr1.dispose();
        texArr2.dispose();
        texAba1.dispose();
        texAba2.dispose();
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public Rectangle getHitbox() { return hitbox; }
}