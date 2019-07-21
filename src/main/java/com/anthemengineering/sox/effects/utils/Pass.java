package com.anthemengineering.sox.effects.utils;

import com.anthemengineering.sox.effects.SoxEffect;

import static com.anthemengineering.sox.ValidationUtil.notNullOrEmpty;

/**
 * highpass|lowpass [−1|−2] frequency[k] [width[q|o|h|k]]
 *
 * Apply a high-pass or low-pass filter with 3dB point frequency.
 *
 * The filter can be either single-pole (with −1), or double-pole (the default, or with −2).
 *
 * Width applies only to double-pole effects; the default is Q = 0.707 and gives a Butterworth response.
 *
 * The effects roll off at 6dB per pole per octave (20dB per pole per decade).
 *
 * The double-pole effects are described in detail in [1].
 *
 * These effects support the −−plot global option.
 *
 * See also sinc for effects with a steeper roll-off.
 */
public abstract class Pass implements SoxEffect {
    public enum Pole {
        DEFAULT_POLE(""),
        SINGLE_POLE("-1"),
        DOUBLE_POLE("-2");

        private final String argText;

        Pole(String argText) {
            this.argText = argText;
        }
    }

    private Pole pole = Pole.DEFAULT_POLE;
    private String frequency = "";
    private String width = "";

    public Pass pole(Pole pole) {
        this.pole = pole;

        return this;
    }

    public Pass frequency(String frequency) {
        this.frequency = frequency;

        return this;
    }

    public Pass width(String width) {
        this.width = width;

        return this;
    }

    @Override
    public String[] getOptionsList() {
        OptList optList = new OptList();
        optList.add(pole.argText);
        optList.add(notNullOrEmpty(frequency, "frequency must be specify"));
        optList.add(width);

        return optList.toStringArray();
    }
}
