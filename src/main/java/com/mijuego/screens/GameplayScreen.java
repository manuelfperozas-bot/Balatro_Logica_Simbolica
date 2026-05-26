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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mijuego.MainGame;
import com.mijuego.utils.ValidadorLogico; // Importamos el motor lógico

public class GameplayScreen extends ScreenAdapter {
    private final MainGame game;
    private Stage stage;
    private Image fondoMesa;

    private Texture mesaFondoTex, anteRondasTex, mazoTex, descartesTex, botonOptionTex;
    private Texture jokerAristotelesTex, jokerMorganTex;
    private Texture texP, texQ, texConj, texDisy;

    private Array<CartaActor> manoJugador;

    public GameplayScreen(MainGame game) {
        this.game = game;
        this.stage = new Stage(new StretchViewport(1280f, 720f));
        Gdx.input.setInputProcessor(stage);
        this.manoJugador = new Array<>();

        cargarAssets();
        construirInterfaz();
    }

    private void cargarAssets() {
        mesaFondoTex = new Texture(Gdx.files.internal("mesa_juego_balatro.png"));
        anteRondasTex = new Texture(Gdx.files.internal("Ante_Rondas.png"));
        mazoTex = new Texture(Gdx.files.internal("mazoDeCartas_juego.png"));
        descartesTex = new Texture(Gdx.files.internal("Mano_descartes.png"));
        botonOptionTex = new Texture(Gdx.files.internal("boton_option.png"));
        jokerAristotelesTex = new Texture(Gdx.files.internal("jokerDeAristoteles.png"));
        jokerMorganTex = new Texture(Gdx.files.internal("jokerDeMorgan.png"));
        texP = new Texture(Gdx.files.internal("carta_p.png"));
        texQ = new Texture(Gdx.files.internal("carta_q.png"));
        texConj = new Texture(Gdx.files.internal("carta_conjunción.png"));
        texDisy = new Texture(Gdx.files.internal("carta_disyuncion.png"));
    }

    private void construirInterfaz() {
        // Fondo con recorte antialiasing de bordes
        int srcY = (int) (mesaFondoTex.getHeight() * 0.11f);
        TextureRegion regionMesa = new TextureRegion(mesaFondoTex, 0, srcY, mesaFondoTex.getWidth(), (int)(mesaFondoTex.getHeight() * 0.78f));
        fondoMesa = new Image(regionMesa);
        fondoMesa.setSize(1280f, 720f);
        stage.addActor(fondoMesa);

        // Decoraciones y UI estática
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
        descartes.setPosition(985f, 40f);
        stage.addActor(descartes);

        Image mazo = new Image(mazoTex);
        mazo.setSize(110f, 155f);
        mazo.setPosition(1115f, 40f);
        stage.addActor(mazo);

        // --- BOTÓN "JUGAR MANO" INTERACTIVO ---
        Image botonJugarMano = new Image(botonOptionTex); // Reutilizamos provisionalmente la textura de botón
        botonJugarMano.setSize(160f, 60f);
        botonJugarMano.setPosition(960f, 270f); // Ubicado al lado de las cartas jugadas
        botonJugarMano.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                procesarManoJugada();
            }
        });
        stage.addActor(botonJugarMano);

        // Construcción de cartas de la mano
        float xInicial = 360f;
        float yMano = 40f;
        float espacioHorizontal = 125f;

        manoJugador.add(new CartaActor(texP, "p", xInicial, yMano));
        manoJugador.add(new CartaActor(texConj, "^", xInicial + espacioHorizontal, yMano));
        manoJugador.add(new CartaActor(texDisy, "|", xInicial + (espacioHorizontal * 2), yMano));
        manoJugador.add(new CartaActor(texQ, "q", xInicial + (espacioHorizontal * 3), yMano));

        for (CartaActor carta : manoJugador) {
            stage.addActor(carta);
        }
    }

    /**
     * Recolecta las cartas en el tablero, las ordena de izquierda a derecha y calcula su validez
     */
    private void procesarManoJugada() {
        // Filtrar solo las cartas que el usuario subió al tablero (y == 270)
        Array<CartaActor> cartasEnTablero = new Array<>();
        for (CartaActor carta : manoJugador) {
            if (carta.estaEnTablero()) {
                cartasEnTablero.add(carta);
            }
        }

        // Ordenar de izquierda a derecha usando su coordenada X en el espacio virtual
        cartasEnTablero.sort((c1, c2) -> Float.compare(c1.getX(), c2.getX()));

        // Extraer los tokens lógicos ordenados
        Array<String> formulaTokens = new Array<>();
        for (CartaActor carta : cartasEnTablero) {
            formulaTokens.add(carta.getTokenLogico());
        }

        System.out.println("--- Evaluando combinación: " + formulaTokens.toString(" ") + " ---");

        // Evaluar validez con las reglas lógicas
        if (ValidadorLogico.esFormulaValida(formulaTokens)) {
            int[] score = ValidadorLogico.calcularPuntaje(formulaTokens);
            int fichasBase = score[0];
            int mult = score[1];
            int totalPuntos = fichasBase * mult;

            System.out.println("¡Estructura Válida!");
            System.out.println("Marcador: " + fichasBase + " Chips x " + mult + " Mult = " + totalPuntos + " Puntos");
        } else {
            System.out.println("Combinación Inválida. Revisa el orden sintáctico.");
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
        if (fondoMesa != null) {
            fondoMesa.setSize(stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        }
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
        texP.dispose();
        texQ.dispose();
        texConj.dispose();
        texDisy.dispose();
    }
}