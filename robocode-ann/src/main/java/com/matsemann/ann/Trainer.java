package com.matsemann.ann;

import org.encog.Encog;
import org.encog.ml.data.MLDataPair;
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

import static com.matsemann.ann.BasicAnn.*;

/**
 * Created by mats kruger svensson on 08/11/16.
 */
public class Trainer {


    public static void main(String[] args) {

        MovementData movementData = new MovementData();
        movementData.load("./test.data");

//        double lowest = 10000, highest = -10000;
//        for (MovementData.Movement m : movementData.movements) {
//            if (m.velocity < lowest) {
//                lowest = m.velocity;
//            } else if (m.velocity > highest) {
//                highest = m.velocity;
//            }
//        }
//        System.out.println(lowest + " " + highest);

        TemporalMLDataSet dataSet = createDataSet();

        for (MovementData.Movement m : movementData.movements) {

            TemporalPoint tmp = new TemporalPoint(4);
            tmp.setData(new double[]{
                    xNorm.normalize(m.x),
                    yNorm.normalize(m.y),
                    headNorm.normalize(m.heading),
                    velNorm.normalize((m.velocity))
            });
            dataSet.getPoints().add(tmp);
        }

        dataSet.generate();
        int inputSize = dataSet.getInputSize();
        int outputSize = dataSet.getIdealSize();


        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(inputSize));
        network.addLayer(new BasicLayer(30));
        network.addLayer(new BasicLayer(outputSize));
        network.getStructure().finalizeStructure();
        network.reset();

// out :[0.7652505899324694, 0.5521298086217646, 0.7684006412462274, 0.5497299515853055, 0.7693786459873886, 0.5568560873022652, 0.7791310224316942, 0.5566603654727099, 0.7773421402520905, 0.5588592359271354]



//        MLDataPair mlDataPair = dataSet.get(0);
//        BasicNeuralData out = dataSet.generateOutputNeuralData(1);
//        BasicNeuralData in = dataSet.generateInputNeuralData(1);


//        mlMethodFactory.create()
//        mlMethodFactory.create()


        ResilientPropagation train = new ResilientPropagation(network, dataSet);
        EncogUtility.trainToError(train, 0.0001);

        EncogDirectoryPersistence.saveObject(new File(FILE_NAME), network);

        Encog.getInstance().shutdown();
//        System.out.println(movementData.movements.size());
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
}
