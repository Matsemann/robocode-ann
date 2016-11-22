package com.matsemann.util;

public class Vector {
    public double x, y;

    public Vector() {}

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(Vector v) {
        set(v);
    }

    public Vector set(Vector v) {
        this.x = v.x;
        this.y = v.y;
        return this;
    }

    public Vector set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector sub(Vector v) {
        return new Vector(x - v.x, y - v.y);
    }

    @Override
    public String toString() {
        return "x: " + x + ", y: " + y;
    }
}
