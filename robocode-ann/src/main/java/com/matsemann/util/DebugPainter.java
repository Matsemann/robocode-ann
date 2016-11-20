package com.matsemann.util;

import robocode.AdvancedRobot;

import java.awt.*;

public class DebugPainter {


    private final AdvancedRobot robot;
    private final Vector frontWAll;
    private final Vector behindWall;
    private final Vector lastSeen;

    public DebugPainter(AdvancedRobot robot, Vector frontWAll, Vector behindWall, Vector lastSeen) {
        this.robot = robot;
        this.frontWAll = frontWAll;
        this.behindWall = behindWall;
        this.lastSeen = lastSeen;
    }

    public void paint(Graphics2D g) {

        Vector pos = new Vector(robot.getX(), robot.getY());

        drawWall(g, pos, frontWAll, new Color(71, 182, 108));
        drawWall(g, pos, behindWall, new Color(183, 97, 69));
    }

    private void drawWall(Graphics2D g, Vector pos, Vector wall, Color c) {
        double length = pos.sub(wall).getLength();

        if (length < 100) {
            c = c.darker().darker();
        }

        double lineWidth = Math.max(Math.min(300 / length, 4), 0.1);

        g.setColor(c);
        drawLine(g, pos, wall, lineWidth);
        drawCircle(g, wall, 20);
    }

    private void drawCircle(Graphics2D g, Vector pos, int r) {
        g.fillOval((int) pos.x - r/2, (int) pos.y - r/2, r, r);
    }

    private void drawLine(Graphics2D g, Vector pos1, Vector pos2, double width) {
        g.setStroke(new BasicStroke((float) width));
        g.drawLine((int) pos1.x, (int) pos1.y, (int) pos2.x, (int) pos2.y);
    }
}
