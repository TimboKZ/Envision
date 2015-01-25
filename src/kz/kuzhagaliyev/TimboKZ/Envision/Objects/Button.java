package kz.kuzhagaliyev.TimboKZ.Envision.Objects;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Timur Kuzhagaliyev
 * @since 23-01-2015
 */

public class Button {

    private boolean hovered = false;
    private boolean clicked = false;
    private boolean active = false;

    private Texture texture;
    private Texture textureHover;
    private Texture textureActive;
    private Texture textureActiveHover;
    private float textureX;
    private float textureY;
    private float textureWidth;
    private float textureHeight;
    private int x;
    private int y;
    private int width;
    private  int height;

    public Button(Texture texture, Texture textureHover, Texture textureActive, Texture textureActiveHover, float textureX, float textureY, float textureWidth, float textureHeight, int x, int y, int width, int height) {
        this.texture = texture;
        this.textureHover = textureHover;
        this.textureActive = textureActive;
        this.textureActiveHover = textureActiveHover;
        this.textureX = textureX;
        this.textureY = textureY;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update() {
        interact();
        render();
    }

    public void render() {
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        if(hovered) if(active) textureActiveHover.bind(); else textureHover.bind();
        else if(active) textureActive.bind(); else texture.bind();
        glColor3f(1, 1, 1);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(textureX, textureY);
            glVertex2f(x, y);
            glTexCoord2f(textureX, textureY + textureHeight);
            glVertex2f(x, y + height);
            glTexCoord2f(textureX + textureWidth, textureY + textureHeight);
            glVertex2f(x + width, y + height);
            glTexCoord2f(textureX + textureWidth, textureY);
            glVertex2f(x + width, y);
        }
        glEnd();
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public void interact() {
        int mouseX = Mouse.getX();
        int mouseY = Display.getHeight() - Mouse.getY();
        if(mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height) {
            hovered = true;
            while(Mouse.next())
                if(!Mouse.getEventButtonState() && Mouse.getEventButton() == 0)
                    clicked = true;
        } else {
            hovered = false;
            clicked = false;
        }
    }

    public void reset() {
        hovered = false;
        clicked = false;
    }

    public boolean isHovered() {
        return hovered;
    }

    public boolean isClicked() {
        return clicked;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
