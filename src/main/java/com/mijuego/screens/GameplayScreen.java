package com.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mijuego.MainGame;
import com.mijuego.data.LeaderboardManager;
import com.mijuego.utils.ValidadorLogico;
import java.util.Random;

public class GameplayScreen extends ScreenAdapter {
    private final MainGame game;
    private Stage stage;
    private Image fondoMesa;

    private Texture mesaFondoTex, anteRondasTex, mazoTex, descartesTex, marcadorTex, roundScoreTex, objetivosTex;
    private Texture jokerAristotelesTex, jokerMorganTex;
    private Texture botonJugarTex, botonDescartarTex, botonOpcionesTex;

    private Array<Texture> poolVariablesTex;
    private Array<String> poolVariablesTokens;
    private Array<Texture> poolConectivosTex;
    private Array<String> poolConectivosTokens;

    private final String[][] MODELOS_PREMISAS = new String[][]{
            {"Ana\nestudia\ningenieria", "Mauricio\njuega\nfútbol"},
            {"El\ncielo\nes\nazul", "Carlos\ncocina\npasta"},
            {"La\ncapital\nes\nCaracas", "El\nagua\nesta\nfria"},
            {"Paris\nesta\nen\nEuropa", "Sofia\nlee\nun\nlibro"}
    };

    private Random random;

    // --- VARIABLES DE CONTROL DE JUEGO ---
    private int manosRestantes = 3;
    private int descartesRestantes = 2;
    private int puntajeAcumulado = 0;
    private int puntajeObjetivo = 1000;
    private int fichasUltimaMano = 0;
    private int multUltimaMano = 0;

    // --- SISTEMA DE NIVELES Y ESTADÍSTICAS ---
    private int nivelActual = 1;
    private final int MAX_NIVEL = 4;
    private int totalManosJugadas = 0;

    // --- ELEMENTOS VISUALES DEL HUD ---
    private BitmapFont fuenteContador;
    private Label labelManos;
    private Label labelDescartes;
    private Label labelPuntaje;
    private Label labelObjetivo;
    private Label labelFichasMano;
    private Label labelMultMano;
    private Label labelNivel;
    private Label labelRonda;

    // --- COMPONENTES DE LAS PANTALLAS DE TRANSICIÓN (VICTORIA / DERROTA) ---
    private boolean nivelSuperado = false;
    private boolean juegoTerminado = false; // Estado estricto de derrota
    private Image overlayFondoGris;
    private Label labelEstadoPantalla; // Reutilizado para "OBJETIVO ALCANZADO" o "JUEGO TERMINADO"
    private Image botonContinuar;

    // --- COMPONENTES PARA REGISTRAR EL NOMBRE DENTRO DEL JUEGO ---
    private TextField campoNombre;
    private Image botonRegistrarNombre;
    private Texture fondoInputTex;
    private Texture cursorTex;

    // --- DIMENSIONES DE LAS CARTAS Y ZONAS ---
    private final float ANCHO_CARTA = 220f;
    private final float ALTO_CARTA = 195f;
    private final float Y_ZONA_MANO = 40f;
    private final float ESPACIO_HORIZONTAL_MANO = 100f;
    private final float Y_ZONA_TABLERO = 340f;
    private final float ESPACIO_HORIZONTAL_TABLERO = 110f;
    private final float UMBRAL_Y_DIVISION = 250f;

