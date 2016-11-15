//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package steffen.utils;

public class Vector2D {
    private double x = 0.0D;
    private double y = 0.0D;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getLength() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public Vector2D add(Vector2D vector) {
        return new Vector2D(this.x + vector.x, this.y + vector.y);
    }

    public Vector2D add(double x, double y) {
        return this.add(new Vector2D(x, y));
    }

    public Vector2D sub(Vector2D vector) {
        return new Vector2D(this.x - vector.x, this.y - vector.y);
    }

    public Vector2D sub(double x, double y) {
        return this.sub(new Vector2D(x, y));
    }

    public Vector2D multiply(double m) {
        return new Vector2D(this.x * m, this.y * m);
    }

    public double getAngle() {
        return this.getAngleRadians() * 180.0D / 3.141592653589793D;
    }

    public double getAngleRadians() {
        return Math.atan2(this.y, this.x);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public static Vector2D getVectorFromDegreesAndLength(double angle, double length) {
        return getVectorFromRadiansAndLength(angle * 3.141592653589793D / 180.0D, length);
    }

    public static Vector2D getVectorFromRadiansAndLength(double angle, double length) {
        return new Vector2D(length * Math.cos(angle), length * Math.sin(angle));
    }
}
