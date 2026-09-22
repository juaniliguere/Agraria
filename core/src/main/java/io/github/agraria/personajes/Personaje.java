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
    private final float velocidad = 150f; //VELOCIDSD
    private Texture textura;
    private Rectangle hitbox;
    
    // Buenas prácticas: Reutilizamos el objeto Polígono para no saturar la memoria (GC)
    private Polygon hitboxPoly;

    // Dirección del movimiento enviada por el Controlador (-1, 0, 1)
    public float dirX = 0;
    public float dirY = 0;

    public Personaje(float xInicial, float yInicial) {
        this.x = xInicial;
        this.y = yInicial;
        System.out.println(x);
        
        this.textura = new Texture("personaje/pjFrenteEstatico.png");
        this.textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        
        // Hitbox en la base/pies del personaje
        this.hitbox = new Rectangle(x, y, textura.getWidth(), textura.getHeight() / 2f);
        
        // Inicializamos el polígono una sola vez
        this.hitboxPoly = new Polygon(new float[]{
            0, 0,
            hitbox.width, 0,
            hitbox.width, hitbox.height,
            0, hitbox.height
        });
    }

    /**
     * Método invocado por el ControladorJugador para actualizar la intención de movimiento.
     */
    public void mover(float dirX, float dirY) {
        this.dirX = dirX;
        this.dirY = dirY;
    }

    public void actualizar(float delta, Array<Polygon> colisiones, float limiteAncho, float limiteAlto) {
        float xAnterior = x;
        float yAnterior = y;
        //nuevaX, nuevaY

        // --- Movimiento y Colisión Horizontal ---
        x += dirX * velocidad * delta;

        hitboxPoly.setPosition(x, y);
        for (Polygon colision : colisiones) {
            if (Intersector.overlapConvexPolygons(hitboxPoly, colision)) {
                x = xAnterior; // Cancela movimiento en X si choca
                break;
            }
        }

        // --- Movimiento y Colisión Vertical ---
        y += dirY * velocidad * delta;

        hitboxPoly.setPosition(x, y);
        for (Polygon colision : colisiones) {
            if (Intersector.overlapConvexPolygons(hitboxPoly, colision)) {
                y = yAnterior; // Cancela movimiento en Y si choca
                break;
            }
        }

        // Límites de los bordes del mapa
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

    // --- GETTERS (Útiles para la cámara, la grilla y las interacciones) ---
    public float getX() { return x; }
    public float getY() { return y; }
    public float getAncho() { return textura.getWidth(); }
    public float getAlto() { return textura.getHeight(); }
    
    // Punto de los pies del personaje (clave para saber qué celda de cultivo está mirando/pisando)
    public float getCentroX() { return x + (textura.getWidth() / 2f); }
    public float getPiesY() { return y + (hitbox.height / 2f); }
}