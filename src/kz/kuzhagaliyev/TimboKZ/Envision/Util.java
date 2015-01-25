package kz.kuzhagaliyev.TimboKZ.Envision;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Timur Kuzhagaliyev
 * @since 25-01-2015
 */

public class Util {

    public static void drawCircle(float x, float y, float radius, float colour) {

        drawCircle(x, y, radius, colour, colour, colour);

    }

    public static void drawCircle(float x, float y, float radius, float r, float g, float b) {
        glPushMatrix();
        {
            glColor3f(r, g, b);
            glTranslatef(x, y, 0);
            glScalef(radius, radius, 0);
            glBegin(GL_TRIANGLE_FAN);
            {
                glVertex2f(0, 0);
                for (int i = 0; i <= 80; i++) { //NUM_PIZZA_SLICES decides how round the circle looks.
                    double angle = Math.PI * 2 * i / 80;
                    glVertex2f((float) Math.cos(angle), (float) Math.sin(angle));
                }
            }
            glEnd();
        }
        glPopMatrix();
    }

}
