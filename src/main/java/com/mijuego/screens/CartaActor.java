package com.mijuego.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CartaActor extends Actor {
    private TextureRegion region;
    private String tokenLogico;
    private boolean enTablero;
    private float xOriginal, yOriginal;

    private static final float ANCHO_CARTA = 110f;
    private static final float ALTO_CARTA = 155f;

    public CartaActor(Texture textura, String tokenLogico, float xInicial, float yInicial) {
        this.tokenLogico = tokenLogico;
        this.enTablero = false;
        this.xOriginal = xInicial;
        this.yOriginal = yInicial;

        // Recorte en caliente para ignorar las franjas transparentes de los lados del PNG
        int srcX = (int) (textura.getWidth() * 0.32f);
        int srcY = 0;
        int srcWidth = (int) (textura.getWidth() * 0.36f);
        int srcHeight = textura.getHeight();

        this.region = new TextureRegion(textura, srcX, srcY, srcWidth, srcHeight);

        setSize(ANCHO_CARTA, ALTO_CARTA);
        setPosition(xInicial, yInicial);
        setBounds(getX(), getY(), getWidth(), getHeight());

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                alternarPosicion();
            }
        });
    }

    private void alternarPosicion() {
        if (!enTablero) {
            setY(270f); // Sube limpiamente al tablero de juego
            enTablero = true;
        } else {
            setY(yOriginal); // Regresa a la mano del jugador
            enTablero = false;
        }
        setBounds(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(region, getX(), getY(), getWidth(), getHeight());
    }

    public String getTokenLogico() {
        return tokenLogico;
    }

    public boolean estaEnTablero() {
        return enTablero;
    }
}