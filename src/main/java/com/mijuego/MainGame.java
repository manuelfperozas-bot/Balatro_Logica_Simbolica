package com.mijuego;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainGame extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture spritePersonaje;
    private Viewport viewport;
    // Constantes de diseño retro nativo (16:9)
    private final float VIRTUAL_WIDTH = 320f;
    private final float VIRTUAL_HEIGHT = 180f;

    @Override
    public void create() {

        batch = new SpriteBatch();
        // Inicialización de la ventana virtual táctica
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        // Ingesta de texturas desde el directorio de recursos de Maven
        spritePersonaje = new Texture("personaje.png");
        // FILTRO CRUCIAL: Mantiene los bordes de los píxeles afilados y definidos
        spritePersonaje.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    }

    @Override
    public void render() {

        // Limpieza de pantalla con un tono gris oscuro industrial
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);
        // Sincronización y proyección de la cámara virtual matemática
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
// Dibujado posicionado exactamente en el baricentro de la matriz retro
        batch.draw(spritePersonaje, 144, 74);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

        // Actualiza las proporciones físicas preservando la relación de aspecto interna
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {

        // Liberación obligatoria de memoria de vídeo (GPU buffers)
        batch.dispose();
        spritePersonaje.dispose();
    }
}