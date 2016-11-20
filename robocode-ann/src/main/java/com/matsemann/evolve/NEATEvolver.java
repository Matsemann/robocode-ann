package com.matsemann.evolve;

import org.encog.Encog;
import org.encog.ml.MLMethod;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATNetwork;

public class NEATEvolver {

    public static void main(String[] args) {
        RobotEvaluator evaluator = new RobotEvaluator();
        TrainEA trainEA = NEATUtil.constructNEATTrainer(evaluator, 8, 2, 40);

        long start = System.currentTimeMillis();

        trainEA.setThreadCount(1);
        for (int i = 0; i < 100; i++) {
            trainEA.iteration(1);

            System.out.println("----------------------------");
            System.out.println("----------------------------");
            System.out.println("NEW ITERATION: " + (i + 1));
            System.out.println("----------------------------");
            System.out.println("best so far is: " + trainEA.getError());
            System.out.println("----------------------------");
        }

        MLMethod best = trainEA.getCODEC().decode(trainEA.getBestGenome());
        MovementAnn movementAnn = new MovementAnn((NEATNetwork) best);
        movementAnn.save();
        evaluator.showCase(best);

//        }
        System.out.println("time: " + (System.currentTimeMillis() - start));
        Encog.getInstance().shutdown();
    }


}
