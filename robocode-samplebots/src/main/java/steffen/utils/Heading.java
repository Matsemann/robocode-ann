//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package steffen.utils;

import robocode.util.Utils;

public enum Heading {
    NORTH(0.0D),
    NORTH_EAST(45.0D),
    EAST(90.0D),
    SOUTH_EAST(135.0D),
    SOUTH(180.0D),
    SOUTH_WEST(225.0D),
    WEST(270.0D),
    NORTH_WEST(315.0D);

    public double angle;

    private Heading(double angle) {
        this.angle = angle;
    }

    public boolean containsDirection(double angle) {
        double absoluteAngle = Utils.normalAbsoluteAngleDegrees(angle);
        double anglediff = (absoluteAngle - this.angle + 180.0D + 360.0D) % 360.0D - 180.0D;
        return anglediff > -22.5D && anglediff <= 22.5D;
    }

    public static Heading getHeadingForAngle(double angle) {
        double absoluteAngle = Utils.normalAbsoluteAngleDegrees(angle);
        Heading[] var4 = values();
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Heading heading = var4[var6];
            if(heading.containsDirection(absoluteAngle)) {
                return heading;
            }
        }

        return NORTH;
    }
}
