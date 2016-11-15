//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package steffen.utils;

import steffen.utils.Vector2D;

public class ScannedEnemy {
    private long scannedTick;
    private Vector2D position;
    private Vector2D velocity;

    public ScannedEnemy(long tick, Vector2D position, Vector2D velocity) {
        this.scannedTick = tick;
        this.position = position;
        this.velocity = velocity;
    }

    public Vector2D getExpectedPosition(long currentTick) {
        int timeSinceScan = (int)(currentTick - this.scannedTick);
        return this.velocity.multiply((double)timeSinceScan).add(this.position);
    }

    public long getScannedTick() {
        return this.scannedTick;
    }
}
