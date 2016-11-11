package com.matsemann;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

/**
 * Created by mats kruger svensson on 08/11/16.
 */
public class ANNTest {


    public static final int WINDOW_SIZE = 1;

    private BasicNetwork ann;

    public void run() {
        ann = new BasicNetwork();

        ann.addLayer(new BasicLayer(WINDOW_SIZE));
        ann.addLayer(new BasicLayer(5));


    }


}
