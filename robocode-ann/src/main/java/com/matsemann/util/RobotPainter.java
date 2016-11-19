package com.matsemann.util;

import robocode.AdvancedRobot;

import java.awt.*;

public class RobotPainter {

    private AdvancedRobot robot;

    private Color main = new Color(250, 165, 26);
    private Color secondary = Color.BLACK;

    public RobotPainter(AdvancedRobot robot) {
        this.robot = robot;
    }

    public void paint() {
        robot.setBodyColor(main);
        robot.setScanColor(main);
        robot.setGunColor(secondary);
        robot.setRadarColor(secondary);
    }

    public void paintInverse() {
        robot.setBodyColor(secondary);
        robot.setScanColor(secondary);
        robot.setGunColor(main);
        robot.setRadarColor(main);
    }

    public void celebrate() {
        for (int i = 0; i < 10; i++) {
            paintInverse();
            robot.turnLeft(20);
            paint();
            robot.turnRight(20);
        }
    }
}
