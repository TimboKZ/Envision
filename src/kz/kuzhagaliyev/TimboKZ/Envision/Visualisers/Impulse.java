package kz.kuzhagaliyev.TimboKZ.Envision.Visualisers;

import kz.kuzhagaliyev.TimboKZ.Envision.Core;
import kz.kuzhagaliyev.TimboKZ.Envision.Objects.Visualiser;
import kz.kuzhagaliyev.TimboKZ.Envision.Util;
import org.lwjgl.opengl.Display;

/**
 * @author Timur Kuzhagaliyev
 * @since 25-01-2015
 */

public class Impulse extends Visualiser {

    public Impulse() {
        super("Impulse");
    }

    public void render() {

        float[] values = Core.getAudio().getSuperCondensedValues();

        float beat = Core.getAudio().getBeat();
        if(beat > 0)
            beat -= 0.05f;
        Core.getAudio().setBeat(beat);

        Util.drawCircle(Display.getWidth() / 5, Display.getHeight() / 2, 10 + values[0] / 25, beat);
        Util.drawCircle(Display.getWidth() / 5 * 2, Display.getHeight() / 2, 10 + values[1] * 5, beat);
        Util.drawCircle(Display.getWidth() / 5 * 3, Display.getHeight() / 2, 10 + values[2] * 5, beat);
        Util.drawCircle(Display.getWidth() / 5 * 4, Display.getHeight() / 2, 10 + values[3] * 5, beat);

    }

}
