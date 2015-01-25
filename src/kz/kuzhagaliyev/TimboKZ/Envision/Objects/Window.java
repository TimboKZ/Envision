package kz.kuzhagaliyev.TimboKZ.Envision.Objects;

import kz.kuzhagaliyev.TimboKZ.Envision.Core;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Timur Kuzhagaliyev
 * @since 23-01-2015
 */

public class Window {

    private HashMap<String, Button> buttons;
    private boolean controls = true;
    private boolean controlsSlidingUp = false;
    private boolean controlsSlidingDown = false;
    private int controlsOffsetY = 0;
    private int counter = 0;

    private Texture barBg;
    private Texture barActiveBg;
    private Texture barIndicator;
    private Texture barIndicatorHover;

    public Window(int width, int height, boolean fullscreen) {

        try {
            DisplayMode displayMode = null;
            DisplayMode[] modes = Display.getAvailableDisplayModes();

            if(fullscreen) {
                for (DisplayMode mode : modes) {
                    if (mode.getWidth() == 1920
                            && mode.getHeight() == 1080
                            && mode.isFullscreenCapable()) {
                        displayMode = mode;
                    }
                }
            } else {
                displayMode = new DisplayMode(width, height);
            }

            Display.setDisplayMode(displayMode);
            Display.setFullscreen(true);
            Display.setResizable(false);
            Display.create();
            Display.setTitle("Envision (" + Display.getWidth() + "x" + Display.getHeight() + ")");
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);

        buttons = new HashMap<String, Button>();

        try {
            barBg = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/progress-bar-bg.png"));
            barActiveBg = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/progress-bar-active-bg.png"));
            barIndicator = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/progress-bar-indicator.png"));
            barIndicatorHover = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/progress-bar-indicator-hover.png"));
            Texture sprite = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/controls.png"));
            Texture spriteHover = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/controls-hover.png"));
            Texture spriteActive = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/controls-active.png"));
            Texture spriteActiveHover = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/controls-active-hover.png"));
            float textureWidth = 0.25f;
            float textureHeight = 0.5f;
            int iconSize = 40;
            for(int i = 0; i < 5; i++) {
                int offsetX;
                int offsetY;
                String name;
                if(i == 0) {
                    offsetX = 2;
                    offsetY = 0;
                    name = "toggle";
                } else if(i == 1) {
                    offsetX = 0;
                    offsetY = 0;
                    name = "audio";
                } else if(i == 2) {
                    offsetX = 1;
                    offsetY = 0;
                    name = "play";
                } else if(i == 3) {
                    offsetX = 0;
                    offsetY = 1;
                    name = "pause";
                } else if(i == 4) {
                    offsetX = 1;
                    offsetY = 1;
                    name = "stop";
                } else {
                    offsetX = 0;
                    offsetY = 0;
                    name = "";
                }
                Button button = new Button(sprite, spriteHover, spriteActive, spriteActiveHover, offsetX * textureWidth, offsetY * textureHeight, textureWidth, textureHeight, i * iconSize, 0, iconSize, iconSize);
                buttons.put(name, button);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }

    public void refresh() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glLoadIdentity();
        glColor3f(1, 1, 1);
    }

    public void update() {

        glColor3f(1, 1, 1);

        if (controlsSlidingUp)
            hideControls();
        if (controlsSlidingDown)
            showControls();

        if(controls) {
            renderControls();
            for (Button button : buttons.values())
                button.update();
        } else
            buttons.get("toggle").update();

    }

    public void showControls() {
        controlsSlidingDown = true;
        if(controlsOffsetY < 0) {
            controlsOffsetY++;
            buttons.get("audio").setY(controlsOffsetY);
            buttons.get("play").setY(controlsOffsetY);
            buttons.get("pause").setY(controlsOffsetY);
            buttons.get("stop").setY(controlsOffsetY);
            controls = true;
        } else {
            controlsSlidingDown = false;
        }
    }

    public void hideControls() {
        controlsSlidingUp = true;
        if(controlsOffsetY > -40) {
            controlsOffsetY--;
            buttons.get("audio").setY(controlsOffsetY);
            buttons.get("play").setY(controlsOffsetY);
            buttons.get("pause").setY(controlsOffsetY);
            buttons.get("stop").setY(controlsOffsetY);
        } else {
            controlsSlidingUp = false;
            controls = false;
        }
    }

    public void renderControls() {
        int barHeight = 40;
        int offsetLeft = 200;

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        barBg.bind();
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 0);
            glVertex2f(offsetLeft, controlsOffsetY);
            glTexCoord2f(0, 1);
            glVertex2f(offsetLeft, controlsOffsetY + barHeight);
            glTexCoord2f(1, 1);
            glVertex2f(Display.getWidth(), controlsOffsetY + barHeight);
            glTexCoord2f(1, 0);
            glVertex2f(Display.getWidth(), controlsOffsetY);
        }
        glEnd();
        if(Core.getAudio().isReady()) {
            double length = Core.getAudio().getLength();
            double position = Core.getAudio().getSamplePlayer().getPosition();
            float barWidth = offsetLeft + (Display.getWidth() - offsetLeft) * (float) (position / length);
            barActiveBg.bind();
            glBegin(GL_QUADS);
            {
                glTexCoord2f(0, 0);
                glVertex2f(offsetLeft, controlsOffsetY);
                glTexCoord2f(0, 1);
                glVertex2f(offsetLeft, controlsOffsetY + barHeight);
                glTexCoord2f(1, 1);
                glVertex2f(barWidth, controlsOffsetY + barHeight);
                glTexCoord2f(1, 0);
                glVertex2f(barWidth, controlsOffsetY);
            }
            glEnd();
            barIndicator.bind();
            float indicatorWidth = 2;
            glBegin(GL_QUADS);
            {
                glTexCoord2f(0, 0);
                glVertex2f(barWidth, controlsOffsetY);
                glTexCoord2f(0, 1);
                glVertex2f(barWidth, controlsOffsetY + barHeight);
                glTexCoord2f(1, 1);
                glVertex2f(barWidth + indicatorWidth, controlsOffsetY + barHeight);
                glTexCoord2f(1, 0);
                glVertex2f(barWidth + indicatorWidth, controlsOffsetY);
            }
            glEnd();
            int mouseX = Mouse.getX();
            int mouseY = Display.getHeight() - Mouse.getY();
            if(mouseX > offsetLeft && mouseX < Display.getWidth() && mouseY > 0 && mouseY < barHeight) {
                barIndicatorHover.bind();
                glBegin(GL_QUADS);
                {
                    glTexCoord2f(0, 0);
                    glVertex2f(mouseX, controlsOffsetY);
                    glTexCoord2f(0, 1);
                    glVertex2f(mouseX, controlsOffsetY + barHeight);
                    glTexCoord2f(1, 1);
                    glVertex2f(mouseX + indicatorWidth, controlsOffsetY + barHeight);
                    glTexCoord2f(1, 0);
                    glVertex2f(mouseX + indicatorWidth, controlsOffsetY);
                }
                glEnd();

                while(Mouse.next())
                    if(!Mouse.getEventButtonState() && Mouse.getEventButton() == 0)
                        Core.getAudio().getSamplePlayer().setPosition(length * (mouseX - offsetLeft) / (Display.getWidth() - offsetLeft));
            }
        }
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

    }

    public void updateName() {

        Display.setTitle("Envision (" + Display.getWidth() + "x" + Display.getHeight() + ") [" + Core.getAudio().getName() + "]");

    }

    public void destroy() {
        Display.destroy();
    }

    public HashMap<String, Button> getButtons() {
        return buttons;
    }

    public boolean isControls() {
        return controls;
    }

    public boolean isControlsSlidingUp() {
        return controlsSlidingUp;
    }

    public boolean isControlsSlidingDown() {
        return controlsSlidingDown;
    }
}
