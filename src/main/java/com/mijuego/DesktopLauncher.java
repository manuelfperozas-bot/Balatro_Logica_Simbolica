package com.mijuego;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {

    public static void main(String[] arg) {

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Pixel Art Engine - Java & Maven");
        config.setWindowedMode(1280, 720); // Ventana física en HD (16:9)
        config.setForegroundFPS(60);
        config.useVsync(true); // Evita el efecto de desgarro de pantalla (Tearing)
        new Lwjgl3Application(new MainGame(), config);

    }
}
