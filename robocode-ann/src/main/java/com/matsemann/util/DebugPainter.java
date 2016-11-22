package com.matsemann.util;

import robocode.AdvancedRobot;

import java.awt.*;

import static com.matsemann.evolve.MovementAnn.TARGET_DISTANCE;

public class DebugPainter {


    private final AdvancedRobot robot;
    private WallDistance wallDistance;
    private final Vector opponentPos;

    public DebugPainter(AdvancedRobot robot, WallDistance wallDistance, Vector opponentPos) {
        this.robot = robot;
        this.wallDistance = wallDistance;
        this.opponentPos = opponentPos;
    }

    public void paint(Graphics2D g) {
        Vector pos = new Vector(robot.getX(), robot.getY());

        drawWall(g, pos, wallDistance.wallIntersections[0], new Color(71, 182, 108));
        drawWall(g, pos, wallDistance.wallIntersections[1], new Color(71, 182, 108));
        drawWall(g, pos, wallDistance.wallIntersections[2], new Color(183, 97, 69));
        drawWall(g, pos, wallDistance.wallIntersections[3], new Color(183, 97, 69));
        drawToOpponent(g, pos);
    }

    private void drawWall(Graphics2D g, Vector pos, Vector wall, Color c) {
        double distance = pos.sub(wall).getLength();

        if (distance < 100) {
            c = c.darker().darker();
        }

        double lineWidth = Math.max(Math.min(300 / distance, 4), 0.1);

        g.setColor(c);
        drawLine(g, pos, wall, lineWidth);
        drawCircle(g, wall, 20);
    }

    private void drawToOpponent(Graphics2D g, Vector pos) {
        double distance = pos.sub(opponentPos).getLength();
        double lineWidth = .6;
        Color c = new Color(227, 228, 3);

        if (distance > TARGET_DISTANCE + 100 || distance < TARGET_DISTANCE - 100) {
            lineWidth = 3;
            c = new Color(32, 31, 9);
        }
        g.setColor(c);
        drawLine(g, pos, opponentPos, lineWidth);
        drawCircle(g, opponentPos, 5);
    }

    private void drawCircle(Graphics2D g, Vector pos, int r) {
        g.fillOval((int) pos.x - r/2, (int) pos.y - r/2, r, r);
    }

    private void drawLine(Graphics2D g, Vector pos1, Vector pos2, double width) {
        g.setStroke(new BasicStroke((float) width));
        g.drawLine((int) pos1.x, (int) pos1.y, (int) pos2.x, (int) pos2.y);
    }
}
