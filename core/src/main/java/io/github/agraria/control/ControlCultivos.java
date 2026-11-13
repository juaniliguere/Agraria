package io.github.agraria.control;

import io.github.agraria.cultivos.Cultivo;
import io.github.agraria.cultivos.GestorCultivos;
import io.github.agraria.cultivos.Parcela;
import io.github.agraria.cultivos.TipoCultivo;
import io.github.agraria.elementos.Item;
import io.github.agraria.inventario.Inventario;
import io.github.agraria.progreso.SistemaNiveles;
import io.github.agraria.sonidos.GestorAudio;

public class ControlCultivos {

    private GestorCultivos gestorCultivos;
    private Inventario inventario;
    private GestorAudio gestorAudio;
    private SistemaNiveles sistemaNiveles;

    public ControlCultivos(
            GestorCultivos gestorCultivos,
            Inventario inventario,
            GestorAudio gestorAudio,
            SistemaNiveles sistemaNiveles) {

        this.gestorCultivos = gestorCultivos;
        this.inventario = inventario;
        this.gestorAudio = gestorAudio;
        this.sistemaNiveles = sistemaNiveles;
    }

    public void procesarCultivar(float mundoX, float mundoY) {

        Parcela parcela = gestorCultivos.getParcelaEnPx(mundoX, mundoY);

        if (parcela == null) {
            return;
        }

        if (parcela.tieneCultivo() || !parcela.isEsPlantable()) {
            return;
        }

        TipoCultivo cultivoSeleccionado = inventario.getCultivoSeleccionado();

        if (cultivoSeleccionado == null) {
            System.out.println("-> No tenés seleccionada ninguna semilla válida en la mano.");
            return;
        }

        boolean exito = gestorCultivos.plantarEn(mundoX, mundoY, cultivoSeleccionado);

        if (exito) {
            gestorAudio.reproducirPlantar();
            inventario.consumirSeleccionado(1);
            sistemaNiveles.ganarXp(cultivoSeleccionado.getXpPlantar());
        }
    }

    public void procesarCosechar(float mundoX, float mundoY) {

        Parcela parcela = gestorCultivos.getParcelaEnPx(mundoX, mundoY);

        if (parcela == null) {
            return;
        }

        Cultivo cosechado = parcela.cosechar();

        if (cosechado == null) {
            return;
        }

        TipoCultivo tipo = cosechado.getTipo();

        Item itemCosecha;

        if (cosechado.isPodrido()) {
            itemCosecha = tipo.crearItemPodrido();
        } else {
            itemCosecha = tipo.crearItemCosecha();
            sistemaNiveles.ganarXp(tipo.getXpRecompensa());
        }

        inventario.agregarItem(itemCosecha, 1);
    }
}