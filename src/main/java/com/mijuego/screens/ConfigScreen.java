package com.mijuego.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mijuego.MainGame;

public class ConfigScreen implements Screen {

    private final MainGame game;
    private Stage stage;
    private Skin skin;
    private ShapeRenderer shapeRenderer; // Se usa para dibujar el fondo del panel de administrador

    // Texturas de interfaz cargadas desde resources
    private Texture textureBackground;
    private Texture textureBannerConf;
    private Texture textureBtnMas;
    private Texture textureBtnMenos;
    private Texture textureBtnAdmin;
    private Texture textureBtnVolMenu;

    // Estado de la capa de visualización de administrador
    private boolean panelAdminAbierto = false;
    private String adminMensajeInfo = "";

    public ConfigScreen(final MainGame game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(game.VIRTUAL_WIDTH, game.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        this.shapeRenderer = new ShapeRenderer();

        loadTextures();
        createBasicSkin();
        setupUI();
    }

    private void loadTextures() {
        // Carga de imágenes desde resources
        textureBackground = new Texture("fondo_mesa.png");
        textureBannerConf = new Texture("banner_AjusConf.png");
        textureBtnMas = new Texture("boton_mas.png");
        textureBtnMenos = new Texture("boton_menos.png");
        textureBtnAdmin = new Texture("boton_Admin.png");
        textureBtnVolMenu = new Texture("boton_VolMenu.png");

        // FILTRO RETRO: Crucial para mantener los píxeles afilados y limpios sin borrosidad
        textureBackground.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        textureBannerConf.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        textureBtnMas.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        textureBtnMenos.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        textureBtnAdmin.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        textureBtnVolMenu.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    }

    private void createBasicSkin() {
        skin = new Skin();
        skin.add("default", new BitmapFont());

        // Texturas básicas de respaldo para los cuadros de diálogo (Popups)
        Pixmap pixUp = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixUp.setColor(new Color(0.25f, 0.15f, 0.1f, 1f));
        pixUp.fill();
        skin.add("btn_up", new Texture(pixUp));

        Pixmap pixDown = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixDown.setColor(new Color(0.4f, 0.25f, 0.15f, 1f));
        pixDown.fill();
        skin.add("btn_down", new Texture(pixDown));

        Pixmap pixWhite = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixWhite.setColor(Color.WHITE);
        pixWhite.fill();
        skin.add("white_bg", new Texture(pixWhite));

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = skin.newDrawable("btn_up");
        btnStyle.down = skin.newDrawable("btn_down");
        btnStyle.font = skin.getFont("default");
        btnStyle.fontColor = Color.GOLD;
        skin.add("default", btnStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        TextField.TextFieldStyle txtStyle = new TextField.TextFieldStyle();
        txtStyle.font = skin.getFont("default");
        txtStyle.fontColor = Color.BLACK;
        txtStyle.background = skin.newDrawable("white_bg");

        // Cursor básico para el campo de texto
        Pixmap pixCursor = new Pixmap(2, 16, Pixmap.Format.RGBA8888);
        pixCursor.setColor(Color.BLACK);
        pixCursor.fill();
        Texture cursorTex = new Texture(pixCursor);
        txtStyle.cursor = new TextureRegionDrawable(new TextureRegion(cursorTex));
        pixCursor.dispose();

        skin.add("default", txtStyle);
    }

    private void setupUI() {
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // 1. BANNER DE AJUSTES (Reemplaza el texto plano del título)
        Image bannerConfImg = new Image(new TextureRegionDrawable(new TextureRegion(textureBannerConf)));
        rootTable.add(bannerConfImg).width(440).height(145).padTop(10).padBottom(25).row();

        // Contenedor de configuraciones
        Table settingsContainer = new Table();

        // Generamos los drawables de tus texturas pixel art
        TextureRegionDrawable drawableMas = new TextureRegionDrawable(new TextureRegion(textureBtnMas));
        TextureRegionDrawable drawableMenos = new TextureRegionDrawable(new TextureRegion(textureBtnMenos));
        TextureRegionDrawable drawableAdmin = new TextureRegionDrawable(new TextureRegion(textureBtnAdmin));
        TextureRegionDrawable drawableVolMenu = new TextureRegionDrawable(new TextureRegion(textureBtnVolMenu));

        // --- FILA 1: CONTROL DE BRILLO ---
        final Label lbBrilloVal = new Label("Brillo: " + (game.getBrilloNivel() * 10) + "%", skin);
        lbBrilloVal.setFontScale(1.3f);
        ImageButton btnBrilloMenos = new ImageButton(drawableMenos);
        ImageButton btnBrilloMas = new ImageButton(drawableMas);

        btnBrilloMenos.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int brillo = game.getBrilloNivel();
                if (brillo > 2) {
                    game.setBrilloNivel(brillo - 1);
                    lbBrilloVal.setText("Brillo: " + (game.getBrilloNivel() * 10) + "%");
                }
            }
        });

