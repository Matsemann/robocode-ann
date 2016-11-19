package com.matsemann.ann;

import com.matsemann.ann.MovementData.Movement;
import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.temporal.TemporalDataDescription;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.data.temporal.TemporalPoint;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.matsemann.ann.BasicAnn.*;

/**
 * Created by mats kruger svensson on 08/11/16.
 */
public class Trainer {


    public static void main(String[] args) {

        BasicMLDataSet trainingData = new BasicMLDataSet();

        for (int i = 0; i < 100; i++) {
            List<MLDataPair> mlDataPairs = loadFromFile("././testsets/" + "steffen" + "_" + i + ".data");
            for (MLDataPair pair : mlDataPairs) {
                trainingData.add(pair);
            }
        }


        int inputSize = trainingData.getInputSize();
        int outputSize = trainingData.getIdealSize();


        BasicNetwork network = createNetwork(inputSize, outputSize);

        ResilientPropagation train = new ResilientPropagation(network, trainingData);
        EncogUtility.trainToError(train, 0.01);

        EncogDirectoryPersistence.saveObject(new File(FILE_NAME), network);

        Encog.getInstance().shutdown();
    }

    public static BasicNetwork createNetwork(int inputSize, int outputSize) {
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(inputSize));
        network.addLayer(new BasicLayer(30));
        network.addLayer(new BasicLayer(outputSize));
        network.getStructure().finalizeStructure();
        network.reset();
        return network;
    }

    public static TemporalMLDataSet createDataSet() {
        TemporalMLDataSet dataSet = new TemporalMLDataSet(BasicAnn.WINDOW_SIZE, BasicAnn.PREDICTION_LENGTH);
        TemporalDataDescription descX = new TemporalDataDescription(TemporalDataDescription.Type.RAW, true, true);
        TemporalDataDescription descY = new TemporalDataDescription(TemporalDataDescription.Type.RAW, true, true);
        TemporalDataDescription heading = new TemporalDataDescription(TemporalDataDescription.Type.RAW, true, false);
        TemporalDataDescription velocity = new TemporalDataDescription(TemporalDataDescription.Type.RAW, true, false);

        dataSet.addDescription(descX);
        dataSet.addDescription(descY);
        dataSet.addDescription(heading);
        dataSet.addDescription(velocity);
        return dataSet;
    }

    public static List<MLDataPair> loadFromFile(String fileName) {

        MovementData movementData = new MovementData();
        movementData.load(fileName);
        return createTrainingDataFromMovements(movementData.movements);

//        TemporalMLDataSet dataSet = createDataSet();
//
//        for (Movement m : movementData.movements) {
//            TemporalPoint tmp = createTemporalPoint(m);
//            dataSet.getPoints().add(tmp);
//        }
//
//        dataSet.generate();

//        return dataSet.getData();
    }

    public static List<MLDataPair> createTrainingDataFromMovements(List<Movement> movements) {

        List<MLDataPair> training = new ArrayList<>();
        // 30 data => lastInput: 25

        // 0 - 20 + 25
        // 1 - 21 + 26
        // 2 - 22 + 27
        // 3 - 23 + 28
        // 4 - 24 + 29
        int lastInput = movements.size() - PREDICTION_FUTURE;

        for (int i = WINDOW_SIZE; i < lastInput; i++) {
            List<Movement> history = movements.subList(i - WINDOW_SIZE, i);
            Movement correct = movements.get(i + PREDICTION_FUTURE - 1);
            MLDataPair trainingData = createTrainingData(history, correct);
            training.add(trainingData);
        }

        return training;
    }

    public static MLDataPair createTrainingData(List<Movement> input, Movement correct) {

        BasicMLData in = createInput(input);
        BasicMLData out = createOutput(correct);

        BasicMLDataPair dataPair = new BasicMLDataPair(in, out);
        return dataPair;
    }

    public static BasicMLData createInput(List<Movement> data) {
        double[] input = new double[WINDOW_SIZE * 4 + 2];

        int index = 0;
        for (Movement m : data) {
            input[index++] = xNorm.normalize(m.x);
            input[index++] = yNorm.normalize(m.y);
            input[index++] = headNorm.normalize(m.heading);
            input[index++] = velNorm.normalize(m.velocity);
        }

        Movement lastMovement = data.get(data.size() - 1);
        input[index++] = xNorm.normalize(lastMovement.myX);
        input[index++] = yNorm.normalize(lastMovement.myY);

        return new BasicMLData(input);
    }

    public static BasicMLData createOutput(Movement correct) {
        return new BasicMLData(new double[]{
                xNorm.normalize(correct.x),
                yNorm.normalize(correct.y)});
    }

    public static TemporalPoint createTemporalPoint(Movement m) {
        TemporalPoint tmp = new TemporalPoint(4);
        tmp.setData(new double[]{
                xNorm.normalize(m.x),
                yNorm.normalize(m.y),
                headNorm.normalize(m.heading),
                velNorm.normalize((m.velocity))
        });
        return tmp;
    }
}
