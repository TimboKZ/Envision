package kz.kuzhagaliyev.TimboKZ.Envision;

import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Audio;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Button;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Visualiser;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Window;
import kz.kuzhagaliyev.TimboKZ.Envision.Visualisers.Impulse;
import kz.kuzhagaliyev.TimboKZ.Envision.Visualisers.Test;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.util.HashMap;

/**
 * @author Timur Kuzhagaliyev
 * @since 23-01-2015
 */

public class Core {

    public static final int WINDOWWIDTH = 1280;
    public static final int WINDOWHEIGHT = 720;
    public static final int FPS = 120;

    private static Window window;
    private static Audio audio;
    private static HashMap<Integer, Visualiser> visualisers;
    private static Visualiser currentVisualiser;

    public static void main(String[] args) {
        window = new Window(WINDOWWIDTH, WINDOWHEIGHT, false);
        audio = new Audio(null);
        visualisers = new HashMap<Integer, Visualiser>();
        visualisers.put(Keyboard.KEY_T, new Test());
        visualisers.put(Keyboard.KEY_1, new Impulse());
        currentVisualiser = visualisers.get(Keyboard.KEY_T);
        loop();
        cleanUp();
    }

    public static void loop() {
        HashMap<String, Button> buttons = window.getButtons();
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            window.refresh();
            while(Keyboard.next()) {
                if(Keyboard.getEventKeyState())
                    if(visualisers.get(Keyboard.getEventKey()) != null)
                        currentVisualiser = visualisers.get(Keyboard.getEventKey());
            }
            if(buttons.get("audio").isClicked() && !audio.isChoosingFile() && audio.chooseFile()) {
                audio.destroy();
                audio.start();
            }
            if(buttons.get("play").isClicked()) audio.play();
            if(buttons.get("pause").isClicked()) audio.pause();
            if(buttons.get("stop").isClicked()) audio.stop();
            if(buttons.get("toggle").isClicked()) {
                if(!window.isControlsSlidingDown() && !window.isControlsSlidingUp()) {
                    if(window.isControls()) window.hideControls();
                    else window.showControls();
                }
            }
            if(currentVisualiser != null)
                currentVisualiser.update();
            window.update();
            Display.sync(FPS);
            Display.update();
        }
    }

    public static void cleanUp() {
        window.destroy();
        audio.destroy();
    }

    public static void log(String string) {
        System.out.println("[Envision] " + string);
    }

    public static int getFps() {
        return FPS;
    }

    public static Window getWindow() {
        return window;
    }

    public static Audio getAudio() {
        return audio;
    }
}
