package com.matsemann.evolve;

import com.matsemann.ann.BasicAnn;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.*;

import static com.matsemann.ann.BasicAnn.*;

public class MovementAnn {

    public static final String FILE_NAME = "movement.eg";
    private NEATNetwork network;

    public MovementAnn() {
    }

    public MovementAnn(NEATNetwork network) {
        this.network = network;
    }

    public double[] getActions(double x, double y, double heading, double otherX, double otherY, double wallDistance) {
        double[] input = {
                xNorm.normalize(x),
                yNorm.normalize(y),
                headNorm.normalize(heading),
                xNorm.normalize(otherX),
                yNorm.normalize(otherY),
                xNorm.normalize(wallDistance)
        };

        MLData output = network.compute(new BasicMLData(input));

        return new double[] {
            turnNorm.deNormalize(output.getData(0)),
            headNorm.deNormalize(output.getData(1))
        };
    }

    public void load() {
        try {
            ObjectInput in = new ObjectInputStream(new FileInputStream(FILE_NAME));
            network = (NEATNetwork) in.readObject();
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
