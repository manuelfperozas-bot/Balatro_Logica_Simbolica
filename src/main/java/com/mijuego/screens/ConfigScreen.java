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
    private ShapeRenderer shapeRenderer;

    // Texturas
    private Texture textureBackground;

    // Variables de configuración de estado estático global (Valores de 0 a 10)
    private static int brilloNivel = 10; // 10 = Brillo Máximo
    private static int volumenNivel = 7;

    // Estado de la capa de visualización
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

    // Métodos estáticos públicos para que cualquier pantalla pueda leer los ajustes actuales
    public static int getBrilloNivel() {
        return brilloNivel;
    }

    public static int getVolumenNivel() {
        return volumenNivel;
    }

    private void loadTextures() {
        textureBackground = new Texture("fondo_mesa.png");
        textureBackground.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    }

    private void createBasicSkin() {
        skin = new Skin();
        skin.add("default", new BitmapFont());

        // Textura gris oscuro para el fondo de botones
        Pixmap pixUp = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixUp.setColor(new Color(0.25f, 0.15f, 0.1f, 1f)); // Café oscuro
        pixUp.fill();
        skin.add("btn_up", new Texture(pixUp));

        // Textura gris claro para cuando se presiona un botón
        Pixmap pixDown = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixDown.setColor(new Color(0.4f, 0.25f, 0.15f, 1f));
        pixDown.fill();
        skin.add("btn_down", new Texture(pixDown));

        // Textura blanca para cajas de texto de inputs
        Pixmap pixWhite = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixWhite.setColor(Color.WHITE);
        pixWhite.fill();
        skin.add("white_bg", new Texture(pixWhite));

        // Estilos para botones
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.up = skin.newDrawable("btn_up");
        btnStyle.down = skin.newDrawable("btn_down");
        btnStyle.font = skin.getFont("default");
        btnStyle.fontColor = Color.GOLD;
        skin.add("default", btnStyle);

        // Estilos para etiquetas
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // Estilo específico para panel de administrador
        Label.LabelStyle adminStyle = new Label.LabelStyle();
        adminStyle.font = skin.getFont("default");
        adminStyle.fontColor = Color.GREEN;
        skin.add("admin_style", adminStyle);

        // Estilos para campos de texto (Clave de administración)
        TextField.TextFieldStyle txtStyle = new TextField.TextFieldStyle();
        txtStyle.font = skin.getFont("default");
        txtStyle.fontColor = Color.BLACK;
        txtStyle.background = skin.newDrawable("white_bg");

        // Cursor básico corregido y libre de fugas de memoria
        Pixmap pixCursor = new Pixmap(2, 16, Pixmap.Format.RGBA8888);
        pixCursor.setColor(Color.BLACK);
        pixCursor.fill();
        Texture cursorTex = new Texture(pixCursor);
        txtStyle.cursor = new TextureRegionDrawable(new TextureRegion(cursorTex));
        pixCursor.dispose(); // Liberación de memoria CPU obligatoria una vez subido a GPU

        skin.add("default", txtStyle);
    }

    private void setupUI() {
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Título de la sección
        Label titleLabel = new Label("AJUSTES DE CONFIGURACION", skin);
        titleLabel.setFontScale(1.8f);
        rootTable.add(titleLabel).padBottom(40).row();

        // Contenedor descendente de ajustes
        Table settingsContainer = new Table();

        // --- FILA 1: CONTROL DE BRILLO ---
        final Label lbBrilloVal = new Label("Brillo: " + (brilloNivel * 10) + "%", skin);
        lbBrilloVal.setFontScale(1.2f);
        TextButton btnBrilloMenos = new TextButton("-", skin);
        TextButton btnBrilloMas = new TextButton("+", skin);

        btnBrilloMenos.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (brilloNivel > 2) { // Evita oscuridad total (mínimo 20%)
                    brilloNivel--;
                    lbBrilloVal.setText("Brillo: " + (brilloNivel * 10) + "%");
                }
            }
        });

        btnBrilloMas.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (brilloNivel < 10) {
                    brilloNivel++;
                    lbBrilloVal.setText("Brillo: " + (brilloNivel * 10) + "%");
                }
            }
        });

        Table brilloRow = new Table();
        brilloRow.add(lbBrilloVal).width(150).left();
        brilloRow.add(btnBrilloMenos).width(50).height(40).padRight(10);
        brilloRow.add(btnBrilloMas).width(50).height(40);
        settingsContainer.add(brilloRow).padBottom(25).row();

        // --- FILA 2: CONTROL DE VOLUMEN ---
        final Label lbVolumenVal = new Label("Volumen: " + (volumenNivel * 10) + "%", skin);
        lbVolumenVal.setFontScale(1.2f);
        TextButton btnVolumenMenos = new TextButton("-", skin);
        TextButton btnVolumenMas = new TextButton("+", skin);

        btnVolumenMenos.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (volumenNivel > 0) {
                    volumenNivel--;
                    lbVolumenVal.setText("Volumen: " + (volumenNivel * 10) + "%");
                }
            }
        });

        btnVolumenMas.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (volumenNivel < 10) {
                    volumenNivel++;
                    lbVolumenVal.setText("Volumen: " + (volumenNivel * 10) + "%");
                }
            }
        });

        Table volumenRow = new Table();
        volumenRow.add(lbVolumenVal).width(150).left();
        volumenRow.add(btnVolumenMenos).width(50).height(40).padRight(10);
        volumenRow.add(btnVolumenMas).width(50).height(40);
        settingsContainer.add(volumenRow).padBottom(25).row();

        // --- FILA 3: BOTÓN DE ADMINISTRACIÓN ---
        TextButton btnAdmin = new TextButton("ADMINISTRACION", skin);
        btnAdmin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                solicitarClaveAcceso();
            }
        });
        settingsContainer.add(btnAdmin).width(260).height(50).padBottom(40).row();

        rootTable.add(settingsContainer).row();

        // Botón para regresar al menú principal
        TextButton btnVolver = new TextButton("VOLVER AL MENU", skin);
        btnVolver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        rootTable.add(btnVolver).width(200).height(45).padTop(10);
    }

    /**
     * Muestra una ventana emergente modal requiriendo contraseña
     */
    private void solicitarClaveAcceso() {
        final Dialog dialog = new Dialog("ACCESO RESTRINGIDO", skin) {
            @Override
            protected void result(Object object) {
                // Sobrescribimos el click para manejarlo de manera sutil e inline
            }
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
                // Clave por defecto para el juego de Lógica Simbólica
                if ("admin".equalsIgnoreCase(input) || "1234".equals(input)) {
                    panelAdminAbierto = true;
                    adminMensajeInfo = "MODO ADMINISTRADOR ACTIVADO\n\nControles de depuracion:\n- Mesa desbloqueada\n- Respuestas correctas visibles\n- Restablecer Top 3";
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

        // 1. Dibujamos la textura de fondo mesa
        stage.getBatch().begin();
        stage.getBatch().draw(textureBackground, 0, 0, game.VIRTUAL_WIDTH, game.VIRTUAL_HEIGHT);
        stage.getBatch().end();

        // 2. Dibujamos la interfaz interactiva
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // 3. CAPA DE BRILLO (Manejo con Shader por transparencia)
        if (brilloNivel < 10) {
            // Calcula opacidad de oscuridad (0.0 = Brillo al máximo, 0.8 = Brillo al mínimo/pantalla oscura)
            float factorOscuridad = (10 - brilloNivel) * 0.08f;

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, factorOscuridad);
            shapeRenderer.rect(0, 0, game.VIRTUAL_WIDTH, game.VIRTUAL_HEIGHT);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        // 4. Panel de Administrador si está activo
        if (panelAdminAbierto) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0.05f, 0.15f, 0.05f, 0.92f)); // Verde consola translúcido
            shapeRenderer.rect(200, 150, 880, 420);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            stage.getBatch().begin();
            skin.getFont("default").draw(stage.getBatch(), adminMensajeInfo, 230, 500);
            skin.getFont("default").draw(stage.getBatch(), "[PRESIONE ESC PARA SALIR DEL PANEL]", 480, 200);
            stage.getBatch().end();

            // Detectar tecla de salida para el modo admin
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
        if (textureBackground != null) textureBackground.dispose();
    }
}