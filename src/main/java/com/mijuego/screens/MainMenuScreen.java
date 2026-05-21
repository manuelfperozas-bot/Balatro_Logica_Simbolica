package com.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mijuego.MainGame;

public class MainMenuScreen implements Screen {

    private final MainGame game;
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(final MainGame game) {
        this.game = game;

        // Al pasarle solo el Viewport, el Stage crea su propio Batch internamente.
        // Esto evita el error de referencia con game.batch.
        this.stage = new Stage(new FitViewport(game.VIRTUAL_WIDTH, game.VIRTUAL_HEIGHT));

        // Gdx.input.setInputProcessor le dice al juego que escuche los clics del mouse en esta pantalla
        Gdx.input.setInputProcessor(stage);

        createBasicSkin(); // Crea estilos temporales para los botones y textos
        setupUI();         // Estructura la pantalla
    }

    private void setupUI() {
        // Table es la herramienta principal de LibGDX para alinear cosas en pantalla (como una tabla HTML)
        Table mainTable = new Table();
        mainTable.setFillParent(true); // Ocupa toda la pantalla
        stage.addActor(mainTable);

        // 1. TÍTULO DEL JUEGO (Arriba al centro)
        Label titleLabel = new Label("BALATRO: LOGICA SIMBOLICA", skin);
        titleLabel.setFontScale(2.5f); // Hacemos el título más grande
        mainTable.add(titleLabel).expandX().padTop(50).row();

        // 2. TABLA DEL TOP 3 (Centro)
        Table leaderboardTable = new Table();
        leaderboardTable.add(new Label("--- TOP 3 JUGADORES ---", skin)).padBottom(20).row();
        leaderboardTable.add(new Label("1. Aristoteles - 50,000", skin)).padBottom(10).row();
        leaderboardTable.add(new Label("2. Boole - 35,000", skin)).padBottom(10).row();
        leaderboardTable.add(new Label("3. Turing - 28,000", skin)).padBottom(30).row();

        // Añadimos botón para ver el top completo
        TextButton btnFullTop = new TextButton("Ver Top Completo", skin);
        leaderboardTable.add(btnFullTop).padBottom(50);

        mainTable.add(leaderboardTable).expand().center().row();

        // 3. BOTONES INFERIORES (Abajo centrados)
        Table buttonTable = new Table();
        TextButton btnPlay = new TextButton("INICIAR JUEGO", skin);
        TextButton btnConfig = new TextButton("CONFIGURACION", skin);

        // Añadir eventos a los botones
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Cargando la mesa de juego...");
                // Aquí cambiaremos a la GameScreen más adelante
                // game.setScreen(new GameScreen(game));
            }
        });

        btnConfig.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Abriendo configuraciones...");
            }
        });

        buttonTable.add(btnPlay).width(300).height(60).padBottom(20).row();
        buttonTable.add(btnConfig).width(300).height(60);

        mainTable.add(buttonTable).expandX().padBottom(50);
    }

    // --- Método temporal para generar botones sin necesitar archivos de imagen externos ---
    private void createBasicSkin() {
        skin = new Skin();
        skin.add("default", new BitmapFont());

        // Textura gris oscura para el botón normal
        Pixmap pixmapUp = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapUp.setColor(Color.DARK_GRAY);
        pixmapUp.fill();
        skin.add("button_up", new Texture(pixmapUp));

        // Textura gris clara para cuando presionas el botón
        Pixmap pixmapDown = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapDown.setColor(Color.LIGHT_GRAY);
        pixmapDown.fill();
        skin.add("button_down", new Texture(pixmapDown));

        // Configurar estilos
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.newDrawable("button_up");
        buttonStyle.down = skin.newDrawable("button_down");
        buttonStyle.font = skin.getFont("default");
        skin.add("default", buttonStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Fondo color morado oscuro (estilo noche/cartas)
        ScreenUtils.clear(0.15f, 0.1f, 0.2f, 1);

        // Actualizamos y dibujamos el Stage (UI)
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
    }
}