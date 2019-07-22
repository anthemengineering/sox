package com.anthemengineering.sox.effects.utils;

import java.util.ArrayList;

import static com.anthemengineering.sox.ValidationUtil.nullOrEmpty;

public class OptList extends ArrayList<String> {
    @Override
    public boolean add(String opt) {
        if (opt == null) {
            return false;
        }

        String toAdd = opt.trim();
        if (toAdd.isEmpty()) {
            return false;
        }

        return super.add(toAdd);
    }

    /**
     * See {@link #add(String)}
     *
     * @param argumentNames the names in order of the options
     * @param opts multiple options to add at once
     * @return true if the collection was modified, false otherwise.
     */
    public boolean addOrderArguments(String[] argumentNames, String... opts) {
        if (argumentNames.length != opts.length) {
            throw new IllegalStateException("ArgumentNames list must match the number of options given.");
        }

        if (opts.length == 0) {
            return false;
        }

        boolean result = true;

        for (int i = 0; i < opts.length; i++) {
            String opt = opts[i];
            if (!result) {
                nullOrEmpty(opt, String.format("Option '%s' requires the previous option '%s' to be set.",
                        argumentNames[i],
                        argumentNames[i - 1]));
            } else {
                result = add(opt);
            }
        }

        return result;
    }

    public String[] toStringArray() {
        return toArray(new String[]{});
    }
}
