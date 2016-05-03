package kz.kuzhagaliyev.TimboKZ.Envision.Visualisers;

import kz.kuzhagaliyev.TimboKZ.Envision.Core;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Visualiser;
import net.beadsproject.beads.ugens.Gain;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Timur Kuzhagaliyev
 * @since 25-01-2015
 */

public class Impulse extends Visualiser {

    public Impulse() {
        super("Impulse");
    }

    private float[] savedValues = new float[4];

    float meanPreCalc1 = 0;
    float meanPreCalc2 = 0;
    float meanPreCalc3 = 0;
    float meanPreCalc4 = 0;
    float meanPreCalc5 = 0;
    float currentBG = 0;

    public void render() {

        Gain gain = Core.getAudio().getGain();

        glPushMatrix();
        glTranslatef(Display.getWidth() / 2, Display.getHeight() / 2, 0);
        glScalef(0.6f, 0.6f, 0);
        glPushMatrix();
        {
            glTranslatef(-Display.getWidth() / 2, -Display.getHeight() / 2, 0);
            float meanPreCalc = 0;

            for (float i = 0.0f; i < 512.0f; i++) {
                float value = gain.getValue(0, (int) i);
                meanPreCalc += value;
            }
            meanPreCalc /= 512.0f;

            meanPreCalc = (meanPreCalc + meanPreCalc1 + meanPreCalc2 + meanPreCalc3 + meanPreCalc4 + meanPreCalc5) / 6;
            meanPreCalc5 = meanPreCalc4;
            meanPreCalc4 = meanPreCalc3;
            meanPreCalc3 = meanPreCalc2;
            meanPreCalc2 = meanPreCalc1;
            meanPreCalc1 = meanPreCalc;

            float barWidth = (float) Display.getWidth() / 512.0f;
            float mean = 0;

            if (meanPreCalc > 0.03f) {
                currentBG = 0.2f;
            } else if (currentBG > 0) {
                currentBG -= 0.01f;
            }
            glClearColor(currentBG, currentBG, currentBG, 1.0f);

            for (float i = 0.0f; i < 512.0f; i++) {
                float value = gain.getValue(0, (int) i);
                mean += value;
                if (i % 16 == 0) {
                    glPushMatrix();
                    {
                        glTranslatef(Display.getWidth() / 2, Display.getHeight() / 2, 1.0f);
                        glRotatef(i * 360.0f / 512.0f - System.nanoTime() / 100000000.0f, 0, 0, 1.0f);
                        float multiplier = 50.0f + 150.0f * Math.abs(meanPreCalc) * Math.abs(meanPreCalc);
                        glColor3f(value * 5.0f, 1.0f - value * 5.0f, mean);
                        glBegin(GL_QUADS);
                        {
                            float barHeight = 50.0f;
                            float indent = 300.0f;
                            glVertex2f(-barWidth, indent);
                            glVertex2f(-barWidth, indent - barHeight - multiplier * Math.abs(value));
                            glVertex2f(barWidth, indent - barHeight - multiplier * Math.abs(value));
                            glVertex2f(barWidth, indent);
                        }
                        glEnd();
                    }
                    glPopMatrix();
                    glPushMatrix();
                    {
                        glTranslatef(Display.getWidth() / 2, Display.getHeight() / 2, 1.0f);
                        glRotatef(i * 360.0f / 512.0f + System.nanoTime() / 100000000.0f, 0, 0, 1.0f);
                        float multiplier = 50.0f + 150.0f * Math.abs(meanPreCalc) * Math.abs(meanPreCalc);
                        glColor3f(value * 5.0f, 1.0f - value * 5.0f, mean);
                        glBegin(GL_QUADS);
                        {
                            float barHeight = 100.0f;
                            float indent = 350.0f;
                            glVertex2f(-barWidth, indent + barHeight + multiplier * Math.abs(value));
                            glVertex2f(-barWidth, indent);
                            glVertex2f(barWidth, indent);
                            glVertex2f(barWidth, indent + barHeight + multiplier * Math.abs(value));
                        }
                        glEnd();
                    }
                    glPopMatrix();
                }
            }

            mean /= 512.0f;

            // Triangles
            {
                float theta = (float) Math.PI / 3.0f;
                float[][] matrix = new float[2][2];
                matrix[0][0] = (float) Math.cos(theta);
                matrix[0][1] = (float) -Math.sin(theta);
                matrix[1][0] = (float) Math.sin(theta);
                matrix[1][1] = (float) Math.cos(theta);
                float[][] inverse = new float[2][2];
                float determinant = matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1];
                inverse[0][0] = matrix[1][1] * determinant;
                inverse[0][1] = -matrix[0][1] * determinant;
                inverse[1][0] = -matrix[1][0] * determinant;
                inverse[1][1] = matrix[0][0] * determinant;
                float sizeMultiplier = 200;
                // Middle triangle
                glPushMatrix();
                {
                    glTranslatef(Display.getWidth() / 2, Display.getHeight() / 2, 0);
                    glRotatef(System.nanoTime() / 100000000.0f, 230, 0, 1);
                    glBegin(GL11.GL_TRIANGLES);
                    float x = 0.0f;
                    float y = -1.0f * sizeMultiplier * Math.abs(mean);

                    glColor3f(mean, 1.0f - mean, 1.0f - mean);
                    glVertex2f(x, y);
                    glColor3f(1.0f - mean, mean, 1.0f - mean);
                    glVertex2f(matrix[0][0] * x + matrix[0][1] * y, matrix[1][0] * x + matrix[1][1] * y + sizeMultiplier * Math.abs(mean));
                    glColor3f(1.0f - mean, 1.0f - mean, mean);
                    glVertex2f(inverse[0][0] * x + inverse[0][1] * y, inverse[1][0] * x + inverse[1][1] * y + sizeMultiplier * Math.abs(mean));
                    glEnd();
                }
                glPopMatrix();
                // Circular triangles
                for (int i = 0; i < 16; i++) {
                    glPushMatrix();
                    {
                        glTranslatef(Display.getWidth() / 2, Display.getHeight() / 2, 0);
                        glRotatef(-System.nanoTime() / 100000000.0f, 0, 0, 1);
                        glPushMatrix();
                        {
                            glRotatef(i * 360.0f / 16.0f, 0.0f, 0.0f, 1.0f);
                            glTranslatef(0.0f, 800.0f, 0);
                            glPushMatrix();
                            {
                                glRotatef(i * 360.0f / 16.0f - System.nanoTime() / 100000000.0f, 0.0f, 0.0f, 1.0f);
                                glBegin(GL11.GL_TRIANGLES);
                                {
                                    float x = 0.0f;
                                    float y = -1.0f * sizeMultiplier * Math.abs(mean);
                                    glColor3f(1.0f * (float) (i + 1) / 16.0f, 1.0f - 1.0f * (float) (i + 1) / 16.0f, Math.abs(mean) * 1.0f * (float) (i + 1) / 16.0f);
                                    glVertex2f(x, y);
                                    glVertex2f(matrix[0][0] * x + matrix[0][1] * y, matrix[1][0] * x + matrix[1][1] * y + sizeMultiplier * Math.abs(mean));
                                    glVertex2f(inverse[0][0] * x + inverse[0][1] * y, inverse[1][0] * x + inverse[1][1] * y + sizeMultiplier * Math.abs(mean));
                                }
                                glEnd();
                            }
                            glPopMatrix();
                        }
                        glPopMatrix();
                    }
                    glPopMatrix();
                }
            }

            // Middle circle #1
            {
                float num_segments = 100;

                float theta = 2 * (float) 3.1415926 / num_segments;
                float c = (float) Math.cos(theta);
                float s = (float) Math.sin(theta);
                float t;

                float x = 550.0f + 100.0f * Math.abs(mean);//we start at angle = 0
                float y = 0;

                glBegin(GL_LINE_LOOP);
                for (int ii = 0; ii < num_segments; ii++) {
                    glColor3f(1.0f - mean, 1.0f - mean, (float) ii / num_segments);
                    glVertex2f(x + Display.getWidth() / 2, y + Display.getHeight() / 2);//output vertex

                    t = x;
                    x = c * x - s * y;
                    y = s * t + c * y;
                }
                glEnd();
            }

            // Middle circle #2
            {
                float num_segments = 100.0f;

                float theta = 2 * (float) 3.1415926 / num_segments;
                float c = (float) Math.cos(theta);//precalculate the sine and cosine
                float s = (float) Math.sin(theta);
                float t;

                float x = 600.0f + 50.0f * Math.abs(mean);//we start at angle = 0
                float y = 0;

                glBegin(GL_LINE_LOOP);
                for (int ii = 0; ii < num_segments; ii++) {
                    glColor3f(1.0f - Math.abs(mean), (float) ii / num_segments, 1.0f - Math.abs(mean));
                    glVertex2f(x + Display.getWidth() / 2, y + Display.getHeight() / 2);//output vertex

                    t = x;
                    x = c * x - s * y;
                    y = s * t + c * y;
                }
                glEnd();
            }

        }
        glPopMatrix();
        glPopMatrix();


    }

}
