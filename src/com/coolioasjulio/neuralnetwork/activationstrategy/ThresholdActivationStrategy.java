package com.coolioasjulio.neuralnetwork.activationstrategy;

import java.util.Arrays;

public class ThresholdActivationStrategy implements ActivationStrategy {

    @Override
    public double[] activate(double[] weightedSums) {
        return Arrays.stream(weightedSums).map(x -> (x == 0) ? 0.5 : ((x > 0) ? 1 : 0)).toArray();
    }

    @Override
    public double[] derivativeOutput(double[] output) {
        double[] out = new double[output.length];
        Arrays.fill(out, 0.0);
        return out;
    }
}
