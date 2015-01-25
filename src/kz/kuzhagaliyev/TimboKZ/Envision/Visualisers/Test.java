package kz.kuzhagaliyev.TimboKZ.Envision.Visualisers;

import kz.kuzhagaliyev.TimboKZ.Envision.Core;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Visualiser;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Timur Kuzhagaliyev
 * @since 24-01-2015
 */

public class Test extends Visualiser {

    public Test() {
        super("Test");
    }

    private float test = 0.0f;

    public void render() {


        float[] values = Core.getAudio().getValues();
        float mean = 0.0f;
        for(float value : values)
            mean += value;
        mean /= 16;


        float barWidth = Display.getWidth() / 256f;
        glColor3f(1, 1, 1);
        for(int i = 0; i < 256; i++) {
            float value = values[i];
            float multiplier = 100;
            glBegin(GL_QUADS);
            glVertex2f(i * barWidth, Display.getHeight());
            glVertex2f(i * barWidth, Display.getHeight() - (float) Math.log(value) * multiplier);
            glVertex2f(i * barWidth + barWidth, Display.getHeight() - (float) Math.log(value) * multiplier);
            glVertex2f(i * barWidth + barWidth, Display.getHeight());
            glEnd();
        }

        test = Math.max(test, values[0]);

        Core.log(test + "");

    }

}
