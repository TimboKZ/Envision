package kz.kuzhagaliyev.TimboKZ.Envision;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Timur Kuzhagaliyev
 * @since 25-01-2015
 */

public class Util {

    public static void drawRect(float x, float y, float width, float height, float colour) {

        drawRect(x, y, width, height, colour, colour, colour);

    }

    public static void drawRect(float x, float y, float width, float height, float r, float g, float b) {

        glPushMatrix();
        {
            glColor3f(r, g, b);
            glBegin(GL_QUADS);
            {
                glVertex2f(x, y);
                glVertex2f(x, y + height);
                glVertex2f(x + width, y + height);
                glVertex2f(x + width, y);
            }
            glEnd();
        }
        glPopMatrix();

    }

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

    public static void drawCircleHollow(float x, float y, float radius, float shade) {

        drawCircleHollow(x, y, radius, shade, shade, shade);

    }

    public static void drawCircleHollow(float x, float y, float radius, float r, float g, float b) {

        glColor3f(r, g, b);
        float num_segments = 100;

        float theta = 2 * (float) 3.1415926 / num_segments;
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float t;

        float u = radius * 2;
        float v = 0;

        glBegin(GL_LINE_LOOP);
        for (int ii = 0; ii < num_segments; ii++) {
            glVertex2f(x + u, y + v);

            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }
        glEnd();

    }

    public static String readFile(InputStream in) throws IOException {
        final StringBuffer sBuffer = new StringBuffer();
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        final char[] buffer = new char[1024];

        int cnt;
        while ((cnt = br.read(buffer, 0, buffer.length)) > -1) {
            sBuffer.append(buffer, 0, cnt);
        }
        br.close();
        in.close();
        return sBuffer.toString();
    }

}
