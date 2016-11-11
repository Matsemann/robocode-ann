package com.matsemann.bot;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import robocode.AdvancedRobot;

/**
 * Created by mats kruger svensson on 08/11/16.
 */
public class AnnTestBot extends AdvancedRobot {

    public static final int WINDOW_SIZE = 1;

    private BasicNetwork ann;

    @Override
    public void run() {
        ann = new BasicNetwork();


        ann.addLayer(new BasicLayer(WINDOW_SIZE));
        ann.addLayer(new BasicLayer(5));
    }



}
