package kz.kuzhagaliyev.TimboKZ.Envision.Objects;

import kz.kuzhagaliyev.TimboKZ.Envision.Core;
import kz.kuzhagaliyev.TimboKZ.Envision.Util;
import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.util.ResourceLoader;
import sun.security.provider.SHA;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author Timur Kuzhagaliyev
 * @since 06-02-2015
 */

public class Shader {

    private int vertexShader;
    private int fragmentShader;
    private int program;

    public Shader(String vertexPath, String fragmentPath) {

        vertexShader = createShader(vertexPath, ARBVertexShader.GL_VERTEX_SHADER_ARB);
        fragmentShader = createShader(fragmentPath, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

        if(vertexShader == 0 || fragmentShader == 0)
            Core.log("Failed to create shaders.");

        createProgram();

    }

    private int createShader(String path, int type) {
        int shader = 0;
        try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(type);

            if(shader == 0)
                return 0;

            ARBShaderObjects.glShaderSourceARB(shader, Util.readFile(ResourceLoader.getResourceAsStream(path)));
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
                if(type == ARBFragmentShader.GL_FRAGMENT_SHADER_ARB)
                    Core.log("Failed to compile fragment shader.");
                else if(type == ARBVertexShader.GL_VERTEX_SHADER_ARB)
                    Core.log("Failed to compile vertex shader.");
            }

        }
        catch(Exception exc) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            Core.log("Failed to create shader.");
        }
        return shader;
    }

    private void createProgram() {

        program = ARBShaderObjects.glCreateProgramObjectARB();
        if(program == 0)
            Core.log("Failed to create program.");
        ARBShaderObjects.glAttachObjectARB(program, vertexShader);
        ARBShaderObjects.glAttachObjectARB(program, fragmentShader);
        ARBShaderObjects.glLinkProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            Core.log("Failed to create program.");
        }
        ARBShaderObjects.glValidateProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
            Core.log("Failed to create program.");
        }

    }

    public int getProgram() {
        return program;
    }

}
