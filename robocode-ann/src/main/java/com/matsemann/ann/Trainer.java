package com.matsemann.ann;

import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.temporal.TemporalDataDescription;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.data.temporal.TemporalPoint;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;
import org.encog.util.simple.EncogUtility;

import java.io.File;
import java.util.List;

import static com.matsemann.ann.BasicAnn.*;

/**
 * Created by mats kruger svensson on 08/11/16.
 */
public class Trainer {


    public static void main(String[] args) {

        BasicMLDataSet trainingData = new BasicMLDataSet();

        for (int i = 0; i < 10; i++) {
            List<MLDataPair> mlDataPairs = loadFromFile("././testsets/walls_" + i + ".data");
            for (MLDataPair pair : mlDataPairs) {
                trainingData.add(pair);
            }
        }


        int inputSize = trainingData.getInputSize();
        int outputSize = trainingData.getIdealSize();


        BasicNetwork network = createNetwork(inputSize, outputSize);

        ResilientPropagation train = new ResilientPropagation(network, trainingData);
        EncogUtility.trainToError(train, 0.001);

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

        TemporalMLDataSet dataSet = createDataSet();

        for (MovementData.Movement m : movementData.movements) {
            TemporalPoint tmp = createTemporalPoint(m);
            dataSet.getPoints().add(tmp);
        }

        dataSet.generate();

        return dataSet.getData();
    }

    public static TemporalPoint createTemporalPoint(MovementData.Movement m) {
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
