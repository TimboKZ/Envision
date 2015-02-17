package kz.kuzhagaliyev.TimboKZ.Envision.Visualisers;

import kz.kuzhagaliyev.TimboKZ.Envision.Core;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Shader;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Visualiser;
import kz.kuzhagaliyev.TimboKZ.Envision.Util;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

/**
 * @author Timur Kuzhagaliyev
 * @since 06-02-2015
 */

public class ShaderTest extends Visualiser {

    private float[] savedValues;
    private Shader shader;

    public ShaderTest() {
        super("Shader Test");
        shader = new Shader("res/shaders/ShaderTest_Vertex.glsl", "res/shaders/ShaderTest_Fragment.glsl");
        savedValues = new float[64];
        for(int i = 0; i < 64; i++)
            savedValues[i] = 0;
    }

    public void render() {

        Core.getWindow().shaderOpenGL();
        ARBShaderObjects.glUseProgramObjectARB(shader.getProgram());

        float[] values = Core.getAudio().getValues();
        float mean = 0.0f;
        for(int i = 0; i < 64; i++)
            mean += values[i];
        mean /= 64;

        Util.drawRect(0, 0, Display.getWidth(), Display.getHeight(), 1);

        GL20.glUniform1f(GL20.glGetUniformLocation(shader.getProgram(), "mean"), Math.abs(mean));

        ARBShaderObjects.glUseProgramObjectARB(0);

    }

}
