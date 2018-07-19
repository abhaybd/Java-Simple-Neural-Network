package com.coolioasjulio.neuralnetwork.activationstrategy;

import com.coolioasjulio.neuralnetwork.Layer;

public interface ActivationStrategy {
    default double[] activate(Layer layer) {
        return activate(layer.getFlattenedWeightedSums());
    }

    default double[] derivativeOutput(Layer layer) {
        return derivativeOutput(layer.getFlattenedOutput());
    }

    double[] activate(double[] weightedSums);

    double[] derivativeOutput(double[] output);
}
