package io.github.agraria.control;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.agraria.economia.Billetera;
import io.github.agraria.inventario.Inventario;
import io.github.agraria.progreso.SistemaNiveles;
import io.github.agraria.sonidos.GestorAudio;
import io.github.agraria.tienda.VentanaTienda;
import io.github.agraria.ui.GestorHud;

public class ControlTienda {

    private Stage stage;
    private Billetera billetera;
    private Inventario inventario;
    private Skin skin;

    private VentanaTienda ventana;
    private ControlJugador controlJugador;
	private GestorAudio gestorAudio;
	private GestorHud gestorHud;
	private SistemaNiveles sistemaNiveles;
	
    public ControlTienda(Stage stage, Billetera billetera,
                         Inventario inventario, Skin skin, ControlJugador controlJugador,
                         GestorHud gestorHud, SistemaNiveles sistemaNiveles) {

        this.stage = stage;
        this.billetera = billetera;
        this.inventario = inventario;
        this.skin = skin;
        this.controlJugador = controlJugador;
        this.gestorAudio = new GestorAudio();
        this.gestorHud = gestorHud;
        this.sistemaNiveles = sistemaNiveles;
        
    }

    public void abrir() {

        if (ventana != null && ventana.estaAbierta()) {
            return;
            
        }

        ventana = new VentanaTienda(stage, billetera, inventario, skin, gestorAudio, gestorHud, sistemaNiveles);
        
        gestorAudio.reproducirAbrirTienda();
        
        
        if (controlJugador != null) {
            controlJugador.setMovimientoBloqueado(true);
        }
    }
    
    public void cerrar() {
        if (ventana != null) {
            ventana.cerrarVentana(); 
            gestorAudio.reproducirAbrirTienda();
            ventana = null;          
        }

        
        if (controlJugador != null) {
            controlJugador.setMovimientoBloqueado(false);
        }
        
    }
    
    public boolean estaAbierta() {
    	
        return ventana != null && ventana.estaAbierta();
    }
    
}