    private final float[] POSICIONES_X_MANO = new float[6];
    private CartaActor[] slotsMano = new CartaActor[6];
    private Array<CartaActor> cartasTablero = new Array<>();
    private Array<CartaActor> cartasSeleccionadas = new Array<>();

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
        generarManoInicial();
    }

    private void cargarAssets() {
        mesaFondoTex = new Texture(Gdx.files.internal("mesa_juego_balatro.png"));
        anteRondasTex = new Texture(Gdx.files.internal("Ante_Rondas.png"));
        mazoTex = new Texture(Gdx.files.internal("mazoDeCartas_juego.png"));
        descartesTex = new Texture(Gdx.files.internal("Mano_descartes.png"));
        marcadorTex = new Texture(Gdx.files.internal("marcador_juego.png"));
        roundScoreTex = new Texture(Gdx.files.internal("Roundscore_juego.png"));
        jokerAristotelesTex = new Texture(Gdx.files.internal("jokerDeAristoteles.png"));
        jokerMorganTex = new Texture(Gdx.files.internal("jokerDeMorgan.png"));
        objetivosTex = new Texture(Gdx.files.internal("objetivos.png"));

        botonJugarTex = new Texture(Gdx.files.internal("botonJugar.png"));
        botonDescartarTex = new Texture(Gdx.files.internal("botonDescartar.png"));
        botonOpcionesTex = new Texture(Gdx.files.internal("boton_opciones.png"));

        registrarVariable("carta_p.png", "p");
        registrarVariable("carta_q.png", "q");
        registrarVariable("carta_r.png", "r");
        registrarVariable("carta_s.png", "s");

        registrarConectivo("carta_negacion.png", "~");
        registrarConectivo("carta_conjunción.png", "^");
        registrarConectivo("carta_disyuncion.png", "|");
        registrarConectivo("carta_condicional.png", "->");
        registrarConectivo("carta_bicondicional.png", "<->");

        fuenteContador = new BitmapFont();
        fuenteContador.getData().setScale(2.2f);

        // Generar dinámicamente un fondo gris oscuro para la caja de texto
        Pixmap pixmap = new Pixmap(300, 50, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();
        fondoInputTex = new Texture(pixmap);
        pixmap.dispose();

        // Generar dinámicamente la barrita blanca del cursor de escritura
        Pixmap cursorPixmap = new Pixmap(3, 32, Pixmap.Format.RGBA8888);
        cursorPixmap.setColor(Color.WHITE);
        cursorPixmap.fill();
        cursorTex = new Texture(cursorPixmap);
        cursorPixmap.dispose();
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
        // 1. CAPA FONDO PRINCIPAL
        TextureRegion regionMesa = new TextureRegion(mesaFondoTex, 0, 0, mesaFondoTex.getWidth(), mesaFondoTex.getHeight());
        fondoMesa = new Image(regionMesa);
        fondoMesa.setSize(1280f, 720f);
        stage.addActor(fondoMesa);

        // 2. CAPA DE CONTENEDORES / CONTORNOS TEXTURIZADOS
        Image anteRondas = new Image(anteRondasTex);
        anteRondas.setSize(230f, 165f);
        anteRondas.setPosition(5f, 20f);
        stage.addActor(anteRondas);

        Image manosDescartes = new Image(descartesTex);
        manosDescartes.setSize(230f, 165f);
        manosDescartes.setPosition(5f, 195f);
        stage.addActor(manosDescartes);

        Image marcadorJuego = new Image(marcadorTex);
        marcadorJuego.setSize(230f, 82.5f);
        marcadorJuego.setPosition(5f, 370f);
        stage.addActor(marcadorJuego);

        Image roundScore = new Image(roundScoreTex);
        roundScore.setSize(230f, 115.5f);
        roundScore.setPosition(5f, 454.5f);
        stage.addActor(roundScore);

        Image panelObjetivos = new Image(objetivosTex);
        panelObjetivos.setSize(230f, 91f);
        panelObjetivos.setPosition(5f, 572f);
        stage.addActor(panelObjetivos);

        Image jokerAristoteles = new Image(jokerAristotelesTex);
        jokerAristoteles.setSize(115f, 155f);
        jokerAristoteles.setPosition(440f, 520f);
        jokerAristoteles.setVisible(false); // Ocultado visualmente
        stage.addActor(jokerAristoteles);

        Image jokerMorgan = new Image(jokerMorganTex);
        jokerMorgan.setSize(115f, 155f);
        jokerMorgan.setPosition(570f, 520f);
        jokerMorgan.setVisible(false); // Ocultado visualmente
        stage.addActor(jokerMorgan);

        Image mazo = new Image(mazoTex);
        mazo.setSize(110f, 155f);
        mazo.setPosition(1160f, 40f);
        stage.addActor(mazo);

        float anchoBoton = 150f;
        float altoBoton = 50f;
        float xBotones = 1280f - anchoBoton - 15f;
        float yBotonJugar = (720f / 2f) + (altoBoton / 2f) + 8f;
        float yBotonDescartar = (720f / 2f) - (altoBoton / 2f) - 8f;

        Image botonJugar = new Image(botonJugarTex);
        botonJugar.setSize(anchoBoton, altoBoton);
        botonJugar.setPosition(xBotones, yBotonJugar);
        botonJugar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!nivelSuperado && !juegoTerminado) procesarManoJugada();
            }
        });
        stage.addActor(botonJugar);

        Image botonDescartar = new Image(botonDescartarTex);
        botonDescartar.setSize(anchoBoton, altoBoton);
        botonDescartar.setPosition(xBotones, yBotonDescartar);
        botonDescartar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!nivelSuperado && !juegoTerminado) procesarDescarte();
            }
        });
        stage.addActor(botonDescartar);

        Image botonOpciones = new Image(botonOpcionesTex);
        botonOpciones.setSize(100f, 45f);
        botonOpciones.setPosition(1280f - 115f, 720f - 60f);
        stage.addActor(botonOpciones);

        // 3. CAPA SUPREMA: LABELS Y TEXTOS
        Label.LabelStyle estiloContadores = new Label.LabelStyle(fuenteContador, Color.WHITE);

        labelNivel = new Label(nivelActual + "/" + MAX_NIVEL, estiloContadores);
        labelNivel.setAlignment(com.badlogic.gdx.utils.Align.center);
        float xCentradoNivel = (5f + (115f / 2f)) - (labelNivel.getPrefWidth() / 2f);
        labelNivel.setPosition(xCentradoNivel, 72f);
        stage.addActor(labelNivel);

        labelRonda = new Label(String.valueOf(totalManosJugadas), estiloContadores);
        labelRonda.setAlignment(com.badlogic.gdx.utils.Align.center);
        float xCentradoRonda = (120f + (115f / 2f)) - (labelRonda.getPrefWidth() / 2f);
        labelRonda.setPosition(xCentradoRonda, 72f);
        stage.addActor(labelRonda);

        labelManos = new Label(String.valueOf(manosRestantes), estiloContadores);
        labelManos.setPosition(52f, 222f);
        stage.addActor(labelManos);

        labelDescartes = new Label(String.valueOf(descartesRestantes), estiloContadores);
        labelDescartes.setPosition(158f, 222f);
        stage.addActor(labelDescartes);

        labelFichasMano = new Label(String.valueOf(fichasUltimaMano), estiloContadores);
        labelFichasMano.setPosition(32f, 394f);
        stage.addActor(labelFichasMano);

        labelMultMano = new Label(String.valueOf(multUltimaMano), estiloContadores);
        labelMultMano.setPosition(158f, 395f);
        stage.addActor(labelMultMano);

        labelPuntaje = new Label(String.valueOf(puntajeAcumulado), estiloContadores);
        labelPuntaje.setPosition(160f, 488f);
        stage.addActor(labelPuntaje);

        labelObjetivo = new Label(String.valueOf(puntajeObjetivo), estiloContadores);
        labelObjetivo.setPosition(88f, 585f);
        stage.addActor(labelObjetivo);

        // 4. CAPA DE INTERFAZ DE FIN DE NIVEL / PARTIDA
        Texture pixelGris = new Texture(Gdx.files.internal("carta_p.png"));
        overlayFondoGris = new Image(new TextureRegion(pixelGris, 0, 0, 1, 1));
        overlayFondoGris.setSize(1280f, 720f);
        overlayFondoGris.setColor(0f, 0f, 0f, 0.75f);
        overlayFondoGris.setVisible(false);
        stage.addActor(overlayFondoGris);

        labelEstadoPantalla = new Label("", estiloContadores);
        labelEstadoPantalla.setAlignment(com.badlogic.gdx.utils.Align.center);
        labelEstadoPantalla.setVisible(false);
        stage.addActor(labelEstadoPantalla);

        botonContinuar = new Image(botonJugarTex);
        botonContinuar.setSize(220f, 65f);
        botonContinuar.setPosition((1280f / 2f) - (220f / 2f), 280f);
        botonContinuar.setVisible(false);
        botonContinuar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (nivelSuperado) {
                    ocultarPantallaTransicion();
                    avanzarSiguienteNivel();
                }
            }
        });
        stage.addActor(botonContinuar);

        // Estilo del Campo de Texto
        TextField.TextFieldStyle estiloCampo = new TextField.TextFieldStyle();
        estiloCampo.font = fuenteContador;
        estiloCampo.fontColor = Color.WHITE;
        estiloCampo.background = new TextureRegionDrawable(new TextureRegion(fondoInputTex));
        estiloCampo.cursor = new TextureRegionDrawable(new TextureRegion(cursorTex));

        // Inicializar campo integrado
        campoNombre = new TextField("", estiloCampo);
        campoNombre.setSize(340f, 55f);
        campoNombre.setPosition((1280f / 2f) - (340f / 2f), 310f);
        campoNombre.setMessageText("Tu Nombre Aqui");
        campoNombre.setAlignment(com.badlogic.gdx.utils.Align.center);
        campoNombre.setVisible(false);
        stage.addActor(campoNombre);

        // Botón integrado para registrar y volver al menú principal
        botonRegistrarNombre = new Image(botonJugarTex);
        botonRegistrarNombre.setSize(180f, 55f);
        botonRegistrarNombre.setPosition((1280f / 2f) - (180f / 2f), 230f);
        botonRegistrarNombre.setVisible(false);
        botonRegistrarNombre.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String textoIngresado = campoNombre.getText();
                LeaderboardManager manager = new LeaderboardManager();

                if (textoIngresado != null && !textoIngresado.trim().isEmpty()) {
                    manager.addScore(textoIngresado, puntajeAcumulado);
                } else {
                    manager.addScore("Anonimo", puntajeAcumulado);
                }

                System.out.println("Puntuacion procesada con exito.");
                ocultarPantallaTransicion();

                // Te redirige limpiamente al menú de inicio del juego
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(botonRegistrarNombre);
    }

    private void mostrarPantallaVictoria() {
        nivelSuperado = true;
        overlayFondoGris.setVisible(true);

        labelEstadoPantalla.setText("¡OBJETIVO ALCANZADO!");
        labelEstadoPantalla.getStyle().fontColor = Color.GOLD;

        // MODIFICACIÓN: Desplazado +230 píxeles a la derecha (los 80 previos + 150 solicitados)
        float xDesplazadoVictoria = ((1280f / 2f) - (labelEstadoPantalla.getPrefWidth() / 2f)) + 230f;
        labelEstadoPantalla.setPosition(xDesplazadoVictoria, 420f);
        labelEstadoPantalla.setVisible(true);

        botonContinuar.setVisible(true);
        redibujarEstructuraFija();
    }

    private void mostrarPantallaDerrota() {
        juegoTerminado = true;
        overlayFondoGris.setVisible(true);

        labelEstadoPantalla.setText("JUEGO TERMINADO");
        labelEstadoPantalla.getStyle().fontColor = Color.RED;

        // Centrado horizontal original de derrota desplazado 160 píxeles a la derecha
        float xDesplazado = ((1280f / 2f) - (labelEstadoPantalla.getPrefWidth() / 2f)) + 160f;
        labelEstadoPantalla.setPosition(xDesplazado, 420f);
        labelEstadoPantalla.setVisible(true);

        botonContinuar.setVisible(false);

        // Mostrar widgets integrados
        campoNombre.setText("");
        campoNombre.setVisible(true);
        botonRegistrarNombre.setVisible(true);
        stage.setKeyboardFocus(campoNombre);

        redibujarEstructuraFija();
    }

    private void ocultarPantallaTransicion() {
        nivelSuperado = false;
        juegoTerminado = false;
        overlayFondoGris.setVisible(false);
        labelEstadoPantalla.setVisible(false);
        botonContinuar.setVisible(false);
        campoNombre.setVisible(false);
        botonRegistrarNombre.setVisible(false);
    }

    private void verificarEstadosDeRonda() {
        if (puntajeAcumulado >= puntajeObjetivo) {
            mostrarPantallaVictoria();
        } else if (manosRestantes <= 0) {
            mostrarPantallaDerrota();
        }
    }

    private void avanzarSiguienteNivel() {
        if (nivelActual < MAX_NIVEL) {
            nivelActual++;
            puntajeObjetivo *= 2.5;
            puntajeAcumulado = 0;
            manosRestantes = 3;
            descartesRestantes = 2;

            labelNivel.setText(nivelActual + "/" + MAX_NIVEL);
            labelObjetivo.setText(String.valueOf(puntajeObjetivo));
            labelPuntaje.setText(String.valueOf(puntajeAcumulado));
            labelManos.setText(String.valueOf(manosRestantes));
            labelDescartes.setText(String.valueOf(descartesRestantes));

            labelNivel.setX((5f + (115f / 2f)) - (labelNivel.getPrefWidth() / 2f));
            generarManoInicial();
        } else {
            System.out.println("¡Felicidades, ganaste el juego completo!");
        }
    }

    private void generarManoInicial() {
        for (int i = 0; i < 6; i++) {
            if (slotsMano[i] != null) slotsMano[i].remove();
        }
        for (CartaActor c : cartasTablero) {
            c.remove();
        }
        cartasTablero.clear();
        cartasSeleccionadas.clear();

        for (int i = 0; i < 6; i++) {
            Texture texSeleccionada;
            String tokenSeleccionado;
            String fraseAsignada = null;

            if (i < 4) {
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

            CartaActor nuevaCarta = new CartaActor(texSeleccionada, tokenSeleccionado, fraseAsignada, POSICIONES_X_MANO[i], Y_ZONA_MANO, i, this);
            configurarEventosCarta(nuevaCarta);
            slotsMano[i] = nuevaCarta;
            stage.addActor(nuevaCarta);
        }
        redibujarEstructuraFija();
    }

    private CartaActor crearCartaCompletamenteAleatoria(float x, float y, int slot) {
        Texture texSeleccionada;
        String tokenSeleccionado;
        String fraseAsignada = null;

        int totalElementosPool = poolVariablesTex.size + poolConectivosTex.size;
        int indiceGlobal = random.nextInt(totalElementosPool);

        if (indiceGlobal < poolVariablesTex.size) {
            texSeleccionada = poolVariablesTex.get(indiceGlobal);
            tokenSeleccionado = poolVariablesTokens.get(indiceGlobal);
            int modeloElegido = random.nextInt(2);
            fraseAsignada = MODELOS_PREMISAS[indiceGlobal][modeloElegido];
        } else {
            int indiceConectivo = indiceGlobal - poolVariablesTex.size;
            texSeleccionada = poolConectivosTex.get(indiceConectivo);
            tokenSeleccionado = poolConectivosTokens.get(indiceConectivo);
        }

        CartaActor nuevaCarta = new CartaActor(texSeleccionada, tokenSeleccionado, fraseAsignada, x, y, slot, this);
        configurarEventosCarta(nuevaCarta);
        return nuevaCarta;
    }

    private void configureEventosCarta(final CartaActor carta) {
        configurarEventosCarta(carta);
    }

    private void configurarEventosCarta(final CartaActor carta) {
        carta.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!nivelSuperado && !juegoTerminado) procesarClicRapido(carta);
            }
        });

        carta.addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (!nivelSuperado && !juegoTerminado) carta.setPosition(carta.getX() + getDeltaX(), carta.getY() + getDeltaY());
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                if (!nivelSuperado && !juegoTerminado) procesarSoltadoMano(carta, event.getStageX());
            }
        });
    }

    public void redibujarEstructuraFija() {
        cartasSeleccionadas.clear();

        for (int i = 0; i < 6; i++) {
            if (slotsMano[i] != null) {
                slotsMano[i].setSlotActual(i);
                slotsMano[i].setSize(ANCHO_CARTA, ALTO_CARTA);
                slotsMano[i].forzarAposicionBase(POSICIONES_X_MANO[i], Y_ZONA_MANO);
                slotsMano[i].toFront();
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
                carta.toFront();
                cartasSeleccionadas.add(carta);
            }
        }

        if (labelManos != null) labelManos.toFront();
        if (labelDescartes != null) labelDescartes.toFront();
        if (labelPuntaje != null) labelPuntaje.toFront();
        if (labelObjetivo != null) labelObjetivo.toFront();
        if (labelFichasMano != null) labelFichasMano.toFront();
        if (labelMultMano != null) labelMultMano.toFront();
        if (labelNivel != null) labelNivel.toFront();
        if (labelRonda != null) labelRonda.toFront();

        if (nivelSuperado || juegoTerminado) {
            overlayFondoGris.toFront();
            labelEstadoPantalla.toFront();
            if (nivelSuperado) {
                botonContinuar.toFront();
            } else {
                campoNombre.toFront();
                botonRegistrarNombre.toFront();
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

    public void procesarSoltadoMano(CartaActor carta, float stageX) {
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
                int slotDestinoMano = calcularSlotManoPorCoordenadaX(stageX);

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
                int slotDestinoMano = calcularSlotManoPorCoordenadaX(stageX);
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

    private int calcularSlotManoPorCoordenadaX(float stageX) {
        int ranuraMejor = 0;
        float menorDistancia = Float.MAX_VALUE;

        for (int i = 0; i < 6; i++) {
            float centroRanuraX = POSICIONES_X_MANO[i] + (ANCHO_CARTA / 2f);
            float dist = Math.abs(stageX - centroRanuraX);
            if (dist < menorDistancia) {
                menorDistancia = dist;
                ranuraMejor = i;
            }
        }
        return ranuraMejor;
    }

    private void procesarManoJugada() {
        if (manosRestantes <= 0 || nivelSuperado || juegoTerminado) return;

        Array<String> formulaTokens = new Array<>();
        Array<CartaActor> cartasAeliminar = new Array<>();

        for (CartaActor carta : cartasSeleccionadas) {
            formulaTokens.add(carta.getTokenLogico());
            cartasAeliminar.add(carta);
        }

        if (formulaTokens.size == 0) return;

        if (ValidadorLogico.esFormulaValida(formulaTokens)) {
            int[] score = ValidadorLogico.calcularPuntaje(formulaTokens);

            fichasUltimaMano = score[0];
            multUltimaMano = score[1];
            labelFichasMano.setText(String.valueOf(fichasUltimaMano));
            labelMultMano.setText(String.valueOf(multUltimaMano));

            int puntosMano = fichasUltimaMano * multUltimaMano;
            puntajeAcumulado += puntosMano;
            labelPuntaje.setText(String.valueOf(puntajeAcumulado));

            manosRestantes--;
            labelManos.setText(String.valueOf(manosRestantes));

            totalManosJugadas++;
            labelRonda.setText(String.valueOf(totalManosJugadas));
            labelRonda.setX((120f + (115f / 2f)) - (labelRonda.getPrefWidth() / 2f));

            for (CartaActor carta : cartasAeliminar) {
                cartasTablero.removeValue(carta, true);
                carta.remove();
            }

            for (int i = 0; i < 6; i++) {
                if (slotsMano[i] == null) {
                    CartaActor nuevaCarta = crearCartaCompletamenteAleatoria(POSICIONES_X_MANO[i], Y_ZONA_MANO, i);
                    slotsMano[i] = nuevaCarta;
                    stage.addActor(nuevaCarta);
                }
            }
            cartasSeleccionadas.clear();

            verificarEstadosDeRonda();
            redibujarEstructuraFija();
        } else {
            System.out.println("Fórmula no válida.");
            manosRestantes--;
            labelManos.setText(String.valueOf(manosRestantes));
            verificarEstadosDeRonda();
        }
    }

    private void procesarDescarte() {
        if (descartesRestantes <= 0 || cartasTablero.size == 0 || nivelSuperado || juegoTerminado) return;

        descartesRestantes--;
        labelDescartes.setText(String.valueOf(descartesRestantes));

        for (CartaActor carta : cartasTablero) {
            carta.remove();
        }
        cartasTablero.clear();
        cartasSeleccionadas.clear();

        for (int i = 0; i < 6; i++) {
            if (slotsMano[i] == null) {
                CartaActor nuevaCarta = crearCartaCompletamenteAleatoria(POSICIONES_X_MANO[i], Y_ZONA_MANO, i);
                slotsMano[i] = nuevaCarta;
                stage.addActor(nuevaCarta);
            }
        }

        verificarEstadosDeRonda();
        redibujarEstructuraFija();
    }

    public float getEspacioHorizontalMano() { return ESPACIO_HORIZONTAL_MANO; }
    public float getEspacioHorizontalTablero() { return ESPACIO_HORIZONTAL_TABLERO; }

    @Override
    public void render(float delta) {
        stage.getViewport().apply();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
        marcadorTex.dispose();
        roundScoreTex.dispose();
        jokerAristotelesTex.dispose();
        jokerMorganTex.dispose();
        fuenteContador.dispose();
        if (objetivosTex != null) objetivosTex.dispose();
        if (botonJugarTex != null) botonJugarTex.dispose();
        if (botonDescartarTex != null) botonDescartarTex.dispose();
        if (botonOpcionesTex != null) botonOpcionesTex.dispose();
        if (fondoInputTex != null) fondoInputTex.dispose();
        if (cursorTex != null) cursorTex.dispose();

        for (Texture tex : poolVariablesTex) tex.dispose();
        for (Texture tex : poolConectivosTex) tex.dispose();
    }
}