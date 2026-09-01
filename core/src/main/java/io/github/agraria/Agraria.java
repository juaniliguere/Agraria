
package io.github.agraria;

import com.badlogic.gdx.Game;
import io.github.agraria.pantallas.PantallaGranja;

public class Agraria extends Game {

    @Override
    public void create() {
        this.setScreen(new PantallaGranja());
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
