
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
    private Sound comprar;
    private Sound vender;
    private static Sound lluvia;
    private static long idLluvia = -1;
    
    public GestorAudio() {


        sonidoPlantar = Gdx.audio.newSound(
            Gdx.files.internal("sonidos/plantar.wav")
        );

        apretarBoton = Gdx.audio.newSound(
            Gdx.files.internal("sonidos/apretarBoton.wav")
        );

        abrirTienda = Gdx.audio.newSound(Gdx.files.internal("sonidos/abrirTienda.wav"));
        
        comprar = Gdx.audio.newSound(Gdx.files.internal("sonidos/comprar.wav"));
        
        vender = Gdx.audio.newSound(Gdx.files.internal("sonidos/vender.wav"));
        
        lluvia = Gdx.audio.newSound(Gdx.files.internal("sonidos/lluvia.wav"));
        
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
	    if (lluvia != null && idLluvia != -1) {
	        lluvia.setVolume(idLluvia, volumenSonidos);
	    }
	    
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
    
    
    
    
    public void reproducirComprar() {
    	
    	if(comprar != null) {
    		comprar.play(volumenSonidos);
    	}
    }
    
    
    public void reproducirVender() {
    	
    	if(vender != null) {
    		vender.play(volumenSonidos);
    	}
    }
    
    
    
    public void reproducirLluvia() {
    	
        if (lluvia != null && idLluvia == -1) {
            idLluvia = lluvia.loop(volumenSonidos);
        }
    }

    public void detenerLluvia() {
    	
        if (lluvia != null && idLluvia != -1) {
            lluvia.stop(idLluvia);
            idLluvia = -1;
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
        
        if (comprar != null) {
        	comprar.dispose();
        	comprar = null;
        }
        
        if (vender != null) {
        	vender.dispose();
        	vender = null;
        }
        
        if (lluvia != null) {
            lluvia.stop();
            lluvia.dispose();
            lluvia = null;
            idLluvia = -1;
        }
        
    }

    public static void disposeMusicas() {

        if (musicaFondo != null) {
            musicaFondo.dispose();
            musicaFondo = null;
        }
    }
}
