//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package steffen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D.Double;
import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;
import robocode.WinEvent;
import robocode.util.Utils;
import steffen.utils.Heading;
import steffen.utils.ScannedEnemy;
import steffen.utils.Vector2D;

public class PredictionBot extends AdvancedRobot {
    ScannedEnemy scannedEnemy;
    double driveDirection = 1.0D;
    double distanceFromWall = 100.0D;
    double optimalDistance = 300.0D;

    public PredictionBot() {
    }

    public void run() {
        this.setColors(Color.magenta, Color.cyan, Color.black);
        this.setBulletColor(Color.yellow);
        this.setAdjustRadarForGunTurn(true);
        this.setAdjustGunForRobotTurn(true);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double enemyHeading = 1.5707963267948966D - e.getHeadingRadians();
        Vector2D velocity = new Vector2D(Math.cos(enemyHeading) * e.getVelocity(), Math.sin(enemyHeading) * e.getVelocity());
        Vector2D myPosition = new Vector2D(this.getX(), this.getY());
        double myHeading = 1.5707963267948966D - this.getHeadingRadians();
        double bearing = myHeading - e.getBearingRadians();
        Vector2D distanceVector = new Vector2D(Math.cos(bearing) * e.getDistance(), Math.sin(bearing) * e.getDistance());
        Vector2D enemyPosition = myPosition.add(distanceVector);
        this.scannedEnemy = new ScannedEnemy(this.getTime(), enemyPosition, velocity);
        if(!this.isCloseToWall()) {
            this.setDrivingDirection(e);
        }

    }

    public void onStatus(StatusEvent e) {
        if(this.scannedEnemy != null && this.getTime() - this.scannedEnemy.getScannedTick() < 15L) {
            double gunHeading = 90.0D - this.getGunHeading();
            Vector2D distanceVector = this.getExpectedFirePosition().sub(this.getPosition());
            double bearing = distanceVector.getAngle();
            double gunTurn = Utils.normalRelativeAngleDegrees(bearing - gunHeading);
            this.setTurnGunLeft(gunTurn);
            if(Math.abs(this.getGunTurnRemaining()) < 5.0D) {
//                this.setFire(this.getFirepower(distanceVector.getLength())); // disabled firing
            }

            double radarHeading = 90.0D - this.getRadarHeading();
            double expectedEnemyBearing = this.scannedEnemy.getExpectedPosition(this.getTime()).sub(this.getPosition()).getAngle();
            double radarTurn = Utils.normalRelativeAngleDegrees(expectedEnemyBearing - radarHeading);
            radarTurn = radarTurn > 0.0D?40.0D:-40.0D;
            this.setTurnRadarLeft(radarTurn);
        } else {
            this.setTurnRadarRight(45.0D);
        }

        this.avoidHittingWalls();
        this.setAhead(100.0D * this.driveDirection);
        this.execute();
    }

    public void onWin(WinEvent e) {
        this.turnRight(20.0D);
        this.turnLeft(40.0D);
        this.turnRight(20.0D);
    }

    private double getFirepower(double distance) {
        return 400.0D / distance;
    }

    private Vector2D getPosition() {
        return new Vector2D(this.getX(), this.getY());
    }

    private Vector2D getExpectedFirePosition() {
        Vector2D currentEnemyPosition = this.scannedEnemy.getExpectedPosition(this.getTime());
        double distance = currentEnemyPosition.sub(this.getPosition()).getLength();
        double firepower = this.getFirepower(distance);
        double radius = this.getWidth() / 2.0D;
        long turnsToHit = (long)Math.ceil(distance / Rules.getBulletSpeed(firepower));
        Vector2D expectedFirePosition = this.scannedEnemy.getExpectedPosition(this.getTime() + turnsToHit);

        for(int i = 0; i < 10; ++i) {
            turnsToHit = (long)Math.ceil(expectedFirePosition.sub(this.getPosition()).getLength() / Rules.getBulletSpeed(firepower));
            Vector2D nextExpectedFirePosition = this.scannedEnemy.getExpectedPosition(this.getTime() + turnsToHit);
            if(nextExpectedFirePosition.getX() < radius) {
                nextExpectedFirePosition.setX(radius);
            }

            if(nextExpectedFirePosition.getY() < radius) {
                nextExpectedFirePosition.setY(radius);
            }

            if(nextExpectedFirePosition.getX() > this.getBattleFieldWidth() - radius) {
                nextExpectedFirePosition.setX(this.getBattleFieldWidth() - radius);
            }

            if(nextExpectedFirePosition.getY() > this.getBattleFieldHeight() - radius) {
                nextExpectedFirePosition.setX(this.getBattleFieldHeight() - radius);
            }

            if(nextExpectedFirePosition.sub(expectedFirePosition).getLength() < 5.0D) {
                return nextExpectedFirePosition;
            }

            expectedFirePosition = nextExpectedFirePosition;
        }

        return expectedFirePosition;
    }

