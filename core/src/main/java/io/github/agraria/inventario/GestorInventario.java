package io.github.agraria.inventario;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.agraria.elementos.Item;

public class GestorInventario {

    private Inventario inventario;
    private ObjectMap<String, Texture> texturasItems;
    private ShapeRenderer shapeRenderer;

    // Configuración visual de la barra
    private float tamanoCasilla = 40f;
    private float separacion = 5f;

    public GestorInventario(Inventario inventario) {
        this.inventario = inventario;
        this.texturasItems = new ObjectMap<>();
        this.shapeRenderer = new ShapeRenderer();
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
        int capacidad = 10; // O inventario.getCapacidad()
        float anchoTotal = (capacidad * tamanoCasilla) + ((capacidad - 1) * separacion);
        
        // Centramos la barra abajo en la pantalla
        float startX = (anchoPantalla - anchoTotal) / 2f;
        float startY = 15f; 

        // -------------------------------------------------------------
        // PASO A: Dibujar los marcos/fondos de las casillas con ShapeRenderer
        // -------------------------------------------------------------
        batch.end(); // Pausamos el batch para usar ShapeRenderer

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < capacidad; i++) {
            float x = startX + i * (tamanoCasilla + separacion);

            // Fondo de casilla
            shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.8f); // Gris oscuro
            shapeRenderer.rect(x, startY, tamanoCasilla, tamanoCasilla);

            // Borde resaltado si es la casilla seleccionada
            if (i == inventario.getCasillaSeleccionada()) {
                shapeRenderer.setColor(1f, 0.84f, 0f, 1f); // Dorado/Amarillo
                // Dibujamos un marco un poco más grande alrededor
                shapeRenderer.rect(x - 2, startY - 2, tamanoCasilla + 4, tamanoCasilla + 4);
                shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
                shapeRenderer.rect(x, startY, tamanoCasilla, tamanoCasilla);
            }
        }

        shapeRenderer.end();

        // -------------------------------------------------------------
        // PASO B: Dibujar los Iconos de los Ítems y las Cantidades
        // -------------------------------------------------------------
        batch.begin(); // Reanudamos el batch

        for (int i = 0; i < capacidad; i++) {
            float x = startX + i * (tamanoCasilla + separacion);

            // Obtenemos el item en la casilla directamente desde el Inventario
            // (Asegurate de tener un getter `getCasilla(i)` en tu clase Inventario)
            CasillaInventario casilla = inventario.getCasilla(i);

            if (casilla != null && !casilla.estaVacia()) {
                Item item = casilla.getItem();
                Texture tex = getTexturaItem(item);

                if (tex != null) {
                    // Dibujar el icono centrado en la casilla
                    batch.draw(tex, x + 4, startY + 4, tamanoCasilla - 8, tamanoCasilla - 8);
                }

                // Dibujar la cantidad si es apilable (> 1)
                if (casilla.getCantidad() > 1) {
                    font.draw(batch, String.valueOf(casilla.getCantidad()), x + tamanoCasilla - 14, startY + 14);
                }
            }
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
        for (Texture t : texturasItems.values()) {
            t.dispose();
        }
    }
}