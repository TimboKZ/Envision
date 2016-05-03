package kz.kuzhagaliyev.TimboKZ.Envision;

import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Audio;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Button;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Visualiser;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Window;
import kz.kuzhagaliyev.TimboKZ.Envision.Visualisers.*;
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

        boolean fullscreen = false;
        String audioPath = null;
        boolean autoplay = false;
        for(int i = 0; i < args.length; i++) {
            if(args[i].equalsIgnoreCase("fullscreen")) fullscreen = true;
            if(args[i].equalsIgnoreCase("audio")) audioPath = args[i + 1];
            if(args[i].equalsIgnoreCase("autoplay")) autoplay = true;
        }

        window = new Window(WINDOWWIDTH, WINDOWHEIGHT, fullscreen);
        audio = new Audio(audioPath);
        if(audioPath != null && autoplay)
            audio.start();
        visualisers = new HashMap<Integer, Visualiser>();
        visualisers.put(Keyboard.KEY_T, new Test());
        visualisers.put(Keyboard.KEY_S, new ShaderTest());
        visualisers.put(Keyboard.KEY_1, new Impulse());
        visualisers.put(Keyboard.KEY_2, new Wave());
        visualisers.put(Keyboard.KEY_3, new Bridges());
        visualisers.put(Keyboard.KEY_4, new Grid());
        currentVisualiser = visualisers.get(Keyboard.KEY_5);
        loop();
        cleanUp();
    }

    public static void loop() {
        HashMap<String, Button> buttons = window.getButtons();
        while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            window.refreshOpenGL();
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
                buttons.get("toggle").reset();
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
