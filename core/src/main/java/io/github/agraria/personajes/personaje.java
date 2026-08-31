package io.github.agraria.personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class personaje {
    private float x, y;
    private float velocidad = 150f;
    private Texture textura;
    private Rectangle hitbox;

    public personaje(float xInicial, float yInicial) {
        this.x = xInicial;
        this.y = yInicial;
        
        this.textura = new Texture("personaje.png");
        this.textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        
        // La hitbox se mantiene en los pies del personaje
        this.hitbox = new Rectangle(x, y, textura.getWidth(), textura.getHeight() / 2f);
    }

    public void actualizar(float delta, Array<Polygon> colisiones, float limiteAncho, float limiteAlto) {
        float xAnterior = x;
        float yAnterior = y;

        // Creamos un polígono para comprobar la interacción con la hitbox del jugador
        Polygon hitboxPoly = new Polygon(new float[]{
            0, 0,
            hitbox.width, 0,
            hitbox.width, hitbox.height,
            0, hitbox.height
        });

        // --- Movimiento Horizontal ---
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) x -= velocidad * delta;
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) x += velocidad * delta;

        hitboxPoly.setPosition(x, y);
        for (Polygon colision : colisiones) {
            if (Intersector.overlapConvexPolygons(hitboxPoly, colision)) {
                x = xAnterior; // Cancela movimiento en X si choca
                break;
            }
        }

        // --- Movimiento Vertical ---
        if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) y += velocidad * delta;
        if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) y -= velocidad * delta;

        hitboxPoly.setPosition(x, y);
        for (Polygon colision : colisiones) {
            if (Intersector.overlapConvexPolygons(hitboxPoly, colision)) {
                y = yAnterior; // Cancela movimiento en Y si choca
                break;
            }
        }

        // Limites del borde del mapa
        x = MathUtils.clamp(x, 0, limiteAncho - textura.getWidth());
        y = MathUtils.clamp(y, 0, limiteAlto - textura.getHeight());

        hitbox.setPosition(x, y);
    }

    public void renderizar(SpriteBatch batch) {
        batch.draw(textura, x, y);
    }

    public void liberarRecursos() {
        textura.dispose();
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public Rectangle getHitbox() { return hitbox; }
}