    private boolean isCloseToWall() {
        double x = this.getX();
        double y = this.getY();
        return x < this.distanceFromWall || x > this.getBattleFieldWidth() - this.distanceFromWall || y < this.distanceFromWall || y > this.getBattleFieldHeight() - this.distanceFromWall;
    }

    private void setDrivingDirection(ScannedRobotEvent e) {
        boolean tooClose = this.optimalDistance > e.getDistance();
        double turnForNormalToRobot = e.getBearing() - 90.0D;
        double turnToFixDistance = 5.0D * this.driveDirection * (double)(tooClose?-1:1);
        this.setTurnRight((turnForNormalToRobot + turnToFixDistance * 3.0D) / 4.0D);
    }

    private void avoidHittingWalls() {
        double x = this.getX();
        double y = this.getY();
        double turnStrength = 45.0D;
        boolean shouldChangeDriveDirection = false;
        Heading heading = Heading.getHeadingForAngle(this.driveDirection == 1.0D?this.getHeading():this.getHeading() + 180.0D);
        if(x < this.distanceFromWall) {
            if(heading == Heading.NORTH_WEST || heading == Heading.NORTH) {
                this.setTurnRight(turnStrength);
            }

            if(heading == Heading.SOUTH_WEST || heading == Heading.SOUTH) {
                this.setTurnLeft(turnStrength);
            }

            if(heading == Heading.WEST) {
                shouldChangeDriveDirection = true;
            }
        }

        if(x > this.getBattleFieldWidth() - this.distanceFromWall) {
            if(heading == Heading.NORTH_EAST || heading == Heading.NORTH) {
                this.setTurnLeft(turnStrength);
            }

            if(heading == Heading.SOUTH_EAST || heading == Heading.SOUTH) {
                this.setTurnRight(turnStrength);
            }

            if(heading == Heading.EAST) {
                shouldChangeDriveDirection = true;
            }
        }

        if(y < this.distanceFromWall) {
            if(heading == Heading.SOUTH_WEST || heading == Heading.WEST) {
                this.setTurnRight(turnStrength);
            }

            if(heading == Heading.SOUTH_EAST || heading == Heading.EAST) {
                this.setTurnLeft(turnStrength);
            }

            if(heading == Heading.SOUTH) {
                shouldChangeDriveDirection = true;
            }
        }

        if(y > this.getBattleFieldHeight() - this.distanceFromWall) {
            if(heading == Heading.NORTH_WEST || heading == Heading.WEST) {
                this.setTurnLeft(turnStrength);
            }

            if(heading == Heading.NORTH_EAST || heading == Heading.EAST) {
                this.setTurnRight(turnStrength);
            }

            if(heading == Heading.NORTH) {
                shouldChangeDriveDirection = true;
            }
        }

        if(shouldChangeDriveDirection) {
            this.driveDirection = -this.driveDirection;
        }

    }

    public void onHitRobot(HitRobotEvent e) {
        this.driveDirection = -this.driveDirection;
    }

    public void onPaint(Graphics2D graphics) {
        double radius = this.getHeight() / 2.0D;
        Double antiCollisonCircle = new Double(this.getX() - radius, this.getY() - radius, 2.0D * radius, 2.0D * radius);
        graphics.setColor(new Color(255, 50, 50, 100));
        graphics.fill(antiCollisonCircle);
        graphics.setColor(Color.black);
        graphics.draw(antiCollisonCircle);
        if(this.scannedEnemy != null) {
            Vector2D enemyPosition = this.getExpectedFirePosition();
            Double expectedPositionCircle = new Double(enemyPosition.getX() - radius, enemyPosition.getY() - radius, 2.0D * radius, 2.0D * radius);
            graphics.setColor(new Color(255, 50, 50, 100));
            graphics.fill(expectedPositionCircle);
            graphics.setColor(Color.black);
            graphics.draw(expectedPositionCircle);
        }

    }
}
