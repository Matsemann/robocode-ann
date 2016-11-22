package com.matsemann.util;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class OpponentMoves {

    public static final OpponentMoves NOTHING = new OpponentMoves();

    public long tick;
    public Vector pos = new Vector();
    public double heading;
    public double velocity;

    public double relativeHeading;
    public double relativeDistance;

    public OpponentMoves() {}

    public OpponentMoves(long tick, Vector pos, double heading, double velocity) {
        this.tick = tick;
        this.pos = pos;
        this.heading = heading;
        this.velocity = velocity;
    }

    public static OpponentMoves fromScan(AdvancedRobot robot, ScannedRobotEvent event) {
        double angle = event.getBearingRadians() + robot.getHeadingRadians();

        double x = robot.getX() + event.getDistance() * Math.sin(angle);
        double y = robot.getY() + event.getDistance() * Math.cos(angle);

        OpponentMoves move = new OpponentMoves(event.getTime(), new Vector(x, y), event.getHeadingRadians(), event.getVelocity());
        move.relativeHeading = event.getBearingRadians();
        move.relativeDistance = event.getDistance();
        return move;
    }
}
