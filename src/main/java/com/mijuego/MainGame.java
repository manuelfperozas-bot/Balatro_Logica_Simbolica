package com.mijuego;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mijuego.screens.MainMenuScreen;

public class MainGame extends Game {

    public SpriteBatch batch;

    // Expandimos la resolución virtual para dar espacio a las cartas y UI
    public final float VIRTUAL_WIDTH = 1280f;
    public final float VIRTUAL_HEIGHT = 720f;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // En lugar de cargar texturas aquí, delegamos el control a la Pantalla del Menú
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        // Es VITAL llamar al super.render() para que la pantalla actual se dibuje
        super.render();
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
    }
}