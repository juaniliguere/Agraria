package io.github.agraria.pantallas;

import com.badlogic.gdx.math.Rectangle;

import io.github.agraria.personajes.Personaje;

public class PantallaTienda {

    private GestorMapa mapa;

    private boolean jugadorDentro = false;

    private float posicionAnteriorX;
    private float posicionAnteriorY;

    private static final float POSICION_INICIAL_X = 80;
    private static final float POSICION_INICIAL_Y = 30;

    public PantallaTienda() {
        mapa = new GestorMapa(
            "pantallas/tienda/interiorTienda.tmx"
        );
    }


    public void entrar(Personaje jugador) {

        if (jugadorDentro) {
            return;
        }

        posicionAnteriorX = jugador.getX();
        posicionAnteriorY = jugador.getY();

        jugador.setPosicion(
            POSICION_INICIAL_X,
            POSICION_INICIAL_Y
        );

        jugadorDentro = true;
    }

    public void salir(Personaje jugador) {

        if (!jugadorDentro) {
            return;
        }

        jugador.setPosicion(
            posicionAnteriorX,
            posicionAnteriorY - 15
        );

        jugadorDentro = false;
    }



    public boolean estaCercaDeLaTienda(Personaje jugador, GestorMapa mapaGranja) {

        Rectangle zonaTienda = mapaGranja.getZonaTienda();

        if (zonaTienda == null) {
            return false;
        }

        return zonaTienda.contains(
            jugador.getCentroX(),
            jugador.getPiesY()
        );
    }

    public boolean estaCercaDeComprar(Personaje jugador) {
    	
        Rectangle zonaComprar = mapa.getZonaComprar();
        
        if (zonaComprar == null) {
        	return false;
        } 
        
        return zonaComprar.contains(jugador.getCentroX(), jugador.getPiesY());
    }
    
    public boolean estaCercaDeSalir(Personaje jugador) {

        Rectangle zonaSalir = mapa.getZonaSalir();

        if (zonaSalir == null) {
            return false;
        }

        return zonaSalir.contains(jugador.getCentroX(), jugador.getPiesY());
    }

    public GestorMapa getMapa() {
        return mapa;
    }

    public boolean estaDentro() {
        return jugadorDentro;
    }


    public void dispose() {
        mapa.dispose();
    }
	
}
