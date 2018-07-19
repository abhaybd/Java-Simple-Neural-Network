package com.coolioasjulio.neuralnetwork.activationstrategy;

import java.util.Arrays;

public class HyperbolicTangentActivationStrategy implements ActivationStrategy {
    @Override
    public double[] activate(double[] weightedSums) {
        return Arrays.stream(weightedSums).map(Math::tanh).toArray();
    }

    @Override
    public double[] derivativeOutput(double[] output) {
        return Arrays.stream(output).map(x -> 1.0 / Math.cosh(x)).toArray();
    }
}
