package com.matsemann.component;

import com.matsemann.util.Vector;
import robocode.AdvancedRobot;
import robocode.util.Utils;

import static com.matsemann.util.MathUtil.*;

public class WallDistance {

    private static final double angleFromFront = PI_8;
    private AdvancedRobot robot;

    public Vector[] wallIntersections = new Vector[4];
    public double[] lengths = new double[4];

    public WallDistance(AdvancedRobot robot) {
        this.robot = robot;
    }

    public double[] calculateWallDistances() {
        Vector pos = new Vector(robot.getX(), robot.getY());
        double heading = robot.getHeadingRadians();
        double width = robot.getBattleFieldWidth();
        double height = robot.getBattleFieldHeight();
        double x = robot.getX();
        double y = robot.getY();

        double angle = PI_2 - heading;

        wallIntersections[0] = WallDistance.pointAtClosestWall(x, y, angle + angleFromFront, width, height);
        wallIntersections[1] = WallDistance.pointAtClosestWall(x, y, angle - angleFromFront, width, height);

        wallIntersections[2] = WallDistance.pointAtClosestWall(x, y, angle + angleFromFront + PI, width, height);
        wallIntersections[3] = WallDistance.pointAtClosestWall(x, y, angle - angleFromFront + PI, width, height);

        for (int i = 0; i < 4; i++) {
            lengths[i] = wallIntersections[i].sub(pos).getLength();
        }
        return lengths;
    }

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


}
