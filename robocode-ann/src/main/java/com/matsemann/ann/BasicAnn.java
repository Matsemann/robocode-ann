package com.matsemann.ann;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mats kruger svensson on 08/11/16.
 */
public class BasicAnn {

    public static final int WINDOW_SIZE = 30;
    public static final int PREDICTION_LENGTH = 5;

    public static final NormalizedField xNorm = new NormalizedField(NormalizationAction.Normalize, "x", 800, 0, 1, -1);
    public static final NormalizedField yNorm = new NormalizedField(NormalizationAction.Normalize, "y", 600, 0, 1, -1);
    public static final NormalizedField headNorm = new NormalizedField(NormalizationAction.Normalize, "head", 2 * Math.PI, 0, 1, -1);
    public static final NormalizedField velNorm = new NormalizedField(NormalizationAction.Normalize, "vel", 8, -8, 1, -1);


    public static final String FILE_NAME = "predict.eg";

    private final BasicNetwork network;
    private final TemporalMLDataSet dataSet;

    public BasicAnn() {
        network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(FILE_NAME));
        dataSet = Trainer.createDataSet();
    }

    public List<Prediction> getPredictions(MovementData data) {
        ArrayList<Prediction> predictions = new ArrayList<>();

//        if (data.movements.size() > WINDOW_SIZE) {
//            List<MovementData.Movement> movements = data.movements.subList(data.movements.size() - 20, data.movements.size());


            // TODO bruk dataSet?

            int inputCount = network.getInputCount();
            BasicMLData input = new BasicMLData(inputCount);

        int c = 0;
        for (int i = 0; i < WINDOW_SIZE; i++) {
            MovementData.Movement m = data.movements.get(i);

            input.add(c++, xNorm.normalize(m.x));
            input.add(c++, yNorm.normalize(m.y));
            input.add(c++, headNorm.normalize(m.heading));
            input.add(c++, velNorm.normalize(m.velocity));
        }

        MLData out = network.compute(input);

        double[] outData = out.getData();
        System.out.println("out :" + Arrays.toString(outData));

        for (int i = 0; i < 5; i++) {
            double preX = outData[i*2];
            double preY = outData[i*2 + 1];

            MovementData.Movement m = data.movements.get(WINDOW_SIZE + i);
            System.out.println(i + "  -----------");
            System.out.println("nx: " + preX + ", ny: " + preY);
            System.out.println("nx: " + xNorm.normalize(m.x) + ", ny: " + yNorm.normalize(m.y));
            System.out.println("x: " + xNorm.deNormalize(preX) + ", y: " + yNorm.deNormalize(preY));
            System.out.println("x: " + m.x + ", y: " + m.y);
        }

//        dataSet.generateInputNeuralData()

//            input.setData(new double[]{
//
//            });

//        }

        return predictions;
    }

    public static void main(String[] args) {
        BasicAnn basicAnn = new BasicAnn();

        MovementData data = new MovementData();
        data.load("./test.data");

        basicAnn.getPredictions(data);
    }

    public static class Prediction {
        double x, y;
    }

}
