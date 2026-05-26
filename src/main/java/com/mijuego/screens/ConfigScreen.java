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
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mijuego.MainGame;
import com.mijuego.data.LeaderboardManager;
import com.mijuego.data.PlayerScore;

import java.util.ArrayList;

public class ConfigScreen implements Screen {

    private final MainGame game;
    private Stage stage;
    private Skin skin;
    private Table rootTable;

    private LeaderboardManager leaderboardManager;

    private Texture textureBackground;
    private Texture textureBannerConf;
    private Texture textureBtnMas;
    private Texture textureBtnMenos;
    private Texture textureBtnAdmin;
    private Texture textureBtnVolMenu;

    private Texture textureBannerConsolaAdmin;

    private boolean panelAdminAbierto = false;

    public ConfigScreen(final MainGame game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(game.VIRTUAL_WIDTH, game.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        this.leaderboardManager = new LeaderboardManager();

        loadTextures();
        createBasicSkin();

        this.rootTable = new Table();
        this.rootTable.setFillParent(true);
        stage.addActor(rootTable);

        rebuildUI();
    }

    private Texture loadTextureSafe(String internalPath) {
        if (Gdx.files.internal(internalPath).exists()) {
            Texture texture = new Texture(internalPath);
            texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
            return texture;
        } else {
            Gdx.app.error("Assets", "¡ATENCION! Falta la textura: " + internalPath);
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(new Color(1f, 0f, 0f, 0.5f));
            pixmap.fill();
            Texture fallbackTexture = new Texture(pixmap);
            pixmap.dispose();
            return fallbackTexture;
        }
    }

    private void loadTextures() {
        textureBackground = loadTextureSafe("fondo_mesa.png");
        textureBannerConf = loadTextureSafe("banner_AjusConf.png");
        textureBtnMas = loadTextureSafe("boton_mas.png");
        textureBtnMenos = loadTextureSafe("boton_menos.png");
        textureBtnAdmin = loadTextureSafe("boton_Admin.png");
        textureBtnVolMenu = loadTextureSafe("boton_VolMenu.png");

        textureBannerConsolaAdmin = loadTextureSafe("banner_ConsolaAdmin.png");
    }

    private void createBasicSkin() {
        skin = new Skin();
        skin.add("default", new BitmapFont());

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

        Label.LabelStyle adminTitleStyle = new Label.LabelStyle();
        adminTitleStyle.font = skin.getFont("default");
        adminTitleStyle.fontColor = Color.GREEN;
        skin.add("admin_title", adminTitleStyle);

        TextField.TextFieldStyle txtStyle = new TextField.TextFieldStyle();
        txtStyle.font = skin.getFont("default");
        txtStyle.fontColor = Color.BLACK;
        txtStyle.background = skin.newDrawable("white_bg");

        Pixmap pixCursor = new Pixmap(2, 16, Pixmap.Format.RGBA8888);
        pixCursor.setColor(Color.BLACK);
        pixCursor.fill();
        Texture cursorTex = new Texture(pixCursor);
        txtStyle.cursor = new TextureRegionDrawable(new TextureRegion(cursorTex));
        pixCursor.dispose();

        skin.add("default", txtStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        Pixmap pixWindow = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixWindow.setColor(new Color(0.18f, 0.11f, 0.07f, 0.96f));
        pixWindow.fill();
        Texture windowBgTex = new Texture(pixWindow);
        windowStyle.background = new TextureRegionDrawable(new TextureRegion(windowBgTex));
        pixWindow.dispose();

        windowStyle.titleFont = skin.getFont("default");
        windowStyle.titleFontColor = Color.GOLD;
        skin.add("default", windowStyle);
    }

    private void rebuildUI() {
        rootTable.clearChildren();

        if (panelAdminAbierto) {
            setupAdminUI();
        } else {
            setupNormalUI();
        }
    }

    private void setupNormalUI() {
        Image bannerConfImg = new Image(new TextureRegionDrawable(new TextureRegion(textureBannerConf)));
        rootTable.add(bannerConfImg).width(440).height(145).padTop(10).padBottom(25).row();

        Table settingsContainer = new Table();

        TextureRegionDrawable drawableMas = new TextureRegionDrawable(new TextureRegion(textureBtnMas));
        TextureRegionDrawable drawableMenos = new TextureRegionDrawable(new TextureRegion(textureBtnMenos));
        TextureRegionDrawable drawableAdmin = new TextureRegionDrawable(new TextureRegion(textureBtnAdmin));
        TextureRegionDrawable drawableVolMenu = new TextureRegionDrawable(new TextureRegion(textureBtnVolMenu));

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

        ImageButton btnAdmin = new ImageButton(drawableAdmin);
        btnAdmin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                solicitarClaveAcceso();
            }
        });
        settingsContainer.add(btnAdmin).width(240).height(120).padBottom(20).row();

        rootTable.add(settingsContainer).row();

