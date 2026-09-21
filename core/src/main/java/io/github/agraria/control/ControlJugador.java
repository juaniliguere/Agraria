package io.github.agraria.control;

	import com.badlogic.gdx.Input.Keys;
	import com.badlogic.gdx.InputAdapter;

import io.github.agraria.pantallas.PantallaGranja;
import io.github.agraria.personajes.Personaje;

	public class ControlJugador extends InputAdapter {
		
		//
		/*Esta clase lo que hace es escuchar el teclado y generar el movimiento del personaje*/
		//
	    private Personaje jugador;
	    private PantallaGranja pantalla;

	    // Guardamos qué teclas están apretadas en este momento
	    private boolean arriba, abajo, izquierda, derecha;

	    public ControlJugador(Personaje jugador, PantallaGranja pantalla) {
	        this.jugador = jugador;
	        this.pantalla = pantalla;
	        
	    }

	    // LibGDX ejecuta esto SOLO cuando APRETÁS una tecla
	    @Override
	    public boolean keyDown(int keycode) {
	        if (keycode == Keys.W || keycode == Keys.UP)    arriba = true;
	        if (keycode == Keys.S || keycode == Keys.DOWN)  abajo = true;
	        if (keycode == Keys.A || keycode == Keys.LEFT)  izquierda = true;
	        if (keycode == Keys.D || keycode == Keys.RIGHT) derecha = true;

	        actualizarMovimientoJugador();
	        return true;
	    }

	    // LibGDX ejecuta esto SOLO cuando SUELTAS una tecla
	    @Override
	    public boolean keyUp(int keycode) {
	        if (keycode == Keys.W || keycode == Keys.UP)    arriba = false;
	        if (keycode == Keys.S || keycode == Keys.DOWN)  abajo = false;
	        if (keycode == Keys.A || keycode == Keys.LEFT)  izquierda = false;
	        if (keycode == Keys.D || keycode == Keys.RIGHT) derecha = false;

	        actualizarMovimientoJugador();
	        return true;
	    }

	    // Calcula la dirección (-1, 0, 1) y le manda la orden al personaje
	    private void actualizarMovimientoJugador() {
	        float dirX = 0;
	        float dirY = 0;

	        if (izquierda) dirX -= 1;
	        if (derecha)   dirX += 1;
	        if (arriba)    dirY += 1;
	        if (abajo)     dirY -= 1;

	        jugador.mover(dirX, dirY);
	    }
	    
	    @Override
	    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	        if (button == com.badlogic.gdx.Input.Buttons.LEFT) {
	            pantalla.hacerClicEn(screenX, screenY);
	            return true;
	        }
	        return false;
	    }
	    
	}
	

	
