package com.coolioasjulio.neuralnetwork.activationstrategy;

import java.util.Arrays;

public class ReLuActivationStrategy implements ActivationStrategy {

    @Override
    public double[] activate(double[] weightedSums) {
        return Arrays.stream(weightedSums).map(x -> Math.max(0, x)).toArray();
    }

    @Override
    public double[] derivativeOutput(double[] output) {
        return Arrays.stream(output).map(x -> x <= 0 ? 0 : 1).toArray();
    }
}
