package com.matsemann.evolve;

import org.encog.Encog;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.MethodFactory;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.genetic.MLMethodGeneticAlgorithm;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

import java.util.List;

public class EAEvolver {

    public static void main(String[] args) {


//        EvolutionaryAlgorithm ea = new TrainEA(new );

        RobotEvaluator evaluator = new RobotEvaluator();
        MLMethodGeneticAlgorithm train = new MLMethodGeneticAlgorithm(new MyMethodFactory(), evaluator, 30);


//        train.setThreadCount(1);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
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

    private static class MyMethodFactory implements MethodFactory {
        @Override
        public MLMethod factor() {
            BasicNetwork basicNetwork = new BasicNetwork();
            basicNetwork.addLayer(new BasicLayer(8));
//            basicNetwork.addLayer(new BasicLayer(6));
            basicNetwork.addLayer(new BasicLayer(2));
            basicNetwork.getStructure().finalizeStructure();
            basicNetwork.reset();
            return basicNetwork;
//            return FreeformNetwork.createElman(8, 12, 2, new ActivationTANH());
        }
    }
}