package com.matsemann.util;

import robocode.AdvancedRobot;

public class RobotMoves {
    public long tick;
    public Vector pos;
    public double heading;
    public double velocity;

    public RobotMoves() {}

    public RobotMoves(long tick, Vector pos, double heading, double velocity) {
        this.tick = tick;
        this.pos = pos;
        this.heading = heading;
        this.velocity = velocity;
    }

    public static RobotMoves fromRobot(AdvancedRobot robot) {
        return new RobotMoves(robot.getTime(), new Vector(robot.getX(), robot.getY()), robot.getHeadingRadians(), robot.getVelocity());
    }
}
