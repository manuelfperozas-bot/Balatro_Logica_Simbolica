package com.mijuego.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class CartaActor extends Actor {
    private Texture textura;
    private String tokenLogico;
    private String textoPremisa;
    private float xBase, yBase;
    private int slotActual;
    private boolean enTablero;
    private GameplayScreen screen;

    private BitmapFont font;
    private GlyphLayout layout;

    public CartaActor(Texture textura, String tokenLogico, String textoPremisa, float xBase, float yBase, int slotActual, GameplayScreen screen) {
        this.textura = textura;
        this.tokenLogico = tokenLogico;
        this.textoPremisa = textoPremisa;
        this.xBase = xBase;
        this.yBase = yBase;
        this.slotActual = slotActual;
        this.screen = screen;
        this.enTablero = false;

        setSize(220f, 195f);
        setPosition(xBase, yBase);

        this.font = new BitmapFont();
        this.font.setColor(Color.BLACK);
        this.layout = new GlyphLayout();
    }

    /**
     * REVISIÓN DE VENTANA INTERACTIVA TOTAL:
     * Ahora aplica el centrado simétrico a todas las cartas por igual,
     * solucionando el solapamiento fantasma en el slot 5 y el último del tablero.
     */
    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && getTouchable() != Touchable.enabled) return null;

        // Si el toque está completamente fuera de los límites físicos de la carta, se descarta
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return null;

        float centroCartaX = getWidth() / 2f;

        // --- MANO: Corrección para todas las ranuras (incluyendo la 5) ---
        if (!enTablero) {
            float espacioMano = screen.getEspacioHorizontalMano(); // 100px
            float radioPermitido = espacioMano / 2f;              // 50px a cada lado

            float limiteIzquierdo = centroCartaX - radioPermitido;
            float limiteDerecho = centroCartaX + radioPermitido;

            // Al aplicar esto a todas, la carta 4 reduce su zona de impacto y ya no bloquea a la 5
            if (x < limiteIzquierdo || x > limiteDerecho) {
                return null;
            }
        }

        // --- TABLERO: Corrección para todas las cartas en juego ---
        if (enTablero) {
            float espacioTablero = screen.getEspacioHorizontalTablero(); // 110px
            float radioPermitido = espacioTablero / 2f;                  // 55px a cada lado

            float limiteIzquierdo = centroCartaX - radioPermitido;
            float limiteDerecho = centroCartaX + radioPermitido;

            if (x < limiteIzquierdo || x > limiteDerecho) {
                return null;
            }
        }

        return this;
    }

    public void forzarAposicionBase(float x, float y) {
        this.xBase = x;
        this.yBase = y;
        setPosition(x, y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textura, getX(), getY(), getWidth(), getHeight());

        if (textoPremisa != null && !textoPremisa.isEmpty()) {
            float margenHorizontal = 18f;
            float anchoMaximoTexto = getWidth() - (margenHorizontal * 2);
            float altoMaximoTexto = getHeight() - 35f;

            font.getData().setScale(0.7f);
            layout.setText(font, textoPremisa, Color.BLACK, anchoMaximoTexto, com.badlogic.gdx.utils.Align.center, true);

            while ((layout.width > anchoMaximoTexto || layout.height > altoMaximoTexto) && font.getData().scaleX > 0.3f) {
                float nuevaEscala = font.getData().scaleX - 0.05f;
                font.getData().setScale(nuevaEscala);
                layout.setText(font, textoPremisa, Color.BLACK, anchoMaximoTexto, com.badlogic.gdx.utils.Align.center, true);
            }

            float textX = getX() + margenHorizontal;
            float textY = getY() + (getHeight() / 2f) + (layout.height / 2f);

            font.draw(batch, layout, textX, textY);
        }
    }

    public String getTokenLogico() { return tokenLogico; }
    public int getSlotActual() { return slotActual; }
    public void setSlotActual(int slot) { this.slotActual = slot; }
    public boolean estaEnTablero() { return enTablero; }
    public void setEnTablero(boolean enTablero) { this.enTablero = enTablero; }
}