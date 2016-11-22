package com.matsemann.evolve;

import org.encog.Encog;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.MethodFactory;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EAEvolver {

    public static void main(String[] args) {
        System.setProperty("NOSECURITY", "true");

        // Make the robo code scenarios equal for everyone
        long seed = new Random().nextLong();
//        seed = 1; // 24191.54670550626 - 28901 ticks
        System.out.println("Seed is: " + seed);
        System.setProperty("RANDOMSEED", seed + "");


        RobotEvaluator evaluator = new RobotEvaluator("com.matsemann.bot.AnnMovementBot*", Arrays.asList("sample.Walls", "sample.Crazy", "steffen.PredictionBot*"));
        int population = 20;
        int generations = 15;

        MLMethodGeneticAlgorithm train = new MLMethodGeneticAlgorithm(new MovementAnnNetworkFactory(), evaluator, population);


        train.setThreadCount(1);
        long start = System.currentTimeMillis();
        for (int i = 0; i < generations; i++) {
            System.out.println("----------------------------");
            System.out.println("----------------------------");
            System.out.println("NEW ITERATION: " + (i + 1));
            evaluator.preIteration();
            train.iteration();

            System.out.println("----------------------------");
            System.out.println("best so far is: " + train.getError());
            List<Genome> members = train.getGenetic().getPopulation().getSpecies().get(0).getMembers();
            double avg = 0;
            for (Genome member : members) {
                avg += member.getScore();
            }
            avg = avg / members.size();
            System.out.println("Avg is: " + avg);
            System.out.println("Time used: " + (System.currentTimeMillis() - start));
            evaluator.postIteration();

            System.out.println("----------------------------");
            start = System.currentTimeMillis();

            boolean shouldBreak = false; // edit this in debugger
            if (shouldBreak) {
                break;
            }
        }

        MLRegression best = (MLRegression) train.getMethod();

        MovementAnn movementAnn = new MovementAnn(best);
        movementAnn.save();

        evaluator.showCase(best);

        Encog.getInstance().shutdown();

    }

    private static class MovementAnnNetworkFactory implements MethodFactory {
        @Override
        public MLMethod factor() {
            BasicNetwork basicNetwork = new BasicNetwork();
            basicNetwork.addLayer(new BasicLayer(10));
//            basicNetwork.addLayer(new BasicLayer(5));
            basicNetwork.addLayer(new BasicLayer(2));
            basicNetwork.getStructure().finalizeStructure();
            basicNetwork.reset();
            return basicNetwork;
//            return FreeformNetwork.createElman(8, 12, 2, new ActivationTANH());
        }
    }
}
