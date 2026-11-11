
package io.github.agraria.sonidos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class GestorAudio {

    private static Music musicaFondo;

    private static float volumenMusica = 0.4f;
    private static float volumenSonidos = 1.0f;
    
    private Sound sonidoPlantar;
    private Sound apretarBoton;
    private Sound abrirTienda;
    
    public GestorAudio() {


        sonidoPlantar = Gdx.audio.newSound(
            Gdx.files.internal("sonidos/plantar.wav")
        );

        apretarBoton = Gdx.audio.newSound(
            Gdx.files.internal("sonidos/apretarBoton.wav")
        );

        abrirTienda = Gdx.audio.newSound(Gdx.files.internal("sonidos/abrirTienda.wav"));
        
        reproducirMusicaFondo();
    }

    public void reproducirMusicaFondo() {
        if (musicaFondo == null) {
            musicaFondo = Gdx.audio.newMusic(
                Gdx.files.internal("sonidos/musicaFondo.wav")
            );

            musicaFondo.setLooping(true);
            musicaFondo.setVolume(volumenMusica);
        }

        if (!musicaFondo.isPlaying()) {
            musicaFondo.play();
        }
    }
    
 // =========================
 // CONFIGURACIÓN DE VOLUMEN
 // =========================

 public static void setVolumenMusica(float volumen) {
     volumenMusica = Math.max(0f, Math.min(1f, volumen));

     if (musicaFondo != null) {
         musicaFondo.setVolume(volumenMusica);
     }
 }

 public static float getVolumenMusica() {
     return volumenMusica;
 }

 public static void setVolumenSonidos(float volumen) {
     volumenSonidos = Math.max(0f, Math.min(1f, volumen));
 }

 public static float getVolumenSonidos() {
     return volumenSonidos;
 }
    

    public static void apagarMusicaGlobal() {
        if (musicaFondo != null) {
            musicaFondo.stop();
            musicaFondo.dispose();
            musicaFondo = null;
        }
    }

    
    
    public void reproducirPlantar() {
        if (sonidoPlantar != null) {
            sonidoPlantar.play(volumenSonidos);
        }
    }

    public void reproducirApretarBoton() {
        if (apretarBoton != null) {
            apretarBoton.play(volumenSonidos);
        }
    }

    public void reproducirAbrirTienda() {
    	
    	if(abrirTienda != null) {
    		
    		abrirTienda.play(volumenSonidos);
    		
    	}
    	
    }
    
    // =========================
    // LIBERAR RECURSOS
    // =========================

    public void dispose() {

        if (sonidoPlantar != null) {
            sonidoPlantar.dispose();
            sonidoPlantar = null;
        }

        if (apretarBoton != null) {
            apretarBoton.dispose();
            apretarBoton = null;
        }
    }

    public static void disposeMusicas() {

        if (musicaFondo != null) {
            musicaFondo.dispose();
            musicaFondo = null;
        }
    }
}
