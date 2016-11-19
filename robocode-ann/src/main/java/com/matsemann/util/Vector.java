package com.matsemann.util;

public class Vector {
    public double x, y;

    public Vector() {}

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector sub(Vector v) {
        return new Vector(x - v.x, y - v.y);
    }
}