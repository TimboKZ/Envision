package kz.kuzhagaliyev.TimboKZ.Envision.Visualisers;

import kz.kuzhagaliyev.TimboKZ.Envision.Core;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Visualiser;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Timur Kuzhagaliyev
 * @since 14-02-2015
 */

public class Grid extends Visualiser {

    public Grid() {
        super("Grid");
        savedValues = new float[64];
        for(int i = 0; i < 64; i++)
            savedValues[i] = 0;
        shades = new float[numHor];
        float num = 0;
        for(int i = 0; i < numHor; i++) {
            shades[i] = num;
            num += 1f / (float) numHor;
        }
    }

    private float[] savedValues;

    private float[] shades;

    private float densityX = 0.02f;
    private float densityY = 0.02f;

    private int numVer = (int) (Display.getWidth() * densityX);
    private int numHor = 32;//(int) (Display.getHeight() * densityY);

    private int segHor = 200;
    private int segVer = segHor * Display.getHeight() / Display.getWidth();

    public void render() {

        float[] values = Core.getAudio().getValues();
        float mean = 0.0f;
        for (int i = 0; i < 64; i++) {
            float value = (float) Math.max(0, Math.log(values[i]));
            value = savedValues[i] - (savedValues[i] - value) / (30f - (float) Math.log(mean));
            savedValues[i] = value;
            mean += values[i];
        }
        mean /= 64;

        // Horizontal
        for(int i = 0; i < numHor; i++) {
            float value = (float) Math.max(0, Math.log(values[i]));
            float shade = Math.min(1, Math.max(0, value / 10f));
            glColor3f(shade, shade, shade);
            value = savedValues[i] - (savedValues[i] - value) / (36f - (float) Math.log(mean));
            savedValues[i] = value;
            glPushMatrix();
            {
                glBegin(GL_LINE_LOOP);
                float y = Display.getHeight() / (float) numHor * i;
                float width = Display.getWidth() / (float) segHor;
                glVertex2f(-1, -1);
                glVertex2f(-1, y + (float) Math.sin(0) * 5f * value);
                for(int k = 0; k < segHor; k++) {
                    float x = width * k;
                    shades[i] += 1f / ((float) numHor * 5000f) * mean / 20f;
                    if(shades[i] > 1)
                        shades[i] = 0;
                    glColor3f(shades[i] * shade, (1 - shades[i]) * shade, (shades[i] - shade) * shade);
                    glVertex2f(x, y + (float) Math.sin(x) * 5f * value);
                }
                glVertex2f(Display.getWidth() + 1, y + (float) Math.sin(Display.getWidth()) * 5f * value);
                glVertex2f(Display.getWidth() + 1, -1);
                glEnd();
            }
            glPopMatrix();
        }


        // Vertical
        for(int i = 0; i < numVer; i++) {

        }

    }

}
