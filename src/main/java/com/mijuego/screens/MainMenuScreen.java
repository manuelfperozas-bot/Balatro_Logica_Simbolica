package com.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mijuego.MainGame;
import com.mijuego.data.LeaderboardManager;
import com.mijuego.data.PlayerScore;

import java.util.ArrayList;

public class MainMenuScreen implements Screen {

    private final MainGame game;
    private Stage stage;
    private Skin skin;

    // Gestor de persistencia de datos
    private LeaderboardManager leaderboardManager;

    // Texturas para los recursos visuales Pixel Art
    private Texture textureBackground;
    private Texture textureLogo;
    private Texture textureBannerTop;
    private Texture textureBtnInicio;
    private Texture textureBtnConfig;

    public MainMenuScreen(final MainGame game) {
        this.game = game;

        // El Stage gestiona todos los widgets de UI en la resolución virtual
        this.stage = new Stage(new FitViewport(game.VIRTUAL_WIDTH, game.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Inicializamos el gestor que procesa la lectura y escritura del JSON
        this.leaderboardManager = new LeaderboardManager();

        // Cargamos todas las texturas de la pantalla de inicio
        loadTextures();

        createBasicSkin(); // Genera estilos de respaldo para etiquetas y botones comunes
        setupUI();         // Distribuye los componentes usando tablas
    }

    private void loadTextures() {
        // Carga de archivos de recursos
        textureBackground = new Texture("fondo_mesa.png");
        textureLogo = new Texture("logo_juego.png");
        textureBannerTop = new Texture("banner_top.png");
        textureBtnInicio = new Texture("btn_inicio.png");
        textureBtnConfig = new Texture("btn_config.png");

        // FILTRO RETRO OBLIGATORIO: Evita bordes difusos y mantiene nitidez extrema
        textureBackground.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        textureLogo.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        textureBannerTop.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        textureBtnInicio.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        textureBtnConfig.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    }

    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // 1. LOGO PRINCIPAL DEL JUEGO
        Image logoImg = new Image(new TextureRegionDrawable(new TextureRegion(textureLogo)));
        mainTable.add(logoImg).width(440).height(220).padTop(15).padBottom(5).row();

        // 2. TABLA DEL LEADERBOARD CON EL BANNER Y LA PLACA DE PUNTUACIONES (Sin fondos cuadrados)
        Table leaderboardTable = new Table();

        // Banner Ornamental del "Top de Jugadores"
        Image bannerTopImg = new Image(new TextureRegionDrawable(new TextureRegion(textureBannerTop)));
        leaderboardTable.add(bannerTopImg).width(360).height(120).padBottom(15).row();

        // Sub-tabla transparente para alinear las puntuaciones de manera limpia en columnas
        Table scoreGrid = new Table();
        scoreGrid.pad(5, 10, 5, 10);

        // Obtenemos las puntuaciones reales ordenadas desde el archivo JSON
        ArrayList<PlayerScore> scores = leaderboardManager.getScores();

        // Fallbacks por seguridad para evitar excepciones si el archivo se daña o está vacío
        String name1 = scores.size() > 0 ? scores.get(0).getName() : "---";
        String val1 = scores.size() > 0 ? String.format("%,d pts", scores.get(0).getScore()) : "0 pts";

        String name2 = scores.size() > 1 ? scores.get(1).getName() : "---";
        String val2 = scores.size() > 1 ? String.format("%,d pts", scores.get(1).getScore()) : "0 pts";

        String name3 = scores.size() > 2 ? scores.get(2).getName() : "---";
        String val3 = scores.size() > 2 ? String.format("%,d pts", scores.get(2).getScore()) : "0 pts";

        // Componentes para el 1er Lugar (Oro)
        Label lbPos1 = new Label("1ro:", skin, "first_place");
        Label lbName1 = new Label(name1, skin, "first_place");
        Label lbScore1 = new Label(val1, skin, "first_place");

        // Componentes para el 2do Lugar (Plata)
        Label lbPos2 = new Label("2do:", skin, "second_place");
        Label lbName2 = new Label(name2, skin, "second_place");
        Label lbScore2 = new Label(val2, skin, "second_place");

        // Componentes para el 3er Lugar (Bronce)
        Label lbPos3 = new Label("3ro:", skin, "third_place");
        Label lbName3 = new Label(name3, skin, "third_place");
        Label lbScore3 = new Label(val3, skin, "third_place");

        // Aplicamos el escalado de fuente homogéneo
        float scale = 1.3f;
        lbPos1.setFontScale(scale); lbName1.setFontScale(scale); lbScore1.setFontScale(scale);
        lbPos2.setFontScale(scale); lbName2.setFontScale(scale); lbScore2.setFontScale(scale);
        lbPos3.setFontScale(scale); lbName3.setFontScale(scale); lbScore3.setFontScale(scale);

        // Añadimos la primera fila alineando posición, nombre y puntaje
        scoreGrid.add(lbPos1).left().padRight(20);
        scoreGrid.add(lbName1).left().width(200).padRight(30);
        scoreGrid.add(lbScore1).right().row();

        // Añadimos la segunda fila con espaciado vertical
        scoreGrid.add(lbPos2).left().padRight(20).padTop(10);
        scoreGrid.add(lbName2).left().width(200).padRight(30).padTop(10);
        scoreGrid.add(lbScore2).right().padTop(10).row();

        // Añadimos la tercera fila
        scoreGrid.add(lbPos3).left().padRight(20).padTop(10);
        scoreGrid.add(lbName3).left().width(200).padRight(30).padTop(10);
        scoreGrid.add(lbScore3).right().padTop(10).row();

        // Acoplamos la rejilla de puntajes alineada al leaderboard
        leaderboardTable.add(scoreGrid).width(380).row();
        mainTable.add(leaderboardTable).expand().center().row();

        // 3. BOTONES DE ACCIÓN PIXEL ART (Abajo centrados horizontales)
        Table buttonTable = new Table();

        TextureRegionDrawable drawableInicio = new TextureRegionDrawable(new TextureRegion(textureBtnInicio));
        TextureRegionDrawable drawableConfig = new TextureRegionDrawable(new TextureRegion(textureBtnConfig));

        ImageButton btnPlay = new ImageButton(drawableInicio);
        ImageButton btnConfig = new ImageButton(drawableConfig);

        // Gestión de transiciones y eventos
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Cargando la mesa de juego...");
                game.setScreen(new GameplayScreen(game));
                // game.setScreen(new GameScreen(game));
            }
        });

        btnConfig.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Abriendo configuraciones...");
                game.setScreen(new ConfigScreen(game));
            }
        });

        // Posicionamiento estético de botones inferiores
        buttonTable.add(btnPlay).width(240).height(120).padRight(25);
        buttonTable.add(btnConfig).width(240).height(120);

        mainTable.add(buttonTable).expandX().padBottom(25);
    }

    private void createBasicSkin() {
        skin = new Skin();
        skin.add("default", new BitmapFont());

        // Estilos para el texto de los tres primeros lugares con colores contrastados sobre el fondo
        // Oro brillante para primer lugar
        Label.LabelStyle firstStyle = new Label.LabelStyle();
        firstStyle.font = skin.getFont("default");
        firstStyle.fontColor = new Color(0.95f, 0.82f, 0.23f, 1f);

        // Plata para segundo lugar
        Label.LabelStyle secondStyle = new Label.LabelStyle();
        secondStyle.font = skin.getFont("default");
        secondStyle.fontColor = new Color(0.85f, 0.85f, 0.85f, 1f);

        // Bronce para tercer lugar
        Label.LabelStyle thirdStyle = new Label.LabelStyle();
        thirdStyle.font = skin.getFont("default");
        thirdStyle.fontColor = new Color(0.80f, 0.58f, 0.42f, 1f);

        skin.add("first_place", firstStyle);
        skin.add("second_place", secondStyle);
        skin.add("third_place", thirdStyle);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Limpiamos los buffers de pantalla
        ScreenUtils.clear(0, 0, 0, 1);

        // Dibujamos primero la textura de fondo escalada sobre el lote del Stage
        stage.getBatch().begin();
        stage.getBatch().draw(textureBackground, 0, 0, game.VIRTUAL_WIDTH, game.VIRTUAL_HEIGHT);
        stage.getBatch().end();

        // Actualizamos y pintamos los widgets que están por encima
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();

        // Liberar de manera estricta todos los buffers gráficos de las texturas
        if (textureBackground != null) textureBackground.dispose();
        if (textureLogo != null) textureLogo.dispose();
        if (textureBannerTop != null) textureBannerTop.dispose();
        if (textureBtnInicio != null) textureBtnInicio.dispose();
        if (textureBtnConfig != null) textureBtnConfig.dispose();
    }
}