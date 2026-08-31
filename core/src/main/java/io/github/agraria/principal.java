
package io.github.agraria;

import com.badlogic.gdx.Game;
import io.github.agraria.pantallas.pantallaGranja;

public class principal extends Game {

    @Override
    public void create() {
        this.setScreen(new pantallaGranja());
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
