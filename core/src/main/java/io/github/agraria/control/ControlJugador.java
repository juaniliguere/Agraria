package io.github.agraria.control;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

import io.github.agraria.pantallas.PantallaGranja;
import io.github.agraria.personajes.Personaje;

	public class ControlJugador extends InputAdapter {
		
		/*Esta clase lo que hace es escuchar el teclado y generar el movimiento del personaje*/
	    private Personaje jugador;
	    private PantallaGranja pantalla;

	    public ControlJugador(Personaje jugador, PantallaGranja pantalla) {
	        this.jugador = jugador;
	        this.pantalla = pantalla;	        
	    }

	    // LibGDX ejecuta esto SOLO cuando APRETÁS una tecla
	    @Override
	    public boolean keyDown(int keycode) { 
	    	
	        if (keycode == Keys.W || keycode == Keys.UP)    jugador.dirY = 1;
	        if (keycode == Keys.S || keycode == Keys.DOWN)  jugador.dirY =-1;
	        if (keycode == Keys.A || keycode == Keys.LEFT)  jugador.dirX = -1;
	        if (keycode == Keys.D || keycode == Keys.RIGHT) jugador.dirX = 1;

	        return true;
	    }

	    // LibGDX ejecuta esto SOLO cuando SUELTAS una tecla
	    @Override
	    public boolean keyUp(int keycode) { 
	    	
	        if (keycode == Keys.W || keycode == Keys.UP)    jugador.dirY = 0;
	        if (keycode == Keys.S || keycode == Keys.DOWN)  jugador.dirY = 0;
	        if (keycode == Keys.A || keycode == Keys.LEFT)  jugador.dirX = 0;
	        if (keycode == Keys.D || keycode == Keys.RIGHT) jugador.dirX = 0;
	        if (keycode == Keys.F) pantalla.procesarCosechar();
	        if (keycode == Keys.Z) pantalla.procesarDormir();
	        
	        return true;
	    }
	    
	    @Override
	    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	        if (button == com.badlogic.gdx.Input.Buttons.LEFT) {
	            pantalla.procesarCultivar(screenX, screenY);
	            return true;
	        }
	        return false;
	    }
	    	    
	}
	

	
