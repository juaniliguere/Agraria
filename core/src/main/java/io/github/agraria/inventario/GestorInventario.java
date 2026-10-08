package io.github.agraria.inventario;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.agraria.elementos.Item;

public class GestorInventario {

    private Inventario inventario;
    private ObjectMap<String, Texture> texturasItems;
    private Texture texturaSlot;
    private Texture texturaSlotSeleccionado;

    // Configuración visual de la barra
    private float tamanoCasilla = 56f;
    private float separacion = 1.5f;

    public GestorInventario(Inventario inventario) {
        this.inventario = inventario;
        this.texturasItems = new ObjectMap<>();
        this.texturaSlot = new Texture("ui/casilla.png");
        this.texturaSlotSeleccionado = new Texture("ui/casillaSeleccionada.png");
    }

    // Obtiene o carga la textura de un ítem para no recargar la imagen en cada frame
    private Texture getTexturaItem(Item item) {
        if (item == null || item.getRutaTextura() == null) return null;

        String ruta = item.getRutaTextura();
        if (!texturasItems.containsKey(ruta)) {
            texturasItems.put(ruta, new Texture(ruta));
        }
        return texturasItems.get(ruta);
    }

   
    public void renderizar(SpriteBatch batch, BitmapFont font, float anchoPantalla, float altoPantalla) {

        int capacidad = 10;
        float anchoTotal = (capacidad * tamanoCasilla) + ((capacidad - 1) * separacion);
        float startX = (anchoPantalla - anchoTotal) / 2f;
        float startY = 15f;

        for (int i = 0; i < capacidad; i++) {

            float x = startX + i * (tamanoCasilla + separacion);

            // 1. DIBUJAR CASILLA (Solo una vez: seleccionada o normal)
            if (i == inventario.getCasillaSeleccionada()) {
                batch.draw(texturaSlotSeleccionado, x, startY, tamanoCasilla, tamanoCasilla);
            } else {
                batch.draw(texturaSlot, x, startY, tamanoCasilla, tamanoCasilla);
            }

            // 2. DIBUJAR ÍTEM Y CANTIDAD
            CasillaInventario casilla = inventario.getCasilla(i);

            if (casilla != null && !casilla.estaVacia()) {

                Item item = casilla.getItem();
                Texture tex = getTexturaItem(item);

                if (tex != null) {
                    // Mantené los píxeles nítidos en los ítems también
                    tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                    
                    batch.draw(
                        tex,
                        x + 8,
                        startY + 8,
                        tamanoCasilla - 16,
                        tamanoCasilla - 16
                    );
                }

                // Cantidad
                if (casilla.getCantidad() > 1) {
                    font.draw(
                        batch,
                        String.valueOf(casilla.getCantidad()),
                        x + tamanoCasilla - 20,
                        startY + 20
                    );
                }
            }
        }
    }
    

    public void dispose() {

        texturaSlot.dispose();

        for (Texture t : texturasItems.values()) {
            t.dispose();
        }
    }
    
}