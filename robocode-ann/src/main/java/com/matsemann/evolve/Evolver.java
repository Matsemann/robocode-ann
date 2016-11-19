package com.matsemann.evolve;

import org.encog.Encog;
import org.encog.ml.ea.train.basic.TrainEA;
import org.encog.neural.neat.NEATUtil;

public class Evolver {

    public static void main(String[] args) {
//        BasicTraining t = new MLMethodGeneticAlgorithm();
//
//        NEATPopulation neatPopulation = new NEATPopulation();

        TrainEA trainEA = NEATUtil.constructNEATTrainer(new RobotEvaluator(), 6, 2, 20);

        long start = System.currentTimeMillis();

        trainEA.setThreadCount(1);
//        for (int i = 0; i < 10; i++) {
            trainEA.iteration(26);
//        }
        System.out.println("time: " + (System.currentTimeMillis() - start));
        // 110 kamper på 16sek
        // 120 på 12sek med flere i samme
        // 150 på 12 sek med 5 i samme
        Encog.getInstance().shutdown();
    }


}
