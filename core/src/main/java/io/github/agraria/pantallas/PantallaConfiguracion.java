package io.github.agraria.pantallas;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.agraria.Agraria;

public class PantallaConfiguracion extends ScreenAdapter {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Texture texturaVolver;
    private ImageButton btnVolver;
    private Stage stage;
    
    private static final float MAP_WIDTH = 480; 
    private static final float MAP_HEIGHT = 320;

    @Override
    public void show() {
        map = new TmxMapLoader().load("pantallas/menu/pantallaMenu.tmx");

        camera = new OrthographicCamera();
        viewport = new StretchViewport(MAP_WIDTH, MAP_HEIGHT, camera);
        viewport.apply();

        // --- ACÁ ESTÁ EL SECRETO: Centrar la cámara en la mitad del mapa ---
        camera.position.set(MAP_WIDTH / 2f, MAP_HEIGHT / 2f, 0);
        camera.update();

        renderer = new OrthogonalTiledMapRenderer(map);
        
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        
        texturaVolver = new Texture(
        	    Gdx.files.internal("pantallas/menu/back_dark_12x12.png")
        	);

        	texturaVolver.setFilter(
        	    Texture.TextureFilter.Nearest,
        	    Texture.TextureFilter.Nearest
        	);
        	
        	TextureRegionDrawable drawableVolver = new TextureRegionDrawable(
        		    new TextureRegion(texturaVolver)
        		);

        		btnVolver = new ImageButton(drawableVolver);
        		
        		btnVolver.setSize(24, 24);

        		btnVolver.setPosition(
        		    15,
        		    MAP_HEIGHT - 15 - btnVolver.getHeight()
        		);

        		stage.addActor(btnVolver);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PantallaMenu((Agraria) Gdx.app.getApplicationListener()));
        }

        // Asegurate de actualizar la cámara antes de pasarla al renderer
        camera.update();
        renderer.setView(camera);
        renderer.render();
        
        stage.act(delta);
        stage.draw();
    }



    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (map != null) map.dispose();
        if (renderer != null) renderer.dispose();
    }
}