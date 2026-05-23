package com.mijuego;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mijuego.screens.MainMenuScreen;

/**
 * Clase principal del juego. Al extender de Game, coordina el ciclo de vida,
 * las transiciones entre pantallas y el renderizado de filtros globales como el brillo.
 */
public class MainGame extends Game {

    public SpriteBatch batch;
    private Viewport viewport;

    // Textura de 1x1 píxel blanco para renderizar el filtro de brillo de forma ultra-robusta
    private Texture filtroBrilloTexture;

    public final float VIRTUAL_WIDTH = 1280f;
    public final float VIRTUAL_HEIGHT = 720f;

    // --- ESTADO DE CONFIGURACIÓN GLOBAL ---
    private int brilloNivel = 10;  // 10 = Brillo Máximo (100%), 2 = Brillo Mínimo (20%)
    private int volumenNivel = 7;   // Nivel de volumen actual (70%)

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        // Generamos dinámicamente una textura blanca de 1x1 píxel en memoria de video
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        filtroBrilloTexture = new Texture(pixmap);
        pixmap.dispose(); // Liberamos de inmediato el pixmap en la CPU

        // Iniciamos el juego mostrando la pantalla del menú principal
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        // 1. Renderiza la pantalla activa (Menu, Config, Mesa de juego, etc.)
        super.render();

        // 2. CAPA DE BRILLO CENTRALIZADA MULTIPLATAFORMA
        // Usar SpriteBatch con una textura de 1x1 es infinitamente más robusto en LibGDX
        // que usar ShapeRenderer, ya que hereda y gestiona correctamente todos los estados
        // de mezcla (Blending) e iluminación sin verse afectado por el renderizado de Scene2D (Stage).
        if (brilloNivel < 10) {
            float factorOscuridad = (10 - brilloNivel) * 0.08f;

            // Sincronizamos las coordenadas del viewport del juego
            viewport.apply();
            batch.setProjectionMatrix(viewport.getCamera().combined);

            // Iniciamos el lote de dibujo, aplicando el filtro de color negro translúcido
            batch.begin();
            batch.setColor(0, 0, 0, factorOscuridad);
            batch.draw(filtroBrilloTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
            batch.end();

            // Restablecemos el color del lote a blanco para no afectar futuros dibujados
            batch.setColor(Color.WHITE);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
        if (filtroBrilloTexture != null) filtroBrilloTexture.dispose();
    }

    // --- GETTERS Y SETTERS GLOBALES ---
    public int getBrilloNivel() {
        return brilloNivel;
    }

    public void setBrilloNivel(int brilloNivel) {
        this.brilloNivel = brilloNivel;
    }

    public int getVolumenNivel() {
        return volumenNivel;
    }

    public void setVolumenNivel(int volumenNivel) {
        this.volumenNivel = volumenNivel;
    }
}