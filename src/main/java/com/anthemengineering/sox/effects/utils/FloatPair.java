package com.anthemengineering.sox.effects.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FloatPair implements Comparable<FloatPair> {
    // DecimalFormat is not thread safe, so have to create a new instance each time
    private final DecimalFormat format = createFormat();

    private final Float first;
    private final Float second;

    public FloatPair(float first) {
        this.first = first;
        this.second = null;
    }

    public FloatPair(float first, float second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        if (second != null) {
            return format.format(first) + "," + format.format(second);
        } else {
            return format.format(first);
        }
    }

    @Override
    public int compareTo(FloatPair o) {
        return first.compareTo(o.first);
    }

    public static DecimalFormat createFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setInfinity("inf");
        return new DecimalFormat("0.###", symbols);
    }
}
