package com.matsemann.ann;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.data.temporal.TemporalPoint;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.matsemann.ann.Trainer.createNetwork;
import static com.matsemann.ann.Trainer.createTemporalPoint;

/**
 * Created by mats kruger svensson on 08/11/16.
 */
public class BasicAnn {

    public static final int WINDOW_SIZE = 10;
    public static final int PREDICTION_LENGTH = 5;

    public static final NormalizedField xNorm = new NormalizedField(NormalizationAction.Normalize, "x", 800, 0, 1, -1);
    public static final NormalizedField yNorm = new NormalizedField(NormalizationAction.Normalize, "y", 600, 0, 1, -1);
    public static final NormalizedField headNorm = new NormalizedField(NormalizationAction.Normalize, "head", 2 * Math.PI, 0, 1, -1);
    public static final NormalizedField velNorm = new NormalizedField(NormalizationAction.Normalize, "vel", 8, -8, 1, -1);


    public static final String FILE_NAME = "predict.eg";

    private final BasicNetwork network;
//    private final TemporalMLDataSet dataSet;

    public BasicAnn() {
        network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(FILE_NAME));
//        network = createNetwork(40, 10);
    }

    public void train(List<MovementData.Movement> movements) {
        if (movements.size() <= WINDOW_SIZE + PREDICTION_LENGTH) {
            return;
        }

        TemporalMLDataSet dataSet = Trainer.createDataSet();
        for (MovementData.Movement m : movements) {
            TemporalPoint tmp = createTemporalPoint(m);
            dataSet.getPoints().add(tmp);
        }
        dataSet.generate();

        ResilientPropagation training = new ResilientPropagation(network, dataSet);
        training.setThreadCount(1);
        training.iteration(20);
        training.finishTraining();
    }

    public List<Prediction> getPredictions(List<MovementData.Movement> movements) {
        ArrayList<Prediction> predictions = new ArrayList<>();


        if (movements.size() < WINDOW_SIZE) {
            return predictions;
        }

        int inputCount = network.getInputCount();
        BasicMLData input = new BasicMLData(inputCount);


        int c = 0;
        for (MovementData.Movement m : movements) {
            input.add(c++, xNorm.normalize(m.x));
            input.add(c++, yNorm.normalize(m.y));
            input.add(c++, headNorm.normalize(m.heading));
            input.add(c++, velNorm.normalize(m.velocity));
        }


        MLData out = network.compute(input);

        double[] outData = out.getData();

        for (int i = 0; i < PREDICTION_LENGTH; i++) {
            double preX = outData[i * 2];
            double preY = outData[i * 2 + 1];

            Prediction prediction = new Prediction(xNorm.deNormalize(preX), yNorm.deNormalize(preY));

            predictions.add(prediction);
        }

        return predictions;
    }

    public static void main(String[] args) {
        BasicAnn basicAnn = new BasicAnn();

        MovementData data = new MovementData();
        data.load("./test.data");

        List<MovementData.Movement> movements = data.movements.subList(data.movements.size() - WINDOW_SIZE - 50, data.movements.size() - 50);
        List<MovementData.Movement> correct = data.movements.subList(data.movements.size() - 50, data.movements.size());

        System.out.println();


        List<Prediction> predictions = basicAnn.getPredictions(movements);

        for (int i = 0; i < 5; i++) {
            MovementData.Movement c = correct.get(i);
            Prediction p = predictions.get(i);

            System.out.println(i + " ----------");
            System.out.println("pre: " + p.x + ", " + p.y);
            System.out.println("cor: " + c.x + ", " + c.y);
        }
    }

    public static class Prediction {
        public double x, y;

        public Prediction(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

}
