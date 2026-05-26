package com.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mijuego.MainGame;
import com.mijuego.utils.ValidadorLogico;
import java.util.Random;

public class GameplayScreen extends ScreenAdapter {
    private final MainGame game;
    private Stage stage;
    private Image fondoMesa;

    private Texture mesaFondoTex, anteRondasTex, mazoTex, descartesTex, botonOptionTex;
    private Texture jokerAristotelesTex, jokerMorganTex;

    private Array<Texture> poolVariablesTex;
    private Array<String> poolVariablesTokens;
    private Array<Texture> poolConectivosTex;
    private Array<String> poolConectivosTokens;

    private final String[][] MODELOS_PREMISAS = new String[][]{
            {"Ana\nestudia\ningenieria", "Mauricio\njuega\nfutbol"},
            {"El\ncielo\nes\nazul", "Carlos\ncocina\npasta"},
            {"La\ncapital\nes\nCaracas", "El\nagua\nesta\nfria"},
            {"Paris\nesta\nen\nEuropa", "Sofia\nlee\nun\nlibro"}
    };

    private Random random;

    // --- DIMENSIONES REDUCIDAS ---
    private final float ANCHO_CARTA = 175f;
    private final float ALTO_CARTA = 195f;
    private final float Y_ZONA_MANO = 40f;
    private final float ESPACIO_HORIZONTAL_MANO = 130f;
    private final float Y_ZONA_TABLERO = 340f;
    private final float ESPACIO_HORIZONTAL_TABLERO = 140f;
    private final float UMBRAL_Y_DIVISION = 250f;

    private final float[] POSICIONES_X_MANO = new float[6];
    private CartaActor[] slotsMano = new CartaActor[6];
    private Array<CartaActor> cartasTablero = new Array<>();

    public GameplayScreen(MainGame game) {
        this.game = game;
        this.stage = new Stage(new StretchViewport(1280f, 720f));
        Gdx.input.setInputProcessor(stage);

        float anchoTotalMano = ANCHO_CARTA + (5 * ESPACIO_HORIZONTAL_MANO);
        float xInicialMano = (1280f - anchoTotalMano) / 2f;
        for (int i = 0; i < 6; i++) {
            POSICIONES_X_MANO[i] = xInicialMano + (i * ESPACIO_HORIZONTAL_MANO);
        }

        this.poolVariablesTex = new Array<>();
        this.poolVariablesTokens = new Array<>();
        this.poolConectivosTex = new Array<>();
        this.poolConectivosTokens = new Array<>();
        this.random = new Random();

        cargarAssets();
        construirInterfaz();
        generarManoAleatoria();
    }

    private void cargarAssets() {
        mesaFondoTex = new Texture(Gdx.files.internal("mesa_juego_balatro.png"));
        anteRondasTex = new Texture(Gdx.files.internal("Ante_Rondas.png"));
        mazoTex = new Texture(Gdx.files.internal("mazoDeCartas_juego.png"));
        descartesTex = new Texture(Gdx.files.internal("Mano_descartes.png"));
        botonOptionTex = new Texture(Gdx.files.internal("boton_option.png"));
        jokerAristotelesTex = new Texture(Gdx.files.internal("jokerDeAristoteles.png"));
        jokerMorganTex = new Texture(Gdx.files.internal("jokerDeMorgan.png"));

        registrarVariable("carta_p.png", "p");
        registrarVariable("carta_q.png", "q");
        registrarVariable("carta_r.png", "r");
        registrarVariable("carta_s.png", "s");

        registrarConectivo("carta_negacion.png", "~");
        registrarConectivo("carta_conjunción.png", "^");
        registrarConectivo("carta_disyuncion.png", "|");
        registrarConectivo("carta_condicional.png", "->");
        registrarConectivo("carta_bicondicional.png", "<->");
    }

    private void registrarVariable(String ruta, String token) {
        poolVariablesTex.add(new Texture(Gdx.files.internal(ruta)));
        poolVariablesTokens.add(token);
    }

    private void registrarConectivo(String ruta, String token) {
        poolConectivosTex.add(new Texture(Gdx.files.internal(ruta)));
        poolConectivosTokens.add(token);
    }

