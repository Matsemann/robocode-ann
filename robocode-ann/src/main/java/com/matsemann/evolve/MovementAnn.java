package com.matsemann.evolve;

import com.matsemann.ann.BasicAnn;
import com.matsemann.util.Vector;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.*;
import java.util.List;

import static com.matsemann.ann.BasicAnn.*;

public class MovementAnn {

    public static final String FILE_NAME = "movement.eg";
    private MLRegression network;

    public MovementAnn() {
    }

    public MovementAnn(MLRegression network) {
        this.network = network;
    }

    public double[] getActions(Vector pos, double heading, double velocity, double headingToOther, double distanceToOther, double frontwallDistance, double backwallDistance) {

        BasicMLData input = new BasicMLData(network.getInputCount());
        int c = 0;
//        for (Vector pos : prevPositions) {
            input.setData(c++, xNorm.normalize(pos.x));
            input.setData(c++, yNorm.normalize(pos.y));
//        }

        input.setData(c++, headNorm.normalize(heading));
        input.setData(c++, velNorm.normalize(velocity));

        input.setData(c++, angleNorm.normalize(headingToOther));
        input.setData(c++, xNorm.normalize(distanceToOther));
//        input.setData(c++, xNorm.normalize(otherX));
//        input.setData(c++, yNorm.normalize(otherY));
        input.setData(c++, xNorm.normalize(frontwallDistance));
        input.setData(c++, xNorm.normalize(backwallDistance));

        MLData output = network.compute(input);

        return new double[] {
            turnNorm.deNormalize(output.getData(0)),
            velNorm.deNormalize(output.getData(1))
        };
    }

    public void load() {
        try {
            ObjectInput in = new ObjectInputStream(new FileInputStream(FILE_NAME));
            network = (MLRegression) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        network = (NEATNetwork) EncogDirectoryPersistence.loadObject(new File(FILE_NAME));
    }

    public void save() {

            try {
                ObjectOutput out = new ObjectOutputStream(new FileOutputStream(FILE_NAME));
                out.writeObject(network);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        EncogDirectoryPersistence.saveObject(new File(FILE_NAME), network);
    }
}
