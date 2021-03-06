package com.matsemann.evolve;

import com.matsemann.util.OpponentMoves;
import com.matsemann.util.RobotMoves;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.File;

import static com.matsemann.ann.BasicAnn.*;

public class MovementAnn {

    public static final String FILE_NAME = "movement.eg";
    private MLRegression network;

    public static final double TARGET_DISTANCE = 350;

    public MovementAnn() {
    }

    public MovementAnn(MLRegression network) {
        this.network = network;
    }

    public double[] getActions(RobotMoves robotMoves, OpponentMoves opponentMove, double[] wallDistances) {
        BasicMLData input = new BasicMLData(network.getInputCount());
        int c = 0;

        input.setData(c++, xNorm.normalize(robotMoves.pos.x));
        input.setData(c++, yNorm.normalize(robotMoves.pos.y));
        input.setData(c++, headNorm.normalize(robotMoves.heading));
        input.setData(c++, velNorm.normalize(robotMoves.velocity));

        input.setData(c++, angleNorm.normalize(opponentMove.relativeHeading));
        input.setData(c++, xNorm.normalize(opponentMove.relativeDistance));

        input.setData(c++, xNorm.normalize(wallDistances[0]));
        input.setData(c++, xNorm.normalize(wallDistances[1]));
        input.setData(c++, xNorm.normalize(wallDistances[2]));
        input.setData(c++, xNorm.normalize(wallDistances[3]));

        MLData output = network.compute(input);

        return new double[]{
                turnNorm.deNormalize(output.getData(0)),
                velNorm.deNormalize(output.getData(1))
        };
    }

    public void load() {
        network = (MLRegression) EncogDirectoryPersistence.loadObject(new File(FILE_NAME));
    }

    public void save() {
        EncogDirectoryPersistence.saveObject(new File(FILE_NAME), network);
    }
}
