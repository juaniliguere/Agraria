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
<<<<<<< HEAD
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
=======
    private final float VELOCIDAD = 150f;
    private Rectangle hitbox;
    private Polygon hitboxPoly;

    public float dirX = 0;
    public float dirY = 0;
>>>>>>> f6ca868 (Actualizacion automatica)

    // --- SPRITES Y ANIMACIONES ---
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

    // 0: Abajo, 1: Derecha/Izquierda, 2: Arriba
    private int ultimaDireccion = 0; 
    private boolean mirandoIzquierda = false;

    public Personaje(float xInicial, float yInicial) {
        this.x = xInicial;
        this.y = yInicial;
<<<<<<< HEAD

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
=======
        
        // 1. Cargar la Spritesheet de 3x3
        this.spriteSheet = new Texture("personaje/pj_spritesheet.png");
        this.spriteSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // 2. Cortar la hoja en 3 filas y 3 columnas
        int anchoTotal = spriteSheet.getWidth();
        int altoTotal = spriteSheet.getHeight();
        
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, anchoTotal / 3, altoTotal / 3);

        this.anchoFrame = anchoTotal / 3f;
        this.altoFrame = altoTotal / 3f;

        // 3. Configurar Frames Estáticos (Columna 0 de cada fila)
        frameEstaticoAbajo = tmp[0][0];   // Fila 0, Columna 0
        frameEstaticoDerecha = tmp[1][0]; // Fila 1, Columna 0
        frameEstaticoArriba = tmp[2][0];  // Fila 2, Columna 0

        // 4. Configurar Animaciones de caminata (Paso1, Quieto, Paso2)
        float duracionPaso = 0.15f;
        animAbajo = new Animation<TextureRegion>(duracionPaso, tmp[0][1], tmp[0][0], tmp[0][2]);
        animDerecha = new Animation<TextureRegion>(duracionPaso, tmp[1][1], tmp[1][0], tmp[1][2]);
        animArriba = new Animation<TextureRegion>(duracionPaso, tmp[2][1], tmp[2][0], tmp[2][2]);

        // Hitbox en la base/pies del personaje
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
>>>>>>> f6ca868 (Actualizacion automatica)
    }

    public void actualizar(float delta, Array<Polygon> colisiones, float limiteAncho, float limiteAlto) {
        float xAnterior = x;
        float yAnterior = y;
<<<<<<< HEAD
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
=======

        // --- ACTUALIZAR DIRECCIÓN DE ANIMACIÓN ---
        if (dirX != 0 || dirY != 0) {
            stateTime += delta; // Avanza tiempo de la caminata

            if (dirX < 0) {
                ultimaDireccion = 1;
                mirandoIzquierda = true;
            } else if (dirX > 0) {
                ultimaDireccion = 1;
                mirandoIzquierda = false;
            } else if (dirY > 0) {
                ultimaDireccion = 2; // Arriba
            } else if (dirY < 0) {
                ultimaDireccion = 0; // Abajo
            }
        } else {
            stateTime = 0f; // Reiniciar animación al frenar
        }

        // --- LÓGICA DE MOVIMIENTO Y COLISIONES (Sin Cambios) ---
        x += dirX * VELOCIDAD * delta;
        hitboxPoly.setPosition(x, y);
        
>>>>>>> f6ca868 (Actualizacion automatica)
        for (Polygon colision : colisiones) {
            if (Intersector.overlapConvexPolygons(hitboxPoly, colision)) {
                x = xAnterior;
                break;
            }
        }

<<<<<<< HEAD
        // Colisión Y
=======
        y += dirY * VELOCIDAD * delta;
>>>>>>> f6ca868 (Actualizacion automatica)
        hitboxPoly.setPosition(x, y);
        
        for (Polygon colision : colisiones) {
            if (Intersector.overlapConvexPolygons(hitboxPoly, colision)) {
                y = yAnterior;
                break;
            }
        }

<<<<<<< HEAD
        // Límites del mapa
        x = MathUtils.clamp(x, 0, limiteAncho - texturaActual.getWidth());
        y = MathUtils.clamp(y, 0, limiteAlto - texturaActual.getHeight());
=======
        // Límites de los bordes del mapa usando el ancho/alto del frame recortado
        x = MathUtils.clamp(x, 0, limiteAncho - anchoFrame);
        y = MathUtils.clamp(y, 0, limiteAlto - altoFrame);
>>>>>>> f6ca868 (Actualizacion automatica)

        hitbox.setPosition(x, y);
    }

    public void renderizar(SpriteBatch batch) {
<<<<<<< HEAD
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
=======
        TextureRegion currentFrame = null;

        boolean enMovimiento = (dirX != 0 || dirY != 0);

        // Selección del frame según la dirección actual
        switch (ultimaDireccion) {
            case 0: // ABAJO
                currentFrame = enMovimiento ? animAbajo.getKeyFrame(stateTime, true) : frameEstaticoAbajo;
                break;
            case 1: // DERECHA / IZQUIERDA
                currentFrame = enMovimiento ? animDerecha.getKeyFrame(stateTime, true) : frameEstaticoDerecha;
                break;
            case 2: // ARRIBA
                currentFrame = enMovimiento ? animArriba.getKeyFrame(stateTime, true) : frameEstaticoArriba;
                break;
        }

        // DIBUJAR CON FLIP EN X SI MIRA A LA IZQUIERDA
        if (mirandoIzquierda && ultimaDireccion == 1) {
            // Se suma anchoFrame a la posición X y se pasa -anchoFrame para invertir horizontalmente
            batch.draw(currentFrame, x + anchoFrame, y, -anchoFrame, altoFrame);
        } else {
            batch.draw(currentFrame, x, y, anchoFrame, altoFrame);
        }
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
>>>>>>> f6ca868 (Actualizacion automatica)
}