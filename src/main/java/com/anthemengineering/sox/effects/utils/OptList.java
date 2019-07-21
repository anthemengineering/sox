package com.anthemengineering.sox.effects.utils;

import java.util.ArrayList;

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
     * @param opts multiple options to add at once
     * @return true if the collection was modified, false otherwise.
     */
    public boolean addMany(String... opts) {
        boolean result = false;
        for (String opt : opts) {
            result |= add(opt);
        }

        return result;
    }

    public String[] toStringArray() {
        return toArray(new String[]{});
    }
}
