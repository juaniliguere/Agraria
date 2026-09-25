
package io.github.agraria;

import com.badlogic.gdx.Game;
import io.github.agraria.pantallas.PantallaGranja;
import io.github.agraria.pantallas.PantallaMenu;
import io.github.agraria.cultivos.TipoCultivo;

public class Agraria extends Game {
	

    @Override
    public void create() {
<<<<<<< HEAD
    	this.setScreen(new PantallaMenu());
=======
        this.setScreen(new PantallaMenu());
>>>>>>> f6ca868 (Actualizacion automatica)
    }
    

    @Override
    public void render() {
        super.render(); // Redirige el loop a PantallaGranja.render(delta)
    }

    @Override
    public void dispose() {
        super.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
