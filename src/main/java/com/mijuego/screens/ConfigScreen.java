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

    // Gestor de persistencia de datos
    private LeaderboardManager leaderboardManager;

    // Texturas de interfaz cargadas desde resources
    private Texture textureBackground;
    private Texture textureBannerConf;
    private Texture textureBtnMas;
    private Texture textureBtnMenos;
    private Texture textureBtnAdmin;
    private Texture textureBtnVolMenu;

    // Estado de la capa de visualización de administrador
    private boolean panelAdminAbierto = false;

    public ConfigScreen(final MainGame game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(game.VIRTUAL_WIDTH, game.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Inicializamos nuestro lector y escritor de archivos JSON
        this.leaderboardManager = new LeaderboardManager();

        loadTextures();
        createBasicSkin();

        // Contenedor principal de la UI
        this.rootTable = new Table();
        this.rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Reconstruimos la interfaz basándonos en el estado actual (Ajustes o Admin)
        rebuildUI();
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

        // Texturas básicas de respaldo para los cuadros de diálogo (Popups) y botones del panel
        Pixmap pixUp = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixUp.setColor(new Color(0.25f, 0.15f, 0.1f, 1f)); // Café oscuro
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

        // Estilo especial para botones de eliminación rápida en color rojo oscuro
        Pixmap pixDeleteUp = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixDeleteUp.setColor(new Color(0.5f, 0.1f, 0.1f, 1f));
        pixDeleteUp.fill();
        skin.add("btn_delete_up", new Texture(pixDeleteUp));

        TextButton.TextButtonStyle deleteBtnStyle = new TextButton.TextButtonStyle();
        deleteBtnStyle.up = skin.newDrawable("btn_delete_up");
        deleteBtnStyle.down = skin.newDrawable("btn_down");
        deleteBtnStyle.font = skin.getFont("default");
        deleteBtnStyle.fontColor = Color.WHITE;
        skin.add("delete_style", deleteBtnStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // Estilos para el texto destacado del panel administrador
        Label.LabelStyle adminTitleStyle = new Label.LabelStyle();
        adminTitleStyle.font = skin.getFont("default");
        adminTitleStyle.fontColor = Color.GREEN;
        skin.add("admin_title", adminTitleStyle);

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

        // --- REGISTRO DEL ESTILO DE VENTANA (OBLIGATORIO PARA EVITAR EL CRASH DEL DIALOG) ---
        Window.WindowStyle windowStyle = new Window.WindowStyle();

        // Generamos un fondo chocolate semitransparente oscuro y estilizado para el cuadro de diálogo
        Pixmap pixWindow = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixWindow.setColor(new Color(0.18f, 0.11f, 0.07f, 0.96f)); // Café oscuro de alta opacidad
        pixWindow.fill();
        Texture windowBgTex = new Texture(pixWindow);
        windowStyle.background = new TextureRegionDrawable(new TextureRegion(windowBgTex));
        pixWindow.dispose();

        windowStyle.titleFont = skin.getFont("default");
        windowStyle.titleFontColor = Color.GOLD;
        skin.add("default", windowStyle);
    }

    /**
     * Limpia la interfaz y decide qué pantalla construir en base a las credenciales de administrador
     */
    private void rebuildUI() {
        rootTable.clearChildren();

        if (panelAdminAbierto) {
            setupAdminUI();
        } else {
            setupNormalUI();
        }
    }

    /**
     * Construye los ajustes estándar del juego (Brillo, Volumen y Acceso Administrador)
     */
    private void setupNormalUI() {
        // 1. BANNER DE AJUSTES
        Image bannerConfImg = new Image(new TextureRegionDrawable(new TextureRegion(textureBannerConf)));
        rootTable.add(bannerConfImg).width(440).height(145).padTop(10).padBottom(25).row();

        Table settingsContainer = new Table();

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

    /**
     * Construye la consola de administración interactiva para eliminar registros y reiniciar podio
     */
    private void setupAdminUI() {
        // Título del Panel de Control
        Label titleLabel = new Label("CONSOLA DE ADMINISTRADOR", skin, "admin_title");
        titleLabel.setFontScale(1.8f);
        rootTable.add(titleLabel).padTop(10).padBottom(15).row();

        Label descLabel = new Label("Gestion de base de datos y eliminacion de puntuaciones locales:", skin);
        descLabel.setFontScale(1.1f);
        rootTable.add(descLabel).padBottom(20).row();

        // Tabla contenedora de la lista de jugadores
        Table scoreListTable = new Table();
        scoreListTable.pad(15);

        // Encabezados de tabla
        Label hPos = new Label("Puesto", skin);
        Label hName = new Label("Jugador", skin);
        Label hScore = new Label("Puntuacion", skin);
        Label hAction = new Label("Acciones", skin);

        hPos.setFontScale(1.2f); hName.setFontScale(1.2f); hScore.setFontScale(1.2f); hAction.setFontScale(1.2f);

        scoreListTable.add(hPos).padRight(30).left();
        scoreListTable.add(hName).width(180).padRight(30).left();
        scoreListTable.add(hScore).width(150).padRight(40).left();
        scoreListTable.add(hAction).center().row();

        // Línea divisoria ornamental
        Label divider = new Label("─────────────────────────────────────────────────────────────────", skin);
        scoreListTable.add(divider).colspan(4).padTop(5).padBottom(10).row();

        // Cargamos los registros reales del JSON
        final ArrayList<PlayerScore> scores = leaderboardManager.getScores();

        if (scores.isEmpty()) {
            Label lbEmpty = new Label("No hay registros de puntuaciones guardados en el sistema.", skin);
            scoreListTable.add(lbEmpty).colspan(4).pad(20).row();
        } else {
            for (int i = 0; i < scores.size(); i++) {
                final int index = i;
                PlayerScore playerScore = scores.get(i);

                Label lbPos = new Label((i + 1) + "º", skin);
                Label lbName = new Label(playerScore.getName(), skin);
                Label lbVal = new Label(String.format("%,d pts", playerScore.getScore()), skin);
                TextButton btnDelete = new TextButton("ELIMINAR", skin, "delete_style");

                lbPos.setFontScale(1.1f); lbName.setFontScale(1.1f); lbVal.setFontScale(1.1f);

                // Evento para remover el registro correspondiente
                btnDelete.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        eliminarRegistroPuntuacion(index);
                    }
                });

                scoreListTable.add(lbPos).padRight(30).left();
                scoreListTable.add(lbName).width(180).padRight(30).left();
                scoreListTable.add(lbVal).width(150).padRight(40).left();
                scoreListTable.add(btnDelete).width(110).height(32).padBottom(6).row();
            }
        }

        rootTable.add(scoreListTable).expandX().row();

        // Botones de acción general (Reiniciar datos / Salir del panel)
        Table adminActionsTable = new Table();

        TextButton btnReset = new TextButton("Restablecer Top 3", skin);
        TextButton btnExitAdmin = new TextButton("Salir Modo Admin", skin);

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

        adminActionsTable.add(btnReset).width(200).height(45).padRight(20);
        adminActionsTable.add(btnExitAdmin).width(200).height(45);

        rootTable.add(adminActionsTable).padTop(25).padBottom(15);
    }

    /**
     * Remueve la puntuación en el índice seleccionado y guarda de inmediato la lista limpia en el archivo JSON
     */
    private void eliminarRegistroPuntuacion(int index) {
        ArrayList<PlayerScore> scores = leaderboardManager.getScores();
        if (index >= 0 && index < scores.size()) {
            scores.remove(index);

            // Guardamos manualmente la lista truncada sobreescribiendo el JSON
            com.badlogic.gdx.utils.Json jsonHelper = new com.badlogic.gdx.utils.Json();
            jsonHelper.setOutputType(com.badlogic.gdx.utils.JsonWriter.OutputType.json);
            com.badlogic.gdx.files.FileHandle fileHandle = Gdx.files.local("leaderboard.json");
            fileHandle.writeString(jsonHelper.prettyPrint(scores), false);

            System.out.println("Registro eliminado en indice: " + index);

            // Reconstruimos la interfaz gráfica al instante
            rebuildUI();
        }
    }

    /**
     * Sobrescribe el almacenamiento para rellenarlo con las puntuaciones iniciales predeterminadas
     */
    private void restablecerValoresPorDefecto() {
        ArrayList<PlayerScore> defaultScores = new ArrayList<>();
        defaultScores.add(new PlayerScore("Aristoteles", 50000));
        defaultScores.add(new PlayerScore("Boole", 35000));
        defaultScores.add(new PlayerScore("Turing", 28000));

        com.badlogic.gdx.utils.Json jsonHelper = new com.badlogic.gdx.utils.Json();
        jsonHelper.setOutputType(com.badlogic.gdx.utils.JsonWriter.OutputType.json);
        com.badlogic.gdx.files.FileHandle fileHandle = Gdx.files.local("leaderboard.json");
        fileHandle.writeString(jsonHelper.prettyPrint(defaultScores), false);

        System.out.println("Leaderboard restaurado a valores por defecto.");
        rebuildUI();
    }

    /**
     * Muestra una ventana emergente modal requiriendo la contraseña de seguridad "123456"
     */
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
                // Verificación de clave de seguridad estricta para el Panel
                if ("123456".equals(input)) {
                    panelAdminAbierto = true;
                    rebuildUI(); // Redibuja el Stage mostrando la lista de gestión
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

        // 2. Pintamos la interfaz de usuario interactiva (Controles de ajustes o Panel de administración)
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

        // Liberar estrictamente de la GPU todas las texturas cargadas
        if (textureBackground != null) textureBackground.dispose();
        if (textureBannerConf != null) textureBannerConf.dispose();
        if (textureBtnMas != null) textureBtnMas.dispose();
        if (textureBtnMenos != null) textureBtnMenos.dispose();
        if (textureBtnAdmin != null) textureBtnAdmin.dispose();
        if (textureBtnVolMenu != null) textureBtnVolMenu.dispose();
    }
}