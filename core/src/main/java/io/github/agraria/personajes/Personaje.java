package io.github.agraria.personajes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Personaje {
    private float x, y;

    private final float VELOCIDAD = 150f;
    private Rectangle hitbox;
    private Polygon hitboxPoly;

    public float dirX = 0;
    public float dirY = 0;


    private Texture spriteSheet;
    
    private Animation<TextureRegion> animAbajo;
    private Animation<TextureRegion> animDerecha;
    private Animation<TextureRegion> animArriba;
    
    private TextureRegion frameEstaticoAbajo;
    private TextureRegion frameEstaticoDerecha;
    private TextureRegion frameEstaticoArriba;

    private float stateTime = 0f;
    private float anchoFrame;
    private float altoFrame;

    private int ultimaDireccion = 0; 
    private boolean mirandoIzquierda = false;

    public Personaje(float xInicial, float yInicial) {
        this.x = xInicial;
        this.y = yInicial;
        
        this.spriteSheet = new Texture("personaje/pj_spritesheet.png");
        this.spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        int anchoTotal = spriteSheet.getWidth();
        int altoTotal = spriteSheet.getHeight();
        
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, anchoTotal / 3, altoTotal / 3);

        this.anchoFrame = anchoTotal / 3f;
        this.altoFrame = altoTotal / 3f;


        frameEstaticoAbajo = tmp[0][0];   
        frameEstaticoDerecha = tmp[1][0]; 
        frameEstaticoArriba = tmp[2][0];  

        float duracionPaso = 0.15f;
        animAbajo = new Animation<TextureRegion>(duracionPaso, tmp[0][1], tmp[0][0], tmp[0][2]);
        animDerecha = new Animation<TextureRegion>(duracionPaso, tmp[1][1], tmp[1][0], tmp[1][2]);
        animArriba = new Animation<TextureRegion>(duracionPaso, tmp[2][1], tmp[2][0], tmp[2][2]);

        this.hitbox = new Rectangle(x, y, anchoFrame, altoFrame / 2f);
        
        this.hitboxPoly = new Polygon(new float[]{
            0, 0,
            hitbox.width, 0,
            hitbox.width, hitbox.height,
            0, hitbox.height
        });
    }

    public void mover(float dirX, float dirY) {
        this.dirX = dirX;
        this.dirY = dirY;
    }

    public void actualizar(float delta, Array<Polygon> colisiones, float limiteAncho, float limiteAlto) {
    	
        float xAnterior = x;
        float yAnterior = y;

        if (dirX != 0 || dirY != 0) {
            stateTime += delta; 

            if (dirX < 0) {
                ultimaDireccion = 1;
                mirandoIzquierda = true;
            } else if (dirX > 0) {
                ultimaDireccion = 1;
                mirandoIzquierda = false;
            } else if (dirY > 0) {
                ultimaDireccion = 2; 
            } else if (dirY < 0) {
                ultimaDireccion = 0; 
            }
        } else {
            stateTime = 0f; 
        }


        float factorVelocidad = (dirX != 0 && dirY != 0) ? 0.7071f : 1f;

        x += dirX * VELOCIDAD * factorVelocidad * delta;
        hitboxPoly.setPosition(x, y);
        
        for (Polygon colision : colisiones) {
            if (Intersector.overlapConvexPolygons(hitboxPoly, colision)) {
                x = xAnterior;
                break;
            }
        }

        y += dirY * VELOCIDAD * factorVelocidad * delta;

        hitboxPoly.setPosition(x, y);
        
        for (Polygon colision : colisiones) {
            if (Intersector.overlapConvexPolygons(hitboxPoly, colision)) {
                y = yAnterior;
                break;
            }
        }

        float maxX = Math.max(0, limiteAncho - anchoFrame);
        float maxY = Math.max(0, limiteAlto - altoFrame);

        x = MathUtils.clamp(x, 0, maxX);
        y = MathUtils.clamp(y, 0, maxY);

        hitbox.setPosition(x, y);
    }

    public void renderizar(SpriteBatch batch) {
    	
        TextureRegion currentFrame = null;

        boolean enMovimiento = (dirX != 0 || dirY != 0);


        switch (ultimaDireccion) {
            case 0: 
                currentFrame = enMovimiento ? animAbajo.getKeyFrame(stateTime, true) : frameEstaticoAbajo;
                break;
            case 1: 
                currentFrame = enMovimiento ? animDerecha.getKeyFrame(stateTime, true) : frameEstaticoDerecha;
                break;
            case 2:
                currentFrame = enMovimiento ? animArriba.getKeyFrame(stateTime, true) : frameEstaticoArriba;
                break;
        }

        if (mirandoIzquierda && ultimaDireccion == 1) {
            batch.draw(currentFrame, x + anchoFrame, y, -anchoFrame, altoFrame);
        } else {
            batch.draw(currentFrame, x, y, anchoFrame, altoFrame);
        }
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;

        hitbox.setPosition(x, y);
        hitboxPoly.setPosition(x, y);


        dirX = 0;
        dirY = 0;
    }
    
    public boolean chocaEn(float px, float py, Array<Polygon> colisiones) {

        hitboxPoly.setPosition(px, py);

        boolean choca = false;

        for (Polygon colision : colisiones) {
            if (Intersector.overlapConvexPolygons(hitboxPoly, colision)) {
                choca = true;
                break;
            }
        }

        hitboxPoly.setPosition(x, y);

        return choca;
    }
    
    public void liberarRecursos() {
        spriteSheet.dispose();
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getAncho() { return anchoFrame; }
    public float getAlto() { return altoFrame; }
    
    public float getCentroX() { return x + (anchoFrame / 2f); }
    public float getPiesY() { return y + (hitbox.height / 2f); }
}