        ImageButton btnVolver = new ImageButton(drawableVolMenu);
        btnVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        rootTable.add(btnVolver).width(240).height(120).padTop(5);
    }

    private void setupAdminUI() {
        Image bannerConsolaImg = new Image(new TextureRegionDrawable(new TextureRegion(textureBannerConsolaAdmin)));
        rootTable.add(bannerConsolaImg).width(440).height(145).padTop(10).padBottom(30).row();

        Table scoreListTable = new Table();
        scoreListTable.pad(10);

        Label hPos = new Label("Puesto", skin);
        Label hName = new Label("Jugador", skin);
        Label hScore = new Label("Puntuacion", skin);
        Label hAction = new Label("Accion", skin);

        hPos.setFontScale(1.15f);
        hName.setFontScale(1.15f);
        hScore.setFontScale(1.15f);
        hAction.setFontScale(1.15f);

        scoreListTable.add(hPos).padRight(20).left();
        scoreListTable.add(hName).width(150).padRight(20).left();
        scoreListTable.add(hScore).width(100).padRight(20).left();
        scoreListTable.add(hAction).left().row();

        Label divider = new Label("──────────────────────────────────────────────────────────────────", skin);
        scoreListTable.add(divider).colspan(4).padTop(3).padBottom(8).row();

        final ArrayList<PlayerScore> scores = leaderboardManager.getScores();

        if (scores.isEmpty()) {
            Label lbEmpty = new Label("No hay registros.", skin);
            scoreListTable.add(lbEmpty).colspan(4).pad(15).row();
        } else {
            int registrosAMostrar = Math.min(scores.size(), 5);
            for (int i = 0; i < registrosAMostrar; i++) {
                final PlayerScore playerScore = scores.get(i);

                Label lbPos = new Label((i + 1) + "º", skin);
                Label lbName = new Label(playerScore.getName(), skin);
                Label lbVal = new Label(String.format("%,d", playerScore.getScore()), skin);

                TextButton btnDelete = new TextButton("Eliminar", skin);
                btnDelete.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        eliminarJugador(playerScore);
                    }
                });

                scoreListTable.add(lbPos).padRight(20).left();
                scoreListTable.add(lbName).width(150).padRight(20).left();
                scoreListTable.add(lbVal).width(100).padRight(20).left();
                scoreListTable.add(btnDelete).width(80).height(30).row();
            }
        }

        rootTable.add(scoreListTable).expandX().row();

        Table adminActionsTable = new Table();
        TextButton btnReset = new TextButton("Restablecer Top", skin);
        TextButton btnExitAdmin = new TextButton("Salir", skin);

        btnReset.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                restablecerValoresPorDefecto();
            }
        });

        btnExitAdmin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                panelAdminAbierto = false;
                rebuildUI();
            }
        });

        adminActionsTable.add(btnReset).width(180).height(42).padRight(20);
        adminActionsTable.add(btnExitAdmin).width(180).height(42);

        rootTable.add(adminActionsTable).padTop(15).padBottom(10);
    }

    private void eliminarJugador(PlayerScore score) {
        ArrayList<PlayerScore> currentScores = leaderboardManager.getScores();
        // Buscamos el índice exacto para asegurar la eliminación
        for (int i = 0; i < currentScores.size(); i++) {
            if (currentScores.get(i).getName().equals(score.getName()) &&
                    currentScores.get(i).getScore() == score.getScore()) {
                currentScores.remove(i);
                break;
            }
        }

        com.badlogic.gdx.utils.Json jsonHelper = new com.badlogic.gdx.utils.Json();
        jsonHelper.setOutputType(com.badlogic.gdx.utils.JsonWriter.OutputType.json);
        com.badlogic.gdx.files.FileHandle fileHandle = Gdx.files.local("leaderboard.json");
        fileHandle.writeString(jsonHelper.prettyPrint(currentScores), false);

        rebuildUI();
    }

    private void restablecerValoresPorDefecto() {
        ArrayList<PlayerScore> defaultScores = new ArrayList<>();
        defaultScores.add(new PlayerScore("Aristoteles", 50000));
        defaultScores.add(new PlayerScore("Boole", 35000));
        defaultScores.add(new PlayerScore("Turing", 28000));

        com.badlogic.gdx.utils.Json jsonHelper = new com.badlogic.gdx.utils.Json();
        jsonHelper.setOutputType(com.badlogic.gdx.utils.JsonWriter.OutputType.json);
        com.badlogic.gdx.files.FileHandle fileHandle = Gdx.files.local("leaderboard.json");
        fileHandle.writeString(jsonHelper.prettyPrint(defaultScores), false);

        rebuildUI();
    }

    private void solicitarClaveAcceso() {
        final Dialog dialog = new Dialog("ACCESO RESTRINGIDO", skin) {
            @Override
            protected void result(Object object) {
            }
        };

        Table dialogTable = new Table();
        dialogTable.pad(15);
        dialogTable.add(new Label("Ingrese Contrasena:", skin)).padBottom(15).row();

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
                if ("123456".equals(pfPass.getText())) {
                    panelAdminAbierto = true;
                    rebuildUI();
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
        stage.setKeyboardFocus(pfPass);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.getBatch().begin();
        stage.getBatch().draw(textureBackground, 0, 0, game.VIRTUAL_WIDTH, game.VIRTUAL_HEIGHT);
        stage.getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (textureBackground != null) textureBackground.dispose();
        if (textureBannerConf != null) textureBannerConf.dispose();
        if (textureBtnMas != null) textureBtnMas.dispose();
        if (textureBtnMenos != null) textureBtnMenos.dispose();
        if (textureBtnAdmin != null) textureBtnAdmin.dispose();
        if (textureBtnVolMenu != null) textureBtnVolMenu.dispose();
        if (textureBannerConsolaAdmin != null) textureBannerConsolaAdmin.dispose();
    }
}