package com.coolioasjulio.neuralnetwork.activationstrategy;

import java.util.Arrays;

public class SigmoidActivationStrategy implements ActivationStrategy {

    @Override
    public double[] activate(double[] weightedSums) {
        return Arrays.stream(weightedSums).map(x -> 1.0 / (1.0 + Math.exp(-x))).toArray();
    }

    @Override
    public double[] derivativeOutput(double[] output) {
        return Arrays.stream(output).map(x -> x * (1 - x)).toArray();
    }
}
