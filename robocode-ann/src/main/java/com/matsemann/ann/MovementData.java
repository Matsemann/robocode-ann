package com.matsemann.ann;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mats kruger svensson on 08/11/16.
 */
public class MovementData {

    public List<Movement> movements = new ArrayList<>();

    public void add(Movement movement) {
        movements.add(movement);
    }
//
//    public void add(int tick, double x, double y, double heading, double velocity) {
//        movements.add(new Movement(tick, x, y, heading, velocity));
//    }

    //Stores data to file using Java built-in serialization.
    public void save(String dirPath, String fileName) {
        try {
            ObjectOutput out = new ObjectOutputStream(new FileOutputStream(dirPath + "/" + fileName));
            out.writeObject(movements);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Loads data from a file serialized by Java built-in serialization.
    public void load(String path) {
        try {
            ObjectInput in = new ObjectInputStream(new FileInputStream(path));
            movements = (List<Movement>) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static class Movement implements Serializable {
        public long tick;
        public double x, y, heading, velocity, myX, myY;

        public Movement() {
        }

        public Movement(long tick, double x, double y, double heading, double velocity, double myX, double myY) {
            this.tick = tick;
            this.x = x;
            this.y = y;
            this.heading = heading;
            this.velocity = velocity;
            this.myX = myX;
            this.myY = myY;
        }
    }

}