        btnBrilloMas.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int brillo = game.getBrilloNivel();
                if (brillo < 10) {
                    game.setBrilloNivel(brillo + 1);
                    lbBrilloVal.setText("Brillo: " + (game.getBrilloNivel() * 10) + "%");
                }
            }
        });

        Table brilloRow = new Table();
        brilloRow.add(lbBrilloVal).width(160).left();
        brilloRow.add(btnBrilloMenos).width(50).height(50).padRight(15);
        brilloRow.add(btnBrilloMas).width(50).height(50);
        settingsContainer.add(brilloRow).padBottom(20).row();

        // --- FILA 2: CONTROL DE VOLUMEN ---
        final Label lbVolumenVal = new Label("Volumen: " + (game.getVolumenNivel() * 10) + "%", skin);
        lbVolumenVal.setFontScale(1.3f);
        ImageButton btnVolumenMenos = new ImageButton(drawableMenos);
        ImageButton btnVolumenMas = new ImageButton(drawableMas);

        btnVolumenMenos.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int volumen = game.getVolumenNivel();
                if (volumen > 0) {
                    game.setVolumenNivel(volumen - 1);
                    lbVolumenVal.setText("Volumen: " + (game.getVolumenNivel() * 10) + "%");
                }
            }
        });

        btnVolumenMas.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int volumen = game.getVolumenNivel();
                if (volumen < 10) {
                    game.setVolumenNivel(volumen + 1);
                    lbVolumenVal.setText("Volumen: " + (game.getVolumenNivel() * 10) + "%");
                }
            }
        });

        Table volumenRow = new Table();
        volumenRow.add(lbVolumenVal).width(160).left();
        volumenRow.add(btnVolumenMenos).width(50).height(50).padRight(15);
        volumenRow.add(btnVolumenMas).width(50).height(50);
        settingsContainer.add(volumenRow).padBottom(25).row();

        // --- FILA 3: BOTÓN DE ADMINISTRACIÓN ---
        ImageButton btnAdmin = new ImageButton(drawableAdmin);
        btnAdmin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                solicitarClaveAcceso();
            }
        });
        settingsContainer.add(btnAdmin).width(240).height(120).padBottom(20).row();

        rootTable.add(settingsContainer).row();

        // --- BOTÓN DE VOLVER AL MENÚ ---
        ImageButton btnVolver = new ImageButton(drawableVolMenu);
        btnVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        rootTable.add(btnVolver).width(240).height(120).padTop(5);
    }

    private void solicitarClaveAcceso() {
        final Dialog dialog = new Dialog("ACCESO RESTRINGIDO", skin) {
            @Override
            protected void result(Object object) {}
        };

        Table dialogTable = new Table();
        dialogTable.pad(15);
        dialogTable.add(new Label("Ingrese Contrasena de Administrador:", skin)).padBottom(15).row();

        final TextField pfPass = new TextField("", skin);
        pfPass.setPasswordMode(true);
        pfPass.setPasswordCharacter('*');
        dialogTable.add(pfPass).width(250).height(35).padBottom(15).row();

        final Label lbError = new Label("", skin);
        lbError.setColor(Color.RED);
        dialogTable.add(lbError).padBottom(10).row();

        Table dialogButtons = new Table();
        TextButton btnOk = new TextButton("Aceptar", skin);
        TextButton btnCancel = new TextButton("Cancelar", skin);

        btnOk.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String input = pfPass.getText();
                if ("admin".equalsIgnoreCase(input) || "1234".equals(input)) {
                    panelAdminAbierto = true;
                    adminMensajeInfo = "MODO ADMINISTRADOR ACTIVADO\n\nControles de depuracion:\n- Mesa de juego desbloqueada\n- Respuestas correctas visibles\n- Restablecer historico de Top 3";
                    dialog.hide();
                } else {
                    lbError.setText("Clave incorrecta!");
                }
            }
        });

        btnCancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialogButtons.add(btnOk).width(100).height(35).padRight(15);
        dialogButtons.add(btnCancel).width(100).height(35);
        dialogTable.add(dialogButtons);

        dialog.getContentTable().add(dialogTable);
        dialog.show(stage);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // 1. Dibujamos la mesa de fondo
        stage.getBatch().begin();
        stage.getBatch().draw(textureBackground, 0, 0, game.VIRTUAL_WIDTH, game.VIRTUAL_HEIGHT);
        stage.getBatch().end();

        // 2. Pintamos la interfaz de usuario interactiva
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // 3. Panel Consola de Administrador (Capa local para el panel verde)
        if (panelAdminAbierto) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0.05f, 0.15f, 0.05f, 0.92f));
            shapeRenderer.rect(200, 150, 880, 420);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            stage.getBatch().begin();
            skin.getFont("default").draw(stage.getBatch(), adminMensajeInfo, 230, 500);
            skin.getFont("default").draw(stage.getBatch(), "[PRESIONE ESC PARA SALIR DEL PANEL]", 480, 200);
            stage.getBatch().end();

            if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
                panelAdminAbierto = false;
            }
        }
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
        shapeRenderer.dispose();

        // Liberar estrictamente de la GPU todas las texturas cargadas
        if (textureBackground != null) textureBackground.dispose();
        if (textureBannerConf != null) textureBannerConf.dispose();
        if (textureBtnMas != null) textureBtnMas.dispose();
        if (textureBtnMenos != null) textureBtnMenos.dispose();
        if (textureBtnAdmin != null) textureBtnAdmin.dispose();
        if (textureBtnVolMenu != null) textureBtnVolMenu.dispose();
    }
}