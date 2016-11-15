package com.matsemann;

import net.sf.robocode.battle.Battle;
import net.sf.robocode.security.HiddenAccess;
import robocode.control.*;

import java.io.File;
import java.net.URL;

/**
 * Created by mats kruger svensson on 07/11/16.
 */
public class Starter {

    public static void main(String[] args) {

        RobocodeEngine.setLogErrorsEnabled(true);
        RobocodeEngine.setLogMessagesEnabled(true);
//        RobocodeEngine engine = new RobocodeEngine(new BattleListener());
        RobocodeEngine engine = new RobocodeEngine(new File("/Users/matssvensson/robo/robocode-core"));
//        RobocodeEngine engine = new RobocodeEngine(new File("/Users/matssvensson/robocode3"));
        engine.addBattleListener(new BattleListener());
        engine.setVisible(true);

//        RobotSpecification[] robots = engine.getLocalRepository("com.matsemann.bot.ObserverBot*, sample.Walls");
//        RobotSpecification[] robots = engine.getLocalRepository("com.matsemann.bot.LiveAnnTestBot*, sample.Crazy");
        RobotSpecification[] robots = engine.getLocalRepository("com.matsemann.bot.AnnTestBot*, sample.Walls");

        BattlefieldSpecification battleField = new BattlefieldSpecification();
        BattleSpecification battle = new BattleSpecification(50, battleField, robots);

        engine.runBattle(battle);


        engine.waitTillBattleOver();
        System.out.println("over");

//        URL itemURL = new URL("file:/Users/matssvensson/robocodeea/target/classes/com/matsemann/robots/mats/TestBot.class");
//        RobotItem fileSpecification = new RobotItem(itemURL, new ClasspathRoot());
//        RobotSpecification robotSpecification = new RobotSpecification(fileSpecification);


//        RobotSpecification robotSpecification = HiddenAccess.createSpecification();

//        RobotSpecification[] robots = engine.getLocalRepository("com.matsemann.bot.MatsTestBot*, sample.TrackFire");
    }
}
