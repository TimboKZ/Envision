package kz.kuzhagaliyev.TimboKZ.Envision.Objects;

import kz.kuzhagaliyev.TimboKZ.Envision.Core;
import net.beadsproject.beads.analysis.featureextractors.FFT;
import net.beadsproject.beads.analysis.featureextractors.PeakDetector;
import net.beadsproject.beads.analysis.featureextractors.PowerSpectrum;
import net.beadsproject.beads.analysis.featureextractors.SpectralDifference;
import net.beadsproject.beads.analysis.segmenters.ShortFrameSegmenter;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.Glide;
import net.beadsproject.beads.ugens.SamplePlayer;

import javax.swing.*;
import java.io.File;

/**
 * @author Timur Kuzhagaliyev
 * @since 24-01-2015
 */

public class Audio {

    private String path;
    private String name;
    private double length;
    private boolean choosingFile = false;
    private boolean ready = false;

    private SamplePlayer samplePlayer;
    private AudioContext audioContext;
    private Gain gain;
    private PowerSpectrum powerSpectrum;

    private float beat = 0.0f;

    public Audio(String path) {

        this.path = path;

    }

    public boolean chooseFile() {
        choosingFile = true;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("D:\\Music\\Singles\\"));
        int result = fileChooser.showOpenDialog(new JPanel());
        if (result == JFileChooser.APPROVE_OPTION) {
            path = fileChooser.getSelectedFile().getPath();
            String[] strings = path.split("(\\\\)");
            name = strings[strings.length - 1];
            Core.getWindow().updateName();
        }
        choosingFile = false;

        Core.getWindow().getButtons().get("audio").reset();
        return result == JFileChooser.APPROVE_OPTION;
    }

    public void start() {
        audioContext = new AudioContext();
        try {
            samplePlayer = new SamplePlayer(audioContext, new Sample(path));
            samplePlayer.setKillOnEnd(false);
        } catch (Exception e) {
            Core.log("Failed to create a SamplePlayer.");
            e.printStackTrace();
            System.exit(1);
        }
        length = samplePlayer.getSample().getLength();
        Glide gainValue = new Glide(audioContext, 1.0f, 20);
        gain = new Gain(audioContext, 1, gainValue);
        gain.addInput(samplePlayer);
        audioContext.out.addInput(gain);
        ShortFrameSegmenter shortFrameSegmenter = new ShortFrameSegmenter(audioContext);
        shortFrameSegmenter.addInput(audioContext.out);
        FFT fft = new FFT();
        shortFrameSegmenter.addListener(fft);
        powerSpectrum = new PowerSpectrum();
        fft.addListener(powerSpectrum);

        SpectralDifference spectralDifference = new SpectralDifference(audioContext.getSampleRate());
        powerSpectrum.addListener(spectralDifference);
        PeakDetector beatDetector = new PeakDetector();
        spectralDifference.addListener(beatDetector);
        beatDetector.setThreshold(0.1f);
        beatDetector.setAlpha(0.98f);
        beatDetector.addMessageListener(new Bead() {
            protected void messageReceived(Bead b) {
                beat = 1.0f;
            }
        });

        audioContext.out.addDependent(shortFrameSegmenter);
        audioContext.start();
        Core.getWindow().getButtons().get("audio").setActive(true);
        Core.getWindow().getButtons().get("play").setActive(true);
        Core.getWindow().getButtons().get("pause").setActive(false);
        Core.getWindow().getButtons().get("stop").setActive(false);
        ready = true;
    }

    public void play() {
        if(ready) {
            samplePlayer.pause(false);
            Core.getWindow().getButtons().get("audio").setActive(true);
            Core.getWindow().getButtons().get("play").setActive(true);
            Core.getWindow().getButtons().get("pause").setActive(false);
            Core.getWindow().getButtons().get("stop").setActive(false);
        }
    }

    public void pause() {
        if(ready) {
            samplePlayer.pause(true);
            Core.getWindow().getButtons().get("audio").setActive(true);
            Core.getWindow().getButtons().get("play").setActive(false);
            Core.getWindow().getButtons().get("pause").setActive(true);
            Core.getWindow().getButtons().get("stop").setActive(false);
        }
    }

    public void stop() {
        if(ready) {
            samplePlayer.pause(true);
            samplePlayer.setPosition(0);
            Core.getWindow().getButtons().get("audio").setActive(true);
            Core.getWindow().getButtons().get("play").setActive(false);
            Core.getWindow().getButtons().get("pause").setActive(false);
            Core.getWindow().getButtons().get("stop").setActive(true);
        }
    }

    public void destroy() {
        if(ready) {
            audioContext.stop();
            samplePlayer.kill();
            Core.getWindow().getButtons().get("audio").setActive(false);
            Core.getWindow().getButtons().get("play").setActive(false);
            Core.getWindow().getButtons().get("pause").setActive(false);
            Core.getWindow().getButtons().get("stop").setActive(false);
            ready = false;
        }
    }

    public float[] getBufferValues() {

        float[] values = new float[512];

        for(int i = 0; i < 512; i++)
            values[i] = gain.getValue(0, (int) i);

        return values;

    }

    public float[] getSemiCondensedBufferValues() {

        float[] values = getBufferValues();

        float[] condensedValues = new float[256];

        for(int i = 0; i < 256; i++)
            condensedValues[i] = (values[i * 2] + values[i * 2 + 1]) / 2;

        return condensedValues;

    }

    public float[] getValues() {
        float[] values = powerSpectrum.getFeatures();
        if(values == null) {
            values = new float[256];
            for(int i = 0; i < 256; i++)
                values[i] = 0.0f;
        }
        return values;
    }

    public float[] getSemiCondensedValues() {
        float[] values = getValues();
        float value = 0.0f;
        float[] condensedValues = new float[32];
        for(int k = 0; k < 32; k++) {
            value = 0.0f;
            for(int i = 0; i < 8; i++) {
                value += values[k * 8 + i];
            }
            condensedValues[k] = value / 16.0f;
        }
        return condensedValues;
    }

    public float[] getCondensedValues() {
        float[] values = getValues();
        float value = 0.0f;
        float[] secondValues = new float[128];
        for(int i = 0; i < 256; i++) {
            if(i % 2 == 0)
                value = values[i];
            else {
                value = (float) Math.max(value, values[i]);
                secondValues[i / 2] = value;
            }
        }
        float[] condensedValues = new float[16];
        for(int k = 0; k < 16; k++) {
            value = 0.0f;
            for(int i = 0; i < 16; i++) {
                value += values[k * 16 + i];
            }
            condensedValues[k] = value / 16.0f;
        }
        return condensedValues;
    }

    public float[] getSuperCondensedValues() {
        float[] values = getValues();
        float value = 0.0f;
        float[] secondValues = new float[128];
        for(int i = 0; i < 256; i++) {
            if(i % 2 == 0)
                value = values[i];
            else {
                value = (float) Math.max(value, values[i]);
                secondValues[i / 2] = value;
            }
        }
        float[] condensedValues = new float[4];
        for(int k = 0; k < 4; k++) {
            value = 0.0f;
            for(int i = 0; i < 32; i++) {
                value += values[k * 32 + i];
            }
            condensedValues[k] = value / 32.0f;
        }
        return condensedValues;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        return length;
    }

    public boolean isChoosingFile() {
        return choosingFile;
    }

    public boolean isReady() {
        return ready;
    }

    public SamplePlayer getSamplePlayer() {
        return samplePlayer;
    }

    public Gain getGain() { return  gain; }

    public float getBeat() {
        return beat;
    }

    public void setBeat(float beat) {
        this.beat = beat;
    }
}
