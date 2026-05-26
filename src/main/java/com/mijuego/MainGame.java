package com.mijuego;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport; // Cambiado para soportar pantalla completa
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

        // SOLUCIÓN CRÍTICA: Cambiamos FitViewport por StretchViewport a nivel global.
        // Esto elimina las restricciones de aspecto y permite que el fondo de la mesa
        // se estire libremente hasta ocupar el 100% de la ventana física de renderizado.
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        // Generamos dinámicamente una textura blanca de 1x1 píxel en memoria de video
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        filtroBrilloTexture = new Texture(pixmap);
        pixmap.dispose(); // Liberamos de inmediato el pixmap en la CPU

        // Sincronizamos la cámara inicialmente
        viewport.apply();

        // Iniciamos el juego mostrando la pantalla del menú principal
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        // 1. Renderiza la pantalla activa (Menu, Config, Mesa de juego, etc.)
        super.render();

        // 2. CAPA DE BRILLO CENTRALIZADA MULTIPLATAFORMA
        // Al usar StretchViewport, el filtro de brillo se estirará de forma idéntica
        // al fondo de tu mesa de juego, cubriendo la pantalla completa de manera uniforme.
        if (brilloNivel < 10) {
            float factorOscuridad = (10 - brilloNivel) * 0.08f;

            // Sincronizamos las coordenadas del viewport del juego y aplicamos la cámara antes del dibujado
            viewport.apply();
            batch.setProjectionMatrix(viewport.getCamera().combined);

            // Habilitamos mezcla de transparencias OpenGL para que actúe de manera correcta
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            // Iniciamos el lote de dibujo, aplicando el filtro de color negro translúcido
            batch.begin();
            batch.setColor(0f, 0f, 0f, factorOscuridad);
            batch.draw(filtroBrilloTexture, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
            batch.end();

            // Restablecemos el color del lote a blanco para no afectar futuros dibujados de otras texturas
            batch.setColor(Color.WHITE);
        }
    }

    @Override
    public void resize(int width, int height) {
        // Actualiza el tamaño físico de la ventana virtual y resetea la proyección de la cámara
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