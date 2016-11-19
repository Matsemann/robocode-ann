package com.matsemann.util;

import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class Tracker {

    private AdvancedRobot robot;
    private boolean scanHit;

    public Tracker(AdvancedRobot robot) {
        this.robot = robot;
    }

    public void init() {
        robot.setAdjustRadarForRobotTurn(true);
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
        double absoluteBearing = robot.getHeading() + e.getBearing();
        double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - robot.getRadarHeading());

        // Some wiggle
        if (bearingFromGun > 0) {
            bearingFromGun += 5;
        } else {
            bearingFromGun -= 5;
        }

        robot.setTurnRadarRight(bearingFromGun);
        scanHit = true;
    }
}
