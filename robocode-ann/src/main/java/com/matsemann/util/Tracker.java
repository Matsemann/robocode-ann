package com.matsemann.util;

import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class Tracker {

    private AdvancedRobot robot;
    private boolean scanHit;
    int wiggle = 10;

    public Tracker(AdvancedRobot robot) {
        this.robot = robot;
    }

    public void setup() {
        robot.setAdjustGunForRobotTurn(true);
        robot.setAdjustRadarForGunTurn(true);
    }

    public void scan() {
        robot.setTurnRadarRight(Rules.GUN_TURN_RATE);
    }

    public void execute() {
        if (!scanHit) {
            scan();
        }
        scanHit = false;
    }

    public void onScan(ScannedRobotEvent e) {
        double angleToRobot = robot.getHeading() + e.getBearing();
        double angleForRadar = normalRelativeAngleDegrees(angleToRobot - robot.getRadarHeading());

        angleForRadar += angleForRadar > 0 ? wiggle : -wiggle;

        robot.setTurnRadarRight(angleForRadar);
        scanHit = true;
    }
}
