package kz.kuzhagaliyev.TimboKZ.Envision.Objects;

import kz.kuzhagaliyev.TimboKZ.Envision.Core;

/**
 * @author Timur Kuzhagaliyev
 * @since 24-01-2015
 */

public abstract class Visualiser {

    String name;

    public Visualiser(String name) {
        this.name = name;
    }

    public void update() {

        if(!Core.getAudio().isReady())
            return;

        render();
    }

    public void render() {

    }

}
