package com.taskapp.dataClasses;

public final class Lbl {
    private final String label;
    private int count;

    public Lbl(String label, int count) {
        this.label = label;
        this.count = count;
    }
    public String getLabel() {
        return label;
    }

    public int getCount() {
        return count;
    }
}
