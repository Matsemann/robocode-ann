package com.matsemann.util;

import robocode.util.Utils;

public class WallDistance {


    public static Vector pointAtClosestWall(double x, double y, double heading, double width, double height) {

        heading = Utils.normalRelativeAngle(heading);
        // y = ax + b
        double a = Math.tan(heading);
        double b = y - a * x;

        if (heading < 0) {
            double bottomCross = -b / a;
            if (bottomCross > 0 && bottomCross < width) {
                return new Vector(bottomCross, 0);
            }
        } else {
            double topCross = (height - b) / a;
            if (topCross > 0 && topCross < width) {
                return new Vector(topCross, height);
            }
        }

        if (heading > -Math.PI / 2 && heading < Math.PI / 2) {
            double rightCross = a*width + b;
            if (rightCross > 0 && rightCross < height) {
                return new Vector(width, rightCross);
            }
        } else {
            double leftCross = b;
            if (leftCross > 0 && leftCross < height) {
                return new Vector(0, leftCross);
            }

        }

        return new Vector();
    }

    public static void main(String[] args) {

//        System.out.println(distanceToClosestWall(0, 100, 0, 800, 600) + " == 600");
//        System.out.println(distanceToClosestWall(100, 100, Math.PI / 4, 800, 600) + " == 600, top crossing");
//        System.out.println(distanceToClosestWall(100, 100, -Math.PI / 4, 800, 600) + " == 200, bottom crossing");
//        System.out.println(distanceToClosestWall(100, 100, (3 * Math.PI) / 4, 800, 600) + " == 200, left crossing");
//        System.out.println(distanceToClosestWall(500, 100, (3 * Math.PI) / 4, 800, 600) + " == 0, left/top crossing");
//        System.out.println(distanceToClosestWall(500, 100, Math.PI / 4, 800, 600) + " == 400, left/top crossing");

    }

}
