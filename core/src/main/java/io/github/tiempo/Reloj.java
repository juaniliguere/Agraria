package io.github.tiempo;

public class Reloj {

    private static final float segsPorMinJuego = 0.1f; //SEGS_POR_MIN_JUEGO
	
    private int minutos;
    private int horas;
    private int dias;

    private float acumuladorSegundos;

    public Reloj() {
        this.minutos = 0;
        this.horas = 6;
        this.dias = 1;

        this.acumuladorSegundos = 0;
    }

    public int actualizar(float delta) {
        acumuladorSegundos += delta;

        int minutosPasados = 0;

        while (acumuladorSegundos >= segsPorMinJuego) {
            acumuladorSegundos -= segsPorMinJuego;

            avanzarUnMinuto();
            minutosPasados++;
        }
//        System.out.println("MP: " + minutosPasados + " / By: " + (horas*60+minutos));
        return minutosPasados;
    }

    private void avanzarUnMinuto() {
        minutos++;

        if (minutos >= 60) {
            minutos = 0;
            horas++;

            if (horas >= 24) {
                horas = 0;
                dias++;
            }
        }
    }

    public int dormir(int horaDespertar) {
    	
    	int minutosPasados = 0;
    	int diaSiguiente = dias + 1;
    	
    	while(dias < diaSiguiente || horas < horaDespertar) {
    		
    		avanzarUnMinuto();
    		minutosPasados++;
    		
    	}
    	
    	acumuladorSegundos = 0;
    	
    	return minutosPasados;
    	
    }
    
    public int getMinutos() {
        return minutos;
    }

    public int getHoras() {
        return horas;
    }

    public int getDias() {
        return dias;
    }
    
    public String getHoraFormateada() {
        return String.format("%02d:%02d", horas, minutos);
    }
}