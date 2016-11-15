package com.matsemann;

import robocode.control.events.*;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;


class BattleListener extends BattleAdaptor {
    @Override
    public void onBattleStarted(BattleStartedEvent event) {
        System.out.println("battle started");
        super.onBattleStarted(event);
    }

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        System.out.println("round ended");
        super.onRoundEnded(event);
    }

    @Override
    public void onBattleFinished(BattleFinishedEvent event) {
        System.out.println("Battle finished");
    }

    @Override
    public void onBattleCompleted(BattleCompletedEvent event) {
        System.out.println("Battle completed");
    }

    @Override
    public void onBattlePaused(BattlePausedEvent event) {
        System.out.println("battle paused");
    }

    @Override
    public void onBattleResumed(BattleResumedEvent event) {
        super.onBattleResumed(event);
    }

    @Override
    public void onRoundStarted(RoundStartedEvent event) {
        System.out.println("Round started");
    }

    @Override
    public void onTurnStarted(TurnStartedEvent event) {
        super.onTurnStarted(event);
    }

    @Override
    public void onTurnEnded(TurnEndedEvent event) {
        ITurnSnapshot turnSnapshot = event.getTurnSnapshot();
        IRobotSnapshot[] robots = turnSnapshot.getRobots();
        super.onTurnEnded(event);
    }

    @Override
    public void onBattleMessage(BattleMessageEvent event) {
        super.onBattleMessage(event);
    }

    @Override
    public void onBattleError(BattleErrorEvent event) {
        System.err.println("error: " + event.getError());
    }
}
