package kz.kuzhagaliyev.TimboKZ.Envision.Visualisers;

import kz.kuzhagaliyev.TimboKZ.Envision.Core;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Visualiser;
import kz.kuzhagaliyev.TimboKZ.Envision.Util;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Timur Kuzhagaliyev
 * @since 13-02-2015
 */

public class Bridges extends Visualiser {

    public Bridges() {
        super("Bridges");
        savedValues = new float[64];
        for(int i = 0; i < 64; i++)
            savedValues[i] = 0;
    }

    private float[] savedValues;

    public void render() {

        float[] values = Core.getAudio().getValues();
        float[] bufferValues = Core.getAudio().getBufferValues();
        float mean = 0.0f;
        for(int i = 0; i < 64; i++)
            mean += values[i];
        mean /= 64;
        float barWidth = (float) Display.getWidth() / 64f;
        float adjustment = 12f;
        for(int i = 0; i < 64; i++) {
            float value = (float) Math.max(0, Math.log(values[i]));
            value = savedValues[i] - (savedValues[i] - value) / (24f - (float) Math.log(mean));
            savedValues[i] = value;
            float multiplier = 40f;
            float shade = Math.min(1, Math.max(0, value / 10f));
            glColor3f(Math.abs(bufferValues[i]) * Math.abs(bufferValues[i]), (1 - Math.abs(bufferValues[i]) * Math.abs(bufferValues[i])) * shade, shade);
            glBegin(GL_QUADS);
            glVertex2f(i * barWidth + adjustment, Display.getHeight() / 2 + value * multiplier);
            glVertex2f(i * barWidth + adjustment, Display.getHeight() / 2 - value * multiplier);
            glVertex2f(i * barWidth + barWidth - adjustment, Display.getHeight() / 2 - value * multiplier);
            glVertex2f(i * barWidth + barWidth - adjustment, Display.getHeight() / 2 + value * multiplier);
            glEnd();
            Util.drawCircle(i * barWidth + barWidth / 2, Display.getHeight() / 2 + value * multiplier, shade * 20f, Math.abs(bufferValues[i]) * Math.abs(bufferValues[i]), (1 - Math.abs(bufferValues[i]) * Math.abs(bufferValues[i])) * shade, shade);
            Util.drawCircle(i * barWidth + barWidth / 2, Display.getHeight() / 2 - value * multiplier, shade * 20f, Math.abs(bufferValues[i]) * Math.abs(bufferValues[i]), (1 - Math.abs(bufferValues[i]) * Math.abs(bufferValues[i])) * shade, shade);
        }

    }

}
