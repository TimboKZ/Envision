package kz.kuzhagaliyev.TimboKZ.Envision.Visualisers;

import kz.kuzhagaliyev.TimboKZ.Envision.Core;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Visualiser;
import kz.kuzhagaliyev.TimboKZ.Envision.Util;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Timur Kuzhagaliyev
 * @since 09-02-2015
 */

public class Wave extends Visualiser {

    public Wave() {
        super("Wave");
        savedValues = new float[64];
        for(int i = 0; i < 64; i++)
            savedValues[i] = 0;
        rotation = 0;
    }

    private float[] savedValues;
    private float rotation;

    public void render() {

        float[] values = Core.getAudio().getSemiCondensedValues();
        float mean = 0.0f;
        for (int i = 0; i < 16; i++)
            mean += values[i];
        mean /= 16;

        float beat = Core.getAudio().getBeat();
        if(beat > 0)
            beat -= 0.01f;
        Core.getAudio().setBeat(beat);

        glPushMatrix();
        glTranslatef(Display.getWidth() / 2, Display.getHeight() / 2, 0);
        glPushMatrix();
        glRotatef(rotation, 0, 0, 1);
        rotation = rotation + 45f / (float) Core.getFps();
        for(int i = 0; i < 16; i++) {

            float value = (float) Math.max(0, Math.log(values[i]));
            value = savedValues[i] - (savedValues[i] - value) / (24f - (float) Math.log(mean));
            savedValues[i] = value;
            float displacementY = Display.getHeight() / 3 + value * Display.getHeight() / 30 + (float) Math.cos((rotation - i * 180f / 32f) * 0.1f) * 5f;
            float displacementX = 0;
            float radius = 5;
            float radiusAdjustment = (float) Math.cos((rotation - i * 180f / 32f) * 0.1f) * 5f + value;

            glPushMatrix();
            glRotatef(-90f + i * 180f / 32f + 180f / 32f, 0, 0, 1);
            Util.drawCircle(displacementX, displacementY, radius + radiusAdjustment, 1);
            glPopMatrix();

            glPushMatrix();
            glRotatef(-90f - i * 180f / 32f, 0, 0, 1);
            Util.drawCircle(displacementX, displacementY, radius + radiusAdjustment, 1);
            glPopMatrix();

            glPushMatrix();
            glRotatef(90f + i * 180f / 32f + 180f / 32f, 0, 0, 1);
            Util.drawCircle(displacementX, displacementY, radius + radiusAdjustment, 1);
            glPopMatrix();

            glPushMatrix();
            glRotatef(90f - i * 180f / 32f, 0, 0, 1);
            Util.drawCircle(displacementX, displacementY, radius + radiusAdjustment, 1);
            glPopMatrix();

        }
        glPopMatrix();
        glPopMatrix();

    }

}