    private void construirInterfaz() {
        int srcY = (int) (mesaFondoTex.getHeight() * 0.11f);
        TextureRegion regionMesa = new TextureRegion(mesaFondoTex, 0, srcY, mesaFondoTex.getWidth(), (int)(mesaFondoTex.getHeight() * 0.78f));
        fondoMesa = new Image(regionMesa);
        fondoMesa.setSize(1280f, 720f);
        stage.addActor(fondoMesa);

        Image anteRondas = new Image(anteRondasTex);
        anteRondas.setSize(180f, 115f);
        anteRondas.setPosition(35f, 560f);
        stage.addActor(anteRondas);

        Image jokerAristoteles = new Image(jokerAristotelesTex);
        jokerAristoteles.setSize(115f, 155f);
        jokerAristoteles.setPosition(440f, 520f);
        stage.addActor(jokerAristoteles);

        Image jokerMorgan = new Image(jokerMorganTex);
        jokerMorgan.setSize(115f, 155f);
        jokerMorgan.setPosition(570f, 520f);
        stage.addActor(jokerMorgan);

        Image descartes = new Image(descartesTex);
        descartes.setSize(110f, 155f);
        descartes.setPosition(1080f, 40f);
        stage.addActor(descartes);

        Image mazo = new Image(mazoTex);
        mazo.setSize(110f, 155f);
        mazo.setPosition(1160f, 40f);
        stage.addActor(mazo);

        Image botonJugarMano = new Image(botonOptionTex);
        botonJugarMano.setSize(160f, 60f);
        botonJugarMano.setPosition(1080f, 340f);
        botonJugarMano.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                procesarManoJugada();
            }
        });
        stage.addActor(botonJugarMano);
    }

    private void generarManoAleatoria() {
        for (int i = 0; i < 6; i++) {
            if (slotsMano[i] != null) slotsMano[i].remove();
        }
        for (CartaActor c : cartasTablero) {
            c.remove();
        }
        cartasTablero.clear();

        for (int i = 0; i < 6; i++) {
            CartaActor nuevaCarta = crearCartaAleatoria(POSICIONES_X_MANO[i], Y_ZONA_MANO, i);
            slotsMano[i] = nuevaCarta;
            stage.addActor(nuevaCarta);
        }
        redibujarEstructuraFija();
    }

    private CartaActor crearCartaAleatoria(float x, float y, int slot) {
        Texture texSeleccionada;
        String tokenSeleccionado;
        String fraseAsignada = null;

        if (slot < 4) {
            int indexVariable = random.nextInt(poolVariablesTex.size);
            texSeleccionada = poolVariablesTex.get(indexVariable);
            tokenSeleccionado = poolVariablesTokens.get(indexVariable);

            int modeloElegido = random.nextInt(2);
            fraseAsignada = MODELOS_PREMISAS[indexVariable][modeloElegido];
        } else {
            int indexConectivo = random.nextInt(poolConectivosTex.size);
            texSeleccionada = poolConectivosTex.get(indexConectivo);
            tokenSeleccionado = poolConectivosTokens.get(indexConectivo);
        }

        CartaActor nuevaCarta = new CartaActor(texSeleccionada, tokenSeleccionado, fraseAsignada, x, y, slot, this);
        configurarEventosCarta(nuevaCarta);
        return nuevaCarta;
    }

    private void configurarEventosCarta(final CartaActor carta) {
        carta.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                procesarClicRapido(carta);
            }
        });

        carta.addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                carta.setPosition(carta.getX() + getDeltaX(), carta.getY() + getDeltaY());
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                procesarSoltadoMano(carta);
            }
        });
    }

    public void redibujarEstructuraFija() {
        for (int i = 0; i < 6; i++) {
            if (slotsMano[i] != null) {
                slotsMano[i].setSlotActual(i);
                slotsMano[i].setSize(ANCHO_CARTA, ALTO_CARTA);
                slotsMano[i].forzarAposicionBase(POSICIONES_X_MANO[i], Y_ZONA_MANO);
            }
        }

        if (cartasTablero.size > 0) {
            float anchoTotalTablero = ANCHO_CARTA + (cartasTablero.size - 1) * ESPACIO_HORIZONTAL_TABLERO;
            float xInicialCentrado = (1280f - anchoTotalTablero) / 2f;

            for (int i = 0; i < cartasTablero.size; i++) {
                CartaActor carta = cartasTablero.get(i);
                float destinoX = xInicialCentrado + (i * ESPACIO_HORIZONTAL_TABLERO);
                carta.setSize(ANCHO_CARTA, ALTO_CARTA);
                carta.forzarAposicionBase(destinoX, Y_ZONA_TABLERO);
            }
        }
    }

    public void procesarClicRapido(CartaActor carta) {
        if (!carta.estaEnTablero()) {
            int slotOrigen = carta.getSlotActual();
            slotsMano[slotOrigen] = null;
            carta.setEnTablero(true);
            cartasTablero.add(carta);
        } else {
            int primerSlotLibreMano = obtenerPrimerSlotLibre(slotsMano);
            if (primerSlotLibreMano != -1) {
                cartasTablero.removeValue(carta, true);
                carta.setEnTablero(false);
                slotsMano[primerSlotLibreMano] = carta;
            }
        }
        redibujarEstructuraFija();
    }

    public void procesarSoltadoMano(CartaActor carta) {
        float centroY = carta.getY() + (carta.getHeight() / 2f);
        int slotOrigenMano = carta.getSlotActual();

        if (centroY > UMBRAL_Y_DIVISION) {
            if (!carta.estaEnTablero()) {
                slotsMano[slotOrigenMano] = null;
                carta.setEnTablero(true);

                int indiceInsercion = 0;
                for (int i = 0; i < cartasTablero.size; i++) {
                    if (carta.getX() > cartasTablero.get(i).getX()) {
                        indiceInsercion = i + 1;
                    }
                }
                cartasTablero.insert(indiceInsercion, carta);
            } else {
                cartasTablero.removeValue(carta, true);
                int indiceInsercion = 0;
                for (int i = 0; i < cartasTablero.size; i++) {
                    if (carta.getX() > cartasTablero.get(i).getX()) {
                        indiceInsercion = i + 1;
                    }
                }
                cartasTablero.insert(indiceInsercion, carta);
            }
        } else {
            if (carta.estaEnTablero()) {
                int slotDestinoMano = calcularSlotManoPorCoordenadaX(carta.getX());

                if (slotsMano[slotDestinoMano] == null) {
                    cartasTablero.removeValue(carta, true);
                    carta.setEnTablero(false);
                    slotsMano[slotDestinoMano] = carta;
                } else {
                    int primerLibre = obtenerPrimerSlotLibre(slotsMano);
                    if (primerLibre != -1) {
                        cartasTablero.removeValue(carta, true);
                        carta.setEnTablero(false);
                        slotsMano[primerLibre] = carta;
                    }
                }
            } else {
                int slotDestinoMano = calcularSlotManoPorCoordenadaX(carta.getX());
                if (slotDestinoMano != slotOrigenMano) {
                    CartaActor cartaOcupante = slotsMano[slotDestinoMano];
                    slotsMano[slotOrigenMano] = cartaOcupante;
                    if (cartaOcupante != null) {
                        cartaOcupante.setSlotActual(slotOrigenMano);
                    }
                    slotsMano[slotDestinoMano] = carta;
                }
            }
        }
        redibujarEstructuraFija();
    }

    private int obtenerPrimerSlotLibre(CartaActor[] slots) {
        for (int i = 0; i < 6; i++) {
            if (slots[i] == null) return i;
        }
        return -1;
    }

    private int calcularSlotManoPorCoordenadaX(float x) {
        int ranuraMejor = 0;
        float menorDistancia = Float.MAX_VALUE;
        float centroCartaX = x + (ANCHO_CARTA / 2f);

        for (int i = 0; i < 6; i++) {
            float centroRanuraX = POSICIONES_X_MANO[i] + (ANCHO_CARTA / 2f);
            float dist = Math.abs(centroCartaX - centroRanuraX);
            if (dist < menorDistancia) {
                menorDistancia = dist;
                ranuraMejor = i;
            }
        }
        return ranuraMejor;
    }

    private void procesarManoJugada() {
        Array<String> formulaTokens = new Array<>();
        Array<CartaActor> cartasAeliminar = new Array<>();

        for (CartaActor carta : cartasTablero) {
            formulaTokens.add(carta.getTokenLogico());
            cartasAeliminar.add(carta);
        }

        if (formulaTokens.size == 0) return;

        if (ValidadorLogico.esFormulaValida(formulaTokens)) {
            int[] score = ValidadorLogico.calcularPuntaje(formulaTokens);
            System.out.println("¡Mano Válida! Puntos: " + (score[0] * score[1]));

            for (CartaActor carta : cartasAeliminar) {
                cartasTablero.removeValue(carta, true);
                carta.remove();
            }

            for (int i = 0; i < 6; i++) {
                if (slotsMano[i] == null) {
                    CartaActor nuevaCarta = crearCartaAleatoria(POSICIONES_X_MANO[i], Y_ZONA_MANO, i);
                    slotsMano[i] = nuevaCarta;
                    stage.addActor(nuevaCarta);
                }
            }
            redibujarEstructuraFija();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        mesaFondoTex.dispose();
        anteRondasTex.dispose();
        mazoTex.dispose();
        descartesTex.dispose();
        botonOptionTex.dispose();
        jokerAristotelesTex.dispose();
        jokerMorganTex.dispose();
        for (Texture tex : poolVariablesTex) tex.dispose();
        for (Texture tex : poolConectivosTex) tex.dispose();
    }
}