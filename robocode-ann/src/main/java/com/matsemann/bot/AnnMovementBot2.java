package com.matsemann.bot;

import com.matsemann.evolve.MovementAnn;
import com.matsemann.evolve.MovementScore;
import robocode.*;

import static com.matsemann.evolve.MovementScore.save;

public class AnnMovementBot2 extends AdvancedRobot {


    private final MovementAnn network;

    public AnnMovementBot2() {
        network = new MovementAnn();
        network.load();
    }

    @Override
    public void run() {
        while (true) {
            setTurnRight(20);
            execute();
        }
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        network.save();

        MovementScore score = new MovementScore();
        score.score = Math.random();
        score.ticks = event.getTime();
//
        save(score);
    }

}
