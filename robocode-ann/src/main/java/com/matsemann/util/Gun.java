package com.matsemann.util;

import com.matsemann.ann.BasicAnn.Prediction;
import robocode.AdvancedRobot;
import robocode.util.Utils;

public class Gun {
    private AdvancedRobot robot;

    /*
    time to hit t = PREDICTION_FUTURE + TICK_LENGTH = 25

    Bullet velocity v = 20 - 3*firepower
    Wish to hit a distance d at time t

    v*t = d

    want to solve for firepower to use

    v = d / t
    20 - 3f = d / t


     */

    public Gun(AdvancedRobot robot) {
        this.robot = robot;
    }

    public void pointAndShoot(Prediction prediction) {
        double angleToTurn = getAngleToTurnGun(prediction);

        robot.setTurnGunLeftRadians(angleToTurn);
        robot.setDebugProperty("Turning", angleToTurn + "");


        if (Math.abs(angleToTurn) < 0.087) {
            int timeDiff = (int) (prediction.tick - robot.getTime());
            double firepower = getFirepowerForDistance(timeDiff, distanceTo(prediction.x, prediction.y));
            robot.setDebugProperty("firepower", firepower + "");

            if (firepower < 3.2) { // If it's much larger than three, we will miss as the power gets capped
                robot.setFire(firepower);
            }
        }
    }

    private double getAngleToTurnGun(Prediction prediction) {
        double gunHeadingRadians = robot.getGunHeadingRadians();
        return getAngleToTurnGun(robot.getX(), robot.getY(), prediction.x, prediction.y, gunHeadingRadians);
    }

    private double getAngleToTurnGun(double roboX, double roboY, double predX, double predY, double gunHeadingRadians) {
        double gunAngle = Math.PI/2 - gunHeadingRadians;
        double relativeAngle = Math.atan2(predY - roboY, predX - roboX);
        double totalAngle = relativeAngle - gunAngle;
        return Utils.normalRelativeAngle(totalAngle);
    }

    private double getFirepowerForDistance(int time, double distance) {
        return (20.0 / 3.0) - (distance / ((double) time * 3));
    }

    private double distanceTo(double x, double y) {
        return Math.sqrt( Math.pow(robot.getX() - x, 2) + Math.pow(robot.getY() - y, 2) );
    }

    public static void main(String[] args) {
        double angleToTurnGun = new Gun(null).getAngleToTurnGun(0, 0, 300, 300, Math.toRadians(270));
        System.out.println(Math.toDegrees(angleToTurnGun));
    }
}
